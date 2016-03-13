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
	
	public static double std_dev(double mean, List<Double> x){
		double sum=0;
		for(int i=0; i<x.size(); i++){
			sum += (x.get(i)-mean)*(x.get(i)-mean);
		}
		return Math.sqrt(sum/x.size());
	}
	
	public static double std_dev(List<Double> x){
		return std_dev(mean(x),x);
	}
	
	public static double leastSquaresSlope(double meanx, double[] x, double meany, double[] y){
		if(x.length != y.length) throw new IllegalArgumentException("Array X and Array Y must be of equal length");
		
		double sum_num = 0.0, sum_den = 0.0; // numerator, denominator
		for(int i=0; i<x.length; i++){
			sum_num += (x[i] - meanx) * (y[i] - meany);
			sum_den += (x[i] - meanx) * (x[i] - meanx);
		}
		return sum_num/sum_den;
	}
	
	public static double leastSquaresSlope(double[] x, double[] y){
		return leastSquaresSlope(mean(x),x,mean(y),y);
	}
	
	public static double leastSquaresSlope(double meanx, List<Double> x, double meany, List<Double> y){
		if(x.size() != y.size()) throw new IllegalArgumentException("List X and List Y must be of equal length");
		
		double sum_num = 0.0, sum_den = 0.0;
		for(int i=0; i<x.size(); i++){
			sum_num += (x.get(i) - meanx) * (y.get(i) - meany);
			sum_den += (x.get(i) - meanx) * (x.get(i) - meanx);
		}
		return sum_num/sum_den;	
	}
	
	public static double leastSquaresSlope(List<Double> x, List<Double> y){
		return leastSquaresSlope(mean(x),x,mean(y),y);
	}
	
	public static double round(double x, int prec){
		double precm = Math.pow(10, prec);
		return (double) Math.round(x*precm)/precm;
	}
}
