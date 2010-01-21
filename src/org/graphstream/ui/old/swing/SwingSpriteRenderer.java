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

package org.graphstream.ui.old.swing;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.graphstream.ui.old.graphicGraph.GraphicEdge;
import org.graphstream.ui.old.graphicGraph.GraphicGraph;
import org.graphstream.ui.old.graphicGraph.GraphicNode;
import org.graphstream.ui.old.graphicGraph.GraphicSprite;
import org.graphstream.ui.old.graphicGraph.stylesheet.Style;

/**
 * Swing renderer for sprites.
 */
public class SwingSpriteRenderer
{
// Attributes
	
	/**
	 * The style sheet.
	 */
	protected Context ctx;

	/**
	 * To avoid creating new shapes at each redraw.
	 */
	protected Line2D line = new Line2D.Float();
	
	/**
	 * To avoid creating new shapes at each redraw.
	 */
	protected Ellipse2D oval = new Ellipse2D.Float();
	
	/**
	 * To avoid creating new shapes at each redraw.
	 */
	protected Arc2D arc = new Arc2D.Float();
	
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

	public boolean isGroupable()
	{
		return true;
	}
	
// Commands

	public void renderSprites( GraphicGraph graph )
	{
		Graphics2D g2 = ctx.g2;
		
		Iterator<GraphicSprite> i = graph.getSpriteManager().getSpriteIterator();

		while( i.hasNext() )
		{
			renderSprite( i.next(), g2 );
		}
	}

	public void renderSprites( Map<String,GraphicSprite> sprites )
	{
		if( ctx != null )
		{
			Graphics2D g2 = ctx.g2;
		
			for( GraphicSprite sprite : sprites.values() )
			{
				renderSprite( sprite, g2 );
			}
		}
	}
	
	protected void renderSprite( GraphicSprite sprite, Graphics2D g2 )
	{
		if( sprite.getNodeAttachment() != null )
		{
			renderSpriteAttachedToNode( sprite, g2 );
		}
		else if( sprite.getEdgeAttachment() != null )
		{
			renderSpriteAttachedToEdge( sprite, g2 );
		}
		else
		{
			renderSpriteNotAttached( sprite, g2 );
		}
	}
	
	public void renderSpriteShadow( GraphicSprite sprite )
	{
		// TODO
	}

	/**
	 * Compute the sprite position and ask for its rendering.
	 * @param sprite The sprite to draw.
	 * @param g2 The graphics.
	 */
	protected void renderSpriteNotAttached( GraphicSprite sprite, Graphics2D g2 )
	{
		// Units are always GU when dealing with non-attached sprites.
		
		renderSprite( sprite, g2, sprite.x, sprite.y, 0, 0 );
	}
	
	/**
	 * Compute the sprite position and ask for its rendering.
	 * @param sprite The sprite to draw.
	 * @param g2 The graphics.
	 */
	protected void renderSpriteAttachedToNode( GraphicSprite sprite, Graphics2D g2 )
	{
		GraphicNode node = sprite.getNodeAttachment();

		float radius = ctx.toGu( sprite.x, sprite.units );
		float nx     = (float) Math.cos( sprite.z ) * radius;
		float ny     = (float) Math.sin( sprite.z ) * radius;

		renderSprite( sprite, g2, node.x + nx, node.y + ny, nx, ny );
	}

