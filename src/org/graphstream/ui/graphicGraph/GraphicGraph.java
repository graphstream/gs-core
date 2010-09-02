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

package org.graphstream.ui.graphicGraph;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;

import org.graphstream.graph.Edge;
import org.graphstream.graph.EdgeFactory;
import org.graphstream.graph.Element;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.NodeFactory;
import org.graphstream.graph.ElementNotFoundException;
import org.graphstream.graph.IdAlreadyInUseException;
import org.graphstream.graph.implementations.AbstractElement;
import org.graphstream.stream.AttributeSink;
import org.graphstream.stream.ElementSink;
import org.graphstream.stream.Sink;
import org.graphstream.stream.SourceBase;
import org.graphstream.stream.SourceBase.ElementType;
import org.graphstream.stream.file.FileSink;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.sync.SinkTime;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.graphicGraph.stylesheet.Style;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants;
import org.graphstream.ui.graphicGraph.stylesheet.StyleSheet;
import org.graphstream.ui.graphicGraph.stylesheet.Value;
import org.graphstream.ui.graphicGraph.stylesheet.Values;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.Units;

/**
 * Graph representation used in display classes.
 * 
 * <p>
 * The purpose of the graphic graph is to represent a graph with some often used graphic attributes
 * (like position, label, etc.) stored as fields in the nodes and edges and most of the style stored
 * in styles pertaining to a style sheet that tries to imitate the way CSS works. For example,
 * the GraphicNode class defines a label, a position (x,y,z) and a style that is taken from the
 * style sheet.
 * </p>
 * 
 * <p>
 * The style sheet is uploaded on the graph using an attribute correspondingly named "stylesheet"
 * or "ui.stylesheet" (the second one is favoured). It can be a string that contains the whole style
 * sheet, or an URL of the form :
 * </p>
 * 
 * <pre>url(name)</pre>
 * 
 * <p>
 * Note that the graphic graph does not completely duplicate a graph, it only store things that
 * are useful for drawing it. Although it implements "Graph", some methods are not implemented
 * and will throw a runtime exception. These methods are mostly utility methods like write(),
 * read(), and naturally display().
 * </p>
 * 
 * <p>
 * The graphic graph has the ability to store attributes like any other graph element, however
 * the attributes stored by the graphic graph are restricted. There is a filter on the attribute
 * adding methods that let pass only :
 * <ul>
 * 		<li>All attributes starting with "ui.".</li>
 * 		<li>The "x", "y", "z", "xy" and "xyz" attributes.</li>
 * 		<li>The "stylesheet" attribute (although "ui.stylesheet" is preferred).</li>
 * 		<li>The "label" attribute.</li>
 * </ul>
 * All other attributes are filtered and not stored. The result is that if the graphic graph is
 * used as an input (a source of graph events) some attributes will not pass through the filter.
 * </p>
 * 
 * TODO : this graph cannot handle modification inside event listener methods !!
 */
public class GraphicGraph extends AbstractElement implements Graph, StyleGroupListener
{
// Attribute

	/**
	 * The style.
	 */
	protected StyleSheet styleSheet;

	/**
	 * The style groups (styles and groups of graph elements).
	 */
	protected StyleGroupSet styleGroups;

	/**
	 * The way nodes are connected one with another. The map is sorted by node. For each node an
	 * array of edges lists the connectivity.
	 */
	protected HashMap<GraphicNode, ArrayList<GraphicEdge>> connectivity;

	/**
	 * The style of this graph.
	 */
	public StyleGroup style;
	
	/**
	 * Set to true each time the graph was modified and a redraw is needed.
	 */
	public boolean graphChanged;
	
	/**
	 * Memorise the step events.
	 */
	public double step = 0;
	
	/**
	 * Set to true each time a sprite or node moved.
	 */
	protected boolean boundsChanged = true;
	
	/**
	 * Maximum position of a node or sprite in the graphic graph. Computed by {@link #computeBounds()}.
	 */
	protected Point3 hi = new Point3();
	
	/**
	 * Minimum position of a node or sprite in the graphic graph. Computed by {@link #computeBounds()}.
	 */
	protected Point3 lo = new Point3();
	
	/**
	 * Set of listeners of this graph.
	 */
	protected GraphListeners listeners;

	/**
	 * Time of other known sources.
	 */
	protected SinkTime sinkTime = new SinkTime();
	
