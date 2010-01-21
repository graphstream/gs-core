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

package org.graphstream.oldUi.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Map;

import org.graphstream.oldUi.graphicGraph.GraphicEdge;
import org.graphstream.oldUi.graphicGraph.GraphicGraph;
import org.graphstream.oldUi.graphicGraph.GraphicNode;
import org.graphstream.oldUi.graphicGraph.stylesheet.Style;
import org.graphstream.ui2.geom.Point3;

/**
 * Swing EdgeRenderer.
 *
 * TODO: draw images aside labels if they are specified ?
 *
 * @since 20061226
 */
public class SwingEdgeRenderer
{
	/**
	 * The rendering context.
	 */
	protected Context ctx;
	
	/**
	 * To avoid creating a shape at each redraw.
	 */
	protected Line2D line = new Line2D.Float();
	
	/**
	 * To avoid creating a shape at each redraw.
	 */
	protected Ellipse2D oval = new Ellipse2D.Float();
	
	/**
	 * To avoid creating a shape at each redraw.
	 */
	protected CubicCurve2D cubic = new CubicCurve2D.Float();
	
	/**
	 * To avoid creating a shape at each redraw.
	 */
	protected GeneralPath path = new GeneralPath();
	
	public void setContext( Context ctx )
	{
		this.ctx = ctx;
	}
	
	public String[] getAttributeSet()
	{
		String list[] = { "width", "color", "edge-graphics" };
		return list;
	}

	public void renderEdge( GraphicEdge edge )
	{
		if( ctx != null && ctx.drawEdges )
		{
			Graphics2D g2 = ctx.g2;
			
			g2.setStroke( Context.defLineStroke );
	
			renderEdge( edge, g2 );
		}
	}

	public void renderEdges( GraphicGraph graph )
	{
		if( ctx != null )
		{
			Graphics2D g2 = ctx.g2;
			
			for( GraphicEdge edge : graph.getEdges() )
				renderEdge( edge, g2 );
			
			g2.setStroke( Context.defLineStroke );
		}
	}

	public void renderEdges( Map<String,GraphicEdge> edges )
	{
		if( ctx != null && ctx.drawEdges )
		{
			Graphics2D g2 = ctx.g2;
			
			for( GraphicEdge edge : edges.values() )
				renderEdge( edge, g2 );
			
			g2.setStroke( Context.defLineStroke );
		}
	}
	
	/**
	 * Render the given edge. This method makes the nodeSelection between edges. There are five cases:
	 * 1 a loop, 2 a multi-edge, 3 an angle, 4 a cubic-curve, 5 a line. There should be three
	 * cases only (three styles, angle, cubic, line). But this renderer does not yet know how
	 * to draw loop and multi-edges with the angle property. Futhermore as they are drawn using
	 * cubics, they cannot be lines. When the category of the edge is chosen, the correct rendering
	 * method is called. 
	 * @param edge The edge.
	 * @param g2 The graphics.
	 */
	protected void renderEdge( GraphicEdge edge, Graphics2D g2 )
	{
		if( edge.hasAttribute( "ui.hide" )
		 || edge.getNode0().hasAttribute( "ui.hide" )
		 || edge.getNode1().hasAttribute( "ui.hide" ) )
			return;
		
		Style           style = edge.getStyle();
		Style.EdgeShape shape = style.getEdgeShape();
		
		if( edge.from.getId().equals( edge.to.getId() ) )
		{
			renderLineLoopEdge( edge, g2 );
		}
		else
		{
			if( shape == Style.EdgeShape.POINTS )
			{
				// The edge shape is imposed.
//				renderEdgePoints( edge, g2 );
				renderLineEdge( edge, g2 );
			}
			else
			{
				if( edge.getGroup() != null )
				{
					renderLineEdgeMulti( edge, g2 );
				}
				else
				{
					switch( shape )
					{
						case ANGLE:
							renderAngleEdge( edge, g2 );
							break;
						case CUBIC_CURVE:
							renderCubicCurve( edge, g2 );
							break;
						case LINE:
						default:
							renderLineEdge( edge, g2 );
							break;
					}
				}
			}
		}
	}
	
	public void renderEdgeShadow( GraphicEdge edge )
	{
		if( edge.hasAttribute( "ui.hide" )
				 || edge.getNode0().hasAttribute( "ui.hide" )
				 || edge.getNode1().hasAttribute( "ui.hide" ) )
					return;
				
		Style           style = edge.getStyle();
		Style.EdgeShape shape = style.getEdgeShape();
		Graphics2D      g2    = ctx.g2;
				
		if( edge.from.getId().equals( edge.to.getId() ) )
		{
//			renderLineLoopEdgeShadow( edge, g2 );
		}
		else
		{
			if( shape == Style.EdgeShape.POINTS )
			{
				// The edge shape is imposed.
//				renderEdgePointsShadow( edge, g2 );
			}
			else
			{
				if( edge.getGroup() != null )
				{
//					renderLineEdgeMultiShadow( edge, g2 );
				}
				else
				{
					switch( shape )
					{
						case ANGLE:
//							renderAngleEdgeShadow( edge, g2 );
							break;
						case CUBIC_CURVE:
//							renderCubicCurveShadow( edge, g2 );
							break;
						case LINE:
						default:
							renderLineEdgeShadow( edge, g2 );
							break;
					}
				}
			}
		}		
	}
	
