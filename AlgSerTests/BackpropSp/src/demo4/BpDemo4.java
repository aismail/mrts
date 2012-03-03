package demo4;

import java.io.File;
import java.io.IOException; 
import java.io.FileNotFoundException;

import network.BackProp;


/**
 * Demonstrate learning of the equation y = -5x-2.
 * This is a 2 input, 3 middle, 1 output network which is 
 * trained to discover if a point is above or below a line.
 *
 * @author G.S. Cole (gsc@digiburo.com)
 * @version $Id: BpDemo1.java,v 1.5 2002/02/03 20:31:41 gsc Exp $
 */

/*
 * Development Environment:
 *   Linux 2.2.14-5.0 (Red Hat 6.2)
 *   Java Developers Kit 1.3.1
 *
 * Legalise:  
 *   Copyright (C) 2002 Digital Burro, INC.
 *
 * Maintenance History:
 *   $Log: BpDemo1.java,v $
 *   Revision 1.5  2002/02/03 20:31:41  gsc
 *   Format tweaks
 *
 *   Revision 1.4  2002/02/02 08:27:27  gsc
 *   Work In Progress
 *
 *   Revision 1.3  2002/02/01 05:09:59  gsc
 *   Tweaks from Unit Testing
 *
 *   Revision 1.2  2002/02/01 02:48:08  gsc
 *   Work In Progress
 *
 *   Revision 1.1  2002/01/22 08:19:35  gsc
 *   Initial Check In
 */

public class BpDemo4 extends BackProp {

    /**
     * Constructor for new backpropagation network.
     *
     * @param input_population input node count
     * @param middle_population middle node count
     * @param output_population output node count
     * @param learning_rate 
     * @param momentum
     */
    public BpDemo4(int input_population, int middle_population, int output_population, double learning_rate, double momentum) {
    	super(input_population, middle_population, output_population, learning_rate, momentum);
    }

    /**
     * Constructor for existing backpropagation network.
     *
     * @param file serialized Network memento
     */
    public BpDemo4(File file) throws IOException, FileNotFoundException, ClassNotFoundException {
    	super(file);
    }

	public int classifier(double[] input) {
		double[] output = runNetwork(input);

		if (output[0] > 0.9) {
			return (1);
		} else if (output[0] < 0.1) {
			return (-1);
		}

		return(0);
	}
}
