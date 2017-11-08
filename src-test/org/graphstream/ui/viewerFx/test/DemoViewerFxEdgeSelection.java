/*
 * Copyright 2006 - 2016
 *     Stefan Balev     <stefan.balev@graphstream-project.org>
 *     Julien Baudry    <julien.baudry@graphstream-project.org>
 *     Antoine Dutot    <antoine.dutot@graphstream-project.org>
 *     Yoann Pigné      <yoann.pigne@graphstream-project.org>
 *     Guilhelm Savin   <guilhelm.savin@graphstream-project.org>
 * 
 * This file is part of GraphStream <http://graphstream-project.org>.
 * 
 * GraphStream is a library whose purpose is to handle static or dynamic
 * graph, create them from scratch, file or any source and display them.
 * 
 * This program is free software distributed under the terms of two licenses, the
 * CeCILL-C license that fits European law, and the GNU Lesser General Public
 * License. You can  use, modify and/ or redistribute the software under the terms
 * of the CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
 * URL <http://www.cecill.info> or under the terms of the GNU LGPL as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C and LGPL licenses and that you accept their terms.
 */
package org.graphstream.ui.viewerFx.test;

import java.util.EnumSet;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.fxViewer.FxViewPanel;
import org.graphstream.ui.fxViewer.FxViewer;
import org.graphstream.ui.fxViewer.util.FxMouseManager;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;
import org.graphstream.ui.view.util.InteractiveElement;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
/**
 * Test the viewer.
 */
public class DemoViewerFxEdgeSelection extends Application implements ViewerListener {
	protected static boolean loop = true;
	public static FxViewPanel viewPanel;
	
	public static void main(String args[]) {
		
		Graph graph = new MultiGraph("main graph");
		viewPanel = (FxViewPanel) graph.displayFx(true).getDefaultView();
		FxViewer view = (FxViewer) viewPanel.getViewer();
		
		view.getDefaultView().setMouseManager(new FxMouseManager(EnumSet.of(InteractiveElement.EDGE, InteractiveElement.NODE, InteractiveElement.SPRITE)));
		ViewerPipe pipe = view.newViewerPipe();

		// graph.setAttribute( "ui.quality" );
		graph.setAttribute("ui.antialias");

		pipe.addViewerListener(new DemoViewerFxEdgeSelection());

		for (String nodeId : new String[]{"A", "B", "C"}) {
			Node node = graph.addNode(nodeId);
			node.setAttribute("ui.label", nodeId);

		}

		Edge ab = graph.addEdge("AB", "A", "B", true);
		graph.addEdge("BC", "B", "C", true);
		graph.addEdge("CA", "C", "A", true);

		graph.setAttribute("ui.stylesheet", styleSheet);

		float color = 0;
		float dir = 0.01f;
		
		Application.launch(DemoViewerFxEdgeSelection.class, args);
		
		while (loop) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			pipe.pump();

			color += dir;

			if (color > 1) {
				color = 1;
				dir = -dir;
			} else if (color < 0) {
				color = 0;
				dir = -dir;
			}

			showSelection(graph);
		}
	}


	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
		
		primaryStage.setScene(new Scene(viewPanel));
		primaryStage.show();
	}

	protected static void showSelection(Graph graph) {
		boolean selection = false;
		StringBuilder sb = new StringBuilder();

		sb.append("[");

		for (Node node : graph) {
			if (node.hasAttribute("ui.selected")) {
				sb.append(String.format(" %s", node.getId()));
				selection = true;
			}
			if (node.hasAttribute("ui.clicked")) {
				System.err.printf("node %s clicked%n", node.getId());
			}
		}

		sb.append(" ]");

		if (selection)
			System.err.printf("selection = %s%n", sb.toString());
	}

	protected static String styleSheet = "graph         { padding: 20px; stroke-width: 0px; }"
			+ "node:selected { fill-color: red;  fill-mode: plain; }"
			+ "node:clicked  { fill-color: blue; fill-mode: plain; }"
			+ "edge:selected { fill-color: purple; fill-mode: plain; }"
			+ "edge:clicked  { fill-color: orange; fill-mode: plain; }"
			+ "node#A        { fill-color: green, yellow, purple; fill-mode: dyn-plain; }";

	public void buttonPushed(String id) {
	}

	public void buttonReleased(String id) {
	}

	public void mouseOver(String id) {
	}

	public void mouseLeft(String id) {
	}

	public void viewClosed(String viewName) {
		loop = false;
	}
}