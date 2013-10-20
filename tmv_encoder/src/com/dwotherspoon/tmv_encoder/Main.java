package com.dwotherspoon.tmv_encoder;

public class Main {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if (args.length > 0) { //some stuff for headless
			System.out.println("TMV Encoder started, to run as gui please relaunch without arugments.");
		}
		else { //load the gui
			new TMVGui();
		}
	}
	

}
