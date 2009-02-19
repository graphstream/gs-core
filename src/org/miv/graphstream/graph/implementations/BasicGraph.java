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

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

//import org.miv.graphstream.algorithm.Algorithms;
import org.miv.graphstream.graph.Edge;
import org.miv.graphstream.graph.EdgeFactory;
import org.miv.graphstream.graph.Graph;
import org.miv.graphstream.graph.GraphListener;
import org.miv.graphstream.graph.Node;
import org.miv.graphstream.graph.NodeFactory;
import org.miv.graphstream.io.GraphParseException;
import org.miv.graphstream.io.GraphReader;
import org.miv.graphstream.io.GraphWriter;
import org.miv.graphstream.ui.GraphViewerRemote;
import org.miv.util.NotFoundException;
import org.miv.util.SingletonException;

/**
 * The most simple graph.
 * 
 * @author Antoine Dutot
 */
public class BasicGraph extends AbstractElement implements Graph
{
// Attributes

	public HashMap<String,BasicNode> nodes;
	
	public HashMap<String,BasicEdge> edges;
	
// Constructors
	
	public BasicGraph( String identifier )
	{
		super( identifier );
	}
	
	public BasicGraph()
	{
		super( "" );
	}
	
// Access
/*
	public Algorithms algorithm()
    {
	    return null;
    }
*/
	public boolean isAutoCreationEnabled()
    {
	    return false;
    }

	public boolean isStrictCheckingEnabled()
    {
	    return false;
    }

	public List<GraphListener> getGraphListeners()
    {
	    return null;
    }

	public NodeFactory nodeFactory()
    {
	    return null;
    }

	public EdgeFactory edgeFactory()
    {
	    return null;
    }

	public Node getNode( String id )
    {
	    return null;
    }

	public int getNodeCount()
    {
	    return 0;
    }

	public Iterator<? extends Node> getNodeIterator()
    {
	    return null;
    }

	public Collection<? extends Node> getNodeSet()
    {
	    return null;
    }

	public Edge getEdge( String id )
    {
	    return null;
    }

	public int getEdgeCount()
    {
	    return 0;
    }

	public Iterator<? extends Edge> getEdgeIterator()
    {
	    return null;
    }

	public Collection<? extends Edge> getEdgeSet()
    {
	    return null;
    }

// Commands

	public void addGraphListener( GraphListener listener )
    {
    }
	
	public void removeGraphListener( GraphListener listener )
    {
    }

// Node and edge commands

	public void clear()
    {
    }

	public void clearListeners()
    {
    }

	public Node addNode( String id ) throws SingletonException
    {
	    return null;
    }

	public Edge addEdge( String id, String node1, String node2 ) throws SingletonException,
            NotFoundException
    {
	    return null;
    }

	public Edge addEdge( String id, String from, String to, boolean directed )
            throws SingletonException, NotFoundException
    {
	    return null;
    }
	
	public Node removeNode( String id ) throws NotFoundException
    {
	    return null;
    }

	public Edge removeEdge( String from, String to ) throws NotFoundException
    {
	    return null;
    }

	public Edge removeEdge( String id ) throws NotFoundException
    {
	    return null;
    }
	
	public void stepBegins( double time )
	{
		
	}
	
// Settings commands

	public void setAutoCreate( boolean on )
    {
    }

	public void setStrictChecking( boolean on )
    {
    }
	
// Utility commands

	public GraphViewerRemote display()
    {
	    return null;
    }

	public GraphViewerRemote display( boolean autoLayout )
    {
	    return null;
    }

	public void read( String filename ) throws IOException, GraphParseException, NotFoundException
    {
    }

	public void read( GraphReader reader, String filename ) throws IOException, GraphParseException
    {
    }
	
	public int readPositionFile( String posFileName ) throws IOException
    {
	    return 0;
    }

	public void write( String filename ) throws IOException
    {
    }

	public void write( GraphWriter writer, String filename ) throws IOException
    {
    }
	
// Implementation

	@Override
    protected void attributeChanged( String attribute, Object oldValue, Object newValue )
    {
	    
    }
	
// Nested classes
	
public class BasicNode extends AbstractElement implements Node
{

	public BasicNode( String id )
    {
	    super( id );
    }

	@Override
    protected void attributeChanged( String attribute, Object oldValue, Object newValue )
    {
    }

	public Iterator<? extends Node> getBreadthFirstIterator()
    {
	    return null;
    }

	public Iterator<? extends Node> getBreadthFirstIterator( boolean directed )
    {
	    return null;
    }

	public int getDegree()
    {
	    return 0;
    }

	public Iterator<? extends Node> getDepthFirstIterator()
    {
	    return null;
    }

	public Iterator<? extends Node> getDepthFirstIterator( boolean directed )
    {
	    return null;
    }

	public Edge getEdge( int i )
    {
	    return null;
    }

	public Edge getEdgeFrom( String id )
    {
	    return null;
    }

	public Iterator<? extends Edge> getEdgeIterator()
    {
	    return null;
    }

	public Collection<? extends Edge> getEdgeSet()
    {
	    return null;
    }

	public Edge getEdgeToward( String id )
    {
	    return null;
    }

	public Iterator<? extends Edge> getEnteringEdgeIterator()
    {
	    return null;
    }

	public Collection<? extends Edge> getEnteringEdgeSet()
    {
	    return null;
    }

	public Graph getGraph()
    {
	    return null;
    }

	public String getGraphName()
    {
	    return null;
    }

	public String getHost()
    {
	    return null;
    }

	public int getInDegree()
    {
	    return 0;
    }

	public Iterator<? extends Edge> getLeavingEdgeIterator()
    {
	    return null;
    }

	public Collection<? extends Edge> getLeavingEdgeSet()
    {
	    return null;
    }

	public Iterator<? extends Node> getNeighborNodeIterator()
    {
	    return null;
    }

	public int getOutDegree()
    {
	    return 0;
    }

	public boolean hasEdgeFrom( String id )
    {
	    return false;
    }

	public boolean hasEdgeToward( String id )
    {
	    return false;
    }

	public boolean isDistributed()
    {
	    return false;
    }

	public void setGraph( Graph graph )
    {
    }

	public void setGraphName( String newHost )
    {
    }

	public void setHost( String newHost )
    {
    }
		
}
	
public class BasicEdge extends AbstractElement implements Edge
{

	public BasicEdge( String id )
    {
	    super( id );
    }

	@Override
    protected void attributeChanged( String attribute, Object oldValue, Object newValue )
    {
    }

	public Node getNode0()
    {
	    return null;
    }

	public Node getNode1()
    {
	    return null;
    }

	public Node getOpposite( Node node )
    {
	    return null;
    }

	public Node getSourceNode()
    {
	    return null;
    }

	public Node getTargetNode()
    {
	    return null;
    }

	public boolean isDirected()
    {
	    return false;
    }

	public void setDirected( boolean on )
    {
    }

	public void switchDirection()
    {
    }
}
}