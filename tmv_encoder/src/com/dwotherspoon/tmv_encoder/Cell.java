package com.dwotherspoon.tmv_encoder;

/* Simple data type for representing encoded cells */
public class Cell {
	private int cha;
	private byte foreground;
	private byte background;
	
	public int getCha() {
		return cha;
	}
	
	public byte getForeground() {
		return foreground;
	}
	
	public byte getBackground() {
		return background;
	}
	
	public void setCha(int i) {
		cha = i;
	}
	
	public void setForeground(byte i) {
		foreground = i;
	}
	
	public void setBackground(byte i) {
		background = i;
	}

}
