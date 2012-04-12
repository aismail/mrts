package cassdb;

import neuralnet.mapred.dmodel.OutputError;
import neuralnet.mapred.dmodel.WGDdW;
import neuralnet.network.NetworkStruct;
import cassdb.interfaces.IHashCl;
import cassdb.internal.HashCl;

public class Main {

	public static void main(String[] args) {
		Connector conx = new Connector();
		IHashCl hash = new HashCl(conx.getKeyspace());
		
		WGDdW wgdw = new WGDdW(0.1, 0.1, 0.1, 0.2), nwgdw;
		OutputError oerr = new OutputError(0.01), noerr;
		
		hash.put(Connector.NET_WGE_COLFAM, 1, 2, wgdw);
		nwgdw = (WGDdW)hash.get(Connector.NET_WGE_COLFAM, 1, 2);
		
		System.out.println(nwgdw.getWeight() + " " + nwgdw.getGradient() + " " + nwgdw.getDeltaW());
		
		hash.put(Connector.NET_WGE_COLFAM, 0, 1, oerr);
		noerr = (OutputError)hash.get(Connector.NET_WGE_COLFAM, 0, 1);
		
		System.out.println(noerr.getValue());
		
		NetworkStruct net_struct = new NetworkStruct(0.1);
		net_struct.setInputPop(301);
		net_struct.addMiddlePop(100);
		net_struct.setOutputPop(2);
		
		
		hash.put(Connector.NET_STRUCT_COLFAM, "experiment1", "structure1", net_struct);
		net_struct = (NetworkStruct)hash.get(Connector.NET_STRUCT_COLFAM, "experiment1", "structure1");
		
		System.out.println("NetworkStruct: " + net_struct.getInputPop() + " " + net_struct.getMiddlezPop() + " " +
				net_struct.getOutputPop() + " " + net_struct.getError() + " " + net_struct.getMaxEpochs());
	}
}
