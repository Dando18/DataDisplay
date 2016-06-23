package com.datadisplay.console;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class ConsoleGUI extends JFrame{
	private static final long serialVersionUID = (long) (Math.random()*1000);

	public static final String TITLE = String.format("DataDisplay v.%X", serialVersionUID);
	
	ConsoleInput ci;
	
	// panels
	private JPanel main;
	private JScrollPane sp;  // for console textarea
	
	// text
	private JTextArea cons;
	private JTextField inp;
	
	// attributes
	private Font font;
	
	public ConsoleGUI(){
		super();
		
		ci = new ConsoleInput(this);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setPreferredSize(new Dimension(550,400));
		this.setMinimumSize(new Dimension(400,300));
		this.setLocationRelativeTo(null);
		
		buildPanel();
		pack();
		
		this.setTitle(TITLE);
		this.setVisible(true);
		this.setResizable(true);
		
	}
	
	private void buildPanel(){
		font = new Font(Font.MONOSPACED, Font.PLAIN, 12);
		
		main = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		cons = new JTextArea();
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
		inp.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				String input = inp.getText();
				cons.append(input+"\n");
				inp.setText("");
				
				// process
				ci.parse(input.trim());
				
				cons.append(ConsoleInput.begin);
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
	
	public void write(String m){
		if("".equals(m)) return;
		cons.append(m+"\n");
	}
	
	public void clear(){
		cons.setText("");
	}
	
	public long getUID(){
		return serialVersionUID;
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run(){
				ConsoleGUI cg = new ConsoleGUI();
				cg.inp.requestFocusInWindow();
			}
		});
	}

}
