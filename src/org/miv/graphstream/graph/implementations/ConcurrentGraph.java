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
import java.util.Collection;
import java.util.Collections;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

//import org.miv.graphstream.algorithm.Algorithms;
import org.miv.graphstream.graph.Element;
import org.miv.graphstream.graph.Edge;
import org.miv.graphstream.graph.EdgeFactory;
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

public class ConcurrentGraph 
	extends AbstractConcurrentElement 
	implements Graph
{

//	protected Algorithms algos;
	
	protected ConcurrentHashMap<String,Node> nodes;
	protected ConcurrentHashMap<String,Edge> edges;
	
	protected ConcurrentLinkedQueue<GraphEvent> eventQueue;
	protected boolean processEvent = false;
	
	protected NodeFactory nodeFactory;
	protected EdgeFactory edgeFactory;
	
	protected boolean strictChecking;
	protected boolean autoCreate;
	
	protected ConcurrentLinkedQueue<GraphListener> listeners;
	
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
			public Edge newInstance( String id, Node src, Node trg )
			{
				return new ConcurrentEdge(id,src,trg);
			}
		};
		
		eventQueue = new ConcurrentLinkedQueue<GraphEvent>();
		
		listeners = new ConcurrentLinkedQueue<GraphListener>();
	}
	
	protected Edge createEdge( String id, Node source, Node target, boolean directed )
		throws SingletonException
	{
		if( edges.containsKey(id) && strictChecking )
			throw new SingletonException( String.format( "edge \"%s\" already exists", id ) );
		
		Edge e = edgeFactory.newInstance(id,source,target);
		e.setDirected(directed);
		
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
	protected void attributeChanged(String attribute, Object oldValue,
			Object newValue)
	{
		attributeChangedEvent( this, attribute, oldValue, newValue );
	}
	
// --- //
	
// --- Graph implementation --- //

	/* @Override */
	public Edge addEdge(String id, String node1, String node2)
			throws SingletonException, NotFoundException
	{
		return addEdge( id, node1, node2, false );
	}

	/* @Override */
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

	/* @Override */
	public void addGraphListener(GraphListener listener)
	{
		listeners.add(listener);
	}

	/* @Override */
	public Node addNode(String id)
		throws SingletonException
	{
		if( nodes.containsKey(id) && strictChecking )
			throw new SingletonException( String.format( "node \"%s\" already exists", id ) );
		
		return createNode(id);
	}
/*
	public Algorithms algorithm()
	{
		return algos;
	}
*/
	/* @Override */
	public void clear()
	{
		graphClearEvent();
		for( Node n : nodes.values() ) removeNode(n);
	}

	/* @Override */
	public void clearListeners()
	{
		listeners.clear();
	}

	/* @Override */
	public GraphViewerRemote display()
	{
		return display(true);
	}

	/* @Override */
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

	/* @Override */
	public Edge getEdge(String id)
	{
		return edges.get(id);
	}

	/* @Override */
	public int getEdgeCount()
	{
		return edges.size();
	}

	/* @Override */
	public Iterator<? extends Edge> getEdgeIterator()
	{
		return Collections.unmodifiableCollection(edges.values()).iterator();
	}

	/* @Override */
	public Collection<? extends Edge> getEdgeSet()
	{
		return Collections.unmodifiableCollection(edges.values());
	}

	/* @Override */
	public List<GraphListener> getGraphListeners()
	{
		throw new UnsupportedOperationException( "Method not implemented." );
	}

	/* @Override */
	public Node getNode(String id)
	{
		return nodes.get(id);
	}

	/* @Override */
	public int getNodeCount()
	{
		return nodes.size();
	}

	/* @Override */
	public Iterator<? extends Node> getNodeIterator()
	{
		return Collections.unmodifiableCollection(nodes.values()).iterator();
	}

	/* @Override */
	public Collection<? extends Node> getNodeSet() {
		// TODO Auto-generated method stub
		return null;
	}

	/* @Override */
	public boolean isAutoCreationEnabled()
	{
		return autoCreate;
	}

	/* @Override */
	public boolean isStrictCheckingEnabled()
	{
		return strictChecking;
	}

	/* @Override */
	public NodeFactory nodeFactory()
	{
		return nodeFactory;
	}
	
	public void setNodeFactory( NodeFactory nf )
	{
		this.nodeFactory = nf;
	}

	/* @Override */
	public void read(String filename) 
		throws IOException, GraphParseException, NotFoundException
	{
		GraphReaderListenerHelper listener = new GraphReaderListenerHelper( this );
		GraphReader reader = GraphReaderFactory.readerFor( filename );
		reader.addGraphReaderListener( listener );
		reader.read( filename );
	}

	/* @Override */
	public void read(GraphReader reader, String filename) 
		throws IOException, GraphParseException
	{
		GraphReaderListenerHelper listener = new GraphReaderListenerHelper( this );
		reader.addGraphReaderListener( listener );
		reader.read( filename );
	}

	/* @Override */
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

	/* @Override */
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

	/* @Override */
	public Edge removeEdge(String id)
		throws NotFoundException
	{
		Edge e = removeEdge(edges.get(id));
		
		if( e == null )
			throw new NotFoundException( String.format( "edge \"%s\" does not exist", id ) );
		
		return e;
	}

	/* @Override */
	public void removeGraphListener(GraphListener listener)
	{
		listeners.remove(listener);
	}

	/* @Override */
	public Node removeNode(String id)
		throws NotFoundException
	{
		Node n = removeNode(nodes.get(id));
		
		if( n == null )
			throw new NotFoundException( String.format( "node \"%s\" does not exist", id ) );
		
		return n;
	}

	/* @Override */
	public void setAutoCreate(boolean on)
	{
		autoCreate = on;
	}

	/* @Override */
	public void setStrictChecking(boolean on)
	{
		strictChecking = on;
	}

	/* @Override */
	public void stepBegins(double time)
	{
		for(GraphListener l : listeners)
			l.stepBegins( this, time );
	}

	/* @Override */
	public void write(String filename) 
		throws IOException
	{
		GraphWriterHelper gwh = new GraphWriterHelper( this );
		gwh.write( filename );
	}

	/* @Override */
	public void write(GraphWriter writer, String filename) 
		throws IOException
	{
		GraphWriterHelper gwh = new GraphWriterHelper( this );
		gwh.write( filename, writer );
	}
	
// --- //

	protected void attributeChangedEvent( Element elt, String key, Object oldValue, Object newValue )
	{
		GraphEvent ev = new AttributeChangedEvent(elt,key,oldValue,newValue);
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
	
	protected void nodeRemovedEvent( Node n )
	{
		GraphEvent ev = new BeforeNodeRemoveEvent(n);
		processEvent(ev);
	}
	
	protected void nodeAddedEvent( Node n )
	{
		GraphEvent ev = new AfterNodeAddEvent(n);
		processEvent(ev);
	}
	
	protected void graphClearEvent()
	{
		GraphEvent ev = new BeforeGraphClearEvent();
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
			for( GraphListener l : listeners )
				l.afterEdgeAdd(ConcurrentGraph.this, edge);
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
			for( GraphListener l : listeners )
				l.beforeEdgeRemove(ConcurrentGraph.this, edge);
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
			for( GraphListener l : listeners )
				l.afterNodeAdd(ConcurrentGraph.this, node);
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
			for( GraphListener l : listeners )
				l.beforeNodeRemove(ConcurrentGraph.this, node);
		}
	}

	class BeforeGraphClearEvent 
		implements GraphEvent
	{
		public void fire()
		{
			for( GraphListener l : listeners )
				l.beforeGraphClear(ConcurrentGraph.this);
		}
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
		
		public void fire()
		{
			for( GraphListener l : listeners )
				l.attributeChanged(element, attribute, oldValue, newValue);
		}
	}
}
