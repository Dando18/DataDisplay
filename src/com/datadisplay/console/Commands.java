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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
		commands.put("stddev", new CommandInterface() {
			@Override
			public String execute(List<String> args) {
				if (args.size() == 1 && ci.var_lists.containsKey(args.get(0))) {
					return "" + MathUtilities.std_dev(ci.var_lists.get(args.get(0)));
				} else if (Pattern.matches("\\[(\\d*(\\.?\\d+)?)(,\\s*\\d*(\\.?\\d+)?)*\\]", args.get(0))) {
					return "" + MathUtilities.std_dev(ConsoleUtilities.inputToList(args.get(0)));
				}
				return "";
			}
		});
		commands.put("len", new CommandInterface() {
			@Override
			public String execute(List<String> args) {
				if (args.size() == 1 && ci.var_lists.containsKey(args.get(0))) {
					return "" + ci.var_lists.get(args.get(0)).size();
				} else if (Pattern.matches("\\[(\\d*(\\.?\\d+)?)(,\\s*\\d*(\\.?\\d+)?)*\\]", args.get(0))) {
					return "" + ConsoleUtilities.inputToList(args.get(0)).size();
				}
				return "";
			}
		});
		commands.put("first", new CommandInterface() {
			@Override
			public String execute(List<String> args) {
				if (args.size() == 1 && ci.var_lists.containsKey(args.get(0))) {
					return "" + ci.var_lists.get(args.get(0)).get(0);
				} else if (Pattern.matches("\\[(\\d*(\\.?\\d+)?)(,\\s*\\d*(\\.?\\d+)?)*\\]", args.get(0))) {
					return "" + ConsoleUtilities.inputToList(args.get(0)).get(0);
				}
				return "";
			}
		});
		commands.put("last", new CommandInterface() {
			@Override
			public String execute(List<String> args) {
				if (args.size() == 1 && ci.var_lists.containsKey(args.get(0))) {
					List<Double> l = ci.var_lists.get(args.get(0));
					return "" + l.get(l.size() - 1);
				} else if (Pattern.matches("\\[(\\d*(\\.?\\d+)?)(,\\s*\\d*(\\.?\\d+)?)*\\]", args.get(0))) {
					List<Double> l = ConsoleUtilities.inputToList(args.get(0));
					return "" + l.get(l.size() - 1);
				}
				return "";
			}
		});
		commands.put("min", new CommandInterface() {
			@Override
			public String execute(List<String> args) {
				if (args.size() == 1 && ci.var_lists.containsKey(args.get(0))) {
					return "" + MathUtilities.min(ci.var_lists.get(args.get(0)));
				} else if (Pattern.matches("\\[(\\d*(\\.?\\d+)?)(,\\s*\\d*(\\.?\\d+)?)*\\]", args.get(0))) {
					return "" + MathUtilities.min(ConsoleUtilities.inputToList(args.get(0)));
				}
				return "";
			}
		});
		commands.put("max", new CommandInterface() {
			@Override
			public String execute(List<String> args) {
				if (args.size() == 1 && ci.var_lists.containsKey(args.get(0))) {
					return "" + MathUtilities.max(ci.var_lists.get(args.get(0)));
				} else if (Pattern.matches("\\[(\\d*(\\.?\\d+)?)(,\\s*\\d*(\\.?\\d+)?)*\\]", args.get(0))) {
					return "" + MathUtilities.max(ConsoleUtilities.inputToList(args.get(0)));
				}
				return "";
			}
		});
		commands.put("sqrt", new CommandInterface() {
			@Override
			public String execute(List<String> args) {
				if (args.size() == 0)
					return "";
				if (ci.vars.containsKey(args.get(0))) {
					return "" + Math.sqrt(ci.vars.get(args.get(0)));
				} else if (Pattern.matches("\\d*(\\.?\\d+)?", args.get(0))) {
					double num;
					try {
						num = Double.parseDouble(args.get(0));
					} catch (NumberFormatException ex) {
						return ConsoleInput.ERROR + "unable to parse input";
					}
					return "" + Math.sqrt(num);
				} else if (ci.var_lists.containsKey(args.get(0))) {
					List<Double> l = new ArrayList<Double>();
					for (Double d : ci.var_lists.get(args.get(0))) {
						l.add(Math.sqrt(d));
					}
					return "" + ConsoleUtilities.listToString(l);
				} else if (Pattern.matches("\\[(\\d*(\\.?\\d+)?)(,\\s*\\d*(\\.?\\d+)?)*\\]", args.get(0))) {
					List<Double> l = new ArrayList<Double>();
					for (Double d : ConsoleUtilities.inputToList(args.get(0))) {
						l.add(Math.sqrt(d));
					}
					return "" + ConsoleUtilities.listToString(l);
				}
				return "";
			}
		});
		commands.put("isprime", new CommandInterface() {
			@Override
			public String execute(List<String> args) {
				if (args.size() == 0)
					return "";
				if (ci.vars.containsKey(args.get(0))) {
					double val = ci.vars.get(args.get(0));
					if (val != Math.floor(val))
						return "only integer values";
					return "" + MathUtilities.isPrime(ci.vars.get(args.get(0)).intValue());
				} else if (Pattern.matches("\\d*", args.get(0))) {
					int num;
					try {
						num = Integer.parseInt(args.get(0));
					} catch (NumberFormatException ex) {
						return ConsoleInput.ERROR + "unable to parse input";
					}
					return "" + MathUtilities.isPrime(num);
				} else if (ci.var_lists.containsKey(args.get(0))) {
					List<Boolean> l = new ArrayList<Boolean>();
					for (Double d : ci.var_lists.get(args.get(0))) {
						double val = d.doubleValue();
						if (val != Math.floor(val))
							return "only integer values";
						l.add(MathUtilities.isPrime(d.intValue()));
					}
					return "" + ConsoleUtilities.listToString(l);
				} else if (Pattern.matches("\\[(\\d*)(,\\s*\\d*)*\\]", args.get(0))) {
					List<Boolean> l = new ArrayList<Boolean>();
					for (Double d : ConsoleUtilities.inputToList(args.get(0))) {
						l.add(MathUtilities.isPrime(d.intValue()));
					}
					return "" + ConsoleUtilities.listToString(l);
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
							return ConsoleInput.ERROR + "invalid port number (" + s.split("\"")[1] + ")";
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
								+ "\t*port=\"<port_number>\"\n" + "\tdata=\"<data_to_send>\"\n"
								+ "\trep=\"<number_of_times_to_send>\" (-1 for continuous)\n" + "\t-v verbose\n"
								+ "\t-flood change port every packet sent";
					}
				}
				if (!"".equals(ip) && port != -1) {
					DatagramSocket ds = null;
					try {
						int port_tmp = port;
						InetAddress ia = InetAddress.getByName(ip);
						DatagramPacket dp = new DatagramPacket(bytes, bytes.length, ia, port);
						ds = new DatagramSocket();
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
							final DatagramPacket DP_TMP = dp;
							final boolean VERBOSE_TMP = verbose;
							final byte[] BYTES_TMP = bytes;
							final String IP_TMP = ip;
							final int PORT_TMP = port;
							Thread t = new Thread(new Runnable() {
								@Override
								public void run() {
									DatagramSocket ds_t = null;
									try {
										ds_t = new DatagramSocket();
										int i = 0;
										while (ci.run) {
											ds_t.send(DP_TMP);
											if (VERBOSE_TMP)
												ci.cg.write(" - (" + (i++) + ") sending " + BYTES_TMP.length
														+ " bytes to " + IP_TMP + " at port " + PORT_TMP);
											Thread.sleep(3000l);
										}
									} catch (InterruptedException | IOException ex) {
										ci.cg.write(ConsoleInput.ERROR + "could not send packet");
										ex.printStackTrace();
									} finally {
										if(ds_t != null)
											ds_t.close();
									}
								}
							});
							ci.cur = t;
							ci.run = true;
							t.start();
						}
					} catch (UnknownHostException e) {
						return ConsoleInput.ERROR + "unknown ip/host";
					} catch (IOException e) {
						e.printStackTrace();
						return ConsoleInput.ERROR + "could not send packet";
					} catch (RuntimeException e) {
						return ConsoleInput.ERROR + "something else went wrong";
					} finally {
						if (ds != null)
							ds.close();
					}
				} else {
					return ConsoleInput.HELP + "(* mandatory) udp <options>\n" + "\t*ip=\"<ip_address>\"\n"
							+ "\t*port=\"<port_number>\"\n" + "\tdata=\"<data_to_send>\"\n"
							+ "\trep=\"<number_of_times_to_send>\" (-1 for continuous)\n" + "\t-v verbose\n"
							+ "\t-flood change port every packet sent";
				}

				return "";
			}
		});
		commands.put("udpopen", new CommandInterface() {
			@Override
			public String execute(List<String> args) {
				int port = -1;
				int size = 1024;
				boolean verbose = false;

				for (String s : args) {
					if ("-v".equals(s)) {
						verbose = true;
					} else if (Pattern.matches("port=\".+\"", s)) {
						try {
							port = Integer.parseInt(s.split("\"")[1]);
						} catch (NumberFormatException e) {
							return ConsoleInput.ERROR + "invalid port number (" + s.split("\"")[1] + ")";
						}
					} else if (Pattern.matches("size=\".+\"", s)) {
						try {
							size = Integer.parseInt(s.split("\"")[1]);
						} catch (NumberFormatException e) {
							return ConsoleInput.ERROR + "invalid size number (" + s.split("\"")[1] + ")";
						}
					} else if ("-h".equals(s)) {
						return ConsoleInput.HELP + "(* mandatory) udp <options>\n" + "\t*port=\"<port_number>\"\n"
								+ "\tsize=\"<size_of_incoming_packet>\"\n" + "\t-v verbose\n";
					}
				}
				if (port != -1) {
					final int PORT_TMP = port;
					final int SIZE_TMP = size;
					final boolean VERBOSE_TMP = verbose;
					try {
						port = 5;
						Thread t = new Thread(new Runnable() {
							@Override
							public void run() {
								DatagramSocket ds = null;
								try {
									ds = new DatagramSocket(PORT_TMP);
									byte[] data = new byte[SIZE_TMP];
									ci.cg.write(String.format("Listening on udp:%s:%d%n",
											InetAddress.getLocalHost().getHostAddress(), PORT_TMP));
									DatagramPacket dp = new DatagramPacket(data, data.length);

									while (ci.run) {
										ds.receive(dp);
										String sentence = new String(dp.getData(), 0, dp.getLength());
										if (VERBOSE_TMP)
											ci.cg.write("RECEIVED: " + sentence);
									}
								} catch (IOException e) {
									ci.cg.write(ConsoleInput.ERROR + "could not open socket");
								} finally {
									if (ds != null)
										ds.close();
								}
							}
						});
						ci.cur = t;
						ci.run = true;
						t.start();
					} catch (RuntimeException e) {
						return ConsoleInput.ERROR + "something else went wrong";
					}
				} else {
					return ConsoleInput.HELP + "(* mandatory) udp <options>\n" + "\t*port=\"<port_number>\"\n"
							+ "\tsize=\"<size_of_incoming_packet>\"\n" + "\t-v verbose\n";
				}

				return "";
			}
		});
		commands.put("ip", new CommandInterface() {
			@Override
			public String execute(List<String> args) {
				try {
					if (args.size() > 0 && ConsoleUtilities.orEquals(args.get(0), "-n", "-name", "-hostname")) {
						return InetAddress.getLocalHost().getHostName();
					} else {
						return InetAddress.getLocalHost().getHostAddress();
					}
				} catch (UnknownHostException e) {
					return ConsoleInput.ERROR + "could not retrieve local ip";
				}
			}
		});
		commands.put("stop", new CommandInterface() {
			@Override
			public String execute(List<String> args) {
				String name = ci.cur.getName();
				ci.run = false;
				ci.cur = null;
				return name + " successfully stopped";
			}
		});
		commands.put("time", new CommandInterface() {
			@Override
			public String execute(List<String> args) {
				return new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
			}
		});
		commands.put("_", new CommandInterface() {
			@Override
			public String execute(List<String> args) {
				for (String s : args) {
					if ("-m".equals(s)) {
						ci.cg.master = true;
						return "master_mode: on";
					} else if ("-n".equals(s)) {
						ci.cg.master = false;
						return "master_mode: off";
					} else if ("-t".equals(s)) {
						ci.cg.master = !ci.cg.master;
						return "master_mode: " + ((ci.cg.master) ? "on" : "off");
					}
				}
				return "master_mode: " + ((ci.cg.master) ? "on" : "off");
			}
		});
		commands.put("help", new CommandInterface() {
			@Override
			public String execute(List<String> args) {
				String resp = "";
				int max_len = 6;
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
