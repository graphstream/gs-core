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

package org.graphstream.oldUi;

import java.util.ArrayList;
import java.util.HashMap;

import org.graphstream.graph.Graph;
import org.graphstream.oldUi.graphicGraph.GraphicGraph;
import org.graphstream.oldUi.graphicGraph.stylesheet.Style;
import org.graphstream.oldUi.layout.Layout;
import org.graphstream.oldUi.layout.LayoutListener;
import org.graphstream.oldUi.layout.LayoutListenerProxy;
import org.graphstream.oldUi.layout.LayoutRunner;
import org.graphstream.oldUi.layout.LayoutRunner.LayoutRemote;
import org.miv.mbox.MBoxListener;
import org.miv.mbox.MBoxStandalone;

/**
 * Base to build graph viewer.
 * 
 * <p>
 * A graph viewer is a component that runs in its own thread and uses a GraphRenderer to draw a
 * graph. This drawing is dynamic, it changes accordingly to the events in the graph. The graph
 * viewer, additionally, is able to launch a specific thread that runs a layout algorithm.
 * </p>
 * 
 * <p>
 * This base class is an utility class that allows to connect to a graph (hereafter called the
 * input graph) and listen at it. Each event occurring in this input graph is then reflected in
 * an instance of GraphicGraph. The graphic graph is the central data chunk used by each node
 * renderer to draw the graph.
 * </p>
 * 
 * <p>
 * This class provides an utility method to launch a layout algorithm in a separate thread. This
 * layout will compute the positions of each node to produce a readable graph. If this thread is
 * launched, the graphic graph will be modified to incorporate the positions of the nodes
 * computed by the layout. This layout algorithm can be changed at any time. 
 * </p>
 * 
 * <p>
 * This class is "passive", to check the events coming from the input graph (and the optional
 * layout thread), one must call the {@link #step()} method. This method must be called
 * repeatedly, each time one wants to synchronise the graphic graph with the input graph.
 * </p>
 * 
 * <p>
 * As far as this base implementation is concerned, only the mechanic to maintain the graphic
 * graph, a graph viewer remote, and the layout thread is provided. There is no rendering, no
 * interface with a windowing
 * system at all, and the thread that runs this viewer is not created. The open and close commands
 * are not implemented (their role is to open a window or a panel and to show the graph rendering).
 * </p>
 */
public abstract class GraphViewerBase implements GraphViewer, MBoxListener
{
// Attributes
	
	/**
	 * The input graph. This one is used by other threads. This reference is used only to
	 * attach proxies to it.
	 */
	protected Graph inputGraph;
	
	/**
	 * The graphic graph.
	 */
	protected GraphicGraph graph;
	
	/**
	 * Events from the input graph.
	 */
	protected GraphListenerProxy graphProxy;
	
	/**
	 * Events from the (optional) layout.
	 */
	protected LayoutListenerProxy layoutProxy;
	
	/**
	 * The layout thread, if any.
	 */
	protected LayoutRunner layoutProcess;
	
	/**
	 * Remote that allows to send orders to the layout algorithm.
	 */
	protected LayoutRunner.LayoutRemote layoutRemote;
	
	/**
	 * Input message box for the various graph viewer remote.
	 */
	protected MBoxStandalone inbox;
	
	/**
	 * The remotes.
	 */
	protected HashMap<Integer,GraphViewerRemoteDefault> remotes = new HashMap<Integer,GraphViewerRemoteDefault>();

	/**
	 * The remotes that requested graph viewer events. The GraphViewerListeners are registered
	 * on the remotes, not on the graph viewer itself, since the part of the program that is
	 * interested in these events runs in a distinct thread.
	 */
	protected ArrayList<GraphViewerRemoteDefault> activeRemotes = new ArrayList<GraphViewerRemoteDefault>();
	
	/**
	 * Identifier allocator for remotes.
	 */
	protected int remotesIds = 0;
	
