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

package org.graphstream.graph.implementations;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.graphstream.graph.Edge;
import org.graphstream.graph.EdgeFactory;
import org.graphstream.graph.Graph;
import org.graphstream.graph.GraphAttributesListener;
import org.graphstream.graph.GraphElementsListener;
import org.graphstream.graph.GraphListener;
import org.graphstream.graph.Node;
import org.graphstream.graph.NodeFactory;
import org.graphstream.io.GraphParseException;
import org.graphstream.io.Pipe;
import org.graphstream.io.SourceBase;
import org.graphstream.io.SourceBase.ElementType;
import org.graphstream.io.file.FileSink;
import org.graphstream.io.file.FileSinkFactory;
import org.graphstream.io.file.FileSource;
import org.graphstream.io.file.FileSourceFactory;
import org.graphstream.io.sync.SinkTime;
import org.graphstream.ui.GraphViewer;
import org.graphstream.ui.GraphViewerRemote;
import org.miv.util.NotFoundException;
import org.miv.util.SingletonException;

/**
 * <p>
 * A light graph class intended to allow the construction of big graphs
 * (millions of elements).
 * </p>
 * 
 * <p>
 * The main purpose here is to minimise memory consumption even if the
 * management of such a graph implies more CPU consuming. See the
 * <code>complexity</code> tags on each method so as to figure out the impact
 * on the CPU.
 * </p>
 */
public class AdjacencyListGraph extends AbstractElement implements Graph
{
	public class EdgeIterator implements Iterator<Edge>
	{
		Iterator<Edge> edgeIterator;
		
		public EdgeIterator()
		{
			edgeIterator = edges.values().iterator();
		}
		
		public boolean hasNext()
		{
			return edgeIterator.hasNext();
		}

		public Edge next()
		{
			return edgeIterator.next();
		}

		public void remove()
		{
			throw new UnsupportedOperationException( "this iterator does not allow removing" );
		}
	}

	
	public class NodeIterator implements Iterator<Node>
	{
		Iterator<Node> nodeIterator;
		
		public NodeIterator()
		{
			nodeIterator = nodes.values().iterator();
		}

		public boolean hasNext()
		{
			return (nodeIterator.hasNext());
		}

		public Node next()
		{
			return  nodeIterator.next();
		}

		public void remove()
		{
			throw new UnsupportedOperationException( "this iterator does not allow removing" );
		}
	}

	/**
	 * All the nodes.
	 */
	protected HashMap<String,Node> nodes = new HashMap<String, Node>();

	/**
	 * All the edges.
	 */
	protected HashMap<String,Edge> edges = new HashMap<String, Edge>();
	
	/**
	 * Verify name space conflicts, removal of non-existing elements, use of
	 * non-existing elements.
	 */
	protected boolean strictChecking = true;

	/**
	 * Automatically create missing elements. For example, if an edge is created
	 * between two non-existing nodes, create the nodes.
	 */
	protected boolean autoCreate = false;

	
	/**
	 *  Help full class that dynamically instantiate nodes according to a given class name.
	 */
	protected NodeFactory nodeFactory;
	
	/**
	 *  Help full class that dynamically instantiate edges according to a given class name.
	 */
	protected EdgeFactory edgeFactory;
	
	/**
	 * The current step.
	 */
	protected double step;

	/**
	 * The set of listeners.
	 */
	protected GraphListeners listeners;
	
// Constructors

	/**
	 * New empty graph, with the empty string as default identifier.
	 * @see #AdjacencyListGraph(String)
	 * @see #AdjacencyListGraph(boolean, boolean)
	 * @see #AdjacencyListGraph(String, boolean, boolean) 
	 */
	@Deprecated
	public AdjacencyListGraph()
	{
		this( "AdjListGraph" );
	}
	
	/**
	 * New empty graph.
	 * @param id Unique identifier of the graph.
	 * @see #AdjacencyListGraph(boolean, boolean)
	 * @see #AdjacencyListGraph(String, boolean, boolean)
	 */
	public AdjacencyListGraph( String id )
	{
		this( id, true, false );
	}

