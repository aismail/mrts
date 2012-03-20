package demo4;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    
    private static final int INPUTN = 301; // Number of samples from input
    private static final int INPUT_STEP = 150; // Step left&right among R peak
    private static final int OUTPUTN = 2; // Number of output values
      
    private PatternList pl;
    
    private CSVReader reader;
    private CSVWriter writer;
    private CSVWriter rwriter;
    
    private double[] ecgData = new double[MAX_INPUT];
    private List<Integer> ecgAnnN = new ArrayList<Integer>();
    private List<Integer> ecgAnnV = new ArrayList<Integer>();
    
    private double norm;
    
    public GenerateDatum() {
    	
    }
        
    public void readAnnSet(String sourceAnn) {
    	String[] values;
    
    	ecgAnnN.clear();
    	ecgAnnV.clear();
    	
    	try {
			reader = new CSVReader(new FileReader(sourceAnn));
			
			reader.readNext();
			
			while ( (values = reader.readNext()) != null) {
				if (values.length < 4) {
					continue;
				}
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
    
    public void readDataSet(String sourceSens) {
    	String[] values;
    	int cont = 0;
    	double max = 0;
    	
    	try {
			reader = new CSVReader(new FileReader(sourceSens));
			
			reader.readNext();
			reader.readNext();
			
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
    public void createTrainingSet(String destTrain, String trnSer) throws Exception {
    	int i, ii, cc;
    	 
    	pl = new PatternList();
    	
		for (i = 0; i < ecgAnnN.size() / 2; i++) {
			Integer sample = ecgAnnN.get(i);    		
    		int samplev = sample.intValue();
    		
    		if (samplev - INPUT_STEP <= 0 || samplev + INPUT_STEP >= MAX_INPUT) {
    			continue;
    		}
    		
    		double[] input = new double[INPUTN];
    		
    		System.out.println("NRpeak:" + ecgData[samplev - 1] + 
    				" normalized:" + (ecgData[samplev - 1] + norm) / (2 * norm));
    		
    		
    		for (cc = 0, ii = samplev - INPUT_STEP - 1; ii < samplev + INPUT_STEP; cc++, ii++) {
    			input[cc] = (ecgData[ii] + norm) / (2 * norm);
    		}
    		
    		System.out.println(input[0] + "..." + input[cc - 1]);
    		
    		double[] output = new double[OUTPUTN];
    		output[0] = NBEAT;
    		output[1] = VBEAT;

    		pl.add(input, output);
    	}
    	
    	for (i = 0; i < ecgAnnV.size() / 2; i++) {
    		Integer sample = ecgAnnV.get(i);
    		int samplev = sample.intValue();
    		
    		if (samplev - INPUT_STEP <= 0 || samplev + INPUT_STEP >= MAX_INPUT) {
    			continue;
    		}
    		
    		double[] input = new double[INPUTN];
    		
    		System.out.println("VRpeak:" + ecgData[samplev - 1] + 
    				" normalized:" + (ecgData[samplev - 1] + norm) / (2 * norm));
    		
    		for (cc = 0, ii = samplev - INPUT_STEP - 1; ii < samplev + INPUT_STEP; cc++, ii++) {
    			input[cc] = (ecgData[ii] + norm) / (2 * norm);
    		}
    		
    		System.out.println(input[0] + "..." + input[cc - 1]);
    		
    		double[] output = new double[OUTPUTN];
    		output[0] = VBEAT;
    		output[1] = NBEAT;
    		       
    		pl.add(input, output);
    	}
    	
    	System.out.println("PatternList:" + pl.size());
    	
    	pl.writer(new File(trnSer));
    	
    	// Generating the learning data - csv file format
    	writer = new CSVWriter(new FileWriter(destTrain));
    	
    	String[] fline = new String[1];
    	fline[0] = "" + pl.size();
    	writer.writeNext(fline);
    	
    	for (i = 0; i < pl.size(); i++) {
    		String[] line = new String[INPUTN + OUTPUTN];
    		Pattern p = pl.get(i);
    		
    		for (ii = 0; ii < INPUTN; ii++) {
    			line[ii] = Double.toString(p.getInput()[ii]);
    		}
    		line[INPUTN] = Double.toString(p.getOutput()[0]);
    		line[INPUTN + 1] = Double.toString(p.getOutput()[1]);
    		
    		writer.writeNext(line);
    	}
    	
    	writer.close();
    }
    
    public void createTestSet(String destTest, String randTest) {
    	int i, cc, ii, totN, totV;
    	Random rand = new Random();
    	
    	try {
			writer = new CSVWriter(new FileWriter(destTest));
			rwriter = new CSVWriter(new FileWriter(randTest));
			
			String[] fline = new String[2];
	    	totN = (ecgAnnN.size() - ecgAnnN.size() / 2);
	    	totV = (ecgAnnV.size() - ecgAnnV.size() / 2);
	    	
	    	if (ecgAnnN.size() > 0 && ecgAnnN.get(ecgAnnN.size() - 1).intValue() 
	    			+ INPUT_STEP >= MAX_INPUT) {
	    		totN--;
	    	}
	    	
	    	if (ecgAnnV.size() > 0 && ecgAnnV.get(ecgAnnV.size() - 1).intValue() 
	    			+ INPUT_STEP >= MAX_INPUT) {
	    		totV--;
	    	}
	    	
	    	fline[0] = "" + totN;
	    	fline[1] = "" + totV;
	    	
	    	writer.writeNext(fline);
	    	rwriter.writeNext(fline);
			
			for (i = ecgAnnN.size() / 2; i < ecgAnnN.size(); i++) {
	    		Integer sample = ecgAnnN.get(i);
	    		int samplev = sample.intValue();
	    	
	    		if (samplev - INPUT_STEP <= 0 || samplev + INPUT_STEP >= MAX_INPUT) {
	    			continue;
	    		}
	    		
	    		String[] line = new String[INPUTN],
	    				 rline = new String[INPUTN];
	    		   		
	    		System.out.println("TEST:NRpeak:" + ecgData[samplev - 1] + 
	    				" normalized:" + (ecgData[samplev - 1] + norm) / (2 * norm));
	    		
	    		for (cc = 0, ii = samplev - INPUT_STEP - 1; ii < samplev + INPUT_STEP; cc++, ii++) {
	    			line[cc] = Double.toString((ecgData[ii] + norm) / (2 * norm));
	    			//rline[cc] = Double.toString(rand.nextDouble());
	    			rline[cc] = Double.toString((ecgData[ii] + norm) / (2 * norm) 
	    					+ (rand.nextFloat() / 10)
	    					- (rand.nextFloat() / 10));
	    		}  		
	    		
	    		writer.writeNext(line);
	    		rwriter.writeNext(rline);
	      	}
			
			for (i = ecgAnnV.size() / 2; i < ecgAnnV.size(); i++) {
	    		Integer sample = ecgAnnV.get(i);
	    		int samplev = sample.intValue();
	    		
	    		if (samplev - INPUT_STEP <= 0 || samplev + INPUT_STEP >= MAX_INPUT) {
	    			continue;
	    		}
	    		
	    		String[] line = new String[INPUTN],
						 rline = new String[INPUTN];
	    		   		
	    		System.out.println("TEST:VRpeak:" + ecgData[samplev - 1] + 
	    				" normalized:" + (ecgData[samplev - 1] + norm) / (2 * norm));
	    		
	    		for (cc = 0, ii = samplev - INPUT_STEP - 1; ii < samplev + INPUT_STEP; cc++, ii++) {
	    			line[cc] = Double.toString((ecgData[ii] + norm) / (2 * norm));
	    			//rline[cc] = Double.toString(rand.nextDouble());
	    			rline[cc] = Double.toString((ecgData[ii] + norm) / (2 * norm) 
	    					+ (rand.nextFloat() / 10)
	    					- (rand.nextFloat() / 10));
	    		}  		
	    		
	    		writer.writeNext(line);
	    		rwriter.writeNext(rline);
	      	}
			
			writer.close();
			rwriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    /**
     * Driver
     */
    public static void main(String args[]) throws Exception {
    	String annSet = null, dataSet = null, trnSet = null, 
    			testSet = null, trnSer = null, randSet = null;
    	int i;
    	
    	System.out.println("begin");
    	GenerateDatum gd = new GenerateDatum();
    	
    	for (i = 100; i < 235; i++) { 
    		annSet = "./dataset/" + "ann_" + i + ".csv";
    		dataSet = "./dataset/" + "samples_" + i + ".csv";
    		trnSet = "./train&test/" + "train_" + i + ".csv";
    		testSet = "./train&test/" + "test_" + i + ".csv";
    		trnSer = "./train_serialized/" + i + ".trn";
    		//randSet = "./rand_test/" + "rtest_" + i + ".csv";
    		randSet = "./noisy_test/" + "ntest_" + i + ".csv";
    		
    		File file = new File(annSet);
    		
    		if (!file.exists()) {
    			continue;
    		}
    		
    		gd.readAnnSet(annSet);
    		gd.readDataSet(dataSet);
    		//gd.createTrainingSet(trnSet, trnSer);
    		gd.createTestSet(testSet, randSet);
    	}
    	
    	System.out.println("end");
    }
}
