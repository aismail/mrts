package neuralnet.mapred;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import neuralnet.mapred.dmodel.OutputError;
import neuralnet.mapred.dmodel.PairDataWritable;
import neuralnet.mapred.dmodel.WGDdW;

import org.apache.commons.lang.ObjectUtils.Null;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import cassdb.Connector;
import cassdb.internal.HashCl;

public class Reduce extends Reducer<Text, PairDataWritable, Null, Null> {
	// Constants
	public static final double NPLUS = 1.2, NMINUS = 0.5, 
		DMAX = 50, DMIN = 1e-6;
	
	// Private members
	private HashMap<Integer, Double> _sumup;
	private Connector _conx;
	private HashCl _hash;
	private int _node;
	
	/**
	 * Default constructor
	 */
	public Reduce() {
		super();
		_conx = new Connector();
		_hash = new HashCl(_conx.getKeyspace());
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
	 */
	public void updateWeight(int input_node, int output_node, double gradient) {
		double weight, last_gradient, delta, deltaw, change;
		WGDdW old_wgd, new_wgd;
		
		old_wgd = (WGDdW) _hash.get(Connector.NET_WGE_COLFAM, input_node, output_node);
		weight = old_wgd.getWeight();
		last_gradient = old_wgd.getGradient();
		delta = old_wgd.getDelta();
		deltaw = old_wgd.getDeltaW();
		
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

		new_wgd = new WGDdW(weight, last_gradient, delta, deltaw);
		_hash.put(Connector.NET_WGE_COLFAM, input_node, output_node, new_wgd);
	}
	
	/**
	 * Update the output error of last layer
	 * @param output_node node from last layer
	 * @param oerr output error of node
	 */
	public void updateOutputError(int output_node, double oerr) {
		OutputError output_error = new OutputError(oerr);
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
		
		// Aggregate (sum-up) data from mapping 
		for (PairDataWritable val : values) {
			this.sumUpData(val.getDestination(), val.getValue());
		}
	
		// Update data
		for (Entry<Integer, Double> arcg : _sumup.entrySet()) {
			if (arcg.getKey() != 0) {
				// Update weights using local adaptive method (Rprop)
				this.updateWeight(_node, arcg.getKey(), arcg.getValue());
			}
			else {
				// Update output error
				this.updateOutputError(_node, arcg.getValue());
			}
		}
	}
}
