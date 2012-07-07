package cassdb.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cassdb.interfaces.IHashClient;
import me.prettyprint.cassandra.serializers.IntegerSerializer;
import me.prettyprint.cassandra.serializers.LongSerializer;
import me.prettyprint.cassandra.serializers.ObjectSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.ColumnSliceIterator;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.OrderedRows;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.ColumnQuery;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.RangeSlicesQuery;
import me.prettyprint.hector.api.query.SliceQuery;

/**
 * Hash Client
 * Hash over cassandra database
 * 
 * @author cbarca
 */
public class HashClient implements IHashClient {
	// Private members
	private Keyspace _keyspace;
	private Mutator<Integer> _batchMutator;
	private ObjectSerializer _objSer;
	
	public HashClient(Keyspace keyspace, ClassLoader cl) {
		_keyspace = keyspace;
		if (cl == null) {
			_objSer = ObjectSerializer.get();
		}
		else {
			_objSer = new ObjectSerializer(cl);
		}
	}
	
	public HashClient(Keyspace keyspace) {
		this(keyspace, null);
	}

	/**
	 * Get the the row<keyL> from colfamName
	 * @param colfamName column family name
	 * @param keyL line/row key
	 * @param rowSize row size
	 * @return map of <keyC, value> from line/row keyL
	 */
	@Override
	public Map<Integer, Object> getRow(String colfamName, Integer keyL, int rowSize) {
		Map<Integer, Object> map = new HashMap<Integer, Object>();

		SliceQuery<Integer, Integer, Object> sliceQuery = HFactory.createSliceQuery(
				_keyspace, IntegerSerializer.get(), IntegerSerializer.get(), _objSer);

		sliceQuery.setColumnFamily(colfamName);
		sliceQuery.setKey(keyL);
		sliceQuery.setRange(null, null, false, rowSize);

		ColumnSlice<Integer, Object> result = sliceQuery.execute().get();
		List<HColumn<Integer, Object>> columns = result.getColumns();

		Iterator<HColumn<Integer, Object>> it = columns.iterator();

		while (it.hasNext()) {
			HColumn<Integer, Object> column = it.next();
			map.put(column.getName(), column.getValue());
		}

		return map;
	}
	
	/**
	 * Paginate-get the values from colfamName<keyL>
	 * @param colfamName column family name
	 * @param keyL line key
	 * @param colsPageSize columns page size (how many columns to take in count at paging)
	 * @return map of <keyC, value> from row keyL
	 */
	@Override
	public Map<Integer, Object> getPaginateRow(String colfamName, Integer keyL, int colsPageSize) {
		Map<Integer, Object> map = new HashMap<Integer, Object>();

		SliceQuery<Integer, Integer, Object> sliceQuery = HFactory.createSliceQuery(
				_keyspace, IntegerSerializer.get(), IntegerSerializer.get(), _objSer);
		sliceQuery.setColumnFamily(colfamName);
		sliceQuery.setKey(keyL);
		
		AllColumnsIterator<Integer, Object> it = new AllColumnsIterator<Integer, Object>(
				sliceQuery, colsPageSize);
		
		while (it.hasNext()) {
			HColumn<Integer, Object> column = it.next();
			map.put(column.getName(), column.getValue());
		}

		return map;
	}

