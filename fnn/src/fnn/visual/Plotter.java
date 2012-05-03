package fnn.visual;

import java.util.List;
import javax.swing.JFrame;

import org.math.plot.Plot2DPanel;
import org.math.plot.utils.Array;

public class Plotter extends JFrame implements IPlotter {
	private static final long serialVersionUID = 2665189929262006143L;
	Plot2DPanel _lplot, _bplot;
	
	public Plotter(String pname) {
		super(pname);
	
		this.setSize(600, 600);
		this.setVisible(true);
	}
	
	@Override
	public void setQerrPlot(String pname, List<Double> qerr) {
		double[] x, y;
		
		// add a line plot to the PlotPanel
		x = Array.increment(qerr.size(), 1, 1);
		
		y = new double[qerr.size()];
		for (int i = 0; i < qerr.size(); i++) {
			y[i] = qerr.get(i);
		}

		_lplot = new Plot2DPanel();
		// define the legend position
		_lplot.addLegend("SOUTH");
		_lplot.addLinePlot(pname, x, y);
		
		this.setContentPane(_lplot);
	}
	
	@Override
	public void setMatchPlot(String pname, List<Integer> match) {
		double[] y;
		
		y = new double[match.size()];
		for (int i = 0; i < match.size(); i++) {
			y[i] = match.get(i);
		}
		
		_bplot = new Plot2DPanel();
		// define the legend position
		_bplot.addLegend("SOUTH");
		_bplot.addBarPlot(pname, y);
		
		this.setContentPane(_bplot);
	}
}
