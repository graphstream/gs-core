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
 * @author kitskub <kitskub@gmail.com>
 * @author Yoann Pigné <yoann.pigne@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.graph.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.junit.Test;

public class TestElement {
	@Test
	public void testElementSimpleAttributes() {
		Graph graph = new MultiGraph("g1");

		Node A = graph.addNode("A");

		assertEquals("A", A.getId());
		assertEquals(0, A.getAttributeCount());

		// Simple attributes.

		A.setAttribute("foo");

		assertEquals(1, A.getAttributeCount());
		assertTrue(A.hasAttribute("foo"));
		assertTrue(A.hasAttribute("foo", Boolean.class));
		assertFalse(A.hasLabel("foo"));
		assertFalse(A.hasNumber("foo"));
		assertFalse(A.hasVector("foo"));
		assertFalse(A.hasArray("foo"));
		assertFalse(A.hasMap("foo"));
		assertNotNull(A.getAttribute("foo"));
		assertEquals(true, A.getAttribute("foo"));
		assertEquals(Boolean.TRUE, A.getAttribute("foo"));

		// Change.

		A.setAttribute("foo", false);

		assertEquals(1, A.getAttributeCount());
		assertTrue(A.hasAttribute("foo"));
		assertTrue(A.hasAttribute("foo", Boolean.class));
		assertFalse(A.hasLabel("foo"));
		assertFalse(A.hasNumber("foo"));
		assertFalse(A.hasVector("foo"));
		assertFalse(A.hasArray("foo"));
		assertFalse(A.hasMap("foo"));
		assertNotNull(A.getAttribute("foo"));
		assertEquals(false, A.getAttribute("foo"));
		assertEquals(Boolean.FALSE, A.getAttribute("foo"));

		// Removal.

		A.removeAttribute("foo");
		assertEquals(0, A.getAttributeCount());
		assertFalse(A.hasAttribute("foo"));
		assertNull(A.getAttribute("foo"));
	}

	@Test
	public void testElementValueAttributes() {
		Graph graph = new MultiGraph("g1");

		Node A = graph.addNode("A");

		assertEquals("A", A.getId());
		assertEquals(0, A.getAttributeCount());

		// Label attributes.

		A.setAttribute("foo", "bar");

		assertEquals(1, A.getAttributeCount());
		assertTrue(A.hasAttribute("foo"));
		assertTrue(A.hasAttribute("foo", String.class));
		assertTrue(A.hasLabel("foo"));
		assertFalse(A.hasNumber("foo"));
		assertFalse(A.hasVector("foo"));
		assertFalse(A.hasArray("foo"));
		assertFalse(A.hasMap("foo"));
		assertNotNull(A.getAttribute("foo"));
		assertEquals("bar", A.getAttribute("foo"));

		// Number attributes.

		A.setAttribute("pi", 3.1415);

		assertEquals(2, A.getAttributeCount());
		assertTrue(A.hasAttribute("pi"));
		assertTrue(A.hasAttribute("pi", Number.class));
		assertFalse(A.hasLabel("pi"));
		assertTrue(A.hasNumber("pi"));
		assertFalse(A.hasVector("pi"));
		assertFalse(A.hasArray("pi"));
		assertFalse(A.hasMap("pi"));
		assertNotNull(A.getAttribute("pi"));
		assertEquals(3.1415, A.getNumber("pi"), 0);
		assertEquals(3.1415, A.getAttribute("pi"));

		A.setAttribute("pi", "3.1415");

		assertEquals(3.1415, A.getNumber("pi"), 0);

		// Vector of numbers.

		ArrayList<Number> numbers = new ArrayList<>();

		numbers.add(3);
		numbers.add(1.4);
		numbers.add(1.5f);

		A.setAttribute("v", numbers);

		assertEquals(3, A.getAttributeCount());
		assertTrue(A.hasAttribute("v"));
		assertTrue(A.hasAttribute("v", ArrayList.class));
		assertFalse(A.hasLabel("v"));
		assertFalse(A.hasNumber("v"));
		assertTrue(A.hasVector("v"));
		assertFalse(A.hasArray("v"));
		assertFalse(A.hasMap("v"));
		assertNotNull(A.getAttribute("v"));
		assertEquals(numbers, A.getAttribute("v"));
		assertEquals(numbers, A.getVector("v"));

		// Hashes 1.

		HashMap<String, String> map = new HashMap<>();

		map.put("A", "a");
		map.put("B", "b");
		map.put("C", "c");

		A.setAttribute("map", map);

		assertEquals(4, A.getAttributeCount());
		assertTrue(A.hasAttribute("map"));
		assertTrue(A.hasAttribute("map", HashMap.class));
		assertFalse(A.hasLabel("map"));
		assertFalse(A.hasNumber("map"));
		assertFalse(A.hasVector("map"));
		assertFalse(A.hasArray("map"));
		assertTrue(A.hasMap("map"));
		assertNotNull(A.getAttribute("map"));
		assertEquals(map, A.getAttribute("map"));
		assertEquals(map, A.getMap("map"));

		// Hashes 2.

		HashMap<String, String> attr = new HashMap<>();

		attr.put("A", "a");
		attr.put("B", "b");
		attr.put("C", "c");

		A.setAttribute("ca", attr);

		assertEquals(5, A.getAttributeCount());
		assertTrue(A.hasAttribute("ca"));
		assertTrue(A.hasAttribute("ca", HashMap.class));
		assertFalse(A.hasLabel("ca"));
		assertFalse(A.hasNumber("ca"));
		assertFalse(A.hasVector("ca"));
		assertFalse(A.hasArray("ca"));
		assertTrue(A.hasMap("ca"));
		assertNotNull(A.getAttribute("ca"));
		assertEquals(attr, A.getAttribute("ca"));
		assertEquals(attr, A.getMap("ca"));

		// Clear

		A.clearAttributes();

		assertEquals(0, A.getAttributeCount());
	}

