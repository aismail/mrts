package neuralnet.iris;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import neuralnet.IClassifier;

import fnn.network.Rprop;
import fnn.network.RpropNetwork;

/**
 * DemoRprop class - Resilient Backpropagation 
 * 
 * @author cbarca
 */
public class IrisRprop extends Rprop implements IClassifier {

    /**
     * Constructor for new Resilient Backpropagation network
     * @param network Rprop network 
     */
    public IrisRprop(RpropNetwork network) {
    	super(network);
    }

    /**
     * Constructor for existing Rprop network
     * @param file serialized Network memento
     */
    public IrisRprop(File file) throws IOException, 
    	FileNotFoundException, ClassNotFoundException {
    	super(file);
    }

    /**
     * Classifier for network's output
     * @param input network test data input
     * @return the network output value
     */
	public int classifier(double[] input) {
		double[] output = runNetwork(input);

		if (output[0] > 0.9 && output[1] < 0.1 && output[2] < 0.1) {
			return (1);
		} else if (output[0] < 0.1 && output[1] > 0.9 && output[2] < 0.1) {
			return (2);
		}
		else if (output[0] < 0.1 && output[1] < 0.1 && output[2] > 0.9) {
			return (3);
		}

		return(0);
	}
}
