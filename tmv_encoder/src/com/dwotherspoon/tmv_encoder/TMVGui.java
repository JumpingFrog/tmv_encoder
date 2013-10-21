package com.dwotherspoon.tmv_encoder;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JProgressBar;
import javax.swing.JButton;
import javax.swing.JTextPane;
import javax.swing.border.LineBorder;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/* The GUI for TMV Encoder */

public class TMVGui {
	private JFrame window;
	private JMenuBar mbar;
	private JMenuItem iopen;
	private JMenuItem iabout;
	private ImgPanel sframe;
	private ImgPanel oframe;
	private JProgressBar pbar;
	private JTextPane console;
	private JButton encbut;
	
	public TMVGui() { //build the gui
		window = new JFrame("TMV Encoder");
		window.setSize(720,700);
		window.setResizable(false);
		window.setLayout(null);
		
		mbar = new JMenuBar();
		mbar.setLocation(0,0);
		mbar.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		iopen = new JMenuItem("Open");
		iopen.addActionListener(new ActionListener () {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				open();
			}
			
		});
		
		iabout = new JMenuItem("About");
		iabout.addActionListener(new ActionListener () {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		mbar.add(iopen);
		mbar.add(iabout);
		
		sframe = new ImgPanel(320,200);
		sframe.setLocation(20,20);
		sframe.setSize(320,200);
		
		oframe = new ImgPanel(320,200);
		oframe.setLocation(380,20);
		oframe.setSize(320,200);
		
		pbar = new JProgressBar();
		pbar.setLocation(20, 240);
		pbar.setSize(680, 25);
		pbar.setStringPainted(true);
		
		console = new JTextPane();
		console.setBorder(new LineBorder(Color.gray, 2));
		console.setEditable(false);
		console.setSize(680, 300);
		console.setLocation(20, 280);
		console.setText("TMV Encoder (Java) Loaded...");
		
		encbut = new JButton();
		encbut.setLocation(20,590);
		encbut.setSize(300, 25);
		encbut.setText("Encode");
		encbut.setEnabled(false);
		encbut.addActionListener( new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		window.setJMenuBar(mbar);
		window.add(sframe);
		window.add(oframe);
		window.add(pbar);
		window.add(console);
		window.add(encbut);
		window.setVisible(true);
		
	}
	
	private void open() {
		JFileChooser chooser = new JFileChooser();
		if (chooser.showOpenDialog(window) == JFileChooser.APPROVE_OPTION) {
			writeConsole("Attempting to open: " + chooser.getSelectedFile().getName());
			Decode video = new Decode(chooser.getSelectedFile().getAbsolutePath());
			encbut.setEnabled(true);
			XuggleFrame f;
			
			while ((f = video.getFrame()) != null) {
				if (f instanceof XuggleVFrame) {
					sframe.setImage(((XuggleVFrame) f).getImage());
				}
				else {
					
				}
			}
		}
	}
	
	public void writeConsole(String text) {
		console.setText(console.getText() + '\n' + text);
	}
}