	/**
	 * Compute the sprite position and ask for its rendering.
	 * @param sprite The sprite to draw.
	 * @param g2 The graphics.
	 */
	protected void renderSpriteAttachedToEdge( GraphicSprite sprite, Graphics2D g2 )
	{
		GraphicEdge edge = sprite.getEdgeAttachment();
		
		if( edge.isCurve() )
		{
			renderSpriteAttachedToCubicEdge( sprite, g2 );
		}
		else
		{
			float x  = ((GraphicNode)edge.getSourceNode()).x;
			float y  = ((GraphicNode)edge.getSourceNode()).y;
			float dx = ((GraphicNode)edge.getTargetNode()).x - x;
			float dy = ((GraphicNode)edge.getTargetNode()).y - y;
			float d  = sprite.x;							// Percent on the edge.
			float o  = ctx.toGu( sprite.y, sprite.units );	// Offset from the position given by percent, perpendicular to the edge.
			
			d = d > 1 ? 1 : d;
			d = d < 0 ? 0 : d;
			
			x += dx * d;
			y += dy * d;
			
			d   = (float) Math.sqrt( dx*dx + dy*dy );
			dx /= d;
			dy /= d;
			
			x += -dy * o;
			y +=  dx * o;
			
			renderSprite( sprite, g2, x, y, -dy*o, dx*o );
		}
	}
	
	/**
	 * Compute the sprite position along a cubic curve then request its drawing.
	 * @param sprite The sprite.
	 * @param g2 The graphics.
	 */
	protected void renderSpriteAttachedToCubicEdge( GraphicSprite sprite, Graphics2D g2 )
	{
		GraphicEdge edge = sprite.getEdgeAttachment();

		float x0     = ((GraphicNode)edge.getSourceNode()).x;
		float y0     = ((GraphicNode)edge.getSourceNode()).y;
		float x1     = ((GraphicNode)edge.getTargetNode()).x;
		float y1     = ((GraphicNode)edge.getTargetNode()).y;
		float o      = ctx.toGu( sprite.y, sprite.units );	// Offset from the position given by percent, perpendicular to the edge.
		float d      = sprite.x;							// Percent on the edge.
		float ctrl[] = edge.getControlPoints(); 
		float x, y;
		
		x = ctx.cubicCurveEval( x0, ctrl[0], ctrl[2], x1, d );
		y = ctx.cubicCurveEval( y0, ctrl[1], ctrl[3], y1, d );
		
		// We do not really follow the curve concerning the offset. 
		
		float dx = x1 - x0;
		float dy = y1 - y0;
		d = (float) Math.sqrt( dx*dx + dy*dy );
		dx/=d;
		dy/=d;
	
		x += -dy * o;
		y +=  dx * o;
		
		renderSprite( sprite, g2, x, y, -dy*o, dx*o );
	}
	
	/**
	 * Render a sprite at the given location (x,y), and considering (dx,dy) as the offset
	 * from the edge. This method looks at the sprite shape and call the corresponding rendering
	 * method. 
	 * @param sprite The sprite.
	 * @param g2 The graphics.
	 * @param x The position.
	 * @param y The position.
	 * @param dx The offset from the attachment (this offset is already applied to x).
	 * @param dy The offset from the attachment (this offset is already applied to y).
	 */
	protected void renderSprite( GraphicSprite sprite, Graphics2D g2, float x, float y, float dx, float dy )
	{
		Style             style  = sprite.getStyle();
		Style.SpriteShape shape  = style.getSpriteShape();
		
		switch( shape )
		{
			case IMAGE:
				renderSpriteImage( sprite, g2, x, y, dx, dy, false );
				break;
			case IMAGES:
				renderSpriteImage( sprite, g2, x, y, dx, dy, true );
				break;
			case ARROW:
				renderSpriteArrow( sprite, g2, x, y, dx, dy );
				break;
			case FLOW:
				renderSpriteFlow( sprite, g2, x, y, dx, dy );
				break;
			case PIE_CHART:
				renderSpritePieChart( sprite, g2, x, y, dx, dy );
				break;
			case TEXT_BOX:
				renderSpriteTextBox( sprite, g2, x, y, dx, dy );
				break;
			case TEXT_ELLIPSE:
				renderSpriteTextEllipse( sprite, g2, x, y, dx, dy );
				break;
			case CIRCLE:
			default:
				renderSpriteCircle( sprite, g2, x, y );
				break;
		}
	}
	
