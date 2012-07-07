package neuralnet.dbconx;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import neuralnet.mapred.dmodel.ArcValues;
import neuralnet.network.AbstractNode;
import neuralnet.network.Arc;
import neuralnet.network.Network;
import neuralnet.network.NetworkStruct;
import neuralnet.network.OutputNode;
import cassdb.interfaces.IHashClient;
import cassdb.internal.HashClient;

/**
 * Test class for network save (into NET_COLFAM_SAVE)
 * 
 * @author cbarca
 */
public class Save {

	public static void main(String[] args) {
		java.util.Map<Integer, Object> map = new HashMap<Integer, Object>();
		MrtsConnector conx = new MrtsConnector();
		IHashClient hash = new HashClient(conx.getKeyspace());
		double qerr = 0;
		
		// Create and get net_struct
		NetworkStruct net_struct; 
		net_struct = (NetworkStruct)hash.get(MrtsConnector.NET_STRUCT_COLFAM, 
						"experiment4", 
						"structure4");
		
		System.out.println("NetworkStruct: " + net_struct.getInputPop() + " " + net_struct.getMiddlezPop() + " " +
				net_struct.getOutputPop() + " " + net_struct.getError() + " " + net_struct.getMaxEpochs());
		
		// Create neural-network
		Network network = new Network(net_struct);
		
		// Get input layer's weights
		for (AbstractNode node : network.getInputNodes()) {
			map = hash.getRow(MrtsConnector.NET_WGE_COLFAM, 
					node.getId(), 
					node.getOutputArcs().size());
			
			for (Arc arc : node.getOutputArcs()) {
				ArcValues wgd = (ArcValues)map.get(arc.getOutputNode().getId());
				if (wgd == null) {
					wgd = (ArcValues) hash.get(MrtsConnector.NET_WGE_COLFAM, 
							node.getId(),
							arc.getOutputNode().getId());
				}
				arc.setWeight(wgd.getWeight());	
			}
		}
		
		System.out.println("Input layer's weights pulled from cassandra");
		
		// Get middles' layers weights
		for (AbstractNode[] nodes : network.getMiddleLayers()) {
			for (AbstractNode node : nodes) {
				map = hash.getRow(MrtsConnector.NET_WGE_COLFAM, 
						node.getId(), 
						node.getOutputArcs().size());
				
				for (Arc arc : node.getOutputArcs()) {
					ArcValues wgd = (ArcValues)map.get(arc.getOutputNode().getId());
					if (wgd == null) {
						wgd = (ArcValues) hash.get(MrtsConnector.NET_WGE_COLFAM, 
								node.getId(),
								arc.getOutputNode().getId());
					}
					arc.setWeight(wgd.getWeight());	
				}
			}
		}
		
		System.out.println("Middle layers' weights pulled from cassandra");
		
		// Get output errors
		for (OutputNode anode : network.getOutputNodes()) {
			double oerr = (Double)hash.get(MrtsConnector.NET_WGE_COLFAM,
					0, // output_errors_row
					anode.getId());
			
			qerr += oerr;			
		}
		
		System.out.println("Network trained error is " + 
				qerr / (double)(network.getOutputNodes().length));
		
		System.out.println("Saving neural network into a readable format ...");
		
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("weights")));
			for (Arc arc : network.getArcs()) {
				bw.write(arc.getInputNode().getId() + "," + arc.getOutputNode().getId() + 
						"," + arc.getWeight() + '\n');
			}
			bw.close();			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (Arc arc : network.getArcs()) {
			hash.put(MrtsConnector.NET_SAVE_COLFAM, 
					arc.getInputNode().getId(), 
					arc.getOutputNode().getId(), 
					arc.getWeight());
		}
		
		System.out.println("Neural network saved succesfully");
	}
}
