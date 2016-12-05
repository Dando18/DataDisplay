package com.datadisplay.function;

public class MultivariateFunction {

	MultivariateFunctionInterface mfi;
	
	public int variables;
	
	public MultivariateFunction(int variables, MultivariateFunctionInterface mfi) {
		this.mfi = mfi;
		this.variables = variables;
	}
	
	public double evaluate(double... x) {
		if(x.length != variables){
			return 0.0;
		}
		return mfi.f(x);
	}
	
	
	
}
