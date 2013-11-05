package com.dwotherspoon.tmv_encoder.match;

import com.dwotherspoon.tmv_encoder.Cell;
import com.dwotherspoon.tmv_encoder.ColourUtil;

public final class Slow implements Algorithm {
	private boolean[][] font;
	private boolean yuv;
	
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
	
	private int[] cgaYUV = new int[] { //n.b. ayuv
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
	private int[] colours;
	
	public Slow(boolean[][] f, boolean yuv) {
		font = f;
		colours = yuv ? cgaYUV : cgaRGB;
		this.yuv = yuv;
	}

	@Override
	public Cell match(int[] image) {
		Cell result = new Cell();
		int diff1 = 0;
		int diff2 = 0;
		int min = Integer.MAX_VALUE;
		int cur;
		byte[] m = getMCommon(image);
				
		for (int cha = 3; cha < 255; cha++) {
			diff1 = 0;
			diff2 = 0;
			for (int pix = 0; pix < 64; pix++) {
				cur = image[pix];
				if (font[cha][pix]) {
					diff1 += ColourUtil.diffCol(cur, colours[m[0]], yuv);
					diff2 += ColourUtil.diffCol(cur, colours[m[1]], yuv);
				}
				else {
					diff1 += ColourUtil.diffCol(cur, colours[m[1]], yuv);
					diff2 += ColourUtil.diffCol(cur, colours[m[0]], yuv);
				}
			}
			if (diff1 < min) {
				min = diff1;
				result.setCha(cha);
				result.setBackground(m[1]);
				result.setForeground(m[0]);
			}
			
			if (diff2 < min) {
				min = diff2;
				result.setCha(cha);
				result.setBackground(m[0]);
				result.setForeground(m[1]);
			}
		}
		return result;
	}
	
	private byte[] getMCommon(int[] image) {
		byte[] counts = new byte[16];
		int min; //stores the current best matching colour ID
		int minval; //stores the minimum diff so far
		int diff; //stores the current difference when searching for closest match
		int cur;
		
		for (int pix = 0; pix < 64; pix++) {
			cur = image[pix];
			minval = Integer.MAX_VALUE;
			min = 0;
			for (int colour = 0; colour<16; colour++) {
				diff = ColourUtil.diffCol(cur, colours[colour], yuv);
				if (diff < minval) {
					min = colour;
					minval = diff;
				}
			}
			counts[min]++;
		}
		byte[] ret = new byte[2];
		int max = 0; //stores the max count so far
		for (int colour = 0; colour < 16; colour++) { //linear search for the first maximum
			if (counts[colour] > max) {
				ret[0] = (byte)colour;
				max = counts[colour];
			}
		}
		// Hacky bit to tweak results to prevent grey reducing image detail
		if (ret[0] == 0) {
			counts[8] = (byte) (counts[8] * 0.3);
			counts[7] = (byte) (counts[7] * 0.6);
		} else if (ret[0] == 8) {
			counts[0] = (byte) (counts[0] * 0.6);
			counts[7] = (byte) (counts[7] * 0.6);
		}
		counts[ret[0]] = 0; //zero out first highest.
		
		max = 0; //stores the max count so far
		for (int colour = 0; colour < 16; colour++) { //linear search for the first maximum
			if (counts[colour] > max) {
				ret[1] = (byte)colour;
				max = counts[colour];
			}
		}
		
		return ret;
	}

}
