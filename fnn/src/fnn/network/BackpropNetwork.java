package fnn.network;

import java.util.ArrayList;

import fnn.util.NetworkStruct;
import fnn.util.SequenceGenerator;


/**
 * Backpropagation Network (feedforward network)
 * Backpropagation network container
 * Extends the AbstractNetwork class 
 * 
 * @author cbarca
 */
public class BackpropNetwork extends AbstractNetwork {

	/**
	 * Constructor for backprop network
	 * @param learning_rate rate of learning
	 * @param momentum momentun 
	 */
	public BackpropNetwork(double learning_rate, double momentum) {
		// Reset id-counting (begin always from 1)
		SequenceGenerator.reset();
		
		_learning_rate = learning_rate;
		_momentum = momentum;
		
		_middle_layers = new ArrayList<MiddleNode[]>();
		_arcz = new ArrayList<AbstractArc>();
	}
	
	/**
	 * Constructor for backprop using net_struct defined
	 * @param net_struct neural-network structure
	 */
	public BackpropNetwork(NetworkStruct net_struct, 
			double learning_rate, double momentum) {
		this(learning_rate, momentum);
		
		this.setInputLayer(net_struct.getInputPop());
		
		for (Integer middp : net_struct.getMiddlezPop()) {
			this.addMiddleLayer(middp);
		}
    	
		this.setOutputLayer(net_struct.getOutputPop());
    	
		this.finalizeStructure();
	}
	
    /**
     * Finalize structure, connect layers 
     */
	@Override
    public void finalizeStructure() {	
    	MiddleNode[] _middlez;
    	
    	_middlez = _middle_layers.get(0);
    	for (int jj = 0; jj < _inputz.length; jj++) {
    		for (int kk = 0; kk < _middlez.length; kk++) {
    			AbstractArc arc = new BackpropArc();
    			_inputz[jj].connect(_middlez[kk], arc);
    			_arcz.add(arc);
    		}
    	}
    	
    	if (_middle_layers.size() > 1) {
    		MiddleNode[] _middle1, _middle2;
    		
    		for (int ii = 0; ii < _middle_layers.size() - 1; ii++) {
    			_middle1 = _middle_layers.get(ii);
    			_middle2 = _middle_layers.get(ii + 1);
    			
    			for (int jj = 0; jj < _middle1.length; jj++) {
    	    		for (int kk = 0; kk < _middle2.length; kk++) {
    	    			AbstractArc arc = new BackpropArc();
    	    			_middle1[jj].connect(_middle2[kk], arc);
    	    			_arcz.add(arc);
    	    		}
    	    	}
    		}
    	}
	
    	_middlez = _middle_layers.get(_middle_layers.size() - 1);
    	for (int jj = 0; jj < _middlez.length; jj++) {
    		for (int kk = 0; kk < _outputz.length; kk++) {
    			AbstractArc arc = new BackpropArc();
    			_middlez[jj].connect(_outputz[kk], arc);
    			_arcz.add(arc);
    		}
    	}	
    }
	
	// Private members
	
	/**
	 * Eclipse generated
	 */
	private static final long serialVersionUID = 122573540602270065L;
}
