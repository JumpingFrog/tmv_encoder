package com.dwotherspoon.tmv_encoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Queue;

/* Class for outputting finished videos */

public class Encode { //from AVCOMP.PAS (available from http://www.oldskool.org/pc/8088_Corruption/8088flex.zip)
	static byte[] header = {'T', 'M', 'A', 'V'}; //header
	static byte compmethod = 0; //compression method, always 0.
	static byte charcols = 40; //number of columns.
	static byte charrows = 25; //number of rows
	static byte version = 0; //special handling, 0 for none, 1 for 512 byte boundary padding.
	
	public Encode(Queue<TMVFrame> video) {
		// TODO Auto-generated constructor stub
	}
	
	public void saveTMV(File out, int samplerate, int chunksize) {
		try {
			FileOutputStream writer = new FileOutputStream(out);
			//Write file header
			writer.write(header);
			writer.write(intConv(samplerate));
			writer.write(intConv(chunksize));
			writer.write(compmethod);
			writer.write(charcols);
			writer.write(charrows);
			writer.write(version);
			//write frames
			
			//finish
			writer.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void saveVideo() {
		
	}
	
	private byte[] intConv(int in) {
		byte[] res = {(byte)(in & 0xFF), (byte)((in & 0xFF00) >> 8)};
		return res;
	}
}