	/**
	 * Paginate-get the values from colfamName
	 * @param colfamName column family name
	 * @param rowsPageSize rows page size
	 * @return a map<keyL, rows_map<keyC, value>>
	 */
	@Override
	public Map<Integer, Map<Integer, Object>> getPaginateColFam(String colfamName, int rowsPageSize) {
		Map<Integer, Map<Integer, Object>> mapr = new HashMap<Integer, Map<Integer, Object>>();
		boolean lastIteration = false;
		
		RangeSlicesQuery<Integer, Integer, Object> rangeSliceQuery = HFactory.createRangeSlicesQuery(
				_keyspace, IntegerSerializer.get(), IntegerSerializer.get(), _objSer);
		rangeSliceQuery.setColumnFamily(colfamName);
		// from 0:to_colfam_end with rowPageSize chunk limit
		rangeSliceQuery.setKeys(null, null);
		// all columns taken into account
		rangeSliceQuery.setRange(null, null, false, Integer.MAX_VALUE);
		rangeSliceQuery.setRowCount(rowsPageSize);
		
		OrderedRows<Integer, Integer, Object> result = rangeSliceQuery.execute().get();
		
		while (!lastIteration) {
			Iterator<Row<Integer, Integer, Object>> itr = result.iterator();
			
			while (itr.hasNext()) {
				Row<Integer, Integer, Object> row = itr.next();
				ColumnSlice<Integer, Object> columnSlice = row.getColumnSlice();
				Iterator<HColumn<Integer, Object>> itc = columnSlice.getColumns().iterator();
				
				Map<Integer, Object> mapc = new HashMap<Integer, Object>();
				while (itc.hasNext()) {
					HColumn<Integer, Object> column = itc.next();
					mapc.put(column.getName(), column.getValue());
				}
				
				mapr.put(row.getKey(), mapc);
			}
			
			if (result.getCount() < rowsPageSize) {
				lastIteration = true;
				continue;
			}
			
			rangeSliceQuery.setKeys(result.peekLast().getKey(), null);
			result = rangeSliceQuery.execute().get();
		}
		
		return mapr;
	}

	/**
	 * Get the value from colfamName<keyL, keyC>
	 * @param colfamName column family name
	 * @param keyL line key
	 * @param keyC column key
	 * @return value of the column, object type
	 */
	@Override
	public Object get(String colfamName, Integer keyL, Integer keyC) {
		ColumnQuery<Integer, Integer, Object> columnQuery = HFactory.createColumnQuery(
				_keyspace, IntegerSerializer.get(), IntegerSerializer.get(), _objSer);

		columnQuery.setColumnFamily(colfamName);
		columnQuery.setKey(keyL);
		columnQuery.setName(keyC);

		QueryResult<HColumn<Integer, Object>> result = columnQuery.execute();

		return result.get().getValue();
	}

	/**
	 * Get the value from colfamName<keyL, keyC>
	 * @param colfamName column family name
	 * @param keyL line key
	 * @param keyC column key
	 * @return value of the column, object type
	 */
	@Override
	public Object get(String colfamName, String keyL, Long keyC) {
		ColumnQuery<String, Long, Object> columnQuery = HFactory.createColumnQuery(
				_keyspace, StringSerializer.get(), LongSerializer.get(), _objSer);

		columnQuery.setColumnFamily(colfamName);
		columnQuery.setKey(keyL);
		columnQuery.setName(keyC);

		QueryResult<HColumn<Long, Object>> result = columnQuery.execute();

		return result.get().getValue();
	}

	/**
	 * Get the value from colfamName<keyL, keyC>
	 * @param colfamName column family name
	 * @param keyL line key
	 * @param keyC column key
	 * @return value of the column, object type
	 */
	@Override
	public Object get(String colfamName, String keyL, String keyC) {
		ColumnQuery<String, String, Object> columnQuery = HFactory.createColumnQuery(
				_keyspace, StringSerializer.get(), StringSerializer.get(), _objSer);

		columnQuery.setColumnFamily(colfamName);
		columnQuery.setKey(keyL);
		columnQuery.setName(keyC);

		QueryResult<HColumn<String, Object>> result = columnQuery.execute();

		return result.get().getValue();
	}

	/**
	 * Get timestamp column slice (from a row with timestamps values)
	 * @param colfamName column family name
	 * @param keyL row key
	 * @param k1 begin column key (begin timestamp)
	 * @param k2 end column key (end timestamp)
	 * @param maxCount max columns
	 * @param tList the list where to put cluster's timestamps
	 * @return the objects' list between k1 and k2 (maxCount objects) 	 
	 */
	@Override
	public List<Object> getTimestampSeries(String colfamName, String keyL, 
			Long k1, Long k2, int maxCount, List<Long> tList) {
		List<Object> list = new ArrayList<Object>();

		SliceQuery<String, Long, Object> sliceQuery = 
			HFactory.createSliceQuery(_keyspace, StringSerializer.get(), 
					LongSerializer.get(), _objSer);

		sliceQuery.setColumnFamily(colfamName);
		sliceQuery.setKey(keyL);
		sliceQuery.setRange(k1, k2, false, maxCount);

		ColumnSliceIterator<String, Long, Object> csit = new ColumnSliceIterator<String, Long, Object>( 
				sliceQuery, k1, k2, false);

		while (csit.hasNext() && maxCount > 0) {
			HColumn<Long, Object> hc = csit.next();	
			list.add(hc.getValue());
			tList.add(hc.getName());
			maxCount--;
		}

		return list;
	}

