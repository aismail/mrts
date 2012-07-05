package neuralnet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mrtsio.mexport.Exporter;
import mrtsio.polling.QErrorPolling;
import fnn.network.AbstractArc;
import fnn.network.Rprop;
import fnn.network.RpropNetwork;
import fnn.util.RunParams;
import fnn.util.RunParams.OutputFormat;
import fnn.visual.ICallbackPlotter;
import gui.dmodel.NeuralNetSSE;
import gui.dmodel.ResponseListModel;
import gui.plotter.NeuralNetCallbackPlotter;

/**
 * Neural network - 2 ways trainer (local & distributed)
 * (manages the separation between both trainers, gets specific
 * commands from the UI)
 *  
 * @author cbarca
 */
public class NeuralNet {
	public static final String RUN_PARAMS_EXT = "_run.xml";
	
	private RunParams _runParams = null;
	private ITrainer _trainer = null;
	private ITester _tester = null;
	private ICallbackPlotter _callbackPlotter = null;
	private ResponseListModel<NeuralNetSSE> _listSSE = null;
	
	public static enum RunMode {
		LocalIris, LocalIono, LocalArrhy208, 
		LocalArrhy, Distributed;
	}
	
	/**
	 * Default constructor - private
	 */
	private NeuralNet() {
		_listSSE = new ResponseListModel<NeuralNetSSE>();
		_listSSE.add(new NeuralNetSSE(0, 0, 0));
		_callbackPlotter = new NeuralNetCallbackPlotter(_listSSE);
	}
	
	/**
	 * Specialized constructor
	 * @param filename run params filename
	 * @throws FileNotFoundException
	 */
	public NeuralNet(String filename) 
		throws FileNotFoundException {
		this();
		_runParams = new RunParams();
		readParams(filename, _runParams);
	}
	
	/**
	 * Specialized constructor
	 * @param runParams run params object
	 */
	public NeuralNet(RunParams runParams) {
		this();
		_runParams = runParams;
	}
	
	/**
     * Read run parameters from XML file
     * @param filename name of the XML file
     * @param runParams run parameters object
     * @throws FileNotFoundException 
     */
    public static void readParams(String filename, RunParams runParams) 
    	throws FileNotFoundException {
    	BufferedReader finput;
		finput = new BufferedReader(new FileReader(filename));
		runParams.readFromXML(finput);
    }
	
	/**
	 * Set the local trainer for the neural network
	 * @param trainer local neural network trainer
	 */
	public void setLocalTrainer(ITrainer trainer) {
		_trainer = trainer;
	}
	
	/**
	 * Set the local tester for the neural network
	 * @param tester local neural network tester
	 */
	public void setLocalTester(ITester tester) {
		_tester = tester;
	}
	
	/**
	 * Get the run parameters
	 * @return run parameters object
	 */
	public RunParams getRunParams() {
		return _runParams;
	}
	
	/**
	 * Get the callback plotter 
	 * @return callback plotter object
	 */
	public ICallbackPlotter getCallbackPlotter() {
		return _callbackPlotter;
	}
	
	/**
	 * Get the sum of squared error list
	 * @return MSE list
	 */
	public ResponseListModel<NeuralNetSSE> getListSSE() {
		return _listSSE;
	}
	
	/**
	 * Train the neural network
	 * @param dflag TRUE on distributed / FALSE on serial-local
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws InterruptedException
	 */
	public void train(RunMode runMode) 
		throws FileNotFoundException, IOException,  ClassNotFoundException, 
		InterruptedException {
		_runParams.writeToXML(_runParams.getExperimentName() + RUN_PARAMS_EXT);
				
		if (runMode.equals(RunMode.Distributed)) {
			this.trainDistributed();
			return;
		}
		
		this.trainSerial();
	}
	
	/**
	 * Test the neural network using the local serial tester
	 */
	public void test() {
		if (_tester == null) {
			return;
		}
		
		System.out.println("Local");
		
		System.out.println("PatternList loaded w/" + 
				_tester.getPatternList().size()  + " patterns");
		
		_tester.performTesting();
		
		System.out.println("end");
	}
	
