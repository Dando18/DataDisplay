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
		pg.setConnectPoints(true);
		pg.scale = 1;
		
		for(double i=0; i<600; i+=0.1){
			pg.plot(0.25*i, i);
		}
		
	}
	
	
}