	/**
	 * Render an edge as a straight line.
	 * @param edge The edge.
	 * @param g2 The graphics.
	 */
	protected void renderLineEdge( GraphicEdge edge, Graphics2D g2 )
	{
		Style       style  = edge.getStyle(); 
		GraphicNode node1  = edge.from;
		GraphicNode node2  = edge.to;
		Color       color  = style.getColor();
		float       bwidth = ctx.toPixels( style.getBorderWidth() );
		
		float   x1 = ctx.xGuToPixels( node1.x );
		float   x2 = ctx.xGuToPixels( node2.x );
		float   y1 = ctx.yGuToPixels( node1.y );
		float   y2 = ctx.yGuToPixels( node2.y );
		String  s  = edge.label;
		
		line.setLine( x1, y1, x2, y2 );

		if( bwidth > 0 )
		{
			Color bcolor = style.getBorderColor();
	
			ctx.chooseBorderStroke( bwidth*2+ctx.toPixels( style.getWidth() )  );
			g2.setColor( bcolor );
			g2.draw( line );
		}
		
		chooseEdgeStroke( g2, edge, false, 0 );

		g2.setColor( color );
		g2.draw( line );
		
		if( edge.directed && ctx.drawEdgeArrows )
			drawEdgeArrow( edge, g2, false );
		
		if( s != null && ctx.drawEdgeLabels )
			ctx.drawStyledLabel( s, node1.x + ((node2.x-node1.x)/2), node1.y + ((node2.y-node1.y)/2), edge );
	}

	protected void renderLineEdgeShadow( GraphicEdge edge, Graphics2D g2 )
	{
		Style       style  = edge.getStyle(); 
		GraphicNode node1  = edge.from;
		GraphicNode node2  = edge.to;
		Color       color  = style.getShadowColor();
		float       swidth = ctx.toPixels( style.getShadowWidth() );
		float       offx   = ctx.toPixels( style.getShadowOffsetX() );
		float       offy   = ctx.toPixels( style.getShadowOffsetY() );
		
		float   x1 = ctx.xGuToPixels( node1.x ) + offx;
		float   x2 = ctx.xGuToPixels( node2.x ) + offx;
		float   y1 = ctx.yGuToPixels( node1.y ) + offy;
		float   y2 = ctx.yGuToPixels( node2.y ) + offy;
	
		chooseEdgeStroke( g2, edge, false, swidth * 2 );
		line.setLine( x1, y1, x2, y2 );
		g2.setColor( color );
		g2.draw( line );
		
//		if( edge.directed && ctx.drawEdgeArrows )
//			drawEdgeArrowShadow( edge, g2, false );
	}
	
