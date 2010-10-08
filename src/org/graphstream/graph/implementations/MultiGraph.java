/*
 * This file is part of GraphStream.
 * 
 * GraphStream is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * GraphStream is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with GraphStream.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Copyright 2006 - 2010
 * 	Julien Baudry
 * 	Antoine Dutot
 * 	Yoann Pigné
 * 	Guilhelm Savin
 */

package org.graphstream.graph.implementations;

import org.graphstream.graph.EdgeFactory;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.NodeFactory;

/**
 * A graph implementation that supports multiple edges between two nodes.
 * 
 * <p>
 * This implementation is only a subclass of {@link DefaultGraph} that renames
 * it and add no behaviour excepted changing the node and edge factories to use
 * the {@link MultiNode} and {@link MultiEdge} classes.
 * </p>
 */
public class MultiGraph extends DefaultGraph {
	/**
	 * New empty graph, with a default identifier.
	 * 
	 * @see #MultiGraph(String)
	 * @see #MultiGraph(boolean, boolean)
	 * @see #MultiGraph(String, boolean, boolean)
	 */
	@Deprecated
	public MultiGraph() {
		this("MultiGraph");
	}

	/**
	 * New empty graph.
	 * 
	 * @param id
	 *            Unique identifier of the graph.
	 * @see #MultiGraph(boolean, boolean)
	 * @see #MultiGraph(String, boolean, boolean)
	 */
	public MultiGraph(String id) {
		this(id, true, false);
	}

	/**
	 * New empty graph, with a default identifier.
	 * 
	 * @param strictChecking
	 *            If true any non-fatal error throws an exception.
	 * @param autoCreate
	 *            If true (and strict checking is false), nodes are
	 *            automatically created when referenced when creating a edge,
	 *            even if not yet inserted in the graph.
	 * @see #MultiGraph(String, boolean, boolean)
	 * @see #setStrict(boolean)
	 * @see #setAutoCreate(boolean)
	 */
	@Deprecated
	public MultiGraph(boolean strictChecking, boolean autoCreate) {
		this("MultiGraph", strictChecking, autoCreate);
	}

	/**
	 * New empty graph.
	 * 
	 * @param id
	 *            Unique identifier of this graph.
	 * @param strictChecking
	 *            If true any non-fatal error throws an exception.
	 * @param autoCreate
	 *            If true (and strict checking is false), nodes are
	 *            automatically created when referenced when creating a edge,
	 *            even if not yet inserted in the graph.
	 * @see #setStrict(boolean)
	 * @see #setAutoCreate(boolean)
	 */
	public MultiGraph(String id, boolean strictChecking, boolean autoCreate) {
		super(id, strictChecking, autoCreate);

		nodeFactory = new NodeFactory<MultiNode>() {
			public MultiNode newInstance(String id, Graph graph) {
				return new MultiNode(graph, id);
			}
		};
		edgeFactory = new EdgeFactory<MultiEdge>() {
			public MultiEdge newInstance(String id, Node src, Node dst,
					boolean directed) {
				return new MultiEdge(id, src, dst, directed);
			}
		};

		addAttribute("ui.multigraph"); // XXX a trick, this allows to inform the
										// viewer
										// That multi-edges will be drawn. When
										// drawing
										// simple edges, the viewer may be
										// faster.
										// XXX We should not see this here !!!
										// See the source code for GraphicGraph
										// for the
										// corresponding code.
	}
}