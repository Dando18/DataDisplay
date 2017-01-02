package com.datadisplay.Tests;

import java.util.Random;

import com.datadisplay.DataDisplay;
import com.datadisplay.PolarGraph;

public class Test4 {
	
	public static void main(String[] args){
		
		@SuppressWarnings("unused")
		Random rand = new Random(System.nanoTime());
		
		DataDisplay dd = new DataDisplay();
		
		PolarGraph pg = dd.showPolarGraph();
		
		pg.plot(theta -> 15*Math.cos(7*theta), 50.0);
		
	}
	
	
}
