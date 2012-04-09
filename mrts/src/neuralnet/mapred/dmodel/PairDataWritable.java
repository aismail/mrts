package neuralnet.mapred.dmodel;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.*;

/**
 * Arc Data  
 * Hadoop Data Type, for value transfer to reducers.
 * 
 * @author cbarca
 */
public class PairDataWritable implements Writable {

	private int _destination;
	private double _value;
	
	public PairDataWritable(int destination, double value) {
		_destination = destination;
		_value = value;
	}
	
	public PairDataWritable(double error) {
		this(0, error);
	}
	
	public int getDestination() {
		return _destination;
	}
	
	public double getValue() {
		return _value;
	}
	
	@Override
	public void readFields(DataInput arg0) throws IOException {
		_destination = arg0.readInt();
		_value = arg0.readDouble();
	}

	@Override
	public void write(DataOutput arg0) throws IOException {
		arg0.write(_destination);
		arg0.writeDouble(_value);
	}

	@Override
	public String toString() {
		return _destination + " - " + _value;
	}
}
