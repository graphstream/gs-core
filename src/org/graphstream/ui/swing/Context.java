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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;

import org.graphstream.ui.graphicGraph.GraphicEdge;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.GraphicNode;
import org.graphstream.ui.graphicGraph.stylesheet.Style;
import org.graphstream.ui.graphicGraph.stylesheet.Value;
import org.graphstream.ui.swing.FontCache;
import org.util.InterpolatedFloat;


/**
 * Shared settings used during graph rendering.
 * 
 * <p>
 * These informations are passed to any swing node or edge renderer. They contain
 * various metrics to render elements. There are two unit spaces:
 * <ul>
 * 		<li>Graph space: is the units inside which the graph layout is computed,
 * 			the graph units starts in a [0..1][0..1] square, and can extend and
 * 			move in this square;</li>
 * 		<li>Pixel space: represents the window inside which the graph is drawn.
 * 		    Here units are pixels.</li>
 * </ul>
 * The two utility methods {@link #xGuToPixels(float)} and {@link #yGuToPixels(float)} do
 * the conversion job for you.
 * </p>
 * 
 * <p>
 * This class also contains shared resources, like images, fonts, etc.
 * </p>
 * 
 * TODO: share the image and font caches across instances that run in the same thread
 * to reduce memory usage.
 * 
 * @since 20061226
 */
public class Context
{
// Constants
	
	protected static float dots[] = { 2f };

	protected static float dashes[] = { 5f };
	
	public static BasicStroke defLineStroke = new BasicStroke( 1 );
	
	public static BasicStroke defDottedLineStroke = new BasicStroke( 1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, dots, 0 );
	
	public static BasicStroke defDashedLineStroke = new BasicStroke( 1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, dashes, 0 );
	
// Attributes
	
	/**
	 * The swing graphics. This is set at start of each render pass (it can (and do) change).
	 */
	public Graphics2D g2;
	
// Attributes -- Metrics
	
	/**
	 * Graph overall position in the graph space, in graph units this position also takes node and
	 * sprite dimensions into account, therefore, if should not be used to compute relative lengths
	 * (percents). In GU.
	 */
	protected float graphX, graphY;
	
	/**
	 * Interpolated graph position. In GU.
	 */
	protected InterpolatedFloat gX, gY;
	
	/**
	 * Graph overall dimensions in graph units. This length take also node and sprite dimensions
	 * into account, therefore, if should not be used to compute relative lengths (percents).
	 * In GU.
	 */
	protected float graphW, graphH;
	
	/**
	 * Interpolated graph dimensions (GU).
	 */
	protected InterpolatedFloat gW, gH;

	/**
	 * Canvas dimensions in pixels (PX).
	 */
	protected int canvasW, canvasH;
	
	/**
	 * Position of the graph without node or edge lengths. At the contrary of the graph overall
	 * x and y, this dimension only considers the node positions to compute the graph
	 * position. It should therefore be possible to set the node and sprite dimensions using it.
	 * In GU.
	 */
	protected float areaX, areaY;
	
	/**
	 * Dimension of the graph without node or edge lengths. At the contrary of the graph overall
	 * width and height, this dimension only considers the node positions to compute the graph
	 * width. It should therefore be possible to set the node and sprite dimensions using it.
	 * In GU.
	 */
	protected float areaW, areaH;
	
	/**
	 * Allows to zoom on the display.
	 */
	protected float zoom = 1f;
	
	/**
	 * Interpolated zoom.
	 */
	protected InterpolatedFloat zm;
	
	/**
	 * Allows to pan along X the display in GU.
	 */
	protected float panx = 0f;
	
	/**
	 * Allows to pan along Y the display in GU.
	 */
	protected float pany = 0f;
	
	/**
	 * The ratio between graph coordinates (GU) and pixel coordinates (PX).
	 */
	public float ratio = 1;
	
// Some specific settings
	
	/**
	 * Are edges drawn ?.
	 */
	public boolean drawEdges = true;
	
	/**
	 * Are nodes drawn ?.
	 */
	public boolean drawNodes = true;
	
	/**
	 * Are sprites drawn ?.
	 */
	public boolean drawSprites = true;
	
	/**
	 * Are node labels drawn ?.
	 */
	public boolean drawNodeLabels = true;
	
