package com.datadisplay;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.SwingWorker;

import com.datadisplay.function.Function;
import com.datadisplay.function.FunctionInterface;

public class PolarGraph extends JPanel{
	public static final long serialVersionUID = 1L;
	
	public final int PT_WIDTH = 4, PT_HEIGHT = 4;
	public int ppp = 20;
	public int scale = 1;
	private int origin_x = this.getWidth()/2, origin_y = this.getHeight()/2;
	private List<Double> ptr;
	private List<Double> ptt;
	private int precision = 3;
	
	private Color pt_color = Color.RED;
	private boolean connectPoints = false;
	private boolean showPoints = false;
	public boolean epilepsy = false;
	
	/**
	 *  __init__
	 */
	public PolarGraph() {
		
		this.setBackground(Color.WHITE);
		
		ptr = new ArrayList<Double>();
		ptt = new ArrayList<Double>();
		
	}
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		
		origin_x = this.getWidth()/2;
		origin_y = this.getHeight()/2;
		
		drawAxes(g2d);
		drawCircles(g2d);
		
		for(int i=0; i< ptr.size(); i++){
			if(showPoints)
				drawPoint(g2d, ptr.get(i), ptt.get(i));
			if(connectPoints && i!=ptr.size()-1){
				g2d.setColor(pt_color);
				g.drawLine((int)(origin_x+Math.cos(ptt.get(i))*ptr.get(i)*ppp/scale), 
						(int)(origin_y-Math.sin(ptt.get(i))*ptr.get(i)*ppp/scale), 
						(int)(origin_x+Math.cos(ptt.get(i+1))*ptr.get(i+1)*ppp/scale), 
						(int)(origin_y-Math.sin(ptt.get(i+1))*ptr.get(i+1)*ppp/scale));
			}
		}
		
	}
	
	private void drawPoint(Graphics2D g2d, double r, double theta){
		g2d.setColor(pt_color);
		g2d.fillOval((int)(origin_x+Math.cos(theta)*r*ppp/scale-PT_WIDTH/2),
				(int)(origin_y-Math.sin(theta)*r*ppp/scale-PT_HEIGHT/2),
				PT_WIDTH, PT_HEIGHT);
	}
	
	private void drawAxes(Graphics2D g2d){
		g2d.setColor(Color.BLACK);
		g2d.drawLine(0, this.getHeight()/2, this.getWidth(), this.getHeight()/2);
		g2d.drawLine(this.getWidth()/2, 0, this.getWidth()/2, this.getHeight());
		
		String x_str = ""+MathUtilities.round(getMaxR(), precision);
		String y_str = ""+MathUtilities.round(getMaxR(), precision);
		FontMetrics fm = g2d.getFontMetrics();
		g2d.drawString(y_str, origin_x-fm.stringWidth(y_str)-2, fm.getHeight()+2);
		g2d.drawString(x_str, this.getWidth()-fm.stringWidth(x_str)-2, origin_y+fm.getHeight()+2);
		g2d.drawString("scale: "+scale, this.getWidth()-fm.stringWidth("scale: "+scale)-5, fm.getHeight());
		g2d.drawString(""+scale, origin_x+ppp-fm.stringWidth(""+scale)/2, origin_y+fm.getHeight()+5);
	}
	
	private void drawCircles(Graphics2D g2d){
		g2d.setColor( Color.GRAY );
		for(int i=0; i<this.getWidth()/ppp;i++){
			if(epilepsy) g2d.setColor(GUIUtilities.randomColor());
			g2d.drawOval(origin_x-(i+1)*ppp, origin_y-(i+1)*ppp, (i+1)*ppp*2, (i+1)*ppp*2);
		}
	}
	
	/**
	 * plot polar coordinate (r,theta)
	 * @param r r value of coordinate (radius)
	 * @param theta theta value of coordinate in radians (angle) NOTE: [theta %= 2*PI]
	 */
	public void plot(double r, double theta){
		theta %= (2*Math.PI);
		ptr.add(r);
		ptt.add(theta);
	}
	
	public void plot(Function f, double p) {
		SwingWorker<Void,Void> sw = new SwingWorker<Void,Void>() {

			@Override
			protected Void doInBackground() throws Exception {
				double step = scale/p;
				double max = Math.PI;
				for(double i=0; i<max; i+=step){
					plot(f.evaluate(i), i);
				}
				return null;
			}
			
		};
		setConnectPoints(true);
		sw.execute();
		repaint();
	}
	
	public void plot(Function f) {
		plot(f, 5.0);
	}
	
	public void plot(FunctionInterface f, double p){
		plot(new Function(f), p);
	}
	
	public void plot(FunctionInterface f){
		plot(f, 5.0);
	}
	
	/**
	 * 
	 * @return maximum radius in panel size
	 */
	public double getMaxR(){
		return (this.getWidth()/2)/ppp*scale;
	}
	
	/**
	 * 
	 * @param max value to set maximum radius in panel size
	 */
	public void setMaxR(double max){
		scale = (int)((2*ppp*max)/this.getWidth());
	}
	
	/**
	 * 
	 * @param c color to set points
	 */
	public void setPointColor(Color c){
		pt_color = c;
	}
	
	/**
	 * 
	 * @return Color of points
	 */
	public Color getPointColor(){
		return pt_color;
	}
	
	/**
	 * @param b if true, points drawn
	 */
	public void setShowPoints(Boolean b){
		showPoints = b;
	}
	
	/**
	 * @return true if points will be drawn
	 */
	public Boolean getShowPoints(){
		return showPoints;
	}
	
	/**
	 * 
	 * @param b if true, points will be sequentially connected by lines in the order they're added
	 */
	public void setConnectPoints(Boolean b){
		connectPoints = b;
	}
	
	/**
	 * 
	 * @return returns true if the lines are to be connected
	 */
	public Boolean getConnectPoints(){
		return connectPoints;
	}
	
}