	/**
	 * New empty graph, with the empty string as default identifier.
	 * @param strictChecking If true any non-fatal error throws an exception.
	 * @param autoCreate If true (and strict checking is false), nodes are
	 *        automatically created when referenced when creating a edge, even
	 *        if not yet inserted in the graph.
	 * @see #AdjacencyListGraph(String, boolean, boolean)
	 * @see #setStrict(boolean)
	 * @see #setAutoCreate(boolean)
	 */
	@Deprecated
	public AdjacencyListGraph( boolean strictChecking, boolean autoCreate )
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
	 * @see #setStrict(boolean)
	 * @see #setAutoCreate(boolean)
	 */
	public AdjacencyListGraph( String id, boolean strictChecking, boolean autoCreate )
	{
		super( id );
		setStrict( strictChecking );
		setAutoCreate( autoCreate );
		
		listeners  = new GraphListeners();
		
		nodeFactory = new NodeFactory()
		{
			public Node newInstance( String id, Graph graph )
			{
				return new AdjacencyListNode(graph,id);
			}
		};
		edgeFactory = new EdgeFactory()
		{
			public Edge newInstance( String id, Node src, Node trg, boolean directed )
			{
				return new AdjacencyListEdge(id,src,trg,directed);
			}
		};
	}
	
	@Override
	protected String getMyGraphId()
	{
		return getId();
	}

	public EdgeFactory edgeFactory()
	{
		return edgeFactory;
	}
	
	public void setEdgeFactory( EdgeFactory ef )
	{
		this.edgeFactory = ef;
	}

	public NodeFactory nodeFactory()
	{
		return nodeFactory;
	}
	
	public void setNodeFactory( NodeFactory nf )
	{
		this.nodeFactory = nf;
	}

	public Edge addEdge( String id, String node1, String node2 ) throws SingletonException, NotFoundException
	{
		return addEdge( id, node1, node2, false );
	}

	protected Edge addEdge_( String sourceId, String edgeId, String from, String to, boolean directed ) throws SingletonException, NotFoundException
	{
		Node src;
		Node trg;

		src =  lookForNode( from );
		trg =  lookForNode( to );

		if( src == null )
		{
			if( strictChecking )
			{
				throw new NotFoundException( "cannot make edge from '" + from + "' to '" + to + "' since node '" + from
						+ "' is not part of this graph" );
			}
			else if( autoCreate )
			{
				src = addNode( from );
			}
		}

		if( trg == null )
		{
			if( strictChecking )
			{
				throw new NotFoundException( "cannot make edge from '" + from + "' to '" + to + "' since node '" + to
						+ "' is not part of this graph" );
			}
			else if( autoCreate )
			{
				trg = addNode( to );
			}
		}

		if( src != null && trg != null )
		{
			Edge edge = null;
			Edge old = lookForEdge( edgeId );
			if( old != null )
			{
				if( strictChecking )
				{
					throw new SingletonException( "id '" + edgeId + "' already used, cannot add edge" );
				}
				else
				{
					edge = old;
				}
			}
			else
			{
				if( ( (AdjacencyListNode) src ).hasEdgeToward( trg ) != null )
				{
					throw new SingletonException( "Cannot add edge between " + from + " and " + to + ". A link already exists." );
				}
				else
				{
					edge = edgeFactory.newInstance(edgeId,src,trg,directed);
					//edge.setDirected( directed );
					
					edges.put( edgeId,edge );
					((AdjacencyListNode)src).edges.add( edge );
					((AdjacencyListNode)trg).edges.add( edge );
					listeners.sendEdgeAdded( sourceId, edgeId, from, to, directed );
				}
			}
			return edge;
		}

		return null;
	}

	/**
	 * @complexity O(log(n)) with n be the number of edges in the graph.
	 */
	public Edge addEdge( String id, String from, String to, boolean directed ) throws SingletonException, NotFoundException
	{
		Edge e = addEdge_( getId(), id, from, to, directed );
		return e;
	}

	/**
	 * @complexity O(log(n)) with n be the number of nodes in the graph.
	 */
	public Node addNode( String id ) throws SingletonException
	{
		Node n = addNode_( getId(), id );
		
		return n;
	}

	protected Node addNode_( String sourceId, String nodeId ) throws SingletonException
	{
		Node node;
		Node old = lookForNode( nodeId );
		if( old != null )
		{
			if( strictChecking )
			{
				throw new SingletonException( "id '" + nodeId + "' already used, cannot add node" );
			}
			else
			{
				node = old;
			}
		}
		else
		{
			node = nodeFactory.newInstance(nodeId,this);
			
			nodes.put(nodeId, node );
			listeners.sendNodeAdded( sourceId, nodeId );
		}

		return node;
	}

