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

package org.graphstream.ui.swing;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.graphicGraph.GraphicNode;
import org.graphstream.ui.graphicGraph.stylesheet.Style;
import org.util.InvalidOperationException;


/**
 * Swing NodeRenderer.
 *
 * @since 20061226
 */
public class SwingNodeRenderer
{
// Attributes
	
	/**
	 * The style sheet.
	 */
	protected Context ctx;
	
	/**
	 * To avoid creating new shapes at each redraw.
	 */
	protected Ellipse2D oval = new Ellipse2D.Float();
	
	/**
	 * To avoid creating new shapes at each redraw.
	 */
	protected Rectangle2D rect = new Rectangle2D.Float();
	
	/**
	 * To avoid creating new shapes at each redraw.
	 */
	protected GeneralPath path = new GeneralPath();
	
// Constructors
	
	public void setContext( Context ctx )
	{
		this.ctx = ctx;
	}
	
// Access

// Commands

	public void renderNode( GraphicNode node ) throws InvalidOperationException
	{
		if( ctx != null && ctx.drawNodes )
		{
			renderNode( node, ctx.g2 );
		}
	}

	public void renderNodes( GraphicGraph graph ) throws InvalidOperationException
	{
		Graphics2D g2 = ctx.g2;
		
		for( GraphicNode node : graph.getNodes() )
		{
			renderNode( node, g2 );
		}
	}

	public void renderNodes( Map<String,GraphicNode> nodes ) throws InvalidOperationException
	{
		if( ctx != null && ctx.drawNodes )
		{
			Graphics2D g2 = ctx.g2;
			
			for( GraphicNode node : nodes.values() )
			{
				renderNode( node, g2 );
			}
		}
	}
	
	protected void renderNode( GraphicNode node, Graphics2D g2 )
	{
		if( node.hasAttribute( "ui.hide" ) || node.hasAttribute( "ui.hide-only" ) )
			return;
		
		Style.NodeShape shape  = node.getStyle().getNodeShape();

		switch( shape )
		{
			case CIRCLE:
				drawCircle( node, g2 );
				drawLabel( node, g2 );
				break;
			case SQUARE:
				drawSquare( node, g2 );
				drawLabel( node, g2 );
				break;
			case CROSS:
				drawCross( node, g2 );
				drawLabel( node, g2 );
				break;
			case TRIANGLE:
				drawTriangle( node, g2 );
				drawLabel( node, g2 );
				break;
			case IMAGE:
				drawImage( node, g2 );
				drawLabel( node, g2 );
				break;
			case TEXT_BOX:
				// The text box draws the text and the box.
				drawTextBox( node, g2 );
				break;
			case TEXT_ELLIPSE:
				// The text ellipse draws the text and the ellipse.
				drawTextEllipse( node, g2 );
				break;
		}
	}
	
	public void renderNodeShadow( GraphicNode node )
	{
		if( node.hasAttribute( "ui.hide" ) || node.hasAttribute( "ui.hide-only" ) )
			return;
		
		Style.NodeShape shape  = node.getStyle().getNodeShape();
		Graphics2D      g2     = ctx.g2;

		switch( shape )
		{
			case CIRCLE:
				drawCircleShadow( node, g2 );
				break;
			case SQUARE:
				drawSquareShadow( node, g2 );
				break;
			case CROSS:
//				drawCrossShadow( node, g2 );
				break;
			case TRIANGLE:
//				drawTriangleShadow( node, g2 );
				break;
			case IMAGE:
//				drawImageShadow( node, g2 );
				break;
			case TEXT_BOX:
				drawTextBoxShadow( node, g2 );
				break;
			case TEXT_ELLIPSE:
//				drawTextEllipseShadow( node, g2 );
				break;
		}		
	}
	
	protected void drawSquare( GraphicNode node, Graphics2D g2 )
	{
		Style style  = node.getStyle();
		Color color  = style.getColor();
		float width  = ctx.toPixels( style.getWidth() );
		float bwidth = ctx.toPixels( style.getBorderWidth() );
		float x      = ctx.xGuToPixels( node.x );
		float y      = ctx.yGuToPixels( node.y );

		g2.setColor( color );
		rect.setFrame( x-width/2, y-width/2, width, width );
		g2.fill( rect );
		
		if( bwidth > 0 )
		{
			Color bcolor = style.getBorderColor();
	
			ctx.chooseBorderStroke( bwidth );
			g2.setColor( bcolor );
			g2.draw( rect );
		}
		
		float w = ctx.pixelsToGu( width );
		
		node.setBounds( node.x, node.y, w, w );
	}
	
