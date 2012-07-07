package neuralnet.network;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Network Structure
 * Defines the structure of a feedforward neural network (serializable)  
 * 
 * @author cbarca
 */
public class NetworkStruct implements Serializable {
	// Private members
	private static final long serialVersionUID = 1L;
	private double _error = 0.1, _threshold = 0.1; 
	private int _ninput, _noutput, _max_epochs = -1;
	private List<Integer> _nmiddlez;
	
	public NetworkStruct() {
		this(0.1, 0.1, -1);
	}
	
	public NetworkStruct(double error) {
		this(error, 0.1);
	}
	
	public NetworkStruct(double error, double threshold) {
		this(error, threshold, -1);
	}
	
	public NetworkStruct(double error, double threshold, int max_epochs) {
		_error = error;
		_threshold = threshold;
		_max_epochs = max_epochs;
		
		_nmiddlez = new ArrayList<Integer>();
	}
	
	
	public void setInputPop(int ninput) {
		_ninput = ninput;
	}
	
	public void setOutputPop(int noutput) {
		_noutput = noutput;
	}
	
	public void addMiddlePop(int middlen) {
		_nmiddlez.add(middlen);
	}
	
	public int getInputPop() {
		return _ninput;
	}
	
	public int getOutputPop() {
		return _noutput;
	}
	
	public List<Integer> getMiddlezPop() {
		return _nmiddlez;
	}
	
	public void setError(double error) {
		_error = error;
	}
	
	public double getError() {
		return _error;
	}
	
	public void setThreshold(double threshold) {
		_threshold = threshold;
	}
	
	public double getThreshold() {
		return _threshold;
	}
	
	public void setMaxEpochs(int max_epochs) {
		_max_epochs = max_epochs;
	}
	
	public int getMaxEpochs() {
		return _max_epochs;
	}
}
