package fnn.network;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import fnn.dataio.PatternList;
import fnn.visual.ICallbackPlotter;

public abstract class AbstractAlgorithm {
	
	/**
     * Run the network w/the specified pattern
     * @param arg input pattern
     * @return classification output
     */
    public double[] runNetwork(double[] arg) {
    	return(_network.runNetWork(arg));
    }
    
    /**
     * Train this network using the supplied pattern list 
     * @param patternz list of training patterns
     * @param max_match quantity of patterns to match for training success, -1 = all
     * @param max_cycles maximum training cycles, -1 = no limit
     * @param threshold limit near zero or one to count as zero or one
     * @param verbose write progress messages
     * @return quantity of trained patterns
     */
    public abstract int trainNetwork(PatternList patternz, double qerr, int max_cycles, 
    		double threshold, boolean verbose, ICallbackPlotter iplotter);
    
    /**
     * Persist this network to a file
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
     * Current network
     */
    protected AbstractNetwork _network;
}
