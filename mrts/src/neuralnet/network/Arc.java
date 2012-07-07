package neuralnet.network;

/**
 * Neural Network Arc
 * Arcs connect nodes
 * Extends AbstractArcNode
 *
 * @author cbarca
 */
public class Arc extends AbstractArcNode {

    /**
     * Define input node
     * @param arg input node
     */
    public void setInputNode(AbstractNode arg) {
        _in = arg;
    }
    
    /**
     * Return the input node of the arc
     * @return input node
     */
    public AbstractNode getInputNode() {
    	return _in;
    }
    
    /**
     * Define output node
     * @param arg output node
     */
    public void setOutputNode(AbstractNode arg) {
        _out = arg;
    }
    
    /**
     * Return the output node of the arc
     * @return output node
     */
    public AbstractNode getOutputNode() {
    	return _out;
    }
    
    /**
     * Set weight value
     * @param weight value
     */
    public void setWeight(double weight) {
    	_weight = weight;
    }
    
    /**
     * Return the weight value of an arc
     * @return weight value
     */
    public double getWeight() {
    	return _weight;
    }
    
    /**
     * Return the node value of input arc
     * @return node value of input arc
     */
    public double getInputValue() {
        return(_in.getValue());
    }
    
    /**
     * Return the product of a input node value and arc weight
     * @return product of input node value and arc weight
     */
    public double getWeightedInputValue() {
        return(_in.getValue() * _weight);
    }
    
    /**
     * Return the product of a output node backprop error and arc weight
     * @return the product of a output node backprop error and arc weight
     */
    public double getWeightedOutputError() {
        return(_out.getError() * _weight);
    }
    
    /**
     * Return the value of the gradient
     * @return the gradient's value
     */
    public double getGradient() {
    	return _gradient;
    }
    
    /**
     * Update weight's delta by adding new value to current delta
     * @param arg new value added to current delta
     */
    public void passGradient(double gradient, double learning_rate) {
        // batch addition of the gradient (rprop)
    	_gradient +=  -1 * gradient;
    }
    
    /**
     * Update link weight by a local adaptive function
     */
    public void updateWeight() {
    	// (rprop)
    	double change; 

    	if (_deltaw == 0 && _last_gradient == 0 && _delta == 0.1) {
    		change = Math.signum(_gradient);
    	}
    	else {
    		change = Math.signum(_gradient * _last_gradient);
    	}

    	if (change > 0) {
    		_delta = Math.min(_delta * NPLUS, DMAX);
    		_deltaw = -1 * Math.signum(_gradient) * _delta;
    		_weight += _deltaw;
    		_last_gradient = _gradient;
    	}
    	else if (change < 0) {
    		_delta = Math.max(_delta * NMINUS, DMIN);
    		_last_gradient = 0;
    	} 
    	else if (change == 0) {
    		_deltaw = -1 * Math.signum(_gradient) * _delta;
    		_weight += _deltaw;
    		_last_gradient = _gradient;
    	}

    	_gradient = 0;
    }
    
	/**
	 * Return description of object
	 * 
	 * @return description of object
	 */
	public String toString() {
		String result = "Arc:" + id + " weight:" + _weight + " delta:" + _delta;

		if (_in == null) {
			result += " in:null";
		} else {
			result += " in:" + _in.getId();
		}

		if (_out == null) {
			result += " out:null";
		} else {
			result += " out:" + _out.getId();
		}

		return (result);
	}
    
	// Private members
	
    /**
     * Weights are initialized to a random value
     */    
    private double _weight = Mathz.getBoundedRandom(-1.0, 1.0);
    
    /**
     * Bias
     */
    private double _delta = 0.1;
    
    /**
     * Gradients (batch mode)
     */
    private double _gradient = 0;
    private double _last_gradient = 0;
    
    /**
     * Weight change
     */
    private double _deltaw = 0;
    
    /**
     * AbstractNode which arc is coming from
     */
    private AbstractNode _in;
    
    /**
     * AbstractNode which arc is going to
     */
    private AbstractNode _out;
    
    /**
     * Eclipse generated
     */
    private static final long serialVersionUID = -8884064153744639354L;
    
    // Constants
    
    /**
     * Rprop constants
     */
    public static final double NPLUS = 1.2, NMINUS = 0.5, 
    	DMAX = 50, DMIN = 1e-6;
}
