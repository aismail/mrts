package neuralnet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import fnn.util.RunParams;

/**
 * Neural network - 2 ways trainer 
 *  
 * @author cbarca
 */
public class NeuralNetwork {
	private RunParams _run_params;
	
	public NeuralNetwork(RunParams run_params) {
		_run_params = run_params;
	}
	
	public void train(boolean dflag) 
		throws FileNotFoundException, IOException, ClassNotFoundException {
		
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
	
	private void trainDistributed() {
		
	}
}
