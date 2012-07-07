package fnn.network;

import java.util.ArrayList;

import fnn.util.NetworkStruct;
import fnn.util.SequenceGenerator;


/**
 * Resilient Backpropagation Network
 * Resilient Backpropagation network container
 * Extends the AbstractNetwork class
 * 
 * @author cbarca
 */
public class RpropNetwork extends AbstractNetwork {

	/**
	 * Constructor for rprop network
	 */
	public RpropNetwork() {
		// Reset id-counting (begin always from 1)
		SequenceGenerator.reset();
	
		_middle_layers = new ArrayList<MiddleNode[]>();
		_arcz = new ArrayList<AbstractArc>();
	}
	
	/**
	 * Constructor for rprop using net_struct defined
	 * @param net_struct neural-network structure
	 */
	public RpropNetwork(NetworkStruct net_struct) {
		this();
		
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
    			AbstractArc arc = new RpropArc();
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
    	    			AbstractArc arc = new RpropArc();
    	    			_middle1[jj].connect(_middle2[kk], arc);
    	    			_arcz.add(arc);
    	    		}
    	    	}
    		}
    	}
	
    	_middlez = _middle_layers.get(_middle_layers.size() - 1);
    	for (int jj = 0; jj < _middlez.length; jj++) {
    		for (int kk = 0; kk < _outputz.length; kk++) {
    			AbstractArc arc = new RpropArc();
    			_middlez[jj].connect(_outputz[kk], arc);
    			_arcz.add(arc);
    		}
    	}	
    }
        
	// Private members
	
    /**
     * Eclipse generated
     */
    private static final long serialVersionUID = 5668812853290831632L;
}
