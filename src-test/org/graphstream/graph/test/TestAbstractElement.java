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
 * @since 2017-03-07
 * 
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.graph.test;

import org.graphstream.graph.implementations.AbstractElement;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * @since 04/03/17.
 */
public class TestAbstractElement {
	@Test
	public void testGetId() {
		final String id = String.format("test-%x", System.currentTimeMillis());

		AbstractElement e = new LocalAbstractElement(id);
		assertEquals(id, e.getId());
	}

	@Test
	public void testGetIndex() {
		AbstractElement e = new LocalAbstractElement(1337);
		assertEquals(1337, e.getIndex());
	}

	@Test
	public void testGetAttribute() {
		AbstractElement e = new LocalAbstractElement("test");
		Object o = new Object();

		e.setAttribute("A", o);
		e.setAttribute("C", "TEST");
		e.setAttribute("D");
		e.setAttribute("E", (Object) null);

		assertSame(o, e.getAttribute("A"));
		assertNull(e.getAttribute("B"));
		assertNull(e.getAttribute("A", String.class));
		assertEquals("TEST", e.getAttribute("C", String.class));
		assertEquals(true, e.getAttribute("D"));
		assertNull(e.getAttribute("E"));
	}

	@Test
	public void testGetFirstAttributeOf() {
		AbstractElement e = new LocalAbstractElement("test");

		e.setAttribute("A", "A");
		e.setAttribute("C", "C");
		e.setAttribute("D", 13.37);

		assertEquals("C", e.getFirstAttributeOf("B", "C", "A"));
		assertEquals(13.37, e.getFirstAttributeOf(Number.class, "B", "C", "D", "A"));
	}

	@Test
	public void testGetLabel() {
		AbstractElement e = new LocalAbstractElement("test");

		e.setAttribute("A", "A");
		e.setAttribute("B", 13.37);

		assertEquals("A", e.getLabel("A"));
		assertNull(e.getLabel("B"));
		assertNull(e.getLabel("C"));
	}

	@Test
	public void testGetNumber() {
		AbstractElement e = new LocalAbstractElement("test");

		e.setAttribute("A", "A");
		e.setAttribute("B", 13.37);

		assertEquals(13.37, e.getNumber("B"), 0);
		assertTrue(Double.isNaN(e.getNumber("A")));
		assertTrue(Double.isNaN(e.getNumber("C")));
	}

	@Test
	public void testGetVector() {
		AbstractElement e = new LocalAbstractElement("test");
		List<Number> vec1 = new LinkedList<>();
		List<Number> vec2 = new ArrayList<>();
		List<Number> vec3 = new Vector<>();
		List<Object> vec4 = new Vector<>();
		List<Object> vec5 = new Vector<>();

		for (int i = 0; i < 10; i++) {
			vec1.add(Math.random());
			vec2.add((int) (Math.random() * 100));
			vec3.add(Math.random());
			vec4.add(new Object());
		}

		e.setAttribute("vec1", vec1);
		e.setAttribute("vec2", vec2);
		e.setAttribute("vec3", vec3);
		e.setAttribute("vec4", vec4);
		e.setAttribute("vec5", vec5);
		e.setAttribute("vec6", new Object());

		assertNotNull(e.getVector("vec1"));
		assertNotNull(e.getVector("vec2"));
		assertNotNull(e.getVector("vec3"));
		assertNull(e.getVector("vec4"));
		assertNull(e.getVector("vec5"));
		assertNull(e.getVector("vec6"));
		assertNull(e.getVector("vec7"));

		assertEquals(vec1, e.getVector("vec1"));
		assertEquals(vec2, e.getVector("vec2"));
		assertEquals(vec3, e.getVector("vec3"));
	}

	@Test
	public void testGetArray() {
		AbstractElement e = new LocalAbstractElement("test");

		e.setAttribute("array1", 1, 2, 3);
		e.setAttribute("array2", (Object) new Integer[] { 1, 2, 3 });
		e.setAttribute("array3", (Object) new Integer[] {});
		e.setAttribute("attr1", new Object());

		assertNotNull(e.getArray("array1"));
		assertNotNull(e.getArray("array2"));
		assertNotNull(e.getArray("array3"));
		assertNull(e.getArray("attr1"));

		assertArrayEquals(new Integer[] { 1, 2, 3 }, e.getArray("array1"));
		assertArrayEquals(new Integer[] { 1, 2, 3 }, e.getArray("array2"));
		assertArrayEquals(new Integer[] {}, e.getArray("array3"));
	}

