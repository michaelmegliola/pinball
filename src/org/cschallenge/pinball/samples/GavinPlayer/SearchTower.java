package org.cschallenge.pinball.samples.GavinPlayer;

import org.cschallenge.pinball.engine.Heading;
import org.cschallenge.pinball.engine.ITower;
import org.cschallenge.pinball.engine.PinballEngine.TeamType;
import org.cschallenge.pinball.engine.Position;
import org.cschallenge.pinball.engine.TowerPosition;

public class SearchTower implements ITower {

	
	int x;
	int y;
	int expires;
	int turn;
	TowerPosition position;

	public SearchTower(Position position) {
		this.x = position.getX();
		this.y = position.getY();
		
	}
	
	@Override
	public TowerPosition initialize(int turn) {
		this.turn = turn;
		return new TowerPosition(x, y, 1);
	}

	@Override
	public Heading onCaptureBall(TeamType teamType, Heading heading) {
		return heading.reverse();
	}

	@Override
	public void onDetectBall(TeamType teamType, Position position) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean extinguish(int turn) {
		return false;

	}

	@Override
	public int getDetectionRadius() {
		// TODO Auto-generated method stub
		return 5;
	}

}
