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

import org.graphstream.graph.Graph;
import org.graphstream.stream.ProxyPipe;
import org.graphstream.stream.thread.ThreadProxyPipe;
import org.graphstream.ui2.graphicGraph.GraphicGraph;
import org.graphstream.ui2.layout.Layout;
import org.graphstream.ui2.layout.LayoutRunner;
import org.graphstream.ui2.swingViewer.basicRenderer.SwingBasicGraphRenderer;
import org.graphstream.ui2.swingViewer.util.FontCache;
import org.graphstream.ui2.swingViewer.util.ImageCache;
import org.graphstream.ui.geom.Point3;

/**
 * Set of views on a graphic graph.
 * 
 * <p>
 * The viewer class is in charge of maintaining :
 * <ul>
 * 	<li>A "graphic graph" (a special graph that internally stores the graph under the form of style
 *      sets of "graphic" elements),</li>
 *	<li>The eventual proxy pipe from which the events come from (but graph events can come from any
 *      kind of source),</li>
 *  <li>A default view, and eventually more views on the graphic graph.</li>
 *  <li>A flag that allows to repaint the view only if the graphic graph changed.<li>
 * </ul>
 * </p>
 * 
 * <p>
 * The graphic graph can be created by the viewer or given at construction (to share it with another
 * viewer).
 * </p>
 * 
 * <p>
 * Once created, the viewer runs in a loop inside the Swing thread. You cannot call methods on
 * it directly if you are not in this thread. The only operation that you can use in other threads
 * is the constructor, the {@link #addView(View)}, {@link #removeView(String)} and the
 * {@link #close()} methods. Other methods are not protected from concurrent accesses.
 * </p>
 * 
 * <p>
 * Some constructors allow a "ProxyPipe" as argument. If given, the graphic graph is made listener
 * of this pipe and the pipe is "pumped" during the view loop. This allows to run algorithms on a
 * graph in the main thread (or any other thread) while letting the viewer run in the swing thread.
 * </p>
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
	
	/**
	 * How does the viewer synchronise its internal graphic graph with the graph displayed. The
	 * graph we display can be in the Swing thread (as will be the viewer, therefore in the
	 * same thread as the viewer), in another thread, or on a distant machine.
	 */
	public enum ThreadingModel { GRAPH_IN_SWING_THREAD, GRAPH_IN_ANOTHER_THREAD, GRAPH_ON_NETWORK };
	
// Attribute
	
	/**
	 * If true the graph we display is in another thread, the synchronisation between the graph
	 * and the graphic graph must therefore use thread proxies.
	 */
	protected boolean graphInAnotherThread = true;
	
	/**
	 * The graph observed by the views.
	 */
	protected GraphicGraph graph;
	
	/**
	 * If we have to pump events by ourself.
	 */
	protected ProxyPipe pumpPipe;
	
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
	
// Attribute
	
	protected LayoutRunner optLayout = null;
	
	protected ProxyPipe layoutPipeIn = null;