	/**
	 * Draw the sprite as a circle.
	 * @param sprite The sprite.
	 * @param g2 The graphics.
	 * @param x The sprite position.
	 * @param y The sprite position.
	 */
	protected void renderSpriteCircle( GraphicSprite sprite, Graphics2D g2, float x, float y )
	{
		Style style  = sprite.getStyle();
		Color color  = style.getColor();
		float width  = ctx.toGu( style.getWidth() );
		float ww     = ctx.toPixels( style.getWidth() );
		float bwidth = ctx.toPixels( style.getBorderWidth() );
		float xx     = ctx.xGuToPixels( x - width / 2 );
		float yy     = ctx.yGuToPixels( y + width / 2 );
		
		g2.setColor( color );
		oval.setFrame( xx, yy, ww, ww );
		g2.fill( oval );
		
		if( bwidth > 0 )
		{
			Color bcolor = style.getBorderColor();
			ctx.chooseBorderStroke( bwidth );
			g2.setColor( bcolor );
			g2.draw( oval );
		}
		
		if( sprite.label != null )
			ctx.drawStyledLabel( sprite.label, x, y, sprite );
		
		sprite.setBounds( x-width/2, y-width/2, width, width );
	}
	
	/**
	 * Render the sprite as an image.
	 * @param sprite The sprite.
	 * @param g2 The graphics.
	 * @param x The sprite position.
	 * @param y The sprite position.
	 * @param dx The offset from the attachment and direction vector (used to compute the image rotation).
	 * @param dy The offset from the attachment and direction vector (used to compute the image rotation).
	 * @param anim If true the image is animated (TODO not yet supported).
	 */
	protected void renderSpriteImage( GraphicSprite sprite, Graphics2D g2, float x, float y, float dx, float dy, boolean anim )
	{
		Style  style  = sprite.getStyle();
		String imgUrl = style.getImageUrl();
		int    xx     = ctx.xGuToRoundPixels( x );
		int    yy     = ctx.yGuToRoundPixels( y );
		
		if( imgUrl == null )
		{
			renderSpriteCircle( sprite, g2, x, y );
		}
		else
		{
			Image img = ctx.getImage( imgUrl );
			int   iw  = (int) ctx.toPixels( style.getWidth() );
			int   ih  = (int) ctx.toPixels( style.getHeight() );
			
			Style.SpriteOrientation orient = style.getSpriteOrientation();
			
			if( img == null )
				img = ctx.getDummyImage();
			
			AffineTransform transform = null;
		
			if( orient != Style.SpriteOrientation.NONE )
			{
				transform = g2.getTransform();
				setSpriteOrientation( g2, sprite, xx, yy, dx, dy );
				g2.drawImage( img, -iw/2, -ih/2, iw, ih, null );
				g2.setTransform( transform );
			}
			else
			{
				g2.drawImage( img, xx-iw/2, yy-ih/2, iw, ih, null );
			}
			
			if( sprite.label != null )
				ctx.drawStyledLabel( sprite.label, x, y, sprite );
			
			float ww = ctx.pixelsToGu( iw );
			float hh = ctx.pixelsToGu( ih );
			
			sprite.setBounds( x-ww/2, y-hh/2, ww, hh );
		}
	}

