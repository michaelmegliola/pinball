package org.cschallenge.pinball.engine;

/**
 * Heading (relative to local coordinate system, with origin 0,0 in lower left corner).
 * 
 * @author megliola
 *
 */
public enum Heading {
	
	NORTH( 1, 0),
	SOUTH(-1, 0),
	EAST ( 0, 1),
	WEST ( 0,-1),
	NONE ( 0, 0);
	
	final int dx;
	final int dy;
	
	Heading(int dy, int dx) {
		this.dy = dy;
		this.dx = dx;
	}
	
	public Heading reverse() {
		return reverseHorizontal().reverseVertical();
	}
	
	public Heading reverseHorizontal() {
		switch (this) {
		case NORTH:
		case SOUTH:
			return this;
		case EAST:
			return WEST;
		case WEST:
			return EAST;
		case NONE:
		default:
			return NONE;
		}
	}

	public Heading reverseVertical() {
		switch (this) {
		case NORTH:
			return SOUTH;
		case SOUTH:
			return NORTH;
		case EAST:
		case WEST:
			return this;
		case NONE:
		default:
			return NONE;
		}
	}
	
	public static Heading random() {
		return Heading.values()[(int) (Math.random() * 4.0)];
	}
	
	public String toString() {
		return name();
	}
}
