package cassdb.interfaces;

/**
 * Interface for Hash Client
 * 
 * @author cbarca
 */
public interface IHashClient {
	// GET methods
	public Object get(String colfamName, Integer keyL, Integer keyC);
	public Object get(String colfamName, String keyL, String keyC);
	
	// PUT methods
	public void put(String colfamName, Integer keyL, Integer keyC, Object value);
	public void put(String colfamName, String keyL, String keyC, Object value);
	public void startBatchPut();
	public void batchPut(String colfamName, Integer keyL, Integer keyC, Object value);
	public void finalizeBatchPut();
	
	// REMOVE methods
	public void remove(String colfamName, Integer keyL, Integer keyC);
	public void remove(String colfamName, String keyL, String keyC);
}