	/**
	 * Render the sprite as an arrow.
	 * @param sprite The sprite.
	 * @param g2 The graphics.
	 * @param x The sprite position.
	 * @param y The sprite position.
	 * @param dx The offset from the attachment and direction vector (used to compute the image rotation).
	 * @param dy The offset from the attachment and direction vector (used to compute the image rotation).
	 */
	protected void renderSpriteArrow( GraphicSprite sprite, Graphics2D g2, float x, float y, float dx, float dy )
	{
		Style style  = sprite.getStyle();
		Color color  = style.getColor();

		float x2 = 0, y2 = 0;
		
		// Ensure orientation.
		
		Style.SpriteOrientation orient = ensureOrientationValue( sprite );
		
		if( sprite.getAttachment() != null )
		{
			switch( orient )
			{
				case FROM:
					x2 = sprite.getEdgeAttachment().from.x;
					y2 = sprite.getEdgeAttachment().from.y;
					break;
				case TO:
					x2 = sprite.getEdgeAttachment().to.x;
					y2 = sprite.getEdgeAttachment().to.y;
					break;
				case ORIGIN:
					x2 = x-dx;
					y2 = y-dy;
					break;
				case NONE:
					x2 = sprite.getAttachment().getX();
					y2 = sprite.getAttachment().getY();
					break;
			}
		}
		else
		{
			x2 = x - dx;
			y2 = y - dy;
		}
		
		// Compute the edge vector (1) and the perpendicular vector (2).
		
		float vx1 = x2 - x;
		float vy1 = y2 - y;
		float vx2 =  vy1;
		float vy2 = -vx1;
		
		// Normalise the vectors.
		
		float d1 = (float) Math.sqrt( vx1*vx1 + vy1*vy1 );
		float d2 = (float) Math.sqrt( vx2*vx2 + vy2*vy2 );
		
		d1 = d1 == 0 ? 0.01f : d1;
		d1 = d2 == 0 ? 0.01f : d2;
		
		vx1 /= d1;
		vy1 /= d1;
		vx2 /= d2;
		vy2 /= d2;
		
		// Choose an arrow "width".
		
		float arrowLength = ctx.toGu( style.getHeight() );
		float arrowWidth  = ctx.toGu( style.getWidth() );
		
		vx1 *= arrowLength;
		vy1 *= arrowLength;
		vx2 *= arrowWidth/2;
		vy2 *= arrowWidth/2;
		
		// Create a polygon.
		
		//Polygon p = new Polygon();
		path.reset();
		
		path.moveTo( ctx.xGuToPixels( x + vx1 ), ctx.yGuToPixels( y + vy1 ) );
		path.lineTo( ctx.xGuToPixels( x + vx2 ), ctx.yGuToPixels( y + vy2 ) );		
		path.lineTo( ctx.xGuToPixels( x - vx2 ), ctx.yGuToPixels( y - vy2 ) );
		path.closePath();
		
		g2.setColor( color );
		g2.fill( path );
		
		int bwidth = (int) ctx.toPixels( style.getBorderWidth() );
		
		if( bwidth > 0 )
		{
			color = style.getBorderColor();
			
			ctx.chooseBorderStroke( bwidth );
			g2.setColor( color );
			g2.draw( path );
		}

		// The label.
		
		if( sprite.label != null )
			ctx.drawStyledLabel( sprite.label, x, y, sprite );
		
		// Store bounds.
		
		sprite.setBounds( x-arrowWidth/2, y-arrowWidth/2, arrowWidth, arrowWidth );
	}
	
	/**
	 * Render the sprite as a flow.
	 * @param sprite The sprite.
	 * @param g2 The graphics.
	 * @param x The sprite position (flow max).
	 * @param y The sprite position (flow max).
	 * @param dx The sprite offset from the attachment.
	 * @param dy The sprite offset from the attachment.
	 */
	protected void renderSpriteFlow( GraphicSprite sprite, Graphics2D g2, float x, float y, float dx, float dy )
	{
		GraphicEdge edge = sprite.getEdgeAttachment();
		
		if( edge != null )
		{
			if( edge.isCurve() )
			     renderSpriteFlowCurve( sprite, edge, g2, x, y, dx, dy );
			else renderSpriteFlowLine( sprite, edge, g2, x, y, dx, dy );
		}
		else
		{
			System.err.printf( "When using 'sprite-shape:flow', attach the sprite to an edge." );
			renderSpriteCircle( sprite, g2, x, y );
		}
	}
	
