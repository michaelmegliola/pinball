package org.cschallenge.pinball.samples.teamB;

import org.cschallenge.pinball.engine.Heading;
import org.cschallenge.pinball.engine.IPlayer;
import org.cschallenge.pinball.engine.ITower;
import org.cschallenge.pinball.engine.PinballEngine.Team;
import org.cschallenge.pinball.engine.PinballEngine.TeamType;
import org.cschallenge.pinball.engine.PinballEngine.TowerQueue;
import org.cschallenge.pinball.engine.PinballEngine.Turn;
import org.cschallenge.pinball.engine.Position;
import org.cschallenge.pinball.samples.teamA.ShotTower;

public class SamplePlayerB implements IPlayer {
	
	private boolean foundBall;
	private Position blockMissing;
	
	@Override
	public void init(Team team) {
		foundBall = false;
	}

	@Override
	public void onExpired(ITower tower) {
		if (tower instanceof BlockTower) {
			BlockTower blockTower = (BlockTower) tower;
			blockMissing = blockTower.getPosition();
		}
	}

	@Override
	public void onDetectBall(Turn turn, TowerQueue queue, TeamType teamType, Position position) {
		switch (teamType) {
		case FOE:
			position.advance(1);
			queue.addTower(new ShotTower(position, Heading.WEST));
			break;
		case FRIEND:
			if (queue.size() == 0) {
				position.advance(1);
				Position position2 = new Position(position.getX(), Position.BOARD_SIZE_SQUARES-1, Heading.NONE);
				if (!(turn.isOccupied(position) || turn.isOccupied(position2))) {
					queue.addTower(new ShotTower(position, Heading.NORTH));
					queue.addTower(new ShotTower(position2, Heading.EAST));
				}
			}
			break;
		}
	}

	@Override
	public void startTurn(Turn turn, TowerQueue queue) {

		if (turn.turn == 1) {
			queue.addTower(new BlockTower(0,4));
			queue.addTower(new BlockTower(1,4));
			queue.addTower(new BlockTower(2,4));
			queue.addTower(new BlockTower(4,2));
			queue.addTower(new BlockTower(4,1));
			queue.addTower(new BlockTower(4,0));
		} else if (blockMissing != null) {
			queue.addTower(new BlockTower(blockMissing.getX(), blockMissing.getY()));
			blockMissing = null;
		} else if (queue.size() == 0) {
			queue.addTower(new RandomTower());
		}
	}
	
	public boolean foundBall() {
		return foundBall;
	}

}
