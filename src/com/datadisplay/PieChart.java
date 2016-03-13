package com.datadisplay;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public class PieChart extends JPanel{
	private static final long serialVersionUID = 1L;
	
	private int cushion = 20;
	
	private Color[] pie_colors = {Color.RED,Color.BLUE,Color.GREEN,Color.ORANGE,Color.PINK};
	private int color_inc = 0;
	
	private List<Double> percentages;
	
	public PieChart(){
		
		this.setBackground(Color.WHITE);
		
		percentages = new ArrayList<Double>();
		
	}
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		
		drawCircle(g2d);
		
	}
	
	private void drawCircle(Graphics2D g2d){
		int diameter = ( (this.getWidth()<this.getHeight()) ? (this.getWidth()-cushion*2) : (this.getHeight()-cushion*2) );
		
		int degree = 0;
		for(int i=0; i<percentages.size(); i++){
			g2d.setColor(pie_colors[color_inc++]);
			if(color_inc==pie_colors.length) color_inc=0;
			g2d.fillArc((this.getWidth()-diameter)/2, (this.getHeight()-diameter)/2, diameter, diameter, 
					degree, (int)(360*percentages.get(i)));
			degree += 360*percentages.get(i);
		}
		color_inc=0;
		
		g2d.setColor(Color.BLACK);
		g2d.drawOval((this.getWidth()-diameter)/2, (this.getHeight()-diameter)/2, diameter, diameter);
	}
	
	public void addPercentage(double percentage){
		percentages.add(percentage);
		if(MathUtilities.sum( percentages ) > 1){
			throw new IllegalArgumentException("sum of values should be <= 1");
		}
	}
	
}