	/**
	 * Are edge labels drawn ?.
	 */
	public boolean drawEdgeLabels = true;
	
	/**
	 * If icons are specified for nodes, draw them ?.
	 */
	public boolean drawNodeIcons = true;
	
	/**
	 * Show or hide the edge orientation.
	 */
	public boolean drawEdgeArrows = true;
	
	/**
	 * Is there a background to display.
	 */
	public boolean drawBackground = true;

// Shared resources

	/**
	 * The image cache.
	 */
	protected ImageCache imgCache = new ImageCache();
	
	/**
	 * The font cache.
	 */
	protected FontCache fontCache = new FontCache();
	
	/**
	 * Background Image file name
	 */
	public String backgroundImage = null;
	
	/**
	 * X position of the background image expressed in the user space coordinate.
	 */
	public float backgroundPositionX=0f;

	/**
	 * Y position of the background image expressed in the user space (graph space) coordinate.
	 */
	public float backgroundPositionY=0f;
	
	/**
	 * Background width expressed in the user space (graph space) coordinate.
	 */
	public float backgroundWidth=0;

	/**
	 * Background height expressed in the user space (graph space) coordinate.
	 */
	public float backgroundHeight=0;
	
	/**
	 * Constant space around the graph representation in pixels.
	 */
	public int border = 20;
	
	/**
	 * Offsets in GU to centre the graph on the display.
	 */
	public float centerOffX = 0, centerOffY = 0; 
	
	public int minBorder = 20;

// Commands

	/**
	 * Constructor that try to initialise its fields with the environment
	 */
	public Context()
	{
		gX = new InterpolatedFloat( 0,    0.4f );
		gY = new InterpolatedFloat( 0,    0.4f );
		gW = new InterpolatedFloat( 0.1f, 0.4f );
		gH = new InterpolatedFloat( 0.1f, 0.4f );
		zm = new InterpolatedFloat( 1,    0.4f );

		initFontCache();
		
		org.util.Environment.getGlobalEnvironment().initializeFieldsOf( this );
	}

// Access
	
	public float xPixelsToGu( float inSwingUnits )
	{                   
		return  (float) (( inSwingUnits - border ) / (ratio*zoom)) + graphX - (panx*graphW/2+centerOffX);
	}
	
	public float yPixelsToGu( float inSwingUnits )
	{
		return  (float) (( (canvasH-inSwingUnits) - border ) / (ratio*zoom)) + graphY - (pany*graphH/2+centerOffY);
	}
	
	public float xGuToPixels( float inGraphUnits )
	{
		return ( ( ( ( inGraphUnits - graphX + (panx*graphW/2+centerOffX) ) * ratio * zoom ) + border ) );
	}

	public float yGuToPixels( float inGraphUnits )
	{
		return ( canvasH - ( ( ( inGraphUnits - graphY + (pany*graphH/2+centerOffY) ) * ratio * zoom ) + border ) );
	}
	
	public int xGuToRoundPixels( float inGraphUnits )
	{
		return Math.round( ( ( ( inGraphUnits - graphX + (panx*graphW/2+centerOffX) ) * ratio * zoom ) + border ) );
	}
	
	public int yGuToRoundPixels( float inGraphUnits )
	{
		return Math.round( canvasH - ( ( ( inGraphUnits - graphY + (pany*graphH/2+centerOffY) ) * ratio * zoom ) + border ) );
	}

	public float xPercentsToPixels( float inPercents )
	{
		return ( ( inPercents / 100f ) * canvasW );
	}
	
	public float yPercentsToPixels( float inPercents )
	{
		return ( ( inPercents / 100f ) * canvasH );
	}
	
	public float percentsToPixels( float inPercents )
	{
		return xPercentsToPixels( inPercents );
	}
	
	public float xPercentsToGu( float inPercents )
	{
		return ( ( inPercents / 100f ) * areaW );
	}
	
	public float yPercentsToGu( float inPercents )
	{
		return ( ( inPercents / 100f ) * areaH );
	}
	
	public float percentsToGu( float inPercents )
	{
		return xPercentsToGu( inPercents );
	}
	
	public float pixelsToPercents( float inPixels )
	{
		return ( ( inPixels / canvasW ) * 100 );
	}
	
