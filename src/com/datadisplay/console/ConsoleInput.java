package com.datadisplay.console;

import java.awt.Color;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.datadisplay.MathUtilities;

public class ConsoleInput {

	public static String begin = ">>> "; // beginning of every input line
	public static Color begin_color = Color.BLACK;
	public static final String HELP = "HELP: ";
	public static final String ERROR = "[-] ";

	public ConsoleGUI cg;

	public List<String> prev; // previous commands entered
	public Map<String, CommandInterface> commands; // "<command_name, function>"
	public Map<String, Double> vars; // "<var_name, value>"
	public Map<String, List<Double>> var_lists; // "<var_name, values>"
	
	public Thread cur = null;
	public boolean run = false;

	public ConsoleInput(ConsoleGUI cg) {
		this.cg = cg;

		prev = new ArrayList<String>();
		commands = new HashMap<String, CommandInterface>();
		vars = new HashMap<String, Double>();
		var_lists = new HashMap<String, List<Double>>();

		initCommands();
	}

	public void parse(String input) {
		if (input == null)
			return;
		if ("".equals(input.trim()))
			return;
		String response = input;
		prev.add(input);

		String command = input.split("\\s+")[0];
		if (commands.containsKey(command)) {
			response = commands.get(command).execute(ConsoleUtilities.getArgs(input));
			cg.write(response);
			return;
		}

		response = testURL(input);
		response = testMath(input);

		cg.write(response);
	}

	private String testURL(String input) {
		String tmp = input.replace(" ", "");
		if (Pattern.matches("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]", tmp)) {
			if (Desktop.isDesktopSupported()) {
				try {
					Desktop.getDesktop().browse(new URI(input));
				} catch (IOException | URISyntaxException ex) {
					return ERROR + "malformed url";
				}
			}
		}
		return "";
	}

