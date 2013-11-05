package com.dwotherspoon.tmv_encoder;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import com.dwotherspoon.tmv_encoder.match.Algorithm;
import com.dwotherspoon.tmv_encoder.match.Fast;
import com.dwotherspoon.tmv_encoder.match.Slow;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class Worker implements Runnable {
	private ConcurrentLinkedQueue<UCell> input;
	private TMVFrame result;
	private boolean yuv;
	private int threshold = 30; //allow this to be set nb. colourspace matters
	private boolean[][] font;
	private ColC[] fcols;
	private int[] colours;
	private int[] cgaRGB = new int[] { //CGA Colours
              0xFF000000,
              0xFF0000AA,
              0xFF00AA00,
              0xFF00AAAA,
              0xFFAA0000,
              0xFFAA00AA,
              0xFFAA5500,
              0xFFAAAAAA,
              0xFF555555,
              0xFF5555FF,
              0xFF55FF55,
              0xFF55FFFF,
              0xFFFF5555,
              0xFFFF55FF,
              0xFFFFFF55,
              0xFFFFFFFF
      };
	private int[] cgaYUV = new int[] {
			0xFF008080,
			0xFF13d572,
			0xFF634738,
			0xFF779c2b,
			0xFF3263d5,
			0xFF46b8c7,
			0xFF6447b1,
			0xFFaa8080,
			0xFF558080,
			0xFF68d572,
			0xFFb84738,
			0xFFcc9c2b,
			0xFF8763d5,
			0xFF9bb8c7,
			0xFFeb2b8d,
			0xFFff8080
	};
	
	public Worker(boolean yuv) throws IOException {
		this.yuv = yuv;
		colours = yuv ? cgaYUV : cgaRGB;
		try { //try and load font and colour tables in
			InputStream font_in = new FileInputStream("font.bin");
			InputStream cols_in = new FileInputStream("cols.dat");
			font = new boolean[256][64];
			fcols = new ColC[136];
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
			//read col table and convert to yuv maybe
			for (int i=0; i<136; i++) {
				if (yuv) {
					fcols[i] = new ColC((byte)cols_in.read(), (byte)cols_in.read(), ColourUtil.convRGBtoYUV(cols_in.read(), cols_in.read(), cols_in.read()));	
				}
				else {
					fcols[i] = new ColC((byte)cols_in.read(), (byte)cols_in.read(), ColourUtil.makeCol(cols_in.read(), cols_in.read(), cols_in.read()));
				}
			}
			font_in.close();
			cols_in.close();
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException("Required files not found.");
		}
	}
	
	public void setup(ConcurrentLinkedQueue<UCell> cells, TMVFrame res) { //set this before run!
		result = res;
		input = cells;
	}

	@Override
	public void run() {
		UCell cur;
		Algorithm matcher;
		while ((cur = input.poll()) != null) { //worker loop
			if (getStdDev(cur.getData()) > threshold) { //AAS
				matcher = new Slow(font, yuv);
			}
			else {
				matcher = new Fast(fcols, yuv);
			}
			result.setCell(cur.getNum(), matcher.match(cur.getData()));
		}
	}
	
	
	
	private int getStdDev(int[] im) {//edit for yuv rgb
		long totR = 0;
		long totG = 0;
		long totB = 0;
		long sigmaR2 = 0;
		long sigmaG2 = 0;
		long sigmaB2 = 0;
		
		for (int pix = 0; pix < 64; pix++) { //calculate totals and square sum
			int col = im[pix];
			int r = ColourUtil.getR(col);
			int g = ColourUtil.getG(col);
			int b = ColourUtil.getB(col);
			
			totR += r;
			totG += g;
			totB += b;
			
			sigmaR2 += (r * r);
			sigmaG2 += (g * g);
			sigmaB2 += (b * b);
		}
		//calculate square of the means (>>6 to divide by 64 fast)
		double mRed = (totR * totR) >> 6;
		double mGreen = (totG * totG) >> 6;
		double mBlue = (totB * totB) >> 6;
		return (int)(Math.sqrt(sigmaR2 - mRed) + Math.sqrt(sigmaG2 - mGreen) + Math.sqrt(sigmaB2 - mBlue)) >> 3;
		//(>>3)8 is close enough to sqrt 63.
	}
}
