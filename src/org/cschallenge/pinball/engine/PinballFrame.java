package org.cschallenge.pinball.engine;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.cschallenge.pinball.engine.PinballEngine.Mode;
import org.cschallenge.pinball.engine.PinballEngine.Result;
import org.cschallenge.pinball.engine.PinballEngine.Team;
import org.jdesktop.core.animation.rendering.JRenderer;
import org.jdesktop.core.animation.rendering.JRendererTarget;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.core.animation.timing.TimingSource.TickListener;
import org.jdesktop.swing.animation.rendering.JRendererFactory;
import org.jdesktop.swing.animation.rendering.JRendererPanel;
import org.jdesktop.swing.animation.timing.sources.SwingTimerTimingSource;

public class PinballFrame implements JRendererTarget<GraphicsConfiguration, Graphics2D> {
	
	public static final int ANIMATION_SPEED = 4;
	public static final int TICKS_PER_TURN = GamePiece.SIZE_PX / ANIMATION_SPEED;
	
	private static final String RESOURCE_PATH = "org/cschallenge/pinball/resources/";
	private static final int TIMER_FREQUENCY_MILLIS = 40;
	
	private static PinballFrame theApp;
	private int ticks = 0;
	private boolean gameOver = false;
	private int ticksRemaining = TICKS_PER_TURN;
	private boolean paused = false;
	private boolean stepping = false;
	private Result result = Result.PLAY_ON;
	
	// UI widgets
	private JFrame frame;
	private JRendererPanel panel;
	private JRenderer renderer;
	private final PinballEngine engine;
	private JLabel collisionsGreen;
	private JLabel queueSizeGreen;
	private JLabel collisionsRed;
	private JLabel queueSizeRed;
	private JMenuItem menuItem;
	
	// Cumulative results
	private static int[] results = new int[Result.values().length];
	
	private static Object lock = new Object();
	
	/**
	 * Used to update the FPS display once a second.
	 */
	static final TimingSource gameTimer = new SwingTimerTimingSource(TIMER_FREQUENCY_MILLIS, MILLISECONDS);

	public static URL getResource(String name) {
		final URL url = Thread.currentThread().getContextClassLoader().getResource(RESOURCE_PATH + name);
		if (url == null) {
			throw new IllegalStateException("Unable to load: " + name);
		} else {
			return url;
		}
	}

	protected static void recordResult(Result result) {
		synchronized(lock) {
			results[result.ordinal()]++;
		}
	}
	
	protected static void clearResults() {
		synchronized(lock) {		
			for (int i = 0; i < results.length; i++)  {
				results[i]= 0;
			}
		}
	}
	
