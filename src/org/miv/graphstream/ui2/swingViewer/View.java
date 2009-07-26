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

package org.miv.graphstream.ui2.swingViewer;

import javax.swing.JPanel;

import org.miv.graphstream.ui2.graphicGraph.GraphicGraph;
import org.miv.util.geom.Point3;

/**
 * A view on a graphic graph.
 */
public abstract class View extends JPanel
{
// Attribute
	
	/**
	 * The view identifier.
	 */
	private String id;
	
// Construction
	
	/**
	 * New view.
	 * @param identifier The view unique identifier.
	 */
	public View( String identifier )
	{
		id = identifier;
	}

// Access
	
	public String getId()
	{
		return id;
	}
	
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
	 * Redisplay or update the view contents.
	 * @param graph The graphic graph to represent.
	 * @param graphChanged True if the graph changed since the last call to this method.
	 */
	public abstract void display( GraphicGraph graph, boolean graphChanged );
	
	/**
	 * Close definitively this view.
	 * @param graph The graphic graph.
	 */
	public abstract void close( GraphicGraph graph );
	
	/**
	 * Open this view JPanel in a frame. The argument allows to put the panel in a new frame or
	 * to remove it from the frame (if it already exists).
	 * @param on Add the panel in its own frame or remove it if it already was in its own frame.
	 */
	public abstract void openInAFrame( boolean on );
	
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
	 * Zoom the view.
	 * @param percent Percent of the graph visible.
	 */
	public abstract void setViewPercent( float percent );
	
	/**
	 * Rotate the view around its centre point by a given theta angles (in degrees).
	 * @param theta The rotation angle in degrees.
	 */
	public abstract void setViewRotation( float theta );
}