	@Test
	public void testGetMap() {
		AbstractElement e = new LocalAbstractElement("test");
		Map<String, Integer> map = new HashMap<>();

		map.put("T", 1);
		map.put("E", 2);
		map.put("S", 3);
		map.put("T", 4);

		e.setAttribute("map", map);

		assertTrue(e.hasMap("map"));
		assertEquals(map, e.getAttribute("map"));
	}

	@Test
	public void testHasAttribute() {
		AbstractElement e = new LocalAbstractElement("test");

		e.setAttribute("A", new Object());
		e.setAttribute("B", new Object());

		assertTrue(e.hasAttribute("A"));
		assertTrue(e.hasAttribute("B"));
		assertFalse(e.hasAttribute("C"));

		e.setAttribute("C", e);

		assertTrue(e.hasAttribute("C"));
		assertTrue(e.hasAttribute("C", LocalAbstractElement.class));
		assertFalse(e.hasAttribute("C", String.class));
	}

	@Test
	public void testHasLabel() {
		AbstractElement e = new LocalAbstractElement("test");

		e.setAttribute("A", new Object());
		e.setAttribute("B", "LABEL");

		assertTrue(e.hasLabel("B"));
		assertFalse(e.hasLabel("A"));
		assertFalse(e.hasLabel("C"));
	}

	@Test
	public void testHasNumber() {
		AbstractElement e = new LocalAbstractElement("test");

		e.setAttribute("A", new Object());
		e.setAttribute("B", 13.37);
		e.setAttribute("C", Double.valueOf(13.37));
		e.setAttribute("D", 1337);
		e.setAttribute("E", Integer.valueOf(1337));
		e.setAttribute("G", "13.37");
		e.setAttribute("H", "AD3.37");

		assertFalse(e.hasNumber("A"));
		assertTrue(e.hasNumber("B"));
		assertTrue(e.hasNumber("C"));
		assertTrue(e.hasNumber("D"));
		assertTrue(e.hasNumber("E"));
		assertFalse(e.hasNumber("F"));
		assertTrue(e.hasNumber("G"));
		assertFalse(e.hasNumber("H"));
	}

	@Test
	public void testHasVector() {
		AbstractElement e = new LocalAbstractElement("test");
		List<Number> vec1 = new LinkedList<>();
		List<Number> vec2 = new ArrayList<>();
		List<Number> vec3 = new Vector<>();
		List<Object> vec4 = new Vector<>();
		List<Object> vec5 = new Vector<>();

		for (int i = 0; i < 10; i++) {
			vec1.add(Math.random());
			vec2.add((int) (Math.random() * 100));
			vec3.add(Math.random());
			vec4.add(new Object());
		}

		e.setAttribute("vec1", vec1);
		e.setAttribute("vec2", vec2);
		e.setAttribute("vec3", vec3);
		e.setAttribute("vec4", vec4);
		e.setAttribute("vec5", vec5);
		e.setAttribute("vec6", new Object());

		assertTrue(e.hasVector("vec1"));
		assertTrue(e.hasVector("vec2"));
		assertTrue(e.hasVector("vec3"));
		assertFalse(e.hasVector("vec4"));
		assertFalse(e.hasVector("vec5"));
		assertFalse(e.hasVector("vec6"));
		assertFalse(e.hasVector("vec7"));
	}

	@Test
	public void testHasArray() {
		AbstractElement e = new LocalAbstractElement("test");

		e.setAttribute("array1", 1, 2, 3);
		e.setAttribute("array2", (Object) new Integer[] { 1, 2, 3 });
		e.setAttribute("array3", (Object) new Integer[] {});
		e.setAttribute("array4", (Object) new int[] { 1, 2, 3 });
		e.setAttribute("attr1", new Object());

		assertTrue(e.hasArray("array1"));
		assertTrue(e.hasArray("array2"));
		assertTrue(e.hasArray("array3"));
		assertFalse(e.hasArray("array4"));
		assertFalse(e.hasArray("attr1"));
		assertFalse(e.hasArray("attr2"));
	}

