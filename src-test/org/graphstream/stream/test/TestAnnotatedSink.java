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
 * @since 2011-12-15
 * 
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.stream.test;

import static org.junit.Assert.assertEquals;

import org.graphstream.graph.implementations.AdjacencyListGraph;
import org.graphstream.stream.AnnotatedSink;
import org.graphstream.stream.SourceBase.ElementType;
import org.junit.Test;

public class TestAnnotatedSink {
	public final static String GRAPH_BINDING_ATTR = "test.object.eventA";
	public final static String NODE_BINDING_ATTR = "test.object.eventB";
	public final static String EDGE_BINDING_ATTR = "test.object.eventC";

	public final static Object GRAPH_BINDING_VALUE = "stringValue";
	public final static Object NODE_BINDING_VALUE = Double.valueOf(100.0);
	public final static Object EDGE_BINDING_VALUE = Integer.valueOf(200);

	public final static String NODE_ID = "nodeB";
	public final static String EDGE_ID = "edgeC";

	public static class TestObject extends AnnotatedSink {
		@Bind(GRAPH_BINDING_ATTR)
		public void graphBinding(String attribute, Object value) {
			assertEquals(GRAPH_BINDING_ATTR, attribute);
			assertEquals(GRAPH_BINDING_VALUE, value);
		}

		@Bind(value = NODE_BINDING_ATTR, type = ElementType.NODE)
		public void nodeBinding(String nodeId, String attribute, Object value) {
			assertEquals(NODE_BINDING_ATTR, attribute);
			assertEquals(NODE_ID, nodeId);
			assertEquals(NODE_BINDING_VALUE, value);
		}

		@Bind(value = EDGE_BINDING_ATTR, type = ElementType.EDGE)
		public void edgeBinding(String edgeId, String attribute, Object value) {
			assertEquals(EDGE_BINDING_ATTR, attribute);
			assertEquals(EDGE_ID, edgeId);
			assertEquals(EDGE_BINDING_VALUE, value);
		}
	}

	@Test
	public void check() {
		AdjacencyListGraph g = new AdjacencyListGraph("test");
		g.addSink(new TestObject());

		g.setAttribute(GRAPH_BINDING_ATTR, GRAPH_BINDING_VALUE);
		g.addNode(NODE_ID).setAttribute(NODE_BINDING_ATTR, NODE_BINDING_VALUE);
		g.addNode("otherNode");
		g.addEdge(EDGE_ID, NODE_ID, "otherNode").setAttribute(EDGE_BINDING_ATTR, EDGE_BINDING_VALUE);
	}
}