	/**
	 * If the edge is a a multi-edge, change it into a cubic curve that allows avoid drawing it
	 * on its peers. Then call the cubic curve renderer.
	 * @param edge The edge.
	 * @param g2 The graphics.
	 */
	protected void renderLineEdgeMulti( GraphicEdge edge, Graphics2D g2 )
	{
		GraphicEdge.EdgeGroup group = edge.getGroup();
		GraphicEdge main = group.getEdge( 0 );
		
		Style style  = edge.getStyle(); 
		Color color  = style.getColor();
/*		float x1     = main.from.x;
		float y1     = main.from.y;
		float x2     = main.to.x;
		float y2     = main.to.y;
*/		float x1     = edge.from.x;
		float y1     = edge.from.y;
		float x2     = edge.to.x;
		float y2     = edge.to.y;
		float vx     = x2 - x1;
		float vy     = y2 - y1;
		float vx2    =  vy;
		float vy2    = -vx;
		float ctrl[] = edge.getControlPoints();
		int   multi  = edge.getMultiIndex();
		float gap    = 0.2f;
		float f      = ((1+multi)/2) * gap;
		float bwidth = ctx.toPixels( style.getBorderWidth() );
		
		if( ctrl == null )
			ctrl = new float[4];
		
		edge.setControlPoints( ctrl );
		
		vx  *= 0.2f;
		vy  *= 0.2f;
		vx2 *= 0.6f;
		vy2 *= 0.6f;
		
		float ox = 0;
		float oy = 0;
	
		if( group.getCount() %2 == 0 )
		{
			if( edge.from != main.from )
			{
				ox = - vx2 * (gap/2);
				oy = - vy2 * (gap/2);
			}
			else
			{
				ox = vx2 * (gap/2);
				oy = vy2 * (gap/2);
			}
		}

		vx2 *= f;
		vy2 *= f;

		ctrl[0] = x1 + vx;
		ctrl[1] = y1 + vy;
		ctrl[2] = x2 - vx;
		ctrl[3] = y2 - vy;
	
		multi += edge.from == main.from ? 0 : 1;
		
		if( multi % 2 == 0 )
		{
			ctrl[0] += vx2 + ox;
			ctrl[1] += vy2 + oy;
			ctrl[2] += vx2 + ox;
			ctrl[3] += vy2 + oy;
		}
		else
		{
			ctrl[0] -= vx2 - ox;
			ctrl[1] -= vy2 - oy;
			ctrl[2] -= vx2 - ox;
			ctrl[3] -= vy2 - oy;			
		}

		if( bwidth > 0 )
		{
			Color bcolor = style.getBorderColor();
	
			ctx.chooseBorderStroke( bwidth*2+ctx.toPixels( style.getWidth() )  );
			g2.setColor( bcolor );
			renderCubicCurve( edge, g2, x1, y1, ctrl, x2, y2 );
		}

/*		This code draws the control points :
		g2.setColor( Color.RED );
		g2.drawLine(
				(int)ctx.xGuToPixels( x1 ),      (int)ctx.yGuToPixels( y1 ),
				(int)ctx.xGuToPixels( ctrl[0] ), (int)ctx.yGuToPixels( ctrl[1] ) );
		g2.drawLine(
				(int)ctx.xGuToPixels( x2 ),      (int)ctx.yGuToPixels( y2 ),
				(int)ctx.xGuToPixels( ctrl[2] ), (int)ctx.yGuToPixels( ctrl[3] ) );
*/
		chooseEdgeStroke( g2, edge, false, 0 );
		g2.setColor( color );
		renderCubicCurve( edge, g2, x1, y1, ctrl, x2, y2 );
	}
	
	/**
	 * Render an edge as a loop from a node to the same node. This makes the edge a cubic curve.
	 * @param edge The edge.
	 * @param g2 The graphics.
	 */
	protected void renderLineLoopEdge( GraphicEdge edge, Graphics2D g2 )
	{
		Style       style  = edge.getStyle(); 
		GraphicNode node   = edge.from;
		Color       color  = style.getColor();
		float       width  = ctx.toGu( node.getStyle().getWidth() );
		float       ctrl[] = edge.getControlPoints();
		float       multi  = 1 + edge.getMultiIndex() * 0.2f;
		float       bwidth = ctx.toPixels( style.getBorderWidth() );
		
		if( ctrl == null )
			ctrl = new float[4];
	
		edge.setControlPoints( ctrl );
		
		assert node == edge.to : "not loop edge ??";
		
		float x0 = node.x;
		float y0 = node.y;
		ctrl[0]  = x0 + width/2*multi + 4*width*multi;
		ctrl[1]  = y0;
		ctrl[2]  = x0;
		ctrl[3]  = y0 + width/2*multi + 4*width*multi;
		float x3 = x0;
		float y3 = y0 + width/2*multi;
		
		x0 += width/2;
		
		String s = edge.label;

		cubic.setCurve(
			ctx.xGuToPixels( x0 ),
			ctx.yGuToPixels( y0 ),
			ctx.xGuToPixels( ctrl[0] ),
			ctx.yGuToPixels( ctrl[1] ),
			ctx.xGuToPixels( ctrl[2] ),
			ctx.yGuToPixels( ctrl[3] ),
			ctx.xGuToPixels( x3 ),
			ctx.yGuToPixels( y3 ) );
		
		if( bwidth > 0 )
		{
			Color bcolor = style.getBorderColor();
	
			ctx.chooseBorderStroke( bwidth*2+ctx.toPixels( style.getWidth() )  );
			g2.setColor( bcolor );
			g2.draw( cubic );
		}
		
		chooseEdgeStroke( g2, edge, false, 0 );
		g2.setColor( color );
		g2.draw( cubic );

		if( edge.directed && ctx.drawEdgeArrows )
			drawEdgeArrow( edge, g2, true );
		
		if( s != null && ctx.drawEdgeLabels )
			ctx.drawStyledLabel( s,
					ctrl[0] + ( ctrl[2] - ctrl[0] ) / 2,
					ctrl[1] + ( ctrl[3] - ctrl[1] ) / 2, edge );		
	}
	
