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
 * @author Antoine Dutot <antoine.dutot@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 * @author Yoann Pigné <yoann.pigne@graphstream-project.org>
 */
package org.graphstream.ui.graphicGraph.test;

import org.graphstream.ui.graphicGraph.stylesheet.Color;
import java.util.HashSet;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.graphicGraph.*;
import org.graphstream.ui.graphicGraph.stylesheet.*;
import org.graphstream.ui.spriteManager.*;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test the graphic graph some parts of the style sheet, the graphic elements
 * (including the graphic sprite) and the sprite manager and sprite classes.
 */
public class TestGraphicGraph {
	// Attribute

	/**
	 * a graph that can server as input to send events to the graphic graph.
	 */
	protected Graph inGraph;

	/**
	 * The graphic graph to test.
	 */
	protected GraphicGraph outGraph;

	// Tests

	@Test
	public void basicTest() {
		// Test the class alone.

		outGraph = new GraphicGraph("GraphicGraph");

		// The usual triangle test.

		outGraph.addNode("A");
		outGraph.addNode("B");
		outGraph.addNode("C");
		outGraph.addEdge("AB", "A", "B", false);
		outGraph.addEdge("BC", "B", "C", true);
		outGraph.addEdge("CA", "C", "A", false);

		assertEquals(3, outGraph.getNodeCount());
		assertEquals(3, outGraph.getEdgeCount());
		assertEquals(0, outGraph.getSpriteCount());

		assertFalse(outGraph.getEdge("AB").isDirected());
		assertTrue(outGraph.getEdge("BC").isDirected());
		assertFalse(outGraph.getEdge("CA").isDirected());

		// Test the case of multi-graphs.

		outGraph.addEdge("AB2", "A", "B", true);

		assertEquals(4, outGraph.getEdgeCount());
		assertFalse(outGraph.getEdge("AB").isDirected());
		assertTrue(outGraph.getEdge("AB2").isDirected());

		outGraph.addEdge("CA2", "C", "A");
		outGraph.removeEdge("CA");

		assertEquals(4, outGraph.getEdgeCount());
		assertEquals(null, outGraph.getEdge("CA"));
		assertTrue(outGraph.getEdge("CA2") != null);

		outGraph.removeNode("C");

		assertEquals(2, outGraph.getNodeCount());
		assertEquals(2, outGraph.getEdgeCount());
		assertEquals(null, outGraph.getNode("C"));
		assertEquals(null, outGraph.getEdge("BC"));
		assertEquals(null, outGraph.getEdge("CA"));

		outGraph.removeNode("A");

		assertEquals(1, outGraph.getNodeCount());
		assertEquals(0, outGraph.getEdgeCount());
		assertEquals(null, outGraph.getNode("A"));
		assertEquals(null, outGraph.getEdge("AB"));
		assertEquals(null, outGraph.getEdge("AB2"));

		// And finally...

		outGraph.clear();

		assertEquals(0, outGraph.getNodeCount());
		assertEquals(0, outGraph.getEdgeCount());
		assertEquals(0, outGraph.getSpriteCount());
	}

	protected static String styleSheet1 = "graph  { fill-color: black; }" + "node   { fill-color: white; }"
			+ "edge   { fill-color: white; }" + "node#A { fill-color: red;   }" + "node#B { fill-color: blue;  }";

