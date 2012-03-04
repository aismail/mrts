package demo4;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import network.Pattern;
import network.PatternList;


/**
 * Generate training patterns for BpDemo4.
 *
 * @author G.S. Cole (gsc@digiburo.com)
 * @version $Id: GenerateDatum.java,v 1.4 2002/02/02 08:27:27 gsc Exp $
 */

/*
 * Development Environment:
 *   Linux 2.2.12-20 (Red Hat 6.1)
 *   Java Developers Kit 1.2.2-RC2-K
 *
 * Legalise:  
 *   Copyright (C) 2002 Digital Burro, INC.
 *
 * Maintenance History:
 *   $Log: GenerateDatum.java,v $
 *   Revision 1.4  2002/02/02 08:27:27  gsc
 *   Work In Progress
 *
 *   Revision 1.3  2002/02/01 05:09:59  gsc
 *   Tweaks from Unit Testing
 *
 *   Revision 1.2  2002/01/22 08:19:35  gsc
 *   Work In Progress
 *
 *   Revision 1.1  2002/01/21 03:04:11  gsc
 *   Initial Check In
 */

public class GenerateDatum {
	// Ideal values for each type of heart beat
    public static final double NBEAT = 0.9999999999;
    public static final double VBEAT = 0.0000000001;

    private static final int MAX_INPUT = 650000;
    private static final int MAX_NBEATS = 1286; // Number of N beats for training
    private static final int MAX_VBEATS = 792; // Number of V beats for training
    
    private static final String DEFAULT_ANN = "ann.stream.csv";
    private static final String DEFAULT_SENS = "sensor.stream.csv";
    
    private static final int INPUTN = 301; // Number of samples from input
    private static final int INPUT_STEP = 150; // Step left&right among R peak
    private static final int OUTPUTN = 1; // Number of output values
    
    private static final String DEFAULT_TRAIN = "train.stream.csv";
    // Serialized training file (with PatternList object) 
    // - for internal use
    private static final String TRAIN_FILENAME = "demo4.trn"; 
    
    private static final String DEFAULT_TEST = "test.stream.csv";
    private static final int NTEST = 250;
    private static final int VTEST = 200;
    
    private PatternList pl = new PatternList();
    
    private String sourceSens = null, sourceAnn = null, 
    	destTest = null, destTrain = null;
    private CSVReader reader;
    private CSVWriter writer;
    
    private double[] ecgData = new double[MAX_INPUT];
    private List<Integer> ecgAnnN = new ArrayList<Integer>();
    private List<Integer> ecgAnnV = new ArrayList<Integer>();
    
    private double norm;
    
    public GenerateDatum() {
    	this(DEFAULT_SENS, DEFAULT_ANN, 
    			DEFAULT_TEST, DEFAULT_TRAIN);
    }
    
    public GenerateDatum(String source1, String source2, 
    		String dest1, String dest2) {	
    	sourceSens = source1;
    	sourceAnn = source2;
    	destTest = dest1;
    	destTrain = dest2;
    }
    
