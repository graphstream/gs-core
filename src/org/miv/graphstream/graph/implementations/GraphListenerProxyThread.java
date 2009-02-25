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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

//import org.miv.graphstream.algorithm.Algorithms;
import org.miv.graphstream.graph.Edge;
import org.miv.graphstream.graph.EdgeFactory;
import org.miv.graphstream.graph.Element;
import org.miv.graphstream.graph.Graph;
import org.miv.graphstream.graph.GraphListener;
import org.miv.graphstream.graph.GraphListenerProxy;
import org.miv.graphstream.graph.Node;
import org.miv.graphstream.graph.NodeFactory;
import org.miv.graphstream.io.GraphParseException;
import org.miv.graphstream.io.GraphReader;
import org.miv.graphstream.io.GraphWriter;
import org.miv.graphstream.ui.GraphViewerRemote;
import org.miv.mbox.CannotPostException;
import org.miv.mbox.MBox;
import org.miv.mbox.MBoxListener;
import org.miv.mbox.MBoxStandalone;
import org.miv.util.NotFoundException;
import org.miv.util.SingletonException;

/**
 * Helper class that allows to listen at a graph across thread boundaries.
 * 
 * <p>
 * This class has two usages. It acts as a proxy that allows to put a listener (or several) for a
 * graph in a separate thread. It can also rebuild a graph from the events into the other thread
 * so that an exact copy is maintained. Lets call the graph witch we listen at the "input graph" and
 * the graph in the other thread the "output graph" (accordingly the input thread and the output
 * thread, etc.).
 * </p>
 * 
 * <p>
 * This class is "passive", you must check that events are available regularly by calling the
 * {@link #checkEvents()} method. This method will check if some events occurred in the input graph
 * and will modify the output graph accordingly (if any) and forward events to each registered
 * graph listener.
 * </p>
 * 
 * <p>
 * Notice that, when listening an input graph without an output graph, some events will have to
 * create "dummy" graphs, nodes and edges in order to return information. For example when you
 * receive a afterEdgeAdd(Graph,Edge) event, the graph is a DummyGraph and the edge is a DummyEdge.
 * The dummy classes contain sufficient informations (identifiers, and for edges the direction and
 * the two node names). 
 * </p>
 * 
 * <p>
 * If you passed an output graph, this one will be maintained according to each change in the input
 * graph. You should always pass true to the "replayGraph" argument of the constructors. This allows
 * to rebuild a partially built input graph into the output graph. The default constructors without
 * this parameter all replay the graph.  
 * </p>
 * 
 * <p>
 * There may be several listeners listening at this proxy. However these listener must be in the
 * same thread.
 * </p>
 * 
 * @see org.miv.graphstream.graph.GraphListener
 * @author Antoine Dutot
 * @author Yoann Pign�
 * @since 20061208
 */
public class GraphListenerProxyThread implements GraphListenerProxy, MBoxListener
{
	// Attributes

	/**
	 * The event sender name, usually the graph name.
	 */
	protected String from;
	
	/**
	 * The message box used to exchange messages between the two threads.
	 */
	protected MBox events;
	
	/**
	 * The layout listeners in another thread than the layout one.
	 */
	protected ArrayList<GraphListener> listeners = new ArrayList<GraphListener>();

	/**
	 * The graph to maintain according to the received events.
	 */
	protected Graph outputGraph;
	
	/**
	 * Used only to remove the listener. We ensure this is done in the Graph thread.
	 */
	protected Graph inputGraph;

	protected boolean unregisterWhenPossible = false;
	
// Constructors

	/**
	 * Listen at an input graph in a given thread and redirect all this events to GraphListeners
	 * that may be in another thread.
	 * By default, if the graph already contains some elements, they are "replayed". This means that
	 * events are sent to mimic the fact they just appeared.
	 * @param inputGraph The graph we listen at.
	 */
	public GraphListenerProxyThread( Graph inputGraph )
	{
		this( inputGraph, true );
	}

