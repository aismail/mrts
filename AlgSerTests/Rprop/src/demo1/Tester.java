package demo1;

import java.io.File;
import java.io.IOException; 
import java.io.FileNotFoundException;

import java.io.FileWriter;
import java.io.BufferedWriter;

/**
 * Test the demo1 network by submitting points and obtaining results.
 *
 * @author G.S. Cole (gsc@digiburo.com)
 * @version $Id: Tester.java,v 1.4 2002/02/02 08:27:27 gsc Exp $
 */

public class Tester {
    private static final String NEG_FILENAME = "negative.txt";
    private static final String POS_FILENAME = "positive.txt";
    private static final String NETWORK_FILENAME = "demo1.serial";

    private BpDemo1 bp;

    /**
     * Create network
     */
    public Tester(File network) throws IOException, FileNotFoundException, ClassNotFoundException {
    	bp = new BpDemo1(network);
    }

	/**
	 * Generate a 2D matrix from -1 to 1 at 0.1 intervals. 
	 * Submit these points to the network for classification.
	 */
	public void performTesting() throws Exception {
		// write positive answers

		BufferedWriter bw = new BufferedWriter(new FileWriter(POS_FILENAME));

		for (double xx = 0.0; xx <= 1.0; xx += 0.1) {
			for (double yy = 0.0; yy <= 1.0; yy += 0.1) {
				if (bp.classifier(xx, yy) > 0) {
					double y = -5 * xx + 2;
					System.out.println(y < yy);
					writeData(bw, xx, yy, 1);
				}
			}
		}

		bw.close();

		// write negative answers

		bw = new BufferedWriter(new FileWriter(NEG_FILENAME));

		for (double xx = 0.0; xx <= 1.0; xx += 0.1) {
			for (double yy = 0.0; yy <= 1.0; yy += 0.1) {
				if (bp.classifier(xx, yy) < 0) {
					double y = -5 * xx + 2;
					System.out.println(y < yy);
					writeData(bw, xx, yy, -1);
				}
			}
		}

		bw.close();
	}

	/**
     *
     */
	public void writeData(BufferedWriter bw, double xx, double yy, int flag) throws Exception {
		bw.write(Double.toString(xx) + " ");
		bw.write(Double.toString(yy) + " ");
		bw.write(Integer.toString(flag));
		bw.newLine();
	}

	/**
	 * Driver
	 */
	public static void main(String args[]) throws Exception {
		System.out.println("begin");

		Tester tr = null;

		if (args.length != 1) {
			tr = new Tester(new File(NETWORK_FILENAME));
		} else {
			tr = new Tester(new File(args[0]));
		}

		tr.performTesting();

		System.out.println("end");
	}
}
