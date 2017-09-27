package org.cschallenge.pinball.samples.teamM;

import org.cschallenge.pinball.engine.Heading;
import org.cschallenge.pinball.engine.ITower;
import org.cschallenge.pinball.engine.PinballEngine.TeamType;
import org.cschallenge.pinball.engine.Position;
import org.cschallenge.pinball.engine.TowerPosition;

public class SentryTower implements ITower {
	
	TowerPosition position;
	Heading exitHeading;
	
	SentryTower(TowerPosition position) {
		this.position = position;
		if (position.getX() < position.getY()) {
			exitHeading = Heading.NORTH;
		} else if (position.getX() > position.getY()) {
			exitHeading = Heading.EAST;
		} else {
			exitHeading = Math.random() > 0.50 ? Heading.NORTH : Heading.WEST;
		}
	}

	@Override
	public TowerPosition initialize(int turn) {
		return position;
	}

	@Override
	public Heading onCaptureBall(TeamType teamType, Heading heading) {
		return teamType == TeamType.FRIEND ? Heading.SOUTH : exitHeading;
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
