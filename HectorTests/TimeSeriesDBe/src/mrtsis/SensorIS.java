package mrtsis;

import java.io.FileReader;
import java.io.IOException;

import au.com.bytecode.opencsv.CSVReader;


/**
 * @author Cristi.Barca
 * 
 * Sensor Input Stream class. 
 * Gets data from a source file with time-series data.
 * Dependencies: opencsv
 * 
 */
public class SensorIS {
	// Constants
	public static final String DEFAULT_SOURCE = "sensor.stream.csv";
	public static final int DPS = 8; // data-per-seconds
	public static final int DPM = DPS * 60; // data-per-minute;
		
	// Private members
	private String sourceStream = null;
	private CSVReader input = null;
	private String[] headNames = null;

	/**
	 * Implicit constructor.
	 * Initializes the sourceStream with _default_ value.
	 */
	public SensorIS() {
		this(DEFAULT_SOURCE);
	}
	
	/**
	 * Specialized constructor.
	 * @param source = filename of the source CSV-format file
	 */
	public SensorIS(String source) {
		sourceStream = source;
	}
	
	/**
	 * Opens the sourceStream using CSV Reader.
	 * @throws IOException
	 */
	public void open() throws IOException {
		input = new CSVReader(new FileReader(sourceStream));	
	}
	
	/**
	 * Reads the first line of the CSV file.
	 * Gets the name of each column.
	 * @throws IOException
	 */
	public void readHeadline() throws IOException {
		if (input == null) {
			this.open();
		} 
		
		headNames = input.readNext();
		
		if (headNames == null) {
			this.close();
			
			/* On null line return a null
			 * object upwards. 
			 */
			return;
		}
	}
	
	/**
	 * Reads a "package" (a line) from the "stream" (ordinary CSV file).
	 * @return a SensorPkg object containing the timestamp (formated) and 
	 * a SensorData (which has a hasmap with keys-values).
	 * @throws IOException
	 */
	public SensorPkg getPkg() throws IOException {
		String[] values = null;
		SensorPkg spkg = null;
			
		values = input.readNext();
		
		if (values == null) {
			this.close();
			
			/* On null line return a null
			 * object upwards. 
			 */
			return null;
		}
		
		spkg = new SensorPkg(headNames, values);
				
		return spkg;
	}
	
	/**
	 * Closes the sourceStream.
	 * @throws IOException
	 */
	public void close() throws IOException {
		input.close();
	}
}
