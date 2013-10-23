package com.dwotherspoon.tmv_encoder;

public final class ColC {
	private int avg;
	private byte col1;
	private byte col2;
	
	/* Simple structure for storing average colours on Fast match when dithering */
	public ColC(byte c1, byte c2, int a) {
		avg = a;
		col1 = c1;
		col2 = c2;
	}
	
	public int getAvg() {
		return avg;
	}
	
	public byte getCol1() {
		return col1;
	}
	
	public byte getCol2() {
		return col2;
	}
}