    public void readAnnSet() {
    	String[] values;
    	
    	try {
			reader = new CSVReader(new FileReader(sourceAnn));
			
			while ( (values = reader.readNext()) != null) {
				if (values[3].compareTo("N") == 0) {
					ecgAnnN.add(Integer.parseInt(values[2]));
				}
				if (values[3].compareTo("V") == 0) {
					ecgAnnV.add(Integer.parseInt(values[2]));
				}
			}
			
			System.out.println("NSize:" + ecgAnnN.size());
			System.out.println("VSize:" + ecgAnnV.size());
			
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
    
    public void readDataSet() {
    	String[] values;
    	int cont = 0;
    	double max = 0;
    	
    	try {
			reader = new CSVReader(new FileReader(sourceSens));
			
			while ( (values = reader.readNext()) != null) {
				ecgData[cont] = Double.parseDouble(values[1]);
				max = Math.max(max, Math.abs(ecgData[cont]));
				cont++;
			}
			
			norm = max;
			
			System.out.println("TotalData:" + cont);
			
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    /**
     * Generate pattern list 
     */
    public void createTrainingSet() throws Exception {
    	int i, ii, cc;
    	    	
		for (i = 0; i < MAX_NBEATS; i++) {
			Integer sample = ecgAnnN.get(i);    		
    		int samplev = sample.intValue();
    		
    		double[] input = new double[INPUTN];
    		
    		System.out.println("NRpeak:" + ecgData[samplev - 1] + 
    				" normalized:" + (ecgData[samplev - 1] + norm) / (2 * norm));
    		
    		for (cc = 0, ii = samplev - INPUT_STEP - 1; ii < samplev + INPUT_STEP; cc++, ii++) {
    			input[cc] = (ecgData[ii] + norm) / (2 * norm);
    		}
    		
    		System.out.println(input[0] + "..." + input[cc - 1]);
    		
    		double[] output = new double[OUTPUTN];
    		output[0] = NBEAT;

    		pl.add(input, output);
    	}
    	
    	for (i = 0; i < MAX_VBEATS; i++) {
    		Integer sample = ecgAnnV.get(i);
    		int samplev = sample.intValue();
    		
    		double[] input = new double[INPUTN];
    		
    		System.out.println("VRpeak:" + ecgData[samplev - 1] + 
    				" normalized:" + (ecgData[samplev - 1] + norm) / (2 * norm));
    		
    		for (cc = 0, ii = samplev - INPUT_STEP - 1; ii < samplev + INPUT_STEP; cc++, ii++) {
    			input[cc] = (ecgData[ii] + norm) / (2 * norm);
    		}
    		
    		System.out.println(input[0] + "..." + input[cc - 1]);
    		
    		double[] output = new double[OUTPUTN];
    		output[0] = VBEAT;

    		pl.add(input, output);
    	}
    	
    	System.out.println("PatternList:" + pl.size());
    	
    	pl.writer(new File(TRAIN_FILENAME));
    	
    	// Generating the learning data - csv file format
    	writer = new CSVWriter(new FileWriter(destTrain));
    	
    	for (i = 0; i < pl.size(); i++) {
    		String[] line = new String[INPUTN + OUTPUTN];
    		Pattern p = pl.get(i);
    		
    		for (ii = 0; ii < INPUTN; ii++) {
    			line[ii] = Double.toString(p.getInput()[ii]);
    		}
    		line[INPUTN] = Double.toString(p.getOutput()[0]);
    		
    		writer.writeNext(line);
    	}
    	
    	writer.close();
    }
    
    public void createTestSet() {
    	int i, cc, ii;
    	
    	try {
			writer = new CSVWriter(new FileWriter(destTest));
			
			for (i = MAX_NBEATS; i < MAX_NBEATS + NTEST; i++) {
	    		Integer sample = ecgAnnN.get(i);
	    		int samplev = sample.intValue();
	    		String[] line = new String[1];
	    		   		
	    		System.out.println("TEST:NRpeak:" + ecgData[samplev - 1] + 
	    				" normalized:" + (ecgData[samplev - 1] + norm) / (2 * norm));
	    		
	    		for (cc = 0, ii = samplev - INPUT_STEP - 1; ii < samplev + INPUT_STEP; cc++, ii++) {
	    			line[0] = Double.toString((ecgData[ii] + norm) / (2 * norm));
	    			writer.writeNext(line);
	    		}  		
	      	}
			
			for (i = MAX_VBEATS; i < MAX_VBEATS + VTEST; i++) {
	    		Integer sample = ecgAnnV.get(i);
	    		int samplev = sample.intValue();
	    		String[] line = new String[1];
	    		   		
	    		System.out.println("TEST:VRpeak:" + ecgData[samplev - 1] + 
	    				" normalized:" + (ecgData[samplev - 1] + norm) / (2 * norm));
	    		
	    		for (cc = 0, ii = samplev - INPUT_STEP - 1; ii < samplev + INPUT_STEP; cc++, ii++) {
	    			line[0] = Double.toString((ecgData[ii] + norm) / (2 * norm));
	    			writer.writeNext(line);
	    		}  		
	      	}
			
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    /**
     * Driver
     */
    public static void main(String args[]) throws Exception {
    	System.out.println("begin");
    	GenerateDatum gd = new GenerateDatum();
    	gd.readAnnSet();
    	gd.readDataSet();
    	gd.createTrainingSet();
    	gd.createTestSet();
    	System.out.println("end");
    }
}
