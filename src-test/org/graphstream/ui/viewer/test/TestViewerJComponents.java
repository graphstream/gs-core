/*
 * Copyright 2006 - 2011 
 *     Stefan Balev 	<stefan.balev@graphstream-project.org>
 *     Julien Baudry	<julien.baudry@graphstream-project.org>
 *     Antoine Dutot	<antoine.dutot@graphstream-project.org>
 *     Yoann Pigné		<yoann.pigne@graphstream-project.org>
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

import javax.swing.UIManager;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.stream.ProxyPipe;
import org.graphstream.stream.thread.ThreadProxyPipe;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.graphstream.ui.swingViewer.Viewer;

public class TestViewerJComponents {
	public static void main(String args[]) {
		// setLAF();
		new TestViewerJComponents();
	}

	public TestViewerJComponents() {
		Graph graph = new MultiGraph("main graph");
		ThreadProxyPipe toSwing = new ThreadProxyPipe(graph);
		Viewer viewer = new Viewer(toSwing);
		ProxyPipe fromSwing = viewer.newThreadProxyOnGraphicGraph();
		SpriteManager sman = new SpriteManager(graph);

		fromSwing.addAttributeSink(graph);
		viewer.addDefaultView(true);

		Node A = graph.addNode("A");
		Node B = graph.addNode("B");
		Node C = graph.addNode("C");

		graph.addEdge("AB", "A", "B");
		graph.addEdge("BC", "B", "C");
		graph.addEdge("CA", "C", "A");

		A.addAttribute("xyz", 0, 1, 0);
		B.addAttribute("xyz", 1, 0, 0);
		C.addAttribute("xyz", -1, 0, 0);

		A.addAttribute("ui.label", "Quit");
		B.addAttribute("ui.label", "Editable text");
		C.addAttribute("ui.label", "Click to edit");

		graph.addAttribute("ui.stylesheet", styleSheet);

		Sprite s1 = sman.addSprite("S1");
		Sprite s2 = sman.addSprite("S2");
		Sprite s3 = sman.addSprite("S3");

		s1.attachToNode("B");
		s2.attachToEdge("BC");
		s1.setPosition(StyleConstants.Units.PX, 1, 0, 0);
		s2.setPosition(0.5f);
		s3.setPosition(0, 0.5f, 0);
		s1.addAttribute("ui.label", "1");
		s2.addAttribute("ui.label", "2");
		// s3.addAttribute( "ui.label", "" );

		boolean loop = true;
		// float x = 0;
		// float y = 1;
		// float dir = 0.005f;
		float angle = 0;

		while (loop) {
			try {
				Thread.sleep(30);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			fromSwing.pump();

			if (graph.hasAttribute("ui.viewClosed")) {
				loop = false;
			} else {
				if (A.hasAttribute("ui.clicked")) {
					System.err.printf("A clicked (%s)%n",
							A.getLabel("ui.label"));
					A.removeAttribute("ui.clicked");
					loop = false;
				} else if (B.hasAttribute("ui.clicked")) {
					System.err.printf("B clicked (%s)%n",
							B.getLabel("ui.label"));
					B.removeAttribute("ui.clicked");
				} else if (C.hasAttribute("ui.clicked")) {
					System.err.printf("C clicked (%s)%n",
							C.getLabel("ui.label"));
					C.removeAttribute("ui.clicked");
					if (C.hasAttribute("ui.class"))
						C.removeAttribute("ui.class");
					else
						C.addAttribute("ui.class", "editable");
				}

				angle += 0.01;
				if (angle > 360)
					angle = 0;
				s1.setPosition(StyleConstants.Units.PX, 70, angle, angle);
				/*
				 * x += dir;
				 * 
				 * if( x > 0.5f || x < -0.5f ) dir = -dir;
				 * 
				 * if( x == -0 ) x = 0;
				 * 
				 * A.setAttribute( "xyz", x, y, 0 );
				 */// showSelection( graph );
			}
		}

		System.out.printf("Bye bye ...%n");
		System.exit(0);
	}

	protected static String styleSheet = "graph {" + "	padding:      60px;"
			+ "	stroke-width: 1px;" + "	stroke-color: rgb(200,200,200);"
			+ "	stroke-mode:  dots;" + "	fill-mode:    gradient-diagonal1;"
			+ "	fill-color:   white, rgb(230,230,230);" + "}" + "node {"
			+ "	shape:        jcomponent;" + "	jcomponent:   button;"
			+ "	size:         100px, 30px;" + "	stroke-width: 2px;"
			+ "	stroke-color: rgb(180,180,180);" + "	fill-mode:    none;"
			+ "	text-font:    arial;" + "	text-size:    11;"
			+ "	text-color:   rgb(30,30,30);" + "	text-style:   bold;" + " }"
			+ "node#B {" + "	shape:      jcomponent;"
			+ "	jcomponent: text-field;" + "	text-color: red;"
			+ "	text-style: italic;" + "}" + "node#C {"
			+ "	icon:		url(\"file:///home/antoine/GSLogo11a32.png\");"
			+ "	icon-mode:	at-left;" + "}" + "sprite#S3 {"
			+ "	size:       70, 80;" + "	size-mode:	fit;"
			+ "	icon:		url(\"file:///home/antoine/GSLogo11a64.png\");"
			+ "	icon-mode:	above;" + "}" + "node.editable {"
			+ "	shape:      jcomponent;" + "	jcomponent: text-field;" + "}"
			+ "node:selected {"
			+ "	stroke-mode: plain; stroke-width: 5px; stroke-color: red;"
			+ "}" + "sprite {" + "	shape:      jcomponent;"
			+ "	jcomponent: button;" + "	size:       30px, 30px;"
			+ "	fill-mode:  none;" + "}";

	protected static void setLAF() {
		try {
			UIManager.LookAndFeelInfo[] installed = UIManager
					.getInstalledLookAndFeels();

			for (int i = 0; i < installed.length; i++) {
				if (installed[i].getName().startsWith("GTK")) {
					UIManager.setLookAndFeel(installed[i].getClassName());
					i = installed.length;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void showSelection(Graph graph) {
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
}