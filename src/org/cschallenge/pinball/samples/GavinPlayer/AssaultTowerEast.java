package org.cschallenge.pinball.samples.GavinPlayer;

import org.cschallenge.pinball.engine.Heading;
import org.cschallenge.pinball.engine.ITower;
import org.cschallenge.pinball.engine.PinballEngine.TeamType;
import org.cschallenge.pinball.engine.Position;
import org.cschallenge.pinball.engine.TowerPosition;

public class AssaultTowerEast implements ITower {
	int turn;
	int x;
	int y;
	int expires;
	
	public AssaultTowerEast(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public TowerPosition initialize(int turn) {
		this.turn = turn;
		return new TowerPosition(x, y, 2);
	}

	@Override
	public Heading onCaptureBall(TeamType teamType, Heading heading) {
		return Heading.EAST;
	}

	@Override
	public void onDetectBall(TeamType teamType, Position position) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean extinguish(int turn) {
		return turn > this.turn + 3;
	}

	@Override
	public int getDetectionRadius() {
		// TODO Auto-generated method stub
		return 0;
	}

}