	@Test
	public void testStyleSheetLoading() {
		// Test the style sheet loading capabilities of the graphic graph.
		outGraph = new GraphicGraph("GraphicGraph");

		outGraph.addNode("A");
		outGraph.addNode("B");
		outGraph.addNode("C");
		outGraph.addEdge("AB", "A", "B");
		outGraph.addEdge("BC", "B", "C");
		outGraph.addEdge("CA", "C", "A");

		// Look at the default style sheet.

		assertNotNull(outGraph.getStyle());
		assertNotNull(((GraphicNode) outGraph.getNode("A")).getStyle());
		assertNotNull(((GraphicNode) outGraph.getNode("B")).getStyle());
		assertNotNull(((GraphicNode) outGraph.getNode("C")).getStyle());

		testStyle(outGraph.getStyle(), Color.WHITE);
		testStyle(((GraphicNode) outGraph.getNode("A")).getStyle(), Color.BLACK);
		testStyle(((GraphicNode) outGraph.getNode("B")).getStyle(), Color.BLACK);
		testStyle(((GraphicNode) outGraph.getNode("C")).getStyle(), Color.BLACK);

		// Load a style sheet by URL.

		outGraph.setAttribute("stylesheet", styleSheet1);

		assertNotNull(outGraph.getStyle());
		assertNotNull(((GraphicNode) outGraph.getNode("A")).getStyle());
		assertNotNull(((GraphicNode) outGraph.getNode("B")).getStyle());
		assertNotNull(((GraphicNode) outGraph.getNode("C")).getStyle());

		testStyle(outGraph.getStyle(), Color.BLACK);
		testStyle(((GraphicNode) outGraph.getNode("A")).getStyle(), Color.RED);
		testStyle(((GraphicNode) outGraph.getNode("B")).getStyle(), Color.BLUE);
		testStyle(((GraphicNode) outGraph.getNode("C")).getStyle(), Color.WHITE);

		// Cascade a style sheet by string.

		outGraph.setAttribute("stylesheet", "node#A { fill-color: green; }");

		assertNotNull(outGraph.getStyle());
		assertNotNull(((GraphicNode) outGraph.getNode("A")).getStyle());
		assertNotNull(((GraphicNode) outGraph.getNode("B")).getStyle());
		assertNotNull(((GraphicNode) outGraph.getNode("C")).getStyle());

		testStyle(outGraph.getStyle(), Color.BLACK);
		testStyle(((GraphicNode) outGraph.getNode("A")).getStyle(), Color.GREEN);
		testStyle(((GraphicNode) outGraph.getNode("B")).getStyle(), Color.BLUE);
		testStyle(((GraphicNode) outGraph.getNode("C")).getStyle(), Color.WHITE);

		// Cascade individual styles on elements.

		outGraph.getNode("A").setAttribute("ui.style", "fill-color: blue;");

		assertNotNull(((GraphicNode) outGraph.getNode("A")).getStyle());
		testStyle(((GraphicNode) outGraph.getNode("A")).getStyle(), Color.BLUE);

		// Clear style.

		outGraph.getStyleSheet().clear();

		assertNotNull(outGraph.getStyle());
		assertNotNull(((GraphicNode) outGraph.getNode("A")).getStyle());
		assertNotNull(((GraphicNode) outGraph.getNode("B")).getStyle());
		assertNotNull(((GraphicNode) outGraph.getNode("C")).getStyle());

		testStyle(outGraph.getStyle(), Color.WHITE);
		testStyle(((GraphicNode) outGraph.getNode("A")).getStyle(), Color.BLACK);
		testStyle(((GraphicNode) outGraph.getNode("B")).getStyle(), Color.BLACK);
		testStyle(((GraphicNode) outGraph.getNode("C")).getStyle(), Color.BLACK);
	}

	protected void testStyle(Style style, Color colorBase) {
		assertTrue(style.getFillColors() != null && style.getFillColors().size() == 1);
		Color color = style.getFillColor(0);
		assertEquals(StyleConstants.FillMode.PLAIN, style.getFillMode());
		assertEquals(StyleConstants.StrokeMode.NONE, style.getStrokeMode());
		assertEquals(colorBase, color);
	}

