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

package org.miv.graphstream.graph.implementations;

import org.miv.graphstream.graph.Edge;
import org.miv.graphstream.graph.EdgeFactory;
import org.miv.graphstream.graph.Graph;
import org.miv.graphstream.graph.Node;
import org.miv.graphstream.graph.NodeFactory;

/**
 * A graph implementation that supports multiple edges between two nodes.
 * 
 * <p>
 * This implementation is only a subclass of {@link DefaultGraph} that renames
 * it and add no behaviour excepted changing the node and edge factories to
 * use the {@link MultiNode} and {@link MultiEdge} classes.
 * </p>
 * 
 * @author Antoine Dutot
 */
public class MultiGraph extends DefaultGraph
{
	/**
	 * New empty graph, with the empty string as default identifier.
	 * @see #MultiGraph(String)
	 * @see #MultiGraph(boolean, boolean)
	 * @see #MultiGraph(String, boolean, boolean) 
	 */
	public MultiGraph()
	{
		this( "" );
	}
	
	/**
	 * New empty graph.
	 * @param id Unique identifier of the graph.
	 * @see #MultiGraph(boolean, boolean)
	 * @see #MultiGraph(String, boolean, boolean)
	 */
	public MultiGraph( String id )
	{
		this( id, true, false );
	}

	/**
	 * New empty graph, with the empty string as default identifier.
	 * @param strictChecking If true any non-fatal error throws an exception.
	 * @param autoCreate If true (and strict checking is false), nodes are
	 *        automatically created when referenced when creating a edge, even
	 *        if not yet inserted in the graph.
	 * @see #MultiGraph(String, boolean, boolean)
	 * @see #setStrictChecking(boolean)
	 * @see #setAutoCreate(boolean)
	 */
	public MultiGraph( boolean strictChecking, boolean autoCreate )
	{
		this( "", strictChecking, autoCreate );
	}
	
	/**
	 * New empty graph.
	 * @param id Unique identifier of this graph.
	 * @param strictChecking If true any non-fatal error throws an exception.
	 * @param autoCreate If true (and strict checking is false), nodes are
	 *        automatically created when referenced when creating a edge, even
	 *        if not yet inserted in the graph.
	 * @see #setStrictChecking(boolean)
	 * @see #setAutoCreate(boolean)
	 */
	public MultiGraph( String id, boolean strictChecking, boolean autoCreate )
	{
		super( id, strictChecking, autoCreate );
		
		nodeFactory = new NodeFactory()
		{
			public Node newInstance( String id, Graph graph )
			{
				return new MultiNode(graph,id);
			}
		};
		edgeFactory = new EdgeFactory()
		{
			public Edge newInstance( String id, Node src, Node dst )
			{
				return new MultiEdge(id,src,dst);
			}
		};
		
		addAttribute( "ui.multigraph" );	// XXX a trick, this allows to inform the viewer
											// That multi-edges will be drawn. When drawing
											// simple edges, the viewer may be faster.
											// XXX We should not see this here !!!
											// See the source code for GraphicGraph for the
											// corresponding code.
	}
}