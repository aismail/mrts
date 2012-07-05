package gui;

import gui.controller.SelectorController;
import gui.view.SelectorView;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import javax.swing.JFrame;

/**
 * Bootstrapper class - bootstraps the GUI engine (singleton class)
 * (here is the place you may add a 'link' to your custom created 
 * module by placing a ShellEnter inside selector's view at the 
 * BootSelector init method level)
 * 
 * @author cbarca
 */
public class Bootstrapper {
	public static final Dimension DEFAULT_DIMENSION = new Dimension(300, 300);
	private JFrame _bootFrame;
	private static Bootstrapper _bootstrapper = null;
	
	public static Bootstrapper getInstance() {
		if (_bootstrapper == null) {
			_bootstrapper = new Bootstrapper();
		}
		
		return _bootstrapper;
	}
	
	/**
	 * Launch the application.
	 */
	public static void main(final String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Bootstrapper window;
					window = Bootstrapper.getInstance();
					window._bootFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Create the application.
	 */
	private Bootstrapper() {
		initialize();
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		_bootFrame = new JFrame("MassML");   
		_bootFrame.setSize(DEFAULT_DIMENSION);
		_bootFrame.setMinimumSize(DEFAULT_DIMENSION);

		_bootFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);         
		
		this.BootSelector();
	}

	/**
	 * Selector page init method
	 */
	public void BootSelector() {
		// Set selector view
		SelectorView selView = new SelectorView();
		
		// Adding modules' enter to selector view
		selView.addShellEnter(new ShellEnter("Neural Network", 
			NeuralNetShell.DEFAULT_DIMENSION), 
			new NeuralNetShell());
		selView.addShellEnter(new ShellEnter("X", 
			NeuralNetShell.DEFAULT_DIMENSION), 
			new NeuralNetShell());
		selView.addShellEnter(new ShellEnter("Y", 
			NeuralNetShell.DEFAULT_DIMENSION), 
			new NeuralNetShell());
		selView.addShellEnter(new ShellEnter("Z", 
			NeuralNetShell.DEFAULT_DIMENSION), 
			new NeuralNetShell());
		
		// Set selector controller
		@SuppressWarnings("unused")
		SelectorController selController = new SelectorController(selView);
		
		// Init bootFrame with SelectorView
		_bootFrame.setContentPane(selView);
		_bootFrame.validate();
	}

	/**
	 * Change content pane 
	 * @param cont container
	 */
	public void BootTo(Container cont) {
		_bootFrame.setContentPane(cont);
		_bootFrame.pack();
		_bootFrame.validate();
	}
	
	/**
	 * Change content pane 
	 * @param cont container
	 * @param dim dimension
	 */
	public void BootTo(Container cont, Dimension dim) {
		_bootFrame.setSize(dim);
		_bootFrame.setMinimumSize(dim);
		_bootFrame.setContentPane(cont);
		_bootFrame.pack();
		_bootFrame.validate();
	}
}



