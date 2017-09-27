package org.cschallenge.pinball.samples.teamB;

import org.cschallenge.pinball.engine.Heading;
import org.cschallenge.pinball.engine.ITower;
import org.cschallenge.pinball.engine.PinballEngine.TeamType;
import org.cschallenge.pinball.engine.Position;
import org.cschallenge.pinball.engine.TowerPosition;

public class BlockTower implements ITower {

	int x;
	int y;
	
	public BlockTower(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public TowerPosition initialize(int turn) {
		return new TowerPosition(x, y, 1);
	}
	
	public Position getPosition() {
		return new Position(x, y);
	}

	@Override
	public Heading onCaptureBall(TeamType teamType, Heading heading) {
		return Math.random() > 0.5 ? Heading.NORTH : Heading.EAST;
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
