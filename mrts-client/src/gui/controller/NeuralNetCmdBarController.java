package gui.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import neuralnet.ITrainer;
import neuralnet.NeuralNet;
import neuralnet.RpropTrainer;

import fnn.util.RunParams;
import gui.interfaces.ITextFieldValidator;
import gui.mediator.NeuralNetMediator;
import gui.plotter.NeuralNetCallbackPlotter;
import gui.util.AppWindow;
import gui.view.NeuralNetCmdBarView;
import gui.view.NeuralNetExpView;
import gui.view.NeuralNetMonitorView;

/**
 * Controller class for neural network command bar
 * (basic commands: save, load, run, stop, view)
 * 
 * @author cbarca
 */
public class NeuralNetCmdBarController {
	private RunParams _runParams;
	private NeuralNetCmdBarView _nnCmdBarView;
	private NeuralNetExpView _nnExpView;
	private JFileChooser _jfChooser;
	private AppWindow _appW;
	private Thread _trainThread;

	public NeuralNetCmdBarController(NeuralNetMediator nnMediator, 
			NeuralNetCmdBarView nnCmdBarView,
			NeuralNetExpView nnExpView) {

		_runParams = nnMediator.getRunParams();
		_nnCmdBarView = nnCmdBarView;
		_nnExpView = nnExpView;

		this.addListeners();
	}

	private void addListeners() {
		_nnCmdBarView.addBtnSaveExpListener(new SaveExpListener());
		_nnCmdBarView.addBtnLoadExpListener(new LoadExpListener());
		_nnCmdBarView.addBtnRunExpListener(new RunExpListener());
		_nnCmdBarView.addBtnStopExpListener(new StopExpListener());
		_nnCmdBarView.addBtnViewMonitorListener(new ViewMonitorListener());
	}

	private boolean validateNNExpView() {
		List<ITextFieldValidator> validators = _nnExpView.getValidators();

		for (ITextFieldValidator validator : validators) {
			if (!validator.validate()) {
				JOptionPane.showMessageDialog(_nnExpView, 
						validator.getErrorMessage(), 
						"Form error",
						JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}

		return true;
	}

	public class SaveExpListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (validateNNExpView()) {
				_nnExpView.pushParams();
				_runParams.writeToXML(_runParams.getExperimentName() + "_run.xml");
			}
		}
	}

	public class LoadExpListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			_jfChooser = new JFileChooser("./");
			int returnVal = _jfChooser.showOpenDialog(_nnExpView);

			if (returnVal == JFileChooser.APPROVE_OPTION && 
					_jfChooser.getSelectedFile() != null) {
				String path = _jfChooser.getSelectedFile().getPath();
				
				try {
					NeuralNet.readParams(path, _runParams);
					_nnExpView.pullParams();
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}

	public class RunExpListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (validateNNExpView()) {
				_nnExpView.pushParams();
				_nnCmdBarView.setBtnRunExpEnable(false);
				_nnCmdBarView.setBtnStopExpEnable(true);
				
				_trainThread = new Thread(new Runnable() {
					public void run() {
						try {
							NeuralNet nn = new NeuralNet(_runParams);

							if (_nnExpView.getRunMode().name().contains("Local")) {
								ITrainer trainer = new RpropTrainer(nn.getRunParams(), 
										_nnExpView.getRunMode());
								nn.setLocalTrainer(trainer);
							}

							NeuralNetMonitorView nnMonitorView = new NeuralNetMonitorView(
									nn.getListSSE());
							nnMonitorView.addNeuralNetCallbackPlotter(
									((NeuralNetCallbackPlotter)nn.getCallbackPlotter()));

							if (_appW != null) {
								_appW.dispose();
							}

							_appW = new AppWindow("Monitor View",
									nnMonitorView);
							_appW.addAppWindowListener(new CloseViewMonitor());

							nn.train(_nnExpView.getRunMode());
							
							_nnCmdBarView.setBtnRunExpEnable(true);
							_nnCmdBarView.setBtnStopExpEnable(false);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
				
				_trainThread.start();
			}
		}
	}

	public class StopExpListener implements ActionListener {
		@SuppressWarnings("deprecation")
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				if (_nnExpView.getRunMode().equals(NeuralNet.RunMode.Distributed)) {
					String[] cmd = {"./mrts_rstop.sh", "hduser@hadoop-e"};
					Runtime.getRuntime().exec(cmd);
					_trainThread.stop();
				}
				else {
					_trainThread.stop();
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			_nnCmdBarView.setBtnRunExpEnable(true);
			_nnCmdBarView.setBtnStopExpEnable(false);
		}
	}

	public class ViewMonitorListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			_appW.setVisible(true);
			_nnCmdBarView.setBtnViewMonitorEnable(false);
		} 
	}

	/**
	 * Hook class - window listener (on window close)
	 * @author cbarca
	 */
	public class CloseViewMonitor implements WindowListener {

		@Override
		public void windowActivated(WindowEvent e) { }

		@Override
		public void windowClosed(WindowEvent e) { }

		@Override
		public void windowClosing(WindowEvent e) {
			_nnCmdBarView.setBtnViewMonitorEnable(true);			
		}

		@Override
		public void windowDeactivated(WindowEvent e) { }

		@Override
		public void windowDeiconified(WindowEvent e) { }

		@Override
		public void windowIconified(WindowEvent e) { }

		@Override
		public void windowOpened(WindowEvent e) { }
	}
}
