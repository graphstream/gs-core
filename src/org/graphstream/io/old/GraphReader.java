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

package org.graphstream.io.old;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.graphstream.graph.NotFoundException;
import org.graphstream.io.GraphParseException;

/**
 * Graph reader interface.
 *
 * <p>Interface for various readers. A graph reader allows to parse a graph
 * format on disk or from a stream and to send events describing this graph to a
 * {@link GraphReaderListenerExtended}. This allows to describe both static and dynamic
 * graphs. Indeed, the graph listener will receive an event notification each
 * time something is read in the graph (a node or edge appearance, disappearance
 * or change). In static graphs, events are node and edge appearance only. In
 * dynamic graphs, nodes and edges can also disappear or change.</p>
 *
 * <p>To receive events, one must register a listener using
 * {@link #addGraphReaderListener(GraphReaderListenerExtended)} (the old
 * GraphReaderListener is deprecated in favor of {@link GraphReaderListenerExtended}
 * in order to process attribute addition and removal).</p>
 *
 * <p>The graph can be read in two ways. A first way is to read the whole graph
 * at once. Simply call the {@link #read(String)}, {@link #read(InputStream)}
 * or {@link #read(Reader)} methods to do this.
 * However, as this is a blocking call that will give back control only when the
 * whole graph has been read, this is not usable for dynamic graphs.</p>
 * 
 * <p>The other way is to begin reading the header of the graph file using the
 * {@link #begin(String)}, {@link #begin(InputStream)} or {@link #begin(Reader)}
 * methods and then
 * process events one by one by calling the {@link #nextEvents()} method in a
 * loop while it returns true. When there are no more events, one must then call
 * {@link #end()} to close the file or stream.</p>
 * 
 * <p>Events in dynamic graphs can be grouped in atomic groups using "steps".
 * To read a graph step by step instead of event by event, one can use {@link #nextStep()}
 * instead of {@link #nextEvents()}. Notice however that very few graph formats
 * handle steps and this method becomes almost equal to the "read()" methods if
 * no steps are available.</p>
 * 
 * <p><em>The GraphReaderListener interface is still usable, but its usage is
 * depreciated since it was not possible to receive attribute removal events
 * with it.</em></p>
 *
 * @see GraphReaderListenerExtended
 */
public interface GraphReader
{
	/**
	 * Add a listener for graph reader events. This method is depreciated, please use the
	 * version that takes a {@link GraphReaderListenerExtended} as argument instead.
	 * @param listener The listener.
	 */
	@Deprecated
	public void addGraphReaderListener( GraphReaderListener listener );
	
	/**
	 * Remove a listener for graph reader events. Fails silently if no such
	 * listener is registered.  This method is depreciated, please use the
	 * version that takes a {@link GraphReaderListenerExtended} as argument instead.
	 * @param listener The listener to remove.
	 */
	@Deprecated
	public void removeGraphReaderListener( GraphReaderListener listener );
	
	/**
	 * Add a listener for graph reader events.
	 * @param listener The listener.
	 */
	public void addGraphReaderListener( GraphReaderListenerExtended listener );
	
	/**
	 * Remove a listener for graph reader events. Fails silently if no such
	 * listener is registered. 
	 * @param listener The listener to remove.
	 */
	public void removeGraphReaderListener( GraphReaderListenerExtended listener );
	
	/**
	 * Read the whole graph in one big operation from the given filename and send events to the
	 * registered listeners.
	 * @param filename Filename of the graph to read.
	 * @throws NotFoundException If filename cannot be found.
	 * @throws GraphParseException If an error occurs in the graph format.
	 * @throws IOException If an I/O error occurs while reading the file.
	 */
	public void read( String filename )
		throws NotFoundException, GraphParseException, IOException;

