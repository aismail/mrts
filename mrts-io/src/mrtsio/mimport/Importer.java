package mrtsio.mimport;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import au.com.bytecode.opencsv.CSVReader;

import cassdb.interfaces.IHashClient;
import cassdb.internal.HashClient;

import mrtsio.dbconx.DataConnector;

/**
 * Static class importer - import training data set 
 * into on of:
 * 	1) Cassandra
 *  2) HDFS
 *  *) keyspace is defined in connector
 * 
 * @author cbarca
 */
public class Importer {
	public static final String SPLIT_TOKEN = "#";
	
	/**
	 * Import training data set to cassandra column family
	 * @param dir_path directory path
	 * @param colfam column family name
	 * @param row_size number of training vectors
	 * @throws IOException
	 */
	public static void importTrainingDataToCASSDB(String dir_path, 
			String colfam, int row_size) throws IOException {
		int row = 1, count = 0;
		String train_vector;
		String[] values;
    	
		DataConnector conx = new DataConnector(colfam);
		IHashClient hash = new HashClient(conx.getKeyspace());
		
		File dir = new File(dir_path);
		File[] files = dir.listFiles();
		
		hash.startBatchPut();
		
		// Each file is read and parsed
		for (File file : files) {
			
			if (!file.getName().contains(".csv")) {
				continue;
			}
			
	    	train_vector = new String();
	    	
	    	CSVReader reader = new CSVReader(new FileReader(file));
	    	
	    	reader.readNext();
	    	
	    	while ((values = reader.readNext())!= null) {
	    		if (count == 0) {
	    			hash.startBatchPut();
	    		}
	    		
	    		train_vector = "";
	    		for (int i = 0; i < values.length - 1; i++) {
	    			train_vector += values[i] + ",";
	    		}
	    		train_vector += values[values.length - 1];
	    		
	    		hash.batchPut(colfam, row, ++count, train_vector);
	    			    		
	    		if (count == row_size) {
	    			row++;
	    			count = 0;
	    			hash.finalizeBatchPut();
	    		}
	    	}
	    	
	    	reader.close();
		}

		if (count < row_size) {
			hash.finalizeBatchPut();
		}
	}
	
	/**
	 * Import training data set to cassandra column family
	 * @param dir_path directory path
	 * @param colfam column family name
	 * @param row_size number of training vectors
	 * @throws IOException
	 */
	public static void importTrainingDataToHDFS(String dir_path, 
			String hdfs_path, int row_size) throws IOException {
		int count = 0;
		String train_vector;
		String[] values;
    	
		File dir = new File(dir_path);
		File[] files = dir.listFiles();
				
		// Each file is read and parsed
		for (File file : files) {
			
			if (!file.getName().contains(".csv")) {
				continue;
			}
			
	    	train_vector = new String();
	    	
	    	CSVReader reader = new CSVReader(new FileReader(file));
	    	BufferedWriter writer = new BufferedWriter(new FileWriter("hdfs_" + file.getName()));
	    	
	    	reader.readNext();
	    	
	    	while ((values = reader.readNext()) != null) {
	    		train_vector = "";
	    		for (int i = 0; i < values.length - 1; i++) {
	    			train_vector += values[i] + ",";
	    		}
	    		train_vector += values[values.length - 1];
	    		count++;
	    			    		
	    		if (count == row_size) {
	    			count = 0;
	    			writer.write("\n");
	    			train_vector = "";
	    			continue;
	    		}
	    		
	    		train_vector += SPLIT_TOKEN;
	    		writer.write(train_vector);
	       	}
	    	
	    	if (count < row_size) {
	    		count = 0;
    			writer.write(train_vector);
	    	}
	    	
	    	reader.close();
	    	writer.close();
	    	
	    	/* import to hdfs (copy from local to hdfs)
	    	 * TODO
	    	 */
		}
	}
}
