package org.cschallenge.pinball.samples.teamQ;

import org.cschallenge.pinball.engine.Heading;
import org.cschallenge.pinball.engine.ITower;
import org.cschallenge.pinball.engine.PinballEngine.TeamType;
import org.cschallenge.pinball.engine.Position;
import org.cschallenge.pinball.engine.TowerPosition;

public class TowerQ implements ITower {

	PlayerQ player;
	Position position;
	
	TowerQ(PlayerQ player, Position position) {
		this.position = position;
	}
	
	@Override
	public TowerPosition initialize(int turn) {
		return new TowerPosition(position);
	}

	@Override
	public Heading onCaptureBall(TeamType teamType, Heading heading) {
		TeamType[][] board = player.turn.board;
		
		return Heading.NORTH;
	}

	@Override
	public void onDetectBall(TeamType teamType, Position position) {


	}

	@Override
	public boolean extinguish(int turn) {
		return false;
	}

	@Override
	public int getDetectionRadius() {
		return 5;
	}

}
