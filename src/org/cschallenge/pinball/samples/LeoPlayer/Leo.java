package org.cschallenge.pinball.samples.LeoPlayer;

import org.cschallenge.pinball.engine.IPlayer;
import org.cschallenge.pinball.engine.ITower;
import org.cschallenge.pinball.engine.PinballEngine.Team;
import org.cschallenge.pinball.engine.PinballEngine.TeamType;
import org.cschallenge.pinball.engine.PinballEngine.TowerQueue;
import org.cschallenge.pinball.engine.PinballEngine.Turn;
import org.cschallenge.pinball.engine.Position;

public class Leo implements IPlayer {
	
	int a = 4;
	int b = 4;
	boolean ballPos = false;
	
	@Override
	public void init(Team team) {
	}

	@Override
	public void onExpired(ITower tower) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startTurn(Turn turn, TowerQueue queue) {
		checkLine(turn, queue);

		if (!turn.isOccupied(new Position(3,3))) {
			queue.addTower(new DefenseTower(new Position(3,3), queue));
		}
		
		if (!turn.isOccupied(new Position(32, 32)) && queue.size() == 0) {
			queue.addTower(new DefenseTower(new Position(32, 32), queue));
		} else if (turn.board[32][32] == TeamType.FOE){
			if (!turn.isOccupied(new Position(31, 31)) && queue.size() == 0) {
				queue.addTower(new DefenseTower(new Position(31, 31), queue));
			}
		}
		
		if (queue.size() == 0 && turn.collisions > 3 && !ballPos ) {
			createScoutTower(turn, queue);
		}
		
		ballPos = false;
		
	}
	
	public void createScoutTower(Turn turn, TowerQueue queue) {
		if (!turn.isOccupied(new Position(a,b))) {
			ScoutTower tow = new ScoutTower(new Position(a, b), queue);
			queue.addTower(tow);
		}
		a += 7;
		if (a > 35) {
			a = 4;
			b += 7;
			if (b > 35) {
				b = 4;
				a = 4;
			}
		}
	}
	
	public void checkLine(Turn turn, TowerQueue queue) {
		TeamType[][] board = turn.board;
		for (int i = 0; i < 3; i ++) {
			if (board[i][4] == TeamType.FOE && board[i][5] == TeamType.FOE && board[i][6] == TeamType.FOE) {
				queue.addTower(new DefenseCollide(new Position(i, 3), 1, false));
				return;
			}
		}
		for (int i = 0; i < 3; i ++) {
			if (board[4][i] == TeamType.FOE && board[5][i] == TeamType.FOE && board[6][i] == TeamType.FOE) {
				queue.addTower(new DefenseCollide(new Position(3, i), 1, false));
				return;
			}
		}
		for (int i = 0; i < 3; i ++) {
			if (board[i][3] == TeamType.FOE && board[i][4] == TeamType.FOE) {
				queue.addTower(new DefenseCollide(new Position(i, 5), 1, false));
				return;
			}
		}
		for (int i = 0; i < 3; i ++) {
			if (board[3][i] == TeamType.FOE && board[4][i] == TeamType.FOE) {
				queue.addTower(new DefenseCollide(new Position(5, i), 1, false));
				return;
			}
		}
	}

	@Override
	public void onDetectBall(Turn turn, TowerQueue queue, TeamType teamType, Position position) {
		if (queue.size() == 0 && teamType == TeamType.FRIEND) {
			ballPos = true;
			position.advance(1);
			queue.addTower(new ScoutTower(position, queue));
			position.advance(1);
			queue.addTower(new ScoutTower(position, queue));
		}
	}

}
