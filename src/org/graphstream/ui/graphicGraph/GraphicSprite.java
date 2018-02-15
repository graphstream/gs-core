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
 * @author Alex Bowen <bowen.a@gmail.com>
 * @author Yoann Pigné <yoann.pigne@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.ui.graphicGraph;

import org.graphstream.stream.SourceBase.ElementType;
import org.graphstream.ui.graphicGraph.stylesheet.Selector;
import org.graphstream.ui.graphicGraph.stylesheet.Style;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants;
import org.graphstream.ui.graphicGraph.stylesheet.Values;

/**
 * A small gentle sprite.
 */
public class GraphicSprite extends GraphicElement {
	// Attributes

	/**
	 * The node this sprite is attached to.
	 */
	protected GraphicNode node;

	/**
	 * The edge this sprite is attached to.
	 */
	protected GraphicEdge edge;

	/**
	 * Sprite position.
	 */
	public Values position = new Values(StyleConstants.Units.GU, 0, 0, 0);

	// Constructors

	/**
	 * New sprite.
	 * 
	 * @param id
	 *            The sprite unique identifier.
	 * @param graph
	 *            The graph containing this sprite.
	 */
	public GraphicSprite(String id, GraphicGraph graph) {
		super(id, graph);

		// Get the position of a random node.

		if (graph.getNodeCount() > 0) {
			GraphicNode node = (GraphicNode) graph.nodes().findFirst().get();

			position.setValue(0, node.x);
			position.setValue(1, node.y);
			position.setValue(2, node.z);
		}

		String myPrefix = String.format("ui.sprite.%s", id);

		if (mygraph.getAttribute(myPrefix) == null)
			mygraph.setAttribute(myPrefix, position);
	}

	// Access

	/**
	 * The node this sprite is attached to or null if not attached to an edge.
	 * 
	 * @return A graphic node.
	 */
	public GraphicNode getNodeAttachment() {
		return node;
	}

	/**
	 * The edge this sprite is attached to or null if not attached to an edge.
	 * 
	 * @return A graphic edge.
	 */
	public GraphicEdge getEdgeAttachment() {
		return edge;
	}

	/**
	 * Return the graphic object this sprite is attached to or null if not attached.
	 * 
	 * @return A graphic object or null if no attachment.
	 */
	public GraphicElement getAttachment() {
		GraphicNode n = getNodeAttachment();

		if (n != null)
			return n;

		return getEdgeAttachment();
	}

	/**
	 * True if the sprite is attached to a node or edge.
	 */
	public boolean isAttached() {
		return (edge != null || node != null);
	}

	/**
	 * True if the sprite is attached to a node.
	 */
	public boolean isAttachedToNode() {
		return node != null;
	}

	/**
	 * True if the node is attached to an edge.
	 */
	public boolean isAttachedToEdge() {
		return edge != null;
	}

	@Override
	public Selector.Type getSelectorType() {
		return Selector.Type.SPRITE;
	}

	@Override
	public double getX() {
		return position.get(0);
	}

	@Override
	public double getY() {
		return position.get(1);
	}

	@Override
	public double getZ() {
		return position.get(2);
	}

	public Style.Units getUnits() {
		return position.getUnits();
	}

	// Commands

	@Override
	public void move(double x, double y, double z) {

		if (isAttachedToNode()) {
			GraphicNode n = getNodeAttachment();
			x -= n.x;
			y -= n.y;
			z -= n.z;
			setPosition(x, y, z, Style.Units.GU);

		} else if (isAttachedToEdge()) {
			GraphicEdge e = getEdgeAttachment();
			double len = e.to.x - e.from.x;
			double diff = x - e.from.x;
			x = diff / len;
			setPosition(x);

		} else {
			setPosition(x, y, z, Style.Units.GU);

		}
	}

	/**
	 * Attach this sprite to the given node.
	 * 
	 * @param node
	 *            A graphic node.
	 */
	public void attachToNode(GraphicNode node) {
		this.edge = null;
		this.node = node;

		String prefix = String.format("ui.sprite.%s", getId());

		if (this.node.getAttribute(prefix) == null)
			this.node.setAttribute(prefix);

		mygraph.graphChanged = true;
	}

	/**
	 * Attach this sprite to the given edge.
	 * 
	 * @param edge
	 *            A graphic edge.
	 */
	public void attachToEdge(GraphicEdge edge) {
		this.node = null;
		this.edge = edge;

		String prefix = String.format("ui.sprite.%s", getId());

		if (this.edge.getAttribute(prefix) == null)
			this.edge.setAttribute(prefix);

		mygraph.graphChanged = true;
	}

	/**
	 * Detach this sprite from the edge or node it was attached to.
	 */
	public void detach() {
		String prefix = String.format("ui.sprite.%s", getId());

		if (this.node != null)
			this.node.removeAttribute(prefix);
		else if (this.edge != null)
			this.edge.removeAttribute(prefix);

		this.edge = null;
		this.node = null;
		mygraph.graphChanged = true;
	}

	/**
	 * Reposition this sprite.
	 * 
	 * @param value
	 *            The coordinate.
	 */
	public void setPosition(double value) {
		setPosition(value, 0, 0, getUnits());
	}

	/**
	 * Reposition this sprite.
	 * 
	 * @param x
	 *            First coordinate.
	 * @param y
	 *            Second coordinate.
	 * @param z
	 *            Third coordinate.
	 * @param units
	 *            The units to use for lengths and radii, null means "unchanged".
	 */
	public void setPosition(double x, double y, double z, Style.Units units) {
		/*
		 * if( node != null ) { y = checkAngle( y ); z = checkAngle( z ); } else
		 */if (edge != null) {
			if (x < 0)
				x = 0;
			else if (x > 1)
				x = 1;
		}

		boolean changed = false;

		if (getX() != x) {
			changed = true;
			position.setValue(0, x);
		}
		if (getY() != y) {
			changed = true;
			position.setValue(1, y);
		}
		if (getZ() != z) {
			changed = true;
			position.setValue(2, z);
		}
		if (getUnits() != units) {
			changed = true;
			position.setUnits(units);
		}

		if (changed) {
			mygraph.graphChanged = true;
			mygraph.boundsChanged = true;

			String prefix = String.format("ui.sprite.%s", getId());

			mygraph.setAttribute(prefix, position);
		}
	}

	public void setPosition(Values values) {
		double x = 0;
		double y = 0;
		double z = 0;

		if (values.getValueCount() > 0)
			x = values.get(0);
		if (values.getValueCount() > 1)
			y = values.get(1);
		if (values.getValueCount() > 2)
			z = values.get(2);

		if (x == 1 && y == 1 && z == 1)
			throw new RuntimeException("WTF !!!");
		setPosition(x, y, z, values.units);
	}

	protected double checkAngle(double angle) {
		if (angle > Math.PI * 2)
			angle = angle % (Math.PI * 2);
		else if (angle < 0)
			angle = (Math.PI * 2) - (angle % (Math.PI * 2));

		return angle;
	}

	@Override
	protected void attributeChanged(AttributeChangeEvent event, String attribute, Object oldValue, Object newValue) {
		super.attributeChanged(event, attribute, oldValue, newValue);

		// if( attribute.equals( "ui.clicked" ) ) // Filter the clicks to avoid
		// loops XXX BAD !!! XXX
		// return;

		String completeAttr = String.format("ui.sprite.%s.%s", getId(), attribute);

		mygraph.listeners.sendAttributeChangedEvent(mygraph.getId(), ElementType.GRAPH, completeAttr, event, oldValue,
				newValue);
	}

	@Override
	protected void removed() {
	}
}