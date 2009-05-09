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

package org.miv.graphstream.graph.implementations;

import java.util.ArrayList;

import org.miv.graphstream.graph.Edge;
import org.miv.graphstream.graph.Graph;
import org.miv.graphstream.graph.GraphListener;
import org.miv.graphstream.graph.GraphListenerProxy;
import org.miv.graphstream.graph.Node;
import org.miv.mbox.CannotPostException;
import org.miv.mbox.MBox;
import org.miv.mbox.MBoxListener;
import org.miv.mbox.MBoxStandalone;

/**
 * Helper class that allows to listen at a graph across thread boundaries.
 * 
 * <p>
 * This class has two usages. It acts as a proxy that allows to put a listener (or several) for a
 * graph in a separate thread. It can also rebuild a graph from the events into the other thread
 * so that an exact copy is maintained. Lets call the graph witch we listen at the "input graph" and
 * the graph in the other thread the "output graph" (accordingly the input thread and the output
 * thread, etc.).
 * </p>
 * 
 * <p>
 * This class is "passive", you must check that events are available regularly by calling the
 * {@link #checkEvents()} method. This method will check if some events occurred in the input graph
 * and will modify the output graph accordingly (if any) and forward events to each registered
 * graph listener.
 * </p>
 * 
 * <p>
 * Notice that, when listening an input graph without an output graph, some events will have to
 * create "dummy" graphs, nodes and edges in order to return information. For example when you
 * receive a afterEdgeAdd(Graph,Edge) event, the graph is a DummyGraph and the edge is a DummyEdge.
 * The dummy classes contain sufficient informations (identifiers, and for edges the direction and
 * the two node names). 
 * </p>
 * 
 * <p>
 * If you passed an output graph, this one will be maintained according to each change in the input
 * graph. You should always pass true to the "replayGraph" argument of the constructors. This allows
 * to rebuild a partially built input graph into the output graph. The default constructors without
 * this parameter all replay the graph.  
 * </p>
 * 
 * <p>
 * There may be several listeners listening at this proxy. However these listener must be in the
 * same thread.
 * </p>
 * 
 * @see org.miv.graphstream.graph.GraphListener
 * @since 20061208
 */
public class GraphListenerProxyThread implements GraphListenerProxy, MBoxListener
{
	// Attributes

	/**
	 * The event sender name, usually the graph name.
	 */
	protected String from;
	
	/**
	 * The message box used to exchange messages between the two threads.
	 */
	protected MBox events;
	
	/**
	 * The layout listeners in another thread than the layout one.
	 */
	protected ArrayList<GraphListener> listeners = new ArrayList<GraphListener>();

	/**
	 * The graph to maintain according to the received events.
	 */
	protected Graph outputGraph;
	
	/**
	 * Used only to remove the listener. We ensure this is done in the Graph thread.
	 */
	protected Graph inputGraph;

	/**
	 * If true, as soon as an event is received, the listener on the input graph is removed.
	 */
	protected boolean unregisterWhenPossible = false;
	
// Constructors

	/**
	 * Listen at an input graph in a given thread and redirect all this events to GraphListeners
	 * that may be in another thread.
	 * By default, if the graph already contains some elements, they are "replayed". This means that
	 * events are sent to mimic the fact they just appeared.
	 * @param inputGraph The graph we listen at.
	 */
	public GraphListenerProxyThread( Graph inputGraph )
	{
		this( inputGraph, true );
	}

	/**
	 * Like {@link #GraphListenerProxyThread(Graph)} but allow to avoid replaying the graph.
	 * @param inputGraph The graph we listen at.
	 * @param replayGraph If false, and if the input graph already contains element they are not
	 *        replayed.
	 */
	public GraphListenerProxyThread( Graph inputGraph, boolean replayGraph )
	{
		this( inputGraph, null, replayGraph );
	}