	/**
	 * Put a value to colfamName<keyL, keyC>
	 * @param colfamName column family name
	 * @param keyL line key
	 * @param keyC column key
	 * @param value object value to put at colfamName<keyL, keyC>
	 */
	@Override
	public void put(String colfamName, Integer keyL, Integer keyC, Object value) {
		Mutator<Integer> mutator = HFactory.createMutator(_keyspace, IntegerSerializer.get());

		mutator.insert(keyL, colfamName, 
				HFactory.createColumn(keyC, value, IntegerSerializer.get(), _objSer));
	}

	/**
	 * Put a value to colfamName<keyL, keyC>
	 * @param colfamName column family name
	 * @param keyL line key
	 * @param keyC column key
	 * @param value object value to put at colfamName<keyL, keyC>
	 */
	@Override
	public void put(String colfamName, String keyL, Long keyC, Object value) {
		Mutator<String> mutator = HFactory.createMutator(_keyspace, StringSerializer.get());

		mutator.insert(keyL, colfamName, 
				HFactory.createColumn(keyC, value, LongSerializer.get(), _objSer));
	}

	/**
	 * Put a value to colfamName<keyL, keyC>
	 * @param colfamName column family name
	 * @param keyL line key
	 * @param keyC column key
	 * @param value object value to put at colfamName<keyL, keyC>
	 */
	@Override
	public void put(String colfamName, String keyL, String keyC, Object value) {
		Mutator<String> mutator = HFactory.createMutator(_keyspace, StringSerializer.get());

		mutator.insert(keyL, colfamName, 
				HFactory.createColumn(keyC, value, StringSerializer.get(), _objSer));
	}

	/**
	 * Start batch-put session
	 */
	@Override
	public void startBatchPut() {
		_batchMutator = HFactory.createMutator(_keyspace, IntegerSerializer.get());
	}

	/**
	 * Perform batch-puts
	 * @param colfamName column family name
	 * @param keyL line key
	 * @param keyC column key
	 * @param value object value to put at colfamName<keyL, keyC>
	 */
	@Override
	public void batchPut(String colfamName, Integer keyL, Integer keyC, Object value) {
		_batchMutator.addInsertion(keyL, colfamName, 
				HFactory.createColumn(keyC, value, IntegerSerializer.get(), _objSer));
	}

	/**
	 * Finalize batch-put session
	 */
	@Override
	public void finalizeBatchPut() {
		_batchMutator.execute();
	}

	/**
	 * Remove the value from colfamName<keyL, keyC>  
	 * @param colfamName name of column family
	 * @param keyL line key
	 * @param keyC column key
	 */
	@Override
	public void remove(String colfamName, Integer keyL, Integer keyC) {
		Mutator<Integer> mutator = HFactory.createMutator(_keyspace, IntegerSerializer.get());

		mutator.delete(keyL, colfamName, keyC, IntegerSerializer.get());		
	}

	/**
	 * Remove the value from colfamName<keyL, keyC>  
	 * @param colfamName name of column family
	 * @param keyL line key
	 * @param keyC column key
	 */
	@Override
	public void remove(String colfamName, String keyL, Long keyC) {
		Mutator<String> mutator = HFactory.createMutator(_keyspace, StringSerializer.get());

		mutator.delete(keyL, colfamName, keyC, LongSerializer.get());
	}

	/**
	 * Remove the value from colfamName<keyL, keyC>  
	 * @param colfamName name of column family
	 * @param keyL line key
	 * @param keyC column key
	 */
	@Override
	public void remove(String colfamName, String keyL, String keyC) {
		Mutator<String> mutator = HFactory.createMutator(_keyspace, StringSerializer.get());

		mutator.delete(keyL, colfamName, keyC, StringSerializer.get());
	}
}
