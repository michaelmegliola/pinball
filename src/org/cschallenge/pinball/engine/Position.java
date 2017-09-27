package org.cschallenge.pinball.engine;

import org.cschallenge.pinball.engine.PinballEngine.Team;

/**
 * The game coordinate system is based on an origin (0,0) located in the lower
 * left corner of the game board. 
 * 
 * @author megliola
 *
 */
public class Position {
	
	public static final int BOARD_SIZE_SQUARES = 36;
	public static final int BOARD_SIZE_PX = Position.BOARD_SIZE_SQUARES * GamePiece.SIZE_PX;
	
	protected int x;
	protected int y;
	protected Heading heading;
	
	protected Position(GamePiece piece) {
		this(piece.x, piece.y, piece.heading);
	}
	public Position(int x, int y) {
		this(x, y, Heading.NONE);
	}
	public Position(int x, int y, Heading heading) {
		this.x = x;
		this.y = y;
		this.heading = heading;
	}
	public Position(Position source) {
		this(source.x, source.y, source.heading);
	}
	public Heading getHeading() {
		return heading;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public void advance(int distance) {
		advance(heading, distance);
	}
	public void setHeading(Heading heading) {
		this.heading = heading;
	}
	public void setX(int x) {
		this.x = x;
	}
	public void setY(int y) {
		this.y = y;
	}
	public void advance(Heading heading, int distance) {	
		x += heading.dx * distance;
		y += heading.dy * distance;		
	}
	public String toString() {
		return Position.class.getSimpleName() + "; x = " + x + "; y = " + y + "; Heading = " + heading.name();
	}
	/**
	 * Transpose to coordinate system of opposing team.
	 * 
	 * @param team
	 */
	protected void transpose() {
		x = Position.BOARD_SIZE_SQUARES - x - 1;
		y = Position.BOARD_SIZE_SQUARES - y - 1;
		heading = heading.reverse();	
	}
	public double distance(Position position) {
		return distance(position.getX(), position.getY());
	}
	public double distance(int x, int y) {
		return Math.sqrt(Math.pow((double) (this.x - x), 2.0) + Math.pow((double) (this.y - y), 2.0));
	}
	public boolean isInBounds() {
		return x >= 0 && x < Position.BOARD_SIZE_SQUARES && y >= 0 && y < Position.BOARD_SIZE_SQUARES;
	}
	public void convertToRenderingPosition(Team team) {
		if (team == Team.GREEN_TEAM) transpose();
		x = x * GamePiece.SIZE_PX;
		y = BOARD_SIZE_PX - ( (y + 1) * GamePiece.SIZE_PX );
	}
	public boolean coordinatesMatch(Position position) {
		return (x == position.x) && (y == position.y);
	}
	public boolean inEitherGoal() {
		return inOwnGoal() || inOpposingGoal();
	}
	public boolean inOwnGoal() {
		return (x < 3) && (y < 3);
	}
	public boolean inOpposingGoal() {
		return (x >= BOARD_SIZE_SQUARES - 3) && (y >= BOARD_SIZE_SQUARES - 3);
	}
}