	/**
	 * Render an edge as an 'angle'.
	 * @param edge The edge.
	 * @param g2 The graphics.
	 */
	protected void renderAngleEdge( GraphicEdge edge, Graphics2D g2 )
	{
		if( ! edge.directed || ! ctx.drawEdgeArrows )
		{
			renderLineEdge( edge, g2 );
		}
		else
		{
			Style       style = edge.getStyle(); 
			GraphicNode node1 = edge.from;
			GraphicNode node2 = edge.to;
			Color       color = style.getColor();
			float       width = ctx.toPixels( style.getWidth() );
			float       bwith = ctx.toPixels( style.getBorderWidth() );
		
			float   x1 = ctx.xGuToPixels( node1.x );
			float   x2 = ctx.xGuToPixels( node2.x );
			float   y1 = ctx.yGuToPixels( node1.y );
			float   y2 = ctx.yGuToPixels( node2.y );
			String  s  = edge.label;

			// Make a vector perpendicular to the edge.
			
			float x3 =  ( y2 - y1 );
			float y3 = -( x2 - x1 );
			float d  =  (float) Math.sqrt( x3*x3 + y3*y3 );
			x3       =  ( ( x3 / d ) * width );
			y3       =  ( ( y3 / d ) * width );
			
			// Now draw.

			g2.setColor( color );
			path.reset();
			path.moveTo( x2, y2 );
			path.lineTo( x1+x3, y1+y3 );
			path.lineTo( x1-x3, y1-y3 );
			path.closePath();
			g2.fill( path );
			
			if( bwith > 0 )
			{
				Color bcolor = style.getBorderColor();
				
				chooseEdgeStroke( g2, edge, true, 0 );
				g2.setColor( bcolor );
				g2.draw( path );
			}
			
			// The edge label.
			
			if( s != null && ctx.drawEdgeLabels )
				ctx.drawStyledLabel( s, node1.x + ((node2.x-node1.x)/2), node1.y + ((node2.y-node1.y)/2), edge );
		}
	}
	
	/**
	 * Render an edge as series of straight line given by points in the style sheet.
	 * @param edge The edge.
	 * @param g2 The graphics.
	 */
	protected void renderEdgePoints( GraphicEdge edge, Graphics2D g2 )
	{
		Style             style  = edge.getStyle(); 
		GraphicNode       node1  = edge.from;
		GraphicNode       node2  = edge.to;
		Color             color  = style.getColor();
		float             bwidth = ctx.toPixels( style.getBorderWidth() );
		ArrayList<Point3> points = new ArrayList<Point3>();
		String            s      = edge.label;
		
		points.add( new Point3( node1.x, node1.y, 0 ) );
		points.addAll( style.getEdgePoints() );
		points.add( new Point3( node2.x, node2.y, 0 ) );

		int n = points.size();
		
		if( bwidth > 0 )
		{
			Color bcolor = style.getBorderColor();
	
			ctx.chooseBorderStroke( bwidth*2+ctx.toPixels( style.getWidth() )  );
			g2.setColor( bcolor );

			for( int i=1; i<n; ++i )
			{
				line.setLine( points.get(i-1).x, points.get(i-1).y,
				              points.get(i).x,   points.get(i).y );
				g2.draw( line );
			}
		}
		
		chooseEdgeStroke( g2, edge, false, 0 );
		g2.setColor( color );

		for( int i=1; i<n; ++i )
		{
			float x0 = ctx.xGuToPixels( points.get(i-1).x );
			float y0 = ctx.yGuToPixels( points.get(i-1).y );
			float x1 = ctx.xGuToPixels( points.get(i).x );
			float y1 = ctx.yGuToPixels( points.get(i).y );
			
			line.setLine( x0, y0, x1, y1 );
			g2.draw( line );
		}
		
		if( edge.directed && ctx.drawEdgeArrows )
		{
			float x  = points.get(n-1).x;
			float y  = points.get(n-1).y;
			float dx = x - points.get(n-2).x;
			float dy = y - points.get(n-2).y;
			
			drawEdgeArrow( edge, g2, x, y, dx, dy );
		}
		
		if( s != null && ctx.drawEdgeLabels )
		{
			Point3 p1 = points.get( n/2 );
			
			ctx.drawStyledLabel( s, p1.x, p1.y, edge );
		}
	}
	
