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

import java.util.Iterator;

import org.miv.graphstream.graph.Edge;
import org.miv.graphstream.graph.Graph;
import org.miv.graphstream.graph.Node;
import org.miv.graphstream.graph.implementations.GraphListenerProxyThread.InputProtocol;
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
	 * The graph to maintain according to the received events.
	 */
	protected Graph outputGraph;
	
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
	 * Like {@link #ThreadProxyFilter(Input)}, but additionally, redirect all events coming from
	 * the input to an output graph.
	 * 
	 * The effect of the proxy is that the output graph will be the exact copy of the input.
	 * By default.
	 * @param input The source of events we listen at.
	 * @param outputGraph The graph that will become a copy of the input graph.
	 */
	public ThreadProxyFilter( Input input, Graph outputGraph )
	{
		this( input, outputGraph, new MBoxStandalone() );
	}
	
	/**
	 * Like {@link #ThreadProxyFilter(Input,Graph)}, but allow to share the
	 * message box with another message processor. This can be needed to share the same message
	 * stack, when message order is important.
	 * @param input The source of events we listen at.
	 * @param outputGraph The graph that will become a copy of the input.
	 * @param sharedMBox The message box used to send and receive graph messages across the thread
	 *        boundary.
	 */
	public ThreadProxyFilter( Input input, Graph outputGraph, MBox sharedMBox )
	{
		this.events      = sharedMBox;
		this.from        = "<in>";
		this.outputGraph = outputGraph;
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
		this( inputGraph, null, replayGraph );
	}

	/**
	 * Like {@link #ThreadProxyFilter(Graph)}, but additionally, redirect all events coming from
	 * the input graph to an output graph.
	 * 
	 * The effect of the proxy is that the output graph will be the exact copy of the input graph.
	 * By default, if the input graph already contains some elements, they are "replayed". This
	 * means that events are sent to mimic the fact they just appeared.
	 * @param inputGraph The graph we listen at.
	 * @param outputGraph The graph that will become a copy of the input graph.
	 */
	public ThreadProxyFilter( Graph inputGraph, Graph outputGraph )
	{
		this( inputGraph, outputGraph, true );
	}
	
	/**
	 * Like {@link #ThreadProxyFilter(Graph, Graph)}, but allow to avoid replaying the graph.
	 * @param inputGraph The graph we listen at.
	 * @param outputGraph The graph that will become a copy of the input graph.
	 * @param replayGraph If false, and if the input graph already contains element they are not
	 *        replayed.
	 */
	public ThreadProxyFilter( Graph inputGraph, Graph outputGraph, boolean replayGraph )
	{
		this( inputGraph, outputGraph, replayGraph, new MBoxStandalone() );
	}
	
	/**
	 * Like {@link #ThreadProxyFilter(Graph, Graph,boolean)}, but allow to share the
	 * message box with another message processor. This can be needed to share the same message
	 * stack, when message order is important.
	 * @param inputGraph The graph we listen at.
	 * @param outputGraph The graph that will become a copy of the input graph.
	 * @param replayGraph If false, and if the input graph already contains element they are not
	 *        replayed.
	 * @param sharedMBox The message box used to send and receive graph messages across the thread
	 *        boundary.
	 */
	public ThreadProxyFilter( Graph inputGraph, Graph outputGraph, boolean replayGraph, MBox sharedMBox )
	{
		this.events      = sharedMBox;
		this.from        = inputGraph.getId();
		this.outputGraph = outputGraph;
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
		ADD_NODE, DEL_NODE, ADD_EDGE, DEL_EDGE,
		ADD_GRAPH_ATTR, CHG_GRAPH_ATTR, DEL_GRAPH_ATTR,
		ADD_NODE_ATTR,  CHG_NODE_ATTR,  DEL_NODE_ATTR,
		ADD_EDGE_ATTR,  CHG_EDGE_ATTR,  DEL_EDGE_ATTR
	};
	
	protected void replayGraph( Graph graph )
	{
		try
		{
			for( String key: graph.getAttributeKeySet() )
			{
				Object val = graph.getAttribute( key );

				events.post( from, GraphEvents.ADD_GRAPH_ATTR, key, val );
			}
			
			// Replay all nodes and their attributes.

			for( Node node: graph )
			{
				events.post( from, GraphEvents.ADD_NODE, node.getId() );

				for( String key: node.getAttributeKeySet() )
				{
					Object val = node.getAttribute( key );

					events.post( from, GraphEvents.ADD_NODE_ATTR, node.getId(), key,
							val );
				}
			}

			// Replay all edges and their attributes.

			for( Edge edge: graph.edgeSet() )
			{
				events.post( from, GraphEvents.ADD_EDGE, edge.getId(), edge
				        .getSourceNode().getId(), edge.getTargetNode().getId(), new Boolean(
				        edge.isDirected() ) );

				for( String key: edge.getAttributeKeySet() )
				{
					Object val = edge.getAttribute( key );

					events.post( from, GraphEvents.ADD_EDGE_ATTR, edge.getId(), key,
						       val );
				}
			}
		}
		catch( CannotPostException e )
		{
			System.err.printf( "GraphRendererRunner: cannot post message to listeners: %s%n", e
			        .getMessage() );
		}		
	}

// Command
	
	public void edgeAttributeAdded( String graphId, String edgeId, String attribute, Object value )
    {
    }

	public void edgeAttributeChanged( String graphId, String edgeId, String attribute,
            Object oldValue, Object newValue )
    {
    }

	public void edgeAttributeRemoved( String graphId, String edgeId, String attribute )
    {
    }

	public void graphAttributeAdded( String graphId, String attribute, Object value )
    {
    }

	public void graphAttributeChanged( String graphId, String attribute, Object oldValue,
            Object newValue )
    {
    }

	public void graphAttributeRemoved( String graphId, String attribute )
    {
    }

	public void nodeAttributeAdded( String graphId, String nodeId, String attribute, Object value )
    {
    }

	public void nodeAttributeChanged( String graphId, String nodeId, String attribute,
            Object oldValue, Object newValue )
    {
    }

	public void nodeAttributeRemoved( String graphId, String nodeId, String attribute )
    {
    }

	public void edgeAdded( String graphId, String edgeId, String fromNodeId, String toNodeId,
            boolean directed )
    {
    }

	public void edgeRemoved( String graphId, String edgeId )
    {
    }

	public void graphCleared( String graphId )
    {
    }

	public void nodeAdded( String graphId, String nodeId )
    {
    }

	public void nodeRemoved( String graphId, String nodeId )
    {
    }

	public void stepBegins( String graphId, double time )
    {
    }

// MBoxListener
	
	public void processMessage( String from, Object[] data )
    {
		if( data[0].equals( GraphEvents.ADD_NODE ) )
		{
			
		}
    }	
}