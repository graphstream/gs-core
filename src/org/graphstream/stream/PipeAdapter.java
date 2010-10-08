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
package org.graphstream.stream;

/**
 * Adapter for the filter interface.
 * 
 * <p>
 * All methods are empty.
 * </p>
 */
public class PipeAdapter implements Pipe {
	public void addAttributeSink(AttributeSink listener) {
	}

	public void addElementSink(ElementSink listener) {
	}

	public void addSink(Sink listener) {
	}

	public void removeAttributeSink(AttributeSink listener) {
	}

	public void removeElementSink(ElementSink listener) {
	}

	public void removeSink(Sink listener) {
	}

	public void edgeAttributeAdded(String sourceId, long timeId, String edgeId,
			String attribute, Object value) {
	}

	public void edgeAttributeChanged(String sourceId, long timeId,
			String edgeId, String attribute, Object oldValue, Object newValue) {
	}

	public void edgeAttributeRemoved(String sourceId, long timeId,
			String edgeId, String attribute) {
	}

	public void graphAttributeAdded(String sourceId, long timeId,
			String attribute, Object value) {
	}

	public void graphAttributeChanged(String sourceId, long timeId,
			String attribute, Object oldValue, Object newValue) {
	}

	public void graphAttributeRemoved(String sourceId, long timeId,
			String attribute) {
	}

	public void nodeAttributeAdded(String sourceId, long timeId, String nodeId,
			String attribute, Object value) {
	}

	public void nodeAttributeChanged(String sourceId, long timeId,
			String nodeId, String attribute, Object oldValue, Object newValue) {
	}

	public void nodeAttributeRemoved(String sourceId, long timeId,
			String nodeId, String attribute) {
	}

	public void edgeAdded(String sourceId, long timeId, String edgeId,
			String fromNodeId, String toNodeId, boolean directed) {
	}

	public void edgeRemoved(String sourceId, long timeId, String edgeId) {
	}

	public void graphCleared(String sourceId, long timeId) {
	}

	public void nodeAdded(String sourceId, long timeId, String nodeId) {
	}

	public void nodeRemoved(String sourceId, long timeId, String nodeId) {
	}

	public void stepBegins(String sourceId, long timeId, double step) {
	}

	public void clearAttributeSinks() {
	}

	public void clearElementSinks() {
	}

	public void clearSinks() {
	}
}