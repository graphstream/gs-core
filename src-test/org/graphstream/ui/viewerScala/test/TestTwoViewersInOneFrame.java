package org.graphstream.ui.viewerScala.test;

import java.awt.GridLayout;

import javax.swing.JFrame;

import org.graphstream.algorithm.generator.DorogovtsevMendesGenerator;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.stream.thread.ThreadProxyPipe;
import org.graphstream.ui.scalaViewer.ScalaGraphRenderer;
import org.graphstream.ui.swingViewer.DefaultView;
import org.graphstream.ui.swingViewer.SwingViewer;
import org.graphstream.ui.view.Viewer;

@SuppressWarnings("serial")
public class TestTwoViewersInOneFrame extends JFrame {
	public static void main(String[] args) {
		(new TestTwoViewersInOneFrame()).run();
	}

	private void run() {
		MultiGraph graph1 = new MultiGraph("g1");
		MultiGraph graph2 = new MultiGraph("g2");
		Viewer viewer1 = new SwingViewer(new ThreadProxyPipe(graph1));
		Viewer viewer2 = new SwingViewer(new ThreadProxyPipe(graph2));

	    graph1.setAttribute("ui.quality");
	    graph2.setAttribute("ui.quality");
	    graph1.setAttribute("ui.antialias");
	    graph2.setAttribute("ui.antialias");
		graph1.setAttribute("ui.stylesheet", styleSheet1);
		graph2.setAttribute("ui.stylesheet", styleSheet2);

		DefaultView view1 = new DefaultView(viewer1, "view1", new ScalaGraphRenderer());
		DefaultView view2 = new DefaultView(viewer2, "view2", new ScalaGraphRenderer());
		viewer1.addView(view1);
		viewer2.addView(view2);
		viewer1.enableAutoLayout();
		viewer2.enableAutoLayout();

		DorogovtsevMendesGenerator gen = new DorogovtsevMendesGenerator();

		gen.addSink(graph1);
		gen.addSink(graph2);
		gen.begin();
		for(int i = 0 ; i < 100; i++)
			gen.nextEvents();
		gen.end();

		gen.removeSink(graph1);
		gen.removeSink(graph2);

		setLayout(new GridLayout(1, 2));
		//add(new JButton("Button"))
		add(view1);
		add(view2);
		setSize(800, 600);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	protected String styleSheet1 =
			"graph { padding: 40px; }" +
			"node { fill-color: red; stroke-mode: plain; stroke-color: black; }";
	
	protected String styleSheet2 =
		"graph { padding: 40px; }" +
		"node { fill-color: blue; stroke-mode: plain; stroke-color: black; }";
}
