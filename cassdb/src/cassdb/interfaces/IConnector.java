package cassdb.interfaces;

import me.prettyprint.hector.api.Keyspace;

/**
 * Interface for cassandra database Connector
 * 
 * @author cbarca
 */
public interface IConnector {
	Keyspace getKeyspace();
}
