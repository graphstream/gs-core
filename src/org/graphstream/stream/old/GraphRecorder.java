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
import java.util.Iterator;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.GraphListener;
import org.graphstream.graph.Node;

/**
 * Dynamically write the contents of a graph to a file, during the graph
 * lifetime.
 * 
 * <p>
 * At the contrary of the {@link GraphWriterHelper} that output a snapshot of a
 * graph at a given time, this helper will output not only the graph as it is
 * when registered, but all the events that occur during the graph life time.
 * </p>
 * 
 * <p>
 * In order to do this, it registers a listener in the graph, and each time
 * something is changed in the graph it outputs the corresponding elements in
 * the graph file. The {@link GraphWriterHelper} class only outputs node
 * additions and edge additions. This class at the contrary not only outputs
 * additions, but also node and edges removal and changes.
 * </p>
 *
 * <p>
 * Due to the dynamic nature of this class, you will have to call
 * Explicitly the {@link #end()} method to signal the end of the 
 * recording and properly close the open file. f you do not, the
 * file contents may not be completely flushed.
 * </p>
 * 
 * <p>
 * This writers implies that the output format is necessarily a format
 * that handles dynamic graphs. You are free to choose the writer, but some of
 * them may not output all the information.
 * </p>
 * 
 * <p>
 * The events registered are all the node/edge addition, deletion and change.
 * The changes are the attribute value changes. It is still possible to use the
 * internal writer to output things while this graph recorder is used. See the
 * {@link #getWriter()} method.
 * </p>
 *
 * <p>
 * Some things do not work well actually:
 * 	<ul>
 * 		<li>It is not possible to delete attributes on edges or node, it is
 *          only possible to change the values. This is a limitation of the
 *          GraphReader and GraphWriter interfaces.</li>
 * 		<li>The I/O exceptions are not reported directly, you have to use
 *          the cumbersome {@link #checkError()} method that returns an
 *          IOException if something occurred during the graph file writing.</li>
 *  </ul>
 * </p>
 *
 * @since 2007
 * @see GraphWriterHelper
 */
public class GraphRecorder implements GraphListener
{
// Attributes
	
	/**
	 * A reference to the graph to output.
	 */
	protected Graph graph;
	
	/**
	 * The graph writer to use.
	 */
	protected GraphWriter writer;
	
	/**
	 * The last error generated, if any.
	 */
	protected IOException lastError;
	
// Constructors
	
	/**
	 * New graph recorder that records all that happens in the given graph inside
	 * a file.
	 * 
	 * This constructor may write a lot of informations to the file if the graph
	 * already contains informations. In this case all nodes and edges are
	 * output at once. 
	 * 
	 * @param graph The graph to record.
	 * @param filename The file to store the graph evolution.
	 * @throws IOException If an error occurs while opening the file.
	 */
	public GraphRecorder( Graph graph, String filename )
		throws IOException
	{
		this( graph, GraphWriterFactory.writerFor( filename ), filename );
	}
	
	/**
	 * New graph recorder that records all that happens in the given graph inside
	 * a file.
	 * 
	 * This constructor may write a lot of informations to the file if the graph
	 * already contains informations. In this case all nodes and edges are
	 * output at once. 
	 * 
	 * @param graph The graph to record.
	 * @param writer The writer to use to write the graph file.
	 * @param filename The file to store the graph evolution.
	 * @throws IOException If an error occurs while opening the file.
	 */
	public GraphRecorder( Graph graph, GraphWriter writer, String filename )
		throws IOException
	{
		this.graph  = graph;
		this.writer = writer;
		
		graph.addGraphListener( this );
		
		writer.begin( filename, "" );
		
		// Write all already present informations.
		
		Iterator<? extends Node> nodes = graph.getNodeIterator();
		
		while( nodes.hasNext() )
		//for( Node node: graph.getNodeSet() )
		{
			Node node = nodes.next();
			
			writer.addNode( node.getId(), node.getAttributeMap() );
		}
		
		Iterator<? extends Edge> edges = graph.getEdgeIterator();
		
		while( edges.hasNext() )
		//for( Edge edge: graph.getEdgeSet() )
		{
			Edge edge = edges.next();
			
			writer.addEdge( edge.getId(), edge.getNode0().getId(),
				edge.getNode1().getId(), edge.isDirected(),
				edge.getAttributeMap() );
		}
	}
	
// Access
	
	/**
	 * Check if an I/O error occured lately. If an error occured it is returned,
	 * else null is returned. Calling this method clears the last error that
	 * occured.
	 * @return An IOException or null if no error occured.
	 */
	public IOException checkError()
	{
		IOException e = lastError;
		lastError = null;
		return e;
	}
	
	/**
	 * The writer used to output the graph.
	 * @return A graph writer.
	 */
	public GraphWriter getWriter()
	{
		return writer;
	}
	
// Commands
	
	/**
	 * End the recording and close all open files cleanly.
	 */
	public void end()
		throws IOException
	{
		if( writer != null )
		{
			writer.end();
			writer = null;
			graph  = null;
		}
	}
	
// Commands -- Graph Listener
	
	public void edgeAdded( String graphId, String edgeId, String fromNodeId, String toNodeId, boolean directed )
	{
		try
		{
			writer.addEdge( edgeId, fromNodeId, toNodeId, directed, null );
		}
		catch( IOException e )
		{			
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void nodeAdded( String graphId, String nodeId )
	{
		try
		{
			writer.addNode( nodeId, null );
		}
		catch( IOException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void edgeRemoved( String graphId, String edgeId )
	{
		try
		{
			writer.delEdge( edgeId );
		}
		catch( IOException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void nodeRemoved( String graphId, String nodeId )
	{
		try
		{
			writer.delNode( nodeId );
		}
		catch( IOException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void graphCleared( String graphId )
	{
		// No clear events in DGS !!
		// TODO 
		System.err.printf( "Cannot send CLEAR event to a file." );
	}

	public void stepBegins( String graphId, double time )
	{
		try
		{
			writer.step( time );
		}
		catch( IOException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void graphAttributeAdded( String graphId, String attribute, Object value )
    {
		try
        {
	        writer.changeGraph( attribute, value, false );
        }
        catch( IOException e )
        {
	        e.printStackTrace();
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
	        e.printStackTrace();
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
	        e.printStackTrace();
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
	        e.printStackTrace();
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
	        e.printStackTrace();
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
	        e.printStackTrace();
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
	        e.printStackTrace();
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
	        e.printStackTrace();
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
	        e.printStackTrace();
        }
    }
}