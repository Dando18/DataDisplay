package com.datadisplay;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

public class BarChart extends DataPanel{
	private static final long serialVersionUID = 1L;

	private int cushion = 20;
	private int origin_x = cushion;
	private int origin_y = this.getHeight()-cushion;
	private int ppp = 20;
	
	private List<Double> bar_values;
	private List<String> titles;
	private boolean showBarValues = false;
	private boolean showBarTitles = false;
	private String title = "";
	
	private Color[] bar_colors = {Color.RED,Color.BLUE,Color.GREEN,Color.ORANGE,Color.MAGENTA,Color.PINK};
	private int color_inc = 0;
	
	public BarChart(){
		super();
		
		this.setBackground(Color.LIGHT_GRAY);
		
		bar_values = new ArrayList<Double>();
		titles = new ArrayList<String>();
		
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
			double bar_height = ppp*bar_values.get(i);
			g2d.fillRect( origin_x+(i*bar_width), (int)(origin_y-bar_height), bar_width, (int)(ppp*bar_values.get(i)));
			g2d.setColor(Color.BLACK);
			g2d.drawRect( origin_x+(i*bar_width), (int)(origin_y-bar_height), bar_width, (int)(ppp*bar_values.get(i)));
			
			if(showBarValues){
				g2d.setColor(Color.WHITE);
				g2d.drawString(""+bar_values.get(i), origin_x+(i*bar_width)+bar_width/2-5, (int)(origin_y-ppp*bar_values.get(i)/2+5));
			}
			if(showBarTitles){
				FontMetrics fm = g2d.getFontMetrics();
				g2d.setColor(Color.WHITE);
				g2d.drawString(titles.get(i), origin_x+(i*bar_width)+bar_width/2-fm.stringWidth(titles.get(i))/2, 
						(int)(origin_y-ppp*bar_values.get(i)-fm.getHeight()));
			}
		}
		color_inc=0; //comment out this line for resize color strobing
		
		g2d.setColor(Color.WHITE);
		FontMetrics fm = g2d.getFontMetrics();
		g2d.drawString(title, this.getWidth()/2-fm.stringWidth(title)/2, cushion/4+fm.getHeight());
	}
	
	public void addValue(double x, String title){
		bar_values.add(x);
		titles.add(title);
	}
	
	public void addValue(double x){
		addValue(x,"");
	}
	
	public void addValues(List<Double> l){
		for(Double d : l){
			addValue(d);
		}
	}
	
	private void drawAxes(Graphics2D g2d){
		g2d.setColor(Color.BLACK);
		g2d.drawLine(origin_x, origin_y, origin_x, cushion);
		g2d.drawLine(origin_x, origin_y, this.getWidth()-cushion, origin_y);
		g2d.drawString(""+ (double)Math.round((origin_y-cushion*2)/ppp*100d)/100d, origin_x-15, cushion-2);
	}
	
	public void animate(int bar, float value, float duration, float delay){
		Thread t = new Thread(new Runnable(){

			@Override
			public void run() {
				int step = 1;
				int steps = (int)(Math.abs(value-bar_values.get(bar))/step+0.5);
				float step_time = duration/steps;
				
				try{
					Thread.sleep((long) delay);
					for(int i=0; i<steps;i++){
						if(bar_values.get(bar)<value){
							bar_values.set(bar, bar_values.get(bar)+step);
						}else if(bar_values.get(bar)>value){
							bar_values.set(bar, bar_values.get(bar)-step);
						}
						repaint();
						Thread.sleep((long) step_time);
					}
				}catch(InterruptedException ex){
					ex.printStackTrace();
				}
			}
			
		});
		t.start();

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
	
	public void showBarTitles(){
		showBarTitles = true;
	}
	
	public void hideBarTitles(){
		showBarTitles = false;
	}
	
}
