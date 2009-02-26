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

package org.miv.graphstream.graph.implementations;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;

//import org.miv.graphstream.algorithm.Algorithms;
import org.miv.graphstream.graph.Edge;
import org.miv.graphstream.graph.EdgeFactory;
import org.miv.graphstream.graph.Element;
import org.miv.graphstream.graph.Graph;
import org.miv.graphstream.graph.GraphListener;
import org.miv.graphstream.graph.Node;
import org.miv.graphstream.graph.NodeFactory;
import org.miv.graphstream.io.GraphParseException;
import org.miv.graphstream.io.GraphReader;
import org.miv.graphstream.io.GraphReaderFactory;
import org.miv.graphstream.io.GraphReaderListenerHelper;
import org.miv.graphstream.io.GraphWriter;
import org.miv.graphstream.io.GraphWriterHelper;
import org.miv.graphstream.ui.GraphViewer;
import org.miv.graphstream.ui.GraphViewerRemote;
import org.miv.util.NotFoundException;
import org.miv.util.SingletonException;

/**
 * <p>
 * A light graph class intended to allow the construction of big graphs
 * (millions of elements).
 * </p>
 * <p>
 * The main purpose here is to minimize memory consumption even if the
 * management of such a graph implies more CPU consuming. See the
 * <code>complexity</code> tags on each method so as to figure out the impact
 * on the CPU.
 * </p>
 * 
 * @since July 12 2007
 */
