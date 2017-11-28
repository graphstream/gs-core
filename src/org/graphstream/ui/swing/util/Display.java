package org.graphstream.ui.swing.util;

import org.graphstream.graph.Graph;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.layout.Layouts;
import org.graphstream.ui.swing.SwingFullGraphRenderer;
import org.graphstream.ui.swingViewer.SwingViewer;
import org.graphstream.ui.view.GraphRenderer;
import org.graphstream.ui.view.Viewer;

public class Display implements org.graphstream.util.Display {

	@Override
	public Viewer display(Graph graph, boolean autoLayout) {
		SwingViewer viewer = new SwingViewer(graph,
				SwingViewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
		GraphRenderer renderer = new SwingFullGraphRenderer();
		viewer.addView(SwingViewer.DEFAULT_VIEW_ID, renderer);
		if (autoLayout) {
			Layout layout = Layouts.newLayoutAlgorithm();
			viewer.enableAutoLayout(layout);
		}
		return viewer;
	}

}
