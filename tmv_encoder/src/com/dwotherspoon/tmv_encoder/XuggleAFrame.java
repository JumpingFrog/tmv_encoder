package com.dwotherspoon.tmv_encoder;

import com.xuggle.xuggler.IAudioSamples;

public final class XuggleAFrame extends XuggleFrame {
	private IAudioSamples samples;
	
	public XuggleAFrame(long ts, IAudioSamples s) {
		super(ts);
		samples = s;
	}

}
