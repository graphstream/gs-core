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
import java.util.Map;

import org.graphstream.io.GraphParseException;

/**
 * This reader listener directly writes all incomming events to a file
 * eventually in another format.
 * 
 * <p>
 * This reader listens at a graph reader and send all events to a give
 * graph writer. No graph is built and the order of events is conserved.
 * It is important to understand that the technique that consist in
 * reading a graph data into a Graph object and then writing it, make
 * all the graph building history disapear : the dynamics is lost. You
 * write the graph at a given point in time. Using the reader-writer
 * you conserves the dynamics.
 * </p>
 * 
 * <p>
 * Graph writers can throw IOException errors. However the graph reader
 * listener methods do not declare exceptions. Therefore if a write
 * error occurs, this helper class stores the exception in a field that
 * can be retrieved later using {@link #getLastError()}.
 * </p>
 * 
 * <p>
 * Also, a good practice when working with Java files is to close the
 * file at end. The graph writers propose a end() method that must be
 * called when writing is finished. This helper class also defines this
 * method that must be called at end to ensure the caches are flushed
 * and the file is complete. YOU CAN LOOSE DATA IF YOU DO NOT CALL THIS
 * METHOD WHEN FINISHED.
 * </p>
 * 
 * @since 20060101
 * @see GraphReader
 * @see GraphReaderListener
 */
public class GraphReaderListenerWriter implements GraphReaderListener
{
	/**
	 * The writer to use.
	 */
	protected GraphWriter writer;

	/**
	 * The last write error encountered.
	 */
	protected Exception lastError;
	
	/**
	 * New default graph reader listener that write the received events using
	 * the given writer.
	 * @param writer The writer to use.
	 * @param fileName The name of the file to create.
	 */
	public GraphReaderListenerWriter( GraphWriter writer, String fileName )
		throws IOException
	{
		this.writer = writer;
		
		writer.begin( fileName, fileName );
	}
	
	/**
	 * The last encountered write error.
	 * @return An exception or null if no error where encountered.
	 */
	public Exception getLastError()
	{
		return lastError;
	}
	
	/**
	 * End the file generation.
	 */
	public void end()
		throws IOException
	{
		if( writer != null )
		{
			writer.end();
			writer = null;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.miv.graphstream.io.GraphReaderListener#edgeAdded(java.lang.String, java.lang.String, java.lang.String, java.util.Map)
	 */
	public void edgeAdded(String id, String from, String to, boolean directed, Map<String, Object> attributes) throws GraphParseException
	{
		try
        {
	        writer.addEdge( id, from, to, directed, attributes );
        }
        catch( IOException e )
        {
        	lastError = e;
	        e.printStackTrace();
        }
	}

	/* (non-Javadoc)
	 * @see org.miv.graphstream.io.GraphReaderListener#edgeChanged(java.lang.String, java.util.Map)
	 */
	public void edgeChanged(String id, Map<String, Object> attributes) throws GraphParseException
	{
		try
        {
	        writer.changeEdge( id, attributes );
        }
        catch( IOException e )
        {
        	lastError = e;
	        e.printStackTrace();
        }
	}

	/* (non-Javadoc)
	 * @see org.miv.graphstream.io.GraphReaderListener#edgeRemoved(java.lang.String)
	 */
	public void edgeRemoved(String id) throws GraphParseException
	{
		try
        {
	        writer.delEdge( id );
        }
        catch( IOException e )
        {
        	lastError = e;
	        e.printStackTrace();
        }
	}

	/* (non-Javadoc)
	 * @see org.miv.graphstream.io.GraphReaderListener#graphChanged(java.util.Map)
	 */
	public void graphChanged(Map<String, Object> attributes) throws GraphParseException
	{
		try
        {
	        writer.changeGraph( attributes );
        }
        catch( IOException e )
        {
        	lastError = e;
	        e.printStackTrace();
        }
	}

	/* (non-Javadoc)
	 * @see org.miv.graphstream.io.GraphReaderListener#nodeAdded(java.lang.String, java.util.Map)
	 */
	public void nodeAdded(String id, Map<String, Object> attributes) throws GraphParseException
	{
		try
        {
	        writer.addNode( id, attributes );
        }
        catch( IOException e )
        {
        	lastError = e;
	        e.printStackTrace();
        }
	}

	/* (non-Javadoc)
	 * @see org.miv.graphstream.io.GraphReaderListener#nodeChanged(java.lang.String, java.util.Map)
	 */
	public void nodeChanged(String id, Map<String, Object> attributes) throws GraphParseException
	{
		try
        {
	        writer.changeNode( id, attributes );
        }
        catch( IOException e )
        {
        	lastError = e;
	        e.printStackTrace();
        }
	}

	/* (non-Javadoc)
	 * @see org.miv.graphstream.io.GraphReaderListener#nodeRemoved(java.lang.String)
	 */
	public void nodeRemoved(String id) throws GraphParseException
	{
		try
        {
	        writer.delNode( id );
        }
        catch( IOException e )
        {
        	lastError = e;
	        e.printStackTrace();
        }
	}

	/* (non-Javadoc)
	 * @see org.miv.graphstream.io.GraphReaderListener#stepBegins(double)
	 */
	public void stepBegins(double time) throws GraphParseException
	{
		try
        {
	        writer.step( time );
        }
        catch( IOException e )
        {
        	lastError = e;
	        e.printStackTrace();
        }
	}

	/* (non-Javadoc)
	 * @see org.miv.graphstream.io.GraphReaderListener#unknownEventDetected(java.lang.String)
	 */
	public void unknownEventDetected(String unknown) throws GraphParseException
	{
		System.out.printf( "GraphReaderListenerWriter : unknown event detected : %s%n", unknown );
	}
}