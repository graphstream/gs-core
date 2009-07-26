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
 */

package org.miv.graphstream.ui2.swingViewer.basicView;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import org.miv.graphstream.ui2.graphicGraph.GraphicGraph;
import org.miv.graphstream.ui2.graphicGraph.stylesheet.Style;
import org.miv.graphstream.ui2.graphicGraph.stylesheet.Values;
import org.miv.util.geom.Point3;

/**
 * Position the view on the graph.
 * 
 * @author Antoine Dutot
 */
class Camera
{
// Attribute
	
	/**
	 * Information on the graph overall dimension and position.
	 */
	protected GraphMetrics metrics = new GraphMetrics();
	
	/**
	 * Automatic centring of the view.
	 */
	protected boolean autoFit = true;
	
	/**
	 * The camera centre of view.
	 */
	protected Point3 center = new Point3();
	
	/**
	 * The camera zoom.
	 */
	protected float zoom;
	
	/**
	 * The graph-space -> pixel-space transformation.
	 */
	protected AffineTransform Tx = new AffineTransform();
	
	/**
	 * The previous affine transform.
	 */
	protected AffineTransform oldTx;
	
	/**
	 * The rotation angle.
	 */
	protected float rotation;
	
	/**
	 * Padding around the graph.
	 */
	protected Values padding = new Values( Style.Units.GU, 0, 0, 0 );
	
// Construction
	
	/**
	 * New camera.
	 */
	public Camera()
	{
	}
	
// Access
	
	/**
	 * The view centre (a point in graph units).
	 * @return The view centre.
	 */
	public Point3 getViewCenter()
	{
		return center;
	}
	
	/**
	 * The visible portion of the graph.
	 * @return A real for which value 1 means the graph is fully visible and uses the whole
	 * view port.
	 */
	public float getViewPercent()
	{
		return zoom;
	}
	
	/**
	 * The rotation angle in degrees.
	 * @return The rotation angle in degrees.
	 */
	public float getViewRotation()
	{
		return rotation;
	}
	
	/**
	 * Various sizes about the graph.
	 * @return The graph metrics.
	 */
	public GraphMetrics getMetrics()
	{
		return metrics;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder( String.format( "Camera :%n" ) );
		
		builder.append( String.format( "    autoFit  = %b%n", autoFit ) );
		builder.append( String.format( "    center   = %s%n", center ) );
		builder.append( String.format( "    rotation = %f%n", rotation ) );
		builder.append( String.format( "    zoom     = %f%n", zoom ) );
		builder.append( String.format( "    padding  = %s%n", padding ) );
		builder.append( String.format( "    metrics  = %s%n", metrics ) );
		
		return builder.toString();
	}

// Command

	/**
	 * Set the camera view in the given graphics and backup the previous transform of the graphics.
	 * Call {@link #popView(Graphics2D)} to restore the saved transform. You can only push one time
	 * the view.
	 * @param g2 The Swing graphics to change.
	 */
	public void pushView( Graphics2D g2 )
	{
		if( oldTx == null )
		{
			oldTx = g2.getTransform();
			
			if( autoFit )
			     Tx = autoFitView( g2, Tx );
			else Tx = userView( g2, Tx );
			
			g2.setTransform( Tx );
		}
	}
	
	/**
	 * Restore the transform that was used before {@link #pushView(Graphics2D)} is used.
	 * @param g2 The Swing graphics to restore.
	 */
	public void popView( Graphics2D g2 )
	{
		if( oldTx != null )
		{
			g2.setTransform( oldTx );
			oldTx = null;
		}
	}
	
	/**
	 * Compute a transformation matrix that pass from graph units (user space) to pixel units
	 * (device space) so that the whole graph is visible.
	 * @param g2 The Swing graphics.
	 * @param Tx The transformation to modify.
	 * @return The transformation modified.
	 */
	protected AffineTransform autoFitView( Graphics2D g2, AffineTransform Tx )
	{
		float sx, sy;
		float tx, ty;
		float padXgu = getPaddingXgu() * 2;
		float padYgu = getPaddingYgu() * 2;
		float padXpx = getPaddingXpx() * 2;
		float padYpx = getPaddingYpx() * 2;
		
		sx = ( metrics.viewport.data[0] - padXpx ) / ( metrics.size.data[0] + padXgu );	// Ratio along X
		sy = ( metrics.viewport.data[1] - padYpx ) / ( metrics.size.data[1] + padYgu );	// Ratio along Y
		tx = metrics.lo.x + ( metrics.size.data[0] / 2 );								// Centre of graph in X
		ty = metrics.lo.y + ( metrics.size.data[1] / 2 );								// Centre of graph in Y
		
		if( sx > sy )	// The least ratio.
		     sx = sy;
		else sy = sx;
		
		Tx.setToIdentity();
		Tx.translate( metrics.viewport.data[0] / 2,
				      metrics.viewport.data[1] / 2 );	// 4. Place the whole result at the centre of the view port.		
		if( rotation != 0 )
			Tx.rotate( rotation/(180/Math.PI) );		// 3. Eventually apply a rotation.
		Tx.scale( sx, -sy );							// 2. Scale the graph to pixels. Scale -y since we reverse the view (top-left to bottom-left).
		Tx.translate( -tx, -ty );						// 1. Move the graph so that its real centre is at (0,0).
		
		zoom = 1;

		center.set( tx, ty, 0 );
		metrics.setRatioPx2Gu( sx );
		metrics.loVisible.copy( metrics.lo );
		metrics.hiVisible.copy( metrics.hi );
		
		return Tx;
	}
	