	/**
	 * Render an edge as a cubic curve. The cubic curve control points are computed, then the
	 * cubic curve renderer is called.
	 * @param edge The edge.
	 * @param g2 The graphics.
	 */
	protected void renderCubicCurve( GraphicEdge edge, Graphics2D g2 )
	{
		Style       style  = edge.getStyle(); 
		GraphicNode node1  = edge.from;
		GraphicNode node2  = edge.to;
		Color       color  = style.getColor();
		float       x1     = node1.x;
		float       x2     = node2.x;
		float       y1     = node1.y;
		float       y2     = node2.y;
		float       bwidth = ctx.toPixels( style.getBorderWidth() );

		// The edge vector.
		
		float vx    = x2 - x1;
		float vy    = y2 - y1;
		
		// The control points of the curve will depend on the vector length.
		
		float d     = (float) Math.sqrt( vx*vx + vy*vy );
		float delta = d / 2.5f;
		
		// The idea is to look at the angle between a vector at noon and the edge vector.
		// Then four zones are delimited around the nodes, called quarters north, east, south
		// and west. Each quarter is 90� (45� around each axis). A very simple dot product
		// give us the angle. The constant 0.707107 is cos(45�) degree (and -0.707107 is
		// cos(135�). They delimit the quarter east. We look at the sign of vx to know if we
		// are at east or at west. Then we compare to 0.707107 to know in which quarter we are.
		
		float angle  = vy / d;	// Our dot product between [vx,vy] and [0,1], simplifies to this.
		float ctrl[] = edge.getControlPoints();
		
		if( ctrl == null || ctrl.length != 4 )
			ctrl = new float[4];
		
		if( ( vx >= 0 && angle > 0.707107f ) || ( vx < 0 && angle > 0.707107f ) )
		{
			// In quarters north (to) and south (from).

			ctrl[0] = x1;
			ctrl[1] = y1 + delta;
			ctrl[2] = x2;
			ctrl[3] = y2 - delta;
		}
		else if( vx >= 0 && angle <= 0.707107f && angle >= -0.707107f )
		{
			// In quarters east (to) and west (from).
			
			ctrl[0] = x1 + delta;
			ctrl[1] = y1;
			ctrl[2] = x2 - delta;
			ctrl[3] = y2;
		}
		else if( ( vx >= 0 && angle < -0.707107f ) || ( vx < 0 && angle < -0.707107f ) )
		{
			// In quarters north (from) and south (to).

			ctrl[0] = x1;
			ctrl[1] = y1 - delta;
			ctrl[2] = x2;
			ctrl[3] = y2 + delta;
		}
		else //if( vx < 0 && angle <= 0.707107f && angle >= -0.707107f )
		{
			// In quarters east (from) and west (to).
			
			ctrl[0] = x1 - delta;
			ctrl[1] = y1;
			ctrl[2] = x2 + delta;
			ctrl[3] = y2;
		}
		
		edge.setControlPoints( ctrl );

		
		if( bwidth > 0 )
		{
			Color bcolor = style.getBorderColor();
	
			ctx.chooseBorderStroke( bwidth*2+ctx.toPixels( style.getWidth() )  );
			g2.setColor( bcolor );
			renderCubicCurve( edge, g2, x1, y1, ctrl, x2, y2 );
		}
		
		// Draw the curve.

		chooseEdgeStroke( g2, edge, false, 0 );
		g2.setColor( color );
		renderCubicCurve( edge, g2, x1, y1, ctrl, x2, y2 );
	}
	
	/**
	 * Cubic curve renderer.
	 * @param edge The edge.
	 * @param g2 The graphics.
	 * @param x1 The edge start position.
	 * @param y1 The edge start position.
	 * @param ctrl The edge control points.
	 * @param x2 The edge end position.
	 * @param y2 The edge end position.
	 */
	protected void renderCubicCurve( GraphicEdge edge, Graphics2D g2, float x1, float y1, float ctrl[], float x2, float y2 ) 
	{
		String      s     = edge.label;
		GraphicNode node1 = edge.from;
		GraphicNode node2 = edge.to;

		cubic.setCurve(
				ctx.xGuToPixels( x1 ),
				ctx.yGuToPixels( y1 ),
				ctx.xGuToPixels( ctrl[0] ),
				ctx.yGuToPixels( ctrl[1] ),
				ctx.xGuToPixels( ctrl[2] ),
				ctx.yGuToPixels( ctrl[3] ),
				ctx.xGuToPixels( x2 ),
				ctx.yGuToPixels( y2 ) );

		g2.draw( cubic );

		if( edge.directed && ctx.drawEdgeArrows )
			drawEdgeArrow( edge, g2, false );
	
		if( s != null && ctx.drawEdgeLabels )
			ctx.drawStyledLabel( s, ( node1.x + node2.x ) / 2, ( node1.y + node2.y ) / 2, edge );
	}

	/**
	 * Draw and arrow on the end of the edge (near the "to" node).
	 * @param edge The edge.
	 * @param g2 The graphics.
	 */
	protected void drawEdgeArrow( GraphicEdge edge, Graphics2D g2, boolean loopEdge )
	{
		if( edge.isCurve() )
		     drawEdgeArrowAlongCubic( edge, g2, loopEdge );
		else drawEdgeArrowAlongLine( edge, g2 );
	}
	
