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
 * @author Tim Wundke <gtwundke@gmail.com>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.ui.graphicGraph.parser.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.ui.graphicGraph.StyleGroup;
import org.graphstream.ui.graphicGraph.StyleGroupSet;
import org.graphstream.ui.graphicGraph.StyleGroup.ElementEvents;
import org.graphstream.ui.graphicGraph.stylesheet.Rule;
import org.graphstream.ui.graphicGraph.stylesheet.Style;
import org.graphstream.ui.graphicGraph.stylesheet.StyleSheet;
import org.junit.*;
import static org.junit.Assert.*;

/**
 * Run several tests on the style sheet package.
 *
 * @author Antoine Dutot
 */
public class TestStyleSheet {
	public static void main(String args[]) {
		// Instead of JUnit, we can use this main() :

		TestStyleSheet tss = new TestStyleSheet();
		tss.setUp();
		tss.testInitialParsing();
		tss.testRuleQuery();
		tss.testStyleGroups();
		tss.testStyleEvents();
	}

	Graph graph;
	Node A, B, C, D;
	Edge AB, BC, CD, DA;
	StyleSheet stylesheet;

	@Before
	public void setUp() {
		graph = new DefaultGraph("g1");

		A = graph.addNode("A");
		B = graph.addNode("B");
		C = graph.addNode("C");
		D = graph.addNode("D");
		AB = graph.addEdge("AB", "A", "B");
		BC = graph.addEdge("BC", "B", "C");
		CD = graph.addEdge("CD", "C", "D");
		DA = graph.addEdge("DA", "D", "A");

		B.setAttribute("ui.class", "foo");
		C.setAttribute("ui.class", "foo");
		D.setAttribute("ui.class", "bar", "foo");

		AB.setAttribute("ui.class", "foo");
		BC.setAttribute("ui.class", "foo");

		// B (foo)
		// _/ \_
		// _/ \_(foo)
		// / (foo) \
		// A C (foo)
		// \_ _/
		// \_ _/
		// \ /
		// D (bar,foo)

		stylesheet = new StyleSheet();

		// The main style sheet, other style sheets are "cascaded" in addition
		// of this one.

		try {
			stylesheet.parseFromString(styleSheet1);
		} catch (IOException e) {
		}
	}

	public static String styleSheet1 = "graph            { fill-color: white,black; }"
			+ "node             { fill-color: blue;        }" + "edge             { fill-color: green;       }"
			+ "sprite           { fill-color: cyan;        }" + "node#A           { fill-color: magenta;     }"
			+ "edge#AB          { fill-color: yellow;      }" + "node.foo         { fill-color: orange;      }"
			+ "node.bar         { fill-color: grey;        }" + "node:clicked     { stroke-width: 1px;       }"
			+ "node#A:clicked   { stroke-width: 2px;       }" + "node.foo:clicked { stroke-width: 3px;       }"
			+ "node#A:selected  { stroke-width: 4px;       }";

	@Test
	public void testInitialParsing() {
		assertEquals(0, stylesheet.getGraphStyleNameSpace().getIdRulesCount());
		assertEquals(0, stylesheet.getGraphStyleNameSpace().getClassRulesCount());

		assertEquals(1, stylesheet.getNodeStyleNameSpace().getIdRulesCount());
		assertEquals(2, stylesheet.getNodeStyleNameSpace().getClassRulesCount());

		assertEquals(1, stylesheet.getEdgeStyleNameSpace().getIdRulesCount());
		assertEquals(0, stylesheet.getEdgeStyleNameSpace().getClassRulesCount());

		assertEquals(0, stylesheet.getSpriteStyleNameSpace().getIdRulesCount());
		assertEquals(0, stylesheet.getSpriteStyleNameSpace().getClassRulesCount());
	}

