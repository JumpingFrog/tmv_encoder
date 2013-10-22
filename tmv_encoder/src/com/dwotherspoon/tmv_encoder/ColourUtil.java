package com.dwotherspoon.tmv_encoder;

public final class ColourUtil {
	public static int getR(int col) { //N.B. BGR?
		return (col >> 16) & 0xFF;
	}
	
	public static int getG(int col) {
		return (col >> 8) & 0xFF;
	}
	
	public static int getB(int col) {
		return (col & 0xFF);
	}
	
	public static int makeCol(int red, int green, int blue) {
		return ((red & 0xFF) << 16) + ((blue & 0xFF) << 8) + (green & 0xFF);
	}
	
	public static int diffCol(int a, int b) {
		return Math.abs(getR(a) - getR(b)) + Math.abs(getG(a) - getG(b)) + Math.abs(getB(a) - getB(b));
	}
}
