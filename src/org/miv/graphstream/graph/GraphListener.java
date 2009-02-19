/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */

package org.miv.graphstream.graph;

/**
 * Graph change listener.
 * 
 * <p>
 * A graph listener allows to follow the graph creation step and each of its
 * modification (by events or by the user).
 * </p>
 * 
 * @see org.miv.graphstream.graph.Graph
 * @author Antoine Dutot
 * @since 20040711
 */
public interface GraphListener
{
	/**
	 * The whole graph is about to be cleared and all its nodes, edges,
	 * attributes and properties removed. When this method is called the graph
	 * is not yet cleaned.
	 * @param graph The graph about to be cleared.
	 */
	public void beforeGraphClear( Graph graph );

	/**
	 * A node was inserted in the given graph.
	 * @param graph The graph where the node was added.
	 * @param node the node added.
	 */
	public void afterNodeAdd( Graph graph, Node node );

	/**
	 * A node of graph is about to be removed. The node will be removed when
	 * this method returns.
	 * @param graph The graph where the node will be removed.
	 * @param node The node that will be removed.
	 */
	public void beforeNodeRemove( Graph graph, Node node );

	/**
	 * An edge was inserted in graph.
	 * @param graph The graph where the edge was added.
	 * @param edge The added edge.
	 */
	public void afterEdgeAdd( Graph graph, Edge edge );

	/**
	 * An edge of graph is about to be removed. The edge will be removed when
	 * this method returns. The nodes the edge connects may already have been
	 * removed from the graph. However the edge still points on them. In any
	 * case the two nodes do not reference this edge any more when this method
	 * is called (the edge it already unbound from its nodes).
	 * @param graph The graph where the edge will be removed.
	 * @param edge The edge that vill be removed.
	 */
	public void beforeEdgeRemove( Graph graph, Edge edge );

	/**
	 * An attribute of the element was added or changed or removed. When the
	 * attribute is added, the oldValue is null, when it is removed the newValue
	 * is null. When the attribute is added this method is called after it has
	 * been added. When the attribute is removed this method is called before it
	 * is removed. Finally when the attribute is changed, this method is called
	 * before the attribute is changed.
	 * @param element The element storing the attribute.
	 * @param attribute The attribute name.
	 * @param oldValue The old value of the attribute.
	 * @param newValue The new value of the attribute.
	 */
	public void attributeChanged( Element element, String attribute,
			Object oldValue, Object newValue );
	
	/**
	 * <p>
	 * Since dynamic graphs are based on discrete event modifications, the notion of step is defined
	 * to simulate elapsed time between events. So a step is a event that occure in the graph, it
	 * does not modify it but it gives a kind of timestamp that allow the tracking of the progress
	 * of the graph over the time.
	 * </p>
	 * <p>
	 * This kind of event is useful for dynamic algorithms that listen to the dynamic graph and need
	 * to measure the time in the graph's evolution.
	 * </p>
	 * 
	 * @param graph
	 *            The graph where the step starts.
	 * @param time
	 *            A numerical value that may give a timestamp to track the evolution of the graph
	 *            over the time.
	 */
	public void  stepBegins(Graph graph, double time);

}