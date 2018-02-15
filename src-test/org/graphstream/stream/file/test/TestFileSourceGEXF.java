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
 * @since 2011-10-06
 * 
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.stream.file.test;

import java.io.IOException;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.AdjacencyListGraph;
import org.graphstream.stream.file.FileSourceGEXF;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestFileSourceGEXF {

	protected Graph readRessource(String url) {
		Graph g = new AdjacencyListGraph(url);
		FileSourceGEXF gexf = new FileSourceGEXF();

		gexf.addSink(g);

		try {
			gexf.readAll(getClass().getResourceAsStream(url));
		} catch (IOException e) {
			fail("IOException occured");
		}

		gexf.removeSink(g);

		return g;
	}

	@Test
	public void testBasic() {
		Graph g = readRessource("data/basic.gexf");

		assertNotNull(g.getNode("0"));
		assertNotNull(g.getNode("1"));
		assertNotNull(g.getEdge("0"));

		assertEquals(g.getEdge("0").getSourceNode().getId(), "0");
		assertEquals(g.getEdge("0").getTargetNode().getId(), "1");

		assertTrue(g.getNode("0").hasLabel("label"));
		assertTrue(g.getNode("1").hasLabel("label"));

		assertEquals(g.getNode("0").getLabel("label"), "Hello");
		assertEquals(g.getNode("1").getLabel("label"), "Word");

		assertTrue(g.getEdge("0").isDirected());
	}

	@Test
	public void testData() {
		Graph g = readRessource("data/data.gexf");

		String[] nodeLabels = { "Gephi", "Webatlas", "RTGI", "BarabasiLab" };
		String[] edges = { "0", "1", "0", "2", "1", "0", "2", "1", "0", "3" };
		String[] urlValues = { "http://gephi.org", "http://webatlas.fr", "http://rtgi.fr", "http://barabasilab.com" };
		Float[] indegreeValues = { 1.0f, 2.0f, 1.0f, 1.0f };
		Boolean[] frogValues = { true, true, true, false };

		assertEquals(g.getAttribute("lastmodifieddate"), "2009-03-20");
		assertEquals(g.getAttribute("creator"), "Gephi.org");
		assertEquals(g.getAttribute("description"), "A Web network");

		for (int i = 0; i < 4; i++) {
			String nid = Integer.toString(i);

			assertNotNull(g.getNode(nid));
			assertTrue(g.getNode(nid).hasLabel("label"));
			assertEquals(g.getNode(nid).getLabel("label"), nodeLabels[i]);

			assertEquals(g.getNode(nid).getAttribute("url"), urlValues[i]);
			assertEquals(g.getNode(nid).getAttribute("indegree"), indegreeValues[i]);
			assertEquals(g.getNode(nid).getAttribute("frog"), frogValues[i]);
		}

		for (int i = 0; i < 5; i++) {
			String eid = Integer.toString(i);

			assertNotNull(g.getEdge(eid));
			assertTrue(g.getEdge(eid).isDirected());

			assertEquals(g.getEdge(eid).getSourceNode().getId(), edges[2 * i]);
			assertEquals(g.getEdge(eid).getTargetNode().getId(), edges[2 * i + 1]);
		}
	}
}
