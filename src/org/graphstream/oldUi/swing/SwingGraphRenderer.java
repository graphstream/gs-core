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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.graphstream.oldUi.GraphViewer;
import org.graphstream.oldUi.graphicGraph.GraphicEdge;
import org.graphstream.oldUi.graphicGraph.GraphicElement;
import org.graphstream.oldUi.graphicGraph.GraphicGraph;
import org.graphstream.oldUi.graphicGraph.GraphicNode;
import org.graphstream.oldUi.graphicGraph.GraphicSprite;
import org.graphstream.oldUi.graphicGraph.stylesheet.Style;
import org.graphstream.oldUi.graphicGraph.stylesheet.Style.TextStyle;
import org.graphstream.oldUi.layout.LayoutListenerProxy;
import org.graphstream.oldUi.swing.settingsPane.SettingsWindow;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

/**
 * Swing GraphRenderer.
 * 
 * @since 20061227
 */
public class SwingGraphRenderer extends JPanel implements MouseListener, MouseMotionListener, KeyListener
{
// Attributes
	
	private static final long serialVersionUID = -4013200166940112284L;

	/**
	 * The graphic graph to render.
	 */
	protected GraphicGraph graph;

	/**
	 * The graph viewer (that contains this renderer).
	 */
	protected SwingGraphViewer viewer;
	
	/**
	 * Node renderer.
	 */
	protected SwingNodeRenderer nodeRenderer;

	/**
	 * Edge renderer.
	 */
	protected SwingEdgeRenderer edgeRenderer;

	/**
	 * Sprite renderer.
	 */
	protected SwingSpriteRenderer spriteRenderer;
	
	/**
	 * Rendering information, shared with the node and edge renderers.
	 */
	protected Context ctx = new Context();

	/**
	 * The graph background, null means no background.
	 */
	protected Image background = null;
	
	/**
	 * Show or hide the FPS monitor.
	 */
	protected boolean showFPS = false;
	
	/**
	 * Show or hide the nodes.
	 */
	protected boolean showNodes = true;
	
	/**
	 * Show or hide the steps.
	 */
	protected boolean showSteps = false;

	/**
	 * Rotation angle.
	 */
	protected float theta = 0;
	
	/**
	 * Current iteration.
	 */
	protected int time = 0;
	
	/**
	 * If non null, output each frame to a file whose base name is this value.
	 */
	protected String outputFrames = null;
	
	/**
	 * The rendering quality.
	 */
	protected int quality = 2;
	
	/**
	 * The layout informations.
	 */
	protected LayoutListenerProxy layoutProxy;
	
	/**
	 * A separate panel to setup and command the viewer and the layout.
	 */
	protected SettingsWindow settingsWin;

	protected float lastLayoutCompletion = 0;
	
// Constructors

	public SwingGraphRenderer( SwingGraphViewer viewer )
	{
		init( viewer, new SwingNodeRenderer(), new SwingEdgeRenderer(), new SwingSpriteRenderer() );
//		setQuality( 2 );
	}

// Access

	public GraphicGraph getGraph()
	{
		return graph;
	}
	
	public Context getContext()
	{
		return ctx;
	}

	public String findNodeAt( int x, int y )
	{
		for( GraphicNode node: graph.getNodes() )
		{
			int w  = 5;
			int nx = (int) ctx.xGuToPixels( node.x );
			int ny = (int) ctx.yGuToPixels( node.y );
			
			int width = (int) ctx.toPixels( node.getStyle().getWidth() );
			
			if( width > 5 ) 	// The min is 5 to be able to pick the node.
				w = (int) width;
			
			if( x > nx - w && y > ny - w && x < nx + w && y < ny + w )
			{
				return node.getId();
			}
		}
	
		return null;
	}

	/**
	 * Return the possible image format the graph renderer supports for the
	 * screenshot.
	 * @see org.graphstream.oldUi.GraphViewer.ScreenshotType
	 */
	public GraphViewer.ScreenshotType[] getScreenshotTypes()
	{
		return new GraphViewer.ScreenshotType[]
		{ GraphViewer.ScreenshotType.PNG,
		  GraphViewer.ScreenshotType.BMP,
		  GraphViewer.ScreenshotType.JPG,
		  GraphViewer.ScreenshotType.SVG };
	}

// Commands

	/**
	 * Enable or disable the display of the layout completion.
	 * @param layoutProxy The listener on the layout process.
	 */
	public void setShowLayoutCompletion( LayoutListenerProxy layoutProxy )
	{
		this.layoutProxy = layoutProxy;
	}
	
	public void setGraph( GraphicGraph graph )
	{
		this.graph = graph;
	}

