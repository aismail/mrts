package neuralnet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import mrtsio.pooling.QErrorPooling;

import fnn.util.RunParams;

/**
 * Neural network - 2 ways trainer 
 *  
 * @author cbarca
 */
public class NeuralNetwork {
	public static final String DEFAULT_FILENAME = "mrts_run.xml";
	private RunParams _run_params;
	private String _filename;
	
	public NeuralNetwork(String filename) 
		throws FileNotFoundException {
		_filename = filename;
		_run_params = new RunParams();
		BufferedReader finput = new BufferedReader(new FileReader(filename));
		_run_params.readFromXML(finput);
	}
	
	public NeuralNetwork(RunParams run_params) {
		_run_params = run_params;
		_filename = DEFAULT_FILENAME;
		_run_params.writeToXML(_filename);
	}
	
	public RunParams getRunParams() {
		return _run_params;
	}
	
	public void train(boolean dflag) 
		throws FileNotFoundException, IOException, 
		ClassNotFoundException, InterruptedException {
		
		if (dflag) {
			this.trainDistributed();
			return;
		}
		
		this.trainSerial();
	}
	
	private void trainSerial() 
		throws FileNotFoundException, IOException, ClassNotFoundException {
		System.out.println("begin");
		
		DemoRpropTrainer tr = new DemoRpropTrainer(_run_params);

		System.out.println("PatternList loaded w/" + 
				tr.getPatternList().size()  + " patterns");
		
		tr.performTraining();
		
		tr.saveTraining(new File(DemoRpropTrainer.NETWORK_FILENAME));

		System.out.println("end");
	}
	
	private void trainDistributed() 
		throws IOException, InterruptedException {
		
		String[] cmd = {"./mrts_start.sh", "mocke@localhost", _filename};
		//Runtime.getRuntime().exec(cmd);
		
		QErrorPooling qerrp = new QErrorPooling();
		
		Thread.sleep(120000);
		
		while(true) {
			Thread.sleep(10000);
			List<Object> list = qerrp.getLastQErrors(_run_params.getExperimentName(), 1);
			
			for (int i = 0; i < list.size() - 1; i++) {
				System.out.println((Double)list.get(i));
			}
		}
	}
}
