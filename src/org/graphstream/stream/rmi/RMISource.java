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
 * Copyright 2006 - 2010
 * 	Julien Baudry
 * 	Antoine Dutot
 * 	Yoann Pigné
 * 	Guilhelm Savin
 */

package org.graphstream.stream.rmi;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.graphstream.stream.AttributeSink;
import org.graphstream.stream.ElementSink;
import org.graphstream.stream.Sink;
import org.graphstream.stream.Source;

public class RMISource extends UnicastRemoteObject implements RMIAdapterIn,
		Source {
	private static final long serialVersionUID = 6635146473737922832L;

	ConcurrentLinkedQueue<AttributeSink> attributesListeners;
	ConcurrentLinkedQueue<ElementSink> elementsListeners;

	public RMISource() throws RemoteException {
		attributesListeners = new ConcurrentLinkedQueue<AttributeSink>();
		elementsListeners = new ConcurrentLinkedQueue<ElementSink>();
	}

	public RMISource(String name) throws RemoteException {
		super();
		bind(name);
	}

	public void bind(String name) {
		try {
			Naming.rebind(String.format("//localhost/%s", name), this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void edgeAdded(String graphId, long timeId, String edgeId,
			String fromNodeId, String toNodeId, boolean directed)
			throws RemoteException {
		for (ElementSink gel : elementsListeners)
			gel.edgeAdded(graphId, timeId, edgeId, fromNodeId, toNodeId,
					directed);
	}

	public void edgeAttributeAdded(String graphId, long timeId, String edgeId,
			String attribute, Object value) throws RemoteException {
		for (AttributeSink gal : attributesListeners)
			gal.edgeAttributeAdded(graphId, timeId, edgeId, attribute, value);
	}

	public void edgeAttributeChanged(String graphId, long timeId,
			String edgeId, String attribute, Object oldValue, Object newValue)
			throws RemoteException {
		for (AttributeSink gal : attributesListeners)
			gal.edgeAttributeChanged(graphId, timeId, edgeId, attribute,
					oldValue, newValue);
	}

	public void edgeAttributeRemoved(String graphId, long timeId,
			String edgeId, String attribute) throws RemoteException {
		for (AttributeSink gal : attributesListeners)
			gal.edgeAttributeRemoved(graphId, timeId, edgeId, attribute);
	}

	public void edgeRemoved(String graphId, long timeId, String edgeId)
			throws RemoteException {
		for (ElementSink gel : elementsListeners)
			gel.edgeRemoved(graphId, timeId, edgeId);
	}

	public void graphAttributeAdded(String graphId, long timeId,
			String attribute, Object value) throws RemoteException {
		for (AttributeSink gal : attributesListeners)
			gal.graphAttributeAdded(graphId, timeId, attribute, value);
	}

	public void graphAttributeChanged(String graphId, long timeId,
			String attribute, Object oldValue, Object newValue)
			throws RemoteException {
		for (AttributeSink gal : attributesListeners)
			gal.graphAttributeChanged(graphId, timeId, attribute, oldValue,
					newValue);
	}

	public void graphAttributeRemoved(String graphId, long timeId,
			String attribute) throws RemoteException {
		for (AttributeSink gal : attributesListeners)
			gal.graphAttributeRemoved(graphId, timeId, attribute);
	}

	public void graphCleared(String graphId, long timeId)
			throws RemoteException {
		for (ElementSink gel : elementsListeners)
			gel.graphCleared(graphId, timeId);
	}

	public void nodeAdded(String graphId, long timeId, String nodeId)
			throws RemoteException {
		for (ElementSink gel : elementsListeners)
			gel.nodeAdded(graphId, timeId, nodeId);
	}

	public void nodeAttributeAdded(String graphId, long timeId, String nodeId,
			String attribute, Object value) throws RemoteException {
		for (AttributeSink gal : attributesListeners)
			gal.nodeAttributeAdded(graphId, timeId, nodeId, attribute, value);
	}

	public void nodeAttributeChanged(String graphId, long timeId,
			String nodeId, String attribute, Object oldValue, Object newValue)
			throws RemoteException {
		for (AttributeSink gal : attributesListeners)
			gal.nodeAttributeChanged(graphId, timeId, nodeId, attribute,
					oldValue, newValue);
	}

	public void nodeAttributeRemoved(String graphId, long timeId,
			String nodeId, String attribute) throws RemoteException {
		for (AttributeSink gal : attributesListeners)
			gal.nodeAttributeRemoved(graphId, timeId, nodeId, attribute);
	}

	public void nodeRemoved(String graphId, long timeId, String nodeId)
			throws RemoteException {
		for (ElementSink gel : elementsListeners)
			gel.nodeRemoved(graphId, timeId, nodeId);
	}

	public void stepBegins(String graphId, long timeId, double step)
			throws RemoteException {
		for (ElementSink gel : elementsListeners)
			gel.stepBegins(graphId, timeId, step);
	}

	public void addAttributeSink(AttributeSink listener) {
		attributesListeners.add(listener);
	}

	public void addElementSink(ElementSink listener) {
		elementsListeners.add(listener);
	}

	public void addSink(Sink listener) {
		attributesListeners.add(listener);
		elementsListeners.add(listener);
	}

	public void removeAttributeSink(AttributeSink listener) {
		attributesListeners.remove(listener);
	}

	public void removeElementSink(ElementSink listener) {
		elementsListeners.remove(listener);
	}

	public void removeSink(Sink listener) {
		attributesListeners.remove(listener);
		elementsListeners.remove(listener);
	}

	public void clearAttributeSinks() {
		attributesListeners.clear();
		elementsListeners.clear();
	}

	public void clearElementSinks() {
		elementsListeners.clear();
	}

	public void clearSinks() {
		attributesListeners.clear();
	}
}
