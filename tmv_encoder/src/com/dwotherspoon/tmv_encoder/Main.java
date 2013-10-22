package com.dwotherspoon.tmv_encoder;


public class Main {
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) {
		
		if (args.length > 0) { //some stuff for headless
			System.out.println("TMV Encoder started, to run as gui please relaunch without arugments.");
		}
		else { //load the gui
			new TMVGui();
		}
	}

}
