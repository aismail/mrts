package gui.view;

import javax.swing.JPanel;

import fnn.util.RunParams;
import fnn.util.RunParams.InputLocation;
import fnn.util.RunParams.OutputFormat;
import gui.interfaces.ITextFieldValidator;
import gui.mediator.NeuralNetMediator;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JComboBox;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JScrollPane;

import neuralnet.NeuralNet;
import neuralnet.NeuralNet.RunMode;

import org.apache.commons.lang.math.NumberUtils;
import org.jfree.ui.tabbedui.VerticalLayout;

/**
 * Neural net experiment view class - view for experiments' form 
 * (parameters insertion, building the RunParams object for running
 * the network training wherever the user wants - local/distributed) 
 * 
 * @author cbarca
 */
public class NeuralNetExpView extends JPanel {
	/*Private members
	 */
	
	// Eclipse generated
	private static final long serialVersionUID = 3619008444748389050L;
	
	// Run params reference
	private RunParams _runParams;
	
	// Swing stuff
	private JPanel _centerLeftPanel, _centerRightPanel, _topPanel, 
		_bottomPanel, _scrollPanel, _scrollCmdBar,
		_middleLayers;
	private JLabel _lblExpName, _lblNetName, _lblInputLocation, 
		_lblInputPath, _lblOutputFormat, _lblTrainParams, 
		_lblMaxEpochs, _lblRunParamaters, _lblStructure, 
		_lblSSE,  _lblDefSSE, _lblThreshold, 
		_lblMode, _lblInputLayer, _lblMiddleLayers, 
		_lblOutputLayer, _lblOutputPath;
	private JTextField _txtInputPath, _txtMaxEpochs, _txtSSE, 
		_txtThreshold, _txtInputNeurons, _txtOutputNeurons, 
		_txtNetName, _txtExpName, _txtOutputPath;
	private JComboBox _cmbBoxInputLocation, _cmbBoxOutputFormat, _cmbBoxMode;
	private JScrollPane _scrollMiddleLayers;
	private JButton _btnAddMiddleLayer, _btnDeleteMiddleLayer;
	private List<JTextField> _txtMiddleLayers;

	// Validator
	private List<ITextFieldValidator> _validators;
	
	public NeuralNetExpView(NeuralNetMediator nnMediator) {
		setLayout(new BorderLayout(20, 10));
		
		_runParams = nnMediator.getRunParams();
		_txtMiddleLayers = new ArrayList<JTextField>();
		_validators = new ArrayList<ITextFieldValidator>();
		
		this.initPanels();
		this.pullParams();
		this.initValidators();
	}
	
	public void pullParams() {
		this.setExperimentName(_runParams.getExperimentName());
		this.setInputPath(_runParams.getInputPath());
		this.setOutputPath(_runParams.getOutputPath());
		this.setMaxEpochs(((Integer)_runParams.getNetStruct().getMaxEpochs()).toString());
		this.setSSE(((Double)_runParams.getNetStruct().getError()).toString());
		this.setThreshold(((Double)_runParams.getNetStruct().getThreshold()).toString());
		this.setNetworkName(_runParams.getNetworkName());
		this.setInputLayer(((Integer)_runParams.getNetStruct().getInputPop()).toString());
		this.setOutputLayer(((Integer)_runParams.getNetStruct().getOutputPop()).toString());
		this.setInputLocation(_runParams.getInputLocation());
		this.setOutputFormat(_runParams.getOutputFormat());
				
		this.pullMiddleLayers();
		
		this.validate();
	}

	public void pushParams() {
		_runParams.setExperimentName(this.getExperimentName());
		_runParams.setInputLocation(this.getInputLoacation());
		_runParams.setInputPath(this.getInputPath());
		_runParams.setNetworkName(this.getNetworkName());
		_runParams.setOutputFormat(this.getOutputFormat());
		_runParams.setOutputPath(this.getOutputPath());
		_runParams.getNetStruct().setError(this.getSSE());
		_runParams.getNetStruct().setInputPop(this.getInputPop());
		_runParams.getNetStruct().setMaxEpochs(this.getMaxEpochs());
		_runParams.getNetStruct().setOutputPop(this.getOutputPop());
		_runParams.getNetStruct().setThreshold(this.getThreshold());
		
		this.pushMiddleLayers();
	}
	
