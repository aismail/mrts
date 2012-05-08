package driver;

import java.io.IOException;

import mrtsio.mimport.Importer;
import neuralnet.NeuralNetwork;

/**
 * Neural network driver class for NeuralNetwork module
 * 
 * @author cbarca
 */
public class NeuralNetDriver {

	public static void main(String[] args) 
		throws IOException, ClassNotFoundException, InterruptedException {		
		NeuralNetwork nn = new NeuralNetwork("iris_run.xml");
		//Importer.importTrainingDataToHDFS(nn.getRunParams().getInputPath(), null, 30);
		nn.train(true);
	}
}
