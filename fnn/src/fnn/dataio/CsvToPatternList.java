package fnn.dataio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import au.com.bytecode.opencsv.CSVReader;

/**
 * CSV to pattern (internal) converter
 *  
 * @author cbarca
 */
public class CsvToPatternList {
	PatternList _pl;
	
	/**
	 * Default constructor
	 */
	public CsvToPatternList() {
		_pl = new PatternList();
	}
	
	/**
	 * Read CSV files from a directory and copy them in a pattern list
	 * @param dir_path directory path
	 * @param inputpop number of data used for network's input
	 * @param outputpop number of data used for network's output
	 * @param out_path output path (for .trn serialized file)
	 * @param normalize true if normalize / false either
	 * @param negative true if data contain negative values / false either
	 * @return pattern list
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public PatternList convert(String dir_path, int inputpop, 
		int outputpop, String out_path, boolean normalize, boolean negative) 
		throws FileNotFoundException, IOException {
		int ninput = inputpop, 
			noutput = outputpop;
    	double[] input, output;
    	String[] values;
    	double norm = Double.NEGATIVE_INFINITY;

		File dir = new File(dir_path);
		File[] files = dir.listFiles();
		
		// Each file is read and parsed
		for (File file : files) {
			
			if (!file.getName().contains(".csv")) {
				continue;
			}
			
	    	input = new double[ninput];
	    	output = new double[noutput];
	    	
	    	CSVReader reader = new CSVReader(new FileReader(file));
	    	
	    	reader.readNext();
	    	
	    	while ((values = reader.readNext())!= null) {
	    		for (int i = 0; i < ninput; i++) {
	    			input[i] = Double.parseDouble(values[i]);
	    			norm = Math.max(norm, Math.abs(input[i]));
	    		}
	    		
	    		for (int i = ninput; i < ninput + noutput; i++) {
	    			output[i - ninput] = Double.parseDouble(values[i]);
	    		}
	    		
	    		_pl.add(input, output);
	    	}
	    	
	    	reader.close();
		}
		
		// Serialize and write pattern list
		_pl.writer(new File(dir_path + "/" + out_path));
		
		// Normalize data
		if (normalize) {
			return normalize(_pl, norm, negative);
		}
		
		return _pl;
	}
	
	/**
	 * Normalize data
	 * @param pl pattern list
	 * @param norm norm value
	 * @param negative 
	 * @return true if data contain negative values / false either
	 */
	private PatternList normalize(PatternList pl, double norm, boolean negative) {
		for (int i = 0; i < pl.size(); i++) {
			double[] input = pl.get(i).getInput();
			for (int j = 0; j < input.length; j++) {
				if (negative) {
					input[j] = (input[j] + norm) / (2 * norm);
				}
				else {
					input[j] = input[j] / norm;
				}
			}
		}
		
		return pl;
	}
}
