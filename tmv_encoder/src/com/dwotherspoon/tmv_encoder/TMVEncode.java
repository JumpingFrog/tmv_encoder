package com.dwotherspoon.tmv_encoder;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;


/* Main encoder thread. */


public final class TMVEncode implements Runnable {
	private String input;
	private BufferedImage cur_frame;
	private TMVFrame cur_enc;
	private ConcurrentLinkedQueue<UCell> pool;
	private ConcurrentLinkedQueue<TMVFrame> output;
	
	public TMVEncode(String input) {
		this.input = input;
		output = new ConcurrentLinkedQueue<TMVFrame>();
	}
	
	
	public void run() {
		Decode video = new Decode(input);
		XuggleFrame cur;
		Worker[] workers = new Worker[Runtime.getRuntime().availableProcessors()];
		for (int  i = 0; i<workers.length; i++) {
			try {
				workers[i] = new Worker(); //create workers (causes fonts and colour tables to be created)
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		while ((cur = video.getFrame()) != null) {
			if (cur instanceof XuggleVFrame) {
				cur_frame = ((XuggleVFrame) cur).getImage(); //n.b. critical section
				cur_enc = new TMVFrame();
				split();
				for (int i = 0; i<workers.length; i++) { //setup workers for new frame + start
					workers[i].setup(pool, cur_enc);
					workers[i].run();
				}
				output.add(cur_enc);
			}
			else { //TODO do something with audio
				
			}
		}
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

}