	/**
	 * Like {@link #GraphListenerProxyThread(Graph)}, but additionally, redirect all events coming from
	 * the input graph to an output graph.
	 * 
	 * The effect of the proxy is that the output graph will be the exact copy of the input graph.
	 * By default, if the input graph already contains some elements, they are "replayed". This
	 * means that events are sent to mimic the fact they just appeared.
	 * @param inputGraph The graph we listen at.
	 * @param outputGraph The graph that will become a copy of the input graph.
	 */
	public GraphListenerProxyThread( Graph inputGraph, Graph outputGraph )
	{
		this( inputGraph, outputGraph, true );
	}
	
	/**
	 * Like {@link #GraphListenerProxyThread(Graph, Graph)}, but allow to avoid replaying the graph.
	 * @param inputGraph The graph we listen at.
	 * @param outputGraph The graph that will become a copy of the input graph.
	 * @param replayGraph If false, and if the input graph already contains element they are not
	 *        replayed.
	 */
	public GraphListenerProxyThread( Graph inputGraph, Graph outputGraph, boolean replayGraph )
	{
		this( inputGraph, outputGraph, replayGraph, new MBoxStandalone() );
	}
	
	/**
	 * Like {@link #GraphListenerProxyThread(Graph, Graph,boolean)}, but allow to share the
	 * message box with another message processor. This can be needed to share the same message
	 * stack, when message order is important.
	 * @param inputGraph The graph we listen at.
	 * @param outputGraph The graph that will become a copy of the input graph.
	 * @param replayGraph If false, and if the input graph already contains element they are not
	 *        replayed.
	 * @param sharedMBox The message box used to send and receive graph messages across the thread
	 *        boundary.
	 */
	public GraphListenerProxyThread( Graph inputGraph, Graph outputGraph, boolean replayGraph, MBox sharedMBox )
	{
		this.events      = sharedMBox;
		this.from        = inputGraph.getId();
		this.outputGraph = outputGraph;
		this.inputGraph  = inputGraph;

		if( replayGraph )
			replayGraph( inputGraph );
		
		inputGraph.addGraphListener( this );		
		((MBoxStandalone)this.events).addListener( this );
	}
	
// Commands

	/**
	 * Ask the proxy to unregister from the graph (stop receive events) as soon as possible
	 * (when the next event will occur in the graph).
	 */
	public void unregisterFromGraph()
	{
		unregisterWhenPossible = true;
	}
	
	/**
	 * Add a listener to the events of the input graph.
	 * @param listener The listener to call for each event in the input graph.
	 */
	public void addGraphListener( GraphListener listener )
	{
		listeners.add( listener );
	}
	
	/**
	 * Remove a listener. 
	 * @param listener The listener to remove.
	 */
	public void removeGraphListener( GraphListener listener )
	{
		int index = listeners.indexOf( listener );
		
		if( index >= 0 )
			listeners.remove( index );
	}
	
	/**
	 * This method must be called regularly to check if the input graph sent
	 * events. If some event occurred, the listeners will be called and the output graph
	 * given as argument will be modified (if any).
	 */
	public void checkEvents()
	{
		((MBoxStandalone)events).processMessages();
	}
	
// Utility
	
