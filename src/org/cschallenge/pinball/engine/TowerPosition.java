package org.cschallenge.pinball.engine;

public class TowerPosition extends Position {
 
	final public int collisions;
	
	public TowerPosition(int x, int y, int collisions) {
		super(x, y);
		this.collisions = collisions;
	}
	
	public TowerPosition(int x, int y) {
		super(x, y);
		this.collisions = 1;
	}
	
	public TowerPosition(Position position, int collisions) {
		super(position.getX(), position.getY());
		this.collisions = collisions;
	}
	
	public TowerPosition(Position position) {
		super(position.getX(), position.getY());
		this.collisions = 1;
	}
}
