package com.datadisplay;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public class PieChart extends JPanel{
	private static final long serialVersionUID = 1L;
	
	private int cushion = 20;
	private int key_square = 10;
	
	private Color[] pie_colors = {Color.RED,Color.BLUE,Color.GREEN,Color.ORANGE,Color.PINK,Color.MAGENTA};
	private int color_inc = 0;
	
	private List<Double> percentages;
	private List<String> labels;
	private int precision=2;
	private String title = "";
	
	public PieChart(){
		
		this.setBackground(Color.WHITE);
		
		percentages = new ArrayList<Double>();
		labels = new ArrayList<String>();
		
	}
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		drawCircle(g2d);
		
	}
	
	private void drawCircle(Graphics2D g2d){
		int diameter = ( (this.getWidth()<this.getHeight()) ? (this.getWidth()-cushion*2) : (this.getHeight()-cushion*2) );
		FontMetrics fm = g2d.getFontMetrics();
		
		int degree = 0;
		for(int i=0; i<percentages.size(); i++){
			g2d.setColor(pie_colors[color_inc++]);
			if(color_inc==pie_colors.length) color_inc=0;
			g2d.fillArc((this.getWidth()-diameter)/2, (this.getHeight()-diameter)/2, diameter, diameter, 
					degree, (int)(360*percentages.get(i)));
			degree += 360*percentages.get(i);
			g2d.fillRect(this.getWidth()/2+diameter/2+cushion, (this.getHeight()/2-diameter/2)+i*fm.getHeight()+cushion/2, 
					key_square, key_square);
			g2d.drawString(labels.get(i)+" - "+MathUtilities.round(percentages.get(i)*100.0, precision)+"%", 
					this.getWidth()/2+diameter/2+cushion*2, (this.getHeight()/2-diameter/2)+i*fm.getHeight()+cushion/2+key_square-1);
		}
		color_inc=0;
		
		g2d.setColor(Color.BLACK);
		g2d.drawOval((this.getWidth()-diameter)/2, (this.getHeight()-diameter)/2, diameter, diameter);
		
		g2d.drawString(title, cushion/2, cushion/2+fm.getHeight());
	}
	
	public void addValue(String label, double percentage){
		labels.add(label);
		percentages.add(percentage);
		if(MathUtilities.sum( percentages ) > 1){
			throw new IllegalArgumentException("sum of values should be <= 1");
		}
	}
	
	public void addValue(double percentage){
		addValue("",percentage);
	}
	
	public void setPrecision(int precision){
		this.precision = precision;
	}
	
	public int getPrecision(){
		return precision;
	}
	
	public void setTitle(String title){
		this.title = title;
	}
	
	public String getTitle(){
		return title;
	}
}
