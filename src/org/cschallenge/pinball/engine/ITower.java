package org.cschallenge.pinball.engine;

import org.cschallenge.pinball.engine.PinballEngine.TeamType;

public interface ITower {
	/**
	 * Called once and only once, when a tower is created. Towers
	 * are created at the end of a player's turn, one per turn,
	 * based upon a pending queue.
	 * 
	 * This method must return the position of the tower, which
	 * includes coordinates (x,y) and the maximum number of
	 * collisions that the tower can sustain before being extinguished.
	 * 
	 * Each time a ball collides with a tower, the remaining number
	 * of collisions is reduced by one; when that count reaches zero,
	 * the tower is extinguished.
	 * 
	 * A tower can also extinguish itself by responding to {@link #extinguish(int)}.
	 * 
	 * @param turn
	 * @return the board position and maximum collisions for this tower.
	 */
	public TowerPosition initialize(int turn);
	/**
	 * Called when a ball collides with this tower. Upon a collision,
	 * a tower can direct the colliding ball in any direction.
	 * 
	 * @param teamType
	 * @param heading
	 * @return the direction for the colliding ball.
	 */
	public Heading onCaptureBall(TeamType teamType, Heading heading);
	/**
	 * Called when a ball passes within detection range of a tower.
	 * Balls are detected within a radius of approximately 5 squares.
	 * 
	 * @param teamType
	 * @param position
	 */
	public void onDetectBall(TeamType teamType, Position position);
	/**
	 * Called once per turn. Returning true causes the tower to be
	 * extinguished.
	 * 
	 * @param turn
	 * @return true to cause the tower to be extinguished; otherwise
	 * false.
	 */
	public boolean extinguish(int turn);
	/**
	 * Called once per turn. Determines the radius at which a ball
	 * can be detected. Minimum is zero (no detection), maximum is
	 * five squares.
	 * 
	 * @return detection radius.
	 */
	public int getDetectionRadius();
}
