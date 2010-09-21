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
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;

import org.graphstream.ui.geom.Vector2;
import org.graphstream.ui.graphicGraph.GraphicEdge;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.GraphicNode;
import org.graphstream.ui.graphicGraph.StyleGroup;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants;
import org.graphstream.ui.graphicGraph.stylesheet.Values;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.FillMode;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.SizeMode;
import org.graphstream.ui.swingViewer.util.Camera;

public class EdgeRenderer extends ElementRenderer
{
	protected Line2D shape = new Line2D.Float();

	protected float width = 1;
	
	protected float arrowLength = 0;
	
	protected float arrowWidth = 0;
	
	@Override
	protected void setupRenderingPass( StyleGroup group, Graphics2D g, Camera camera )
	{
		configureText( group, camera );
	}

	@Override
	protected void pushDynStyle( StyleGroup group, Graphics2D g, Camera camera,
	        GraphicElement element )
	{
		Color color = group.getFillColor( 0 );
		
		if( element != null && group.getFillMode() == FillMode.DYN_PLAIN )
			color = interpolateColor( group, element );
		
		g.setColor( color );
		
		if( group.getSizeMode() == SizeMode.DYN_SIZE )
		{
			width  = camera.getMetrics().lengthToGu( StyleConstants.convertValue( element.getAttribute( "ui.size" ) ) );
//			width = camera.getMetrics().lengthToGu( (float) element.getNumber( "ui.size" ), Units.PX );
			
			g.setStroke( new BasicStroke( width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL ) );
		}
	}

	@Override
	protected void pushStyle( StyleGroup group, Graphics2D g, Camera camera )
	{
		width       = camera.getMetrics().lengthToGu( group.getSize(), 0 );
		arrowLength = camera.getMetrics().lengthToGu( group.getArrowSize(), 0 );
		arrowWidth  = camera.getMetrics().lengthToGu( group.getArrowSize(), group.getArrowSize().size() > 1 ? 1 : 0 );

		g.setColor( group.getFillColor( 0 ) );
		g.setStroke( new BasicStroke( width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL ) );
	}

	@Override
	protected void elementInvisible( StyleGroup group, Graphics2D g, Camera camera,
	        GraphicElement element )
	{
	}

	@Override
	protected void renderElement( StyleGroup group, Graphics2D g, Camera camera,
	        GraphicElement element )
	{
		GraphicEdge edge  = (GraphicEdge) element;
		GraphicNode node0 = (GraphicNode) edge.getNode0();
		GraphicNode node1 = (GraphicNode) edge.getNode1();
		
		shape.setLine( node0.x, node0.y, node1.x, node1.y );
		g.draw( shape );
		renderArrow( group, g, camera, edge );
		renderText( group, g, camera, element );
	}
	
	protected void renderArrow( StyleGroup group, Graphics2D g, Camera camera, GraphicEdge edge ) 
	{
	    if( edge.isDirected() && arrowWidth > 0 && arrowLength > 0 )
	    {
	    	Path2D shape = new Path2D.Float();
		GraphicNode node0 = (GraphicNode) edge.getNode0();
		GraphicNode node1 = (GraphicNode) edge.getNode1();
		float off = evalEllipseRadius( edge, node0, node1, camera );
		Vector2 theDirection = new Vector2(
			node1.getX() - node0.getX(),
			node1.getY() - node0.getY() );
			
		theDirection.normalize();
  
		float   x    = node1.x - ( theDirection.data[0] * off );
		float   y    = node1.y - ( theDirection.data[1] * off );
		Vector2 perp = new Vector2( theDirection.data[1], -theDirection.data[0] );
		
		perp.normalize();
		theDirection.scalarMult( arrowLength );
		perp.scalarMult( arrowWidth );
		
		// Create a polygon.
		
		shape.reset();
		shape.moveTo( x , y );
		shape.lineTo( x - theDirection.data[0] + perp.data[0], y - theDirection.data[1] + perp.data[1] );		
		shape.lineTo( x - theDirection.data[0] - perp.data[0], y - theDirection.data[1] - perp.data[1] );
		shape.closePath();
		
		g.fill( shape );
	    }
	}
	
	protected float evalEllipseRadius( GraphicEdge edge, GraphicNode node0, GraphicNode node1, Camera camera ) {
	    	Values size = node0.getStyle().getSize();
	    	float w = camera.getMetrics().lengthToGu( size.get( 0 ), size.getUnits() );
	    	float h = size.size() > 1 ? camera.getMetrics().lengthToGu( size.get( 1 ), size.getUnits() ) : w; 
	    
	  	if( w == h )
 	  	     return w / 2;	// Welcome simplification for circles ...

		// Vector of the entering edge.

		float dx = node1.getX() - node0.getX();
		float dy = node1.getY() - node0.getY();
		
		// The entering edge must be deformed by the ellipse ratio to find the correct angle.

		dy *= ( w / h );

		// Find the angle of the entering vector with (1,0).

		float d  = (float) Math.sqrt( dx*dx + dy*dy );
		float a  = dx / d;

		// Compute the coordinates at which the entering vector and the ellipse cross.

		a  = (float) Math.acos( a );
		dx = (float) ( Math.cos( a ) * w );
		dy = (float) ( Math.sin( a ) * h );

		// The distance from the ellipse centre to the crossing point of the ellipse and
		// vector. Yo !

		return (float) Math.sqrt( dx*dx + dy*dy );
	}
}