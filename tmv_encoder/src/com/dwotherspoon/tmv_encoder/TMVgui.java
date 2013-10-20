package com.dwotherspoon.tmv_encoder;

import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JProgressBar;
import javax.swing.JButton;

public class TMVgui {
	private JFrame window;
	private JMenuBar mbar;
	private JMenuItem iopen;
	private JMenuItem iabout;
	private ImgPanel sframe;
	private ImgPanel oframe;
	private JProgressBar pbar;
	
	public TMVgui() { //build the gui
		window = new JFrame("TMV Encoder");
		window.setSize(720,800);
		window.setResizable(false);
		window.setLayout(null);
		
		mbar = new JMenuBar();
		mbar.setLocation(0,0);
		mbar.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		iopen = new JMenuItem("Open");
		iabout = new JMenuItem("About");
		
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
		
		window.setJMenuBar(mbar);
		window.add(sframe);
		window.add(oframe);
		window.add(pbar);
		window.setVisible(true);
	}
}
