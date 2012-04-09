package neuralnet.mapred.dmodel;

import java.io.Serializable;

/**
 * Output Error
 * Store the output error.
 * 
 * @author cbarca
 */
public class OutputError implements Serializable {
	// Private memebers
	private static final long serialVersionUID = 1L;
	private double _output_error;
	
	public OutputError(double output_error) {
		_output_error = output_error;
	}
	
	public double getValue() {
		return _output_error;
	}
}
