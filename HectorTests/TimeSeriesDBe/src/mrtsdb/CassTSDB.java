package mrtsdb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import me.prettyprint.cassandra.serializers.FloatSerializer;
import me.prettyprint.cassandra.serializers.LongSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.ColumnSliceIterator;
import me.prettyprint.cassandra.service.ThriftKsDef;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.OrderedRows;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
import me.prettyprint.hector.api.ddl.ComparatorType;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.RangeSlicesQuery;
import me.prettyprint.hector.api.query.SliceQuery;

import mrtsdm.*;
import mrtsis.*;


/** 
 * @author Cristi.Barca
 * 
 * Cassandra Time Series Database class.
 * Establishes the connection with Cassandra cluster and 
 * implements query methods to use on time series database.
 * Dependencies: Hector API
 * 
 */
public class CassTSDB implements ICassTSDB {
	// Constants
	public static final String MRTS_CLUSTERNAME = "mrtscluster";
	public static final String MRTS_CLUSTERADDR = "emerald:9160";
	public static final String KEYSPACE_NAME = "Test";
	public static final String COLFAM_NAME = "tsdbe";
	
	// Private members
	private Cluster mrtsCluster;
	private Keyspace keyspace;
	private StringSerializer strSer = new StringSerializer();
	private LongSerializer lngSer = new LongSerializer();
	private FloatSerializer flSer = new FloatSerializer();
	
	private Mutator<Long> batchMutator;
	
	/**
	 *  Implicit constructor, uses the default init settings.
	 */
	public CassTSDB() {
		this(MRTS_CLUSTERNAME, MRTS_CLUSTERADDR);
	}
	
	/**
	 * Specialized constructor.
	 * @param clusterName = virtual cluster name, just for inside-project use;
	 * @param clusterAddr = the IPAddress:port of cluster;
	 */
	public CassTSDB(String clusterName, String clusterAddr) {
		mrtsCluster = HFactory.getOrCreateCluster(clusterName, clusterAddr);
		
		KeyspaceDefinition ksDef = mrtsCluster.describeKeyspace(KEYSPACE_NAME);
		
		if (ksDef == null) {
			createSchema();
			return;
		}
		
		keyspace = HFactory.createKeyspace(KEYSPACE_NAME, mrtsCluster);
	}
	
	/**
	 * Creates the database schema. (if it is not yet created)
	 */
	private void createSchema() {
		ColumnFamilyDefinition cfDef = HFactory.createColumnFamilyDefinition(
				KEYSPACE_NAME, 
				COLFAM_NAME,
				ComparatorType.UTF8TYPE);
		
		KeyspaceDefinition ksDef = HFactory.createKeyspaceDefinition(
				KEYSPACE_NAME, 
				ThriftKsDef.DEF_STRATEGY_CLASS,
				1,
				Arrays.asList(cfDef));
		
		mrtsCluster.addKeyspace(ksDef, true);
		
		keyspace = HFactory.createKeyspace(KEYSPACE_NAME, mrtsCluster);
	}
	
	/**
	 * Inserts a sensor package into the database.
	 * @param spkg = sensor package - contains the timestamp and the sensor data
	 */
	@Override
	public void add(SensorPkg spkg) {
		long ts = spkg.getTimestamp();
		SensorData data = spkg.getData();
				
		Mutator<Long> mutator = HFactory.createMutator(keyspace, lngSer);
		
		for (Map.Entry<String, Float> mpEntry : data.getEntrySet()) {
			mutator.insert(ts, COLFAM_NAME, HFactory.createColumn(mpEntry.getKey(), mpEntry.getValue(), strSer, flSer));
		}
	}
	
	/**
	 * Inserts list of sensor packages into the database.
	 * @param lspkg = list of sensor packages
	 */
	@Override
	public void add(List<SensorPkg> lspkg) {
		long ts;
		SensorData data;
				
		Mutator<Long> mutator = HFactory.createMutator(keyspace, lngSer);
		
		for (SensorPkg spkg : lspkg) {
			ts = spkg.getTimestamp();
			data = spkg.getData();
			
			for (Map.Entry<String, Float> mpEntry : data.getEntrySet()) {
				mutator.addInsertion(ts, COLFAM_NAME, HFactory.createColumn(mpEntry.getKey(), mpEntry.getValue(), strSer, flSer));
			}
		}
		
		mutator.execute();
	}
	
