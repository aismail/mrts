package neuralnet.mapred.dmodel;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.*;

/**
 * Pair Data Writable 
 * Hadoop Data Type, for pair<key, value> transfers to reducers
 * 
 * @author cbarca
 */
public class PairDataWritable implements Writable {
	// Private members
	private int _destination;
	private double _value;
	
	public PairDataWritable(int destination, double value) {
		_destination = destination;
		_value = value;
	}
	
	public PairDataWritable() {
		this(0, 0.0);
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
		arg0.writeInt(_destination);
		arg0.writeDouble(_value);
	}

	@Override
	public String toString() {
		return _destination + " - " + _value;
	}
}
