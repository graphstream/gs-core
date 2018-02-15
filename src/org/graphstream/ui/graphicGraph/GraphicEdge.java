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
 * @since 2009-02-19
 * 
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Antoine Dutot <antoine.dutot@graphstream-project.org>
 * @author Yoann Pigné <yoann.pigne@graphstream-project.org>
 * @author Alex Bowen <bowen.a@gmail.com>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.ui.graphicGraph;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.stream.SourceBase.ElementType;
import org.graphstream.ui.graphicGraph.stylesheet.Selector;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Graphical edge.
 * 
 * <p>
 * The graphic edge defines its source and target node as well as a direction, a
 * string label and a style from the style sheet.
 * </p>
 * 
 * @see GraphicGraph
 */
public class GraphicEdge extends GraphicElement implements Edge {
	// Attributes

	/**
	 * The first node.
	 */
	public GraphicNode from;

	/**
	 * The second node.
	 */
	public GraphicNode to;

	/**
	 * Is the edge directed ?.
	 */
	public boolean directed;

	/**
	 * In case of a multi-graph this is the index of the edge between to and from.
	 */
	public int multi;

	/**
	 * If non null, this gives the number of edges between the two same nodes.
	 */
	public EdgeGroup group;

	/**
	 * Control points for curved edges or polylines. This contains the control
	 * points of an edge. If the edge is in 2D each sequence of two cells gives the
	 * x and y coordinates of a control point. Else each sequence of three cells
	 * gives the x, y and z coordinates. Therefore the number of control points can
	 * be obtained by dividing by 2 or 3 the length of this array. For example for
	 * cubic Bezier curves in 2D this array contains four cells. The control points
	 * are ordered from node0 to node1.
	 */
	public double[] ctrl;

	// Constructors

	/**
	 * New graphic edge.
	 * 
	 * @param id
	 *            The edge unique identifier.
	 * @param from
	 *            The source node.
	 * @param to
	 *            The target node.
	 * @param dir
	 *            True if the edge is directed in the direction from-to.
	 * @param attributes
	 *            A set of initial attributes.
	 */
	public GraphicEdge(String id, GraphicNode from, GraphicNode to, boolean dir, HashMap<String, Object> attributes) {
		super(id, from.mygraph);

		this.from = from;
		this.to = to;
		this.directed = dir;

		if (this.attributes == null)
			this.attributes = new HashMap<String, Object>();

		if (attributes != null)
			setAttributes(attributes);
	}

	@Override
	public Selector.Type getSelectorType() {
		return Selector.Type.EDGE;
	}

	/**
	 * Obtain the node that is not "n" attached to this edge.
	 * 
	 * @param n
	 *            One of the node of this edge.
	 * @return The other node of this edge.
	 */
	public GraphicNode otherNode(GraphicNode n) {
		return (GraphicNode) getOpposite(n);
	}

	@Override
	public double getX() {
		return from.x + ((to.x - from.x) / 2);
	}

	@Override
	public double getY() {
		return from.y + ((to.y - from.y) / 2);
	}

	@Override
	public double getZ() {
		return from.z + ((to.z - from.z) / 2);
	}

	/**
	 * Control points for curved edges or polylines. This contains the control
	 * points of an edge. If the edge is in 2D each sequence of two cells gives the
	 * x and y coordinates of a control point. Else each sequence of three cells
	 * gives the x, y and z coordinates. Therefore the number of control points can
	 * be obtained by dividing by 2 or 3 the length of this array. For example for
	 * cubic Bezier curves in 2D this array contains four cells. The control points
	 * are ordered from node0 to node1. The units are "graph units".
	 * 
	 * @return The control points coordinates or null if this edge is a straight
	 *         line.
	 */
	public double[] getControlPoints() {
		return ctrl;
	}

	/**
	 * True if the the edge defines control points to draw a curve or polyline. This
	 * does not mean the edge style asks to paint the edge as a curve, only that
	 * control points are defined.
	 * 
	 * @return True if control points are available.
	 */
	public boolean isCurve() {
		return ctrl != null;
	}

	/**
	 * Change the control points array for this edge.
	 * 
	 * @param points
	 *            The new set of points. See the {@link #getControlPoints()} method
	 *            for an explanation on the organisation of this array.
	 * @see #getControlPoints()
	 */
	public void setControlPoints(double points[]) {
		ctrl = points;
	}

