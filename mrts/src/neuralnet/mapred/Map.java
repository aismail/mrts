package neuralnet.mapred;

import java.io.IOException;

import neuralnet.mapred.dmodel.PairDataWritable;
import neuralnet.mapred.dmodel.WGDdW;
import neuralnet.network.Arc;
import neuralnet.network.Mathz;
import neuralnet.network.Network;
import neuralnet.network.NetworkStruct;
import neuralnet.network.OutputNode;
import neuralnet.network.Pattern;
import neuralnet.network.PatternList;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cassdb.Connector;
import cassdb.interfaces.IHashCl;
import cassdb.internal.HashCl;

public class Map extends Mapper<Text, Text, Text, PairDataWritable>  {
	// Constants
	public static final String SPLIT_TOKEN = "#";
	
	// Private members
	private Connector _conx;
	private IHashCl _hash;
	private PatternList _pattern;
	private NetworkStruct _net_struct;
	private Network _network;
	private Text _nkey;
	private static Logger logger = LoggerFactory.getLogger(Map.class);
	
	/**
	 * Default constructor
	 */
	public Map() {
		super();
		_conx = new Connector();
		_hash = new HashCl(_conx.getKeyspace());
		_net_struct = pullNetStruct();
	}
	
	/**
	 * Initialize map: initialize pattern list and network
	 */
	public void initMap() {
		_pattern = new PatternList();
		_network = new Network(_net_struct);
		this.initWeights(_network);
	}
	
	/**
	 * Get the network structure from cassandra
	 * @return network structure
	 */
	public NetworkStruct pullNetStruct() {
		// [Iter1] Hardcoded keyL and keyC
		return (NetworkStruct)_hash.get(Connector.NET_STRUCT_COLFAM, 
				"experiment1", 
				"structure1");
	}
	
	/**
	 * Initialize network's weights with values from cassandra
	 * @param network neural-network
	 */
	private void initWeights(Network network) {
		for (Arc arc : network.getArcs()) {
			WGDdW wgd = (WGDdW)_hash.get(Connector.NET_WGE_COLFAM, 
					arc.getInputNode().getId(), 
					arc.getOutputNode().getId());
			
			arc.setWeight(wgd.getWeight());
		}
	}
	
	/**
	 * Adds a line (after parsing) to the pattern list for train
	 * @param line train vector
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
	 */
	public boolean runEpoch(Network network) {
		// run_net + train_net
		int limit = _pattern.size();
		boolean success = false;
		long run_start, run_end, tot_run = 0, 
			train_start, train_end, tot_train = 0;
		double threshold = 0.01;
		
		network.resetQError();
		
		for (int i = 0; i < limit; i++) {
			Pattern pattern = _pattern.get(i);
			
			run_start = System.currentTimeMillis();
			_network.runNetWork(pattern.getInput());
			run_end = System.currentTimeMillis();
			
			tot_run += (run_end - run_start);

			train_start = System.currentTimeMillis();
			double[] raw_results = _network.trainNetWork(pattern.getOutput());
			train_end = System.currentTimeMillis();
			
			tot_train += (train_end - train_start);
			
			// Just for self-checking
			
			int[] truth = Mathz.thresholdArray(threshold, pattern.getOutput());
			int[] results = Mathz.thresholdArray(threshold, raw_results);
	
			pattern.setTrained(true);
			for (int jj = 0; jj < raw_results.length; jj++) {
				if (results[jj] != truth[jj]) {
					pattern.setTrained(false);
					break;
				}
			}
	
			if (pattern.isTrained()) {
				success = true;
			}
		}
		
		return success;
	}

	/**
	 * Map function   
	 */
	@Override
	public void map(Text key, Text value, Context context) 
		throws IOException, InterruptedException {
	
		// Initialize map function
		this.initMap();
		
		logger.info("Map function initialized");
		
		// Parse each line from the chunk
		String[] vectors = value.toString().split(SPLIT_TOKEN);
		for (String vector : vectors) {
			this.addToTrainSet(vector);
		}
		
		logger.info("PatternList for network-train created");
		
		// Run one epoch with the train data
		boolean success = this.runEpoch(_network);
		
		logger.info("Epoch finnised - success = " + success);
		
		// Pass the values to reducers
		
		// PairDataWritable: N1 <N2 G>
		for (Arc arc : _network.getArcs()) {
			_nkey.set(new Text(arc.getInputNode().getId() + ""));
			PairDataWritable pdw = new PairDataWritable(arc.getOutputNode().getId(), arc.getGradient());
			context.write(_nkey, pdw);
		}
		
		logger.info("Gradients sent");
		
		// PairDataWritable: No <0 Err> 
		for (OutputNode node : _network.getOutputNodes()) {
			_nkey.set(new Text(node.getId() + ""));
			PairDataWritable pdw = new PairDataWritable(node.getOutputError());
			context.write(_nkey, pdw);
		}
		
		logger.info("OutputErrors sent");
	}
}
