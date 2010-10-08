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
 * Copyright 2006 - 2009
 * 	Julien Baudry
 * 	Antoine Dutot
 * 	Yoann Pigné
 * 	Guilhelm Savin
 */

package org.graphstream.ui.layout;

import java.util.Map;

/**
 * Listener for layout algorithms.
 * 
 * <p>
 * This listener allows to be notified of each position change in the graph.
 * </p>
 */
public interface LayoutListener {
	/**
	 * A node moved to (x,y,z).
	 * 
	 * @param id
	 *            Identifier of the node that moved.
	 * @param x
	 *            new abscissa of the node.
	 * @param y
	 *            new ordinate of the node.
	 * @param z
	 *            new depth of the node.
	 */
	void nodeMoved(String id, float x, float y, float z);

	/**
	 * Only if requested in the layout algorithm, this reports various
	 * information on the node.
	 * 
	 * @param id
	 *            The node identifier.
	 * @param dx
	 *            The node displacement vector.
	 * @param dy
	 *            The node displacement vector.
	 * @param dz
	 *            The node displacement vector.
	 */
	void nodeInfos(String id, float dx, float dy, float dz);

	/**
	 * The break points of an edge changed.
	 * 
	 * @param id
	 *            The edge that changed.
	 * @param points
	 *            The points description. This description is specific to the
	 *            layout algorithm.
	 */
	void edgeChanged(String id, float points[]);

	/**
	 * Several nodes moved at once.
	 * 
	 * @param nodes
	 *            The new node positions.
	 */
	void nodesMoved(final Map<String, float[]> nodes);

	/**
	 * Several edges changed at once.
	 * 
	 * @param edges
	 *            The new edges description.
	 */
	void edgesChanged(final Map<String, float[]> edges);

	/**
	 * The current step is completed at the given percent. This allows to
	 * implement a progress bar if the operation takes a too long time.
	 * 
	 * @param percent
	 *            a number between 0 and 1, 1 means the steps is completed.
	 */
	void stepCompletion(float percent);
}
