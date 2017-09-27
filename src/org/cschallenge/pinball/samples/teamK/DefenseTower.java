package org.cschallenge.pinball.samples.teamK;

import org.cschallenge.pinball.engine.Heading;
import org.cschallenge.pinball.engine.ITower;
import org.cschallenge.pinball.engine.PinballEngine.TeamType;
import org.cschallenge.pinball.engine.PinballEngine.TowerQueue;
import org.cschallenge.pinball.engine.Position;
import org.cschallenge.pinball.engine.TowerPosition;

public class DefenseTower implements ITower {

	boolean main = false;
	TowerPosition pos;
	TowerQueue queue;
	int placement;
	
	
	// Different constructors for main defense towers and sub towers
	public DefenseTower(TowerQueue queue, int placement) {
		main = true;
		this.queue = queue;
		this.placement = placement;
	}
	
	public DefenseTower(TowerPosition pos) {
		this.pos = pos;
	}
	
	@Override
	public TowerPosition initialize(int turn) {
		if (main) {
			if (placement == 0) {
				return new TowerPosition(1, 4, 1);
			} else {
				return new TowerPosition(4, 1, 1);
			}
		}
		return pos;
	}

	@Override
	public Heading onCaptureBall(TeamType teamType, Heading heading) {
		if (heading == Heading.SOUTH) {
			return Heading.EAST;
		}
		if (heading == Heading.WEST) {
			return Heading.NORTH;
		}
		return null;
	}

	// Assumes this method is called first
	@Override
	public void onDetectBall(TeamType teamType, Position position) {
		// Makes sure queue is empty and is enemy ball
		if (TeamType.FOE == teamType && queue.size() == 0) {
			int x = position.getX();
			int y = position.getY();
			// Adds new tower to knock ball away
			switch(position.getHeading()) {
			case SOUTH:
				queue.addTower(new DefenseTower(new TowerPosition(x, y-1, 1)));
				break;
			case WEST:
				queue.addTower(new DefenseTower(new TowerPosition(x-1, y, 1)));
				break;
			default:
				break;
			}
		}
	}

	@Override
	public boolean extinguish(int turn) {
		return false;
	}

	@Override
	public int getDetectionRadius() {
		if (this.main) {
			return 5;
		} else {
			return 0;
		}
	}

}