	@Test
	public void testRuleQuery() {
		ArrayList<Rule> rulesA = stylesheet.getRulesFor(A);
		String idA = stylesheet.getStyleGroupIdFor(A, rulesA);
		ArrayList<Rule> rulesB = stylesheet.getRulesFor(B);
		String idB = stylesheet.getStyleGroupIdFor(B, rulesB);
		ArrayList<Rule> rulesC = stylesheet.getRulesFor(C);
		String idC = stylesheet.getStyleGroupIdFor(C, rulesC);
		ArrayList<Rule> rulesD = stylesheet.getRulesFor(D);
		String idD = stylesheet.getStyleGroupIdFor(D, rulesD);

		ArrayList<Rule> rulesAB = stylesheet.getRulesFor(AB);
		String idAB = stylesheet.getStyleGroupIdFor(AB, rulesAB);
		ArrayList<Rule> rulesBC = stylesheet.getRulesFor(BC);
		String idBC = stylesheet.getStyleGroupIdFor(BC, rulesBC);
		ArrayList<Rule> rulesCD = stylesheet.getRulesFor(CD);
		String idCD = stylesheet.getStyleGroupIdFor(CD, rulesCD);
		ArrayList<Rule> rulesDA = stylesheet.getRulesFor(DA);
		String idDA = stylesheet.getStyleGroupIdFor(DA, rulesDA);

		assertTrue(idA.equals("n_A"));
		assertTrue(idB.equals("n(foo)"));
		assertTrue(idC.equals("n(foo)"));
		assertTrue(idD.equals("n(bar,foo)"));
		assertTrue(idAB.equals("e_AB"));
		assertTrue(idBC.equals("e"));
		assertTrue(idCD.equals("e"));
		assertTrue(idDA.equals("e"));

		System.err.printf("----%n");
		System.err.printf("A %s%n", displayGroup(idA, rulesA));
		System.err.printf("B %s%n", displayGroup(idB, rulesB));
		System.err.printf("C %s%n", displayGroup(idC, rulesC));
		System.err.printf("D %s%n", displayGroup(idD, rulesD));
		System.err.printf("----%n");
		System.err.printf("AB %s%n", displayGroup(idAB, rulesAB));
		System.err.printf("BC %s%n", displayGroup(idBC, rulesBC));
		System.err.printf("CD %s%n", displayGroup(idCD, rulesCD));
		System.err.printf("DA %s%n", displayGroup(idDA, rulesDA));
	}

	protected void populateGroupSet(StyleGroupSet sgs) {
		sgs.addElement(graph);
		sgs.addElement(A);
		sgs.addElement(B);
		sgs.addElement(C);
		sgs.addElement(D);
		sgs.addElement(AB);
		sgs.addElement(BC);
		sgs.addElement(CD);
		sgs.addElement(DA);
	}

	@Test
	public void testStyleGroups() {
		StyleGroupSet sgs = new StyleGroupSet(stylesheet);

		populateGroupSet(sgs);

		System.err.printf("There are %d groups !!%n", sgs.getGroupCount());
		Iterator<? extends StyleGroup> i = sgs.getGroupIterator();
		while (i.hasNext())
			System.err.printf("  %s", i.next().toString());

		assertTrue(sgs.getGroupCount() == 6);

		System.err.printf("----%n");
		System.err.printf(sgs.toString());

		Style sG = sgs.getStyleForElement(graph);
		Style sA = sgs.getStyleForElement(A);
		Style sB = sgs.getStyleForElement(B);
		Style sC = sgs.getStyleForElement(C);
		Style sD = sgs.getStyleForElement(D);

		Style sAB = sgs.getStyleForElement(AB);
		Style sBC = sgs.getStyleForElement(BC);
		Style sCD = sgs.getStyleForElement(CD);
		Style sDA = sgs.getStyleForElement(DA);

		assertEquals(2, sG.getFillColorCount());
		assertEquals(1, sA.getFillColorCount());
		assertEquals(1, sB.getFillColorCount());
		assertEquals(1, sC.getFillColorCount());
		assertEquals(1, sD.getFillColorCount());
		assertTrue(sG.getFillColor(0).getRed() == 255 && sG.getFillColor(0).getGreen() == 255
				&& sG.getFillColor(0).getBlue() == 255);
		assertTrue(sG.getFillColor(1).getRed() == 0 && sG.getFillColor(1).getGreen() == 0
				&& sG.getFillColor(1).getBlue() == 0);
		assertTrue(sA.getFillColor(0).getRed() == 255 && sA.getFillColor(0).getGreen() == 0
				&& sA.getFillColor(0).getBlue() == 255);
		assertTrue(sB.getFillColor(0).getRed() == 255 && sB.getFillColor(0).getGreen() == 165
				&& sB.getFillColor(0).getBlue() == 0);
		assertTrue(sC.getFillColor(0).getRed() == 255 && sC.getFillColor(0).getGreen() == 165
				&& sC.getFillColor(0).getBlue() == 0);
		assertTrue(sD.getFillColor(0).getRed() == 190 && sD.getFillColor(0).getGreen() == 190
				&& sD.getFillColor(0).getBlue() == 190);

		assertEquals(1, sA.getStrokeWidth().value, 0);
		assertEquals(1, sB.getStrokeWidth().value, 0);
		assertEquals(1, sC.getStrokeWidth().value, 0);
		assertEquals(1, sD.getStrokeWidth().value, 0);

		assertTrue(sAB.getFillColor(0).getRed() == 255 && sAB.getFillColor(0).getGreen() == 255
				&& sAB.getFillColor(0).getBlue() == 0);
		assertTrue(sBC.getFillColor(0).getRed() == 0 && sBC.getFillColor(0).getGreen() == 255
				&& sBC.getFillColor(0).getBlue() == 0);
		assertTrue(sCD.getFillColor(0).getRed() == 0 && sCD.getFillColor(0).getGreen() == 255
				&& sCD.getFillColor(0).getBlue() == 0);
		assertTrue(sDA.getFillColor(0).getRed() == 0 && sDA.getFillColor(0).getGreen() == 255
				&& sDA.getFillColor(0).getBlue() == 0);

		sgs.release();
	}