	/**
	 * Send all nodes and edges of the given graph to the distant message box, with their
	 * attributes. This method is primarily used to send the graph state if the distant mbox is
	 * connected to a graph that is already (maybe partially) built.
	 * 
	 * @param inputGraph The graph to process.
	 */
	protected void replayGraph( Graph inputGraph )
	{
		try
		{
			String gid = inputGraph.getId();
			
			// Replay all attributes of the graph.

			//Iterator<String> k = inputGraph.getAttributeKeyIterator();

			if( inputGraph.getAttributeKeySet() != null )
			{
				for( String key : inputGraph.getAttributeKeySet() )
				{
					Object val = inputGraph.getAttribute( key );
	
					events.post( from, "gaa", gid, key, val );
				}
			}

			// Replay all nodes and their attributes.

			for( Node node: inputGraph )
			{
				events.post( from, "an", gid, node.getId() );

				if( node.getAttributeKeySet() != null )
				{
					for( String key: node.getAttributeKeySet() )
					{
						Object val = node.getAttribute( key );

						events.post( from, "naa", gid, node.getId(), key, val );
					}
				}
			}

			// Replay all edges and their attributes.

			for( Edge edge: inputGraph.edgeSet() )
			{
				events.post( from, "ae", gid, edge.getId(),
						edge.getSourceNode().getId(),
						edge.getTargetNode().getId(),
						new Boolean( edge.isDirected() ) );

				if( edge.getAttributeKeySet() != null )
				{
					for( String key: edge.getAttributeKeySet() )
					{
						Object val = edge.getAttribute( key );

						events.post( from, "eaa", gid, edge.getId(), key, val );
					}
				}
			}
		}
		catch( CannotPostException e )
		{
			System.err.printf( "GraphRendererRunner: cannot post message to listeners: %s%n", e
			        .getMessage() );
		}
	}
	
// GraphListener -- Redirect events to the message box.

	public void graphAttributeAdded( String graphId, String attribute, Object value )
    {
		if( maybeUnregister() ) return;
		
		try
		{
			events.post( from, "gaa", graphId, attribute, value );
		}
		catch( CannotPostException e )
		{
			System.err.printf( "GraphRendererRunner: cannot post message to listeners: %s%n", e
			        .getMessage() );			
		}
    }

	public void graphAttributeChanged( String graphId, String attribute, Object oldValue, Object newValue )
    {
		if( maybeUnregister() ) return;
		
		try
		{
			events.post( from, "gac", graphId, attribute, oldValue, newValue );
		}
		catch( CannotPostException e )
		{
			System.err.printf( "GraphRendererRunner: cannot post message to listeners: %s%n", e
			        .getMessage() );			
		}
    }

	public void graphAttributeRemoved( String graphId, String attribute )
    {
		if( maybeUnregister() ) return;
		
		try
		{
			events.post( from, "gar", graphId, attribute );
		}
		catch( CannotPostException e )
		{
			System.err.printf( "GraphRendererRunner: cannot post message to listeners: %s%n", e
			        .getMessage() );			
		}
    }

	public void edgeAdded( String graphId, String edgeId, String fromNodeId, String toNodeId,
            boolean directed )
    {
		if( maybeUnregister() ) return;

		try
		{
			events.post( from, "ae", graphId, edgeId, fromNodeId, toNodeId, directed );
		}
		catch( CannotPostException e )
		{
			System.err.printf( "GraphRendererRunner: cannot post message to listeners: %s%n", e
			        .getMessage() );
		}
    }

	public void edgeRemoved( String graphId, String edgeId )
    {
		if( maybeUnregister() ) return;

		try
		{
			events.post( from, "de", graphId, edgeId );
		}
		catch( CannotPostException e )
		{
			System.err.printf( "GraphRendererRunner: cannot post message to listeners: %s%n", e
			        .getMessage() );
		}
    }

	public void edgeAttributeAdded( String graphId, String edgeId, String attribute, Object value )
    {
		if( maybeUnregister() ) return;
		
		try
		{
			events.post( from, "eaa", graphId, edgeId, attribute, value );
		}
		catch( CannotPostException e )
		{
		}
    }

	public void edgeAttributeChanged( String graphId, String edgeId, String attribute, Object oldValue, Object newValue )
    {
		if( maybeUnregister() ) return;
		
		try
		{
			events.post( from, "eac", graphId, edgeId, attribute, oldValue, newValue );
		}
		catch( CannotPostException e )
		{
		}
    }

	public void edgeAttributeRemoved( String graphId, String edgeId, String attribute )
    {
		if( maybeUnregister() ) return;
		
		try
		{
			events.post( from, "ear", graphId, edgeId, attribute );
		}
		catch( CannotPostException e )
		{
		}
    }

