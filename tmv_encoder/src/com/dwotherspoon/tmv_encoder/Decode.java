package com.dwotherspoon.tmv_encoder;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
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
	private IVideoPicture picture;
	private IAudioSamples samples;
	
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
			coder.open(null, null);
		}
		audStream.getSampleRate();
		
		converter = ConverterFactory.createConverter("XUGGLER-BGR-24", vidStream.getPixelType(), vidStream.getWidth(), vidStream.getHeight());
		//TODO allow video or audio only mode
		pkt = IPacket.make();
		offset = pkt.getSize();
	}
	
	   public XuggleVFrame readFrame() {
           int retval = 0;
           do {
                   if (offset < pkt.getSize()) {
                           while (offset < pkt.getSize()) {
                                   if (pkt.getStreamIndex() == vidID) {
                                           offset += vidStream.decodeVideo(picture, pkt, offset);
                                           if (picture.isComplete()) {
                                                   BufferedImage img = converter.toImage(picture);
                                                   return new XuggleVFrame(picture.getTimeStamp(),img);
                                           }
                                   } else if (pkt.getStreamIndex() == audID) {
                                           offset += audStream.decodeAudio(samples, pkt, offset);
                                           if (samples.isComplete()) {
                                                   return null;
                                           }
                                   }
                           }
                   }
                   retval = file.readNextPacket(pkt);
                   if (pkt.getStreamIndex() == 0) {
                           picture = IVideoPicture.make(vidStream.getPixelType(), vidStream.getWidth(), vidStream.getHeight());
                   } else {
                           samples = IAudioSamples.make(1024, audStream.getChannels());
                   }
                   offset = 0;
           } while (retval >= 0);
           
           return null;
   }
	
	
	public XuggleFrame getFrame() { //https://github.com/artclarke/xuggle-xuggler/blob/master/src/com/xuggle/xuggler/demos/DecodeAndPlayAudioAndVideo.java
		int val = 0;
		while (val >= 0) {
				if (pkt.getStreamIndex() == vidID) { //is packet a video?
					picture = IVideoPicture.make(vidStream.getPixelType(), vidStream.getWidth(), vidStream.getHeight());
					 while (offset < pkt.getSize()) { //loop until we've got the whole packet read
						 offset += vidStream.decodeVideo(picture, pkt, offset);
						 if (picture.isComplete()) { //complete frame get - resize and return.
							 cur_frame = new BufferedImage(320, 200, BufferedImage.TYPE_3BYTE_BGR);
							 Graphics g = cur_frame.getGraphics();
							 g.drawImage(converter.toImage(picture), 0, 0, 320, 200, null);
							 g.dispose();
							 return new XuggleVFrame(picture.getTimeStamp(), cur_frame);
						 }
					 }
				}
				else if (pkt.getStreamIndex() == audID) {
					samples = IAudioSamples.make(1024, audStream.getChannels());
					
					while (offset < pkt.getSize()) {
						offset += audStream.decodeAudio(samples, pkt, offset);
						if (samples.isComplete()) {
							return new XuggleAFrame(samples.getTimeStamp());
						}
					}
				}
				offset = 0;
				val = file.readNextPacket(pkt);
		}
		return null;
	}
	
	public double getFrameRate() {
		return vidStream.getFrameRate().getDouble();
	}
	
	public int getSampleRate() {
		return audStream.getSampleRate();
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
