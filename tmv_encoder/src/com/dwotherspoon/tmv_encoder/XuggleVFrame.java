package com.dwotherspoon.tmv_encoder;

import java.awt.image.BufferedImage;

import com.xuggle.xuggler.IVideoPicture;

public final class XuggleVFrame extends XuggleFrame {
	private BufferedImage img;
		
	public XuggleVFrame(long ts, BufferedImage i) {
		super(ts);
		img = i;
	}
	
	public BufferedImage getImage() {
		return img;
	}
}