	public void nodeAdded( String graphId, String nodeId )
    {
		if( maybeUnregister() ) return;

		try
		{
			events.post( from, "an", graphId, nodeId );
		}
		catch( CannotPostException e )
		{
			System.err.printf( "GraphRendererRunner: cannot post message to listeners: %s%n", e
			        .getMessage() );
		}
    }

	public void nodeRemoved( String graphId, String nodeId )
    {
		if( maybeUnregister() ) return;

		try
		{
			events.post( from, "dn", graphId, nodeId );
		}
		catch( CannotPostException e )
		{
			System.err.printf( "GraphRendererRunner: cannot post message to listeners: %s%n", e
			        .getMessage() );
		}
    }

	public void nodeAttributeAdded( String graphId, String nodeId, String attribute, Object value )
    {
		if( maybeUnregister() ) return;

		try
		{
			events.post( from, "naa", graphId, nodeId, attribute, value );
		}
		catch( CannotPostException e )
		{
			System.err.printf( "GraphRendererRunner: cannot post message to listeners: %s%n", e
		        .getMessage() );
		}
    }

	public void nodeAttributeChanged( String graphId, String nodeId, String attribute, Object oldValue, Object newValue )
    {
		if( maybeUnregister() ) return;

		try
		{
			events.post( from, "nac", graphId, nodeId, attribute, oldValue, newValue );
		}
		catch( CannotPostException e )
		{
			System.err.printf( "GraphRendererRunner: cannot post message to listeners: %s%n", e
		        .getMessage() );
		}
    }

	public void nodeAttributeRemoved( String graphId, String nodeId, String attribute )
    {
		if( maybeUnregister() ) return;

		try
		{
			events.post( from, "nar", graphId, nodeId, attribute );
		}
		catch( CannotPostException e )
		{
			System.err.printf( "GraphRendererRunner: cannot post message to listeners: %s%n", e
		        .getMessage() );
		}
    }
	
	public void graphCleared( String graphId )
	{
		if( maybeUnregister() ) return;

		try
		{
			events.post( from, "clear", graphId );
		}
		catch( CannotPostException e )
		{
			System.err.printf( "GraphRendererRunner: cannot post message to listeners: %s%n", e
			        .getMessage() );
		}		
	}

	public void stepBegins( String graphId, double time )
    {
		if( maybeUnregister() ) return;

		try
		{
			events.post( from, "step", graphId, time );
		}
		catch( CannotPostException e )
		{
			System.err.printf( "GraphRendererRunner: cannot post message to listeners: %s%n", e
			        .getMessage() );
		}
    }

	protected boolean maybeUnregister()
	{
		if( unregisterWhenPossible )
		{
			inputGraph.removeGraphListener( this );
			return true;
		}
		
		return false;
	}

// MBoxListener -- receive redirected events and re-send them to listeners.
	
