package neuralnet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import fnn.dataio.CsvToPatternList;
import fnn.dataio.PatternList;
import fnn.util.RunParams;

/**
 * Rprop tester class - wrapper over 'fnn' module 
 * - calling the local rprop extension (classifier)
 * - loading the test-data to internal data structure (pattern list)
 * 
 * @author cbarca
 */
public class RpropTester implements ITester {
	public static final String TEST_FILENAME_EXT = ".test";
	public static final String RESULTS_FILENAME = "results.txt";
	public static final String DEFAULT_TEST_PATH = "./dataset/iono/test";
	
	private RunParams _runParams;
	private IClassifier _rpClassifier;
	private PatternList _pl;
	
	 /**
     * Create network tester
     * @param params run parameters object
     * @param runMode which local tester to be used
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     * @throws FileNotFoundException 
     */
	public RpropTester(RunParams runParams, NeuralNet.RunMode runMode) 
		throws FileNotFoundException, IOException, ClassNotFoundException {
		_runParams = runParams;
		
		this.initTester(new File(_runParams.getOutputPath() + "/" + 
				_runParams.getNetworkName() + RpropTrainer.NETWORK_FILENAME_EXT),
				runMode);
	}
	
	/**
     * Create network tester
     * @param paramsFilename run parameters filename
     * @param runMode which local tester to be used
     * @throws ClassNotFoundException 
     * @throws IOException 
     * @throws FileNotFoundException 
     */
	public RpropTester(String paramsFilename, NeuralNet.RunMode runMode) 
		throws FileNotFoundException, IOException, ClassNotFoundException {
		_runParams = new RunParams();
		this.readParams(paramsFilename, _runParams);
		
		this.initTester(new File(_runParams.getOutputPath() + "/" + 
				_runParams.getNetworkName() + RpropTrainer.NETWORK_FILENAME_EXT), 
				runMode);
	}
	
	 /**
     * Read run parameters from XML file
     * @param filename name of the XML file
     * @param runParams run parameters object
     * @throws FileNotFoundException 
     */
    private void readParams(String filename, RunParams runParams) 
    	throws FileNotFoundException {
    	BufferedReader finput;
		finput = new BufferedReader(new FileReader(filename));
		runParams.readFromXML(finput);
    }

	/**
	 * Initialize tester
	 * @param network resilient backpropagation network 
	 */
	private void initTester(File network, NeuralNet.RunMode runMode) 
	throws IOException, FileNotFoundException, ClassNotFoundException {    	
		_rpClassifier = (IClassifier) RpropFactory.getRprop(runMode, network);
		this.loadTestData(DEFAULT_TEST_PATH);
	}

	 /**
     * Load training datum from directory
     * @param path the path to directory
	 * @throws IOException 
	 * @throws FileNotFoundException 
     */
	private void loadTestData(String path) 
		throws FileNotFoundException, IOException {
		CsvToPatternList csvp = new CsvToPatternList();
		_pl = csvp.convert(path, _runParams.getNetStruct().getInputPop(),
				_runParams.getNetStruct().getOutputPop(), 
				_runParams.getNetworkName() + TEST_FILENAME_EXT, 
				true, false);
	}

	/** 
	 * Submit these points to the network for classification
	 */
	public void performTesting() {
		BufferedWriter bw;
		double[] input;
		
		try {
			bw = new BufferedWriter(new FileWriter(DEFAULT_TEST_PATH + "/" + RESULTS_FILENAME));
	
			for (int i = 0;  i < _pl.size(); i++) {
				input = _pl.get(i).getInput();
				writeData(bw, _rpClassifier.classifier(input));
			}

			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Write result
	 */
	private void writeData(BufferedWriter bw, int flag) throws Exception {
		bw.write(Integer.toString(flag));
		bw.newLine();
	}
	
	/**
     * Return the pattern list 
     * @return pattern list
     */
    public PatternList getPatternList() {
    	return _pl;
    }
}
