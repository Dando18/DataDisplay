package com.datadisplay.Tests;

import com.datadisplay.CartesianGraph;
import com.datadisplay.DataDisplay;

public class Test2 {
	
	/*
	 * This class is an example of a multi-window frame.
	 * Pass in the number of panels you want as a parameter to the constructor
	 * and add the panels.
	 */
	
	@SuppressWarnings("unused")
	public static void main(String[] args){
		
		// the frame will split into 4 panels
		DataDisplay dd = new DataDisplay(4);
		
		CartesianGraph cg0 = dd.showCartesian();
		CartesianGraph cg1 = dd.showCartesian();
		CartesianGraph cg2 = dd.showCartesian();
		CartesianGraph cg3 = dd.showCartesian();
	}
	
}