	public void init( SwingGraphViewer viewer, SwingNodeRenderer nodeRenderer, SwingEdgeRenderer edgeRenderer, SwingSpriteRenderer spriteRenderer )
	{
		this.viewer         = viewer;
		this.graph          = viewer.getGraph();
		this.edgeRenderer   = edgeRenderer;
		this.nodeRenderer   = nodeRenderer;
		this.spriteRenderer = spriteRenderer;

		edgeRenderer.setContext( ctx );
		nodeRenderer.setContext( ctx );
		spriteRenderer.setContext( ctx );

		// The Background
		initBackground();

		setMinimumSize( new Dimension( 200, 200 ) );
		setPreferredSize( new Dimension( 300, 300 ) );
		setOpaque( true );
		setLayout( new BorderLayout() );
		setBackground( Color.white );
		setForeground( Color.BLUE );
		setFont( new Font( "Vernada", Font.PLAIN, 10 ) );

		computeDisplaySize();
		
		addMouseListener( this );
		addMouseMotionListener( this );
		addKeyListener( this );
	}

	/**
	 * Tries to load the given image as a buffered image, for the background.
	 */
	private void initBackground()
	{
		if( ctx.drawBackground )
		{
			if( ctx.getBackgroundImage() != null && !ctx.getBackgroundImage().equals( "" ) )
			{
				background = ctx.getImage( ctx.getBackgroundImage() );
			}
		}
	}

	protected void computeDisplaySize()
	{
		if( graph.getNodes().size() > 0 )
		{
			float minx =  Float.MAX_VALUE;
			float maxx = -Float.MAX_VALUE;
			float miny =  Float.MAX_VALUE;
			float maxy = -Float.MAX_VALUE;

			for( GraphicNode node: graph.getNodes() )
			{
				if( ! node.hasAttribute( "ui.hide" ) )
				{
					if( node.x < minx ) minx = node.x;
					if( node.x > maxx ) maxx = node.x;
					if( node.y < miny ) miny = node.y;
					if( node.y > maxy ) maxy = node.y;
				}
			}

			if( graph.getSpriteManager() != null )
			{
				Iterator<GraphicSprite> sprites = graph.getSpriteManager().getSpriteIterator();

				while( sprites.hasNext() )
				{
					GraphicSprite sprite = sprites.next();
				
					if( ! sprite.hasAttribute( "ui.hide" ) )
					{
						if( sprite.getAttachment() == null )
						{
							float x0 = sprite.x; //boundsX + ( sprite.boundsW / 2 );
							float y0 = sprite.y; //boundsY + ( sprite.boundsH / 2 );
						
							if( x0 > maxx ) maxx = x0;
							if( x0 < minx ) minx = x0;
							if( y0 > maxy ) maxy = y0;
							if( y0 < miny ) miny = y0;
						}
					}
				}
			}
		
			ctx.setBounds( minx, miny, ( maxx-minx ), ( maxy - miny ) );
			ctx.areaX  = minx;
			ctx.areaY  = miny;
			ctx.areaW  = ( maxx - minx );
			ctx.areaH  = ( maxy - miny );
		}
		
		if( graph.getStyle() != null )
			ctx.border = (int) ctx.toPixels( graph.getStyle().getPadding() )
			           + (int)(ctx.toPixels( graph.getStyle().getBorderWidth() ) / 2 - 0.5f );

		// minus 0.5f to avoid integer problems.
		
		if( ctx.border < ctx.minBorder )
			ctx.border = ctx.minBorder;
/*
 * 		This would really be appreciated : it sets the bounds exactly so that
 * 		the graph is visible on the drawing surface. It computes it according
 * 		to the nodes and sprites width/height. However this process is recursive :
 * 		to compute the overall length of the graph you have to know length of everything.
 * 		If a length is specified in PX, it must be converted in GU. To convert it
 * 		in GU you have to know the a scaling factor from PX to GU. This factor
 * 		is computed knowing the width of the graph. More simply : to know the length
 * 		of the graph, you have to know the length of the graph. 
 * 
		if( graph.getNodes().size() > 0 )
		{
			float minx =  Float.MAX_VALUE;
			float maxx = -Float.MAX_VALUE;
			float miny =  Float.MAX_VALUE;
			float maxy = -Float.MAX_VALUE;

			float minAreaX =  Float.MAX_VALUE;
			float maxAreaX = -Float.MAX_VALUE;
			float minAreaY =  Float.MAX_VALUE;
			float maxAreaY = -Float.MAX_VALUE;
	
			for( GraphicNode node: graph.getNodes() )
			{
				float x0, y0, x1, y1;
	
				x0 =      node.boundsX;
				y0 =      node.boundsY;
				x1 = x0 + node.boundsW;
				y1 = y0 + node.boundsH;
	
				if( x1 > maxx ) maxx = x1;
				if( x0 < minx ) minx = x0;
				if( y1 > maxy ) maxy = y1;
				if( y0 < miny ) miny = y0;
				
				if( node.x < minAreaX ) minAreaX = node.x;
				if( node.x > maxAreaX ) maxAreaX = node.x;
				if( node.y < minAreaY ) minAreaY = node.y;
				if( node.y > maxAreaY ) maxAreaY = node.y;
			}

			if( graph.getSpriteManager() != null )
			{
				Iterator<GraphicSprite> sprites = graph.getSpriteManager().getSpriteIterator();

				while( sprites.hasNext() )
				{
					GraphicSprite sprite = sprites.next();
				
					float x0, y0, x1, y1;
				
					x0 =      sprite.boundsX;
					y0 =      sprite.boundsY;
					x1 = x0 + sprite.boundsW;
					y1 = y0 + sprite.boundsH;
	
					if( x1 > maxx ) maxx = x1;
					if( x0 < minx ) minx = x0;
					if( y1 > maxy ) maxy = y1;
					if( y0 < miny ) miny = y0;
				}
			}

			ctx.setBounds( minx, miny, ( maxx-minx ), ( maxy - miny ) );
			ctx.areaX = minAreaX;
			ctx.areaY = minAreaY;
			ctx.areaW = ( maxAreaX - minAreaX );
			ctx.areaH = ( maxAreaY - minAreaY );
		}
*/
	}

//	FpsCounter fpsCounter;
	
