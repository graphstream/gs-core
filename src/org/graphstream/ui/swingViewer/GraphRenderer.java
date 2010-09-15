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
 * 
 * Copyright 2006 - 2009
 * 	Julien Baudry
 * 	Antoine Dutot
 * 	Yoann Pigné
 * 	Guilhelm Savin
 */

package org.graphstream.ui.swingViewer;

import java.awt.Container;
import java.awt.Graphics2D;
import java.util.ArrayList;

import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.GraphicGraph;

/**
 * Interface for classes that draw a GraphicGraph in a swing component.
 * 
 * <p>There are two rendering mechanisms in the Swing ui package : the viewer and the
 * renderers. The viewer is a complete architecture to render a graph in a panel or
 * frame, handling all the details. The renderer architecture is a way to only
 * render the graph in any surface, handled directly by the developer. When using
 * the render you are must handle the graphic graph by yourself, but you have a
 * lot more flexibility.</p>
 * 
 * <p>The viewer mechanisms uses graph renderers.</p>
 */
public interface GraphRenderer
{
// Initialisation

	void open( GraphicGraph graph, Container drawingSurface );
	
	void close();
	
// Access
	
	/**
	 * The view centre (a point in graph units).
	 * @return The view centre.
	 */
	public abstract Point3 getViewCenter();
	
	/**
	 * The portion of the graph visible.
	 * @return A real for which value 1 means the graph is fully visible and uses the whole view
	 * port.
	 */
	public abstract float getViewPercent();
	
	/**
	 * The current rotation angle.
	 * @return The rotation angle in degrees.
	 */
	public abstract float getViewRotation();

	/**
	 * A number in GU that gives the approximate graph size (often the diagonal of the graph). This
	 * allows to compute displacements in the graph as percent of its overall size. For example
	 * this can be used to move the view centre.
	 * @return The graph estimated size in graph units.
	 */
	public abstract float getGraphDimension();

	/**
	 * Search for the first node or sprite (in that order) that contains the point at coordinates
	 * (x, y).
	 * @param x The point abscissa.
	 * @param y The point ordinate.
	 * @return The first node or sprite at the given coordinates or null if nothing found. 
	 */
	public abstract GraphicElement findNodeOrSpriteAt( float x, float y );

	/**
	 * Search for all the nodes and sprites contained inside the rectangle (x1,y1)-(x2,y2).
	 * @param x1 The rectangle lowest point abscissa.
	 * @param y1 The rectangle lowest point ordinate.
	 * @param x2 The rectangle highest point abscissa.
	 * @param y2 The rectangle highest point ordinate.
	 * @return The set of sprites and nodes in the given rectangle.
	 */
	public abstract ArrayList<GraphicElement> allNodesOrSpritesIn( float x1, float y1, float x2, float y2 );
	
// Command
	
	/**
	 * Set the bounds of the graphic graph in GU.
	 * @param minx Lowest abscissa.
	 * @param miny Lowest ordinate.
	 * @param minz Lowest depth.
	 * @param maxx Highest abscissa.
	 * @param maxy Highest ordinate.
	 * @param maxz Highest depth.
	 */
	public abstract void setBounds( float minx, float miny, float minz, float maxx, float maxy, float maxz );
	
	/**
	 * Redisplay or update the graph.
	 */
	public abstract void render( Graphics2D g, int width, int height );
	
	/**
	 * Reset the view to the automatic mode.
	 */
	public abstract void resetView();

	/**
	 * Change the view centre.
	 * @param x The new abscissa.
	 * @param y The new ordinate.
	 * @param z The new depth.
	 */
	public abstract void setViewCenter( float x, float y, float z );

	/**
	 * Specify exactly the minimum and maximum points in GU that are visible (more points may be
	 * visible due to aspect-ratio constraints).
	 * @param minx The minimum abscissa visible.
	 * @param miny The minimum ordinate visible.
	 * @param maxx The maximum abscissa visible.
	 * @param maxy The maximum abscissa visible.
	 * @see #removeGraphViewport()
	 */
	public abstract void setGraphViewport( float minx, float miny, float maxx, float maxy );
	
	/**
	 * Remove the specified graph view port.
	 * @see #setGraphViewport(float, float, float, float)
	 */
	public abstract void removeGraphViewport();

	/**
	 * Zoom the view.
	 * @param percent Percent of the graph visible.
	 */
	public abstract void setViewPercent( float percent );
	
	/**
	 * Rotate the view around its centre point by a given theta angles (in degrees).
	 * @param theta The rotation angle in degrees.
	 */
	public abstract void setViewRotation( float theta );
	
	/**
	 * Called by the mouse manager to specify where a node and sprite selection started.
	 * @param x1 The selection start abscissa.
	 * @param y1 The selection start ordinate.
	 */
	public abstract void beginSelectionAt( float x1, float y1 );
	
	/**
	 * The selection already started grows toward position (x, y).
	 * @param x The new end selection abscissa.
	 * @param y The new end selection ordinate.
	 */
	public abstract void selectionGrowsAt( float x, float y );
	
	/**
	 * Called by the mouse manager to specify where a node and spite selection stopped.
	 * @param x2 The selection stop abscissa.
	 * @param y2 The selection stop ordinate.
	 */
	public abstract void endSelectionAt( float x2, float y2 );
	
	/**
	 * Force an element to move at the given location in pixels.
	 * @param element The element.
	 * @param x The requested position abscissa in pixels.
	 * @param y The requested position ordinate in pixels.
	 */
	public abstract void moveElementAtPx( GraphicElement element, float x, float y );
	
	public abstract void screenshot( String filename, int width, int height );
	
	/**
	 * Set a layer renderer that will be called each time the graph needs to be redrawn before the
	 * graph is rendered. Pass "null" to remove the layer renderer. 
	 * @param renderer The renderer (or null to remove it).
	 */
	public abstract void setBackLayerRenderer( LayerRenderer renderer );
	
	/**
	 * Set a layer renderer that will be called each time the graph needs to be redrawn after the
	 * graph is rendered. Pass "null" to remove the layer renderer. 
	 * @param renderer The renderer (or null to remove it).
	 */
	public abstract void setForeLayoutRenderer( LayerRenderer renderer );
}