	@Test
	public void testAsOutput() {
		// Test the GraphicGraph as an output for another graph.
		inGraph = new MultiGraph("inputGraph");
		outGraph = new GraphicGraph("GraphicGraph");

		// Simply put the graphic graph as listener of the input graph.

		inGraph.addSink(outGraph);

		// The usual triangle test : add some nodes and edges.

		inGraph.addNode("A");
		inGraph.addNode("B");
		inGraph.addNode("C");
		inGraph.addEdge("AB", "A", "B", false);
		inGraph.addEdge("BC", "B", "C", true);
		inGraph.addEdge("CA", "C", "A", false);

		// Are they in the output graph ?

		assertEquals(3, outGraph.getNodeCount());
		assertEquals(3, outGraph.getEdgeCount());
		assertEquals(0, outGraph.getSpriteCount());

		assertFalse(outGraph.getEdge("AB").isDirected());
		assertTrue(outGraph.getEdge("BC").isDirected());
		assertFalse(outGraph.getEdge("CA").isDirected());

		// Now try to remove some nodes and edges in the in graph.

		inGraph.removeNode("A"); // This also removes edge "AB" and "CA".
		inGraph.removeEdge("BC");

		// Are they removed from the out graph ?

		assertEquals(2, outGraph.getNodeCount());
		assertEquals(0, outGraph.getEdgeCount());
		assertNull(outGraph.getNode("A"));
		assertNotNull(outGraph.getNode("B"));
		assertNotNull(outGraph.getNode("C"));
		assertNull(outGraph.getEdge("AB"));
		assertNull(outGraph.getEdge("BC"));
		assertNull(outGraph.getEdge("CA"));
	}