	/**
	 * This edge is the i-th between the two same nodes.
	 * 
	 * @return The edge index between the two nodes if there are several such edges.
	 */
	public int getMultiIndex() {
		return multi;
	}

	@Override
	public void move(double x, double y, double z) {
		// NOP on edges !!!
	}

	@Override
	protected void attributeChanged(AttributeChangeEvent event, String attribute, Object oldValue, Object newValue) {
		super.attributeChanged(event, attribute, oldValue, newValue);

		if (attribute.startsWith("ui.sprite.")) {
			mygraph.spriteAttribute(event, this, attribute, newValue);
		}

		mygraph.listeners.sendAttributeChangedEvent(getId(), ElementType.EDGE, attribute, event, oldValue, newValue);
	}

	/**
	 * Count the number of identical edges between the two nodes of this edge and
	 * create or update the edge group. The edge group contains all the edges
	 * between two same nodes and allows to render faster multiple edges in a
	 * multigraph.
	 * 
	 * @param edgeList
	 *            The actual set of edges between two nodes (see the connectivity in
	 *            the graphic graph).
	 */
	protected void countSameEdges(Iterable<GraphicEdge> edgeList) {
		for (GraphicEdge other : edgeList) {
			if (other != this) {
				if ((other.from == from && other.to == to) || (other.to == from && other.from == to)) {
					group = other.group;

					if (group == null)
						group = new EdgeGroup(other, this);
					else
						group.increment(this);

					break;
				}
			}
		}
	}

	@Override
	public void removed() {
		if (group != null) {
			group.decrement(this);

			if (group.getCount() == 1)
				group = null;
		}
	}

	// Edge interface

	@Override
	public Node getNode0() {
		return from;
	}

	@Override
	public Node getNode1() {
		return to;
	}

	/**
	 * If there are several edges between two nodes, this edge pertains to a group.
	 * Else this method returns null.
	 * 
	 * @return The group of edges between two same nodes, null if the edge is alone
	 *         between the two nodes.
	 */
	public EdgeGroup getGroup() {
		return group;
	}

	@Override
	public Node getOpposite(Node node) {
		if (node == from)
			return to;

		return from;
	}

	@Override
	public Node getSourceNode() {
		return from;
	}

	@Override
	public Node getTargetNode() {
		return to;
	}

	public boolean isDirected() {
		return directed;
	}

	public boolean isLoop() {
		return (from == to);
	}

	public void setDirected(boolean on) {
		directed = on; // / XXX
	}

	public void switchDirection() {
		GraphicNode tmp; // XXX
		tmp = from;
		from = to;
		to = tmp;
	}

	// Nested classes

	/**
	 * An edge group contains the set of edges between two given nodes. This allows
	 * to quickly know how many 'multi' edges there is between two nodes in a
	 * multigraph and to associate invariant indices to edges (the
	 * {@link GraphicEdge#multi} attribute) inside the multi-representation.
	 */
	public class EdgeGroup {
		/**
		 * The set of multiple edges.
		 */
		public ArrayList<GraphicEdge> edges;

		/**
		 * Create a new edge group, starting with two edges.
		 * 
		 * @param first
		 *            The initial edge.
		 * @param second
		 *            The second edge.
		 */
		public EdgeGroup(GraphicEdge first, GraphicEdge second) {
			edges = new ArrayList<GraphicEdge>();
			first.group = this;
			second.group = this;
			edges.add(first);
			edges.add(second);
			first.multi = 0;
			second.multi = 1;
		}

		/**
		 * I-th edge of the group.
		 * 
		 * @param i
		 *            The edge index.
		 * @return The i-th edge.
		 */
		public GraphicEdge getEdge(int i) {
			return edges.get(i);
		}

		/**
		 * Number of edges in this group.
		 * 
		 * @return The edge count.
		 */
		public int getCount() {
			return edges.size();
		}

		/**
		 * Add an edge in the group.
		 * 
		 * @param edge
		 *            The edge to add.
		 */
		public void increment(GraphicEdge edge) {
			edge.multi = getCount();
			edges.add(edge);
		}

		/**
		 * Remove an edge from the group.
		 * 
		 * @param edge
		 *            The edge to remove.
		 */
		public void decrement(GraphicEdge edge) {
			edges.remove(edges.indexOf(edge));

			for (int i = 0; i < edges.size(); i++)
				edges.get(i).multi = i;
		}
	}

}