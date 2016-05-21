package com.datadisplay.function;

import java.util.ArrayList;

public class Function {

	FunctionInterface fi;
	
	public Function(FunctionInterface fi){
		this.fi = fi;
	}
	
	public double evaluate(double x){
		return fi.f(x);
	}
	
	public ArrayList<ArrayList<Double>> ddx(double a, double b, double step){
		// (f(b)-f(a))/(b-a)
		
		if(a>b) {
			double temp = a;
			b=a;
			a=temp;
		}
		
		ArrayList<Double> valx = new ArrayList<Double>();
		ArrayList<Double> valy = new ArrayList<Double>();
		
		for(double i=a+step; i<b-step; i+=step){
			valx.add(i);
			valy.add((fi.f(i+step)-fi.f(i-step))/(2*step));
		}
		
		ArrayList<ArrayList<Double>> l = new ArrayList<ArrayList<Double>>();
		l.add(valx);
		l.add(valy);
		
		return l;
	}
	
	
}
