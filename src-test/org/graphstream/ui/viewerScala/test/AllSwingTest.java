package org.graphstream.ui.viewerScala.test;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import org.graphstream.algorithm.generator.DorogovtsevMendesGenerator;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.scalaViewer.ScalaGraphRenderer;
import org.graphstream.ui.swingViewer.DefaultView;
import org.graphstream.ui.swingViewer.SwingViewer;

@SuppressWarnings("serial")
public class AllSwingTest extends JFrame {
	public static void main(String[] args) {
		AllSwingTest test = new AllSwingTest() ;
		test.run();
	}
	
	protected String styleSheet = "graph {padding: 60px;}";

	public void run() {
		MultiGraph g = new MultiGraph("mg");
		SwingViewer v = new SwingViewer(g, SwingViewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
		DorogovtsevMendesGenerator gen = new DorogovtsevMendesGenerator();
		
		g.setAttribute("ui.antialias");
		g.setAttribute("ui.quality");
		g.setAttribute("ui.stylesheet", styleSheet);
		
		v.enableAutoLayout();
		add((DefaultView)v.addDefaultView(false, new ScalaGraphRenderer()), BorderLayout.CENTER);
		
		gen.addSink(g);
		gen.begin();
		for(int i = 0 ; i < 100 ; i++)
			gen.nextEvents();
		gen.end();
		gen.removeSink(g);
		
		setSize( 800, 600 );
		setVisible( true );
	}
}