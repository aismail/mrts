package neuralnet.mapred;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import neuralnet.mapred.dmodel.PairDataWritable;
import neuralnet.mapred.dmodel.ArcValues;
import neuralnet.mapred.util.RunParams;
import neuralnet.mapred.util.RunParams.InputLocation;
import neuralnet.network.Arc;
import neuralnet.network.Network;
import neuralnet.network.NetworkStruct;
import neuralnet.network.OutputNode;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FileSystem;
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
	// Constants
	public final static String NAME_NODE = "hdfs://localhost:9000";
	public static final String SPARAMS_FILENAME = "short_run.xml";
		
	// Private members
	private Connector _conx;
	private IHashClient _hash;
	private static Logger logger = LoggerFactory.getLogger(Driver.class);
	private Configuration _conf;
	private RunParams _run_params;
	
	/**
	 * Default constructor
	 */
	public Driver() {
		super();
		_conx = new Connector();
		_hash = new HashClient(_conx.getKeyspace());
		_run_params = new RunParams();
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
	private void pushNetStruct(NetworkStruct net_struct, RunParams run_params) {
		_hash.put(Connector.NET_STRUCT_COLFAM, 
				run_params.getExperimentName(), //"experiment1", 
				run_params.getNetworkName(), //"structure1", 
				net_struct);
	}
	
	/**
	 * Push the mean squared error to the database
	 * @param epoch train epoch
	 * @param qerr mean squared error (quadratic loss)
	 */
	private void pushQErr(int epoch, double qerr) {
		_hash.put(Connector.NET_QERR_COLFAM,
				_run_params.getExperimentName(), //"experiment1", 
				Integer.toString(epoch), 
				qerr);
	}
	
	/**
	 * Read run parameters
	 * @param local_path path of the parameters file
	 * @throws FileNotFoundException 
	 */
	public void readRunParams(String local_path) 
		throws FileNotFoundException {
		BufferedReader fis = new BufferedReader(new FileReader(local_path));
		_run_params.readFromXML(fis);
	}
	
	/**
	 * Write -short- run parameters
	 * @param filename name of the short run parameters file
	 * @throws IOException 
	 */
	public void writeShortRunParams(String filename) 
		throws IOException {
		_run_params.shortWriteToXML(filename);
		FileSystem fs = FileSystem.get(_conf);
		fs.delete(new Path("/config/" + filename), true);
		fs.copyFromLocalFile(new Path(filename), new Path("/config/" + filename));
	}
	
	/**
	 * Share run parameters file to Mappers through distributed cache
	 * @param local_path path of the parameters file
	 * @throws URISyntaxException
	 */
	public void shareShortRunParams(String filename) 
		throws URISyntaxException {
		DistributedCache.addCacheFile(new URI(NAME_NODE + "/config/" + filename), _conf);
	}
	
	/**
	 * Run job in HDFS mode
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ClassNotFoundException
	 */
	private void runHDFS() 
		throws IOException, InterruptedException, ClassNotFoundException {
		double qerr = Double.MAX_VALUE;
		int ep = 0;
		long te1, te2, tep, tt1, tt2, ttotal;
		
		NetworkStruct net_struct = _run_params.getNetStruct();
		this.pushNetStruct(net_struct, _run_params);
		
		logger.info("Network structure created & pushed to cassandra");
		
		Network network = new Network(net_struct);
		this.initNetWeights(network);
		this.initOutputErrors(network);
		
		logger.info("Neural network created, weights & out_errors initialized");
		
		logger.info("Begin training, running map-reduce jobs ...");
		
		// Start timer
		tt1 = System.currentTimeMillis();
		
		// Run map-reduce jobs until the network has 
		// the desired error value
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
		
			FileInputFormat.setInputPaths(job, new Path(_run_params.getInputPath()));
		
			logger.info("Job sent to map-reduce cluster");
		
			// Start timer
			te1 = System.currentTimeMillis(); 
			
			job.waitForCompletion(true);
			
			qerr = this.computeQError(network);
			
			this.pushQErr(ep, qerr);
			
			// Stop timer
			te2 = System.currentTimeMillis();
			
			tep = (te2 - te1) / 1000;
			logger.info("Episode " + ep + " finnished with " + 
					qerr + " qerr in " + tep + " sec");
		}
		
		// Stop timer
		tt2 = System.currentTimeMillis();
		
		ttotal = (tt2 - tt1) / 1000;
		logger.info("Total train time: " + ttotal + " sec");
	}
	
	/**
	 * Run map-reduce jobs for neural-network training
	 * @param arg0 path of the run parameters file
	 */
	@Override
	public int run(String[] arg0) 
		throws Exception, IOException, InterruptedException, ClassNotFoundException {
		
		// Read run parameters from local file
		this.readRunParams(arg0[1]); 
		this.writeShortRunParams(SPARAMS_FILENAME);
		this.shareShortRunParams(SPARAMS_FILENAME);
		
		logger.info("Run parameters file - read, short write & short share");
		
		if (_run_params.getInputLocation().equals(InputLocation.HDFS)) {
			this.runHDFS();
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
