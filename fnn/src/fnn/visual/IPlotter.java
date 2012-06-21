package fnn.visual;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.XYPlot;

public interface IPlotter {
	public void addXYValue(String series, double x, double y);
	public void addSeries(String name);
	public ChartPanel getChartPanel();
	public XYPlot getChartPlot();
}