	/**
	 * Like {@link #GraphListenerProxyThread(Graph)} but allow to avoid replaying the graph.
	 * @param inputGraph The graph we listen at.
	 * @param replayGraph If false, and if the input graph already contains element they are not
	 *        replayed.
	 */
	public GraphListenerProxyThread( Graph inputGraph, boolean replayGraph )
	{
		this( inputGraph, null, replayGraph );
	}

	/**
	 * Like {@link #GraphListenerProxyThread(Graph)}, but additionally, redirect all events comming from
	 * the input graph to an output graph.
	 * 
	 * The effect of the proxy is that the output graph will be the exact copy of the input graph.
	 * By default, if the input graph already contains some elements, they are "replayed". This
	 * means that events are sent to mimic the fact they just appeared.
	 * @param inputGraph The graph we listen at.
	 * @param outputGraph The graph that will become a copy of the input graph.
	 */
	public GraphListenerProxyThread( Graph inputGraph, Graph outputGraph )
	{
		this( inputGraph, outputGraph, true );
	}
	
	/**
	 * Like {@link #GraphListenerProxyThread(Graph, Graph)}, but allow to avoid replaying the graph.
	 * @param inputGraph The graph we listen at.
	 * @param outputGraph The graph that will become a copy of the input graph.
	 * @param replayGraph If false, and if the input graph already contains element they are not
	 *        replayed.
	 */
	public GraphListenerProxyThread( Graph inputGraph, Graph outputGraph, boolean replayGraph )
	{
		this( inputGraph, outputGraph, replayGraph, new MBoxStandalone() );
	}
	
	/**
	 * Like {@link #GraphListenerProxyThread(Graph, Graph,boolean)}, but allow to share the
	 * message box with another message processor. This can be needed to share the same message
	 * stack, when message order is important.
	 * @param inputGraph The graph we listen at.
	 * @param outputGraph The graph that will become a copy of the input graph.
	 * @param replayGraph If false, and if the input graph already contains element they are not
	 *        replayed.
	 * @param sharedMBox The message box used to send and receive graph messages across the thread
	 *        boundary.
	 */
	public GraphListenerProxyThread( Graph inputGraph, Graph outputGraph, boolean replayGraph, MBox sharedMBox )
	{
		this.events      = sharedMBox;
		this.from        = inputGraph.getId();
		this.outputGraph = outputGraph;
		this.inputGraph  = inputGraph;

		if( replayGraph )
			replayGraph( inputGraph );
		
		inputGraph.addGraphListener( this );		
		((MBoxStandalone)this.events).addListener( this );
	}
	
// Commands

	/**
	 * Ask the proxy to unregister from the graph (stop receive events) as soon as possible
	 * (when the next event will occur in the graph).
	 */
	public void unregisterFromGraph()
	{
		unregisterWhenPossible = true;
	}
	
	/**
	 * Add a listener to the events of the input graph.
	 * @param listener The listener to call for each event in the input graph.
	 */
	public void addGraphListener( GraphListener listener )
	{
		listeners.add( listener );
	}
	
	/**
	 * Remove a listener. 
	 * @param listener The listener to remove.
	 */
	public void removeGraphListener( GraphListener listener )
	{
		int index = listeners.indexOf( listener );
		
		if( index >= 0 )
			listeners.remove( index );
	}
	
	/**
	 * This method must be called regularly to check if the input graph sent
	 * events. If some event occurred, the listeners will be called and the output graph
	 * given as argument will be modified (if any).
	 */
	public void checkEvents()
	{
		((MBoxStandalone)events).processMessages();
	}
	
// Utility
	
