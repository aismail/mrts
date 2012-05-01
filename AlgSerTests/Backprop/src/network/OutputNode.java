package network;

/**
 * Concrete output node
 */
import java.util.Iterator;

/**
 * Concrete output node
 * 
 * @author gsc
 * @author cbarca
 */
public class OutputNode extends AbstractNode {

	/**
	 * Constructor
	 * @param learning_rate
	 * @param momentum
	 */
	public OutputNode(double learning_rate, double momentum) {
		this.learning_rate = learning_rate;
		this.momentum = momentum;
	}
    
    /**
     * Return learning rate
     * @return learning rate
     */
    public double getLearningRate() {
    	return (learning_rate);
    }
    
    /**
     * Return momentum term
     * @return momentum term
     */
    public double getMomentum() {
    	return (momentum);
    }

	/**
	 * Update node value by summing weighted inputs
	 */
	public void runNode() {
		double total = 0.0;

		Iterator<Arc> ii = input_arcs.iterator();
		while (ii.hasNext()) {
			Arc arc = ii.next();
			total += arc.getWeightedInputValue();
		}

		value = sigmoidTransfer(total);
	}

	/**
	 * Update input weights based on error (delta rule)
	 */
	public void trainNode() {
		error = computeError();
		bpError = computeBpError();

		Iterator<Arc> ii = input_arcs.iterator();
		while (ii.hasNext()) {
			Arc arc = ii.next();
			double gradient = bpError * arc.getInputValue();
			arc.passGradient(gradient, learning_rate);
		}
	}
    
    /**
     * Return sigmoid transfer value, result 0.0 < value < 1.0
     * @return sigmoid transfer value, result 0.0 < value < 1.0
     */
    private double sigmoidTransfer(double value) {
    	return (1.0 / (1.0 + Math.exp(-value)));
    }
    
    /**
     * Compute output node error
     * @return output node error
     */
    private double computeError() {
    	return (error - value);
    }
    
    /**
     * Compute output node backprop error using the derivative of 
     * the sigmoid transfer function.
     * @return output node backrop error
     */
    private double computeBpError() {
    	return (value * (1.0 - value) * error);
    }
    
    /**
     * Return description of object
     * @return description of object
     */
    public String toString() {
    	return (toString("OutputNode:"));
    }
    
    /**
     * Return description of object
     * @return description of object
     */
    public String toString(String prefix) {
    	String result = prefix + super.toString() + " learning rate:" + learning_rate + " momentum:" + momentum;
	
    	return (result);
    }
    
    /**
     * Learning rate is used to help compute error term.
     */
    double learning_rate;
    
    /**
     * Momentum term is used to compute weight in Arc
     */
    double momentum;
    
    /**
     * Eclipse generated
     */
    private static final long serialVersionUID = -8313299157918441811L;
}