	protected class GraphListeners extends SourceBase
	{
		public GraphListeners( String id, SinkTime sinkTime ) { super( id ); sourceTime.setSinkTime( sinkTime ); }
		public long newEvent() { return sourceTime.newEvent(); }
	};

	/**
	 * Report back the XYZ events on nodes and sprites.
	 */
	protected boolean feedbackXYZ = true;
	
// Construction

	/**
	 * New empty graphic graph.
	 * 
	 * A default style sheet is created, it then can be "cascaded" with other style sheets.
	 */
	public GraphicGraph( String id )
	{
		super( id );

		listeners    = new GraphListeners( id, sinkTime );
		styleSheet   = new StyleSheet();
		styleGroups  = new StyleGroupSet( styleSheet );
		connectivity = new HashMap<GraphicNode, ArrayList<GraphicEdge>>();

		styleGroups.addListener( this );
		styleGroups.addElement( this );	// Add style to this graph.
		
		style = styleGroups.getStyleFor( this );
	}

// Access

	@Override
	protected String myGraphId()	// XXX
	{
		return getId();
	}
	
	@Override
	protected long newEvent()		// XXX
	{
		return listeners.newEvent();
	}
	
	/**
	 * True if the graph was edited or changed in any way since the last reset of the "changed"
	 * flag.
	 * @return true if the graph was changed. 
	 */
	public boolean graphChangedFlag()
	{
		return graphChanged;
	}
	
	/**
	 * Reset the "changed" flag.
	 * @see #graphChangedFlag()
	 */
	public void resetGraphChangedFlag()
	{
		graphChanged = false;
	}
	
	/**
	 * The style sheet. This style sheet is the result of the "cascade" or accumulation of styles
	 * added via attributes of the graph.
	 * @return A style sheet.
	 */
	public StyleSheet getStyleSheet()
	{
		return styleSheet;
	}
	
	/**
	 * The graph style group.
	 * @return A style group.
	 */
	public StyleGroup getStyle()
	{
		return style;
	}
	
	/**
	 * The complete set of style groups.
	 * @return The style groups.
	 */
	public StyleGroupSet getStyleGroups()
	{
		return styleGroups;
	}
	
	@Override
	public String toString()
	{
		return String.format( "[%s %d nodes %d edges]", getId(), getNodeCount(), getEdgeCount() );
	}
	
	public double getStep()
	{
		return step;
	}
	
	/**
	 * The maximum position of a node or sprite. Notice that this is updated only each time the
	 * {@link #computeBounds()} method is called.
	 * @return The maximum node or sprite position.
	 */
	public Point3 getMaxPos()
	{
		return hi;
	}
	
	/**
	 * The minimum position of a node or sprite. Notice that this is updated only each time the
	 * {@link #computeBounds()} method is called.
	 * @return The minimum node or sprite position.
	 */
	public Point3 getMinPos()
	{
		return lo;
	}
	
	/**
	 * Does the graphic graph publish via attribute changes the XYZ changes on nodes and sprites
	 * when changed ?.
	 */
	public boolean feedbackXYZ()
	{
		return feedbackXYZ;
	}
	
// Command

	/**
	 * Should the graphic graph publish via attribute changes the XYZ changes on nodes and sprites
	 * when changed ?.
	 */
	public void feedbackXYZ( boolean on )
	{
		feedbackXYZ = on;
	}
	
	/**
	 * Compute the overall bounds of the graphic graph according to the nodes and sprites positions.
	 * We can only compute the graph bounds from the nodes and sprites centres since the node and
	 * graph bounds may in certain circumstances be computed according to the graph bounds. The
	 * bounds are stored in the graph metrics. 
	 * 
	 * This operation will process each node and sprite and is therefore costly. However it does
	 * this computation again only when a node or sprite moved. Therefore it can be called several
	 * times, if nothing moved in the graph, the computation will not be redone.
	 * 
	 * @see #getMaxPos()
	 * @see #getMinPos()
	 */
	public void computeBounds()
	{
		if( boundsChanged )
		{
			lo.x = lo.y = lo.z =  10000000;	// A bug with Float.MAX_VALUE during comparisons ?
			hi.x = hi.y = hi.z = -10000000;	// A bug with Float.MIN_VALUE during comparisons ?
			
			for( Node n: getEachNode() )
			{
				GraphicNode node = (GraphicNode) n;
				
				if( node.x < lo.x ) lo.x = node.x; if( node.x > hi.x ) hi.x = node.x;
				if( node.y < lo.y ) lo.y = node.y; if( node.y > hi.y ) hi.y = node.y;
				if( node.z < lo.z ) lo.z = node.z; if( node.z > hi.z ) hi.z = node.z;
			}
			
			for( GraphicSprite sprite: spriteSet() )
			{
				if( ! sprite.isAttached() && sprite.getUnits() == StyleConstants.Units.GU )
				{
					float x = sprite.getX();
					float y = sprite.getY();
					float z = sprite.getZ();
				
					if( x < lo.x ) lo.x = x; if( x > hi.x ) hi.x = x;
					if( y < lo.y ) lo.y = y; if( y > hi.y ) hi.y = y;
					if( z < lo.z ) lo.z = z; if( z > hi.z ) hi.z = z;
				}
			}
			
			boundsChanged = false;
		}
	}
	
