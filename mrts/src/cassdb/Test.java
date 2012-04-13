package cassdb;

import neuralnet.mapred.dmodel.OutputErrorO;
import neuralnet.mapred.dmodel.WGDdW;
import neuralnet.network.Arc;
import neuralnet.network.Network;
import neuralnet.network.NetworkStruct;
import neuralnet.network.OutputNode;
import cassdb.interfaces.IHashCl;
import cassdb.internal.HashCl;

public class Test {

	public static void main(String[] args) {
		double qerr = 0;
		Connector conx = new Connector();
		IHashCl hash = new HashCl(conx.getKeyspace());
		
		// Create and put net_struct
		NetworkStruct net_struct; 
		net_struct = (NetworkStruct)hash.get(Connector.NET_STRUCT_COLFAM, 
						"experiment1", 
						"structure1");
		
		System.out.println("NetworkStruct: " + net_struct.getInputPop() + " " + net_struct.getMiddlezPop() + " " +
				net_struct.getOutputPop() + " " + net_struct.getError() + " " + net_struct.getMaxEpochs());
		
		// Create neural-network
		Network network = new Network(net_struct);
		
		// Initialize weights
		for (Arc arc : network.getArcs()) {
			WGDdW wgd = (WGDdW)hash.get(Connector.NET_WGE_COLFAM, 
					arc.getInputNode().getId(), 
					arc.getOutputNode().getId());
			
			arc.setWeight(wgd.getWeight());
		}
		
		// Get output errors
		for (OutputNode anode : network.getOutputNodes()) {
			OutputErrorO oerr = (OutputErrorO)hash.get(Connector.NET_WGE_COLFAM,
					0, // output_errors_row
					anode.getId());
			qerr += Math.pow(oerr.getValue(), 2);			
		}
		
		// Print mean squared error (a.k.a quadratic loss)
		System.out.println(qerr / (double)(network.getOutputNodes().length));
	}
}
