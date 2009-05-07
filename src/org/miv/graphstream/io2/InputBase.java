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

package org.miv.graphstream.io2;

import java.util.ArrayList;

import org.miv.graphstream.graph.GraphAttributesListener;
import org.miv.graphstream.graph.GraphElementsListener;
import org.miv.graphstream.graph.GraphListener;

/**
 * Base implementation of an input that provide basic listener handling.
 * 
 * <p>
 * This implementation can register a set of graph listeners (or separate sets of
 * attributes or elements listeners) and provides protected methods to easily broadcast events
 * to all the listeners (beginning with "send").
 * </p>
 * 
 * <p>Each time you want to produce an event toward all registered listeners, you call
 * one of the "send*" methods with correct parameters. The parameters of the "send*" methods
 * maps to the usual GraphStream events.</p>
 */
public abstract class InputBase implements Input
{
	/**
	 * Set of graph attributes listeners.
	 */
	protected ArrayList<GraphAttributesListener> attrListeners = new ArrayList<GraphAttributesListener>();
	
	/**
	 * Set of graph elements listeners.
	 */
	protected ArrayList<GraphElementsListener> eltsListeners = new ArrayList<GraphElementsListener>();
	
	public void addGraphListener( GraphListener listener )
	{
		attrListeners.add( listener );
		eltsListeners.add( listener );		
	}
	
	public void removeGraphListener( GraphListener listener )
	{
		int index = attrListeners.lastIndexOf( listener );

		if( index >= 0 )
			attrListeners.remove( index );
	}
	
	public void addGraphAttributesListener( GraphAttributesListener listener )
	{
		attrListeners.add( listener );		
	}
	
	public void removeGraphAttributesListener( GraphAttributesListener listener )
	{
		int index = attrListeners.lastIndexOf( listener );

		if( index >= 0 )
			attrListeners.remove( index );
		
		index = eltsListeners.lastIndexOf( listener );
		
		if( index >= 0 )
			eltsListeners.remove( index );
	}
	
	public void addGraphElementsListener( GraphElementsListener listener )
	{
		eltsListeners.add( listener );		
	}
	
	public void removeGraphElementsListener( GraphElementsListener listener )
	{
		int index = eltsListeners.lastIndexOf( listener );

		if( index >= 0 )
			eltsListeners.remove( index );
	}

	/**
	 * Send a "edge attribute added" event to all attribute listeners.
	 * @param graphId The graph identifier.
	 * @param edgeId The edge identifier.
	 * @param attribute The attribute name.
	 * @param value The attribute value.
	 */
	protected void sendEdgeAttributeAdded( String graphId, String edgeId, String attribute, Object value )
    {
		for( GraphAttributesListener l: attrListeners )
			l.edgeAttributeAdded( graphId, edgeId, attribute, value );
    }

	/**
	 * Send a "edge attribute changed" event to all attribute listeners.
	 * @param graphId The graph identifier.
	 * @param edgeId The edge identifier.
	 * @param attribute The attribute name.
	 * @param oldValue The old attribute value.
	 * @param newValue The new attribute value.
	 */
	protected void sendEdgeAttributeChanged( String graphId, String edgeId, String attribute,
            Object oldValue, Object newValue )
    {
		for( GraphAttributesListener l: attrListeners )
			l.edgeAttributeChanged( graphId, edgeId, attribute, oldValue, newValue );
    }

	/**
	 * Send a "edge attribute removed" event to all attribute listeners.
	 * @param graphId The graph identifier.
	 * @param edgeId The edge identifier.
	 * @param attribute The attribute name.
	 */
	protected void sendEdgeAttributeRemoved( String graphId, String edgeId, String attribute )
    {
		for( GraphAttributesListener l: attrListeners )
			l.edgeAttributeRemoved( graphId, edgeId, attribute );
    }

	/**
	 * Send a "graph attribute added" event to all attribute listeners.
	 * @param graphId The graph identifier.
	 * @param attribute The attribute name.
	 * @param value The attribute value.
	 */
	protected void sendGraphAttributeAdded( String graphId, String attribute, Object value )
    {
		for( GraphAttributesListener l: attrListeners )
			l.graphAttributeAdded( graphId, attribute, value );
    }