	@Override
	public void paintComponent( Graphics g )
	{
		Graphics2D g2 = (Graphics2D) g;
/*		
		if( benchmark )
		{
			if( fpsCounter == null )
				fpsCounter = new FpsCounter();
			fpsCounter.beginFrame();
		}
*/		
		computeDisplaySize();
		super.paintComponent( g );
		checkRatio();
		setupG2( g2 );
		drawGraphStyle( g2  );
		showLayoutCompletion( g2 );
		showScale( g2 );
		showSteps( g2 );
		
		// Check the rotation.

		AffineTransform transform = g2.getTransform();

		if( theta != 0 )
		{
			g2.rotate( theta * (Math.PI/180f), getWidth()/2, getHeight()/2 );
		}

		// Draw the shadows.
		
		Set<GraphicElement> shadows = graph.getShadowCasts();
		
		if( shadows != null )
		{
			for( GraphicElement element: shadows )
			{
				if( element instanceof GraphicEdge )
				{
					edgeRenderer.renderEdgeShadow( (GraphicEdge)element );
				}
				else if( element instanceof GraphicNode )
				{
					nodeRenderer.renderNodeShadow( (GraphicNode)element );
				}
				else if( element instanceof GraphicSprite )
				{
					spriteRenderer.renderSpriteShadow( (GraphicSprite)element );
				}
			}
		}
		
		// Draw each element, in z-index order.
		
		ArrayList<GraphicGraph.Elements> zIndex = graph.getZIndex();

		int i = graph.getLowestZIndex();
		int n = graph.getHighestZIndex();
		
		for( ; i<=n; i++ )
		{
			GraphicGraph.Elements elements = zIndex.get( i );

			if( elements != null )
			{
				if( elements.edges.size() > 0 )
					edgeRenderer.renderEdges(  elements.edges );
				
				if( elements.nodes.size() > 0 )
					nodeRenderer.renderNodes( elements.nodes );
				
				if( elements.sprites.size() > 0 )
					spriteRenderer.renderSprites( elements.sprites );
			}
		}
		
		g2.setTransform( transform );
/*		
		if( benchmark )
			fpsCounter.endFrame();
*/	}

	/**
	 * Check the scaling ratio so that the graph fills the window and conserve its aspect.
	 */
	protected void checkRatio()
	{
		if( ctx.areaW == 0 ) ctx.areaW = 0.1f;	// Avoid a division by zero...
		if( ctx.areaH == 0 ) ctx.areaH = 0.1f;
		
		float winW   = getWidth()  - ( ctx.border * 2 );
		float winH   = getHeight() - ( ctx.border * 2 );
		float ratioX = winW / ctx.areaW;
		float ratioY = winH / ctx.areaH;
		
		ctx.canvasW = getWidth();
		ctx.canvasH = getHeight();

		if( ratioY < ratioX )
		{
			ctx.ratio = ratioY;
			float cw  = ctx.pixelsToGu( ctx.canvasW );
			float bw  = ctx.pixelsToGu( ctx.border );
			ctx.centerOffX  = ( ( cw ) - (ctx.graphW+(bw*2)) ) / 2; 
			ctx.centerOffY  = 0;
		}
		else
		{
			ctx.ratio = ratioX;
			float ch  = ctx.pixelsToGu( ctx.canvasH );
			float bh  = ctx.pixelsToGu( ctx.border );
			ctx.centerOffY  = (( ch ) - (ctx.graphH+(bh*2)) ) / 2;
			ctx.centerOffX  = 0;
		}
	}
	
