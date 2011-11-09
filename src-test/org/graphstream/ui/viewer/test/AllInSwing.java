/*
 * Copyright 2006 - 2011 
 *     Stefan Balev 	<stefan.balev@graphstream-project.org>
 *     Julien Baudry	<julien.baudry@graphstream-project.org>
 *     Antoine Dutot	<antoine.dutot@graphstream-project.org>
 *     Yoann PignÃ©		<yoann.pigne@graphstream-project.org>
 *     Guilhelm Savin	<guilhelm.savin@graphstream-project.org>
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