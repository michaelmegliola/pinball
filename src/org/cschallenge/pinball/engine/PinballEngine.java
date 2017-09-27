package org.cschallenge.pinball.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.cschallenge.pinball.samples.LeoPlayer.Leo;
import org.cschallenge.pinball.samples.empty.EmptyPlayer;
import org.cschallenge.pinball.samples.teamK.SamplePlayerK;
import org.jarclassloader.LoadClass;

/**
 * Controlling class that manages and executes an automated game of Pinball,
 * played between two instances of IPlayer. Each player codes a solution to
 * the game assuming that the home goal resides in the lower left corner of
 * the board, at the origin (coordinates: 0,0). In an actual game, one player's
 * goal will be located in the upper right corner of the board (coordinates:
 * BOARD_SIZE_SQUARES-1, BOARD_SIZE_SQUARES-1). This class will transpose
 * coordinates to account for that difference at runtime. Coding an IPlayer
 * does not require any consideration of which goal is assigned to which
 * player.
 * <br><br>
 * The execution order is as follows:<br>
 * 1. Pinballs are moved<br>
 * 2. onCollision() is called in ITower objects<br>
 * 3. extinguishDeadTowers() is called<br>
 * 4. onDetectBall() is called in ITower objects<br>
 * 5. onDetectBall() is called in IPlayer<br>
 * 6. startTurn() is called in IPlayer<br>
 * 7. extinguishChosenTowers() is called<br>
 * 8. Item(s) popped off queue
 * 
 * @author megliola, ergin, moore
 *
 */
public class PinballEngine {
	
	// Change players here!
	private IPlayer redPlayer = new SamplePlayerK();
	private IPlayer greenPlayer = new Leo();
	
	// Change mode here!
	protected Mode mode = Mode.CLIENT;
	
	public enum Part {
		BALL,
		TOWER
	}
	
	private class PlayerProfile {
		Team team;
		int collisions;
		TowerQueue queue;
		PlayerProfile(Team team, int collisions) {
			this.team = team;
			this.collisions = collisions;
			this.queue = new TowerQueue();
		}
		public String toString() {
			return "Player="+ team.name() + "; collisions=" + collisions;
		}
	}
	
	public enum Result {
		RED_VICTORY,
		GREEN_VICTORY,
		TIE_GAME,
		PLAY_ON
	}
	
	public enum Team {
		RED_TEAM,
		GREEN_TEAM
	}
	
	public enum TeamType {
		FRIEND,
		FOE
	}
	
	/**
	 * For choosing the mode the engine runs on.
	 * Client mode is the default runtime mode.
	 * Server mode grabs class files from server based on arguments.
	 * This is built for jnlp arguments allowing only the jnlp file to be changed.
	 * Debug mode is for debugging AIs.
	 * Debug mode allows for users to choose the starting positions and directions of the balls.
	 * You can set the positions under Ball.java
	 * @author ergin
	 *
	 */
	public enum Mode {
		CLIENT,
		SERVER,
		DEBUG
	}
	
	private class TowerInstance {
		final IPlayer player;
		final ITower code;
		final TowerPiece piece;
		TowerInstance(IPlayer player, ITower tower, TowerPiece piece) {
			this.player = player;
			this.code = tower;
			this.piece = piece;
		}
	}
	
	public class TowerQueue {
		private LinkedList<TowerRequest> queue;
		private TowerQueue() {
			queue = new LinkedList<TowerRequest>();
		}
		public void addTower(ITower tower) {
			try {
				queue.add(new TowerRequest(tower));
			} catch (Exception e) {
				// no code.
			}
		}
		public int size() {
			return queue.size();
		}
		public String toString() {
			StringBuilder buf = new StringBuilder();
			for (int i = 0; i < queue.size(); i++) {
				if (buf.length() > 0) buf.append(" ");
				buf.append("(" + i + ") " + queue.get(i).toString());
			}
			return buf.toString();
		}
	}
	
	private class TowerRequest {
		final private ITower tower;
		final private TowerPosition position;
		public TowerRequest(ITower tower) {
			this.tower = tower;
			this.position = tower.initialize(turn / 2);
		}
		public String toString() {
			return getClass().getSimpleName() + " " + position.toString();
		}
	}
	
	public class Turn {
		
		public final int turn;
		public final Team team;
		public final int collisions;
		public final TeamType[][] board;
		
		public Turn(int turn, Team team, int collisions, TeamType[][] board) {
			this.turn = turn;
			this.team = team;
			this.collisions = collisions;
			this.board = board;
		}
		
