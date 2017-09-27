package org.cschallenge.pinball.samples.teamQ;

import java.util.LinkedList;

import org.cschallenge.pinball.engine.IPlayer;
import org.cschallenge.pinball.engine.ITower;
import org.cschallenge.pinball.engine.PinballEngine.Team;
import org.cschallenge.pinball.engine.PinballEngine.TeamType;
import org.cschallenge.pinball.engine.PinballEngine.TowerQueue;
import org.cschallenge.pinball.engine.PinballEngine.Turn;
import org.cschallenge.pinball.engine.Position;

public class PlayerQ implements IPlayer {

	enum PositionType {
		EMPTY,
		FRIEND,
		FOE,
		OUT_OF_BOUNDS
	}
	
	LinkedList<Position> initialPositions;
	Position friendBall;
	Position foeBall;
	Turn turn;
	
	@Override
	public void init(Team team) {
		initialPositions = new LinkedList<Position>();
		initialPositions.add(new Position(5,5));
		initialPositions.add(new Position(2,8));
		initialPositions.add(new Position(8,2));
	}

	@Override
	public void onExpired(ITower tower) {

	}

	@Override
	public void startTurn(Turn turn, TowerQueue queue) {
		this.turn = turn;
		while (initialPositions.size() > 0) {
			queue.addTower(new TowerQ(this, initialPositions.poll()));
		}
	}

	@Override
	public void onDetectBall(Turn turn, TowerQueue queue, TeamType teamType, Position position) {
		position.advance(1);
		switch (teamType) {
		case FRIEND:
			friendBall = position;
			break;
		case FOE:
			foeBall = position;
			break;
		}
		queue.addTower(new TowerQ(this, position));
	}

	PositionType getPositionType(TeamType[][] board, Position position) {
		if (position.getX() < 0 || 
			position.getY() < 0 || 
			position.getX() > Position.BOARD_SIZE_SQUARES - 1 || 
			position.getY() > Position.BOARD_SIZE_SQUARES - 1) {
			return PositionType.OUT_OF_BOUNDS;
		} else if (board[position.getX()][position.getY()] == null) {
			return PositionType.EMPTY;
		} else {
			return board[position.getX()][position.getY()] == TeamType.FRIEND ? PositionType.FRIEND : PositionType.FOE;
		}
	}

}
