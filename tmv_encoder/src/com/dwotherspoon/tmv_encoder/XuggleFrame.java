package com.dwotherspoon.tmv_encoder;

public abstract class XuggleFrame {
	private long timestamp;

	public XuggleFrame(long ts) {
		timestamp = ts;
	}
	
	public long getTimestamp() {
		return timestamp;
	}

}
