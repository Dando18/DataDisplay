package com.datadisplay;

import java.util.Iterator;
import java.util.List;

public class MathUtilities {
	
	private MathUtilities(){}
	
	public static double sum(double... x){
		double sum = 0;
		for(double i : x){
			sum += i;
		}
		return sum;
	}
	
	public static double sum(List<Double> x){
		Iterator<Double> iter = x.iterator();
		double sum = 0; 
		while(iter.hasNext()){
			sum += (double) iter.next();
		}
		return sum;
	}
	
	public static double mean(double... x){
		return sum(x)/x.length;
	}
	
	public static double mean(List<Double> x){
		return sum(x)/x.size();
	}
	
	public static double std_dev(double mean, double... x){
		double sum=0;
		for(int i=0; i<x.length; i++){
			sum += (x[i]-mean)*(x[i]-mean);
		}
		return Math.sqrt( sum/x.length );
	}
	
	public static double std_dev(double... x){
		return std_dev(mean(x),x);
	}
}