	protected void renderSpriteFlowLine( GraphicSprite sprite, GraphicEdge edge, Graphics2D g2, float x, float y, float dx, float dy )
	{
		Style.SpriteOrientation orient = ensureOrientationValue( sprite );

		float xx = 0, yy = 0, ox = 0, oy = 0;
		
		xx = ctx.xGuToPixels( x );
		yy = ctx.yGuToPixels( y );
		
		switch( orient )
		{
			case ORIGIN:
			case NONE:
			case FROM:
				ox = ctx.xGuToPixels( edge.from.x + dx );
				oy = ctx.yGuToPixels( edge.from.y + dy );
				break;
			case TO:
				ox = ctx.xGuToPixels( edge.to.x + dx );
				oy = ctx.yGuToPixels( edge.to.y + dy );
				break;
		}

		Style style = sprite.getStyle();
		Color color = style.getColor();
		float width = ctx.toPixels( style.getWidth() );
		
		ctx.chooseBorderStroke( width );
		g2.setColor( color );
		line.setLine( ox, oy, xx, yy );
		g2.draw( line );
		sprite.setBounds( x, y, 0, 0 );
	}
	
	protected void renderSpriteFlowCurve( GraphicSprite sprite, GraphicEdge edge, Graphics2D g2, float x, float y, float dx, float dy )
	{
		Style.SpriteOrientation orient = ensureOrientationValue( sprite );

		float ox = 0, oy = 0, tx = 0, ty = 0;
		float c1x = 0, c1y = 0, c2x = 0, c2y = 0;
		float ctrl[] = edge.getControlPoints();
		float d = sprite.x;

		ox  = ctx.xGuToPixels( edge.from.x + dx );
		oy  = ctx.yGuToPixels( edge.from.y + dy );
		c1x = ctx.xGuToPixels( ctrl[0] + dx );
		c1y = ctx.yGuToPixels( ctrl[1] + dy );
		c2x = ctx.xGuToPixels( ctrl[2] + dx );
		c2y = ctx.yGuToPixels( ctrl[3] + dy );
		tx  = ctx.xGuToPixels( edge.to.x + dx );
		ty  = ctx.yGuToPixels( edge.to.y + dy );

		Style style = sprite.getStyle();
		Color color = style.getColor();
		float width = ctx.toPixels( style.getWidth() );
		ctx.chooseBorderStroke( width );
		g2.setColor( color );
		
		switch( orient )
		{
			case ORIGIN:
			case NONE:
			case FROM:
				path.reset();
				path.moveTo( ox, oy );
				
				for( float t=0.01f; t<d; t+=0.01f )
				{
					path.lineTo(
						ctx.cubicCurveEval( ox, c1x, c2x, tx, t ),
						ctx.cubicCurveEval( oy, c1y, c2y, ty, t ) );
				}
				break;
			case TO:
				path.reset();
				path.moveTo( tx, ty );
				
				for( float t=0.99f; t>d; t-=0.01f )
				{
					path.lineTo(
							ctx.cubicCurveEval( ox, c1x, c2x, tx, t ),
							ctx.cubicCurveEval( oy, c1y, c2y, ty, t ) );
				}
				break;
		}
		
		g2.draw( path );
		sprite.setBounds( x, y, 0, 0 );		
	}
	
	protected void renderSpritePieChart( GraphicSprite sprite, Graphics2D g2, float x, float y, float dx, float dy )
	{
		Style  style  = sprite.getStyle();
		Color  color  = style.getColor();
		float  width  = ctx.toGu( style.getWidth() );
		float  ww     = ctx.toPixels( style.getWidth() );
		float  bwidth = ctx.toPixels( style.getBorderWidth() );
		float  xx     = ctx.xGuToPixels( x - width / 2 );
		float  yy     = ctx.yGuToPixels( y + width / 2 );
		Object value  = sprite.getAttribute( "pie-values" );

		Object[] values = null;
		
		if( value instanceof Object[] )
			values = (Object[]) value;
		
		if( values == null )
		{
			if( value == null || ( ! ( value instanceof Number ) ) )
				return;
			
			arc.setArc( xx, yy, ww, ww, 0, (int)(((Number)value).floatValue()*360), Arc2D.PIE );
			g2.setColor( color );
			g2.fill( arc );
		
			if( bwidth > 0 )
			{
				Color bcolor = style.getBorderColor();
				ctx.chooseBorderStroke( bwidth );
				g2.setColor( bcolor );
				g2.draw( arc );
			}
		}
		else
		{
			float startAngle = 0;
			int i = 0;
			
			for( Object n: values )
			{
				if( n instanceof Number )
				{
					ArrayList<Color> colors = style.getPalette();
					float angle = ((Number)n).floatValue();
					arc.setArc( xx, yy, ww, ww, startAngle, (int)(360*angle), Arc2D.PIE );
					g2.setColor( colors.get( i % colors.size() ) );
					g2.fill( arc );
					startAngle += (int)360*angle;
					i++;
				}
			}
				
			if( bwidth > 0 )
			{
				Color bcolor = style.getBorderColor();
				ctx.chooseBorderStroke( bwidth );
				oval.setFrame( xx, yy, ww, ww );
				g2.setColor( bcolor );
				g2.draw( oval );
			}
		}
		
		if( sprite.label != null )
			ctx.drawStyledLabel( sprite.label, x, y, sprite );
		
		sprite.setBounds( x-width/2, y-width/2, width, width );
	}
	
