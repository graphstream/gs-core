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

package org.miv.graphstream.io2.rmi;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.miv.graphstream.graph.GraphAttributesListener;
import org.miv.graphstream.graph.GraphElementsListener;
import org.miv.graphstream.graph.GraphListener;
import org.miv.graphstream.io2.Input;

public class RMIInput
	extends UnicastRemoteObject
	implements RMIAdapterIn, Input
{
	private static final long serialVersionUID = 6635146473737922832L;

	ConcurrentLinkedQueue<GraphAttributesListener> 	attributesListeners;
	ConcurrentLinkedQueue<GraphElementsListener> 	elementsListeners;
	
	public RMIInput()
		throws RemoteException
	{
		attributesListeners	= new ConcurrentLinkedQueue<GraphAttributesListener>();
		elementsListeners	= new ConcurrentLinkedQueue<GraphElementsListener>();
	}
	
	public RMIInput( String name )
		throws RemoteException
	{
		super(); bind(name);
	}
	
	public void bind( String name )
	{
		try
		{
			Naming.rebind( String.format( "//localhost/%s", name ), this );
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	public void edgeAdded(String graphId, String edgeId, String fromNodeId,
			String toNodeId, boolean directed) throws RemoteException
	{
		for( GraphElementsListener gel : elementsListeners )
			gel.edgeAdded(graphId,edgeId,fromNodeId,toNodeId,directed);
	}

	public void edgeAttributeAdded(String graphId, String edgeId,
			String attribute, Object value)
		throws RemoteException
	{
		for( GraphAttributesListener gal : attributesListeners )
			gal.edgeAttributeAdded(graphId,edgeId,attribute,value);
	}

	public void edgeAttributeChanged(String graphId, String edgeId,
			String attribute, Object oldValue, Object newValue)
			throws RemoteException
	{
		for( GraphAttributesListener gal : attributesListeners )
			gal.edgeAttributeChanged(graphId,edgeId,attribute,oldValue,newValue);
	}

	public void edgeAttributeRemoved(String graphId, String edgeId,
			String attribute)
		throws RemoteException
	{
		for( GraphAttributesListener gal : attributesListeners )
			gal.edgeAttributeRemoved(graphId,edgeId,attribute);
	}

	public void edgeRemoved(String graphId, String edgeId)
			throws RemoteException
	{
		for( GraphElementsListener gel : elementsListeners )
			gel.edgeRemoved(graphId,edgeId);
	}

	public void graphAttributeAdded(String graphId, String attribute,
			Object value)
		throws RemoteException
	{
		for( GraphAttributesListener gal : attributesListeners )
			gal.graphAttributeAdded(graphId,attribute,value);
	}

	public void graphAttributeChanged(String graphId, String attribute,
			Object oldValue, Object newValue)
		throws RemoteException
	{
		for( GraphAttributesListener gal : attributesListeners )
			gal.graphAttributeChanged(graphId,attribute,oldValue,newValue);
	}

	public void graphAttributeRemoved(String graphId, String attribute)
			throws RemoteException
	{
		for( GraphAttributesListener gal : attributesListeners )
			gal.graphAttributeRemoved(graphId,attribute);
	}

	public void graphCleared(String graphId)
		throws RemoteException
	{
		for( GraphElementsListener gel : elementsListeners )
			gel.graphCleared(graphId);
	}

	public void nodeAdded(String graphId, String nodeId)
		throws RemoteException
	{
		for( GraphElementsListener gel : elementsListeners )
			gel.nodeAdded(graphId,nodeId);
	}

	public void nodeAttributeAdded(String graphId, String nodeId,
			String attribute, Object value)
		throws RemoteException
	{
		for( GraphAttributesListener gal : attributesListeners )
			gal.nodeAttributeAdded(graphId,nodeId,attribute,value);
	}

	public void nodeAttributeChanged(String graphId, String nodeId,
			String attribute, Object oldValue, Object newValue)
			throws RemoteException
	{
		for( GraphAttributesListener gal : attributesListeners )
			gal.nodeAttributeChanged(graphId,nodeId,attribute,oldValue,newValue);
	}

	public void nodeAttributeRemoved(String graphId, String nodeId,
			String attribute)
		throws RemoteException
	{
		for( GraphAttributesListener gal : attributesListeners )
			gal.nodeAttributeRemoved(graphId,nodeId,attribute);
	}

	public void nodeRemoved(String graphId, String nodeId)
			throws RemoteException
	{
		for( GraphElementsListener gel : elementsListeners )
			gel.nodeRemoved(graphId,nodeId);
	}

	public void stepBegins(String graphId, double time)
		throws RemoteException
	{
		for( GraphElementsListener gel : elementsListeners )
			gel.stepBegins(graphId,time);
	}

	public void addGraphAttributesListener(GraphAttributesListener listener)
	{
		attributesListeners.add(listener);
	}

	public void addGraphElementsListener(GraphElementsListener listener)
	{
		elementsListeners.add(listener);
	}

	public void addGraphListener(GraphListener listener)
	{
		attributesListeners.add(listener);
		elementsListeners.add(listener);
	}

	public void removeGraphAttributesListener(GraphAttributesListener listener)
	{
		attributesListeners.remove(listener);
	}

	public void removeGraphElementsListener(GraphElementsListener listener)
	{
		elementsListeners.remove(listener);
	}

	public void removeGraphListener(GraphListener listener)
	{
		attributesListeners.remove(listener);
		elementsListeners.remove(listener);
	}
}
