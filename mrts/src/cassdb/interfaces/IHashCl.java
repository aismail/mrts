package cassdb.interfaces;

/**
 * Interface for Hash Client
 * 
 * @author cbarca
 */
public interface IHashCl {
	// GET methods
	public Object get(String colfamName, Integer keyL, Integer keyC);
	public Object get(String colfamName, String keyL, String keyC);
	
	// PUT methods
	public void put(String colfamName, Integer keyL, Integer keyC, Object value);
	public void put(String colfamName, String keyL, String keyC, Object value);
}
