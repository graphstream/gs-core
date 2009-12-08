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

import org.graphstream.graph.GraphAttributesListener;
import org.graphstream.graph.GraphElementsListener;
import org.graphstream.graph.GraphListener;
import org.graphstream.graph.implementations.AbstractElement.AttributeChangeEvent;

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
public abstract class SourceBase implements Source
{
// Attribute
	
	public enum ElementType { NODE, EDGE, GRAPH };

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
	protected boolean eventProcessing = false;
	
	/**
	 * List of listeners to remove if the {@link #removeGraphListener(GraphListener)} is called
	 * inside from the listener. This can happen !! We create this list on demand.
	 */
	protected ArrayList<Object> listenersToRemove;
	
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
		if( eventProcessing )
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
		if( eventProcessing )
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
		if( eventProcessing )
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
		for( GraphElementsListener listener: eltsListeners )
				listener.graphCleared( sourceId );
	}

	/**
	 * Send a "step begins" event to all element listeners.
	 * @param sourceId The graph identifier.
	 * @param time The step time stamp.
	 */
	public void sendStepBegins( String sourceId, double time )
	{
		for( GraphElementsListener l : eltsListeners )
				l.stepBegins( sourceId, time );
	}

	/**
	 * Send a "node added" event to all element listeners.
	 * @param sourceId The source identifier.
	 * @param nodeId The node identifier.
	 */
	public void sendNodeAdded( String sourceId, String nodeId )
	{
		if( ! eventProcessing )
		{
			eventProcessing = true;
			manageEvents();

			for( GraphElementsListener l: eltsListeners )
					l.nodeAdded( sourceId, nodeId );
		
			manageEvents();
			eventProcessing = false;
			checkListenersToRemove();
		}
		else 
		{
			eventQueue.add( new AfterNodeAddEvent( sourceId, nodeId ) );
		}
	}

	/**
	 * Send a "node removed" event to all element listeners.
	 * @param sourceId The graph identifier.
	 * @param nodeId The node identifier.
	 */
	public void sendNodeRemoved( String sourceId, String nodeId )
	{
		if( ! eventProcessing )
		{
			eventProcessing = true;
			manageEvents();

			for( GraphElementsListener l: eltsListeners )
					l.nodeRemoved( sourceId, nodeId );

			manageEvents();
			eventProcessing = false;
			checkListenersToRemove();
		}
		else 
		{
			eventQueue.add( new BeforeNodeRemoveEvent( sourceId, nodeId ) );
		}
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
		if( ! eventProcessing )
		{
			eventProcessing = true;
			manageEvents();

			for( GraphElementsListener l: eltsListeners )
					l.edgeAdded( sourceId, edgeId, fromNodeId, toNodeId, directed );

			manageEvents();
			eventProcessing = false;
			checkListenersToRemove();
		}
		else 
		{
//			printPosition( "AddEdge in EventProc" );
			eventQueue.add( new AfterEdgeAddEvent( sourceId, edgeId, fromNodeId, toNodeId, directed ) );
		}
	}

	/**
	 * Send a "edge removed" event to all element listeners.
	 * @param sourceId The source identifier.
	 * @param edgeId The edge identifier.
	 */
	public void sendEdgeRemoved( String sourceId, String edgeId )
	{
		if( ! eventProcessing )
		{
			eventProcessing = true;
			manageEvents();

			for( GraphElementsListener l: eltsListeners )
					l.edgeRemoved( sourceId, edgeId );

			manageEvents();
			eventProcessing = false;
			checkListenersToRemove();
		}
		else
		{
//			printPosition( "DelEdge in EventProc" );
			eventQueue.add( new BeforeEdgeRemoveEvent( sourceId, edgeId ) );
		}
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
		sendAttributeChangedEvent( sourceId, edgeId, ElementType.EDGE, attribute,
				AttributeChangeEvent.ADD, null, value );
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
		sendAttributeChangedEvent( sourceId, edgeId, ElementType.EDGE, attribute,
				AttributeChangeEvent.CHANGE, oldValue, newValue );
    }

	/**
	 * Send a "edge attribute removed" event to all attribute listeners.
	 * @param sourceId The source identifier.
	 * @param edgeId The edge identifier.
	 * @param attribute The attribute name.
	 */
	protected void sendEdgeAttributeRemoved( String sourceId, String edgeId, String attribute )
    {
		sendAttributeChangedEvent( sourceId, edgeId, ElementType.EDGE, attribute,
				AttributeChangeEvent.REMOVE, null, null );
    }

	/**
	 * Send a "graph attribute added" event to all attribute listeners.
	 * @param sourceId The source identifier.
	 * @param attribute The attribute name.
	 * @param value The attribute value.
	 */
	protected void sendGraphAttributeAdded( String sourceId, String attribute, Object value )
    {
		sendAttributeChangedEvent( sourceId, null, ElementType.GRAPH, attribute,
				AttributeChangeEvent.ADD, null, value );
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
		sendAttributeChangedEvent( sourceId, null, ElementType.GRAPH, attribute,
				AttributeChangeEvent.CHANGE, oldValue, newValue );
    }

	/**
	 * Send a "graph attribute removed" event to all attribute listeners.
	 * @param sourceId The source identifier.
	 * @param attribute The attribute name.
	 */
	protected void sendGraphAttributeRemoved( String sourceId, String attribute )
    {
		sendAttributeChangedEvent( sourceId, null, ElementType.GRAPH, attribute,
				AttributeChangeEvent.REMOVE, null, null );
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
		sendAttributeChangedEvent( sourceId, nodeId, ElementType.NODE, attribute,
				AttributeChangeEvent.ADD, null, value );
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
		sendAttributeChangedEvent( sourceId, nodeId, ElementType.NODE, attribute,
				AttributeChangeEvent.CHANGE, oldValue, newValue );
    }

	/**
	 * Send a "node attribute removed" event to all attribute listeners.
	 * @param sourceId The source identifier.
	 * @param nodeId The node identifier.
	 * @param attribute The attribute name.
	 */
	protected void sendNodeAttributeRemoved( String sourceId, String nodeId, String attribute )
    {
		sendAttributeChangedEvent( sourceId, nodeId, ElementType.NODE, attribute,
				AttributeChangeEvent.REMOVE, null, null );
    }

	/**
	 * Send a add/change/remove attribute event on an element. This method is a generic way of
	 * notifying of an attribute change and is equivalent to individual send*Attribute*() methods.
	 * @param sourceId The source identifier.
	 * @param eltId The changed element identifier.
	 * @param eltType The changed element type.
	 * @param attribute The changed attribute.
	 * @param event The add/change/remove action.
	 * @param oldValue The old attribute value (null if the attribute is removed or added).
	 * @param newValue The new attribute value (null if removed).
	 */
	public void sendAttributeChangedEvent( String sourceId, String eltId, ElementType eltType, String attribute, AttributeChangeEvent event, Object oldValue, Object newValue )
	{
		if( ! eventProcessing )
		{
			eventProcessing = true;
			manageEvents();

			if( event == AttributeChangeEvent.ADD )
			{
				if( eltType == ElementType.NODE )
				{
					for( GraphAttributesListener l: attrListeners )
							l.nodeAttributeAdded( sourceId, eltId, attribute, newValue );
				}
				else if( eltType == ElementType.EDGE )
				{
					for( GraphAttributesListener l: attrListeners )
							l.edgeAttributeAdded( sourceId, eltId, attribute, newValue );
				}
				else
				{
					for( GraphAttributesListener l: attrListeners )
							l.graphAttributeAdded( sourceId, attribute, newValue );					
				}
			}
			else if( event == AttributeChangeEvent.REMOVE )
			{
				if( eltType == ElementType.NODE )
				{
					for( GraphAttributesListener l: attrListeners )
							l.nodeAttributeRemoved( sourceId, eltId, attribute );
				}
				else if( eltType == ElementType.EDGE )
				{
					for( GraphAttributesListener l: attrListeners )
							l.edgeAttributeRemoved( sourceId, eltId, attribute );
				}
				else
				{
					for( GraphAttributesListener l: attrListeners )
							l.graphAttributeRemoved( sourceId, attribute );					
				}								
			}
			else
			{
				if( eltType == ElementType.NODE )
				{
					for( GraphAttributesListener l: attrListeners )
							l.nodeAttributeChanged( sourceId, eltId, attribute, oldValue, newValue );
				}
				else if( eltType == ElementType.EDGE )
				{
					for( GraphAttributesListener l: attrListeners )
							l.edgeAttributeChanged( sourceId, eltId, attribute, oldValue, newValue );
				}
				else
				{
					for( GraphAttributesListener l: attrListeners )
							l.graphAttributeChanged( sourceId, attribute, oldValue, newValue );					
				}				
			}

			manageEvents();
			eventProcessing = false;
			checkListenersToRemove();
		}
		else
		{
//			printPosition( "ChgEdge in EventProc" );
			eventQueue.add( new AttributeChangedEvent( sourceId, eltId, eltType, attribute, event, oldValue, newValue ) );
		}
	}
	
