package cassdb.interfaces;

import java.util.List;
import java.util.Map;

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
	public Map<Integer, Object> getRow(String colfamName, Integer keyL, int rowSize);
	public Map<Integer, Object> getPaginateRow(String colfamName, Integer keyL, int colsPageSize);
	public Map<Integer, Map<Integer, Object>> getPaginateColFam(String colfamName, int rowsPageSize);
	public List<Object> getTimestampSeries(String colfamName, String keyL, 
			Long k1, Long k2, int maxCount);
	
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