	/**
	 * Inserts a series of sensor packages by using the batch-mutator, 
	 * depends of the user-control of start-curr-finish.
	 * @param spkg = sensor package
	 * @param start = the begin of the series, when we create the mutator
	 * @param curr = current state of the series
	 * @param finish = the finish of the series, when we execute the batch
	 */
	@Override
	public void add(SensorPkg spkg, int start, int curr, int finish) {
		long ts = spkg.getTimestamp();
		SensorData data = spkg.getData();
			
		// Begin a new series of insertions
		if (start == curr) {
			batchMutator = HFactory.createMutator(keyspace, lngSer);
		}
		
		// Assemble the insertions
		for (Map.Entry<String, Float> mpEntry : data.getEntrySet()) {
			batchMutator.addInsertion(ts, COLFAM_NAME, HFactory.createColumn(mpEntry.getKey(), mpEntry.getValue(), strSer, flSer));
		}
		
		// Execute batch
		if (finish == curr) {
			batchMutator.execute();
		}		
	}

	/**
	 * Gets a row from the database (a row is actually a SensorData).
	 * @param ts = timestamp, the key of the row
	 * @return SensorData object (with all the columns)
	 */
	@Override
	public SensorData get(long ts) {
		SensorData data = new SensorData();
						
		SliceQuery<Long, String, Float> sliceQuery = HFactory.createSliceQuery(keyspace, lngSer, strSer, flSer);
		sliceQuery.setColumnFamily(COLFAM_NAME);
		sliceQuery.setKey(ts);
        
        ColumnSliceIterator<Long, String, Float> csit = new ColumnSliceIterator<Long, String, Float>( 
				sliceQuery, null, "\uFFFF", false);
		
		while (csit.hasNext()) {
			HColumn<String, Float> hc = csit.next();
			data.putValue(hc.getName(), hc.getValue());
		}
		
		return data;
	}
     
	/**
	 * Gets the columns that you specify within the row you want.
	 * @param ts = timestamp (row key)
	 * @param colNames = list of column names that we want to get from that row
	 * @return a SensorData object with the columns specified 
	 */
	@Override
	public SensorData get(long ts, List<String> colNames) {
		SensorData data = new SensorData();
		String[] colNamesArr = new String[colNames.size()];
		
		SliceQuery<Long, String, Float> sliceQuery = HFactory.createSliceQuery(keyspace, lngSer, strSer, flSer);
		sliceQuery.setColumnFamily(COLFAM_NAME);
		sliceQuery.setKey(ts);
		sliceQuery.setColumnNames(colNames.toArray(colNamesArr));
		
		QueryResult<ColumnSlice<String, Float>> result = sliceQuery.execute();
		Iterator<HColumn<String, Float>> csit = result.get().getColumns().iterator();
        
		while (csit.hasNext()) {
			HColumn<String, Float> hc = csit.next();
			data.putValue(hc.getName(), hc.getValue());
		}
		
		return data;
	}

	/**
	 * Gets the timeseries (as a list of sensor-data) from tsStart to tsEnd.
	 * @param tsBegin = begin timestamp
	 * @param tsEnd = end timestamp
	 * @return a list of SensorData objects (ordered by timestamp) 
	 */
	@Override
	public List<SensorData> get(long tsBegin, long tsEnd) {
		List<SensorData> lData = new ArrayList<SensorData>();
		
		RangeSlicesQuery<Long, String, Float> rangeSlicesQuery = HFactory.createRangeSlicesQuery(
				keyspace, lngSer, strSer, flSer);
		rangeSlicesQuery.setColumnFamily(COLFAM_NAME);
		rangeSlicesQuery.setKeys(tsBegin, tsEnd);
		rangeSlicesQuery.setRange(null, null, false, 1);
		
		QueryResult<OrderedRows<Long, String, Float>> result = rangeSlicesQuery.execute();
        OrderedRows<Long, String, Float> rows = result.get();
        Iterator<Row<Long, String, Float>> rowsIt = rows.iterator();
        
        /* TODO
         * To be paged _soon_ - maybe the day before the presentation :))  
         */
        while (rowsIt.hasNext()) {
        	Iterator<HColumn<String, Float>> colsIt = rowsIt.next().getColumnSlice().getColumns().iterator();
        	
        	SensorData data = new SensorData();
        	
        	while (colsIt.hasNext()) {
        		HColumn<String, Float> hc = colsIt.next();
        		data.putValue(hc.getName(), hc.getValue());
        	}
        	
        	lData.add(data);
        }
		
		return lData;
	}