// Deferred event management

	/**
	 * If in "event processing mode", ensure all pending events are processed.
	 */
	protected void manageEvents()
	{
		if( eventProcessing )
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
		if( event.getClass() == AttributeChangedEvent.class )
		{
			AttributeChangedEvent ev = (AttributeChangedEvent)event;
			
			if( ev.event == AttributeChangeEvent.ADD )
			{
				if( ev.eltType == ElementType.NODE )
				{
					for( GraphAttributesListener l: attrListeners )
						l.nodeAttributeAdded( ev.sourceId, ev.eltId, ev.attribute, ev.newValue );
				}
				else if( ev.eltType == ElementType.EDGE )
				{
					for( GraphAttributesListener l: attrListeners )
						l.edgeAttributeAdded( ev.sourceId, ev.eltId, ev.attribute, ev.newValue );					
				}
				else
				{
					for( GraphAttributesListener l: attrListeners )
						l.graphAttributeAdded( ev.sourceId, ev.attribute, ev.newValue );										
				}
			}
			else if( ev.event == AttributeChangeEvent.REMOVE )
			{
				if( ev.eltType == ElementType.NODE )
				{
					for( GraphAttributesListener l: attrListeners )
						l.nodeAttributeRemoved( ev.sourceId, ev.eltId, ev.attribute );
				}
				else if( ev.eltType == ElementType.EDGE )
				{
					for( GraphAttributesListener l: attrListeners )
						l.edgeAttributeRemoved( ev.sourceId, ev.eltId, ev.attribute );					
				}
				else
				{
					for( GraphAttributesListener l: attrListeners )
						l.graphAttributeRemoved( ev.sourceId, ev.attribute );										
				}
			}
			else
			{
				if( ev.eltType == ElementType.NODE )
				{
					for( GraphAttributesListener l: attrListeners )
						l.nodeAttributeChanged( ev.sourceId, ev.eltId, ev.attribute, ev.oldValue, ev.newValue );
				}
				else if( ev.eltType == ElementType.EDGE )
				{
					for( GraphAttributesListener l: attrListeners )
						l.edgeAttributeChanged( ev.sourceId, ev.eltId, ev.attribute, ev.oldValue, ev.newValue );					
				}
				else
				{
					for( GraphAttributesListener l: attrListeners )
						l.graphAttributeChanged( ev.sourceId, ev.attribute, ev.oldValue, ev.newValue );										
				}				
			}
		}
		
		// Elements events
		
		else if( event.getClass() == AfterEdgeAddEvent.class )
		{
			AfterEdgeAddEvent e = (AfterEdgeAddEvent) event;
			
			for( GraphElementsListener l: eltsListeners )
				l.edgeAdded( e.sourceId, e.edgeId, e.fromNodeId, e.toNodeId, e.directed );
		}
		else if( event.getClass() == AfterNodeAddEvent.class )
		{
			AfterNodeAddEvent e = (AfterNodeAddEvent) event;
			
			for( GraphElementsListener l: eltsListeners )
				l.nodeAdded( e.sourceId, e.nodeId );
		}
		else if( event.getClass() == BeforeEdgeRemoveEvent.class )
		{
			BeforeEdgeRemoveEvent e = (BeforeEdgeRemoveEvent) event;
			
			for( GraphElementsListener l: eltsListeners )
				l.edgeRemoved( e.sourceId, e.edgeId );
		}
		else if( event.getClass() == BeforeNodeRemoveEvent.class )
		{
			BeforeNodeRemoveEvent e = (BeforeNodeRemoveEvent) event;
			
			for( GraphElementsListener l: eltsListeners )
				l.nodeRemoved( e.sourceId, e.nodeId );
		}
	}

