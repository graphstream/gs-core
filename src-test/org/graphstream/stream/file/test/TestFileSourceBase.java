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
 * @since 2011-05-11
 * 
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.stream.file.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.stream.file.FileSource;
import org.junit.Test;

/**
 * Base test class for file inputs.
 * 
 * <p>
 * This test class propose a set of JUnit standard tests that can apply to any
 * file format.
 * </p>
 */
public abstract class TestFileSourceBase {
	// Attribute

	/**
	 * The graph built when reading the file.
	 */
	protected Graph graph;

	/**
	 * The current input tested.
	 */
	protected FileSource input;

	/**
	 * If false, edge identifiers are not tested (some format cannot specify edge
	 * identifiers).
	 */
	protected boolean testEdgeIds = true;

	// Access

	/**
	 * Return a string containing a file defining a simple triangle made of three
	 * nodes named "A", "B" and "C", tied with three edges "AB", "BC" and "CA".
	 */
	public abstract String anUndirectedTriangle();

	/**
	 * Return a string containing a file defining a simple triangle made of three
	 * nodes named "A", "B" and "C", tied with three edges "AB", "BC" and "CA", with
	 * direction "A" toward "B", undirected between "B" and "C" and directed from
	 * "A" to "C".
	 */
	public abstract String aDirectedTriangle();

	/**
	 * Return a string containing the triangle of {@link #anUndirectedTriangle()},
	 * but each element has attributes :
	 * <ul>
	 * <li>node "A" as three attributes</li>
	 * </ul>
	 */
	public abstract String basicAttributes();

	/**
	 * Return a string containing the name of a local file pointing at the
	 * definition of the triangle evoked in {@link #anUndirectedTriangle()}.
	 */
	public abstract String anUndirectedTriangleFileName();

	/**
	 * Return a string containing an HTTP URL pointing at the definition of the
	 * triangle evoked in {@link #anUndirectedTriangle()}.
	 */
	public abstract String anUndirectedTriangleHttpURL();

	// Test

	@Test
	public void test_Access_ReadAll_Reader() {
		try {
			input.addSink(graph);
			input.readAll(new StringReader(anUndirectedTriangle()));
			undirectedTriangleTests();
		} catch (IOException e) {
			assertTrue("IOException, should not happen" + e.getMessage(), false);
			e.printStackTrace();
		}
	}

	@Test
	public void test_Access_ByStep_Reader() {
		try {
			input.addSink(graph);
			input.begin(new StringReader(anUndirectedTriangle()));
			while (input.nextEvents())
				;
			input.end();

			undirectedTriangleTests();
		} catch (IOException e) {
			assertTrue("IOException, should not happen" + e.getMessage(), false);
			e.printStackTrace();
		}
	}

	@Test
	public void test_Access_ReadAll_Stream() {
		try {
			input.addSink(graph);
			input.readAll(new FileInputStream(anUndirectedTriangleFileName()));
			undirectedTriangleTests();
		} catch (IOException e) {
			assertTrue("IOException, should not happen" + e.getMessage(), false);
			e.printStackTrace();
		}
	}

	@Test
	public void test_Access_ReadAll_URL() {
		try {
			URL url = new URL(anUndirectedTriangleHttpURL());

			HttpURLConnection c = (HttpURLConnection)url.openConnection();
			c.setDefaultUseCaches(false);
			c.setReadTimeout(5000);
			c.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
			c.addRequestProperty("User-Agent", "Mozilla");
			c.addRequestProperty("Referer", "google.com");
			boolean redirect = false;
			int status = c.getResponseCode();
			if (status != HttpURLConnection.HTTP_OK) {
				if (status == HttpURLConnection.HTTP_MOVED_TEMP
					|| status == HttpURLConnection.HTTP_MOVED_PERM
					|| status == HttpURLConnection.HTTP_SEE_OTHER)
			redirect = true;
			if (redirect) {

				// get redirect url from "location" header field
				String newUrl = c.getHeaderField("Location");
	
				// get the cookie if need, for login
				String cookies = c.getHeaderField("Set-Cookie");
		
				// open the new connection again
				url = new URL(newUrl);
				c = (HttpURLConnection) url.openConnection();
				c.setRequestProperty("Cookie", cookies);
				c.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
				c.addRequestProperty("User-Agent", "Mozilla");
				c.addRequestProperty("Referer", "google.com");
		
				System.out.println("Redirect to URL : " + newUrl);
		
			}
	
		}

			input.addSink(graph);
			input.readAll(url);
			undirectedTriangleTests();
		} catch (IOException e) {
			e.printStackTrace();
			assertTrue("IOException, should not happen" + e.getMessage(), false);
		}
	}

