package com.datadisplay.Tests;

import java.util.ArrayList;

import com.datadisplay.CartesianGraph;
import com.datadisplay.DataDisplay;
import com.datadisplay.function.Function;
import com.datadisplay.function.FunctionInterface;

public class TestFunction {
	
	public static void main(String[] args){
		
		DataDisplay dd = new DataDisplay();
		
		CartesianGraph cg = dd.showCartesian();
		
		Function f = new Function(new FunctionInterface(){
			@Override
			public double f(double x) {
				return 1.0/Math.cos(x);
			}
		});
		
		cg.plot(f,15);
		ArrayList<ArrayList<Double>> ddx = f.ddx(-cg.getMaxX(), cg.getMaxX(), 0.1);
		cg.plot(ddx.get(0),ddx.get(1));
		cg.setEditable(false);
	}
	
}
