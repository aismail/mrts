package fnn.visual;

import org.jfree.chart.ChartPanel;

public interface IPlotter {
	public void addXYValue(String series, double x, double y);
	public void addSeries(String name);
	public ChartPanel getChartPanel();
}
