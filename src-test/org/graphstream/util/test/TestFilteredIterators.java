/*
 * Copyright 2006 - 2012
 *      Stefan Balev    <stefan.balev@graphstream-project.org>
 *      Julien Baudry	<julien.baudry@graphstream-project.org>
 *      Antoine Dutot	<antoine.dutot@graphstream-project.org>
 *      Yoann Pigné	    <yoann.pigne@graphstream-project.org>
 *      Guilhelm Savin	<guilhelm.savin@graphstream-project.org>
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
package org.graphstream.util.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashSet;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.AdjacencyListGraph;
import org.graphstream.stream.file.FileSourceDGS;
import org.graphstream.util.FilteredEdgeIterator;
import org.graphstream.util.FilteredNodeIterator;
import org.graphstream.util.Filters;
import org.junit.Before;
import org.junit.Test;

public class TestFilteredIterators {

	Graph baseGraph;

	@Before
	public void loadGraph() throws IOException {
		FileSourceDGS dgs = new FileSourceDGS();

		baseGraph = new AdjacencyListGraph("g");

		dgs.addSink(baseGraph);
		dgs.readAll(getClass().getResource("data/TestFilteredIterators.dgs"));
		dgs.removeSink(baseGraph);
	}

	@Test
	public void testFilteredNodeIterator() {
		HashSet<Node> expected = new HashSet<Node>();
		HashSet<Node> reached = new HashSet<Node>();
		FilteredNodeIterator<Node> ite = new FilteredNodeIterator<Node>(
				baseGraph.getNodeIterator(), Filters.<Node> byAttributeFilter(
						"type", "A"));

		expected.add(baseGraph.getNode("A0"));
		expected.add(baseGraph.getNode("A1"));
		expected.add(baseGraph.getNode("A2"));

		while (ite.hasNext())
			reached.add(ite.next());

		assertEquals(expected, reached);
	}

	@Test
	public void testFiltereEdgeIterator() {
		HashSet<Edge> expected = new HashSet<Edge>();
		HashSet<Edge> reached = new HashSet<Edge>();
		FilteredEdgeIterator<Edge> ite = new FilteredEdgeIterator<Edge>(
				baseGraph.getEdgeIterator(), Filters.<Edge> byAttributeFilter(
						"type", "A"));

		expected.add(baseGraph.getEdge("A01"));
		expected.add(baseGraph.getEdge("A02"));
		expected.add(baseGraph.getEdge("A12"));

		while (ite.hasNext())
			reached.add(ite.next());

		assertEquals(expected, reached);
	}
}
