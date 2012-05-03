package fnn.network;

import java.io.Serializable;

import fnn.util.SequenceGenerator;


/**
 * Abstract parent for arcs and nodes
 *
 * @author cbarca
 */
public abstract class AbstractArcNode implements Serializable {

	/**
     * Each arc and node has a globally unique identifier
     * @return globally unique identifier
     */
    public int getId() {
    	return(id);
    }
    
    /**
     * Globally unique identifier
     */
    final int id = SequenceGenerator.getId();

    /**
	 * Eclipse generated
	 */
	private static final long serialVersionUID = -876174107044791987L;
}
