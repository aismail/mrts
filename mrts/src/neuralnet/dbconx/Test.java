package neuralnet.dbconx;

import neuralnet.mapred.dmodel.ArcValues;
import neuralnet.network.Arc;
import neuralnet.network.Network;
import neuralnet.network.NetworkStruct;
import neuralnet.network.OutputNode;
import cassdb.interfaces.IHashClient;
import cassdb.internal.HashClient;

public class Test {

	public static void main(String[] args) {
		double qerr = 0;
		MrtsConnector conx = new MrtsConnector();
		IHashClient hash = new HashClient(conx.getKeyspace());
		
		// Create and put net_struct
		NetworkStruct net_struct; 
		net_struct = (NetworkStruct)hash.get(MrtsConnector.NET_STRUCT_COLFAM, 
						"experiment1", 
						"structure1");
		
		System.out.println("NetworkStruct: " + net_struct.getInputPop() + " " + net_struct.getMiddlezPop() + " " +
				net_struct.getOutputPop() + " " + net_struct.getError() + " " + net_struct.getMaxEpochs());
		
		// Create neural-network
		Network network = new Network(net_struct);
		
		// Initialize weights
		for (Arc arc : network.getArcs()) {
			ArcValues wgd = (ArcValues)hash.get(MrtsConnector.NET_WGE_COLFAM, 
					arc.getInputNode().getId(), 
					arc.getOutputNode().getId());
			
			arc.setWeight(wgd.getWeight());
		}
		
		// Get output errors
		for (OutputNode anode : network.getOutputNodes()) {
			double oerr = (Double)hash.get(MrtsConnector.NET_WGE_COLFAM,
					0, // output_errors_row
					anode.getId());
			qerr += oerr;			
		}
		
		// Print mean squared error (a.k.a quadratic loss)
		System.out.println(qerr / (double)(network.getOutputNodes().length));
	}
}
