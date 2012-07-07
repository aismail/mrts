package fnn.visual;

/**
 * Interface for callback plotter
 * 
 * @author cbarca
 */
public interface ICallbackPlotter {
	public void addXYValue(String series, double xval, double yval, long timestamp);
}
