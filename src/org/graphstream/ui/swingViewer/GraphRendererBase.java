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

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics2D;

import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.graphicGraph.StyleGroupListener;

public abstract class GraphRendererBase implements GraphRenderer, StyleGroupListener
{
// Attribute
	
	/**
	 * The graph to draw.
	 */
	protected GraphicGraph graph;

	/**
	 * Current selection or null.
	 */
	protected Selection selection = null;
	
	/**
	 * The surface we are rendering on (used only 
	 */
	protected Container renderingSurface;

// Initialisation
	
	public void open( GraphicGraph graph, Container renderingSurface )
	{
		if( this.graph != null )
			throw new RuntimeException( "renderer already open, cannot open twice" );

		this.graph = graph;
		this.renderingSurface = renderingSurface;
		
		this.graph.getStyleGroups().addListener( this );
	}

	public void close()
	{
		if( graph != null )
		{
			graph.getStyleGroups().removeListener( this );
			graph = null;
		}
	}

// Access
	
	public Container getRenderingSurface()
	{
		return renderingSurface;
	}
	
// Selection
	
    public void beginSelectionAt( float x1, float y1 )
    {
		if( selection == null )
			selection = new Selection();
		
		selection.x1  = x1;
		selection.y1  = y1;
		selection.x2  = x1;
		selection.y2  = y1;
    }

    public void selectionGrowsAt( float x, float y )
    {
		selection.x2  = x;
		selection.y2  = y;
    }

    public void endSelectionAt( float x2, float y2 )
    {
		selection = null;
    }
	
// Utilities

	protected void displayNothingToDo( Graphics2D g, int w, int h )
	{
		String msg1 = "Graph width/height/depth is zero !!";
		String msg2 = "Place components using the 'xyz' attribute.";
		
		g.setColor( Color.RED );
		g.drawLine( 0, 0, w, h );
		g.drawLine( 0, h, w, 0 );
		
		float msg1length = g.getFontMetrics().stringWidth( msg1 );
		float msg2length = g.getFontMetrics().stringWidth( msg2 );
		
		float x = w/2;
		float y = h/2;

		g.setColor( Color.BLACK );
		g.drawString( msg1, x - msg1length/2, y-20 );
		g.drawString( msg2, x - msg2length/2, y+20 );
	}
}