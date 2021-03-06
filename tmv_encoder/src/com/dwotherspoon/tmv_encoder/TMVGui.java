package com.dwotherspoon.tmv_encoder;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JProgressBar;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.JTextPane;
import javax.swing.border.LineBorder;
import javax.swing.JLabel;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/* The GUI for TMV Encoder */

public final class TMVGui { //prefix things
	private JFrame window;
	private JMenuBar mbar;
	private JMenuItem iopen;
	private JMenuItem iabout;
	private ImgPanel sframe;
	private ImgPanel oframe;
	private JProgressBar pbar;
	private JTextPane console;
	private JButton encbut;
	private JLabel srcf;
	private JLabel outf;
	private JLabel lbl_cspace;
	private JRadioButton rb_yuv;
	private JRadioButton rb_rgb;
	private ButtonGroup bg_cspace;
	private String fpath;
	
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
				About abox = new About();
				
			}
			
		});
		
		mbar.add(iopen);
		mbar.add(iabout);
		
		sframe = new ImgPanel(320,200);
		sframe.setLocation(20,25);
		sframe.setSize(320,200);
		
		srcf = new JLabel("Source frame:");
		srcf.setLocation(20, 10);
		srcf.setSize(100, 15);
		
		outf = new JLabel("Output frame:");
		outf.setLocation(380, 10);
		outf.setSize(100, 15);
		
		oframe = new ImgPanel(320,200);
		oframe.setLocation(380,25);
		oframe.setSize(320,200);
		
		pbar = new JProgressBar();
		pbar.setLocation(20, 240);
		pbar.setSize(680, 25);
		pbar.setStringPainted(true);
		pbar.setMaximum(100);
		pbar.setMinimum(0);
		
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
				encode();
			}
			
		});
		
		lbl_cspace = new JLabel("Colour space:");
		lbl_cspace.setLocation(400,590);
		lbl_cspace.setSize(100,15);
		
		bg_cspace = new ButtonGroup();
		
		rb_yuv = new JRadioButton("YUV");
		rb_yuv.setLocation(400,610);
		rb_yuv.setSize(60,15);
		
		rb_rgb = new JRadioButton("RGB");
		rb_rgb.setLocation(460, 610);
		rb_rgb.setSize(60,15);
		
		bg_cspace.add(rb_rgb);
		bg_cspace.add(rb_yuv);
		
		window.setJMenuBar(mbar);
		window.add(srcf);
		window.add(outf);
		window.add(sframe);
		window.add(oframe);
		window.add(pbar);
		window.add(console);
		window.add(encbut);
		window.add(lbl_cspace);
		window.add(rb_yuv);
		window.add(rb_rgb);
		window.setVisible(true);
	}
	
	private void open() {
		JFileChooser chooser = new JFileChooser();
		if (chooser.showOpenDialog(window) == JFileChooser.APPROVE_OPTION) {
			writeConsole("Attempting to open: " + chooser.getSelectedFile().getName());
			encbut.setEnabled(true);	
			fpath = chooser.getSelectedFile().getAbsolutePath();
			}
		}
	public void encode() {
		TMVEncode enc = new TMVEncode(fpath, this, false);
		Thread starter = new Thread(enc, "Encode Thread");
		starter.start();
		writeConsole("Starting encode thread...");
		encbut.setEnabled(false);
	}
	
	public void updateSframe(BufferedImage in) {
		sframe.setImage(in);
	}
	
	public void updateOframe(BufferedImage in) {
		oframe.setImage(in);
	}
	
	public void setBar(int percent) {
		pbar.setValue(percent);
	}
	
	public void writeConsole(String text) {
		console.setText(console.getText() + '\n' + text);
	}
	
	public void convFinish() {
		encbut.setEnabled(true);
		writeConsole("Conversion finished, file saved as output.tmv");
	}
}