	protected void drawCircle( GraphicNode node, Graphics2D g2 )
	{
		Style style  = node.getStyle();
		Color color  = style.getColor();
		float width  = ctx.toPixels( style.getWidth() );
		float bwidth = ctx.toPixels( style.getBorderWidth() );
		float x      = ctx.xGuToPixels( node.x );
		float y      = ctx.yGuToPixels( node.y );
		
		g2.setColor( color );
		oval.setFrame( x-width/2, y-width/2, width, width );
		g2.fill( oval );
		
		if( bwidth > 0 )
		{
			Color bcolor = style.getBorderColor();

			ctx.chooseBorderStroke( bwidth );
			g2.setColor( bcolor );
			g2.draw( oval );
		}		
	
		float w = ctx.toGu( style.getWidth() );
		
		node.setBounds( node.x-w, node.y-w, w+w, w+w );
	}
	
	protected void drawCircleShadow( GraphicNode node, Graphics2D g2 )
	{
		Style style  = node.getStyle();
		Color color  = style.getShadowColor();
		float width  = ctx.toPixels( style.getWidth() );
		float swidth = ctx.toPixels( style.getShadowWidth() );
		float x      = ctx.xGuToPixels( node.x ) + ctx.toPixels( style.getShadowOffsetX() );
		float y      = ctx.yGuToPixels( node.y ) + ctx.toPixels( style.getShadowOffsetY() );

		x -= swidth;
		y -= swidth;
		
		g2.setColor( color );
		oval.setFrame( x-width/2, y-width/2, width + swidth * 2, width + swidth * 2 );
		g2.fill( oval );
	}
	
	protected void drawSquareShadow( GraphicNode node, Graphics2D g2 )
	{
		Style style  = node.getStyle();
		Color color  = style.getShadowColor();
		float width  = ctx.toPixels( style.getWidth() );
		float swidth = ctx.toPixels( style.getShadowWidth() );
		float x      = ctx.xGuToPixels( node.x ) + ctx.toPixels( style.getShadowOffsetX() );
		float y      = ctx.yGuToPixels( node.y ) + ctx.toPixels( style.getShadowOffsetY() );

		width += ( swidth * 2 );
		x     -= swidth;
		y     -= swidth;
		
		g2.setColor( color );
		rect.setFrame( x-width/2, y-width/2, width, width );
		g2.fill( rect );
	}
	
	protected void drawCross( GraphicNode node, Graphics2D g2 )
	{
		System.out.printf( "draw cross !! TODO" );		
	}

	protected void drawTriangle( GraphicNode node, Graphics2D g2 )
	{
		System.out.printf( "draw triangle !! TODO" );				
	}
	
	protected void drawImage( GraphicNode node, Graphics2D g2 )
	{
		Style style = node.getStyle();
		
		if( style.getImageUrl() == null )
		{
			drawCircle( node, g2 );
		}
		else
		{
			Image image  = ctx.getImage( style.getImageUrl() );
			
			if( image == null )
				image = ctx.getDummyImage();

			int   iw     = (int) ctx.toPixels( style.getWidth() );
			int   ih     = (int) ctx.toPixels( style.getHeight() );
			int   x      = (int) ctx.xGuToPixels( node.x );
			int   y      = (int) ctx.yGuToPixels( node.y );
			int   bwidth = (int) ctx.toPixels( style.getBorderWidth() );
		
			g2.drawImage( image, x - iw/2, y - ih/2, iw, ih, null );
			
			if( bwidth > 0 )
			{	
				Color bcolor = style.getBorderColor();

				ctx.chooseBorderStroke( bwidth );
				g2.setColor( bcolor );
				g2.drawRect( x-iw/2, y-iw/2, iw, ih );
			}
			
			float w = ctx.pixelsToGu( iw );
			float h = ctx.pixelsToGu( ih );
			
			node.setBounds( node.x-w/2, node.y-h/2, w, h );
		}
	}

	protected void drawLabel( GraphicNode node, Graphics2D g2 )
	{
		ctx.drawStyledLabel( node.label, node.x, node.y, node );
	}
	
	protected void drawTextBox( GraphicNode node, Graphics2D g2 )
	{
		if( node.label == null || node.label.length() == 0 )
		{
			drawSquare( node, g2 );
		}
		else
		{
			ctx.drawTextBox( node.label, node.x, node.y, node, false );
		}
	}
	
	protected void drawTextBoxShadow( GraphicNode node, Graphics2D g2 )
	{
		if( node.label == null || node.label.length() == 0 )
		{
			drawSquareShadow( node, g2 );
		}
		else
		{
			ctx.drawTextBoxShadow( node.label, node.x, node.y, node, false );
		}
	}
	
	protected void drawTextEllipse( GraphicNode node, Graphics2D g2 )
	{
		if( node.label == null || node.label.length() == 0 )
		{
			drawSquare( node, g2 );
		}
		else
		{
			ctx.drawTextBox( node.label, node.x, node.y, node, true );
		}
	}
}