	@Test
	public void test_Access_ReadAll_FileName() {
		try {
			input.addSink(graph);
			input.readAll(anUndirectedTriangleFileName());
			undirectedTriangleTests();
		} catch (IOException e) {
			e.printStackTrace();
			assertTrue("IOException, should not happen" + e.getMessage(), false);
		}
	}

	@Test
	public void test_DirectedTriangle() {
		try {
			input.addSink(graph);
			input.readAll(new StringReader(aDirectedTriangle()));
			directedTriangleTests();
		} catch (IOException e) {
			e.printStackTrace();
			assertTrue("IOException, should not happen" + e.getMessage(), false);
		}
	}

	@Test
	public void test_Attributes() {
		try {
			input.addSink(graph);
			input.readAll(new StringReader(basicAttributes()));
			basicAttributesTests();
		} catch (IOException e) {
			e.printStackTrace();
			assertTrue("IOException, should not happen" + e.getMessage(), true);
		}
	}

	// Test

	protected void undirectedTriangleTests() {
		assertEquals(3, graph.getEdgeCount());
		assertEquals(3, graph.getNodeCount());
		assertNotNull(graph.getNode("A"));
		assertNotNull(graph.getNode("B"));
		assertNotNull(graph.getNode("C"));

		if (testEdgeIds) {
			assertNotNull(graph.getEdge("AB"));
			assertNotNull(graph.getEdge("BC"));
			assertNotNull(graph.getEdge("CA"));
		}
	}

	protected void directedTriangleTests() {
		assertEquals(3, graph.getEdgeCount());
		assertEquals(3, graph.getNodeCount());

		Node A = graph.getNode("A");
		Node B = graph.getNode("B");
		Node C = graph.getNode("C");

		assertNotNull(A);
		assertNotNull(B);
		assertNotNull(C);

		if (testEdgeIds) {
			Edge AB = graph.getEdge("AB");
			Edge BC = graph.getEdge("BC");
			Edge CA = graph.getEdge("CA");

			assertNotNull(AB);
			assertNotNull(BC);
			assertNotNull(CA);

			assertTrue(AB.isDirected());
			assertFalse(BC.isDirected());
			assertTrue(CA.isDirected());

			assertEquals("A", AB.getNode0().getId());
			assertEquals("B", AB.getNode1().getId());
			assertEquals("B", BC.getNode0().getId());
			assertEquals("C", BC.getNode1().getId());
			assertEquals("A", CA.getNode0().getId());
			assertEquals("C", CA.getNode1().getId());
		}

		assertTrue(A.hasEdgeToward("B"));
		assertTrue(A.hasEdgeToward("C"));
		assertTrue(B.hasEdgeToward("C"));
		assertFalse(B.hasEdgeToward("A"));
		assertFalse(C.hasEdgeToward("A"));
		assertTrue(C.hasEdgeToward("B"));

		Edge AB = A.getEdgeToward("B");
		Edge BC = B.getEdgeToward("C");
		Edge CA = A.getEdgeToward("C");

		assertNotNull(AB);
		assertNotNull(BC);
		assertNotNull(CA);

		assertTrue(AB.isDirected());
		assertFalse(BC.isDirected());
		assertTrue(CA.isDirected());
		assertEquals("B", AB.getNode1().getId());
		assertEquals("C", BC.getNode1().getId());
		assertEquals("C", CA.getNode1().getId());
	}

	protected void basicAttributesTests() {
		assertEquals(3, graph.getEdgeCount());
		assertEquals(3, graph.getNodeCount());

		Node A = graph.getNode("A");
		Node B = graph.getNode("B");
		Node C = graph.getNode("C");

		assertNotNull(A);
		assertNotNull(B);
		assertNotNull(C);

		assertTrue(A.hasAttribute("a"));
		assertTrue(A.hasAttribute("b"));
		assertTrue(A.hasAttribute("c"));
		assertTrue(B.hasAttribute("aa"));
		assertTrue(B.hasAttribute("bb"));
		assertTrue(B.hasAttribute("cc"));
		assertTrue(C.hasAttribute("aaa"));

		assertEquals(1.0, ((Number) A.getAttribute("a")).doubleValue(), 1E-12);
		assertEquals("truc", A.getAttribute("b"));
		assertEquals("true", A.getAttribute("c"));

		assertNotNull(B.getAttribute("aa"));
		assertEquals("foo", B.getAttribute("bb"));
		assertEquals("bar", B.getAttribute("cc"));

		assertEquals(1.234, C.getNumber("aaa"), 0);
	}
}