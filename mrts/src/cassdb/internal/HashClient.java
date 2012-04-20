package cassdb.internal;

import me.prettyprint.cassandra.serializers.IntegerSerializer;
import me.prettyprint.cassandra.serializers.ObjectSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.ColumnQuery;
import me.prettyprint.hector.api.query.QueryResult;
import cassdb.interfaces.IHashClient;

/**
 * Hash Client
 * Hash over cassandra database.
 * 
 * @author cbarca
 */
public class HashClient implements IHashClient {
	// Private members
	private Keyspace _keyspace;
	
	public HashClient(Keyspace keyspace) {
		_keyspace = keyspace;
	}
	
	/**
	 * Get the value from colfamName<keyL, keyC>
	 * @param colfamName name of column family
	 * @param keyL line key
	 * @param keyC column key
	 * @return value of the column, object type
	 */
	@Override
	public Object get(String colfamName, Integer keyL, Integer keyC) {
		ColumnQuery<Integer, Integer, Object> columnQuery = HFactory.createColumnQuery(
				_keyspace, IntegerSerializer.get(), IntegerSerializer.get(), ObjectSerializer.get());
		
		columnQuery.setColumnFamily(colfamName);
		columnQuery.setKey(keyL);
		columnQuery.setName(keyC);
		
		QueryResult<HColumn<Integer, Object>> result = columnQuery.execute();
		
		return result.get().getValue();
	}

	/**
	 * Get the value from colfamName<keyL, keyC>
	 * @param colfamName name of column family
	 * @param keyL line key
	 * @param keyC column key
	 * @return value of the column, object type
	 */
	@Override
	public Object get(String colfamName, String keyL, String keyC) {
		ColumnQuery<String, String, Object> columnQuery = HFactory.createColumnQuery(
				_keyspace, StringSerializer.get(), StringSerializer.get(), ObjectSerializer.get());
		
		columnQuery.setColumnFamily(colfamName);
		columnQuery.setKey(keyL);
		columnQuery.setName(keyC);
		
		QueryResult<HColumn<String, Object>> result = columnQuery.execute();
		
		return result.get().getValue();
	}

	/**
	 * Put a value to colfamName<keyL, keyC>
	 * @param colfamName name of column family
	 * @param keyL line key
	 * @param keyC column key
	 */
	@Override
	public void put(String colfamName, Integer keyL, Integer keyC, Object value) {
		Mutator<Integer> mutator = HFactory.createMutator(_keyspace, IntegerSerializer.get());
		
		mutator.insert(keyL, colfamName, 
				HFactory.createColumn(keyC, value, IntegerSerializer.get(), ObjectSerializer.get()));
	}

	/**
	 * Put a value to colfamName<keyL, keyC>
	 * @param colfamName name of column family
	 * @param keyL line key
	 * @param keyC column key
	 */
	@Override
	public void put(String colfamName, String keyL, String keyC, Object value) {
		Mutator<String> mutator = HFactory.createMutator(_keyspace, StringSerializer.get());
		
		mutator.insert(keyL, colfamName, 
				HFactory.createColumn(keyC, value, StringSerializer.get(), ObjectSerializer.get()));
	}
}
