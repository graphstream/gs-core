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
 * @since 2010-02-18
 * 
 * @author Antoine Dutot <antoine.dutot@graphstream-project.org>
 * @author Yoann Pigné <yoann.pigne@graphstream-project.org>
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.stream;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

/**
 * A simple source of graph events that takes an existing graph and creates a
 * flow of events by enumerating all nodes, edges and attributes of the graph.
 * <p>
 * <p>
 * The only method of this class is {@link #replay(Graph)} that takes a graph as
 * argument and :
 * <ul>
 * <li>First exports all graph attributes as attribute-addition events.</li>
 * <li>Then exports all nodes as node-creation events.
 * <ul>
 * <li>For each node exports all the node attributes as attribute-addition
 * events.</li>
 * </ul>
 * </li>
 * <li>Then exports all edges ad edge-creation events.
 * <ul>
 * <li>For each edge exports all the edge attribute as attribute-addition
 * events.</li>
 * </ul>
 * </li>
 * </ul>
 * In this order.
 * </p>
 * <p>
 * <p>
 * Note that this is a source, not a pipe. This means that it has its own
 * identifier and is a producer of "new" events. Also note that is does not
 * export the dynamics of the graph, only its structure at the present time (the
 * evolution of the graph is not stored in the graph, to produce a dynamic flow
 * of events of the evolution of a graph you have to register the sinks in the
 * graph itself just after its creation).
 * </p>
 */
public class GraphReplay extends SourceBase implements Source {
	public GraphReplay(String id) {
		super(id);
	}

	/**
	 * Echo each element and attribute of the graph to the registered sinks.
	 *
	 * @param graph
	 *            The graph to export.
	 */
	public void replay(Graph graph) {
		graph.attributeKeys().forEach(key -> sendGraphAttributeAdded(sourceId, key, graph.getAttribute(key)));

		graph.nodes().forEach(node -> {
			String nodeId = node.getId();
			sendNodeAdded(sourceId, nodeId);

			if (node.getAttributeCount() > 0)
				node.attributeKeys()
						.forEach(key -> sendNodeAttributeAdded(sourceId, nodeId, key, node.getAttribute(key)));
		});

		graph.edges().forEach(edge -> {
			String edgeId = edge.getId();
			sendEdgeAdded(sourceId, edgeId, edge.getNode0().getId(), edge.getNode1().getId(), edge.isDirected());

			if (edge.getAttributeCount() > 0)
				edge.attributeKeys()
						.forEach(key -> sendEdgeAttributeAdded(sourceId, edgeId, key, edge.getAttribute(key)));
		});
	}
}