	@Test
	public void testAsOutputSprites() {
		inGraph = new MultiGraph("inputGraph");
		outGraph = new GraphicGraph("GraphicGraph");

		inGraph.addSink(outGraph);

		SpriteManager sman = new SpriteManager(inGraph);

		inGraph.addNode("A");
		inGraph.addNode("B");
		inGraph.addNode("C");
		inGraph.addEdge("AB", "A", "B", false);
		inGraph.addEdge("BC", "B", "C", true);
		inGraph.addEdge("CA", "C", "A", false);

		assertEquals(3, outGraph.getNodeCount());
		assertEquals(3, outGraph.getEdgeCount());
		assertEquals(0, outGraph.getSpriteCount());

		// Now test sprites.

		Sprite s1 = sman.addSprite("S1");
		Sprite s2 = sman.addSprite("S2");

		// Test the sprite manager.

		HashSet<String> spriteIds = new HashSet<String>();

		assertTrue(sman.hasSprite("S1"));
		assertTrue(sman.hasSprite("S2"));
		assertEquals(s1, sman.getSprite("S1"));
		assertEquals(s2, sman.getSprite("S2"));
		assertEquals(2, sman.getSpriteCount());

		spriteIds.add("S1");
		spriteIds.add("S2");

		for (Sprite sprite : sman) {
			if (spriteIds.contains(sprite.getId()))
				spriteIds.remove(sprite.getId());
		}

		assertTrue(spriteIds.isEmpty());

		// Test the out graph for corresponding sprites.

		assertEquals(2, outGraph.getSpriteCount());

		spriteIds.add("S1");
		spriteIds.add("S2");

		outGraph.sprites().filter(sprite -> spriteIds.contains(sprite.getId()))
				.forEach(sprite -> spriteIds.remove(sprite.getId()));

		assertTrue(spriteIds.isEmpty());

		// Now remove a sprite.

		sman.removeSprite("S2");

		assertEquals(1, sman.getSpriteCount());
		assertEquals(1, outGraph.getSpriteCount());
		assertNotNull(outGraph.getSprite("S1"));
		assertNull(outGraph.getSprite("S2"));

		// Now test adding attributes to a sprite.
		// Look if they are transfered in the out graph. Only attributes
		// beginning with
		// "ui." are transfered. So we also check that a "foo" attribute does
		// not pass.

		s1.setAttribute("ui.foo", "bar");
		s1.setAttribute("ui.foo1", 1, 2, 3);
		s1.setAttribute("foo", "bar");

		GraphicSprite gs1 = outGraph.getSprite("S1");

		testSprite1(s1);
		testSprite1(gs1);

		assertTrue(s1.hasLabel("foo"));
		assertEquals("bar", s1.getLabel("foo"));
		assertFalse(gs1.hasLabel("foo"));

		// Now removing some attributes to a sprite.

		s1.removeAttribute("ui.foo1");
		s1.removeAttribute("foo");

		assertFalse(s1.hasAttribute("ui.foo1"));
		assertFalse(gs1.hasAttribute("ui.foo1"));
		assertFalse(s1.hasAttribute("foo"));
		assertFalse(gs1.hasAttribute("foo")); // Would not pass the GraphicGraph
												// filter anyway.

		// Position a sprite.

		assertEquals(0, gs1.getX(), 0);
		assertEquals(0, gs1.getY(), 0);
		assertEquals(0, gs1.getZ(), 0);

		s1.setPosition(0.5f);

		assertEquals(0.5f, gs1.getX(), 0);

		s1.setPosition(0.5f, 0.5f, 0.5f);

		assertEquals(0.5f, gs1.getX(), 0);
		assertEquals(0.5f, gs1.getY(), 0);
		assertEquals(0.5f, gs1.getZ(), 0);

		// Now test removing the sprite manager and creating a new one to see if
		// it gets sprites back. We first add some new sprites with attribute,
		// Check all is here in the two graphs, then detach the manager.
		// All sprites should stay in place since we did not removed them
		// explicitly (the manager is only a view on sprites of a graph).

		s2 = sman.addSprite("S2");
		Sprite s3 = sman.addSprite("S3");

		s2.setAttribute("ui.foo", "bar");
		s3.setAttribute("ui.foo", "bar");

		assertEquals(3, sman.getSpriteCount());
		assertEquals(3, outGraph.getSpriteCount());
		assertNotNull(sman.getSprite("S1"));
		assertNotNull(sman.getSprite("S2"));
		assertNotNull(sman.getSprite("S3"));
		assertNotNull(outGraph.getSprite("S1"));
		assertNotNull(outGraph.getSprite("S2"));
		assertNotNull(outGraph.getSprite("S3"));

		sman.detach();

		SpriteManager sman2 = new SpriteManager(inGraph);

		assertEquals(3, sman2.getSpriteCount());
		assertEquals(3, outGraph.getSpriteCount());
		assertNotNull(sman2.getSprite("S1"));
		assertNotNull(sman2.getSprite("S2"));
		assertNotNull(sman2.getSprite("S3"));
		assertNotNull(outGraph.getSprite("S1"));
		assertNotNull(outGraph.getSprite("S2"));
		assertNotNull(outGraph.getSprite("S3"));

		// Now test having two managers at the same time and see if they
		// synchronise.

		SpriteManager sman3 = new SpriteManager(inGraph);

		assertEquals(3, sman3.getSpriteCount());
		assertNotNull(sman3.getSprite("S1"));
		assertNotNull(sman3.getSprite("S2"));
		assertNotNull(sman3.getSprite("S3"));

		// If we add sprites in the graphic graph, the two sprite managers
		// should be
		// synchronised at the same time. We also check the old sprite manager 1
		// we
		// detached is not touched.
		// outGraph.addSink(new org.graphstream.util.VerboseSink(System.out));
		outGraph.addAttributeSink(inGraph);
		outGraph.addSprite("S4");

		assertNotNull(sman2.getSprite("S4"));
		assertNotNull(sman3.getSprite("S4"));
		assertNull(sman.getSprite("S4"));

		// Now test the removal synchronisation.

		outGraph.removeAttributeSink(inGraph); // This is tested in another
												// test.

		sman2.removeSprite("S4");

		assertNull(sman3.getSprite("S4"));
	}

	protected void testSprite1(Element e) {
		Object values[] = { 1, 2, 3 };

		assertTrue(e.hasLabel("ui.foo"));
		assertTrue(e.hasAttribute("ui.foo"));
		assertEquals("bar", e.getLabel("ui.foo"));
		assertEquals("bar", e.getAttribute("ui.foo"));
		assertTrue(e.hasArray("ui.foo1"));
		assertTrue(e.hasAttribute("ui.foo1"));
		assertArrayEquals(values, e.getArray("ui.foo1"));
	}

