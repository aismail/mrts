package driver;

import java.io.IOException;

import neuralnet.ITester;
import neuralnet.NeuralNet;
import neuralnet.RpropTester;

/**
 * Driver class for NeuralNetTest
 * 
 * @author cbarca
 */
public class NeuralNetTestDriver {
	
	public static void main(String[] args) 
		throws IOException, ClassNotFoundException {
		NeuralNet nn = new NeuralNet("iris_run.xml");
		
		ITester tester = new RpropTester(nn.getRunParams(), NeuralNet.RunMode.LocalIris);
		nn.setLocalTester(tester);
		tester.performTesting();
	}
}
