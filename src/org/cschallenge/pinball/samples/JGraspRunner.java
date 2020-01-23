package org.cschallenge.pinball.samples;

import org.cschallenge.pinball.engine.GameLauncher;
import org.cschallenge.pinball.engine.IPlayer;
import javax.swing.SwingUtilities;
import org.cschallenge.pinball.engine.PinballFrame;
import org.cschallenge.pinball.engine.PinballEngine.Mode;

/*
   to run the game under JGrasp, complete steps 1 & 2, below.
*/

// step 1. make sure to import the classes representing players
import org.cschallenge.pinball.samples.LeoPlayer.Leo;
import org.cschallenge.pinball.samples.teamM.SamplePlayerM;

public class JGraspRunner {

	public static final void main(String... args) throws ClassNotFoundException {
			
      // step 2. set these variables to the class of each player
		Class player0 = Leo.class;
		Class player1 = SamplePlayerM.class;
			
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				PinballFrame theApp = new PinballFrame(player0, player1, Mode.CLIENT);
				theApp.start();
			}
		});
	} 
}