	/**
	 * @complexity O(1).
	 */
	public void clear()
	{
		clear_( getId() );
	}
	
	protected void clear_( String sourceId )
	{
		listeners.sendGraphCleared( sourceId );

		nodes.clear();
		edges.clear();
	}

	/**
	 * @complexity O(1).
	 */
	public void clearListeners()
	{
		listeners.clearListeners();
	}

	/**
	 * @complexity O(log(n)) with n be the number of edges in the graph.
	 */
	public Edge getEdge( String id )
	{
		return lookForEdge(id);
	}

	/**
	 * @complexity constant
	 */
	public int getEdgeCount()
	{
		return edges.size();
	}

	/**
	 * @complexity constant
	 */
	public Iterator<Edge> getEdgeIterator()
	{
		return new EdgeIterator();
	}

	/**
	 * @complexity Constant.
	 */
	public Iterable<Edge> edgeSet()
	{
		return edges.values();
	}

	/**
	 * @complexity O(log(n)) with n be the number of nodes in the graph.
	 */
	public Node getNode( String id )
	{
		return lookForNode( id );
	}

	/**
	 * @complexity Constant.
	 */
	public int getNodeCount()
	{
		return nodes.size();
	}

	/**
	 * @complexity Constant.
	 */
	public Iterator<Node> getNodeIterator()
	{
		return new NodeIterator();
	}
	
	public Iterator<Node> iterator()
	{
		return new NodeIterator();
	}

	/**
	 * @complexity Constant.
	 */
	public Iterable<Node> nodeSet()
	{
		return nodes.values();
	}

	public boolean isAutoCreationEnabled()
	{
		return autoCreate;
	}

	public boolean isStrict()
	{
		return strictChecking;
	}

	public Iterable<GraphAttributesListener> getGraphAttributesListeners()
	{
		return listeners.graphAttributesListeners();
	}
	
	public Iterable<GraphElementsListener> getGraphElementsListeners()
	{
		return listeners.graphElementsListeners();
	}
	
	public double getStep()
	{
		return step;
	}

	/**
	 * @complexity O( 2*log(n)+log(m) ) with n the number of nodes and m the number of edges in the graph.
	 */
	public Edge removeEdge( String from, String to ) throws NotFoundException
	{
		return removeEdge_( getId(), from, to );
	}
	
	protected Edge removeEdge_( String sourceId, String from, String to )
	{
		Node n0 = lookForNode( from );
		Node n1 = lookForNode( to );

		if( n0 != null && n1 != null )
		{
			Edge e = ( (AdjacencyListNode) n0 ).hasEdgeToward( n1 );
			if( e != null )
			{
				return removeEdge_( sourceId, e);
			}
			else
			{
				e = ( (AdjacencyListNode) n0 ).hasEdgeToward( n1 );
				if( e != null )
				{
					return removeEdge_( sourceId, e);
				}
			}
		}
		return null;
	}

	/**
	 * @complexity O( 2*log(m) ) with  m the number of edges in the graph.
	 */
	public Edge removeEdge( String id ) throws NotFoundException
	{
		Edge edge = lookForEdge( id );
		
		if( edge != null )
			removeEdge_( getId(), edge );
		
		return edge;
	}

	/**
	 * Removes an edge from a given reference to it.
	 * @param edge The reference of the edge to remove.
	 * @complexity O( log(m) ) with  m the number of edges in the graph.
	 */
	public Edge removeEdge( Edge edge ) throws NotFoundException
	{
		return removeEdge_( getId(), edge );
	}
	
	protected Edge removeEdge_( String sourceId, Edge edge )
	{
		listeners.sendEdgeRemoved( sourceId, edge.getId() );
		
		Node n0 = edge.getSourceNode();
		Node n1 = edge.getTargetNode();
		
		( (AdjacencyListNode) n0 ).edges.remove( edge );
		( (AdjacencyListNode) n1 ).edges.remove( edge );
		edges.remove( edge.getId() );
	
		return edge;
	}

	/**
	 * @complexity 0( 2*log(n) ) with n the number of nodes in the graph.
	 */
	public Node removeNode( String id ) throws NotFoundException
	{
		Node node = lookForNode( id );
		if( node != null )
		{
			return removeNode_( getId(), node );
		}
		return null;
	}

	/**
	 * Remove a node form a given reference of it.
	 * @param node The reference of the node to be removed.
	 * @complexity 0( log(n) ) with n the number of nodes in the graph.
	 */
	public Node removeNode( Node node ) throws NotFoundException
	{
		return removeNode_( getId(), node );
	}
	
