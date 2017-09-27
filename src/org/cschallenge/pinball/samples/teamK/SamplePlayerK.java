package org.cschallenge.pinball.samples.teamK;

import org.cschallenge.pinball.engine.IPlayer;
import org.cschallenge.pinball.engine.ITower;
import org.cschallenge.pinball.engine.PinballEngine.Team;
import org.cschallenge.pinball.engine.PinballEngine.TeamType;
import org.cschallenge.pinball.engine.PinballEngine.TowerQueue;
import org.cschallenge.pinball.engine.PinballEngine.Turn;
import org.cschallenge.pinball.engine.Position;
import org.cschallenge.pinball.engine.TowerPosition;

public class SamplePlayerK implements IPlayer {

	Position ballPos = null;
	int a = 4;
	int b = 4;
	
	@Override
	public void init(Team team) {
		return;
	}

	@Override
	public void onExpired(ITower tower) {
		return;
	}
	
	@Override
	public void startTurn(Turn turn, TowerQueue queue) {
		// Keeps queue empty
		if (queue.size() != 0) {
			return;
		}
		// Restores defense towers
		if (!turn.isOccupied(new Position(1,4))) {
			queue.addTower(new DefenseTower(queue, 0));
			return;
		}
		if (!turn.isOccupied(new Position(4,1))) {
			queue.addTower(new DefenseTower(queue, 1));
			return;
		}
		// Adds in scouts if ball isn't located
		if (ballPos == null) {
			setScout(turn, queue);
		}
		ballPos = null;
		return;
	}

	// Creates a scout
	public void setScout(Turn turn, TowerQueue queue) {
		if (!turn.isOccupied(new Position(a,b))) {
			ScoutTower tow = new ScoutTower(new TowerPosition(a, b, 1), 5);
			queue.addTower(tow);
		}
		a += 7;
		if (a > 35) {
			a = 4;
			b += 7;
			if (b > 35) {
				b = 4;
				a = 4;
			}
		}
		return;
	}
	
	// Assumes second method that is called
	@Override
	public void onDetectBall(Turn turn, TowerQueue queue, TeamType teamType, Position position) {
		// Places new tower in front of ball
		if (TeamType.FRIEND == teamType && queue.size() == 0) {
			ballPos = position;
			int x = position.getX();
			int y = position.getY();
			switch(position.getHeading()) {
			case NORTH:
				y += 1;
				break;
			case SOUTH:
				y -= 1;
				break;
			case EAST:
				x += 1;
				break;
			case WEST:
				x -= 1;
				break;
			default:
				break;
			}
			if (turn.isOccupied(new Position(x,y))) {
				// Assumes occupied space is a tower defending
				// Sets up against defending tower
				queue.addTower(new ScoutTower(new TowerPosition(ballPos, 1), 3));
			} else {
				// Adds tower in to move toward goal
				queue.addTower(new ScoutTower(new TowerPosition(x, y, 2), 4));
			}
		}
		return;
	}


}