	public static void main(final String[] args) {
		//System.setProperty("swing.defaultlaf", "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");

		clearResults();
				
		// Does not display frame and runs a crunch to find the outcome of n games
		boolean genFrame = true;
		
		if (genFrame) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					theApp = new PinballFrame(args);
					theApp.start();
				}
			});
		} else {
			int n = 10; // Number of threads
			int m = 10; // Number of times thread spawns engine
			
			long time = System.currentTimeMillis();
			
			Thread[] myThreads = new Thread[n];
			for (int i = 0; i < n; i++) {
				(myThreads[i] = new Thread(new RunGame(args, m))).start();
			}
			for (int i = 0; i < n; i++) {
				try {
					myThreads[i].join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.out.printf("%d ties\n%d red wins\n%d green wins\n", results[Result.TIE_GAME.ordinal()], results[Result.RED_VICTORY.ordinal()], results[Result.GREEN_VICTORY.ordinal()]);
			System.out.printf("Execution took %d seconds\n", (System.currentTimeMillis() - time ) / 1000);
		}
		
	}
	
	public PinballFrame(String[] args) {
		engine = new PinballEngine(args);
		createFrame();
	}
	
	public PinballFrame(Class playerOne, Class playerTwo, Mode mode) {
		engine = new PinballEngine(playerOne, playerTwo, mode);
		createFrame();
	}
	
	private void createFrame(){
		/*
		A Frame is a top-level window with a title and a border. The size of the frame includes any area 
		designated for the border. The dimensions of the border area may be obtained using the getInsets 
		method. Since the border area is included in the overall size of the frame, the border effectively 
		obscures a portion of the frame, constraining the area available for rendering  and/or displaying 
		subcomponents to the rectangle which has an upper-left corner location of (insets.left, insets.top), 
		and has a size of width - (insets.left + insets.right) by height - (insets.top + insets.bottom).
		 */
		frame = new JFrame("Pinball game");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				super.windowClosed(e);
				gameTimer.dispose();
				renderer.getTimingSource().dispose();
				renderer.shutdown();
			}
		});
		frame.setLayout(new BorderLayout());
		
		panel = new JRendererPanel();
		JPanel bottomPanel = new JPanel();
		JPanel topPanel = new JPanel();
		
		frame.add(panel, BorderLayout.CENTER);
		frame.add(topPanel, BorderLayout.NORTH);
		frame.add(bottomPanel, BorderLayout.SOUTH);
		
		panel.setBackground(Color.white);
		renderer = JRendererFactory.getDefaultRenderer(panel, this, false);
		
		JMenuBar menuBar;
		JMenu menu;
		
		menuBar = new JMenuBar();
		menu = new JMenu("Control");
		menu.setMnemonic(KeyEvent.VK_A);
		menu.getAccessibleContext().setAccessibleDescription("Command for controling flow of program");
		menuBar.add(menu);
		
		/*
		 * Handler for CTRL+P
		 */
		menuItem = new JMenuItem("Pause/Play");
		menuItem.setMnemonic(KeyEvent.VK_B);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
		menu.add(menuItem);
		
		// CTRL+P resumes normal play
		menuItem.addActionListener(new ActionListener() {
			@Override
		    public void actionPerformed(ActionEvent arg0) {
				if (paused) {
					paused = false;
					stepping = false;
				} else {
					paused = true;
					stepping = false;
				}
			}
		});
		
		/*
		 * Handler for CTRL+S
		 */
		JMenuItem stepper = new JMenuItem("Step");
		stepper.setMnemonic(KeyEvent.VK_C);
		stepper.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		menu.add(stepper);
		
		// CTRL+S invokes a single step
		stepper.addActionListener(new ActionListener() {
			@Override
		    public void actionPerformed(ActionEvent arg0) {
				if (!paused) {
					paused = true;
					stepping = false;
				} else {
					stepping = true;
				}
			}
		});
		
		frame.setJMenuBar(menuBar);
		
		
		topPanel.setLayout(new BorderLayout());
		bottomPanel.setLayout(new BorderLayout());
		
		JPanel holdLeftTop = new JPanel();
		JPanel holdCenterTop = new JPanel();
		JPanel holdRightTop = new JPanel();
		
		JPanel holdLeftBottom = new JPanel();
		JPanel holdRightBottom = new JPanel();
		
		
		topPanel.add(holdLeftTop, BorderLayout.WEST);
		topPanel.add(holdCenterTop, BorderLayout.CENTER);
		topPanel.add(holdRightTop, BorderLayout.EAST);
		
		bottomPanel.add(holdLeftBottom, BorderLayout.WEST);
		bottomPanel.add(holdRightBottom, BorderLayout.EAST);
		
		JLabel resourcesGreen = new JLabel("Collisions remaining: ");
		collisionsGreen = new JLabel("0");
		
		JLabel resourcesRed = new JLabel("Collisions remaining: ");
		collisionsRed = new JLabel("0");
		
		JLabel queueGreen = new JLabel("Queue size: ");
		queueSizeGreen = new JLabel("0");
		
		JLabel queueRed = new JLabel("Queue size: ");
		queueSizeRed = new JLabel("0");
		
		JLabel instructions = new JLabel("CTRL+P / CTRL+S");
		
		holdLeftTop.add(resourcesGreen);
		holdLeftTop.add(collisionsGreen);
		holdRightTop.add(queueGreen);
		holdRightTop.add(queueSizeGreen);
		holdLeftBottom.add(resourcesRed);
		holdLeftBottom.add(collisionsRed);
		holdRightBottom.add(queueRed);
		holdRightBottom.add(queueSizeRed);
		holdCenterTop.add(instructions);
		
		topPanel.setPreferredSize(new Dimension(Position.BOARD_SIZE_PX, 25));
		bottomPanel.setPreferredSize(new Dimension(Position.BOARD_SIZE_PX, 25));
		panel.setPreferredSize(new Dimension(Position.BOARD_SIZE_PX, Position.BOARD_SIZE_PX));
		frame.pack();
		frame.setResizable(false);
		frame.setVisible(true);
		
		gameTimer.addTickListener(new TickListener() {
			@Override
			public void timingSourceTick(TimingSource source, long nanoTime) {
				if (ticks++ % TICKS_PER_TURN == 0 && !gameOver) {
			
					if (!paused || stepping) {
						result = engine.move();
						stepping = false;
					}
						
					int[] gameInfo = engine.getInfo(Team.GREEN_TEAM);
					collisionsGreen.setText(Integer.toString(gameInfo[0]));
					queueSizeGreen.setText(Integer.toString(gameInfo[1]));
					
					gameInfo = engine.getInfo(Team.RED_TEAM);
					collisionsRed.setText(Integer.toString(gameInfo[0]));
					queueSizeRed.setText(Integer.toString(gameInfo[1]));
					
					gameOver |= ( result != Result.PLAY_ON );
					
				}
				engine.setRenderingCoordinates();
				if (gameOver) {
					// Finish animation of current turn before stopping animation.
					if (PinballFrame.this.ticksRemaining-- == 0) {
						if (result.equals(Result.GREEN_VICTORY)) {
							JOptionPane.showMessageDialog(null, "Blue wins!");
						} else if (result.equals(Result.RED_VICTORY)) {
							JOptionPane.showMessageDialog(null, "Yellow wins!");
						} else if (result.equals(Result.TIE_GAME)) {
							JOptionPane.showMessageDialog(null, "Tied game.");
						}
						gameTimer.dispose();
					}
				}
			}
		});
	}

	final List<GamePiece> pieces = new ArrayList<GamePiece>();
	
	@Override
	public void renderSetup(GraphicsConfiguration gc) {
		// no code
	}

	@Override
	public void renderUpdate() {
		// no code
	}
	
	public void start() {
		gameTimer.init();
	}

	@Override
	public void render(Graphics2D g2d, int width, int height) {
		g2d.setBackground(Color.white);
		g2d.clearRect(0, 0, width, height);
		
		g2d.setColor(Color.white);
		if (engine.mode == Mode.DEBUG) {
			g2d.setColor(Color.LIGHT_GRAY);
		}
		
		// By always placing dashed lines (must be dashed) even if white,
		// fixes rendering issue found on Linux systems
		Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 12 }, 6);
		g2d.setStroke(dashed);
		for (int i = 1; i < Position.BOARD_SIZE_SQUARES; i++) {
			g2d.drawLine(i * GamePiece.SIZE_PX, 0, i * GamePiece.SIZE_PX, Position.BOARD_SIZE_PX);
			g2d.drawLine(0, i * GamePiece.SIZE_PX, Position.BOARD_SIZE_PX, i * GamePiece.SIZE_PX);
		}
		
		// Set stroke back to default
		Stroke def = new BasicStroke();
		g2d.setStroke(def);
		
		g2d.setColor(Color.yellow);
		g2d.drawRect(0, Position.BOARD_SIZE_PX - GamePiece.SIZE_PX * 3 - 1, GamePiece.SIZE_PX * 3, GamePiece.SIZE_PX * 3);
		g2d.setColor(Color.blue);
		g2d.drawRect(Position.BOARD_SIZE_PX - GamePiece.SIZE_PX * 3 - 1, 0, GamePiece.SIZE_PX * 3, GamePiece.SIZE_PX * 3);
		for (GamePiece piece : engine.getDeletedGamePieces()) {
			g2d.drawImage(piece.getImage(ticks), piece.px, piece.py, null);
		}
		int tempX = -1;
		int tempY = -1;
		for (GamePiece piece : engine.getGamePiecesZOrdered()) {
			g2d.drawImage(piece.getImage(ticks), piece.px, piece.py, null);
			// For testing if two balls occupy the same space
			if (piece.getClass() == Ball.class && tempX == piece.px && tempY == piece.py) {
				BufferedImage blackBall = null;
				try {
					blackBall = ImageIO.read(PinballFrame.getResource("black-ball.png"));
				} catch (IOException e) {
					e.printStackTrace();
				}
				g2d.drawImage(blackBall, piece.px, piece.py, null);
			}
			if (piece.getClass() == Ball.class) {
				tempX = piece.px;
				tempY = piece.py;
			}
		}
	}

	public int getWidth() {
		return frame.getWidth() - (frame.getInsets().right + frame.getInsets().left);
	}
	
	public int getHeight() {
		return frame.getHeight() - (frame.getInsets().top + frame.getInsets().bottom);
	}

	@Override
	public void renderShutdown() {
		// no code
	}
}