	public float guToPixels( float inGraphUnits )
	{
		return ( inGraphUnits * ratio * zoom );
	}
	
	public float xOffsetGuToPixels( float inGraphUnits )
	{
		return guToPixels( inGraphUnits );
	}
	
	public float yOffsetGyToPixels( float inGraphUnits )
	{
		return -guToPixels( inGraphUnits );
	}
	
	public float pixelsToGu( float inPixelUnits )
	{
		return ( inPixelUnits / (ratio*zoom) );
	}

	public float xToGu( Value value )
	{                   
		if( value.units == Style.Units.PX )
			return xPixelsToGu( value.value );
		else if( value.units == Style.Units.PERCENTS )
			return xPercentsToGu( value.value );
		
		return value.value;
	}
	
	public float yToGu( Value value )
	{
		if( value.units == Style.Units.PX )
			return yPixelsToGu( value.value );
		else if( value.units == Style.Units.PERCENTS )
			return yPercentsToGu( value.value );
	
		return value.value;
	}
	
	public float xToPixels( Value value )
	{
		if( value.units == Style.Units.GU )
			return xGuToPixels( value.value );
		else if( value.units == Style.Units.PERCENTS )
			return xPercentsToPixels( value.value );
		
		return value.value;
	}

	public float yToPixels( Value value )
	{
		if( value.units == Style.Units.GU )
			return yGuToPixels( value.value );
		else if( value.units == Style.Units.PERCENTS )
			return yPercentsToPixels( value.value );
		
		return value.value;
	}
	
	public float xOffsetToPixels( Value value )
	{
		return toPixels( value );
	}
	
	public float yOffsetToPixels( Value value )
	{
		return -toPixels( value );
	}

	public float toPixels( Value value )
	{
		if( value.units == Style.Units.GU )
			return guToPixels( value.value );
		else if( value.units == Style.Units.PERCENTS )
			return percentsToPixels( value.value );
		
		return value.value;
	}
	
	public float toGu( Value value )
	{
		if( value.units == Style.Units.PX )
			return pixelsToGu( value.value );
		else if( value.units == Style.Units.PERCENTS )
			return percentsToGu( value.value );
		
		return value.value;
	}
	
	public float toGu( float value, Style.Units units )
	{
		if( units == Style.Units.PX )
			return pixelsToGu( value );
		else if( units == Style.Units.PERCENTS )
			return percentsToGu( value );
		
		return value;
	}

	public float xToGu( float value, Style.Units units )
	{                   
		if( units == Style.Units.PX )
			return xPixelsToGu( value );
		else if( units == Style.Units.PERCENTS )
			return xPercentsToGu( value );
		
		return value;
	}
	
	public float yToGu( float value, Style.Units units )
	{
		if( units == Style.Units.PX )
			return yPixelsToGu( value );
		else if( units == Style.Units.PERCENTS )
			return yPercentsToGu( value );
	
		return value;
	}
	
	/**
	 * Evaluate a cubic curve according to control points (x) and return the position at
	 * a given "percent" (t) of the curve.
	 * @param x0 The first control point.
	 * @param x1 The second control point.
	 * @param x2 The third control point.
	 * @param x3 The fourth control point.
	 * @param t The percent on the curve (between 0 and 1 included).
	 * @return The coordinate at t percent on the curve.
	 */
	protected float cubicCurveEval( float x0, float x1, float x2, float x3, float t )
	{
		float tt = ( 1f - t );
		
		return x0 * (tt*tt*tt)
		 + 3 * x1 * t * (tt*tt)
		 + 3 * x2 * (t*t) * tt
		     + x3 * (t*t*t);
	}

// Access
	
	/**
	 * Try to load an image from the given resource name (either on disk or in a
	 * jar file) and return it. This method uses a cache and an already loaded
	 * resource is reused. If the resource is not found, null is returned.
	 * @param resourceName The image name (in a jar or on the file system).
	 * @return The image or null if not found.
	 */
	public Image getImage( String resourceName )
	{
		return imgCache.getImage( resourceName );
	}
	
	/**
	 * A dummy 16x16 image.
	 * @return An image.
	 */
	public Image getDummyImage()
	{
		return imgCache.getDummyImage();
	}
	
