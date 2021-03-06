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
		cg0.setConnectPoints(false);
		int graph_max = 16;
//		for(double i=-graph_max; i<graph_max+1; i+=0.5){
//			cg0.plot(i, (rand.nextInt(2)+rand.nextDouble())*i);
//		}
		cg0.x_scale = 4;
		cg0.y_scale = 3;
		for(double i=-32; i<32; i+=0.5){
			cg0.plot(i, i+Math.sin(i));
		}
		cg0.showMean();
		cg0.showStandardDeviation();
		cg0.showLeastSquaresLine();
		
		BarChart bc0 = dd.showBarChart();
		bc0.showBarValues();
		bc0.setTitle("Bar Chart");
		bc0.showBarTitles();
		int numbars = rand.nextInt(7)+5;
		for(int i=0; i<numbars; i++){
			bc0.addValue(rand.nextInt(10)+1, "p"+i);
		}
		
		PieChart pc0 = dd.showPieChart();
		pc0.setTitle("Pie Chart");
		double sum = 0;
		int s=0;
		while(sum<=1){
			double val = rand.nextDouble()%0.35;
			sum += val;
			if(sum>1) break;
			pc0.addValue(val,"p"+s++);
		}
		
		CartesianGraph cg1 = dd.showCartesian();
		cg1.setConnectPoints(true);
		for(int i=-graph_max; i<graph_max+1; i+=1){
			cg1.plot(i, rand.nextGaussian()*3);
		}
		cg1.interpolatePoints();
		cg1.showEquation();
		cg1.showLabels();
		cg1.showGrid();
		
		//bc0.animate(2, 3, 500, 2000);
		//cg1.animate(graph_max*2-5, 0, 0, 4000, 2000);

		
	}
	
}