	/**
	 * Draw an indicator the layout process progression.
	 * @param g2 The graphics.
	 */
	protected void showLayoutCompletion( Graphics2D g2 )
	{
		if( layoutProxy != null )
		{
			Color c = new Color( 1f, 0f, 0f, 1f - layoutProxy.getCompletion() );
			g2.setColor( c );
			g2.fillOval( 3, 3, 10, 10 );
		}
	}
	
	/**
	 * Draw the graph scale.
	 * @param g2 The graphics.
	 */
	protected void showScale( Graphics2D g2 )
	{
		if( showFPS )
		{
			int y = 15;
			int offy = 15;
			
			g2.setColor( Color.GRAY );
			g2.setFont( ctx.fontCache.getDefaultFont() );
			g2.drawString( String.format( "w %.3f  h %.3f", ctx.areaW, ctx.areaH ), 20, y );
			y += offy;
			if( ctx.panx != 0 || ctx.pany != 0 )
				g2.drawString( String.format( "pan %.3f %.3f", ctx.panx, ctx.pany ), 20, y );
		}
	}
	
	protected void showSteps( Graphics2D g2 )
	{
		if( showSteps )
		{
			g2.setColor( Color.DARK_GRAY );
			g2.setFont( ctx.fontCache.getFont( "Bitstream Vera Sans", TextStyle.BOLD, 12 ) );
			g2.drawString( String.format( "%.3f", graph.step ), 50, 15 );
		}
/*		
		if( benchmark )
		{
			g2.setColor( Color.DARK_GRAY );
			g2.setFont( ctx.fontCache.getFont( "Bitstream Vera Sans", TextStyle.BOLD, 12 ) );
			g2.drawString( String.format( "fps %.6f", fpsCounter.getAverageFramesPerSecond() ), 70, 15 );
		}
*/	}

