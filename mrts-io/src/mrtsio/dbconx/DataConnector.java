package mrtsio.dbconx;

import cassdb.interfaces.IConnector;

import java.util.Arrays;
import java.util.List;

import me.prettyprint.cassandra.service.ThriftKsDef;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
import me.prettyprint.hector.api.ddl.ComparatorType;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.factory.HFactory;

/**
 * Cassandra Training Database Connector
 * 
 * @author cbarca
 * @email cristi.barca@gmail.com
 */
public class DataConnector implements IConnector {
	// Constants
	public static final String MRTS_CLUSTERNAME = "mrtscluster";
	public static final String MRTS_CLUSTERADDR = "emerald:9160";
	public static final String MRTS_KEYSPACENAME = "traindb";
	
	// Private members
	private Cluster _mrtsCluster;
	private Keyspace _keyspace;
		
	/**
	 * Specialized constructor
	 * @param colfamName train data column family name
	 */
	public DataConnector(String colfamName) {
		this(MRTS_CLUSTERNAME, MRTS_CLUSTERADDR, 
				MRTS_KEYSPACENAME, colfamName);
	}
	
	/**
	 * Specialized constructor
	 * @param clusterName virtual cluster name, just for inside-project use
	 * @param clusterAddr the IPAddress:port of cluster
	 * @param colfamName train data column family name
	 */
	public DataConnector(String clusterName, String clusterAddr, 
			String keyspaceName, String colfamName) {
		_mrtsCluster = HFactory.getOrCreateCluster(clusterName, clusterAddr);
		
		KeyspaceDefinition ksDef = _mrtsCluster.describeKeyspace(keyspaceName);
		
		if (ksDef == null) {
			this.createSchema(keyspaceName, colfamName);
			return;
		}
		
		_keyspace = HFactory.createKeyspace(keyspaceName, _mrtsCluster);
		
		List<ColumnFamilyDefinition> cfDef = ksDef.getCfDefs();
		
		for (ColumnFamilyDefinition def : cfDef) {
			if (def.getName().equals(colfamName)) {
				this.dropColumnFamily(keyspaceName, colfamName);
				break;
			}
		}
		
		this.createColumnFamily(keyspaceName, colfamName);
	}
	
	/**
	 * Creates the database schema (if is not yet created)
	 */
	private void createSchema(String keyspaceName, String colfamName) {
		ColumnFamilyDefinition trainDataCfDef = HFactory.createColumnFamilyDefinition(
				keyspaceName, 
				colfamName,
				ComparatorType.INTEGERTYPE);
		
		KeyspaceDefinition ksDef = HFactory.createKeyspaceDefinition(
				keyspaceName, 
				ThriftKsDef.DEF_STRATEGY_CLASS,
				1,
				Arrays.asList(trainDataCfDef));
		
		_mrtsCluster.addKeyspace(ksDef, true);
		
		_keyspace = HFactory.createKeyspace(keyspaceName, _mrtsCluster);
	}
	
	/**
	 * Drop column family definition
	 * @param keyspaceName keyspace name for train database
	 * @param colfamName train column family name
	 */
	private void dropColumnFamily(String keyspaceName, String colfamName) {
		_mrtsCluster.dropColumnFamily(keyspaceName, colfamName);
	}
	
	/**
	 * Create column family definition
	 * @param keyspaceName keyspace name for train database
	 * @param colfamName train column family name
	 */
	private void createColumnFamily(String keyspaceName, String colfamName) {	
		ColumnFamilyDefinition trainDataCfDef = HFactory.createColumnFamilyDefinition(
				keyspaceName, 
				colfamName,
				ComparatorType.INTEGERTYPE);
		
		_mrtsCluster.addColumnFamily(trainDataCfDef);
	}
	
	/**
	 * Get registered keyspace
	 * @return keyspace
	 */
	public Keyspace getKeyspace() {
		return _keyspace;
	}
}
