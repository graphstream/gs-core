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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.graphstream.graph.Edge;
import org.graphstream.graph.EdgeFactory;
import org.graphstream.graph.Element;
import org.graphstream.graph.Graph;
import org.graphstream.graph.GraphAttributesListener;
import org.graphstream.graph.GraphElementsListener;
import org.graphstream.graph.GraphListener;
import org.graphstream.graph.Node;
import org.graphstream.graph.NodeFactory;
import org.graphstream.io.GraphParseException;
import org.graphstream.io.Pipe;
import org.graphstream.io.SourceBase;
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
 * @see org.graphstream.graph.GraphListener
 * @see org.graphstream.graph.GraphAttributesListener
 * @see org.graphstream.graph.GraphElementsListener
 * @see org.graphstream.graph.implementations.DefaultNode
 * @see org.graphstream.graph.implementations.DefaultEdge
 * @see org.graphstream.graph.implementations.AbstractElement
 */
public class DefaultGraph extends AbstractElement implements Graph
{
	/**
	 * Set of nodes indexed by their id.
	 */
	protected HashMap<String,Node> nodes = new HashMap<String,Node>();

	/**
	 * Set of edges indexed by their id.
	 */
	protected HashMap<String,Edge> edges = new HashMap<String,Edge>();

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
	protected NodeFactory nodeFactory;
	
	/**
	 *  Helpful class that dynamically instantiate edges according to a given class name.
	 */
	protected EdgeFactory edgeFactory;

	/**
	 * Current time step.
	 */
	protected double step;

	/**
	 * The set of listeners of this graph.
	 */
	protected GraphListeners listeners;
	/*
	protected SourceTime time;
	protected SinkTime otherTimes;
	*/
	
// Constructors

	/**
	 * New empty graph, with the empty string as default identifier.
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
	 * New empty graph, with the empty string as default identifier.
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

		nodeFactory = new NodeFactory()
		{
			public Node newInstance( String id, Graph graph )
			{
				return new SingleNode(graph,id);
			}
		};

		edgeFactory = new EdgeFactory()
		{
			public Edge newInstance( String id, Node src, Node trg, boolean  directed )
			{
				return new SingleEdge(id,src,trg, directed);
			}
		};
		
	}

// Access -- Nodes

	@Override
	protected String getMyGraphId()
	{
		return getId();
		//return time.newEvent();
	}
	
	/**
	 * @complexity O(1).
	 */
	public Node getNode( String id )
	{
		return nodes.get( id );
	}
	
	/**
	 * @complexity O(1)
	 */
	public Edge getEdge( String id )
	{
		return edges.get( id );
	}

	/**
	 * @complexity O(1)
	 */
	public int getNodeCount()
	{
		return nodes.size();
	}

	/**
	 * @complexity O(1)
	 */
	public int getEdgeCount()
	{
		return edges.size();
	}

	/**
	 * @complexity O(1)
	 */
	public Iterator<Node> getNodeIterator()
	{
		return new ElementIterator<Node>( this, nodes, true );
	}
	
	public Iterator<Node> iterator()
	{
		return new ElementIterator<Node>( this, nodes, true );
	}

	/**
	 * @complexity O(1)
	 */
	public Iterator<Edge> getEdgeIterator()
	{
		return new ElementIterator<Edge>( this, edges, false );
	}
	
	/**
	 * @complexity O(1)
	 */
	public Iterable<Node> nodeSet()
	{
		return nodes.values();
	}

