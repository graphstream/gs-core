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

package org.miv.graphstream.io2.thread;

import java.util.ArrayList;

import org.miv.graphstream.graph.Edge;
import org.miv.graphstream.graph.Graph;
import org.miv.graphstream.graph.GraphAttributesListener;
import org.miv.graphstream.graph.GraphElementsListener;
import org.miv.graphstream.graph.GraphListener;
import org.miv.graphstream.graph.Node;
import org.miv.graphstream.io2.Filter;
import org.miv.graphstream.io2.Input;
import org.miv.graphstream.io2.InputBase;
import org.miv.graphstream.io2.ProxyFilter;
import org.miv.graphstream.io2.SynchronizableInput;
import org.miv.mbox.CannotPostException;
import org.miv.mbox.MBox;
import org.miv.mbox.MBoxListener;
import org.miv.mbox.MBoxStandalone;
import org.miv.mbox.MBoxBase.Message;

/**
 * Filter that allows to pass graph events between two threads without explicit synchronisation.
 * 
 * <p>
 * This filter allows to register it as an output for some source of events in a source thread
 * (hereafter called the input thread) and to register listening outputs in a destination
 * thread (hereafter called the output thread).
 * </p>
 * 
 * <pre>
 *                       |
 *   Input ----> ThreadProxyFilter ----> Outputs
 *  Thread 1             |              Thread 2
 *                       |
 * </pre>
 * 
 * <p>
 * In other words, this class allows to listen in a (output) thread graph events that are produced
 * in another (input) thread without any explicit synchronisation on the source of events.
 * </p>
 * 
 * <p>
 * The only restriction is that the output thread must regularly call the {@link #checkEvents()}
 * method to dispatch events coming from the source to all outputs registered (see the
 * explanation in {@link org.miv.graphstream.io2.ProxyFilter}).
 * </p>
 * 
 * <p>
 * You can register any kind of input as source of event, but if the input is a graph, then
 * you can choose to "replay" all the content of the graph so that at the other end of the filter,
 * all outputs receive the complete content of the graph. This is the default behaviour if this
 * filter is constructed with a graph as input.
 * </p>
 */
public class ThreadProxyFilter extends InputBase implements ProxyFilter, MBoxListener
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
	 * Used only to remove the listener. We ensure this is done in the Input thread.
	 */
	protected Input input;

	/**
	 * Signals that this proxy must be removed from the source input.
	 */
	protected boolean unregisterWhenPossible = false;

	protected ArrayList<InputSynchro> synchro = new ArrayList<InputSynchro>();
	
	protected static class InputSynchro
	{
		public Input input;
		public GraphAttributesListener attrListener;
		public GraphElementsListener eltsListener;
		
		public InputSynchro( Input input, GraphAttributesListener attrListener )
		{
			this.input        = input;
			this.attrListener = attrListener;
			this.eltsListener = null;
		}
		
		public InputSynchro( Input input, GraphElementsListener eltsListener )
		{
			this.input        = input;
			this.attrListener = null;
			this.eltsListener = eltsListener;			
		}
		
		public InputSynchro( Input input, GraphListener listener )
		{
			this.input        = input;
			this.attrListener = listener;
			this.eltsListener = listener;
		}
		
		public void disable()
		{
//System.err.printf( "    remove %s from %s%n", attrListener, input );
		
			if( input instanceof SynchronizableInput )
			{
				SynchronizableInput si = (SynchronizableInput) input;
				
				if( attrListener != null )
					si.muteSource( attrListener );
				
				if( eltsListener != null )
					si.muteSource( eltsListener );
			}
			else
			{
				if( attrListener != null )
					input.removeGraphAttributesListener( attrListener );
				if( eltsListener != null )
					input.removeGraphElementsListener( eltsListener );
			}
		}
		
		public void enable()
		{
			if( ! ( input instanceof SynchronizableInput ) )
			{
				if( attrListener != null )
					input.addGraphAttributesListener( attrListener );
				if( eltsListener != null )
					input.addGraphElementsListener( eltsListener );
			}
			else
			{
			//	((SynchronizableInput)input).unmute();
			}
		}
	}
	
