package com.dwotherspoon.tmv_encoder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Queue;

/* Class for outputting finished videos */

public class Encode { //format info from AVCOMP.PAS (available from http://www.oldskool.org/pc/8088_Corruption/8088flex.zip)
	static byte[] header = {'T', 'M', 'A', 'V'}; //header
	static byte compmethod = 0; //compression method, always 0.
	static byte charcols = 40; //number of columns.
	static byte charrows = 25; //number of rows
	static byte version = 0; //special handling, 0 for none, 1 for 512 byte boundary padding.
	private Queue<TMVFrame> video;
	private byte[] audio;
	
	public Encode(Queue<TMVFrame> video, ByteArrayOutputStream audio) {
		this.video = video;
		this.audio = audio.toByteArray();
	}
	
	public void saveTMV(File out, int samplerate, int chunksize) {
		System.out.println("Saving file:");
		System.out.println("Sample rate: " + samplerate);
		System.out.println("Chunk size: " + chunksize);
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
			TMVFrame cur_frame;
			
			int audio_p = 0; //audio pointer
			
			while ((cur_frame = video.poll()) != null) { //the write loop
				for (int c = 0; c < 1000; c++) {
					Cell cur_cell = cur_frame.getCell(c);
					writer.write((byte)cur_cell.getCha());
					writer.write(colourConv(cur_cell.getForeground(), cur_cell.getBackground()));
				}
				for (int s = 0; s < chunksize; s++) {
					writer.write(audio[audio_p++]);
				}
			}
			
			//finish
			writer.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void saveVideo() {
		//TODO one day save a video
	}
	
	private byte colourConv(byte fore, byte back) {
		return (byte)((byte)((back & 0x0F) << 4) | (byte)(fore & 0x0F));
	}
	private byte[] intConv(int in) {
		byte[] res = {(byte)(in & 0xFF), (byte)((in & 0xFF00) >> 8)};
		return res;
	}
}
