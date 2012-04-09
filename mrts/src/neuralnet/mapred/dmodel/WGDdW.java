package neuralnet.mapred.dmodel;

import java.io.Serializable;

/**
 * Weight Gradient DeltaWeight 
 * Store 3 values important for weight update.
 * 
 * @author cbarca
 */
public class WGDdW implements Serializable {
	// Private members
	private static final long serialVersionUID = 1L;
	private double _weight, _gradient, _delta, _deltaw;
	
	public WGDdW(double weight, double gradient, double delta, double deltaw) {
		_weight = weight;
		_gradient = gradient;
		_delta = delta;
		_deltaw = deltaw;
	}
	
	public double getWeight() {
		return _weight;
	}
	
	public double getGradient() {
		return _gradient;
	}
	
	public double getDelta() {
		return _delta;
	}
	
	public double getDeltaW() {
		return _deltaw;
	}
}