	@Test
	public void testElementMultiAttributes() {
		Graph graph = new MultiGraph("g1");

		Node A = graph.addNode("A");

		assertEquals("A", A.getId());
		assertEquals(0, A.getAttributeCount());

		// Arrays

		A.setAttribute("array", 0, 1.1, 1.3f, "foo");

		Object expected[] = { 0, 1.1, 1.3f, "foo" };

		assertEquals(1, A.getAttributeCount());
		assertTrue(A.hasAttribute("array"));
		assertTrue(A.hasAttribute("array", Object[].class));
		assertFalse(A.hasLabel("array"));
		assertFalse(A.hasNumber("array"));
		assertFalse(A.hasVector("array"));
		assertTrue(A.hasArray("array"));
		assertFalse(A.hasMap("array"));
		assertArrayEquals(expected, (Object[]) A.getAttribute("array"));
		assertArrayEquals(expected, A.getArray("array"));
		assertNotNull(A.getAttribute("array"));
	}

	@Test
	public void testElementUtilityMethods() {
		Graph graph = new MultiGraph("g1");

		Node A = graph.addNode("A");

		assertEquals("A", A.getId());
		assertEquals(0, A.getAttributeCount());

		// First attribute of.

		A.setAttribute("C", "c");
		A.setAttribute("I", "i");
		A.setAttribute("Z", "z");

		String s = A.getFirstAttributeOf(String.class, "A", "B", "C", "I", "Z");

		assertNotNull(s);
		assertEquals("c", s);

		// First attribute of 2.

		A.setAttribute("J", 1);
		A.setAttribute("X", 2);

		Number n = A.getFirstAttributeOf(Number.class, "A", "B", "C", "I", "J", "X", "Z");

		assertNotNull(n);
		assertEquals(1, n);
	}

	@Test
	public void testElementIterables() {
		Graph graph = new MultiGraph("g1");

		Node A = graph.addNode("A");

		assertEquals("A", A.getId());
		assertEquals(0, A.getAttributeCount());

		// First attribute of.

		A.setAttribute("A", "a");
		A.setAttribute("B", "b");
		A.setAttribute("C", "c");

		assertEquals(3, A.getAttributeCount());

		Set<String> keys = A.attributeKeys().collect(Collectors.toSet());

		assertEquals(3, keys.size());
		assertTrue(keys.contains("A"));
		assertTrue(keys.contains("B"));
		assertTrue(keys.contains("C"));
	}

	@Test
	public void testNullAttributes() {
		Graph graph = new MultiGraph("g1");

		graph.setAttribute("foo");
		graph.setAttribute("bar", (Object) null); // Yes an attribute with a
													// null value, You can !

		assertTrue(graph.hasAttribute("foo"));
		assertTrue(graph.hasAttribute("bar"));

		graph.removeAttribute("foo");
		graph.removeAttribute("bar");

		assertFalse(graph.hasAttribute("foo"));
		assertFalse(graph.hasAttribute("bar"));
	}
}