package com.datadisplay;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public class CartesianGraph extends JPanel {
	private static final long serialVersionUID = 1L;
	
	public final int PT_WIDTH = 4, PT_HEIGHT = 4;
	public int ppp = 20;
	private int origin_x = this.getWidth()/2, origin_y = this.getHeight()/2;
	private List<Point> pts;
	
	private Color pt_color = Color.RED;
	private boolean connectPoints = false;
	private boolean showMean = false;
	private boolean showStandardDeviation = false;
	
	//stats
	private double mean = 0;
	private double std_dev = 0;
	
	public CartesianGraph() {
		
		this.setBackground(Color.WHITE);
		
		pts = new ArrayList<Point>();
	}
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		origin_x = this.getWidth()/2;
		origin_y = this.getHeight()/2;
		drawAxes(g);
		drawTicks(g);
		for(int i=0; i<pts.size(); i++){
			drawPoint(g, pts.get(i).x,pts.get(i).y);
			if(connectPoints && i!=pts.size()-1){
				g.drawLine(origin_x+(pts.get(i).x*ppp), origin_y-(pts.get(i).y*ppp), 
						origin_x+(pts.get(i+1).x*ppp), origin_y-(pts.get(i+1).y*ppp));
			}
		}
		int num_text=1;
		if(showMean){
			g.setColor(Color.BLUE);
			g.drawLine(0, (int)(origin_y-(mean*ppp)), this.getWidth(), (int)(origin_y-(mean*ppp)));
			g.drawString("mean: "+(double)Math.round(mean*1000d)/1000d, 5, (num_text*15)+10);
			num_text++;
		}
		if(showStandardDeviation){
			g.setColor(Color.GREEN);
			g.drawLine(0, (int)(origin_y-(std_dev*ppp)), this.getWidth(), (int)(origin_y-(std_dev*ppp)));
			g.drawString("std_dev: "+(double)Math.round(std_dev*1000d)/1000d, 5, (num_text*15)+10);
			num_text++;
		}
	}
	
	public void plot(double x, double y){
		plot(new Point((int)x,(int)y));
	}
	
	public void plot(Point p){
		pts.add(p);
	}
	
	public void calculate(){
		double[] pts_y = new double[pts.size()];
		for(int i=0; i<pts.size(); i++){
			pts_y[i] = (double)pts.get(i).y;
		}
		mean = MathUtilities.mean(pts_y);
		std_dev = MathUtilities.std_dev(mean, pts_y);
	}
	
	private void drawPoint(Graphics g, double x, double y){
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(pt_color);
		g2d.fillOval((int)(origin_x+(x*ppp)-PT_WIDTH/2), (int)(origin_y-(y*ppp)-PT_HEIGHT/2), PT_WIDTH, PT_HEIGHT);		
	}
	
	private void drawAxes(Graphics g){
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.BLACK);
		g2d.drawLine(0, this.getHeight()/2, this.getWidth(), this.getHeight()/2);
		g2d.drawLine(this.getWidth()/2, 0, this.getWidth()/2, this.getHeight());
	}
	
	private void drawTicks(Graphics g){
		Graphics2D g2d = (Graphics2D) g;
		// x-axis
		for(int i=-this.getWidth()/ppp; i<this.getWidth()/ppp;i++){
			g2d.drawLine(origin_x+(i+1)*ppp, origin_y-5, origin_x+(i+1)*ppp, origin_y+5);
		}
	}
	
	public void setPointColor(Color c){
		pt_color = c;
	}
	
	public Color getPointColor(){
		return pt_color;
	}
	
	public void setConnectPoints(Boolean b){
		connectPoints = b;
	}
	
	public Boolean getConnectPoints(){
		return connectPoints;
	}
	
	public void showMean(){
		calculate();
		showMean = true;
	}
	
	public void hideMean(){
		showMean = false;
	}

	public void showStandardDeviation(){
		calculate();
		showStandardDeviation = true;
	}
	
	public void hideStandardDeviation(){
		showStandardDeviation = false;
	}

}