	protected String displayGroup(String id, ArrayList<Rule> rules) {
		StringBuilder builder = new StringBuilder();

		builder.append("(");
		builder.append(id);
		builder.append(") ");
		builder.append(rules.size());
		builder.append(" [ ");
		for (int i = 0; i < rules.size(); ++i) {
			builder.append(rules.get(i).selector.toString());
			builder.append(" ");
		}
		builder.append("]");
		return builder.toString();
	}

	@Test
	public void testStyleEvents() {
		StyleGroupSet sgs = new StyleGroupSet(stylesheet);

		populateGroupSet(sgs);

		StyleGroup sA = sgs.getStyleForElement(A);
		StyleGroup sB = sgs.getStyleForElement(B);
		StyleGroup sC = sgs.getStyleForElement(C);
		StyleGroup sD = sgs.getStyleForElement(D);

		assertEquals(1, sA.getStrokeWidth().value, 0);
		assertEquals(1, sB.getStrokeWidth().value, 0);
		assertEquals(1, sC.getStrokeWidth().value, 0);
		assertEquals(1, sD.getStrokeWidth().value, 0);

		// Test global events (events that apply to a whole group or groups).

		sgs.pushEvent("clicked"); // This is normally done automatically by the
									// GraphicElement

		sA = sgs.getStyleForElement(A);
		sB = sgs.getStyleForElement(B);
		sC = sgs.getStyleForElement(C);
		sD = sgs.getStyleForElement(D);

		assertEquals(2, sA.getStrokeWidth().value, 0);
		assertEquals(3, sB.getStrokeWidth().value, 0);
		assertEquals(3, sC.getStrokeWidth().value, 0);
		assertEquals(3, sD.getStrokeWidth().value, 0);

		// Ensure that inherited styles are still correct
		assertTrue(sA.getFillColor(0).getRed() == 255 && sA.getFillColor(0).getGreen() == 0
				&& sA.getFillColor(0).getBlue() == 255);
		assertTrue(sB.getFillColor(0).getRed() == 255 && sB.getFillColor(0).getGreen() == 165
				&& sB.getFillColor(0).getBlue() == 0);
		assertTrue(sC.getFillColor(0).getRed() == 255 && sC.getFillColor(0).getGreen() == 165
				&& sC.getFillColor(0).getBlue() == 0);
		assertTrue(sD.getFillColor(0).getRed() == 190 && sD.getFillColor(0).getGreen() == 190
				&& sD.getFillColor(0).getBlue() == 190);

		sgs.popEvent("clicked"); // This is normally done automatically by the
									// GraphicElement

		sA = sgs.getStyleForElement(A);
		sB = sgs.getStyleForElement(B);
		sC = sgs.getStyleForElement(C);
		sD = sgs.getStyleForElement(D);

		assertEquals(1, sA.getStrokeWidth().value, 0);
		assertEquals(1, sB.getStrokeWidth().value, 0);
		assertEquals(1, sC.getStrokeWidth().value, 0);
		assertEquals(1, sD.getStrokeWidth().value, 0);

		sgs.pushEvent("clicked"); // Both events at a time.
		sgs.pushEvent("selected"); // They should cascade.

		sA = sgs.getStyleForElement(A);
		sB = sgs.getStyleForElement(B);
		sC = sgs.getStyleForElement(C);
		sD = sgs.getStyleForElement(D);

		assertEquals(4, sA.getStrokeWidth().value, 0);
		assertEquals(3, sB.getStrokeWidth().value, 0);
		assertEquals(3, sC.getStrokeWidth().value, 0);
		assertEquals(3, sD.getStrokeWidth().value, 0);

		// Ensure that inherited styles are still correct
		assertTrue(sA.getFillColor(0).getRed() == 255 && sA.getFillColor(0).getGreen() == 0
				&& sA.getFillColor(0).getBlue() == 255);
		assertTrue(sB.getFillColor(0).getRed() == 255 && sB.getFillColor(0).getGreen() == 165
				&& sB.getFillColor(0).getBlue() == 0);
		assertTrue(sC.getFillColor(0).getRed() == 255 && sC.getFillColor(0).getGreen() == 165
				&& sC.getFillColor(0).getBlue() == 0);
		assertTrue(sD.getFillColor(0).getRed() == 190 && sD.getFillColor(0).getGreen() == 190
				&& sD.getFillColor(0).getBlue() == 190);

		sgs.popEvent("clicked"); // This is normally done automatically by the
									// GraphicElement
		sgs.popEvent("selected"); // This is normally done automatically by the
									// GraphicElement

		// Now test individual events, that is events that apply to
		// an individual element only.

		sA = sgs.getStyleForElement(A);

		assertFalse(sA.hasEventElements());

		sgs.pushEventFor(A, "clicked"); // This is normally done automatically
										// by the GraphicElement
		sgs.pushEventFor(B, "clicked"); // This is normally done automatically
										// by the GraphicElement

		sA = sgs.getStyleForElement(A);
		sB = sgs.getStyleForElement(B);
		sC = sgs.getStyleForElement(C);
		sD = sgs.getStyleForElement(D);

		assertTrue(sA.hasEventElements());

		assertEquals(1, sA.getStrokeWidth().value, 0); // Individual events must be
														// activated
		assertEquals(1, sB.getStrokeWidth().value, 0); // to work, so just pushing
														// them is not
		assertEquals(1, sC.getStrokeWidth().value, 0); // sufficient.
		assertEquals(1, sD.getStrokeWidth().value, 0);

		sA.activateEventsFor(A);
		assertEquals(2, sA.getStrokeWidth().value, 0); // Only A should change.
		assertEquals(1, sB.getStrokeWidth().value, 0);
		assertEquals(1, sC.getStrokeWidth().value, 0);
		assertEquals(1, sD.getStrokeWidth().value, 0);
		sA.deactivateEvents();
		sB.activateEventsFor(B);
		assertEquals(1, sA.getStrokeWidth().value, 0);
		assertEquals(3, sB.getStrokeWidth().value, 0); // B and all its group
														// change.
		assertEquals(3, sC.getStrokeWidth().value, 0); // Therefore C also changes.
		assertEquals(1, sD.getStrokeWidth().value, 0);
		sB.deactivateEvents();

		sgs.popEventFor(A, "clicked"); // This is normally done automatically by
										// the GraphicElement
		sgs.popEventFor(B, "clicked"); // This is normally done automatically by
										// the GraphicElement

		// Now two individual events at a time.

		sgs.pushEventFor(A, "clicked"); // This is normally done automatically
										// by the GraphicElement
		sgs.pushEventFor(A, "selected"); // This is normally done automatically
											// by the GraphicElement

		sA = sgs.getStyleForElement(A);
		sB = sgs.getStyleForElement(B);
		sC = sgs.getStyleForElement(C);
		sD = sgs.getStyleForElement(D);

		assertEquals(1, sA.getStrokeWidth().value, 0); // Individual events must be
														// activated
		assertEquals(1, sB.getStrokeWidth().value, 0); // to work, so just pushing
														// them is not
		assertEquals(1, sC.getStrokeWidth().value, 0); // sufficient.
		assertEquals(1, sD.getStrokeWidth().value, 0);

		sA.activateEventsFor(A);
		assertEquals(4, sA.getStrokeWidth().value, 0); // Only A should change,
														// "selected" has
		assertEquals(1, sB.getStrokeWidth().value, 0); // precedence over "clicked"
														// since added
		assertEquals(1, sC.getStrokeWidth().value, 0); // after.
		assertEquals(1, sD.getStrokeWidth().value, 0);
		sA.deactivateEvents();

		sgs.popEventFor(A, "clicked"); // This is normally done automatically by
										// the GraphicElement
		sgs.popEventFor(A, "selected"); // This is normally done automatically
										// by the GraphicElement

		sgs.release();
	}

