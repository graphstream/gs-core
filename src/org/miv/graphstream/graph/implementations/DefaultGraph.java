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

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;

//import org.miv.graphstream.algorithm.Algorithms;
import org.miv.graphstream.graph.AbstractElement;
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
 * DefaultGraph.
 * 
 * <p>
 * A graph is a set of graph {@link org.miv.graphstream.graph.AbstractElement}s. Graph
 * elements can be nodes (descendants of {@link org.miv.graphstream.graph.Node}),
 * edges (descendants of {@link org.miv.graphstream.graph.Edge}).
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
 * @see org.miv.graphstream.graph.GraphListener
 * @see org.miv.graphstream.graph.implementations.DefaultNode
 * @see org.miv.graphstream.graph.implementations.DefaultEdge
 * @see org.miv.graphstream.graph.AbstractElement
 * @author Antoine Dutot
 * @author Yoann Pigné
 * @since 09 Sept. 2002
 */
public class DefaultGraph
	extends	AbstractElement 
	implements Graph
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
	 * Set of graph listeners.
	 */
	protected ArrayList<GraphListener> listeners = new ArrayList<GraphListener>();

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
	 * A queue that allow the management of events (nodes/edge add/delete/change) in the right order. 
	 */
	protected LinkedList<GraphEvent> eventQueue = new LinkedList<GraphEvent>();
	
	/**
	 * A boolean that indicates whether or not an GraphListener event is being sent during another one. 
	 */
	protected boolean eventProcessing=false;
	
	/**
	 * List of listeners to remove if the {@link #removeGraphListener(GraphListener)} is called
	 * inside from the listener. This can happen !! We create this list on demand.
	 */
	protected ArrayList<GraphListener> listenersToRemove;
	
	/**
	 * Set of common algorithms.
	protected Algorithms algos;
	 */
	
	/**
	 *  Helpful class that dynamically instantiate nodes according to a given class name.
	 */
	protected NodeFactory nodeFactory;
	
	/**
	 *  Helpful class that dynamically instantiate edges according to a given class name.
	 */
	protected EdgeFactory edgeFactory;
	
	
