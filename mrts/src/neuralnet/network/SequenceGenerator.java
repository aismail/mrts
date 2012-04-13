package neuralnet.network;

/**
 * Provide unique ID for arcs/nodes
 */

/**
 * Provide unique ID for arcs/nodes
 * 
 * @author gsc
 * @author cbarca
 */
public class SequenceGenerator {
	/**
	 * TODO: to_be_removed when we move to _cluster_
	 */
	
    /**
     * Each arc/node has a unique identifier. 
     * This was to help w/development, but it doesn't hurt to retain.
     * @return unique identifier
     */
    public static synchronized int getId() {
    	return(_id++);
    }
    
    /**
     * Reset counting
     */
    public static void reset() {
    	_id = 1;
    }
    
    /**
     * Contains the next Arc/Node identifier.
     */
    private static int _id = 1;
}