	public static String styleSheet2 = "node { fill-color: yellow; stroke-width: 10px; }";

	public static String styleSheet3 = "node#B { stroke-width: 5px; }";

	public static String styleSheet4 = "edge.foo { stroke-width: 2px; }";

	@Test
	public void testStyleChange() throws IOException {
		StyleGroupSet sgs = new StyleGroupSet(stylesheet);

		populateGroupSet(sgs);

		assertTrue(sgs.getGroupCount() == 6);

		// Augment the style sheet a new style sheet that change an existing
		// style.

		stylesheet.parseFromString(styleSheet2);

		assertEquals(6, sgs.getGroupCount());
		assertEquals(0, stylesheet.getGraphStyleNameSpace().getIdRulesCount());
		assertEquals(0, stylesheet.getGraphStyleNameSpace().getClassRulesCount());
		assertEquals(1, stylesheet.getNodeStyleNameSpace().getIdRulesCount());
		assertEquals(2, stylesheet.getNodeStyleNameSpace().getClassRulesCount());
		assertEquals(1, stylesheet.getEdgeStyleNameSpace().getIdRulesCount());
		assertEquals(0, stylesheet.getEdgeStyleNameSpace().getClassRulesCount());
		assertEquals(0, stylesheet.getSpriteStyleNameSpace().getIdRulesCount());
		assertEquals(0, stylesheet.getSpriteStyleNameSpace().getClassRulesCount());

		// All nodes should have a border of 10px except the clicked ones.

		Style sA = sgs.getStyleForElement(A);
		Style sB = sgs.getStyleForElement(B);
		Style sC = sgs.getStyleForElement(C);
		Style sD = sgs.getStyleForElement(D);

		assertEquals(10, sA.getStrokeWidth().value, 0);
		assertEquals(10, sB.getStrokeWidth().value, 0);
		assertEquals(10, sC.getStrokeWidth().value, 0);
		assertEquals(10, sD.getStrokeWidth().value, 0);

		sgs.pushEvent("clicked");
		sA = sgs.getStyleForElement(A);
		sB = sgs.getStyleForElement(B);
		sC = sgs.getStyleForElement(C);
		sD = sgs.getStyleForElement(D);

		assertEquals(2, sA.getStrokeWidth().value, 0);
		assertEquals(3, sB.getStrokeWidth().value, 0);
		assertEquals(3, sC.getStrokeWidth().value, 0);
		assertEquals(3, sD.getStrokeWidth().value, 0);

		sgs.popEvent("clicked");
		sA = sgs.getStyleForElement(A);
		sB = sgs.getStyleForElement(B);
		sC = sgs.getStyleForElement(C);
		sD = sgs.getStyleForElement(D);

		assertEquals(10, sA.getStrokeWidth().value, 0);
		assertEquals(10, sB.getStrokeWidth().value, 0);
		assertEquals(10, sC.getStrokeWidth().value, 0);
		assertEquals(10, sD.getStrokeWidth().value, 0);

		// Now augment the style sheet with a change that applies only to node
		// B.

		stylesheet.parseFromString(styleSheet3);

		assertEquals(7, sgs.getGroupCount());
		assertEquals(0, stylesheet.getGraphStyleNameSpace().getIdRulesCount());
		assertEquals(0, stylesheet.getGraphStyleNameSpace().getClassRulesCount());
		assertEquals(2, stylesheet.getNodeStyleNameSpace().getIdRulesCount()); // <--
																				// +1
		assertEquals(2, stylesheet.getNodeStyleNameSpace().getClassRulesCount());
		assertEquals(1, stylesheet.getEdgeStyleNameSpace().getIdRulesCount());
		assertEquals(0, stylesheet.getEdgeStyleNameSpace().getClassRulesCount());
		assertEquals(0, stylesheet.getSpriteStyleNameSpace().getIdRulesCount());
		assertEquals(0, stylesheet.getSpriteStyleNameSpace().getClassRulesCount());

		sA = sgs.getStyleForElement(A);
		sB = sgs.getStyleForElement(B);
		sC = sgs.getStyleForElement(C);
		sD = sgs.getStyleForElement(D);

		assertEquals(10, sA.getStrokeWidth().value, 0);
		assertEquals(5, sB.getStrokeWidth().value, 0); // <-- The specific style
														// changed.
		assertEquals(10, sC.getStrokeWidth().value, 0);
		assertEquals(10, sD.getStrokeWidth().value, 0);

		// Now augment the style sheet with a change that applies to all edges
		// with the ".foo" class.

		stylesheet.parseFromString(styleSheet4);

		assertEquals(8, sgs.getGroupCount()); // (e_AB disappears, e_AB(foo) and
												// e(foo) appear)
		assertEquals(0, stylesheet.getGraphStyleNameSpace().getIdRulesCount());
		assertEquals(0, stylesheet.getGraphStyleNameSpace().getClassRulesCount());
		assertEquals(2, stylesheet.getNodeStyleNameSpace().getIdRulesCount());
		assertEquals(2, stylesheet.getNodeStyleNameSpace().getClassRulesCount());
		assertEquals(1, stylesheet.getEdgeStyleNameSpace().getIdRulesCount());
		assertEquals(1, stylesheet.getEdgeStyleNameSpace().getClassRulesCount()); // <--
																					// +1
		assertEquals(0, stylesheet.getSpriteStyleNameSpace().getIdRulesCount());
		assertEquals(0, stylesheet.getSpriteStyleNameSpace().getClassRulesCount());

		Style sAB = sgs.getStyleForElement(AB);
		Style sBC = sgs.getStyleForElement(BC);
		Style sCD = sgs.getStyleForElement(CD);
		Style sDA = sgs.getStyleForElement(DA);

		assertEquals(2, sAB.getStrokeWidth().value, 0);
		assertEquals(2, sBC.getStrokeWidth().value, 0);
		assertEquals(1, sCD.getStrokeWidth().value, 0);
		assertEquals(1, sDA.getStrokeWidth().value, 0);

		System.err.printf("After adding new style sheets, there are %d groups !!%n", sgs.getGroupCount());
		Iterator<? extends StyleGroup> i = sgs.getGroupIterator();
		while (i.hasNext())
			System.err.printf("  %s", i.next().toString());

		sgs.release();
	}

