package fnn.network;

import fnn.util.Mathz;

/**
 * Abstract Arc
 * Abstract class for arc connection (network's synapses)
 * 
 * @author cbarca
 */
public abstract class AbstractArc extends AbstractArcNode {

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
    public abstract void passGradient(double gradient, double learning_rate);
    
    /**
     * Update link weight by a local adaptive function
     */
    public abstract void updateWeight();

    /**
	 * Return description of object
	 * @return description of object
	 */
    @Override
	public abstract String toString();
    
    // Protected members
    
    /**
     * Weights are initialized to a random value
     */    
    protected double _weight = Mathz.getBoundedRandom(-1.0, 1.0);
    
    /**
     * Gradients (batch mode)
     */
    protected double _gradient = 0;

    /**
     * Bias
     */
    protected double _delta = 0.1;
    
    /**
     * Weight change
     */
    protected double _deltaw = 0;
    
    /**
     * AbstractNode which arc is coming from
     */
    protected AbstractNode _in;
    
    /**
     * AbstractNode which arc is going to
     */
    protected AbstractNode _out;
    
    // Private members
    
    /**
	 * Eclipse generated 
	 */
	private static final long serialVersionUID = 6249220268524315201L;
}
