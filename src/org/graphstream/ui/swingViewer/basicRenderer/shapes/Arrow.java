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

package org.graphstream.ui.swingViewer.basicRenderer.shapes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import org.graphstream.ui.graphicGraph.GraphicEdge;
import org.graphstream.ui.graphicGraph.GraphicNode;
import org.graphstream.ui.graphicGraph.StyleGroup;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.StrokeMode;
import org.graphstream.ui.swingViewer.util.GraphMetrics;

public class Arrow extends Shape
{
	protected Color fillColor = Color.BLACK;
	
	protected Color strokeColor = Color.BLACK; 
	
	protected int lengthGu = 0;
	
	protected int widthGu = 0;
	
	protected float x, y;
	
	protected Path2D.Float path = new Path2D.Float();
	
	public void setArrowLengthGu( int lengthGu )
	{
		this.lengthGu = lengthGu;
	}
	
	public void setArrowWidthGu( int widthGu )
	{
		this.widthGu = widthGu;
	}
	
	public void setFillColor( Color color )
	{
		fillColor = color;
	}
	
	public void setStrokeColor( Color color )
	{
		strokeColor = color;
	}

	@Override
    public void renderFill( Graphics2D g, GraphMetrics metrics )
    {
		g.setColor( fillColor );
		g.fill( path );
    }

	@Override
    public void renderStroke( Graphics2D g, GraphMetrics metrics )
    {
		g.setColor( strokeColor );
		g.draw( path );
    }
	
// Utility
	
	protected void setPositionAndShape( GraphicEdge edge, GraphMetrics metrics )
	{
		// Compute the direction vector and some lengths.
		
		x         = edge.to.x;
		y         = edge.to.y;
		float vx  = x - edge.from.x;
		float vy  = y - edge.from.y;
		float off = evalTargetRadius( edge, metrics );
		
		// Normalise the vectors.
		
		float d = (float) Math.sqrt( vx*vx + vy*vy );
		
		vx /= d;
		vy /= d;
		
		// Choose an arrow "length".
		
		x -= vx * off;
		y -= vy * off;
		
		setShapeAt( x, y, vx, vy );
	}

	/**
	 * Compute the shape of the arrow.
	 * @param x Point at which the edge crosses the node shape.
	 * @param y Point at which the edge crosses the node shape.
	 * @param dx The arrow vector (and length).
	 * @param dy The arrow vector (and length).
	 */
	protected void setShapeAt( float x, float y, float dx, float dy )
	{
		// Compute the edge vector (1) and the perpendicular vector (2).
		
		float dx2 =  dy;
		float dy2 = -dx;
		
		// Normalise the vectors.
		
		float d2 = (float) Math.sqrt( dx2*dx2 + dy2*dy2 );
		
		dx2 /= d2;
		dy2 /= d2;
		
		// Choose an arrow "width".
		
		dx2 *= widthGu;
		dy2 *= widthGu;
		
		// Create a polygon.
		
		path.reset();
		path.moveTo( x,            y );
		path.lineTo( x - dx + dx2, y - dy + dy2 );		
		path.lineTo( x - dx - dx2, y - dy - dy2 );
		path.closePath();
	}

	/**
	 * Evaluate the position of the arrow to avoid putting it above or under the target node.
	 * @param edge The edge.
	 * @param metrics The metrics.
	 * @return The length from the node centre along the edge to position the arrow.
	 */
	protected float evalTargetRadius( GraphicEdge edge, GraphMetrics metrics )
	{
		GraphicNode target = edge.to;
		StyleGroup  group  = target.getStyle();
		float       w      = metrics.lengthToGu( group.getSize(), 0 );
		float       h      = group.getSize().size() > 1 ? metrics.lengthToGu( group.getSize(), 1 ) : w;
		
		if( w == h )
		{
			float b = group.getStrokeMode() != StrokeMode.NONE ? metrics.lengthToGu( group.getStrokeWidth() ) : 0;
			return( ( w / 2 ) + b );
		}
		else
		{
			return evalEllipseRadius( edge, w, h );
		}
	}

	/**
	 * Compute the length of a vector along the edge from the ellipse centre to the intersection
	 * between the edge and the ellipse.
	 * @param edge The edge representing the vector.
	 * @param w The ellipse first radius (width/2).
	 * @param h The ellipse second radius (height/2).
	 * @return The length of the radius along the edge vector.
	 */
	protected float evalEllipseRadius( GraphicEdge edge, float w, float h )
	{
		// Vector of the entering edge.

		float dx;
		float dy;

		dx = edge.to.x - edge.from.x;
		dy = edge.to.y - edge.from.y;
		
		// The entering edge must be deformed by the ellipse ratio to find the correct angle.

		dy *= (w/h);	// I searched a lot to find this line was missing ! Tsu ! This comment is in memory of this long search.

		// Find the angle of the entering vector with (1,0).

		float d  = (float) Math.sqrt( dx*dx + dy*dy );
		float a  = dx/d;

		// Compute the coordinates at which the entering vector and the ellipse cross.

		a  = (float) Math.acos(a);
		dx = (float) Math.cos(a) * w;
		dy = (float) Math.sin(a) * h;

		// The distance from the ellipse centre to the crossing point of the ellipse and
		// vector. Yo !

		return (float) Math.sqrt( dx*dx+dy*dy );
	}
}