	/**
	 * Try to get a font according to the given style.
	 * @param style The style specifying the font.
	 * @return A font.
	 */
	public Font getFont( Style style )
	{
		String name = style.getTextFont();
		
		if( name != null )
		{
			int             size = (int) style.getTextSize();
			Style.TextStyle tsty = style.getTextStyle();
		
			return fontCache.getFont( name, tsty, size );
		}
		else
		{
			return fontCache.getDefaultFont();
		}
	}
	
	/**
	 * Set the graph bounds (in graph units).
	 * @param x The graph lowest position along X.
	 * @param y The graph lowest position along Y.
	 * @param w The graph width.
	 * @param h The graph height.
	 */
	public void setBounds( float x, float y, float w, float h )
	{
		gX.setValue( x );
		gY.setValue( y );
		gW.setValue( w );
		gH.setValue( h );
	}
	
	/**
	 * The zoom value.
	 * @param z The new zoom.
	 */
	public void setZoom( float z )
	{
		//if( org.miv.util.Environment.getGlobalEnvironment().getBooleanParameter( "SwingGraphRenderer.interpolateBounds" ) )
		zm.setValue( z );
		zoom = z;
	}
	
	public void incrZoom( float z )
	{
		//if( org.miv.util.Environment.getGlobalEnvironment().getBooleanParameter( "SwingGraphRenderer.interpolateBounds" ) )
		zm.incrValue( z );
		zoom += z;	
	}
	
	/**
	 * Do value interpolations. This method compute all the values for interpolated
	 * floats to smooth rendering. Return true if one of the values changed.
	 * @return True if one of the interpolated values changed.
	 */
	public boolean step()
	{
		if( org.util.Environment.getGlobalEnvironment().getBooleanParameter( "SwingGraphRenderer.interpolateBounds" ) )
		{
			float gx = gX.getDirectValue();
			float gy = gY.getDirectValue();
			float gw = gW.getDirectValue();
			float gh = gH.getDirectValue();
			float zz = zm.getDirectValue();
			
			gX.energy();
			gY.energy();
			gW.energy();
			gH.energy();
			zm.energy();
		
			graphX = gX.getValue();
			graphY = gY.getValue();
			graphW = gW.getValue();
			graphH = gH.getValue();
			zoom   = zm.getValue();
		
//			System.err.printf( "X=%f x=%f  |  Y=%f y=%f  |  W=%f w=%f  |  H=%f h=%f  |  Z=%f z=%f%n",
//					graphX, gx, graphY, gy, graphW, gw, graphH, gh, zoom, zz );
			
			if( (int)(gx*1000) != (int)(graphX*1000)
			 || (int)(gy*1000) != (int)(graphY*1000)
			 || (int)(gw*1000) != (int)(graphW*1000)
			 || (int)(gh*1000) != (int)(graphH*1000)
			 || (int)(zz*1000) != (int)(zoom*1000) )
				return true;
		}
		else
		{
			float gx = graphX;
			float gy = graphY;
			float gw = graphW;
			float gh = graphH;
			float zz = zoom;
			
			graphX = gX.getDirectValue();
			graphY = gY.getDirectValue();
			graphW = gW.getDirectValue();
			graphH = gH.getDirectValue();
			zoom   = zm.getDirectValue();

			if( (int)(gx*1000) != (int)(graphX*1000)
			 || (int)(gy*1000) != (int)(graphY*1000)
			 || (int)(gw*1000) != (int)(graphW*1000)
			 || (int)(gh*1000) != (int)(graphH*1000)
			 || (int)(zz*1000) != (int)(zoom*1000) )
			{
/*				System.err.printf( "  > x=%f%s%f  y=%f%s%f  w=%f%s%f  h=%f%s%f  z=%f%s%f%n",
						gx, gx!=graphX?"!=":"==",  graphX,
						gy, gy!=graphY?"!=":"==", graphY,
						gw, gw!=graphW?"!=":"==", graphW,
						gh, gh!=graphH?"!=":"==", graphH,
						zz, zz!=zoom?"!=":"==",   zoom );
*/				return true;
			}
		}
		
		return false;
	}
	
	protected void initFontCache()
	{
		
		
	}
	
// Utilities
	
	protected static int TRUNCATED_TEXT_LENGTH = 20;
	