	/**
	 * Compute the position of the arrow along a line edge.
	 * @param edge The edge.
	 * @param g2 The graphics.
	 */
	protected void drawEdgeArrowAlongLine( GraphicEdge edge, Graphics2D g2 )
	{
		Style style = edge.getStyle();
		
		// Compute the direction vector and some lengths.
		
		float x   = edge.to.x;
		float y   = edge.to.y;
		float vx  = x - edge.from.x;
		float vy  = y - edge.from.y;
		float ew  = ctx.toPixels( style.getWidth() );
		float ww  = ctx.toGu( style.getWidth() );
		float off = evalTargetRadius( edge );
		
		// Normalise the vectors.
		
		float d = (float) Math.sqrt( vx*vx + vy*vy );
		
		vx /= d;
		vy /= d;
		
		// Choose an arrow "length".
		
		float arrowLength = ctx.toGu( style.getArrowLength() );
		
		if( ew > 1 )
			arrowLength += ww;
		
		x -= vx*off;
		y -= vy*off;
		
		drawEdgeArrow( edge, g2, x, y, vx*arrowLength, vy*arrowLength );
	}
	
	/**
	 * Compute the position of an arrow along a cubic curve edge.
	 * @param edge The edge.
	 * @param g2 The graphics.
	 */
	protected void drawEdgeArrowAlongCubic( GraphicEdge edge, Graphics2D g2, boolean loopEdge )
	{
		Style style = edge.getStyle();
		
		float x0 = edge.from.x;
		float x1 = edge.to.x;
		float y0 = edge.from.y;
		float y1 = edge.to.y;
		float ctrl[] = edge.getControlPoints();
		float ew  = ctx.toPixels( style.getWidth() );
		float ww  = ctx.toGu( style.getWidth() );
		float off = loopEdge ? ww : evalTargetRadius( edge );
		
		// Express node width in percents of the edge length.
		
		float dx = x1 - x0;
		float dy = y1 - y0;

		if( loopEdge )
		{
			x1 = x0;
			y1 = y0 + ((ctx.toGu( edge.from.getStyle().getWidth() )) / (2*(1+edge.getMultiIndex()*0.2f)));
			dx = ( ( x1 - ctrl[2] ) + ( ctrl[0] - ctrl[2] ) + ( ctrl[0] - x0 ) );
			dy = ( ( y1 - ctrl[3] ) + ( ctrl[1] - ctrl[3] ) + ( ctrl[1] - y0 ) );
		}
		
		float d  = (float) Math.sqrt( dx*dx + dy*dy );
		
		if( off > d )
			return;
		
		float t1 = ( ( d - off ) / d );
		
		// Express arrow length in percents of the edge length.
		
		float arrowLength = ctx.toGu( style.getArrowLength() );
		
		if( ew > 1 )
			arrowLength += ww;
		
		if( arrowLength+off > d )
			return;
		
		float t2 = ( ( d - ( off + arrowLength ) ) / d );
		
		// Now compute arrow coordinates and vector.

		float x = ctx.cubicCurveEval( x0, ctrl[0], ctrl[2], x1, t1 );
		float y = ctx.cubicCurveEval( y0, ctrl[1], ctrl[3], y1, t1 );
		dx      = x - ctx.cubicCurveEval( x0, ctrl[0], ctrl[2], x1, t2 ); 
		dy      = y - ctx.cubicCurveEval( y0, ctrl[1], ctrl[3], y1, t2 ); 
		
		drawEdgeArrow( edge, g2, x, y, dx, dy ); 
	}
	
	/**
	 * Draw an arrow at position (x,y) along vector (dx,dy). The position is a point at the
	 * end of the arrow (the head of the arrow). The 'd' vector gives the direction
	 * of the arrow. Its length is the length of the arrow. The arrow shape is chosen according
	 * to the edge style.
	 * @param edge The edge.
	 * @param g2 The graphics.
	 * @param x The arrow position.
	 * @param y The arrow position.
	 * @param dx The arrow vector.
	 * @param dy The arrow vector.
	 */
	protected void drawEdgeArrow( GraphicEdge edge, Graphics2D g2, float x, float y, float dx, float dy )
	{
		Style.ArrowShape astyle = edge.getStyle().getArrowShape();
		
		if( astyle == Style.ArrowShape.NONE )
			return;

		switch( astyle )
		{
			case NONE:
				return;
			case CIRCLE:
				drawEdgeArrowCircle( edge, g2, x, y, dx, dy );
				break;
			case IMAGE:
				drawEdgeArrowImage( edge, g2, x, y, dx, dy );
				break;
			case DIAMANT:
				drawEdgeArrowSimple( edge, g2, x, y, dx, dy, true );
				break;
			case SIMPLE:
			default:
				drawEdgeArrowSimple( edge, g2, x, y, dx, dy, false );
				break;
		}
	}

