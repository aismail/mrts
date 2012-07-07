package fnn.network;

/**
 * Backpropagation Arc
 * Concrete class for Backpropagation network arc
 * Extends the AbstractArc class
 * 
 * @author cbarca
 */
public class BackpropArc extends AbstractArc {
	
	/**
     * Update weight's delta by adding new value to current delta
     * @param arg new value added to current delta
     */
	@Override
    public void passGradient(double gradient, double learning_rate) {
		OutputNode on = (OutputNode) _out;
        double deltaw = learning_rate * gradient;
        _deltaw += deltaw + on.getMomentum() * _delta;
        _delta = deltaw;
    }
    
    /**
     * Update link weight by a local adaptive function
     */
	@Override
    public void updateWeight() {
		_weight += _deltaw;
        _deltaw = 0;
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
	
	/**
	 * Eclipse generated
	 */
	private static final long serialVersionUID = 4568667747520248266L;
}
