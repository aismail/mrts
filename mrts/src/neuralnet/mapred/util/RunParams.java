package neuralnet.mapred.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

import neuralnet.network.NetworkStruct;

public class RunParams {
	// Constants
	public static final String SPARAMS_FILENAME = "short_run.xml";
	public static final String PARAMS_FILENAME = "mrts_run.xml";
	
	// Private members
	private String _exper_name, _input_path, 
		_output_path, _network_name;
	private OutputFormat _output_format;
	private InputLocation _input_location;
	private NetworkStruct _net_struct;
	private SAXBuilder _builder;
	private String _own_filename;
	
	/**
	 * Output format enum
	 * @author cbarca
	 */
	public enum OutputFormat {
		CSV, MATRIX;
	}

	/**
	 * Input location enum
	 * @author cbarca
	 */
	public enum InputLocation {
		HDFS, Cassandra;
	}

	/**
	 * Default constructor
	 */
	public RunParams() {
		_net_struct = new NetworkStruct();
		_builder = new SAXBuilder();
	}
		
	/**
	 * Read parameters from XML file
	 * @param finput buffered reader input 
	 */
	public void readFromXML(BufferedReader finput) {
		try {
			Document document = (Document) _builder.build(finput);
			// Setup root node
			Element rootNode = document.getRootElement();
			
			// Experiment node
			Element experiment = rootNode.getChild("experiment");
			_exper_name = experiment.getAttributeValue("name");
			
			// Input & Output nodes
			Element input_path = experiment.getChild("inputpath"),
					output_path = experiment.getChild("outputpath");
			
			if (input_path != null) {
				_input_path = input_path.getText();
				_input_location = InputLocation.valueOf(input_path.getAttributeValue("location"));
			}
			
			if (output_path != null) {
				_output_path = output_path.getText();
				_output_format = OutputFormat.valueOf(output_path.getAttributeValue("format"));
			}
			
			// Network node
			Element network = experiment.getChild("network");
			_network_name = network.getAttributeValue("name");
			
			List<Element> elems = network.getChildren();
			
			// Parsing network's children
			for (Element elem : elems) {
				if (elem.getName().compareTo("inputpop") == 0) {
					_net_struct.setInputPop(Integer.parseInt(elem.getText()));
				}
				else
				if (elem.getName().compareTo("middlepop") == 0) {
					_net_struct.addMiddlePop(Integer.parseInt(elem.getText()));
				}
				else
				if (elem.getName().compareTo("outputpop") == 0) {
					_net_struct.setOutputPop(Integer.parseInt(elem.getText()));
				}
				else
				if (elem.getName().compareTo("error") == 0) {
					_net_struct.setError(Double.parseDouble(elem.getText()));
				}
				else
				if (elem.getName().compareTo("threshold") == 0) {
					_net_struct.setThreshold(Double.parseDouble(elem.getText()));
				}
				else
				if (elem.getName().compareTo("maxepochs") == 0) {
					_net_struct.setMaxEpochs(Integer.parseInt(elem.getText()));
				}
			}
					
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Add an element to XML tree
	 * @param root tree node, where to insert our element
	 * @param elem_name element name (tag name)
	 * @param elem_text element text (between tags)
	 * @param elem_att_name attribute name (inside tag)
	 * @param elem_att_value attribute vale (inside tag)
	 * @return the created element
	 */
	private Element addXMLElement(Element root, String elem_name, String elem_text, 
			String elem_att_name, String elem_att_value) {
		
		Element element = new Element(elem_name);
		
		if (elem_text != null) {
			element.setText(elem_text);
		}
		
		if (elem_att_name != null && elem_att_value != null) {
			element.setAttribute(elem_att_name, elem_att_value);
		}
		
		root.addContent(element);
		
		return element;
	}
	
	/**
	 * Write a short form of run parameters in XML file
	 * @param filename name of XML file
	 */
	public void shortWriteToXML(String filename) {
		_own_filename = filename;
		
		Element root = new Element("params");
		
		Element experiment = this.addXMLElement(root, "experiment", null, 
				"name", this.getExperimentName());
		
		this.addXMLElement(experiment, "network", null, 
				"name", this.getNetworkName());
				
		Document document = new Document(root);
		
		XMLOutputter foutput = new XMLOutputter();
		Writer out;
		
		try {
		
			out = new FileWriter(new File(filename));
			foutput.output(document, out);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Write the long form of run parameters in XML file
	 * @param filename name of XML file
	 */
	public void writeToXML(String filename) {
		_own_filename = filename;
		
		Element root = new Element("params");
		
		Element experiment = this.addXMLElement(root, "experiment", null, 
				"name", this.getExperimentName());
		
		this.addXMLElement(experiment, "inputpath", this.getInputPath(), 
				"location", this.getInputLocation().name());
		
		this.addXMLElement(experiment, "outputpath", this.getOutputPath(), 
				"format", this.getOutputFormat().name());
		
		Element network = this.addXMLElement(experiment, "network", null, 
				"name", this.getNetworkName());
		
		this.addXMLElement(network, "inputpop", Integer.toString(_net_struct.getInputPop()), 
				null, null);
		
		for (int i = 0; i < _net_struct.getMiddlezPop().size(); i++) {
			this.addXMLElement(network, "middlepop", Integer.toString(_net_struct.getMiddlezPop().get(i)), 
					null, null);
		}
		
		this.addXMLElement(network, "outputpop", Integer.toString(_net_struct.getOutputPop()), 
				null, null);
		
		this.addXMLElement(network, "error", Double.toString(_net_struct.getError()), 
				null, null);
		
		this.addXMLElement(network, "threshold", Double.toString(_net_struct.getThreshold()), 
				null, null);
				
		this.addXMLElement(network, "maxepochs", Integer.toString(_net_struct.getMaxEpochs()), 
				null, null);
		
		
		Document document = new Document(root);
		
		XMLOutputter foutput = new XMLOutputter();
		Writer out;
		
		try {
		
			out = new FileWriter(new File(filename));
			foutput.output(document, out);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setNetStruct(NetworkStruct net_struct) {
		_net_struct = net_struct;
	}
	
	public NetworkStruct getNetStruct() {
		return _net_struct;
	}
	
	public void setExperimentName(String exper_name) {
		_exper_name = exper_name;
	}
	
	public String getExperimentName() {
		return _exper_name;
	}
	
	public void setInputPath(String input_path) {
		_input_path = input_path;
	}
	
	public String getInputPath() {
		return _input_path;
	}
	
	public void setOutputPath(String output_path) {
		_output_path = output_path;
	}
	
	public String getOutputPath() {
		return _output_path;
	}
	
	public void setNetworkName(String network_name) {
		_network_name = network_name;
	}
	
	public String getNetworkName() {
		return _network_name;
	}
	
	public void setInputLocation(InputLocation input_location) {
		_input_location = input_location;
	}
	
	public InputLocation getInputLocation() {
		return _input_location;
	}
	
	public void setOutputFormat(OutputFormat output_format) {
		_output_format = output_format;
	}
	
	public OutputFormat getOutputFormat() {
		return _output_format;
	}
	
	public String getOwnFilename() {
		return _own_filename;
	}
}
