package demo1;

import java.io.File;
import java.io.IOException; 
import java.io.FileNotFoundException;

import network.Network;
import network.PatternList;

/**
 * Train the demo1 backpropagation network.
 *
 * @author G.S. Cole (gsc@digiburo.com)
 * @version $Id: Trainer.java,v 1.5 2002/02/02 20:53:53 gsc Exp $
 */

public class Trainer {
    private static final String TRAIN_FILENAME = "demo1.trn";
    private static final String NETWORK_FILENAME = "demo1.serial";

    private BpDemo1 bp;
    private PatternList pl;

    /**
     * Create network
     */
    public Trainer() {
    	Network network = new Network(0.25, 0.9);
    	network.setInputLayer(2);
    	network.addMiddleLayer(7);
    	network.setOutputLayer(1);
    	network.finalizeStructure();
  
    	bp = new BpDemo1(network);
    }

    /**
     * Load training datum
     * @param datum training file
     */
    public int loadTraining(File datum) throws IOException, FileNotFoundException, ClassNotFoundException {
    	pl = new PatternList();
    	pl.reader(datum);
    	return(pl.size());
    }

	/**
	 * Train the network on these patterns
	 */
	public void performTraining() {	
		bp.trainNetwork(pl, 0.2, -1, 0.2, true);
	}

    /**
     * Save this network for later use.
     * @param datum file to save as
     */
    public void saveTraining(File datum) throws IOException, FileNotFoundException {
    	bp.saveNetwork(datum);
    }

	/**
	 * Driver.
	 */
	public static void main(String args[]) throws Exception {
		System.out.println("begin");

		int population = 0;
		Trainer tr = new Trainer();

		if (args.length == 0) {
			population = tr.loadTraining(new File(TRAIN_FILENAME));
		} else {
			population = tr.loadTraining(new File(args[0]));
		}

		System.out.println("PatternList loaded w/" + population + " patterns");
		tr.performTraining();
		tr.saveTraining(new File(NETWORK_FILENAME));

		System.out.println("end");
	}
}
