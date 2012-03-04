package demo1;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.lma.LevenbergMarquardtTraining;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;

import au.com.bytecode.opencsv.CSVReader;

public class Demo1 {

	private static final int MAX_TRAINSZ = 2078;
	private static final int INPUTN = 301;
	private static final int MIDDLEN = 10;
	private static final int OUTPUTN = 1;
	
	public static double ECG_INPUT[][];
	public static double ECG_IDEAL[][];
	public static double ECG_TEST[][];
	
    // Number of test data used
    private static final int NTEST = 250;
    private static final int VTEST = 200;
    
    // Serialized train file, contains pattern list object
    private static final String DEFAULT_TRAIN = "train.stream.csv";   
    private static final String DEFAULT_TEST = "test.stream.csv";
    
    private static CSVReader reader;
    
    /**
     * Read training data - uses the training file generated
     * by Demo4 from BackpropSp project
     * @param datum training file
     */
    public static void readTrainingData(String filename) {
    	String[] line;
    	ECG_INPUT = new double[MAX_TRAINSZ][INPUTN];
    	ECG_IDEAL = new double[MAX_TRAINSZ][OUTPUTN];
    	int cont = 0;
    	
    	try {
			reader = new CSVReader(new FileReader(filename));
			
			while ((line = reader.readNext()) != null) {
				for (int i = 0; i < INPUTN; i++) {
					ECG_INPUT[cont][i] = Double.parseDouble(line[i]);
				}
				ECG_IDEAL[cont][0] = Double.parseDouble(line[INPUTN]);
				cont++;
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
     * Read test data - uses the test file generated
     * by Demo4 from BackpropSp project
     * @param datum training file
     */
    public static void readTestData(String filename) {
    	String[] line;
    	ECG_TEST = new double[NTEST + VTEST][INPUTN];
    	int i = 0, cont = 0;
    	
    	try {
			reader = new CSVReader(new FileReader(filename));
			
			while ((line = reader.readNext()) != null) {
				ECG_TEST[cont][i++] = Double.parseDouble(line[0]);
				
				if (i == INPUTN) {
					cont++;
					i = 0;
				}
			}
			
			System.out.println(cont);
			
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
	 * The main method.
	 * @param args No arguments are used.
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static void main(final String args[]) throws FileNotFoundException, IOException, ClassNotFoundException {

		// create a neural network, without using a factory
		BasicNetwork network = new BasicNetwork();
		network.addLayer(new BasicLayer(null, true, INPUTN));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), true, MIDDLEN));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), false, OUTPUTN));
		network.getStructure().finalizeStructure();
		network.reset();

		// read training data & create training set
		readTrainingData(DEFAULT_TRAIN);
		MLDataSet trainingSet = new BasicMLDataSet(ECG_INPUT, ECG_IDEAL);

		// train the neural network with Backpropagation
		final MLTrain train = new Backpropagation(network, trainingSet, 0.25, 0.9);
		
		// train the neural network with ResilientBackpropagation
		// final MLTrain train = new ResilientPropagation(network, trainingSet, 0.25, 0.9);
				
		// train the neural network with LMA
		// final MLTrain train = new LevenbergMarquardtTraining(network, trainingSet);

		int epoch = 1;

		Long t1, t2;
		
		t1 = System.currentTimeMillis();
		
		do {
			train.iteration();
			System.out.println("Epoch #" + epoch + " Error:" + train.getError());
			epoch++;
		} while(train.getError() > 0.002);
		
		t2 = System.currentTimeMillis();
		
		System.out.println("Training execution time was " + (t2 - t1) / 1000 + " s.");

		// read test data & create test set
		readTestData(DEFAULT_TEST);
		MLDataSet testSet = new BasicMLDataSet(ECG_TEST, null);
		
		int nb = 0, nv = 0;
		
		// test the neural network
		System.out.println("Neural Network Results:");
		for(MLDataPair pair: testSet ) {
			final MLData output = network.compute(pair.getInput());
			if (output.getData(0) < 0.1) {
				nv++;
			}
			else {
				nb++;
			}
		}
		
		System.out.println("NB: " + nb + " NV: " + nv);
	}
}
