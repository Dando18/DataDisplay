package com.datadisplay;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunctionLagrangeForm;

public class CartesianGraph extends DataPanel {
	private static final long serialVersionUID = 1L;
	
	public final int PT_WIDTH = 4, PT_HEIGHT = 4;
	public int ppp = 20;
	public int x_scale = 1;
	public int y_scale = 1;
	private int origin_x = this.getWidth()/2, origin_y = this.getHeight()/2;
	private List<Double> ptx;
	private List<Double> pty;
	private double[] inter_x;
	private double[] inter_y;
	private int precision = 3;
	
	private Color pt_color = Color.RED;
	private boolean connectPoints = false;
	private boolean interpolate = false;
	private boolean showEquation = false;
	private boolean showMean = false;
	private boolean showStandardDeviation = false;
	private boolean showLeastSquaresLine = false;
	private boolean showLabels = false;
	private boolean showGrid = false;
	
	private boolean drag_pt = false;
	private int hover = Cursor.HAND_CURSOR;
	private int drag_index = -1;
	
	private PolynomialFunctionLagrangeForm pflf;
	private double[] coeff;
	
	//stats
	private double mean = 0;
	private double std_dev = 0;
	private double leastSquaresSlope = 0;
	private double leastSquaresInt = 0;
	
	public CartesianGraph() {
		super();
		
		this.setBackground(Color.WHITE);
		
		ptx = new ArrayList<Double>();
		pty = new ArrayList<Double>();
		
		this.addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e){
				double[] pts = mouseToCoordinate(e.getX(),e.getY());
				for(int i=0; i<ptx.size(); i++){
					if((Math.abs(pts[0]-ptx.get(i))<=0.2) && (Math.abs(pts[1]-pty.get(i))<=0.2)){
						drag_pt = true;
						drag_index = i;
						break;
					}
				}
			}
			@Override
			public void mouseReleased(MouseEvent e){
				if(drag_pt){
					calculate();
					repaint();
					drag_pt = false;
					drag_index = -1;
				}
			}	
		});
		this.addMouseMotionListener(new MouseMotionAdapter(){
			@Override
			public void mouseDragged(MouseEvent e){
				if(drag_pt && drag_index>=0){
					double[] pts = mouseToCoordinate(e.getX(),e.getY());
					
					if(drag_index>0 && ptx.get(drag_index)<ptx.get(drag_index-1)){
						ptx.set(drag_index, ptx.get(drag_index-1));
						pty.set(drag_index, pty.get(drag_index-1));
						ptx.set(drag_index-1, pts[0]);
						pty.set(drag_index-1, pts[1]);
						drag_index--;
					}else if(drag_index<ptx.size()-1 && ptx.get(drag_index)>ptx.get(drag_index+1)){
						ptx.set(drag_index, ptx.get(drag_index+1));
						pty.set(drag_index, pty.get(drag_index+1));
						ptx.set(drag_index+1, pts[0]);
						pty.set(drag_index+1, pts[1]);
						drag_index++;
					}else{
						ptx.set(drag_index, pts[0]);
						pty.set(drag_index, pts[1]);
					}
					
					if(!interpolate)
						calculate();
					repaint();
				}
			}
			@Override
			public void mouseMoved(MouseEvent e){
				double[] pts = mouseToCoordinate(e.getX(),e.getY());
				for(int i=0; i<ptx.size(); i++){
					if((Math.abs(pts[0]-ptx.get(i))<=0.2) && (Math.abs(pts[1]-pty.get(i))<=0.2)){
						if(getCursor().getType()!=hover){
							setCursor(new Cursor(hover));
						}
						break;
					}else{
						if(getCursor().getType()!=Cursor.DEFAULT_CURSOR){
							setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
						}
					}
				}
			}
		});
	}
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		origin_x = this.getWidth()/2;
		origin_y = this.getHeight()/2;
		if(showGrid){
			drawGrid(g2d);
		}
		drawAxes(g2d);
		drawTicks(g2d);
		for(int i=0; i<ptx.size(); i++){
			drawPoint(g2d, ptx.get(i),pty.get(i));
			if(connectPoints && i!=ptx.size()-1){
				if(interpolate){
					interpolate(g2d);
				}else{
					g.drawLine((int)(origin_x+(ptx.get(i)*ppp)/x_scale), (int)(origin_y-(pty.get(i)*ppp)/y_scale), 
						(int)(origin_x+(ptx.get(i+1)*ppp)/x_scale), (int)(origin_y-(pty.get(i+1)*ppp)/y_scale));
				}
			}
			if(showLabels){
				g2d.setFont(new Font(g2d.getFont().getFontName(),g2d.getFont().getStyle(),g2d.getFont().getSize()/2));
				g2d.drawString("("+MathUtilities.round(ptx.get(i), 3)+","+MathUtilities.round(pty.get(i), 3)+")",
						(int)(origin_x+(ptx.get(i)*ppp)/x_scale-PT_WIDTH/2),(int)(origin_y-(pty.get(i)*ppp)/y_scale-PT_HEIGHT/2));
				g2d.setFont(new Font(g2d.getFont().getFontName(),g2d.getFont().getStyle(),g2d.getFont().getSize()*2));
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
		if(showEquation){
			if(pflf != null){
				int width = 300;
				String eq = "";
				String cur = "";
				for(int i=0; i<coeff.length; i++){
					if(!(Math.abs(coeff[i]) < 2*Double.MIN_VALUE)){
						String term = MathUtilities.round(Math.abs(coeff[i]),precision)+
								((i!=coeff.length-1) ? "x^"+(coeff.length-i-1)+((coeff[i+1]<0)?" - ":" + ") : "");
						
						if(g2d.getFontMetrics().stringWidth(cur+term)>width){
							eq += "\n";
							cur = "";
						}
						eq += term;
						cur += term;
					}
				}
				g2d.setColor(Color.WHITE);
				g2d.fillRect(3, (num_text)*15+10, width, g2d.getFontMetrics().getHeight()*eq.split("\n").length+5);
				g2d.setColor(Color.BLACK);
				g2d.drawRect(3, (num_text)*15+10, width, g2d.getFontMetrics().getHeight()*eq.split("\n").length+5);
				g2d.setColor(Color.RED);
				GUIUtilities.drawString(g2d, eq, 5, (num_text++*15)+10);
			}
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
		
		if(interpolate){
			double[] x = new double[ptx.size()];
			double[] y = new double[pty.size()];
			for(int i=0; i<x.length; i++){
				x[i] = ptx.get(i);
				y[i] = pty.get(i);
			}
			pflf = new PolynomialFunctionLagrangeForm(x,y);
			coeff = pflf.getCoefficients();
			double step = 0.1;
			int numSteps = (int) ((getMaxX()*2)/step);
			inter_x = new double[numSteps];
			inter_y = new double[numSteps];
			double cur = -getMaxX();
			for(int i=0; i<numSteps; i++){
				inter_x[i] = cur;
				inter_y[i] = pflf.value(inter_x[i]);
				cur += step;
			}
		}
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
	
	private void drawGrid(Graphics2D g2d){
		g2d.setColor(Color.LIGHT_GRAY);
		// x-axis
		for(int i=-this.getWidth()/ppp; i<this.getWidth()/ppp;i++){
			g2d.drawLine(origin_x+(i+1)*ppp, 0, origin_x+(i+1)*ppp, this.getHeight());
		}
		// y-axis
		for(int i=-this.getHeight()/ppp; i<this.getHeight()/ppp;i++){
			g2d.drawLine(0, origin_y-(i+1)*ppp, this.getWidth(), origin_y-(i+1)*ppp);
		}
	}
	
	public void animate(double cur_x, double cur_y, double new_x, double new_y, float duration, float delay){
		Thread t1 = new Thread(new Runnable(){

			@Override
			public void run() {
				double step = 0.01;
				double steps_x = (Math.abs(new_x-cur_x))/step;
				long step_time = (long)(duration/steps_x);
				
				try{
					
					Thread.sleep((long)delay);
					int index = ptx.indexOf(cur_x);
					for(int i=0; i<steps_x; i++){
						double val = ptx.get(index);
						if(val<new_x)
							ptx.set(index, val+step);
						else if(val>new_x)
							ptx.set(index, val-step);
						repaint();
						Thread.sleep(step_time);
					}
					ptx.set(index, new_x);
					
				}catch(InterruptedException ex){
					ex.printStackTrace();
				}
				
			}
			
		});
		Thread t2 = new Thread(new Runnable(){

			@Override
			public void run() {
				double step = 0.01;
				double steps_y = (Math.abs(new_y-cur_y))/step;
				long step_time = (long)(duration/steps_y);
				
				try{
					
					Thread.sleep((long)delay);
					int index = pty.indexOf(cur_y);
					for(int i=0; i<steps_y; i++){
						double val = pty.get(index);
						if(val<new_y)
							pty.set(index, val+step);
						else if(val>new_y)
							pty.set(index, val-step);
						repaint();
						Thread.sleep(step_time);
					}
					pty.set(index, new_y);
					
				}catch(InterruptedException ex){
					ex.printStackTrace();
				}
			}
			
		});
		t1.start();
		t2.start();
	}
	
	public void animate(int index, double new_x, double new_y, float duration, float delay){
		animate(ptx.get(index), pty.get(index), new_x, new_y, duration, delay);
	}
	
	private void interpolate(Graphics2D g2d){
		for(int i=0; i<inter_x.length-1; i++){
			g2d.drawLine((int)(origin_x+(inter_x[i]*ppp)/x_scale), (int)(origin_y-(inter_y[i]*ppp)/y_scale),
					(int)(origin_x+(inter_x[i+1]*ppp)/x_scale), (int)(origin_y-(inter_y[i+1]*ppp)/y_scale));
		}
	}
	
	public double[] mouseToCoordinate(double x, double y){
		double[] pts = new double[2];
		pts[0] = (x_scale*(x-origin_x))/ppp;
		pts[1] = (y_scale*(origin_y-y))/ppp;
		return pts;
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
	
	public void interpolatePoints(){
		interpolate = true;
		calculate();
	}
	
	public void linearInterpolation(){
		interpolate = false;
	}
	
	public void showEquation(){
		showEquation = true;
	}
	
	public void hideEquation(){
		showEquation = false;
	}
	
	public void setPrecision(int precision){
		this.precision = precision;
	}
	
	public int getPrecision(){
		return precision;
	}
	
	public void showMean(){
		showMean = true;
		calculate();
	}
	
	public void hideMean(){
		showMean = false;
	}

	public void showStandardDeviation(){
		showStandardDeviation = true;
		calculate();
	}
	
	public void hideStandardDeviation(){
		showStandardDeviation = false;
	}
	
	public void showLeastSquaresLine(){
		showLeastSquaresLine = true;
		calculate();
	}
	
	public void hideLeastSquaresLine(){
		showLeastSquaresLine = false;
	}
	
	public void showLabels(){
		showLabels = true;
		repaint();
	}
	
	public void hideLabels(){
		showLabels = false;
	}
	
	public void showGrid(){
		showGrid = true;
		repaint();
	}
	
	public void hideGrid(){
		showGrid = false;
	}

}
