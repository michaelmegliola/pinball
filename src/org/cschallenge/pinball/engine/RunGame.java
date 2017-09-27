package org.cschallenge.pinball.engine;

import org.cschallenge.pinball.engine.PinballEngine.Result;

class RunGame implements Runnable {
	
	String[] myArgs;
	int n;
	
	public RunGame(String[] args, int m) {
		myArgs = args;
		n = m;
	}
	
	@Override
	public void run() {
		for (int i = 0; i < n; i++) {
			final PinballEngine myEngine = new PinballEngine(myArgs);
			boolean gameOver = false;
			Result result = Result.PLAY_ON;
			while (!gameOver) {
				result = myEngine.move();
				gameOver |= ( result != Result.PLAY_ON );
				if (gameOver) {
					PinballFrame.recordResult(result);
				}
			}
		}
	}
}