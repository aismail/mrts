package cassdb.interfaces;

import java.util.List;

/**
 * Interface for Hash Client
 * 
 * @author cbarca
 */
public interface IHashClient {
	// GET methods
	public Object get(String colfamName, Integer keyL, Integer keyC);
	public Object get(String colfamName, String keyL, Long keyC);
	public Object get(String colfamName, String keyL, String keyC);
	public List<Object> get(String colfamName, String keyL, Long k1, Long k2, int maxc);
	
	// PUT methods
	public void put(String colfamName, Integer keyL, Integer keyC, Object value);
	public void put(String colfamName, String keyL, Long keyC, Object value);
	public void put(String colfamName, String keyL, String keyC, Object value);
	public void startBatchPut();
	public void batchPut(String colfamName, Integer keyL, Integer keyC, Object value);
	public void finalizeBatchPut();
	
	// REMOVE methods
	public void remove(String colfamName, Integer keyL, Integer keyC);
	public void remove(String colfamName, String keyL, Long keyC);
	public void remove(String colfamName, String keyL, String keyC);	
}
