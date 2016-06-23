package com.datadisplay.console;

import java.awt.Desktop;
import java.awt.Frame;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;

import com.datadisplay.BarChart;
import com.datadisplay.CartesianGraph;
import com.datadisplay.DataDisplay;
import com.datadisplay.MathUtilities;

public class ConsoleInput {

	public static String begin = ">>> "; // beginning of every input line
	public static final String HELP = "HELP: ";
	public static final String ERROR = "[-] ";

	private ConsoleGUI cg;

	List<String> prev; // previous commands entered
	Map<String, CommandInterface> commands; // "<command_name, function>"
	Map<String, Double> vars; // "<var_name, value>"
	Map<String, List<Double>> var_lists; // "<var_name, values>"

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

		String command = input.split(" ")[0];
		if (commands.containsKey(command)) {
			response = commands.get(command).execute(ConsoleUtilities.getArgs(input));
			cg.write(response);
			return;
		}
		
		response = testURL(input);
		response = testMath(input);

		cg.write(response);
	}
	
	private String testURL(String input){
		String tmp = input.replace(" ", "");
		if(Pattern.matches("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]", tmp)){
			if(Desktop.isDesktopSupported()){
				try{
					Desktop.getDesktop().browse(new URI(input));
				}catch(IOException | URISyntaxException ex){
					return "malformed url";
				}
			}
		}
		return "";
	}

	private String testMath(String input) {
		String response = input;

		String tmp = input.replace(" ", "");
		
		if (vars.containsKey(tmp)) {
			response = Double.toString(vars.get(tmp));
			return response;	
		}
		if(var_lists.containsKey(tmp)){
			response = "[";
			List<Double> list = var_lists.get(tmp);
			for(int i=0; i<list.size()-1; i++){
				response += list.get(i)+", ";
			}
			return response + list.get(list.size()-1) +"]";
		}
		
		if(Pattern.matches("[a-z]+[a-z0-9]*\\[(\\-?\\d+(:\\d+)?|[a-z]+[a-z0-9]*)]", tmp)){
			String[] parts = tmp.split("\\[");
			if(var_lists.containsKey(parts[0])){
				parts[1] = parts[1].replace("]", "");
				try{
					int index = Integer.parseInt(parts[1]);
					return ""+var_lists.get(parts[0]).get(index);
				}catch(NumberFormatException ex){
					return "index must be an integer value";
				}catch(IndexOutOfBoundsException ex){
					return "index out of bounds";
				}
			}else{
				return "unknown variable";
			}
		}

		if (Pattern.matches("([a-z]+[a-z0-9]*|\\d*(\\.?\\d+)?)([\\+\\-/\\*]|[=]{2})([a-z]+[a-z0-9]*|\\d*(\\.?\\d+)?)",
				tmp)) {
			String[] var_keys = tmp.split("([\\+\\-/\\*]|[=]{2})");

			int inc = 1;
			if (tmp.contains("=="))
				inc = 2;
			String op = tmp.substring(var_keys[0].length(), var_keys[0].length() + inc);
			double a, b;   // a op. b   a-left b-right
			if (vars.containsKey(var_keys[0])) {
				a = vars.get(var_keys[0]);
			} else {
				try {
					a = Double.parseDouble(var_keys[0]);
				} catch (NumberFormatException ex) {
					return ERROR + " syntax error";
				}
			}
			if (vars.containsKey(var_keys[1])) {
				b = vars.get(var_keys[1]);
			} else {
				try {
					b = Double.parseDouble(var_keys[1]);
				} catch (NumberFormatException ex) {
					return ERROR + " syntax error";
				}
			}
			switch (op) {
			case "+":
				return "" + (a + b);
			case "-":
				return "" + (a - b);
			case "*":
				return "" + (a * b);
			case "/":
				return "" + (a / b);
			case "==":
				return "" + (a == b);
			}
		}

		Pattern var = Pattern.compile(
				"[a-z]+[a-z0-9]*[\\+\\-/\\*=](\\[(\\d*(\\.?\\d+)?)(,\\s*\\d*(\\.?\\d+)?)*\\]|[a-z0-9]+|\\d*(\\.?\\d+)?)");
		Matcher m = var.matcher(tmp);
		if (m.matches()) {
			response = "**  " + input;
			// assignment
			if (tmp.contains("=")) {
				String[] eq = tmp.split("[\\+\\-/\\*=]=?");
				String left = eq[0];
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
				} else if (Pattern.matches("\\[(\\d*(\\.?\\d+)?)(,\\s*\\d*(\\.?\\d+)?)*\\]", eq[1])) {
					var_lists.put(left, ConsoleUtilities.inputToList(eq[1]));
				}

			}
		}

		return response;
	}

	// initialize commands, keep at bottom (cause it's long duh)
	private void initCommands() {
		commands.put("quit", new CommandInterface() {
			@Override
			public String execute(List<String> args) {
				System.exit(0);
				return "";
			}
		});
		commands.put("clear", new CommandInterface() {
			@Override
			public String execute(List<String> args) {
				cg.clear();
				return "";
			}
		});
		commands.put("stack", new CommandInterface() {
			@Override
			public String execute(List<String> args) {
				String resp = "";
				if (args.contains("-c")) {
					prev.clear();
					resp = "";
				} else if (args.contains("-p")) {
					resp = "command stack(" + prev.size() + "):\n";
					for (int i = 0; i < prev.size(); i++) {
						resp += "   -" + prev.get(i) + "\n";
					}
				} else {
					resp = HELP + "stack -<arg>\n" + "\t-c : clear command stack\n" + "\t-p : print command stack\n"
							+ "\t-h : help";
				}
				return resp;
			}
		});
		commands.put("sum", new CommandInterface(){
			@Override
			public String execute(List<String> args) {
				
				if(args.size()==1 && var_lists.containsKey(args.get(0)) ){
					return ""+MathUtilities.sum(var_lists.get(args.get(0)));
				}else if(Pattern.matches("\\[(\\d*(\\.?\\d+)?)(,\\s*\\d*(\\.?\\d+)?)*\\]", args.get(0))){
					return ""+MathUtilities.sum(ConsoleUtilities.inputToList(args.get(0)));
				}
				
				return "";
			}
		});
		// generic plot method, should choose best plot
		commands.put("plot", new CommandInterface() {
			@Override
			public String execute(List<String> args) {
				String response = "";

				DataDisplay dd = new DataDisplay();
				dd.getFrame().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				CartesianGraph cg = dd.showCartesian();

				for (String s : args) {
					if (Pattern.matches("\\(\\-?\\d*(\\.?\\d+)?,\\-?\\d*(\\.?\\d+)?\\)", s)) {
						String[] nums = s.split(",");
						nums[0] = nums[0].replace("(", "");
						nums[1] = nums[1].replace(")", "");

						cg.plot(Double.parseDouble(nums[0]), Double.parseDouble(nums[1]));
					}
				}

				return response;
			}
		});
		commands.put("barchart", new CommandInterface(){
			@Override
			public String execute(List<String> args) {
				String response = "";
				
				DataDisplay dd = new DataDisplay();
				dd.getFrame().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				BarChart bc = dd.showBarChart();
				bc.showBarValues();
				
				for(String s : args){
					if(var_lists.containsKey(s)){
						int count = 0;
						for(Double d : var_lists.get(s)){
							bc.addValue(d, s+"_"+count++);
						}
						bc.showBarTitles();
					}else if(Pattern.matches("\\[(\\d*(\\.?\\d+)?)(,\\s*\\d*(\\.?\\d+)?)*\\]", s)){
						List<Double> l = ConsoleUtilities.inputToList(s);
						bc.addValues(l);
					}else if(Pattern.matches("title=\"\\s*[A-Za-z0-9,;:\\s]+\\s*\"", s)){
						bc.setTitle(s.split("\"")[1]);
					}else if("-m".equals(s)){
						dd.getFrame().setState(Frame.ICONIFIED);
					}else if("-f".equals(s)){
						dd.getFrame().setSize(Toolkit.getDefaultToolkit().getScreenSize());
					}
				}
				
				return response;
			}
		});
		commands.put("windowsize", new CommandInterface(){
			@Override
			public String execute(List<String> args) {
				return "w: "+cg.getWidth()+"   h: "+cg.getHeight();
			}
		});
		commands.put("version", new CommandInterface(){
			@Override
			public String execute(List<String> args) {
				return String.format("%X", cg.getUID());
			}
		});
	}

}
