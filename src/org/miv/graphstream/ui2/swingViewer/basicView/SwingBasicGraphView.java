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

package org.miv.graphstream.ui2.swingViewer.basicView;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.Iterator;

import org.miv.graphstream.graph.Element;
import org.miv.graphstream.ui2.graphicGraph.GraphicEdge;
import org.miv.graphstream.ui2.graphicGraph.GraphicNode;
import org.miv.graphstream.ui2.graphicGraph.GraphicSprite;
import org.miv.graphstream.ui2.graphicGraph.StyleGroup;
import org.miv.graphstream.ui2.graphicGraph.StyleGroupSet;
import org.miv.graphstream.ui2.graphicGraph.stylesheet.Value;
import org.miv.graphstream.ui2.graphicGraph.stylesheet.Values;
import org.miv.graphstream.ui2.swingViewer.ViewBase;
import org.miv.graphstream.ui2.swingViewer.Viewer;
import org.miv.util.geom.Point3;

/**
 * A very simple view of the graph (programming example).
 */
public class SwingBasicGraphView extends ViewBase
{
// Attribute
	
	/**
	 * Set the view on the view port defined by the metrics.
	 */
	protected Camera camera = new Camera();
	
// Construction
	
	public SwingBasicGraphView( Viewer viewer, String identifier )
	{
		super( viewer, identifier );
	}
	
// Access

	@Override
	public Point3 getViewCenter()
	{
		return camera.getViewCenter();
	}
	
	@Override
	public float getViewPercent()
	{
		return camera.getViewPercent();
	}
	
	@Override
	public float getViewRotation()
	{
		return camera.getViewRotation();
	}
	
	@Override
	public float getGraphDimension()
	{
		return camera.metrics.diagonal;
	}

// Command	

	@Override
	public void setBounds( float minx, float miny, float minz, float maxx, float maxy, float maxz )
	{
		camera.metrics.setBounds( minx, miny, minz, maxx, maxy, maxz );
	}

	@Override
	public void render( Graphics2D g )
	{
		if( camera.metrics.diagonal == 0 | ( graph.getNodeCount() == 0 && graph.getSpriteCount() == 0 ) )
		{
			displayNothingToDo( g );
		}
		else
		{
			setBackground( graph.getStyle().getFillColor( 0 ) );
			camera.setPadding( graph );
			camera.setViewport( getWidth(), getHeight() );
			camera.pushView( g );
//			System.err.printf( "%s", camera );
//			debugVisibleArea( g );
			renderGraph( g );
			camera.popView( g );
		}
	}
	
	
	@Override
	public void resetView()
	{
		camera.setAutoFitView( true );
		camera.setRotation( 0 );
		canvasChanged = true;
	}

	@Override
	public void setViewCenter( float x, float y, float z )
	{
		camera.setAutoFitView( false );
		camera.setCenter( x, y /* ignore Z */ );
		canvasChanged = true;
	}

	@Override
	public void setViewPercent( float percent )
	{
		camera.setAutoFitView( false );
		camera.setZoom( percent );
		canvasChanged = true;
	}

	@Override
	public void setViewRotation( float theta )
	{
		camera.setRotation( theta );
		canvasChanged = true;
	}
	
// Rendering
	
	protected void renderGraph( Graphics2D g )
	{
		StyleGroup   style   = graph.getStyle(); 
		Rectangle2D  rect    = new Rectangle2D.Float();
		GraphMetrics metrics = camera.metrics; 
		float        px1     = metrics.px1;
		Value        stroke  = style.getShadowWidth();
		
		renderGraphElements( g );
		
		if( style.getStrokeWidth().value != 0 )
		{
			rect.setFrame( metrics.lo.x, metrics.lo.y+px1, metrics.size.data[0]-px1, metrics.size.data[1]-px1 );
			g.setStroke( new BasicStroke( metrics.lengthToGu( stroke ) ) );
			g.setColor( graph.getStyle().getStrokeColor( 0 ) );
			g.draw( rect );
		}
	}
	
	/**
	 * Render the element of the graph.
	 * @param g The Swing graphics.
	 */
	protected void renderGraphElements( Graphics2D g )
	{
		StyleGroupSet sgs = graph.getStyleGroups();
		
		Iterator<HashSet<StyleGroup>> z = sgs.getZIterator();
		
		while( z.hasNext() )
		{
			HashSet<StyleGroup> groups = z.next();
			
			for( StyleGroup group: groups )
				renderGroup( g, group );
		}
	}
	
	/**
	 * Show the frame per second indicator.
	 * @param g2 The Swing graphics.
	protected void renderFps( Graphics2D g2 )
	{
		if( fpsInfo )
		{
			g2.setColor( Color.GRAY );
			g2.drawString( String.format( "fps %.6f", fps.getAverageFramesPerSecond() ), 10, 15 );
		}
	}
	 */

