package neuralnet.mapred;

import java.io.IOException;

import neuralnet.mapred.dmodel.PairDataWritable;
import neuralnet.mapred.dmodel.ArcValues;
import neuralnet.network.Arc;
import neuralnet.network.Network;
import neuralnet.network.NetworkStruct;
import neuralnet.network.OutputNode;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cassdb.Connector;
import cassdb.interfaces.IHashClient;
import cassdb.internal.HashClient;

public class Driver extends Configured implements Tool {
	// Private members
	private Connector _conx;
	private IHashClient _hash;
	private static Logger logger = LoggerFactory.getLogger(Driver.class);
	private Configuration _conf;
	
	/**
	 * Default constructor
	 */
	public Driver() {
		super();
		_conx = new Connector();
		_hash = new HashClient(_conx.getKeyspace());
		_conf = new Configuration();
		_conf.setBoolean("mapred.used.genericoptionparser", true);
		super.setConf(_conf);
	}
	
	/**
	 * Get the job configuration object
	 * @return configuration object
	 */
	public Configuration getJobConfiguration() {
		return _conf;
	}
	
	/**
	 * Initialize network weights
	 * @param network neural-network (feedfwd)
	 */
	private void initNetWeights(Network network) {
		for (Arc arc : network.getArcs()) {
			ArcValues wgd = new ArcValues(arc.getWeight(), 0, 0.1, 0);
			_hash.put(Connector.NET_WGE_COLFAM, 
					arc.getInputNode().getId(), 
					arc.getOutputNode().getId(), 
					wgd);
		}
	}
	
	/**
	 * Initialize (with 0) network output error
	 * @param network neural-network
	 */
	private void initOutputErrors(Network network) {
		for (OutputNode anode : network.getOutputNodes()) {			
			_hash.put(Connector.NET_WGE_COLFAM,
					0, // output_errors_row
					anode.getId(), 
					0.0);			
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
			Double oerr = (Double)_hash.get(Connector.NET_WGE_COLFAM,
					0, // output_errors_row
					anode.getId());
			logger.info("Output error: node = " + anode.getId() + " oerr = " + oerr.doubleValue());
			qerr += oerr.doubleValue();			
		}
		
		return (qerr / (double)(network.getOutputNodes().length));
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
	@Override
	public int run(String[] arg0) throws 
		Exception, IOException, InterruptedException, ClassNotFoundException {
		double qerr = Double.MAX_VALUE;
		int ep = 0;
		
		// [Iter1] Harcoded structure, it should be taken from somewhere else
		NetworkStruct net_struct = new NetworkStruct(0.1, 100);
		net_struct.setInputPop(301);
		net_struct.addMiddlePop(100);
		net_struct.setOutputPop(2);
		this.pushNetStruct(net_struct);
		
		logger.info("Network structure created & pushed to cassandra");
		
		Network network = new Network(net_struct);
		this.initNetWeights(network);
		this.initOutputErrors(network);
		
		logger.info("Neural network created, weights & out_errors initialized");
		
		logger.info("Running map-reduce jobs ...");
		
		// Run map-reduce jobs until the network has the desired error
		while (qerr > net_struct.getError()) {
			ep++;
			
			if (net_struct.getMaxEpochs() != -1 &&
					ep > net_struct.getMaxEpochs()) {
				break;
			}
			
			Job job = new Job(_conf, "mrts");
			
			job.setMapOutputKeyClass(Text.class);
			job.setMapOutputValueClass(PairDataWritable.class);
		
			job.setOutputKeyClass(BooleanWritable.class);
			job.setOutputValueClass(BooleanWritable.class);
		
			job.setJarByClass(Driver.class);
			job.setMapperClass(Map.class);
			job.setReducerClass(Reduce.class);
		
			job.setInputFormatClass(TextInputFormat.class);
			job.setOutputFormatClass(NullOutputFormat.class);
		
			FileInputFormat.setInputPaths(job, new Path(arg0[1]));
		
			logger.info("Job sent to map-reduce cluster");
			
			job.waitForCompletion(true);
			
			qerr = this.computeQError(network);
			
			logger.info("Episode " + ep + " finnished: " + qerr);
		}
		
		return 0;
	}
	
	/**
	 * Main function - run driver 
	 * @param args 
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {		
		String[] otherArgs = new GenericOptionsParser(args).getRemainingArgs();
		ToolRunner.run(new Driver(), otherArgs);
	}
}
