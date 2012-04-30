package neuralnet.network;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Facade for Backpropagation neural network
 *
 * @author gsc
 * @author cbarca
 */
public class Backprop {

    /**
     * Constructor for new backpropagation network.
     *
     * @param input_population input node count
     * @param middle_population middle node count
     * @param output_population output node count
     * @param learning_rate learning rate to use during error calculations (suggest 25 to 50 percent)
     * @param momentum used during weight calculations 
     */
    public Backprop(Network network) {
    	_network = network;
    }
    
    /**
     * ctor for a persisted backpropagation network.
     * 
     * @param file persisted network
     * @throws IOException if problem
     * @throws FileNotFoundException if problem
     * @throws ClassNotFoundException if problem
     */
    public Backprop(File file) throws IOException, FileNotFoundException, ClassNotFoundException {
    	ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
    	_network = (Network) ois.readObject();
    	ois.close();
    }
    
    /**
     * Run the network w/the specified pattern
     * 
     * @param arg input pattern
     * @return classification output
     */
    public double[] runNetwork(double[] arg) {
    	return(_network.runNetWork(arg));
    }
    
    /**
     * Train this network using the supplied pattern list.
     * 
     * @param patternz list of training patterns
     * @param max_match quantity of patterns to match for training success, -1 = all
     * @param max_cycles maximum training cycles, -1 = no limit
     * @param threshold limit near zero or one to count as zero or one
     * @param verbose write progress messages
     * @return quantity of trained patterns
     */
    public int trainNetwork(PatternList patternz, double qerr, int max_cycles, double threshold, boolean verbose) {
    	int limit = patternz.size();
    	
    	int counter = 0;
    	int success;
    	int max_success = 0;
    	
    	long tot_train = 0, tot_run = 0;
    	long train_start, train_end,
    		 run_start, run_end;
	
    	do {
    		success = 0;
    		_network.resetQError();
    		_network.resetAggOutputError();
    		
    		for (int ii = 0; ii < limit; ii++) {
    			Pattern pattern = patternz.get(ii);
	
    			run_start = System.currentTimeMillis();
    			_network.runNetWork(pattern.getInput());
    			run_end = System.currentTimeMillis();
    			
    			tot_run += (run_end - run_start);
	
    			train_start = System.currentTimeMillis();
    			double[] raw_results = _network.trainNetWork(pattern.getOutput());
    			train_end = System.currentTimeMillis();
    			
    			tot_train += (train_end - train_start);
    
    			// Online mode
    			_network.updateWeights();
    			
    			int[] truth = Mathz.thresholdArray(threshold, pattern.getOutput());
    			int[] results = Mathz.thresholdArray(threshold, raw_results);
		
    			pattern.setTrained(true);
    			for (int jj = 0; jj < raw_results.length; jj++) {
    				if (results[jj] != truth[jj]) {
    					pattern.setTrained(false);
    					break;
    				}
    			}
		
    			if (pattern.isTrained()) {
    				++success;
    			}
    		}
    		
    		// Batch mode
    		// _network.updateWeights();
	    
    		if (max_success < success) {
    			max_success = success;
    		}
	    
    		if ((++counter % 1000) == 0) {		
    			if (verbose) {
    				System.out.println("Fwd time: " + tot_run);
    				System.out.println("Bwd time: " + tot_train);
    				System.out.println("Netw qerror: " + _network.getQError());
    				System.out.println(counter + " success:" + success + " best run:" + max_success);
    			}
    		}
    		
    		if (max_cycles != -1) {
    			if (counter > max_cycles) {
    				break;
    			}
    		}
    	} while(_network.getQError() > qerr);
    	
    	if (verbose) {
    		System.out.println("Fwd time: " + tot_run);
			System.out.println("Bwd time: " + tot_train);
    		System.out.println("Netw error: " + _network.getQError());
    		System.out.println("Training complete in " + counter + " cycles");
    	}
    	
    	return (success);
    }
    
    /**
     * Persist this backprop network to a file
     * 
     * @param file to save
     * @throws IOException if problem
     * @throws FileNotFoundException if problem
     */
    public void saveNetwork(File file) throws IOException, FileNotFoundException {
    	ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
    	oos.writeObject(_network);
    	oos.close();
    }
    
    /**
     * Current backprop network
     */
    private Network _network;
}
