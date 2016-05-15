package com.datadisplay;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("unused")
public class DataPanel extends JPanel{
	private static final long serialVersionUID = 1L;
	
	private boolean drag;
	private Point dragLoc = new Point();
	
	public DataPanel(){
		super();
		
		addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e){
				drag = true;
				dragLoc = e.getPoint();
				repaint();
			}
			@Override
			public void mouseReleased(MouseEvent e){
				drag = false;
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		});
		addMouseMotionListener(new MouseMotionAdapter(){
			@Override
			public void mouseDragged(MouseEvent e){
				if(drag){
					if (dragLoc.getX()> getWidth()-10 && dragLoc.getY()>getHeight()-10) {
						setCursor(new Cursor(Cursor.MOVE_CURSOR));
            			setSize((int)(getWidth()+(e.getPoint().getX()-dragLoc.getX())),
            					(int)(getHeight()+(e.getPoint().getY()-dragLoc.getY())));
            			repaint();
            			dragLoc = e.getPoint();
            		}
				}
			}
		});
		
	}
	
	private JFrame getFrame(){
		return this.getFrame();
	}

}
