package neuralnet.dbconx;

import java.util.Arrays;

import cassdb.interfaces.IConnector;

import me.prettyprint.cassandra.service.OperationType;
import me.prettyprint.cassandra.service.ThriftKsDef;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.ConsistencyLevelPolicy;
import me.prettyprint.hector.api.HConsistencyLevel;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
import me.prettyprint.hector.api.ddl.ComparatorType;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.factory.HFactory;

/**
 * Cassandra MRTS Database Connector
 * Connection and keyspace creation
 * 
 * @author cbarca
 */
public class MrtsConnector implements IConnector {
	// Constants
	public static final String MRTS_CLUSTERNAME = "mrtscluster";
	public static final String MRTS_CLUSTERADDR = "emerald:9160";
	public static final String MRTS_KEYSPACENAME = "mrtsdb";
	public static final int MRTS_KEYSPACE_REPFACTOR = 11; 
		
	public static final String NET_STRUCT_COLFAM = "NetStruct";
	public static final String NET_WGE_COLFAM = "NetWGE";
	public static final String NET_SAVE_COLFAM = "NetSave";
	public static final String NET_QERR_COLFAM = "NetQErr";
	
	// Private members
	private Cluster _mrtsCluster;
	private Keyspace _keyspace;
		
	// Consistency class
	class MrtsConsistencyLevel implements ConsistencyLevelPolicy {
		@Override
		public HConsistencyLevel get(OperationType op) {
			switch (op) {
			case READ:
				return HConsistencyLevel.QUORUM;
			case WRITE:
				return HConsistencyLevel.QUORUM;
			default:
				return HConsistencyLevel.QUORUM;
			}
		}

		@Override
		public HConsistencyLevel get(OperationType op, String cfName) {
			return this.get(op);
		}
	}
	
	/**
	 *  Default constructor, uses the default init settings
	 */
	public MrtsConnector() {
		this(MRTS_CLUSTERNAME, MRTS_CLUSTERADDR, 
				MRTS_KEYSPACENAME, MRTS_KEYSPACE_REPFACTOR);
	}
	
	/**
	 * Specialized constructor
	 * @param clusterName virtual cluster name, just for inside-project use
	 * @param clusterAddr the IPAddress:port of cluster
	 * @param keyspaceName keyspace name
	 * @param keyspaceRepFactor keyspace replication factor
	 */
	public MrtsConnector(String clusterName, String clusterAddr, 
			String keyspaceName, int keyspaceRepFactor) {
		_mrtsCluster = HFactory.getOrCreateCluster(clusterName, clusterAddr);
		
		KeyspaceDefinition ksDef = _mrtsCluster.describeKeyspace(keyspaceName);
		
		if (ksDef == null) {
			this.createSchema(keyspaceName, keyspaceRepFactor);
			return;
		}
		
		_keyspace = HFactory.createKeyspace(keyspaceName, _mrtsCluster);
	}
	
	/**
	 * Creates the database schema (if is not yet created)
	 * @param keyspaceName keyspace name
	 * @param keyspaceRepFactor keyspace replication factor
	 */
	private void createSchema(String keyspaceName, int keyspaceRepFactor) {
		ColumnFamilyDefinition netStructCfDef = HFactory.createColumnFamilyDefinition(
				keyspaceName, 
				NET_STRUCT_COLFAM,
				ComparatorType.UTF8TYPE);
		
		ColumnFamilyDefinition netWGECfDef = HFactory.createColumnFamilyDefinition(
				keyspaceName, 
				NET_WGE_COLFAM,
				ComparatorType.INTEGERTYPE);
		
		ColumnFamilyDefinition netSerCfDef = HFactory.createColumnFamilyDefinition(
				keyspaceName, 
				NET_SAVE_COLFAM,
				ComparatorType.INTEGERTYPE);	
		
		ColumnFamilyDefinition netQErrCfDef = HFactory.createColumnFamilyDefinition(
				keyspaceName, 
				NET_QERR_COLFAM,
				ComparatorType.LONGTYPE);
		
		KeyspaceDefinition ksDef = HFactory.createKeyspaceDefinition(
				keyspaceName, 
				ThriftKsDef.DEF_STRATEGY_CLASS,
				keyspaceRepFactor,
				Arrays.asList(netStructCfDef, netWGECfDef, netSerCfDef, netQErrCfDef));
		
		_mrtsCluster.addKeyspace(ksDef, true);
		
		_keyspace = HFactory.createKeyspace(keyspaceName, _mrtsCluster, 
				new MrtsConsistencyLevel());
	}
	
	/**
	 * Get registered keyspace
	 * @return keyspace
	 */
	public Keyspace getKeyspace() {
		return _keyspace;
	}
}
