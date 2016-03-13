package com.datadisplay.Tests;

import java.util.Random;

import com.datadisplay.BarChart;
import com.datadisplay.CartesianGraph;
import com.datadisplay.DataDisplay;
import com.datadisplay.PieChart;

public class Test2 {
	
	/*
	 * This class is an example of a multi-window frame.
	 * Pass in the number of panels you want as a parameter to the constructor
	 * and add the panels.
	 */
	
	public static void main(String[] args){
		
		Random rand = new Random(System.nanoTime());
		
		// the frame will split into 4 panels
		DataDisplay dd = new DataDisplay(4);
		
		CartesianGraph cg0 = dd.showCartesian();
		cg0.setConnectPoints(true);
		int graph_max = 16;
		for(int i=-graph_max; i<graph_max+1; i+=1){
			cg0.plot(i, rand.nextInt(5));
		}
		cg0.showMean();
		cg0.showStandardDeviation();
		
		BarChart bc0 = dd.showBarChart();
		bc0.showBarValues();
		for(int i=0; i<5; i++){
			bc0.addValue(rand.nextInt(5)+1);
		}
		
		PieChart pc0 = dd.showPieChart();
		double sum = 0;
		while(sum<=1){
			double val = rand.nextDouble()%0.5;
			sum += val;
			if(sum>1) break;
			pc0.addPercentage(val);
		}
		
		CartesianGraph cg1 = dd.showCartesian();
		cg1.setConnectPoints(true);
		for(int i=-graph_max; i<graph_max+1; i+=1){
			cg1.plot(i, rand.nextGaussian() *4* ((rand.nextBoolean()) ? 1 : 1));
		}
		cg1.showMean();
		cg1.showStandardDeviation();
		
		
		
		
	}
	
}
