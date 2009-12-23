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

import org.graphstream.graph.Graph;
import org.graphstream.graph.GraphListener;

/**
 * A graph writer helper that listens at a graph and writes back all modifications as events.
 * 
 * <p>
 * At the contrary of the {@link org.graphstream.io.old.GraphWriterHelper} that takes
 * a snap shot of the graph at the current time, this dynamic writer helper outputs
 * each change in the graph at the time of its occurrence, and therefore stores in
 * the file the history of the graph evolution. 
 * </p>
 * 
 * <p>
 * Be careful, this helper works only with output graph file formats that support dynamic
 * graphs (DGS supports all commands, or at least GML extended by GraphStream, but probably
 * not readable elsewhere).
 * </p>
 */
public class DynamicGraphWriterHelper implements GraphListener
{
// Attribute

	/**
	 * The output.
	 */
	protected GraphWriter writer;
	
	/**
	 * The graph.
	 */
	protected Graph graph;
	
	/**
	 * The last error encountered.
	 */
	protected Exception lastError;
	
// Construction
	
	public DynamicGraphWriterHelper()
		throws IOException
	{
	}
	
// Access

	/**
	 * The last encountered I/O error. The GraphListener interface does not support the reporting
	 * of errors. Therefore if the writer raises an error, this one is memorised and can then be
	 * checked with this method. 
	 * @return an exception.
	 */
	public Exception getLastError()
	{
		return lastError;
	}
	
// Command
    
    public void flush()
    {
    	writer.flush();
    }
	
	/**
	 * Begin the graph output. The file format is automatically guessed from the file name
	 * extension.
	 * @param graph The graph to listen at.
	 * @param fileName The output file name.
	 */
	public void begin( Graph graph, String fileName )
		throws IOException
	{
		begin( graph, GraphWriterFactory.writerFor( fileName ), fileName );
	}
	
	/**
	 * Begin the graph output.
	 * @param graph The graph to listen at.
	 * @param writer The writer to use to output the graph.
	 * @param fileName The output file name.
	 */
	public void begin( Graph graph, GraphWriter writer, String fileName )
		throws IOException
	{
		this.graph = graph;
		graph.addGraphListener( this );
		this.writer = writer;
		writer.begin( fileName, graph.getId() );
	}
	
	/**
	 * Generate a step event.
	 * @param time The time stamp of this step.
	 */
	public void step( double time )
	{
		try
		{
			writer.step( time );
		}
		catch( IOException e )
		{
			lastError = e;
		}
	}

	/**
	 * End the graph output and cleanly close the files.
	 */
	public void end()
		throws IOException
	{
		if( writer != null )
		{
			writer.end();
			graph.removeGraphListener( this );
			writer = null;
		}
	}

    public void edgeAdded( String graphId, String edgeId, String nodeFromId, String nodeToId, boolean directed )
    {
    	try
    	{
    		if( writer != null )
    			writer.addEdge( edgeId, nodeFromId, nodeToId, directed, null );
    	}
    	catch( IOException e )
    	{
    		lastError = e;
    	}
    }

    public void nodeAdded( String graphId, String nodeId )
    {
    	try
    	{
    		if( writer != null )
    			writer.addNode( nodeId, null );
    	}
    	catch( IOException e )
    	{
    		lastError = e;
    	}
    }

    public void edgeRemoved( String graphId, String edgeId )
    {
    	try
    	{
    		if( writer != null )
    			writer.delEdge( edgeId );
    	}
    	catch( IOException e )
    	{
    		lastError = e;
    	}
    }

    public void nodeRemoved( String graphId, String nodeId )
    {
    	try
    	{
    		if( writer != null )
    			writer.delNode( nodeId );
    	}
    	catch( IOException e )
    	{
    		lastError = e;
    	}
    }

	public void stepBegins( String graphId, double time )
	{
		try
    	{
    		if( writer != null )
    			writer.step( time );
    	}
    	catch( IOException e )
    	{
    		lastError = e;
    	}
    }
	
	public void graphCleared( String graphId )
	{
		// No clear events in DGS !!
		// TODO 
		System.err.printf( "Cannot send CLEAR event to a file." );
	}
    
    public void graphAttributeAdded( String graphId, String attribute, Object value )
    {
		try
        {
	        writer.changeGraph( attribute, value, false );
        }
        catch( IOException e )
        {
        	lastError = e;
        }
    }

	public void graphAttributeChanged( String graphId, String attribute, Object oldValue, Object newValue )
    {
		try
        {
	        writer.changeGraph( attribute, newValue, false );
        }
        catch( IOException e )
        {
        	lastError = e;
        }
    }

	public void graphAttributeRemoved( String graphId, String attribute )
    {
		try
        {
	        writer.changeGraph( attribute, null, true );
        }
        catch( IOException e )
        {
        	lastError = e;
        }
    }

	public void nodeAttributeAdded( String graphId, String nodeId, String attribute, Object value )
    {
		try
        {
	        writer.changeNode( nodeId, attribute, value, false );
        }
        catch( IOException e )
        {
        	lastError = e;
        }
    }

	public void nodeAttributeChanged( String graphId, String nodeId, String attribute, Object oldValue, Object newValue )
    {
		try
        {
	        writer.changeNode( nodeId, attribute, newValue, false );
        }
        catch( IOException e )
        {
        	lastError = e;
        }
    }

	public void nodeAttributeRemoved( String graphId, String nodeId, String attribute )
    {
		try
        {
	        writer.changeNode( nodeId, attribute, null, true );
        }
        catch( IOException e )
        {
        	lastError = e;
        }
    }

	public void edgeAttributeAdded( String graphId, String edgeId, String attribute, Object value )
    {
		try
        {
	        writer.changeEdge( edgeId, attribute, value, false );
        }
        catch( IOException e )
        {
        	lastError = e;
        }
    }

	public void edgeAttributeChanged( String graphId, String edgeId, String attribute, Object oldValue, Object newValue )
    {
		try
        {
	        writer.changeEdge( edgeId, attribute, newValue, false );
        }
        catch( IOException e )
        {
        	lastError = e;
        }
    }

	public void edgeAttributeRemoved( String graphId, String edgeId, String attribute )
    {
		try
        {
	        writer.changeEdge( edgeId, attribute, null, true );
        }
        catch( IOException e )
        {
        	lastError = e;
        }
    }
}