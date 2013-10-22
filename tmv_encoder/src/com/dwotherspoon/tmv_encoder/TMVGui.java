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
			//Decode video = new Decode(chooser.getSelectedFile().getAbsolutePath());
			encbut.setEnabled(true);
			//XuggleFrame f = video.getFrame();
			BufferedImage in = new BufferedImage(320,200, BufferedImage.TYPE_3BYTE_BGR);
			Graphics g = in.getGraphics();
				BufferedImage tempi;
				try {
					tempi = ImageIO.read(chooser.getSelectedFile());

					g.drawImage(tempi, 0,0,320,200,null);
					g.dispose();
					sframe.setImage(in); //hacked together for testing
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
			
				
				ConcurrentLinkedQueue<UCell> togo = new ConcurrentLinkedQueue<UCell>();
				int[] buf = new int[64];
				for (int row = 0; row < 25; row++) { //split the image
					for (int col = 0; col < 40; col++) {
						for (int y = 0; y < 8; y++) {
							for (int x =0; x< 8; x++) {
								buf[(y*8)+x] = (in.getRGB((col*8)+x, (row*8)+y) & 0x00FFFFFF); //AND removes alpha channel.
								//System.out.println((y*8)+x + " : " + buf[(y*8)+x]);
							}
						}
						togo.add(new UCell(buf, (row*40)+col ));
					}
				}
				TMVFrame back = new TMVFrame();
				InputStream font_in;
				boolean[][] font = new boolean[256][64];
				try {
					font_in = new FileInputStream("font.bin");
					int temp = 0;
					int mask = 0;
					for (int cha = 0; cha < 256; cha++) { //read font table
						for (int row = 0; row < 8; row++) {
							temp = font_in.read();
							for (int col = 7; col >= 0; col--) {
								mask = (128 >> col); //bit mask reverses endian
								mask &= temp;
								font[cha][(row*8) + col] = (mask != 0);
							}
						}
					}
				} catch (Exception e1)  {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				
				try {
					Thread test = new Thread(new Worker(togo, back));
					test.run();
					while (test.isAlive()) {
					}
					oframe.setImage(back.render(font));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	
	public void writeConsole(String text) {
		console.setText(console.getText() + '\n' + text);
	}
}
