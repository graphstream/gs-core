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

package org.graphstream.ui.old;

import org.graphstream.graph.Graph;
import org.graphstream.ui.old.GraphViewer.ScreenshotType;
import org.graphstream.ui.old.graphicGraph.stylesheet.Style;
import org.graphstream.ui.old.layout.LayoutRunner;

/**
 * A graph viewer remote allows to control a graph viewer from another thread.
 * 
 * <p>
 * The viewer remote allows to send commands to the viewer like order to take a screen shot, to
 * change the rendering quality, etc.
 * </p>
 * 
 * <p>
 * It also provides a "sprite" API. A sprite is a little graphic object that can be displayed on a
 * graph display. Sprites can be styled like nodes or edges, and can provide visual indicators for
 * data attached on the graph or using the graph.
 * </p>
 * 
 * <p>
 * Sprites are an optional feature of viewers. To check the viewer supports sprites use the
 * {@link #viewerSupportsSprites()} method.
 * </p>
 */
public interface GraphViewerRemote
{
// Access
	
	/**
	 * True if the view is a 3D view.
	 */
	boolean is3D();
	
// Commands

	/**
	 * Close the view.
	 * 
	 * <p>
	 * This can be done only once. Afterwards the viewer is no more usable (and will be garbage
	 * collected).
	 * </p>
	 */
	void close();

	/**
	 * Take a screen shot of the viewer display and save it in the given file name using the given
	 * file format.
	 * @param fileName Where to store the screen shot.
	 * @param type The screen shot file format.
	 */
	void screenShot( String fileName, ScreenshotType type );

	/**
	 * Set the rendering quality from 0 (low) to 4 (high).
	 * @param value A value between 0 to 5 (not included).
	 */
	void setQuality( int value );
	
	/**
	 * Show or hide the current time. The time is defined by the step events
	 * {@link org.graphstream.graph.Graph#stepBegins(double)}.
	 * @param on If true, show the time.
	 */
	void setStepsVisible( boolean on );
	
	/**
	 * Specify a zoom level and the position of the view centre (x,y). In 2D this method position
	 * the view centre at (x,y) in graph units. The zoom level is such that 1 means "the whole
	 * graph is visible". Value smaller than 1 show only a part of the graph. In 3D this method
	 * uses spherical coordinates. The zoom is a radius from the graph centre and the x and y
	 * position give angles from a position along the Z axis. In 3D if the view is attached
	 * to a node (see {@link #attachViewToNode(String)}), these spherical coordinates use the
	 * node as centre. In 2D if attached to a node, the (x,y) coordinates express an offset from
	 * the node used as centre. 
	 * @param zoom The zoom value.
	 * @param x The X position.
	 * @param y The Y position.
	 */
//	void setViewArea( float zoom, float x, float y );
	
	/**
	 * Like the zoom parameter of the {@link #setViewArea(float, float, float)} method. The zoom
	 * meaning changes in 2D and 3D.
	 * @param zoom The zoom value.
	 */
//	void setViewZoom( float zoom );
	
	/**
	 * Attach the view centre to a given node. The view centre will be set according to this node
	 * position. In other words, the node will always be at the centre of the view.
	 * @param name The node name.
	 */
//	void attachViewCentreToNode( String name );
	
	/**
	 * Detach the view centre from the node it was attached to.
	 */
//	void detachViewCentre();
	
	/**
	 * Set or change the current layout algorithm. The layout remote is still valid and need not
	 * be changed.
	 * @param layoutClassName The fully qualified class name of the layout to use. 
	 */
	void setLayout( String layoutClassName );
	
	/**
	 * Remote any layout algorithm set previously.
	 */
	void removeLayout();

	/**
	 * The layout remote allows to control the layout algorithm that runs in another thread (if
	 * any).
	 * @return A layout remote, or null if no layout has been enabled.
	 */
	LayoutRunner.LayoutRemote getLayoutRemote();

	/**
	 * This method makes the currant thread wait for the stabilisation of the layout algorithm (if
	 * there is one). This method is useful before requesting a screen shot with
	 * {@link #screenShot(String, GraphViewer.ScreenshotType)} If there is no layout algorithm running in
	 * parallel, this does nothing. The timeout is expressed in milliseconds and allows to escape
	 * this method if the wait is too long.
	 * @param timeOutMs do not wait more than this number of milliseconds.
	 */
	void waitForLayoutStabilisation( long timeOutMs );

	// Graphic event listeners

	/**
	 * Register a listener on the viewer for all graphic events like dragging a node or clicking in
	 * the viewer with the mouse for example.
	 * @param listener The new listener.
	 */
	void addViewerListener( GraphViewerListener listener );

	/**
	 * Remove a previously registered graph viewer listener.
	 * @param listener The listener to remove.
	 */
	void removeViewerListener( GraphViewerListener listener );

