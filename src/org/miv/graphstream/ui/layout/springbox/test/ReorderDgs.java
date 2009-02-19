/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */

package org.miv.graphstream.algorithm.layout2.springbox.test;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import org.miv.graphstream.graph.Edge;
import org.miv.graphstream.graph.Graph;
import org.miv.graphstream.graph.Node;
import org.miv.graphstream.graph.implementations.MultiGraph;
import org.miv.graphstream.io.GraphParseException;
import org.miv.graphstream.io.GraphReader;
import org.miv.graphstream.io.GraphReaderFactory;
import org.miv.graphstream.io.GraphReaderListenerHelper;
import org.miv.graphstream.io.GraphWriter;
import org.miv.graphstream.io.GraphWriterDGS;
import org.miv.util.NotFoundException;

public class ReorderDgs
{
	protected Graph graph;
	protected GraphReader reader;
	protected GraphWriter writer;

	public static void main( String args[] ) {
		if( args.length > 1 )
		{
			try
			{
				new ReorderDgs( args[0], args[1] );
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}
		}
	}
	
	public ReorderDgs( String fileIn, String fileOut )
		throws IOException, NotFoundException, GraphParseException
	{
		graph  = new MultiGraph();
		reader = GraphReaderFactory.readerFor( fileIn );
		writer = new GraphWriterDGS();
		
		GraphReaderListenerHelper helper = new GraphReaderListenerHelper( graph );
		
		reader.addGraphReaderListener( helper );
		reader.read( fileIn );
		writer.begin( fileOut, fileIn );
		
		//exploreRec( graph.algorithm().getRandomNode() );
		
		open.add( graph.algorithm().getRandomNode() );
		exploreList();
		
		writer.end();
	}
	
	protected void exploreRec( Node start ) throws IOException
	{
		createNode( start );
		
		Iterator<?extends Edge> edges = start.getEdgeIterator();

		while( edges.hasNext() )
		{
			Edge edge = edges.next();
			
			if( ! edge.hasAttribute( "marked" ) )
			{
				Node other = edge.getOpposite( start );
				
				createNode( other );
				createEdge( edge );
				exploreRec( other );
			}
		}
	}
	
	protected LinkedList<Node> open = new LinkedList<Node>(); 
	
	protected void exploreList()
		throws IOException
	{
		Node node = null;
		
		if( ! open.isEmpty() )
			node = open.pop();
		
		while( node != null )
		{
			createNode( node );

			Iterator<?extends Edge> edges = node.getEdgeIterator();

			while( edges.hasNext() )
			{
				Edge edge = edges.next();
				
				if( ! edge.hasAttribute( "marked" ) )
				{
					Node other = edge.getOpposite( node );
					
					createNode( other );
					open.addFirst( other );
					createEdge( edge );
				}
			}

			if( ! open.isEmpty() )
			     node = open.pop();
			else node = null;
		}
	}
	
	protected void createNode( Node start ) throws IOException
	{
		if( ! start.hasAttribute( "marked" ) ) 
		{
			start.addAttribute( "marked" );
			writer.addNode( start.getId(), null );
		}
	}
	
	protected void createEdge( Edge edge ) throws IOException
	{
		if( ! edge.hasAttribute( "marked" ) )
		{
			edge.addAttribute( "marked" );
			writer.addEdge( edge.getId(), edge.getNode0().getId(), edge.getNode1().getId(), edge.isDirected(), null );
		}
	}
}