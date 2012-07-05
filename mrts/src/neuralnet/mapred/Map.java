package neuralnet.mapred;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import neuralnet.dbconx.MrtsConnector;
import neuralnet.mapred.dmodel.ArcValues;
import neuralnet.mapred.dmodel.PairDataWritable;
import neuralnet.mapred.util.RunParams;
import neuralnet.network.AbstractNode;
import neuralnet.network.Arc;
import neuralnet.network.Mathz;
import neuralnet.network.Network;
import neuralnet.network.NetworkStruct;
import neuralnet.network.OutputNode;
import neuralnet.network.Pattern;
import neuralnet.network.PatternList;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cassdb.interfaces.IConnector;
import cassdb.interfaces.IHashClient;
import cassdb.internal.HashClient;

public class Map extends Mapper<LongWritable, Text, Text, PairDataWritable>  {
	// Constants
	public static final String SPLIT_TOKEN = "#";
	public static final String SPARAMS_FILENAME = "short_run.xml";
	
	// Private members
	private IConnector _conx;
	private IHashClient _hash;
	private PatternList _pattern;
	private NetworkStruct _net_struct;
	private Network _network;
	private Text _nkey;
	private RunParams _run_params;
	private static Logger _logger = LoggerFactory.getLogger(Map.class);
	
	/**
	 * Default constructor
	 */
	public Map() {
		super();
		_conx = new MrtsConnector();
		_hash = new HashClient(_conx.getKeyspace());
		_nkey = new Text();
	}
	
	/**
	 * Initialize map: initialize pattern list and network
	 */
	public void initMap() {
		_pattern = new PatternList();
		_network = new Network(_net_struct);
		this.initNetWeights(_network);
	}
	
	/**
	 * Get the network structure from cassandra
	 * @return network structure
	 */
	public NetworkStruct pullNetStruct(RunParams run_params) {
		return (NetworkStruct)_hash.get(MrtsConnector.NET_STRUCT_COLFAM, 
				run_params.getExperimentName(),  
				run_params.getNetworkName()); 
	}
	
	/**
	 * Initialize network's weights with values from cassandra
	 * @param network neural-network
	 */
	private void initNetWeights(Network network) {
		java.util.Map<Integer, Object> map;
		
		_logger.info("Init weights");
		_logger.info("Hash UP = " + ((_hash == null) ? false : true));
		_logger.info("Arcs number is " + network.getArcs().size());
		
		for (AbstractNode node : network.getInputNodes()) {
			map = _hash.getRow(MrtsConnector.NET_WGE_COLFAM, 
					node.getId(), 
					node.getOutputArcs().size());
			
			_logger.info("Node's arcs number is " + node.getOutputArcs().size());
			_logger.info("Map for input node " + node.getId() + 
					" received, size = " + map.size());
			
			for (Arc arc : node.getOutputArcs()) {
				ArcValues wgd = (ArcValues)map.get(arc.getOutputNode().getId());
				
				// TO BE REPAIRED: incomplete row retrieval?!
				// Happens just when the experiment is changed 
				// and the schema isn't dropped
				if (wgd == null) {
					_logger.info("Wgd is null for arc: " +
					+ arc.getInputNode().getId() + " - " +
					+ arc.getOutputNode().getId());
					_logger.info("Get the arc value again!");
					
					wgd = (ArcValues)_hash.get(MrtsConnector.NET_WGE_COLFAM, 
							arc.getInputNode().getId(), 
							arc.getOutputNode().getId());
				}
				
				arc.setWeight(wgd.getWeight());	
			}
			
			_logger.info("Weights for input node " + node.getId() + " initialized");
		}
		
		_logger.info("Input layer's weights initialized");
		
		for (AbstractNode[] nodes : network.getMiddleLayers()) {
			for (AbstractNode node : nodes) {
				map = _hash.getRow(MrtsConnector.NET_WGE_COLFAM, 
						node.getId(), 
						node.getOutputArcs().size());
				
				_logger.info("Node's arcs number is " + node.getOutputArcs().size());
				_logger.info("Map for middle node " + node.getId() + 
						" received, size = " + map.size());
				
				for (Arc arc : node.getOutputArcs()) {
					ArcValues wgd = (ArcValues)map.get(arc.getOutputNode().getId());
					
					// Happens just when the experiment is changed 
					// and the schema isn't dropped
					if (wgd == null) {
						_logger.info("Wgd is null for arc: " +
						+ arc.getInputNode().getId() + " - " +
						+ arc.getOutputNode().getId());
						_logger.info("Get the arc value again!");
						
						wgd = (ArcValues)_hash.get(MrtsConnector.NET_WGE_COLFAM, 
								arc.getInputNode().getId(), 
								arc.getOutputNode().getId());
					}
					
					arc.setWeight(wgd.getWeight());	
				}
				
				_logger.info("Weights for input node " + node.getId() + " initialized");
			}
		}
		
		_logger.info("Middle layers' weights initialized");
	}
	
