package com.datadisplay.console;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.apache.commons.lang3.math.NumberUtils;

import com.apple.eawt.Application;

public class ConsoleGUI extends JFrame {
	private static final long serialVersionUID = (long) (Math.random() * 10000);

	public static final String TITLE = String.format("DataDisplay v.%X", serialVersionUID);
	public static final Dimension SIZE = new Dimension(550, 400);

	ConsoleInput ci;

	// panels
	private JPanel main;
	private JScrollPane sp; // for console textarea

	// text
	private JTextPane cons;
	private JTextField inp;

	// menu
	private JMenuBar mbar;
	private JMenu run;
	private JMenuItem open_and_run;

	// attributes
	private Font font;
	private Color font_color = Color.BLACK;
	private Color var_color = new Color(128, 0, 0);
	private Color num_color = new Color(0, 0, 128);

	// for index thru previous commands
	private int command_index = 0;

	// suggesting
	private boolean suggesting = false;
	private String suggested = "";

	// master mode opposed to normal mode
	public boolean master = false;

	public ConsoleGUI() {
		super();

		ci = new ConsoleInput(this);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setPreferredSize(SIZE);
		this.setMinimumSize(new Dimension(400, 300));
		this.setLocationRelativeTo(null);

		buildPanel();
		pack();

		this.setTitle(TITLE);
		this.setVisible(true);
		this.setResizable(true);

		buildPref();
	}