// Constructors

	/**
	 * New empty graph, with the empty string as default identifier.
	 * @see #DefaultGraph(String)
	 * @see #DefaultGraph(boolean, boolean)
	 * @see #DefaultGraph(String, boolean, boolean) 
	 */
	public DefaultGraph()
	{
		this( "" );
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
	 * @see #setStrictChecking(boolean)
	 * @see #setAutoCreate(boolean)
	 */
	public DefaultGraph( boolean strictChecking, boolean autoCreate )
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
	public DefaultGraph( String id, boolean strictChecking, boolean autoCreate )
	{
		super( id );
		setStrictChecking( strictChecking );
		setAutoCreate( autoCreate );
		
		// Factories that dynamicaly create nodes and edges.
		nodeFactory = new DefaultNodeFactory();
		edgeFactory = new DefaultEdgeFactory();
		
	}

// Access -- Nodes

	/**
	 * @complexity O(1).
	 */
	public Node
	getNode( String id )
	{
		return nodes.get( id );
	}
	
	/**
	 * @complexity O(1)
	 */
	public Edge
	getEdge( String id )
	{
		return edges.get( id );
	}

	/**
	 * @complexity O(1)
	 */
	public int
	getNodeCount()
	{
		return nodes.size();
	}

	/**
	 * @complexity O(1)
	 */
	public int
	getEdgeCount()
	{
		return edges.size();
	}

	/**
	 * @complexity O(1)
	 */
	public Iterator<Node> 
	getNodeIterator()
	{
		return new ElementIterator<Node>( this, nodes, true );
	}

	/**
	 * @complexity O(1)
	 */
	public Iterator<Edge>
	getEdgeIterator()
	{
		return new ElementIterator<Edge>( this, edges, false );
	}
	
	/**
	 * @complexity O(1)
	 */
	public final Collection<Node>
	getNodeSet()
	{
		return nodes.values();
	}

	/**
	 * @complexity O(1)
	 */
	public Collection<Edge>
	getEdgeSet()
	{
		return edges.values();
	}

	public EdgeFactory edgeFactory()
	{
		return edgeFactory;
	}

	public NodeFactory nodeFactory()
	{
		return nodeFactory;
	}

// Commands

	/**
	 * @complexity O(1)
	 */
	public void
	clear()
	{
		beforeClearEvent();
		nodes.clear();
		edges.clear();
		clearAttributes();
	}
	
	/**
	 * @complexity O(1)
	 */
	public void
	clearListeners()
	{
		listeners.clear();
	}
	
	public boolean
	isStrictCheckingEnabled()
	{
		return strictChecking;
	}
	
	public boolean
	isAutoCreationEnabled()
	{
		return autoCreate;
	}

	public List<GraphListener> getGraphListeners()
	{
		return listeners;
	}

// Commands -- Nodes

	public void setStrictChecking( boolean on )
	{
		strictChecking = on;
	}
	
	public void setAutoCreate( boolean on )
	{
		autoCreate = on;
	}
	
	protected Node
	addNode_( String tag )
		throws SingletonException
	{
		DefaultNode node = (DefaultNode) nodeFactory.newInstance();
		node.setId(tag);
		node.setGraph(this);
		
		DefaultNode old = (DefaultNode) nodes.put( tag, node );

		if( old != null  )
		{
			nodes.put( tag, old );
			
			if( strictChecking )
			{
				throw new SingletonException( "id '"+tag+
						"' already used, cannot add node" );
			}
			else
			{
				node = old;
			}
		}
		else
		{
			afterNodeAddEvent( (DefaultNode)node );
		}

		return (DefaultNode)node;
	}
	

	/**
	 * @complexity O(1)
	 */
	public Node
	addNode( String id )
		throws SingletonException
	{
		return addNode_( id ) ; 
	}
	
	protected Node
	removeNode_( String tag, boolean fromNodeIterator )
		throws NotFoundException
	{
		// The fromNodeIterator flag allows to know if this remove node call was
		// made from inside a node iterator or not. If from a node iterator,
		// we must not remove the node from the nodes set, this is done by the
		// iterator.
		
		DefaultNode node = (DefaultNode) nodes.get( tag );
		
		if( node != null )
		{
			node.disconnectAllEdges();
			beforeNodeRemoveEvent( node );
			
			if( ! fromNodeIterator )
				nodes.remove( tag );
			
			return node;
		}

		if( strictChecking )
			throw new NotFoundException( "node id '"+tag+"' not found, cannot remove" );
		
		return null;
	}

	/**
	 * @complexity O(1)
	 */
	public Node
	removeNode( String id )
		throws NotFoundException
	{
		return removeNode_( id, false );
	}

	protected Edge
	addEdge_( String tag, String from, String to, boolean directed )
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
			DefaultEdge edge = (DefaultEdge) ((DefaultNode)src).addEdgeToward( tag, (DefaultNode)trg, directed );
			edges.put( edge.getId(), (DefaultEdge) edge );
			
			return edge;
		}
		
		return null;
	}

	/**
	 * @complexity O(1)
	 */
	public Edge
	addEdge( String id, String node1, String node2 )
		throws SingletonException, NotFoundException
	{
		return addEdge( id, node1, node2, false );
	}
	
	/**
	 * @complexity O(1)
	 */
	public Edge
	addEdge( String id, String from, String to, boolean directed )
		throws SingletonException, NotFoundException
	{
		Edge edge = addEdge_( id, from, to, directed );
		// An explanation for this strange "if": in the SingleGraph implementation
		// when a directed edge between A and B is added with id AB, if a second
		// directed edge between B and A is added with id BA, the second edge is
		// erased, its id is not remembered and the edge with id AB is transformed
		// in undirected edge. Therefore, sometimes the id is not the same as the
		// edge.getId(). Nevertheless only one edge exists and so no event must
		// be generated.
		if( edge.getId().equals( id ) )
			afterEdgeAddEvent( edge );
		return edge;
	}

	/**
	 * @complexity O(1)
	 */
	public Edge
	removeEdge( String from, String to )
		throws NotFoundException
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

			//	beforeEdgeRemoveEvent( edge );

				edges.remove( ( (AbstractElement) edge ).getId() );
				((DefaultEdge)edge).unbind();

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
		return removeEdge_( id, false );
	}
	
	protected Edge removeEdge_( String id, boolean fromEdgeIterator )
	{
		try
		{
			DefaultEdge edge = null;
			
			if( fromEdgeIterator )
			     edge = (DefaultEdge) edges.get( id );
			else edge = (DefaultEdge) edges.remove( id );

			if( edge != null )
			{
				edge.unbind();
				return edge;
			}
		}
		catch( IllegalStateException e )
		{
			if( strictChecking )
				throw new NotFoundException( "illegal edge state while removing edge '"+id+"'" );
		}

		if( strictChecking )
			throw new NotFoundException( "edge '"+id+"' does not exist, cannot remove" );
		
		return null;
	}

	public void stepBegins(double time)
	{
		for (GraphListener l : listeners)
			l.stepBegins(this, time);
	}
	
	
