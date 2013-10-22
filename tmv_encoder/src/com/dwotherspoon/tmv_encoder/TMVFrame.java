package com.dwotherspoon.tmv_encoder;

import java.awt.image.BufferedImage;

/* A data structure for TMV video frames */

public class TMVFrame {
	private Cell[] cells;
	
	private int[] colours = new int[] { //CGA Colours
              0xFF000000,
              0xFF0000AA,
              0xFF00AA00,
              0xFF00AAAA,
              0xFFAA0000,
              0xFFAA00AA,
              0xFFAA5500,
              0xFFAAAAAA,
              0xFF555555,
              0xFF5555FF,
              0xFF55FF55,
              0xFF55FFFF,
              0xFFFF5555,
              0xFFFF55FF,
              0xFFFFFF55,
              0xFFFFFFFF
      };
	
	public TMVFrame() {
		cells = new Cell[1000];
	}
	
	public void setCell(int n, int cha, byte foreground, byte background) {
		cells[n].setCha(cha);
		cells[n].setForeground(foreground);
		cells[n].setBackground(background);
	}
	
	public void setCell(int n, Cell c) {
		cells[n] = c;
	}
	
	public Cell getCell(int n) {
		return cells[n];
	}
	
	public BufferedImage render(boolean[][] font) {
		BufferedImage res = new BufferedImage(320, 200, BufferedImage.TYPE_3BYTE_BGR);
		for (int row = 0; row < 25; row++) {
			for (int col = 0; col < 40; col++) {
				for (int y = 0; y < 8; y++) {
					for (int x = 0; x < 8; x++) {
						if (font[cells[(row*40)+col].getCha()][(y*8) + x]) {
							res.setRGB((col * 8) + x, (row*8) + y, colours[cells[(row*40)+col].getForeground()]);
						}
						else {
							res.setRGB((col * 8) + x, (row*8) + y, colours[cells[(row*40)+col].getBackground()]);
						}
					}
				}
			}
			
		}
		return res;
	}

}
