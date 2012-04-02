package demo4;

import java.io.File;
import java.io.IOException; 
import java.io.FileNotFoundException;

import network.Network;
import network.Rprop;

/**
 * 
 * @author cbarca
 *
 */
public class BpDemo4 extends Rprop {

    /**
     * Constructor for new resilient backprop network.
     * 
     * @param network = rprop network 
     */
    public BpDemo4(Network network) {
    	super(network);
    }

    /**
     * Constructor for existing backpropagation network.
     *
     * @param file serialized Network memento
     */
    public BpDemo4(File file) throws IOException, FileNotFoundException, ClassNotFoundException {
    	super(file);
    }

	public int classifier(double[] input) {
		double[] output = runNetwork(input);

		if (output[0] > 0.9 && output[1] < 0.1) {
			return (1);
		} else if (output[0] < 0.1 && output[1] > 0.9) {
			return (-1);
		}

		return(0);
	}
}
