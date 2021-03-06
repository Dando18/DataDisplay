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
		cg.setMaxX(10000);
		for(int i=0; i<16000;i++){
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
