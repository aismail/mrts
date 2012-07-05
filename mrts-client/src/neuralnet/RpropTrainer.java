package neuralnet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import fnn.dataio.CsvToPatternList;
import fnn.dataio.PatternList;
import fnn.network.AbstractArc;
import fnn.network.Rprop;
import fnn.network.RpropNetwork;
import fnn.util.NetworkStruct;
import fnn.util.RunParams;
import fnn.visual.ICallbackPlotter;

/**
 * Rprop trainer class - wrapper over 'fnn' module 
 * - calling the local neural network training/saving methods
 * - loading the train-data to internal data structure (pattern list)
 * 
 * @author cbarca
 */
public class RpropTrainer implements ITrainer {
	public static final String TRAIN_FILENAME_EXT = ".trn";
    public static final String NETWORK_FILENAME_EXT = ".serial";
    public static final String NETWORK_CSV_EXT = ".csv";
    
    private RunParams _runParams;
    private Rprop _rp;
    private PatternList _pl;
    private RpropNetwork _network;
    private NetworkStruct _netStruct;

    /**
     * Create network trainer
     * @param params run parameters object
     * @param runMode which local trainer to be used
     * @throws ClassNotFoundException 
     * @throws IOException 
     * @throws FileNotFoundException 
     */
    public RpropTrainer(RunParams params, NeuralNet.RunMode runMode) 
    	throws FileNotFoundException, IOException, ClassNotFoundException {
    	_runParams = params;
    	
    	this.initTrainer(runMode);
    }
    
    /**
     * Create network trainer
     * @param paramsFilename run parameters filename
     * @param runMode which local trainer to be used
     * @throws ClassNotFoundException 
     * @throws IOException 
     * @throws FileNotFoundException 
     */
    public RpropTrainer(String paramsFilename, NeuralNet.RunMode runMode) 
    	throws FileNotFoundException, IOException, ClassNotFoundException {
    	_runParams = new RunParams();
		NeuralNet.readParams(paramsFilename, _runParams);
		
		this.initTrainer(runMode);
      }
    
    /**
     * Initialize network trainer
     * @throws FileNotFoundException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void initTrainer(NeuralNet.RunMode runMode) 
    	throws FileNotFoundException, IOException, ClassNotFoundException {
    	_netStruct = _runParams.getNetStruct();
    	_network = new RpropNetwork(_netStruct);
    	
    	_rp = RpropFactory.getRprop(runMode, _network);
    	
    	_pl = new PatternList();
    	
    	if (_runParams.getInputLocation().equals(
    			RunParams.InputLocation.LocalDir)) {
    		this.loadTrainingData(_runParams.getInputPath());
    	}
    	else if (_runParams.getInputLocation().equals(
    			RunParams.InputLocation.PatternFile)) {
    		this.loadPatternList(_runParams.getInputPath());
    	}
    }
    
    /**
     * Load training datum from directory
     * @param path the path to directory
     */
    private void loadTrainingData(String path) 
    	throws IOException, FileNotFoundException, ClassNotFoundException {
    	CsvToPatternList csvp = new CsvToPatternList();
    	_pl = csvp.convert(path, _netStruct.getInputPop(), 
    			_netStruct.getOutputPop(), 
    			_runParams.getExperimentName() + TRAIN_FILENAME_EXT, 
    			// TODO: auto-normalizer
    			false, false);
    }

    /**
     * Load training datum from pattern file
     * @param filepath the path to pattern file
     * @throws FileNotFoundException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void loadPatternList(String filepath) 
    	throws FileNotFoundException, IOException, ClassNotFoundException {
    	File datum = new File(filepath);
    	_pl.reader(datum);
    }
    
	/**
	 * Train the network on these patterns
	 */
	public void performTraining(ICallbackPlotter callbackPlotter) {	
		_rp.trainNetwork(_pl, _netStruct.getError(), _netStruct.getMaxEpochs(), 
				_netStruct.getThreshold(), 1, callbackPlotter);
	}

    /**
     * Save the trained network for later use, serialization 
     */
    public void saveTrainingSerial() {
    	try {
			_rp.saveNetwork(new File(_runParams.getOutputPath() + "/" +
					_runParams.getNetworkName() + NETWORK_FILENAME_EXT));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    /**
     * Save the trained network for later use, CSV file 
     */
    public void saveTrainingCSV() {
    	try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(
					new File(_runParams.getOutputPath() + "/" + 
						_runParams.getNetworkName() + RpropTrainer.NETWORK_CSV_EXT)));
			
			writer.write(_runParams.getNetStruct().getInputPop() + ",");
			for (Integer middlepop : _runParams.getNetStruct().getMiddlezPop()) {
				writer.write(middlepop + ",");
			}
			writer.write(_runParams.getNetStruct().getOutputPop() + "\n");
			
			for (AbstractArc arc : _network.getArcs()) {
				writer.write(arc.getInputNode().getId() + "," + 
						arc.getOutputNode().getId() + "," +  
						arc.getWeight() + "\n");
			}
			
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    /**
     * Return the pattern list 
     * @return pattern list
     */
    public PatternList getPatternList() {
    	return _pl;
    }
}