	private void buildPanel() {
		font = new Font(Font.MONOSPACED, Font.PLAIN, 12);

		main = new JPanel(new GridBagLayout()) {
			private static final long serialVersionUID = 1L;

			@Override
			public Dimension getPreferredSize() {
				return SIZE;
			}

			@Override
			public void paint(Graphics g) {
				super.paint(g);
				Graphics2D g2d = (Graphics2D) g.create();
				if (!"".equals(inp.getText())) {
					List<String> keys = new ArrayList<String>(ci.commands.keySet());
					Collections.sort(keys);
					for (String key : keys) {
						if (key.startsWith(inp.getText()) && !key.equals(inp.getText())
								&& !keys.contains(inp.getText())) {
							try {
								int pos = inp.getCaretPosition();
								Rectangle loc = inp.modelToView(pos);
								FontMetrics fm = g2d.getFontMetrics();
								int x = loc.x + 10;
								int y = cons.getHeight() - loc.y;
								int x_padding = 5;
								int y_padding = 2;

								g2d.setColor(Color.WHITE);
								g2d.fillRect(x, y, fm.stringWidth(key) + x_padding * 2, fm.getHeight() + y_padding * 2);
								g2d.setColor(Color.BLACK);
								g2d.drawRect(x, y, fm.stringWidth(key) + x_padding * 2, fm.getHeight() + y_padding * 2);
								g2d.setColor(Color.GRAY);
								g2d.drawString(key, x + x_padding, y + fm.getHeight() - y_padding);

								suggesting = true;
								suggested = key.substring(inp.getText().length());

								this.repaint();
								break;
							} catch (BadLocationException ex) {
								write(ConsoleInput.ERROR + "completion error");
							}
						} else {
							suggesting = false;
						}
					}
				}
			}

		};
		GridBagConstraints c = new GridBagConstraints();

		cons = new JTextPane() {
			private static final long serialVersionUID = 1L;

			@Override
			public String getToolTipText(MouseEvent e) {

				return "";
			}
		};
		cons.setToolTipText("");
		cons.setEditable(false);
		cons.setFont(font);
		cons.setText(ConsoleInput.begin);
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.5;
		c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = 0;
		sp = new JScrollPane();
		sp.setViewportView(cons);
		main.add(sp, c);

		inp = new JTextField();
		inp.setFont(font);
		inp.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_QUOTE) {
					if (inp.getText().length() > inp.getCaretPosition()
							&& inp.getText().charAt(inp.getCaretPosition()) == '"') {
						// if caret is not at end of string && caret is before quote ("dog|")
						int caret = inp.getCaretPosition();
						inp.setText(inp.getText().substring(0, caret)+inp.getText().substring(caret+1, inp.getText().length()));
						inp.setCaretPosition(caret);
					} else {
						// insert quote after caret
						int caret = inp.getCaretPosition();
						inp.setText(inp.getText().substring(0, inp.getCaretPosition()) + "\""
								+ inp.getText().substring(inp.getCaretPosition(), inp.getText().length()));
						inp.setCaretPosition(caret);
					}
				} else if (e.getKeyCode() == KeyEvent.VK_UP) {
					if (command_index <= ci.getPreviousSize() - 1)
						inp.setText(ci.getPreviousCommand(command_index++));
				} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					if (command_index != 0)
						inp.setText(ci.getPreviousCommand(--command_index));
				} else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					if (suggesting) {
						inp.setText(inp.getText() + suggested);
						suggesting = false;
						suggested = "";
					}
				}
			}
		});
		inp.addActionListener(e -> input(inp.getText()));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = 1;
		main.add(inp, c);

		mbar = new JMenuBar();
		run = new JMenu("Run");
		open_and_run = new JMenuItem("Run .txt File", KeyEvent.VK_R);
		open_and_run.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_R, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		open_and_run.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FileDialog fd = new FileDialog(getFrame(), "Select File", FileDialog.LOAD);
				fd.setFilenameFilter((File dir, String name) -> name.toLowerCase().endsWith(".txt"));
				fd.setVisible(true);
				if (fd.getFile() == null)
					return;

				try (Stream<String> lines = Files.lines(Paths.get(fd.getFile()))) {
					lines.forEachOrdered(s -> input(s));
				} catch (IOException ex) {

				}

			}
		});

		run.add(open_and_run);
		mbar.add(run);
		setJMenuBar(mbar);

		getContentPane().add(main);
	}

	private void buildPref() {
		if (System.getProperty("os.name").toLowerCase().contains("mac")) {
			Application ma = Application.getApplication();
			ma.setPreferencesHandler(arg0 -> buildPrefFrame());
			ma.setAboutHandler(arg0 -> buildAboutFrame());
		}
	}

	private void buildPrefFrame() {
		JFrame pref = new JFrame("Preferences");
		pref.setLocationRelativeTo(getFrame());

		JPanel panel = new JPanel();

		JButton font_color_btn = new JButton("Font Color");
		font_color_btn.addActionListener(e -> font_color = JColorChooser.showDialog(null, "Font Color", font_color));

		JButton bg_color_btn = new JButton("Background Color");
		bg_color_btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JColorChooser jcc = new JColorChooser();
				AbstractColorChooserPanel[] panels = jcc.getChooserPanels();
				for (AbstractColorChooserPanel accp : panels) {
					if (accp.getDisplayName().equals("RGB")) {
						JOptionPane.showMessageDialog(null, accp);
					}
				}
			}
		});

		panel.add(font_color_btn);
		panel.add(bg_color_btn);
		pref.setContentPane(panel);

		pref.setMinimumSize(new Dimension(150, 150));
		pref.pack();
		pref.setVisible(true);
	}

	private void buildAboutFrame() {
		JFrame about = new JFrame("About");
		about.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		about.setLocationRelativeTo(getFrame());
		JPanel panel = new JPanel();
		JLabel about_txt = new JLabel("Console v." + getUID());

		panel.add(about_txt);
		about.setContentPane(panel);

		about.setMinimumSize(new Dimension(150, 100));
		about.pack();
		about.setVisible(true);
	}

	public void write(String m) {
		if ("".equals(m))
			return;
		String split_regex = "[\\s\\+\\-\\*/=,\\[\\]]+";
		String[] parts = m.split("(?=[" + split_regex + "])|(?<=[" + split_regex + "])");
		for (String s : parts) {
			if (ci.isVar(s)) {
				append(cons, s, var_color);
			} else if (NumberUtils.isNumber(s)) {
				append(cons, s, num_color);
			} else if (ci.isCommand(s)) {
				append(cons, s, font_color, true);
			} else {
				append(cons, s, font_color);
			}
		}
		append(cons, "\n", font_color);
	}

	private void append(JTextPane tp, String msg, Color c) {
		append(tp, msg, c, false);
	}

	private void append(JTextPane tp, String msg, Color c, boolean bold) {
		StyledDocument doc = tp.getStyledDocument();
		SimpleAttributeSet sas = new SimpleAttributeSet();
		StyleConstants.setForeground(sas, c);
		StyleConstants.setBold(sas, bold);
		try {
			doc.insertString(doc.getLength(), msg, sas);
		} catch (Exception e) {

		}
		JScrollBar vert = sp.getVerticalScrollBar();
		vert.setValue(vert.getMaximum());
	}

	public void input(String input) {
		write(input);
		inp.setText("");

		// process
		ci.parse(input.trim());

		if (!ci.run)
			append(cons, ConsoleInput.begin, ConsoleInput.begin_color);
	}

	public void clear() {
		cons.setText("");
	}

	public JFrame getFrame() {
		return this;
	}

	public long getUID() {
		return serialVersionUID;
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					System.setProperty("apple.laf.useScreenMenuBar", "true");
					System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Test");
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
						| UnsupportedLookAndFeelException e) {
					e.printStackTrace();
				}
				ConsoleGUI cg = new ConsoleGUI();
				cg.inp.requestFocusInWindow();
			}
		});
	}

}