// Constructors

	/**
	 * Listen at an input in a given thread and redirect all events to GraphListeners
	 * that may be in another thread.
	 * @param input The source of graph events we listen at.
	 */
	public ThreadProxyFilter( Input input )
	{
		this( input, null );
	}
	
	/**
	 * Like {@link #ThreadProxyFilter(Input)}, but allow to share the
	 * message box with another message processor. This can be needed to share the same message
	 * stack, when message order is important.
	 * @param input The source of events we listen at.
	 * @param sharedMBox The message box used to send and receive graph messages across the thread
	 *        boundary.
	 */
	public ThreadProxyFilter( Input input, MBox sharedMBox )
	{
		this.events      = sharedMBox;
		this.from        = "<in>";
		this.input       = input;

		input.addGraphListener( this );		
		((MBoxStandalone)this.events).addListener( this );		
	}
	
	/**
	 * Listen at an input graph in a given thread and redirect all events to GraphListeners
	 * that may be in another thread.
	 * By default, if the graph already contains some elements, they are "replayed". This means that
	 * events are sent to mimic the fact they just appeared.
	 * @param inputGraph The graph we listen at.
	 */
	public ThreadProxyFilter( Graph inputGraph )
	{
		this( inputGraph, true );
	}

	/**
	 * Like {@link #ThreadProxyFilter(Graph)} but allow to avoid replaying the graph.
	 * @param inputGraph The graph we listen at.
	 * @param replayGraph If false, and if the input graph already contains element they are not
	 *        replayed.
	 */
	public ThreadProxyFilter( Graph inputGraph, boolean replayGraph )
	{
		this( inputGraph, replayGraph, new MBoxStandalone() );
	}

	/**
	 * Like {@link #ThreadProxyFilter(Graph,boolean)}, but allow to share the
	 * message box with another message processor. This can be needed to share the same message
	 * stack, when message order is important.
	 * @param inputGraph The graph we listen at.
	 * @param replayGraph If false, and if the input graph already contains element they are not
	 *        replayed.
	 * @param sharedMBox The message box used to send and receive graph messages across the thread
	 *        boundary.
	 */
	public ThreadProxyFilter( Graph inputGraph, boolean replayGraph, MBox sharedMBox )
	{
		this.events      = sharedMBox;
		this.from        = inputGraph.getId();
		this.input       = inputGraph;

		if( replayGraph )
			replayGraph( inputGraph );
		
		input.addGraphListener( this );		
		((MBoxStandalone)this.events).addListener( this );
	}
	
// Access
	
	@Override
	public String toString()
	{
		String dest = "nil";
		
		if( attrListeners.size() > 0 )
			dest = attrListeners.get( 0 ).toString();
		
		return String.format( "thread-proxy(from %s to %s)", from, dest );
	}
	
// Command

	/**
	 * Allow filter/filter synchronisation through two thread proxy filters in the two opposite
	 * directions.
	 * @param other The other thread proxy filter going in the reverse direction.
	 * @param onThis one of the outputs of this proxy thread filter, source of events for the
	 * "other" thread proxy filter.
	 */
	public void synchronizeWith( ThreadProxyFilter other, Filter onThis )
	{
		synchro.add( new InputSynchro( onThis, other ) );
	}
	
	/**
	 * Ask the proxy to unregister from the event input source (stop receive events) as soon as
	 * possible (when the next event will occur in the graph).
	 */
	public void unregisterFromGraph()
	{
		unregisterWhenPossible = true;
	}
	
	/**
	 * This method must be called regularly in the output thread to check if the input source sent
	 * events. If some event occurred, the listeners will be called.
	 */
	public void checkEvents()
	{
		ArrayList<Message> messages = ((MBoxStandalone)events).popPendingMessages();
//System.err.printf( "from %s : %d messages%n", from, messages.size() );

		for( Message message: messages )
		{
			for( InputSynchro sync: synchro ) sync.disable();
			processMessage( message.from, message.data );
			for( InputSynchro sync: synchro ) sync.enable();
		}
//System.err.printf( "end-from %s%n", from );
			
		//((MBoxStandalone)events).processMessages();
	}

