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

import java.io.IOException;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.stream.ProxyPipe;
import org.graphstream.stream.file.FileSourceDGS;
import org.graphstream.stream.thread.ThreadProxyPipe;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.layout.springbox.SpringBox;
import org.graphstream.ui.swingViewer.Viewer;

public class TestLayoutAndViewer {
	public static final String GRAPH = "data/dorogovtsev_mendes6000.dgs";

	public static void main(String args[]) {
		new TestLayoutAndViewer();
	}

	public TestLayoutAndViewer() {
		boolean loop = true;
		Graph graph = new MultiGraph("g1");
		Viewer viewer = new Viewer(new ThreadProxyPipe(graph));
		ProxyPipe fromViewer = viewer.newThreadProxyOnGraphicGraph();
		Layout layout = new SpringBox(false);

		graph.addAttribute("ui.antialias");
		graph.addAttribute("ui.stylesheet", styleSheet);
		fromViewer.addSink(graph);
		viewer.addDefaultView(true);
		graph.addSink(layout);
		layout.addAttributeSink(graph);

		FileSourceDGS dgs = new FileSourceDGS();

		dgs.addSink(graph);
		try {
			dgs.begin(getClass().getResourceAsStream(GRAPH));
			for (int i = 0; i < 5000 && dgs.nextEvents(); i++)
				;
			dgs.end();
		} catch (IOException e1) {
			e1.printStackTrace();
			System.exit(1);
		}

		while (loop) {
			fromViewer.pump();

			if (graph.hasAttribute("ui.viewClosed")) {
				loop = false;
			} else {
				try {
					Thread.sleep(20);
				} catch (Exception e) {
				}
				layout.compute();
			}
		}

		System.exit(0);
	}

	protected static String styleSheet = "node { size: 3px; fill-color: rgb(150,150,150); }"
			+ "edge { fill-color: rgb(100,100,100); }";
}