	private void initValidators() {
		_validators.add(new TextFieldNameValidator(_txtExpName, 
				"Use just letters (a..zA..Z) for the experiment name!"));
		_validators.add(new TextFieldInputPathValidator(_txtInputPath, _cmbBoxInputLocation, 
				"Wrong input path!"));
		_validators.add(new TextFieldOutputPathValidator(_txtOutputPath, 
				"Wrong output path!"));
		_validators.add(new TextFieldNumberValidator(_txtMaxEpochs, 
				"Invalid epochs number!"));
		_validators.add(new TextFieldNumberValidator(_txtSSE, 
				"Wrong SSE number!"));
		_validators.add(new TextFieldNumberValidator(_txtThreshold, 
				"Wrong threshold number!"));
		_validators.add(new TextFieldNameValidator(_txtNetName, 
				"Use just letters (a..zA..Z) for the network name!"));
		_validators.add(new TextFieldNumberValidator(_txtInputNeurons, 
				"Wrong input neurons number!"));
		_validators.add(new TextFieldNumberValidator(_txtOutputNeurons, 
				"Wrong output neurons number!"));
	}
		
	private void initPanels() {
		this.initTopPanel();
		this.initCenterLeftPanel();
		this.initCenterRightPanel();
		this.initBottomPanel();
	}
	
	private void initTopPanel() {
		_topPanel = new JPanel();
		FlowLayout fl__topPanel = (FlowLayout) _topPanel.getLayout();
		fl__topPanel.setHgap(0);
		fl__topPanel.setVgap(0);
		fl__topPanel.setAlignment(FlowLayout.LEFT);
		add(_topPanel, BorderLayout.NORTH);
		
		_lblRunParamaters = new JLabel("Neural Network Training - Run Parameters");
		_lblRunParamaters.setVerticalAlignment(SwingConstants.TOP);
		_topPanel.add(_lblRunParamaters);
	}
	
	private void initBottomPanel() {
		_bottomPanel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) _bottomPanel.getLayout();
		flowLayout.setVgap(0);
		flowLayout.setHgap(0);
		flowLayout.setAlignment(FlowLayout.LEFT);
		add(_bottomPanel, BorderLayout.SOUTH);
	
