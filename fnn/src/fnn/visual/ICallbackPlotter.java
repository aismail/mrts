package fnn.visual;

public interface ICallbackPlotter {
	public void addQErr(double qerr);
	public void addMatch(int match);
	public void setRefreshTime(int time);
}
