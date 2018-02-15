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
 * @since 2011-12-06
 * 
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Yoann Pigné <yoann.pigne@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.stream.file.dgs.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.AdjacencyListGraph;
import org.graphstream.stream.SinkAdapter;
import org.graphstream.stream.file.FileSourceDGS;
import org.graphstream.util.VerboseSink;
import org.graphstream.util.parser.ParseException;
import org.junit.Test;

public class TestDGSParser {

	protected File newTemporaryFile(String suffix) throws IOException {
		return File.createTempFile(getClass().getSimpleName(), suffix);
	}

	protected Graph getGraph(String resource) throws IOException {
		Graph g = new AdjacencyListGraph("test");
		FileSourceDGS in = new FileSourceDGS();

		in.addSink(g);
		in.readAll(getClass().getResourceAsStream(resource));
		in.removeSink(g);

		return g;
	}

	@Test
	public void testArrayAttribute() throws IOException {
		Graph g = getGraph("data/attributes_array.dgs");
		Node n = g.getNode(0);

		if (!n.hasArray("a1"))
			fail();

		if (!n.hasArray("a2"))
			fail();

		Object[] a1 = n.getArray("a1");
		Object[] a2 = n.getArray("a2");
		Object[] expected = { "A", "B", "C" };

		assertArrayEquals(expected, a1);
		assertArrayEquals(expected, a2);
	}

	private static class Attribute {
		String key;
		Class<?> clazz;
		Object expected;

		Attribute(String key, Class<?> clazz, Object expected) {
			this.key = key;
			this.clazz = clazz;
			this.expected = expected;
		}

		void check(Element e) {
			Object obj = e.getAttribute(key);

			if (expected.getClass().isArray()) {
				Object[] objArray = (Object[]) obj;

				assertTrue(clazz.isAssignableFrom(objArray[0].getClass()));
				assertArrayEquals((Object[]) expected, objArray);
			} else {
				assertTrue(clazz.isAssignableFrom(obj.getClass()));
				assertEquals(expected, obj);
			}
		}
	}

	@Test
	public void testAttributes() throws IOException {
		Graph g = getGraph("data/attributes.dgs");

		HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("a", 1);
		map.put("b", 2);
		map.put("c", 3);

		Object[][] aoa = { { 1, 2 }, { 3 }, { 4, 5 } };

		Attribute[] attributes = { new Attribute("int", Integer.class, Integer.valueOf(123)),
				new Attribute("double", Double.class, Double.valueOf(123.321)),
				new Attribute("string", String.class, "a string"), new Attribute("word", String.class, "aWord"),
				new Attribute("color", Color.class, Color.RED), new Attribute("map", Map.class, map),
				new Attribute("array", Integer.class, new Object[] { 1, 2, 3 }),
				new Attribute("aoa", Object[].class, aoa),
				new Attribute("big_sci", Double.class, Double.valueOf("1.27E+07")),
				new Attribute("small_sci", Double.class, Double.valueOf("1.27E-07")),
				new Attribute("neg_sci", Double.class, Double.valueOf("-1.27E-07")) };

		for (Node n : g) {
			for (Attribute a : attributes)
				a.check(n);
		}
	}

	@Test
	public void testElements() throws IOException {
		Graph g = getGraph("data/elements.dgs");

		Node A, B, C;
		Edge AB, AC, BC;

		A = g.getNode("A");
		B = g.getNode("B");
		C = g.getNode("C");

		assertEquals(g.getNodeCount(), 3);

		assertNotNull(A);
		assertNotNull(B);
		assertNotNull(C);

		AB = g.getEdge("AB");
		AC = g.getEdge("AC");
		BC = g.getEdge("BC");

		assertEquals(g.getEdgeCount(), 3);

		assertNotNull(AB);
		assertNotNull(AC);
		assertNotNull(BC);

		assertFalse(AB.isDirected());
		assertTrue(AC.isDirected());
		assertTrue(BC.isDirected());

		assertEquals(A, AB.getNode0());
		assertEquals(B, AB.getNode1());
		assertEquals(A, AC.getSourceNode());
		assertEquals(C, AC.getTargetNode());
		assertEquals(B, BC.getSourceNode());
		assertEquals(C, BC.getTargetNode());
	}

	@Test
	public void testBadExamples() throws IOException {
		String[] data = { "bad1.dgs", "bad2.dgs" };

		for (int i = 0; i < data.length; i++) {
			try {
				getGraph("data/" + data[i]);
				fail();
			} catch (Exception e) {
				if (!(e.getCause() instanceof ParseException)) {

					if (e instanceof IOException)
						throw (IOException) e;
					else
						fail();
				}
			}
		}
	}

	/**
	 * <pre>
	 * LF:    Line Feed, U+000A
	 * VT:    Vertical Tab, U+000B
	 * FF:    Form Feed, U+000C
	 * CR:    Carriage Return, U+000D
	 * CR+LF: CR (U+000D) followed by LF (U+000A)
	 * NEL:   Next Line, U+0085
	 * LS:    Line Separator, U+2028
	 * PS:    Paragraph Separator, U+2029
	 * </pre>
	 * 
	 * Current supported EOL are LF and CR+LF.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testEOL() throws IOException {
		String base = "DGS004%neol 0 0%n%nan A%n";
		String[] eols = { /* LF */ "\n", /* CR+LF */ "\r\n" };
		FileSourceDGS source = new FileSourceDGS();
		Graph g = new AdjacencyListGraph("eol");

		source.addSink(g);

		for (String eol : eols) {
			String dgs = base.replace("%n", eol);
			StringReader in = new StringReader(dgs);

			try {
				source.readAll(in);
				assertNotNull(g.getNode("A"));
				g.clear();
			} catch (IOException e) {
				if (e.getCause() instanceof ParseException)
					fail();
				else
					throw e;
			}
		}
	}

	@Test
	public void testAttributeRemoved() throws IOException {
		FileSourceDGS source = new FileSourceDGS();
		Graph g = new AdjacencyListGraph("eol");

		source.addSink(g);
		g.addSink(new TestAttributeRemoved("A", g));
		g.addSink(new VerboseSink());

		source.begin(getClass().getResourceAsStream("data/removeAttribute.dgs"));

		while (source.nextStep())
			;

		source.end();
	}

	private static class TestAttributeRemoved extends SinkAdapter {
		String nodeId;
		boolean added;
		boolean changed;
		boolean removed;
		Object value;
		Graph g;

		TestAttributeRemoved(String nodeId, Graph g) {
			added = changed = removed = false;
			value = null;
			this.nodeId = nodeId;
			this.g = g;
		}

		public void nodeAttributeAdded(String sourceId, long timeId, String nodeId, String attributeId, Object value) {
			if (this.nodeId.equals(nodeId)) {
				assertFalse(added);
				assertFalse(changed);
				assertFalse(removed);

				added = true;
				this.value = value;
			}
		}

		public void nodeAttributeChanged(String sourceId, long timeId, String nodeId, String attributeId,
				Object oldValue, Object newValue) {
			if (this.nodeId.equals(nodeId)) {
				assertTrue(added);
				assertFalse(changed);
				assertFalse(removed);
				assertEquals(value, oldValue);

				changed = true;
				value = newValue;
			}
		}

		public void nodeAttributeRemoved(String sourceId, long timeId, String nodeId, String attributeId) {
			if (this.nodeId.equals(nodeId)) {
				assertTrue(added);
				assertTrue(changed);
				assertFalse(removed);
				assertEquals(value, g.getNode(nodeId).getAttribute(attributeId));

				removed = true;
				value = null;
			}
		}
	}
}
