package org.cschallenge.pinball.samples.teamM;

import java.util.LinkedList;

import org.cschallenge.pinball.engine.Heading;
import org.cschallenge.pinball.engine.IPlayer;
import org.cschallenge.pinball.engine.ITower;
import org.cschallenge.pinball.engine.PinballEngine.Team;
import org.cschallenge.pinball.engine.PinballEngine.TeamType;
import org.cschallenge.pinball.engine.PinballEngine.TowerQueue;
import org.cschallenge.pinball.engine.PinballEngine.Turn;
import org.cschallenge.pinball.engine.Position;
import org.cschallenge.pinball.engine.TowerPosition;

public class SamplePlayerM implements IPlayer {

	int blockTurn;
	int shotTurn;
	Position[] sentries = new Position[] {new Position(3,9), new Position(9,9), new Position(9,3)};
	int scoutX = 3;
	int scoutY = 3;
	boolean extinguish = false;
		
	@Override
	public void init(Team team) {

	}

	void setExtinguish() {extinguish = true;}
	boolean getExtinguish() {return extinguish;}
	
	@Override
	public void onExpired(ITower tower) {

	}
	
	@Override
	public void startTurn(Turn turn, TowerQueue queue) {
		// Moved from endTurn
		placeScouts(turn, queue);
		extinguish = false;
		
		placeSentries(turn, queue);
	}
	
	private void placeSentries(Turn turn, TowerQueue queue) {
		if (queue.size() < 2) {
			for (Position position : sentries) {
				if (!turn.isOccupied(position)) {
					queue.addTower(new SentryTower(new TowerPosition(position, 1)));
				}
			}
		}
	}
	
	private void placeScouts(Turn turn, TowerQueue queue) {
		if (queue.size() == 0 && turn.turn > shotTurn) {
			queue.addTower(new ScoutTower(new TowerPosition(scoutX * 6, scoutY * 6, 1), this));
			scoutX += 1;
			if (scoutX > 5) {
				scoutX = 1;
				scoutY += 1;
			}
			if (scoutY > 5) {
				scoutY = 1;
			}
		}
	}

	@Override
	public void onDetectBall(Turn turn, TowerQueue queue, TeamType teamType, Position position) {
		Position home = new Position(0, 0);
		if (teamType == teamType.FOE && turn.turn > blockTurn && home.distance(position) < 15) {
			position.advance(3);
			if (turn.isOccupied(position)) position.advance(1);
			queue.addTower(new SentryTower(new TowerPosition(position, 1)));
			blockTurn = turn.turn + 5;
		}
		if (teamType == teamType.FRIEND && queue.size() < 2) {
			position.advance(3);
			queue.addTower(new ShotTower(new TowerPosition(position, 1), Heading.EAST));
			Position pos2 = new Position(position);
			pos2.setX(Position.BOARD_SIZE_SQUARES - 1 - (int) (Math.random() * 3.0));
			queue.addTower(new ShotTower(new TowerPosition(pos2, 1), Heading.NORTH));
			shotTurn = turn.turn + pos2.getX() - position.getX();
		}
	}

}
