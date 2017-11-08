package org.graphstream.util;

import org.graphstream.graph.Graph;
import org.graphstream.ui.view.Viewer;

public class Launcher
{
	public static Viewer display(Graph graph, boolean autoLayout) {
		String launcherClassName = System.getProperty("UI");
				
		if (launcherClassName == null) {
			throw new RuntimeException("No UI package detected ! Please use System.setProperty() for the selected package.");
		}
		else {
			try {
				Class<?> c = Class.forName(launcherClassName);
				Object object = c.newInstance();
				
				if (object instanceof Display) {
					return ((Display)object).display(graph, autoLayout);
				} else {
					throw new RuntimeException("Cannot launch viewer ! Please verify the name in System.setProperty()");
				}
			}
			catch (Exception e) { 
				throw new RuntimeException("Cannot launch viewer ! Please verify your package.");
			}
		}
	}
	
	public static Viewer display(Graph graph) {
		return Launcher.display(graph, true);
	}
}
