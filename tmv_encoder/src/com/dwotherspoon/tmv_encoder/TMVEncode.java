package com.dwotherspoon.tmv_encoder;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.xuggle.xuggler.IAudioSamples;


/* Main encoder thread. */


public final class TMVEncode implements Runnable {
	private String input;
	private BufferedImage cur_frame;
	private IAudioSamples cur_samples;
	private ByteArrayOutputStream audio_out;
	private BufferedImage out_frame;
	private TMVFrame cur_enc;
	private ConcurrentLinkedQueue<UCell> pool;
	private ConcurrentLinkedQueue<TMVFrame> output;
	private TMVGui gui;
	private boolean[][] font;
	
	public TMVEncode(String input, TMVGui src) {
		this.input = input;
		output = new ConcurrentLinkedQueue<TMVFrame>();
		audio_out = new ByteArrayOutputStream();
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
		int srate = video.getSampleRate();
		int fcount = 0; //local frame number (because O(n) traversal is slow).
		double divisor = (double)video.getDuration()/100000000; //for progress bar
		
		for (int  i = 0; i<workers.length; i++) {
			try {
				workers[i] = new Worker();
				threads[i] = new Thread(workers[i]); //create workers (causes fonts and colour tables to be created)
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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
				for (int i = 0; i<workers.length; i++) { //wait for workers to finish
					try {
						threads[i].join();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				output.add(cur_enc);
				out_frame = cur_enc.render(font);
				gui.updateOframe(out_frame);
				gui.setBar((int)Math.round((fcount++/video.getFrameRate())/divisor));
			}
			else { //TODO do something with audio
				cur_samples = ((XuggleAFrame) cur).getSamples();
				if (srate == 44100) {
					for (long i = 0; i < cur_samples.getNumSamples(); i+= 2) { //every other sample (halve the samplesrate)
						audio_out.write((byte)((cur_samples.getSample(i, 0, IAudioSamples.Format.FMT_S16) + 0x8000)>>8)); //TODO Stereo
					}
				}
				else { //assume 22050Hz, no downsampling
					for (long i = 0; i < cur_samples.getNumSamples(); i++) { //N.B. Xuggler current supports Signed 16bit audio ONLY. We need U8 N.B.
						audio_out.write((byte)((cur_samples.getSample(i, 0, IAudioSamples.Format.FMT_S16) + 0x8000)>>8)); //TODO Stereo
						//N.B. We need to decrease the sample rate to 22050Hz
					}
				}
			}
		}
		Encode enc = new Encode(output, audio_out);
		File tmvout = new File("//home//david//output.tmv");
		
		srate = (srate == 44100) ? 22050 : srate;
		
		double chunksize = srate/video.getFrameRate();
		enc.saveTMV(tmvout, srate, (int)chunksize);
		
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