	/**
	 * Send all nodes and edges of the given graph to the distant message box, with their
	 * attributes. This method is primarily used to send the graph state if the distant mbox is
	 * connected to a graph that is already (maybe partially) built.
	 * 
	 * @param inputGraph The graph to process.
	 */
	protected void replayGraph( Graph inputGraph )
	{
		try
		{
			// Replay all attributes of the graph.

			Iterator<String> k = inputGraph.getAttributeKeyIterator();

			if( k != null )
			{
				while( k.hasNext() )
				{
					String key = k.next();
					Object val = inputGraph.getAttribute( key );

					events.post( from, InputProtocol.CHANGE_GRAPH.tag, key, val );
				}
			}

			k = null;

			// Replay all nodes and their attributes.

			Iterator<? extends Node> nodes = inputGraph.getNodeIterator();

			while( nodes.hasNext() )
			// for( Node node: graph.getNodeSet() )
			{
				Node node = nodes.next();

				events.post( from, InputProtocol.ADD_NODE.tag, node.getId() );

				k = node.getAttributeKeyIterator();

				if( k != null )
				{
					while( k.hasNext() )
					{
						String key = k.next();
						Object val = node.getAttribute( key );

						events.post( from, InputProtocol.CHANGE_NODE.tag, node.getId(), key,
						        val );
					}
				}
			}

			k = null;

			// Replay all edges and their attributes.

			Iterator<? extends Edge> edges = inputGraph.getEdgeIterator();
			
			while( edges.hasNext() )
			//for( Edge edge : graph.getEdgeSet() )
			{
				Edge edge = edges.next();
				
				events.post( from, InputProtocol.ADD_EDGE.tag, edge.getId(), edge
				        .getSourceNode().getId(), edge.getTargetNode().getId(), new Boolean(
				        edge.isDirected() ) );

				k = edge.getAttributeKeyIterator();

				if( k != null )
				{
					while( k.hasNext() )
					{
						String key = k.next();
						Object val = edge.getAttribute( key );

						events.post( from, InputProtocol.CHANGE_EDGE.tag, edge.getId(), key,
						        val );
					}
				}
			}
		}
		catch( CannotPostException e )
		{
			System.err.printf( "GraphRendererRunner: cannot post message to listeners: %s%n", e
			        .getMessage() );
		}
	}
	
// GraphListener -- Redirect events to the message box.

	public void attributeChanged( Element element, String attribute, Object oldValue,
	        Object newValue )
	{
		if( unregisterWhenPossible )
		{
			inputGraph.removeGraphListener( this );
			//System.err.printf( "Proxy unregistered from graph !! (%s)%n", Thread.currentThread().getName() );
			return;
		}
		
		try
		{
			if( element instanceof Node )
			{
				events.post( from, InputProtocol.CHANGE_NODE.tag, element.getId(), attribute,
				        newValue );
			}
			else if( element instanceof Edge )
			{
				events.post( from, InputProtocol.CHANGE_EDGE.tag, element.getId(), attribute,
				        newValue );
			}
			else
			{
				events.post( from, InputProtocol.CHANGE_GRAPH.tag, attribute, newValue );
			}
		}
		catch( CannotPostException e )
		{
			System.err.printf( "GraphRendererRunner: cannot post message to listeners: %s%n", e
			        .getMessage() );
		}
	}

	public void afterNodeAdd( Graph graph, Node node )
	{
		if( unregisterWhenPossible )
		{
			inputGraph.removeGraphListener( this );
			//System.err.printf( "Proxy unregistered from graph !! (%s)%n", Thread.currentThread().getName() );
			return;
		}

		try
		{
			events.post( from, InputProtocol.ADD_NODE.tag, node.getId() );
		}
		catch( CannotPostException e )
		{
			System.err.printf( "GraphRendererRunner: cannot post message to listeners: %s%n", e
			        .getMessage() );
		}
	}

