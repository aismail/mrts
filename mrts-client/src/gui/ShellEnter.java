package gui;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

import gui.interfaces.IShellEnter;

/**
 * Shell Enter class - enterTo your custom created shell
 * 
 * @author cbarca
 */
public class ShellEnter extends JPanel implements IShellEnter {
	private static final long serialVersionUID = -8651792070967914582L;
	private JButton _enterBtn;
	private Dimension _shellDimension;

	public ShellEnter(String name, Dimension shellDimension) {
		this.setLayout(new GridLayout(0, 1, 0, 0));
		_enterBtn = new JButton(name);
		_shellDimension = shellDimension;
		this.add(_enterBtn);
	}

	@Override
	public JButton getEnterButton() {
		return _enterBtn;
	}
	
	@Override
	public JPanel getEnterPanel() {
		return this;
	}

	@Override
	public Dimension getShellDimension() {
		return _shellDimension;
	}
}
