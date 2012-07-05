package mrtsio.mexport;

import java.util.HashMap;
import java.util.Map;

import mrtsio.dbconx.MrtsConnector;
import cassdb.interfaces.IHashClient;
import cassdb.internal.HashClient;
import fnn.network.AbstractArc;
import fnn.network.AbstractNode;
import fnn.network.RpropNetwork;
import fnn.util.RunParams;

/**
 * Static class exporter - export the trained neural network
 * from cassandra, in different formats
 * 
 * @author cbarca
 */
public class Exporter {

	/**
	 * Export from cassandra the trained rprop network
	 * @param runParams run parameters object
	 * @return resilient backprop network (trained one)
	 */
	public static RpropNetwork exportRpropNeuralNet(RunParams runParams) {
		MrtsConnector conx = new MrtsConnector();
		IHashClient hash = new HashClient(conx.getKeyspace());
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		
		// Create Resilient Backprop neural network
		RpropNetwork network = new RpropNetwork(runParams.getNetStruct());
		
		// Pull neural net from cassandra
		
		// Pull the first layer of weights (input layer) 
		for (AbstractNode node : network.getInputNodes()) {
			map = hash.getRow(MrtsConnector.NET_SAVE_COLFAM, 
					node.getId(), 
					node.getOutputArcs().size());
			
			for (AbstractArc arc : node.getOutputArcs()) {
				Double w = (Double)map.get(arc.getOutputNode().getId());
				arc.setWeight(w);	
			}
		}
		
		// Pull the rest of the weights (middle layers)
		for (AbstractNode[] nodes : network.getMiddleLayers()) {
			for (AbstractNode node : nodes) {
				map = hash.getRow(MrtsConnector.NET_SAVE_COLFAM, 
						node.getId(), 
						node.getOutputArcs().size());
				
				for (AbstractArc arc : node.getOutputArcs()) {
					Double w = (Double)map.get(arc.getOutputNode().getId());
					arc.setWeight(w);	
				}
			}
		}
		
		return network;
	}
}
