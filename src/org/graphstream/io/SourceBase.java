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

package org.graphstream.io;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

import org.graphstream.graph.GraphAttributesListener;
import org.graphstream.graph.GraphElementsListener;
import org.graphstream.graph.GraphEvent;
import org.graphstream.graph.GraphEvent.GraphAction;
import org.graphstream.graph.GraphEvent.ElementType;
import org.graphstream.graph.GraphEvent.ElementEvent;
import org.graphstream.graph.GraphEvent.NodeEvent;
import org.graphstream.graph.GraphEvent.EdgeEvent;
import org.graphstream.graph.GraphEvent.AttributeEvent;
import org.graphstream.graph.GraphEvent.GraphStepEvent;
import org.graphstream.graph.GraphListener;

/**
 * Base implementation of an input that provide basic listener handling.
 * 
 * <p>
 * This implementation can register a set of graph listeners (or separate sets of
 * attributes or elements listeners) and provides protected methods to easily broadcast events
 * to all the listeners (beginning with "send").
 * </p>
 * 
 * <p>
 * Each time you want to produce an event toward all registered listeners, you call
 * one of the "send*" methods with correct parameters. The parameters of the "send*" methods
 * maps to the usual GraphStream events.
 * </p>
 * 
 * <p>
 * This class is "reentrant". This means that if a send*() method is called during the 
 * execution of another or the same send*() method, the event is deferred until the first
 * send*() method is finished. This avoid recursive loops if a listener modifies the input
 * during event handling.
 * </p>
 */
