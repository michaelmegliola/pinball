package org.cschallenge.pinball.engine;

import java.awt.image.BufferedImage;

import org.cschallenge.pinball.engine.PinballEngine.Part;
import org.cschallenge.pinball.engine.PinballEngine.Team;

public abstract class GamePiece {
	
	public static final int SIZE_PX = 24;
	
	protected Team team;
	/**
	 * Local X coordinate (origin is 0,0 in lower left)
	 */
	protected int x;
	/**
	 * Local Y coordinate (origin is 0,0 in lower left)
	 */
	protected int y;
	/**
	 * Absolute rendering coordinate (origin is 0,0 in upper left;
	 * vertical axis is reversed)
	 */
	protected int px;
	/**
	 * Absolute rendering coordinate (origin is 0,0 in upper left;
	 * vertical axis is reversed)
	 */
	protected int py;
	/**
	 * Local heading (relates to local coordinates)
	 */
	protected Heading heading;
	
	private BufferedImage imageDim;
	private BufferedImage imageBright;
	private int ticks;
	
	protected GamePiece(Team team) {
		super();
		this.team = team;
		this.heading = Heading.NONE;
		this.imageDim = getImageDim(team);
		this.imageBright = getImageBright(team);
	}
	
	abstract protected BufferedImage getImageDim(Team team);
	abstract protected BufferedImage getImageBright(Team team);
	abstract protected void setRenderingCoordinates();
	abstract protected Part getPart();
	
	final protected BufferedImage getImage(int tickCount) {
		ticks = ticks < tickCount ? tickCount : ticks;
		return ticks > tickCount ? imageBright : imageDim;
	}
		
	public String toString() {
		return getClass().getSimpleName() +  "; team=" + team.name() + "; x=" + x + "; y=" + y + "; heading=" + heading.name();
	}
	
	final protected void flash() {
		ticks += ( SIZE_PX / PinballFrame.ANIMATION_SPEED );
	}
	
	final protected void move() {
		advancePosition();
		if (isOutOfBounds()) resolveOutOfBounds();
	}
	
	protected void resolveOutOfBounds() {
		reverseDirection();
		advancePosition();
		advancePosition();
	}
	
	final protected void advancePosition() {
		x += heading.dx;
		y += heading.dy;
	}
	
	private boolean isOutOfBounds() {
		return x < 0 || x > Position.BOARD_SIZE_SQUARES - 1 || y < 0 || y > Position.BOARD_SIZE_SQUARES - 1;
	}
	
	protected void reverseDirection() {
		heading = heading.reverse();
	}
	
	protected void setHeading(Heading heading) {
		this.heading = heading;
	}
}
