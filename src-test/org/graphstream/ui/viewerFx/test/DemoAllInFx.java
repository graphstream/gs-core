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

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.fxViewer.FxViewPanel;
import org.graphstream.ui.fxViewer.FxViewer;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DemoAllInFx extends Application {
	protected static String styleSheet =
			"graph {"+
			"	padding: 60px;"+
			"}";

	
	public static void main(String args[]) {
        Application.launch(DemoAllInFx.class, args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		Graph graph  = new MultiGraph("mg");
		
		FxViewer viewer = new FxViewer(graph, FxViewer.ThreadingModel.GRAPH_IN_GUI_THREAD);

		graph.addNode("A");
		graph.addNode("B");
		graph.addNode("C");
		graph.addEdge("AB", "A", "B");
		graph.addEdge("BC", "B", "C");
		graph.addEdge("CA", "C", "A");
		graph.setAttribute( "ui.antialias" );
		graph.setAttribute( "ui.quality" );
		graph.setAttribute( "ui.stylesheet", styleSheet );
   
		graph.getNode("A").setAttribute("xyz", -1, 0, 0 );
		graph.getNode("B").setAttribute("xyz",  1, 0, 0 );
  		graph.getNode("C").setAttribute("xyz",  0, 1, 0 );
   
  		// On ins�re la vue principale du viewer dans la JFrame.
  		
  		FxViewPanel v =  (FxViewPanel) viewer.addDefaultView( false ) ;
  		Scene scene = new Scene(v, 800, 600);
  		primaryStage.setScene(scene);
  				
		primaryStage.show();
	}
}