	/**
	 * Listeners for graph viewer events in the thread of the graph viewer. For example when using
	 * the default graph viewer, under Swing, the thread of the graph viewer is the Swing thread.
	 */
	protected ArrayList<GraphViewerListener> directListeners = new ArrayList<GraphViewerListener>();
	
	/**
	 * Identifiers of the layouts waiting for a stabilisation message.
	 */
	protected ArrayList<Integer> waitingForLayoutStabilisation = new ArrayList<Integer>();
	
// Constructors
	
	public GraphViewerBase()
	{
	}
	
	public GraphViewerBase( Graph inputGraph )
	{
		init( inputGraph );
	}
	
	public GraphViewerBase( Graph inputGraph, boolean direct )
	{
		init( inputGraph, direct );
	}
	
	protected void init( Graph inputGraph )
	{
		init( inputGraph, false );
	}
	
	protected void init( Graph inputGraph, boolean direct )
	{
		// Connect to the input graph.
		
		this.inputGraph = inputGraph;
		this.graph      = new GraphicGraph();
		this.inbox      = new MBoxStandalone( this );
		
		if( direct )
		     this.graphProxy = new GraphListenerProxyCopy( inputGraph, graph, true );
		else this.graphProxy = new GraphListenerProxyThread( inputGraph, graph, true, this.inbox );
//		else this.graphProxy = new GraphListenerProxyThread( inputGraph, graph, true );
	}
	
// Access

	public LayoutRunner.LayoutRemote getLayoutRemote()
	{
		return layoutRemote;
	}
	
	public LayoutListenerProxy getLayoutListenerProxy( LayoutListener listener )
	{
		return layoutProcess.getListenerProxy( listener ); 
	}
	
	public LayoutListenerProxy getLayoutListenerProxy( Graph graph )
	{
		return layoutProcess.getListenerProxy( graph );
	}
	
	public GraphViewerRemote newViewerRemote()
	{
//		if( inbox == null )
//			inbox = new MBoxStandalone( this );
	
		remotesIds++;
		
		GraphViewerRemoteDefault remote = new GraphViewerRemoteDefault( remotesIds, inbox, layoutRemote, layoutProcess );
		
		remotes.put( remotesIds, remote );
		
		return remote;
	}
	
// Commands
	
	public void addGraphViewerListener( GraphViewerListener listener )
	{
		directListeners.add( listener );
	}
	
	public void removeGraphViewerListener( GraphViewerListener listener )
	{
		int index = directListeners.indexOf( listener );
		
		if( index >= 0 )
			directListeners.remove( index );
	}
	
	/**
	 * This method must be called to synchronise the graphic graph with the input graph (and
	 * optionally to synchronise with an eventual layout thread).
	 */
	public void step()
	{
		graphProxy.checkEvents();

		// TODO, check it, but normally not neede as the inbox is shared with the graph proxy.
		if( inbox != null )
			inbox.processMessages();
		
		if( layoutProxy != null )
			layoutProxy.checkEvents();

		if(  layoutRemote != null )
			layoutRemote.pumpEvents();
		
		// Send a "coucou" to all remotes waiting for us.
		
		if( waitingForLayoutStabilisation.size() > 0 )
		{
			if( layoutProxy.getCompletion() == 1 )
			{
				for( Integer i: waitingForLayoutStabilisation )
				{
					GraphViewerRemoteDefault remote = remotes.get( i );
					
					if( remote != null )
						remote.inbox.post( "GV", "STABILIZED!!" );
				}
				
				waitingForLayoutStabilisation.clear();
			}
		}
	}
	
	/**
	 * Set or change the layout algorithm running in a distinct thread. If null is given as argument
	 * the current layout is removed.
	 * @param layoutAlgorithm The layout algorithm to use.
	 */
	public void setLayout( Layout layoutAlgorithm )
	{
		removeLayout();
		
		if( layoutAlgorithm != null )
		{
			layoutProcess = new LayoutRunner( inputGraph, layoutAlgorithm );
			layoutProxy   = layoutProcess.getListenerProxy( graph );
			layoutRemote  = layoutProcess.newRemote();
		
			layoutProcess.start();
		}
	}
	
