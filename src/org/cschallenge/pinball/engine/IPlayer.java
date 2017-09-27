package org.cschallenge.pinball.engine;

import org.cschallenge.pinball.engine.PinballEngine.Team;
import org.cschallenge.pinball.engine.PinballEngine.TeamType;
import org.cschallenge.pinball.engine.PinballEngine.TowerQueue;
import org.cschallenge.pinball.engine.PinballEngine.Turn;

/**
 * Interface required for a class that represents a player in a game of Pinball.
 * When implementing an IPlayer, assume that the home goal resides in the lower left 
 * corner of the board, at the origin (coordinates: 0,0) and that coordinate system is 
 * mathematical (ie, the vertical axis increases numerically in the upward direction,
 * and the horizontal axis increases numerically from left to right).
 * 
 * All inbound information considering position or direction will respect that
 * orientation. In addition, the direction Heading.NORTH is upward, Heading.SOUTH 
 * is downward, Heading.EAST is to the right, and Heading.WEST is to the left.
 * 
 * @author megliola, ergin, moore
 *
 */
public interface IPlayer {
	/**
	 * Called once per game, prior to the first turn.
	 * 
	 * @param team the team color assigned to this IPlayer for this game.
	 */
	public void init(Team team);
	/**
	 * Called when any of this IPlayer's towers expire (ie, when a tower 
	 * reports that it has expired or has no collisions remaining).
	 * 
	 * @param tower the expired tower
	 */
	public void onExpired(ITower tower);
	/**
	 * Called at the start of each of this IPlayer's turns.
	 * 
	 * @param turn
	 * @param queue
	 */
	public void startTurn(Turn turn, TowerQueue queue);
	/**
	 * Called when any of this player's towers detects a ball.
	 * 
	 * @param turn
	 * @param queue
	 */
	public void onDetectBall(Turn turn, TowerQueue queue, TeamType teamType, Position position);
}
