package cassdb;

import neuralnet.mapred.dmodel.OutputError;
import neuralnet.mapred.dmodel.WGDdW;
import cassdb.internal.HashCl;

public class Main {

	public static void main(String[] args) {
		Connector conx = new Connector();
		HashCl hash = new HashCl(conx.getKeyspace());
		
		WGDdW wgdw = new WGDdW(0.1, 0.1, 0.1, 0.2), nwgdw;
		OutputError oerr = new OutputError(0.01), noerr;
		
		hash.put(Connector.NET_WGE_COLFAM, new Integer(1), new Integer(2), wgdw);
		nwgdw = (WGDdW)hash.get(Connector.NET_WGE_COLFAM, new Integer(1), new Integer(2));
		
		System.out.println(nwgdw.getWeight() + " " + nwgdw.getGradient() + " " + nwgdw.getDeltaW());
		
		hash.put(Connector.NET_WGE_COLFAM, new Integer(0), new Integer(1), oerr);
		noerr = (OutputError)hash.get(Connector.NET_WGE_COLFAM, new Integer(0), new Integer(1));
		
		System.out.println(noerr.getValue());
	}
}
