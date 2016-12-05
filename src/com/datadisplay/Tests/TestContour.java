package com.datadisplay.Tests;

import com.datadisplay.ContourPlot;
import com.datadisplay.DataDisplay;
import com.datadisplay.function.MultivariateFunction;

public class TestContour {
	
	public static void main(String[] args) {
		
		DataDisplay dd = new DataDisplay();
		
		ContourPlot cp = dd.showContourPlot();
		
		MultivariateFunction mf = new MultivariateFunction(2, x -> {return x[0] + x[1]; }); 
		
		cp.setFunction(mf);
	}
	
}
