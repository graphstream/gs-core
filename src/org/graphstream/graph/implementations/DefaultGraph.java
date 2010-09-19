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
 * Copyright 2006 - 2010
 * 	Julien Baudry
 * 	Antoine Dutot
 * 	Yoann Pigné
 * 	Guilhelm Savin
 */

package org.graphstream.graph.implementations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import org.graphstream.graph.Edge;
import org.graphstream.graph.EdgeFactory;
import org.graphstream.graph.Element;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.NodeFactory;
import org.graphstream.graph.ElementNotFoundException;
import org.graphstream.graph.IdAlreadyInUseException;
import org.graphstream.stream.AttributeSink;
import org.graphstream.stream.ElementSink;
import org.graphstream.stream.Sink;
import org.graphstream.stream.GraphParseException;
import org.graphstream.stream.Pipe;
import org.graphstream.stream.SourceBase;
import org.graphstream.stream.file.FileSink;
import org.graphstream.stream.file.FileSinkFactory;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceFactory;
import org.graphstream.stream.sync.SinkTime;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.layout.Layouts;
import org.graphstream.ui.swingViewer.GraphRenderer;
import org.graphstream.ui.swingViewer.Viewer;

/**
 * Default implementation for the Graph interface.
 *
 * <p>
 * This implementation is declined in two sub implementations {@link SingleGraph} and
 * {@link MultiGraph}. 
 * </p>
 * 
 * <p>
 * A graph is a set of graph {@link org.graphstream.graph.Element}s. Graph
 * elements can be nodes (descendants of {@link org.graphstream.graph.Node}),
 * edges (descendants of {@link org.graphstream.graph.Edge}).
 * </p>
 * 
 * <p>
 * A graph contains a set of nodes and edges indexed by their names (id), as
 * well as a set of listeners that are called each time something occurs in the
 * graph:
 * <ul>
 * <li>Topological changes (nodes and edges added or removed);</li>
 * <li>Attributes changes.</li>
 * </ul>.
 * </p>
 * 
 * <p>
 * Nodes have to be added using the {@link #addNode(String)} method. Identically
 * edges are added using the {@link #addEdge(String,String,String,boolean)}
 * method. This allows the graph to work as a factory creating edges and nodes
 * implementation that suit its needs.
 * </p>
 * 
 * <p>
 * This graph tries to ensure its consistency at any time. For this, it:
 * <ul>
 * <li>automatically deletes edges connected to a node when this one disappears;</li>
 * <li>automatically updates the node when an edge disappears;</li>
 * </ul>
 * </p>
 * 
 * @see org.graphstream.stream.Sink
 * @see org.graphstream.stream.AttributeSink
 * @see org.graphstream.stream.ElementSink
 * @see org.graphstream.graph.implementations.DefaultNode
 * @see org.graphstream.graph.implementations.DefaultEdge
 * @see org.graphstream.graph.implementations.AbstractElement
 */
public class DefaultGraph extends AbstractElement implements Graph
{
	/**
	 * Set of nodes indexed by their id.
	 */
	protected HashMap<String,? extends Node> nodes = new HashMap<String,Node>();

	/**
	 * Set of edges indexed by their id.
	 */
	protected HashMap<String,? extends Edge> edges = new HashMap<String,Edge>();

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
	 *  Helpful class that dynamically instantiate nodes according to a given class name.
	 */
	protected NodeFactory<? extends DefaultNode> nodeFactory;
	
	/**
	 *  Helpful class that dynamically instantiate edges according to a given class name.
	 */
	protected EdgeFactory<? extends DefaultEdge> edgeFactory;

	/**
	 * Current time step.
	 */
	protected double step;

	/**
	 * The set of listeners of this graph.
	 */
	protected GraphListeners listeners;
	
// Constructors

	/**
	 * New empty graph, with a default identifier.
	 * @see #DefaultGraph(String)
	 * @see #DefaultGraph(boolean, boolean)
	 * @see #DefaultGraph(String, boolean, boolean) 
	 */
	@Deprecated
	public DefaultGraph()
	{
		this( "DefaultGraph" );
	}
	
	/**
	 * New empty graph.
	 * @param id Unique identifier of the graph.
	 * @see #DefaultGraph(boolean, boolean)
	 * @see #DefaultGraph(String, boolean, boolean)
	 */
	public DefaultGraph( String id )
	{
		this( id, true, false );
	}