// Command
	
	/**
	 * Set of events sent via the message box.
	 */
	protected static enum GraphEvents
	{
		ADD_NODE, DEL_NODE, ADD_EDGE, DEL_EDGE, STEP, CLEARED,
		ADD_GRAPH_ATTR, CHG_GRAPH_ATTR, DEL_GRAPH_ATTR,
		ADD_NODE_ATTR,  CHG_NODE_ATTR,  DEL_NODE_ATTR,
		ADD_EDGE_ATTR,  CHG_EDGE_ATTR,  DEL_EDGE_ATTR
	};
	
	protected void replayGraph( Graph graph )
	{
		try
		{
			String graphId = graph.getId();
			
			// Replay all graph attributes.
			
			if( graph.getAttributeKeySet() != null )
				for( String key: graph.getAttributeKeySet() )
					events.post( from, GraphEvents.ADD_GRAPH_ATTR, graphId, key, graph.getAttribute( key ) );
			
			// Replay all nodes and their attributes.

			for( Node node: graph )
			{
				events.post( from, GraphEvents.ADD_NODE, graphId, node.getId() );

				if( node.getAttributeKeySet() != null )
					for( String key: node.getAttributeKeySet() )
						events.post( from, GraphEvents.ADD_NODE_ATTR, graphId, node.getId(), key,
							node.getAttribute( key ) );
			}

			// Replay all edges and their attributes.

			for( Edge edge: graph.edgeSet() )
			{
				events.post( from, GraphEvents.ADD_EDGE, graphId, edge.getId(),
						edge.getSourceNode().getId(),
						edge.getTargetNode().getId(), edge.isDirected() );

				if( edge.getAttributeKeySet() != null )
					for( String key: edge.getAttributeKeySet() )
						events.post( from, GraphEvents.ADD_EDGE_ATTR, graphId, edge.getId(), key,
						       edge.getAttribute( key ) );
			}
		}
		catch( CannotPostException e )
		{
			System.err.printf( "GraphRendererRunner: cannot post message to listeners: %s%n",
					e.getMessage() );
		}		
	}

	protected boolean maybeUnregister()
	{
		if( unregisterWhenPossible )
		{
			input.removeGraphListener( this );
			return true;
		}
		
		return false;
	}

