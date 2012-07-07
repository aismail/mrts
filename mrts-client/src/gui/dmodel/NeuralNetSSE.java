package gui.dmodel;

import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * Data model for SumOfSquaredError values
 * (use: JList Data Model)
 * SSE value are pulled from Cassandra Database 
 * while performing the neural net training
 * 
 * @author cbarca
 */
public class NeuralNetSSE {
	public static final String HEADER = "dd.mm hh:mm:ss zone, SSE";
	private long _timestamp;
	private double _x, _y;
	
	public NeuralNetSSE(double x, double y, long timestamp) {
		_x = x;
		_y = y;
		_timestamp = timestamp;
	}
	
	public double getX() {
		return _x;
	}
	
	public double getY() {
		return _y;
	}
	
	public long getTimestamp() {
		return _timestamp;
	}
	
	@Override
	public String toString() {
		
		if (_x == 0 && _y == 0 && _timestamp == 0) {
			return HEADER;
		}
		
		Date dt = new Date(_timestamp);
		return new SimpleDateFormat("dd.MM HH:mm:ss z").format(dt) + ", " + _y;
	}
}
