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
 * @since 2010-03-05
 * 
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Stefan Balev <stefan.balev@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.stream.file.gml.test;

import java.io.IOException;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.stream.file.FileSinkDynamicGML;
import org.graphstream.stream.file.FileSinkGML;
import org.junit.Ignore;

@Ignore
public class TestSinkGML {
	public static void main(String args[]) {
		try {
			(new TestSinkGML()).test();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void test() throws IOException {
		Graph graph = new MultiGraph("test GML");
		FileSinkGML out1 = new FileSinkGML();
		FileSinkDynamicGML out2 = new FileSinkDynamicGML();

		out1.begin("TestSinkGML.gml");
		out2.begin("TestSinkGML.dgml");

		graph.addSink(out1);
		graph.addSink(out2);

		graph.addNode("A");
		graph.getNode("A").setAttribute("s", "foo bar");
		graph.addNode("B");
		graph.stepBegins(1);
		graph.addEdge("AB", "A", "B", true);
		graph.getEdge("AB").setAttribute("n", 1);
		graph.stepBegins(2);
		graph.setAttribute("b", true);
		graph.getNode("B").setAttribute("c", 'X');
		graph.getNode("B").setAttribute("d", 'Y');
		graph.stepBegins(3);
		graph.getNode("B").removeAttribute("c");
		graph.removeAttribute("b");
		graph.removeNode("A");
		graph.removeNode("B");

		out1.end();
		out2.end();
	}
}