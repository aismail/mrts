package fnn.network;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Date;


import fnn.dataio.Pattern;
import fnn.dataio.PatternBuffIterator;
import fnn.dataio.PatternList;
import fnn.util.Mathz;
import fnn.visual.ICallbackPlotter;

/**
 * Resilient Backpropagation Algorithm
 * Concrete class for Rprop neural network (buffered version)
 * Extends the AbstractAlgorithm class
 *
 * @author cbarca
 */
public class RpropBuff extends AbstractAlgorithm {

    /**
     * Constructor for a new Resilient Backpropagation network
     * @param network Rprop network
     */
    public RpropBuff(AbstractNetwork network) {
    	_network = network;
    }
    
    /**
     * Constructor for a persisted Rprop network
     * @param file persisted network
     * @throws IOException if problem
     * @throws FileNotFoundException if problem
     * @throws ClassNotFoundException if problem
     */
    public RpropBuff(File file) throws IOException, FileNotFoundException, ClassNotFoundException {
    	ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
    	_network = (RpropNetwork) ois.readObject();
    	ois.close();
    }
    
    public int trainNetwork(String dir_path, int nin, int nout, double qerr, int max_cycles, 
    		double threshold, int verbose_rate, ICallbackPlotter iplotter) {  
    	int counter = 0;
    	int success;
    	int max_success = 0;
    	
    	long tot_fwd = 0, tot_bwd = 0, tot_train = 0;
    	long fwd_start, fwd_end,
    		 bwd_start, bwd_end,
    		 train_start, train_end;
    	
    	train_start = System.currentTimeMillis();
    	
    	do {
    		success = 0;
    		_network.resetQError();
    		_network.resetAggOutputError();
    		
    		PatternBuffIterator bit = new PatternBuffIterator(250000, dir_path, nin, nout);
    		
    		while (bit.hasNext()) {
    			Pattern pattern = bit.next(); 
	
    			fwd_start = System.currentTimeMillis();
    			_network.runNetWork(pattern.getInput());
    			fwd_end = System.currentTimeMillis();
    			
    			tot_fwd += (fwd_end - fwd_start);
	
    			bwd_start = System.currentTimeMillis();
    			double[] raw_results = _network.trainNetWork(pattern.getOutput());
    			bwd_end = System.currentTimeMillis();
    			
    			tot_bwd += (bwd_end - bwd_start);
    
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
    		_network.updateWeights();
	    
    		if (max_success < success) {
    			max_success = success;
    		}
	    
    		if (iplotter != null) {
    			iplotter.addXYValue("error", counter + 1, _network.getQError(),
    					new Date().getTime());
    		}
    		
    		if (verbose_rate > 0 && (++counter % verbose_rate) == 0) {		
    			System.out.println(">> " + verbose_rate + " epoch(s) finished in " + 
    					(tot_fwd + tot_bwd) / (double)1000 +  " sec");	
    			System.out.println("Fwd time: " + tot_fwd / (double)1000 +  " sec");
    			System.out.println("Bwd time: " + tot_bwd / (double)1000 +  " sec");
    			System.out.println("Netw qerror: " + _network.getQError());
    			System.out.println("Step " + counter + ", success:" + success + ", " +
    					"best run:" + max_success);
    			tot_fwd = tot_bwd = 0;
    		}
    		
    		if (max_cycles != -1) {
    			if (counter >= max_cycles) {
    				break;
    			}
    		}
    	} while(_network.getQError() > qerr);
    	
    	train_end = System.currentTimeMillis();
    	
    	tot_train = (train_end - train_start);
    	
    	if (verbose_rate > 0) {
			System.out.println("Total train time: " + tot_train / (double)1000 + " sec");
    		System.out.println("Netw qerror: " + _network.getQError());
    		System.out.println("Training complete in " + counter + " epochs");
    	}
    	
    	return (success);
    }

	@Override
	public int trainNetwork(PatternList patternz, double qerr, int maxCycles,
			double threshold, int verboseRate, ICallbackPlotter iplotter) {
		// TODO Auto-generated method stub
		return 0;
	}
}
