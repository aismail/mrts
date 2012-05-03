package driver;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import fnn.util.RunParams;
import mrtsio.mimport.Importer;
import neuralnet.NeuralNetwork;

/**
 * Neural network driver class for NeuralNetwork module
 * 
 * @author cbarca
 */
public class NeuralNetDriver {

	public static void main(String[] args) 
		throws IOException, ClassNotFoundException {
		
		RunParams run_params = new RunParams();
		BufferedReader finput = new BufferedReader(new FileReader("runx.xml"));
		run_params.readFromXML(finput);
		
		Importer.importTrainingDataToHDFS(run_params.getInputPath(), null, 323);
		
		NeuralNetwork nn = new NeuralNetwork(run_params);
		nn.train(false);
	}
}
