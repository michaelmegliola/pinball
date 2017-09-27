package org.cschallenge.pinball.samples.LeoPlayer;

import org.cschallenge.pinball.engine.Heading;
import org.cschallenge.pinball.engine.ITower;
import org.cschallenge.pinball.engine.PinballEngine.TeamType;
import org.cschallenge.pinball.engine.PinballEngine.TowerQueue;
import org.cschallenge.pinball.engine.Position;
import org.cschallenge.pinball.engine.TowerPosition;

public class DefenseTower implements ITower {

	private int x;
	private int y;
	private TowerQueue q;
	public DefenseTower(Position position, TowerQueue q) {
		x = position.getX();
		y = position.getY();
		this.q = q;
	}
	
	@Override
	public TowerPosition initialize(int turn) {
		return new TowerPosition(x, y, 1);
	}

	@Override
	public Heading onCaptureBall(TeamType teamType, Heading heading) {
		return Heading.NORTH;
	}

	@Override
	public void onDetectBall(TeamType teamType, Position position) {
		if (teamType == TeamType.FRIEND) {
			
		} else {
			position.advance(1);
			if (position.getHeading() == Heading.SOUTH || position.getHeading() == Heading.WEST) {
				q.addTower(new DefenseCollide(position, 2));
			} else {
				q.addTower(new DefenseCollide(position, 1));
			}
		}
	}

	@Override
	public boolean extinguish(int turn) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getDetectionRadius() {
		return 5;
	}
	
}
