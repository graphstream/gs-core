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
 * @author Yoann Pigné <yoann.pigne@graphstream-project.org>
 * @author Antoine Dutot <antoine.dutot@graphstream-project.org>
 * @author kitskub <kitskub@gmail.com>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.ui.layout;

import org.graphstream.stream.Pipe;
import org.graphstream.ui.geom.Point3;

/**
 * Layout algorithm interface.
 * 
 * <p>
 * The layout algorithm role is to compute the best possible positions of nodes
 * in a given space (2D or 3D) and eventually break points for edges if
 * supported using either aesthetic constraints, hierarchical constraints or
 * grouping constraints. As there are many such algorithms with distinct
 * qualities and uses, this interface defines what is awaited from a general
 * layout algorithm.
 * </p>
 * 
 * <p>
 * This algorithm is a {@link Pipe} that receives notifications on the graph
 * eventually maintain an internal representation of it (or for some of them
 * work directly on the graph), and in return send graph events to give each
 * node a position via "xyz" attributes. Some algorithms may also export more
 * information for nodes and edges. For example some algorithms are also able to
 * work on the shape of an edge or the shape of a node.
 * </p>
 * 
 * <p>
 * The layout algorithm described by this interface may be iterative. Some
 * algorithm will compute directly their final representation of the graph in
 * one pass. However most algorithms will probably work step by step until a
 * global quality function is satisfied. This is the best way to handle evolving
 * graphs.
 * </p>
 * 
 * <p>
 * This behavior has been chosen because this algorithm is often run aside the
 * main thread that works on the graph. We want a thread to be able to compute a
 * new layout on its side, without disturbing the main algorithm run on the
 * graph. See the {@link org.graphstream.ui.layout.LayoutRunner} for an helper
 * class allowing to create such a thread.
 * </p>
 * 
 * <p>
 * To be notified of the layout changes dynamically, you must register as a sink
 * of the layout.
 * </p>
 * 
 * <p>
 * The graph viewers in the UI package often use a layout algorithm to present
 * graphs on screen.
 * </p>
 * 
 * @since 20050706
 */
public interface Layout extends Pipe {
	/**
	 * Name of the layout algorithm.
	 */
	String getLayoutAlgorithmName();

	/**
	 * How many nodes moved during the last step?. When this method returns zero,
	 * the layout stabilized.
	 */
	int getNodeMovedCount();

	/**
	 * Estimate of how close to stabilization the layout algorithm is.
	 * 
	 * @return a value between 0 and 1. 1 means fully stabilized.
	 */
	double getStabilization();

	/**
	 * Above which value a correct stabilization is achieved?
	 * 
	 * @return The stabilization limit.
	 */
	double getStabilizationLimit();

	/**
	 * Smallest point in space of the layout bounding box.
	 */
	Point3 getLowPoint();

	/**
	 * Largest point in space of the layout bounding box.
	 */
	Point3 getHiPoint();

	/**
	 * Number of calls made to step() so far.
	 */
	int getSteps();

	/**
	 * Time in nanoseconds used by the last call to step().
	 */
	long getLastStepTime();

	/**
	 * The current layout algorithm quality. A number between 0 and 1 with 1 the
	 * highest (but probably slowest) quality.
	 * 
	 * @return A number between 0 and 1.
	 */
	double getQuality();

	/**
	 * The current layout force.
	 * 
	 * @return A real number.
	 */
	double getForce();

	/**
	 * Clears the whole nodes and edges structures
	 */
	void clear();

	/**
	 * The general "speed" of the algorithm. For some algorithm this will have no
	 * effect. For most "dynamic" algorithms, this change the way iterations toward
	 * stabilization are done.
	 * 
	 * @param value
	 *            A number in [0..1].
	 */
	void setForce(double value);

	/**
	 * Change the stabilization limit for this layout algorithm.
	 * 
	 * <p>
	 * The stabilization is a number between 0 and 1 that indicates how close to
	 * stabilization (no nodes need to move) the layout is. The value 1 means the
	 * layout is fully stabilized. Naturally this is often only an indication only,
	 * for some algorithms, it is difficult to determine if the layout is correct or
	 * acceptable enough. You can get the actual stabilization limit using
	 * {@link #getStabilizationLimit()}. You can get the actual stabilization using
	 * {@link #getStabilization()}.
	 * </p>
	 * 
	 * <p>
	 * Be careful, most layout classes do not use the stabilization limit, this
	 * number is mostly used the process that control the layout, like the
	 * {@link LayoutRunner} for example. The stabilization limit is only an
	 * indication with a default set for each layout algorithm. However this default
	 * can be changed using this method, or by storing on the graph an attribute
	 * "layout.stabilization-limit" (or "layout.stabilisation-limit").
	 * </p>
	 * 
	 * <p>
	 * The convention is that the value 0 means that the process controlling the
	 * layout will not stop the layout (will therefore not consider the
	 * stabilization limit). In other words the layout will compute endlessly.
	 * </p>
	 * 
	 * @param value
	 *            The new stabilization limit, 0 means no need to stabilize. Else a
	 *            value larger than zero or equal to 1 is accepted.
	 */
	void setStabilizationLimit(double value);

	/**
	 * Set the overall quality level, a number between 0 and 1 with 1 the highest
	 * quality available, but often with a slower computation.
	 * 
	 * @param qualityLevel
	 *            The quality level, a number between 0 and 1.
	 */
	void setQuality(double qualityLevel);

	/**
	 * If true, node informations messages are sent for every node. This is mainly
	 * for debugging and slows down the process a lot. The contents of the node
	 * information is specific to the algorithm, and sent via a specific
	 * "layout.info" attribute.
	 * 
	 * @param send
	 *            If true, send node informations to a "layout.info" attribute.
	 */
	void setSendNodeInfos(boolean send);

	/**
	 * Add a random vector whose length is 10% of the size of the graph to all node
	 * positions.
	 */
	void shake();

	/**
	 * Move a node by force to a new location. It is preferable to first freeze the
	 * node before moving it by force, and then un-freeze it.
	 * 
	 * @param id
	 *            The node identifier.
	 * @param x
	 *            The node new X.
	 * @param y
	 *            The node new Y.
	 * @param z
	 *            The node new Z.
	 */
	void moveNode(String id, double x, double y, double z);

	/**
	 * Freeze or un-freeze a node. The freezed node position will not be changed by
	 * the algorithm until un-freezed.
	 * 
	 * @param id
	 *            The node identifier.
	 * @param frozen
	 *            If true the node is frozen.
	 */
	void freezeNode(String id, boolean frozen);

	/**
	 * Method to call repeatedly to compute the layout.
	 * 
	 * <p>
	 * This method implements the layout algorithm proper. It must be called in a
	 * loop, until the layout stabilizes. You can know if the layout is stable by
	 * using the {@link #getNodeMovedCount()} method that returns the number of node
	 * that have moved during the last call to step().
	 * </p>
	 * 
	 * <p>
	 * The listener is called by this method, therefore each call to step() will
	 * also trigger layout events, allowing to reproduce the layout process
	 * graphically for example. You can insert the listener only when the layout
	 * stabilized, and then call step() anew if you do not want to observe the
	 * layout process.
	 * </p>
	 */
	void compute();
}