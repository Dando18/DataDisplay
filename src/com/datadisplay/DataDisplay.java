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
		frame.setPreferredSize(new Dimension(800,600));
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
	
	public PieChart showPieChart(){
		PieChart pc = new PieChart();
		frame.getContentPane().add(pc);
		
		if(BOXES>1){
			pc.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		}
		
		frame.pack();
		return pc;
	}
	
	public PolarGraph showPolarGraph(){
		PolarGraph pg = new PolarGraph();
		frame.getContentPane().add(pg);
		
		if(BOXES>1){
			pg.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		}
		
		frame.pack();
		return pg;
	}
	
	public ImageView showImageView(){
		ImageView iv = new ImageView();
		frame.getContentPane().add(iv);
		
		if(BOXES>1){
			iv.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		}
		
		frame.pack();
		return iv;
	}
	
	public BoxPlot showBoxPlot(){
		BoxPlot bawp = new BoxPlot();
		frame.getContentPane().add(bawp);
		
		if(BOXES>1){
			bawp.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		}
		
		frame.pack();
		return bawp;
	}
	
	public ContourPlot showContourPlot() {
		ContourPlot cp = new ContourPlot();
		frame.getContentPane().add(cp);
		
		if(BOXES>1){
			cp.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		}
		
		frame.pack();
		return cp;
	}
	
	public JFrame getFrame(){
		return frame;
	}
	
	
}
