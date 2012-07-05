package gui.view;

import javax.swing.JPanel;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import javax.swing.JList;
import javax.swing.JScrollPane;

import gui.dmodel.NeuralNetSSE;
import gui.dmodel.ResponseListModel;
import gui.plotter.NeuralNetCallbackPlotter;

/**
 * Neural net monitor view class - view for errors plot & list
 * 
 * @author cbarca
 */
public class NeuralNetMonitorView extends JPanel {
	public static final Dimension DEFAULT_PLOTTER_DIMENSION = NeuralNetCallbackPlotter.DEFAULT_DIMENSION;
	public static final Dimension DEFAULT_SCROLL_LIST_DIMENSION = 
		new Dimension(200, DEFAULT_PLOTTER_DIMENSION.height);
	
	private static final long serialVersionUID = -4979882438407818073L;
	private JPanel _plotterPanel, _valuesListPanel;
	private JList _valuesList;
	private JScrollPane _listScrollpane;
	private ResponseListModel<NeuralNetSSE> _listSSE;
	
	public NeuralNetMonitorView(ResponseListModel<NeuralNetSSE> listSSE) {
		setLayout(new BorderLayout(0, 0));
		_listSSE = listSSE;
		this.initPanels();
	}

	public void initPanels() {
		_plotterPanel = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) _plotterPanel.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		add(_plotterPanel, BorderLayout.CENTER);
				
		_valuesListPanel = new JPanel();
		FlowLayout fl__vtablePanel = (FlowLayout) _valuesListPanel.getLayout();
		fl__vtablePanel.setAlignment(FlowLayout.LEFT);
		add(_valuesListPanel, BorderLayout.EAST);
		
		_valuesList = new JList(_listSSE);
		_listScrollpane = new JScrollPane(_valuesList);
		_listScrollpane.setPreferredSize(DEFAULT_SCROLL_LIST_DIMENSION);
		_valuesListPanel.add(_listScrollpane);
	}
	
	public void addNeuralNetCallbackPlotter(NeuralNetCallbackPlotter nnCallbackPlotter) {
		_plotterPanel.add(nnCallbackPlotter.getXYSeriesPlotter().getChartPanel());
		_plotterPanel.validate();
		_plotterPanel.repaint();
		this.validate();
	}
}
