package com.datadisplay;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;

public class DataDisplay {
	
	
	private JFrame frame;
	
	private final int BOXES;
	
	public DataDisplay(int boxes){
		BOXES = boxes;
		frame = new JFrame();
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Data Display");
		frame.setMinimumSize(new Dimension(400,400));
		if(boxes==2){
			frame.getContentPane().setLayout(new GridLayout(0,1));
		}else if(boxes>=3){
			frame.getContentPane().setLayout(new GridLayout(2,2));
		}
		
		frame.setResizable(true);
		frame.setVisible(true);
		
	}
	
	public DataDisplay(){
		this(1);
	}
	
	
	public CartesianGraph showCartesian(){
		CartesianGraph cg = new CartesianGraph();
		frame.getContentPane().add(cg);
		
		if(BOXES>1){
			cg.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		}
		
		frame.pack();
		return cg;
	}
	
	public BarChart showBarChart(){
		BarChart bc = new BarChart();
		frame.getContentPane().add(bc);
		
		if(BOXES>1){
			bc.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		}
		
		frame.pack();
		return bc;
	}
	
	public JFrame getFrame(){
		return frame;
	}
	
	
}
