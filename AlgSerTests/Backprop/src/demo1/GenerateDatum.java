package demo1;

import java.io.File;

import network.Mathz;
import network.PatternList;


/**
 * Generate training patterns for BpDemo1.
 *
 * @author G.S. Cole (gsc@digiburo.com)
 * @version $Id: GenerateDatum.java,v 1.4 2002/02/02 08:27:27 gsc Exp $
 */

public class GenerateDatum {
    public static final double above = 0.9999999999;
    public static final double below = 0.0000000001;

    private static final int MAX_CANDIDATES = 250;
    private static final String TRAIN_FILENAME = "demo1.trn";
    
    private PatternList pl = new PatternList();
    
    /**
     * Generate random points and the correct output
     */
    public void createTrainingSet() throws Exception {
    	for (int ii = 0; ii < MAX_CANDIDATES; ii++) {
    		double result;
    		double x1, y1, y2;
	    
    		x1 = Mathz.getBoundedRandom(0.0, 1.0);
    		y1 = Mathz.getBoundedRandom(0.0, 1.0);
	    
    		y2 = -5 * x1 + 2;
	    
    		result = (y1 < y2) ? below:above;
	    
    		double[] input = new double[2];
    		input[0] = x1;
    		input[1] = y1;
	    
    		double[] output = new double[1];
    		output[0] = result;

    		pl.add(input, output);
	    
	    //	    System.out.println(x1 + " " + y1 + " " + result);
    	}

    	pl.writer(new File(TRAIN_FILENAME));
    }

    /**
     * Driver
     */
    public static void main(String args[]) throws Exception {
    	System.out.println("begin");
    	GenerateDatum gd = new GenerateDatum();
    	gd.createTrainingSet();
    	System.out.println("end");
    }
}
