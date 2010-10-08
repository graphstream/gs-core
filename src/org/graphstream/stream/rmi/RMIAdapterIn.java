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

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIAdapterIn extends Remote {
	void edgeAttributeAdded(String graphId, long timeId, String edgeId,
			String attribute, Object value) throws RemoteException;

	void edgeAttributeChanged(String graphId, long timeId, String edgeId,
			String attribute, Object oldValue, Object newValue)
			throws RemoteException;

	void edgeAttributeRemoved(String graphId, long timeId, String edgeId,
			String attribute) throws RemoteException;

	void graphAttributeAdded(String graphId, long timeId, String attribute,
			Object value) throws RemoteException;

	void graphAttributeChanged(String graphId, long timeId, String attribute,
			Object oldValue, Object newValue) throws RemoteException;

	void graphAttributeRemoved(String graphId, long timeId, String attribute)
			throws RemoteException;

	void nodeAttributeAdded(String graphId, long timeId, String nodeId,
			String attribute, Object value) throws RemoteException;

	void nodeAttributeChanged(String graphId, long timeId, String nodeId,
			String attribute, Object oldValue, Object newValue)
			throws RemoteException;

	void nodeAttributeRemoved(String graphId, long timeId, String nodeId,
			String attribute) throws RemoteException;

	void edgeAdded(String graphId, long timeId, String edgeId,
			String fromNodeId, String toNodeId, boolean directed)
			throws RemoteException;

	void edgeRemoved(String graphId, long timeId, String edgeId)
			throws RemoteException;

	void graphCleared(String graphId, long timeId) throws RemoteException;

	void nodeAdded(String graphId, long timeId, String nodeId)
			throws RemoteException;

	void nodeRemoved(String graphId, long timeId, String nodeId)
			throws RemoteException;

	void stepBegins(String graphId, long timeId, double step)
			throws RemoteException;
}
