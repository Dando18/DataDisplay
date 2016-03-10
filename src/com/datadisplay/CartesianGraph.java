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
	public int origin_x = this.getWidth()/2, origin_y = this.getHeight()/2;
	
	
	public List<Point> pts;
	
	public CartesianGraph() {
		
		this.setBackground(Color.WHITE);
		
		pts = new ArrayList<Point>();
	}
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		drawAxes(g);
		for(int i=0; i<pts.size(); i++){
			drawPoint(g, pts.get(i).x,pts.get(i).y);
		}
	}
	
	public void plot(double x, double y){
		plot(new Point((int)x,(int)y));
	}
	
	public void plot(Point p){
		pts.add(p);
		//drawPoint(p.x,p.y);
	}
	
	private void drawPoint(Graphics g, double x, double y){
		origin_x = this.getWidth()/2;
		origin_y = this.getHeight()/2;
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.RED);
		g2d.fillOval((int)(origin_x+(x*ppp)-PT_WIDTH/2), (int)(origin_y-(y*ppp)-PT_HEIGHT/2), PT_WIDTH, PT_HEIGHT);
		System.out.println((int)(origin_x+(x*ppp))+", "+ (int)(origin_y-(y*ppp)));
		
	}
	
	private void drawAxes(Graphics g){
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.BLACK);
		g2d.drawLine(0, this.getHeight()/2, this.getWidth(), this.getHeight()/2);
		g2d.drawLine(this.getWidth()/2, 0, this.getWidth()/2, this.getHeight());
	}

}
