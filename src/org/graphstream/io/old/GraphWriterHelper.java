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
import org.graphstream.graph.Node;

/**
 * Helper to easily output a Graph instance to a file.
 *
 * <p>
 * Instances of this class take a graph as argument and write it in a file. It
 * defines two methods to do so: {@link #write(String)} and
 * {@link #write(String, GraphWriter)}. The first one tried to deduce the graph
 * output format from the filename extension (and if it cannot or if there is
 * no extension is uses DGS). The other takes an already create graph writer.
 * </p>
 * 
 * @since 20070125
 */
public class GraphWriterHelper
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

// Constructors
	
	/**
	 * New writer helper that can output the given graph.
	 * @param graph The graph to output.
	 */
	public GraphWriterHelper( Graph graph )
	{
		this.graph = graph;
	}
	
	/**
	 * Write the graph to a file whose name is given. The writer to use is
	 * deduced from the given filename extension. If the filename has an unknown
	 * extension or no extension at all, the DGS format is used.
	 * @param filename The name of the file to create, the extension tells the
	 *        graph writer to use.
	 */
	public void write( String filename )
		throws IOException
	{
		writer = GraphWriterFactory.writerFor( filename );
		
		write( filename, writer );
	}
	
	/**
	 * Write the graph to a file whose name is given using the given writer. It
	 * is not checked that the file name extension matches the format of the
	 * graph writer.
	 * @param filename The name of the file to write.
	 * @param writer The graph writer to use to output the file.
	 */
	public void write( String filename, GraphWriter writer )
		throws IOException
	{
		this.writer = writer;
		
		writer.begin( filename, graph.getId() );
		outputGraph();
		writer.end();
	}
		
	protected void outputGraph()
		throws IOException
	{
		Iterator<? extends Node> nodes = graph.getNodeIterator();
		Iterator<? extends Edge> edges = graph.getEdgeIterator();
		
		while( nodes.hasNext() )
		{
			Node node = nodes.next();

			writer.addNode( node.getId(), node.getAttributeMap() );
		}
		
		while( edges.hasNext() )
		{
			Edge edge = edges.next();
			
			writer.addEdge( edge.getId(), edge.getNode0().getId(),
					edge.getNode1().getId(),
					edge.isDirected(), edge.getAttributeMap() );
		}
	}
}