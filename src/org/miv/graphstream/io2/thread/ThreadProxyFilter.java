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

import org.miv.graphstream.graph.Edge;
import org.miv.graphstream.graph.Graph;
import org.miv.graphstream.graph.Node;
import org.miv.graphstream.io2.Filter;
import org.miv.graphstream.io2.Input;
import org.miv.graphstream.io2.InputBase;
import org.miv.mbox.CannotPostException;
import org.miv.mbox.MBox;
import org.miv.mbox.MBoxListener;
import org.miv.mbox.MBoxStandalone;

/**
 * Filter that allows to pass graph events between two threads without explicit synchronisation.
 */
public class ThreadProxyFilter extends InputBase implements Filter, MBoxListener
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
			
			for( String key: graph.getAttributeKeySet() )
				events.post( from, GraphEvents.ADD_GRAPH_ATTR, graphId, key, graph.getAttribute( key ) );
			
			// Replay all nodes and their attributes.

			for( Node node: graph )
			{
				events.post( from, GraphEvents.ADD_NODE, graphId, node.getId() );

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
			
		}
		else if( data[0].equals( GraphEvents.CHG_GRAPH_ATTR ) )
		{
			
		}
		else if( data[0].equals( GraphEvents.DEL_GRAPH_ATTR ) )
		{
			
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
			
		}
		else if( data[0].equals( GraphEvents.CHG_NODE_ATTR ) )
		{
			
		}
		else if( data[0].equals( GraphEvents.DEL_NODE_ATTR ) )
		{
			
		}
		else if( data[0].equals( GraphEvents.CLEARED ) )
		{
			String graphId = (String) data[1];
			
			sendGraphCleared( graphId );
		}
    }
}