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
 * Adapter for the input interface.
 * 
 * <p>
 * All methods are empty.
 * </p>
 */
public class SourceAdapter implements Source {
	public void addAttributeSink(AttributeSink sink) {
	}

	public void addElementSink(ElementSink sink) {
	}

	public void addSink(Sink sink) {
	}

	public void removeAttributeSink(AttributeSink sink) {
	}

	public void removeElementSink(ElementSink sink) {
	}

	public void removeSink(Sink sink) {
	}

	public void clearAttributeSinks() {
	}

	public void clearElementSinks() {
	}

	public void clearSinks() {
	}
}