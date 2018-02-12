package org.graphstream.util;

import org.graphstream.graph.Graph;
import org.graphstream.ui.view.Viewer;

public interface Display {
	public Viewer display(Graph graph, boolean autoLayout);
}
