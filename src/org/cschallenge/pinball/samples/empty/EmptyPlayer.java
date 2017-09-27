package org.cschallenge.pinball.samples.empty;

import org.cschallenge.pinball.engine.IPlayer;
import org.cschallenge.pinball.engine.ITower;
import org.cschallenge.pinball.engine.PinballEngine.Team;
import org.cschallenge.pinball.engine.PinballEngine.TeamType;
import org.cschallenge.pinball.engine.PinballEngine.TowerQueue;
import org.cschallenge.pinball.engine.PinballEngine.Turn;
import org.cschallenge.pinball.engine.Position;

public class EmptyPlayer implements IPlayer {

	@Override
	public void init(Team team) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onExpired(ITower tower) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startTurn(Turn turn, TowerQueue queue) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDetectBall(Turn turn, TowerQueue queue, TeamType teamType,
			Position position) {
		// TODO Auto-generated method stub

	}

}
