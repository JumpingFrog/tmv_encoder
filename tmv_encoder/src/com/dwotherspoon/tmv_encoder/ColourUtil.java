package com.dwotherspoon.tmv_encoder;

/* Functions for working with colour */
public final class ColourUtil {
	
	public static int convRGBtoYUV(int in) { //http://softpixel.com/~cwright/programming/colorspace/yuv/
		int r = getR(in);
		int g = getG(in);
		int b = getB(in);
		
		int y = (int) Math.round(r *  .299000 + g *  .587000 + b *  .114000);
		int u = (int) Math.round(r * -.168736 + g * -.331264 + b *  .500000 + 128);
		int v = (int) Math.round(r *  .500000 + g * -.418688 + b * -.081312 + 128);
		return ((y&0xFF)<<16) + ((u&0xFF)<<8) + (v&0xFF);
	}
	
	public static int convRGBtoYUV(int r, int g, int b) { //http://softpixel.com/~cwright/programming/colorspace/yuv/ just an alternate form
		int y = (int) Math.round(r *  .299000 + g *  .587000 + b *  .114000);
		int u = (int) Math.round(r * -.168736 + g * -.331264 + b *  .500000 + 128);
		int v = (int) Math.round(r *  .500000 + g * -.418688 + b * -.081312 + 128);
		return ((y&0xFF)<<16) + ((u&0xFF)<<8) + (v&0xFF);
	}
	
	public static int getR(int col) { //N.B. BGR?
		return (col >> 16) & 0xFF;
	}
	
	public static int getG(int col) {
		return (col >> 8) & 0xFF;
	}
	
	public static int getB(int col) {
		return (col & 0xFF);
	}
	
	public static int getY(int col) { //just for readability tbh -- same as RGB
		return (col >> 16) & 0xFF;
	}
	
	public static int getU(int col) {
		return (col >> 8) & 0xFF;
	}
	
	public static int getV(int col) {
		return (col & 0xFF);
	}
	
	public static int makeCol(int red, int green, int blue) {
		return ((red & 0xFF) << 16) + ((blue & 0xFF) << 8) + (green & 0xFF);
	}
	
	public static int diffCol(int a, int b, boolean yuv) { //http://www.compuphase.com/cmetric.htm
		if (yuv) {
			return (int) Math.sqrt(((getY(a)-getY(b))*(getY(a)-getY(b))) + ((getU(a)-getU(b))*(getU(a)-getU(b))) + ((getV(a)-getV(b))*(getV(a)-getV(b))));
		} else {
			return (int) Math.sqrt(2*((getR(a)-getR(b))*(getR(a)-getR(b))) + 4*((getG(a)-getG(b))*(getG(a)-getG(b))) + 3*((getB(a)-getB(b))*(getB(a)-getB(b))));
		}
	}
	
	public static int diffsCol(int a, int b) {
		return Math.abs(getR(a) - getR(b))/2 + Math.abs(getG(a) - getG(b)) * 2 + Math.abs(getB(a) - getB(b))*2;
	}
	
	
}
