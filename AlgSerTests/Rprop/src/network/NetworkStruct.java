package network;

import java.util.ArrayList;
import java.util.List;

/**
 * Network Structure
 * Defines the structure of a feedforward neural network. 
 * 
 * @author cbarca
 */
public class NetworkStruct {
	private double _error = 0.1; 
	private int _ninput, _noutput, _max_epochs = -1;
	private List<Integer> _nmiddlez;
	
	public NetworkStruct(double error) {
		this(error, -1);
	}
	
	public NetworkStruct(double err, int max_epochs) {
		_error = err;
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
	
	public double getError() {
		return _error;
	}
	
	public int getMaxEpochs() {
		return _max_epochs;
	}
}
