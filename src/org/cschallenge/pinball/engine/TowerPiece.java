package org.cschallenge.pinball.engine;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import org.cschallenge.pinball.engine.PinballEngine.Part;
import org.cschallenge.pinball.engine.PinballEngine.Team;

public class TowerPiece extends GamePiece {

	protected int collisions;
	
	public TowerPiece(Team team, int x, int y, int collisions) {
		super(team);
		this.x = x;
		this.y = y;
		this.collisions = collisions;	
	}
	
	protected BufferedImage getImageDim(Team team) {
		try {
			switch (team) {
			case RED_TEAM:
				return ImageIO.read(PinballFrame.getResource("red-cursor.png"));
			case GREEN_TEAM:
				return ImageIO.read(PinballFrame.getResource("green-cursor.png"));
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
				return ImageIO.read(PinballFrame.getResource("red-cursor-bright.png"));
			case GREEN_TEAM:
				return ImageIO.read(PinballFrame.getResource("green-cursor-bright.png"));
			default:
				return null;
			}
		} catch (Exception e) {
			return null;
		}	
	}

	@Override
	protected void setRenderingCoordinates() {
		Position position = new Position(x, y);
		position.convertToRenderingPosition(team);
		this.px = position.x;
		this.py = position.y;
	}
	
	protected int getCollisions() {return collisions;}
	final protected Part getPart() {return Part.TOWER;}
}
