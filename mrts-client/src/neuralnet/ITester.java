package neuralnet;

import fnn.dataio.PatternList;

/**
 * Neural network tester wrapper interface
 * 
 * @author cbarca
 */
public interface ITester {
	public void performTesting();
	public PatternList getPatternList();
}
