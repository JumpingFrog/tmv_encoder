package com.dwotherspoon.tmv_encoder.match;

import com.dwotherspoon.tmv_encoder.Cell;
import com.dwotherspoon.tmv_encoder.ColourUtil;

public class Slow implements Algorithim {
	private boolean[][] font;
	
	private int[] colours = new int[] { //CGA Colours
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
	
	public Slow(boolean[][] f) {
		font = f;
	}

	@Override
	public Cell match(int[] image) {
		// TODO Auto-generated method stub
		return null;
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
				diff = ColourUtil.diffCol(cur, colours[colour]);
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
			counts[0] = (byte) (counts[8] * 0.6);
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