	/**
	 * Draw an arrow.
	 * @param edge The edge.
	 * @param g2 The graphics.
	 * @param x Point at which the edge crosses the node shape.
	 * @param y Point at which the edge crosses the node shape.
	 * @param dx The arrow vector (and length).
	 * @param dy The arrow vector (and length).
	 */
	protected void drawEdgeArrowSimple( GraphicEdge edge, Graphics2D g2, float x, float y, float dx, float dy, boolean diamant )
	{
		Style style = edge.getStyle();
		
		// Compute the edge vector (1) and the perpendicular vector (2).
		
		float dx2 =  dy;
		float dy2 = -dx;
		float ew  = ctx.toPixels( style.getWidth() );
		float ww  = ctx.toGu( style.getWidth() );
		
		// Normalise the vectors.
		
		float d2 = (float) Math.sqrt( dx2*dx2 + dy2*dy2 );
		
		dx2 /= d2;
		dy2 /= d2;
		
		// Choose an arrow "width".
		
		float arrowWidth  = ctx.toGu( style.getArrowWidth() );
		
		if( ew > 1 )
			arrowWidth += ww/2;
		
		dx2 *= arrowWidth;
		dy2 *= arrowWidth;
		
		// Create a polygon.
		
		path.reset();
		
		if( diamant )
		{
			path.moveTo( ctx.xGuToPixels( x ), ctx.yGuToPixels( y ) );
			path.lineTo( ctx.xGuToPixels( x - dx/2 + dx2 ), ctx.yGuToPixels( y - dy/2 + dy2 ) );
			path.lineTo( ctx.xGuToPixels( x - dx ), ctx.yGuToPixels( y - dy ) );
			path.lineTo( ctx.xGuToPixels( x - dx/2 - dx2 ), ctx.yGuToPixels( y - dy/2 - dy2 ) );
		}
		else
		{
			path.moveTo( ctx.xGuToPixels( x ), ctx.yGuToPixels( y ) );
			path.lineTo( ctx.xGuToPixels( x - dx + dx2 ), ctx.yGuToPixels( y - dy + dy2 ) );		
			path.lineTo( ctx.xGuToPixels( x - dx - dx2 ), ctx.yGuToPixels( y - dy - dy2 ) );
		}
		
		path.closePath();
		g2.fill( path );
	}

	/**
	 * Draw a circle in place of the arrow.
	 * @param edge The edge.
	 * @param g2 The graphics.
	 * @param x Point at which the edge crosses the node shape.
	 * @param y Point at which the edge crosses the node shape.
	 * @param dx The arrow vector (and length).
	 * @param dy The arrow vector (and length).
	 */
	protected void drawEdgeArrowCircle( GraphicEdge edge, Graphics2D g2, float x, float y, float dx, float dy )
	{
		Style style = edge.getStyle();
		
		// Compute the edge vector.
		
		float ew  = ctx.toPixels( style.getWidth() );
		float ww  = ctx.toGu( style.getWidth() );
		
		float arrowLength = ctx.toGu( style.getArrowLength() );
		
		if( ew > 1 )
			arrowLength += ww;
		
		x -= ( dx / 2 );
		y -= ( dy / 2 );
		
		// Draw.

		float diam = ctx.guToPixels( arrowLength );
		
		oval.setFrame( ctx.xGuToPixels( x ) - diam/2, ctx.yGuToPixels( y ) - diam/2, diam, diam );
		g2.fill( oval );
	}
	
	/**
	 * Draw an image for the arrow.
	 * @param edge The edge.
	 * @param g2 The graphics.
	 * @param x Point at which the edge crosses the node shape.
	 * @param y Point at which the edge crosses the node shape.
	 * @param dx The arrow vector (and length).
	 * @param dy The arrow vector (and length).
	 */
	protected void drawEdgeArrowImage( GraphicEdge edge, Graphics2D g2, float x, float y, float dx, float dy )
	{
		Style  style  = edge.getStyle();
		String imgUrl = style.getArrowImageUrl();
		
		if( imgUrl == null )
		{
			drawEdgeArrowSimple( edge, g2, x, y, dx, dy, false );
		}
		else
		{
			Image img = ctx.getImage( imgUrl );
			
			if( img == null )
				img = ctx.getDummyImage();
			
			// Compute the edge vector.
			
			int   ew  = (int) ctx.toPixels( style.getWidth() );
			float ww  = ctx.toGu( style.getWidth() );
//			int   iw  = img.getWidth( null );
//			int   ih  = img.getHeight( null );
			
			// Choose an arrow "width".
			
			float arrowLength = ctx.toGu( style.getArrowLength() );
			int iw = (int) ctx.toPixels( style.getArrowLength() );
			int ih = (int) ctx.toPixels( style.getArrowWidth() );
			
	//		arrowLength;
			
			if( ew > 1 )
				arrowLength += ww;
			
			x -= ( dx * arrowLength );
			y -= ( dy * arrowLength );
			
			// Draw.
	
			g2.drawImage( img,
					(int)ctx.xGuToPixels( x ) - iw/2,
					(int)ctx.yGuToPixels( y ) - ih/2, iw, ih, null );
		}
	}

