package org.cschallenge.pinball.samples.GavinPlayer;

import org.cschallenge.pinball.engine.Heading;
import org.cschallenge.pinball.engine.ITower;
import org.cschallenge.pinball.engine.PinballEngine.TeamType;
import org.cschallenge.pinball.engine.Position;
import org.cschallenge.pinball.engine.TowerPosition;

public class AttackTower2 implements ITower {


	int x;
	int y;
	int expires;
	int turn;
	TowerPosition position;

	Heading heading;
	
	public AttackTower2(Position position,Heading heading) {
		this.x = position.getX();
		this.y = position.getY();
		this.heading = heading;
	}
	
	@Override
	public TowerPosition initialize(int turn) {
		this.turn = turn;
		return new TowerPosition(x, y, 2);
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
		return turn > this.turn + 30;
	}

	@Override
	public int getDetectionRadius() {
		return 5;
	}

}