	protected GraphicEdge addEdge( String sourceId, long timeId, String id, String from, String to, boolean directed, HashMap<String, Object> attributes )
	{
		GraphicEdge edge = (GraphicEdge) styleGroups.getEdge( id );
		
		if( edge == null )
		{
			GraphicNode n1 = (GraphicNode) styleGroups.getNode( from );
			GraphicNode n2 = (GraphicNode) styleGroups.getNode( to );

			if( n1 == null || n2 == null )
				throw new RuntimeException(
					"org.miv.graphstream.ui.graphicGraph.GraphicGraph.addEdge() : ERROR : one of the nodes does not exist" );
				
			edge = new GraphicEdge( id, n1, n2, directed, attributes );
			
			styleGroups.addElement( edge );
			
			ArrayList<GraphicEdge> l1 = connectivity.get( n1 );
			ArrayList<GraphicEdge> l2 = connectivity.get( n2 );
	
			if( l1 == null )
			{
				l1 = new ArrayList<GraphicEdge>();
				connectivity.put( n1, l1 );
			}
			
			if( l2 == null )
			{
				l2 = new ArrayList<GraphicEdge>();
				connectivity.put( n2, l2 );
			}
	
			l1.add( edge );
			l2.add( edge );
			edge.countSameEdges( l1 );
			
			graphChanged = true;
			
			listeners.sendEdgeAdded( sourceId, timeId, id, from, to, directed );
		}
			
		return edge;
	}

	protected GraphicNode addNode( String sourceId, long timeId, String id, HashMap<String, Object> attributes )
	{
		GraphicNode node = (GraphicNode) styleGroups.getNode( id );
		
		if( node == null )
		{
			node = new GraphicNode( this, id, attributes );

			styleGroups.addElement( node );
		
			graphChanged = true;
		
			listeners.sendNodeAdded( sourceId, timeId, id );
		}
			
		return node;
	}

	protected void moveNode( String id, float x, float y, float z )
	{
		GraphicNode node = (GraphicNode) styleGroups.getNode( id );
		
		if( node != null )
		{
			node.x = x;
			node.y = y;
			node.z = z;
			node.addAttribute( "x", x );
			node.addAttribute( "y", y );
			node.addAttribute( "z", z );
			
			graphChanged = true;
		}
	}

	public Edge removeEdge( String sourceId, long timeId, String id ) throws ElementNotFoundException
	{
		GraphicEdge edge = (GraphicEdge) styleGroups.getEdge( id );
		
		if( edge != null )
		{
			listeners.sendEdgeRemoved( sourceId, timeId, id );

			if( connectivity.get( edge.from ) != null )
				connectivity.get( edge.from ).remove( edge );
			if( connectivity.get( edge.to ) != null )
				connectivity.get( edge.to ).remove( edge );
			
			styleGroups.removeElement( edge );
			edge.removed();
			
			graphChanged = true;
		}
		
		return edge;
	}

	public Edge removeEdge( String sourceId, long timeId, String from, String to ) throws ElementNotFoundException
	{
		GraphicNode node0 = (GraphicNode) styleGroups.getNode( from );
		GraphicNode node1 = (GraphicNode) styleGroups.getNode( to );
		
		if( node0 != null && node1 != null )
		{
			ArrayList<GraphicEdge> edges0 = connectivity.get( node0 );
			ArrayList<GraphicEdge> edges1 = connectivity.get( node1 ); 
			
			for( GraphicEdge edge0: edges0 )
			{
				for( GraphicEdge edge1: edges1 )
				{
					if( edge0 == edge1 )
					{
						removeEdge( sourceId, timeId, edge0.getId() );
						return edge0;
					}
				}
			}
		}
		
		return null;
	}

