package network;

/**
 * Concrete middle node
 */
import java.util.Iterator;

/**
 * Concrete middle node
 *
 * @author gsc
 */
public class MiddleNode extends OutputNode {
    
    /**
     * ctor
     * 
     * @param learning_rate
     * @param momentum
     */
    public MiddleNode(double learning_rate, double momentum) {
    	super(learning_rate, momentum);
    }

	/**
	 * Compute node error
	 * 
	 * @return node error
	 */
	private double computeBpError() {
		double total = 0.0;

		Iterator<Arc> ii = output_arcs.iterator();
		while (ii.hasNext()) {
			Arc arc = ii.next();
			total += arc.getWeightedOutputError();
		}

		double result = value * (1.0 - value) * total;

		return (result);
	}

	/**
	 * Update input weights based on error (delta rule)
	 */
	public void trainNode() {
		bpError = computeBpError();

		Iterator<Arc> ii = input_arcs.iterator();
		while (ii.hasNext()) {
			Arc arc = ii.next();
			double delta = learning_rate * bpError * arc.getInputValue();
			arc.updateWeight(delta);
		}
	}
    
    /**
     * Return description of object
     * 
     * @return description of object
     */
    public String toString() {
    	return (toString("MiddleNode:"));
    }
    
    /**
     * eclipse generated
     */
    private static final long serialVersionUID = -1713752591254222978L;
}

/*
 * Copyright 2009 Digital Burro, INC
 * Created on August 31, 2009 by gsc
 */