	/**
	 * Compute a transformation that pass from graph units (user space) to a pixel units (device
	 * space) so that the view (zoom and centre) requested by the user is produced.
	 * @param g2 The Swing graphics.
	 * @param Tx The transformation to modify.
	 * @return The transformation modified.
	 */
	protected AffineTransform userView( Graphics2D g2, AffineTransform Tx )
	{
		float sx, sy;
		float tx, ty;
		float padXgu = getPaddingXgu() * 2;
		float padYgu = getPaddingYgu() * 2;
		float padXpx = getPaddingXpx() * 2;
		float padYpx = getPaddingYpx() * 2;
//		float diag   = ((float)Math.max( metrics.size.data[0]+padXgu, metrics.size.data[1]+padYgu )) * zoom; 
//		
//		sx = ( metrics.viewport.data[0] - padXpx ) / diag; 
//		sy = ( metrics.viewport.data[1] - padYpx ) / diag;
		sx = ( metrics.viewport.data[0] - padXpx ) / (( metrics.size.data[0] + padXgu )*zoom); 
		sy = ( metrics.viewport.data[1] - padYpx ) / (( metrics.size.data[1] + padYgu )*zoom);
		tx = center.x;
		ty = center.y;
		
		if( sx > sy )	// The least ratio.
		     sx = sy;
		else sy = sx;
		
		Tx.setToIdentity();
		Tx.translate( metrics.viewport.data[0] / 2,
				      metrics.viewport.data[1] / 2 );	// 4. Place the whole result at the centre of the view port.			
		Tx.scale( sx, -sy );							// 3. Scale the graph to pixels. Scale -y since we reverse the view (top-left to bottom-left).
		Tx.translate( -tx, -ty );						// 2. Move the graph so that the given centre is at (0,0).
		if( rotation != 0 )
			Tx.rotate( rotation/(180/Math.PI) );		// 1. Eventually apply a rotation.
		
		metrics.setRatioPx2Gu( sx );

		float w2 = ( metrics.viewport.data[0] / sx ) / 2;
		float h2 = ( metrics.viewport.data[1] / sx ) / 2;
		
		metrics.loVisible.set( center.x-w2, center.y-h2 );
		metrics.hiVisible.set( center.x+w2, center.y+h2 );
		
		return Tx;
	}

	/**
	 * Enable or disable automatic adjustment of the view to see the entire graph.
	 * @param on If true, automatic adjustment is enabled.
	 */
	public void setAutoFitView( boolean on )
	{
		if( autoFit && ( ! on ) )
		{
			// We go from autoFit to user view, ensure the current centre is at the
			// middle of the graph, and the zoom is at one.
			
			zoom = 1;
			center.set( metrics.lo.x + ( metrics.size.data[0] / 2 ),
			            metrics.lo.y + ( metrics.size.data[1] / 2 ), 0 );
		}

		autoFit = on;
	}
	
	/**
	 * Set the centre of the view (the looked at point). As the viewer is only 2D, the z value is
	 * not required.
	 * @param x The new position abscissa.
	 * @param y The new position ordinate.
	 */
	public void setCenter( float x, float y )
	{
		center.set( x, y, 0 );
	}
	
	/**
	 * Set the zoom (or percent of the graph visible), 1 means the graph is fully visible.
	 * @param z The zoom.
	 */
	public void setZoom( float z )
	{
		zoom = z;
	}
	
	/**
	 * Set the rotation angle around the centre.
	 * @param angle The rotation angle in degrees.
	 */
	public void setRotation( float angle )
	{
		rotation = angle;
	}

	/**
	 * Set the output view port size in pixels. 
	 * @param viewportWidth The width in pixels of the view port.
	 * @param viewportHeight The width in pixels of the view port.
	 */
	public void setViewport( float viewportWidth, float viewportHeight )
	{
		metrics.setViewport( viewportWidth, viewportHeight );
	}
	
	/**
	 * Set the graph padding.
	 * @param graph The graphic graph.
	 */
	public void setPadding( GraphicGraph graph )
	{
		padding.copy( graph.getStyle().getPadding() );
	}
	
// Utility
	
	protected float getPaddingXgu()
	{
		if( padding.units == Style.Units.GU && padding.size() > 0 )
			return padding.get( 0 );
		
		return 0;
	}
	
	protected float getPaddingYgu()
	{
		if( padding.units == Style.Units.GU && padding.size() > 1 )
			return padding.get( 1 );
		
		return getPaddingXgu();		
	}

	protected float getPaddingXpx()
	{
		if( padding.units == Style.Units.PX && padding.size() > 0 )
			return padding.get( 0 );
		
		return 0;
	}
	
	protected float getPaddingYpx()
	{
		if( padding.units == Style.Units.PX && padding.size() > 1 )
			return padding.get( 1 );
		
		return getPaddingXpx();
	}	
}