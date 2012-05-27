package neuralnet.dbconx;

import java.util.Arrays;

import cassdb.interfaces.IConnector;

import me.prettyprint.cassandra.service.ThriftKsDef;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
import me.prettyprint.hector.api.ddl.ComparatorType;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.factory.HFactory;

/**
 * Cassandra MRTS Database Connector
 * 
 * @author cbarca
 * @email cristi.barca@gmail.com
 */
public class MrtsConnector implements IConnector {
	// Constants
	public static final String MRTS_CLUSTERNAME = "mrtscluster";
	public static final String MRTS_CLUSTERADDR = "emerald:9160";
	public static final String MRTS_KEYSPACENAME = "mrtsdb";
		
	public static final String NET_STRUCT_COLFAM = "NetStruct";
	public static final String NET_WGE_COLFAM = "NetWGE";
	public static final String NET_SER_COLFAM = "NetSer";
	public static final String NET_QERR_COLFAM = "NetQErr";
	
	// Private members
	private Cluster _mrtsCluster;
	private Keyspace _keyspace;
		
	/**
	 *  Default constructor, uses the default init settings
	 */
	public MrtsConnector() {
		this(MRTS_CLUSTERNAME, MRTS_CLUSTERADDR, MRTS_KEYSPACENAME);
	}
	
	/**
	 * Specialized constructor
	 * @param clusterName virtual cluster name, just for inside-project use
	 * @param clusterAddr the IPAddress:port of cluster
	 */
	public MrtsConnector(String clusterName, String clusterAddr, String keyspaceName) {
		_mrtsCluster = HFactory.getOrCreateCluster(clusterName, clusterAddr);
		
		KeyspaceDefinition ksDef = _mrtsCluster.describeKeyspace(keyspaceName);
		
		if (ksDef == null) {
			this.createSchema(keyspaceName);
			return;
		}
		
		_keyspace = HFactory.createKeyspace(keyspaceName, _mrtsCluster);
	}
	
	/**
	 * Creates the database schema (if is not yet created)
	 */
	private void createSchema(String keyspaceName) {
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
				NET_SER_COLFAM,
				ComparatorType.UTF8TYPE);	
		
		ColumnFamilyDefinition netQErrCfDef = HFactory.createColumnFamilyDefinition(
				keyspaceName, 
				NET_QERR_COLFAM,
				ComparatorType.LONGTYPE);
		
		KeyspaceDefinition ksDef = HFactory.createKeyspaceDefinition(
				keyspaceName, 
				ThriftKsDef.DEF_STRATEGY_CLASS,
				1,
				Arrays.asList(netStructCfDef, netWGECfDef, netSerCfDef, netQErrCfDef));
		
		_mrtsCluster.addKeyspace(ksDef, true);
		
		_keyspace = HFactory.createKeyspace(keyspaceName, _mrtsCluster);
	}
	
	/**
	 * Get registered keyspace
	 * @return keyspace
	 */
	public Keyspace getKeyspace() {
		return _keyspace;
	}
}
