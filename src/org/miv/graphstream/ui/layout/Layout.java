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
package org.miv.graphstream.algorithm.layout2;

import org.miv.graphstream.graph.GraphListener;
import org.miv.util.geom.*;

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
 * The algorithm follows a graph by being a {@link org.miv.graphstream.graph.GraphListener}.
 * However, at the contrary of several
 * other algorithms, it does not work on the graph itself. It works on a
 * description of the graph and maintains its own vision of this graph. In
 * return, it does not modify the graph, but sends events to listeners telling
 * the new positions of nodes in the graph.
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
 * graph. See the {@link org.miv.graphstream.algorithm.layout.LayoutRunner} for
 * an helper class allowing to create such a thread.
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
 * @author Antoine Dutot
 * @author Yoann Pigné
 * @since 20050706
 */
public interface Layout extends GraphListener
{
// Access

	/**
	 * Name of the layout algorithm.
	 */
	String getLayoutAlgorithmName();
	
	/**
	 * How many nodes moved during the last step?. When this method returns zero,
	 * the layout stabilised.
	 */
	int getNodeMoved();

	/**
	 * Percent of nodes moving (between [0..1]).
	 */
	double getStabilization();

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
	 * The current layout algorithm quality. There are five quality levels. The higher the level,
	 * the higher the quality.
	 * @return A number between 0 and 4.
	 */
	int getQuality();
	
	/**
	 * The current layout force.
	 * @return A real number.
	 */
	float getForce();

// Commands

	/**
	 * Clears the whole nodes and edges structures
	 */
	void clear();

	/**
	 * Add a listener for layout events.
	 */
	void addListener( LayoutListener listener );

	/**
	 * Remove a listener for layout events.
	 */
	void removeListener( LayoutListener listener );

	/**
	 * The general "speed" of the algorithm.
	 * @param value A number in [0..1].
	 */
	void setForce( float value );
	
	/**
	 * Set the overall quality level. There are five quality levels.
	 * @param qualityLevel The quality level in [0..4].
	 */
	void setQuality( int qualityLevel );
	
	/**
	 * If true, node informations messages are sent for every node. This is mainly for
	 * debugging and slows down the process a lot.
	 * @param send If true, send node informations.
	 */
	void setSendNodeInfos( boolean send );
	
	/**
	 * Add a random vector whose length is 10% of the size of
	 * the graph to all node positions.
	 */
	void shake();

	/**
	 * Move a node by force to a new location.
	 * @param id The node identifier.
	 * @param x The node new X.
	 * @param y The node new Y.
	 * @param z The node new Z.
	 */
	void moveNode( String id, float x, float y, float z );
	
	/**
	 * Freeze or un-freeze a node.
	 * @param id The node identifier.
	 * @param frozen If true the node is frozen.
	 */
	void freezeNode( String id, boolean frozen );
	
	/**
	 * Method to call repeatedly to compute the layout.
	 * 
	 * <p>
	 * This method implements
	 * the layout algorithm proper. It must be called in a loop, until the
	 * layout stabilises. You can know if the layout is stable by using the
	 * {@link #getNodeMoved()} method that returns the number of node that have
	 * moved during the last call to step().
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
	void inputPos( String filename ) throws java.io.IOException;

	/**
	 * Output the nodes positions to a file. The file format is
	 * <ul>
	 * 		<li>each line gives the position of one node.</li>
	 * 		<li>the list starts with the node identifier (maybe between quotes
	 * 		    if needed).</li>
	 * 		<li>a colon.<li>
	 * 		<li>and a  list of two to three float numbers indicating the
	 * 		    position of the node in a given space.</li>
	 * 		<li>Empty lines are ignored.</li>
	 * 		<li>Lines beginning with an arbitrary number of spaces and then a
	 *		    sharp sign (#) are ignored.</li>
	 * </ul>
	 */
	void outputPos( String filename ) throws java.io.IOException;
}