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

package org.graphstream.io.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIAdapterIn
	extends Remote
{
	void edgeAttributeAdded(String graphId, String edgeId,
			String attribute, Object value)	throws RemoteException;

	void edgeAttributeChanged(String graphId, String edgeId,
			String attribute, Object oldValue, Object newValue)	throws RemoteException;

	void edgeAttributeRemoved(String graphId, String edgeId,
			String attribute) throws RemoteException;

	void graphAttributeAdded(String graphId, String attribute,
			Object value) throws RemoteException;

	void graphAttributeChanged(String graphId, String attribute,
			Object oldValue, Object newValue) throws RemoteException;

	void graphAttributeRemoved(String graphId, String attribute) throws RemoteException;

	void nodeAttributeAdded(String graphId, String nodeId,
			String attribute, Object value) throws RemoteException;

	void nodeAttributeChanged(String graphId, String nodeId,
			String attribute, Object oldValue, Object newValue) throws RemoteException;

	void nodeAttributeRemoved(String graphId, String nodeId,
			String attribute) throws RemoteException;

	void edgeAdded(String graphId, String edgeId, String fromNodeId,
			String toNodeId, boolean directed) throws RemoteException;

	void edgeRemoved(String graphId, String edgeId) throws RemoteException;

	void graphCleared(String graphId) throws RemoteException;

	void nodeAdded(String graphId, String nodeId) throws RemoteException;

	void nodeRemoved(String graphId, String nodeId) throws RemoteException;

	void stepBegins(String graphId, double time) throws RemoteException;
}