		public boolean isOccupied(Position position) {
			try {
				return board[position.x][position.y] != null;
			} catch (Exception e) {
				return false;
			}
		}
	}
	
	public static final int INITIAL_COLLISIONS = 12;
	private static final int QUEUEPOP_DEFAULT = 3;
	
	private HashMap<IPlayer, PlayerProfile> profileMap;
	private HashSet<GamePiece> activePieceSet;
	private HashSet<GamePiece> deletedPieceSet;
	private HashMap<TowerPiece, TowerInstance> towers;
	
	private LoadClass redLoad;
	private LoadClass greenLoad;
	
	// For random queue processing
	public final int queuePop;
	private Random rand = new Random();
	
	private int numBalls = 6;
	private Ball[] allBalls = new Ball[numBalls];
	
	protected boolean isTied = false;
	
	private int turn;
	
	private Thread mainThread;
	private Thread myTimer;
	private boolean killedThread = false;
	
	protected PinballEngine(Class playerOne, Class playerTwo, Mode setMode) {
		mode = setMode;
		queuePop = QUEUEPOP_DEFAULT;
		
		try {
			redPlayer = (IPlayer) playerOne.getConstructor().newInstance();
			greenPlayer = (IPlayer) playerTwo.getConstructor().newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		activePieceSet = new HashSet<GamePiece>();
		deletedPieceSet = new HashSet<GamePiece>();
		allBalls[0] = new Ball(Team.RED_TEAM, mode);
		allBalls[1] = new Ball(Team.GREEN_TEAM, mode);
		for (int i = 0; i < allBalls.length; i ++) {
			if (allBalls[i] != null) {
				addPiece(allBalls[i]);
			}
		}
		try {redPlayer.init(Team.RED_TEAM);} catch (Exception e) {/* no code */}
		try {greenPlayer.init(Team.GREEN_TEAM);} catch (Exception e) {/* no code */}
		profileMap = new HashMap<IPlayer, PlayerProfile>();
		profileMap.put(redPlayer, new PlayerProfile(Team.RED_TEAM, INITIAL_COLLISIONS));
		profileMap.put(greenPlayer, new PlayerProfile(Team.GREEN_TEAM, INITIAL_COLLISIONS));
		towers = new HashMap<TowerPiece, TowerInstance>();
	}
	
	protected PinballEngine(String[] args) {
		
		int hold = QUEUEPOP_DEFAULT;
		
		if (mode == Mode.SERVER) {
			// For loading from server
			// Uses arguments for loading classes
			
			// Arguments are as follows:
			// 1. class path
			// 2. player name
			redLoad = new LoadClass(args[0], args[1]);
			greenLoad = new LoadClass(args[2], args[3]);
			redPlayer = (IPlayer)redLoad.getPlayer();
			greenPlayer = (IPlayer)greenLoad.getPlayer();
			
			if (args.length > 4) {
				try {
					hold = Integer.parseInt(args[4]);
				} catch(Exception e) {
				}
			}
		} else {
			if (args.length > 0) {
				try {
					hold = Integer.parseInt(args[0]);
				} catch(Exception e) {
				}
			}
		}
		queuePop = hold;	
		
		activePieceSet = new HashSet<GamePiece>();
		deletedPieceSet = new HashSet<GamePiece>();
		allBalls[0] = new Ball(Team.RED_TEAM, mode);
		allBalls[1] = new Ball(Team.GREEN_TEAM, mode);
		for (int i = 0; i < allBalls.length; i ++) {
			if (allBalls[i] != null) {
				addPiece(allBalls[i]);
			}
		}
		try {redPlayer.init(Team.RED_TEAM);} catch (Exception e) {/* no code */}
		try {greenPlayer.init(Team.GREEN_TEAM);} catch (Exception e) {/* no code */}
		profileMap = new HashMap<IPlayer, PlayerProfile>();
		profileMap.put(redPlayer, new PlayerProfile(Team.RED_TEAM, INITIAL_COLLISIONS));
		profileMap.put(greenPlayer, new PlayerProfile(Team.GREEN_TEAM, INITIAL_COLLISIONS));
		towers = new HashMap<TowerPiece, TowerInstance>();
	}
	
	private void addNewTower(IPlayer player, TowerRequest towerRequest) {
		try {		
			PlayerProfile profile = profileMap.get(player);
			TowerPosition position = towerRequest.position;
			if (position.isInBounds()) {
				GamePiece occupant = pieceAt(position, profile.team);
				if (occupant == null && !position.inEitherGoal()) {
					int collisions = Math.min(profile.collisions, position.collisions);
					if (collisions > 0) {
						TowerPiece towerPiece = new TowerPiece(profileMap.get(player).team, position.x, position.y, collisions);
						profile.collisions -= collisions;
						addPiece(towerPiece);
						TowerInstance towerInstance = new TowerInstance(player, towerRequest.tower, towerPiece);
						towers.put(towerPiece, towerInstance);
					} else if (mode.equals(Mode.DEBUG)) {
						System.out.println("No collisions remain available.");
					}
				}
			} 
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void addPiece(GamePiece piece) {
		activePieceSet.add(piece);
	}
	
	private Result checkForVictoryCondition() {
		// Check for victory condition.
		boolean redVictory = false;
		boolean greenVictory = false;
		for (int i = 0; i < allBalls.length; i ++) {
			if (allBalls[i] != null) {
				if (new Position(allBalls[i]).inOpposingGoal()) {
					if (allBalls[i].team == Team.GREEN_TEAM) {
						greenVictory = true;
					} else if (allBalls[i].team == Team.RED_TEAM){
						redVictory = true;
					}
				}
			}
		}
		if (redVictory && greenVictory) {
			return Result.TIE_GAME;
		} else if (redVictory) {
			if (mode.equals(Mode.DEBUG)) {
				System.out.println("Red wins from scoring.");
			}
			return Result.RED_VICTORY;
		} else if (greenVictory) {
			if (mode.equals(Mode.DEBUG)) {
				System.out.println("Green wins from scoring.");
			}
			return Result.GREEN_VICTORY;
		} else {
			return Result.PLAY_ON;
		}
	}
	
	private boolean detectBall(Team team, Ball ball) {	
		
		boolean detected = false;
		
		for (Entry<TowerPiece,TowerInstance> piece : towers.entrySet()) {
			int radius = piece.getValue().code.getDetectionRadius();
			radius = Math.max(radius, 0);
			radius = Math.min(radius, 5);
			if (piece.getKey().team == team && distanceSquared(piece.getKey(), ball) <= radius * radius) {
				detected = true;
				Position position = new Position(ball);
				TeamType teamType;
				if (team == ball.team) {
					teamType = TeamType.FRIEND;
				} else {
					teamType = TeamType.FOE;
					position.transpose();
				}
				piece.getValue().code.onDetectBall(teamType, position);
				piece.getKey().flash();
			}
		}
		return detected;
	}
	
	private void detectBalls(Team team) {
		boolean didDetect[] = new boolean[numBalls];
		for (int i = 0; i < allBalls.length; i ++) {
			if (allBalls[i] != null) {
				didDetect[i] = detectBall(team, allBalls[i]);
			}
		}
		for (int i = 0; i < allBalls.length; i ++) {
			if (allBalls[i] != null) {
				if (didDetect[i]) {
					reportDetection(team, allBalls[i]);
				}
			}
		}
	}
	
	private int distanceSquared(GamePiece p0, GamePiece p1) {
		Position position0 = new Position(p0);
		Position position1 = new Position(p1);
		if (p0.team != p1.team) position1.transpose();
		int dx = position0.x - position1.x;
		int dy = position0.y - position1.y;
		return dx * dx + dy * dy;
	}
	
	private void extinguishChosenTowers(IPlayer player) {
		HashSet<TowerInstance> remove = new HashSet<TowerInstance>();
		for (TowerInstance tower : towers.values()) {
			if (tower.player == player && tower.code.extinguish(turn / 2)) {
				remove.add(tower);
			}
		}
		for (TowerInstance tower : remove) {
			removeTowerInstance(tower);
		}
	}
	
	private void extinguishDeadTowers() {
		HashSet<TowerInstance> remove = new HashSet<TowerInstance>();
		for (TowerInstance tower : towers.values()) {
			if (tower.piece.getCollisions() == 0) {
				remove.add(tower);
			}
		}
		for (TowerInstance tower : remove) {
			removeTowerInstance(tower);
		}
	}

	protected Set<GamePiece> getActiveGamePieces() {
		return activePieceSet;
	}
	
	private HashSet<HashSet<GamePiece>> getCollisions() {
		HashMap<Integer, HashSet<GamePiece>> collisionMap = new HashMap<Integer, HashSet<GamePiece>>();
		for (GamePiece piece : activePieceSet) {
			Position position = new Position(piece);
			if (piece.team == Team.GREEN_TEAM) position.transpose();
			int index = position.x * Position.BOARD_SIZE_SQUARES + position.y;
			if (!collisionMap.containsKey(index)) collisionMap.put(index, new HashSet<GamePiece>());
			collisionMap.get(index).add(piece);
		}
		HashSet<HashSet<GamePiece>> collisions = new HashSet<HashSet<GamePiece>>();
		for (Integer key : collisionMap.keySet()) {
			if (collisionMap.get(key).size() > 1) {
				collisions.add(collisionMap.get(key));
			}
		}
		return collisions;
	}
	
	protected int[] getInfo(Team team) {
		PlayerProfile whoAmI;
		if (team == Team.RED_TEAM) {
			whoAmI = profileMap.get(redPlayer);
			int[] gameInfo = { whoAmI.collisions, whoAmI.queue.size() };
			return gameInfo;
		} else if (team == Team.GREEN_TEAM){
			whoAmI = profileMap.get(greenPlayer);
			int[] gameInfo = { whoAmI.collisions, whoAmI.queue.size() };
			return gameInfo;
		} else {
			int[] gameInfo = { 0, 0 };
			return gameInfo;
		}
	}
	
	private TeamType[][] getCopyOfBoard(Team team) {
		TeamType[][] board = new TeamType[Position.BOARD_SIZE_SQUARES][Position.BOARD_SIZE_SQUARES];
		for (TowerPiece towerPiece : towers.keySet()) {
			Position position = new Position(towerPiece);
			if (towerPiece.team != team) position.transpose();
			board[position.x][position.y] = (towerPiece.team == team) ? TeamType.FRIEND : TeamType.FOE;
		}
		return board;
	}
	
	protected Set<GamePiece> getDeletedGamePieces() {
		return deletedPieceSet;
	}
	
	protected List<GamePiece> getGamePiecesZOrdered() {
		ArrayList<GamePiece> list = new ArrayList<GamePiece>();
		for (GamePiece piece : activePieceSet) {
			if (!(piece instanceof Ball)) list.add(piece);
		}
		for (GamePiece piece : activePieceSet) {
			if (piece instanceof Ball) list.add(piece);
		}
		return list;
	}
	
	public class Timer implements Runnable {

		int time;
		
		public Timer() {
			time = 1000;
		}
		
		public Timer(int t) {
			time = t;
		}
		
		@Override
		public void run() {
			try {
				Thread.sleep(time);
			} catch (InterruptedException e) {
				// Move was successful
				return;
			}
			// Move took too long
			mainThread.stop();
			killedThread = true;
		}
	}
	
	protected Result move() {
		if(turn > 1000) {
			if (mode.equals(Mode.DEBUG)) {
				System.out.println("Reached max number of turns. Game tied.");
			}
			return Result.TIE_GAME;
		}
		
		// Runs moves on a thread
		// Only for server version
		if (mode == Mode.SERVER) {
			myTimer = new Thread(new Timer());
			mainThread = new Thread(new Move());
			// Starts timer
			myTimer.start();
			// Starts move
			mainThread.start();
			
			// If myTimer finishes first, kills mainThread and exits
			// If mainThread finishes first, interrupts myTimer and continues
			// Current thread then waits for timer to finish before continuing
			
			try {
				myTimer.join();
			} catch (InterruptedException e) {
				if (mode.equals(Mode.DEBUG)) {
					System.out.println("Main thread interrupted!");
				}
			}
			if (killedThread) {
				killedThread = false;
				if (turn % 2 == 1) {
					if (mode.equals(Mode.DEBUG)) {
						System.out.println("Green wins due to red running out of time.");
					}
					return Result.GREEN_VICTORY;
				} else {
					if (mode.equals(Mode.DEBUG)) {
						System.out.println("Red wins due to green running out of time.");
					}
					return Result.RED_VICTORY;
				}
			}
		} else {
			// Runs moves normally
			// Can get caught in infinite loops!
			new Move().run();
		}
		return checkForVictoryCondition();
	}
	
	protected class Move implements Runnable{
		@Override
		public void run() {
			
			// Clean up pieces eliminated in prior move
			deletedPieceSet.clear();
			// Find current player (players take turns moving)
			IPlayer player = ( turn++ % 2 == 0 ) ? redPlayer : greenPlayer;
			PlayerProfile profile = profileMap.get(player);
			// Move pinballs.
			movePinballs();
			resolveCollisions();
			// Extinguish towers that have 0 collisions remaining
			extinguishDeadTowers();
			// Check for towers that detect balls.
			detectBalls(profile.team);
			// Start turn.
			player.startTurn(new Turn((turn + 1) / 2, profile.team, profile.collisions, getCopyOfBoard(profile.team)), profile.queue);
			// Extinguish towers chosen by the player
			extinguishChosenTowers(player);
			// Add new towers (unless these collide).
			int numProcess = rand.nextInt(queuePop) + 1;
			for (int i = 0; i < numProcess; i ++) {
				processQueue(player, profile.queue);
			}
			// Check for victory condition.
			if (mode == Mode.SERVER) {
				myTimer.interrupt();
			}
		}
	}
	
	private void movePinballs() {
		Position redPosition = new Position(allBalls[0]);
		Position greenPosition = new Position(allBalls[1]);
		redPosition.advance(1);
		redPosition.transpose();
		if (redPosition.coordinatesMatch(greenPosition) && redPosition.heading == greenPosition.heading.reverse()) {
			allBalls[0].reverseDirection();
			allBalls[1].reverseDirection();
		}
		allBalls[0].move();
		allBalls[1].move();
	}
	
	private GamePiece pieceAt(Position position, Team perspective) {
		for (GamePiece piece : activePieceSet) {
			Position piecePosition = new Position(piece);
			if (piece.team != perspective) piecePosition.transpose();
			if (position.coordinatesMatch(piecePosition)) return piece;
		}
		return null;
	}

	private void processQueue(IPlayer player, TowerQueue queue) {
		// Add new towers (unless these collide).
		if (queue.size() > 0) {
			addNewTower(player, queue.queue.poll());
		}
	}
	
	private void removePiece(GamePiece piece) {
		deletedPieceSet.add(piece);
		activePieceSet.remove(piece);
	}
	
	private void removeTowerInstance(TowerInstance towerInstance) {
		profileMap.get(towerInstance.player).collisions += towerInstance.piece.getCollisions();
		towerInstance.player.onExpired(towerInstance.code);
		towers.remove(towerInstance.piece);
		removePiece(towerInstance.piece);
	}
	
	private void reportDetection(Team team, Ball ball) {	
		Position position = new Position(ball);
		TeamType teamType;
		if (team == ball.team) {
			teamType = TeamType.FRIEND;
		} else {
			teamType = TeamType.FOE;
			position.transpose();
		}
		IPlayer player = (team == Team.RED_TEAM) ? redPlayer : greenPlayer;
		PlayerProfile profile = profileMap.get(player);
		try {
			player.onDetectBall(new Turn(turn, profile.team, profile.collisions, getCopyOfBoard(profile.team)), profile.queue, teamType, position);
		} catch (Exception e) {
			// no code
		}
	}
	
	private void resolveCollisions() {
		for (HashSet<GamePiece> collision : getCollisions()) {
			TowerPiece towerPiece = null;
			ArrayList<Ball> balls = new ArrayList<Ball>();
			for (GamePiece piece : collision) {
				if (piece instanceof TowerPiece) {
					towerPiece = (TowerPiece) piece;
				} else {
					balls.add((Ball) piece);
				}
			}
			if (towerPiece == null) {
				// Collision between two balls (in the absence of a tower).
				Heading heading0 = balls.get(0).heading;
				Heading heading1 = balls.get(1).heading;
				balls.get(0).heading = heading1.reverse();
				balls.get(1).heading = heading0.reverse();
				balls.get(0).flash();
				balls.get(1).flash();
			} else {
				// Collision between one or more balls and a tower.
				HashSet<TowerInstance> active = new HashSet<TowerInstance>();
				for (Ball ball : balls) {		
					TowerInstance instance = towers.get(towerPiece);
					Heading heading = null;
					TeamType teamType = (ball.team == instance.piece.team) ? TeamType.FRIEND : TeamType.FOE;
					try {	
						Heading relativeHeading = (teamType == TeamType.FRIEND) ? ball.heading : ball.heading.reverse();
						heading = instance.code.onCaptureBall(teamType, relativeHeading);	
					} catch (Exception e) {
						heading = null;
					}
					ball.flash();
					// If client code returns null or Heading.NONE, continue in original direction.
					if (heading != null && heading != Heading.NONE) {
						switch (teamType) {
						case FRIEND:
							ball.setHeading(heading);
							break;
						case FOE:
							ball.setHeading(heading.reverse());
							break;
						}		
					}
					active.add(instance);
				}
				// Account for collisions after prior loop, in case both balls strike the
				// same tower in the same turn (which counts as only one collision).
				for (TowerInstance instance : active) {
					instance.piece.collisions--;
					profileMap.get(instance.player).collisions++;
					if (instance.piece.collisions == 0) {
						removeTowerInstance(instance);
					}
				}
			}
		}
	}
	
	protected void setRenderingCoordinates() {
		for (GamePiece piece : getActiveGamePieces()) {
			piece.setRenderingCoordinates();
		}
	}

	protected int turn() {
		return turn;
	}
}


