package neuralnet.network;

/**
 * Concrete middle node
 */
import java.util.Iterator;

/**
 * Concrete middle node
 *
 * @author gsc
 * @author cbarca
 */
public class MiddleNode extends OutputNode {
    
    /**
     * ctor 
     * @param learning_rate
     * @param momentum
     */
    public MiddleNode(double learning_rate, double momentum) {
    	super(learning_rate, momentum);
    }

	/**
	 * Compute node error
	 * @return node error
	 */
	private double computeError() {
		double total = 0.0;

		Iterator<Arc> ii = output_arcs.iterator();
		while (ii.hasNext()) {
			Arc arc = ii.next();
			total += arc.getWeightedOutputError();
		}

		double result = this.sigmoidDerivate(value) * total;

		return (result);
	}

	/**
	 * Update input weights based on error (delta rule)
	 */
	public void trainNode() {
		error = computeError();

		Iterator<Arc> ii = input_arcs.iterator();
		while (ii.hasNext()) {
			Arc arc = ii.next();
			double gradient = error * arc.getInputValue();
			arc.passGradient(gradient, learning_rate);
		}
	}
    
    /**
     * Return description of object
     * @return description of object
     */
    public String toString() {
    	return (toString("MiddleNode:"));
    }
    
    /**
     * Eclipse generated
     */
    private static final long serialVersionUID = -1713752591254222978L;
}
