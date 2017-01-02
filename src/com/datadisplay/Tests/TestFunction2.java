package com.datadisplay.Tests;

import com.datadisplay.CartesianGraph;
import com.datadisplay.DataDisplay;

public class TestFunction2 {
	
	public static void main(String[] args){
		
		DataDisplay dd = new DataDisplay();
		CartesianGraph cg = dd.showCartesian();
		cg.plot(x -> x*Math.cos(x));
		
	}
	
	
}