public abstract class SourceBase
	implements Source
{
// Attribute
	
	/**
	 * Set of graph attributes listeners.
	 */
	protected ArrayList<GraphAttributesListener> attrListeners = new ArrayList<GraphAttributesListener>();
	
	/**
	 * Set of graph elements listeners.
	 */
	protected ArrayList<GraphElementsListener> eltsListeners = new ArrayList<GraphElementsListener>();

	/**
	 * A queue that allow the management of events (nodes/edge add/delete/change) in the right order. 
	 */
	protected LinkedList<GraphEvent> eventQueue = new LinkedList<GraphEvent>();
	
	/**
	 * A boolean that indicates whether or not an GraphListener event is being sent during another one. 
	 */
	protected AtomicBoolean eventProcessing = new AtomicBoolean(false);
	
	/**
	 * List of listeners to remove if the {@link #removeGraphListener(GraphListener)} is called
	 * inside from the listener. This can happen !! We create this list on demand.
	 */
	protected ArrayList<Object> listenersToRemove;
	
	volatile long timeOrEventId = 0;
	
// Construction
	
// Access

	public Iterable<GraphAttributesListener> graphAttributesListeners()
	{
		return attrListeners;
	}
	
	public Iterable<GraphElementsListener> graphElementsListeners()
	{
		return eltsListeners;
	}
	
// Command
	
	public void addGraphListener( GraphListener listener )
	{
		attrListeners.add( listener );
		eltsListeners.add( listener );		
	}
	
	public void addGraphAttributesListener( GraphAttributesListener listener )
	{
		attrListeners.add( listener );		
	}
	
	public void addGraphElementsListener( GraphElementsListener listener )
	{
		eltsListeners.add( listener );		
	}

	public void clearListeners()
	{
		eltsListeners.clear();
		attrListeners.clear();
	}
	
	public void removeGraphListener( GraphListener listener )
	{
		if( eventProcessing.get() )
		{
			// We cannot remove the listener while processing events !!!
			removeListenerLater( listener );
		}
		else
		{
			attrListeners.remove( listener );
			eltsListeners.remove( listener );
		}
	}
	
	public void removeGraphAttributesListener( GraphAttributesListener listener )
	{
		if( eventProcessing.get() )
		{
			// We cannot remove the listener while processing events !!!
			removeListenerLater( listener );
		}
		else
		{
			attrListeners.remove( listener );
		}		
	}
	
	public void removeGraphElementsListener( GraphElementsListener listener )
	{
		if( eventProcessing.get() )
		{
			// We cannot remove the listener while processing events !!!
			removeListenerLater( listener );
		}
		else
		{
			eltsListeners.remove( listener );
		}
	}
	
	protected void removeListenerLater( Object listener )
	{
		if( listenersToRemove == null )
			listenersToRemove = new ArrayList<Object>();
		
		listenersToRemove.add( listener );	
	}
	
	protected void checkListenersToRemove()
	{
		if( listenersToRemove != null && listenersToRemove.size() > 0 )
		{
			for( Object listener: listenersToRemove )
			{
				if( listener instanceof GraphListener )
					removeGraphListener( (GraphListener) listener );
				else if( listener instanceof GraphAttributesListener )
					removeGraphAttributesListener( (GraphAttributesListener) listener );
				else if( listener instanceof GraphElementsListener )
					removeGraphElementsListener( (GraphElementsListener) listener );
			}

			listenersToRemove.clear();
			listenersToRemove = null;
		}
	}
	/**
	 * Send a "graph cleared" event to all element listeners.
	 * @param sourceId The source identifier.
	 */
	public void sendGraphCleared( String sourceId )
	{
		appendEvent(GraphEvent.createGraphClearedEvent(sourceId, timeOrEventId++));
	}

	/**
	 * Send a "step begins" event to all element listeners.
	 * @param sourceId The graph identifier.
	 * @param time The step time stamp.
	 */
	public void sendStepBegins( String sourceId, double time )
	{
		appendEvent(GraphEvent.createGraphStepEvent(sourceId, timeOrEventId++, time));
	}

	/**
	 * Send a "node added" event to all element listeners.
	 * @param sourceId The source identifier.
	 * @param nodeId The node identifier.
	 */
	public void sendNodeAdded( String sourceId, String nodeId )
	{
		appendEvent(GraphEvent.createNodeAddedEvent(sourceId, timeOrEventId++, nodeId));
	}

	/**
	 * Send a "node removed" event to all element listeners.
	 * @param sourceId The graph identifier.
	 * @param nodeId The node identifier.
	 */
	public void sendNodeRemoved( String sourceId, String nodeId )
	{
		appendEvent(GraphEvent
				.createNodeRemovedEvent(sourceId, timeOrEventId++, nodeId));
	}
	/**
	 * Send an "edge added" event to all element listeners.
	 * @param sourceId The source identifier.
	 * @param edgeId The edge identifier.
	 * @param fromNodeId The edge start node.
	 * @param toNodeId The edge end node.
	 * @param directed Is the edge directed?.
	 */
	public void sendEdgeAdded( String sourceId, String edgeId, String fromNodeId, String toNodeId, boolean directed )
	{
		appendEvent(GraphEvent.createEdgeAddedEvent(sourceId, timeOrEventId++, edgeId,
				fromNodeId, toNodeId, directed));
	}

	/**
	 * Send a "edge removed" event to all element listeners.
	 * @param sourceId The source identifier.
	 * @param edgeId The edge identifier.
	 */
	public void sendEdgeRemoved( String sourceId, String edgeId )
	{
		appendEvent(GraphEvent
				.createEdgeRemovedEvent(sourceId, timeOrEventId++, edgeId));
	}

	/**
	 * Send a "edge attribute added" event to all attribute listeners.
	 * @param sourceId The source identifier.
	 * @param edgeId The edge identifier.
	 * @param attribute The attribute name.
	 * @param value The attribute value.
	 */
	protected void sendEdgeAttributeAdded( String sourceId, String edgeId, String attribute, Object value )
    {
		appendEvent(GraphEvent.createEdgeAttributeAddedEvent(sourceId, timeOrEventId++,
				edgeId, attribute, value));
    }

	/**
	 * Send a "edge attribute changed" event to all attribute listeners.
	 * @param sourceId The source identifier.
	 * @param edgeId The edge identifier.
	 * @param attribute The attribute name.
	 * @param oldValue The old attribute value.
	 * @param newValue The new attribute value.
	 */
	protected void sendEdgeAttributeChanged( String sourceId, String edgeId, String attribute,
            Object oldValue, Object newValue )
    {
		appendEvent(GraphEvent.createEdgeAttributeChangedEvent(sourceId,
				timeOrEventId++, edgeId, attribute, oldValue, newValue));
    }

	/**
	 * Send a "edge attribute removed" event to all attribute listeners.
	 * @param sourceId The source identifier.
	 * @param edgeId The edge identifier.
	 * @param attribute The attribute name.
	 */
	protected void sendEdgeAttributeRemoved( String sourceId, String edgeId, String attribute )
    {
		appendEvent(GraphEvent.createEdgeAttributeRemovedEvent(sourceId,
				timeOrEventId++, edgeId, attribute));
    }

	/**
	 * Send a "graph attribute added" event to all attribute listeners.
	 * @param sourceId The source identifier.
	 * @param attribute The attribute name.
	 * @param value The attribute value.
	 */
	protected void sendGraphAttributeAdded( String sourceId, String attribute, Object value )
    {
		appendEvent(GraphEvent.createGraphAttributeAddedEvent(sourceId,
				timeOrEventId++, attribute, value));
    }

	/**
	 * Send a "graph attribute changed" event to all attribute listeners.
	 * @param sourceId The source identifier.
	 * @param attribute The attribute name.
	 * @param oldValue The attribute old value.
	 * @param newValue The attribute new value.
	 */
	protected void sendGraphAttributeChanged( String sourceId, String attribute, Object oldValue,
            Object newValue )
    {
		appendEvent(GraphEvent.createGraphAttributeChangedEvent(sourceId,
				timeOrEventId++, attribute, oldValue, newValue));
    }

	/**
	 * Send a "graph attribute removed" event to all attribute listeners.
	 * @param sourceId The source identifier.
	 * @param attribute The attribute name.
	 */
	protected void sendGraphAttributeRemoved( String sourceId, String attribute )
    {
		appendEvent(GraphEvent.createGraphAttributeRemovedEvent(sourceId,
				timeOrEventId++, attribute));
    }

	/**
	 * Send a "node attribute added" event to all attribute listeners.
	 * @param sourceId The source identifier.
	 * @param nodeId The node identifier.
	 * @param attribute The attribute name.
	 * @param value The attribute value.
	 */
	protected void sendNodeAttributeAdded( String sourceId, String nodeId, String attribute, Object value )
    {
		appendEvent(GraphEvent.createNodeAttributeAddedEvent(sourceId, timeOrEventId++,
				nodeId, attribute, value));
    }

	/**
	 * Send a "node attribute changed" event to all attribute listeners.
	 * @param sourceId The source identifier.
	 * @param nodeId The node identifier.
	 * @param attribute The attribute name.
	 * @param oldValue The attribute old value.
	 * @param newValue The attribute new value.
	 */
	protected void sendNodeAttributeChanged( String sourceId, String nodeId, String attribute,
            Object oldValue, Object newValue )
    {
		appendEvent(GraphEvent.createNodeAttributeChangedEvent(sourceId,
				timeOrEventId++, nodeId, attribute, oldValue, newValue));
    }

	/**
	 * Send a "node attribute removed" event to all attribute listeners.
	 * @param sourceId The source identifier.
	 * @param nodeId The node identifier.
	 * @param attribute The attribute name.
	 */
	protected void sendNodeAttributeRemoved( String sourceId, String nodeId, String attribute )
    {
		appendEvent(GraphEvent.createNodeAttributeRemovedEvent(sourceId,
				timeOrEventId++, nodeId, attribute));
    }
// Deferred event management

	protected void appendEvent( GraphEvent e )
	{
		if( eventProcessing.compareAndSet(false,true) )//! eventProcessing )
		{
			//eventProcessing = true;
			manageEvents();
			manageEvent(e);
			//eventProcessing = false;
			eventProcessing.set(false);
			checkListenersToRemove();
		}
		else
		{
			eventQueue.add(e);
		}
	}
	
	/**
	 * If in "event processing mode", ensure all pending events are processed.
	 */
	protected void manageEvents()
	{
		if( eventProcessing.get() )
		{
			while( ! eventQueue.isEmpty() )
				manageEvent( eventQueue.remove() );
		}
	}
	
	/**
	 * Private method that manages the events stored in the {@link #eventQueue}.
	 * These event where created while being invoked from another event
	 * invocation.
	 * @param event
	 */
	private void manageEvent( GraphEvent event )
	{
		if( event.getGraphAction() == GraphAction.attributeAdded )
		{
			AttributeEvent ae = (AttributeEvent) event;
			
			if( ae.getElementType() == ElementType.node )
			{
				for( GraphAttributesListener l: attrListeners )
					l.nodeAttributeAdded(ae);
			}
			else if( ae.getElementType() == ElementType.edge )
			{
				for( GraphAttributesListener l: attrListeners )
					l.edgeAttributeAdded(ae);
			}
			else if( ae.getElementType() == ElementType.graph )
			{
				for( GraphAttributesListener l: attrListeners )
					l.graphAttributeAdded(ae);
			}
		}
		else if( event.getGraphAction() == GraphAction.attributeChanged )
		{
			AttributeEvent ae = (AttributeEvent) event;
			
			if( ae.getElementType() == ElementType.node )
			{
				for( GraphAttributesListener l: attrListeners )
					l.nodeAttributeChanged(ae);
			}
			else if( ae.getElementType() == ElementType.edge )
			{
				for( GraphAttributesListener l: attrListeners )
					l.edgeAttributeChanged(ae);
			}
			else if( ae.getElementType() == ElementType.graph )
			{
				for( GraphAttributesListener l: attrListeners )
					l.graphAttributeChanged(ae);
			}
		}
		else if( event.getGraphAction() == GraphAction.attributeRemoved )
		{
			AttributeEvent ae = (AttributeEvent) event;
			
			if( ae.getElementType() == ElementType.node )
			{
				for( GraphAttributesListener l: attrListeners )
					l.nodeAttributeRemoved(ae);
			}
			else if( ae.getElementType() == ElementType.edge )
			{
				for( GraphAttributesListener l: attrListeners )
					l.edgeAttributeRemoved(ae);
			}
			else if( ae.getElementType() == ElementType.graph )
			{
				for( GraphAttributesListener l: attrListeners )
					l.graphAttributeRemoved(ae);
			}
		}
		else if( event.getGraphAction() == GraphAction.elementAdded )
		{
			ElementEvent ee = (ElementEvent) event;
			
			if( ee.getElementType() == ElementType.node )
			{
				for( GraphElementsListener l: eltsListeners )
					l.nodeAdded( (NodeEvent) event );
			}
			else if( ee.getElementType() == ElementType.edge )
			{
				for( GraphElementsListener l: eltsListeners )
					l.edgeAdded( (EdgeEvent) event );
			}
		}
		else if( event.getGraphAction() == GraphAction.elementRemoved )
		{
			ElementEvent ee = (ElementEvent) event;
			
			if( ee.getElementType() == ElementType.node )
			{
				for( GraphElementsListener l: eltsListeners )
					l.nodeRemoved( (NodeEvent) event );
			}
			else if( ee.getElementType() == ElementType.edge )
			{
				for( GraphElementsListener l: eltsListeners )
					l.edgeRemoved( (EdgeEvent) event );
			}
		}
		else if( event.getGraphAction() == GraphAction.graphCleared )
		{
			for( GraphElementsListener l: eltsListeners )
				l.graphCleared(event);
		}
		else if( event.getGraphAction() == GraphAction.stepBegins )
		{
			for( GraphElementsListener l: eltsListeners )
				l.stepBegins((GraphStepEvent)event);
		}
	}
}