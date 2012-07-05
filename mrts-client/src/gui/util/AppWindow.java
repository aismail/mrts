package gui.util;

import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.ui.RefineryUtilities;

/**
 * Application window class - creates a separate/independently JFrame window
 * 
 * @author cbarca
 */
public class AppWindow extends JFrame {
	private static final long serialVersionUID = 4332890032229738005L;

	public AppWindow(String title, JPanel panel) {
		super(title);
		this.setContentPane(panel);
		this.pack();
		RefineryUtilities.centerFrameOnScreen(this);
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		this.setVisible(true);
	}
	
	public void addAppWindowListener(WindowListener windowListener) {
		this.addWindowListener(windowListener);
	}
}
