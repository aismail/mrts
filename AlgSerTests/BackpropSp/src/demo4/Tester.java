package demo4;

import java.io.File;
import java.io.FileReader;
import java.io.IOException; 
import java.io.FileNotFoundException;

import java.io.FileWriter;
import java.io.BufferedWriter;

import au.com.bytecode.opencsv.CSVReader;

/**
 * Test the demo1 network by submitting points and obtaining results.
 *
 * @author G.S. Cole (gsc@digiburo.com)
 * @version $Id: Tester.java,v 1.4 2002/02/02 08:27:27 gsc Exp $
 */

/*
 * Development Environment:
 *   Linux 2.2.14-5.0 (Red Hat 6.2)
 *   Java Developers Kit 1.3.1
 *
 * Legalise:  
nn *   Copyright (C) 2002 Digital Burro, INC.
 *
 * Maintenance History:
 *   $Log: Tester.java,v $
 *   Revision 1.4  2002/02/02 08:27:27  gsc
 *   Work In Progress
 *
 *   Revision 1.3  2002/02/01 05:09:59  gsc
 *   Tweaks from Unit Testing
 *
 *   Revision 1.2  2002/02/01 02:48:08  gsc
 *   Work In Progress
 *
 *   Revision 1.1  2002/01/22 08:19:35  gsc
 *   Initial Check In
 */

public class Tester {
    private static final String FILENAME = "beats.txt";
    private static final String NETWORK_FILENAME = "demo4.serial";
    private static final int INPUTN = 301;
    private static final int NTEST = 250;
    private static final int VTEST = 200;
    private static final int MAX_TESTS = INPUTN * (NTEST + VTEST);
    private static final String DEFAULT_TEST = "test.stream.csv";

    private BpDemo4 bp;
    private CSVReader reader;
    private double[] ecgData = new double[MAX_TESTS];

    /**
     * Create network
     */
    public Tester(File network) throws IOException, FileNotFoundException, ClassNotFoundException {
    	bp = new BpDemo4(network);
    }

    
    public void readTests(String testfile) {
    	String[] values;
    	int cont = 0;
    	
    	try {
			reader = new CSVReader(new FileReader(testfile));
			
			// reading normalized data tests
			while ((values = reader.readNext()) != null) {
				ecgData[cont++] = Double.parseDouble(values[0]);
			}
			
			System.out.println("TotalData:" + ecgData.length);
			
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
	 * Submit these points to the network for classification.
	 */
	public void performTesting() throws Exception {
		int ii, jj;
		
		// write answers

		BufferedWriter bw = new BufferedWriter(new FileWriter(FILENAME));

		for (ii = 0; ii < ecgData.length; ii += INPUTN) {
			double[] input = new double[INPUTN];
						
			for (jj = ii; jj < ii + INPUTN; jj++) {
				input[jj - ii] = ecgData[jj];
			}
			
			writeData(bw, bp.classifier(input));
		}
		
		bw.close();
	}

	/**
     *
     */
	public void writeData(BufferedWriter bw, int flag) throws Exception {
		bw.write(Integer.toString(flag));
		bw.newLine();
	}

	/**
	 * Driver
	 */
	public static void main(String args[]) throws Exception {
		System.out.println("begin");

		Tester tr = null;

		if (args.length != 1) {
			tr = new Tester(new File(NETWORK_FILENAME));
		} else {
			tr = new Tester(new File(args[0]));
		}

		tr.readTests(DEFAULT_TEST);
		tr.performTesting();

		System.out.println("end");
	}
}
