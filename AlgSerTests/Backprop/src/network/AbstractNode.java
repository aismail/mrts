package network;

/**
 * Abstract parent for nodes
 */
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Abstract parent for nodes
 * 
 * @author gsc
 * @author cbarca
 */
public abstract class AbstractNode extends AbstractArcNode {

	/**
     * Return node error term
     * @return node error term
     */
    public double getError() {
    	return(error);
    }
    
    /**
     * Define node error term
     * @param arg new error term
     */
    public void setError(double arg) {
    	error = arg;
    }
    
    /**
     * Return node backprop error term
     * @param node backprop error term
     */
    public double getBpError() {
    	return(bpError);
    }
    
    /**
     * Define node backprop error term
     * @param arg new backprop error term
     */
    public void setBpError(double arg) {
    	bpError = arg;
    }
    
    /**
     * Return node value
     * @return node value
     */
    public double getValue() {
    	return(value);
    }
    
    /**
     * Define node value
     * @param arg new node value
     */
    public void setValue(double arg) {
    	value = arg;
    }
    
    /**
     * Connect to another node via an arc
     * @param destination node
     * @param arc to connect with
     */
    public void connect(AbstractNode destination, Arc arc) {
    	output_arcs.add(arc);
	
    	destination.input_arcs.add(arc);
	
    	arc.setInputNode(this);
    	arc.setOutputNode(destination);
    }
    
    /**
     * What am I connected to?
     */
    public String dumpConnections() {
    	String result = "id:" + id;
	
    	result += ":input:";
    	Iterator<Arc> ii = input_arcs.iterator();
    	while (ii.hasNext()) {
    		result += ii.next().toString() + ":";
    	}
	
    	result += "output:";
    	ii = output_arcs.iterator();
    	while (ii.hasNext()) {
    		result += ii.next().toString() + ":";
    	}
	
    	return(result);
    }
    
    /**
     * Return object state as a string
     * @return object state as a string
     */
    public String toString() {
    	return(id + " error:" + error + " value:" + value + " input:" + input_arcs.size() + " output:" + output_arcs.size());
    }
    
    /**
     * Error for this node
     */
    double error;
    
    /**
     * Backprop error for this node
     */
    double bpError;
    
    /**
     * Value for this node
     */
    double value;
    
    /**
     * Input arcs
     */
    ArrayList<Arc> input_arcs = new ArrayList<Arc>();
    
    /**
     * Output arcs
     */
    ArrayList<Arc> output_arcs = new ArrayList<Arc>();	
    
    /**
	 * Eclipse generated 
	 */
	private static final long serialVersionUID = -8437051058370670157L;
}