	public void setLayout( String layoutAlgorithmClassName )
	{
		throw new RuntimeException( "TODO!!! That's a shame, flame the developers !" );
		// TODO!!
	}
	
	/**
	 * Terminate and remove a layout process running in its thread.
	 */
	public void removeLayout()
	{
		if( layoutProcess != null )
		{
			layoutRemote.stop();
            
            layoutProcess = null;
            layoutProxy   = null;
            layoutRemote  = null;
		}
	}
	
// GraphViewer interface

	public abstract void open( Graph graph );

	public abstract void open( Graph graph, boolean autoLayout );

	public abstract void open( Graph graph, Layout layout );

	/**
	 * Call this method to send an event to all graph viewer listeners that the node 'id' has been
	 * selected.
	 * @param id The node identifier.
	 * @param selected The node state.
	 */
	public void sendNodeSelectedEvent( String id, boolean selected )
	{
		for( GraphViewerRemoteDefault remote: activeRemotes )
			remote.inbox.post( "GV", "NSel", id, selected );
		
		for( GraphViewerListener listener: directListeners )
			listener.nodeSelected( id, selected );
	}

	/**
	 * Call this method to send an event to all graph viewer listeners that the sprite 'id' has been
	 * selected.
	 * @param id The sprite identifier.
	 * @param selected The node state.
	 */
	public void sendSpriteSelectedEvent( String id, boolean selected )
	{
		for( GraphViewerRemoteDefault remote: activeRemotes )
			remote.inbox.post( "GV", "SSel", id, selected );
		
		for( GraphViewerListener listener: directListeners )
			listener.spriteSelected( id, selected );
	}
	
	/**
	 * Call this method to send an event to all graph viewer listeners that the node 'id' has moved.
	 * @param id The node identifier.
	 * @param x The new node x.
	 * @param y The new node y.
	 * @param z The new node z.
	 */
	public void sendNodeMovedEvent( String id, float x, float y, float z )
	{
		for( GraphViewerRemoteDefault remote: activeRemotes )
			remote.inbox.post( "GV", "NMov", id, x, y, z );
		
		for( GraphViewerListener listener: directListeners )
			listener.nodeMoved( id, x, y, z );
	}
	
	/**
	 * Call this method to send an event to all graph viewer listeners that the sprite 'id' has moved.
	 * @param id The sprite identifier.
	 * @param x The new node x.
	 * @param y The new node y.
	 * @param z The new node z.
	 */
	public void sendSpriteMovedEvent( String id, float x, float y, float z )
	{
		for( GraphViewerRemoteDefault remote: activeRemotes )
			remote.inbox.post( "GV", "SMov", id, x, y, z );
		
		for( GraphViewerListener listener: directListeners )
			listener.spriteMoved( id, x, y, z );
	}
	
	/**
	 * Call this method to send an event to all graph viewer listeners that the viewer background
	 * has been clicked.
	 * @param x The mouse click x position.
	 * @param y The mouse click y position.
	 * @param button The button number.
	 * @param pressed The button state.
	 */
	public void sendBackgroundClickedEvent( float x, float y, int button, boolean pressed )
	{
		for( GraphViewerRemoteDefault remote: activeRemotes )
			remote.inbox.post( "GV", "BC", x, y, button, pressed );
		
		for( GraphViewerListener listener: directListeners )
			listener.backgroundClicked( x, y, button, pressed );
	}

// Sprite API

	public Sprite addSprite( String id )
	{
		return new DirectSprite( id, this );
	}

	public void addSpriteNoInstance( String id )
	{
		graph.getSpriteManager().addSprite( id );
	}
	
	public void removeSprite( String id )
	{
		graph.getSpriteManager().removeSprite( id );		
	}

	public void attachSpriteToNode( String id, String nodeId )
	{
		graph.getSpriteManager().attachSpriteToNode( id, nodeId );		
	}

	public void attachSpriteToEdge( String id, String edgeId )
	{
		graph.getSpriteManager().attachSpriteToEdge( id, edgeId );		
	}