	/**
	 * Draw a string at a position given in graph coordinates, using the given style.
	 * @param label The string to draw.
	 * @param xInGraphSpace The X coordinate in graph space.
	 * @param yInGraphSpace The Y coordinate in graph space.
	 * @param element The element the label pertains to.
	 */
	public void drawStyledLabel( String label, float xInGraphSpace, float yInGraphSpace, GraphicElement element )
	{
		if( drawNodeLabels == false && element instanceof GraphicNode )
			return;
		else if( drawEdgeLabels == false && element instanceof GraphicEdge )
			return;

		if( label != null )
		{
			Style          style = element.getStyle();
			Style.TextMode mode  = style.getTextMode();
			
			if( mode == Style.TextMode.HIDDEN )
				return;
			
			if( mode == Style.TextMode.TRUNCATED )
				label = truncate( label );
			
			Style.TextAlignment align   = style.getTextAlignment();
			Color               color   = style.getTextColor();
			float               offx    = xOffsetToPixels( style.getTextOffsetX() );
			float               offy    = yOffsetToPixels( style.getTextOffsetY() );
			float               width   = toPixels( style.getWidth() );
			float               x       = xGuToPixels( xInGraphSpace );
			float               y       = yGuToPixels( yInGraphSpace );
			Font                font    = getFont( style );
			FontMetrics         mtrx    = g2.getFontMetrics( font );
			float               theight = mtrx.getHeight();
			float               twidth  = mtrx.stringWidth( label );

			g2.setFont( font );
			g2.setColor( color );
			
			if( align == Style.TextAlignment.ALONG && ( element == null || ! ( element instanceof GraphicEdge ) ) )
				align = Style.TextAlignment.CENTER;
			
			switch( align )
			{
				case LEFT:
					g2.drawString( label, x + offx, y + offy + theight/2 - mtrx.getDescent() );					
					break;
				case RIGHT:
					g2.drawString( label, x + offx - twidth, y + offy + theight/2 - mtrx.getDescent() );					
					break;
				case ASIDE:
					g2.drawString( label, x + width/2 + 4 + offx, y + offy + theight/2 - mtrx.getDescent() );
					break;
				case ALONG:
					AffineTransform t = g2.getTransform();
					g2.translate( x, y );
					g2.rotate( getRotationAngle( (GraphicEdge)element ) );
					g2.drawString( label, offx - twidth/2, offy + theight/2 - mtrx.getDescent() );
//					g2.drawRect( (int)-twidth/2, (int)-theight/2, (int)twidth, (int)theight );
					g2.setTransform( t );
					break;
				case CENTER:
				default:
					g2.drawString( label, x + offx - twidth/2, y + offy + theight/2 - mtrx.getDescent() );					
					break;
			}
		}		
	}
	
	/**
	 * To avoid creating a shape at each redraw.
	 */
	protected Rectangle2D rect = new Rectangle2D.Float();
	
	/**
	 * To avoid creating a shape at each redraw.
	 */
	protected Ellipse2D oval = new Ellipse2D.Float();
	
