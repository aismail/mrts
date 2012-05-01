package network;

/**
 * Backpropagation Network Container.
 */
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Backpropagation network container.
 * 
 * @author gsc
 * @author cbarca
 */
public class Network implements Serializable {
	
	/**
	 * Constructor for backprop network
	 * @param learning_rate
	 * @param momentum
	 */
	public Network(double learning_rate, double momentum) {
		this.learning_rate = learning_rate;
		this.momentum = momentum;
		
		_middleLayers = new ArrayList<MiddleNode[]>();
		_arcz = new ArrayList<Arc>();
	}
	
	/**
	 * Input layer init
	 * @param input_population
	 */
    public void setInputLayer(int input_population) {
    	_inputz = new InputNode[input_population];
    	for (int ii = 0; ii < _inputz.length; ii++) {
    		_inputz[ii] = new InputNode();
    	}
    }
    
    /**
     * Add middle layer
     * @param middle_population
     */
    public void addMiddleLayer(int middle_population) {
    	MiddleNode[] _middlez = new MiddleNode[middle_population];
    	for (int ii = 0; ii < _middlez.length; ii++) {
    		_middlez[ii] = new MiddleNode(learning_rate, momentum);
    	}
    	
    	_middleLayers.add(_middlez);
    }
    
    /**
     * Ouput layer init
     * @param output_population
     */
    public void setOutputLayer(int output_population) {
    	_outputz = new OutputNode[output_population];
    	for (int ii = 0; ii < _outputz.length; ii++) {
    		_outputz[ii] = new OutputNode(learning_rate, momentum);
    	}
    }
    
    /**
     * Finalize structure, connect layers
     */
    public void finalizeStructure() {	
    	MiddleNode[] _middlez;
    	
    	_middlez = _middleLayers.get(0);
    	for (int jj = 0; jj < _inputz.length; jj++) {
    		for (int kk = 0; kk < _middlez.length; kk++) {
    			Arc arc = new Arc();
    			_inputz[jj].connect(_middlez[kk], arc);
    			_arcz.add(arc);
    		}
    	}
    	
    	if (_middleLayers.size() > 1) {
    		MiddleNode[] _middle1, _middle2;
    		
    		for (int ii = 0; ii < _middleLayers.size() - 1; ii++) {
    			_middle1 = _middleLayers.get(ii);
    			_middle2 = _middleLayers.get(ii + 1);
    			
    			for (int jj = 0; jj < _middle1.length; jj++) {
    	    		for (int kk = 0; kk < _middle2.length; kk++) {
    	    			Arc arc = new Arc();
    	    			_middle1[jj].connect(_middle2[kk], arc);
    	    			_arcz.add(arc);
    	    		}
    	    	}
    		}
    	}
	
    	_middlez = _middleLayers.get(_middleLayers.size() - 1);
    	for (int jj = 0; jj < _middlez.length; jj++) {
    		for (int kk = 0; kk < _outputz.length; kk++) {
    			Arc arc = new Arc();
    			_middlez[jj].connect(_outputz[kk], arc);
    			_arcz.add(arc);
    		}
    	}	
    }
    
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
    	return (_middleLayers);
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
    public List<Arc> getArcs() {
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
    	return 0.5 * qerror;
    }
        
    /**
     * Run network to classify pattern.
     * 
     * @param input node values (pattern to classify)
     * @return output node values (classification answer)
     */
    public double[] runNetWork(double[] input) {
    	MiddleNode[] _middlez;
    	
    	for (int ii = 0; ii < input.length; ii++) {
    		_inputz[ii].setValue(input[ii]);
    	}
	
    	for (int ii = 0; ii < _middleLayers.size(); ii++) {
    		_middlez = _middleLayers.get(ii);
    		
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
    		_outputz[ii].setError(truth[ii]);
    	}
	
    	for (int ii = _outputz.length - 1; ii >= 0; ii--) {
    		_outputz[ii].trainNode();
    		qerror += Math.pow(_outputz[ii].getError(), 2);
    	}
	
    	for (int ii = _middleLayers.size() - 1; ii >= 0; ii--) {
    		_middlez = _middleLayers.get(ii);
    		
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
     * Update weights in graph - batch mode
     */
    public void updateWeights() {
    	for (Arc arc : _arcz) {
    		arc.updateWeight();
    	}
    }
    
    /**
     * Quadratic error of the network
     */
    private double qerror = 0;
    
    /**
     * Arcs connect nodes
     */
    private List<Arc> _arcz;
    
    /**
     * Input nodes contain pattern to classify
     */
    private InputNode[] _inputz;
    
    /**
     * Opaque middle layers
     */
    private List<MiddleNode[]> _middleLayers;
    
    /**
     * Classifier result
     */
    private OutputNode[] _outputz;
    
    /**
     * Learning rate & momentum
     */
    private double learning_rate, momentum;
    
    /**
     * Eclipse generated
     */
    private static final long serialVersionUID = 5668812853290831632L;
}