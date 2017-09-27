package org.cschallenge.pinball.loader;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.cschallenge.pinball.engine.PinballEngine.Mode;
import org.cschallenge.pinball.engine.PinballFrame;

public class PlayPinball {
	
	static JFrame myFrame;
	
	// Alternative entry point for starting pinball engine
	// Allows for user to choose players
	public static final void main(final String... args) throws ClassNotFoundException, IOException {
		
		// Runs through packages and finds classes that implement IPlayer
		PlayPinball p = new PlayPinball();
		Class[] c = p.getClasses("");
		final ArrayList<Class> players = new ArrayList<Class>();
		Pattern interfacePattern = Pattern.compile("org\\.cschallenge\\.pinball\\.engine\\.IPlayer");
		for (int i = 0; i < c.length; i++) {
			Class<?>[] myInts = c[i].getInterfaces();
			for (int j = 0; j < myInts.length; j++) {
				Matcher interfaceMatch = interfacePattern.matcher(myInts[j].getName());
				if (interfaceMatch.matches()) {
					players.add(c[i]);
				}
			}
		}
		
		// Has radio buttons for choosing players
		myFrame = new JFrame();
		myFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		JPanel myPanel = new JPanel();
		
		myPanel.setLayout(new GridLayout(0, 2));
		
		final JRadioButton[] redBoxes = new JRadioButton[players.size()];
		final JRadioButton[] greenBoxes = new JRadioButton[players.size()];
		
		ButtonGroup redGroup = new ButtonGroup();
		ButtonGroup greenGroup = new ButtonGroup();
		
		JLabel redLabel = new JLabel("Red player", SwingConstants.CENTER);
		JLabel greenLabel = new JLabel("Green player", SwingConstants.CENTER);
		
		myPanel.add(redLabel);
		myPanel.add(greenLabel);
		
		// Places radio buttons on panel
		for (int i = 0; i < players.size(); i++) {
			redBoxes[i] = new JRadioButton(players.get(i).getSimpleName());
			myPanel.add(redBoxes[i]);
			redGroup.add(redBoxes[i]);
			greenBoxes[i] = new JRadioButton(players.get(i).getSimpleName());
			myPanel.add(greenBoxes[i]);
			greenGroup.add(greenBoxes[i]);
		}
		
		final JCheckBox debug = new JCheckBox("Debug");
		myPanel.add(debug);
		
		// Creates game and disposes of selection screen
		JButton submit = new JButton("Play");
		submit.addActionListener(new ActionListener() {
			@Override
		    public void actionPerformed(ActionEvent arg0) {
				
				final Class[] classes = new Class[2];
				int selected = 0;
				boolean canRun = true;
				for (int i = 0; i < redBoxes.length; i++) {
					if (redBoxes[i].isSelected()) {
						selected++;
						classes[0] = players.get(i);
					}
				}
				for (int i = 0; i < greenBoxes.length; i++) {
					if (greenBoxes[i].isSelected()) {
						selected++;
						classes[1] = players.get(i);
					}
				}
				if (selected < 2) {
					JOptionPane.showMessageDialog(null, "Not enough players selected!");
					canRun = false;
				}
				final Mode mode;
				if (debug.isSelected()) {
					mode = Mode.DEBUG;
				} else {
					mode = Mode.CLIENT;
				}
				
				if (canRun) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							PinballFrame theApp = new PinballFrame(classes[0], classes[1], mode);
							theApp.start();
						}
					});
				}
				myFrame.dispose();
			}
		});
		myPanel.add(submit);
		myFrame.add(myPanel);
		myFrame.pack();
		myFrame.setVisible(true);
	}
	
	// Code below taken from and modified https://dzone.com/articles/get-all-classes-within-package
	
	/**
	 * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
	 *
	 * @param packageName The base package
	 * @return The classes
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private static Class[] getClasses(String packageName) throws ClassNotFoundException, IOException {
	    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	    assert classLoader != null;
	    String path = packageName.replace('.', '/');
	    Enumeration<URL> resources = classLoader.getResources(path);
	    List<File> dirs = new ArrayList<File>();
	    while (resources.hasMoreElements()) {
	        URL resource = resources.nextElement();
	        dirs.add(new File(resource.getFile()));
	    }
	    ArrayList<Class> classes = new ArrayList<Class>();
	    for (File directory : dirs) {
	        classes.addAll(findClasses(directory, packageName));
	    }
	    return classes.toArray(new Class[classes.size()]);
	}

	/**
	 * Recursive method used to find all classes in a given directory and subdirs.
	 *
	 * @param directory   The base directory
	 * @param packageName The package name for classes found inside the base directory
	 * @return The classes
	 * @throws ClassNotFoundException
	 */
	private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
	    List<Class> classes = new ArrayList<Class>();
	    if (!directory.exists()) {
	        return classes;
	    }
	    File[] files = directory.listFiles();
	    for (File file : files) {
	        if (file.isDirectory()) {
	            assert !file.getName().contains(".");
	            if (packageName == "") {
	            	classes.addAll(findClasses(file, file.getName()));
	            } else {
	            	classes.addAll(findClasses(file, packageName + "." + file.getName()));
	            }
	        } else if (file.getName().endsWith(".class")) {
	            classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
	        }
	    }
	    return classes;
	}
}
