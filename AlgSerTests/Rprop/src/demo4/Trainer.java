package demo4;

import java.io.File;
import java.io.IOException; 
import java.io.FileNotFoundException;

import network.Network;
import network.NetworkStruct;
import network.PatternList;


/**
 * Train the demo4 resilient backprop network.
 * 
 * @author cbarca 
 */

public class Trainer {
    private static final String TRAIN_FILENAME = "208.trn";
    private static final String NETWORK_FILENAME = "208.serial";

    private BpDemo4 bp;
    private PatternList pl;
    private NetworkStruct net_struct;
    
    /**
     * Create network
     */
    public Trainer() {
    	net_struct = new NetworkStruct(0.1);
    	net_struct.setInputPop(301);
    	net_struct.addMiddlePop(100);
    	net_struct.setOutputPop(2);
    	
    	Network network = new Network(net_struct);
    	bp = new BpDemo4(network);
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
		bp.trainNetwork(pl, net_struct.getError(), net_struct.getMaxEpochs(), 0.1, true);
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
		
		Long t1, t2;
		
		t1 = System.currentTimeMillis();
		
		tr.performTraining();
		
		t2 = System.currentTimeMillis();
		
		System.out.println("Execution time was " + (t2 - t1) + " ms.");
		
		tr.saveTraining(new File(NETWORK_FILENAME));

		System.out.println("end");
	}
}
