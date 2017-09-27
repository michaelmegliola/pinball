package org.cschallenge.pinball.samples.GavinPlayer;

import org.cschallenge.pinball.engine.Heading;
import org.cschallenge.pinball.engine.IPlayer;
import org.cschallenge.pinball.engine.ITower;
import org.cschallenge.pinball.engine.PinballEngine.Team;
import org.cschallenge.pinball.engine.PinballEngine.TeamType;
import org.cschallenge.pinball.engine.PinballEngine.TowerQueue;
import org.cschallenge.pinball.engine.PinballEngine.Turn;
import org.cschallenge.pinball.engine.Position;

public class GavinPlayer implements IPlayer {

	
	int shotTurn;
	public int turnNumber;
	Team ATeam;
	Position enemyBallPosition;
	@Override
	public void init(Team team) {
		ATeam = team;
	}

	@Override
	public void onExpired(ITower tower) {
	
	}

	@Override
	public void startTurn(Turn turn, TowerQueue queue) {

		
		if (queue.size() == 0) {
			if (turn.turn == 1){
				
				
				queue.addTower(new GavTowerDefence(2,6,Heading.EAST,queue));
				queue.addTower(new GavTowerDefence(2,15,Heading.EAST,queue));
				queue.addTower(new GavTowerDefence(12,12,Heading.EAST,queue));
				queue.addTower(new GavTowerDefence(28,28,Heading.NORTH,queue));
				queue.addTower(new GavTowerDefence(6,2,Heading.NORTH,queue));
				queue.addTower(new GavTowerDefence(15,2,Heading.NORTH,queue));

			}
		}
	}
	
	


	@Override
	public void onDetectBall(Turn turn, TowerQueue queue, TeamType teamType,Position position) {

		switch (teamType) 
		{
		case FOE:
			enemyBallPosition=position;
			break;
			
		case FRIEND:
	

			if (turn.turn > turnNumber){
				if((TeamType.FRIEND==teamType)&&(queue.size()==0)){
					
					
					if(enemyBallPosition.distance(0, 0)>=15){
						if(position.getY()!=Position.BOARD_SIZE_SQUARES-1){
							position.advance(2);
							if(position.getY()<=30){
								Position position1234 = new Position(position.getX(),position.getY());
								queue.addTower(new AttackTower2(position1234,Heading.NORTH));
								turnNumber = turn.turn+5;
							}
							else {
								Position position1234 = new Position(position.getX(),position.getY());
								queue.addTower(new AttackTower2(position1234,Heading.EAST));
								turnNumber = turn.turn+5;
							

							}
						
						}
						
						
					}
					
				}
			}
		}
	}
}
			
			
		
	
			
	