	public Node removeNode( String sourceId, long timeId, String id )
	{
		GraphicNode node = (GraphicNode) styleGroups.getNode( id );
		
		if( node != null )
		{
			listeners.sendNodeRemoved( sourceId, timeId, id );
			
		    if(connectivity.get(node) != null)
		    {
		    	// We must do a copy of the connectivity set for the node
		    	// since we will be modifying the connectivity as we process
		    	// edges.
		    	ArrayList<GraphicEdge> l = new ArrayList<GraphicEdge>( connectivity.get( node ) );
		    	
		    	for( GraphicEdge edge: l )
		    		removeEdge( sourceId, newEvent(), edge.getId() );

		    	connectivity.remove( node );
		    }
		    
		    styleGroups.removeElement( node );
		    node.removed();
			
		    graphChanged = true;
		}
		
		return node;
	}

	public GraphicNode getNode( String id )
	{
		return (GraphicNode) styleGroups.getNode( id );
	}

	public GraphicEdge getEdge( String id )
	{
		return (GraphicEdge) styleGroups.getEdge( id );
	}
	
	public GraphicSprite getSprite( String id )
	{
		return styleGroups.getSprite( id );
	}

	@Override
	protected void attributeChanged( String sourceId, long timeId, String attribute, AttributeChangeEvent event, Object oldValue, Object newValue )
	{
		// One of the most important method. Most of the communication comes from
		// attributes.
		
		if( attribute.equals( "ui.stylesheet" ) || attribute.equals( "stylesheet" ) )
		{
			if( event == AttributeChangeEvent.ADD || event == AttributeChangeEvent.CHANGE )
			{
				if( newValue instanceof String )
				{
					try
					{
						loadStyleSheet( (String) newValue );
						graphChanged = true;
					}
					catch( IOException e )
					{
						System.err.printf( "Error while parsing style sheet for graph '%s' : %n", getId() );
						if( ((String)newValue).startsWith( "url" ) )
							System.err.printf( "    %s%n", ((String)newValue) );
						System.err.printf( "    %s%n", e.getMessage() );
					}
				}
				else
				{
					System.err.printf( "Error with stylesheet specification what to do with '%s' ?%n", newValue );
				}
			}
			else	// Remove the style.
			{
				styleSheet.clear();
				graphChanged = true;
			}
		}
		else if( attribute.startsWith( "ui.sprite." ) )
		{
			// Defers the sprite handling to the sprite API.

//			if( ! attrLock )	// The attrLock allows us to add/change/remove sprites attributes without entering in a recursive loop.
				spriteAttribute( event, null, attribute, newValue );
		}

		// We filter attributes.
		
//		Matcher matcher = GraphicElement.acceptedAttribute.matcher( attribute );
			
//		if( matcher.matches() )
			listeners.sendAttributeChangedEvent( sourceId, timeId, getId(),
					ElementType.GRAPH, attribute, event, oldValue, newValue );
	}

	public void clear( String sourceId, long timeId )
	{
		listeners.sendGraphCleared( sourceId, timeId );
		connectivity.clear();
		styleGroups.clear();

		step         = 0;
		graphChanged = true;
	}
	
	/**
	 * Display the node/edge relations.
	 */
	public void printConnectivity()
	{
		Iterator<GraphicNode> keys = connectivity.keySet().iterator();
		
		System.err.printf( "Graphic graph connectivity:%n" );
		
		while( keys.hasNext() )
		{
			GraphicNode node = keys.next();
			System.err.printf( "    [%s] -> ", node.getId() );
			ArrayList<GraphicEdge> edges = connectivity.get( node );
			for( GraphicEdge edge: edges )
				System.err.printf( " (%s %d)", edge.getId(), edge.getMultiIndex() );
			System.err.printf( "%n" );
		}
	}

// Style group listener interface
	
	public void elementStyleChanged( Element element, StyleGroup oldStyle, StyleGroup style )
    {
		if( element instanceof GraphicElement )
		{
			GraphicElement ge = (GraphicElement) element;
			ge.style          = style;
			graphChanged      = true;
		}
		else if( element instanceof GraphicGraph )
		{
			GraphicGraph gg = (GraphicGraph) element;
			gg.style        = style;
			graphChanged    = true;
		}
		else
		{
			throw new RuntimeException( "WTF ?" );
		}
    }
	
