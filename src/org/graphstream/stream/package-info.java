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

/**
 * A major package responsible for the production and management of streams of events. 
 * 
 * <p>
 * GraphStream deal with dynamic graphs. The idea of dynamics is handled by the general idea that elements 
 * of the dynamics are events thats occur at given dates. this events are produced by producers (or sources) 
 * and a aimed at target (sinks). A set of events moving from a source to a sink it a stream of events. 
 * </p>
 * 
 * <p>
 * Streams are produced form an outside source of events. See the {@link org.graphstream.stream.Source} Interface for more information 
 * about event producers. It can be a file (with it's own description of a graph format) or any file descriptor 
 * like an url. See the {@link org.graphstream.stream.file.FileSource} interface for more information about 
 * file-based event producers. It can also be originated from another program running in another thread (see 
 * {@link org.graphstream.stream.ProxyPipe} and {@link org.graphstream.stream.thread.ThreadProxyPipe}).
 * </p>
 * 
 * <p>
 * Objects responsible for receiving streams of events and dealing with it are {@link org.graphstream.stream.Sink}s. 
 * Sinks can produce output files (see {@link org.graphstream.stream.file.FileSink})or being used to display the graph on a screen.
 * </p>
 * 
 * <p> Some objects are both {@link org.graphstream.stream.Source} and {@link org.graphstream.stream.Sink}, 
 * they receive events but also send some events. They are {@link org.graphstream.stream.Pipe}s. The 
 * {@link org.graphstream.graph.Graph} interface is a famous {@link org.graphstream.stream.Pipe}.      
 * 
 */
package org.graphstream.stream;