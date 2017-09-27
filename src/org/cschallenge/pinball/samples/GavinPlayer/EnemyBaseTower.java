package org.cschallenge.pinball.samples.GavinPlayer;

import org.cschallenge.pinball.engine.Heading;
import org.cschallenge.pinball.engine.ITower;
import org.cschallenge.pinball.engine.PinballEngine.TeamType;
import org.cschallenge.pinball.engine.Position;
import org.cschallenge.pinball.engine.TowerPosition;

public class EnemyBaseTower implements ITower {

	
	TowerPosition position;
	Heading heading;
	int turn;
	
	EnemyBaseTower(TowerPosition position) {
		this.position = position;
	}
	
	@Override
	public TowerPosition initialize(int turn) {
		return new TowerPosition(position, 1);
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getDetectionRadius() {
		// TODO Auto-generated method stub
		return 5;
	}

}
