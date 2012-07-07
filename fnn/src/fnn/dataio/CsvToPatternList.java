package fnn.dataio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

/**
 * CSV to pattern (internal) converter
 *  
 * @author cbarca
 */
public class CsvToPatternList {
	// Private members
	private PatternList _pl;
	
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
	 * @param out_filename output path (for .trn serialized file)
	 * @param normalize true if normalize / false either
	 * @return pattern list
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public PatternList convert(String dir_path, int inputpop, 
		int outputpop, String out_filename, boolean normalize) 
		throws FileNotFoundException, IOException {
		return convert(dir_path, inputpop, outputpop, out_filename, normalize, false);
	}
	
	/**
	 * Read CSV files from a directory and convert/copy them in a pattern list
	 * @param dir_path directory path
	 * @param inputpop number of data used for network's input
	 * @param outputpop number of data used for network's output
	 * @param out_filename output filename (for .trn serialized file)
	 * @param normalize true if normalize / false either
	 * @param save true if we want to save the data to CSV (usually after normalization)
	 * @return the conversion into pattern list 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public PatternList convert(String dir_path, int inputpop, 
		int outputpop, String out_filename, boolean normalize, boolean save) 
		throws FileNotFoundException, IOException {
		int ninput = inputpop, noutput = outputpop;
    	double[] input, output;
    	String[] values;
    	
		File dir = new File(dir_path);
		File[] files = dir.listFiles();
		
		double[] norm = new double[ninput];
		boolean[] negative = new boolean[ninput];
    	for (int i = 0; i < ninput; i++) {
    		norm[i] = Double.NEGATIVE_INFINITY;
    		negative[i] = false;
    	}
		
		// Each file is read and parsed
		for (File file : files) {
			if (!file.getName().contains(".csv")) {
				continue;
			}
			
			System.out.println("File: " + file.getName());
			
	    	input = new double[ninput];
	    	output = new double[noutput];
	    	
	    	CSVReader reader = new CSVReader(new FileReader(file));
	    	
	    	reader.readNext();
	    	
	    	while ((values = reader.readNext()) != null) {
	    		if (values.length < ninput) {
	    			continue;
	    		}
	    		
	    		for (int i = 0; i < ninput; i++) {
	    			input[i] = Double.parseDouble(values[i]);
	    			norm[i] = Math.max(norm[i], Math.abs(input[i]));
	    			
	    			if (input[i] < 0 && !negative[i]) {
	    				negative[i] = true;
	    			}
	    		}
	    		
	    		for (int i = ninput; i < values.length; i++) {
	    			output[i - ninput] = Double.parseDouble(values[i]);
	    		}
	    		
	    		_pl.add(input, output);
	    	}
	    	
	    	reader.close();
		}
		
		// Serialize and write pattern list
		_pl.writer(new File(dir_path + "/" + out_filename));
		
		// Normalize data
		if (normalize) {
			_pl = CsvToPatternList.normalize(_pl, norm, negative);
			
			if (save) {
				CsvToPatternList.saveToCSV(_pl, dir_path, out_filename + ".csv");
			}
		}
		
		return _pl;
	}
	
	/**
	 * Normalize data
	 * @param pl pattern list
	 * @param norm norm value 
	 * @param negative list (flags for columns which contain negative columns)
	 * @return normalized pattern list
	 */
	public static PatternList normalize(PatternList pl, double[] norm, boolean[] negative) {
		for (int i = 0; i < pl.size(); i++) {
			double[] input = pl.get(i).getInput();
			
			for (int j = 0; j < input.length; j++) {
				if (norm[j] == 0) {
					norm[j] = 1; // for DivideByZero operation
				}
				
				if (negative[j]) {
					input[j] = (input[j] + norm[j]) / (2 * norm[j]);
				}
				else {
					input[j] = input[j] / norm[j];
				}
			}
		}
		
		return pl;
	}
	
	/**
	 * Save pattern list to CSV file
	 * @param pl pattern list
	 * @param dir_path directory path
	 * @param out_filename
	 * @throws IOException
	 */
	public static void saveToCSV(PatternList pl, String dir_path, String out_filename) 
		throws IOException {
		String[] vector;
		double[] input, output;
		CSVWriter writer = new CSVWriter(new FileWriter(new File(dir_path + "/" + out_filename)));
		
		vector = new String[1];
		vector[0] = Integer.toString(pl.size());
		
		writer.writeNext(vector);
		
		for (int i = 0; i < pl.size(); i++) {
			input = pl.get(i).getInput(); 
			output = pl.get(i).getOutput();
			vector = new String[input.length + output.length];
			
			for (int j = 0; j < input.length; j++) {
				vector[j] = Double.toString(input[j]);
			}
			
			for (int j = 0; j < output.length; j++) {
				vector[input.length + j] = Double.toString(output[j]);
			}
			
			writer.writeNext(vector); 
		}
		
		writer.close();
	}
}
