package neuralnet.mapred;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import neuralnet.mapred.dmodel.PairDataWritable;
import neuralnet.mapred.dmodel.ArcValues;

import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cassdb.Connector;
import cassdb.interfaces.IHashClient;
import cassdb.internal.HashClient;

public class Reduce extends Reducer<Text, PairDataWritable, BooleanWritable, BooleanWritable> {
	// Constants
	public static final double NPLUS = 1.2, NMINUS = 0.5, 
		DMAX = 50, DMIN = 1e-6;
	
	// Private members
	private HashMap<Integer, Double> _sumup;
	private Connector _conx;
	private IHashClient _hash;
	private int _node;
	private static Logger logger = LoggerFactory.getLogger(Reduce.class);
	
	/**
	 * Default constructor
	 */
	public Reduce() {
		super();
		_conx = new Connector();
		_hash = new HashClient(_conx.getKeyspace());
	}
	
	/**
	 * Initialize reduce: initialize input node and hash sum-up
	 * @param key
	 */
	public void initReduce(Text key) {
		_sumup = new HashMap<Integer, Double>();
		_node = Integer.parseInt(key.toString());
	}
	
	/**
	 * Aggregate gradient and error from mapping
	 * @param key 
	 * @param value 
	 */
	public void sumUpData(Integer key, Double value) {
		if (_sumup.containsKey(key)) {
			double newv = _sumup.get(key) + value;
			_sumup.put(key, newv);
		} 
		else {
			_sumup.put(key, value);
		}
	}
	
	/**
	 * Update weight using current gradient (local adaptive method)
	 * Batch mode - Resilient backpropagation
	 * @param input_node input arc node
	 * @param output_node output arc node
	 * @param gradient arc gradient value
	 */
	public void updateWeight(int input_node, int output_node, double gradient) {
		double weight, last_gradient, delta, deltaw, change;
		ArcValues last_wgd, curr_wgd;
		
		last_wgd = (ArcValues)_hash.get(Connector.NET_WGE_COLFAM, input_node, output_node);
		weight = last_wgd.getWeight();
		last_gradient = last_wgd.getGradient();
		delta = last_wgd.getDelta();
		deltaw = last_wgd.getDeltaW();
		
		// Do the dew - Rprop 
    	if (deltaw == 0 && last_gradient == 0 && delta == 0.1) {
    		change = Math.signum(gradient);
    	}
    	else {
    		change = Math.signum(gradient * last_gradient);
    	}

    	if (change > 0) {
    		delta = Math.min(delta * NPLUS, DMAX);
    		deltaw = -1 * Math.signum(gradient) * delta;
    		weight += deltaw;
    		last_gradient = gradient;
    	}
    	else if (change < 0) {
    		delta = Math.max(delta * NMINUS, DMIN);
    		last_gradient = 0;
    	} 
    	else if (change == 0) {
    		deltaw = -1 * Math.signum(gradient) * delta;
    		weight += deltaw;
    		last_gradient = gradient;
    	}

		curr_wgd = new ArcValues(weight, last_gradient, delta, deltaw);
		_hash.put(Connector.NET_WGE_COLFAM, input_node, output_node, curr_wgd);
	}
	
	/**
	 * Update the output error of last layer
	 * @param output_node node from last layer
	 * @param oerr output error of node
	 */
	public void updateOutputError(int output_node, double oerr) {
		Double output_error = new Double(oerr);
		_hash.put(Connector.NET_WGE_COLFAM, 0, output_node, output_error);
	}
	
	/**
	 * Reduce function
	 */
	@Override
	public void reduce(Text key, Iterable<PairDataWritable> values, Context context)
			throws IOException, InterruptedException {
		
		// Initialize reduce function
		this.initReduce(key);
		
		logger.info("Reduce function initialized for Node " + key.toString());
		
		int cont = 0;
		// Aggregate (sum-up) data from mapping 
		for (PairDataWritable val : values) {
			this.sumUpData(val.getDestination(), val.getValue());
			cont++;
		}
		
		logger.info("Gradients/errors aggregated, list size " + cont);
	
		cont = 0;
		// Update data
		for (Entry<Integer, Double> arcg : _sumup.entrySet()) {
			cont++;
			if (arcg.getKey() != 0) {
				// Update weights using local adaptive method (Rprop)
				this.updateWeight(_node, arcg.getKey(), arcg.getValue());
			}
			else {
				// Update output error
				this.updateOutputError(_node, arcg.getValue());
			}
		}
		
		logger.info("Map.entrySet size " + cont);
		logger.info("Data - weight & output error - updated");
	}
}