	/**
	 * @complexity O(1)
	 */
	public Iterable<Edge> edgeSet()
	{
		return edges.values();
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

	public Iterable<GraphAttributesListener> getGraphAttributesListeners()
	{
		return listeners.graphAttributesListeners();
	}
	
	public Iterable<GraphElementsListener> getGraphElementsListeners()
	{
		return listeners.graphElementsListeners();
	}

// Commands
	
	/**
	 * @complexity O(1)
	 */
	public void clearListeners()
	{
		listeners.clearListeners();
	}

	/**
	 * @complexity O(1)
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
	
	protected Node addNode_( String sourceId, String nodeId ) throws SingletonException
	{
//System.err.printf( "%s.addNode_(%s, %s)%n", getId(), sourceId, nodeId );
		Node n = nodes.get( nodeId );
		
		if( n == null )
		{
			DefaultNode node = (DefaultNode) nodeFactory.newInstance(nodeId,this);
			DefaultNode old  = (DefaultNode) nodes.put( nodeId, node );
			
			n = node;

			assert( old == null );
			
			nodes.put( nodeId, node );
		}
		else if( strictChecking )
		{
			throw new SingletonException( "id '" + nodeId + "' already used, cannot add node" );
		}
		
		listeners.sendNodeAdded( sourceId, nodeId );

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
	 * @complexity O(1)
	 */
	public Node addNode( String id ) throws SingletonException
	{
		return addNode_( getId(), id ) ; 
	}
	
	protected Node removeNode_( String sourceId, String nodeId, boolean fromNodeIterator ) throws NotFoundException
	{
System.err.printf( "%s.removeNode_(%s, %s)%n", getId(), sourceId, nodeId );
		// The fromNodeIterator flag allows to know if this remove node call was
		// made from inside a node iterator or not. If from a node iterator,
		// we must not remove the node from the nodes set, this is done by the
		// iterator.
		
		DefaultNode node = (DefaultNode) nodes.get( nodeId );
		
		if( node != null )	// XXX probably to remove !!
		{
			listeners.sendNodeRemoved( sourceId, nodeId );
			node.disconnectAllEdges();
			
			if( ! fromNodeIterator )
				nodes.remove( nodeId );
			
			return node;
		}

		if( strictChecking )
			throw new NotFoundException( "node id '"+nodeId+"' not found, cannot remove" );

		listeners.sendNodeRemoved( sourceId, nodeId );

		return null;
	}

	/**
	 * @complexity O(1)
	 */
	public Node removeNode( String id ) throws NotFoundException
	{
		return removeNode_( getId(), id, false );
	}

	protected Edge addEdge_( String sourceId, String edgeId, String from, String to, boolean directed )
		throws SingletonException, NotFoundException
	{
		Node src;
		Node trg;

		src = nodes.get( from );
		trg = nodes.get( to );

		if( src == null )
		{
			if( strictChecking )
			{
				throw new NotFoundException( "cannot make edge from '"
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
				throw new NotFoundException( "cannot make edge from '"
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
			Edge e = edges.get( edgeId );
			
			if( e == null )	// Avoid recursive calls when synchronising graphs.
			{
				DefaultEdge edge = (DefaultEdge) ((DefaultNode)src).addEdgeToward( edgeId, (DefaultNode)trg, directed );
				edges.put( edge.getId(), (DefaultEdge) edge );
				e = edge;
				
				if( edge.getId().equals( edgeId ) )
					listeners.sendEdgeAdded( sourceId, edgeId, from, to, directed );
			}
			else if( strictChecking )
			{
				throw new SingletonException( "cannot add edge '" + edgeId + "', identifier already exists" );
			}
			
			return e;
		}
		
		return null;
	}

	/**
	 * @complexity O(1)
	 */
	public Edge addEdge( String id, String node1, String node2 )
		throws SingletonException, NotFoundException
	{
		return addEdge( id, node1, node2, false );
	}
	
	/**
	 * @complexity O(1)
	 */
	public Edge addEdge( String id, String from, String to, boolean directed )
		throws SingletonException, NotFoundException
	{
		Edge edge = addEdge_( getId(), id, from, to, directed );
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
	 * @complexity O(1)
	 */
	public Edge removeEdge( String from, String to )
		throws NotFoundException
	{
		return removeEdge_( getId(), from, to );
	}
	
	protected Edge removeEdge_( String sourceId, String from, String to )
	{
		try
		{
			Node src = nodes.get( from );
			Node trg = nodes.get( to );

			if( src == null )
			{
				if( strictChecking )
					throw new NotFoundException( "error while removing edge '"
							+from+"->"+to+"' node '"+from+"' cannot be found" );
			}

			if( trg == null )
			{
				if( strictChecking )
					throw new NotFoundException( "error while removing edge '"
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

				edges.remove( ( (AbstractElement) edge ).getId() );
				((DefaultEdge)edge).unbind( sourceId );

				return edge;
			}
		}
		catch( IllegalStateException e )
		{
			if( strictChecking )
				throw new NotFoundException(
						"illegal edge state while removing edge between nodes '"
						+from+"' and '"+to+"'" );
		}

		if( strictChecking )
			throw new NotFoundException( "no edge between nodes '"
					+from+"' and '"+to+"'" );
	
		return null;
	}

	/**
	 * @complexity O(1)
	 */
	public Edge removeEdge( String id ) throws NotFoundException
	{
		return removeEdge_( getId(), id, false );
	}
	
	protected Edge removeEdge_( String sourceId, String edgeId, boolean fromEdgeIterator )
	{
		try
		{
			DefaultEdge edge = null;
			
			if( fromEdgeIterator )
			     edge = (DefaultEdge) edges.get( edgeId );
			else edge = (DefaultEdge) edges.remove( edgeId );

			if( edge != null )
			{
				edge.unbind( sourceId );
				return edge;
			}
		}
		catch( IllegalStateException e )
		{
			if( strictChecking )
				throw new NotFoundException( "illegal edge state while removing edge '"+edgeId+"'" );
		}

//		if( strictChecking )
//			throw new NotFoundException( "edge '"+edgeId+"' does not exist, cannot remove" );
		
		return null;
	}

	public void stepBegins( double step )
	{
		stepBegins_( getId(), step );
	}
	
	protected void stepBegins_( String sourceId, double time )
	{
		step = time;

		listeners.sendStepBegins( sourceId, time );
	}
	
// Events

	@Override
	protected void attributeChanged( String sourceId, String attribute, AttributeChangeEvent event, Object oldValue, Object newValue )
	{
		listeners.sendAttributeChangedEvent( sourceId, null, SourceBase.ElementType.GRAPH,
				attribute, event, oldValue, newValue );
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
		String viewerClass = "org.miv.graphstream.ui.swing.SwingGraphViewer";
		
		try
        {
	        Class<?> c      = Class.forName( viewerClass );
	        Object   object = c.newInstance();
	        
	        if( object instanceof GraphViewer )
	        {
	        	GraphViewer gv = (GraphViewer) object;
	        	
	        	gv.open(  this, autoLayout );
	        	
	        	return gv.newViewerRemote();
	        }
	        else
	        {
	        	System.err.printf( "Viewer class '%s' is not a 'GraphViewer'%n", object );
	        }
        }
        catch( ClassNotFoundException e )
        {
	        e.printStackTrace();
        	System.err.printf( "Cannot display graph, 'GraphViewer' class not found : " + e.getMessage() );
        }
        catch( InstantiationException e )
        {
            e.printStackTrace();
        	System.err.printf( "Cannot display graph, class '"+viewerClass+"' error : " + e.getMessage() );
        }
        catch( IllegalAccessException e )
        {
            e.printStackTrace();
        	System.err.printf( "Cannot display graph, class '"+viewerClass+"' illegal access : " + e.getMessage() );
        }
        
        return null;
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
	static class ElementIterator<T extends Element> implements Iterator<T>
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
					graph.removeNode_( graph.getId(), current.getId(), true );
					iterator.remove();
				}
			}
			else	// On edges.
			{
				if( current != null )
				{
					graph.removeEdge_( graph.getId(), current.getId(), true );
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
	
	public void addGraphAttributesListener( GraphAttributesListener listener )
	{
		listeners.addGraphAttributesListener( listener );
	}
	
	public void addGraphElementsListener( GraphElementsListener listener )
	{
		listeners.addGraphElementsListener( listener );
	}
	
	public void addGraphListener( GraphListener listener )
	{
		listeners.addGraphListener( listener );
	}
	
	public void removeGraphAttributesListener( GraphAttributesListener listener )
	{
		listeners.removeGraphAttributesListener( listener );
	}
	
	public void removeGraphElementsListener( GraphElementsListener listener )
	{
		listeners.removeGraphElementsListener( listener );
	}
	
	public void removeGraphListener( GraphListener listener )
	{
		listeners.removeGraphListener( listener );
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
					((DefaultEdge)edge).addAttribute_( sourceId, attribute, value );
			}
		}

		public void edgeAttributeChanged(String sourceId, long timeId,
				String edgeId, String attribute, Object oldValue,
				Object newValue) {
			if( sinkTime.isNewEvent(sourceId, timeId) )
			{
				Edge edge = getEdge( edgeId );
				
				if( edge != null )
					((DefaultEdge)edge).changeAttribute_( sourceId, attribute, newValue );
			}
		}

		public void edgeAttributeRemoved(String sourceId, long timeId,
				String edgeId, String attribute) {
			if( sinkTime.isNewEvent(sourceId, timeId) )
			{
				Edge edge = getEdge( edgeId );
				
				if( edge != null )
					((DefaultEdge)edge).removeAttribute_( sourceId, attribute );
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
					((DefaultNode)node).addAttribute_( sourceId, attribute, value );
			}
		}

		public void nodeAttributeChanged(String sourceId, long timeId,
				String nodeId, String attribute, Object oldValue,
				Object newValue) {
			if( sinkTime.isNewEvent(sourceId, timeId) )
			{
				Node node = getNode( nodeId );
				
				if( node != null )
					((DefaultNode)node).changeAttribute_( sourceId, attribute, newValue );
			}
		}

		public void nodeAttributeRemoved(String sourceId, long timeId,
				String nodeId, String attribute) {
			if( sinkTime.isNewEvent(sourceId, timeId) )
			{
				Node node = getNode( nodeId );
				
				if( node != null )
					((DefaultNode)node).removeAttribute_( sourceId, attribute );
			}
		}

		public void edgeAdded(String sourceId, long timeId, String edgeId,
				String fromNodeId, String toNodeId, boolean directed) {
			if( sinkTime.isNewEvent(sourceId, timeId) )
			{
				addEdge_( sourceId, edgeId, fromNodeId, toNodeId, directed );
			}
		}

		public void edgeRemoved(String sourceId, long timeId, String edgeId) {
			if( sinkTime.isNewEvent(sourceId, timeId) )
			{
				removeEdge_( sourceId, edgeId, false );
			}
		}

		public void graphCleared(String sourceId, long timeId) {
			if( sinkTime.isNewEvent(sourceId, timeId) )
			{
				clear_( sourceId );
			}
		}

		public void nodeAdded(String sourceId, long timeId, String nodeId) {
			if( sinkTime.isNewEvent(sourceId, timeId) )
			{
				addNode_( sourceId, nodeId );
			}
		}

		public void nodeRemoved(String sourceId, long timeId, String nodeId) {
			if( sinkTime.isNewEvent(sourceId, timeId) )
			{
				removeNode_( sourceId, nodeId, false );
			}
		}

		public void stepBegins(String sourceId, long timeId, double step) {
			if( sinkTime.isNewEvent(sourceId, timeId) )
			{
				stepBegins_(sourceId, step );
			}
		}
	}
}