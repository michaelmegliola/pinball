package org.cschallenge.pinball.samples.teamB;

import org.cschallenge.pinball.engine.Heading;
import org.cschallenge.pinball.engine.ITower;
import org.cschallenge.pinball.engine.Position;
import org.cschallenge.pinball.engine.TowerPosition;
import org.cschallenge.pinball.engine.PinballEngine.TeamType;

public class RandomTower implements ITower {

	int x;
	int y;
	int expires;
	
	public RandomTower() {
		x = (int) (Math.random() * 20 + 6);
		y = (int) (Math.random() * 20 + 6);	
	}
	
	@Override
	public TowerPosition initialize(int turn) {
		this.expires = turn + 3;
		return new TowerPosition(x, y, 1);
	}

	@Override
	public Heading onCaptureBall(TeamType teamType, Heading heading) {
		return teamType == TeamType.FRIEND ? Heading.WEST : Heading.NORTH;
	}

	@Override
	public boolean extinguish(int turn) {
		return turn > expires;
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
