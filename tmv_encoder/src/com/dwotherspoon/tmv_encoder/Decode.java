package com.dwotherspoon.tmv_encoder;

import java.awt.image.BufferedImage;

import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;

/* Decodes video files */

public class Decode {
	private IContainer file;
	private int vidID = -1;
	private int audID = -1;
	private IStreamCoder vidStream = null;
	private IStreamCoder audStream = null;
	private IConverter converter;
	private BufferedImage cur_frame;
	private IPacket pkt;
	private int offset;
	
	public Decode(String in) {
		file = IContainer.make();
		file.open(in, IContainer.Type.READ, null);
		System.out.println("Found " + file.getNumStreams() + " streams in file.");
		for (int s = 0; s < file.getNumStreams(); s++) {
			IStream temp = file.getStream(s);
			IStreamCoder coder = temp.getStreamCoder();
			if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) { //TODO at some point allow user selection of streams
				vidID = s;
				vidStream = coder;
				System.out.println("Video stream found: " + coder.toString());
			}
			else if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_AUDIO) {
				audID = s;
				audStream = coder;
				System.out.println("Audio stream found: " + coder.toString());
			}
		}
		converter = ConverterFactory.createConverter("XUGGLER-BGR-24", vidStream.getPixelType(), vidStream.getWidth(), vidStream.getHeight());
		//TODO allow video or audio only mode
		pkt = IPacket.make();
		offset = pkt.getSize();
	}
	
	public void getFrame() {
		while (file.readNextPacket(pkt) > 0) {
			
		}
		
	}
	
	public double getFrameRate() {
		return vidStream.getFrameRate().getDouble();
	}
	
	public IStreamCoder getAudio() {
		return audStream;
	}
	
	public IStreamCoder getVideo() {
		return vidStream;
	}
	
	public void dispose() {
		vidStream.close();
		audStream.close();
		file.close();
	}
}
