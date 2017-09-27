package org.cschallenge.pinball.samples.teamM;

import org.cschallenge.pinball.engine.Heading;
import org.cschallenge.pinball.engine.ITower;
import org.cschallenge.pinball.engine.PinballEngine.TeamType;
import org.cschallenge.pinball.engine.Position;
import org.cschallenge.pinball.engine.TowerPosition;

public class ScoutTower implements ITower {

	SamplePlayerM player;
	boolean extinguish = false;
	Position position;
	int expires;
	
	ScoutTower(Position position, SamplePlayerM player) {
		this.position = position;
		this.player = player;
	}
	
	@Override
	public TowerPosition initialize(int turn) {
		expires = turn + 3;
		return new TowerPosition(position, 1);
	}

	@Override
	public Heading onCaptureBall(TeamType teamType, Heading heading) {
		return teamType == TeamType.FRIEND ? Heading.SOUTH : Heading.EAST;
	}

	@Override
	public void onDetectBall(TeamType teamType, Position position) {
		extinguish = true;
		player.setExtinguish();
	}

	@Override
	public boolean extinguish(int turn) {
		return extinguish || turn > expires || player.getExtinguish();
	}

	@Override
	public int getDetectionRadius() {
		return 5;
	}

}