	public void styleChanged( StyleGroup style )
	{
		
	}

// Graph interface

	public Iterable<? extends Edge> getEachEdge()
    {
		return styleGroups.edges();
    }

	public Iterable<? extends Node> getEachNode()
    {
	    return styleGroups.nodes();
    }
	
	public Collection<? extends Node> getNodeSet() 
	{
		throw new RuntimeException( "Not yet implemented" );
	}
	
	public Collection<? extends Edge> getEdgeSet()
	{
		throw new RuntimeException( "Not yet implemented" );
	}
	
	@SuppressWarnings( "unchecked" )
    public Iterator<Node> iterator()
    {
	    return (Iterator<Node>) styleGroups.getNodeIterator();
    }
	
	public void addSink( Sink listener )
	{
		listeners.addSink( listener );
	}
	
	public void removeSink( Sink listener )
	{
		listeners.removeSink( listener );
	}
	
	public void addAttributeSink( AttributeSink listener )
	{
		listeners.addAttributeSink( listener );
	}
	
	public void removeAttributeSink( AttributeSink listener )
	{
		listeners.removeAttributeSink( listener );
	}
	
	public void addElementSink( ElementSink listener )
	{
		listeners.addElementSink( listener );
	}
	
	public void removeElementSink( ElementSink listener )
	{
		listeners.removeElementSink( listener );
	}

	public Iterable<AttributeSink> attributeSinks()
	{
		return listeners.attributeSinks();
	}
	
	public Iterable<ElementSink> elementSinks()
	{
		return listeners.elementSinks();
	}
	
	public Edge addEdge( String id, String from, String to ) throws IdAlreadyInUseException,
            ElementNotFoundException
    {
		return addEdge( getId(), newEvent(), id, from, to, false, null );
    }

	public Edge addEdge( String id, String from, String to, boolean directed )
            throws IdAlreadyInUseException, ElementNotFoundException
    {
		return addEdge( getId(), newEvent(), id, from, to, directed, null );
    }

	public Node addNode( String id ) throws IdAlreadyInUseException
    {
		return addNode( getId(), newEvent(), id, null );
    }

	public void clear()
    {
		clear( getId(), newEvent() );
    }

	public Edge removeEdge( String id ) throws ElementNotFoundException
    {
	    return removeEdge( getId(), newEvent(), id );
    }
	
	public Edge removeEdge( String from, String to ) throws ElementNotFoundException
	{
		return removeEdge( getId(), newEvent(), from, to );
	}

	public Node removeNode( String id ) throws ElementNotFoundException
    {
	    return removeNode( getId(), newEvent(), id );
    }
	
	public org.graphstream.ui.swingViewer.Viewer display() { throw new RuntimeException( "not implemented !" ); }
	public org.graphstream.ui.swingViewer.Viewer display( boolean autoLayout ) { throw new RuntimeException( "not implemented !" ); }

	public void stepBegins( double step )
	{
		stepBegins( getId(), newEvent(), step );
	}
	
	public EdgeFactory edgeFactory()
    {
		return null;
    }

	public int getEdgeCount()
    {
	    return styleGroups.getEdgeCount();
    }

	public Iterator<? extends Edge> getEdgeIterator()
    {
	    return styleGroups.getEdgeIterator();
    }

	public int getNodeCount()
    {
	    return styleGroups.getNodeCount();
    }
	
	public int getSpriteCount()
	{
		return styleGroups.getSpriteCount();
	}

	public Iterator<? extends Node> getNodeIterator()
    {
	    return styleGroups.getNodeIterator();
    }
	
	public Iterator<? extends GraphicSprite> getSpriteIterator()
	{
		return styleGroups.getSpriteIterator();
	}
	
	public Iterable<? extends GraphicSprite> spriteSet()
	{
		return styleGroups.sprites();
	}

	public boolean isAutoCreationEnabled()
    {
	    return false;
    }

	public boolean isStrictCheckingEnabled()
    {
	    return false;
    }

	public NodeFactory nodeFactory()
    {
	    return null;
    }

	public void setAutoCreate( boolean on )
    {
		throw new RuntimeException( "not implemented !" );
    }

	public void setStrictChecking( boolean on )
    {
		throw new RuntimeException( "not implemented !" );
    }
	
	public boolean isStrict()
    {
	    return false;
    }

	public void setStrict( boolean on )
    {
		throw new RuntimeException( "not implemented !" );
    }

