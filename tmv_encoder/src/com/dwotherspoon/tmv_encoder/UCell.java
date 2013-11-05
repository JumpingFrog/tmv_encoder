package com.dwotherspoon.tmv_encoder;

 /* An unprocessed cell */
public class UCell {
	private int[] img; //the image data
	private int num; //the cell's id within the frame
	public UCell(int[] i, int n) {
		img = i;
		num = n;
	}
	
	public int[] getData() {
		return img;
	}
	
	public int getNum() {
		return num;
	}

}
