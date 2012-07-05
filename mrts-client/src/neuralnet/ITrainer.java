package neuralnet;

import fnn.dataio.PatternList;
import fnn.visual.ICallbackPlotter;

/**
 * Neural network trainer wrapper interface 
 * 
 * @author cbarca
 */
public interface ITrainer {
	public void performTraining(ICallbackPlotter callbackPlotter);
	public void saveTrainingSerial();
	public void saveTrainingCSV();
	public PatternList getPatternList();
}
