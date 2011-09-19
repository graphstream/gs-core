package org.graphstream.ui.viewer.test;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.swingViewer.Viewer;

public class AllInSwing {
	public static void main(String args[]) {
		new AllInSwing();
	}
	
	public AllInSwing() {
		// On est dans le thread main.
		
		Graph graph  = new MultiGraph("mg");
		
		// On demande au viewer de considérer que le graphe ne sera lu et modifié que
		// dans le thread Swing.
		
		Viewer viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_SWING_THREAD);

		// À Partir de là, le viewer considère que le graphe est dans son propre thread,
		// c'est-à-dire le thread Swing. Il est donc dangereux d'y toucher dans la thread
		// main. On utilise invokeLater pour faire tourner du code dans le thread Swing,
		// par exemple pour initialiser l'application :
		
		SwingUtilities.invokeLater(new InitializeApplication(viewer, graph));
	}
}

class InitializeApplication extends JFrame implements Runnable {
	private static final long serialVersionUID = - 804177406404724792L;
	protected Graph graph;
	protected Viewer viewer;
	
	public InitializeApplication(Viewer viewer, Graph graph) {
		this.viewer = viewer;
		this.graph = graph;
	}
	
	public void run() {
		graph.addNode("A");
		graph.addNode("B");
		graph.addNode("C");
		graph.addEdge("AB", "A", "B");
		graph.addEdge("BC", "B", "C");
		graph.addEdge("CA", "C", "A");
		graph.addAttribute( "ui.antialias" );
		graph.addAttribute( "ui.quality" );
		graph.addAttribute( "ui.stylesheet", styleSheet );
   
		graph.getNode("A").setAttribute("xyz", -1, 0, 0 );
		graph.getNode("B").setAttribute("xyz",  1, 0, 0 );
  		graph.getNode("C").setAttribute("xyz",  0, 1, 0 );
   
  		// On insère la vue principale du viewer dans la JFrame.
  		
		add(viewer.addDefaultView( false ), BorderLayout.CENTER );
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(800, 600);
		setVisible(true);
	}
  
	protected static String styleSheet =
			"graph {"+
			"	padding: 60px;"+
			"}";
}