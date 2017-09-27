package org.cschallenge.pinball.samples.teamA;

import org.cschallenge.pinball.engine.Heading;
import org.cschallenge.pinball.engine.ITower;
import org.cschallenge.pinball.engine.PinballEngine.TeamType;
import org.cschallenge.pinball.engine.Position;
import org.cschallenge.pinball.engine.TowerPosition;

public class SearchTower implements ITower {

	int x;
	int y;
	int expires;
	
	public SearchTower(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public TowerPosition initialize(int turn) {
		return new TowerPosition(x, y, 1);
	}

	@Override
	public Heading onCaptureBall(TeamType teamType, Heading heading) {
		return teamType == TeamType.FRIEND ? Heading.WEST : Heading.EAST;
	}

	@Override
	public boolean extinguish(int turn) {
		return false;
	}

	@Override
	public void onDetectBall(TeamType teamType, Position position) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public int getDetectionRadius() {
		return 5;
	}
}
