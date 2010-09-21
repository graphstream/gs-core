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

package org.graphstream.ui.swingViewer.basicRenderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import javax.imageio.ImageIO;

import org.graphstream.graph.Element;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.graphicGraph.StyleGroup;
import org.graphstream.ui.graphicGraph.StyleGroupSet;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants;
import org.graphstream.ui.graphicGraph.stylesheet.Value;
import org.graphstream.ui.swingViewer.GraphRendererBase;
import org.graphstream.ui.swingViewer.LayerRenderer;
import org.graphstream.ui.swingViewer.util.Camera;
import org.graphstream.ui.swingViewer.util.GraphMetrics;

/**
 * A very simple view of the graph that respect only a subset of CSS.
 * 
 * <p>
 * This is a minimal implementation of a renderer that only supports a
 * subset of the CSS :
 * 	<ul>
 * 		<li>Colours</li>
 * 		<li>Widths</li>
 * 		<li>Borders</li>
 * 	</ul>
 * </p>
 * 
 * TODO
 * 	- Les sprites.
 * 	- Les bordures.
 */
public class SwingBasicGraphRenderer extends GraphRendererBase
{
// Attribute
	
	/**
	 * Set the view on the view port defined by the metrics.
	 */
	protected Camera camera = new Camera();

	protected NodeRenderer nodeRenderer = new NodeRenderer();

	protected EdgeRenderer edgeRenderer = new EdgeRenderer();
	
	protected SpriteRenderer spriteRenderer = new SpriteRenderer();
	
	protected LayerRenderer backRenderer = null;
	
	protected LayerRenderer foreRenderer = null;
	
// Construction
	
	public SwingBasicGraphRenderer() {}
	
	@Override
	public void open( GraphicGraph graph, Container renderingSurface )
	{
		super.open( graph, renderingSurface );
	}
	
	@Override
	public void close()
	{
		super.close();
	}
	
// Access

	public Point3 getViewCenter()
	{
		return camera.getViewCenter();
	}
	
	public float getViewPercent()
	{
		return camera.getViewPercent();
	}
	
	public float getViewRotation()
	{
		return camera.getViewRotation();
	}
	
	public float getGraphDimension()
	{
		return camera.getMetrics().diagonal;
	}

    public ArrayList<GraphicElement> allNodesOrSpritesIn( float x1, float y1, float x2, float y2 )
    {
		return camera.allNodesOrSpritesIn( graph, x1, y1, x2, y2 );
    }

    public GraphicElement findNodeOrSpriteAt( float x, float y )
    {
	    return camera.findNodeOrSpriteAt( graph, x, y );
    }

// Command	

	public void setBounds( float minx, float miny, float minz, float maxx, float maxy, float maxz )
	{
		camera.getMetrics().setBounds( minx, miny, minz, maxx, maxy, maxz );
	}

	public void render( Graphics2D g, int width, int height )
	{
		if( graph != null )	// If not closed, one or two renders can occur after closed.
		{
			if( camera.getGraphViewport() == null && camera.getMetrics().diagonal == 0 && ( graph.getNodeCount() == 0 && graph.getSpriteCount() == 0 ) )
			{
				displayNothingToDo( g, width, height );
			}
			else
			{
				camera.setPadding( graph );
				camera.setViewport( width, height );
//				System.err.printf( "%s", camera );
//				debugVisibleArea( g );
				renderGraph( g );
				renderSelection( g );
			}
		}
	}
	
	
	public void resetView()
	{
		camera.setAutoFitView( true );
		camera.setRotation( 0 );
	}

	public void setViewCenter( float x, float y, float z )
	{
		camera.setAutoFitView( false );
		camera.setCenter( x, y /* ignore Z */ );
	}

	public void setGraphViewport( float minx, float miny, float maxx, float maxy )
	{
		camera.setAutoFitView( false );
		camera.setCenter( minx + ( maxx - minx ), miny + ( maxy - miny ) );
		camera.setGraphViewport( minx, miny, maxx, maxy );
		camera.setZoom( 1 );
	}

	public void removeGraphViewport()
	{
		camera.removeGraphViewport();
		resetView();
	}
	
	public void setViewPercent( float percent )
	{
		camera.setAutoFitView( false );
		camera.setZoom( percent );
	}

