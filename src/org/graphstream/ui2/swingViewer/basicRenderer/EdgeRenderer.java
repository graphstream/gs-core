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

package org.graphstream.ui2.swingViewer.basicRenderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import org.graphstream.ui2.graphicGraph.GraphicEdge;
import org.graphstream.ui2.graphicGraph.GraphicElement;
import org.graphstream.ui2.graphicGraph.GraphicNode;
import org.graphstream.ui2.graphicGraph.StyleGroup;
import org.graphstream.ui2.graphicGraph.stylesheet.StyleConstants.FillMode;
import org.graphstream.ui2.swingViewer.util.Camera;

public class EdgeRenderer extends ElementRenderer
{
	protected Line2D shape = new Line2D.Float();

	@Override
	protected void setupRenderingPass( StyleGroup group, Graphics2D g, Camera camera )
	{
	}

	@Override
	protected void pushDynStyle( StyleGroup group, Graphics2D g, Camera camera,
	        GraphicElement element )
	{
		Color color = group.getFillColor( 0 );
		
		if( element != null && group.getFillMode() == FillMode.DYN_PLAIN )
			color = interpolateColor( group, element );
		
		g.setColor( color );
	}

	@Override
	protected void pushStyle( StyleGroup group, Graphics2D g, Camera camera )
	{
		float width = camera.getMetrics().lengthToGu( group.getSize(), 0 );

		g.setColor( group.getFillColor( 0 ) );
		g.setStroke( new BasicStroke( width ) );
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
	}
}