package gui.interfaces;

import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Shell enter interface - enterTo custom created shell
 * 
 * @author cbarca
 */
public interface IShellEnter {
	public JButton getEnterButton();
	public JPanel getEnterPanel();
	public Dimension getShellDimension();
}