	/**
	 * Save in local the exported rprop network, serialization  
	 * @param rpn exported rprop network (from Cassandra)
	 */
	private void saveExportedRpropNetToSerial(RpropNetwork rpn) {
		try {
			Rprop rp = new Rprop(rpn);
			rp.saveNetwork(new File(_runParams.getOutputPath() + "/" + 
					_runParams.getNetworkName() + RpropTrainer.NETWORK_FILENAME_EXT));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Save in local the exported rprop network, into CSV format
	 * @param rpn exported rprop network (from Cassandra)
	 */
	private void saveExportedRpropNetToCSV(RpropNetwork rpn) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(
					new File(_runParams.getOutputPath() + "/" + 
						_runParams.getNetworkName() + RpropTrainer.NETWORK_CSV_EXT)));
			
			writer.write(_runParams.getNetStruct().getInputPop() + ",");
			for (Integer middlepop : _runParams.getNetStruct().getMiddlezPop()) {
				writer.write(middlepop + ",");
			}
			writer.write(_runParams.getNetStruct().getOutputPop() + "\n");
			
			for (AbstractArc arc : rpn.getArcs()) {
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
	 * Train the neural network using the local serial trainer
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void trainSerial() 
		throws FileNotFoundException, IOException, ClassNotFoundException {
		
		if (_trainer == null) {
			return;
		}
		
		System.out.println("Local");
		System.out.println("begin");

		System.out.println("PatternList loaded w/" + 
				_trainer.getPatternList().size()  + " patterns");
		
		_trainer.performTraining(_callbackPlotter);
		
		if (_runParams.getOutputFormat().equals(OutputFormat.Serial)) {
			_trainer.saveTrainingSerial();
		}
		else if (_runParams.getOutputFormat().equals(OutputFormat.CSV)) {
			_trainer.saveTrainingCSV();
		}
		
		System.out.println("end");
	}
	
	/**
	 * Train the neural network using the distributed system
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void trainDistributed() 
		throws IOException, InterruptedException {
		int count = 0;
		double yval = 1;
		long lastTimestamp = 0, currTimestamp = 0, 
			waitTime = 1 * 60 * 1000, time; // #minutes 
		
		String filename = _runParams.getExperimentName() + RUN_PARAMS_EXT;
		String[] cmd = {"./mrts_rstart.sh", "hduser@hadoop-e", filename, "5"};
		Runtime.getRuntime().exec(cmd);
		
		System.out.println("Distributed");
		System.out.println("begin");
		
		QErrorPolling qerrp = new QErrorPolling();
		
		while (yval > 0) {
			// Sleep for 'time';
			Thread.sleep(waitTime);
			// Pull last qerror (sum of squared error)
			List<Long> tList = new ArrayList<Long>();
			List<Object> list = qerrp.getLastQErrors(_runParams.getExperimentName(), 1, tList);
			// Add  yvals to plotter (the last value is the last extracted yval)
			for (int i = 0; i < list.size() - 1; i++) {
				yval = (Double)list.get(i);
				time = tList.get(i);
				System.out.println(yval);
				_callbackPlotter.addXYValue("error", ++count, yval, time);
			}
			
			if (list.size() > 0) {
				// Extract last yval value - on -1 stop plotting
				yval = (Double)list.get(list.size() - 1);
			}
			
			// Recompute time
			currTimestamp = (Long)tList.get(list.size() - 1);
			if (lastTimestamp != 0) {
				waitTime = currTimestamp - lastTimestamp;
			}
			lastTimestamp = currTimestamp;
		}
		
		System.out.println("end");
		
		// Saving the network
		if (yval <= 0) { // We know in this way that perhaps the train has finished
			RpropNetwork rpn = Exporter.exportRpropNeuralNet(_runParams);
			
			if (_runParams.getOutputFormat().equals(OutputFormat.Serial)) {
				this.saveExportedRpropNetToSerial(rpn);
			}
			else if (_runParams.getOutputFormat().equals(OutputFormat.CSV)) {
				this.saveExportedRpropNetToCSV(rpn);
			}
		}
	}
}