	protected Node removeNode_( String sourceId, Node node )
	{
		if( node != null )
		{
			disconnectEdges( node );
			listeners.sendNodeRemoved( sourceId, node.getId() );
			nodes.remove( node.getId() );

			return node;
		}

		if( strictChecking )
			throw new NotFoundException( "node not found, cannot remove" );

		return null;
	}

	public void stepBegins( double time )
	{
		stepBegins_( getId(), time );
	}
	
	protected void stepBegins_( String sourceId, double time )
	{
		step = time;
		
		listeners.sendStepBegins( sourceId, time );
	}
	
	/**
	 * When a node is unregistered from a graph, it must not keep edges
	 * connected to nodes still in the graph. This methods unbind all edges
	 * connected to this node (this also unregister them from the graph).
	 */
	protected void disconnectEdges( Node node ) throws IllegalStateException
	{
		int n = node.getDegree();

		// We cannot use a "for" since unbinding an edge removes this edge from
		// the node. The number of edges will change continuously.

		while( n > 0 )
		{
			Edge e = ((AdjacencyListNode)node).edges.get( 0 );
			removeEdge( e );
			n = node.getDegree(); //edges.size(); ???
		}
	}

	public void setAutoCreate( boolean on )
	{
		autoCreate = on;
	}

	public void setStrict( boolean on )
	{
		strictChecking = on;
	}

	/**
	 * Tries to retrieve a node in the internal structure identified by the given string.
	 * @param id The string identifier of the seek node.
	 * @complexity 0( log(n) ), with n the number of nodes;
	 */
	protected Node lookForNode( String id )
	{
		return nodes.get( id );
	}

	/**
	 * 
	 * Tries to retrieve an edge in the internal structure identified by the given string.
	 * @param id The string identifier of the seek edges.
	 * @complexity 0( log(m) ), with m the number of edges;
	 */
	protected Edge lookForEdge( String id )
	{
		return edges.get( id );
	}

	
// Events

	public void addGraphListener( GraphListener listener )
	{
		listeners.addGraphListener( listener );
	}
	
	public void addGraphAttributesListener( GraphAttributesListener listener )
	{
		listeners.addGraphAttributesListener( listener );
	}
	
	public void addGraphElementsListener( GraphElementsListener listener )
	{
		listeners.addGraphElementsListener( listener );
	}
	
	public void removeGraphListener( GraphListener listener )
	{
		listeners.removeGraphListener( listener );
	}
	
	public void removeGraphAttributesListener( GraphAttributesListener listener )
	{
		listeners.removeGraphAttributesListener( listener );
	}
	
	public void removeGraphElementsListener( GraphElementsListener listener )
	{
		listeners.removeGraphElementsListener( listener );
	}

	@Override
	protected void attributeChanged( String sourceId, String attribute, AttributeChangeEvent event, Object oldValue, Object newValue )
	{
		listeners.sendAttributeChangedEvent( sourceId, getId(),
				ElementType.GRAPH, attribute, event, oldValue, newValue );
	}

	public void graphCleared()
    {
		clear_( getId() );
    }

// Commands -- Utility

	public void read( FileSource input, String filename ) throws IOException, GraphParseException
    {
		input.readAll( filename );
    }

	public void read( String filename )
		throws IOException, GraphParseException, NotFoundException
	{
		FileSource input = FileSourceFactory.inputFor( filename );
		input.addGraphListener( this );
		read( input, filename );
	}
	
	public void write( FileSink output, String filename ) throws IOException
    {
		output.writeAll( this, filename );
    }
	
	public void write( String filename )
		throws IOException
	{
		FileSink output = FileSinkFactory.outputFor( filename );
		write( output, filename );
	}	

	public GraphViewerRemote display()
	{
		return display( true );
	}

	public GraphViewerRemote display( boolean autoLayout )
	{
		try
        {
			Class<?> clazz = Class.forName( "org.miv.graphstream.ui.swing.SwingGraphViewer" );
	        Object object = clazz.newInstance();
	        
	        if( object instanceof GraphViewer )
	        {
	        	GraphViewer gv = (GraphViewer) object;
	        	
	        	gv.open(  this, autoLayout );
	        	
	        	return gv.newViewerRemote();
	        }
	        else
	        {
	        	System.err.printf( "not a GraphViewer\n", object == null ? "" : object.getClass().getName() );
	        }
        }
        catch( ClassNotFoundException e )
        {
	        e.printStackTrace();
        	System.err.printf( "Cannot display graph, GraphViewer class not found : " + e.getMessage() );
        }
        catch( IllegalAccessException e )
        {
        	e.printStackTrace();
        }
        catch( InstantiationException e )
        {
        	e.printStackTrace();
        }
        
        return null;
	}

// Output

