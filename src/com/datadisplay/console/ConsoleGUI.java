package com.datadisplay.console;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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
					for (String key : ci.commands.keySet()) {
						if (key.startsWith(inp.getText()) && !key.equals(inp.getText())) {
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
						inp.setText(inp.getText().substring(0, inp.getText().length() - 1));
						inp.setCaretPosition(inp.getCaretPosition());
					} else {
						inp.setText(inp.getText() + "\"");
						inp.setCaretPosition(inp.getCaretPosition() - 1);
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
		inp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String input = inp.getText();
				write(input);
				// append(cons,input+"\n",font_color);
				inp.setText("");

				// process
				ci.parse(input.trim());

				append(cons, ConsoleInput.begin, ConsoleInput.begin_color);
			}
		});
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = 1;
		main.add(inp, c);

		getContentPane().add(main);
	}

	private void buildPref() {
		if (System.getProperty("os.name").toLowerCase().contains("mac")) {
			Application ma = Application.getApplication();
			ma.setPreferencesHandler(arg0 -> buildPrefFrame());
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
						accp.getColorSelectionModel().addChangeListener(new ChangeListener() {
							@Override
							public void stateChanged(ChangeEvent e) {
								cons.setBackground(jcc.getColor());
							}
						});
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
				ConsoleGUI cg = new ConsoleGUI();
				cg.inp.requestFocusInWindow();
			}
		});
	}

}
