package mrtsis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Cristi.Barca
 * To CSV format parser class.	
 * Parses a particular format-file (as you define it) into CSV format.
 */
public class ToCSVParser {
	
	/**
	 * Parses the srcFile into a CSV destFile.
	 * The way that the srcFile is parsed is properly defined in _code_.
	 * @param srcFile = source file (any format file)
	 * @param destFile = destination file (CSV format file)
	 */
	public static void parse(String srcFile, String destFile) {
		
		// Nothing to do here ...vuuuuum
		try {
		
			BufferedReader input = new BufferedReader(new FileReader(srcFile));
			BufferedWriter output = new BufferedWriter(new FileWriter(destFile));
			String line;
			String[] elems;
			int i;
			
			line = input.readLine();
			
			if (line == null) {
				input.close();
				output.close();
				
				return;
			}
			
			elems = line.split("\t");
			
			for (i = 0; i < elems.length - 1; i++) {
				output.write(elems[i] + ",");
			}
			output.write(elems[elems.length - 1] + "\n");
			
			line = input.readLine();
			
			while (line != null) {
				elems = line.split("\t");
				
				output.write(elems[0].replace("[", "").replace("]", "") + ",");
				for (i = 1; i < elems.length - 1; i++) {
					output.write(elems[i] + ",");
				}
				if (elems.length > 1) {
					output.write(elems[elems.length - 1]);
				}
				output.write("\n");
				
				line = input.readLine();
			}
			
			input.close();
			output.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
