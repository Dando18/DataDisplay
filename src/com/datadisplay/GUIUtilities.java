package com.datadisplay;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

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
	
}
