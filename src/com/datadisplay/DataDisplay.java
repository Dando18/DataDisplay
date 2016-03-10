package com.datadisplay;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;

public class DataDisplay {
	
	
	private JFrame frame;
	
	public DataDisplay(){
		
		frame = new JFrame();
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Data Display");
		frame.setMinimumSize(new Dimension(400,400));
		
		frame.setResizable(true);
		frame.setVisible(true);
		
	}
	
	
	public CartesianGraph showCartesian(){
		CartesianGraph cg = new CartesianGraph();
		frame.getContentPane().add(cg, BorderLayout.CENTER);
		frame.pack();
		return cg;
	}
	
	public JFrame getFrame(){
		return frame;
	}
	
	
}