	public void processMessage( String from, Object[] data )
    {
/*		System.err.printf("Message from=%s", from );
		for( Object o: data )
			System.err.printf( " (%s=%s)", o.getClass().getName(), o );
		System.err.printf( "%n" );
*/		
		if( data.length > 0 )
		{
			if( data[0].equals( "gaa" ) )
			{
				if( data.length >= 4 && data[1] instanceof String && data[2] instanceof String )
				{
					String gid  = (String) data[1];
					String attr = (String) data[2];
					
					if( outputGraph != null )
					{
						outputGraph.addAttribute( attr, data[3] );
						gid = outputGraph.getId();
					}
					
					for( GraphListener listener: listeners )
						listener.graphAttributeAdded( gid, attr, data[3] );
				}
			}
			else if( data[0].equals( "gac" ) )
			{
				if( data.length >= 5 && data[1] instanceof String && data[2] instanceof String )
				{
					String gid  = (String) data[1];
					String attr = (String) data[2];
					
					if( outputGraph != null )
					{
						outputGraph.changeAttribute( attr, data[3] );
						gid = outputGraph.getId();
					}
					
					for( GraphListener listener: listeners )
						listener.graphAttributeChanged( gid, attr, data[3], data[4] );
				}
			}
			else if( data[0].equals( "gar" ) )
			{
				if( data.length >= 3 && data[1] instanceof String && data[2] instanceof String )
				{
					String gid  = (String) data[1];
					String attr = (String) data[2];
					
					if( outputGraph != null )
					{
						outputGraph.removeAttribute( attr );
						gid = outputGraph.getId();
					}
					
					for( GraphListener listener: listeners )
						listener.graphAttributeRemoved( gid, attr );
				}				
			}
			else if( data[0].equals( "naa" ) )
			{
				if( data.length >= 5 && data[1] instanceof String
				&&  data[2] instanceof String
				&&  data[3] instanceof String )
				{
					String gid  = (String) data[1];
					String id   = (String) data[2];
					String attr = (String) data[3];
					Node   node = null;
					
					if( outputGraph != null )
					{
						node = outputGraph.getNode( id );
						gid  = outputGraph.getId();
						
						if( node != null )
							node.addAttribute( attr, data[4] );
					}

					for( GraphListener listener: listeners )
						listener.nodeAttributeAdded( gid, id, attr, data[4] );
				}
			}
			else if( data[0].equals( "nac" ) )
			{
				if( data.length >= 6 && data[1] instanceof String
				&&  data[2] instanceof String
				&&  data[3] instanceof String )
				{
					String gid  = (String) data[1];
					String id   = (String) data[2];
					String attr = (String) data[3];
					Node   node = null;
					
					if( outputGraph != null )
					{
						node = outputGraph.getNode( id );
						gid  = outputGraph.getId();
						
						if( node != null )
							node.changeAttribute( attr, data[4] );
					}

					for( GraphListener listener: listeners )
						listener.nodeAttributeChanged( gid, id, attr, data[4], data[5] );
				}
			}
			else if( data[0].equals( "nar" ) )
			{
				if( data.length >= 4 && data[1] instanceof String
				&&  data[2] instanceof String
				&&  data[3] instanceof String )
				{
					String gid  = (String) data[1];
					String id   = (String) data[2];
					String attr = (String) data[3];
					Node   node = null;
					
					if( outputGraph != null )
					{
						node = outputGraph.getNode( id );
						gid  = outputGraph.getId();
						
						if( node != null )
							node.removeAttribute( attr );
					}

					for( GraphListener listener: listeners )
						listener.nodeAttributeRemoved( gid, id, attr );
				}
			}
			else if( data[0].equals( "eaa" ) )
			{
				if( data.length >= 5 && data[1] instanceof String
				&&  data[2] instanceof String
				&&  data[3] instanceof String )
				{
					String gid  = (String) data[1];
					String id   = (String) data[2];
					String attr = (String) data[3];
					Edge   edge = null;
					
					if( outputGraph != null )
					{
						edge = outputGraph.getEdge( id );
						gid  = outputGraph.getId();
						
						if( edge != null )
							edge.addAttribute( attr, data[4] );
					}

					for( GraphListener listener: listeners )
						listener.edgeAttributeAdded( gid, id, attr, data[4] );
				}
			}
			else if( data[0].equals( "eac" ) )
			{
				if( data.length >= 6 && data[1] instanceof String
				&&  data[2] instanceof String
				&&  data[3] instanceof String )
				{
					String gid  = (String) data[1];
					String id   = (String) data[2];
					String attr = (String) data[3];
					Edge   edge = null;
					
					if( outputGraph != null )
					{
						edge = outputGraph.getEdge( id );
						gid  = outputGraph.getId();
						
						if( edge != null )
							edge.changeAttribute( attr, data[4] );
					}

					for( GraphListener listener: listeners )
						listener.edgeAttributeChanged( gid, id, attr, data[4], data[5] );
				}
			}
			else if( data[0].equals( "ear" ) )
			{
				if( data.length >= 4 && data[1] instanceof String
				&&  data[2] instanceof String
				&&  data[3] instanceof String )
				{
					String gid  = (String) data[1];
					String id   = (String) data[2];
					String attr = (String) data[3];
					Edge   edge = null;
					
					if( outputGraph != null )
					{
						edge = outputGraph.getEdge( id );
						gid  = outputGraph.getId();
						
						if( edge != null )
							edge.removeAttribute( attr );
					}

					for( GraphListener listener: listeners )
						listener.edgeAttributeRemoved( gid, id, attr );
				}
			}
			else if( data[0].equals( "an" ) )
			{
				if( data.length >= 3 && data[1] instanceof String && data[2] instanceof String )
				{
					String gid  = (String) data[1];
					String id   = (String) data[2];
			
					if( outputGraph != null )
					{
						outputGraph.addNode( id );

						gid = outputGraph.getId();
					}

					for( GraphListener listener: listeners )
						listener.nodeAdded( gid, id );
				}
			}
			else if( data[0].equals( "ae" ) )
			{
				if( data.length >= 5 && data[1] instanceof String
						&& data[2] instanceof String && data[3] instanceof String
				        && data[4] instanceof String && data[5] instanceof Boolean )
				{
					String  gid  = (String)  data[1];
					String  id   = (String)  data[2];
					String  froM = (String)  data[3];
					String  to   = (String)  data[4];
					boolean dir  = (Boolean) data[5];
					
					if( outputGraph != null )
					{
						outputGraph.addEdge( id, froM, to, dir );

						gid = outputGraph.getId();
					}

					for( GraphListener listener: listeners )
						listener.edgeAdded( gid, id, froM, to, dir );
				}
			}
			else if( data[0].equals( "dn" ) )
			{
				if( data.length >= 2 && data[1] instanceof String && data[2] instanceof String )
				{
					String gid  = (String) data[1];
					String id   = (String) data[2];
					
					if( outputGraph != null )
					{
						outputGraph.removeNode( id );

						gid = outputGraph.getId();
					}

					for( GraphListener listener: listeners )
						listener.nodeRemoved( gid, id );
				}
			}
			else if( data[0].equals( "de" ) )
			{
				if( data.length >= 2 && data[1] instanceof String )
				{
					String gid  = (String) data[1];
					String id   = (String) data[2];
					
					if( outputGraph != null )
					{
						outputGraph.removeEdge( id );

						gid = outputGraph.getId();
					}

					for( GraphListener listener: listeners )
						listener.edgeRemoved( gid, id );
				}
			}
			else if( data[0].equals( "clear" ) )
			{
				if( data.length >= 2 && data[1] instanceof String )
				{
					String gid  = (String) data[1];
					
					if (outputGraph != null)
					{
						outputGraph.clear();
						gid = outputGraph.getId();
					}
	
					for (GraphListener listener : listeners)
						listener.graphCleared( gid );
				}
			}
			else if( data[0].equals( "step" ) )
			{
				if( data.length >= 3 && data[1] instanceof String && data[2] instanceof Number )
				{
					String gid  = (String) data[1];
					double time = ((Number)data[2]).doubleValue();
					
					if (outputGraph != null)
					{
						outputGraph.stepBegins( time );
						gid = outputGraph.getId();
					}
	
					for (GraphListener listener : listeners)
						listener.stepBegins( gid, time );
				}
			}
			else
			{
				// Ignore, it can be read by another processor if the message box is shared.
				
				// What to do ?
//
//				if( data.length > 0 && data[0] instanceof String )
//					throw new RuntimeException( "GraphListenerProxy: uncaught message from "+
//					        from+" : "+data[0]+"["+ data.length+"]" );
//				else
//					throw new RuntimeException( "GraphListenerProxy: uncaught message from "+
//					        from+" : ["+data.length+"]" );
			}
		}	    
    }
}