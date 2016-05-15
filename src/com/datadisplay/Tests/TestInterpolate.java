package com.datadisplay.Tests;

import java.util.Random;

import com.datadisplay.CartesianGraph;
import com.datadisplay.DataDisplay;

public class TestInterpolate {
	
	public static void main(String[] args){
		
		DataDisplay dd = new DataDisplay();
		
		CartesianGraph cg = dd.showCartesian();
		cg.setMaxX(30);
		Random r = new Random(System.nanoTime());
		int max = (int) cg.getMaxX();
		for(int i=-max; i<max; i+=2){
			cg.plot(i, r.nextDouble()*i);
		}
		cg.setConnectPoints(true);
		cg.interpolatePoints();
		cg.showLabels();
		cg.showEquation();
		cg.showGrid();
		
	}
	
}
