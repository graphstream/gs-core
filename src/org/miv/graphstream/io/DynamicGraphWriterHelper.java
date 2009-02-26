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

package org.miv.graphstream.io;

import java.io.IOException;
import java.util.HashMap;

import org.miv.graphstream.graph.Edge;
import org.miv.graphstream.graph.Element;
import org.miv.graphstream.graph.Graph;
import org.miv.graphstream.graph.GraphListener;
import org.miv.graphstream.graph.Node;

/**
 * A graph writer helper that listens at a graph and writes back all modifications as events.
 * 
 * <p>
 * At the contrary of the {@link org.miv.graphstream.io.GraphWriterHelper} that takes
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
 * 
 * <p>
 * TODO How to handle attributes correctly.
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

    public void afterEdgeAdd( Graph graph, Edge edge )
    {
    	try
    	{
    		if( writer != null )
    			writer.addEdge( edge.getId(),
    					edge.getNode0().getId(),
    					edge.getNode1().getId(),
    					edge.isDirected(),
    					edge.getAttributeMap() );
    	}
    	catch( IOException e )
    	{
    		lastError = e;
    	}
    }

    public void afterNodeAdd( Graph graph, Node node )
    {
    	try
    	{
    		if( writer != null )
    			writer.addNode( node.getId(), node.getAttributeMap() );
    	}
    	catch( IOException e )
    	{
    		lastError = e;
    	}
    }
    
    protected HashMap<String,Object> attributes = new HashMap<String,Object>();

    public void attributeChanged( Element element, String attribute, Object oldValue,
            Object newValue )
    {
    	try
    	{
    		if( element instanceof Node )
    		{
    			//attributes.clear();
    			//attributes.put( attribute, newValue );
    			//writer.changeNode( element.getId(), attributes );
    			writer.changeNode( element.getId(), attribute, newValue, newValue==null );
    		}
    		else if( element instanceof Edge )
    		{
    			//attributes.clear();
    			//attributes.put( attribute, newValue );
    			//writer.changeEdge( element.getId(), attributes );
    			writer.changeEdge( element.getId(), attribute, newValue, newValue==null );
    		}
    		else if( element instanceof Graph )
    		{
    			//attributes.clear();
    			//attributes.put( attribute, newValue );
    			//writer.changeGraph( attributes );
    			writer.changeGraph( attribute, newValue, newValue==null );
    		}
    	}
    	catch( IOException e )
    	{
    		lastError = e;
    	}
    }

    public void beforeEdgeRemove( Graph graph, Edge edge )
    {
    	try
    	{
    		if( writer != null )
    			writer.delEdge( edge.getId() );
    	}
    	catch( IOException e )
    	{
    		lastError = e;
    	}
    }

    public void beforeGraphClear( Graph graph )
    {
    	// Ah ah!!!  not in DGS !!!!!
    }

    public void beforeNodeRemove( Graph graph, Node node )
    {
    	try
    	{
    		if( writer != null )
    			writer.delNode( node.getId() );
    	}
    	catch( IOException e )
    	{
    		lastError = e;
    	}
    }
    
    public void flush()
    {
    	writer.flush();
    }

	public void stepBegins(Graph graph, double time)
	{
		try
    	{
    		if( writer != null )
    			writer.step(time);
    	}
    	catch( IOException e )
    	{
    		lastError = e;
    	}
    }
}