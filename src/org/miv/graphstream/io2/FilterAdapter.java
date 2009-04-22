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
package org.miv.graphstream.io2;

import org.miv.graphstream.graph.GraphAttributesListener;
import org.miv.graphstream.graph.GraphElementsListener;
import org.miv.graphstream.graph.GraphListener;

public class FilterAdapter
	implements Filter
{

	public void addGraphAttributesListener(GraphAttributesListener listener) {
		// TODO Auto-generated method stub

	}

	public void addGraphElementsListener(GraphElementsListener listener) {
		// TODO Auto-generated method stub

	}

	public void addGraphListener(GraphListener listener) {
		// TODO Auto-generated method stub

	}

	public void removeGraphAttributesListener(GraphAttributesListener listener) {
		// TODO Auto-generated method stub

	}

	public void removeGraphElementsListener(GraphElementsListener listener) {
		// TODO Auto-generated method stub

	}

	public void removeGraphListener(GraphListener listener) {
		// TODO Auto-generated method stub

	}

	public void edgeAttributeAdded(String graphId, String edgeId,
			String attribute, Object value) {
		// TODO Auto-generated method stub

	}

	public void edgeAttributeChanged(String graphId, String edgeId,
			String attribute, Object oldValue, Object newValue) {
		// TODO Auto-generated method stub

	}

	public void edgeAttributeRemoved(String graphId, String edgeId,
			String attribute) {
		// TODO Auto-generated method stub

	}

	public void graphAttributeAdded(String graphId, String attribute,
			Object value) {
		// TODO Auto-generated method stub

	}

	public void graphAttributeChanged(String graphId, String attribute,
			Object oldValue, Object newValue) {
		// TODO Auto-generated method stub

	}

	public void graphAttributeRemoved(String graphId, String attribute) {
		// TODO Auto-generated method stub

	}

	public void nodeAttributeAdded(String graphId, String nodeId,
			String attribute, Object value) {
		// TODO Auto-generated method stub

	}

	public void nodeAttributeChanged(String graphId, String nodeId,
			String attribute, Object oldValue, Object newValue) {
		// TODO Auto-generated method stub

	}

	public void nodeAttributeRemoved(String graphId, String nodeId,
			String attribute) {
		// TODO Auto-generated method stub

	}

	public void edgeAdded(String graphId, String edgeId, String fromNodeId,
			String toNodeId, boolean directed) {
		// TODO Auto-generated method stub

	}

	public void edgeRemoved(String graphId, String edgeId) {
		// TODO Auto-generated method stub

	}

	public void graphCleared(String graphId) {
		// TODO Auto-generated method stub

	}

	public void nodeAdded(String graphId, String nodeId) {
		// TODO Auto-generated method stub

	}

	public void nodeRemoved(String graphId, String nodeId) {
		// TODO Auto-generated method stub

	}

	public void stepBegins(String graphId, double time) {
		// TODO Auto-generated method stub

	}

}
