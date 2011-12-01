/*
 * Copyright 2006 - 2011 
 *     Stefan Balev 	<stefan.balev@graphstream-project.org>
 *     Julien Baudry	<julien.baudry@graphstream-project.org>
 *     Antoine Dutot	<antoine.dutot@graphstream-project.org>
 *     Yoann Pigné		<yoann.pigne@graphstream-project.org>
 *     Guilhelm Savin	<guilhelm.savin@graphstream-project.org>
 * 
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
	void nodeMoved(String id, double x, double y, double z);

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
	void nodeInfos(String id, double dx, double dy, double dz);

	/**
	 * The break points of an edge changed.
	 * 
	 * @param id
	 *            The edge that changed.
	 * @param points
	 *            The points description. This description is specific to the
	 *            layout algorithm.
	 */
	void edgeChanged(String id, double points[]);

	/**
	 * Several nodes moved at once.
	 * 
	 * @param nodes
	 *            The new node positions.
	 */
	void nodesMoved(final Map<String, double[]> nodes);

	/**
	 * Several edges changed at once.
	 * 
	 * @param edges
	 *            The new edges description.
	 */
	void edgesChanged(final Map<String, double[]> edges);

	/**
	 * The current step is completed at the given percent. This allows to
	 * implement a progress bar if the operation takes a too long time.
	 * 
	 * @param percent
	 *            a number between 0 and 1, 1 means the steps is completed.
	 */
	void stepCompletion(double percent);
}
