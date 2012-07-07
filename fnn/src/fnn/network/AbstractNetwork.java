package fnn.network;

import java.io.Serializable;
import java.util.List;

/**
 * Abstract Network 
 * Abstract class for feedforward neural network type
 * 
 * @author cbarca
 */
public abstract class AbstractNetwork implements Serializable {

	/**
	 * Input layer init
	 * @param input_population input units
	 */
    public void setInputLayer(int input_population) {
    	_inputz = new InputNode[input_population];
    	for (int ii = 0; ii < _inputz.length; ii++) {
    		_inputz[ii] = new InputNode();
    	}
    }
    
    /**
     * Add middle layer
     * @param middle_population hidden units
     */
    public void addMiddleLayer(int middle_population) {
    	MiddleNode[] _middlez = new MiddleNode[middle_population];
    	for (int ii = 0; ii < _middlez.length; ii++) {
    		_middlez[ii] = new MiddleNode(_learning_rate, _momentum);
    	}
    	
    	_middle_layers.add(_middlez);
    }
    
    /**
     * Output layer initialization
     * @param output_population output units
     */
    public void setOutputLayer(int output_population) {
    	_outputz = new OutputNode[output_population];
    	for (int ii = 0; ii < _outputz.length; ii++) {
    		_outputz[ii] = new OutputNode(_learning_rate, _momentum);
    	}
    }

    /**
     * Finalize structure, connect layers 
     */
    public abstract void finalizeStructure();
    
    /**
     * Retrieve input nodes, only used by JUnit
     * @return input nodes
     */
    public InputNode[] getInputNodes() {
    	return (_inputz);
    }
    
    /**
     * Retrieve middle layers, only used by JUnit
     * @return middle nodes
     */
    public List<MiddleNode[]> getMiddleLayers() {
    	return (_middle_layers);
    }
    
    /**
     * Retrieve output nodes, only used by JUnit
     * @return output nodes
     */
    public OutputNode[] getOutputNodes() {
    	return (_outputz);
    }
    
    /**
     * Retrieve arcs, only used by JUnit
     * @return arcs
     */
    public List<AbstractArc> getArcs() {
    	return (_arcz);
    }
    
    /**
     * Set the quadratic error
     */
    public void resetQError() {
    	qerror = 0.0; 
    }
    
    /**
     * Retrieve the quadratic error
     * @return quadratic error (double value)
     */
    public double getQError() {
    	return qerror / (double)(_outputz.length);
    }
        
    /**
     * Run network to classify pattern.
     * @param input node values (pattern to classify)
     * @return output node values (classification answer)
     */
    public double[] runNetWork(double[] input) {
    	MiddleNode[] _middlez;
    	
    	for (int ii = 0; ii < input.length; ii++) {
    		_inputz[ii].setValue(input[ii]);
    	}
	
    	for (int ii = 0; ii < _middle_layers.size(); ii++) {
    		_middlez = _middle_layers.get(ii);
    		
    		for (int jj = 0; jj < _middlez.length; jj++) {
    			_middlez[jj].runNode();
    		}
    	}
	
    	for (int ii = 0; ii < _outputz.length; ii++) {
    		_outputz[ii].runNode();
    	}
	
    	double[] result = new double[_outputz.length];
    	for (int ii = 0; ii < _outputz.length; ii++) {
    		result[ii] = _outputz[ii].getValue();
    	}
	
    	return (result);
    }
    
    /**
     * Train by backpropagation (move backward through nodes and tweak weights w/error values).
     * @param truth pattern w/output (truth) for network to learn
     */
    public double[] trainNetWork(double[] truth) {
    	MiddleNode[] _middlez;
    	
    	for (int ii = 0; ii < truth.length; ii++) {
    		_outputz[ii].setOutputError(truth[ii]);
    	}
	
    	for (int ii = _outputz.length - 1; ii >= 0; ii--) {
    		_outputz[ii].trainNode();
    		qerror += _outputz[ii].getOutputError() * 
    			_outputz[ii].getOutputError();
    	}
	
    	for (int ii = _middle_layers.size() - 1; ii >= 0; ii--) {
    		_middlez = _middle_layers.get(ii);
    		
    		for (int jj = _middlez.length - 1; jj >= 0; jj--) {
    			_middlez[jj].trainNode();
    		}
    	}
	
    	double[] result = new double[_outputz.length];
    	for (int ii = 0; ii < _outputz.length; ii++) {
    		result[ii] = _outputz[ii].getValue();
    	}
	
    	return (result);
    }
    
    /**
     * Update network's weights
     */
    public void updateWeights() {
    	for (AbstractArc arc : _arcz) {
    		arc.updateWeight();
    	}
    }

    /**
     * Reset aggregate output error of output nodes
     */
    public void resetAggOutputError() {
    	for (int i = 0; i < _outputz.length; i++) {
    		_outputz[i].resetAggOutputError();
    	}
    }
    
    // Protected members
    
    /**
     * Quadratic error of the network
     */
    protected double qerror = 0;
    
    /**
     * Arcs connect nodes
     */
    protected List<AbstractArc> _arcz;
    
    /**
     * Input nodes contain pattern to classify
     */
    protected InputNode[] _inputz;
    
    /**
     * Hidden middle layers
     */
    protected List<MiddleNode[]> _middle_layers;
    
    /**
     * Classifier result
     */
    protected OutputNode[] _outputz;
    
    /**
     * Learning rate & momentum
     */
    protected double _learning_rate = 1.0, 
    	_momentum = 0;
    
    // Private members
    
    /**
	 * Eclipse generated 
	 */
	private static final long serialVersionUID = 3305052012837898632L;
}
