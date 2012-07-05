package gui.plotter;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;

import fnn.visual.ICallbackPlotter;
import fnn.visual.XYSeriesPlotter;
import gui.dmodel.NeuralNetSSE;
import gui.dmodel.ResponseListModel;

/**
 * Neural net callback plotter class - implements ICallbackPloter
 * interface from 'fnn' module for plotting the error line (described
 * by the sum of squared error value received), some extra features added
 * for listing (in the same time) the values into a JList container
 * 
 * @author cbarca
 */
public class NeuralNetCallbackPlotter extends JPanel implements ICallbackPlotter {
	public static final Dimension DEFAULT_DIMENSION = new Dimension(600, 600);
	private static final long serialVersionUID = 1088364981099379609L;
	private XYSeriesPlotter _xySeriesPlotter;
	private ResponseListModel<NeuralNetSSE> _listMSE;

	public NeuralNetCallbackPlotter(ResponseListModel<NeuralNetSSE> listMSE) {
		_listMSE = listMSE;
		_xySeriesPlotter = new XYSeriesPlotter("Sum of squared error", 1, DEFAULT_DIMENSION);
		_xySeriesPlotter.getChartPlot().setBackgroundPaint(Color.white);
		_xySeriesPlotter.getChartPlot().setRangeGridlinePaint(Color.gray);
		_xySeriesPlotter.addSeries("error");
		this.add(_xySeriesPlotter.getChartPanel());
		this.setVisible(true);
	}

	@Override
	public void addXYValue(String series, double xval, double yval, long timestamp) {
		_listMSE.add(new NeuralNetSSE(xval, yval, timestamp));
		_xySeriesPlotter.addXYValue(series, xval, yval);
	}
	
	public XYSeriesPlotter getXYSeriesPlotter() {
		return _xySeriesPlotter;
	}
}
