package fnn.visual;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.XYPlot;

/**
 * Interface for the plotting mechanism used in error's curve display
 * 
 * @author cbarca
 */
public interface IPlotter {
	public void addXYValue(String series, double x, double y);
	public void addSeries(String name);
	public ChartPanel getChartPanel();
	public XYPlot getChartPlot();
}
