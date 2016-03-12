package com.datadisplay;

public class MathUtilities {
	
	private MathUtilities(){}
	
	public static double sum(double... x){
		double sum = 0;
		for(double i : x){
			sum += i;
		}
		return sum;
	}
	
	public static double mean(double... x){
		return sum(x)/x.length;
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
