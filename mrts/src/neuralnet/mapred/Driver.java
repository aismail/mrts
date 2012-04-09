package neuralnet.mapred;

import java.io.IOException;

import neuralnet.mapred.dmodel.PairDataWritable;
import neuralnet.mapred.dmodel.OutputError;
import neuralnet.mapred.dmodel.WGDdW;
import neuralnet.network.Arc;
import neuralnet.network.Network;
import neuralnet.network.NetworkStruct;
import neuralnet.network.OutputNode;

import org.apache.commons.lang.ObjectUtils.Null;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;

import cassdb.Connector;
import cassdb.internal.HashCl;

public class Driver {
	private Connector _conx;
	private HashCl _hash;
	
	/**
	 * Default constructor
	 */
	public Driver() {
		_conx = new Connector();
		_hash = new HashCl(_conx.getKeyspace());
	}
	
	/**
	 * Initialize network weights
	 * @param network neural-network (feedfwd)
	 */
	private void initNetWeights(Network network) {
		for (Arc arc : network.getArcs()) {
			WGDdW wgdw = new WGDdW(arc.getWeight(), 0, 0.1, 0);
			
			_hash.put(Connector.NET_WGE_COLFAM, 
					arc.getInputNode().getId(), 
					arc.getOutputNode().getId(), 
					wgdw);
		}
	}
	
	/**
	 * Compute the mean squared error of the network (quadratic loss)
	 * @param network neural-network (feedfwd)
	 * @return mean squared error
	 */
	private double computeQError(Network network) {
		double qerr = 0;
		
		for (OutputNode anode : network.getOutputNodes()) {
			OutputError oerr = (OutputError)_hash.get(Connector.NET_WGE_COLFAM,
					0, // output_errors_row
					anode.getId());
			qerr += Math.pow(oerr.getValue(), 2);			
		}
		
		return qerr / (double)(network.getOutputNodes().length);
	}
	
	/**
	 * Push the network structure to the database
	 * @param net_struct neural network structure
	 */
	private void pushNetStruct(NetworkStruct net_struct) {
		// [Iter1] Hardcoded keyL & keyC
		_hash.put(Connector.NET_STRUCT_COLFAM, 
				"experiment1", 
				"structure1", 
				net_struct);
	}
	
	/**
	 * Run map-reduce jobs for neural-network training
	 */
	public void run(String input) 
		throws IOException, InterruptedException, ClassNotFoundException {
		double qerr = Double.MAX_VALUE, ep = 0;
		
		// [Iter1] Harcoded structure, it should be taken from somewhere else
		NetworkStruct net_struct = new NetworkStruct(0.1);
		net_struct.setInputPop(301);
		net_struct.addMiddlePop(100);
		net_struct.setOutputPop(2);
		this.pushNetStruct(net_struct);
		
		Network network = new Network(net_struct);
		this.initNetWeights(network);
		
		Configuration conf = new Configuration();
		
		// Run map-reduce jobs until the network has the desired error
		while (qerr > net_struct.getError()) {
			ep++;
			
			if (net_struct.getMaxEpochs() != -1 &&
					ep > net_struct.getMaxEpochs()) {
				break;
			}
			
			Job job = new Job(conf, "mrts");
		
			job.setMapOutputKeyClass(Text.class);
			job.setMapOutputValueClass(PairDataWritable.class);
		
			job.setOutputKeyClass(Null.class);
			job.setOutputValueClass(Null.class);
		
			job.setJarByClass(Driver.class);
			job.setMapperClass(Map.class);
			job.setReducerClass(Reduce.class);
		
			job.setInputFormatClass(TextInputFormat.class);
			job.setOutputFormatClass(NullOutputFormat.class);
		
			FileInputFormat.setInputPaths(job, new Path(input));
					
			job.waitForCompletion(true);
			
			qerr = this.computeQError(network);
		}
	}
	
	/**
	 * Main function - run driver 
	 * @param args 
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {		
		Driver neuralDriver = new Driver();
		neuralDriver.run(args[0]);		
	}
}