	/**
	 * Push a stroke in the given graphics according to the edge style.
	 * @param g2 The graphics.
	 * @param edge The edge.
	 * @param useBorder True if we paint the edge border, else we paint the edge.
	 * @return The stroke width.
	 */
	protected float chooseEdgeStroke( Graphics2D g2, GraphicEdge edge, boolean useBorder, float addWidth )
	{
		BasicStroke stroke = null;
		
		Style           style = edge.getStyle();
		float           width = useBorder ? ctx.toPixels( style.getBorderWidth() ) : ctx.toPixels( style.getWidth() );
		Style.EdgeStyle estyl = style.getEdgeStyle();
		
		width += addWidth;
		
		if( width >= 0 && width != 1 )
		{
			if( estyl != Style.EdgeStyle.PLAIN )
			{
				switch( estyl )
				{
					case DASHES: stroke = new BasicStroke( width, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10, Context.dashes, 0 ); break;
					case DOTS:   stroke = new BasicStroke( width, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10, Context.dots, 0 ); break;
					default:     stroke = new BasicStroke( width, BasicStroke.CAP_ROUND,  BasicStroke.JOIN_ROUND ); break;
				}
				
				g2.setStroke( stroke );
			}
			else
			{
				stroke = new BasicStroke( width, BasicStroke.CAP_ROUND,  BasicStroke.JOIN_ROUND );
				
				g2.setStroke( stroke );
			}
			
			return width;
		}
		else if( estyl != null )
		{
			switch( estyl )
			{
				case DASHES: stroke = Context.defDashedLineStroke; break;
				case DOTS:   stroke = Context.defDottedLineStroke; break;
				default:     stroke = Context.defLineStroke;       break;
			}

			g2.setStroke( stroke );
			
			return 1;
		}
		
		g2.setStroke( Context.defLineStroke );
		
		return 1;
	}
	
	/**
	 * Try to evaluate the "radius" of the edge target node shape along the edge. In other words
	 * this method computes the intersection point between the edge and the node shape contour.
	 * The returned length is the length of a line going from the centre of the shape toward
	 * the point of intersection between the target node shape contour and the edge.
	 * @param edge The edge (it contains its target node).
	 * @return The radius.
	 */
	protected float evalTargetRadius( GraphicEdge edge )
	{
		Style style = edge.to.getStyle();

		// FIXME: The curve edges are still badly computed for box and ellipse radii.
		// This is quite difficult to compute however.
		
		switch( style.getNodeShape() )
		{
			case CIRCLE:
			case CROSS:
			case TRIANGLE:
				return ctx.toGu( edge.to.getStyle().getWidth() ) / 2 + ctx.toGu( edge.to.getStyle().getBorderWidth() );
			case SQUARE:
				return evalBoxRadius( edge, edge.to.boundsW/2, edge.to.boundsH/2 );
			case IMAGE:
				return evalBoxRadius( edge, edge.to.boundsW/2, edge.to.boundsH/2 );
			case TEXT_BOX:
				return evalBoxRadius( edge, edge.to.boundsW/2, edge.to.boundsH/2 );
			case TEXT_ELLIPSE:
				return evalEllipseRadius( edge, edge.to.boundsW/2, edge.to.boundsH/2 );
			default:
				return 0;
		}
	}
	
	/**
	 * Compute the length of a vector along the edge from the box centre that match the box
	 * "radius".
	 * @param edge The edge representing the vector.
	 * @param w The box first radius (width/2).
	 * @param h The box second radius (height/2).
	 * @return The length of the radius along the edge vector.
	 */
	protected float evalBoxRadius( GraphicEdge edge, float w, float h )
	{
		// Pythagora : Angle at which we compute the intersection with the height or the width.
		
		float da = ( w ) / ( (float)Math.sqrt( w*w+h*h ) );
		da = da < 0 ? -da : da;
		
		// Angle of the incident vector.
		
		float dx = edge.to.x - edge.from.x;
		float dy = edge.to.y - edge.from.y;
		float d  = (float) Math.sqrt( dx*dx + dy*dy );
		float a  = dx/d; a = a < 0 ? -a : a;
	
		// Choose the side of the rectangle the incident edge vector crosses.
		
		if( da < a )
		{
			return (w/a);
		}
		else
		{
			a = dy/d; a = a < 0 ? -a : a;
			return (h/a);
		}
	}

	/**
	 * Compute the length of a vector along the edge from the ellipse centre that match the
	 * ellipse radius.
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

		if( edge.isCurve() )
		{
			float ctrl[] = edge.getControlPoints();
			float x0 = ctrl[2] + (( ctrl[0] - ctrl[2] ) / 4);
			float y0 = ctrl[3] + (( ctrl[1] - ctrl[3] ) / 4);
			dx = edge.to.x - x0;
			dy = edge.to.y - y0;
		}
		else
		{
			dx = edge.to.x - edge.from.x;
			dy = edge.to.y - edge.from.y;
		}
		
		// The entering edge must be deformed by the ellipse ratio to find the correct angle.

		dy *= (w/h);	// I searched a lot to find this line was missing ! Tsu !

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