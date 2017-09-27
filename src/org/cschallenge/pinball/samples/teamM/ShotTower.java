package org.cschallenge.pinball.samples.teamM;

import org.cschallenge.pinball.engine.Heading;
import org.cschallenge.pinball.engine.ITower;
import org.cschallenge.pinball.engine.PinballEngine.TeamType;
import org.cschallenge.pinball.engine.Position;
import org.cschallenge.pinball.engine.TowerPosition;

public class ShotTower implements ITower {

	TowerPosition position;
	Heading heading;
	int turn;
	
	ShotTower(TowerPosition position, Heading heading) {
		this.position = position;
		this.heading = heading;
	}
	
	@Override
	public TowerPosition initialize(int turn) {
		this.turn = turn;
		return position;
	}

	@Override
	public Heading onCaptureBall(TeamType teamType, Heading heading) {
		return this.heading;
	}

	@Override
	public void onDetectBall(TeamType teamType, Position position) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean extinguish(int turn) {
		return turn > this.turn + 36;
	}

	@Override
	public int getDetectionRadius() {
		return 0;
	}

}
