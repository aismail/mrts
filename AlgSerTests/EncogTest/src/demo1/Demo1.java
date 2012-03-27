package demo1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;

import au.com.bytecode.opencsv.CSVReader;

public class Demo1 {

	private static final int INPUTN = 301;
	private static final int MIDDLEN = 100;
	private static final int OUTPUTN = 2;
	private static final int NHIDDL = 2;
	private static final String NETW_FILE = "netw_all2.serial";
	private static final double ERR = 0.01;
		
    // private static final String DEFAULT_TRAIN = "train.stream.csv";   
    // private static final String DEFAULT_TEST = "test.stream.csv";
    
	public static final double NBEAT = 0.9999999999;
    public static final double VBEAT = 0.0000000001;
    public static final double THRESH = 0.01;
	
    private static CSVReader reader;
    
    /**
     * Read training data - uses the training files generated
     * by Demo4 from BackpropSp project
     * @param datum training file
     */
    public static void readTrainingData(String filename, MLDataSet mld) {
    	String[] line;
    	int i;
    	double[] ECG_INPUT = new double[INPUTN];
    	double[] ECG_IDEAL = new double[OUTPUTN];
    	    	
    	try {
			reader = new CSVReader(new FileReader(filename));
			
			line = reader.readNext();
			    	
			while ((line = reader.readNext()) != null) {
				for (i = 0; i < INPUTN; i++) {
					ECG_INPUT[i] = Double.parseDouble(line[i]);
				}
				ECG_IDEAL[0] = Double.parseDouble(line[INPUTN]);
				ECG_IDEAL[1] = Double.parseDouble(line[INPUTN + 1]);
				
				mld.add(new BasicMLData(ECG_INPUT), new BasicMLData(ECG_IDEAL));
			}
			
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
     * Read test data - uses the test files generated
     * by Demo4 from BackpropSp project
     * @param datum training file
     */
    public static void readTestData(String filename, MLDataSet mld) {
    	String[] line;
    	int i = 0;
    	double[] ECG_TEST = new double[INPUTN];
    	
    	try {
			reader = new CSVReader(new FileReader(filename));
			
			line = reader.readNext();
			System.out.println(filename + " N: " + line[0] + " V: " + line[1]);
			
			while ((line = reader.readNext()) != null) {
				for (i = 0; i < INPUTN; i++) {
					ECG_TEST[i] = Double.parseDouble(line[i]);
				}
				
				mld.add(new BasicMLData(ECG_TEST));
			}
			
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
     * Train the network and save it
     * @param filename
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void trainNetwork(String filename) throws FileNotFoundException, IOException {
    	// create a neural network, without using a factory
		BasicNetwork network = new BasicNetwork();
		MLDataSet trainSet = new BasicMLDataSet();
		MLTrain train;
		String trainFile;
		int i, epoch = 1;
		Long t1, t2, tit1, tit2;
		
		network.addLayer(new BasicLayer(null, true, INPUTN));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), true, MIDDLEN));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), true, MIDDLEN));
		//network.addLayer(new BasicLayer(new ActivationSigmoid(), true, MIDDLEN));
		//network.addLayer(new BasicLayer(new ActivationSigmoid(), true, MIDDLEN));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), false, OUTPUTN));
		network.getStructure().finalizeStructure();
		network.reset();
		
		// Read train data & create train set	
		for (i = 100; i < 235; i++) {
			trainFile = "./train&test/" + "train_" + i + ".csv";
						
			File file = new File(trainFile);
			
			if (!file.exists()) {
				continue;
			}
			
			readTrainingData(trainFile, trainSet);
		}
		
		System.out.println("ResilientBackpropagation");
		System.out.println("#hiddenLayers " + NHIDDL);
		System.out.println("#inputUnits " + INPUTN);
		System.out.println("#hiddenUnits " + MIDDLEN);
		System.out.println("#outputUnits " + OUTPUTN);
		System.out.println("TrainSetSize: " + trainSet.getRecordCount());
		System.out.println("TrainError: " + ERR);
		
		// Train the neural network with ResilientBackpropagation
		train = new ResilientPropagation(network, trainSet);
					
		t1 = System.currentTimeMillis();
		
		do {
			tit1 = System.currentTimeMillis();
			
			train.iteration();
			
			tit2 = System.currentTimeMillis();
			
			//System.out.println("Epoch #" + epoch + " fwd_bwd_time: " 
				//	+ (tit2 - tit1) + " msec");
			
			if (epoch % 1000 == 0) {
				System.out.println("Epoch #" + epoch + " Error:" + train.getError());
			}
			
			epoch++;	
		} 
		while (train.getError() > ERR);
		
		t2 = System.currentTimeMillis();
		
		System.out.println("Training execution time was " + (t2 - t1) / 1000 + 
				" sec in " + epoch + " epochs");
		
		// Save the network to filename
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename));
		oos.writeObject(network);
		oos.close();
    }
    
    /**
     * Load the network and test it
     * @param filename
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    public static void testNetwork(String filename) throws IOException, ClassNotFoundException {
    	BasicNetwork network;
    	MLDataSet testSet;
    	String testFile;
		int i, rn, rv;
		
		// Load the network from filename 
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename));
    	network = (BasicNetwork) ois.readObject();
    	ois.close();
				
    	// Read test data, create test set & test the network	
		for (i = 100; i < 235; i++) {
			testFile = "./noisy_test/" + "ntest_" + i + ".csv";
			//testFile = "./rand_test/" + "rtest_" + i + ".csv";
			//testFile = "./train&test/" + "test_" + i + ".csv";
			
			File file = new File(testFile);
			
			if (!file.exists()) {
				continue;
			}
			
			testSet = new BasicMLDataSet();
			rn = 0; 
			rv = 0;
				
			readTestData(testFile, testSet);
			
			System.out.println("TestSetSize: " + testSet.getRecordCount());
			System.out.println("Neural Network Results:");
			
			// Test the neural network
			for(MLDataPair pair: testSet) {
				final MLData output = network.compute(pair.getInput());
				
				//System.out.println(pair.getInput());
				//System.out.println(output.getData(0) + " " + output.getData(1));
				
				if (Math.abs(output.getData(0) - VBEAT) < THRESH && 
						Math.abs(output.getData(1) - NBEAT) < THRESH) {
					rv++;
				}
				else
				if (Math.abs(output.getData(0) - NBEAT) < THRESH && 
						Math.abs(output.getData(1) - VBEAT) < THRESH) {
					rn++;
				}
			}
			
			System.out.println("N: " + rn + " V: " + rv);
		}
    }
    
	/**
	 * The main method.
	 * @param args No arguments are used.
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static void main(final String args[]) throws FileNotFoundException, 
		IOException, ClassNotFoundException {
		
		//trainNetwork(NETW_FILE);
		testNetwork(NETW_FILE);
	}
}
