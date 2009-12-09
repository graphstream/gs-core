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
package org.graphstream.io;

import org.graphstream.graph.GraphEvent;
import org.graphstream.graph.GraphEvent.AttributeEvent;
import org.graphstream.graph.GraphEvent.EdgeEvent;
import org.graphstream.graph.GraphEvent.GraphStepEvent;
import org.graphstream.graph.GraphEvent.NodeEvent;

/**
 * Adapter for the {@link Sink} interface.
 * 
 * <p>All methods are empty.</p>
 */
public class SinkAdapter
	implements Sink
{
	public void edgeAttributeAdded(AttributeEvent e) {
	}

	public void edgeAttributeChanged(AttributeEvent e) {
	}

	public void edgeAttributeRemoved(AttributeEvent e) {
	}

	public void graphAttributeAdded(AttributeEvent e) {
	}

	public void graphAttributeChanged(AttributeEvent e) {
	}

	public void graphAttributeRemoved(AttributeEvent e) {
	}

	public void nodeAttributeAdded(AttributeEvent e) {
	}

	public void nodeAttributeChanged(AttributeEvent e) {
	}

	public void nodeAttributeRemoved(AttributeEvent e) {
	}

	public void edgeAdded(EdgeEvent e) {
	}

	public void edgeRemoved(EdgeEvent e) {
	}

	public void graphCleared(GraphEvent e) {
	}

	public void nodeAdded(NodeEvent e) {
	}

	public void nodeRemoved(NodeEvent e) {
	}

	public void stepBegins(GraphStepEvent e) {
	}
}