// Events

	/**
	 * @complexity 0(1)
	 */
	public void
	addGraphListener( GraphListener listener )
	{
		listeners.add( listener );
	}

	/**
	 * @complexity O(n) with n the numbers of listeners. 
	 */
	public void removeGraphListener( GraphListener listener )
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

	protected void afterNodeAddEvent( Node node )
	{
		if( ! eventProcessing )
		{
			eventProcessing = true;

			while( ! eventQueue.isEmpty() )
				manageEvent( eventQueue.remove() );

			for( GraphListener l: listeners )
				l.afterNodeAdd( this, node );

			while( ! eventQueue.isEmpty() )
				manageEvent( eventQueue.remove() );

			eventProcessing = false;
			checkListenersToRemove();
		}
		else 
		{
			eventQueue.add( new AfterNodeAddEvent(node) );
		}
	}

	protected void
	beforeNodeRemoveEvent( Node node )
	{
		if( ! eventProcessing )
		{
			eventProcessing = true;

			while( ! eventQueue.isEmpty() )
				manageEvent( eventQueue.remove() );

			for( GraphListener l: listeners )
				l.beforeNodeRemove( this, node );

			while( ! eventQueue.isEmpty() )
				manageEvent( eventQueue.remove() );

			eventProcessing = false;
			checkListenersToRemove();
		}
		else 
		{
			eventQueue.add( new BeforeNodeRemoveEvent( node ) );
		}
	}

	protected void
	afterEdgeAddEvent( Edge edge )
	{
		if( ! eventProcessing )
		{
			eventProcessing = true;

			while( ! eventQueue.isEmpty() )
				manageEvent( eventQueue.remove() );

			for( GraphListener l: listeners )
				l.afterEdgeAdd( this, edge );

			while( ! eventQueue.isEmpty() )
				manageEvent( eventQueue.remove() );

			eventProcessing = false;
			checkListenersToRemove();
		}
		else 
		{
//			printPosition( "AddEdge in EventProc" );
			eventQueue.add( new AfterEdgeAddEvent(edge) );
		}
	}