	protected void renderSpriteTextBox( GraphicSprite sprite, Graphics2D g2, float x, float y, float dx, float dy )
	{
		if( sprite.label == null || sprite.label.length() == 0 )
		{
			renderSpriteCircle( sprite, g2, x, y );
		}
		else
		{
			ctx.drawTextBox( sprite.label, x, y, sprite, false );
		}
	}
	
	protected void renderSpriteTextEllipse( GraphicSprite sprite, Graphics2D g2, float x, float y, float dx, float dy )
	{
		if( sprite.label == null || sprite.label.length() == 0 )
		{
			renderSpriteCircle( sprite, g2, x, y );
		}
		else
		{
			ctx.drawTextBox( sprite.label, x, y, sprite, true );
		}
	}
	
	protected Style.SpriteOrientation ensureOrientationValue( GraphicSprite sprite )
	{
		Style.SpriteOrientation orient = sprite.getStyle().getSpriteOrientation();
		
		switch( orient )
		{
			case FROM:
			case TO:
				if( sprite.getEdgeAttachment() == null )
					orient = Style.SpriteOrientation.NONE;
				break;
			case ORIGIN:
				if( sprite.getEdgeAttachment() == null && sprite.getNodeAttachment() == null )
					orient = Style.SpriteOrientation.NONE;
				break;
			case NONE:
		}
		
		return orient;
	}
	
	protected void setSpriteOrientation( Graphics2D g2, GraphicSprite sprite, float x, float y, float ox, float oy )
	{
		float angle = 0;
		
		// Ensure orientation.
		
		Style.SpriteOrientation orient = ensureOrientationValue( sprite );
		
		// Compute the orientation angle
		
		GraphicEdge edge = sprite.getEdgeAttachment();
		float dx = 0 , dy = 0, d = 1;
		
		switch( orient )
		{
			case FROM:
				dx = edge.from.x - x;
				dy = edge.from.y - y;
				break;
			case TO:
				dx = edge.from.x - x;
				dy = edge.from.y - y;
				break;
			case ORIGIN:
				dx = edge.from.x - edge.to.x;
				dy = edge.from.y - edge.to.y;
				break;
			case NONE:
				return;
		}
		
		// Compute the angle between vector (0,1,0), therefore only the 'dy' part is
		// interesting here.
		
		d = (float)Math.sqrt(dx*dx+dy*dy);
		d = d == 0 ? 0.01f : d;
		angle = dy / d;

		// The angle is not oriented, if the x of our vector is positive we must add PI.
		
		if( dx > 0 )
		     angle = (float) ( Math.acos( -angle ) + Math.PI );
		else angle = (float) Math.acos( angle );

		// Translate and rotate :
		// We want something that point toward the origin of the sprite that is on the edge.
		// Therefore we add 45� to the angle made by the edge vector and (0,1,0).
		
		g2.translate( x, y );
	//	g2.rotate( -(angle+(Math.PI/2)) );
		g2.rotate( -angle );
	}
}