	public void setViewRotation( float theta )
	{
		camera.setRotation( theta );
	}

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
		GraphMetrics metrics = camera.getMetrics(); 
		float        px1     = metrics.px1;
		Value        stroke  = style.getShadowWidth();

		setupGraphics( g );
		renderGraphBackground( g );
		renderBackLayer( g );
		camera.pushView( g );
		renderGraphElements( g );
		
		if( style.getStrokeMode() != StyleConstants.StrokeMode.NONE && style.getStrokeWidth().value != 0 )
		{
			rect.setFrame( metrics.lo.x, metrics.lo.y+px1, metrics.size.data[0]-px1, metrics.size.data[1]-px1 );
			g.setStroke( new BasicStroke( metrics.lengthToGu( stroke ) ) );
			g.setColor( graph.getStyle().getStrokeColor( 0 ) );
			g.draw( rect );
		}

		camera.popView( g );
		renderForeLayer( g );
	}
	
	protected void setupGraphics( Graphics2D g )
	{
	   g.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL,      RenderingHints.VALUE_STROKE_PURE );
	   
	   if( graph.hasAttribute( "ui.antialias" ) ) {
		   g.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING,   RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
		   g.setRenderingHint( RenderingHints.KEY_ANTIALIASING,        RenderingHints.VALUE_ANTIALIAS_ON );
	   } else {
		   g.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING,   RenderingHints.VALUE_TEXT_ANTIALIAS_OFF );
		   g.setRenderingHint( RenderingHints.KEY_ANTIALIASING,        RenderingHints.VALUE_ANTIALIAS_OFF );
	   }
    
	   if( graph.hasAttribute( "ui.quality" ) ) {
		   g.setRenderingHint( RenderingHints.KEY_RENDERING,           RenderingHints.VALUE_RENDER_SPEED );
		   g.setRenderingHint( RenderingHints.KEY_INTERPOLATION,       RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR );
		   g.setRenderingHint( RenderingHints.KEY_COLOR_RENDERING,     RenderingHints.VALUE_COLOR_RENDER_SPEED );
		   g.setRenderingHint( RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED );
	   } else {
		   g.setRenderingHint( RenderingHints.KEY_RENDERING,           RenderingHints.VALUE_RENDER_QUALITY );
		   g.setRenderingHint( RenderingHints.KEY_INTERPOLATION,       RenderingHints.VALUE_INTERPOLATION_BICUBIC );
		   g.setRenderingHint( RenderingHints.KEY_COLOR_RENDERING,     RenderingHints.VALUE_COLOR_RENDER_QUALITY );
		   g.setRenderingHint( RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY );
	   }
	}
	
	/**
	 * Render the background of the graph.
	 * @param g The Swing graphics.
	 */
	protected void renderGraphBackground( Graphics2D g )
	{
		StyleGroup group = graph.getStyle();
		
		g.setColor( group.getFillColor( 0 ) );
		g.fillRect( 0, 0, (int)camera.getMetrics().viewport.data[0],
		                  (int)camera.getMetrics().viewport.data[1] );
	}

	/**
	 * Render the element of the graph.
	 * @param g The Swing graphics.
	 */
	protected void renderGraphElements( Graphics2D g )
	{
	   try
	   {
		StyleGroupSet sgs = graph.getStyleGroups();
		
		if( sgs != null )
		{
		    for( HashSet<StyleGroup> groups : sgs.zIndex() )
		    {
			for( StyleGroup group: groups )
			{
				renderGroup( g, group );
			}
		    }
		}
	   }
	   catch( NullPointerException e )
	   {
	       // Mysterious bug, where are you ?
	       e.printStackTrace();
	       System.err.printf( "We spotted the mysterious bug ..." );
	       System.exit( 1 );
	   }
	}
	
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
				nodeRenderer.render( group, g, camera );
				break;
			case EDGE:
				edgeRenderer.render( group, g, camera );
				break;
			case SPRITE:
				spriteRenderer.render( group, g, camera );
				break;
		}
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
			
			float w = camera.getMetrics().getSize().data[0];
			float h = camera.getMetrics().getSize().data[1];
			
			if( x1 > x2 ) { t = x1; x1 = x2; x2 = t; }
			if( y1 > y2 ) { t = y1; y1 = y2; y2 = t; }
	
			Stroke s = g.getStroke();
			g.setStroke( new BasicStroke( 1 ) );
			
			g.setColor( new Color( 50, 50, 200, 128 ) );
			g.fillRect( (int)x1, (int)y1, (int)(x2-x1), (int)(y2-y1) );
			g.setColor( new Color( 0, 0, 0, 128 ) );
			g.drawLine( 0, (int)y1, (int)w, (int)y1 );
			g.drawLine( 0, (int)y2, (int)w, (int)y2 );
			g.drawLine( (int)x1, 0, (int)x1, (int)h );
			g.drawLine( (int)x2, 0, (int)x2, (int)h );
			g.setColor( new Color( 50, 50, 200, 64 ) );
			g.drawRect( (int)x1, (int)y1, (int)(x2-x1), (int)(y2-y1) );
			g.setStroke( s );
		}
	}
	
	protected void renderBackLayer( Graphics2D g )
	{
		if( backRenderer != null )
			renderLayer( g, backRenderer );
	}
	
	protected void renderForeLayer( Graphics2D g )
	{
		if( foreRenderer != null )
			renderLayer( g, foreRenderer );
	}
	
	protected void renderLayer( Graphics2D g, LayerRenderer renderer )
	{
		GraphMetrics metrics = camera.getMetrics();
		
		renderer.render( g, graph, metrics.ratioPx2Gu,
			(int)metrics.viewport.data[0],
			(int)metrics.viewport.data[1],
			metrics.loVisible.x,
			metrics.loVisible.y,
			metrics.hiVisible.x,
			metrics.hiVisible.y );
	}
	
