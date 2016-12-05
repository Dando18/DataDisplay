package com.datadisplay;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.datadisplay.function.MultivariateFunction;

public class ContourPlot extends DataPanel {
	private static final long serialVersionUID = 1L;

	public int ppp = 20;
	public int x_scale = 1;
	public int y_scale = 1;
	private int origin_x = this.getWidth() / 2, origin_y = this.getHeight() / 2;
	private int precision = 3;
	private double minz, maxz;
	
	private boolean drawkey = true;
	private int key_cushion = 15;
	private int key_width = 30;
	private int key_height = 100;

	MultivariateFunction mf;

	public ContourPlot() {
		super();
		
		this.setBackground(Color.WHITE);

	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		origin_x = getWidth() / 2;
		origin_y = getHeight() / 2;
		BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		
		for(int c=0; c<getWidth(); c++){
			for(int r=0; r<getHeight(); r++){
				
				if(mf != null){
					// System.out.println("("+c+","+r+") -> ("+pixelToPointX(c)+","+pixelToPointY(r)+")");
					double z = mf.evaluate(pixelToPointX(c), pixelToPointY(r));
					// System.out.println("z: "+z);
					img.setRGB(c, r, getColor(minz, maxz, z).getRGB() );
				}
			}
		}
		
		g2d.drawImage(img, 0, 0, null);
		drawAxes(g2d);
		if(drawkey)
			drawKey(g2d);
	}

	public void setFunction(MultivariateFunction mf) {
		if (mf.variables != 2) {
			throw new IllegalArgumentException("MultivariateFunction must have two variables for CountourPlot");
		}
		this.mf = mf;
		setZBounds();
	}

	private void setZBounds() {
		double largest = mf.evaluate(0, 0);
		double smallest = largest;
		for (double x : MathUtilities.range(-10, 10, 0.5)) {
			for (double y : MathUtilities.range(-10, 10, 0.5)) {
				double tmp = mf.evaluate(x, y);
				if (tmp > largest) {
					largest = tmp;
				} else if (tmp < smallest) {
					smallest = tmp;
				}
			}
		}
		maxz = largest;
		minz = smallest;
	}
	
	private double pixelToPointX(int x){
		return (x-origin_x)*((double)x_scale/ppp);
	}
	
	private double pixelToPointY(int y){
		return (y-origin_y)*((double)y_scale/ppp);
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
	
	private void drawKey(Graphics2D g2d){
		// draw rect
		int rect_x = getWidth() - key_cushion - key_width;
		int rect_y = getHeight() - key_cushion - key_height;
		g2d.drawRect(rect_x, rect_y, key_width, key_height);
		
		// fill with image gradient
		BufferedImage img = new BufferedImage(key_width, key_height, BufferedImage.TYPE_INT_RGB);
		for(int c=0; c<key_width; c++){
			for(int r=0; r<key_height; r++){
				img.setRGB(c, r, getColor(0, key_height, r).getRGB());
			}
		}
		g2d.drawImage(img, rect_x, rect_y, null);
		
		// label 
		FontMetrics fm = g2d.getFontMetrics();
		g2d.drawString(""+maxz, rect_x - fm.stringWidth(""+maxz) - 3, rect_y + fm.getHeight() - 2);
		g2d.drawString(""+minz, rect_x - fm.stringWidth(""+minz) - 3, rect_y + key_height);
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

	@SuppressWarnings("unused")
	private Color getColor(double z) {				
		// z = MathUtilities.clamp(minz, maxz, z);
		// return Color.decode(String.format("#%02x", (int)(0xFFFFFF * (z-minz) / (maxz-minz))));
		
		z = MathUtilities.clamp(0.0, 1.0, Math.abs(minz + z) / (maxz-minz));
		return Color.decode(String.format("#%02x%02x%02x",(int)(0xFF * 1),(int)(0xFF * z),(int)(0xFF * z)));
	}
	
	private Color getColor(double min, double max, double val) {
		val = MathUtilities.clamp(min, max, val);
		float hue = (float)(GUIUtilities.getHue(Color.BLUE) + 
				(GUIUtilities.getHue(Color.RED) - GUIUtilities.getHue(Color.BLUE)) * 
				(val-min) / (max-min));
		return Color.getHSBColor(hue, 1f, 1f);
	}
	

}
