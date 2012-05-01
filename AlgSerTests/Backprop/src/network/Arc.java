package network;

/**
 * Arcs connect nodes
 */

/**
 * Arcs connect nodes
 *
 * @author gsc
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
     * Define output node
     * @param arg output node
     */
    public void setOutputNode(AbstractNode arg) {
        _out = arg;
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
        return(_out.getBpError() * _weight);
    }
    
    /**
     * Update weight's delta by adding new value to current delta
     * @param arg new value added to current delta
     */
    public void passGradient(double gradient, double learning_rate) {
        OutputNode on = (OutputNode) _out;
        double deltaw = learning_rate * gradient;
        _deltaw += deltaw + on.getMomentum() * _delta;
        _delta = deltaw;
    }
    
    /**
     * Update link weight by adding delta to current weight
     */
    public void updateWeight() {
        _weight += _deltaw;
        _deltaw = 0;
    }

	/**
	 * Return description of object
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
    
    /**
     * Weights are initialized to a random value
     */    
    private double _weight = Mathz.getBoundedRandom(-1.0, 1.0);
    
    /**
     * Previous weight change
     */
    private double _delta = 0;
    
    /**
     * All weight changes
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
}
