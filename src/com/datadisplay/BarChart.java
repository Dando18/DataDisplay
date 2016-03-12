package com.datadisplay;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public class BarChart extends JPanel{
	private static final long serialVersionUID = 1L;

	private int origin_x = 20;
	private int origin_y = this.getHeight()-20;
	private int ppp = 20;
	
	List<Integer> bar_values;
	
	private Color[] bar_colors = {Color.RED,Color.BLUE,Color.GREEN,Color.ORANGE,Color.PINK};
	private int color_inc = 0;
	
	public BarChart(){
		
		this.setBackground(Color.LIGHT_GRAY);
		
		bar_values = new ArrayList<Integer>();
		
	}
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		origin_y = this.getHeight()-20;
		if(bar_values.size()<=0) return;
		int bar_width = (this.getWidth()-origin_x*2)/bar_values.size();
		for(int i=0; i<bar_values.size(); i++){
			g.setColor(bar_colors[color_inc++]);
			if(color_inc==bar_colors.length) color_inc=0;
			int bar_height = ppp*bar_values.get(i);
			g.fillRect( origin_x+(i*bar_width), origin_y-bar_height, bar_width, ppp*bar_values.get(i));
			g.setColor(Color.BLACK);
			g.drawRect( origin_x+(i*bar_width), origin_y-bar_height, bar_width, ppp*bar_values.get(i));
		}
		color_inc=0; //comment out this line for resize color strobing
	}
	
	public void addValue(int x){
		bar_values.add(x);
	}
	
}
