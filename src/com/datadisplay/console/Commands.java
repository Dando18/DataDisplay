package com.datadisplay.console;

import java.awt.Desktop;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.datadisplay.BarChart;
import com.datadisplay.BoxAndWhiskerPlot;
import com.datadisplay.CartesianGraph;
import com.datadisplay.DataDisplay;
import com.datadisplay.ImageView;
import com.datadisplay.MathUtilities;
import com.datadisplay.PieChart;

public class Commands {

	private Commands() {
	}

	public static Map<String, CommandInterface> getCommands(ConsoleInput ci) {
		Map<String, CommandInterface> commands = new HashMap<String, CommandInterface>();
		commands.put("quit", new CommandInterface() {
			@Override
			public String execute(List<String> args) {
				System.exit(0);
				return "";
			}
		});
		commands.put("restart", new CommandInterface() {
			@Override
			public String execute(List<String> args) {
				try {
					ci.cg.dispose();
					Thread.sleep(1000);
					ConsoleGUI.main(null);
				} catch (InterruptedException ex) {
					// eh
				}
				return "successful";
			}
		});
		commands.put("clear", new CommandInterface() {
			@Override
			public String execute(List<String> args) {
				ci.cg.clear();
				return "";
			}
		});
		commands.put("skip", new CommandInterface() {
			@Override
			public String execute(List<String> args) {
				if (args.size() >= 1) {
					try {
						int lines = Integer.parseInt(args.get(0));
						for (int i = 0; i < lines; i++)
							ci.cg.write(" ");
					} catch (NumberFormatException e) {
						ci.cg.write(" ");
					}
				} else {
					ci.cg.write(" ");
				}
				return "";
			}
		});
		commands.put("stack", new CommandInterface() {
			@Override
			public String execute(List<String> args) {
				String resp = "";
				if (args.contains("-c")) {
					ci.prev.clear();
					resp = "";
				} else if (args.contains("-p")) {
					resp = "command stack(" + ci.prev.size() + "):\n";
					for (int i = 0; i < ci.prev.size(); i++) {
						resp += "   -" + ci.prev.get(i) + "\n";
					}
				} else {
					resp = ConsoleInput.HELP + "stack -<arg>\n" + "\t-c : clear command stack\n"
							+ "\t-p : print command stack\n" + "\t-h : help";
				}
				return resp;
			}
		});
		commands.put("sum", new CommandInterface() {
			@Override
			public String execute(List<String> args) {

				if (args.size() == 1 && ci.var_lists.containsKey(args.get(0))) {
					return "" + MathUtilities.sum(ci.var_lists.get(args.get(0)));
				} else if (Pattern.matches("\\[(\\d*(\\.?\\d+)?)(,\\s*\\d*(\\.?\\d+)?)*\\]", args.get(0))) {
					return "" + MathUtilities.sum(ConsoleUtilities.inputToList(args.get(0)));
				}

				return "";
			}
		});
		commands.put("mean", new CommandInterface() {
			@Override
			public String execute(List<String> args) {
				if (args.size() == 1 && ci.var_lists.containsKey(args.get(0))) {
					return "" + MathUtilities.mean(ci.var_lists.get(args.get(0)));
				} else if (Pattern.matches("\\[(\\d*(\\.?\\d+)?)(,\\s*\\d*(\\.?\\d+)?)*\\]", args.get(0))) {
					return "" + MathUtilities.mean(ConsoleUtilities.inputToList(args.get(0)));
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
		commands.put("barchart", new CommandInterface() {
			@Override
			public String execute(List<String> args) {
				String response = "";

				DataDisplay dd = new DataDisplay();
				dd.getFrame().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				BarChart bc = dd.showBarChart();
				bc.showBarValues();

				for (String s : args) {
					if (ci.var_lists.containsKey(s)) {
						int count = 0;
						for (Double d : ci.var_lists.get(s)) {
							bc.addValue(d, s + "_" + count++);
						}
						bc.showBarTitles();
					} else if (Pattern.matches("\\[(\\d*(\\.?\\d+)?)(,\\s*\\d*(\\.?\\d+)?)*\\]", s)) {
						List<Double> l = ConsoleUtilities.inputToList(s);
						bc.addValues(l);
					} else if (Pattern.matches("title=\"\\s*[A-Za-z0-9,;:\\s]+\\s*\"", s)) {
						bc.setTitle(s.split("\"")[1]);
					} else if ("-m".equals(s)) {
						dd.getFrame().setState(Frame.ICONIFIED);
					} else if ("-f".equals(s)) {
						dd.getFrame().setSize(Toolkit.getDefaultToolkit().getScreenSize());
					} else if (Pattern.matches("bg=\".+\"", s)) {
						bc.setBackground(ConsoleUtilities.stringToColor(s.split("\"")[1]));
					} else if (Pattern.matches("dump=\".+\"", s)) {
						if (!ConsoleUtilities.dump(s.split("\"")[1], bc)) {
							response += "\n could not dump to file";
						}
					}
				}

				return response;
			}
		});
		commands.put("piechart", new CommandInterface() {
			@Override
			public String execute(List<String> args) {
				String response = "";

				DataDisplay dd = new DataDisplay();
				dd.getFrame().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				PieChart pc = dd.showPieChart();

				for (String s : args) {
					if (ci.var_lists.containsKey(s)) {
						int count = 0;
						List<Double> l = ci.var_lists.get(s);
						if (MathUtilities.sum(l) > 1.0) {
							return "sum of list must be > 0.0 and <= 1.0";
						}
						for (Double d : l) {
							pc.addValue(s + "_" + count++, d);
						}
					} else if (Pattern.matches("\\[(\\d*(\\.?\\d+)?)(,\\s*\\d*(\\.?\\d+)?)*\\]", s)) {
						List<Double> l = ConsoleUtilities.inputToList(s);
						pc.addValues(l);
					} else if (Pattern.matches("title=\"\\s*[A-Za-z0-9,;:\\s]+\\s*\"", s)) {
						pc.setTitle(s.split("\"")[1]);
					} else if ("-m".equals(s)) {
						dd.getFrame().setState(Frame.ICONIFIED);
					} else if ("-f".equals(s)) {
						dd.getFrame().setSize(Toolkit.getDefaultToolkit().getScreenSize());
					} else if (Pattern.matches("bg=\".+\"", s)) {
						pc.setBackground(ConsoleUtilities.stringToColor(s.split("\"")[1]));
					} else if (Pattern.matches("dump=\".+\"", s)) {
						if (!ConsoleUtilities.dump(s.split("\"")[1], pc)) {
							response += "\n could not dump to file";
						}
					}
				}

				return response;
			}
		});
		commands.put("boxplot", new CommandInterface() {
			@Override
			public String execute(List<String> args) {
				String response = "";

				DataDisplay dd = new DataDisplay();
				dd.getFrame().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				BoxAndWhiskerPlot bawp = dd.showBoxAndWhiskerPlot();

				for (String s : args) {
					if (ci.var_lists.containsKey(s)) {
						bawp.addPlot(ci.var_lists.get(s));
					} else if (Pattern.matches("\\[(\\d*(\\.?\\d+)?)(,\\s*\\d*(\\.?\\d+)?)*\\]", s)) {
						List<Double> l = ConsoleUtilities.inputToList(s);
						bawp.addPlot(l);
					} else if (Pattern.matches("title=\"\\s*[A-Za-z0-9,;:\\s]+\\s*\"", s)) {
						bawp.setTitle(s.split("\"")[1]);
					} else if ("-m".equals(s)) {
						dd.getFrame().setState(Frame.ICONIFIED);
					} else if ("-f".equals(s)) {
						dd.getFrame().setSize(Toolkit.getDefaultToolkit().getScreenSize());
					} else if (Pattern.matches("bg=\".+\"", s)) {
						bawp.setBackground(ConsoleUtilities.stringToColor(s.split("\"")[1]));
					} else if (Pattern.matches("dump=\".+\"", s)) {
						if (!ConsoleUtilities.dump(s.split("\"")[1], bawp)) {
							response += "\n could not dump to file";
						}
					}
				}

				return response;
			}
		});
		commands.put("image", new CommandInterface() {
			@Override
			public String execute(List<String> args) {
				String response = "";

				DataDisplay dd = new DataDisplay();
				dd.getFrame().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				ImageView iv = dd.showImageView();

				for (String s : args) {
					if (Pattern.matches("[\\w,\\s-]+\\.[A-Za-z]{3}", s)) {
						try {
							BufferedImage bi = ImageIO.read(new File(s));
							iv.showImage(bi);
						} catch (IOException ex) {
							response = "file not found";
						}
					}
				}

				return response;
			}
		});
		commands.put("google", new CommandInterface() {
			@Override
			public String execute(List<String> args) {
				if (args.size() >= 1 && Desktop.isDesktopSupported()) {
					try {
						Desktop.getDesktop().browse(new URI("https://google.com/#q=" + args.get(0)));
						return "";
					} catch (IOException | URISyntaxException ex) {
						return ConsoleInput.ERROR + "malformed url";
					}
				} else {
					return "could not open link";
				}
			}
		});
		commands.put("windowsize", new CommandInterface() {
			@Override
			public String execute(List<String> args) {
				return "w: " + ci.cg.getWidth() + "   h: " + ci.cg.getHeight();
			}
		});
		commands.put("version", new CommandInterface() {
			@Override
			public String execute(List<String> args) {
				return String.format("%X", ci.cg.getUID());
			}
		});
		commands.put("os", new CommandInterface() {
			@Override
			public String execute(List<String> args) {
				return System.getProperty("os.name");
			}
		});
		commands.put("udp", new CommandInterface() {
			@Override
			public String execute(List<String> args) {
				String ip = "";
				int port = -1;
				int rep = 1;
				byte[] bytes = { 0 };
				boolean verbose = false;
				boolean flood = false;

				for (String s : args) {
					if ("-v".equals(s)) {
						verbose = true;
					} else if ("-flood".equals(s)) {
						flood = true;
					} else if (Pattern.matches("ip=\".+\"", s)) {
						ip = s.split("\"")[1];
					} else if (Pattern.matches("port=\".+\"", s)) {
						try {
							port = Integer.parseInt(s.split("\"")[1]);
						} catch (NumberFormatException e) {
							return ConsoleInput.ERROR + "invalid port number";
						}
					} else if (Pattern.matches("data=\".+\"", s)) {
						bytes = s.split("\"")[1].getBytes();
					} else if (Pattern.matches("data=\\[(\\d*(\\.?\\d+)?)(,\\s*\\d*(\\.?\\d+)?)*\\]", s)) {
						List<Double> in = ConsoleUtilities.inputToList(s.split("=")[1]);
						bytes = new byte[in.size()];
						for (int i = 0; i < bytes.length; i++) {
							bytes[i] = (byte) in.get(i).doubleValue();
						}
					} else if (Pattern.matches("rep=\".+\"", s)) {
						try {
							rep = Integer.parseInt(s.split("\"")[1]);
						} catch (NumberFormatException e) {
							return ConsoleInput.ERROR + "invalid rep";
						}
					} else if ("-h".equals(s)) {
						return ConsoleInput.HELP + "(* mandatory) udp <options>\n" + "\t*ip=\"<ip_address>\"\n"
								+ "\t*port=\"<port_number>\"\n"
								+ "\trep=\"<number_of_times_to_send>\" (-1 for continuous)\n" + "\t-v verbose\n"
								+ "\t-flood change port every packet sent";
					}
				}
				if (!"".equals(ip) && port != -1) {
					try {
						int port_tmp = port;
						InetAddress ia = InetAddress.getByName(ip);
						DatagramPacket dp = new DatagramPacket(bytes, bytes.length, ia, port);
						DatagramSocket ds = new DatagramSocket();
						if (rep != -1) {
							for (int i = 0; i < rep; i++) {
								if (flood && i != 0) {
									port_tmp = (int) (Math.random() * (65536 - 1)) + 1;
									dp = new DatagramPacket(bytes, bytes.length, ia, port_tmp);
								}
								ds.send(dp);
								if (verbose)
									ci.cg.write(" - (" + i + ") sending " + bytes.length + " bytes to " + ip
											+ " at port " + port_tmp);
							}
						} else {
							int i = 0;
							while (true) {
								ds.send(dp);
								if (verbose)
									ci.cg.write(" - (" + (i++) + ") sending " + bytes.length + " bytes to " + ip
											+ " at port " + port);
								Thread.sleep(10l);
							}
						}
						ds.close();
					} catch (UnknownHostException e) {
						return ConsoleInput.ERROR + "unknown ip/host";
					} catch (IOException e) {
						return ConsoleInput.ERROR + "could not send packet";
					} catch (InterruptedException e) {
						return ConsoleInput.ERROR + "could not continually send packets";
					} catch (RuntimeException e) {
						return ConsoleInput.ERROR + "something else went wrong";
					}
				} else {
					return ConsoleInput.HELP + "(* mandatory) udp <options>\n" + "\t*ip=\"<ip_address>\"\n"
							+ "\t*port=\"<port_number>\"\n"
							+ "\trep=\"<number_of_times_to_send>\" (-1 for continuous)\n" + "\t-v verbose\n"
							+ "\t-flood change port every packet sent";
				}

				return "";
			}
		});
		commands.put("help", new CommandInterface() {
			@Override
			public String execute(List<String> args) {
				String resp = "";
				int max_len = 5;
				List<String> keys = new ArrayList<String>(commands.keySet());
				Collections.sort(keys);
				int num_cols = keys.size() / max_len;
				int count = 0;

				for (String s : keys) {
					resp += "\t-" + s;
					if (count >= num_cols) {
						count = 0;
						resp += "\n";
					} else {
						count++;
					}
				}

				return resp;
			}
		});
		return commands;
	}

}
