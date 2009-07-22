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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.miv.graphstream.graph.Edge;
import org.miv.graphstream.graph.EdgeFactory;
import org.miv.graphstream.graph.Element;
import org.miv.graphstream.graph.Graph;
import org.miv.graphstream.graph.GraphAttributesListener;
import org.miv.graphstream.graph.GraphElementsListener;
import org.miv.graphstream.graph.GraphListener;
import org.miv.graphstream.graph.Node;
import org.miv.graphstream.graph.NodeFactory;
import org.miv.graphstream.io.GraphParseException;
import org.miv.graphstream.io2.file.FileInput;
import org.miv.graphstream.io2.file.FileInputFactory;
import org.miv.graphstream.io2.file.FileOutput;
import org.miv.graphstream.io2.file.FileOutputFactory;
import org.miv.graphstream.ui.GraphViewer;
import org.miv.graphstream.ui.GraphViewerRemote;
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
	 * A queue that allow the management of events (nodes/edge add/delete/change) in the right order. 
	 */
	protected LinkedList<GraphEvent> eventQueue = new LinkedList<GraphEvent>();
	
	/**
	 * A boolean that indicates whether or not an GraphListener event is being sent during another one. 
	 */
	protected boolean eventProcessing = false;

	/**
	 * Set of graph attributes listeners.
	 */
	protected ArrayList<GraphAttributesListener> attrListeners = new ArrayList<GraphAttributesListener>();
	
	/**
	 * set of graph elements listeners.
	 */
	protected ArrayList<GraphElementsListener> eltsListeners = new ArrayList<GraphElementsListener>();

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
	 * List of listeners to remove if the {@link #removeGraphListener(GraphListener)} is called
	 * inside from the listener. This can happen !! We create this list on demand.
	 */
	protected ArrayList<Object> listenersToRemove;

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
	 * @see #setStrict(boolean)
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
	 * @see #setStrict(boolean)
	 * @see #setAutoCreate(boolean)
	 */
	public AdjacencyListGraph( String id, boolean strictChecking, boolean autoCreate )
	{
		super( id );
		setStrict( strictChecking );
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
			public Edge newInstance( String id, Node src, Node trg, boolean directed )
			{
				return new AdjacencyListEdge(id,src,trg,directed);
			}
		};
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

	protected Edge addEdge_( String tag, String from, String to, boolean directed ) throws SingletonException, NotFoundException
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
					edge = edgeFactory.newInstance(tag,src,trg,directed);
					//edge.setDirected( directed );
					
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

	/**
	 * @complexity O(log(n)) with n be the number of edges in the graph.
	 */
	public Edge addEdge( String id, String from, String to, boolean directed ) throws SingletonException, NotFoundException
	{
		Edge e = addEdge_( id, from, to, directed );
		return e;
	}

	/**
	 * @complexity O(log(n)) with n be the number of nodes in the graph.
	 */
	public Node addNode( String id ) throws SingletonException
	{
		Node n = addNode_( id );
		
		return n;
	}

	protected Node addNode_( String tag ) throws SingletonException
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

	/**
	 * @complexity O(1).
	 */
	public void clear()
	{
		for( GraphElementsListener listener: eltsListeners )
			listener.graphCleared( getId() );

		nodes.clear();
		edges.clear();
		
/* Generates a ConcurrentModificationException :
		for( String n: nodes.keySet() )
		{
			removeNode( n );
		}
*/
	}

	/**
	 * @complexity O(1).
	 */
	public void clearListeners()
	{
		attrListeners.clear();
		eltsListeners.clear();
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
		return attrListeners;
	}
	
	public Iterable<GraphElementsListener> getGraphElementsListeners()
	{
		return eltsListeners;
	}

	/**
	 * @complexity O( 2*log(n)+log(m) ) with n the number of nodes and m the number of edges in the graph.
	 */
	public Edge removeEdge( String from, String to ) throws NotFoundException
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

	/**
	 * @complexity O( 2*log(m) ) with  m the number of edges in the graph.
	 */
	public Edge removeEdge( String id ) throws NotFoundException
	{
		Edge edge = lookForEdge( id );
		if( edge != null )
		{
			removeEdge( edge );
		}
		return edge;
	}

	/**
	 * Removes an edge from a given reference to it.
	 * @param edge The reference of the edge to remove.
	 * @complexity O( log(m) ) with  m the number of edges in the graph.
	 */
	public Edge removeEdge( Edge edge ) throws NotFoundException
	{
		beforeEdgeRemoveEvent( (AdjacencyListEdge) edge );
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
			return removeNode( node );
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
		for( GraphElementsListener l : eltsListeners )
			l.stepBegins( getId(), time );
	}
	
	/**
	 * When a node is deregistered from a graph, it must not keep edges
	 * connected to nodes still in the graph. This methods unbind all edges
	 * connected to this node (this also deregister them from the graph).
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

	/**
	 * @complexity 0( 1).
	 */
	public void addGraphListener( GraphListener listener )
	{
		attrListeners.add( listener );
		eltsListeners.add( listener );
	}
	
	public void addGraphAttributesListener( GraphAttributesListener listener )
	{
		attrListeners.add( listener );
	}
	
	public void addGraphElementsListener( GraphElementsListener listener )
	{
		eltsListeners.add( listener );
	}
	
	public void removeGraphListener( GraphListener listener )
	{
		if( eventProcessing )
		{
			// We cannot remove the listener while processing events !!!
			removeListenerLater( listener );
		}
		else
		{
			int index = attrListeners.lastIndexOf( listener );

			if( index >= 0 )
				attrListeners.remove( index );
			
			index = eltsListeners.lastIndexOf( listener );
			
			if( index >= 0 )
				eltsListeners.remove( index );
		}
	}
	
	public void removeGraphAttributesListener( GraphAttributesListener listener )
	{
		if( eventProcessing )
		{
			// We cannot remove the listener while processing events !!!
			removeListenerLater( listener );
		}
		else
		{
			int index = attrListeners.lastIndexOf( listener );

			if( index >= 0 )
				attrListeners.remove( index );
		}		
	}
	
	public void removeGraphElementsListener( GraphElementsListener listener )
	{
		if( eventProcessing )
		{
			// We cannot remove the listener while processing events !!!
			removeListenerLater( listener );
		}
		else
		{
			int index = eltsListeners.lastIndexOf( listener );

			if( index >= 0 )
				eltsListeners.remove( index );
		}		
	}

	protected void removeListenerLater( Object listener )
	{
		if( listenersToRemove == null )
			listenersToRemove = new ArrayList<Object>();
		
		listenersToRemove.add( listener );	
	}
	
	protected void checkListenersToRemove()
	{
		if( listenersToRemove != null && listenersToRemove.size() > 0 )
		{
			for( Object listener: listenersToRemove )
			{
				if( listener instanceof GraphListener )
					removeGraphListener( (GraphListener) listener );
				else if( listener instanceof GraphAttributesListener )
					removeGraphAttributesListener( (GraphAttributesListener) listener );
				else if( listener instanceof GraphElementsListener )
					removeGraphElementsListener( (GraphElementsListener) listener );
			}

			listenersToRemove.clear();
			listenersToRemove = null;
		}
	}

	/**
	 * If in "event processing mode", ensure all pending events are processed.
	 */
	protected void manageEvents()
	{
		if( eventProcessing )
		{
			while( ! eventQueue.isEmpty() )
				manageEvent( eventQueue.remove() );
		}
	}

	protected void afterNodeAddEvent( AdjacencyListNode node )
	{
		if(!eventProcessing)
		{
			eventProcessing=true;
			manageEvents();
			for( GraphElementsListener l: eltsListeners )
				l.nodeAdded( getId(), node.getId() );
			manageEvents();
			eventProcessing=false;
			checkListenersToRemove();
		}
		else 
		{
			eventQueue.add( new AfterNodeAddEvent(node) );
		}
	}

	protected void beforeNodeRemoveEvent( AdjacencyListNode node )
	{
		if(!eventProcessing)
		{
			eventProcessing=true;
			manageEvents();
			for( GraphElementsListener l: eltsListeners )
				l.nodeRemoved( getId(), node.getId() );
			manageEvents();
			eventProcessing=false;
			checkListenersToRemove();
		}
		else 
		{
			eventQueue.add( new BeforeNodeRemoveEvent( node ) );
		}
	}

	protected void afterEdgeAddEvent( AdjacencyListEdge edge )
	{
		if(!eventProcessing)
		{
			eventProcessing=true;
			manageEvents();
			for( GraphElementsListener l: eltsListeners )
				l.edgeAdded( getId(), edge.getId(), edge.getNode0().getId(), edge.getNode1().getId(), edge.isDirected() );
			manageEvents();
			eventProcessing=false;
			checkListenersToRemove();
		}
		else 
		{
			eventQueue.add( new AfterEdgeAddEvent(edge) );
		}
	}

	protected void beforeEdgeRemoveEvent( AdjacencyListEdge edge )
	{
		if(!eventProcessing)
		{
			eventProcessing=true;
			manageEvents();
			for( GraphElementsListener l: eltsListeners )
				l.edgeRemoved( getId(), edge.getId() );
			manageEvents();
			eventProcessing=false;
			checkListenersToRemove();
		}
		else
		{
			eventQueue.add( new BeforeEdgeRemoveEvent( edge ) );
		}
	}

	@Override
	protected void attributeChanged( String attribute, AttributeChangeEvent event, Object oldValue, Object newValue )
	{
		attributeChangedEvent( this, attribute, event, oldValue, newValue );
	}

	protected void attributeChangedEvent( Element element, String attribute, AttributeChangeEvent event, Object oldValue, Object newValue )
	{
		if(!eventProcessing)
		{
			eventProcessing=true;
			manageEvents();
	
			if( event == AttributeChangeEvent.ADD )
			{
				if( element instanceof Node )
				{
					for( GraphAttributesListener l: attrListeners )
						l.nodeAttributeAdded( getId(), element.getId(), attribute, newValue );
				}
				else if( element instanceof Edge )
				{
					for( GraphAttributesListener l: attrListeners )
						l.edgeAttributeAdded( getId(), element.getId(), attribute, newValue );					
				}
				else
				{
					for( GraphAttributesListener l: attrListeners )
						l.graphAttributeAdded( getId(), attribute, newValue );					
				}
			}
			else if( event == AttributeChangeEvent.REMOVE )
			{
				if( element instanceof Node )
				{
					for( GraphAttributesListener l: attrListeners )
						l.nodeAttributeRemoved( getId(), element.getId(), attribute );
				}
				else if( element instanceof Edge )
				{
					for( GraphAttributesListener l: attrListeners )
						l.edgeAttributeRemoved( getId(), element.getId(), attribute );					
				}
				else
				{
					for( GraphAttributesListener l: attrListeners )
						l.graphAttributeRemoved( getId(), attribute );					
				}								
			}
			else
			{
				if( element instanceof Node )
				{
					for( GraphAttributesListener l: attrListeners )
						l.nodeAttributeChanged( getId(), element.getId(), attribute, oldValue, newValue );
				}
				else if( element instanceof Edge )
				{
					for( GraphAttributesListener l: attrListeners )
						l.edgeAttributeChanged( getId(), element.getId(), attribute, oldValue, newValue );					
				}
				else
				{
					for( GraphAttributesListener l: attrListeners )
						l.graphAttributeChanged( getId(), attribute, oldValue, newValue );					
				}				
			}
	
			manageEvents();
			eventProcessing=false;
			checkListenersToRemove();
		}
		else
		{
			eventQueue.add( new AttributeChangedEvent( element, attribute, event, oldValue, newValue ) );
		}
	}

// Commands -- Utility

	public void read( FileInput input, String filename ) throws IOException, GraphParseException
    {
		input.readAll( filename );
    }

	public void read( String filename )
		throws IOException, GraphParseException, NotFoundException
	{
		FileInput input = FileInputFactory.inputFor( filename );
		input.addGraphListener( this );
		read( input, filename );
	}
	
	public void write( FileOutput output, String filename ) throws IOException
    {
		output.writeAll( this, filename );
    }
	
	public void write( String filename )
		throws IOException
	{
		FileOutput output = FileOutputFactory.outputFor( filename );
		write( output, filename );
	}	
/*
	public void read( String filename ) throws IOException, GraphParseException, NotFoundException
	{
		GraphReaderListenerHelper listener = new GraphReaderListenerHelper( this );
		GraphReader reader = GraphReaderFactory.readerFor( filename );
		reader.addGraphReaderListener( listener );
		reader.read( filename );
	}

	public void read( GraphReader reader, String filename ) throws IOException, GraphParseException
	{
		GraphReaderListenerHelper listener = new GraphReaderListenerHelper( this );
		reader.addGraphReaderListener( listener );
		reader.read( filename );
	}

	public void write( String filename ) throws IOException
	{
		GraphWriterHelper gwh = new GraphWriterHelper( this );
		gwh.write( filename );
	}

	public void write( GraphWriter writer, String filename ) throws IOException
	{
		GraphWriterHelper gwh = new GraphWriterHelper( this );
		gwh.write( filename, writer );
	}

	public int readPositionFile( String posFileName ) throws IOException
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
*/
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

// Events Management
	
	/**
	 * Interface that provide general purpose classification for evens involved
	 * in graph modifications
	 */
	interface GraphEvent
	{
	}

	class AfterEdgeAddEvent  implements GraphEvent
	{
		AdjacencyListEdge edge;

		AfterEdgeAddEvent( AdjacencyListEdge edge )
		{
			this.edge = edge;
		}
	}

	class BeforeEdgeRemoveEvent implements GraphEvent
	{
		AdjacencyListEdge edge;

		BeforeEdgeRemoveEvent( AdjacencyListEdge edge )
		{
			this.edge = edge;
		}
	}

	class AfterNodeAddEvent implements GraphEvent
	{
		AdjacencyListNode node;

		AfterNodeAddEvent( AdjacencyListNode node )
		{
			this.node = node;
		}
	}

	class BeforeNodeRemoveEvent implements GraphEvent
	{
		AdjacencyListNode node;

		BeforeNodeRemoveEvent( AdjacencyListNode node )
		{
			this.node = node;
		}
	}

	class BeforeGraphClearEvent implements GraphEvent
	{
	}

	class AttributeChangedEvent implements GraphEvent
	{
		Element element;

		String attribute;
		
		AttributeChangeEvent event;

		Object oldValue;

		Object newValue;

		AttributeChangedEvent( Element element, String attribute, AttributeChangeEvent event, Object oldValue, Object newValue )
		{
			this.element   = element;
			this.attribute = attribute;
			this.event     = event;
			this.oldValue  = oldValue;
			this.newValue  = newValue;
		}
	}
	
	/**
	 * Private method that manages the events stored in the {@link #eventQueue}.
	 * These event where created while being invoked from another event
	 * invocation.
	 * @param event
	 */
	private void manageEvent( GraphEvent event )
	{
		if( event.getClass() == AttributeChangedEvent.class )
		{
			AttributeChangedEvent ev = (AttributeChangedEvent)event;
			
			if( ev.event == AttributeChangeEvent.ADD )
			{
				if( ev.element instanceof Node )
				{
					for( GraphAttributesListener l: attrListeners )
						l.nodeAttributeAdded( getId(), ev.element.getId(), ev.attribute, ev.newValue );
				}
				else if( ev.element instanceof Edge )
				{
					for( GraphAttributesListener l: attrListeners )
						l.edgeAttributeAdded( getId(), ev.element.getId(), ev.attribute, ev.newValue );					
				}
				else
				{
					for( GraphAttributesListener l: attrListeners )
						l.graphAttributeAdded( getId(), ev.attribute, ev.newValue );										
				}
			}
			else if( ev.event == AttributeChangeEvent.REMOVE )
			{
				if( ev.element instanceof Node )
				{
					for( GraphAttributesListener l: attrListeners )
						l.nodeAttributeRemoved( getId(), ev.element.getId(), ev.attribute );
				}
				else if( ev.element instanceof Edge )
				{
					for( GraphAttributesListener l: attrListeners )
						l.edgeAttributeRemoved( getId(), ev.element.getId(), ev.attribute );					
				}
				else
				{
					for( GraphAttributesListener l: attrListeners )
						l.graphAttributeRemoved( getId(), ev.attribute );										
				}
			}
			else
			{
				if( ev.element instanceof Node )
				{
					for( GraphAttributesListener l: attrListeners )
						l.nodeAttributeChanged( getId(), ev.element.getId(), ev.attribute, ev.oldValue, ev.newValue );
				}
				else if( ev.element instanceof Edge )
				{
					for( GraphAttributesListener l: attrListeners )
						l.edgeAttributeChanged( getId(), ev.element.getId(), ev.attribute, ev.oldValue, ev.newValue );					
				}
				else
				{
					for( GraphAttributesListener l: attrListeners )
						l.graphAttributeChanged( getId(), ev.attribute, ev.oldValue, ev.newValue );										
				}				
			}
		}
		
		// Elements events
		
		else if( event.getClass() == AfterEdgeAddEvent.class )
		{
			Edge e = ((AfterEdgeAddEvent)event).edge;
			
			for( GraphElementsListener l: eltsListeners )
				l.edgeAdded( getId(), e.getId(), e.getNode0().getId(), e.getNode1().getId(), e.isDirected() );
		}
		else if( event.getClass() == AfterNodeAddEvent.class )
		{
			for( GraphElementsListener l: eltsListeners )
				l.nodeAdded( getId(), ((AfterNodeAddEvent)event).node.getId() );
		}
		else if( event.getClass() == BeforeEdgeRemoveEvent.class )
		{
			for( GraphElementsListener l: eltsListeners )
				l.edgeRemoved( getId(), ((BeforeEdgeRemoveEvent)event).edge.getId() );
		}
		else if( event.getClass() == BeforeNodeRemoveEvent.class )
		{
			for( GraphElementsListener l: eltsListeners )
				l.nodeRemoved( getId(), ((BeforeNodeRemoveEvent)event).node.getId() );
		}
	}

// Output

	public void edgeAdded( String graphId, String edgeId, String fromNodeId, String toNodeId,
            boolean directed )
    {
		addEdge( edgeId, fromNodeId, toNodeId, directed );
    }

	public void edgeRemoved( String graphId, String edgeId )
    {
		removeEdge( edgeId );
    }

	public void graphCleared()
    {
		clear();
    }

	public void nodeAdded( String graphId, String nodeId )
    {
		addNode( nodeId );
    }

	public void nodeRemoved( String graphId, String nodeId )
    {
		removeNode( nodeId );
    }

	public void stepBegins( String graphId, double time )
    {
		stepBegins( time );
    }

	public void graphCleared( String graphId )
    {
		clear();
    }

	public void edgeAttributeAdded( String graphId, String edgeId, String attribute, Object value )
    {
		Edge edge = getEdge( edgeId );
		
		if( edge != null )
			edge.addAttribute( attribute, value );
    }

	public void edgeAttributeChanged( String graphId, String edgeId, String attribute,
            Object oldValue, Object newValue )
    {
		Edge edge = getEdge( edgeId );
		
		if( edge != null )
			edge.changeAttribute( attribute, newValue );
    }

	public void edgeAttributeRemoved( String graphId, String edgeId, String attribute )
    {
		Edge edge = getEdge( edgeId );
		
		if( edge != null )
			edge.removeAttribute( attribute );
    }

	public void graphAttributeAdded( String graphId, String attribute, Object value )
    {
		addAttribute( attribute, value );
    }

	public void graphAttributeChanged( String graphId, String attribute, Object oldValue,
            Object newValue )
    {
		changeAttribute( attribute, newValue );
    }

	public void graphAttributeRemoved( String graphId, String attribute )
    {
		removeAttribute( attribute );
    }

	public void nodeAttributeAdded( String graphId, String nodeId, String attribute, Object value )
    {
		Node node = getNode( nodeId );
		
		if( node != null )
			node.addAttribute( attribute, value );
    }

	public void nodeAttributeChanged( String graphId, String nodeId, String attribute,
            Object oldValue, Object newValue )
    {
		Node node = getNode( nodeId );
		
		if( node != null )
			node.changeAttribute( attribute, newValue );
    }

	public void nodeAttributeRemoved( String graphId, String nodeId, String attribute )
    {
		Node node = getNode( nodeId );
		
		if( node != null )
			node.removeAttribute( attribute );
    }
}