package com.dwotherspoon.tmv_encoder;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentLinkedQueue;


/* Main encoder thread. */


public final class TMVEncode implements Runnable {
	private String input;
	private BufferedImage cur_frame;
	private TMVFrame cur_enc;
	private ConcurrentLinkedQueue<UCell> pool;
	private ConcurrentLinkedQueue<TMVFrame> output;
	private TMVGui gui;
	private boolean[][] font;
	
	public TMVEncode(String input, TMVGui src) {
		this.input = input;
		output = new ConcurrentLinkedQueue<TMVFrame>();
		gui = src;
		
		InputStream font_in;
		font = new boolean[256][64];
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
		
		
		
	}
	
	
	public void run() {
		Decode video = new Decode(input);
		XuggleFrame cur;
		Thread[] threads = new Thread[Runtime.getRuntime().availableProcessors()];
		Worker[] workers = new Worker[threads.length];
		
		
		for (int  i = 0; i<workers.length; i++) {
			try {
				workers[i] = new Worker();
				threads[i] = new Thread(workers[i]); //create workers (causes fonts and colour tables to be created)
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		boolean wflag;
		while ((cur = video.getFrame()) != null) {
			if (cur instanceof XuggleVFrame) {
				cur_frame = ((XuggleVFrame) cur).getImage(); //n.b. critical section
				gui.updateSframe(cur_frame);
				cur_enc = new TMVFrame();
				split();
				for (int i = 0; i<workers.length; i++) { //setup workers for new frame + start
					threads[i] = new Thread(workers[i]);
					workers[i].setup(pool, cur_enc);
					threads[i].start();
				}
				wflag = true;
				while (wflag == true) {
					for (int i = 0; i < workers.length; i++) {
						if (!threads[i].isAlive()) {
							wflag = false;
						}
					}
				}
				gui.updateOframe(cur_enc.render(font));
				output.add(cur_enc);

				//System.out.println(output.size());
			}
			else { //TODO do something with audio
				
			}
		}
		System.out.println("FINISH!");
	}
	
	private void split() { //splits the image for processing.
		pool = new ConcurrentLinkedQueue<UCell>();
		int[] buf;
		for (int row = 0; row < 25; row++) {
			for (int col = 0; col < 40; col++) {
				buf = new int[64];
				for (int y = 0; y < 8; y++) {
					for (int x = 0; x < 8; x++) {
						buf[(y*8) + x] = cur_frame.getRGB((col * 8) + x, (row * 8) + y); //TODO 4*4 mode
					}
				}
				pool.add(new UCell(buf, (row*40) + col));
			}
		}
		//END
	}
	
	public BufferedImage getLast() {
		return cur_frame;
	}

}
