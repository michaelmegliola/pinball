package org.cschallenge.pinball.samples.teamA;

import org.cschallenge.pinball.engine.Heading;
import org.cschallenge.pinball.engine.IPlayer;
import org.cschallenge.pinball.engine.ITower;
import org.cschallenge.pinball.engine.PinballEngine.Team;
import org.cschallenge.pinball.engine.PinballEngine.TeamType;
import org.cschallenge.pinball.engine.PinballEngine.TowerQueue;
import org.cschallenge.pinball.engine.PinballEngine.Turn;
import org.cschallenge.pinball.engine.Position;

public class SamplePlayerA implements IPlayer {
	
	Team myTeam;
	
	@Override
	public void init(Team team) {
		myTeam = team;
	}

	@Override
	public void onExpired(ITower tower) {

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
		
//		if (queue.size() == 0) {
//			if (turn.turn == 1) {
//				queue.addTower(new SearchTower(8, 8));
//				queue.addTower(new SearchTower(16, 16));
//				queue.addTower(new SearchTower(24, 24));
//				queue.addTower(new SearchTower(8, 24));
//				queue.addTower(new SearchTower(24, 8));
//				queue.addTower(new SearchTower(3, 16));
//				queue.addTower(new SearchTower(29, 16));
//			}
//		}
	}

}
