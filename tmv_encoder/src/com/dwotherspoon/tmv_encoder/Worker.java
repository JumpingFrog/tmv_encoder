package com.dwotherspoon.tmv_encoder;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import com.dwotherspoon.tmv_encoder.match.Algorithim;
import com.dwotherspoon.tmv_encoder.match.Fast;
import com.dwotherspoon.tmv_encoder.match.Slow;

public class Worker implements Runnable {
	private ConcurrentLinkedQueue<UCell> input;
	private TMVFrame result;
	private int threshold = 60;
	
	public Worker(ConcurrentLinkedQueue<UCell> cells, TMVFrame res) {
		input = cells;
		result = res;
	}

	@Override
	public void run() {
		UCell cur;
		Algorithim matcher;
		while ((cur = input.poll()) != null) { //worker loop
			if (getStdDev(cur.getData()) > threshold) {
				matcher = new Slow();
			}
			else {
				matcher = new Fast();
			}
			result.setCell(cur.getNum(), matcher.match(cur.getData()));
		}
		
	}
	
	private int getStdDev(int[] im) {
		return 0;
	}
	
	private int Red(int col) { //N.B. BGR?
		return (col >> 16) & 0xFF;
	}
	
	private int Blue(int col) {
		return (col >> 8) & 0xFF;
	}
	
	private int Green(int col) {
		return (col & 0xFF);
	}
}