	public void setEdgeFactory( EdgeFactory ef )
    {
		throw new RuntimeException( "you cannot change the edge factory for graphic graphs !" );	    
    }

	public void setNodeFactory( NodeFactory nf )
    {
		throw new RuntimeException( "you cannot change the node factory for graphic graphs !" );	    
    }

	public void read( String filename ) throws IOException
    {
		throw new RuntimeException( "not implemented !" );
    }

	public void read( FileSource input, String filename ) throws IOException
    {
		throw new RuntimeException( "not implemented !" );
    }

	public void write( FileSink output, String filename ) throws IOException
    {
		throw new RuntimeException( "not implemented !" );
    }

	public void write( String filename ) throws IOException
    {
		throw new RuntimeException( "not implemented !" );
    }

// Output interface
	
	public void edgeAttributeAdded( String sourceId, long timeId, String edgeId, String attribute, Object value )
    {
		if( sinkTime.isNewEvent( sourceId, timeId ) )
		{
			Edge edge = getEdge( edgeId );

			if( edge != null )
				((GraphicEdge)edge).addAttribute_( sourceId, timeId, attribute, value );
		}
    }

	public void edgeAttributeChanged( String sourceId, long timeId, String edgeId, String attribute,
            Object oldValue, Object newValue )
    {
		if( sinkTime.isNewEvent( sourceId, timeId ) )
		{
			Edge edge = getEdge( edgeId );

			if( edge != null )
				((GraphicEdge)edge).changeAttribute_( sourceId, timeId, attribute, newValue );
		}
    }	

	public void edgeAttributeRemoved( String sourceId, long timeId, String edgeId, String attribute )
    {
		if( sinkTime.isNewEvent( sourceId, timeId ) )
		{
			Edge edge = getEdge( edgeId );

			if( edge != null )
				((GraphicEdge)edge).removeAttribute_( sourceId, timeId, attribute );
		}
    }

	public void graphAttributeAdded( String sourceId, long timeId, String attribute, Object value )
    {
		if( sinkTime.isNewEvent( sourceId, timeId ) )
			addAttribute_( sourceId, timeId, attribute, value );
    }

	public void graphAttributeChanged( String sourceId, long timeId, String attribute, Object oldValue,
            Object newValue )
    {
		if( sinkTime.isNewEvent( sourceId, timeId ) )
			changeAttribute_( sourceId, timeId, attribute, newValue );
    }

	public void graphAttributeRemoved( String sourceId, long timeId, String attribute )
    {
		if( sinkTime.isNewEvent( sourceId, timeId ) )
			removeAttribute_( sourceId, timeId, attribute );
    }

	public void nodeAttributeAdded( String sourceId, long timeId, String nodeId, String attribute, Object value )
    {
		if( sinkTime.isNewEvent( sourceId, timeId ) )
		{
			Node node = getNode( nodeId );
		
			if( node != null )
				((GraphicNode)node).addAttribute_( sourceId, timeId, attribute, value );
		}
    }

	public void nodeAttributeChanged( String sourceId, long timeId, String nodeId, String attribute,
            Object oldValue, Object newValue )
    {
		if( sinkTime.isNewEvent( sourceId, timeId ) )
		{
			Node node = getNode( nodeId );
		
			if( node != null )
				((GraphicNode)node).changeAttribute_( sourceId, timeId, attribute, newValue );
		}
    }

	public void nodeAttributeRemoved( String sourceId, long timeId, String nodeId, String attribute )
    {
		if( sinkTime.isNewEvent( sourceId, timeId ) )
		{
			Node node = getNode( nodeId );
		
			if( node != null )
				((GraphicNode)node).removeAttribute_( sourceId, timeId, attribute );
		}
    }

	public void edgeAdded( String sourceId, long timeId, String edgeId, String fromNodeId, String toNodeId,
            boolean directed )
    {
		if( sinkTime.isNewEvent( sourceId, timeId ) )
			addEdge( sourceId, timeId, edgeId, fromNodeId, toNodeId, directed, null );
    }

	public void edgeRemoved( String sourceId, long timeId, String edgeId )
    {
		if( sinkTime.isNewEvent( sourceId, timeId ) )
			removeEdge( sourceId, timeId, edgeId );
    }

	public void graphCleared( String sourceId, long timeId )
    {
		if( sinkTime.isNewEvent( sourceId, timeId ) )
			clear( sourceId, timeId );
    }

