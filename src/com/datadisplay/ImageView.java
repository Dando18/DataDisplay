package com.datadisplay;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

public class ImageView extends DataPanel{
	private static final long serialVersionUID = 1L;

	private Image img;
	
	private int x = 0;
	private int y = 0;
	private int width = this.getWidth();
	private int height = this.getHeight();
	
	public ImageView(){
		super();
		
		
	}
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		
		if(img!=null){
			g2d.drawImage(img, x, y, width, height, this);
		}
	}
	
	public void showImage(Image img){
		this.img = img;
		repaint();
	}

	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}
	
	
	
}