	@Test
	public void testHasMap() {
		AbstractElement e = new LocalAbstractElement("test");

		e.setAttribute("map", new HashMap<String, Object>());
		e.setAttribute("attr", new Object());

		assertTrue(e.hasMap("map"));
		assertFalse(e.hasMap("attr1"));
		assertFalse(e.hasMap("attr2"));
	}

	@Test
	public void testAttributeKeys() {
		AbstractElement e = new LocalAbstractElement("test");
		final int count = 10 + (int) (Math.random() * 90);
		List<String> keys = new LinkedList<>();

		for (int j = 0; j < count; j++) {
			String key = String.format("attribute%d", j);
			e.setAttribute(key, new Object());
			keys.add(key);
		}

		assertEquals(count, keys.size());

		e.attributeKeys().forEach(keys::remove);

		assertEquals(0, keys.size());
	}

	@Test
	public void testGetAttributeCount() {
		AbstractElement e = new LocalAbstractElement("test");
		final int count = 10 + (int) (Math.random() * 90);

		for (int j = 0; j < count; j++) {
			e.setAttribute(String.format("attribute%d", j), new Object());
		}

		assertEquals(count, e.getAttributeCount());
	}

	@Test
	public void testClearAttributes() {
		AbstractElement e = new LocalAbstractElement("test");
		final int count = 10 + (int) (Math.random() * 90);

		for (int j = 0; j < count; j++) {
			e.setAttribute(String.format("attribute%d", j), new Object());
		}

		e.clearAttributes();
		assertEquals(0, e.getAttributeCount());
	}

	@Test
	public void testAddAttribute() {
		LocalAbstractElement e = new LocalAbstractElement("test");
		Object o = new Object();

		e.setAttribute("A", o);

		assertEquals(1, e.getTheMap().size());
		assertTrue(e.getTheMap().containsKey("A"));
		assertSame(o, e.getTheMap().get("A"));

		e.setAttribute("B");

		assertTrue(e.hasAttribute("B"));
		assertEquals(true, e.getAttribute("B"));

		e.setAttribute("C", (Object) null);

		assertTrue(e.hasAttribute("C"));
		assertNull(e.getAttribute("C"));

		e.setAttribute("D", "T", "E", "S", "T");

		assertTrue(e.hasAttribute("D"));
		assertTrue(e.hasArray("D"));
		assertArrayEquals(new String[] { "T", "E", "S", "T" }, e.getArray("D"));
	}

	@Test
	public void testAddAttributes() {
		Map<String, Object> attributes = new HashMap<>();

		for (int i = 0; i < 100; i++)
			attributes.put(String.format("attr%d", i), new Object());

		AbstractElement e = new LocalAbstractElement("test");
		e.setAttributes(attributes);

		e.attributeKeys().forEach(key -> {
			assertNotNull(attributes.get(key));
			assertEquals(attributes.get(key), e.getAttribute(key));

			attributes.remove(key);
		});

		assertEquals(0, attributes.size());
	}

	@Test
	public void testRemoveAttribute() {
		LocalAbstractElement e = new LocalAbstractElement("test");
		Object o = new Object();

		e.setAttribute("A", o);
		assertTrue(e.getTheMap() != null && e.getTheMap().containsKey("A"));

		e.removeAttribute("A");
		assertFalse(e.getTheMap() != null && e.getTheMap().containsKey("A"));
	}

	protected static class LocalAbstractElement extends AbstractElement {
		LocalAbstractElement(int index) {
			this(Integer.toString(index));
			setIndex(index);
		}

		LocalAbstractElement(String id) {
			super(id);
		}

		@Override
		protected void attributeChanged(AttributeChangeEvent event, String attribute, Object oldValue,
				Object newValue) {

		}

		Map<String, Object> getTheMap() {
			return this.attributes;
		}
	}
}
