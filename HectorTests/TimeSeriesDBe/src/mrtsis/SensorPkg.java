package mrtsis;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import mrtsdm.*;

/**
 * @author Cristi.Barca
 * 
 * Sensor Package class.
 * Describes the "package" structure received from sensors.
 * A package from sensors contains the timestamp & data.
 * 
 */
public class SensorPkg {
	// Private members
	private DateFormat formater;
	private Date timedate;
	private long timestamp;
	private SensorData sdm;
	
	// The database model:
	// RowKey: timestamp
	// ColumnName/Key: sdm.col.name 
	// ColumValue: sdm.col.val
	
	/**
	 * Implicit constructor.
	 */
	private SensorPkg() {
		sdm = new SensorData();		
	}
	
	/**
	 * Specialized constructor
	 * @param keys = the names of the columns
	 * @param values = the values of the columns
	 */
	public SensorPkg(String[] keys, String[] values) {
		this();
		
		int i;
		 
		// TimeDate Format: 09:30:09.797 31/01/2003
		 
		formater = new SimpleDateFormat("H:mm:ss.SSS dd/MM/yyy");
		formater.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		try {
			
			timedate = (Date)formater.parse(values[0]);
			timestamp = timedate.getTime();
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		for (i = 1; i < values.length; i++) {
			sdm.putValue(keys[i], Float.parseFloat(values[i]));
		}
	}
	
	/**
	 * Getter for timestamp.
	 * @return timestamp long value
	 */
	public long getTimestamp() {
		return timestamp;
	}
	
	/**
	 * Getter for sensor data.
	 * @return SensorData object
	 */
	public SensorData getData() {
		return sdm;
	}
}
