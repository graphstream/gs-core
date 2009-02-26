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

package org.miv.graphstream.ui;

import org.miv.graphstream.ui.layout.LayoutListenerProxy;


/**
 * Listener for graph viewer events.
 * 
 * <p>
 * This listeners allows to catch user interactions with graphic graph elements. Most graph viewer
 * implementations allows to select nodes and sprites, and to drag them to modify the layout. This
 * listener role is to report back these events to the program.
 * </p>
 * 
 * <p>
 * Note that when an automatic layout process is used the automatic movement of nodes is not
 * repported back to the program via this interface. There is a specific mechanism for this
 * (see {@link LayoutListenerProxy}).
 * </p>
 * 
 * <p>
 * All coordinates given by this listener are in "graph units". This means that the rendering
 * medium units are never used (you never see integer pixels). When you choose to disable the
 * automatic graph layout mechanism, you specify the coordinates of elements by yourself in your
 * own coordinate system. This is this coordinate system that is used to report back click
 * locations. This coordinate system is called "graph units".
 * </p>
 * 
 * <p>
 * Graph units consider the positive Y axis as going up (and in 3D the positive Z axis goes "out
 * of the screen" a "right-handed" coordinate system). Be careful, as some rendering toolkit like
 * swing consider the Y positive axis to go down (with an origin at the left-top corder of the
 * viewport). In GraphStream, in graph units, the origin can be anywhere in the window and the Y
 * axis goes up. 
  * </p>
 * 
 * @since 2007
 */
public interface GraphViewerListener
{
	/**
	 * Called each time the node is "selected" or "deselected".
	 * @param id The node identifier.
	 * @param selected True if the node is selected, the same event is sent with selected set to
	 *		false when the node is unselected.
	 */
	void nodeSelected( String id, boolean selected );
	
	/**
	 * Called each time the sprite is "selected" or "deselected".
	 * @param id The sprite identifier.
	 * @param selected True if the sprite is selected, the same event is sent with selected set to
	 * 		false when the node is unselected.
	 */
	void spriteSelected( String id, boolean selected );
	
	/**
	 * Called each time the user dragged a node. This is not called when the
	 * automatic layout process moves a node, only when the user drags it with the mouse.
	 * @param id The node identifier.
	 * @param x The new node abscissa (in graph units).
	 * @param y The new node ordinate (in graph units).
	 * @param z The new node depth (in graph units).
	 */
	void nodeMoved( String id, float x, float y, float z );
	
	/**
	 * Called each time the user dragged a sprite. This is not called when the sprite is moved
	 * by the program, only when the user drags the sprite with the mouse.
	 * @param id The sprite identifier.
	 * @param x The new sprite abscissa (in graph units).
	 * @param y The new sprite ordinate (in graph units).
	 * @param z the new sprite depth (in graph units).
	 */
	void spriteMoved( String id, float x, float y, float z );
	
	/**
	 * The first button of the mouse has been clicked somewhere in the graph
	 * panel, but not on a node. This event is sent twice, once for the button press, and once for
	 * the button release.
	 * @param x The abscissa of the mouse (in graph units).
	 * @param y The ordinate of the mouse (in graph units).
	 * @param button The button that was used.
	 * @param clicked True if this was a button press, false if it was a button release.
	 */
	void backgroundClicked( float x, float y, int button, boolean clicked );
}