// Command
	
	public void edgeAttributeAdded( String graphId, String edgeId, String attribute, Object value )
    {
		if( maybeUnregister() ) return;

		try
        {
	        events.post( from, GraphEvents.ADD_EDGE_ATTR, graphId, edgeId, attribute, value );
        }
        catch( CannotPostException e )
        {
	        e.printStackTrace();
        }
    }

	public void edgeAttributeChanged( String graphId, String edgeId, String attribute,
            Object oldValue, Object newValue )
    {
		if( maybeUnregister() ) return;

		try
        {
	        events.post( from, GraphEvents.CHG_EDGE_ATTR, graphId, edgeId, attribute, oldValue, newValue );
        }
        catch( CannotPostException e )
        {
	        e.printStackTrace();
        }
    }

	public void edgeAttributeRemoved( String graphId, String edgeId, String attribute )
    {
		if( maybeUnregister() ) return;

		try
        {
	        events.post( from, GraphEvents.DEL_EDGE_ATTR, graphId, edgeId, attribute );
        }
        catch( CannotPostException e )
        {
	        e.printStackTrace();
        }
    }

	public void graphAttributeAdded( String graphId, String attribute, Object value )
    {
		if( maybeUnregister() ) return;

		try
        {
	        events.post( from, GraphEvents.ADD_GRAPH_ATTR, graphId, attribute, value );
        }
        catch( CannotPostException e )
        {
	        e.printStackTrace();
        }
    }

	public void graphAttributeChanged( String graphId, String attribute, Object oldValue,
            Object newValue )
    {
		if( maybeUnregister() ) return;

		try
        {
	        events.post( from, GraphEvents.CHG_GRAPH_ATTR, graphId, attribute, oldValue, newValue );
        }
        catch( CannotPostException e )
        {
	        e.printStackTrace();
        }
    }

	public void graphAttributeRemoved( String graphId, String attribute )
    {
		if( maybeUnregister() ) return;

		try
        {
	        events.post( from, GraphEvents.DEL_GRAPH_ATTR, graphId, attribute );
        }
        catch( CannotPostException e )
        {
	        e.printStackTrace();
        }
    }

	public void nodeAttributeAdded( String graphId, String nodeId, String attribute, Object value )
    {
		if( maybeUnregister() ) return;

		try
        {
	        events.post( from, GraphEvents.ADD_NODE_ATTR, graphId, nodeId, attribute, value );
        }
        catch( CannotPostException e )
        {
	        e.printStackTrace();
        }
    }

	public void nodeAttributeChanged( String graphId, String nodeId, String attribute,
            Object oldValue, Object newValue )
    {
		if( maybeUnregister() ) return;

		try
        {
	        events.post( from, GraphEvents.CHG_NODE_ATTR, graphId, nodeId, attribute, oldValue, newValue );
        }
        catch( CannotPostException e )
        {
	        e.printStackTrace();
        }
    }

	public void nodeAttributeRemoved( String graphId, String nodeId, String attribute )
    {
		if( maybeUnregister() ) return;

		try
        {
	        events.post( from, GraphEvents.DEL_NODE_ATTR, graphId, nodeId, attribute );
        }
        catch( CannotPostException e )
        {
	        e.printStackTrace();
        }
    }

	public void edgeAdded( String graphId, String edgeId, String fromNodeId, String toNodeId,
            boolean directed )
    {
		if( maybeUnregister() ) return;

		try
        {
	        events.post( from, GraphEvents.ADD_EDGE, graphId, edgeId, fromNodeId, toNodeId, directed );
        }
        catch( CannotPostException e )
        {
	        e.printStackTrace();
        }
    }

	public void edgeRemoved( String graphId, String edgeId )
    {
		if( maybeUnregister() ) return;

		try
        {
	        events.post( from, GraphEvents.DEL_EDGE, graphId, edgeId );
        }
        catch( CannotPostException e )
        {
	        e.printStackTrace();
        }
    }

	public void graphCleared( String graphId )
    {
		if( maybeUnregister() ) return;

		try
        {
	        events.post( from, GraphEvents.CLEARED, graphId );
        }
        catch( CannotPostException e )
        {
	        e.printStackTrace();
        }
    }

	public void nodeAdded( String graphId, String nodeId )
    {
		if( maybeUnregister() ) return;

		try
        {
	        events.post( from, GraphEvents.ADD_NODE, graphId, nodeId );
        }
        catch( CannotPostException e )
        {
	        e.printStackTrace();
        }
    }

	public void nodeRemoved( String graphId, String nodeId )
    {
		if( maybeUnregister() ) return;

		try
        {
	        events.post( from, GraphEvents.DEL_NODE, graphId, nodeId );
        }
        catch( CannotPostException e )
        {
	        e.printStackTrace();
        }
    }

	public void stepBegins( String graphId, double time )
    {
		if( maybeUnregister() ) return;

		try
        {
	        events.post( from, GraphEvents.STEP, graphId, time );
        }
        catch( CannotPostException e )
        {
	        e.printStackTrace();
        }
    }