	@Test
	public void testZIndex() throws IOException {
		StyleGroupSet sgs = new StyleGroupSet(stylesheet);

		populateGroupSet(sgs);

		assertTrue(sgs.getGroupCount() == 6);

		// Now test the default Z index

		Iterator<HashSet<StyleGroup>> zIndex = sgs.getZIterator();

		// The groups we expect in order.
		HashSet<String> groups1 = new HashSet<String>();
		HashSet<String> groups2 = new HashSet<String>();
		HashSet<String> groups3 = new HashSet<String>();
		HashSet<String> groups4 = new HashSet<String>();
		HashSet<String> groups5 = new HashSet<String>();
		groups1.add("g");
		groups2.add("e");
		groups2.add("e_AB");
		groups3.add("n_A");
		groups3.add("n(foo)");
		groups3.add("n(bar,foo)");

		System.err.printf("---- zIndex ----%n");

		assertTrue(zIndex.hasNext());
		HashSet<StyleGroup> cell = zIndex.next();
		for (StyleGroup g : cell) {
			assertTrue(groups1.contains(g.getId()));
			assertTrue(g.getZIndex() == 0);
		}

		assertTrue(zIndex.hasNext());
		cell = zIndex.next();
		for (StyleGroup g : cell) {
			assertTrue(groups2.contains(g.getId()));
			assertTrue(g.getZIndex() == 1);
		}

		assertTrue(zIndex.hasNext());
		cell = zIndex.next();
		for (StyleGroup g : cell) {
			assertTrue(groups3.contains(g.getId()));
			assertTrue(g.getZIndex() == 2);
		}

		assertTrue(!zIndex.hasNext());

		System.err.printf("The Z index is :%n");
		System.err.printf("%s", sgs.getZIndex().toString());

		// Now test the way the z-index is kept up to date when changing the
		// style.
		// The change affects styles that already exist.

		System.err.printf("---- zIndex 2 ----%n");

		stylesheet.parseFromString(styleSheet5);

		assertTrue(sgs.getGroupCount() == 6);

		zIndex = sgs.getZIterator();
		assertTrue(zIndex.hasNext());
		cell = zIndex.next();
		for (StyleGroup g : cell) {
			assertTrue(groups1.contains(g.getId()));
			assertTrue(g.getZIndex() == 0);
		}

		assertTrue(zIndex.hasNext());
		cell = zIndex.next();
		for (StyleGroup g : cell) {
			assertTrue(groups3.contains(g.getId()));
			assertTrue(g.getZIndex() == 1);
		}

		assertTrue(zIndex.hasNext());
		cell = zIndex.next();
		for (StyleGroup g : cell) {
			assertTrue(groups2.contains(g.getId()));
			assertTrue(g.getZIndex() == 2);
		}

		assertTrue(!zIndex.hasNext());

		System.err.printf("The Z index is : %n");
		System.err.printf("%s", sgs.getZIndex().toString());

		// Now change only one specific (id) style.

		System.err.printf("---- zIndex 3 ----%n");
		stylesheet.parseFromString(styleSheet6);

		assertTrue(sgs.getGroupCount() == 6);
		groups2.clear();
		groups3.clear();
		groups2.add("n_A");
		groups3.add("e");
		groups3.add("e_AB");
		groups4.add("n(bar,foo)");
		groups4.add("n(foo)");

		zIndex = sgs.getZIterator();
		assertTrue(zIndex.hasNext());
		cell = zIndex.next();
		for (StyleGroup g : cell) {
			assertTrue(groups1.contains(g.getId()));
			assertTrue(g.getZIndex() == 0);
		}

		assertTrue(zIndex.hasNext());
		cell = zIndex.next();
		for (StyleGroup g : cell) {
			assertTrue(groups2.contains(g.getId()));
			assertTrue(g.getZIndex() == 1);
		}

		assertTrue(zIndex.hasNext());
		cell = zIndex.next();
		for (StyleGroup g : cell) {
			assertTrue(groups3.contains(g.getId()));
			assertTrue(g.getZIndex() == 2);
		}

		assertTrue(zIndex.hasNext());
		cell = zIndex.next();
		for (StyleGroup g : cell) {
			assertTrue(groups4.contains(g.getId()));
			assertTrue(g.getZIndex() == 5);
		}

		assertTrue(!zIndex.hasNext());

		System.err.printf("The Z index is : %n");
		System.err.printf("%s", sgs.getZIndex().toString());

		// Now add a style with a Z index.s

		System.err.printf("---- zIndex 4 ----%n");
		stylesheet.parseFromString(styleSheet7);

		assertTrue(sgs.getGroupCount() == 7);
		groups5.add("e_DA");

		zIndex = sgs.getZIterator();
		assertTrue(zIndex.hasNext());
		cell = zIndex.next();
		for (StyleGroup g : cell) {
			assertTrue(groups1.contains(g.getId()));
			assertTrue(g.getZIndex() == 0);
		}

		assertTrue(zIndex.hasNext());
		cell = zIndex.next();
		for (StyleGroup g : cell) {
			assertTrue(groups2.contains(g.getId()));
			assertTrue(g.getZIndex() == 1);
		}

		assertTrue(zIndex.hasNext());
		cell = zIndex.next();
		for (StyleGroup g : cell) {
			assertTrue(groups3.contains(g.getId()));
			assertTrue(g.getZIndex() == 2);
		}

		assertTrue(zIndex.hasNext());
		cell = zIndex.next();
		for (StyleGroup g : cell) {
			assertTrue(groups4.contains(g.getId()));
			assertTrue(g.getZIndex() == 5);
		}

		assertTrue(zIndex.hasNext());
		cell = zIndex.next();
		for (StyleGroup g : cell) {
			assertTrue(groups5.contains(g.getId()));
			assertTrue(g.getZIndex() == 7);
		}

		System.err.printf("The Z index is : %n");
		System.err.printf("%s", sgs.getZIndex().toString());
	}