/*
protected void printPosition( String msg ){
	System.err.printf( "%s (%s):%n", msg, Thread.currentThread().getName() );
	StackTraceElement[] elts = Thread.currentThread().getStackTrace();
	for( StackTraceElement elt: elts )
		System.err.printf( "    %s.%s %d%n", elt.getClassName(), elt.getMethodName(), elt.getLineNumber() );

}
*/
	protected void
	beforeEdgeRemoveEvent( Edge edge )
	{
		if( ! eventProcessing )
		{
			eventProcessing = true;

			while(!eventQueue.isEmpty())
				manageEvent( eventQueue.remove() );

			for( GraphListener l: listeners )
				l.beforeEdgeRemove( this, edge );

			while(!eventQueue.isEmpty())
				manageEvent( eventQueue.remove() );

			eventProcessing = false;
			checkListenersToRemove();
		}
		else {
//			printPosition( "DelEdge in EventProc" );
			eventQueue.add( new BeforeEdgeRemoveEvent( edge ) );
		}
	}

	protected void
	beforeClearEvent()
	{
		if( ! eventProcessing )
		{
			eventProcessing = true;

			while( ! eventQueue.isEmpty() )
				manageEvent( eventQueue.remove() );

			for( GraphListener l: listeners )
				l.beforeGraphClear( this );

			while( ! eventQueue.isEmpty() )
				manageEvent( eventQueue.remove() );

			eventProcessing = false;
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
		if( ! eventProcessing )
		{
			eventProcessing = true;

			while( ! eventQueue.isEmpty() )
				manageEvent(eventQueue.remove());

			for( GraphListener l: listeners )
				l.attributeChanged( element, attribute, oldValue, newValue );

			while( ! eventQueue.isEmpty() )
				manageEvent(eventQueue.remove());

			eventProcessing = false;
			checkListenersToRemove();
		}
		else {
//			printPosition( "ChgEdge in EventProc" );
			eventQueue.add( new AttributeChangedEvent( element, attribute, oldValue, newValue ) );
		}
	}

// Commands -- Utility

	/* (non-Javadoc)
	 * @see org.miv.graphstream.graph.GraphInterface#read(java.lang.String)
	 */
	public void read( String filename )
		throws IOException, GraphParseException, NotFoundException
	{
		GraphReaderListenerHelper listener = new GraphReaderListenerHelper( this );
		GraphReader reader = GraphReaderFactory.readerFor( filename );
		reader.addGraphReaderListener( listener );
		reader.read( filename );
	}
	
	/* (non-Javadoc)
	 * @see org.miv.graphstream.graph.GraphInterface#read(org.miv.graphstream.io.GraphReader, java.lang.String)
	 */
	public void read( GraphReader reader, String filename )
		throws IOException, GraphParseException
	{
		GraphReaderListenerHelper listener = new GraphReaderListenerHelper( this );
		reader.addGraphReaderListener( listener );
		reader.read( filename );
	}
	
	/* (non-Javadoc)
	 * @see org.miv.graphstream.graph.GraphInterface#write(java.lang.String)
	 */
	public void write( String filename )
		throws IOException
	{
		GraphWriterHelper gwh = new GraphWriterHelper( this );
		gwh.write( filename );
	}
	
	/* (non-Javadoc)
	 * @see org.miv.graphstream.graph.GraphInterface#write(org.miv.graphstream.io.GraphWriter, java.lang.String)
	 */
	public void write( GraphWriter writer, String filename )
		throws IOException
	{
		GraphWriterHelper gwh = new GraphWriterHelper( this );
		gwh.write( filename, writer );
	}
	
	/* (non-Javadoc)
	 * @see org.miv.graphstream.graph.GraphInterface#readPositionFile(java.lang.String)
	 */
	public int readPositionFile( String posFileName )
		throws IOException
	{
		if( posFileName == null )
			throw new IOException( "no filename given" );
		
		Scanner scanner = new Scanner( new BufferedInputStream( new FileInputStream( posFileName ) ) );
		int     ignored = 0;
		int     mapped  = 0;
		int     line    = 1;
		String  id      = null;
		float   x = 0, y = 0, z = 0;

		scanner.useLocale( Locale.US );
		scanner.useDelimiter( "\\s|\\n|:" );

		try
		{
			while( scanner.hasNext() )
			{
				id = scanner.next();

				x  = scanner.nextFloat();
				y  = scanner.nextFloat();
				z  = scanner.nextFloat();

				line++;
				
				DefaultNode node = (DefaultNode) nodes.get( id );

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
			throw new IOException( "parse error '"+posFileName+"':"+line+": " + e.getMessage() );
		}
		catch( NoSuchElementException e )
		{
			throw new IOException( "unexpected end of file '"+posFileName+"':"+line+": " + e.getMessage() );
		}
		catch( IllegalStateException e )
		{
			throw new IOException( "scanner error '"+posFileName+"':"+line+": " + e.getMessage() );
		}

		scanner.close();

		return ignored;
	}
	
	/* (non-Javadoc)
	 * @see org.miv.graphstream.graph.GraphInterface#display()
	 */
	public GraphViewerRemote display()
	{
		return display( true );
	}

	/* (non-Javadoc)
	 * @see org.miv.graphstream.graph.GraphInterface#display(boolean)
	 */
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
	
// Algorithms
		
	/**
	 * Compute the degree distribution of this graph. Each cell of the returned
	 * array contains the number of nodes having degree n where n is the index
	 * of the cell. For example cell 0 counts how many nodes have zero edges,
	 * cell 5 counts how many nodes have five edges. The last index indicates
	 * the maximum degree.
	 * @deprecated See {@link org.miv.graphstream.algorithm.Algorithms}
	 */
	@Deprecated
	public int[] getDegreeDistribution()
	{
		int      max = 0;
		int[]    dd;
		int      d;

		for( Node n: nodes.values() )
		{
			d = n.getDegree();

			if( d > max )
				max = d;
		}

		dd = new int[max+1];

		for( Node n: nodes.values() )
		{
			d = n.getDegree();

			dd[d] += 1;
		}

		return dd;
	}
	
	/**
	 * Return a list of nodes sorted by degree, the larger first.
	 * @return The degree map.
	 * @deprecated See {@link org.miv.graphstream.algorithm.Algorithms}
	 */
	@Deprecated
	public ArrayList<Node> getDegreeMap()
	{
		ArrayList<Node> map = new ArrayList<Node>();
		
		map.addAll(  (Collection<Node>) nodes.values() );
	
		Collections.sort( map, new Comparator<Node>() {
			public int compare( Node a, Node b )
			{
				return b.getDegree() - a.getDegree();
			}
		});
		
		return map;
	}

	/**
	 * Clustering coefficient for each node of the graph.
	 * @return An array whose size correspond to the number of nodes, where each
	 * element is the clustering coefficient of a node.
	 * @deprecated See {@link org.miv.graphstream.algorithm.Algorithms}
	 */
	@Deprecated
	public double[] getClusteringCoefficients()
	{
		int n = getNodeCount();

		if( n > 0 )
		{
			int j = 0;
			double coefs[] = new double[n];

			for( Node node: nodes.values() )
			{
				coefs[j++] = getClusteringCoefficient( node );
			}

			assert( j == n );

			return coefs;
		}
		
		return null;
	}

	/**
	 * Clustering coefficient for one node of the graph.
	 * @param node The node to compute the clustering coefficient for.
	 * @return The clustering coefficient for this node.
	 * @deprecated See {@link org.miv.graphstream.algorithm.Algorithms}
	 */
	@Deprecated
	public double getClusteringCoefficient( Node node )
	{
		double coef = 0.0;
		int    n    = node.getDegree();

		if( n > 1 )
		{
			// Collect the neighbor nodes.

			DefaultNode          nodes[] = new DefaultNode[n];
			HashSet<DefaultEdge> set     = new HashSet<DefaultEdge>();
			int           i       = 0;

			for( Edge e: node.getEdgeSet() )
			{
				nodes[i++] = (DefaultNode) e.getOpposite( node );
			}

			// Count the number of edges between these nodes.

			for( i=0; i<n; ++i )	// For all neighbor nodes.
			{
				for( int j=0; j<n; ++j )	// For all other nodes of this clique.
				{
					if( j != i )
					{
						Edge e = nodes[j].getEdgeToward( nodes[i].getId() );

						if( e != null )
						{
							if( ! set.contains( e ) )
								set.add( (DefaultEdge)e );
						}
					}
				}
			}

			double ne  = set.size();
			double max = ( n * ( n - 1 ) ) / 2;

			coef = ne / max;
		}

		return coef;
	}
	
	/*
	public Algorithms algorithm()
	{
		if( algos == null )
			algos = new Algorithms( this );
		
		return algos;
	}
	*/
	
	/* (non-Javadoc)
	 * @see org.miv.graphstream.graph.GraphInterface#toString()
	 */
	@Override
	public String toString()
	{
		return String.format( "[graph %s (%d nodes %d edges)]", getId(),
				nodes.size(), edges.size() );
	}


	/**
	 * An internal class that represent an iterator able to browse edge or node sets
	 * and to remove correctly elements. This is tricky since removing a node or edge
	 * does more than only altering the node or edge sets.
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
					graph.removeNode_( current.getId(), true );
					iterator.remove();
				}
			}
			else	// On edges.
			{
				if( current != null )
				{
					graph.removeEdge_( current.getId(), true );
					iterator.remove();
				}
			}
		}
	}
	
	
//-------------------------Events Management------------------------
	
	/**
	 * Interface that provide general purpose classification for evens involved
	 * in graph modifications
	 * @author Yoann Pigné
	 * 
	 */
	interface GraphEvent
	{
	}

	class AfterEdgeAddEvent 
		implements GraphEvent
	{
		Edge edge;

		AfterEdgeAddEvent( Edge edge )
		{
			this.edge = edge;
		}
	}

	class BeforeEdgeRemoveEvent 
		implements GraphEvent
	{
		Edge edge;

		BeforeEdgeRemoveEvent( Edge edge )
		{
			this.edge = edge;
		}
	}

	class AfterNodeAddEvent 
		implements GraphEvent
	{
		Node node;

		AfterNodeAddEvent( Node node )
		{
			this.node = node;
		}
	}

	class BeforeNodeRemoveEvent 
		implements GraphEvent
	{
		Node node;

		BeforeNodeRemoveEvent( Node node )
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