	public void detachSprite( String id )
	{
		graph.getSpriteManager().detachSprite( id );		
	}

	public void positionSprite( String id, float percent )
	{
		graph.getSpriteManager().positionSprite( id, percent );		
	}

	public void positionSprite( String id, float x, float y, float z, Style.Units units )
	{
		graph.getSpriteManager().positionSprite( id, x, y, z, units );		
	}

	public void positionSprite( String id, float x, float y, float z )
	{
		graph.getSpriteManager().positionSprite( id, x, y, z );		
	}
	
	public void addSpriteAttribute( String id, String attribute, Object value )
	{
		graph.getSpriteManager().addSpriteAttribute( id, attribute, value );		
	}

	public void removeSpriteAttribute( String id, String attribute )
	{
		graph.getSpriteManager().removeSpriteAttribute( id, attribute );		
	}
	
// Things that must be implemented by descendants.
	
	/**
	 * Close the viewer, definitively.
	 */
	public void close()
	{
		graphProxy.unregisterFromGraph();
		removeLayout();
	}
	
	/**
	 * Take a screen shot of the viewer display in the given format.
	 * @param fileName The output file name.
	 * @param type The output format. 
	 */
	protected abstract void screenShot( String fileName, ScreenshotType type );
	
	/**
	 * Set the rendering quality.
	 * @param quality A quality setting going from 0 (low quality) to 4 (high quality).
	 */
	public abstract void setQuality( int quality );
	
	/**
	 * Show or hide the current time. The time is defined by steps,
	 * see {@link org.graphstream.graph.Graph#stepBegins(double)}.
	 * @param on If true, the time is visible.
	 */
	public abstract void setStepsVisible( boolean on );
	
// MBoxListener and GraphViewerRemote default implementation
	