	@Test
	public void testShadow() throws IOException {
		StyleGroupSet sgs = new StyleGroupSet(stylesheet);

		populateGroupSet(sgs);

		// First test with the default style sheet, no shadows.

		Iterator<StyleGroup> shadow = sgs.getShadowIterator();
		int count = 0;

		while (shadow.hasNext()) {
			shadow.next();
			count++;
		}

		assertTrue(count == 0);

		// Then we add a style that adds shadows to all nodes.

		stylesheet.parseFromString(styleSheet8);
		assertTrue(sgs.getGroupCount() == 6);
		HashSet<String> groups = new HashSet<String>();
		groups.add("n_A");
		groups.add("n(bar,foo)");
		groups.add("n(foo)");

		shadow = sgs.getShadowIterator();
		count = 0;

		while (shadow.hasNext()) {
			StyleGroup g = shadow.next();
			assertTrue(groups.contains(g.getId()));
			count++;
		}

		assertTrue(count == 3); // There are three node groups.

		// Then we add a style that adds shadows to a specific edge.

		stylesheet.parseFromString(styleSheet9);
		assertTrue(sgs.getGroupCount() == 6);
		groups.add("e_AB");

		shadow = sgs.getShadowIterator();
		count = 0;

		while (shadow.hasNext()) {
			StyleGroup g = shadow.next();
			assertTrue(groups.contains(g.getId()));
			count++;
		}

		assertTrue(count == 4); // Three node groups, plus one edge group (e_AB)
	}