	protected boolean notMyEvent( String sourceId )
	{
		return notMyId1( sourceId );
	}

	protected String extendsSourceId( String sourceId )
	{
		// We know that our ID is not yet in the sourceId.
		return String.format( "%s,%s", sourceId, getId() );
	}

	protected boolean notMyId1( String sourceId )
	{
		String ids[] = sourceId.split( "," );
		String myId = getId();
		for( String id: ids )
			if( id.equals( myId ) )
				return false;
		
		return true;
	}
	
	public void edgeAdded( String sourceId, long timeId, String edgeId, String fromNodeId, String toNodeId,
            boolean directed )
    {
		listeners.edgeAdded(sourceId, timeId, edgeId, fromNodeId, toNodeId, directed);
    }

	public void edgeRemoved( String sourceId, long timeId, String edgeId )
    {
		listeners.edgeRemoved(sourceId, timeId, edgeId);
    }

	public void graphCleared( String sourceId, long timeId )
    {
		listeners.graphCleared(sourceId, timeId);
    }

	public void nodeAdded( String sourceId, long timeId, String nodeId )
    {
		listeners.nodeAdded(sourceId, timeId, nodeId);
    }

	public void nodeRemoved( String sourceId, long timeId, String nodeId )
    {
		listeners.nodeRemoved(sourceId, timeId, nodeId);
    }

	public void stepBegins( String sourceId, long timeId, double step )
    {
		listeners.stepBegins(sourceId, timeId, step);
    }

	public void edgeAttributeAdded( String sourceId, long timeId, String edgeId, String attribute, Object value )
    {
		listeners.edgeAttributeAdded(sourceId, timeId, edgeId, attribute, value);
    }

	public void edgeAttributeChanged( String sourceId, long timeId, String edgeId, String attribute,
            Object oldValue, Object newValue )
    {
		listeners.edgeAttributeChanged(sourceId, timeId, edgeId, attribute, oldValue, newValue);
    }

	public void edgeAttributeRemoved( String sourceId, long timeId, String edgeId, String attribute )
    {
		listeners.edgeAttributeRemoved(sourceId, timeId, edgeId, attribute);
    }

	public void graphAttributeAdded( String sourceId, long timeId, String attribute, Object value )
    {
		listeners.graphAttributeAdded(sourceId, timeId, attribute, value);
    }

	public void graphAttributeChanged( String sourceId, long timeId, String attribute, Object oldValue,
            Object newValue )
    {
		listeners.graphAttributeChanged(sourceId, timeId, attribute, oldValue, newValue);
    }

	public void graphAttributeRemoved( String sourceId, long timeId, String attribute )
    {
		listeners.graphAttributeRemoved(sourceId, timeId, attribute);
    }

	public void nodeAttributeAdded( String sourceId, long timeId, String nodeId, String attribute, Object value )
    {
		listeners.nodeAttributeAdded(sourceId, timeId, nodeId, attribute, value);
    }

	public void nodeAttributeChanged( String sourceId, long timeId, String nodeId, String attribute,
            Object oldValue, Object newValue )
    {
		listeners.nodeAttributeChanged(sourceId, timeId, nodeId, attribute, oldValue, newValue);
    }

	public void nodeAttributeRemoved( String sourceId, long timeId, String nodeId, String attribute )
    {
		listeners.nodeAttributeRemoved(sourceId, timeId, nodeId, attribute);
    }

	// Handling the listeners -- We use the IO2 InputBase for this.
		
