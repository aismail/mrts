package neuralnet.network;

import java.io.Serializable;

/**
 * Abstract ArcNode
 * Abstract top class for arcs and nodes (serializable)
 * Both, arcs and nodes, have in common consecutive id numbers 
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

    // Private members
    
    /**
	 * Eclipse generated
	 */
	private static final long serialVersionUID = -876174107044791987L;
}
