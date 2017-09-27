package org.cschallenge.pinball.samples.GavinPlayer;

import org.cschallenge.pinball.engine.Heading;
import org.cschallenge.pinball.engine.ITower;
import org.cschallenge.pinball.engine.PinballEngine.TeamType;
import org.cschallenge.pinball.engine.PinballEngine.TowerQueue;
import org.cschallenge.pinball.engine.Position;
import org.cschallenge.pinball.engine.TowerPosition;

public class GavTowerDefence implements ITower {

	int x;
	int y;
	int expires;
	Heading heading;
	TowerPosition position;
	TowerQueue queue;
	int placement;
	
	

	

	
	
	public GavTowerDefence(int x, int y, Heading heading, TowerQueue queue) {
		this.x = x;
		this.y = y;
		this.heading = heading;
		this.queue = queue;
	}
	
	@Override
	public TowerPosition initialize(int turn) {
		return new TowerPosition(x, y, 1);
	}

	@Override
	public Heading onCaptureBall(TeamType teamType, Heading heading) {
		if(heading==Heading.WEST){
			return Heading.NORTH;
		}
		if(heading==Heading.SOUTH){
			return Heading.EAST;
			
		}
		if(heading==Heading.NORTH){
			return Heading.NORTH;
		}
		if(heading==Heading.EAST){
			return Heading.EAST;
		}
		return heading.reverse();
	}

	@Override
	public void onDetectBall(TeamType teamType, Position position) {
//		
		if (TeamType.FOE == teamType){
			position.advance(2);
			queue.addTower(new InstaTowerY(position.getX(),position.getY()));
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
