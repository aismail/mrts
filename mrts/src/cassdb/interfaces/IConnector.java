package cassdb.interfaces;

import me.prettyprint.hector.api.Keyspace;

public interface IConnector {
	void createSchema(String keyspaceName);
	Keyspace getKeyspace();
}