// Construction
	
	/**
	 * The graph is in another thread or machine, but the pipe already exists. The graphic graph 
	 * displayed by this viewer is created.
	 * @param source The source of graph events.
	 */
	public Viewer( ProxyPipe source )
	{
		graphInAnotherThread = true;
		init( new GraphicGraph( String.format( "GraphicGraph_%d", (int)(Math.random()*10000) ) ), source );
	}
	
	/**
	 * We draw a pre-existing graphic graph.
	 * @param graph THe graph to draw.
	 */
	public Viewer( GraphicGraph graph )
	{
		graphInAnotherThread = false;
		init( graph, (ProxyPipe)null );
	}
	
	/**
	 * New viewer on a graph. The viewer always run in the Swing thread, therefore, you must specify
	 * how it will take graph events from the graph you give. If the graph you give will be accessed
	 * only from the Swing thread use ThreadingModel.GRAPH_IN_SWING_THREAD. If the graph you use
	 * is accessed in another thread use ThreadingModel.GRAPH_IN_SWING_THREAD. This last scheme is
	 * more powerful since it allows to run algorithms on the graph in parallel with the viewer.
	 * @param graph The graph to render.
	 * @param threadingModel The threading model.
	 */
	public Viewer( Graph graph, ThreadingModel threadingModel )
	{
		switch( threadingModel )
		{
			case GRAPH_IN_SWING_THREAD:
				graphInAnotherThread = false;
				init( new GraphicGraph( String.format( "GraphicGraph_%d", (int)(Math.random()*10000) ) ), (ProxyPipe)null );
				break;
			case GRAPH_IN_ANOTHER_THREAD:
				graphInAnotherThread = true;
				init( new GraphicGraph( String.format( "GraphicGraph_%d", (int)(Math.random()*10000) ) ), new ThreadProxyPipe( graph, true ) );
				break;
			case GRAPH_ON_NETWORK:
				throw new RuntimeException( "TO DO, sorry !:-)" );
		}
	}
	
	protected void init( GraphicGraph graph, ProxyPipe source )
	{
		this.graph    = graph;
		this.pumpPipe = source;
		this.timer    = new Timer( delay, this );
		this.images   = ImageCache.defaultImageCache();
		this.fonts    = FontCache.defaultFontCache();
		
		if( source != null )
			source.addSink( graph );

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
			pumpPipe.removeSink( graph );
		
			for( View view: views.values() )
				view.close( graph );

			graph    = null;
			pumpPipe = null;
			timer    = null;
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
	
	/**
	 * New proxy pipe on events coming from the viewer through a thread.
	 * @return
	 */
	public ProxyPipe newThreadProxyOnGraphicGraph()
	{
		return new ThreadProxyPipe( graph );
	}
	
	/**
	 * New viewer pipe on the events coming from the viewer through a thread.
	 * @return
	 */
	public ViewerPipe newViewerPipe()
	{
		return new ViewerPipe( String.format( "viewer_%d", (int)(Math.random()*10000) ), new ThreadProxyPipe( graph ) );
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
	 * Add a view using its identifier. If there was already a view with this identifier,
	 * it is closed and returned (if different of the one added).
	 * @param view The view to add.
	 * @return The old view that was at the given identifier, if any, else null.
	 */
	public View addView( View view )
	{
		synchronized( views )
		{
			View old = views.put( view.getId(), view );
			
			if( old != null && old != view )
				old.close( graph );
		
			return old;
		}
	}
	
	/**
	 * Add a new default view with a specific renderer. If a view with the same id exists, it is
	 * removed and closed. By default the view is open in a frame.
	 * @param id The new view identifier.
	 * @param renderer The renderer to use.
	 * @return The created view.
	 */
	public View addView( String id, GraphRenderer renderer )
	{
		return addView( id, renderer, true );
	}
	
	/**
	 * Same as {@link #addView(String, GraphRenderer)} but allows to specify that the view uses a
	 * frame or not.
	 * @param id The new view identifier.
	 * @param renderer The renderer to use.
	 * @param openInAFrame If true the view is open in a frame, else the returned view is a JPanel
	 * that can be inserted in a GUI.
	 * @return The created view.
	 */
	public View addView( String id, GraphRenderer renderer, boolean openInAFrame )
	{
		synchronized( views )
		{
			View view = new DefaultView( this, id, renderer );
			addView( view );
			
			if( openInAFrame )
				view.openInAFrame( true );
			
			return view;
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
		if( pumpPipe != null )
			pumpPipe.pump();

		if( layoutPipeIn != null )
			layoutPipeIn.pump();
		
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
	
// Optional layout algorithm
	
	public void enableAutoLayout( Layout layoutAlgorithm )
	{
		if( optLayout == null )
		{
			optLayout = new LayoutRunner( graph, layoutAlgorithm );
			layoutPipeIn = optLayout.newLayoutPipe();
			layoutPipeIn.addAttributeSink( graph );
		}
	}
	
	public void disableAutoLayout()
	{
		if( optLayout != null )
		{
			((ThreadProxyPipe)layoutPipeIn).unregisterFromSource();
			layoutPipeIn.removeSink( graph );
			layoutPipeIn = null;
			optLayout.release();
			optLayout = null;
		}
	}
}