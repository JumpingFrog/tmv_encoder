package com.dwotherspoon.tmv_encoder.match;

import com.dwotherspoon.tmv_encoder.Cell;
import com.dwotherspoon.tmv_encoder.ColC;
import com.dwotherspoon.tmv_encoder.ColourUtil;

public class Fast implements Algorithim{
	private ColC[] fcols;
	
	public Fast(ColC[] cols) {
		fcols = cols;
	}
	
	@Override
	public Cell match(int[] image) {
		int avg = getAvgCol(image);
		int min = Integer.MAX_VALUE;
		
		Cell result = new Cell();
		result.setCha(177);
		int temp;
		
		for (int i = 0; i < 136; i++) {
			temp = ColourUtil.diffCol(avg, fcols[i].getAvg());
			if (temp < min) {
				min = temp;
				result.setBackground(fcols[i].getCol1());
				result.setForeground(fcols[i].getCol2());
			}
		}
		return result;
	}
	
	private int getAvgCol(int[] img) { //returns array to minimise post processing
		int totR = 0;
		int totG = 0;
		int totB = 0;
		for (int pix = 0; pix < 64; pix++) {
			int col = img[pix];
			totR += ColourUtil.getR(col);
			totG += ColourUtil.getG(col);
			totB += ColourUtil.getB(col);
		}
		return ColourUtil.makeCol((totR>>6),(totG>>6), (totB>>6));
		
	}

}
