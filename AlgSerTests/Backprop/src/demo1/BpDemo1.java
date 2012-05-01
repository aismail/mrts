package demo1;

import java.io.File;
import java.io.IOException; 
import java.io.FileNotFoundException;

import network.Backprop;
import network.Network;


/**
 * Demonstrate learning of the equation y = -5x-2.
 * This is a 2 input, 3 middle, 1 output network which is 
 * trained to discover if a point is above or below a line.
 *
 * @author G.S. Cole (gsc@digiburo.com)
 * @version $Id: BpDemo1.java,v 1.5 2002/02/03 20:31:41 gsc Exp $
 */

public class BpDemo1 extends Backprop {

    /**
     * Constructor for new backpropagation network.
     *
     * @param input_population input node count
     * @param middle_population middle node count
     * @param output_population output node count
     * @param learning_rate 
     * @param momentum
     */
    public BpDemo1(Network network) {
    	super(network);
    }

    /**
     * Constructor for existing backpropagation network.
     *
     * @param file serialized Network memento
     */
    public BpDemo1(File file) throws IOException, FileNotFoundException, ClassNotFoundException {
    	super(file);
    }

	/**
	 * Classify a point as either above or below the line.
	 * 
	 * @param xx x coordinate
	 * @param yy y coordinate
	 * @return -1 = below line, 1 = above line, 0 = ambiguous
	 */
	public int classifier(double xx, double yy) {
		double[] input = new double[2];
		input[0] = xx;
		input[1] = yy;
		
		double[] output = runNetwork(input);

		if (output[0] > 0.9) {
			return (1);
		} else if (output[0] < 0.1) {
			return (-1);
		}

		return(0);
	}
}
