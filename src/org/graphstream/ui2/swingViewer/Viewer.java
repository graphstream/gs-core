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

package org.graphstream.ui2.swingViewer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.Timer;

import org.graphstream.io.ProxyFilter;
import org.graphstream.io.thread.ThreadProxyPipe;
import org.graphstream.ui2.graphicGraph.GraphicGraph;
import org.graphstream.ui2.swingViewer.basicRenderer.SwingBasicGraphRenderer;
import org.graphstream.ui2.swingViewer.util.FontCache;
import org.graphstream.ui2.swingViewer.util.ImageCache;
import org.miv.util.geom.Point3;

/**
 * Set of views on a graphic graph.
 */
public class Viewer implements ActionListener
{
// Attributes
	
	/**
	 * Name of the default view.
	 */
	public static String DEFAULT_VIEW_ID = "defaultView";
	
	/**
	 * What to do when a view frame is closed.
	 */
	public static enum CloseFramePolicy { CLOSE_VIEWER, HIDE_ONLY };
	
// Attribute
	
	/**
	 * The graph observed by the views.
	 */
	protected GraphicGraph graph;
	
	/**
	 * The source of graph events.
	 */
	protected ProxyFilter input;
	
	/**
	 * Timer in the Swing thread.
	 */
	protected Timer timer;
	
	/**
	 * Delay in milliseconds between frames.
	 */
	protected int delay = 40;
	
	/**
	 * The set of views.
	 */
	protected HashMap<String,View> views = new HashMap<String,View>();

	/**
	 * What to do when a view frame is closed.
	 */
	protected CloseFramePolicy closeFramePolicy = CloseFramePolicy.HIDE_ONLY;
	
	/**
	 * Set of images.
	 */
	protected ImageCache images;
	
	/**
	 * Set of fonts.
	 */
	protected FontCache fonts;

// Construction
	
	public Viewer( ProxyFilter input )
	{
		this( new GraphicGraph(), input );
	}
	
	public Viewer( GraphicGraph graph )
	{
		this( graph, null );
	}
	
	protected Viewer( GraphicGraph graph, ProxyFilter input )
	{
		this.graph  = graph;
		this.input  = input;
		this.timer  = new Timer( delay, this );
		this.images = new ImageCache();
		this.fonts  = new FontCache();
		
		if( input != null )
			input.addGraphListener( graph );

		timer.setCoalesce( true );
		timer.setRepeats( true );
		timer.start();
	}
	
	/**
	 * Close definitively this viewer and all its views.
	 */
	public void close()
	{
		synchronized( views )
		{
			timer.stop();
			input.removeGraphListener( graph );
		
			for( View view: views.values() )
				view.close( graph );

			graph = null;
			input = null;
			timer = null;
		}
	}
	
// Access
	
	/**
	 * What to do when a frame is closed.
	 */
	public CloseFramePolicy getCloseFramePolicy()
	{
		return closeFramePolicy;
	}
	
	public ThreadProxyPipe getThreadProxyOnGraphicGraph()
	{
		return new ThreadProxyPipe( graph );
	}
	
	/**
	 * The underlying graphic graph.
	 */
	public GraphicGraph getGraphicGraph()
	{
		return graph;
	}

	/**
	 * The view that correspond to the given identifier.
	 * @param id The view identifier.
	 * @return A view or null if not found.
	 */
	public View getView( String id )
	{
		synchronized( views )
		{
			return views.get( id );
		}
	}
	
	/**
	 * Set of fonts.
	 */
	public FontCache getFontCache()
	{
		return fonts;
	}

	/**
	 * Set of images.
	 */
	public ImageCache getImageCache()
	{
		return images;
	}

// Command

	/**
	 * Build the default graph view and insert it. The view identifier is {@link #DEFAULT_VIEW_ID}.
	 * You can request the view to be open in its own frame.
	 * @param openInAFrame It true, the view is placed in a frame, else the view is only created
	 * and you must embed it yourself in your application.
	 */
	public View addDefaultView( boolean openInAFrame )
	{
		synchronized( views )
		{
			View view = new DefaultView( this, DEFAULT_VIEW_ID, new SwingBasicGraphRenderer() ); // new SwingBasicGraphView( this, DEFAULT_VIEW_ID );
//			View view = new DefaultView( this, DEFAULT_VIEW_ID, new SwingGraphRenderer() );
			addView( view );
		
			if( openInAFrame )
				view.openInAFrame( true );
			
			return view;
		}
	}
	
	/**
	 * Add a view using its identifier identifier. If there was already a view with this identifier,
	 * it is closed and returned.
	 * @param view The view to add.
	 * @return The old view that was at the given identifier, if any, else null.
	 */
	public View addView( View view )
	{
		synchronized( views )
		{
			View old = views.put( view.getId(), view );
			
			if( old != null )
				old.close( graph );
		
			return old;
		}
	}
	
	/**
	 * Remove a view.
	 * @param id The view identifier.
	 */
	public void removeView( String id )
	{
		synchronized( views )
		{
			views.remove( id );
		}
	}
	
	/**
	 * Never call this method.
	 */
	public void actionPerformed( ActionEvent e )
    {
		if( input != null )
			input.checkEvents();

		boolean changed = graph.graphChangedFlag();
	
		if( changed )
			computeGraphMetrics();
		
		synchronized( views )
		{
			for( View view: views.values() )
				view.display( graph, changed );
		}
			
		graph.resetGraphChangedFlag();
    }

	/**
	 * Compute the overall bounds of the graphic graph according to the nodes and sprites positions.
	 * We can only compute the graph bounds from the nodes and sprites centres since the node and
	 * graph bounds may in certain circumstances be computed according to the graph bounds. The
	 * bounds are stored in the graph metrics. 
	 */
	protected void computeGraphMetrics()
	{
		graph.computeBounds();

		synchronized( views )
		{
			Point3 lo = graph.getMinPos();
			Point3 hi = graph.getMaxPos();
			
			for( View view: views.values() )
				view.setBounds( lo.x, lo.y, lo.z, hi.x, hi.y, hi.z );
		}
	}
}