/*
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

/**
 * @since 2011-07-04
 * 
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Stefan Balev <stefan.balev@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 * @author Yoann Pigné <yoann.pigne@graphstream-project.org>
 */
package org.graphstream.stream.file.gml.test;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.stream.file.FileSourceGML;
import org.junit.Ignore;

@Ignore
public class TestSourceGML {
	public static void main(String args[]) {
		// System.setProperty("gs.ui.renderer",
		// "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		System.setProperty("org.graphstream.ui", "org.graphstream.ui.swingViewer.util.SwingDisplay");
		new TestSourceGML();
	}

	public TestSourceGML() {
		testBigFile();
		testDynFile();
		testSmallFile();
	}

	public void testDynFile() {
		try {
			Graph graph = new MultiGraph("Dynamic !");
			FileSourceGML source = new FileSourceGML();

			graph.setAttribute("ui.quality");
			graph.setAttribute("ui.antialias");
			graph.display();
			source.addSink(graph);
			source.begin(TestSourceGML.class.getResourceAsStream("dynamic.gml"));
			int step = 0;
			while (source.nextStep()) {
				System.err.printf("Step %d%n", step);
				step++;
				sleep(1000);
			}
			source.end();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (Exception e) {
		}
	}

	public void testBigFile() {
		try {
			Graph graph = new MultiGraph("foo");
			FileSourceGML source = new FileSourceGML();

			graph.setAttribute("ui.quality");
			graph.setAttribute("ui.antialias");
			graph.setAttribute("ui.stylesheet",
					"node { text-size:8; text-color: #0008; text-alignment: at-right; } edge { text-size:8; text-color: #0008; }");
			graph.display(false);
			source.addSink(graph);
			source.begin(TestSourceGML.class.getResourceAsStream("example2.sif.gml"));
			while (source.nextEvents()) {
			}
			source.end();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testSmallFile() {
		try {
			Graph graph = new MultiGraph("foo");
			FileSourceGML source = new FileSourceGML();

			graph.setAttribute("ui.quality");
			graph.setAttribute("ui.antialias");
			// graph.setAttribute("ui.stylesheet", "node { text-size:8; text-color: #0008;
			// text-alignment: at-right; } edge { text-size:8; text-color: #0008; }");
			graph.display();
			source.addSink(graph);
			source.begin(TestSourceGML.class.getResourceAsStream("SmallTest.gml"));
			while (source.nextEvents()) {
			}
			source.end();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
