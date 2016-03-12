package com.datadisplay.Tests;

import java.util.Random;

import com.datadisplay.BarChart;
import com.datadisplay.CartesianGraph;
import com.datadisplay.DataDisplay;

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
		for(int i=-4; i<5; i+=1){
			cg0.plot(i, rand.nextInt(5));
		}
		cg0.showMean();
		cg0.showStandardDeviation();
		
		BarChart bc0 = dd.showBarChart();
		bc0.showBarValues();
		for(int i=0; i<5; i++){
			bc0.addValue(rand.nextInt(5)+1);
		}
		
		BarChart bc1 = dd.showBarChart();
		bc1.showBarValues();
		int bc1_max = rand.nextInt(8-3)+3;
		for(int i=0; i<bc1_max; i++){
			bc1.addValue(rand.nextInt(5)+1);
		}
		
		CartesianGraph cg1 = dd.showCartesian();
		cg1.setConnectPoints(true);
		for(int i=-4; i<5; i+=1){
			cg1.plot(i, rand.nextInt(3)* ((rand.nextBoolean()) ? 1 : -1));
		}
		cg1.showMean();
		cg1.showStandardDeviation();
		
		
		
		
	}
	
}