	/**
	 * Adds a train vector (after parsing) to the pattern list
	 * @param vector train-vector
	 */
	public void addToTrainSet(String vector) {
		String[] elems = vector.split(",");
		int ninput = _net_struct.getInputPop(), 
			noutput = _net_struct.getOutputPop();
		double[] input = new double[ninput],
			output = new double[noutput];
		
		for (int i = 0; i < ninput; i++) {
			input[i] = Double.parseDouble(elems[i]);
		}
		
		for (int i = ninput; i < ninput + noutput; i++) {
			output[i - ninput] = Double.parseDouble(elems[i]);
		}
		
		_pattern.add(new Pattern(input, output));
	}
	
	/**
	 * Run a train epoch over the pattern list
	 * @param network neural-network
	 * @return number of successful train iterations
	 */
	public int runEpoch(Network network) {
		// run network + train network
		int limit = _pattern.size();
		int success = 0;
		long fwd_start, fwd_end, tot_fwd = 0, 
			bwd_start, bwd_end, tot_bwd = 0;
		double threshold = _net_struct.getThreshold();
		
		network.resetQError();
		network.resetAggOutputError();
		
		for (int i = 0; i < limit; i++) {
			Pattern pattern = _pattern.get(i);
			
			fwd_start = System.currentTimeMillis();
			_network.runNetWork(pattern.getInput());
			fwd_end = System.currentTimeMillis();
			
			tot_fwd += (fwd_end - fwd_start);

			bwd_start = System.currentTimeMillis();
			double[] raw_results = _network.trainNetWork(pattern.getOutput());
			bwd_end = System.currentTimeMillis();
			
			tot_bwd += (bwd_end - bwd_start);
			
			// Just for self-checking
			
			int[] truth = Mathz.thresholdArray(threshold, pattern.getOutput());
			int[] results = Mathz.thresholdArray(threshold, raw_results);
	
			pattern.setTrained(true);
			for (int j = 0; j < raw_results.length; j++) {
				if (results[j] != truth[j]) {
					pattern.setTrained(false);
					break;
				}
			}
	
			if (pattern.isTrained()) {
				success++;
			}
		}
		
		_logger.info("Fwd time = " + tot_fwd);
		_logger.info("Bwd time = " + tot_bwd);
		_logger.info("Epoch finnished with qerr = " + _network.getQError());
	
		return success;
	}
	
	/**
	 * Read run parameters from distributed cache
	 * @param conf job configuration
	 * @throws IOException
	 */
	public void readRunParams(Configuration conf) throws IOException {
		Path[] uris = DistributedCache.getLocalCacheFiles(conf);
		
		for (int i = 0; i < uris.length; i++) {
			if (uris[i].toString().contains(SPARAMS_FILENAME)) {
				BufferedReader fis = new BufferedReader(new FileReader(uris[i].toString()));
				_run_params.readFromXML(fis);
			}
		}
	}

	/**
	 * Map setup function
	 */
	@Override
	protected void setup(Mapper<LongWritable, Text, Text, PairDataWritable>.Context context) 
		throws IOException, InterruptedException {
		
		_run_params = new RunParams();
		
		// Read run parameters
		this.readRunParams(context.getConfiguration());
		
		_logger.info("Short run parameters read");
		
		_net_struct = pullNetStruct(_run_params);
	}
		
	/**
	 * Map function   
	 */
	@Override
	public void map(LongWritable key, Text value, Context context) 
		throws IOException, InterruptedException {
		long tstart, tend;
		
		tstart = System.currentTimeMillis();
		// Initialize map function
		this.initMap();
		tend = System.currentTimeMillis();
				
		_logger.info("Map function initialized in " + 
				(double)(tend - tstart) / 1000 + " sec");
		
		tstart = System.currentTimeMillis();
		// Parse each line from the chunk
		String[] vectors = value.toString().split(SPLIT_TOKEN);
		for (String vector : vectors) {
			this.addToTrainSet(vector);
		}
		tend = System.currentTimeMillis();
		
		_logger.info("PatternList for network-train created in " + 
				(double)(tend - tstart) / 1000 + " sec" +
				", size is " + _pattern.size());
		
		tstart = System.currentTimeMillis();
		// Run one epoch with the train data
		int success = this.runEpoch(_network);
		tend = System.currentTimeMillis();
		
		_logger.info("Epoch finnised with success rate " + success 
				+ " out of " + _pattern.size() + " in " +
				(double)(tend - tstart) / 1000 + " sec");
		
		// Pass the values to reducers
		
		tstart = System.currentTimeMillis();
		// PairDataWritable: N1 <N2 G>
		for (Arc arc : _network.getArcs()) {
			_nkey.set(new Text(arc.getInputNode().getId() + ""));
			PairDataWritable pdw = new PairDataWritable(
					arc.getOutputNode().getId(),
					arc.getGradient());
			context.write(_nkey, pdw);
		}
		tend = System.currentTimeMillis();
		
		_logger.info("Gradients sent in " +
				(double)(tend - tstart) / 1000 + " sec");
		
		tstart = System.currentTimeMillis();
		// PairDataWritable: No <0 Err> 
		for (OutputNode node : _network.getOutputNodes()) {
			_nkey.set(new Text(node.getId() + ""));
			PairDataWritable pdw = new PairDataWritable(node.getAggOuputError());
			context.write(_nkey, pdw);
		}
		tend = System.currentTimeMillis();
		
		_logger.info("OutputErrors sent in " + 
				(double)(tend - tstart) / 1000 + " sec");
	}
}
