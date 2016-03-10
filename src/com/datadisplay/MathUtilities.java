package com.datadisplay;

public class MathUtilities {
	
	private MathUtilities(){}
	
	public static double sum(int... x){
		int sum = 0;
		for(int i : x){
			sum += i;
		}
		return sum;
	}
	
	public static double mean(int... x){
		return sum(x)/x.length;
	}
	
}
