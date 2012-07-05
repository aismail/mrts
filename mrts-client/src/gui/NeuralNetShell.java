package gui;

import gui.controller.NeuralNetCmdBarController;
import gui.controller.NeuralNetExpController;
import gui.mediator.NeuralNetMediator;
import gui.view.NeuralNetCmdBarView;
import gui.view.NeuralNetExpView;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

/**
 * Neural network GUI Shell class - contains regions for 
 * neural network components' insertion 
 * 
 * @author cbarca
 */
public class NeuralNetShell extends JPanel{
	/* Public members
	 */
	
	// Shell default dimension 
	public static final Dimension DEFAULT_DIMENSION = new Dimension(600, 350);
	 
	/* Private members
	 */
	
	// Eclipse generated
	private static final long serialVersionUID = 4004647836541111517L;

	// Regions panels
	private JPanel _topCenter, _bottomCenter;

	// Mediator
	private NeuralNetMediator _mediator;
	
	// Neural Net Experiment
	private NeuralNetExpView _nnExpView;
	@SuppressWarnings("unused")
	private NeuralNetExpController _nnExpController;
	
	// Neural Net CmdBar
	private NeuralNetCmdBarView _nnCmdBarView;
	@SuppressWarnings("unused")
	private NeuralNetCmdBarController _nnCmdBarController;

	public NeuralNetShell() {
		setLayout(new BorderLayout(0, 0));
		
		// Init regions
		this.initTopRegion();
		this.initBottomRegion();
		
		// Init data-model
		this.initMediator();
		
		// Init components
		this.initNeuralNetExpView();
		this.initNeuralNetCmdBarView();
	}
	
	private void initTopRegion() {
		_topCenter = new JPanel();
		FlowLayout flowLayout = (FlowLayout) _topCenter.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		add(_topCenter, BorderLayout.CENTER);
	}

	private void initBottomRegion() {
		_bottomCenter = new JPanel();
		add(_bottomCenter, BorderLayout.SOUTH);
	}
	
	private void initMediator() {
		_mediator = NeuralNetMediator.getInstance();
	}
	
	private void initNeuralNetExpView() {
		_nnExpView = new NeuralNetExpView(_mediator);
		_nnExpController = new NeuralNetExpController(_mediator, _nnExpView);
		_topCenter.add(_nnExpView);
	}
	
	private void initNeuralNetCmdBarView() {
		_nnCmdBarView = new NeuralNetCmdBarView();
		_nnCmdBarController = new NeuralNetCmdBarController(_mediator, 
				_nnCmdBarView, _nnExpView);
		_bottomCenter.add(_nnCmdBarView);
	}
}