	public void afterEdgeAdd( Graph graph, Edge edge )
	{
		if( unregisterWhenPossible )
		{
			inputGraph.removeGraphListener( this );
			System.err.printf( "Proxy unregistered from graph !! (%s)%n", Thread.currentThread().getName() );
			return;
		}

		try
		{
			events.post( from, InputProtocol.ADD_EDGE.tag, edge.getId(), edge.getSourceNode()
			        .getId(), edge.getTargetNode().getId(), new Boolean( edge.isDirected() ) );
		}
		catch( CannotPostException e )
		{
			System.err.printf( "GraphRendererRunner: cannot post message to listeners: %s%n", e
			        .getMessage() );
		}
	}

	public void beforeNodeRemove( Graph graph, Node node )
	{
		if( unregisterWhenPossible )
		{
			inputGraph.removeGraphListener( this );
			//System.err.printf( "Proxy unregistered from graph !! (%s)%n", Thread.currentThread().getName() );
			return;
		}

		try
		{
			events.post( from, InputProtocol.DEL_NODE.tag, node.getId() );
		}
		catch( CannotPostException e )
		{
			System.err.printf( "GraphRendererRunner: cannot post message to listeners: %s%n", e
			        .getMessage() );
		}
	}

	public void beforeEdgeRemove( Graph graph, Edge edge )
	{
		if( unregisterWhenPossible )
		{
			inputGraph.removeGraphListener( this );
			//System.err.printf( "Proxy unregistered from graph !! (%s)%n", Thread.currentThread().getName() );
			return;
		}

		try
		{
			events.post( from, InputProtocol.DEL_EDGE.tag, edge.getId() );
		}
		catch( CannotPostException e )
		{
			System.err.printf( "GraphRendererRunner: cannot post message to listeners: %s%n", e
			        .getMessage() );
		}
	}

	public void beforeGraphClear( Graph graph )
	{
		if( unregisterWhenPossible )
		{
			inputGraph.removeGraphListener( this );
			//System.err.printf( "Proxy unregistered from graph !! (%s)%n", Thread.currentThread().getName() );
			return;
		}

		try
		{
			events.post( from, InputProtocol.CLEAR_GRAPH.tag );
		}
		catch( CannotPostException e )
		{
			System.err.printf( "GraphRendererRunner: cannot post message to listeners: %s%n", e
			        .getMessage() );
		}
	}

// MBoxListener -- receive redirected events and re-send them to listeners.
	
