package gui.controller;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map.Entry;

import javax.swing.JPanel;

import gui.Bootstrapper;
import gui.interfaces.IShellEnter;
import gui.view.SelectorView;

/**
 * Controller class for selector (first page - module selection)
 * 
 * @author cbarca
 */
public class SelectorController {
	private SelectorView _selView;
	
	public SelectorController(SelectorView selView) {
		_selView = selView;
		
		this.addListeners();
	}
	
	private void addListeners() {
		for (Entry<IShellEnter, JPanel> entry : _selView.getEnterMap().entrySet()) {
			entry.getKey().getEnterButton().addActionListener(
					new ShellEnterListener(entry.getValue(), 
							entry.getKey().getShellDimension()));
		}
	}
	
	class ShellEnterListener implements ActionListener {
		private JPanel _shell;
		private Dimension _shellDimension;
		
		public ShellEnterListener(JPanel shell, Dimension shellDimension) {
			_shell = shell;
			_shellDimension = shellDimension;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			Bootstrapper.getInstance().BootTo(_shell, _shellDimension);			
		}
	}
}