	/**
	 * Draw a text with a rectangular or oval frame around it. If the element style has an image.
	 * @param label The label to draw.
	 * @param xInGraphSpace The position of the box.
	 * @param yInGraphSpace The position of the box.
	 * @param element The element the label pertains to.
	 * @param isOval If the box frame an oval or a rectangle?.
	 */
	protected void drawTextBox( String label, float xInGraphSpace, float yInGraphSpace, GraphicElement element, boolean isOval )
	{
		Style          style = element.getStyle();
		Style.TextMode mode  = style.getTextMode();

		if( mode == Style.TextMode.TRUNCATED )
			label = truncate( label );
		
		Style.TextAlignment align   = style.getTextAlignment();
		Color               color   = style.getColor();
		float               bwidth  = toPixels( style.getBorderWidth() );
        float               x       = xGuToPixels( xInGraphSpace );
        float               y       = yGuToPixels( yInGraphSpace );
		Color               tcolor  = style.getTextColor();
		float               offx    = xOffsetToPixels( style.getTextOffsetX() );
		float               offy    = yOffsetToPixels( style.getTextOffsetY() );
		Font                font    = getFont( style );
		FontMetrics         mtrx    = g2.getFontMetrics( font );
		int                 theight = mtrx.getHeight();
        int                 twidth  = mode == Style.TextMode.HIDDEN ? 0 : mtrx.stringWidth( label );
		String              imgUrl  = style.getImageUrl();
		Image               image   = null;
		
		// The gap is the space between the border and the contents, as well as the space
		// between the image (if there is one) and the text.
		
		float gap = 5;
		float w   = twidth  + 2 * offx + 2*gap;
		float h   = theight + 2 * offy + 2*gap;
		float iw  = 0;
		float ih  = 0;
		
		// If there is an image, add it to the box size.
		
		if( imgUrl != null )
		{
			image = getImage( imgUrl );
			
			if( image == null )
				image = getDummyImage();
			
			iw    = image.getWidth( null );
			ih    = image.getHeight( null );
			w    += iw + 2*gap;
			h     = Math.max( 2*gap + ih, h );
		}
		
		// Choose the shape and alignment.
		
		RectangularShape shape = isOval ? oval : rect;
		
		switch( align )
		{
			case CENTER:
					x -= w/2;
					y -= h/2;
				break;
			case LEFT:
					x -= w;
					y -= h/2;
				break;
			case RIGHT:
			case ALONG:
			case ASIDE:
					y -= h/2;
				break;
		}
		
		// Draw the background and eventual border.
		
		shape.setFrame( x, y, w, h );
		g2.setColor( color );
		g2.fill( shape );

		if( bwidth > 0 )
		{
			Color bcolor = style.getBorderColor();
			
			chooseBorderStroke( bwidth );
			g2.setColor( bcolor );
			g2.draw( shape );
		}		
		
		// Draw the contents.

		g2.setColor( tcolor );
		g2.setFont( font );
		
		if( imgUrl != null )
		{
			if( ! ( mode == Style.TextMode.HIDDEN ) )
				g2.drawString( label, x + offx +iw+2*gap, y + h/2 + offy + theight/2 - mtrx.getDescent() );
			g2.drawImage( image, (int)(x + gap), (int)(y + gap), null );
		}
		else
		{
			if( ! ( mode == Style.TextMode.HIDDEN ) )
				g2.drawString( label, x + offx +gap, y + h/2 + offy + gap - mtrx.getDescent() );				
		}

		// Reset the element bounds according to the drawing.
		
		float xx = xPixelsToGu( x );
		float yy = yPixelsToGu( y );
		float ww = pixelsToGu( w );
		float hh = pixelsToGu( h );

		element.setBounds( xx, yy-hh, ww, hh );
		//element.setBounds( element.getX(), element.getY(), 1, 1 );
	}

	protected void drawTextBoxShadow( String label, float xInGraphSpace, float yInGraphSpace, GraphicElement element, boolean isOval )
	{
		Style          style = element.getStyle();
		Style.TextMode mode  = style.getTextMode();

		if( mode == Style.TextMode.TRUNCATED )
			label = truncate( label );
		
		Style.TextAlignment align   = style.getTextAlignment();
		Color               color   = style.getShadowColor();
		float               swidth  = toPixels( style.getShadowWidth() );
        float               x       = xGuToPixels( xInGraphSpace ) + toPixels( style.getShadowOffsetX() );
        float               y       = yGuToPixels( yInGraphSpace ) + toPixels( style.getShadowOffsetY() );
		float               offx    = xOffsetToPixels( style.getTextOffsetX() );
		float               offy    = yOffsetToPixels( style.getTextOffsetY() );
		Font                font    = getFont( style );
		FontMetrics         mtrx    = g2.getFontMetrics( font );
		int                 theight = mtrx.getHeight();
        int                 twidth  = mode == Style.TextMode.HIDDEN ? 0 : mtrx.stringWidth( label );
		String              imgUrl  = style.getImageUrl();
		Image               image   = null;

		twidth += ( swidth * 2 );
		x      -= swidth;
		y      -= swidth;
		
		// The gap is the space between the border and the contents, as well as the space
		// between the image (if there is one) and the text.
		
		float gap = 5;
		float w   = twidth  + 2 * offx + 2*gap;
		float h   = theight + 2 * offy + 2*gap;
		float iw  = 0;
		float ih  = 0;
		
		// If there is an image, add it to the box size.
		
		if( imgUrl != null )
		{
			image = getImage( imgUrl );
			
			if( image == null )
				image = getDummyImage();
			
			iw    = image.getWidth( null );
			ih    = image.getHeight( null );
			w    += iw + 2*gap;
			h     = Math.max( 2*gap + ih, h );
		}
		
		// Choose the shape and alignment.
		
		RectangularShape shape = isOval ? oval : rect;
		
		switch( align )
		{
			case CENTER:
					x -= w/2;
					y -= h/2;
				break;
			case LEFT:
					x -= w;
					y -= h/2;
				break;
			case RIGHT:
			case ALONG:
			case ASIDE:
					y -= h/2;
				break;
		}
		
		// Draw the background and eventual border.
		
		shape.setFrame( x, y, w, h );
		g2.setColor( color );
		g2.fill( shape );
	}
	