	public void processMessage( String from, Object[] data )
    {
		if( data.length > 0 )
		{
			if( data[0].equals( InputProtocol.CHANGE_GRAPH.tag ) )
			{
				if( data.length >= 3 && data[1] instanceof String )
				{
					if( outputGraph != null )
						outputGraph.changeAttribute( (String)data[1], data[2] );
					
					Graph graph = outputGraph != null ? outputGraph : new DummyGraph( "" );
					
					for( GraphListener listener: listeners )
						listener.attributeChanged( graph, (String) data[1], null, data[2] );
				}
			}
			else if( data[0].equals( InputProtocol.CHANGE_NODE.tag ) )
			{
				if( data.length >= 4 && data[1] instanceof String && data[2] instanceof String )
				{
					String id   = (String) data[1];
					Node   node = null;
					
					if( outputGraph != null )
					{
						node = outputGraph.getNode( id );
						
						if( node != null )
							node.changeAttribute( (String)data[2], data[3] );
					}

					Node n = node != null ? node : new DummyNode( id );
					
					for( GraphListener listener: listeners )
						listener.attributeChanged( n, (String)data[2], null/*TODO*/, data[3] );
				}
			}
			else if( data[0].equals( InputProtocol.CHANGE_EDGE.tag ) )
			{
				if( data.length >= 4 && data[1] instanceof String && data[2] instanceof String )
				{
					String id   = (String) data[1];
					Edge   edge = null;
					
					if( outputGraph != null )
					{
						edge = outputGraph.getEdge( id );
						
						if( edge != null )
							edge.changeAttribute( (String) data[2], data[3] );
					}

					Edge e = edge != null ? edge : new DummyEdge( id, null, null, false );
					
					for( GraphListener listener: listeners )
						listener.attributeChanged( e, (String)data[2], null/*TODO*/, data[3] );
				}
			}
			else if( data[0].equals( InputProtocol.ADD_NODE.tag ) )
			{
				if( data.length >= 2 && data[1] instanceof String )
				{
					String id   = (String) data[1];
					Node   node = null;
			
					if( outputGraph != null )
						node = outputGraph.addNode( id );

					Node n = node != null ? node : new DummyNode( id );
					
					for( GraphListener listener: listeners )
						listener.afterNodeAdd( outputGraph, n );
				}
			}
			else if( data[0].equals( InputProtocol.ADD_EDGE.tag ) )
			{
				if( data.length >= 5 && data[1] instanceof String && data[2] instanceof String
				        && data[3] instanceof String && data[4] instanceof Boolean )
				{
					Edge    edge = null;
					String  id   = (String) data[1];
					String  froM = (String) data[2];
					String  to   = (String) data[3];
					boolean dir  = (Boolean) data[4];
					
					if( outputGraph != null )
						edge = outputGraph.addEdge( id, froM, to, dir );

					Edge e = edge != null ? edge : new DummyEdge( id, froM, to, dir );
					
					for( GraphListener listener: listeners )
						listener.afterEdgeAdd( outputGraph, e );
				}
			}
			else if( data[0].equals( InputProtocol.DEL_NODE.tag ) )
			{
				if( data.length >= 2 && data[1] instanceof String )
				{
					Node   node = null;
					String id   = (String) data[1];
					
					if( outputGraph != null )
						node = outputGraph.removeNode( id );

					Node n = node != null ? node : new DummyNode( id );
					
					for( GraphListener listener: listeners )
						listener.beforeNodeRemove( outputGraph, n );
				}
			}
			else if( data[0].equals( InputProtocol.DEL_EDGE.tag ) )
			{
				if( data.length >= 2 && data[1] instanceof String )
				{
					Edge   edge = null;
					String id   = (String) data[1];
					
					if( outputGraph != null )
						edge = outputGraph.removeEdge( id );

					Edge e = edge != null ? edge : new DummyEdge( id, null, null, false );
					
					for( GraphListener listener: listeners )
						listener.beforeEdgeRemove( outputGraph, e );
				}
			}
			else if( data[0].equals( InputProtocol.CLEAR_GRAPH.tag ) )
			{
				Graph graph = outputGraph != null ? outputGraph : new DummyGraph( "" );
				
				for( GraphListener listener: listeners )
					listener.beforeGraphClear( graph );
			}
			else if( data[0].equals( InputProtocol.STEP_BEGINS.tag ) )
			{
				if (outputGraph != null)
					outputGraph.stepBegins((Double) data[2]);

				for (GraphListener listener : listeners)
					listener.stepBegins((Graph) data[1], (Double) data[2]);
			}
			else
			{
				// Ignore, it can be read by another processor if the message box is shared.
				
/*				// What to do ?

				if( data.length > 0 && data[0] instanceof String )
					throw new RuntimeException( "GraphListenerProxy: uncaught message from "+
					        from+" : "+data[0]+"["+ data.length+"]" );
				else
					throw new RuntimeException( "GraphListenerProxy: uncaught message from "+
					        from+" : ["+data.length+"]" );
*/			}
		}	    
    }
    
// Nested classes
	
