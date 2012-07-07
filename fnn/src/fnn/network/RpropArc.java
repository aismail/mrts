package fnn.network;

/**
 * Resilient Backpropagation Arc
 * Concrete class for Resilient Backpropagation network arc
 * Extends the AbstractArc class
 *
 * @author cbarca
 */
public class RpropArc extends AbstractArc {
	
    /**
     * Update weight's delta by adding new value to current delta
     * @param arg new value added to current delta
     */
	@Override
    public void passGradient(double gradient, double learning_rate) {
        // batch addition of the gradient (rprop)
    	_gradient +=  -1 * gradient;
    }
    
    /**
     * Update link weight by a local adaptive function
     */
	@Override
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
	 * @return description of object
	 */
	@Override
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
     * Gradients (batch mode)
     */
    private double _last_gradient = 0;
       
    /**
     * Rprop constants
     */
    public static final double NPLUS = 1.2, NMINUS = 0.5, 
    	DMAX = 50, DMIN = 1e-6;
    
    /**
     * Eclipse generated
     */
    private static final long serialVersionUID = -8884064153744639354L;
}