	/**
	 * New empty graph, with a default identifier.
	 * @param strictChecking If true any non-fatal error throws an exception.
	 * @param autoCreate If true (and strict checking is false), nodes are
	 *        automatically created when referenced when creating a edge, even
	 *        if not yet inserted in the graph.
	 * @see #DefaultGraph(String, boolean, boolean)
	 * @see #setStrict(boolean)
	 * @see #setAutoCreate(boolean)
	 */
	@Deprecated
	public DefaultGraph( boolean strictChecking, boolean autoCreate )
	{
		this( "DefaultGraph", strictChecking, autoCreate );
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
	public DefaultGraph( String id, boolean strictChecking, boolean autoCreate )
	{
		super( id );
		setStrict( strictChecking );
		setAutoCreate( autoCreate );
		
		listeners  = new GraphListeners();
		
		/*otherTimes = new SinkTime();
		time       = new SourceTime( id, otherTimes );*/
		
		// Factories that dynamically create nodes and edges.

		nodeFactory = new NodeFactory<SingleNode>()
		{
			public SingleNode newInstance( String id, Graph graph )
			{
				return new SingleNode(graph,id);
			}
		};

		edgeFactory = new EdgeFactory<SingleEdge>()
		{
			public SingleEdge newInstance( String id, Node src, Node trg, boolean  directed )
			{
				return new SingleEdge(id,src,trg, directed);
			}
		};
		
	}

// Access -- Nodes

	@Override
	protected String myGraphId() { return getId(); }			// XXX how to avoid this ?
	@Override
	protected long newEvent() { return listeners.newEvent(); }	// XXX how to avoid this ?
	
	/**
	 * @complexity O(log(n)) with n being the number of nodes.
	 */
	@SuppressWarnings("unchecked")
	public <T extends Node> T getNode( String id )
	{
		try
		{
			return (T) nodes.get( id );
		}
		catch( ClassCastException e )
		{
			StackTraceElement elt = e.getStackTrace()[0];
			
			System.err.printf("%s:%s:%d incompatible node type%n",
					elt.getFileName(),elt.getMethodName(),elt.getLineNumber());
		}
		
		return null;
	}
	
	/**
	 * @complexity O(log(m)) with m being the number of edges.
	 */
	@SuppressWarnings("unchecked")
	public <T extends Edge> T getEdge( String id )
	{
		try
		{
			return (T) edges.get( id );
		}
		catch( ClassCastException e )
		{
			StackTraceElement elt = e.getStackTrace()[0];
			
			System.err.printf("%s:%s:%d incompatible edge type%n",
					elt.getFileName(),elt.getMethodName(),elt.getLineNumber());
		}
		
		return null;
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
	public int getEdgeCount()
	{
		return edges.size();
	}

	/**
	 * @complexity Constant.
	 */
	@SuppressWarnings("unchecked")
	public <T extends Node> Iterator<T> getNodeIterator()
	{
		return new ElementIterator<T>( this, (HashMap<String,T>) nodes, true );
	}
	
	public Iterator<Node> iterator()
	{
		return new ElementIterator<Node>( this, nodes, true );
	}

	/**
	 * @complexity Constant.
	 */
	@SuppressWarnings("unchecked")
	public <T extends Edge> Iterator<T> getEdgeIterator()
	{
		return new ElementIterator<T>( this, (HashMap<String,T>) edges, false );
	}
	
	/**
	 * @complexity Constant.
	 */
	@SuppressWarnings("unchecked")
	public <T extends Node> Iterable<? extends T> getEachNode()
	{
		return (Iterable<T>) nodes.values();
	}

	/**
	 * @complexity Constant.
	 */
	@SuppressWarnings("unchecked")
	public <T extends Edge> Iterable<? extends T> getEachEdge()
	{
		return (Iterable<T>) edges.values();
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Edge> Collection<T> getEdgeSet()
	{
		return (Collection<T>)
			Collections.unmodifiableCollection( edges.values() );
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Node> Collection<T> getNodeSet()
	{
		return (Collection<T>)
			Collections.unmodifiableCollection( nodes.values() );
	}

	public EdgeFactory<? extends Edge> edgeFactory()
	{
		return edgeFactory;
	}

	@SuppressWarnings("unchecked")
	public void setEdgeFactory( EdgeFactory<? extends Edge> ef )
	{
		try
		{
			this.edgeFactory = 
				(EdgeFactory<? extends DefaultEdge>) ef;
		}
		catch( ClassCastException e )
		{
			System.err.printf("need an EdgeFactory<? extends DefaultEdge>%n");
		}
	}

	public NodeFactory<? extends Node> nodeFactory()
	{
		return nodeFactory;
	}
	
	@SuppressWarnings("unchecked")
	public void setNodeFactory( NodeFactory<? extends Node> nf )
	{
		try
		{
			this.nodeFactory =
				(NodeFactory<? extends DefaultNode>) nf;
		}
		catch( ClassCastException e )
		{
			System.err.printf("need an NodeFactory<? extends DefaultNode>%n");
		}
	}
	
	public double getStep()
	{
		return step;
	}
	
	public boolean isStrict()
	{
		return strictChecking;
	}
	
	public boolean isAutoCreationEnabled()
	{
		return autoCreate;
	}

	public Iterable<AttributeSink> attributeSinks()
	{
		return listeners.attributeSinks();
	}
	
	public Iterable<ElementSink> elementSinks()
	{
		return listeners.elementSinks();
	}

// Commands
	
	/**
	 * @complexity Constant.
	 */
	public void clearSinks()
	{
		listeners.clearSinks();
	}
	
	public void clearAttributeSinks()
	{
		listeners.clearAttributeSinks();
	}
	
	public void clearElementSinks()
	{
		listeners.clearElementSinks();
	}

	/**
	 * @complexity Constant.
	 */
	public void clear()
	{
		clear_( getId(), listeners.newEvent() );
	}
	
	protected void clear_( String sourceId, long timeId )
	{
		listeners.sendGraphCleared( sourceId, timeId );
		nodes.clear();
		edges.clear();
		clearAttributes();
	}

// Commands -- Nodes

	public void setStrict( boolean on )
	{
		strictChecking = on;
	}
	
	public void setAutoCreate( boolean on )
	{
		autoCreate = on;
	}
	
	@SuppressWarnings("unchecked")
	protected <T extends Node> T addNode_( String sourceId, long timeId, String nodeId )
		throws IdAlreadyInUseException
	{
//System.err.printf( "%s.addNode_(%s, %d, %s)%n", getId(), sourceId, timeId, nodeId );
		T n = (T) nodes.get( nodeId );
		
		if( n == null )
		{
			T node = (T) nodeFactory.newInstance(nodeId,this);
			T old  = ( (HashMap<String,T>) nodes ).put( nodeId, node );
			
			n = node;

			assert( old == null );
			
			( (HashMap<String,T>) nodes ).put( nodeId, node );
		}
		else if( strictChecking )
		{
			throw new IdAlreadyInUseException( "id '" + nodeId + "' already used, cannot add node" );
		}
		
		listeners.sendNodeAdded( sourceId, timeId, nodeId );

		return n;
		
//		DefaultNode node = (DefaultNode) nodeFactory.newInstance(tag,this);
//		DefaultNode old  = (DefaultNode) nodes.put( tag, node );
//
//		if( old != null  )
//		{
//			nodes.put( tag, old );
//			
//			if( strictChecking )
//			{
//				throw new SingletonException( "id '"+tag+
//						"' already used, cannot add node" );
//			}
//			else
//			{
//				node = old;
//			}
//		}
//		else
//		{
//			afterNodeAddEvent( (DefaultNode)node );
//		}
//
//		return (DefaultNode)node;
	}
	
	/**
	 * @complexity O(log(n)) with n being the number of nodes.
	 */
	public <T extends Node> T addNode( String id ) throws IdAlreadyInUseException
	{
		return addNode_( getId(), listeners.newEvent(), id ) ; 
	}
	
	@SuppressWarnings("unchecked")
	protected <T extends Node> T removeNode_( String sourceId, long timeId, String nodeId, boolean fromNodeIterator ) throws ElementNotFoundException
	{
		// The fromNodeIterator flag allows to know if this remove node call was
		// made from inside a node iterator or not. If from a node iterator,
		// we must not remove the node from the nodes set, this is done by the
		// iterator.
		
		DefaultNode node = (DefaultNode) nodes.get( nodeId );
		
		if( node != null )	// XXX probably to remove !!
		{
			listeners.sendNodeRemoved( sourceId, timeId, nodeId );
			node.disconnectAllEdges();
			
			if( ! fromNodeIterator )
				nodes.remove( nodeId );
			
			return (T) node;
		}
		else
		{
			listeners.sendNodeRemoved( sourceId, timeId, nodeId );

			if( strictChecking )
				throw new ElementNotFoundException( "node id '"+nodeId+"' not found, cannot remove" );
		}

		return null;
	}

	/**
	 * @complexity O(log(n)) with n being the number of nodes.
	 */
	public <T extends Node> T removeNode( String id )
		throws ElementNotFoundException
	{
		return removeNode_( getId(), listeners.newEvent(), id, false );
	}

	@SuppressWarnings("unchecked")
	protected <T extends Edge> T addEdge_( String sourceId, long timeId,
			String edgeId, String from, String to, boolean directed )
		throws IdAlreadyInUseException, ElementNotFoundException
	{
		DefaultNode src;
		DefaultNode trg;

		src = (DefaultNode) nodes.get( from );
		trg = (DefaultNode) nodes.get( to );

		if( src == null )
		{
			if( strictChecking )
			{
				throw new ElementNotFoundException( "cannot make edge from '"
						+from+"' to '"+to+"' since node '"
						+from+"' is not part of this graph" );
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
				throw new ElementNotFoundException( "cannot make edge from '"
						+from+"' to '"+to+"' since node '"+to
						+"' is not part of this graph" );
			}
			else if( autoCreate )
			{
				trg = addNode( to );
			}
		}

		if( src != null && trg != null )
		{
			T e = (T) edges.get( edgeId );
			
			if( e == null )	// Avoid recursive calls when synchronising graphs.
			{
				T edge = (T) src.addEdgeToward( edgeId, (DefaultNode)trg, directed );
				( (HashMap<String,T>) edges ).put( edge.getId(), edge );
				e = edge;
				
				if( edge.getId().equals( edgeId ) )
					listeners.sendEdgeAdded( sourceId, timeId, edgeId, from, to, directed );
			}
			else if( strictChecking )
			{
				throw new IdAlreadyInUseException( "cannot add edge '" + edgeId + "', identifier already exists" );
			}
			
			return e;
		}
		
		return null;
	}

	/**
	 * @complexity O(log(n+m)) with n being the number of nodes and m the number of edges.
	 */
	public <T extends Edge> T addEdge( String id, String node1, String node2 )
		throws IdAlreadyInUseException, ElementNotFoundException
	{
		return addEdge( id, node1, node2, false );
	}
	
	/**
	 * @complexity O(log(n+m)) with n being the number of nodes and m the number of edges.
	 */
	public <T extends Edge> T addEdge( String id, String from, String to, boolean directed )
		throws IdAlreadyInUseException, ElementNotFoundException
	{
		T edge = addEdge_( getId(), listeners.newEvent(), id, from, to, directed );
		// An explanation for this strange "if": in the SingleGraph implementation
		// when a directed edge between A and B is added with id AB, if a second
		// directed edge between B and A is added with id BA, the second edge is
		// erased, its id is not remembered and the edge with id AB is transformed
		// in undirected edge. Therefore, sometimes the id is not the same as the
		// edge.getId(). Nevertheless only one edge exists and so no event must
		// be generated.
		// TODO: this strange behaviour should disappear ! Adding BA should cause
		// an error. Use changeOrientation instead.
//		if( edge.getId().equals( id ) )
//			listeners.sendEdgeAdded( getId(), id, from, to, directed );
		return edge;
	}

	/**
	 * @complexity O(log(n+m)) with n being the number of nodes and m the number of edges.
	 */
	public <T extends Edge> T removeEdge( String from, String to )
		throws ElementNotFoundException
	{
		return removeEdge_( getId(), listeners.newEvent(), from, to );
	}
	
	@SuppressWarnings("unchecked")
	protected <T extends Edge> T removeEdge_( String sourceId, long timeId, String from, String to )
	{
		try
		{
			Node src = nodes.get( from );
			Node trg = nodes.get( to );

			if( src == null )
			{
				if( strictChecking )
					throw new ElementNotFoundException( "error while removing edge '"
							+from+"->"+to+"' node '"+from+"' cannot be found" );
			}

			if( trg == null )
			{
				if( strictChecking )
					throw new ElementNotFoundException( "error while removing edge '"
							+from+"->"+to+"' node '"+to+"' cannot be found" );
			}

			Edge edge = null;
			
			if( src != null && trg != null )
			{
				edge = src.getEdgeToward( to );
			}

			if( edge != null )
			{
				// We cannot execute the edge remove event here since edges, at
				// the contrary of other elements can disappear automatically
				// when the nodes that is linked by them disappear.
				//		beforeEdgeRemoveEvent( edge );

				((DefaultEdge) edge).unbind(sourceId, timeId);
				edges.remove(((AbstractElement) edge).getId());

				return (T) edge;
			}
		}
		catch( IllegalStateException e )
		{
			if( strictChecking )
				throw new ElementNotFoundException(
						"illegal edge state while removing edge between nodes '"
						+from+"' and '"+to+"'" );
		}

		if( strictChecking )
			throw new ElementNotFoundException( "no edge between nodes '"
					+from+"' and '"+to+"'" );
	
		return null;
	}

	/**
	 * @complexity O(log(n+m)) with n being the number of nodes and m the number of edges.
	 */
	public <T extends Edge> T removeEdge( String id )
		throws ElementNotFoundException
	{
		return removeEdge_( getId(), listeners.newEvent(), id, false );
	}
	
	@SuppressWarnings("unchecked")
	protected <T extends Edge> T removeEdge_( String sourceId, long timeId, String edgeId, boolean fromEdgeIterator )
	{
		try
		{
			DefaultEdge edge = (DefaultEdge) edges.get( edgeId );
				
			if( edge != null )
			{
				edge.unbind( sourceId, timeId );
				if( ! fromEdgeIterator )
					edge = (DefaultEdge) edges.remove( edgeId );
				return (T) edge;
			}
		}
		catch( IllegalStateException e )
		{
			if( strictChecking )
				throw new ElementNotFoundException( "illegal edge state while removing edge '"+edgeId+"'" );
		}

//		if( strictChecking )
//			throw new NotFoundException( "edge '"+edgeId+"' does not exist, cannot remove" );
		
		return null;
	}

	public void stepBegins( double step )
	{
		stepBegins_( getId(), listeners.newEvent(), step );
	}
	
	protected void stepBegins_( String sourceId, long timeId, double step )
	{
		this.step = step;

		listeners.sendStepBegins( sourceId, timeId, step );
	}
	
// Events

	@Override
	protected void attributeChanged( String sourceId, long timeId, String attribute, AttributeChangeEvent event, Object oldValue, Object newValue )
	{
		listeners.sendAttributeChangedEvent( sourceId, timeId, getId(), SourceBase.ElementType.GRAPH,
				attribute, event, oldValue, newValue );
	}

// Commands -- Utility

	public void read( FileSource input, String filename ) throws IOException, GraphParseException
    {
		input.readAll( filename );
    }

	public void read( String filename )
		throws IOException, GraphParseException, ElementNotFoundException
	{
		FileSource input = FileSourceFactory.sourceFor( filename );
		
		if( input != null ) {
			input.addSink( this );
			read( input, filename );
			input.removeSink( this );
		} else {
			throw new ElementNotFoundException( String.format( "no reader for unknown format of file '%s'", filename ) );
		}
	}

	public void write( FileSink output, String filename ) throws IOException
    {
		output.writeAll( this, filename );
    }
	
	public void write( String filename )
		throws IOException
	{
		FileSink output = FileSinkFactory.sinkFor( filename );
		if( output != null )
			write( output, filename );
		else throw new IOException( String.format( "no file output factory for unknown format of file %s", filename ) );
	}

	public Viewer display()
	{
		return display( true );
	}
	
	public Viewer display( boolean autoLayout )
	{
		Viewer        viewer   = new Viewer( this, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD );
		GraphRenderer renderer = Viewer.newGraphRenderer();
		
		viewer.addView( String.format( "defaultView_%d", (long)(Math.random()*10000) ), renderer );
	
		if( autoLayout )
		{
			Layout layout = Layouts.newLayoutAlgorithm();
			viewer.enableAutoLayout( layout );
		}
		
		return viewer;
	}
	
	@Override
	public String toString()
	{
		return String.format( "[graph %s (%d nodes %d edges)]", getId(),
				nodes.size(), edges.size() );
	}

	/**
	 * An internal class that represent an iterator able to browse edge or node sets
	 * and to remove correctly elements. This is tricky since removing a node or edge
	 * does more than only altering the node or edge sets (events for example).
	 * @param <T> Can be an Edge or a Node.
	 */
	class ElementIterator<T extends Element> implements Iterator<T>
	{
		/**
		 * If true, acts on the node set, else on the edge set.
		 */
		boolean onNodes;
		
		/**
		 * Iterator on the set of elements to browse / remove.
		 */
		Iterator<? extends T> iterator;
		
		/**
		 * The last browsed element via next(). This allows to get the
		 * element id to (eventually) remove it.
		 */
		T current;
		
		/**
		 * The graph reference to remove elements.
		 */
		DefaultGraph graph;

		/**
		 * New iterator on elements of hash maps.
		 * @param graph The graph the set of elements pertains to, this reference is used for
		 *    removing elements.
		 * @param elements The elements set to browse.
		 * @param onNodes If true the set is a node set, else it is an edge set.
		 */
		ElementIterator( DefaultGraph graph, HashMap<String, ? extends T> elements, boolean onNodes )
		{
			iterator = elements.values().iterator();
			this.graph   = graph;
			this.onNodes = onNodes;
		}
		
		/**
		 * New iterator on elements of hash maps.
		 * @param graph The graph the set of elements pertains to, this reference is used for
		 *    removing elements.
		 * @param elements The elements set to browse.
		 * @param onNodes If true the set is a node set, else it is an edge set.
		 */
		ElementIterator( DefaultGraph graph, ArrayList<T> elements, boolean onNodes )
		{
			iterator = elements.iterator();
			this.graph   = graph;
			this.onNodes = onNodes;
		}

		public boolean hasNext()
		{
			return iterator.hasNext();
		}

		public T next()
		{
			current = iterator.next();
			return current;
		}

		public void remove()
		{
			if( onNodes )
			{
				if( current != null )
				{
					graph.removeNode_( graph.getId(), listeners.newEvent(), current.getId(), true );
					iterator.remove();
				}
			}
			else	// On edges.
			{
				if( current != null )
				{
					graph.removeEdge_( graph.getId(), listeners.newEvent(), current.getId(), true );
					iterator.remove();
				}
			}
		}
	}

	public void graphCleared()
    {
		clear();
    }
	
// Sink interface
	
	public void edgeAdded( String sourceId, long timeId, String edgeId, String fromNodeId, String toNodeId,
            boolean directed )
    {
		listeners.edgeAdded(sourceId, timeId, edgeId, fromNodeId, toNodeId, directed);
    }

	public void edgeRemoved( String sourceId, long timeId, String edgeId )
    {
		listeners.edgeRemoved(sourceId, timeId, edgeId);
    }

	public void nodeAdded( String sourceId, long timeId, String nodeId )
    {
		listeners.nodeAdded(sourceId, timeId, nodeId);
    }

	public void nodeRemoved( String sourceId, long timeId, String nodeId )
    {
		listeners.nodeRemoved(sourceId, timeId, nodeId);
    }

	public void stepBegins( String sourceId, long timeId, double time )
    {
		listeners.stepBegins(sourceId, timeId, time);
    }

	public void graphCleared( String sourceId, long timeId )
    {
		listeners.graphCleared(sourceId, timeId);
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
	
	public void addAttributeSink( AttributeSink listener )
	{
		listeners.addAttributeSink( listener );
	}
	
	public void addElementSink( ElementSink listener )
	{
		listeners.addElementSink( listener );
	}
	
	public void addSink( Sink listener )
	{
		listeners.addSink( listener );
	}
	
	public void removeAttributeSink( AttributeSink listener )
	{
		listeners.removeAttributeSink( listener );
	}
	
	public void removeElementSink( ElementSink listener )
	{
		listeners.removeElementSink( listener );
	}
	
	public void removeSink( Sink listener )
	{
		listeners.removeSink( listener );
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
		
		public long newEvent()
		{
			return sourceTime.newEvent();
		}

		public void edgeAttributeAdded(String sourceId, long timeId,
				String edgeId, String attribute, Object value) {
			if( sinkTime.isNewEvent(sourceId, timeId) )
			{
				Edge edge = getEdge( edgeId );
				
				if( edge != null )
					((DefaultEdge)edge).addAttribute_( sourceId, timeId, attribute, value );
			}
		}

		public void edgeAttributeChanged(String sourceId, long timeId,
				String edgeId, String attribute, Object oldValue,
				Object newValue) {
			if( sinkTime.isNewEvent(sourceId, timeId) )
			{
				Edge edge = getEdge( edgeId );
				
				if( edge != null )
					((DefaultEdge)edge).changeAttribute_( sourceId, timeId, attribute, newValue );
			}
		}

		public void edgeAttributeRemoved(String sourceId, long timeId,
				String edgeId, String attribute) {
			if( sinkTime.isNewEvent(sourceId, timeId) )
			{
				Edge edge = getEdge( edgeId );
				
				if( edge != null )
					((DefaultEdge)edge).removeAttribute_( sourceId, timeId, attribute );
			}
		}

		public void graphAttributeAdded(String sourceId, long timeId,
				String attribute, Object value) {
			if( sinkTime.isNewEvent(sourceId, timeId) )
			{
				addAttribute_( sourceId, timeId, attribute, value );
			}
		}

		public void graphAttributeChanged(String sourceId, long timeId,
				String attribute, Object oldValue, Object newValue) {
			if( sinkTime.isNewEvent(sourceId, timeId) )
			{
				changeAttribute_( sourceId, timeId, attribute, newValue );
			}
		}

		public void graphAttributeRemoved(String sourceId, long timeId,
				String attribute) {
			if( sinkTime.isNewEvent(sourceId, timeId) )
			{
				removeAttribute_( sourceId, timeId, attribute );
			}
		}

		public void nodeAttributeAdded(String sourceId, long timeId,
				String nodeId, String attribute, Object value) {
			if( sinkTime.isNewEvent(sourceId, timeId) )
			{
				Node node = getNode( nodeId );
				
				if( node != null )
					((DefaultNode)node).addAttribute_( sourceId, timeId, attribute, value );
			}
		}

		public void nodeAttributeChanged(String sourceId, long timeId,
				String nodeId, String attribute, Object oldValue,
				Object newValue) {
			if( sinkTime.isNewEvent(sourceId, timeId) )
			{
				Node node = getNode( nodeId );
				
				if( node != null )
					((DefaultNode)node).changeAttribute_( sourceId, timeId, attribute, newValue );
			}
		}

		public void nodeAttributeRemoved(String sourceId, long timeId,
				String nodeId, String attribute) {
			if( sinkTime.isNewEvent(sourceId, timeId) )
			{
				Node node = getNode( nodeId );
				
				if( node != null )
					((DefaultNode)node).removeAttribute_( sourceId, timeId, attribute );
			}
		}

		public void edgeAdded(String sourceId, long timeId, String edgeId,
				String fromNodeId, String toNodeId, boolean directed) {
			if( sinkTime.isNewEvent(sourceId, timeId) )
			{
				addEdge_( sourceId, timeId, edgeId, fromNodeId, toNodeId, directed );
			}
		}

		public void edgeRemoved(String sourceId, long timeId, String edgeId) {
			if( sinkTime.isNewEvent(sourceId, timeId) )
			{
				removeEdge_( sourceId, timeId, edgeId, false );
			}
		}

		public void graphCleared(String sourceId, long timeId) {
			if( sinkTime.isNewEvent(sourceId, timeId) )
			{
				clear_( sourceId, timeId );
			}
		}

		public void nodeAdded(String sourceId, long timeId, String nodeId) {
//System.err.printf( "%s.nodeAdded( %s, %d, %s ) =>", getId(), sourceId, timeId, nodeId );
			if( sinkTime.isNewEvent(sourceId, timeId) )
			{
//System.err.printf( " adding%n" );
				addNode_( sourceId, timeId, nodeId );
			}
//else System.err.printf( " ignoring%n" );
		}

		public void nodeRemoved(String sourceId, long timeId, String nodeId) {
			if( sinkTime.isNewEvent(sourceId, timeId) )
			{
				removeNode_( sourceId, timeId, nodeId, false );
			}
		}

		public void stepBegins(String sourceId, long timeId, double step) {
			if( sinkTime.isNewEvent(sourceId, timeId) )
			{
				stepBegins_(sourceId, timeId, step );
			}
		}
	}
}