	public static String styleSheet10 = "node.foo { fill-mode: dyn-plain; fill-color: red, green, blue; }";

	@Test
	public void testStyleGroupIterators() {
		try {
			stylesheet.parseFromString(styleSheet10);
		} catch (IOException e) {
			assertFalse(true);
		}

		StyleGroupSet sgs = new StyleGroupSet(stylesheet);

		populateGroupSet(sgs);

		StyleGroup sA = sgs.getStyleForElement(A);
		StyleGroup sB = sgs.getStyleForElement(B);
		StyleGroup sC = sgs.getStyleForElement(C);
		StyleGroup sD = sgs.getStyleForElement(D);

		// First test the basic iterator. B and C should be in the same group.

		assertTrue(sB == sC); // B and C are in the same group.
		assertFalse(sA == sB);
		assertFalse(sB == sD);

		HashSet<String> expected = new HashSet<String>();

		expected.add("B");
		expected.add("C");

		for (Element element : sB) {
			assertTrue(expected.contains(element.getId()));
			expected.remove(element.getId());
		}

		assertEquals(0, expected.size());

		// Now test the fact

		B.setAttribute("ui.color", 2);
		sgs.pushEventFor(B, "clicked"); // This is normally done automatically
										// by the GraphicElement
		sgs.pushElementAsDynamic(B); // This is normally done automatically by
										// the GraphicElement
		sA = sgs.getStyleForElement(A);
		sB = sgs.getStyleForElement(B);
		sC = sgs.getStyleForElement(C);
		sD = sgs.getStyleForElement(D);
		assertTrue(sB == sC); // B and C are still in the same group.
		assertFalse(sA == sB);
		assertFalse(sB == sD);
		assertTrue(sB.elementHasEvents(B));
		assertTrue(sB.elementIsDynamic(B));

		expected.add("B");

		for (Element element : sB.dynamicElements()) {
			assertTrue(expected.contains(element.getId()));
			expected.remove(element.getId());
		}

		assertEquals(0, expected.size());

		expected.add("B");

		for (ElementEvents events : sB.elementsEvents()) {
			assertTrue(expected.contains(events.getElement().getId()));
			assertEquals(1, events.events().length);
			assertEquals("clicked", events.events()[0]);
			expected.remove(events.getElement().getId());
		}

		assertEquals(0, expected.size());

		expected.add("C");

		for (Element element : sB.bulkElements()) {
			assertTrue(expected.contains(element.getId()));
			expected.remove(element.getId());
		}

		assertEquals(0, expected.size());

		sgs.popEventFor(B, "clicked");
	}

	public static String styleSheet5 = "node { z-index: 1; }" + "edge { z-index: 2; }";

	public static String styleSheet6 = "node.foo { z-index: 5; }";

	public static String styleSheet7 = "edge#DA { z-index: 7; }";

	public static String styleSheet8 = "node { shadow-mode: plain; }";

	public static String styleSheet9 = "edge#AB { shadow-mode: plain; }";
}
