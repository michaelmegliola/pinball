package org.cschallenge.pinball.engine;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import org.cschallenge.pinball.engine.PinballEngine.Mode;
import org.cschallenge.pinball.engine.PinballEngine.Part;
import org.cschallenge.pinball.engine.PinballEngine.Team;

final public class Ball extends GamePiece {
	
	protected Ball(Team team, PinballEngine.Mode mode) {
		super(team);
		switch (team) {
		case RED_TEAM:
			if (mode == Mode.DEBUG) {
				this.x = 0;
				this.y = 0;
				heading = Heading.EAST;
			} else {
				this.x = randomStart();
				this.y = randomStart();
				heading = Heading.random();
			}
			break;
		case GREEN_TEAM:
			if (mode == Mode.DEBUG) {
				this.x = 0;
				this.y = 0;
				heading = Heading.EAST;
			} else {
				this.x = randomStart();
				this.y = randomStart();
				heading = Heading.random();
			}
			break;
		}
		Position position = new Position(this);
		position.convertToRenderingPosition(team);
		this.px = position.x;
		this.py = position.y;
	}
	
	protected BufferedImage getImageDim(Team team) {
		try {
			switch (team) {
			case RED_TEAM:
				return ImageIO.read(PinballFrame.getResource("red-ball.png"));
			case GREEN_TEAM:
				return ImageIO.read(PinballFrame.getResource("green-ball.png"));
			default:
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}
	
	protected BufferedImage getImageBright(Team team) {
		try {
			switch (team) {
			case RED_TEAM:
				return ImageIO.read(PinballFrame.getResource("red-ball-bright.png"));
			case GREEN_TEAM:
				return ImageIO.read(PinballFrame.getResource("green-ball-bright.png"));
			default:
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}
	
	private int randomStart() {
		return ((int) (Math.random() * Position.BOARD_SIZE_SQUARES / 2)) + Position.BOARD_SIZE_SQUARES / 4;
	}
	
	final protected Part getPart() {return Part.BALL;}
	
	@Override
	protected void setRenderingCoordinates() {
		Position position = new Position(this);
		position.convertToRenderingPosition(team);
		int dpx = position.x - px;
		px += ( (dpx < 0) ? -1 : 1 ) * Math.min(Math.abs(dpx), PinballFrame.ANIMATION_SPEED);
		int dpy = position.y - py;
		py += ( (dpy < 0) ? -1 : 1 ) * Math.min(Math.abs(dpy), PinballFrame.ANIMATION_SPEED);
	}
}
