package com.dwotherspoon.tmv_encoder.match;

import com.dwotherspoon.tmv_encoder.Cell;
import com.dwotherspoon.tmv_encoder.ColourUtil;

public class Fast implements Algorithim{
	
	@Override
	public Cell match(int[] image) {
		int[] avg = getAvgCol(image);
		
		return null;
	}
	
	private int[] getAvgCol(int[] img) { //returns array to minimise post processing
		int totR = 0;
		int totG = 0;
		int totB = 0;
		for (int pix = 0; pix < 64; pix++) {
			int col = img[pix];
			totR += ColourUtil.getR(col);
			totG += ColourUtil.getG(col);
			totB += ColourUtil.getB(col);
		}
		int[] ret = {(totR>>6), (totG>>6), (totB>>6)};
		return ret;
		
	}

}