	/**
	 * Commands that can be sent through the event message box. Messages are array of objects. The
	 * first element of the array is a string identifying the message. The other elements depend on
	 * the message kind, and for all messages are given in order under the form "elementName:Type".
	 * For example to send a node add event, you can use:
	 * 
	 * <pre>
	 * mbox.post( &quot;fromMe&quot;, InputProtocol.ADD_NODE.tag, &quot;nodeId&quot; );
	 * </pre>
	 * 
	 * or:
	 * 
	 * <pre>
	 * mbox.post( &quot;fromMe&quot;, &quot;an&quot;, &quot;nodeId&quot; );
	 * </pre>
	 * 
	 * The post() method handles variable argument list, which simplify the creation of messages.
	 * 
	 * @author Antoine Dutot
	 * @author Yoann Pign�
	 * @since 20061208
	 */
	public static enum InputProtocol
	{
		/**
		 * Clear the whole graph. Format[0]: empty.
		 */
		CLEAR_GRAPH( "clear" ),
		/**
		 * Change the graph. Format[2]: attributeKey:String, value:Object.
		 */
		CHANGE_GRAPH( "cg" ),
		/**
		 * Change a node. Format[3]: nodeId:String, attributeKey:String, value:Object.
		 */
		CHANGE_NODE( "cn" ),
		/**
		 * Change an edge. Format[3]: edgeId:String, attributeKey:String, value:Object.
		 */
		CHANGE_EDGE( "ce" ),
		/**
		 * Add a node. Format[1]: nodeId:String.
		 */
		ADD_NODE( "an" ),
		/**
		 * Add an edge. Format[4]: edgeId:String, node0Id:String, node1Id:String, directed:Boolean
		 */
		ADD_EDGE( "ae" ),
		/**
		 * Remove a node. Format[1]: nodeId:String.
		 */
		DEL_NODE( "dn" ),
		/**
		 * Remove an edge. Format[1]: edgeId:String.
		 */
		DEL_EDGE( "de" ),
		/**
		 * One more step. Format[1]: step:double.
		 */
		STEP_BEGINS("st");
		
		/**
		 * The message identifier.
		 */
		public String tag;

		InputProtocol( String tag )
		{
			this.tag = tag;
		}

		/**
		 * Get the message identifier.
		 * 
		 * @return The message identifier.
		 */
		public String getTag()
		{
			return tag;
		}
	}

	public static class DummyNode extends AbstractElement implements Node
	{
		String nid;
		public DummyNode( String id ) { super( id ); }
		
		@Override
		public String getId() { return nid; }
		public void setId( String id ) { nid = id; }
		
		@Override
        protected void attributeChanged( String attribute, Object oldValue, Object newValue ) {}
		public Iterator<? extends Node> getBreadthFirstIterator() { return null; }
		public Iterator<? extends Node> getBreadthFirstIterator( boolean directed ) { return null; }
		public int getDegree() { return 0; }
		public Iterator<? extends Node> getDepthFirstIterator() { return null; }
		public Iterator<? extends Node> getDepthFirstIterator( boolean directed ) { return null; }
		public Edge getEdge( int i ) { return null; }
		public Edge getEdgeFrom( String id ) { return null; }
		public Iterator<? extends Edge> getEdgeIterator() { return null; }
		public Collection<? extends Edge> getEdgeSet() { return null; }
		public Edge getEdgeToward( String id ) { return null; }
		public Iterator<? extends Edge> getEnteringEdgeIterator() { return null; }
		public Collection<? extends Edge> getEnteringEdgeSet() { return null; }
		public Graph getGraph() { return null; }
		public String getGraphName() { return null; }
		public String getHost() { return null; }
		public int getInDegree() { return 0; }
		public Iterator<? extends Edge> getLeavingEdgeIterator() { return null; }
		public Collection<? extends Edge> getLeavingEdgeSet() { return null; }
		public Iterator<? extends Node> getNeighborNodeIterator() { return null; }
		public int getOutDegree() { return 0; }
		public boolean hasEdgeFrom( String id ) { return false; }
		public boolean hasEdgeToward( String id ) { return false; }
		public boolean isDistributed() { return false; }
		public void setGraph( Graph graph ) {}
		public void setGraphName( String newHost ) {}
		public void setHost( String newHost ) {}
	}
	
	public static class DummyEdge extends AbstractElement implements Edge
	{
		/**
		 * Edge end point.
		 */
		public String from, to;
		
		/**
		 * Is the edge directed.
		 */
		public boolean directed = false;
		
		public DummyNode FROM = new DummyNode( "" );
		public DummyNode TO = new DummyNode( "" );
		
