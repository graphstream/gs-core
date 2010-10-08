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
 * Sink of graph events.
 * 
 * <p>
 * An output is something that can receive graph events. The output will send or
 * transform the graph events in another form: a file, a network stream, a
 * visualization, an algorithm, a metric, etc.
 * </p>
 * 
 * <p>
 * The output can filter the stream of attribute events using
 * {@link AttributePredicate}s.
 * </p>
 * 
 * 
 * <p>
 * This listener is in fact the grouping of two specialized listeners. The first
 * one listens only at structural changes in the graph (node and edge addition
 * and removal). It is the {@link org.graphstream.stream.ElementSink}. The
 * second one listens only at attributes values changes on elements of the graph
 * (attribute addition, removal and change of values). It is the
 * {@link org.graphstream.stream.AttributeSink}.
 * </p>
 * 
 * <p>
 * It is possible to listen only at elements or attributes changes with these
 * two interfaces. Registering a graph listener will allow to listen at the both
 * elements and attributes at the same time.
 * </p>
 * 
 * @see Source
 * @see Pipe
 * @see AttributeSink
 * @see ElementSink
 */
public interface Sink extends AttributeSink, ElementSink {
}