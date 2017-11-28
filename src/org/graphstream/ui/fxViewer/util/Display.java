package org.graphstream.ui.fxViewer.util;

import org.graphstream.graph.Graph;
import org.graphstream.ui.fxViewer.FxViewer;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.layout.Layouts;
import org.graphstream.ui.view.GraphRenderer;
import org.graphstream.ui.view.Viewer;

import javafx.application.Application;

public class Display implements org.graphstream.util.Display {

	@Override
	public Viewer display(Graph graph, boolean autoLayout) {
		FxViewer viewer = new FxViewer(graph,
				FxViewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
		
		GraphRenderer renderer = FxViewer.newGraphRenderer();
		viewer.addView(FxViewer.DEFAULT_VIEW_ID, renderer);
		
		if(autoLayout) {
			Layout layout = Layouts.newLayoutAlgorithm() ;
			viewer.enableAutoLayout(layout);
		}
		
		DefaultApplication.view = viewer;
	    new Thread(() -> Application.launch(DefaultApplication.class)).start();
		
		return viewer;
	}
	
}
