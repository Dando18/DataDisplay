package com.datadisplay.Tests;

import com.datadisplay.BarChart;
import com.datadisplay.BoxPlot;
import com.datadisplay.ContourPlot;
import com.datadisplay.DataDisplay;
import com.datadisplay.PieChart;

public class TestBlog {

	public static void main(String[] args){
		
		DataDisplay dd = new DataDisplay(4);
		BarChart bc = dd.showBarChart();
		for(int i=0; i<10; i++){
			bc.addValue( (int)(Math.random()*10+1), "box_"+i );
		}
		
		BoxPlot bp = dd.showBoxPlot();
		bp.addValues(0,1,1,2,3,5,8,13,21,34,55);
		bp.addValues(2,3,5,7,11,13,17,19,23,27);
		
		PieChart pc = dd.showPieChart();
		pc.addValue(0.3, "one third");
		pc.addValue(0.1, "one tenth");
		pc.addValue(0.6, "one sixth");
		
		ContourPlot cp = dd.showContourPlot();
		cp.plot(x -> x[0]*x[0] + x[1]*x[1]);
		
	}
	
	
}
