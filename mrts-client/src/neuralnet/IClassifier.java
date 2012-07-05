package neuralnet;

/**
 * Rprop classifier interface
 * (rprop extension for classifying the neural 
 * network output - on query)
 * 
 * @author cbarca
 */
public interface IClassifier {
	public int classifier(double[] input);
}
