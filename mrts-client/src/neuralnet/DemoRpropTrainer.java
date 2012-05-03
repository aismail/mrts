package neuralnet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import fnn.dataio.CsvToPatternList;
import fnn.dataio.PatternList;
import fnn.network.RpropNetwork;
import fnn.util.NetworkStruct;
import fnn.util.RunParams;

/**
 * Demo - train with resilient backprop network
 * 
 * @author cbarca
 */
public class DemoRpropTrainer {
	public static final String NETWORK_FILENAME = "demo.serial";
    public static final String PARAMS_FILENAME = "runx.xml";
    private static final String TRAIN_FILENAME = "demo.trn";
    
    private RunParams run_params;
    private DemoRprop rp;
    private PatternList pl;
    private NetworkStruct net_struct;

    /**
     * Create network trainer
     * @param params run parameters object
     * @throws ClassNotFoundException 
     * @throws IOException 
     * @throws FileNotFoundException 
     */
    public DemoRpropTrainer(RunParams params) 
    	throws FileNotFoundException, IOException, ClassNotFoundException {
    	run_params = params;
    	
    	this.initTrainer();
    }
    
    /**
     * Create network trainer
     * @param params_filename run parameters filename
     * @throws ClassNotFoundException 
     * @throws IOException 
     * @throws FileNotFoundException 
     */
    public DemoRpropTrainer(String params_filename) 
    	throws FileNotFoundException, IOException, ClassNotFoundException {
    	run_params = new RunParams();
		this.readParams(params_filename, run_params);
		
		this.initTrainer();
      }
    
    /**
     * Initialize network trainer
     * @throws FileNotFoundException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void initTrainer() 
    	throws FileNotFoundException, IOException, ClassNotFoundException {
    	net_struct = run_params.getNetStruct();
    	RpropNetwork network = new RpropNetwork(net_struct);
    	
    	rp = new DemoRprop(network);
    	
    	if (run_params.getInputLocation().name().equals(
    			RunParams.InputLocation.LocalDir.name())) {
    		this.loadTrainingData(run_params.getInputPath());
    	}
    	else if (run_params.getInputLocation().name().equals(
    			RunParams.InputLocation.PatternFile.name())) {
    		this.loadPatternList(run_params.getInputPath());
    	}
    }

    /**
     * Read run parameters from XML file
     * @param filename name of the XML file
     * @param run_params run parameters object
     * @throws FileNotFoundException 
     */
    private void readParams(String filename, RunParams run_params) 
    	throws FileNotFoundException {
    	BufferedReader finput;
		finput = new BufferedReader(new FileReader(filename));
		run_params.readFromXML(finput);
    }
    
    /**
     * Load training datum from directory
     * @param path the path to directory
     */
    private void loadTrainingData(String path) 
    	throws IOException, FileNotFoundException, ClassNotFoundException {
    	CsvToPatternList csvp = new CsvToPatternList();
    	pl = csvp.convert(path, net_struct.getInputPop(), 
    			net_struct.getOutputPop(), TRAIN_FILENAME, true, false);
    }

    /**
     * Load training datum from pattern file
     * @param path the path to pattern file
     * @throws FileNotFoundException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void loadPatternList(String path) 
    	throws FileNotFoundException, IOException, ClassNotFoundException {
    	File datum = new File(path);
    	pl.reader(datum);
    }
    
	/**
	 * Train the network on these patterns
	 */
	public void performTraining() {	
		rp.trainNetwork(pl, net_struct.getError(), net_struct.getMaxEpochs(), 
				net_struct.getThreshold(), true, null);
	}

    /**
     * Save this network for later use
     * @param datum file to save as
     */
    public void saveTraining(File datum) throws IOException, FileNotFoundException {
    	rp.saveNetwork(datum);
    }
    
    /**
     * Return the pattern list 
     * @return pattern list
     */
    public PatternList getPatternList() {
    	return pl;
    }
}