	public void nodeAdded( String sourceId, long timeId, String nodeId )
    {
		if( sinkTime.isNewEvent( sourceId, timeId ) )
			addNode( sourceId, timeId, nodeId, null );
    }

	public void nodeRemoved( String sourceId, long timeId, String nodeId )
    {
		if( sinkTime.isNewEvent( sourceId, timeId ) )
			removeNode( sourceId, timeId, nodeId );
    }

	public void stepBegins( String sourceId, long timeId, double time )
    {
		step = time;
	
		listeners.sendStepBegins( sourceId, timeId, time );
    }
	
// Sprite interface

	protected void spriteAttribute( AttributeChangeEvent event, Element element, String attribute, Object value )
	{
//System.err.printf( "GG sprite attr %s %s (%s) (%s)%n",
//		event,
//		attribute,
//		value,
//		element != null ? element.getId() : "no element" );

		String spriteId = attribute.substring( 10 );		// Remove the "ui.sprite." prefix.
		int    pos      = spriteId.indexOf( '.' );			// Look if there is something after the sprite id.
		String attr     = null;
		
		if( pos > 0 )
		{
			attr     = spriteId.substring( pos + 1 );		// Cut the sprite id.
			spriteId = spriteId.substring( 0, pos ); 		// Cut the sprite attribute name.
		}
		
		if( attr == null )
		{
			addOrChangeSprite( event, element, spriteId, value );
		}
		else
		{
			if( event == AttributeChangeEvent.ADD )
			{
				GraphicSprite sprite = styleGroups.getSprite( spriteId );
				
				if( sprite != null )
					sprite.addAttribute( attr, value );
			}
			else if( event == AttributeChangeEvent.CHANGE )
			{
				GraphicSprite sprite = styleGroups.getSprite( spriteId );
				
				if( sprite != null )
					sprite.changeAttribute( attr, value );				
			}
			else if( event == AttributeChangeEvent.REMOVE )
			{
				GraphicSprite sprite = styleGroups.getSprite( spriteId );
				
				if( sprite != null )
					sprite.removeAttribute( attr );
			}			
		}
	}
	
	protected void addOrChangeSprite( AttributeChangeEvent event, Element element, String spriteId, Object value )
	{
		if( event == AttributeChangeEvent.ADD || event == AttributeChangeEvent.CHANGE )
		{
			GraphicSprite sprite = styleGroups.getSprite( spriteId );
			
			if( sprite == null )
				sprite = addSprite_( spriteId );

			if( element != null )
			{
				if( element instanceof GraphicNode )
					sprite.attachToNode( (GraphicNode)element );
				else if( element instanceof GraphicEdge )
					sprite.attachToEdge( (GraphicEdge)element );
			}
			
			if( value != null && ( ! ( value instanceof Boolean ) ) )
				positionSprite( sprite, value );
		}
		else if( event == AttributeChangeEvent.REMOVE )
		{
			if( element == null )
			{
				if( styleGroups.getSprite( spriteId ) != null )
				{
					removeSprite_( spriteId );
				}
			}
			else
			{
				GraphicSprite sprite = styleGroups.getSprite( spriteId );
				
				if( sprite != null )
					sprite.detach();
			}
		}
	}
	
	public GraphicSprite addSprite( String id )
	{
		String prefix = String.format( "ui.sprite.%s", id );
		addAttribute( prefix, 0, 0, 0 );
		
		GraphicSprite s = styleGroups.getSprite( id );
		assert( s != null );
		return s;
	}
	
	protected GraphicSprite addSprite_( String id )
	{
		GraphicSprite s = new GraphicSprite( id, this );
		styleGroups.addElement( s );
		graphChanged = true;
		
		return s;
	}
	
	public void removeSprite( String id )
	{
	    String prefix = String.format( "ui.sprite.%s", id );
	    removeAttribute( prefix );
	}
	
	protected GraphicSprite removeSprite_( String id )
	{
		GraphicSprite sprite = (GraphicSprite) styleGroups.getSprite( id );
		
		if( sprite != null )
		{
			sprite.detach();
		    styleGroups.removeElement( sprite );
		    sprite.removed();
		    
		    graphChanged = true;
		}
		
		return sprite;
	}
	
