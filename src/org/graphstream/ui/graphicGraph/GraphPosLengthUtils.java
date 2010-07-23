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

package org.graphstream.ui.graphicGraph;

import java.util.Iterator;
import java.util.Random;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.ElementNotFoundException;

/**
 * Lots of small often used measuring algorithms on graphs.
 * 
 * <p>
 * Use this class with a static import.
 * </p>
 */
public class GraphPosLengthUtils
{
// Access

	/**
	 * Choose a node at random.
	 * @return A node chosen at random.
	 * @complexity at worse O(n) where n is the number of nodes.
	 */
	public static Node randomNode( Graph graph )
	{
		return randomNode( graph, new Random() );
	}
	
	/**
	 * Choose a node at random.
	 * @param random The random number generator to use.
	 * @return A node chosen at random.
	 * @complexity at worse O(n) where n is the number of nodes.
	 */
	public static Node randomNode( Graph graph, Random random )
	{
		int n = graph.getNodeCount();
		int r = random.nextInt( n );
		int i = 0;
		
		Iterator<? extends Node> nodes = graph.getNodeIterator();
		
		while( nodes.hasNext() )
		{
		    Node node = nodes.next();
		    
		    if( r == i )
		    {
			return node;
		    }
			
		    i++;
		}
		
		throw new RuntimeException( "Outch !!" );
	}
	
	/**
	 * Retrieve a node position from its attributes ("x", "y", "z", or "xy", or "xyz").
	 * @param id The node identifier.
	 * @return A newly allocated array of three floats containing the (x,y,z) position of the node,
	 * or null if the node is not part of the graph.
	 */
	public static float[] nodePosition( Graph graph, String id )
	{
		Node node = graph.getNode( id );
		
		if( node != null )
			return nodePosition( node );

		return null;
	}
	
	/**
	 * Like {@link #nodePosition(Graph,String)} but use an existing node as argument.
	 * @param node The node to consider.
	 * @return A newly allocated array of three floats containing the (x,y,z) position of the node.
	 */
	public static float[] nodePosition( Node node )
	{
		float xyz[] = new float[3];
		
		nodePosition( node, xyz );
		
		return xyz;
	}
	
	/**
	 * Like {@link #nodePosition(Graph,String)}, but instead of returning a newly allocated array,
	 * fill up the array given as parameter. This array must have at least three cells.
	 * @param id The node identifier.
	 * @param xyz An array of at least three cells.
	 * @throws ElementNotFoundException If the node with the given identifier does not exist.
	 */
	public static void nodePosition( Graph graph, String id, float xyz[] )
		throws ElementNotFoundException
	{
		Node node = graph.getNode( id );
		
		if( node != null )
			nodePosition( node, xyz );
		
		throw new ElementNotFoundException( "node '"+id+"' does not exist" );
	}
	
	/**
	 * Like {@link #nodePosition(Graph,String,float[])} but use an existing node as argument.
	 * @param node The node to consider.
	 * @param xyz An array of at least three cells.
	 */
	public static void nodePosition( Node node, float xyz[] )
	{
		if( xyz.length < 3 ) {
			System.err.println( "xyz[] argument must be at least 3 cells in size." );
			return;
		}
		
		if( node.hasAttribute( "xyz" ) || node.hasAttribute( "xy" ) )
		{
			Object o = node.getAttribute( "xyz" );
			
			if( o == null )
				o = node.getAttribute( "xy" );
			
			if( o != null && o instanceof Object[] )
			{
				Object oo[] = (Object[]) o;
				
				if( oo.length > 0 && oo[0] instanceof Number )
				{
					xyz[0] = ((Number)oo[0]).floatValue();
					
					if( oo.length > 1 )
						xyz[1] = ((Number)oo[1]).floatValue();
					if( oo.length > 2 )
						xyz[2] = ((Number)oo[2]).floatValue();
				}
			}
		}
		else if( node.hasAttribute( "x" ) )
		{
			xyz[0] = (float) node.getNumber( "x" );
			
			if( node.hasAttribute( "y" ) )
				xyz[1] = (float) node.getNumber( "y" );

			if( node.hasAttribute( "z" ) )
				xyz[2] = (float) node.getNumber( "z" );
		}
	}
	
	/**
	 * Compute the edge length of the given edge according to its two nodes positions.
	 * @param id The identifier of the edge.
	 * @return The edge length or -1 if the nodes of the edge have no positions.
	 * @throws ElementNotFoundException If the edge cannot be found.
	 */
	public static float edgeLength( Graph graph, String id )
		throws ElementNotFoundException
	{
		Edge edge = graph.getEdge( id );
		
		if( edge != null )
			return edgeLength( edge );
		
		throw new RuntimeException( "edge '"+id+"' cannot be found" );
	}
	
	/**
	 * Like {@link #edgeLength(Graph,String)} but use an existing edge as argument.
	 * @param edge
	 * @return The edge length or -1 if the nodes of the edge have no positions.
	 */
	public static float edgeLength( Edge edge )
	{
		float xyz0[] = nodePosition( edge.getNode0() );
		float xyz1[] = nodePosition( edge.getNode1() );
		
		if( xyz0 == null || xyz1 == null )
			return -1;
		
		xyz0[0] = xyz1[0] - xyz0[0];
		xyz0[1] = xyz1[1] - xyz0[1];
		xyz0[2] = xyz1[2] - xyz0[2];
		
		return (float) Math.sqrt( xyz0[0]*xyz0[0] + xyz0[1]*xyz0[1] + xyz0[2]*xyz0[2] );
	}
}