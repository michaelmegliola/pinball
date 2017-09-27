package org.cschallenge.pinball.engine;

public class PinballException extends Exception {
	public PinballException(String s) {super(s);}
	public PinballException(String s, Exception e) {super(s, e);}
}