	/**
	 * Read the whole graph in one big operation from the given stream and
	 * send events to the registered listeners.
	 * @param stream Stream containing the graph to read.
	 * @throws GraphParseException If an error occurs in the graph format.
	 * @throws IOException If an I/O error occurs while reading the file.
	 */
	public void read( InputStream stream )
		throws GraphParseException, IOException;
	
	/**
	 * Read the whole graph in one big operation from the given reader and send events to the
	 * registered listeners.
	 * @param reader The input reader containing the graph to read.
	 * @throws GraphParseException If an error occurs in the graph format.
	 * @throws IOException If an I/O error occurs while reading the file.
	 */
	public void read( Reader reader )
		throws GraphParseException, IOException;

	/**
	 * Start the processing of the graph in "by-event" mode from
	 * the given filename, and send events to the registered listeners. Each
	 * call to {@link #begin(String)} must be closed by a
	 * call to {@link #end()}. Call {@link #nextEvents()} or {@link #nextStep()} repeatedly to
	 * process the graph.
	 * @param filename Filename of the graph to read.
	 * @throws NotFoundException If filename cannot be found.
	 * @throws GraphParseException If an error occurs in the graph format.
	 * @throws IOException If an I/O error occurs while reading the file.
	 * @see #nextEvents()
	 * @see #end()
	 */
	public void begin( String filename )
		throws NotFoundException, GraphParseException, IOException;
	
	/**
	 * Start the processing of the graph in "by-event" mode from the given
	 * stream, and send events to the registered listeners. Each
	 * call to {@link #begin(InputStream)} must be closed
	 * by a call to {@link #end()}. Call {@link #nextEvents()} or {@link #nextStep()} repeatedly to
	 * process the graph.
	 * @param stream Stream containing the graph to read.
	 * @throws GraphParseException If an error occurs in the graph format.
	 * @throws IOException If an I/O error occurs while reading the file.
	 * @see #nextEvents()
	 * @see #end()
	 */
	public void begin( InputStream stream )
		throws GraphParseException, IOException;
	
	/**
	 * Start the processing of the graph in "by-event" mode from the given
	 * reader, and send events to the registered listeners. Each
	 * call to {@link #begin(InputStream)} must be closed
	 * by a call to {@link #end()}. Call {@link #nextEvents()} or {@link #nextStep()} repeatedly to
	 * process the graph.
	 * @param reader Reader containing the graph to read.
	 * @throws GraphParseException If an error occurs in the graph format.
	 * @throws IOException If an I/O error occurs while reading the file.
	 * @see #nextEvents()
	 * @see #end()
	 */
	public void begin( Reader reader )
		throws GraphParseException, IOException;

	/**
	 * Process the next available events. This method will try to process as few
	 * events as possible at once, but can process several events if necessary.
	 * In general only top-level elements in the grammar of the  file format are
	 * processed as one event. If a top-level element contains sub-elements that
	 * will produce events, one call to {@link #nextEvents()} will generate all
	 * the associated with this top-level element, but this is not a rule.
	 * @return False as soon as processing finished
	 * @throws GraphParseException If an error occurs in the graph format.
	 * @throws IOException If an I/O error occurs while reading the file.
	 */
	public boolean nextEvents()
		throws GraphParseException, IOException;
	
	/**
	 * The idea of step is to consider groups of events to be processed at the
	 * same time. This method will try to process as many events as there are
	 * between two steps. If not <em>step</em> notion is given in the
	 * graphReader, the this method should behave like the {@link #nextEvents()}
	 * method.
	 * 
	 * @return False as soon as processing finished. True if a step could be
	 *         read.
	 * @throws GraphParseException If an error occurs in the graph format.
	 * @throws IOException If an I/O error occurs while reading the file.
	 */
	public boolean nextStep() 
		throws GraphParseException, IOException;
	
	/**
	 * End <em>by-event</em> modeField processing.
	 * 
	 * @throws GraphParseException If an error occurs in the graph format.
	 * @throws IOException If an I/O error occurs while reading the file.
	 */
	public void end()
		throws GraphParseException, IOException;
}