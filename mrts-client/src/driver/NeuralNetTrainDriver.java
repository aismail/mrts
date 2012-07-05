package driver;

import java.io.IOException;

import mrtsio.mimport.Importer;
import neuralnet.NeuralNet;

/**
 * Driver class for NeuralNetTrain
 * 
 * @author cbarca
 */
public class NeuralNetTrainDriver {

	public static void main(String[] args) 
		throws IOException, ClassNotFoundException, InterruptedException  {
		//*****//
		NeuralNet nn = new NeuralNet("arrhyall_run.xml");
		Importer.importTrainingDataToHDFS(nn.getRunParams().getInputPath(), null, 1000, 4);
	}
}
