package org.jarclassloader;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class LoadClass {
	private Object playerObject;
	
	public LoadClass(String playerClassName, String teamName) {
		URL[] classLoaderUrls = new URL[1];
		try {
			classLoaderUrls[0] = new URL("http://cschallenges.org/players/" + teamName + "/" + teamName + ".jar");
		} catch( MalformedURLException e) {
			e.printStackTrace();
		}
		URLClassLoader urlClassLoader = new URLClassLoader(classLoaderUrls, Thread.currentThread().getContextClassLoader());
		
		Class<?> playerClass = null;
		try {
			playerClass = urlClassLoader.loadClass( playerClassName );
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Constructor<?> constructor = null;
		try {
			constructor = playerClass.getConstructor();
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		playerObject = null;
		try {
			playerObject = constructor.newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public Object getPlayer() {
		return playerObject;
	}
}