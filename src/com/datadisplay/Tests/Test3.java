package com.datadisplay.Tests;

import com.datadisplay.BarChart;
import com.datadisplay.DataDisplay;

public class Test3 {
	
	public static void main(String[] args){
		DataDisplay dd = new DataDisplay();
		BarChart bc = dd.showBarChart();
		bc.addValue(2);
		bc.addValue(4);
		bc.addValue(3);
		bc.addValue(1);
		bc.addValue(7);
		bc.showBarValues();
	}
	
}
