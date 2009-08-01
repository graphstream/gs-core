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
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashSet;

import org.miv.graphstream.graph.Element;
import org.miv.graphstream.ui2.graphicGraph.GraphicEdge;
import org.miv.graphstream.ui2.graphicGraph.GraphicElement;
import org.miv.graphstream.ui2.graphicGraph.GraphicNode;
import org.miv.graphstream.ui2.graphicGraph.GraphicSprite;
import org.miv.graphstream.ui2.graphicGraph.StyleGroup;
import org.miv.graphstream.ui2.graphicGraph.StyleGroupSet;
import org.miv.graphstream.ui2.graphicGraph.StyleGroup.ElementEvents;
import org.miv.graphstream.ui2.graphicGraph.stylesheet.Value;
import org.miv.graphstream.ui2.graphicGraph.stylesheet.Values;
import org.miv.graphstream.ui2.graphicGraph.stylesheet.Style;
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

	@Override
    public ArrayList<GraphicElement> allNodesOrSpritesIn( float x1, float y1, float x2, float y2 )
    {
		return camera.allNodesOrSpritesIn( graph, x1, y1, x2, y2 );
    }

	@Override
    public GraphicElement findNodeOrSpriteAt( float x, float y )
    {
	    return camera.findNodeOrSpriteAt( graph, x, y );
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
			renderSelection( g );
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

	@Override
    public void moveElementAtPx( GraphicElement element, float x, float y )
    {
	   Point2D.Float p = camera.inverseTransform( x, y );
	   element.move( p.x, p.y, element.getZ() );
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
		
		for( HashSet<StyleGroup> groups : sgs.zIndex() )
		{
			for( StyleGroup group: groups )
			{
				renderGroup( g, group );
			}
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
		renderNodeGroup( g, group, null );
	}
	
	protected void renderNodeGroup( Graphics2D g, StyleGroup group, Element element )
	{
		GraphMetrics metrics = camera.metrics;
		Values       size    = group.getSize();
		Ellipse2D    shape   = new Ellipse2D.Float();
		float        width   = metrics.lengthToGu( size, 0 );
		float        height  = size.size() > 1 ? metrics.lengthToGu( size, 1 ) : width;
		float        w2      = width  / 2;
		float        h2      = height / 2;
		
		setupNodeStyle( g, group, element );

		if( element != null )
		{
			GraphicNode node = (GraphicNode) element;
			
			shape.setFrame( node.x-w2, node.y-h2, width, height );
			g.fill( shape );
		}
		else
		{
			for( Element e: group.bulkElements() )
			{
				GraphicNode node = (GraphicNode) e;
	
				if( camera.isVisible( node ) )
				{
					shape.setFrame( node.x-w2, node.y-h2, width, height );
					g.fill( shape );
				}
			}
			
			if( group.hasDynamicElements() )
			{
				for( Element e: group.dynamicElements() )
				{
					GraphicElement ge = (GraphicElement)e;
					
					if( camera.isVisible( ge ) && ! group.elementHasEvents( ge ) )
						renderNodeGroup( g, group, ge );
				}
			}
			
			if( group.hasEventElements() )
			{
				for( ElementEvents event: group.elementsEvents() )
				{
					GraphicElement e = (GraphicElement) event.getElement();
					
					if( camera.isVisible( e ) )
					{
						event.activate();
						renderNodeGroup( g, group, e );
						event.deactivate();
					}
				}
			}
		}
	}
	
	protected void setupNodeStyle( Graphics2D g, StyleGroup group, Element element )
	{
		Color color = group.getFillColor( 0 );
		
		if( element != null && group.getFillMode() == Style.FillMode.DYN_PLAIN )
		{
			int n = group.getFillColorCount();
			
			if( element.hasNumber( "ui.color" ) && n > 1 )
			{
				float value = (float) element.getNumber( "ui.color" );
				
				if( value < 0 ) value = 0; else if( value > 1 ) value = 1;
				
				if( value == 1 )
				{
					color = group.getFillColor( n-1 );	// Simplification, faster.
				}
				else if( value != 0 )	// If value == 0, color is already set above.
				{
					float div = 1f / (n-1);
					int   col = (int) ( value / div );

					div = ( value - (div*col) ) / div;
//					div = value / div - col;
					
					Color color0 = group.getFillColor( col );
					Color color1 = group.getFillColor( col + 1 );
					float red    = ( (color0.getRed()  *(1-div)) + (color1.getRed()  *div) ) / 255f;
					float green  = ( (color0.getGreen()*(1-div)) + (color1.getGreen()*div) ) / 255f;
					float blue   = ( (color0.getBlue() *(1-div)) + (color1.getBlue() *div) ) / 255f;
					float alpha  = ( (color0.getAlpha()*(1-div)) + (color1.getAlpha()*div) ) / 255f;
						
					color = new Color( red, green, blue, alpha );
				}
			}
		}
		
		g.setColor( color );
	}
	
	protected void renderEdgeGroup( Graphics2D g, StyleGroup group )
	{
		renderEdgeGroup( g, group, null );
	}
	
	protected void renderEdgeGroup( Graphics2D g, StyleGroup group, ElementEvents events )
	{
		if( events != null  )
			events.activate();

		Line2D shape = new Line2D.Float();
		
		setupEdgeStyle( g, group );
		
		if( events != null )
		{
			GraphicEdge edge  = (GraphicEdge) events.getElement();
			GraphicNode node0 = (GraphicNode) edge.getNode0();
			GraphicNode node1 = (GraphicNode) edge.getNode1();
			
			shape.setLine( node0.x, node0.y, node1.x, node1.y );
			g.draw( shape );
		}
		else
		{
			for( Element element: group )
			{
				GraphicEdge edge = (GraphicEdge) element;
				
				if( ! group.elementHasEvents( edge ) && camera.isVisible( edge ) )
				{
					GraphicNode node0 = (GraphicNode) edge.getNode0();
					GraphicNode node1 = (GraphicNode) edge.getNode1();

					shape.setLine( node0.x, node0.y, node1.x, node1.y );
					g.draw( shape );
				}
			}
			
			if( group.hasEventElements() )
			{
				for( ElementEvents e: group.elementsEvents() )
				{
					if( camera.isVisible( (GraphicElement)e.getElement() ) )
						renderEdgeGroup( g, group, e );
				}
			}
		}
		
		if( events != null )
			events.deactivate();
	}
	
	protected void setupEdgeStyle( Graphics2D g2, StyleGroup group )
	{
		float width = camera.metrics.lengthToGu( group.getSize(), 0 );

		g2.setColor( group.getFillColor( 0 ) );
		g2.setStroke( new BasicStroke( width ) );
	}

	protected void renderSpriteGroup( Graphics2D g, StyleGroup group )
	{
		renderSpriteGroup( g, group, null );
	}

	protected void renderSpriteGroup( Graphics2D g, StyleGroup group, ElementEvents events )
	{
		if( events != null )
			events.activate();
		
		GraphMetrics metrics = camera.metrics;
		Ellipse2D    shape   = new Ellipse2D.Float();
		Values       size    = group.getSize();
		float        width   = metrics.lengthToGu( size, 0 );
		float        height  = size.size() > 1 ? metrics.lengthToGu( size, 1 ) : width;
		
		setupSpriteStyle( g, group );

		if( events != null )
		{
			GraphicSprite sprite = (GraphicSprite) events.getElement();

			float w2 = width  / 2;
			float h2 = height / 2;
			shape.setFrame( sprite.getX()-w2, sprite.getY()-h2, width, height );
			g.fill( shape );			
		}
		else
		{
			for( Element element: group )
			{
				GraphicSprite sprite = (GraphicSprite) element;
	
				if( ! group.elementHasEvents( sprite ) && camera.isVisible( sprite ) )
				{
					float w2 = width  / 2;
					float h2 = height / 2;
					shape.setFrame( sprite.getX()-w2, sprite.getY()-h2, width, height );
					g.fill( shape );
				}
			}
			
			if( group.hasEventElements() )
			{
				for( ElementEvents e: group.elementsEvents() )
				{
					if( camera.isVisible( (GraphicElement)e.getElement() ) )
						renderSpriteGroup( g, group, e );
				}
			}
		}
		
		if( events != null )
			events.deactivate();
	}
	
	protected void setupSpriteStyle( Graphics2D g, StyleGroup group )
	{
		g.setColor( group.getFillColor( 0 ) );
	}
	
	protected void renderSelection( Graphics2D g )
	{
		if( selection != null && selection.x1 != selection.x2 && selection.y1 != selection.y2 )
		{
			float x1 = selection.x1;
			float y1 = selection.y1;
			float x2 = selection.x2;
			float y2 = selection.y2;
			float t;
			
			if( x1 > x2 ) { t = x1; x1 = x2; x2 = t; }
			if( y1 > y2 ) { t = y1; y1 = y2; y2 = t; }
	
			g.setColor( new Color( 50, 50, 200, 128 ) );
			g.fillRect( (int)x1, (int)y1, (int)(x2-x1), (int)(y2-y1) );
			g.setColor( new Color( 0, 0, 0, 128 ) );
			g.drawLine( 0, (int)y1, getWidth(), (int)y1 );
			g.drawLine( 0, (int)y2, getWidth(), (int)y2 );
			g.drawLine( (int)x1, 0, (int)x1, getHeight() );
			g.drawLine( (int)x2, 0, (int)x2, getHeight() );
			g.setColor( new Color( 50, 50, 200, 64 ) );
			g.drawRect( (int)x1, (int)y1, (int)(x2-x1), (int)(y2-y1) );
		}
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