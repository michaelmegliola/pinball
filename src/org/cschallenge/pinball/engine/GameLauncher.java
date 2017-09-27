package org.cschallenge.pinball.engine;

import javax.swing.SwingUtilities;

import org.cschallenge.pinball.engine.PinballEngine.Mode;
import org.cschallenge.pinball.samples.LeoPlayer.Leo;
import org.cschallenge.pinball.samples.teamK.SamplePlayerK;

/**
 * Provides an external entry point to allow a launch
 * from any development environment.
 * 
 * @author berwick
 *
 */
public class GameLauncher {

	public static final void main(String... args) throws ClassNotFoundException {
		
		final Class player0;
		final Class player1;
		
		if (args.length != 2) {
			System.out.println("Requires two arguments: [java class of player 1], [java class of player 2]");
			System.out.println("Using default players for now...");
			player0 = Leo.class;
			player1 = SamplePlayerK.class;	
		} else {
			player0 = GameLauncher.class.getClassLoader().loadClass(args[0]);
			player1 = GameLauncher.class.getClassLoader().loadClass(args[1]);		
		}
			
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				PinballFrame theApp = new PinballFrame(player0, player1, Mode.CLIENT);
				theApp.start();
			}
		});
	}
	
}
