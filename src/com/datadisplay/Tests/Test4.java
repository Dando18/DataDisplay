package com.datadisplay.Tests;

import java.util.Random;

import com.datadisplay.DataDisplay;
import com.datadisplay.PolarGraph;

public class Test4 {
	
	public static void main(String[] args){
		
		//@SuppressWarnings("unused")
		Random rand = new Random(System.nanoTime());
		
		DataDisplay dd = new DataDisplay();
		
		PolarGraph pg = dd.showPolarGraph();
		pg.setConnectPoints(true);
		pg.scale = 1;
		
		for(double i=0; i<10; i+=0.1){
			pg.plot((rand.nextInt((int)pg.getMaxR())-rand.nextDouble())%pg.getMaxR(), i);
		}
		
	}
	
	
}