	/**
	 * Process the messages sent by the viewer remote.
	 */
	public void processMessage( String from, Object data[] )
	{
		if( data.length > 0 )
		{
			if( data[0] instanceof String )
			{
				if( data.length == 1 && data[0].equals( "X" ) )
				{
					close();
				}
				else if( data.length == 3 && data[0].equals( "SC" ) && data[1] instanceof String && data[2] instanceof ScreenshotType )
				{
					String fileName = (String) data[1];
					ScreenshotType type = (ScreenshotType) data[2];
					
					screenShot( fileName, type );
				}
				else if( data.length == 2 && data[0].equals( "SQ" ) && data[1] instanceof Number )
				{
					int quality = ((Number)data[1]).intValue();
					
					setQuality( quality );
				}
				else if( data.length == 2 && data[0].equals( "SV" ) && data[1] instanceof Boolean )
				{
					setStepsVisible( (Boolean)data[1] );
				}
				// Sprite API
				else if( data.length == 2 && data[0].equals( "XAS" ) && data[1] instanceof String )
				{
					// Add sprite.
					addSprite( (String)data[1] );
				}
				else if( data.length == 2 && data[0].equals( "XRS" ) && data[1] instanceof String )
				{
					// Remove sprite.
					removeSprite( (String)data[1] );
				}
				else if( data.length == 3 && data[0].equals( "XASTN" ) && data[1] instanceof String && data[2] instanceof String )
				{
					// Attach sprite to node.
					attachSpriteToNode( (String)data[1], (String)data[2] );
				}
				else if( data.length == 3 && data[0].equals( "XASTE" ) && data[1] instanceof String && data[2] instanceof String )
				{
					// Attach sprite to edge.
					attachSpriteToEdge( (String)data[1], (String)data[2] );
				}
				else if( data.length == 2 && data[0].equals( "XDS" ) && data[1] instanceof String )
				{
					// Detach sprite.
					detachSprite( (String)data[1] );
				}
				else if( data.length == 3 && data[0].equals( "XPS1" ) && data[1] instanceof String && data[2] instanceof Number )
				{
					// Position sprite 1.
					float value = ((Number)data[2]).floatValue();
					positionSprite( (String)data[1], value );
				}
				else if( data.length == 6 && data[0].equals( "XPS2" ) && data[1] instanceof String && data[2] instanceof Number && data[3] instanceof Number && data[4] instanceof Number && data[5] instanceof Style.Units )
				{
					// Position sprite 2.
					float x = ((Number)data[2]).floatValue();
					float y = ((Number)data[3]).floatValue();
					float z = ((Number)data[4]).floatValue();
					positionSprite( (String)data[1], x, y, z, (Style.Units) data[5] );
				}
				else if( data.length == 5 && data[0].equals( "XPS3" ) && data[1] instanceof String && data[2] instanceof Number && data[3] instanceof Number && data[4] instanceof Number )
				{
					// Position sprite 3.
					float x = ((Number)data[2]).floatValue();
					float y = ((Number)data[3]).floatValue();
					float z = ((Number)data[4]).floatValue();
					positionSprite( (String)data[1], x, y, z );
				}
				else if( data.length == 4 && data[0].equals( "XASA" ) && data[1] instanceof String && data[2] instanceof String )
				{
					// Add sprite attribute.
					addSpriteAttribute( (String)data[1], (String)data[2], data[3] );
				}
				else if( data.length == 3 && data[0].equals( "XRSA" ) && data[1] instanceof String && data[2] instanceof String )
				{
					// Remove sprite attribute.
					removeSpriteAttribute( (String)data[1], (String)data[2] );
				}
				//
				//
				// Remotes
				//
				//
				else if( data.length == 2 && data[0].equals( "WFS" ) && data[1] instanceof Integer )
				{
					waitingForLayoutStabilisation.add( (Integer)data[1] );
				}
				else if( data.length == 2 && data[0].equals( "RACTIVE" ) && data[1] instanceof Integer )
				{
					GraphViewerRemoteDefault remote = remotes.get( data[1] );
					
					if( remote != null )
					{
						activeRemotes.add( remote );
					}
					else
					{
						throw new RuntimeException( "inconsistency : activating a remote that is not registered !!!" );
					}
				}
				else if( data.length == 2 && data[0].equals( "RINACTIVE" ) && data[1] instanceof Integer )
				{
					GraphViewerRemoteDefault remote = remotes.get( data[1] );
					
					if( remote != null )
					{
						int index = activeRemotes.indexOf( remote );
						assert( index >= 0 );
						activeRemotes.remove( index );
					}
					else
					{
						throw new RuntimeException( "inconsistency : de-activating a remote that is not registered !!!" );
					}					
				}
				else if( data.length == 2 && data[0].equals( "RKILL" ) && data[1] instanceof Integer )
				{
					GraphViewerRemoteDefault remote = remotes.remove( data[1] );
					
					if( remote == null )
					{
						throw new RuntimeException( "inconsistency : destroying a remote that is not registered !!!" );
					}
					else
					{
						int index = activeRemotes.indexOf( remote );
						
						if( index >= 0 )
							activeRemotes.remove( index );
					}
				}
				else if( data.length == 1 && data[0].equals( "rmLayout" ) )
				{
					removeLayout();
				}
				else if( data.length == 2 && data[0].equals( "setLayout" ) && data[1] instanceof String )
				{
					setLayout( (String)data[1] );
				}
				else
				{
//					System.err.printf( "GraphViewerBase.processMessage() : unknown %s->%s [size = %d]%n", from, data[0], data.length );
//					for( int i=0; i<data.length; ++i )
//						System.err.printf( "    argument %d : %s = %s%n", i, data[i].getClass().getName(), data[i] );
				}
			}
			else
			{
//				System.err.printf( "GraphViewerBase.processMessage() : unknown %s->%s [size = %d]%n", from, data[0], data.length );
//				for( int i=0; i<data.length; ++i )
//					System.err.printf( "    argument %d : %s = %s%n", i, data[i].getClass().getName(), data[i] );
			}
		}
		else
		{
//			System.err.printf( "GraphViewerBase.processMessage() : unknown empty message from %s%n", from );
		}
	}
	