	/**
	 * Compute the angle between the edge vector and the (1,0,0) vector.
	 * @param edge The edge used.
	 * @return The angle in radians.
	 */
	protected float getRotationAngle( GraphicEdge edge )
	{
		float x = edge.to.x - edge.from.x;
		float y = edge.to.y - edge.from.y;
		float d = (float) Math.sqrt( x*x + y*y );
		
		x /= d;
		
		float a = (float) Math.acos( x );

		if( y > 0 )
			a = (float) ( Math.PI - a );
		
		if( a > Math.PI/2 )
			a = a + (float)Math.PI;
		
		return a;
	}
	
	/**
	 * Choose the stroke according to a width in pixels.
	 * @param width The stroke width in pixels.
	 * @return The chosen width.
	 */
	protected float chooseBorderStroke( float width )
	{
		BasicStroke stroke = null;
		
		if( width >= 0 && width != 1 )
		{
			stroke = new BasicStroke( width, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER );
				
			g2.setStroke( stroke );
			
			return width;
		}
		
		g2.setStroke( Context.defLineStroke );
		
		return 1;
	}
	
	protected String truncate( String label )
	{
		if( label.length() > TRUNCATED_TEXT_LENGTH )
			return String.format( "%s...", label.substring( 0, TRUNCATED_TEXT_LENGTH ) );
		
		return label;
	}

// Access
	
	public boolean isDrawEdges()
	{
		return this.drawEdges;
	}
	public boolean isDrawNodes()
	{
		return this.drawNodes;
	}
	public boolean isDrawNodeLabels()
	{
		return this.drawNodeLabels;
	}
	public boolean isDrawNodeIcons()
	{
		return this.drawNodeIcons;
	}
	public boolean isDrawBackground()
	{
		return this.drawBackground;
	}
	public String getBackgroundImage()
	{
		return this.backgroundImage;
	}
	public float getBackgroundPositionX()
	{
		return this.backgroundPositionX;
	}
	public float getBackgroundPositionY()
	{
		return this.backgroundPositionY;
	}
	public float getBackgroundWidth()
	{
		return this.backgroundWidth;
	}
	public float getBackgroundHeight()
	{
		return this.backgroundHeight;
	}
	public void setDrawEdges( boolean drawEdges )
	{
		this.drawEdges = drawEdges;
	}
	public void setDrawNodes( boolean drawNodes )
	{
		this.drawNodes = drawNodes;
	}
	public void setDrawNodeLabels( boolean drawNodeLabels )
	{
		this.drawNodeLabels = drawNodeLabels;
	}
	public void setDrawNodeIcons( boolean drawNodeIcons )
	{
		this.drawNodeIcons = drawNodeIcons;
	}
	public void setDrawBackground( boolean drawBackground )
	{
		this.drawBackground = drawBackground;
	}
	public void setBackgroundImage( String backgroundImage )
	{
		this.backgroundImage = backgroundImage;
	}
	public void setBackgroundPositionX( float backgroundPositionX )
	{
		this.backgroundPositionX = backgroundPositionX;
	}
	public void setBackgroundPositionY( float backgroundPositionY )
	{
		this.backgroundPositionY = backgroundPositionY;
	}
	public void setBackgroundWidth( float backgroundWidth )
	{
		this.backgroundWidth = backgroundWidth;
	}
	public void setBackgroundHeight( float backgroundHeight )
	{
		this.backgroundHeight = backgroundHeight;
	}
}