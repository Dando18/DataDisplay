package com.datadisplay.console;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import com.datadisplay.GUIUtilities;
import com.fathzer.soft.javaluator.DoubleEvaluator;

public class ConsoleUtilities {

	private ConsoleUtilities() {
	}

	// expected [num0, num1, num2, num3] NOTE: spaces don't matter
	public static List<Double> inputToList(String input) {
		String tmp = input.replaceAll("(\\[|\\])", "");
		List<Double> nums = new ArrayList<Double>();
		if (tmp.contains(",")) {
			String[] vals = tmp.split(",\\s*");
			for (String s : vals) {
				try {
					nums.add(Double.parseDouble(s));
				} catch (NumberFormatException ex) {
					nums.add(0.0);
				}
			}
		} else if (tmp.contains(":")) {
			String[] vals = tmp.split(":\\s*");
			double beg = 0;
			double end = 0;
			double step = 1;
			if (vals.length == 2) {
				try {
					beg = Double.parseDouble(vals[0]);
					end = Double.parseDouble(vals[1]);
				} catch (NumberFormatException ex) {
					nums.add(0.0);
				}
			} else if (vals.length == 3) {
				try {
					beg = Double.parseDouble(vals[0]);
					end = Double.parseDouble(vals[1]);
					step = Double.parseDouble(vals[2]);
				} catch (NumberFormatException ex) {
					nums.add(0.0);
				}
			}
			if (step != 0 && ((beg < end && step > 0) || (beg > end && step < 0))) {
				for (double d = beg; d <= end; d += step) {
					nums.add(d);
				}
			}
		}
		return nums;
	}

	public static Double eval(String in) {
		if (in == null || in.trim().equals(""))
			return null;
		return new DoubleEvaluator().evaluate(in);
	}

	public static <T> String listToString(List<T> l) {
		String response = "[";
		for (int i = 0; i < l.size() - 1; i++) {
			response += l.get(i) + ", ";
		}
		return response + l.get(l.size() - 1) + "]";
	}

	// a+2|a=1 -> 1+2
	public static String replaceVarWithValue(String input, Map<String, Double> vars) {
		String result = "";

		String[] terms = input.split(String.format("((?<=%1$s)|(?=%1$s))", "\\s*\\)?[\\+\\-\\*/,\\[\\]]\\(?\\s*"));

		for (int i = 0; i < terms.length; i++) {
			if (vars.containsKey(terms[i])) {
				result += "" + vars.get(terms[i]);
			} else {
				result += terms[i];
			}
		}

		return result;
	}

	// expecting format <command> -<key> ... <key>="<value>" ...
	public static List<String> getArgs(String input) {
		List<String> sep = new ArrayList<String>();
		Matcher m = Pattern.compile("(?:[^\\s\"]+|\"[^\"]*\")+").matcher(input);
		while (m.find()) {
			sep.add(m.group());
		}
		List<String> args = new ArrayList<String>();
		if (sep.size() > 1) {
			for (int i = 1; i < sep.size(); i++) {
				args.add(sep.get(i));
			}
		}
		return args;
	}

	public static Color stringToColor(String in) {
		if (in.startsWith("#")) {
			return new Color(Integer.valueOf(in.substring(1, 3), 16), Integer.valueOf(in.substring(3, 5), 16),
					Integer.valueOf(in.substring(5, 7), 16));
		} else if (Pattern.matches("\\d{1,3},\\d{1,3},\\d{1,3}", in)) {
			String[] nums = in.split(",");
			return new Color(Integer.parseInt(nums[0]), Integer.parseInt(nums[1]), Integer.parseInt(nums[2]));
		} else {
			switch (in.toLowerCase()) {
			case "black":
				return Color.BLACK;
			case "white":
				return Color.WHITE;
			case "blue":
				return Color.BLUE;
			case "red":
				return Color.RED;
			case "yellow":
				return Color.YELLOW;
			case "gray":
				return Color.GRAY;
			case "grey":
				return Color.GRAY;
			case "orange":
				return Color.ORANGE;
			case "purple":
				return Color.MAGENTA;
			case "magenta":
				return Color.MAGENTA;
			case "light gray":
				return Color.LIGHT_GRAY;
			case "light grey":
				return Color.LIGHT_GRAY;
			case "dark gray":
				return Color.DARK_GRAY;
			case "dark grey":
				return Color.DARK_GRAY;
			case "cyan":
				return Color.CYAN;
			default:
				return Color.WHITE;
			}
		}

	}
	
	public static boolean orEquals(String test, String... in){
		if(test==null) return false;
		for(String s : in){
			if(test.equals(s))
				return true;
		}
		return false;
	}

	public static boolean dump(String file_name, JPanel p) {
		try {
			if (file_name.endsWith("/") || "".equals(file_name)) {
				file_name += String.format("console_%X", (long) (Math.random() * 1000));
			}
			if (!file_name.endsWith(".png")) {
				file_name += ".png";
			}
			File f = new File(file_name);
			f.getParentFile().mkdirs();
			f.createNewFile();
			BufferedImage bi = GUIUtilities.createImage(p);
			ImageIO.write(bi, "png", f);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