// MBoxListener
	
	public void processMessage( String from, Object[] data )
	{
//System.err.printf( "    msg(%s, %s, %s)%n", from, data[0], data[2] );
		
		if( data[0].equals( GraphEvents.ADD_NODE ) )
		{
			String graphId = (String) data[1];
			String nodeId  = (String) data[2];
			
			sendNodeAdded( graphId, nodeId );
		}
		else if( data[0].equals( GraphEvents.DEL_NODE ) )
		{
			String graphId = (String) data[1];
			String nodeId  = (String) data[2];
			
			sendNodeRemoved( graphId, nodeId );
		}
		else if( data[0].equals( GraphEvents.ADD_EDGE ) )
		{
			String  graphId  = (String) data[1];
			String  edgeId   = (String) data[2];
			String  fromId   = (String) data[3];
			String  toId     = (String) data[4];
			boolean directed = (Boolean) data[5];
			
			sendEdgeAdded( graphId, edgeId, fromId, toId, directed );
		}
		else if( data[0].equals( GraphEvents.DEL_EDGE ) )
		{
			String graphId = (String) data[1];
			String edgeId  = (String) data[2];
			
			sendEdgeRemoved( graphId, edgeId );
		}
		else if( data[0].equals( GraphEvents.STEP ) )
		{
			String graphId = (String) data[1];
			double time    = (Double) data[2];
			
			sendStepBegins( graphId, time );
		}
		else if( data[0].equals( GraphEvents.ADD_GRAPH_ATTR ) )
		{
			String graphId   = (String) data[1];
			String attribute = (String) data[2];
			Object value     = data[3];
			
			sendGraphAttributeAdded( graphId, attribute, value );
		}
		else if( data[0].equals( GraphEvents.CHG_GRAPH_ATTR ) )
		{
			String graphId   = (String) data[1];
			String attribute = (String) data[2];
			Object oldValue  = data[3];
			Object newValue  = data[4];

			sendGraphAttributeChanged( graphId, attribute, oldValue, newValue );			
		}
		else if( data[0].equals( GraphEvents.DEL_GRAPH_ATTR ) )
		{
			String graphId   = (String) data[1];
			String attribute = (String) data[2];
			
			sendGraphAttributeRemoved( graphId, attribute );						
		}
		else if( data[0].equals( GraphEvents.ADD_EDGE_ATTR ) )
		{
			String graphId   = (String) data[1];
			String edgeId    = (String) data[2];
			String attribute = (String) data[3];
			Object value     = data[4];
			
			sendEdgeAttributeAdded( graphId, edgeId, attribute, value );
		}
		else if( data[0].equals( GraphEvents.CHG_EDGE_ATTR ) )
		{
			String graphId   = (String) data[1];
			String edgeId    = (String) data[2];
			String attribute = (String) data[3];
			Object oldValue  = data[4];
			Object newValue  = data[5];
			
			sendEdgeAttributeChanged( graphId, edgeId, attribute, oldValue, newValue );
		}
		else if( data[0].equals( GraphEvents.DEL_EDGE_ATTR ) )
		{
			String graphId   = (String) data[1];
			String edgeId    = (String) data[2];
			String attribute = (String) data[3];
			
			sendEdgeAttributeRemoved( graphId, edgeId, attribute );			
		}
		else if( data[0].equals( GraphEvents.ADD_NODE_ATTR ) )
		{
			String graphId   = (String) data[1];
			String nodeId    = (String) data[2];
			String attribute = (String) data[3];
			Object value     = data[4];
			
			sendNodeAttributeAdded( graphId, nodeId, attribute, value );			
		}
		else if( data[0].equals( GraphEvents.CHG_NODE_ATTR ) )
		{
			String graphId   = (String) data[1];
			String nodeId    = (String) data[2];
			String attribute = (String) data[3];
			Object oldValue  = data[4];
			Object newValue  = data[5];
			
			sendNodeAttributeChanged( graphId, nodeId, attribute, oldValue, newValue );			
		}
		else if( data[0].equals( GraphEvents.DEL_NODE_ATTR ) )
		{
			String graphId   = (String) data[1];
			String nodeId    = (String) data[2];
			String attribute = (String) data[3];
			
			sendNodeAttributeRemoved( graphId, nodeId, attribute );						
		}
		else if( data[0].equals( GraphEvents.CLEARED ) )
		{
			String graphId = (String) data[1];
			
			sendGraphCleared( graphId );
		}
		else
		{
			System.err.printf( "ThreadProxyFilter : Unknown message %s !!%n", data[0] );
		}
    }
}