	/**
	 * Setup the graphics with the current quality settings.
	 * @param g2 The graphics.
	 */
	protected void setupG2( Graphics2D g2 )
	{
		ctx.g2 = g2;
		
		switch( quality )
		{
			case 0:
				ctx.g2.setRenderingHint( RenderingHints.KEY_INTERPOLATION,       RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR );
				ctx.g2.setRenderingHint( RenderingHints.KEY_RENDERING,           RenderingHints.VALUE_RENDER_SPEED );
				ctx.g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING,   RenderingHints.VALUE_TEXT_ANTIALIAS_OFF );
			    ctx.g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING,        RenderingHints.VALUE_ANTIALIAS_OFF );
			    ctx.g2.setRenderingHint( RenderingHints.KEY_COLOR_RENDERING,     RenderingHints.VALUE_COLOR_RENDER_SPEED );
			    ctx.g2.setRenderingHint( RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED );
			    ctx.g2.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL,      RenderingHints.VALUE_STROKE_PURE );
				break;
			case 1:
				ctx.g2.setRenderingHint( RenderingHints.KEY_INTERPOLATION,       RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR );
				ctx.g2.setRenderingHint( RenderingHints.KEY_RENDERING,           RenderingHints.VALUE_RENDER_SPEED );
				ctx.g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING,   RenderingHints.VALUE_TEXT_ANTIALIAS_OFF );
			    ctx.g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING,        RenderingHints.VALUE_ANTIALIAS_OFF );
			    ctx.g2.setRenderingHint( RenderingHints.KEY_COLOR_RENDERING,     RenderingHints.VALUE_COLOR_RENDER_SPEED );
			    ctx.g2.setRenderingHint( RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED );
			    ctx.g2.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL,      RenderingHints.VALUE_STROKE_PURE );
				break;
			case 2:
				ctx.g2.setRenderingHint( RenderingHints.KEY_INTERPOLATION,       RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR );
				ctx.g2.setRenderingHint( RenderingHints.KEY_RENDERING,           RenderingHints.VALUE_RENDER_DEFAULT );
				ctx.g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING,   RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
			    ctx.g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING,        RenderingHints.VALUE_ANTIALIAS_ON );
			    ctx.g2.setRenderingHint( RenderingHints.KEY_COLOR_RENDERING,     RenderingHints.VALUE_COLOR_RENDER_SPEED );
			    ctx.g2.setRenderingHint( RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED );
			    ctx.g2.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL,      RenderingHints.VALUE_STROKE_PURE );
				break;
			case 3:
				ctx.g2.setRenderingHint( RenderingHints.KEY_INTERPOLATION,       RenderingHints.VALUE_INTERPOLATION_BILINEAR );
				ctx.g2.setRenderingHint( RenderingHints.KEY_RENDERING,           RenderingHints.VALUE_RENDER_DEFAULT );
				ctx.g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING,   RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
			    ctx.g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING,        RenderingHints.VALUE_ANTIALIAS_ON );
			    ctx.g2.setRenderingHint( RenderingHints.KEY_COLOR_RENDERING,     RenderingHints.VALUE_COLOR_RENDER_SPEED );
			    ctx.g2.setRenderingHint( RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED );
			    ctx.g2.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL,      RenderingHints.VALUE_STROKE_PURE );
				break;
			case 4:
				ctx.g2.setRenderingHint( RenderingHints.KEY_INTERPOLATION,       RenderingHints.VALUE_INTERPOLATION_BICUBIC );
				ctx.g2.setRenderingHint( RenderingHints.KEY_RENDERING,           RenderingHints.VALUE_RENDER_QUALITY );
				ctx.g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING,   RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
			    ctx.g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING,        RenderingHints.VALUE_ANTIALIAS_ON );
			    ctx.g2.setRenderingHint( RenderingHints.KEY_COLOR_RENDERING,     RenderingHints.VALUE_COLOR_RENDER_QUALITY );
			    ctx.g2.setRenderingHint( RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY );
			    ctx.g2.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL,      RenderingHints.VALUE_STROKE_PURE );
				break;
		}
	}
	
	/**
	 * Check the style that applies to the graph and draw it.
	 * @param g2 The graphics.
	 */
	protected void drawGraphStyle( Graphics2D g2 )
	{
		Style style = graph.getStyle(); //graph.getStyleSheet().getStyleFor( graph );
		
		if( style == null )
			style = graph.getStyleSheet().getStyleFor( graph );	// This can happen at start.
		
		setBackground( style.getBackgroundColor() );
		
		String imageUrl = style.getImageUrl();
		
		if( imageUrl != null )
		{
			ctx.setBackgroundImage( imageUrl );
			ctx.drawBackground      = true;
			ctx.backgroundPositionX = ctx.xToPixels( style.getImageOffsetX() );
			ctx.backgroundPositionY = ctx.yToPixels( style.getImageOffsetY() ) + ctx.yOffsetToPixels( style.getHeight() );
			ctx.backgroundWidth     = ctx.toPixels( style.getWidth() );
			ctx.backgroundHeight    = ctx.toPixels( style.getHeight() );
				
			initBackground();
		}
		else
		{
			ctx.drawBackground = false;
		}

		//
		// Old way to draw the background, from the environment. Still compatible.
		//
		if( ctx.drawBackground )
		{
			if( ctx.backgroundHeight != 0f )
			{
				g2.drawImage( background,
						(int) ctx.backgroundPositionX,
						(int) ctx.backgroundPositionY,
						(int) ctx.backgroundWidth,
					    (int) ctx.backgroundHeight, null );
			}
			else
			{
				if( background != null )
					g2.drawImage( background,
							(int) ctx.xGuToPixels( ctx.areaX ),
							(int) ctx.yGuToPixels( ctx.areaY ),
							(int) ctx.guToPixels( ctx.areaW ),
							(int) ctx.guToPixels( ctx.areaH ), null );
			}
		}
		
		// Draw a border.
		
		int bbw = (int) ctx.toPixels( style.getBorderWidth() );
		
		int gw = ctx.border*2 + (int)ctx.guToPixels( ctx.areaW );
		int gh = ctx.border*2 + (int)ctx.guToPixels( ctx.areaH );
		int cw = ctx.canvasW;
		int ch = ctx.canvasH;
		
		if( gw+1 < cw )
		{
			g2.setColor( Color.GRAY );
			int w = ( ( cw - gw ) / 2 ); 
			g2.fillRect( 0, 0, w, ch );
			g2.fillRect( gw+w, 0, w, ch );
		}
		else if( gh+1 < ch )
		{
			g2.setColor( Color.GRAY );
			int h = ( ( ch - gh ) / 2 );
			g2.fillRect( 0, 0, cw, h );
			g2.fillRect( 0, gh+h, cw, h );
		}
		
		if( quality > 3 )
		{
			// Draw shadows !!!
			
			if( gw+1 < cw )
			{
				int w = ( ( cw - gw ) / 2 ); 
				g2.setPaint( new GradientPaint( w-10, 0, Color.GRAY, w, 0, Color.DARK_GRAY ) );
				g2.fillRect( w-10, 0, 10, ch );
				g2.setPaint( new GradientPaint( gw+w+1, 0, Color.DARK_GRAY, gw+w+11, 0, Color.GRAY ) );
				g2.fillRect( gw+w, 0, 10, ch );
			}
			else if( gh+1 < ch )
			{
				int h = ( ( ch - gh ) / 2 );
				g2.setPaint( new GradientPaint( 0, h-10, Color.GRAY, 0, h, Color.DARK_GRAY ) );
				g2.fillRect( 0, h-10, cw, 10 );
				g2.setPaint( new GradientPaint( 0, gh+h+1, Color.DARK_GRAY, 0, gh+h+11, Color.GRAY ) );
				g2.fillRect( 0, gh+h, cw, 10 );
			}
			
			g2.setPaint( null );
		}
		
		if( bbw > 0 )
		{
			Color color = style.getBorderColor();
			
			g2.setColor( color );
			ctx.chooseBorderStroke( bbw );

			int p = (int)ctx.toPixels( style.getPadding() );
			int x = ((int)ctx.xGuToPixels( ctx.graphX )) - p;
			int y = ((int)( ctx.yGuToPixels( ctx.graphY ) - ctx.guToPixels( ctx.graphH ) )) - p;
			int w = ((int)ctx.guToPixels( ctx.graphW )) + p*2;
			int h = ((int)ctx.guToPixels( ctx.graphH )) + p*2;
			
			g2.drawRect( x, y, w, h );
		}
		
//		drawGraphBounds( g2 );
	}
	
	public void drawGraphBounds( Graphics2D g2 )
	{
		g2.setColor( Color.RED );
		g2.drawRect(
				(int)ctx.xGuToPixels( ctx.graphX ),
				(int)(ctx.yGuToPixels( ctx.graphY )-ctx.guToPixels( ctx.graphH )),
				(int)ctx.guToPixels( ctx.graphW ),
				(int)ctx.guToPixels( ctx.graphH )
			);
		g2.setColor( Color.GREEN );
		g2.drawRect(
				(int)ctx.xGuToPixels( ctx.graphX ) - ctx.border,
				(int)(ctx.yGuToPixels( ctx.graphY )-ctx.guToPixels( ctx.graphH )) - ctx.border,
				(int)ctx.guToPixels( ctx.graphW ) + ctx.border*2,
				(int)ctx.guToPixels( ctx.graphH ) + ctx.border*2
			);
	}
	
	public void display()
	{
		time++;
		
		boolean changed = ctx.step();
		boolean layChgd = checkLayoutCompletion();
		
		if( graph.graphChanged || changed || layChgd || benchmark )
		{
			repaint();
			
			graph.graphChanged = false;
		}
		
		if( outputFrames != null )
		{
			String n = String.format( Locale.US, "%06d", time );
			
			screenshot( outputFrames + n + ".jpg" );
		}
	}
	
	protected boolean checkLayoutCompletion()
	{
		if( layoutProxy != null )
		{
			float lc = layoutProxy.getCompletion();

			if( lc != lastLayoutCompletion )
			{
				lastLayoutCompletion = lc;
				return true;
			}
		}
		
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.miv.graphstream.ui.GraphRenderer#screenshot(java.lang.String)
	 */
	public void screenshot( String filename )
	{
		if( filename.endsWith( "svg" ) || filename.endsWith( "SVG" ) )
		{
			// Get a DOMImplementation.
			DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();

			// Create an instance of org.w3c.dom.Document.
			String svgNS = "http://www.w3.org/2000/svg";
			Document document = domImpl.createDocument( svgNS, "svg", null );

			// Create an instance of the SVG Generator.
			SVGGraphics2D svgGenerator = new SVGGraphics2D( document );

			// Ask the test to render into the SVG Graphics2D implementation.
			paintComponent( svgGenerator );

			// Finally, stream out SVG to the standard output using
			// UTF-8 encoding.
			boolean useCSS = true; // we want to use CSS style attributes
			Writer out = null;
			try
			{
				out = new OutputStreamWriter( new PrintStream( new File( filename ) ), "UTF-8" );
			}
			catch( UnsupportedEncodingException e )
			{
				e.printStackTrace();
			}
			catch( FileNotFoundException e )
			{
				e.printStackTrace();
			}
			try
			{
				svgGenerator.stream( out, useCSS );
			}
			catch( SVGGraphics2DIOException e )
			{
				e.printStackTrace();
			}
			svgGenerator.dispose();
		}
		else if( filename.endsWith( "png" ) || filename.endsWith( "PNG" ) )
		{
			BufferedImage img = new BufferedImage( getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB );
			paintComponent( img.createGraphics() );

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
			BufferedImage img = new BufferedImage( getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB );
			paintComponent( img.createGraphics() );

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
			BufferedImage img = new BufferedImage( getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB );
			paintComponent( img.createGraphics() );

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

	public Context getStyleSheet()
	{
		return this.ctx;
	}

	public void setStyleSheet( Context ss )
	{
		this.ctx = ss;
	}

	/* (non-Javadoc)
	 * @see org.miv.graphstream.ui.GraphRenderer#moveNode(java.lang.String, int, int)
	 */
	public void moveNode( String id, int x, int y )
	{
		graph.moveNode( id,
				ctx.xPixelsToGu( x ),
				ctx.yPixelsToGu( y ), 0f );

		graph.graphChanged = true;
	}
	
	public void showFPS( boolean on )
	{
		showFPS = on;
		graph.graphChanged = true;
	}
	
	public void setQuality( int value )
	{
		if( value < 0 )
			value = 0;
		else if( value > 4 )
			value = 4;
	
		quality = value;
		graph.graphChanged = true;
	}
	
	public void setStepsVisible( boolean on )
	{
		showSteps = on;
	}

	public int getQuality()
	{
		return quality;
	}
	
	public void setZoom( float zoomValue )
	{
		if( zoomValue <= 0  )
			zoomValue = 0.01f;
		
		ctx.setZoom( zoomValue );		
		graph.graphChanged = true;
	}
	
	public void setTranslation( float dx, float dy )
	{
		if( dx < -1 ) dx = -1;
		else if( dx > 1 ) dx = 1;
		if( dy < -1 ) dy = -1;
		else if( dy > 1 ) dy = 1;
		
		ctx.panx = dx;
		ctx.pany = dy;
		graph.graphChanged = true;
	}

	public void showEdges( boolean on )
	{
		ctx.drawEdges = on;
		graph.graphChanged = true;
	}
	
	public void showNodes( boolean on )
	{
		ctx.drawNodes = on;
		graph.graphChanged = true;
	}
	
	public void setRotation( float theta, float phi )
	{
		this.theta = theta; 
		graph.graphChanged = true;
	}
/*
	protected float oldTheta = 0;
	
	public void setRotation( float theta, float phi )
	{
		float angle = theta - oldTheta;
		oldTheta = theta;
		
		rotateGraph( angle );
	}
*/
	/* (non-Javadoc)
	 * @see org.miv.graphstream.ui.GraphRenderer#showEdgeLabels(boolean)
	 */
	public void showEdgeLabels( boolean on )
	{
		ctx.drawEdgeLabels = on;
		graph.graphChanged = true;
	}

	/* (non-Javadoc)
	 * @see org.miv.graphstream.ui.GraphRenderer#showNodeLabels(boolean)
	 */
	public void showNodeLabels( boolean on )
	{
		ctx.drawNodeLabels = on;		
		graph.graphChanged = true;
	}
/*	
	protected void rotateGraph( float angle )
	{
		Matrix4 rot = new Matrix4();
		Vector3 vec = new Vector3();
		Vector3 res = new Vector3();
		
		rot.setZRotation( angle );
		
		for( GraphicNode node: graph.getNodes() )
		{
			vec.set( node.x, node.y, node.z );
			rot.vec3Mult( vec, res );
			node.x = res.data[0];
			node.y = res.data[1];
			node.z = res.data[2];
		}		
	}
*/
	public void outputFrames( String baseFilename )
	{
		outputFrames = baseFilename;
	}
	
	public void showEdgeDirection( boolean on )
	{
		ctx.drawEdgeArrows = on;
		graph.graphChanged = true;
	}
	
// MouseListener

	protected GraphicElement selection;
	
	public void mouseClicked( MouseEvent e )
    {
		if( e.getButton() == 3 )
		{
			if( settingsWin == null )
			{
				settingsWin = new SettingsWindow( viewer );
			}
			
			settingsWin.setVisible( !settingsWin.isVisible() );
		}
    }

	public void mouseEntered( MouseEvent e )
    {
    }

	public void mouseExited( MouseEvent e )
    {
    }

	int mouseStartX = 0;
	int mouseStartY = 0;
	
	public void mousePressed( MouseEvent e )
	{
		selection = graph.findNodeOrSprite(
				ctx.xPixelsToGu( e.getX() ),
				ctx.yPixelsToGu( e.getY() ) );

		if( selection != null )
		{
			mouseStartX = e.getX();
			mouseStartY = e.getY();
			
			if( selection instanceof GraphicNode )
			{
				selection.addAttribute( "ui.clicked" );
				viewer.sendNodeSelectedEvent( selection.getId(), true );
				
				if( viewer.getLayoutRemote() != null )
					viewer.getLayoutRemote().freezeNode( selection.getId(), true );
			}
			else
			{
				selection.addAttribute( "ui.clicked" );
				viewer.sendSpriteSelectedEvent( selection.getId(), true );
			}
		}
		else
		{
			viewer.sendBackgroundClickedEvent(
				ctx.xPixelsToGu( e.getX() ),
				ctx.yPixelsToGu( e.getY() ),
				e.getButton(), true );
		}
    }

	public void mouseReleased( MouseEvent e )
    {
		if( selection != null )
		{
			if( selection instanceof GraphicNode )
			{
				selection.removeAttribute( "ui.clicked" );
				viewer.sendNodeSelectedEvent( selection.getId(), false );
				
				if( e.getX() != mouseStartX || e.getY() != mouseStartY )
				{
					float oldx = selection.getX();
					float oldy = selection.getY();
			    	float xx   = ctx.xPixelsToGu( e.getX() );
			    	float yy   = ctx.yPixelsToGu( e.getY() );
				
					if( viewer.getLayoutRemote() == null )
						selection.move( xx, yy, 0 );
				
					if( viewer.getLayoutRemote() != null )
						viewer.getLayoutRemote().forceMoveNode( selection.getId(), xx-oldx, yy-oldy, 0 );
					
					viewer.sendNodeMovedEvent( selection.getId(), xx, yy, 0 );
				}

				if( viewer.getLayoutRemote() != null )
					viewer.getLayoutRemote().freezeNode( selection.getId(), false );
			}
			else
			{
				selection.removeAttribute( "ui.clicked" );
				//nodeSelection.move( ctx.pixelsToX( e.getX() ), ctx.pixelsToY( e.getY() ), 0 );
				if( e.getX() != mouseStartX || e.getY() != mouseStartY )
					viewer.sendSpriteMovedEvent( selection.getId(), selection.getX(), selection.getY(), 0 );
				viewer.sendSpriteSelectedEvent( selection.getId(), false );				
			}
			
			selection = null;
		}
		else
		{
			viewer.sendBackgroundClickedEvent(
				ctx.xPixelsToGu( e.getX() ),
				ctx.yPixelsToGu( e.getY() ),
				e.getButton(), false );
		}
    }

	public void mouseDragged( MouseEvent e )
    {
	    if( selection != null && selection instanceof GraphicNode )
	    {
	    	float oldx = selection.getX();
	    	float oldy = selection.getY();
	    	float xx   = ctx.xPixelsToGu( e.getX() );
	    	float yy   = ctx.yPixelsToGu( e.getY() );
	    	
			if( viewer.getLayoutRemote() == null )
				selection.move( xx, yy, 0 );
			
			if( selection instanceof GraphicNode )
			{
				if( viewer.getLayoutRemote() != null )
					viewer.getLayoutRemote().forceMoveNode( selection.getId(), xx-oldx, yy-oldy, 0 );
			    
				viewer.sendNodeMovedEvent( selection.getId(), xx, yy, 0 );					
			}
	    }
    }

	public void mouseMoved( MouseEvent e )
    {
	    
    }

	public void keyPressed( KeyEvent e )
    {
    }

	public void keyReleased( KeyEvent e )
    {
		if( e.isAltDown() )
		{
			switch( e.getKeyCode() )
			{
				case KeyEvent.VK_R:
					ctx.panx = ctx.pany = 0;
					ctx.zoom = 1;
					ctx.zm.setValue( 1 );
					graph.graphChanged = true;
					break;
				case KeyEvent.VK_RIGHT:
					ctx.panx -= 0.05f;
					graph.graphChanged = true;
					break;
				case KeyEvent.VK_LEFT:
					ctx.panx += 0.05f;
					graph.graphChanged = true;
					break;
				case KeyEvent.VK_DOWN:
					ctx.pany += 0.05f;
					graph.graphChanged = true;
					break;
				case KeyEvent.VK_UP:
					ctx.pany -= 0.05f;
					graph.graphChanged = true;
					break;
				case KeyEvent.VK_PAGE_UP:
					ctx.incrZoom( 0.1f );
					graph.graphChanged = true;
					break;
				case KeyEvent.VK_PAGE_DOWN:
					ctx.incrZoom( -0.1f );
					graph.graphChanged = true;
					break;
				default:
					// NOP.
					break;
			}
		}
     }

	protected boolean benchmark = false;
	
	public void keyTyped( KeyEvent e )
    {
		if( e.getKeyChar() == 'B' )
		{
			if( ! benchmark )
				viewer.timer.setDelay( 1 );
			else
				viewer.timer.setDelay( viewer.msPerFrame );
			
			benchmark = ! benchmark;
			
			graph.graphChanged = true;
		}
    }
}