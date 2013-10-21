package com.dwotherspoon.tmv_encoder;

public class TMVFrame {
	private Cell[] cells;
	
	public TMVFrame() {
		cells = new Cell[1000];
	}
	
	public void setCell(int n, byte cha, byte foreground, byte background) {
		cells[n].setCha(cha);
		cells[n].setForeground(foreground);
		cells[n].setBackground(background);
	}
	
	public void setCell(int n, Cell c) {
		cells[n] = c;
	}
	
	public Cell getCell(int n) {
		return cells[n];
	}

}
