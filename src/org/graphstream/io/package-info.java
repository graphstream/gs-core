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

/**
 * Classes to read and write graphs data from files and streams.
 * 
 * <p>
 * The two main interfaces of this package are
 * {@link org.miv.graphstream.io.GraphReader} for reading graphs descriptions
 * from files and {@link org.miv.graphstream.io.GraphWriter} to write graph
 * descriptions to files.
 * </p>
 * 
 * <p>
 * The readers and writers are then implemented in several classes dedicated to
 * specific formats.
 * </p>
 * 
 * <p>
 * The concept of readers and writers is based on the fact that they are
 * disconnected from the {@link org.miv.graphstream.graph.Graph} class. This
 * means that it is possible to write or read a graph without building it in
 * memory. Someone that has its one graph class can read and write graphs in
 * several usual formats without needing to convert it to the graph class
 * provided by GraphStream.
 * </p>
 * 
 * <p>
 * However there exists two utility classes
 * {@link org.graphstream.io.old.graphstream.io.GraphWriterHelper} and
 * {@link org.miv.graphstream.io.GraphReaderListenerHelper} are provided to read
 * and write instances of the {@link org.miv.graphstream.graph.Graph} class. As
 * a last facility, this graph class contains two methods nammed read and write
 * that allow in one simple call to read or write a graph.
 * </p>
 * 
 * <p>
 * The {@link org.graphstream.io.old.graphstream.io.GraphReaderFactory} class allows to
 * instanciate a graph reader according to a given file name. This class tries
 * to deduce the graph format from the file name, and for some formats from the
 * first bytes of the file.
 * </p>
 */
package org.graphstream.io;