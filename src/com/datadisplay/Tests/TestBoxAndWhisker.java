package com.datadisplay.Tests;

import java.util.ArrayList;

import com.datadisplay.BoxPlot;
import com.datadisplay.DataDisplay;

public class TestBoxAndWhisker {
	
public static void main(String[] args) {
		
		DataDisplay dd = new DataDisplay();
		BoxPlot bawp = dd.showBoxPlot();
		
		ArrayList<Double> p1 = new ArrayList<Double>();
		p1.add(1.0);
		p1.add(3.0);
		p1.add(5.0);
		p1.add(7.0);
		p1.add(11.0);
		bawp.addPlot(p1);
		
		bawp.addValues(1.0, 3.0, 5.0, 34.0, 8.0, 12.0);
		
		ArrayList<Double> p2 = new ArrayList<Double>();
		for(int i=0; i<50; i++){
			p2.add((Math.random()*50));
		}
		bawp.addPlot(p2);
	}
	
}
