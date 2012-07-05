package gui.mediator;

import fnn.util.NetworkStruct;
import fnn.util.RunParams;
import fnn.util.RunParams.InputLocation;
import fnn.util.RunParams.OutputFormat;

/**
 * NeuralNet GUI mediator class - takes care of data models initialization
 * 
 * @author cbarca
 */
public class NeuralNetMediator {
	private RunParams _runParams;
	private static NeuralNetMediator _nnMediator = null;
	
	public static NeuralNetMediator getInstance() {
		if (_nnMediator == null) {
			_nnMediator = new NeuralNetMediator();
		}
		
		return _nnMediator;
	}
	
	private NeuralNetMediator() {
		_runParams = new RunParams();
		_runParams.setExperimentName("name");
		_runParams.setInputPath("path");
		_runParams.setNetworkName("name");
		_runParams.setOutputPath("path");
		_runParams.setInputLocation(InputLocation.LocalDir);
		_runParams.setOutputFormat(OutputFormat.Serial);
		
		// Default network structure
		NetworkStruct netStruct = new NetworkStruct(0.0, 0.0, 0);
		netStruct.addMiddlePop(0);
		_runParams.setNetStruct(netStruct);
	}
	
	public RunParams getRunParams() {
		return _runParams;
	}
}
