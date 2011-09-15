package org.graphstream.ui.viewer.test;

import java.io.IOException;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.stream.file.FileSourceDGS;
import org.graphstream.stream.thread.*;
import org.graphstream.ui.swingViewer.*;

public class TestTwoGraphsInOneViewer {
	public static final String GRAPH = "data/dorogovtsev_mendes6000.dgs";

	public static void main(String args[]) {
		new TestTwoGraphsInOneViewer();
	}

	public TestTwoGraphsInOneViewer() {
		Graph graph1 = new MultiGraph("g1");
		Graph graph2 = new MultiGraph("g2");
		Viewer viewer1 = new Viewer(new ThreadProxyPipe(graph1));
		Viewer viewer2 = new Viewer(new ThreadProxyPipe(graph2));

		graph1.addAttribute("ui.stylesheet", styleSheet1);
		graph2.addAttribute("ui.stylesheet", styleSheet2);
		//View view1 =
				viewer1.addDefaultView(true);
		viewer2.addDefaultView(true);
		viewer1.enableAutoLayout();
		viewer2.enableAutoLayout();

		//view1.setBackLayerRenderer(view2);

		FileSourceDGS dgs = new FileSourceDGS();

		dgs.addSink(graph1);
		try {
			dgs.begin(getClass().getResourceAsStream(GRAPH));
			for (int i = 0; i < 100 && dgs.nextEvents(); i++)
				;
			dgs.end();
		} catch (IOException e1) {
			e1.printStackTrace();
			System.exit(1);
		}
		dgs.removeSink(graph1);

		dgs.addSink(graph2);
		try {
			dgs.begin(getClass().getResourceAsStream(GRAPH));
			for (int i = 0; i < 100 && dgs.nextEvents(); i++)
				;
			dgs.end();
		} catch (IOException e1) {
			e1.printStackTrace();
			System.exit(1);
		}
		dgs.removeSink(graph2);
	}
	
	protected String styleSheet1 =
		"graph { padding: 40px; }" +
		"node { fill-color: red; }";
	
	protected String styleSheet2 =
		"graph { padding: 40px; }" +
		"node { fill-color: blue; }";
}
