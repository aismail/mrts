package neuralnet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import fnn.network.Rprop;
import fnn.network.RpropNetwork;
import neuralnet.arrhy.ArrhyRprop;
import neuralnet.arrhy208.Arrhy208Rprop;
import neuralnet.iono.IonoRprop;
import neuralnet.iris.IrisRprop;

/**
 * Rprop factory class - factory like pattern implementation
 * (get rprop extensions specific instances) 
 * 
 * @author cbarca
 */
public class RpropFactory {
	public static Rprop getRprop(NeuralNet.RunMode ext, RpropNetwork rpn) {
		switch (ext) {
			case LocalIris:
				return new IrisRprop(rpn);
			case LocalIono:
				return new IonoRprop(rpn);
			case LocalArrhy208:
				return new Arrhy208Rprop(rpn);
			case LocalArrhy:
				return new ArrhyRprop(rpn);
		}
		
		return null;
	}
	
	public static Rprop getRprop(NeuralNet.RunMode ext, File rpn) 
		throws FileNotFoundException, IOException, ClassNotFoundException {
		switch (ext) {
			case LocalIris:
				return new IrisRprop(rpn);
			case LocalIono:
				return new IonoRprop(rpn);
			case LocalArrhy208:
				return new Arrhy208Rprop(rpn);
			case LocalArrhy:
				return new ArrhyRprop(rpn);
		}
		
		return null;
	}
}
