package fnn.visual;

import java.awt.Dimension;
import java.util.HashMap;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * XY Series Plotter
 * XY series/curve drawing implementation
 * 
 * @author cbarca
 */
public class XYSeriesPlotter implements IPlotter {
	public static final Dimension DEFAULT_DIMENSION = new Dimension(600, 600);
	private double[] _buff_xvals, _buff_yvals;
	private String _title;
	private int _buff_size = 0, _max_buff_size = 0;
	private JFreeChart _chart;
	private ChartPanel _chart_panel;
	private XYSeriesCollection _data;
	private HashMap<String, XYSeries> _hseries;
	
	public XYSeriesPlotter(String title, int max_buff_size) {
		this(title, max_buff_size, DEFAULT_DIMENSION);
	}
	
	public XYSeriesPlotter(String title, int max_buff_size, Dimension dimension) {
		_max_buff_size = max_buff_size;
		_buff_xvals = new double[_max_buff_size];
		_buff_yvals = new double[_max_buff_size];
		_title = title;
		_data = new XYSeriesCollection();
		_hseries = new HashMap<String, XYSeries>();
		_chart = ChartFactory.createXYLineChart(
				_title, 
				"X", 
				"Y", 
				_data, 
				PlotOrientation.VERTICAL, 
				true, true, false);
		_chart_panel = new ChartPanel(_chart);
		_chart_panel.setPreferredSize(dimension);
	}
	
	@Override
	public void addSeries(String name) {
		XYSeries series = new XYSeries(name);
		_data.addSeries(series);
		_hseries.put(name, series);
	}

	@Override
	public void addXYValue(String series, double x, double y) {
		_buff_xvals[_buff_size] = x;
		_buff_yvals[_buff_size] = y;
		_buff_size++;
		
		if (_buff_size == _max_buff_size) {
			for (int i = 0; i < _buff_xvals.length; i++) {
				_hseries.get(series).add(_buff_xvals[i], _buff_yvals[i]);
			}
			
			_buff_size = 0;
			
			return;
		}
	}
	
	@Override
	public ChartPanel getChartPanel() {
		return _chart_panel;
	}
	
	@Override
	public XYPlot getChartPlot() {
		return _chart.getXYPlot();
	}
}
