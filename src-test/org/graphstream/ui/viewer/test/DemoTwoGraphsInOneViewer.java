/*
 * Copyright 2006 - 2012
 *      Stefan Balev       <stefan.balev@graphstream-project.org>
 *      Julien Baudry	<julien.baudry@graphstream-project.org>
 *      Antoine Dutot	<antoine.dutot@graphstream-project.org>
 *      Yoann Pigné	<yoann.pigne@graphstream-project.org>
 *      Guilhelm Savin	<guilhelm.savin@graphstream-project.org>
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

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.stream.file.FileSourceDGS;
import org.graphstream.stream.thread.*;
import org.graphstream.ui.swingViewer.*;

public class DemoTwoGraphsInOneViewer {
	public static final String GRAPH = "data/dorogovtsev_mendes6000.dgs";

	public static void main(String args[]) {
		new DemoTwoGraphsInOneViewer();
	}

	public DemoTwoGraphsInOneViewer() {
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
