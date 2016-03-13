package com.datadisplay;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public class BarChart extends JPanel{
	private static final long serialVersionUID = 1L;

	private int cushion = 20;
	private int origin_x = cushion;
	private int origin_y = this.getHeight()-cushion;
	private int ppp = 20;
	
	private List<Integer> bar_values;
	private boolean showBarValues = false;
	private String title = "";
	
	private Color[] bar_colors = {Color.RED,Color.BLUE,Color.GREEN,Color.ORANGE,Color.MAGENTA,Color.PINK};
	private int color_inc = 0;
	
	public BarChart(){
		
		this.setBackground(Color.LIGHT_GRAY);
		
		bar_values = new ArrayList<Integer>();
		
	}
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		origin_y = this.getHeight()-cushion;
		
		drawAxes(g2d);
		if(bar_values.size()<=0) return;
		int bar_width = (this.getWidth()-origin_x*2)/bar_values.size();
		for(int i=0; i<bar_values.size(); i++){
			g2d.setColor(bar_colors[color_inc++]);
			if(color_inc==bar_colors.length) color_inc=0;
			int bar_height = ppp*bar_values.get(i);
			g2d.fillRect( origin_x+(i*bar_width), origin_y-bar_height, bar_width, ppp*bar_values.get(i));
			g2d.setColor(Color.BLACK);
			g2d.drawRect( origin_x+(i*bar_width), origin_y-bar_height, bar_width, ppp*bar_values.get(i));
			
			if(showBarValues){
				g2d.setColor(Color.WHITE);
				g2d.drawString(""+bar_values.get(i), origin_x+(i*bar_width)+bar_width/2-5, origin_y-ppp*bar_values.get(i)/2+5);
			}
		}
		color_inc=0; //comment out this line for resize color strobing
		
		g2d.setColor(Color.WHITE);
		FontMetrics fm = g2d.getFontMetrics();
		g2d.drawString(title, this.getWidth()/2-fm.stringWidth(title)/2, cushion/4+fm.getHeight());
	}
	
	public void addValue(int x){
		bar_values.add(x);
	}
	
	private void drawAxes(Graphics2D g2d){
		g2d.setColor(Color.BLACK);
		g2d.drawLine(origin_x, origin_y, origin_x, cushion);
		g2d.drawLine(origin_x, origin_y, this.getWidth()-cushion, origin_y);
		g2d.drawString(""+ (double)Math.round((origin_y-cushion*2)/ppp*100d)/100d, origin_x-15, cushion-2);
	}
	
	public void setTitle(String title){
		this.title = title;
	}
	
	public String getTitle(){
		return title;
	}
	
	public void showBarValues(){
		showBarValues = true;
	}
	
	public void hideBarValues(){
		showBarValues = false;
	}
	
}
