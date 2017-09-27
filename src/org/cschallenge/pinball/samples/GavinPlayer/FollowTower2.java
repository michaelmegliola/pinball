package org.cschallenge.pinball.samples.GavinPlayer;

import org.cschallenge.pinball.engine.Heading;
import org.cschallenge.pinball.engine.ITower;
import org.cschallenge.pinball.engine.PinballEngine.TeamType;
import org.cschallenge.pinball.engine.Position;
import org.cschallenge.pinball.engine.TowerPosition;

public class FollowTower2 implements ITower {


	
	int x;
	int y;
	int expires;
	int turn;
	TowerPosition position;

	public FollowTower2(Position position) {
		this.x = position.getX();
		this.y = position.getY();
		
	}
	
	@Override
	public TowerPosition initialize(int turn) {
		this.turn = turn;
		return new TowerPosition(x, y, 2);
		
	}

	@Override
	public Heading onCaptureBall(TeamType teamType, Heading heading) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onDetectBall(TeamType teamType, Position position) {
		
		
	}

	@Override
	public boolean extinguish(int turn) {
		return turn > this.turn + 10;
	}

	@Override
	public int getDetectionRadius() {
		
		return 5;
	}

}