	private String testMath(String input) {
		String response = input;

		String tmp = input.replace(" ", "");

		if (vars.containsKey(tmp)) {
			return Double.toString(vars.get(tmp));
		}
		if (var_lists.containsKey(tmp)) {
			return ConsoleUtilities.listToString(var_lists.get(tmp));
		}

		if (Pattern.matches("[a-z]+[a-z0-9]*\\[(\\-?\\d+(:\\d+)?|[a-z]+[a-z0-9]*)]", tmp)) {
			String[] parts = tmp.split("\\[");
			if (var_lists.containsKey(parts[0])) {
				parts[1] = parts[1].replace("]", "");
				try {
					int index = Integer.parseInt(parts[1]);
					return "" + var_lists.get(parts[0]).get(index);
				} catch (NumberFormatException ex) {
					return ERROR + "index must be an integer value";
				} catch (IndexOutOfBoundsException ex) {
					return ERROR + "index out of bounds";
				}
			} else {
				return ERROR + "unknown variable";
			}
		}

		if (Pattern.matches("([a-z]+[a-z0-9]*|\\d*(\\.?\\d+)?)([\\+\\-/\\*]|[=]{2})([a-z]+[a-z0-9]*|\\d*(\\.?\\d+)?)",
				tmp)) {
			String[] var_keys = tmp.split("([\\+\\-/\\*]|[=]{2})");

			int inc = 1;
			if (tmp.contains("=="))
				inc = 2;
			String op = tmp.substring(var_keys[0].length(), var_keys[0].length() + inc);
			double a = 0, b = 0; // a op. b a-left b-right
			List<Double> a_l = null, b_l = null; // a op. b a-left b-right
			boolean left_l = false, right_l = false;
			if (vars.containsKey(var_keys[0])) {
				a = vars.get(var_keys[0]);
			} else if (var_lists.containsKey(var_keys[0])) {
				a_l = var_lists.get(var_keys[0]);
				left_l = true;
			} else {
				try {
					a = Double.parseDouble(var_keys[0]);
				} catch (NumberFormatException ex) {
					return ERROR + " syntax error";
				}
			}
			if (vars.containsKey(var_keys[1])) {
				b = vars.get(var_keys[1]);
			} else if (var_lists.containsKey(var_keys[1])) {
				b_l = var_lists.get(var_keys[1]);
				right_l = true;
			} else {
				try {
					b = Double.parseDouble(var_keys[1]);
				} catch (NumberFormatException ex) {
					return ERROR + " syntax error";
				}
			}
			switch (op) {
			case "+":
				if (left_l && right_l) {

				} else if (left_l) {
					return "" + ConsoleUtilities.listToString(MathUtilities.addToList(a_l, b));
				} else if (right_l) {
					return "" + ConsoleUtilities.listToString(MathUtilities.addToList(b_l, a));
				} else {
					return "" + (a + b);
				}
			case "-":
				if (left_l && right_l) {

				} else if (left_l) {
					return "" + ConsoleUtilities.listToString(MathUtilities.addToList(a_l, -b));
				} else if (right_l) {
					// a-b = (-b)+a
					return "" + ConsoleUtilities
							.listToString(MathUtilities.addToList(MathUtilities.multToList(b_l, -1), a));
				} else {
					return "" + (a - b);
				}
			case "*":
				if (left_l && right_l) {

				} else if (left_l) {
					return "" + ConsoleUtilities.listToString(MathUtilities.multToList(a_l, b));
				} else if (right_l) {
					return "" + ConsoleUtilities.listToString(MathUtilities.multToList(b_l, a));
				} else {
					return "" + (a * b);
				}
			case "/":
				if (left_l && right_l) {

				} else if (left_l) {
					return "" + ConsoleUtilities.listToString(MathUtilities.multToList(a_l, 1.0 / b));
				} else if (right_l) {
					// a/b =
					return "" + ConsoleUtilities.listToString(MathUtilities.multToList(b_l, 1.0 / a));
				} else {
					return "" + (a / b);
				}
			case "==":
				if (left_l && right_l) {
					return "" + a_l.equals(b_l);
				} else if (left_l) {
					return "" + ConsoleUtilities.listToString(MathUtilities.addToList(a_l, b));
				} else if (right_l) {
					return "" + ConsoleUtilities.listToString(MathUtilities.addToList(b_l, a));
				} else {
					return "" + (a == b);
				}
			}
		}

		Pattern var = Pattern.compile(
				"[a-z]+[a-z0-9]*[\\+\\-/\\*=](\\[((\\d*(\\.?\\d+)?|[a-z]+[a-zA-Z\\d]*)(,\\s*(\\d*(\\.?\\d+)?|[a-z]+[a-zA-Z\\d]*))*|\\d*(\\.?\\d+)?(:\\s*\\d*(\\.?\\d+)?){1,2})\\]|[a-z0-9]+|\\d*(\\.?\\d+)?)");
		Matcher m = var.matcher(tmp);
		if (m.matches()) {
			response = input;
			// assignment
			if (tmp.contains("=")) {
				String[] eq = tmp.split("[\\+\\-/\\*=]=?");
				String left = eq[0];
				eq[1] = ConsoleUtilities.replaceVarWithValue(eq[1], vars);
				if (Pattern.matches("\\d*(\\.?\\d+)?", eq[1])) {
					try {
						vars.put(left, Double.parseDouble(eq[1]));
					} catch (IndexOutOfBoundsException ex) {
						response = "ill formatted input";
					} catch (NumberFormatException ex) {
						response = "ill formatted number";
					}
				} else if (vars.containsKey(eq[1])) {
					try {
						vars.put(left, vars.get(eq[1]));
					} catch (NullPointerException ex) {
						response = "could not evaluate variable " + eq[1];
					}
				} else if (Pattern.matches("\\[((\\d*(\\.?\\d+)?)(,\\s*\\d*(\\.?\\d+)?)*|\\d*(\\.?\\d+)?(:\\s*\\d*(\\.?\\d+)?){1,2})\\]", eq[1])) {
					var_lists.put(left, ConsoleUtilities.inputToList(eq[1]));
				}

			}
		}
		if (Pattern.matches("\\s*[a-z]+[]a-z0-9]*\\s*=\\s*[\\+\\-\\*/\\(\\)\\d\\.a-zA-Z]+\\s*", tmp)) {
			try {
				String[] sides = input.split("\\s*=\\s*");
				String right = ConsoleUtilities.replaceVarWithValue(sides[1], vars);
				vars.put(sides[0], ConsoleUtilities.eval(right));
				response = "" + vars.get(sides[0]);
			} catch (IllegalArgumentException ex) {
				ex.printStackTrace();
			}
		}

		return response;
	}

	// initialize commands, keep at bottom (cause it's long duh)
	private void initCommands() {
		commands = Commands.getCommands(this);
	}

	public boolean isVar(String s) {
		return vars.containsKey(s) || var_lists.containsKey(s);
	}

	public boolean isCommand(String s) {
		return commands.containsKey(s);
	}
	
	public String getPreviousCommand(){
		return getPreviousCommand(0);
	}
	
	public String getPreviousCommand(int back){
		if(back < 0) back = 0;
		return prev.get(prev.size()-1-back);
	}
	
	public int getPreviousSize(){
		return prev.size();
	}

}