// Events Management

	/**
	 * Interface that provide general purpose classification for evens involved
	 * in graph modifications
	 */
	class GraphEvent
	{
		String sourceId;
		
		GraphEvent( String sourceId )
		{
			this.sourceId = sourceId;
		}
	}

	class AfterEdgeAddEvent extends GraphEvent
	{
		String edgeId;
		String fromNodeId;
		String toNodeId;
		boolean directed;

		AfterEdgeAddEvent( String sourceId, String edgeId, String fromNodeId, String toNodeId, boolean directed )
		{
			super( sourceId );
			this.edgeId = edgeId;
			this.fromNodeId = fromNodeId;
			this.toNodeId = toNodeId;
			this.directed = directed;
		}
	}

	class BeforeEdgeRemoveEvent extends GraphEvent
	{
		String edgeId;

		BeforeEdgeRemoveEvent( String sourceId, String edgeId )
		{
			super( sourceId );
			this.edgeId = edgeId;
		}
	}

	class AfterNodeAddEvent extends GraphEvent
	{
		String nodeId;

		AfterNodeAddEvent( String sourceId, String nodeId )
		{
			super( sourceId );
			this.nodeId = nodeId;
		}
	}

	class BeforeNodeRemoveEvent extends GraphEvent
	{
		String nodeId;

		BeforeNodeRemoveEvent( String sourceId, String nodeId )
		{
			super( sourceId );
			this.nodeId = nodeId;
		}
	}

	class BeforeGraphClearEvent extends GraphEvent
	{
		BeforeGraphClearEvent( String sourceId )
		{
			super( sourceId );
		}
	}

	class AttributeChangedEvent extends GraphEvent
	{
		ElementType eltType;
		
		String eltId;
		
		String attribute;
		
		AttributeChangeEvent event;

		Object oldValue;

		Object newValue;

		AttributeChangedEvent( String sourceId, String eltId, ElementType eltType, String attribute, AttributeChangeEvent event, Object oldValue, Object newValue )
		{
			super( sourceId );
			this.eltType   = eltType;
			this.eltId     = eltId;
			this.attribute = attribute;
			this.event     = event;
			this.oldValue  = oldValue;
			this.newValue  = newValue;
		}
	}
}