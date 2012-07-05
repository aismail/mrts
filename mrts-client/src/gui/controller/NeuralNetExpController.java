package gui.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import gui.mediator.NeuralNetMediator;
import gui.view.NeuralNetExpView;

/**
 * Controller class for neural network experiment
 * 
 * @author cbarca
 */
public class NeuralNetExpController {
	private NeuralNetExpView _nnExpView;
	
	public NeuralNetExpController(NeuralNetMediator nnMediator, NeuralNetExpView nnExpView) {
		_nnExpView = nnExpView;
		
		this.addListeners();
	}
	
	private void addListeners() {
		_nnExpView.addBtnAddMiddleLayerListener(new AddMiddleLayerListener());
		_nnExpView.addBtnDeleteMiddleLayerListener(new DeleteMiddleLayerListener());
	}
	
	class DeleteMiddleLayerListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			int middPop = NeuralNetMediator.getInstance().getRunParams().
				getNetStruct().getMiddlezPop().size();
			
			if (middPop > 1) {
				NeuralNetMediator.getInstance().getRunParams().
					getNetStruct().getMiddlezPop().remove(middPop - 1);
				
				_nnExpView.updateMiddleLayers();
			}
		}
	}
	
	class AddMiddleLayerListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			NeuralNetMediator.getInstance().getRunParams().
				getNetStruct().addMiddlePop(0);	
			
			_nnExpView.updateMiddleLayers();
		}
	}
}