	@Test
	public void testAsPipe() {
		// Now test the graphic graph as a pipe loop.
		// This allows to synchronise two graphs.
		inGraph = new MultiGraph("input graph");
		outGraph = new GraphicGraph("GraphicGraph");

		inGraph.addSink(outGraph);
		outGraph.addSink(inGraph); // You can do this !! We are careful to
									// recursive calls !!!

		// Add a nodes in one graph and check they are in the other.

		inGraph.addNode("A");
		outGraph.addNode("B");

		assertNotNull(outGraph.getNode("A"));
		assertNotNull(inGraph.getNode("B"));

		// Do the same for other nodes and edges, the usual triangle graph.

		inGraph.addNode("C");
		outGraph.addEdge("AB", "A", "B");
		inGraph.addEdge("BC", "B", "C");
		outGraph.addEdge("CA", "C", "A");

		assertNotNull(outGraph.getNode("C"));
		assertNotNull(inGraph.getEdge("AB"));
		assertNotNull(outGraph.getEdge("BC"));
		assertNotNull(inGraph.getEdge("CA"));

		// Now test the attributes.
		// For this to work, we have to use attributes prefixed by "ui." since
		// only these
		// will pass toward the graphic graph.

		inGraph.setAttribute("ui.foo", "bar");
		outGraph.setAttribute("ui.bar", "foo");
		inGraph.getNode("A").setAttribute("ui.foo", "bar");
		outGraph.getNode("A").setAttribute("ui.bar", "foo");
		inGraph.getEdge("AB").setAttribute("ui.foo", "bar");
		outGraph.getEdge("AB").setAttribute("ui.bar", "foo");

		assertEquals("bar", outGraph.getAttribute("ui.foo"));
		assertEquals("foo", inGraph.getAttribute("ui.bar"));
		assertEquals("bar", outGraph.getNode("A").getAttribute("ui.foo"));
		assertEquals("foo", inGraph.getNode("A").getAttribute("ui.bar"));
		assertEquals("bar", outGraph.getEdge("AB").getAttribute("ui.foo"));
		assertEquals("foo", inGraph.getEdge("AB").getAttribute("ui.bar"));

		// Now test the sprites as they are quite special attributes.

		SpriteManager sman = new SpriteManager(inGraph);

		Sprite s1 = sman.addSprite("S1");
		GraphicSprite gs1 = outGraph.getSprite("S1");

		assertNotNull(gs1);

		s1.setAttribute("ui.foo", "bar");
		gs1.setAttribute("ui.bar", "foo");

		assertEquals("bar", gs1.getAttribute("ui.foo"));
		assertEquals("foo", s1.getAttribute("ui.bar"));

		s1.removeAttribute("ui.foo");
		gs1.removeAttribute("ui.bar");

		assertNull(gs1.getAttribute("ui.foo"));
		assertNull(s1.getAttribute("ui.bar"));

		// Test creating sprites in the graphic graph and retrieving them
		// in the sprite manager.

		GraphicSprite gs2 = outGraph.addSprite("S2");
		Sprite s2 = sman.getSprite("S2");

		assertNotNull(s2);

		gs2.setAttribute("ui.foo", "bar");
		s2.setAttribute("ui.bar", "foo");

		assertEquals("bar", s2.getAttribute("ui.foo"));
		assertEquals("foo", gs2.getAttribute("ui.bar"));

		gs2.removeAttribute("ui.foo");
		s2.removeAttribute("ui.bar");

		assertNull(s2.getAttribute("ui.foo"));
		assertNull(gs2.getAttribute("ui.bar"));

		outGraph.removeSprite("S2");

		assertNull(sman.getSprite("S2"));
	}
}