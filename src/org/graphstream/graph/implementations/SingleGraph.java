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
 * A graph implementation that supports only one edge between two nodes.
 * 
 * <p>
 * This implementation is only a subclass of {@link DefaultGraph} that renames
 * it and add no behaviour. It is here for clarity only, you can use
 * {@link DefaultGraph} instead. If you use both multi-graphs and single-graphs,
 * it can be cleaner to use the classes named {@link MultiGraph} and
 * {@link SingleGraph}.
 * </p>
 */
public class SingleGraph extends DefaultGraph {
	/**
	 * New empty graph, with a default identifier.
	 * 
	 * @see #SingleGraph(String)
	 * @see #SingleGraph(boolean, boolean)
	 * @see #SingleGraph(String, boolean, boolean)
	 */
	@Deprecated
	public SingleGraph() {
		this("SingleGraph");
	}

	/**
	 * New empty graph.
	 * 
	 * @param id
	 *            Unique identifier of the graph.
	 * @see #SingleGraph(boolean, boolean)
	 * @see #SingleGraph(String, boolean, boolean)
	 */
	public SingleGraph(String id) {
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
	 * @see #SingleGraph(String, boolean, boolean)
	 * @see #setStrict(boolean)
	 * @see #setAutoCreate(boolean)
	 */
	@Deprecated
	public SingleGraph(boolean strictChecking, boolean autoCreate) {
		this("SingleGraph", strictChecking, autoCreate);
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
	public SingleGraph(String id, boolean strictChecking, boolean autoCreate) {
		super(id, strictChecking, autoCreate);

		nodeFactory = new NodeFactory<SingleNode>() {
			public SingleNode newInstance(String id, Graph graph) {
				return new SingleNode(graph, id);
			}
		};
		edgeFactory = new EdgeFactory<SingleEdge>() {
			public SingleEdge newInstance(String id, Node src, Node dst,
					boolean directed) {
				return new SingleEdge(id, src, dst, directed);
			}
		};
	}
}