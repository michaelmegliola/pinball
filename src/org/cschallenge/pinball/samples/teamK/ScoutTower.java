package org.cschallenge.pinball.samples.teamK;

import org.cschallenge.pinball.engine.Heading;
import org.cschallenge.pinball.engine.ITower;
import org.cschallenge.pinball.engine.PinballEngine.TeamType;
import org.cschallenge.pinball.engine.Position;
import org.cschallenge.pinball.engine.TowerPosition;

public class ScoutTower implements ITower{

	TowerPosition myPos;
	int turn;
	int lives;
	
	public ScoutTower(TowerPosition pos, int lives) {
		myPos = pos;
		this.lives = lives;
	}
	
	@Override
	public TowerPosition initialize(int turn) {
		this.turn = turn;
		return myPos;
	}

	@Override
	public Heading onCaptureBall(TeamType teamType, Heading heading) {
		if (myPos.getX() > 32) {
			return Heading.NORTH;
		} else if (myPos.getY() > 32) {
			return Heading.EAST;
		}
		switch(heading) {
		case WEST:
			return Heading.EAST;
		case SOUTH:
			return Heading.NORTH;
		default:
			break;
		}
		return null;
	}

	@Override
	public void onDetectBall(TeamType teamType, Position position) {
		return;
	}

	@Override
	public boolean extinguish(int turn) {
		if (this.turn + lives < turn) {
			return true;
		}
		return false;
	}

	@Override
	public int getDetectionRadius() {
		return 5;
	}

}
