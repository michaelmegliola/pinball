package org.cschallenge.pinball.samples.LeoPlayer;

import org.cschallenge.pinball.engine.Heading;
import org.cschallenge.pinball.engine.ITower;
import org.cschallenge.pinball.engine.PinballEngine.TeamType;
import org.cschallenge.pinball.engine.Position;
import org.cschallenge.pinball.engine.TowerPosition;

public class DefenseCollide implements ITower {

	Position pos;
	int myTurn;
	int col;
	boolean shouldKill = true;;
	
	public DefenseCollide(Position pos, int collisions) {
		this.pos = pos;
		col = collisions;
	}
	
	public DefenseCollide(Position pos, int collisions, boolean shouldKill) {
		this.pos = pos;
		col = collisions;
		this.shouldKill = shouldKill;
	}
	
	@Override
	public TowerPosition initialize(int turn) {
		myTurn = turn;
		return new TowerPosition(pos, col);
	}

	@Override
	public Heading onCaptureBall(TeamType teamType, Heading heading) {
		if (pos.getHeading() == Heading.SOUTH) {
			return Heading.EAST;
		} else if (pos.getHeading() == Heading.WEST) {
			return Heading.NORTH;
		} else if (pos.getHeading() == Heading.NORTH) {
			return Heading.EAST;
		} else {
			return Heading.NORTH;
		}
	}

	@Override
	public void onDetectBall(TeamType teamType, Position position) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean extinguish(int turn) {
		if (myTurn + 3 == turn && shouldKill) {
			return true;
		}
		return false;
	}

	@Override
	public int getDetectionRadius() {
		// TODO Auto-generated method stub
		return 0;
	}

}
