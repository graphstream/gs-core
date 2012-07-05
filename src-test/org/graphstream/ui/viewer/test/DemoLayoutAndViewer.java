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

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.stream.ProxyPipe;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceDGS;
import org.graphstream.stream.file.FileSourceGML;
import org.graphstream.stream.thread.ThreadProxyPipe;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.graphicGraph.GraphPosLengthUtils;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.layout.springbox.implementations.LinLog;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;
import org.graphstream.ui.swingViewer.Viewer;

/**
 * This test creates a layout (instead of using the default layout of
 * the viewer) and creates a loop :
 * 
 * <pre>
 *   Graph ------> Layout --+
 *     ^                    |
 *     |                    |
 *     +--------------------+
 * </pre>
 */
public class DemoLayoutAndViewer {
//	public static final String GRAPH = "data/dorogovtsev_mendes6000.dgs"; public static final double a= 0; public static final double r=-1.3; public static double force = 3;
//	public static final String GRAPH = "data/karate.gml";		public static double a= 0; public static double r=-1.3; public static double force = 3;
	public static final String GRAPH = "data/dolphins.gml";		public static double a= 0; public static double r=-1.2; public static double force = 8;
//	public static final String GRAPH = "data/polbooks.gml";		public static double a= 0; public static double r=-2; public static double force = 3;
//	public static final String GRAPH = "data/triangles.dgs";	public static double a= 1; public static double r=-1; public static double force = 3;
//	public static final String GRAPH = "data/FourClusters.dgs";	public static double a= 0; public static double r=-1; public static double force = 3;
//	public static final String GRAPH = "data/grid7x7.dgs";		public static double a= 0; public static double r=-1; public static double force = 100;
//	public static final String GRAPH = "data/imdb.dgs";			

	public static void main(String args[]) {
		new DemoLayoutAndViewer();
	}

	public DemoLayoutAndViewer() {
		boolean loop = true;
		Graph graph = new MultiGraph("test");
		Viewer viewer = new Viewer(new ThreadProxyPipe(graph));
		ProxyPipe fromViewer = viewer.newThreadProxyOnGraphicGraph();
		LinLog layout = new LinLog(false);
		
		layout.configure(a, r, true, force);

		graph.addAttribute("ui.antialias");
		graph.addAttribute("ui.stylesheet", styleSheet);
		fromViewer.addSink(graph);
		viewer.addDefaultView(true);
		graph.addSink(layout);
		layout.addAttributeSink(graph);

		FileSource dgs = GRAPH.endsWith(".gml") ? new FileSourceGML() : new FileSourceDGS();

		dgs.addSink(graph);
		try {
			dgs.begin(getClass().getResourceAsStream(GRAPH));
			for (int i = 0; i < 5000 && dgs.nextEvents(); i++) {
//				fromViewer.pump();
//				layout.compute();
//				sleep(100);
			}
			dgs.end();
		} catch (IOException e1) {
			e1.printStackTrace();
			System.exit(1);
		}
		
		System.out.println("Finished creating the graph.");

		while (loop) {
			fromViewer.pump();

			if (graph.hasAttribute("ui.viewClosed")) {
				loop = false;
			} else {
				//sleep(1000);				
				layout.compute();
				findCommunities(graph, 1.3);
			}
		}

		System.exit(0);
	}
	
	protected void findCommunities(Graph graph, double threshold) {
		int nedges = graph.getEdgeCount();
		double avgDist = 0;
		double edgesDists[] = new double[nedges];
		for(int i=0; i<nedges; i++) {
			Edge edge = graph.getEdge(i);
			Point3 posFrom = GraphPosLengthUtils.nodePointPosition(edge.getNode0());
			Point3 posTo   = GraphPosLengthUtils.nodePointPosition(edge.getNode1());
			edgesDists[i]  = posFrom.distance(posTo);
			avgDist       += edgesDists[i];
		}
		avgDist /= nedges;
		// Nothing happened to the graph so the order remains.
		for(int i=0; i<nedges; i++) {
			Edge edge = graph.getEdge(i);
			if(edgesDists[i] > avgDist*threshold) {
				edge.addAttribute("ui.class", "cut");
			} else {
				edge.removeAttribute("ui.class");
			}
		}
	}
	
	protected static void sleep(long ms) {
		try { Thread.sleep(ms); } catch(Exception e) {} 
	}

	protected static String styleSheet =
		"node { size: 7px; fill-color: rgb(150,150,150); }" +
		"edge { fill-color: rgb(255,50,50); size: 2px; }" +
		"edge.cut { fill-color: rgba(200,200,200,128); }";
}