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
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.stream.file.test;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.AdjacencyListGraph;
import org.graphstream.stream.file.FileSourceGraphML;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class TestFileSourceGraphML {

	protected Graph readRessource(String url, boolean strict) {
		Graph g = new AdjacencyListGraph(url);
		FileSourceGraphML graphml = new FileSourceGraphML();
		graphml.setStrictMode(strict);
		graphml.addSink(g);

		try {
			graphml.readAll(getClass().getResourceAsStream(url));
		} catch (IOException e) {
			fail("IOException occured : " + e.getMessage());
		}

		graphml.removeSink(g);

		return g;
	}

	@Test
	public void testBasic() {
		Graph g = readRessource("data/example.graphml", true);

		Node n1 = g.getNode("1");
		Node n2 = g.getNode("2");
		Node n3 = g.getNode("3");

		assertNotNull(n1);
		assertNotNull(n2);
		assertNotNull(n3);

		assertNotNull(n1.getEdgeToward(n2));
		assertNotNull(n2.getEdgeToward(n3));
		assertNotNull(n3.getEdgeToward(n1));

		assertTrue(g.hasAttribute("label"));
		assertEquals("This is a label", g.getAttribute("label"));

		assertTrue(g.getNode("1").hasAttribute("ordering"));
		assertEquals("1 2", g.getNode("1").getAttribute("ordering"));
		assertTrue(g.getNode("2").hasAttribute("ordering"));
		assertEquals("2 3", g.getNode("2").getAttribute("ordering"));
		assertTrue(g.getNode("3").hasAttribute("ordering"));
		assertEquals("3 1", g.getNode("3").getAttribute("ordering"));
	}

	@Test
	public void testUndeclaredAttributes() {
		Graph g = readRessource("data/example-extraattributes.graphml", false);

		Node n1 = g.getNode("1");
		Node n2 = g.getNode("2");
		Node n3 = g.getNode("3");

		assertNotNull(n1);
		assertNotNull(n2);
		assertNotNull(n3);

		assertTrue(g.getNode("1").hasAttribute("label"));
		assertEquals("My label 1", g.getNode("1").getAttribute("label"));
		assertTrue(g.getNode("2").hasAttribute("other"));
		assertEquals("Other undeclared attribute", g.getNode("2").getAttribute("other"));
	}
}
