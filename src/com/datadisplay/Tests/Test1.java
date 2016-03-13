package com.datadisplay.Tests;

import java.util.ArrayList;
import java.util.List;

import com.datadisplay.CartesianGraph;
import com.datadisplay.DataDisplay;

public class Test1 {

	public static void main(String[] args) {
		
		DataDisplay dd = new DataDisplay();
		CartesianGraph cg = dd.showCartesian();
		cg.setConnectPoints(true);
		for(int i=0; i<33;i++){
			cg.plot(i, primeFactors(i).size());
		}
		cg.showMean();
		cg.showStandardDeviation();
		cg.showLeastSquaresLine();
		
	}
	
	public static List<Integer> primeFactors(long number) { 
		List<Integer> primefactors = new ArrayList<>(); 
		long copyOfInput = number; 
		for (int i = 2; i <= copyOfInput; i++) { 
			if (copyOfInput % i == 0) { 
				primefactors.add(i); // prime factor 
				copyOfInput /= i; i--; 
			} 
		} 
		return primefactors; 
		
	}

}
