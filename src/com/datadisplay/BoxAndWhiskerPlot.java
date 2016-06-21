package com.datadisplay;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Collections;

public class BoxAndWhiskerPlot extends DataPanel {
	private static final long serialVersionUID = 1L;

	public int ppp = 20;
	public int scale = 1;
	public ArrayList<ArrayList<Double>> plots;
	public ArrayList<Box> data;

	private final int PADDING = 20;
	private final int origin_x = PADDING;
	private int origin_y = getHeight() - PADDING;

	private class Box {
		public double min;
		public double q1;
		public double q2;
		public double q3;
		public double max;

		public Box(double min, double q1, double q2, double q3, double max) {
			this.min = min;
			this.q1 = q1;
			this.q2 = q2;
			this.q3 = q3;
			this.max = max;
		}
	}

	public BoxAndWhiskerPlot() {
		plots = new ArrayList<ArrayList<Double>>();
		data = new ArrayList<Box>();
	}

	public void addPlot(ArrayList<Double> values) {
		Collections.sort(values);
		plots.add(values);
		calculate();
	}

	public void addValueAtIndex(int index, double... value) {
		if (index < 0)
			throw new IllegalArgumentException("Index must be greater than zero");
		if (index >= plots.size())
			throw new IllegalArgumentException("Plot does not exist");
		for (double d : value)
			plots.get(index).add(d);
		Collections.sort(plots.get(index));
		calculate();
	}

	public void addValueAtIndex(int index, ArrayList<Double> value) {
		if (index < 0)
			throw new IllegalArgumentException("Index must be greater than zero");
		if (index >= plots.size())
			throw new IllegalArgumentException("Plot does not exist");
		for (double d : value)
			plots.get(index).add(d);
		Collections.sort(plots.get(index));
		calculate();
	}

	public void addValues(double... values) {
		ArrayList<Double> a = new ArrayList<Double>();
		for (double d : values) {
			a.add(d);
		}
		Collections.sort(a);
		plots.add(a);
		calculate();
	}

	private void calculate() {
		for (int i = 0; i < plots.size(); i++) {
			if (plots.get(i).size() <= 2)
				break;
			double min = MathUtilities.min(plots.get(i));
			double max = MathUtilities.max(plots.get(i));
			double q2 = MathUtilities.median(plots.get(i));
			int q2_index = MathUtilities.medianIndex(plots.get(i));
			double q1 = MathUtilities.median(plots.get(i).subList(0, q2_index));
			double q3 = MathUtilities.median(plots.get(i).subList(q2_index, plots.get(i).size() - 1));
			try {
				data.set(i, new Box(min, q1, q2, q3, max));
			} catch (IndexOutOfBoundsException | NullPointerException ex) {
				data.add(new Box(min, q1, q2, q3, max));
			}
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		origin_y = getHeight() - PADDING;

		drawAxes(g2d);
		drawTicks(g2d);

		for (int i = 0; i < plots.size(); i++) {
			plot(g2d, plots.get(i), data.get(i), i);
		}
	}

	private void drawAxes(Graphics2D g2d) {
		g2d.drawLine(PADDING, PADDING, PADDING, getHeight() - PADDING);
		g2d.drawLine(PADDING, getHeight() - PADDING, getWidth() - PADDING, getHeight() - PADDING);
	}

	private void drawTicks(Graphics2D g2d){
		int tickheight = 5;
		for(int i=0; i<(getWidth()-PADDING*2)/ppp;i++){
			g2d.drawLine(origin_x+(i+1)*ppp, origin_y-tickheight, origin_x+(i+1)*ppp, origin_y+tickheight);
		}
		FontMetrics fm = g2d.getFontMetrics();
		g2d.drawString(""+scale, origin_x+ppp-fm.stringWidth(""+scale)/2, origin_y+fm.getHeight());
	}

	private void plot(Graphics2D g2d, ArrayList<Double> plot, Box b, int index) {
		g2d.setColor(Color.black);
		int height = getBarHeight();
		int center_y = origin_y - (height / 2 + PADDING) * (index + 1) - index * height / 2;
		// min to q1
		g2d.drawLine((int) (origin_x + (b.min * ppp) / scale), center_y, (int) (origin_x + (b.q1 * ppp) / scale),
				center_y);
		// q3 to max
		g2d.drawLine((int) (origin_x + (b.q3 * ppp) / scale), center_y, (int) (origin_x + (b.max * ppp) / scale),
				center_y);
		// q1 vertical
		g2d.drawLine((int) (origin_x + (b.q1 * ppp) / scale), center_y - height / 2,
				(int) (origin_x + (b.q1 * ppp) / scale), center_y + height / 2);
		// q2 vertical
		g2d.drawLine((int) (origin_x + (b.q2 * ppp) / scale), center_y - height / 2,
				(int) (origin_x + (b.q2 * ppp) / scale), center_y + height / 2);
		// q3 vertical
		g2d.drawLine((int) (origin_x + (b.q3 * ppp) / scale), center_y - height / 2,
				(int) (origin_x + (b.q3 * ppp) / scale), center_y + height / 2);
		// box top
		g2d.drawLine((int) (origin_x + (b.q1 * ppp) / scale), center_y - height / 2,
				(int) (origin_x + (b.q3 * ppp) / scale), center_y - height / 2);
		// box top
		g2d.drawLine((int) (origin_x + (b.q1 * ppp) / scale), center_y + height / 2,
				(int) (origin_x + (b.q3 * ppp) / scale), center_y + height / 2);

	}

	private int getBarHeight() {
		return (getHeight() - PADDING * 2) / (plots.size()) - PADDING;
	}

}