// Utility | Debug

	/** 
	 * Show the centre, the low and high points of the graph, and the visible area (that should
	 * always map to the window borders).
	 */
	protected void debugVisibleArea( Graphics2D g )
	{
		Rectangle2D rect = new Rectangle2D.Float();
		GraphMetrics metrics = camera.getMetrics();
		
		float x = metrics.loVisible.x;
		float y = metrics.loVisible.y;
		float w = (float) Math.abs( metrics.hiVisible.x - x );
		float h = (float) Math.abs( metrics.hiVisible.y - y );
		
		rect.setFrame( x, y, w, h );
		g.setStroke( new BasicStroke( metrics.px1 * 4 ) );
		g.setColor( Color.RED );
		g.draw( rect );

		g.setColor( Color.BLUE );
		Ellipse2D ellipse = new Ellipse2D.Float();
		float px1 = metrics.px1;
		ellipse.setFrame( camera.getViewCenter().x-3*px1, camera.getViewCenter().y-3*px1, px1*6, px1*6 );
		g.fill( ellipse );
		ellipse.setFrame( metrics.lo.x-3*px1, metrics.lo.y-3*px1, px1*6, px1*6 );
		g.fill( ellipse );
		ellipse.setFrame( metrics.hi.x-3*px1, metrics.hi.y-3*px1, px1*6, px1*6 );
		g.fill( ellipse );
	}
	
	public void screenshot( String filename, int width, int height )
	{
	    if( graph != null ) 
	    {
		if( filename.endsWith( "png" ) || filename.endsWith( "PNG" ) )
		{
			BufferedImage img = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
			renderGraph( img.createGraphics() );

			File file = new File( filename );
			try
			{
				ImageIO.write( img, "png", file );
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
		}
		else if( filename.endsWith( "bmp" ) || filename.endsWith( "BMP" ) )
		{
			BufferedImage img = new BufferedImage( width, height, BufferedImage.TYPE_INT_RGB );
			renderGraph( img.createGraphics() );

			File file = new File( filename );
			try
			{
				ImageIO.write( img, "bmp", file );
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}
		}
		else if( filename.endsWith( "jpg" ) || filename.endsWith( "JPG" ) || filename.endsWith( "jpeg" ) || filename.endsWith( "JPEG" ) )
		{
			BufferedImage img = new BufferedImage( width, height, BufferedImage.TYPE_INT_RGB );
			renderGraph( img.createGraphics() );

			File file = new File( filename );
			try
			{
				ImageIO.write( img, "jpg", file );
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}
		}
	    }
	}

	public void setBackLayerRenderer( LayerRenderer renderer )
    {
		backRenderer = renderer;
    }

	public void setForeLayoutRenderer( LayerRenderer renderer )
    {
		foreRenderer = renderer;
    }
	
// Style Group Listener

    public void elementStyleChanged( Element element, StyleGroup oldStyle, StyleGroup style )
    {
    }
}