		_lblDefSSE = new JLabel("SSE* = Sum of Squared Error");
		_lblDefSSE.setHorizontalAlignment(SwingConstants.LEFT);
		_lblDefSSE.setVerticalAlignment(SwingConstants.TOP);
		_bottomPanel.add(_lblDefSSE);
	}
	
	private void initCenterLeftPanel() {	
		_centerLeftPanel = new JPanel();
		add(_centerLeftPanel, BorderLayout.WEST);
		GridBagLayout gbl__centerLeftPanel = new GridBagLayout();
		gbl__centerLeftPanel.columnWidths = new int[]{0, 0};
		gbl__centerLeftPanel.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl__centerLeftPanel.columnWeights = new double[]{1.0, 1.0};
		gbl__centerLeftPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		_centerLeftPanel.setLayout(gbl__centerLeftPanel);
		
		_lblExpName = new JLabel("Experiment");
		GridBagConstraints gbc__lblExpName = new GridBagConstraints();
		gbc__lblExpName.fill = GridBagConstraints.VERTICAL;
		gbc__lblExpName.insets = new Insets(0, 0, 5, 5);
		gbc__lblExpName.anchor = GridBagConstraints.WEST;
		gbc__lblExpName.gridx = 0;
		gbc__lblExpName.gridy = 0;
		_centerLeftPanel.add(_lblExpName, gbc__lblExpName);
		
		_txtExpName = new JTextField();
		_txtExpName.setText("<name>");
		_txtExpName.setColumns(15);
		GridBagConstraints gbc__txtExpName = new GridBagConstraints();
		gbc__txtExpName.fill = GridBagConstraints.VERTICAL;
		gbc__txtExpName.anchor = GridBagConstraints.WEST;
		gbc__txtExpName.insets = new Insets(0, 0, 5, 0);
		gbc__txtExpName.gridx = 1;
		gbc__txtExpName.gridy = 0;
		_centerLeftPanel.add(_txtExpName, gbc__txtExpName);
		
		_lblInputLocation = new JLabel("Input Location");
		GridBagConstraints gbc__lblInputLocation = new GridBagConstraints();
		gbc__lblInputLocation.anchor = GridBagConstraints.WEST;
		gbc__lblInputLocation.insets = new Insets(0, 0, 5, 5);
		gbc__lblInputLocation.gridx = 0;
		gbc__lblInputLocation.gridy = 1;
		_centerLeftPanel.add(_lblInputLocation, gbc__lblInputLocation);
		
		_cmbBoxInputLocation = new JComboBox(RunParams.InputLocation.values());
		GridBagConstraints gbc__cmbBoxInputLocation = new GridBagConstraints();
		gbc__cmbBoxInputLocation.insets = new Insets(0, 0, 5, 0);
		gbc__cmbBoxInputLocation.fill = GridBagConstraints.HORIZONTAL;
		gbc__cmbBoxInputLocation.gridx = 1;
		gbc__cmbBoxInputLocation.gridy = 1;
		_centerLeftPanel.add(_cmbBoxInputLocation, gbc__cmbBoxInputLocation);
		
		_lblInputPath = new JLabel("Input Path");
		GridBagConstraints gbc__lblInputPath = new GridBagConstraints();
		gbc__lblInputPath.anchor = GridBagConstraints.WEST;
		gbc__lblInputPath.insets = new Insets(0, 0, 5, 5);
		gbc__lblInputPath.gridx = 0;
		gbc__lblInputPath.gridy = 2;
		_centerLeftPanel.add(_lblInputPath, gbc__lblInputPath);
		
		_txtInputPath = new JTextField();
		_txtInputPath.setText("<path>");
		GridBagConstraints gbc__txtInputPath = new GridBagConstraints();
		gbc__txtInputPath.insets = new Insets(0, 0, 5, 0);
		gbc__txtInputPath.anchor = GridBagConstraints.WEST;
		gbc__txtInputPath.gridx = 1;
		gbc__txtInputPath.gridy = 2;
		_centerLeftPanel.add(_txtInputPath, gbc__txtInputPath);
		_txtInputPath.setColumns(15);
		
		_lblOutputFormat = new JLabel("Output Format");
		GridBagConstraints gbc__lblOutputFormat = new GridBagConstraints();
		gbc__lblOutputFormat.anchor = GridBagConstraints.EAST;
		gbc__lblOutputFormat.insets = new Insets(0, 0, 5, 5);
		gbc__lblOutputFormat.gridx = 0;
		gbc__lblOutputFormat.gridy = 3;
		_centerLeftPanel.add(_lblOutputFormat, gbc__lblOutputFormat);
		
		_cmbBoxOutputFormat = new JComboBox(RunParams.OutputFormat.values());
		GridBagConstraints gbc__cmbBoxOutputFormat = new GridBagConstraints();
		gbc__cmbBoxOutputFormat.insets = new Insets(0, 0, 5, 0);
		gbc__cmbBoxOutputFormat.fill = GridBagConstraints.HORIZONTAL;
		gbc__cmbBoxOutputFormat.gridx = 1;
		gbc__cmbBoxOutputFormat.gridy = 3;
		_centerLeftPanel.add(_cmbBoxOutputFormat, gbc__cmbBoxOutputFormat);
		
		_lblTrainParams = new JLabel("Train:");
		GridBagConstraints gbc__lblTrainParams = new GridBagConstraints();
		gbc__lblTrainParams.anchor = GridBagConstraints.WEST;
		gbc__lblTrainParams.insets = new Insets(0, 0, 5, 5);
		gbc__lblTrainParams.gridx = 0;
		gbc__lblTrainParams.gridy = 5;
		_centerLeftPanel.add(_lblTrainParams, gbc__lblTrainParams);
		
		_lblMaxEpochs = new JLabel("Max Epochs");
		GridBagConstraints gbc__lblMaxEpochs = new GridBagConstraints();
		gbc__lblMaxEpochs.anchor = GridBagConstraints.WEST;
		gbc__lblMaxEpochs.insets = new Insets(0, 0, 5, 5);
		gbc__lblMaxEpochs.gridx = 0;
		gbc__lblMaxEpochs.gridy = 6;
		_centerLeftPanel.add(_lblMaxEpochs, gbc__lblMaxEpochs);
		
		_txtMaxEpochs = new JTextField();
		_txtMaxEpochs.setText("<epochs>");
		GridBagConstraints gbc__txtMaxEpochs = new GridBagConstraints();
		gbc__txtMaxEpochs.insets = new Insets(0, 0, 5, 0);
		gbc__txtMaxEpochs.anchor = GridBagConstraints.WEST;
		gbc__txtMaxEpochs.gridx = 1;
		gbc__txtMaxEpochs.gridy = 6;
		_centerLeftPanel.add(_txtMaxEpochs, gbc__txtMaxEpochs);
		_txtMaxEpochs.setColumns(15);
		
		_lblSSE = new JLabel("SSE*");
		GridBagConstraints gbc__lblMse = new GridBagConstraints();
		gbc__lblMse.anchor = GridBagConstraints.WEST;
		gbc__lblMse.insets = new Insets(0, 0, 5, 5);
		gbc__lblMse.gridx = 0;
		gbc__lblMse.gridy = 7;
		_centerLeftPanel.add(_lblSSE, gbc__lblMse);
		
		_txtSSE = new JTextField();
		_txtSSE.setText("<mse>");
		GridBagConstraints gbc__txtMSE = new GridBagConstraints();
		gbc__txtMSE.anchor = GridBagConstraints.WEST;
		gbc__txtMSE.insets = new Insets(0, 0, 5, 0);
		gbc__txtMSE.gridx = 1;
		gbc__txtMSE.gridy = 7;
		_centerLeftPanel.add(_txtSSE, gbc__txtMSE);
		_txtSSE.setColumns(15);
		
		_lblThreshold = new JLabel("Threshold");
		GridBagConstraints gbc__lblThreshold = new GridBagConstraints();
		gbc__lblThreshold.anchor = GridBagConstraints.WEST;
		gbc__lblThreshold.insets = new Insets(0, 0, 5, 5);
		gbc__lblThreshold.gridx = 0;
		gbc__lblThreshold.gridy = 8;
		_centerLeftPanel.add(_lblThreshold, gbc__lblThreshold);
		
		_txtThreshold = new JTextField();
		_txtThreshold.setText("<threshold>");
		GridBagConstraints gbc__txtThreshold = new GridBagConstraints();
		gbc__txtThreshold.insets = new Insets(0, 0, 5, 0);
		gbc__txtThreshold.anchor = GridBagConstraints.WEST;
		gbc__txtThreshold.gridx = 1;
		gbc__txtThreshold.gridy = 8;
		_centerLeftPanel.add(_txtThreshold, gbc__txtThreshold);
		_txtThreshold.setColumns(15);
		
		_lblMode = new JLabel("Run Mode");
		GridBagConstraints gbc__lblMode = new GridBagConstraints();
		gbc__lblMode.anchor = GridBagConstraints.WEST;
		gbc__lblMode.insets = new Insets(0, 0, 5, 5);
		gbc__lblMode.gridx = 0;
		gbc__lblMode.gridy = 9;
		_centerLeftPanel.add(_lblMode, gbc__lblMode);
		
		_cmbBoxMode = new JComboBox(NeuralNet.RunMode.values());
		GridBagConstraints gbc__cmbBoxMode = new GridBagConstraints();
		gbc__cmbBoxMode.insets = new Insets(0, 0, 5, 0);
		gbc__cmbBoxMode.fill = GridBagConstraints.HORIZONTAL;
		gbc__cmbBoxMode.gridx = 1;
		gbc__cmbBoxMode.gridy = 9;
		_centerLeftPanel.add(_cmbBoxMode, gbc__cmbBoxMode);
		
		_lblOutputPath = new JLabel("Output Path");
		GridBagConstraints gbc__lblOutputPath = new GridBagConstraints();
		gbc__lblOutputPath.anchor = GridBagConstraints.WEST;
		gbc__lblOutputPath.insets = new Insets(0, 0, 0, 5);
		gbc__lblOutputPath.gridx = 0;
		gbc__lblOutputPath.gridy = 4;
		_centerLeftPanel.add(_lblOutputPath, gbc__lblOutputPath);
		
		_txtOutputPath = new JTextField();
		_txtOutputPath.setText("<path>");
		GridBagConstraints gbc__txtOutputPath = new GridBagConstraints();
		gbc__txtOutputPath.fill = GridBagConstraints.HORIZONTAL;
		gbc__txtOutputPath.gridx = 1;
		gbc__txtOutputPath.gridy = 4;
		_centerLeftPanel.add(_txtOutputPath, gbc__txtOutputPath);
		_txtOutputPath.setColumns(10);
	}
	
	private void initCenterRightPanel() {
		_centerRightPanel = new JPanel();
		add(_centerRightPanel, BorderLayout.CENTER);
		GridBagLayout gbl__centerRightPanel = new GridBagLayout();
		gbl__centerRightPanel.columnWidths = new int[]{0, 0, 0};
		gbl__centerRightPanel.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
		gbl__centerRightPanel.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl__centerRightPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		_centerRightPanel.setLayout(gbl__centerRightPanel);
		
		_lblNetName = new JLabel("Network");
		_lblNetName.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc__lblNetName = new GridBagConstraints();
		gbc__lblNetName.fill = GridBagConstraints.VERTICAL;
		gbc__lblNetName.anchor = GridBagConstraints.WEST;
		gbc__lblNetName.insets = new Insets(0, 0, 5, 5);
		gbc__lblNetName.gridx = 0;
		gbc__lblNetName.gridy = 0;
		_centerRightPanel.add(_lblNetName, gbc__lblNetName);
		
		_txtNetName = new JTextField();
		_txtNetName.setText("<name>");
		_txtNetName.setColumns(15);
		GridBagConstraints gbc__txtNetName = new GridBagConstraints();
		gbc__txtNetName.insets = new Insets(0, 0, 5, 0);
		gbc__txtNetName.anchor = GridBagConstraints.NORTHWEST;
		gbc__txtNetName.gridx = 1;
		gbc__txtNetName.gridy = 0;
		_centerRightPanel.add(_txtNetName, gbc__txtNetName);
		
		_lblStructure = new JLabel("Structure:");
		GridBagConstraints gbc_lblStructure = new GridBagConstraints();
		gbc_lblStructure.anchor = GridBagConstraints.WEST;
		gbc_lblStructure.insets = new Insets(0, 0, 5, 5);
		gbc_lblStructure.gridx = 0;
		gbc_lblStructure.gridy = 1;
		_centerRightPanel.add(_lblStructure, gbc_lblStructure);
	
		_lblInputLayer = new JLabel("Input Layer");
		GridBagConstraints gbc__lblInputLayer = new GridBagConstraints();
		gbc__lblInputLayer.anchor = GridBagConstraints.WEST;
		gbc__lblInputLayer.insets = new Insets(0, 0, 5, 5);
		gbc__lblInputLayer.gridx = 0;
		gbc__lblInputLayer.gridy = 2;
		_centerRightPanel.add(_lblInputLayer, gbc__lblInputLayer);
		
		_txtInputNeurons = new JTextField();
		_txtInputNeurons.setText("<#neurons>");
		GridBagConstraints gbc__txtInputNeurons = new GridBagConstraints();
		gbc__txtInputNeurons.anchor = GridBagConstraints.WEST;
		gbc__txtInputNeurons.insets = new Insets(0, 0, 5, 0);
		gbc__txtInputNeurons.gridx = 1;
		gbc__txtInputNeurons.gridy = 2;
		_centerRightPanel.add(_txtInputNeurons, gbc__txtInputNeurons);
		_txtInputNeurons.setColumns(15);
		
		_lblMiddleLayers = new JLabel("Middle Layers");
		GridBagConstraints gbc__lblMiddleLayers = new GridBagConstraints();
		gbc__lblMiddleLayers.anchor = GridBagConstraints.NORTHWEST;
		gbc__lblMiddleLayers.insets = new Insets(0, 0, 5, 5);
		gbc__lblMiddleLayers.gridx = 0;
		gbc__lblMiddleLayers.gridy = 3;
		_centerRightPanel.add(_lblMiddleLayers, gbc__lblMiddleLayers);
		
		// Scroll panel
		_scrollPanel = new JPanel();
		GridBagConstraints gbc__scrollPanel = new GridBagConstraints();
		gbc__scrollPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc__scrollPanel.anchor = GridBagConstraints.NORTH;
		gbc__scrollPanel.insets = new Insets(0, 0, 5, 0);
		gbc__scrollPanel.gridx = 1;
		gbc__scrollPanel.gridy = 3;
		_centerRightPanel.add(_scrollPanel, gbc__scrollPanel);
		_scrollPanel.setLayout(new BorderLayout(0, 0));
		
		_scrollCmdBar = new JPanel();
		FlowLayout fl__scrollCmdBar = (FlowLayout) _scrollCmdBar.getLayout();
		fl__scrollCmdBar.setVgap(2);
		fl__scrollCmdBar.setHgap(2);
		_scrollPanel.add(_scrollCmdBar, BorderLayout.SOUTH);
		
		_btnAddMiddleLayer = new JButton("+");
		_scrollCmdBar.add(_btnAddMiddleLayer);
		_btnAddMiddleLayer.setVerticalAlignment(SwingConstants.TOP);
		
		_btnDeleteMiddleLayer = new JButton("-");
		_scrollCmdBar.add(_btnDeleteMiddleLayer);
		_btnDeleteMiddleLayer.setVerticalAlignment(SwingConstants.TOP);
						
		_middleLayers = new JPanel(); 
		_middleLayers.setLayout(new VerticalLayout());
						
		_scrollMiddleLayers = new JScrollPane(_middleLayers);	
		_scrollMiddleLayers.setPreferredSize(new Dimension(100, 120));
		_scrollPanel.add(_scrollMiddleLayers, BorderLayout.NORTH);
		
		_lblOutputLayer = new JLabel("Output Layer");
		GridBagConstraints gbc__lblOutputLayer = new GridBagConstraints();
		gbc__lblOutputLayer.anchor = GridBagConstraints.WEST;
		gbc__lblOutputLayer.insets = new Insets(0, 0, 0, 5);
		gbc__lblOutputLayer.gridx = 0;
		gbc__lblOutputLayer.gridy = 4;
		_centerRightPanel.add(_lblOutputLayer, gbc__lblOutputLayer);
		
		_txtOutputNeurons = new JTextField();
		_txtOutputNeurons.setText("<#neurons>");
		GridBagConstraints gbc__txtOutputNeurons = new GridBagConstraints();
		gbc__txtOutputNeurons.anchor = GridBagConstraints.WEST;
		gbc__txtOutputNeurons.gridx = 1;
		gbc__txtOutputNeurons.gridy = 4;
		_centerRightPanel.add(_txtOutputNeurons, gbc__txtOutputNeurons);
		_txtOutputNeurons.setColumns(15);
	}
	
	public void setExperimentName(String expName) {
		_txtExpName.setText(expName);
	}
	
	public String getExperimentName() {
		return _txtExpName.getText();
	}
	
	public void setInputPath(String inputPath) {
		_txtInputPath.setText(inputPath);
	}
	
	public String getInputPath() {
		return _txtInputPath.getText();
	}
	
	public void setOutputPath(String outputPath) {
		_txtOutputPath.setText(outputPath);
	}
	
	public String getOutputPath() {
		return _txtOutputPath.getText();
	}
	
	public void setMaxEpochs(String maxEpochs) {
		_txtMaxEpochs.setText(maxEpochs);
	}
	
	public int getMaxEpochs() {
		return Integer.parseInt(_txtMaxEpochs.getText());
	}
	
	public void setSSE(String mse) {
		_txtSSE.setText(mse);
	}
	
	public double getSSE() {
		return Double.parseDouble(_txtSSE.getText());
	}
	
	public void setThreshold(String th) {
		_txtThreshold.setText(th);
	}
	
	public double getThreshold() {
		return Double.parseDouble(_txtThreshold.getText());
	}
	
	public void setNetworkName(String netName) {
		_txtNetName.setText(netName);
	}
	
	public String getNetworkName() {
		return _txtNetName.getText();
	}
	
	public void setInputLayer(String inputNeurons) {
		_txtInputNeurons.setText(inputNeurons);
	}
	
	public int getInputPop() {
		return Integer.parseInt(_txtInputNeurons.getText());
	}
	
	public void pullMiddleLayers() {
		_txtMiddleLayers.clear();
		_middleLayers.removeAll();
	
		for (Integer num : _runParams.getNetStruct().getMiddlezPop()) {
			JTextField txtf = new JTextField(num.toString());
			_txtMiddleLayers.add(txtf);
			_validators.add(new TextFieldNumberValidator(txtf, 
					"Wrong middle neurons numbers!"));
			_middleLayers.add(txtf);
		}
		
		_middleLayers.validate();
		_middleLayers.repaint();
		this.validate();
	}
		
	public void pushMiddleLayers() {
		for (int i = 0; i < _txtMiddleLayers.size(); i++) {
			_runParams.getNetStruct().getMiddlezPop().set(i, 
					Integer.parseInt(_txtMiddleLayers.get(i).getText()));
		}
	}
	
	public void updateMiddleLayers() {
		int sz = _runParams.getNetStruct().getMiddlezPop().size();
				
		if (sz > _txtMiddleLayers.size()) {
			this.incMiddleLayers(_runParams.getNetStruct().getMiddlezPop()); 
		}
		else {
			this.decMiddleLayers(_runParams.getNetStruct().getMiddlezPop());
		}
		
		this.validate();
	}
	
	public void incMiddleLayers(List<Integer> middleNeurons) {
		int sz = middleNeurons.size();
		
		JTextField txtf = new JTextField(middleNeurons.get(sz - 1).toString());
		_txtMiddleLayers.add(txtf);
		_validators.add(new TextFieldNumberValidator(txtf, 
			"Wrong middle neurons numbers!"));
		_middleLayers.add(txtf);
		
		_middleLayers.validate();
		_middleLayers.repaint();
	}
	
	public void decMiddleLayers(List<Integer> middleNeurons) {
		int sz = middleNeurons.size();
		
		JTextField txtf = _txtMiddleLayers.get(sz);
		_txtMiddleLayers.remove(sz);
		_validators.remove(sz);
		_middleLayers.remove(txtf);
		
		_middleLayers.validate();
		_middleLayers.repaint();
	}
	
	public void setOutputLayer(String outputNeurons) {
		_txtOutputNeurons.setText(outputNeurons);
	}
	
	public int getOutputPop() {
		return Integer.parseInt(_txtOutputNeurons.getText());
	}
	
	public void setInputLocation(InputLocation inputLocation) {
		_cmbBoxInputLocation.setSelectedItem(inputLocation);
	}
	
	public InputLocation getInputLoacation() {
		return (InputLocation)_cmbBoxInputLocation.getSelectedItem();
	}
	
	public void setOutputFormat(OutputFormat outputFormat) {
		_cmbBoxOutputFormat.setSelectedItem(outputFormat);
	}
	
	public OutputFormat getOutputFormat() {
		return (OutputFormat)_cmbBoxOutputFormat.getSelectedItem();
	}
	
	public void setRunMmode(RunMode runMode) {
		_cmbBoxMode.setSelectedItem(runMode);
	}
	
	public RunMode getRunMode() {
		return (RunMode)_cmbBoxMode.getSelectedItem();
	}
	
	public void addBtnAddMiddleLayerListener(ActionListener actionListener) {
		_btnAddMiddleLayer.addActionListener(actionListener);
	}
	
	public void addBtnDeleteMiddleLayerListener(ActionListener actionListener) {
		_btnDeleteMiddleLayer.addActionListener(actionListener);
	}
	
	public List<ITextFieldValidator> getValidators() {
		return _validators;
	}
	
	// JTextField validator classes
	
	/**
	 * Number validator
	 * 
	 * @author cbarca
	 */
	public class TextFieldNumberValidator implements ITextFieldValidator {
		private JTextField _textf;
		private String _errMsg;
		
		public TextFieldNumberValidator(JTextField textf, String errMsg) {
			_textf = textf;
			_errMsg = errMsg;
		}
		
		@Override
		public boolean validate() {
			if (NumberUtils.isNumber(_textf.getText())) {
				return true;
			}
			
			return false;
		}

		@Override
		public String getErrorMessage() {
			return _errMsg;
		}
	}
	
	/**
	 * Name validator
	 * 
	 * @author cbarca
	 */
	public class TextFieldNameValidator implements ITextFieldValidator {
		private JTextField _textf;
		private String _errMsg;

		public TextFieldNameValidator(JTextField textf, String errMsg) {
			_textf = textf;
			_errMsg = errMsg;
		}
		
		@Override
		public boolean validate() {
			if (!_textf.getText().contains(" ")) {
				return true;
			}
			
			return false;
		}

		@Override
		public String getErrorMessage() {
			return _errMsg;
		}
	}
	
	/**
	 * Path validator (if input location is a local directory)
	 * 
	 * @author cbarca
	 */
	public class TextFieldInputPathValidator implements ITextFieldValidator {
		private JTextField _textf;
		private JComboBox _cmbBox;
		private String _errMsg;

		public TextFieldInputPathValidator(JTextField textf, JComboBox cmbBox, String errMsg) {
			_textf = textf;
			_cmbBox = cmbBox;
			_errMsg = errMsg;
		}
		
		@Override
		public boolean validate() {
			if (_cmbBox.getSelectedItem().equals(RunParams.InputLocation.LocalDir)) {
				File file = new File(_textf.getText());
				
				if (file.exists()) {
					return true;
				}
				
				return false;
			}
			
			return true;
		}

		@Override
		public String getErrorMessage() {
			return _errMsg;
		}
	}
	
	/**
	 * Path validator (for output path)
	 * 
	 * @author cbarca
	 */
	public class TextFieldOutputPathValidator implements ITextFieldValidator {
		private JTextField _textf;
		private String _errMsg;

		public TextFieldOutputPathValidator(JTextField textf, String errMsg) {
			_textf = textf;
			_errMsg = errMsg;
		}
		
		@Override
		public boolean validate() {
			File file = new File(_textf.getText());
				
			if (file.exists()) {
				return true;
			}
				
			return false;
		}

		@Override
		public String getErrorMessage() {
			return _errMsg;
		}
	}
}
