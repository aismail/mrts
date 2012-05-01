package network;

/**
 * Concrete input node
 */

/**
 * Concrete input node
 *
 * @author gsc
 * @author cbarca
 */
public class InputNode extends AbstractNode {

	/**
	 * Define node value
	 *
	 * @param arg new node value
	 * @throws IllegalArgumentException
	 *             if arg < 0.0 or > 1.0
	 */
	public void setValue(double arg) {
		if ((arg < 0.0) || (arg > 1.0)) {
			throw new IllegalArgumentException("bad input value");
		}

		value = arg;
	}

	/**
	 * Return object state as a string
	 * @return object state as a string
	 */
	public String toString() {
		return ("InputNode:" + super.toString());
	}
    
    /**
     * Eclipse generated 
     */
    private static final long serialVersionUID = -7434630887769481379L;
}
