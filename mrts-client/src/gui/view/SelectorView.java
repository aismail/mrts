package gui.view;

import gui.interfaces.IShellEnter;

import javax.swing.JPanel;

import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;

/**
 * Selector view class - view for modules' selection, first page
 * (dynamic addition of links to a custom created shell)
 * 
 * @author cbarca
 */
public class SelectorView extends JPanel {
	private static final long serialVersionUID = -4180837172461393492L;
	Map<IShellEnter, JPanel> map = new HashMap<IShellEnter, JPanel>();

	public SelectorView() {
		setLayout(new GridLayout(0, 2, 1, 1));
	}
	
	public void addShellEnter(IShellEnter shellEnter, JPanel shell) {
		this.add(shellEnter.getEnterPanel());
		map.put(shellEnter, shell);
	}
	
	public Map<IShellEnter, JPanel> getEnterMap() {
		return map;
	}
}
