package com.dwotherspoon.tmv_encoder;

/* Simple data type for representing encoded cells */
public class Cell {
	private byte cha;
	private byte foreground;
	private byte background;
	
	public byte getCha() {
		return cha;
	}
	
	public byte getForeground() {
		return foreground;
	}
	
	public byte getBackground() {
		return background;
	}
	
	public void setCha(byte i) {
		cha = i;
	}
	
	public void setForeground(byte i) {
		foreground = i;
	}
	
	public void setBackground(byte i) {
		background = i;
	}

}