	/**
	 * Gets the timeseries (as a list of sensor-data) from tsStart to tsEnd, selecting
	 * just specific columns from the rows
	 * @param tsBegin = begin timestamp
	 * @param tsEnd = end timestamp
	 * @param colNames = list of column names that we want to get from the row
	 * @return a list of SensorData objects (ordered by timestamp) with the columns specified 
	 */
	@Override
	public List<SensorData> get(long tsBegin, long tsEnd, List<String> colNames) {
		List<SensorData> lData = new ArrayList<SensorData>();
		String[] colNamesArr = new String[colNames.size()];
		
		RangeSlicesQuery<Long, String, Float> rangeSlicesQuery = HFactory.createRangeSlicesQuery(
				keyspace, lngSer, strSer, flSer);
		rangeSlicesQuery.setColumnFamily(COLFAM_NAME);
		rangeSlicesQuery.setKeys(tsBegin, tsEnd);
		rangeSlicesQuery.setColumnNames(colNames.toArray(colNamesArr));
		
		QueryResult<OrderedRows<Long, String, Float>> result = rangeSlicesQuery.execute();
        OrderedRows<Long, String, Float> rows = result.get();
        Iterator<Row<Long, String, Float>> rowsIt = rows.iterator();
        
        /* TODO
         * To be paged _soon_ - maybe the day before the presentation :))  
         */
        while (rowsIt.hasNext()) {
        	Iterator<HColumn<String, Float>> colsIt = rowsIt.next().getColumnSlice().getColumns().iterator();
        	
        	SensorData data = new SensorData();
        	
        	while (colsIt.hasNext()) {
        		HColumn<String, Float> hc = colsIt.next();
        		data.putValue(hc.getName(), hc.getValue());
        	}
        	
        	lData.add(data);
        }
		
		return lData;		
	}
	
	/**
	 * Deletes the entire row from a specified key.
	 * @param ts = timestamp (row key)
	 */
	@Override
	public void delete(long ts) {
		Mutator<Long> mutator = HFactory.createMutator(keyspace, lngSer);
		
		// Seems that this operation deletes all the columns from a row.
		mutator.delete(ts, COLFAM_NAME, null, lngSer);
	}	
	
	/**
	 * Deletes the range of rows from tsBegin to tsEnd.
	 * @param tsBegin = begin timestamp
	 * @param tsEnd = end timestamp
	 */
	@Override
	public void delete(long tsBegin, long tsEnd) {
		long ts;
		
		Mutator<Long> mutator = HFactory.createMutator(keyspace, lngSer);
		
		for (ts = tsBegin; ts < tsEnd; ts++) {
			mutator.addDeletion(ts, COLFAM_NAME, null, lngSer);
		}
		
		mutator.execute();
 	}
	
	// TEST
	public static void main(String[] args) {
		SensorPkg spkg;
		SensorData sdata;

		// Establish a connection with the cluster
		ICassTSDB ctsdb = new CassTSDB();
		// Parse the _original_ input file
		ToCSVParser.parse("sensor.stream", SensorIS.DEFAULT_SOURCE);
		// Create a _input_stream_ (creates a BufferedStream over the SensorIS.DEFAULT_SOURCE file)
		SensorIS sis = new SensorIS();
		
		// TESTuse
		int cont = 0;
		long s1 = 0, s2 = 0;
		
		try {
			
			sis.open();
			sis.readHeadline();
			
			while ( (spkg = sis.getPkg()) != null && cont < 10) {
				cont++;
			
				// TESTuse
				if (cont == 1) {
					s1 = spkg.getTimestamp();
				}
				
				// TESTuse
				if (cont == 4) {
					s2 = spkg.getTimestamp();
				}
			
				System.out.println("Pkg: timestamp=" + spkg.getTimestamp() + " val=" + spkg.getData().getValue("ecg"));
				
				ctsdb.add(spkg);
				sdata = ctsdb.get(spkg.getTimestamp());
				
				System.out.println("After add&get: timestamp=" + spkg.getTimestamp() + " val=" + sdata.getValue("ecg"));
			}
			
			sis.close();
			
			List<String> al = new ArrayList<String>();
			al.add("ecg");
			List<SensorData> lsd = ctsdb.get(s1, s2);
			// List<SensorData> lsd = ctsdb.get(s1, s2, al);
			
			System.out.println("Range operation:");
			System.out.println(lsd.size() + " timestamps: from " + s1 + " to " + s2);
			for (SensorData sd : lsd) {
				for (Map.Entry<String, Float> mpe : sd.getEntrySet()) {
					System.out.println("name=" + mpe.getKey() + " val=" + mpe.getValue());
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