	/**
	 * Send a "graph attribute changed" event to all attribute listeners.
	 * @param graphId The graph identifier.
	 * @param attribute The attribute name.
	 * @param oldValue The attribute old value.
	 * @param newValue The attribute new value.
	 */
	protected void sendGraphAttributeChanged( String graphId, String attribute, Object oldValue,
            Object newValue )
    {
		for( GraphAttributesListener l: attrListeners )
			l.graphAttributeChanged( graphId, attribute, oldValue, newValue );
    }

	/**
	 * Send a "graph attribute removed" event to all attribute listeners.
	 * @param graphId The graph identifier.
	 * @param attribute The attribute name.
	 */
	protected void sendGraphAttributeRemoved( String graphId, String attribute )
    {
		for( GraphAttributesListener l: attrListeners )
			l.graphAttributeRemoved( graphId, attribute );
    }

	/**
	 * Send a "node attribute added" event to all attribute listeners.
	 * @param graphId The graph identifier.
	 * @param nodeId The node identifier.
	 * @param attribute The attribute name.
	 * @param value The attribute value.
	 */
	protected void sendNodeAttributeAdded( String graphId, String nodeId, String attribute, Object value )
    {
		for( GraphAttributesListener l: attrListeners )
			l.nodeAttributeAdded( graphId, nodeId, attribute, value );
    }

	/**
	 * Send a "node attribute changed" event to all attribute listeners.
	 * @param graphId The graph identifier.
	 * @param nodeId The node identifier.
	 * @param attribute The attribute name.
	 * @param oldValue The attribute old value.
	 * @param newValue The attribute new value.
	 */
	protected void sendNodeAttributeChanged( String graphId, String nodeId, String attribute,
            Object oldValue, Object newValue )
    {
		for( GraphAttributesListener l: attrListeners )
			l.nodeAttributeChanged( graphId, nodeId, attribute, oldValue, newValue );
    }

	/**
	 * Send a "node attribute removed" event to all attribute listeners.
	 * @param graphId The graph identifier.
	 * @param nodeId The node identifier.
	 * @param attribute The attribute name.
	 */
	protected void sendNodeAttributeRemoved( String graphId, String nodeId, String attribute )
    {
		for( GraphAttributesListener l: attrListeners )
			l.nodeAttributeRemoved( graphId, nodeId, attribute );
    }

	/**
	 * Send an "edge added" event to all element listeners.
	 * @param graphId The graph identifier.
	 * @param edgeId The edge identifier.
	 * @param fromNodeId The edge start node.
	 * @param toNodeId The edge end node.
	 * @param directed Is the edge directed?.
	 */
	protected void sendEdgeAdded( String graphId, String edgeId, String fromNodeId, String toNodeId,
            boolean directed )
    {
		for( GraphElementsListener l: eltsListeners )
			l.edgeAdded( graphId, edgeId, fromNodeId, toNodeId, directed );
    }

	/**
	 * Send a "edge removed" event to all element listeners.
	 * @param graphId The graph identifier.
	 * @param edgeId The edge identifier.
	 */
	protected void sendEdgeRemoved( String graphId, String edgeId )
    {
		for( GraphElementsListener l: eltsListeners )
			l.edgeRemoved( graphId, edgeId );
    }

	/**
	 * Send a "graph cleared" event to all element listeners.
	 * @param graphId The graph identifier.
	 */
	protected void sendGraphCleared( String graphId )
    {
		for( GraphElementsListener l: eltsListeners )
			l.graphCleared( graphId );
    }

	/**
	 * Send a "node added" event to all element listeners.
	 * @param graphId The graph identifier.
	 * @param nodeId The node identifier.
	 */
	protected void sendNodeAdded( String graphId, String nodeId )
    {
		for( GraphElementsListener l: eltsListeners )
			l.nodeAdded( graphId, nodeId );
    }

	/**
	 * Send a "node removed" event to all element listeners.
	 * @param graphId The graph identifier.
	 * @param nodeId The node identifier.
	 */
	protected void sendNodeRemoved( String graphId, String nodeId )
    {
		for( GraphElementsListener l: eltsListeners )
			l.nodeRemoved( graphId, nodeId );
    }

	/**
	 * Send a "step begins" event to all element listeners.
	 * @param graphId The graph identifier.
	 * @param time The step time stamp.
	 */
	protected void sendStepBegins( String graphId, double time )
    {
		for( GraphElementsListener l: eltsListeners )
			l.stepBegins( graphId, time );
    }
}