public class AdjacencyListGraph 
	extends AbstractElement 
	implements Graph
{
//	protected Algorithms algos;
	
	public class EdgeIterator 
		implements 
			Iterator<Edge>
	{

		Iterator<Edge> edgeIterator;
		
		public EdgeIterator()
		{
			edgeIterator = edges.values().iterator();
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext()
		{
			return edgeIterator.hasNext();
		}

		/*
		 * (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		public Edge next()
		{
			return edgeIterator.next();
		}

		/*
		 * (non-Javadoc)
		 * @see java.util.Iterator#remove()
		 */
		public void remove()
		{
			throw new UnsupportedOperationException( "this iterator does not allow removing" );
		}

	}

	
	public class NodeIterator 
		implements 
			Iterator<Node>
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

		/* (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		public Node next()
		{
			return  nodeIterator.next();
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#remove()
		 */
		public void remove()
		{
			throw new UnsupportedOperationException( "this iterator does not allow removing" );
		}

	}

	protected HashMap<String,Node> nodes = new HashMap<String, Node>();

	protected HashMap<String,Edge> edges = new HashMap<String, Edge>();
	
	/**
	 * A queue that allow the management of events (nodes/edge add/delete/change) in the right order. 
	 */
	protected LinkedList<GraphEvent> eventQueue = new LinkedList<GraphEvent>();
	
	/**
	 * A boolean that indicates whether or not an GraphListener event is being sent during another one. 
	 */
	protected boolean eventProcessing=false;


	/**
	 * Set of graph listeners.
	 */
	protected ArrayList<GraphListener> listeners = new ArrayList<GraphListener>();

	/**
	 * Verify namespace conflicts, removal of non-existing elements, use of
	 * non-existing elements.
	 */
	protected boolean strictChecking = true;

	/**
	 * Automatically create missing elements. For example, if an edge is created
	 * between two non-existing nodes, create the nodes.
	 */
	protected boolean autoCreate = false;

	
	/**
	 *  Helpfull class that dynamicaly instantiate nodes according to a given class name.
	 */
	protected NodeFactory nodeFactory;
	
	/**
	 *  Helpfull class that dynamicaly instantiate edges according to a given class name.
	 */
	protected EdgeFactory edgeFactory;
	
	/**
	 * List of listeners to remove if the {@link #removeGraphListener(GraphListener)} is called
	 * inside from the listener. This can happen !! We create this list on demand.
	 */
	protected ArrayList<GraphListener> listenersToRemove;

	
// Constructors

	/**
	 * New empty graph, with the empty string as default identifier.
	 * @see #AdjacencyListGraph(String)
	 * @see #AdjacencyListGraph(boolean, boolean)
	 * @see #AdjacencyListGraph(String, boolean, boolean) 
	 */
	public AdjacencyListGraph()
	{
		this( "" );
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
	 * @see #setStrictChecking(boolean)
	 * @see #setAutoCreate(boolean)
	 */
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
	 * @see #setStrictChecking(boolean)
	 * @see #setAutoCreate(boolean)
	 */
	public AdjacencyListGraph( String id, boolean strictChecking, boolean autoCreate )
	{
		super( id );
		setStrictChecking( strictChecking );
		setAutoCreate( autoCreate );
		
		nodeFactory = new NodeFactory()
		{
			public Node newInstance( String id, Graph graph )
			{
				return new AdjacencyListNode(graph,id);
			}
		};
		edgeFactory = new EdgeFactory()
		{
			public Edge newInstance( String id, Node src, Node trg )
			{
				return new AdjacencyListEdge(id,src,trg);
			}
		};
	}

	
	/* (non-Javadoc)
	 * @see org.miv.graphstream.graph.Graph#edgeFactory()
	 */
	public EdgeFactory edgeFactory()
	{
		return edgeFactory;
	}
	
	/* (non-Javadoc)
	 * @see org.miv.graphstream.graph.Graph#edgeFactory()
	 */
	public void setEdgeFactory( EdgeFactory ef )
	{
		this.edgeFactory = ef;
	}

	/* (non-Javadoc)
	 * @see org.miv.graphstream.graph.Graph#nodeFactory()
	 */
	public NodeFactory nodeFactory()
	{
		return nodeFactory;
	}
	
	/* (non-Javadoc)
	 * @see org.miv.graphstream.graph.Graph#edgeFactory()
	 */
	public void setNodeFactory( NodeFactory nf )
	{
		this.nodeFactory = nf;
	}

	/*
	 * (non-Javadoc)
	 * @see org.miv.graphstream.graph.GraphInterface#addEdge(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	  
	/**
	 * @complexity O( log(m) ) with m be the number of edges in the graph.
	 */
	public Edge 
	addEdge( String id, String node1, String node2 ) throws SingletonException, NotFoundException
	{
		return addEdge( id, node1, node2, false );
	}

	protected Edge 
	addEdge_( String tag, String from, String to, boolean directed ) throws SingletonException, NotFoundException
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
			Edge old = lookForEdge( tag );
			if( old != null )
			{
				if( strictChecking )
				{
					throw new SingletonException( "id '" + tag + "' already used, cannot add edge" );
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
					edge = edgeFactory.newInstance(tag,src,trg);
					edge.setDirected( directed );
					
					edges.put( tag,edge );
					((AdjacencyListNode)src).edges.add( edge );
					((AdjacencyListNode)trg).edges.add( edge );
					afterEdgeAddEvent( (AdjacencyListEdge) edge );
				}
			}
			return edge;
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.miv.graphstream.graph.GraphInterface#addEdge(java.lang.String,
	 *      java.lang.String, java.lang.String, boolean)
	 */
	/**
	 * @complexity O(log(n)) with n be the number of edges in the graph.
	 */
	public Edge 
	addEdge( String id, String from, String to, boolean directed ) throws SingletonException, NotFoundException
	{
		Edge e = addEdge_( id, from, to, directed );
		return e;
	}

	/*
	 * (non-Javadoc)
	 * @see org.miv.graphstream.graph.GraphInterface#addNode(java.lang.String)
	 */
	/**
	 * @complexity O(log(n)) with n be the number of nodes in the graph.
	 */
	public Node 
	addNode( String id ) throws SingletonException
	{
		Node n = addNode_( id );
		
		return n;
	}

	protected Node 
	addNode_( String tag ) throws SingletonException
	{
		Node node;
		Node old = lookForNode( tag );
		if( old != null )
		{
			if( strictChecking )
			{
				throw new SingletonException( "id '" + tag + "' already used, cannot add node" );
			}
			else
			{
				node = old;
			}
		}
		else
		{
			node = nodeFactory.newInstance(tag,this);
			
			nodes.put(tag, node );
			afterNodeAddEvent( (AdjacencyListNode) node);
		}

		return node;
	}

	/*
	 * (non-Javadoc)
	 * @see org.miv.graphstream.graph.GraphInterface#clear()
	 */
	/**
	 * @complexity O(1).
	 */
	
	public void 
	clear()
	{
		for( String n: nodes.keySet() )
		{
			removeNode( n );
		}

	}

	/*
	 * (non-Javadoc)
	 * @see org.miv.graphstream.graph.GraphInterface#clearListeners()
	 */
	/**
	 * @complexity O(1).
	 */
	public void 
	clearListeners()
	{
		listeners.clear();
	}

	/*
	 * (non-Javadoc)
	 * @see org.miv.graphstream.graph.GraphInterface#getEdge(java.lang.String)
	 */
	/**
	 * @complexity O(log(n)) with n be the number of edges in the graph.
	 */
	public Edge 
	getEdge( String id )
	{
		return lookForEdge(id);
	}

	/*
	 * (non-Javadoc)
	 * @see org.miv.graphstream.graph.GraphInterface#getEdgeCount()
	 */
	/**
	 * @complexity constant
	 */
	public int 
	getEdgeCount()
	{
		return edges.size();
	}

	/*
	 * (non-Javadoc)
	 * @see org.miv.graphstream.graph.GraphInterface#getEdgeIterator()
	 */
	/**
	 * @complexity constant
	 */
	public Iterator<Edge> 
	getEdgeIterator()
	{
		return new EdgeIterator();
	}

	/*
	 * (non-Javadoc)
	 * @see org.miv.graphstream.graph.GraphInterface#getEdgeSet()
	 */
	/**
	 * @complexity Constant.
	 */
	public Collection<Edge> 
	getEdgeSet()
	{
		
		return edges.values();
	}

	/*
	 * (non-Javadoc)
	 * @see org.miv.graphstream.graph.GraphInterface#getNode(java.lang.String)
	 */
	/**
	 * @complexity O(log(n)) with n be the number of nodes in the graph.
	 */
	public Node 
	getNode( String id )
	{
		return lookForNode( id );
	}

	/*
	 * (non-Javadoc)
	 * @see org.miv.graphstream.graph.GraphInterface#getNodeCount()
	 */
	/**
	 * @complexity Constant.
	 */
	public int 
	getNodeCount()
	{
		return nodes.size();
	}

	/*
	 * (non-Javadoc)
	 * @see org.miv.graphstream.graph.GraphInterface#getNodeIterator()
	 */
	/**
	 * @complexity Constant.
	 */
	public Iterator<Node> 
	getNodeIterator()
	{
		return new NodeIterator();
	}

	/*
	 * (non-Javadoc)
	 * @see org.miv.graphstream.graph.GraphInterface#getNodeSet()
	 */
	/**
	 * @complexity Constant.
	 */
	public Collection<Node> 
	getNodeSet()
	{
		return nodes.values();
	}

	/*
	 * (non-Javadoc)
	 * @see org.miv.graphstream.graph.GraphInterface#isAutoCreationEnabled()
	 */
	public boolean 
	isAutoCreationEnabled()
	{
		return autoCreate;
	}

	/*
	 * (non-Javadoc)
	 * @see org.miv.graphstream.graph.GraphInterface#isStrictCheckingEnabled()
	 */
	public boolean 
	isStrictCheckingEnabled()
	{
		return strictChecking;
	}

	/* (non-Javadoc)
	 * @see org.miv.graphstream.graph.Graph#getGraphListeners()
	 */
	public List<GraphListener> getGraphListeners()
	{
		return listeners;
	}

	/*
	 * (non-Javadoc)
	 * @see org.miv.graphstream.graph.GraphInterface#removeEdge(java.lang.String,
	 *      java.lang.String)
	 */
	/**
	 * @complexity O( 2*log(n)+log(m) ) with n the number of nodes and m the number of edges in the graph.
	 */
	public Edge 
	removeEdge( String from, String to ) throws NotFoundException
	{
		Node n0 = lookForNode( from );
		Node n1 = lookForNode( to );
		if( n0 != null && n1 != null )
		{
			Edge e = ( (AdjacencyListNode) n0 ).hasEdgeToward( n1 );
			if( e != null )
			{
				return removeEdge(e);
			}
			else
			{
				e = ( (AdjacencyListNode) n0 ).hasEdgeToward( n1 );
				if( e != null )
				{
					return removeEdge(e);
				}
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.miv.graphstream.graph.GraphInterface#removeEdge(java.lang.String)
	 */
	/**
	 * @complexity O( 2*log(m) ) with  m the number of edges in the graph.
	 */
	public Edge 
	removeEdge( String id ) throws NotFoundException
	{
		Edge edge = lookForEdge( id );
		if( edge != null )
		{
			removeEdge( edge );
		}
		return null;
	}

	/**
	 * Removes an edge from a given reference to it.
	 * @param edge The reference of the edge to remove.
	 * @complexity O( log(m) ) with  m the number of edges in the graph.
	 */
	public Edge 
	removeEdge( Edge edge ) throws NotFoundException
	{
		beforeEdgeRemoveEvent( (AdjacencyListEdge) edge );
		Node n0 = edge.getSourceNode();
		Node n1 = edge.getTargetNode();
		( (AdjacencyListNode) n0 ).edges.remove( edge );
		( (AdjacencyListNode) n1 ).edges.remove( edge );
		edges.remove( edge.getId() );
		return edge;

	}

	/*
	 * (non-Javadoc)
	 * @see org.miv.graphstream.graph.GraphInterface#removeNode(java.lang.String)
	 */
	/**
	 * @complexity 0( 2*log(n) ) with n the number of nodes in the graph.
	 */
	public Node 
	removeNode( String id ) throws NotFoundException
	{
		Node node = lookForNode( id );
		if( node != null )
		{
			return removeNode( node );
		}
		return null;
	}

	/**
	 * Remove a node form a given reference of it.
	 * @param node The reference of the node to be removed.
	 * @complexity 0( log(n) ) with n the number of nodes in the graph.
	 */
	public Node 
	removeNode( Node node ) throws NotFoundException
	{
		if( node != null )
		{
			disconnectEdges( node );
			beforeNodeRemoveEvent( (AdjacencyListNode) node );
			nodes.remove( node.getId() );

			return node;
		}

		if( strictChecking )
			throw new NotFoundException( "node not found, cannot remove" );

		return null;
	}

	public void stepBegins(double time)
	{
		for (GraphListener l : listeners)
			l.stepBegins(this, time);
	}
	
	/**
	 * When a node is deregistered from a graph, it must not keep edges
	 * connected to nodes still in the graph. This methods unbind all edges
	 * connected to this node (this also deregister them from the graph).
	 */
	protected void 
	disconnectEdges( Node node ) throws IllegalStateException
	{
		int n = node.getDegree();

		// We cannot use a "for" since unbinding an edge removes this edge from
		// the node. The number of edges will change continuously.

		while( n > 0 )
		{
			Edge e = ( (AdjacencyListNode) node ).edges.get( 0 );
			removeEdge( e );
			n = edges.size();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.miv.graphstream.graph.GraphInterface#setAutoCreate(boolean)
	 */
	public void 
	setAutoCreate( boolean on )
	{
		autoCreate = on;
	}

	/*
	 * (non-Javadoc)
	 * @see org.miv.graphstream.graph.GraphInterface#setStrictChecking(boolean)
	 */
	public void 
	setStrictChecking( boolean on )
	{
		strictChecking = on;
	}

	/**
	 * Tries to retrieve a node in the internal structure identified by the given string.
	 * @param id The string identifier of the seek node.
	 * @complexity 0( log(n) ), with n the number of nodes;
	 */
	protected Node 
	lookForNode( String id )
	{
		// for( NodeInterface n: nodes )
		// {
		// if( ( (ElementInterface) n ).getId().equals( id ) )
		// {
		// return n;
		// }
		// }
		// return null;
		return nodes.get( id );
	}

	/**
	 * 
	 * Tries to retrieve an edge in the internal structure identified by the given string.
	 * @param id The string identifier of the seek edges.
	 * @complexity 0( log(m) ), with m the number of edges;
	 */
	protected Edge 
	lookForEdge( String id )
	{
		// for( EdgeInterface e: edges )
		// {
		// if( ( (ElementInterface) e ).getId().equals( id ) )
		// {
		// return e;
		// }
		// }
		// return null;
		return edges.get( id );
	}

	
	// Events

	/*
	 * (non-Javadoc)
	 * @see org.miv.graphstream.graph.GraphInterface#addGraphListener(org.miv.graphstream.graph.GraphListener)
	 */
	/**
	 * @complexity 0( 1).
	 */
	public void 
	addGraphListener( GraphListener listener )
	{
		listeners.add( listener );
	}

	/*
	 * (non-Javadoc)
	 * @see org.miv.graphstream.graph.GraphInterface#removeGraphListener(org.miv.graphstream.graph.GraphListener)
	 */
	/**
	 * @complexity O(n) with n the numbers of listeners.
	 */
	public void 
	removeGraphListener( GraphListener listener )
	{
		if( eventProcessing )
		{
			// We cannot remove the listener while processing events !!!

			if( listenersToRemove == null )
				listenersToRemove = new ArrayList<GraphListener>();
			
			listenersToRemove.add( listener );
		}
		else
		{
			int index = listeners.lastIndexOf( listener );

			if( index >= 0 )
				listeners.remove( index );
		}
	}
	
	protected void checkListenersToRemove()
	{
		if( listenersToRemove != null && listenersToRemove.size() > 0 )
		{
			for( GraphListener listener: listenersToRemove )
				removeGraphListener( listener );

			listenersToRemove.clear();
			listenersToRemove = null;
		}
	}

	protected void
	afterNodeAddEvent( AdjacencyListNode node )
	{
		if(!eventProcessing)
		{
			eventProcessing=true;
			for( GraphListener l: listeners )
				l.afterNodeAdd( this, node );
			while(!eventQueue.isEmpty())
			{
				manageEvent(eventQueue.remove());
			}
			eventProcessing=false;
			checkListenersToRemove();
		}
		else 
		{
			eventQueue.add( new AfterNodeAddEvent(node) );
		}
	}

	protected void
	beforeNodeRemoveEvent( AdjacencyListNode node )
	{
		if(!eventProcessing)
		{
			eventProcessing=true;
			for( GraphListener l: listeners )
				l.beforeNodeRemove( this, node );
			while(!eventQueue.isEmpty())
			{
				manageEvent(eventQueue.remove());
			}
			eventProcessing=false;
			checkListenersToRemove();
		}
		else 
		{
			eventQueue.add( new BeforeNodeRemoveEvent( node ) );
		}
	}

	protected void
	afterEdgeAddEvent( AdjacencyListEdge edge )
	{
		if(!eventProcessing)
		{
			eventProcessing=true;
			while(!eventQueue.isEmpty())
			{
				manageEvent(eventQueue.remove());
			}
			for( GraphListener l: listeners )
				l.afterEdgeAdd( this, edge );
			eventProcessing=false;
			checkListenersToRemove();
		}
		else 
		{
			eventQueue.add( new AfterEdgeAddEvent(edge) );
		}
	}


	protected void
	beforeEdgeRemoveEvent( AdjacencyListEdge edge )
	{
		if(!eventProcessing)
		{
			eventProcessing=true;
			while(!eventQueue.isEmpty())
			{
				manageEvent(eventQueue.remove());
			}
			for( GraphListener l: listeners )
				l.beforeEdgeRemove( this, edge );
			eventProcessing=false;
			checkListenersToRemove();
		}
		else {
			eventQueue.add( new BeforeEdgeRemoveEvent( edge ) );
		}
	}

	protected void
	beforeClearEvent()
	{
		if(!eventProcessing)
		{
			eventProcessing=true;
			while(!eventQueue.isEmpty())
			{
				manageEvent(eventQueue.remove());
			}
			for( GraphListener l: listeners )
				l.beforeGraphClear( this );
			eventProcessing=false;
			checkListenersToRemove();
		}
		else {
			eventQueue.add( new BeforeGraphClearEvent() );
		}
	}

	@Override
	protected void
	attributeChanged( String attribute, Object oldValue, Object newValue )
	{
		attributeChangedEvent( this, attribute, oldValue, newValue );
	}

	protected void
	attributeChangedEvent( Element element, String attribute, Object oldValue, Object newValue )
	{
		if(!eventProcessing)
		{
			eventProcessing=true;
			while(!eventQueue.isEmpty())
			{
				manageEvent(eventQueue.remove());
			}
			for( GraphListener l: listeners )
			{
				l.attributeChanged( element, attribute, oldValue, newValue );
		//System.out.println("Listener "+l+"  --  "+element.getId()+" "+attribute+"="+newValue);
			}
			eventProcessing=false;
			checkListenersToRemove();
		}
		else {
			eventQueue.add( new AttributeChangedEvent( element, attribute, oldValue, newValue ) );
		}
	}

// Commands -- Utility

	/*
	 * (non-Javadoc)
	 * @see org.miv.graphstream.graph.GraphInterface#read(java.lang.String)
	 */
	public void 
	read( String filename ) throws IOException, GraphParseException, NotFoundException
	{
		GraphReaderListenerHelper listener = new GraphReaderListenerHelper( this );
		GraphReader reader = GraphReaderFactory.readerFor( filename );
		reader.addGraphReaderListener( listener );
		reader.read( filename );
	}

	/*
	 * (non-Javadoc)
	 * @see org.miv.graphstream.graph.GraphInterface#read(org.miv.graphstream.io.GraphReader,
	 *      java.lang.String)
	 */
	public void 
	read( GraphReader reader, String filename ) throws IOException, GraphParseException
	{
		GraphReaderListenerHelper listener = new GraphReaderListenerHelper( this );
		reader.addGraphReaderListener( listener );
		reader.read( filename );
	}

	/*
	 * (non-Javadoc)
	 * @see org.miv.graphstream.graph.GraphInterface#write(java.lang.String)
	 */
	public void 
	write( String filename ) throws IOException
	{
		GraphWriterHelper gwh = new GraphWriterHelper( this );
		gwh.write( filename );
	}

	/*
	 * (non-Javadoc)
	 * @see org.miv.graphstream.graph.GraphInterface#write(org.miv.graphstream.io.GraphWriter,
	 *      java.lang.String)
	 */
	public void 
	write( GraphWriter writer, String filename ) throws IOException
	{
		GraphWriterHelper gwh = new GraphWriterHelper( this );
		gwh.write( filename, writer );
	}

	/*
	 * (non-Javadoc)
	 * @see org.miv.graphstream.graph.GraphInterface#readPositionFile(java.lang.String)
	 */
	public int 
	readPositionFile( String posFileName ) throws IOException
	{
		if( posFileName == null )
			throw new IOException( "no filename given" );

		Scanner scanner = new Scanner( new BufferedInputStream( new FileInputStream( posFileName ) ) );
		int ignored = 0;
		int mapped = 0;
		int line = 1;
		String id = null;
		float x = 0, y = 0, z = 0;

		scanner.useLocale( Locale.US );
		scanner.useDelimiter( "\\s|\\n|:" );

		try
		{
			while( scanner.hasNext() )
			{
				id = scanner.next();

				x = scanner.nextFloat();
				y = scanner.nextFloat();
				z = scanner.nextFloat();

				line++;

				Node node = lookForNode( id );

				if( node != null )
				{
					node.addAttribute( "x", x );
					node.addAttribute( "y", y );
					node.addAttribute( "z", z );
					mapped++;
				}
				else
				{
					ignored++;
				}
			}
		}
		catch( InputMismatchException e )
		{
			e.printStackTrace();
			throw new IOException( "parse error '" + posFileName + "':" + line + ": " + e.getMessage() );
		}
		catch( NoSuchElementException e )
		{
			throw new IOException( "unexpected end of file '" + posFileName + "':" + line + ": " + e.getMessage() );
		}
		catch( IllegalStateException e )
		{
			throw new IOException( "scanner error '" + posFileName + "':" + line + ": " + e.getMessage() );
		}

		scanner.close();

		return ignored;
	}

	/*
	 * (non-Javadoc)
	 * @see org.miv.graphstream.graph.GraphInterface#display()
	 */
	public GraphViewerRemote display()
	{
		return display( true );
	}

	/*
	 * (non-Javadoc)
	 * @see org.miv.graphstream.graph.GraphInterface#display(boolean)
	 */
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
/*	
	public Algorithms algorithm()
	{
		if( algos == null )
			algos = new Algorithms( this );
		
		return algos;
	}
*/
//-------------------------Events Management------------------------
	
	/**
	 * Interface that provide general purpose classification for evens involved
	 * in graph modifications
	 * @author Yoann Pign�
	 * 
	 */
	interface GraphEvent
	{
	}

	class AfterEdgeAddEvent 
		implements GraphEvent
	{
		AdjacencyListEdge edge;

		AfterEdgeAddEvent( AdjacencyListEdge edge )
		{
			this.edge = edge;
		}
	}

	class BeforeEdgeRemoveEvent 
		implements GraphEvent
	{
		AdjacencyListEdge edge;

		BeforeEdgeRemoveEvent( AdjacencyListEdge edge )
		{
			this.edge = edge;
		}
	}

	class AfterNodeAddEvent 
		implements GraphEvent
	{
		AdjacencyListNode node;

		AfterNodeAddEvent( AdjacencyListNode node )
		{
			this.node = node;
		}
	}

	class BeforeNodeRemoveEvent 
		implements GraphEvent
	{
		AdjacencyListNode node;

		BeforeNodeRemoveEvent( AdjacencyListNode node )
		{
			this.node = node;
		}
	}

	class BeforeGraphClearEvent 
		implements GraphEvent
	{
	}

	class AttributeChangedEvent 
		implements GraphEvent
	{
		Element element;

		String attribute;

		Object oldValue;

		Object newValue;

		AttributeChangedEvent( Element element, String attribute, Object oldValue, Object newValue )
		{
			this.element = element;
			this.attribute = attribute;
			this.oldValue = oldValue;
			this.newValue = newValue;
		}
	}
	
	/**
	 * Private method that manages the events stored in the {@link #eventQueue}.
	 * These event where created while being invoked from another event
	 * invocation.
	 * @param event
	 */
	private void 
	manageEvent( GraphEvent event )
	{
		if(event.getClass() == AttributeChangedEvent.class)
		{
			for( GraphListener l: listeners )
				l.attributeChanged( ( (AttributeChangedEvent) event ).element, ( (AttributeChangedEvent) event ).attribute,
						( (AttributeChangedEvent) event ).oldValue, ( (AttributeChangedEvent) event ).newValue );
		}
		else if( event.getClass() == AfterEdgeAddEvent.class )
		{
			for( GraphListener l: listeners )
				l.afterEdgeAdd( this, ( (AfterEdgeAddEvent) event ).edge );
		}
		else if( event.getClass() == AfterNodeAddEvent.class )
		{
			for( GraphListener l: listeners )
				l.afterNodeAdd( this, ( (AfterNodeAddEvent) event ).node );
		}
		else if( event.getClass() == BeforeEdgeRemoveEvent.class )
		{
			for( GraphListener l: listeners )
				l.beforeEdgeRemove( this, ( (BeforeEdgeRemoveEvent) event ).edge );
		}
		else if( event.getClass() == BeforeNodeRemoveEvent.class )
		{
			for( GraphListener l: listeners )
				l.beforeNodeRemove( this, ( (BeforeNodeRemoveEvent) event ).node );
		}
		else if( event.getClass() == BeforeGraphClearEvent.class )
		{
			for( GraphListener l: listeners )
				l.beforeGraphClear(this);

		}
	}
}
