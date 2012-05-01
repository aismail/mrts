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
 * Hash over cassandra database
 * 
 * @author cbarca
 */
public class HashClient implements IHashClient {
	// Private members
	private Keyspace _keyspace;
	private Mutator<Integer> _batchMutator;
	
	public HashClient(Keyspace keyspace) {
		_keyspace = keyspace;
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
				_keyspace, IntegerSerializer.get(), IntegerSerializer.get(), ObjectSerializer.get());
		
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
	 * @param colfamName column family name
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
	 * @param colfamName column family name
	 * @param keyL line key
	 * @param keyC column key
	 */
	@Override
	public void put(String colfamName, String keyL, String keyC, Object value) {
		Mutator<String> mutator = HFactory.createMutator(_keyspace, StringSerializer.get());
		
		mutator.insert(keyL, colfamName, 
				HFactory.createColumn(keyC, value, StringSerializer.get(), ObjectSerializer.get()));
	}
	
	/**
	 * Start batch put session
	 */
	@Override
	public void startBatchPut() {
		_batchMutator = HFactory.createMutator(_keyspace, IntegerSerializer.get());
	}
	
	/**
	 * Batch version of put operation
	 * @param colfamName column family name
	 * @param keyL line key
	 * @param keyC column key
	 */
	@Override
	public void batchPut(String colfamName, Integer keyL, Integer keyC, Object value) {
		_batchMutator.addInsertion(keyL, colfamName, 
				HFactory.createColumn(keyC, value, IntegerSerializer.get(), ObjectSerializer.get()));
	}

	/**
	 * Finalize batch put session
	 */
	@Override
	public void finalizeBatchPut() {
		_batchMutator.execute();
	}

	/**
	 * Remove the value from colfamName<keyL, keyC>  
	 * @param colfamName name of column family
	 * @param keyL
	 * @param keyC
	 */
	@Override
	public void remove(String colfamName, Integer keyL, Integer keyC) {
		Mutator<Integer> mutator = HFactory.createMutator(_keyspace, IntegerSerializer.get());
		
		mutator.delete(keyL, colfamName, keyC, IntegerSerializer.get());		
	}

	/**
	 * Put a value to colfamName<keyL, keyC>
	 * @param colfamName column family name
	 * @param keyL line key
	 * @param keyC column key
	 */
	@Override
	public void remove(String colfamName, String keyL, String keyC) {
		Mutator<String> mutator = HFactory.createMutator(_keyspace, StringSerializer.get());
		
		mutator.delete(keyL, colfamName, keyC, StringSerializer.get());
	}
}
