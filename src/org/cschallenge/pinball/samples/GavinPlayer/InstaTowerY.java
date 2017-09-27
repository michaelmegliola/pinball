package org.cschallenge.pinball.samples.GavinPlayer;

import org.cschallenge.pinball.engine.Heading;
import org.cschallenge.pinball.engine.ITower;
import org.cschallenge.pinball.engine.PinballEngine.TeamType;
import org.cschallenge.pinball.engine.Position;
import org.cschallenge.pinball.engine.TowerPosition;

public class InstaTowerY implements ITower {

	int turnWhenCreated = 0;
	int x;
	int y;
	int expires;
	int turn;
	public InstaTowerY(int x, int y) {
		this.x = x;
		this.y = y;
	}
		
	@Override
	public TowerPosition initialize(int turn) {
		this.turn = turn;
		return new TowerPosition(x, y, 1);
	}

	@Override
	public Heading onCaptureBall(TeamType teamType, Heading heading) {
		
		return Heading.NORTH;
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
		
		return 5;
	}

}