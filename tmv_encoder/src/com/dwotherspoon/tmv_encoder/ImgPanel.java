package com.dwotherspoon.tmv_encoder;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;

/* Simple class for displaying an image on a swing form. */

public class ImgPanel extends JComponent {
	 private BufferedImage img;
	
	 public ImgPanel(int width, int height) {
		super.setSize(width, height);
		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	}
	 
	 public void setImage(BufferedImage im) {
		 img = im;
		 super.repaint();
	 }
	 
	 @Override
	public void paint(Graphics g) {
		super.paint(g);
		g.drawImage(img, 0, 0, null);
	 }

}