	/**
	 * Render a style group.
	 * @param g The Swing graphics.
	 * @param group The group to render.
	 */
	protected void renderGroup( Graphics2D g, StyleGroup group )
	{
		switch( group.getType() )
		{
			case NODE:
				renderNodeGroup( g, group );
				break;
			case EDGE:
				renderEdgeGroup( g, group );
				break;
			case SPRITE:
				renderSpriteGroup( g, group );
				break;
		}
	}
	
	protected void renderNodeGroup( Graphics2D g, StyleGroup group )
	{
		GraphMetrics metrics = camera.metrics;
		Values       size    = group.getSize();
		Ellipse2D    shape   = new Ellipse2D.Float();
		float        width   = metrics.lengthToGu( size, 0 );
		float        height  = size.size() > 1 ? metrics.lengthToGu( size, 1 ) : width;
		
		setupNodeStyle( g, group );

		for( Element element: group )
		{
			GraphicNode node = (GraphicNode) element;

			if( camera.isVisible( node ) )
			{
				float w2 = width  / 2;
				float h2 = height / 2;
				shape.setFrame( node.x-w2, node.y-h2, width, height );
				g.fill( shape );
			}
		}
	}
	
	protected void setupNodeStyle( Graphics2D g, StyleGroup group )
	{
		g.setColor( group.getFillColor( 0 ) );
	}
	
	protected void renderEdgeGroup( Graphics2D g, StyleGroup group )
	{
		Line2D shape = new Line2D.Float();
		
		setupEdgeStyle( g, group );
		
		for( Element element: group )
		{
			GraphicEdge edge  = (GraphicEdge) element;
			GraphicNode node0 = (GraphicNode) edge.getNode0();
			GraphicNode node1 = (GraphicNode) edge.getNode1();
			
			if( camera.isVisible( edge ) )
			{
				shape.setLine( node0.x, node0.y, node1.x, node1.y );
				g.draw( shape );
			}
		}		
	}
	
	protected void setupEdgeStyle( Graphics2D g2, StyleGroup group )
	{
		float width = camera.metrics.lengthToGu( group.getSize(), 0 );

		g2.setColor( group.getFillColor( 0 ) );
		g2.setStroke( new BasicStroke( width ) );
	}

	protected void renderSpriteGroup( Graphics2D g2, StyleGroup group )
	{
		GraphMetrics metrics = camera.metrics;
		Ellipse2D    shape   = new Ellipse2D.Float();
		Values       size    = group.getSize();
		float        width   = metrics.lengthToGu( size, 0 );
		float        height  = size.size() > 1 ? metrics.lengthToGu( size, 1 ) : width;
		
		setupSpriteStyle( g2, group );
		
		for( Element element: group )
		{
			GraphicSprite sprite = (GraphicSprite) element;

			if( camera.isVisible( sprite ) )
			{
				float w2 = width  / 2;
				float h2 = height / 2;
				shape.setFrame( sprite.getX()-w2, sprite.getY()-h2, width, height );
				g2.fill( shape );
			}
		}		
	}
	
	protected void setupSpriteStyle( Graphics2D g, StyleGroup group )
	{
		g.setColor( group.getFillColor( 0 ) );
	}
	
// Utility | Debug

	/**
	 * Show the centre, the low and high points of the graph, and the visible area (that should
	 * always map to the window borders).
	 */
	protected void debugVisibleArea( Graphics2D g )
	{
		Rectangle2D rect = new Rectangle2D.Float();
		
		float x = camera.metrics.loVisible.x;
		float y = camera.metrics.loVisible.y;
		float w = (float) Math.abs( camera.metrics.hiVisible.x - x );
		float h = (float) Math.abs( camera.metrics.hiVisible.y - y );
		
		rect.setFrame( x, y, w, h );
		g.setStroke( new BasicStroke( camera.metrics.px1 * 4 ) );
		g.setColor( Color.RED );
		g.draw( rect );

		g.setColor( Color.BLUE );
		Ellipse2D ellipse = new Ellipse2D.Float();
		float px1 = camera.metrics.px1;
		ellipse.setFrame( camera.center.x-3*px1, camera.center.y-3*px1, px1*6, px1*6 );
		g.fill( ellipse );
		ellipse.setFrame( camera.metrics.lo.x-3*px1, camera.metrics.lo.y-3*px1, px1*6, px1*6 );
		g.fill( ellipse );
		ellipse.setFrame( camera.metrics.hi.x-3*px1, camera.metrics.hi.y-3*px1, px1*6, px1*6 );
		g.fill( ellipse );
	}
}