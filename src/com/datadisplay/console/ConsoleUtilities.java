package com.datadisplay.console;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConsoleUtilities {

	private ConsoleUtilities() {
	}

	// expected [num0, num1, num2, num3] NOTE: spaces don't matter
	public static List<Double> inputToList(String input) {
		String tmp = input.replaceAll("(\\[|\\])", "");
		String[] vals = tmp.split(",\\s*");
		List<Double> nums = new ArrayList<Double>();
		for (String s : vals) {
			try {
				nums.add(Double.parseDouble(s));
			} catch (NumberFormatException ex) {
				nums.add(0.0);
			}
		}
		return nums;
	}

	// a+2|a=1 -> 1+2
	public static String replaceVarWithValue(String input, Map<String, Double> vars) {
		String result = "";

		Pattern p = Pattern.compile("\\s*((\\d*(\\.?\\d+)?)|([\\+\\-\\*/]))\\s*");
		Matcher m = p.matcher(input);
		while (m.find()) {
			String term = m.group();
			if (vars.containsKey(term)) {
				term = vars.get(term).toString();
			}
			result += term;
		}

		return result;
	}
	
	// expecting format <command> -<key> ... <key>="<value>" ...
	public static List<String> getArgs(String input) {
		List<String> sep = new ArrayList<String>();
		Matcher m = Pattern.compile("(?:[^\\s\"]+|\"[^\"]*\")+").matcher(input);
		while(m.find()){
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

}
