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

package org.miv.graphstream.ui2.swingViewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import org.miv.graphstream.ui2.graphicGraph.GraphicGraph;
import org.miv.graphstream.ui2.swingViewer.View;
import org.miv.graphstream.ui2.swingViewer.Viewer;

/**
 * Base for constructing views.
 * 
 * <p>
 * This base view is an abstract class that provides mechanism that are necessary in any view :
 * <ul>
 * 		<li>the painting and repainting mechanism.</li>
 * 		<li>the optional frame handling.</li>
 * 		<li>the frame closing protocol.</li>
 * </ul>
 * </p>
 * 
 * <h3>The painting mechanism</h3>
 * 
 * 		<p>This mechanism pushes a repaint query each time the viewer asks us to repaint. Two flags
 * 		are provided to know what to repaint : {@link #graphChanged} allows to know when the graph
 *		needs to be rendered anew because its structure changed and {@link #canvasChanged} allows
 *		to know one must repaint because the rendering canvas was resized, shown, etc.</p>
 *
 *		<p>The main method to implement is {@link #render(Graphics2D)}. This method is called each
 *		time the graph needs to be rendered anew in the canvas.</p>
 *
 *		<p>The {@link #render(Graphics2D)} is called only when a repainting is really needed.</p>
 * 
 * <h3>The optional frame handling</h3>
 * 
 * 		<p>This abstract view is able to create a frame that is added around this panel (each view
 *		is a JPanel instance). The frame can be removed at any time.</p>
 * 
 * <h3>The frame closing protocol</h3>
 * 
 * 		<p>This abstract view handles the closing protocol. This means that it will close the view
 * 		if needed, or only hide it to allow reopening it later. Furthermore it adds the "ui.viewClosed"
 * 		attribute to the graph when the view is closed or hidden, and removes it when the view
 * 		is shown. The value of this graph attribute is the identifier of the view.</p>
 */
public abstract class ViewBase extends View implements ComponentListener, WindowListener
{
// Attribute
	
	/**
	 * Parent viewer.
	 */
	protected Viewer viewer;
	
	/**
	 * The graph to render.
	 */
	protected GraphicGraph graph;
	
	/**
	 * The (optional) frame.
	 */
	protected JFrame frame;
	
	/**
	 * The graph changed since the last repaint.
	 */
	protected boolean graphChanged;
	
	/**
	 * True as soon as the canvas dimensions changed.
	 */
	protected boolean canvasChanged = true;

	protected ShortcutManager shortcuts;

// Construction
	
	public ViewBase( Viewer viewer, String identifier )
	{
		super( identifier );
		
		this.viewer = viewer;
		this.graph  = viewer.getGraphicGraph();
		shortcuts   = new ShortcutManager( this );
		
		addComponentListener( this );
		addKeyListener( shortcuts );
	}
	
// Access
	
// Command	

	@Override
	public void display( GraphicGraph graph, boolean graphChanged )
    {
		this.graphChanged = graphChanged;
		
		repaint();
    }
	
	@Override
	public void paint( Graphics g )
	{
		if( graphChanged || canvasChanged )
		{
			Graphics2D g2 = (Graphics2D) g;

			super.paint( g );
			setupGraphics( g2 );
			render( g2 );

			graphChanged = canvasChanged = false;
		}
    }

	@Override
    public void close( GraphicGraph graph )
    {
		graph.addAttribute( "ui.viewClosed", getId() );
		removeComponentListener( this );
		removeKeyListener( shortcuts );
	    openInAFrame( false );
    }
	
	@Override
	public void openInAFrame( boolean on )
	{
		if( on )
		{
			if( frame == null )
			{
				frame = new JFrame( "GraphStream" );
				frame.setLayout( new BorderLayout() );
				frame.add( this, BorderLayout.CENTER );
				frame.setSize( 800, 600 );
				frame.setVisible( true );
				frame.addWindowListener( this );
				frame.addKeyListener( shortcuts );
			}
			else
			{
				frame.setVisible( true );
			}
		}
		else
		{
			if( frame != null )
			{
				frame.removeWindowListener( this );
				frame.removeKeyListener( shortcuts );
				frame.remove( this );
				frame.setVisible( false );
				frame.dispose();
			}
		}
	}
	
	public abstract void render( Graphics2D g );
	
// Utility
	
	protected void setupGraphics( Graphics2D g )
	{
		g.setRenderingHint( RenderingHints.KEY_INTERPOLATION,       RenderingHints.VALUE_INTERPOLATION_BICUBIC );
		g.setRenderingHint( RenderingHints.KEY_RENDERING,           RenderingHints.VALUE_RENDER_QUALITY );
		g.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING,   RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
	    g.setRenderingHint( RenderingHints.KEY_ANTIALIASING,        RenderingHints.VALUE_ANTIALIAS_ON );
	    g.setRenderingHint( RenderingHints.KEY_COLOR_RENDERING,     RenderingHints.VALUE_COLOR_RENDER_QUALITY );
	    g.setRenderingHint( RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY );
	    g.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL,      RenderingHints.VALUE_STROKE_PURE );
	}
	
	protected void displayNothingToDo( Graphics2D g )
	{
		int w = getWidth();
		int h = getHeight();
		
		String msg1 = "Graph width/height/depth is zero !!";
		String msg2 = "Place components using the 'xyz' attribute.";
		
		g.setColor( Color.RED );
		g.drawLine( 0, 0, w, h );
		g.drawLine( 0, h, w, 0 );
		
		float msg1length = g.getFontMetrics().stringWidth( msg1 );
		float msg2length = g.getFontMetrics().stringWidth( msg2 );
		
		float x = w/2;
		float y = h/2;

		g.setColor( Color.BLACK );
		g.drawString( msg1, x - msg1length/2, y-20 );
		g.drawString( msg2, x - msg2length/2, y+20 );
	}
	
// Component listener

	public void componentShown( ComponentEvent e )
    {
		canvasChanged = true;
    }

	public void componentHidden( ComponentEvent e )
    {
    }

	public void componentMoved( ComponentEvent e )
    {
    }

	public void componentResized( ComponentEvent e )
    {
		canvasChanged = true;
    }

// Window Listener
	
	public void windowActivated( WindowEvent e )
    {
		canvasChanged = true;
    }

	public void windowClosed( WindowEvent e )
    {
    }

	public void windowClosing( WindowEvent e )
    {
		graph.addAttribute( "ui.viewClosed", getId() );
		
		switch( viewer.getCloseFramePolicy() )
		{
			case CLOSE_VIEWER:
				viewer.removeView( getId() );
				break;
			case HIDE_ONLY:
				if( frame != null )
					frame.setVisible( true );
				break;
			default:
				throw new RuntimeException(
					String.format( "The %s view is not up to date, do not know %s CloseFramePolicy.",
						getClass().getName(), viewer.getCloseFramePolicy() ) );
		}
    }

	public void windowDeactivated( WindowEvent e )
    {
    }

	public void windowDeiconified( WindowEvent e )
    {
		canvasChanged = true;
    }

	public void windowIconified( WindowEvent e )
    {
    }

	public void windowOpened( WindowEvent e )
    {
		graph.removeAttribute( "ui.viewClosed" );
		canvasChanged = true;
    }
}