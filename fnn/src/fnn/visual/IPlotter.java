package fnn.visual;

import java.util.List;

public interface IPlotter {
	public void setQerrPlot(String pname, List<Double> qerr);
	public void setMatchPlot(String pname, List<Integer> match);
}
