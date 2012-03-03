package mrtsdm;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * @author Cristi.Barca
 * 
 * Sensor Data class.
 * Describes the data inside the "package".
 * Uses a HashMap to store key-value.
 *  
 */
public class SensorData {
	// Private members	
	private Map<String, Float> mp;
	
	/**
	 * Implicit constructor.
	 * Initializes the hash-map.
	 */
	public SensorData() {
		mp = new HashMap<String, Float>();
	} 
	
	/**
	 * Gets the entry set of the hash-map.
	 * @return a set of entries from the hash-map
	 */
	public Set<Entry<String, Float>> getEntrySet() {
		return mp.entrySet();
	}
	
	/**
	 * Puts the value to the specified key in hash-map.
	 * @param key = the key (column name)
	 * @param value = the value (column value)
	 */
	public void putValue(String key, Float value) {
		mp.put(key, value);
	}
	
	/**
	 * Gets the value from the specified key in hash-map.
	 * @param key = the key (column name)
	 * @return a float value (the value we want to get)
	 */
	public float getValue(String key) {
		return ((Float)mp.get(key)).floatValue();
	}
}