	/**
	 * Default implementation of a graph viewer remote.
	 * 
	 * This remote allows to send commands to the graph viewer when in another thread.
	 */
	protected static class GraphViewerRemoteDefault implements GraphViewerRemote, MBoxListener
	{
		/**
		 * Remote identifier.
		 */
		protected int id;
		
		/**
		 * The input box for this remote. The graph viewer sends data here. 
		 */
		protected MBoxStandalone inbox;
		
		/**
		 * The message box where to send events.
		 */
		protected MBoxStandalone outbox;
		
		/**
		 * Optional layout remote.
		 */
		protected LayoutRemote layoutRemote;
		
		/**
		 * Optional layout process.
		 */
		protected LayoutRunner layoutProcess;

		/**
		 * Listeners for graph viewer events.
		 */
		protected ArrayList<GraphViewerListener> listeners = new ArrayList<GraphViewerListener>();
		
		/**
		 * Used to wait for the layout process.
		 */
		protected boolean waitingForStabilisation = false;
		
		/**
		 * Set of layout proxies.
		 */
		protected ArrayList<LayoutListenerProxy> layoutListenerProxies;
		
		/**
		 * New graph viewer remote that send all its events to the given message box.
		 * @param graphViewerInbox The message box where to send events.
		 */
		protected GraphViewerRemoteDefault( int id, MBoxStandalone graphViewerInbox, LayoutRemote layoutRemote, LayoutRunner layoutProcess )
		{
			this.id            = id;
			this.outbox        = graphViewerInbox;
			this.layoutRemote  = layoutRemote;
			this.layoutProcess = layoutProcess;
			this.inbox         = new MBoxStandalone( this );
		}
		
		@Override
		protected void finalize()
		{
			outbox.post( "VR", "RKILL", id );
		}
		
	// The GraphViewerRemote interface.

		public void close()
        {
			outbox.post( "VR", "X" );
        }

		public void screenShot( String fileName, ScreenshotType type )
        {
			outbox.post( "VR", "SC", fileName, type );
        }
		
		public void setQuality( int value )
		{
			outbox.post( "VR", "SQ", value );
		}
		
		public void setStepsVisible( boolean on )
		{
			outbox.post( "VR", "SV", on );
		}
		
		public void waitForLayoutStabilisation( long timeOutMs )
		{
			if( layoutRemote != null )
			{
				outbox.post( "VR", "WFS", id );
				waitingForStabilisation = true;
				
				long timeStart = System.currentTimeMillis();
				long t = timeStart;
				
				while( waitingForStabilisation && (t-timeStart) < timeOutMs )
				{
					pumpEvents();
					
					try{ Thread.sleep( 100 ); } catch( Exception e ) {}
					
					t = System.currentTimeMillis();
				}
			}
		}
		
// Sprites API

		public Sprite addSprite( String id )
        {
			return new RemoteSprite( id, this );
        }
		
		public void addSpriteNoInstance( String id )
		{
			outbox.post( "VR", "XAS", id );
		}

		public void removeSprite( String id )
        {
			outbox.post( "VR", "XRS", id );
        }

		public void attachSpriteToEdge( String id, String edgeId )
        {
			outbox.post( "VR", "XASTE", id, edgeId );
        }

		public void attachSpriteToNode( String id, String nodeId )
        {
			outbox.post( "VR" , "XASTN", id, nodeId );
        }

		public void detachSprite( String id )
        {
			outbox.post( "VR", "XDS", id );
        }

		public void positionSprite( String id, float percent )
        {
			outbox.post( "VR", "XPS1", id, percent );
        }

		public void positionSprite( String id, float x, float y, float z, Style.Units units )
        {
			outbox.post( "VR", "XPS2", id, x, y, z, units );
        }

		public void positionSprite( String id, float x, float y, float z )
        {
			outbox.post( "VR", "XPS3", id, x, y, z );
        }

		public void addSpriteAttribute( String id, String attribute, Object value )
        {
			outbox.post( "VR", "XASA", id, attribute, value );
        }

		public void removeSpriteAttribute( String id, String attribute )
        {
			outbox.post( "VR", "XRSA", id, attribute );
        }

