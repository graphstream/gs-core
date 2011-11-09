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

import org.graphstream.stream.Pipe;
import org.graphstream.ui.geom.Point3;

/**
 * Layout algorithm interface.
 * 
 * <p>
 * The layout algorithm role is to compute the best possible positions of nodes
 * in a given space (2D or 3D). As there are many such algorithms with distinct
 * qualities and uses, this interface defines what is awaited from a layout
 * algorithm.
 * </p>
 * 
 * <p>
 * The algorithm follows a graph by being a {@link org.graphstream.stream.Sink}.
 * However, at the contrary of several other algorithms, it does not work on the
 * graph itself. It works on a description of the graph and maintains its own
 * vision of this graph. In return, it does not modify the graph, but sends
 * events to listeners telling the new positions of nodes in the graph.
 * </p>
 * 
 * <p>
 * Here a layout algorithm continuously updates its internal representation of
 * the graph following a given method and outputs its computations to a listener
 * for each element of the graph (iterative algorithm). Such a layout algorithm
 * is not made to compute a layout once and for all. This is the best way to
 * handle evolving graphs.
 * </p>
 * 
 * <p>
 * This behaviour has been chosen because this algorithm is often run aside the
 * main thread that works on the graph. We want a thread to be able to compute a
 * new layout on its side, without disturbing the main algorithm run on the
 * graph. See the {@link org.graphstream.ui.layout.LayoutRunner} for an
 * helper class allowing to create such a thread.
 * </p>
 * 
 * <p>
 * To be notified of the layout changes dynamically, you must register a
 * {@link LayoutListener} that will be called each time a node changes its
 * position.
 * </p>
 * 
 * <p>
 * The graph viewers in the UI package often use a layout algorithm to present
 * graphs on screen.
 * </p>
 * 
 * <p>
 * TODO: would it be interesting, for some layouts, to have edges that contain
 * "break points" or curve points (Bezier for example) that are also moved by
 * the layout algorithm ?
 * </p>
 * 
 * @since 20050706
 */
public interface Layout extends Pipe {
	// Access

	/**
	 * Name of the layout algorithm.
	 */
	String getLayoutAlgorithmName();

	/**
	 * How many nodes moved during the last step?. When this method returns
	 * zero, the layout stabilised.
	 */
	int getNodeMoved();

	/**
	 * How close to stabilisation the layout algorithm is.
	 * @return a value between 0 and 1. 1 means fully stabilised.
	 */
	double getStabilization();
	
	/**
	 * Above which value a correct stabilisation is achieved?
	 * @return The stabilisation limit.
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
	 * The current layout algorithm quality. There are five quality levels. The
	 * higher the level, the higher the quality.
	 * 
	 * @return A number between 0 and 4.
	 */
	int getQuality();

	/**
	 * The current layout force.
	 * 
	 * @return A real number.
	 */
	double getForce();

	// Commands

	/**
	 * Clears the whole nodes and edges structures
	 */
	void clear();

	/**
	 * Add a listener for specific layout events.
	 */
	void addListener(LayoutListener listener);

	/**
	 * Remove a listener for specific layout events.
	 */
	void removeListener(LayoutListener listener);

	/**
	 * The general "speed" of the algorithm. For some algorithm this will have no effect. For most
	 * "dynamic" algorithms, this change the way iterations toward stabilisation are done.
	 * 
	 * @param value
	 *            A number in [0..1].
	 */
	void setForce(double value);
	
	/**
	 * Change the stabilisation limit for this layout algorithm.
	 * 
	 * <p> 
	 * The stabilisation is a number
	 * between 0 and 1 that indicates how close to stabilisation (no nodes need to move) the
	 * layout is. The value 1 means the layout is fully stabilised. Naturally this is often only
	 * an indication only, for some algorithms, it is difficult to determine if the layout is
	 * correct or acceptable enough. You can get the actual stabilisation limit using
	 * {@link #getStabilizationLimit()}. You can get the actual stabilisation using
	 * {@link #getStabilization()}.
	 * </p> 
	 * 
	 * <p>
	 * Be careful, most layout classes do not use the stabilisation limit, this number is mostly
	 * used the process that control the layout, like the {@link LayoutRunner} for example. The
	 * stabilisation limit is only an indication with a default set for each layout algorithm.
	 * However this default can be changed using this method, or by storing on the graph an
	 * attribute "layout.stabilization-limit" (or "layout.stabilisation-limit").
	 * </p>
	 * 
	 * <p>
	 * The convention is that the value 0 means that the process controlling the layout will not
	 * stop the layout (will therefore not consider the stabilisation limit). In other words the
	 * layout will compute endlessly.
	 * </p>
	 * 
	 * @param value
	 * 			The new stabilisation limit, 0 means no need to stabilise. Else a value larger than
	 * 			zero or equal to 1 is accepted.
	 */
	void setStabilizationLimit(double value);

	/**
	 * Set the overall quality level. There are five quality levels.
	 * 
	 * @param qualityLevel
	 *            The quality level in [0..4].
	 */
	void setQuality(int qualityLevel);

	/**
	 * If true, node informations messages are sent for every node. This is
	 * mainly for debugging and slows down the process a lot.
	 * 
	 * @param send
	 *            If true, send node informations.
	 */
	void setSendNodeInfos(boolean send);

	/**
	 * Add a random vector whose length is 10% of the size of the graph to all
	 * node positions.
	 */
	void shake();

	/**
	 * Move a node by force to a new location.
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
	 * Freeze or un-freeze a node.
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
	 * This method implements the layout algorithm proper. It must be called in
	 * a loop, until the layout stabilises. You can know if the layout is stable
	 * by using the {@link #getNodeMoved()} method that returns the number of
	 * node that have moved during the last call to step().
	 * </p>
	 * 
	 * <p>
	 * The listener is called by this method, therefore each call to step() will
	 * also trigger layout events, allowing to reproduce the layout process
	 * graphically for example. You can insert the listener only when the layout
	 * stabilised, and then call step() anew if you do not want to observe the
	 * layout process.
	 * </p>
	 */
	void compute();

	// Output

	/**
	 * Read the nodes positions from a file. See {@link #outputPos(String)} for
	 * the file format.
	 */
	void inputPos(String filename) throws java.io.IOException;

	/**
	 * Output the nodes positions to a file. The file format is
	 * <ul>
	 * <li>each line gives the position of one node.</li>
	 * <li>the list starts with the node identifier (maybe between quotes if
	 * needed).</li>
	 * <li>a colon.
	 * <li>
	 * <li>and a list of two to three double numbers indicating the position of
	 * the node in a given space.</li>
	 * <li>Empty lines are ignored.</li>
	 * <li>Lines beginning with an arbitrary number of spaces and then a sharp
	 * sign (#) are ignored.</li>
	 * </ul>
	 */
	void outputPos(String filename) throws java.io.IOException;
}