	/**
	 * Copy back to the graph attributes the nodes coordinates computed by the automatic layout
	 * process. For this method to work, and for coordinates to be updated in the graph, you must
	 * call {@link #pumpEvents} regularly.
	 * @param graph The graph to modify.
	 */
	void copyBackLayoutCoordinates( Graph graph );
	
	/**
	 * This method must be called repeatedly for the graph viewer listeners to be activated. This
	 * method checks there are events coming from the graph viewer and dispatch them to the various
	 * listeners.
	 */
	void pumpEvents();

// The "sprite" API.

	/**
	 * If true, the viewer is able to display sprites, else the sprite option is not usable.
	 * @return True if the sprite API is implemented in the viewer.
	 */
	boolean viewerSupportsSprites();

	/**
	 * Declare a new sprite.
	 * @param id The unique identifier of the sprite.
	 * @return An instance of Sprite.
	 */
	Sprite addSprite( String id );

	/**
	 * The same as {@link #addSprite(String)} but do not create a Sprite instance to represent
	 * the sprite. Use this method if you intend to subclass the Sprite class.
	 * @param id The sprite unique identifier.
	 */
	void addSpriteNoInstance( String id );
	
	/**
	 * Remove a sprite.
	 * @param id The unique identifier of the sprite.
	 */
	void removeSprite( String id );

	/**
	 * Attach a sprite to a node.
	 * 
	 * <p>
	 * The use of this method is discouraged, use the corresponding {@link Sprite#attachToNode(String)}
	 * method of the {@link Sprite} class.
	 * </p>
	 * 
	 * @param id The sprite identifier.
	 * @param nodeId The node identifier.
	 */
	void attachSpriteToNode( String id, String nodeId );

	/**
	 * Attach a sprite to an edge.
	 * 
	 * <p>
	 * The use of this method is discouraged, use the corresponding {@link Sprite#attachToEdge(String)}
	 * method of the {@link Sprite} class.
	 * </p>
	 * 
	 * @param id The sprite id.
	 * @param edgeId The edge id.
	 */
	void attachSpriteToEdge( String id, String edgeId );

	/**
	 * Detach a sprite from the node or edge it was attached to.
	 * 
	 * <p>
	 * The use of this method is discouraged, use the corresponding {@link Sprite#detach()}
	 * method of the {@link Sprite} class.
	 * </p>
	 * 
	 * @param id The sprite identifier.
	 */
	void detachSprite( String id );

	/**
	 * Position a sprite along an edge.
	 * 
	 * <p>
	 * The use of this method is discouraged, use the corresponding {@link Sprite#position(float)}
	 * method of the {@link Sprite} class.
	 * </p>
	 * 
	 * @param id The sprite identifier.
	 * @param percent The percent of the distance between the two nodes of the edge.
	 */
	void positionSprite( String id, float percent );

	/**
	 * Position a sprite in the graph, node or edge space.
	 * 
	 * <p>
	 * The use of this method is discouraged, use the corresponding {@link Sprite#position(float, float, float, org.graphstream.ui.old.graphicGraph.stylesheet.Style.Units)}
	 * method of the {@link Sprite} class.
	 * </p>
	 * 
	 * @param id The sprite identifier.
	 * @param x First coordinate.
	 * @param y Second coordinate.
	 * @param z Third coordinate.
	 * @param units for measuring length.
	 */
	void positionSprite( String id, float x, float y, float z, Style.Units units );

	/**
	 * Like {@link #positionSprite(String, float, float, float, org.graphstream.ui.old.graphicGraph.stylesheet.Style.Units)}
	 * but consider the units are unchanged. By default the units are graph units.
	 * 
	 * <p>
	 * The use of this method is discouraged, use the corresponding {@link Sprite#position(float, float, float)}
	 * method of the {@link Sprite} class.
	 * </p>
	 * 
	 * @param id The sprite identifier.
	 * @param x First coordinate.
	 * @param y Second coordinate.
	 * @param z Third coordinate.
	 */
	void positionSprite( String id, float x, float y, float z );
	
	/**
	 * Add or change an attribute on a sprite.
	 * 
	 * <p>
	 * The use of this method is discouraged, use the corresponding {@link Sprite#addAttribute(String, Object...)}
	 * method of the {@link Sprite} class.
	 * </p>
	 * 
	 * @param id The sprite identifier.
	 * @param attribute The attribute name.
	 * @param value The attribute value.
	 */
	void addSpriteAttribute( String id, String attribute, Object value );

	/**
	 * Remove an attribute from a sprite.
	 * 
	 * <p>
	 * The use of this method is discouraged, use the corresponding {@link Sprite#removeAttribute(String)}
	 * method of the {@link Sprite} class.
	 * </p>
	 * 
	 * @param id The sprite identifier.
	 * @param attribute The attribute name.
	 */
	void removeSpriteAttribute( String id, String attribute );
}