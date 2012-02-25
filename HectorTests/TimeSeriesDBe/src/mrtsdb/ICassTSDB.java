package mrtsdb;

import java.util.List;

import mrtsis.*;
import mrtsdm.*;


/**
 * @author Cristi.Barca
 * 
 * Cassandra Time Series Database interface.
 * 
 * Interfaces a Time Series Database using SensorData class type.
 * 
 */
public interface ICassTSDB {
	// One-timestamp CRUD
	void add(SensorPkg pkgs);
	SensorData get(long ts);
	SensorData get(long ts, List<String> colNames);
	void delete(long ts);
	
	// Multiple-timestamps CRUD
	void add(List<SensorPkg> lpkgs);
	void add(SensorPkg spkg, int start, int curr, int finish);
	List<SensorData> get(long tsBegin, long tsEnd);
	List<SensorData> get(long tsBegin, long tsEnd, List<String> cols);
	void delete(long tsStart, long tsEnd);
}
