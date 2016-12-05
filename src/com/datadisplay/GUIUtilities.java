package com.datadisplay;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class GUIUtilities {
	
	private GUIUtilities(){}
	
	public static Color randomColor(){
		Random rand = new Random(System.nanoTime());
		float[] hsb = Color.RGBtoHSB(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256), null);
		return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
	}
	
	public static void drawString(Graphics2D g, String text, int x, int y) {
	    for (String line : text.split("\n"))
	        g.drawString(line, x, y += g.getFontMetrics().getHeight());
	}
	
	public static BufferedImage createImage(JPanel panel) {
	    int w = panel.getWidth();
	    int h = panel.getHeight();
	    BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
	    Graphics2D g = bi.createGraphics();
	    panel.paint(g);
	    return bi;
	}
	
	public static BufferedImage createImage(JComponent comp) {
	    int w = comp.getWidth();
	    int h = comp.getHeight();
	    BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
	    Graphics2D g = bi.createGraphics();
	    comp.paint(g);
	    return bi;
	}
	
	public static float getHue(Color c){
		return Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null)[0];
	}
	
	
}
