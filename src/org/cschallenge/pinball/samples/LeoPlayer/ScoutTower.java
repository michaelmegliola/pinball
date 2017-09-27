package org.cschallenge.pinball.samples.LeoPlayer;

import org.cschallenge.pinball.engine.Heading;
import org.cschallenge.pinball.engine.ITower;
import org.cschallenge.pinball.engine.PinballEngine.TeamType;
import org.cschallenge.pinball.engine.PinballEngine.TowerQueue;
import org.cschallenge.pinball.engine.Position;
import org.cschallenge.pinball.engine.TowerPosition;

public class ScoutTower implements ITower{

	Position pos;
	TowerQueue queue;
	int turn;
	
	public ScoutTower(Position p, TowerQueue q) {
		pos = p;
		queue = q;
	}
	
	@Override
	public TowerPosition initialize(int turn) {
		this.turn = turn;
		return new TowerPosition(pos, 2);
	}

	@Override
	public Heading onCaptureBall(TeamType teamType, Heading heading) {
		if (pos.getX() > 32) {
			if (heading == Heading.SOUTH) {
				return Heading.EAST;
			}
			return Heading.NORTH;
		} else if (pos.getY() > 32) {
			if (heading == Heading.WEST) {
				return Heading.NORTH;
			}
			return Heading.EAST;
		} else if (pos.getX() >= pos.getY()) {
			return Heading.EAST;
		} else {
			return Heading.NORTH;
		}
	}

	@Override
	public void onDetectBall(TeamType teamType, Position position) {
		return;
	}

	@Override
	public boolean extinguish(int turn) {
		if (this.turn + 3 == turn) {
			return true;
		}
		return false;
	}

	@Override
	public int getDetectionRadius() {
		return 5;
	}

}