		public boolean viewerSupportsSprites()
        {
	        return true;
        }

		public LayoutRemote getLayoutRemote()
        {
	        return layoutRemote;
        }

		public void addViewerListener( GraphViewerListener listener )
		{
			if( listeners.isEmpty() )
				outbox.post( "VR", "RACTIVE", id );
			
			listeners.add( listener );
        }

		public void removeViewerListener( GraphViewerListener listener )
        {
			int index = listeners.indexOf( listener );
			
			if( index >= 0 )
			{
				listeners.remove( index );
			
				if( listeners.isEmpty() )
					outbox.post( "VR", "RINACTIVE", id );
			}
        }
		
		public void copyBackLayoutCoordinates( Graph graph )
		{
			if( layoutProcess != null )
			{
				if( layoutListenerProxies == null )
					layoutListenerProxies = new ArrayList<LayoutListenerProxy>();
				
				LayoutListenerProxy proxy = layoutProcess.getListenerProxy( graph );
				layoutListenerProxies.add( proxy );
			}
		}
		
		public void pumpEvents()
		{
			inbox.processMessages();
			
			if( layoutListenerProxies != null )
			{
				for( LayoutListenerProxy proxy: layoutListenerProxies )
					proxy.checkEvents();
			}
		}

		public void processMessage( String from, Object[] data )
        {
			if( data.length > 0 )
			{
				if( data[0] instanceof String )
				{
					if( data.length == 3 && data[0].equals( "NSel" ) && data[1] instanceof String && data[2] instanceof Boolean )
					{
						for( GraphViewerListener listener: listeners )
							listener.nodeSelected( (String)data[1], (Boolean)data[2] );
					}
					else if( data.length == 3 && data[0].equals( "SSel" )
					     &&  data[1] instanceof String && data[2] instanceof Boolean )
					{
						for( GraphViewerListener listener: listeners )
							listener.spriteSelected( (String)data[1], (Boolean)data[2] );
					}
					else if( data.length == 5 && data[0].equals( "NMov" )
						 &&  data[1] instanceof String && data[2] instanceof Number
						 &&  data[3] instanceof Number && data[4] instanceof Number )
					{
						float x = ((Number)data[2]).floatValue();
						float y = ((Number)data[3]).floatValue();
						float z = ((Number)data[4]).floatValue();
						
						for( GraphViewerListener listener: listeners )
							listener.nodeMoved( (String)data[1], x, y, z );
					}
					else if( data.length == 5 && data[0].equals( "SMov" )
						 &&  data[1] instanceof String && data[2] instanceof Number
						 &&  data[3] instanceof Number && data[4] instanceof Number )
					{
						float x = ((Number)data[2]).floatValue();
						float y = ((Number)data[3]).floatValue();
						float z = ((Number)data[4]).floatValue();
						
						for( GraphViewerListener listener: listeners )
							listener.spriteMoved( (String)data[1], x, y, z );
					}
					else if( data.length == 5 && data[0].equals( "BC" )
						 &&  data[1] instanceof Number && data[2] instanceof Number
						 &&  data[3] instanceof Number && data[4] instanceof Boolean )
					{
						// Background clicked.
						
						float x = ((Number)data[1]).floatValue();
						float y = ((Number)data[2]).floatValue();
						int   b = ((Number)data[3]).intValue();
					
						for( GraphViewerListener listener: listeners )
							listener.backgroundClicked( x, y, b, ((Boolean)data[4]) );
					}
					else if( data.length == 1 && data[0].equals( "STABILIZED!!" ) )
					{
						waitingForStabilisation = false;
					}
				}
				else
				{
					throw new RuntimeException( "What's the fuck ?" );
				}
			}
        }

		public boolean is3D()
        {
	        return false;
        }

		public void removeLayout()
        {
			outbox.post( "VR", "rmLayout" );
        }

		public void setLayout( String layoutClassName )
        {
			outbox.post( "VR", "setLayout", layoutClassName );
        }
	}
}