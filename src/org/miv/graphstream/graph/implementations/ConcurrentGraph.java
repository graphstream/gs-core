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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.miv.graphstream.graph.Edge;
import org.miv.graphstream.graph.EdgeFactory;
import org.miv.graphstream.graph.Graph;
import org.miv.graphstream.graph.GraphListener;
import org.miv.graphstream.graph.GraphAttributesListener;
import org.miv.graphstream.graph.GraphElementsListener;
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

public class ConcurrentGraph 
	extends AbstractConcurrentElement 
	implements Graph
{

	protected ConcurrentHashMap<String,Node> nodes;
	protected ConcurrentHashMap<String,Edge> edges;
	
	protected ConcurrentLinkedQueue<GraphEvent> eventQueue;
	protected boolean processEvent = false;
	
	protected NodeFactory nodeFactory;
	protected EdgeFactory edgeFactory;
	
	protected boolean strictChecking;
	protected boolean autoCreate;
	
	protected ConcurrentLinkedQueue<GraphAttributesListener> alisteners;
	protected ConcurrentLinkedQueue<GraphElementsListener> elisteners;
	
	public ConcurrentGraph()
	{
		this( "" );
	}
	
	public ConcurrentGraph( String id )
	{
		this( id, true, false );
	}
	
	public ConcurrentGraph( boolean strictChecking, boolean autoCreate )
	{
		this( "", strictChecking, autoCreate );
	}
	
	public ConcurrentGraph( String id, boolean strictChecking, boolean autoCreate )
	{
		super( id );
		
		this.strictChecking = strictChecking;
		this.autoCreate = autoCreate;
		
		nodes = new ConcurrentHashMap<String,Node>();
		edges = new ConcurrentHashMap<String,Edge>();
		nodeFactory = new NodeFactory()
		{
			public Node newInstance( String id, Graph graph )
			{
				return new ConcurrentNode(graph,id);
			}
		};
		edgeFactory = new EdgeFactory()
		{
			public Edge newInstance( String id, Node src, Node trg, boolean directed )
			{
				return new ConcurrentEdge(id,src,trg,directed);
			}
		};
		
		eventQueue = new ConcurrentLinkedQueue<GraphEvent>();
		
		alisteners = new ConcurrentLinkedQueue<GraphAttributesListener>();
		elisteners = new ConcurrentLinkedQueue<GraphElementsListener>();
	}
	
	protected Edge createEdge( String id, Node source, Node target, boolean directed )
		throws SingletonException
	{
		if( edges.containsKey(id) && strictChecking )
			throw new SingletonException( String.format( "edge \"%s\" already exists", id ) );
		
		Edge e = edgeFactory.newInstance(id,source,target,directed);
		//e.setDirected(directed);
		
		edges.put( id, e );
		edgeAddedEvent( e );
		
		return e;
	}
	
	protected Node createNode( String id )
	{
		Node n = nodeFactory.newInstance(id,this);
		
		nodes.put( id, n );
		nodeAddedEvent(n);
		
		return n;
	}
	
	protected void checkNodes( String ... ids )
	{
		if( ids != null ) for( String id : ids ) checkNode(id);
	}
	
	protected void checkNode( String id )
		throws NotFoundException
	{
		if( ! nodes.containsKey(id) )
		{
			if( strictChecking )
				throw new NotFoundException( String.format( "node \"%s\" not found (strict checking enable", id ) );
			else if( autoCreate )
				createNode(id);
		}
	}
	
	protected Node removeNode( Node n )
	{
		if( nodes.contains(n) )
		{
			disconnectNode(n);
			
			nodeRemovedEvent(n);
			nodes.remove(n.getId());
			
			return n;
		}
		
		return null;
	}
	
	protected void disconnectNode( Node n )
	{
		Iterator<? extends Edge> ite = n.getEdgeIterator();
		while( ite.hasNext() ) removeEdge( ite.next() );
	}
	
	protected Edge removeEdge( Edge e )
	{
		if( edges.contains(e) )
		{
			edgeRemovedEvent(e);
			
			if( e.getSourceNode() instanceof ConcurrentNode )
				((ConcurrentNode)e.getSourceNode()).unregisterEdge(e);
			if( e.getTargetNode() instanceof ConcurrentNode )
				((ConcurrentNode)e.getTargetNode()).unregisterEdge(e);
			
			edges.remove(e.getId());
			
			return e;
		}
		
		return null;
	}
	
// --- AbstractConcurrentElement implementation --- //
	
	@Override
	protected void attributeChanged( String attribute, Object oldValue,
			Object newValue )
	{
		
	}

	@Override
	protected void attributeAdded( String attribute, Object value )
	{
		
	}
	
	@Override
	protected void attributeRemoved( String attribute )
	{
		
	}
	
// --- //
	
// --- Graph implementation --- //

	public Edge addEdge(String id, String node1, String node2)
			throws SingletonException, NotFoundException
	{
		return addEdge( id, node1, node2, false );
	}

	public Edge addEdge(String id, String from, String to, boolean directed)
			throws SingletonException, NotFoundException
	{
		checkNodes(from,to);
		
		Node n0 = nodes.get(from);
		Node n1 = nodes.get(to);
		
		if( n0 == null || n1 == null )
			throw new NotFoundException( String.format( "node \"%s\" or \"%s\" not found", from, to ) );
		
		return createEdge( id, n0, n1, directed );
	}

	public void addGraphListener(GraphListener listener)
	{
		addGraphAttributesListener(listener);
		addGraphElementsListener(listener);
	}
	
	public void addGraphAttributesListener( GraphAttributesListener listener )
	{
		alisteners.add(listener);
	}
	
	public void addGraphElementsListener( GraphElementsListener listener )
	{
		elisteners.add(listener);
	}

	public Node addNode(String id)
		throws SingletonException
	{
		if( nodes.containsKey(id) && strictChecking )
			throw new SingletonException( String.format( "node \"%s\" already exists", id ) );
		
		return createNode(id);
	}

	public void clear()
	{
		graphClearedEvent();
		for( Node n : nodes.values() ) removeNode(n);
	}

	public void clearListeners()
	{
		alisteners.clear();
		elisteners.clear();
	}

	public GraphViewerRemote display()
	{
		return display(true);
	}

	public GraphViewerRemote display(boolean autoLayout)
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

	/* @Override */
	public EdgeFactory edgeFactory()
	{
		return edgeFactory;
	}
	
	public void setEdgeFactory( EdgeFactory ef )
	{
		this.edgeFactory = ef;
	}

	public Edge getEdge(String id)
	{
		return edges.get(id);
	}

	public int getEdgeCount()
	{
		return edges.size();
	}

	public Iterator<? extends Edge> getEdgeIterator()
	{
		return Collections.unmodifiableCollection(edges.values()).iterator();
	}

	public Iterable<Edge> edgeSet()
	{
		return Collections.unmodifiableCollection(edges.values());
	}

	public List<GraphListener> getGraphListeners()
	{
		throw new UnsupportedOperationException( "Method not implemented." );
	}

	public List<GraphAttributesListener> getGraphAttributesListeners()
	{
		throw new UnsupportedOperationException( "Method not implemented." );
	}

	public List<GraphElementsListener> getGraphElementsListeners()
	{
		throw new UnsupportedOperationException( "Method not implemented." );
	}

	public Node getNode(String id)
	{
		return nodes.get(id);
	}

	public int getNodeCount()
	{
		return nodes.size();
	}

	public Iterator<? extends Node> getNodeIterator()
	{
		return Collections.unmodifiableCollection(nodes.values()).iterator();
	}
	
	public Iterator<Node> iterator()
	{
		return Collections.unmodifiableCollection(nodes.values()).iterator();
	}

	public Iterable<Node> nodeSet()
	{
		return Collections.unmodifiableCollection(nodes.values());
	}

	public boolean isAutoCreationEnabled()
	{
		return autoCreate;
	}

	public boolean isStrict()
	{
		return strictChecking;
	}

	public NodeFactory nodeFactory()
	{
		return nodeFactory;
	}
	
	public void setNodeFactory( NodeFactory nf )
	{
		this.nodeFactory = nf;
	}
/*
	public void read(String filename) 
		throws IOException, GraphParseException, NotFoundException
	{
		GraphReaderListenerHelper listener = new GraphReaderListenerHelper( this );
		GraphReader reader = GraphReaderFactory.readerFor( filename );
		reader.addGraphReaderListener( listener );
		reader.read( filename );
	}

	public void read(GraphReader reader, String filename) 
		throws IOException, GraphParseException
	{
		GraphReaderListenerHelper listener = new GraphReaderListenerHelper( this );
		reader.addGraphReaderListener( listener );
		reader.read( filename );
	}

	public int readPositionFile(String posFileName)
		throws IOException
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

				Node node = nodes.get( id );

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
	public Edge removeEdge(String from, String to)
		throws NotFoundException
	{
		Node source = nodes.get(from);
		
		if( source == null )
			throw new NotFoundException( String.format( "node \"%s\" does not exist", from ) );
		if( ! nodes.containsKey(to) )
			throw new NotFoundException( String.format( "node \"%s\" does not exist", to ) );
		
		Edge e = removeEdge(source.getEdgeToward(to));
		
		if( e == null )
			throw new NotFoundException( String.format( "edge from \"%s\" to \"%s\" does not exist", from, to ) );
		
		return e;
	}

	public Edge removeEdge(String id)
		throws NotFoundException
	{
		Edge e = removeEdge(edges.get(id));
		
		if( e == null )
			throw new NotFoundException( String.format( "edge \"%s\" does not exist", id ) );
		
		return e;
	}

	public void removeGraphListener(GraphListener listener)
	{
		removeGraphAttributesListener(listener);
		removeGraphElementsListener(listener);
	}

	public void removeGraphAttributesListener(GraphAttributesListener listener)
	{
		alisteners.remove(listener);
	}

	public void removeGraphElementsListener(GraphElementsListener listener)
	{
		elisteners.remove(listener);
	}

	public Node removeNode(String id)
		throws NotFoundException
	{
		Node n = removeNode(nodes.get(id));
		
		if( n == null )
			throw new NotFoundException( String.format( "node \"%s\" does not exist", id ) );
		
		return n;
	}

	public void setAutoCreate(boolean on)
	{
		autoCreate = on;
	}

	public void setStrict(boolean on)
	{
		strictChecking = on;
	}

	public void stepBegins(double time)
	{
		for(GraphElementsListener l : elisteners)
			l.stepBegins( getId(), time );
	}
/*
	public void write(String filename) 
		throws IOException
	{
		GraphWriterHelper gwh = new GraphWriterHelper( this );
		gwh.write( filename );
	}

	public void write(GraphWriter writer, String filename) 
		throws IOException
	{
		GraphWriterHelper gwh = new GraphWriterHelper( this );
		gwh.write( filename, writer );
	}
*/

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
	
// --- //
/*
	protected void attributeChangedEvent( Element elt, String key, Object oldValue, Object newValue )
	{
		GraphEvent ev = new AttributeChangedEvent(elt,key,oldValue,newValue);
		processEvent(ev);
	}
	*/
	
	protected void nodeAddedEvent( Node n )
	{
		GraphEvent ev = new AfterNodeAddEvent(n);
		processEvent(ev);
	}
	
	protected void nodeAttributeAddedEvent( Node n, String key, Object value )
	{
		GraphEvent ev = new NodeAttributeAddedEvent(n.getId(),key,value);
		processEvent(ev);
	}
	
	protected void nodeAttributeChangedEvent( Node n, String key, Object oldValue, Object newValue )
	{
		GraphEvent ev = new NodeAttributeChangedEvent(n.getId(),key,oldValue,newValue);
		processEvent(ev);
	}
	
	protected void nodeAttributeRemovedEvent( Node n, String key )
	{
		GraphEvent ev = new NodeAttributeRemovedEvent(n.getId(),key);
		processEvent(ev);
	}
	
	protected void nodeRemovedEvent( Node n )
	{
		GraphEvent ev = new BeforeNodeRemoveEvent(n);
		processEvent(ev);
	}
	
	protected void edgeAttributeAddedEvent( Edge e, String key, Object value )
	{
		GraphEvent ev = new EdgeAttributeAddedEvent(e.getId(),key,value);
		processEvent(ev);
	}
	
	protected void edgeAttributeChangedEvent( Edge e, String key, Object oldValue, Object newValue )
	{
		GraphEvent ev = new EdgeAttributeChangedEvent(e.getId(),key,oldValue,newValue);
		processEvent(ev);
	}
	
	protected void edgeAttributeRemovedEvent( Edge e, String key )
	{
		GraphEvent ev = new EdgeAttributeRemovedEvent(e.getId(),key);
		processEvent(ev);
	}
	
	protected void edgeRemovedEvent( Edge e )
	{
		GraphEvent ev = new BeforeEdgeRemoveEvent(e);
		processEvent(ev);
	}
	
	protected void edgeAddedEvent( Edge e )
	{
		GraphEvent ev = new AfterEdgeAddEvent(e);
		processEvent(ev);
	}
	
	protected void graphClearedEvent()
	{
		GraphEvent ev = new GraphClearedEvent();
		processEvent(ev);
	}
	
	protected void processEvent( GraphEvent ... add )
	{
		if( ! processEvent )
		{
			processEvent = true;
		
			if( add != null && add.length > 0 )
				for( GraphEvent event : add ) eventQueue.add(event);
		
			while( eventQueue.size() > 0 )
				eventQueue.poll().fire();
		
			processEvent = false;
		}
		else
		{
			if( add != null && add.length > 0 )
				for( GraphEvent event : add ) eventQueue.add(event);
		}
	}
	
	/**
	 * Interface that provide general purpose classification for evens involved
	 * in graph modifications
	 * @author Yoann Pigné
	 * @author Guilhelm Savin
	 * 
	 */
	interface GraphEvent
	{
		void fire();
	}

	class AfterEdgeAddEvent 
		implements GraphEvent
	{
		Edge edge;

		AfterEdgeAddEvent( Edge edge )
		{
			this.edge = edge;
		}
		
		public void fire()
		{
			for( GraphElementsListener l : elisteners )
				l.edgeAdded(getId(),edge.getId(),edge.getSourceNode().getId(),edge.getTargetNode().getId(),edge.isDirected());
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
		
		public void fire()
		{
			for( GraphElementsListener l : elisteners )
				l.edgeRemoved(ConcurrentGraph.this.getId(), edge.getId());
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
		
		public void fire()
		{
			for( GraphElementsListener l : elisteners )
				l.nodeAdded(ConcurrentGraph.this.getId(), node.getId());
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
		
		public void fire()
		{
			for( GraphElementsListener l : elisteners )
				l.nodeRemoved(ConcurrentGraph.this.getId(), node.getId());
		}
	}
	
	class GraphClearedEvent 
		implements GraphEvent
	{
		public void fire()
		{
			for( GraphElementsListener l : elisteners )
				l.graphCleared(ConcurrentGraph.this.getId());
		}
	}
	/*
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
		
		public void fire()
		{
			for( GraphAttributesListener l : alisteners )
			{
				//l.attributeChanged(element, attribute, oldValue, newValue);
				
			}
		}
	}
	*/
	class NodeAttributeAddedEvent
		implements GraphEvent
	{
		String id;
		String key;
		
		Object value;
		
		public NodeAttributeAddedEvent( String id, String key, Object value )
		{
			this.id = id;
			this.key = key;
			this.value = value;
		}
		
		public void fire()
		{
			for( GraphAttributesListener l : alisteners )
				l.nodeAttributeAdded(ConcurrentGraph.this.getId(), id, key, value);
		}
	}
	
	class NodeAttributeRemovedEvent
		implements GraphEvent
	{
		String id;
		String key;
		
		public NodeAttributeRemovedEvent( String id, String key )
		{
			this.id = id;
			this.key = key;
		}
		
		public void fire()
		{
			for( GraphAttributesListener l : alisteners )
				l.nodeAttributeRemoved(ConcurrentGraph.this.getId(), id, key);
		}
	}
	
	class NodeAttributeChangedEvent
		implements GraphEvent
	{
		String id;
		String key;
		
		Object oldValue;
		Object newValue;
		
		public NodeAttributeChangedEvent( String id, String key, Object oldValue, Object newValue )
		{
			this.id = id;
			this.key = key;
			this.oldValue = oldValue;
			this.newValue = newValue;
		}
		
		public void fire()
		{
			for( GraphAttributesListener l : alisteners )
				l.nodeAttributeChanged(ConcurrentGraph.this.getId(), id, key, oldValue, newValue);
		}
	}
	
	class EdgeAttributeAddedEvent
		implements GraphEvent
	{
		String id;
		String key;
		
		Object value;
		
		public EdgeAttributeAddedEvent( String id, String key, Object value )
		{
			this.id = id;
			this.key = key;
			this.value = value;
		}
		
		public void fire()
		{
			for( GraphAttributesListener l : alisteners )
				l.edgeAttributeAdded(ConcurrentGraph.this.getId(), id, key, value);
		}
	}
	
	class EdgeAttributeRemovedEvent
		implements GraphEvent
	{
		String id;
		String key;
		
		public EdgeAttributeRemovedEvent( String id, String key )
		{
			this.id = id;
			this.key = key;
		}
		
		public void fire()
		{
			for( GraphAttributesListener l : alisteners )
				l.edgeAttributeRemoved(ConcurrentGraph.this.getId(), id, key);
		}
	}
	
	class EdgeAttributeChangedEvent
		implements GraphEvent
	{
		String id;
		String key;
		
		Object oldValue;
		Object newValue;
		
		public EdgeAttributeChangedEvent( String id, String key, Object oldValue, Object newValue )
		{
			this.id = id;
			this.key = key;
			this.oldValue = oldValue;
			this.newValue = newValue;
		}
		
		public void fire()
		{
			for( GraphAttributesListener l : alisteners )
				l.edgeAttributeChanged(ConcurrentGraph.this.getId(), id, key, oldValue, newValue);
		}
	}
	
	class GraphAttributeAddedEvent
		implements GraphEvent
	{
		String key;
		
		Object value;
		
		public GraphAttributeAddedEvent( String key, Object value )
		{
			this.key = key;
			this.value = value;
		}
		
		public void fire()
		{
			for( GraphAttributesListener l : alisteners )
				l.graphAttributeAdded(ConcurrentGraph.this.getId(), key, value);
		}
	}
	
	class GraphAttributeRemovedEvent
		implements GraphEvent
	{
		String key;
		
		public GraphAttributeRemovedEvent( String key )
		{
			this.key = key;
		}
		
		public void fire()
		{
			for( GraphAttributesListener l : alisteners )
				l.graphAttributeRemoved(ConcurrentGraph.this.getId(), key);
		}
	}
	
	class GraphAttributeChangedEvent
		implements GraphEvent
	{
		String key;
		
		Object oldValue;
		Object newValue;
		
		public GraphAttributeChangedEvent( String key, Object oldValue, Object newValue )
		{
			this.key = key;
			this.oldValue = oldValue;
			this.newValue = newValue;
		}
		
		public void fire()
		{
			for( GraphAttributesListener l : alisteners )
				l.graphAttributeChanged(ConcurrentGraph.this.getId(), key, oldValue, newValue);
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