		public DummyEdge( String id, String from, String to, boolean dir ) { super( id ); this.from = from; this.to = to; directed = dir; }
		
		@Override
        protected void attributeChanged( String attribute, Object oldValue, Object newValue ) {}
		public Node getNode0() { FROM.setId( from ); return FROM; }
		public Node getNode1() { TO.setId( to ); return TO; }
		public Node getOpposite( Node node ) { return null; }
		public Node getSourceNode() { FROM.setId( from ); return FROM; }
		public Node getTargetNode() { TO.setId( to ); return TO; }
		public boolean isDirected() { return directed; }
		public void setDirected( boolean on ) {}
		public void switchDirection() {}
		public String getNode0Id() { return from; }
		public String getNode1Id() { return to; }
		public String getSourceId() { return from; }
		public String getTargetId() { return to; }
	}
	
	public static class DummyGraph extends AbstractElement implements Graph
	{
		public DummyGraph( String id ) { super( id ); }
		
		@Override
        protected void attributeChanged( String attribute, Object oldValue, Object newValue ) {}
		public Edge addEdge( String id, String node1, String node2 ) throws SingletonException, NotFoundException { return null; }
		public Edge addEdge( String id, String from, String to, boolean directed ) throws SingletonException, NotFoundException { return null; }
		public void addGraphListener( GraphListener listener ) {}
		public Node addNode( String id ) throws SingletonException { return null; }
//		public Algorithms algorithm() { return null; }
		public void clear() {}
		public void clearListeners() {}
		public GraphViewerRemote display() { return null; }
		public GraphViewerRemote display( boolean autoLayout ) { return null; }
		public EdgeFactory edgeFactory() { return null; }
		public void setEdgeFactory( EdgeFactory ef ) {}
		public Edge getEdge( String id ) { return null; }
		public int getEdgeCount() { return 0; }
		public Iterator<? extends Edge> getEdgeIterator() { return null; }
		public Collection<? extends Edge> getEdgeSet() { return null; }
		public List<GraphListener> getGraphListeners() { return null; }
		public Node getNode( String id ) { return null; }
		public int getNodeCount() { return 0; }
		public Iterator<? extends Node> getNodeIterator() { return null; }
		public Iterator<Node> iterator() { return null; }
		public Collection<? extends Node> getNodeSet() { return null; }
		public boolean isAutoCreationEnabled() { return false; }
		public boolean isStrictCheckingEnabled() { return false; }
		public NodeFactory nodeFactory() { return null; }
		public void setNodeFactory( NodeFactory nf ) {}
		public void read( String filename ) throws IOException, GraphParseException, NotFoundException {}
		public void read( GraphReader reader, String filename ) throws IOException, GraphParseException {}
		public int readPositionFile( String posFileName ) throws IOException { return 0; }
		public Edge removeEdge( String from, String to ) throws NotFoundException { return null; }
		public Edge removeEdge( String id ) throws NotFoundException { return null; }
		public void removeGraphListener( GraphListener listener ) {}
		public Node removeNode( String id ) throws NotFoundException { return null; }
		public void setAutoCreate( boolean on ) {}
		public void setStrictChecking( boolean on ) {}
		public void write( String filename ) throws IOException {}
		public void write( GraphWriter writer, String filename ) throws IOException {}
		public void stepBegins(double time){}
	}

	public void stepBegins(Graph graph, double time)
	{

		if( unregisterWhenPossible )
		{
			inputGraph.removeGraphListener( this );
			//System.err.printf( "Proxy unregistered from graph !! (%s)%n", Thread.currentThread().getName() );
			return;
		}

		try
		{
			events.post( from, InputProtocol.STEP_BEGINS.tag, graph, time );
		}
		catch( CannotPostException e )
		{
			System.err.printf( "GraphRendererRunner: cannot post message to listeners: %s%n", e
			        .getMessage() );
		}
	}
}