package gui.view;

import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JButton;

/**
 * Neural net command bar view - view for command panel 
 * (on the bottom of the experiment's form view)
 * 
 * @author cbarca
 */
public class NeuralNetCmdBarView extends JPanel {
	private static final long serialVersionUID = 8973456311930549976L;
	private JButton _btnSaveExp, _btnRunExp, _btnLoadExp, 
		_btnStopExp, _btnViewMonitor;
	
	public NeuralNetCmdBarView() {	
		_btnSaveExp = new JButton("Save");
		add(_btnSaveExp);
		
		_btnLoadExp = new JButton("Load");
		add(_btnLoadExp);
		
		_btnRunExp = new JButton("Run");
		add(_btnRunExp);
		
		_btnStopExp = new JButton("Stop");
		_btnStopExp.setEnabled(false);
		add(_btnStopExp);
		
		_btnViewMonitor = new JButton("View Monitor");
		_btnViewMonitor.setEnabled(false);
		add(_btnViewMonitor);
	}
	
	public void addBtnSaveExpListener(ActionListener actionListener) {
		_btnSaveExp.addActionListener(actionListener);
	}
	
	public void addBtnLoadExpListener(ActionListener actionListener) {
		_btnLoadExp.addActionListener(actionListener);
	}
	
	public void addBtnRunExpListener(ActionListener actionListener) {
		_btnRunExp.addActionListener(actionListener);
	}
	
	public void addBtnStopExpListener(ActionListener actionListener) {
		_btnStopExp.addActionListener(actionListener);
	}
	
	public void addBtnViewMonitorListener(ActionListener actionListener) {
		_btnViewMonitor.addActionListener(actionListener);
	}
	
	public void setBtnRunExpEnable(boolean enable) {
		_btnRunExp.setEnabled(enable);
	}
	
	public void setBtnStopExpEnable(boolean enable) {
		_btnStopExp.setEnabled(enable);
	}
	
	public void setBtnViewMonitorEnable(boolean enable) {
		_btnViewMonitor.setEnabled(enable);
	}
}