	protected void positionSprite( GraphicSprite sprite, Object value )
	{
//System.err.printf( "GG.positionSprite(%s, %s) =>", sprite.getId(), value );
		if( value instanceof Object[] )
		{
//System.err.printf( " object[]%n" );
			Object[] values = (Object[]) value;
			
			if( values.length == 4 )
			{
				if( values[0] instanceof Number && values[1] instanceof Number
				 && values[2] instanceof Number && values[3] instanceof Style.Units )
				{
					sprite.setPosition(
							((Number)values[0]).floatValue(),
							((Number)values[1]).floatValue(),
							((Number)values[2]).floatValue(),
							(Style.Units)values[3] );					
				}
				else
				{
					System.err.printf( "GraphicGraph : cannot parse values[4] for sprite position.%n" );
				}
			}
			else if( values.length == 3 )
			{
				if( values[0] instanceof Number && values[1] instanceof Number
				 && values[2] instanceof Number )
				{
					sprite.setPosition(
						((Number)values[0]).floatValue(),
						((Number)values[1]).floatValue(),
						((Number)values[2]).floatValue(),
						Units.GU );
				}
				else
				{
					System.err.printf( "GraphicGraph : cannot parse values[3] for sprite position.%n" );
				}
			}
			else if( values.length == 1 )
			{
				if( values[0] instanceof Number )
				{
					sprite.setPosition( ((Number)value).floatValue() );					
				}
				else
				{
					System.err.printf( "GraphicGraph : sprite position percent is not a number.%n" );
				}
			}
			else
			{
				System.err.printf( "GraphicGraph : cannot transform value '%s' (length=%d) into a position%n", values, values.length );
			}
		}
		else if( value instanceof Number )
		{
//System.err.printf( " Number %f%n", ((Number)value).floatValue() );
			sprite.setPosition( ((Number)value).floatValue() );
		}
		else if( value instanceof Value )
		{
//System.err.printf( " Value %s%n", ((Value)value).value );
			sprite.setPosition( ((Value)value).value );
		}
		else if( value instanceof Values )
		{
//System.err.printf( " Values %s%n", ((Values)value) );
			sprite.setPosition( (Values)value );
		}
		else if( value == null )
		{
			throw new RuntimeException( "What do you expect with a null value ?" );
		}
		else
		{
			System.err.printf( "GraphicGraph : cannot place sprite with posiiton '%s' (instance of %s)%n", value,
					value.getClass().getName() );
		}
	}

// Style sheet API
	
	/**
	 * Load a style sheet from an attribute.
	 * @param styleSheetValue The style sheet name of content.
	 * @throws IOException If the loading or parsing of the style sheet failed.
	 */
	protected void loadStyleSheet( String styleSheetValue )
		throws IOException
	{
		if( styleSheetValue.startsWith( "url" ) )
		{
			// Extract the part between '(' and ')'.
			
			int beg = styleSheetValue.indexOf( '(' );
			int end = styleSheetValue.lastIndexOf( ')' );
			
			if( beg >= 0 && end > beg )
				styleSheetValue = styleSheetValue.substring( beg+1, end );
			
			styleSheetValue = styleSheetValue.trim();
			
			// Remove the quotes (') or (").
			
			if( styleSheetValue.startsWith( "'" ) )
			{
				beg = 0;
				end = styleSheetValue.lastIndexOf( '\'' );
				
				if( beg >= 0 && end > beg )
					styleSheetValue = styleSheetValue.substring( beg+1, end );
			}
			
			styleSheetValue = styleSheetValue.trim();
			
			if( styleSheetValue.startsWith( "\"" ) )
			{
				beg = 0;
				end = styleSheetValue.lastIndexOf( '"' );
				
				if( beg >= 0 && end > beg )
					styleSheetValue = styleSheetValue.substring( beg+1, end );			
			}
			
			// That's it.

			styleSheet.parseFromURL( styleSheetValue );
		}
		else // Parse from string, the value is considered to be the style sheet contents.
		{
			styleSheet.parseFromString( styleSheetValue );
		}
	}
	
// Redefinition of the attribute setting mechanism to filter attributes.
	
	@Override
	public void addAttribute( String attribute, Object ... values )
	{
		Matcher matcher = GraphicElement.acceptedAttribute.matcher( attribute );
		
		if( matcher.matches() )
			super.addAttribute( attribute, values );
	}

	public void clearAttributeSinks()
	{
		listeners.clearAttributeSinks();		
	}

	public void clearElementSinks()
	{
		listeners.clearElementSinks();
	}

	public void clearSinks()
	{
		listeners.clearSinks();
	}
}