	class GraphListeners
		extends SourceBase
		implements Pipe
	{
		SinkTime sinkTime;

		public GraphListeners()
		{
			super( getId() );

			sinkTime = new SinkTime();
			sourceTime.setSinkTime(sinkTime);
		}

		public void edgeAttributeAdded(String sourceId, long timeId,
				String edgeId, String attribute, Object value) {
			if( sinkTime.isNewEvent(sourceId, timeId) )
			{
				Edge edge = getEdge( edgeId );
				
				if( edge != null )
					((AdjacencyListEdge)edge).addAttribute_( extendsSourceId( sourceId ), attribute, value );
			}
		}

		public void edgeAttributeChanged(String sourceId, long timeId,
				String edgeId, String attribute, Object oldValue,
				Object newValue) {
			if( sinkTime.isNewEvent(sourceId, timeId) )
			{
				Edge edge = getEdge( edgeId );
				
				if( edge != null )
					((AdjacencyListEdge)edge).changeAttribute_( extendsSourceId( sourceId ), attribute, newValue );
			}
		}

		public void edgeAttributeRemoved(String sourceId, long timeId,
				String edgeId, String attribute) {
			if( sinkTime.isNewEvent(sourceId, timeId) )
			{
				Edge edge = getEdge( edgeId );
				
				if( edge != null )
					((AdjacencyListEdge)edge).removeAttribute_( extendsSourceId( sourceId ), attribute );
			}
		}

		public void graphAttributeAdded(String sourceId, long timeId,
				String attribute, Object value) {
			if( sinkTime.isNewEvent(sourceId, timeId) )
			{
				addAttribute_( sourceId, attribute, value );
			}
		}

		public void graphAttributeChanged(String sourceId, long timeId,
				String attribute, Object oldValue, Object newValue) {
			if( sinkTime.isNewEvent(sourceId, timeId) )
			{
				changeAttribute_( sourceId, attribute, newValue );
			}
		}

		public void graphAttributeRemoved(String sourceId, long timeId,
				String attribute) {
			if( sinkTime.isNewEvent(sourceId, timeId) )
			{
				removeAttribute_( sourceId, attribute );
			}
		}

		public void nodeAttributeAdded(String sourceId, long timeId,
				String nodeId, String attribute, Object value) {
			if( sinkTime.isNewEvent(sourceId, timeId) )
			{
				Node node = getNode( nodeId );
				
				if( node != null )
					((AdjacencyListNode)node).addAttribute_( extendsSourceId( sourceId ), attribute, value );
			}
		}

		public void nodeAttributeChanged(String sourceId, long timeId,
				String nodeId, String attribute, Object oldValue,
				Object newValue) {
			if( sinkTime.isNewEvent(sourceId, timeId) )
			{
				Node node = getNode( nodeId );
				
				if( node != null )
					((AdjacencyListNode)node).changeAttribute_( extendsSourceId( sourceId ), attribute, newValue );
			}
		}

		public void nodeAttributeRemoved(String sourceId, long timeId,
				String nodeId, String attribute) {
			if( sinkTime.isNewEvent(sourceId, timeId) )
			{
				Node node = getNode( nodeId );

				if( node != null )
					((AdjacencyListNode)node).removeAttribute_( extendsSourceId( sourceId ), attribute );
			}
		}

		public void edgeAdded(String sourceId, long timeId, String edgeId,
				String fromNodeId, String toNodeId, boolean directed) {
			if( sinkTime.isNewEvent(sourceId, timeId) )
			{
				addEdge_( extendsSourceId( sourceId ), edgeId, fromNodeId, toNodeId, directed );
			}
		}

		public void edgeRemoved(String sourceId, long timeId, String edgeId) {
			if( sinkTime.isNewEvent(sourceId, timeId) )
			{
				Edge e = getEdge( edgeId );
				
				if( e != null )
					removeEdge_( extendsSourceId( sourceId ), getEdge( edgeId ) );
			}
		}

		public void graphCleared(String sourceId, long timeId) {
			if( sinkTime.isNewEvent(sourceId, timeId) )
			{
				clear_( extendsSourceId( sourceId ) );
			}
		}

		public void nodeAdded(String sourceId, long timeId, String nodeId) {
			if( sinkTime.isNewEvent(sourceId, timeId) )
			{
				addNode_( extendsSourceId( sourceId ), nodeId );
			}
		}

		public void nodeRemoved(String sourceId, long timeId, String nodeId) {
			if( sinkTime.isNewEvent(sourceId, timeId) )
			{
				Node n = getNode( nodeId );
				
				if( n != null )
					removeNode_( extendsSourceId( sourceId ), n );
			}
		}

		public void stepBegins(String sourceId, long timeId, double step) {
			if( sinkTime.isNewEvent(sourceId, timeId) )
			{
				stepBegins_( extendsSourceId( sourceId ), step );
			}
		}
	}
}