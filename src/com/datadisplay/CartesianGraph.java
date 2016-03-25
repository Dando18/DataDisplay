package com.datadisplay;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public class CartesianGraph extends JPanel {
	private static final long serialVersionUID = 1L;
	
	public final int PT_WIDTH = 4, PT_HEIGHT = 4;
	public int ppp = 20;
	public int x_scale = 1;
	public int y_scale = 1;
	private int origin_x = this.getWidth()/2, origin_y = this.getHeight()/2;
	private List<Double> ptx;
	private List<Double> pty;
	private int precision = 3;
	
	private Color pt_color = Color.RED;
	private boolean connectPoints = false;
	private boolean showMean = false;
	private boolean showStandardDeviation = false;
	private boolean showLeastSquaresLine = false;
	
	//stats
	private double mean = 0;
	private double std_dev = 0;
	private double leastSquaresSlope = 0;
	private double leastSquaresInt = 0;
	
	public CartesianGraph() {
		
		this.setBackground(Color.WHITE);
		
		ptx = new ArrayList<Double>();
		pty = new ArrayList<Double>();
	}
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		origin_x = this.getWidth()/2;
		origin_y = this.getHeight()/2;
		drawAxes(g2d);
		drawTicks(g2d);
		for(int i=0; i<ptx.size(); i++){
			drawPoint(g2d, ptx.get(i),pty.get(i));
			if(connectPoints && i!=ptx.size()-1){
				g.drawLine((int)(origin_x+(ptx.get(i)*ppp)/x_scale), (int)(origin_y-(pty.get(i)*ppp)/y_scale), 
						(int)(origin_x+(ptx.get(i+1)*ppp)/x_scale), (int)(origin_y-(pty.get(i+1)*ppp)/y_scale));
			}
		}
		int num_text=1;
		if(showMean){
			g2d.setColor(Color.BLUE);
			g2d.drawLine(0, (int)(origin_y-(mean*ppp)/y_scale), this.getWidth(), (int)(origin_y-(mean*ppp)/y_scale));
			g2d.drawString("mean(ȳ): "+MathUtilities.round(mean, precision), 5, (num_text++*15)+10);
		}
		if(showStandardDeviation){
			g2d.setColor(Color.GREEN);
			g2d.drawLine(0, (int)(origin_y-(std_dev*ppp)/y_scale), this.getWidth(), (int)(origin_y-(std_dev*ppp)/y_scale));
			g2d.drawString("std_dev(σ): "+MathUtilities.round(std_dev, precision), 5, (num_text++*15)+10);
		}
		if(showLeastSquaresLine){
			g2d.setColor(Color.MAGENTA);
			g2d.drawLine(0, (int)((origin_y-(-this.getWidth()/2*leastSquaresSlope*x_scale/y_scale + ppp*leastSquaresInt/y_scale))), 
					this.getWidth(), (int)((origin_y-(this.getWidth()/2*leastSquaresSlope*x_scale/y_scale + ppp*leastSquaresInt/y_scale))));
			g2d.drawString(
					"ŷ= "+MathUtilities.round(leastSquaresSlope, precision)+"x + "+MathUtilities.round(leastSquaresInt, precision),
					5, (num_text++*15)+10);
		}
	}
	
	public void plot(double x, double y){
		ptx.add(x);
		pty.add(y);
	}
	
	public void calculate(){
		mean = MathUtilities.mean(pty);
		std_dev = MathUtilities.std_dev(mean, pty);
		double meanx = MathUtilities.mean(ptx);
		leastSquaresSlope = MathUtilities.leastSquaresSlope(meanx,ptx,mean,pty);
		leastSquaresInt = mean - leastSquaresSlope*meanx;
	}
	
	private void drawPoint(Graphics2D g2d, double x, double y){
		g2d.setColor(pt_color);
		g2d.fillOval((int)(origin_x+(x*ppp)/x_scale-PT_WIDTH/2), (int)(origin_y-(y*ppp)/y_scale-PT_HEIGHT/2), PT_WIDTH, PT_HEIGHT);		
	}
	
	private void drawAxes(Graphics2D g2d){
		g2d.setColor(Color.BLACK);
		g2d.drawLine(0, this.getHeight()/2, this.getWidth(), this.getHeight()/2);
		g2d.drawLine(this.getWidth()/2, 0, this.getWidth()/2, this.getHeight());
		
		String x_str = ""+MathUtilities.round(getMaxX(), precision);
		String y_str = ""+MathUtilities.round(getMaxY(), precision);
		FontMetrics fm = g2d.getFontMetrics();
		g2d.drawString(y_str, origin_x-fm.stringWidth(y_str)-2, fm.getHeight()+2);
		g2d.drawString(x_str, this.getWidth()-fm.stringWidth(x_str)-2, origin_y+fm.getHeight()+2);
		g2d.drawString("x_scale: "+x_scale, this.getWidth()-fm.stringWidth("x_scale: "+x_scale)-5, fm.getHeight());
		g2d.drawString(""+x_scale, origin_x+ppp-fm.stringWidth(""+x_scale)/2, origin_y+fm.getHeight()+5);
		g2d.drawString("y_scale: "+y_scale, this.getWidth()-fm.stringWidth("y_scale: "+y_scale)-5, fm.getHeight()*2);
		g2d.drawString(""+y_scale, origin_x-fm.stringWidth(""+y_scale)-8, origin_y-ppp+fm.getHeight()/4);
	}
	
	private void drawTicks(Graphics2D g2d){
		// x-axis
		for(int i=-this.getWidth()/ppp; i<this.getWidth()/ppp;i++){
			g2d.drawLine(origin_x+(i+1)*ppp, origin_y-5, origin_x+(i+1)*ppp, origin_y+5);
		}
		// y-axis
		for(int i=-this.getHeight()/ppp; i<this.getHeight()/ppp;i++){
			g2d.drawLine(origin_x-5, origin_y-(i+1)*ppp, origin_x+5, origin_y-(i+1)*ppp);
		}
	}
	
	public double getMaxX(){
		return (this.getWidth()/2)/ppp*x_scale;
	}
	
	public void setMaxX(double max){
		x_scale = (int)((2*ppp*max)/this.getWidth());
	}
	
	public double getMaxY(){
		return (this.getHeight()/2)/ppp*y_scale;
	}
	
	public void setMaxY(double max){
		y_scale = (int)((2*ppp*max)/this.getHeight());
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
	
	public void setPrecision(int precision){
		this.precision = precision;
	}
	
	public int getPrecision(){
		return precision;
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
	
	public void showLeastSquaresLine(){
		calculate();
		showLeastSquaresLine = true;
	}
	
	public void hideLeastSquaresLine(){
		showLeastSquaresLine = false;
	}

}
