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

package org.miv.graphstream.ui2.graphicGraph;

import java.io.IOException;
import java.util.*;

import org.miv.graphstream.graph.implementations.AbstractElement;
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
import org.miv.graphstream.io.GraphReader;
import org.miv.graphstream.io.GraphWriter;
import org.miv.graphstream.io2.file.FileInput;
import org.miv.graphstream.io2.file.FileOutput;
import org.miv.graphstream.ui2.graphicGraph.stylesheet.Style;
import org.miv.graphstream.ui2.graphicGraph.stylesheet.StyleSheet;
import org.miv.graphstream.ui2.graphicGraph.stylesheet.StyleConstants.Units;

import org.miv.util.NotFoundException;
import org.miv.util.SingletonException;

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
 * The style sheet is updated on the graph using an attribute correspondingly named "stylesheet".
 * It can be a string that contains the whole style sheet, or an URL of the form :
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
	 * Allow to know if this graph is or not a multigraph. It is possible do
	 * draw and handle faster non-multigraphs. Multigraphs declare themselves
	 * by putting a "multigraph" attribute on the graph. TODO: this is awful.
	 */
	protected boolean isMultigraph = false;
	
	/**
	 * Memorise the step events.
	 */
	public double step = 0;
	
// Construction

	/**
	 * New empty graphic graph.
	 * 
	 * A default style sheet is created, it then can be "cascaded" with other style sheets.
	 */
	public GraphicGraph()
	{
		super( "GraphicGraph" );

		styleSheet   = new StyleSheet();
		styleGroups  = new StyleGroupSet( styleSheet );
		connectivity = new HashMap<GraphicNode, ArrayList<GraphicEdge>>();

		styleGroups.addListener( this );
		styleGroups.addElement( this );	// Add style to this graph.
		
		style = styleGroups.getStyleFor( this );
	}

// Access

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
	
	/**
	 * Return true if this represents a multi-graph, that is a graph
	 * that can have several edges between two nodes..
	 * @return True if this is a multi-graph.
	 */
	public boolean isMultiGraph()
	{
		return isMultigraph;
	}
		
	/**
	 * Find the first node that is at the given coordinates. If there are several such nodes,
	 * only one is selected. The coordinates are given in 2D (as the screen is 2D) and if the
	 * graph is in 3D the z coordinate is ignored.
	 * @param x The X coordinate.
	 * @param y The Y coordinate.
	 * @param z The Z coordinate.
	 * @return The first node that match the coordinates, or null if no node match the coordinates.
	 */
	public GraphicNode findNode( float x, float y, float z )
	{
		for( Node n: styleGroups.nodes() )
		{
			GraphicNode node = (GraphicNode) n;
			
			if( node.contains( x, y, z ) )
				return node;
		}
		
		return null;
	}
	
	/**
	 * Find the first sprite that is at the given coordinates. If there are several such sprites,
	 * only one is selected. The coordinates are given in 2D (as the screen is 2D) and if the
	 * graph is in 3D the z coordinate is ignored.
	 * @param x The X coordinate.
	 * @param y The Y coordinate.
	 * @return The first sprite that match the coordinates, or null if no sprite match the coordinates.
	 */
	public GraphicSprite findSprite( float x, float y, float z )
	{
		for( GraphicSprite sprite: styleGroups.sprites() )
		{
			if( sprite.contains( x, y, z ) )
				return sprite;
		}
		
		return null;
	}

	/**
	 * Find a node or sprite at or around the given coordinates. If there is a node and a sprite,
	 * the node is preferred. Return null if nothing is found at (x,y). The coordinates are given in
	 * 2D (as the screen is 2D) and if the graph is in 3D, the z coordinate is ignored.  
	 * @param x The X coordinate.
	 * @param y The Y coordinate.
	 * @param z The Z coordinate.
	 * @return The first node or sprite that match the coordinate, or null if no node or sprite
	 *    match these coordinates.
	 */
	public GraphicElement findNodeOrSprite( float x, float y, float z )
	{
		GraphicElement e = findNode( x, y, z );
		
		if( e == null )
			e = findSprite( x, y, z );
		
		return e;
	}
	
// Command

	protected GraphicEdge addEdge( String id, String from, String to, boolean directed, HashMap<String, Object> attributes )
	{
		GraphicNode n1 = (GraphicNode) styleGroups.getNode( from );
		GraphicNode n2 = (GraphicNode) styleGroups.getNode( to );

		// TODO: A better test + raise of an exception

		if( n1 == null || n2 == null )
			throw new RuntimeException(
					"org.miv.graphstream.ui.graphicGraph.GraphicGraph.addEdge() : ERROR : one of the nodes does not exist" );

		GraphicEdge edge = new GraphicEdge( id, n1, n2, directed, attributes );
		
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
		
		graphChanged = true;

		if( isMultigraph )
			edge.countSameEdges( l1 );
		
		return edge;
	}

	protected GraphicNode addNode( String id, float x, float y, float z, HashMap<String, Object> attributes )
	{
		GraphicNode n = new GraphicNode( this, id, x, y, z, attributes );

		styleGroups.addElement( n );
		
		graphChanged = true;
		
		return n;
	}

	protected void changeEdge( String id, String attribute, Object value )
	{
		GraphicEdge edge = (GraphicEdge) styleGroups.getEdge( id );
		
		if( edge != null )
		{
			edge.addAttribute( attribute, value );
		
			graphChanged = true;
		}
	}

	protected void changeNode( String id, String attribute, Object value )
	{
		GraphicNode node = (GraphicNode) styleGroups.getNode( id );
		
		if( node != null )
		{
			node.addAttribute( attribute, value );

			graphChanged = true;
		}
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

	public Edge removeEdge( String id ) throws NotFoundException
	{
		GraphicEdge edge = (GraphicEdge) styleGroups.getEdge( id );
		
		if( edge != null )
		{
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

	public Edge removeEdge( String from, String to ) throws NotFoundException
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
						removeEdge( edge0.getId() );
						return edge0;
					}
				}
			}
		}
		
		return null;
	}

	public Node removeNode( String id )
	{
		GraphicNode node = (GraphicNode) styleGroups.getNode( id );
		
		if( node != null )
		{
		    if(connectivity.get(node) != null)
		    {
		    	for( GraphicEdge edge: connectivity.get( node ) )
		    	{
		    		GraphicNode node2 = edge.otherNode( node );
		    		connectivity.get( node2 ).remove( edge );
		    	}
		    	connectivity.remove( node );
		    }
		    
		    styleGroups.removeElement( node );
		    node.removed();
			
		    graphChanged = true;
		}
		
		return node;
	}
	
	protected GraphicSprite addSprite( String id )
	{
		GraphicSprite s = new GraphicSprite( id, this );

		styleGroups.addElement( s );
		
		graphChanged = true;
		
		return s;
	}

	protected GraphicSprite removeSprite( String id )
	{
		GraphicSprite sprite = (GraphicSprite) styleGroups.getSprite( id );
		
		if( sprite != null )
		{
		    styleGroups.removeElement( sprite );
		    sprite.removed();
			
		    graphChanged = true;
		}
		
		return sprite;
	}

	protected void attachSpriteToNode( String id, String nodeId )
	{
		GraphicSprite sprite = styleGroups.getSprite( id );
		GraphicNode   node   = (GraphicNode) styleGroups.getNode( nodeId );
		
		if( sprite != null && node != null )
			sprite.attachToNode( node );
	}

	protected void attachSpriteToEdge( String id, String edgeId )
	{
		GraphicSprite sprite = styleGroups.getSprite( id );
		GraphicEdge   edge   = (GraphicEdge) styleGroups.getEdge( edgeId );
		
		if( sprite != null && edge != null )
			sprite.attachToEdge( edge );
	}

	protected void detachSprite( String id )
	{
		GraphicSprite sprite = styleGroups.getSprite( id );
		
		sprite.detach();
	}

	protected void positionSprite( String id, float percent )
	{
		GraphicSprite sprite = styleGroups.getSprite( id );
		
		sprite.setPosition( percent );
	}

	protected void positionSprite( String id, float x, float y, float z, Style.Units units )
	{
		GraphicSprite sprite = styleGroups.getSprite( id );
		
		sprite.setPosition( x, y, z, units );
	}

	protected void positionSprite( String id, float x, float y, float z )
	{
		GraphicSprite sprite = styleGroups.getSprite( id );
		
		sprite.setPosition( x, y, z, Units.GU );
	}
	
	protected void addSpriteAttribute( String id, String attribute, Object value )
	{
		GraphicSprite sprite = styleGroups.getSprite( id );
		
		sprite.addAttribute( attribute, value );
	}

	protected void removeSpriteAttribute( String id, String attribute )
	{
		GraphicSprite sprite = styleGroups.getSprite( id );

		sprite.removeAttribute( attribute );
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
	protected void attributeChanged( String attribute, Object oldValue, Object newValue )
	{
		if( attribute.equals( "ui.stylesheet" ) || attribute.equals( "stylesheet" ) )
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
			else if( newValue == null )
			{
				// Remove the style.
				
				styleSheet.clear();
				graphChanged = true;
			}
		}
		else if( attribute.equals( "ui.multigraph" ) )
		{
			if( newValue == null )
			     isMultigraph = false;
			else isMultigraph = true;
		}
	}
	
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

	public void clear()
	{
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
	
	public void elementStyleChanged( Element element, StyleGroup style )
    {
		if( element instanceof GraphicElement )
		{
			GraphicElement ge = (GraphicElement) element;
			ge.style          = style;
			graphChanged      = true;
		}
    }

// Graph interface

	public Iterable<? extends Edge> edgeSet()
    {
		return styleGroups.edges();
    }

	public Iterable<? extends Node> nodeSet()
    {
	    return styleGroups.nodes();
    }

	@SuppressWarnings( "unchecked" )
    public Iterator<Node> iterator()
    {
	    return (Iterator<Node>) styleGroups.nodes();
    }
	
	public void addGraphListener( GraphListener listener )
    {
		throw new RuntimeException( "not implemented !" );
    }

	public void addGraphAttributesListener( GraphAttributesListener listener )
    {
		throw new RuntimeException( "not implemented !" );
    }

	public void addGraphElementsListener( GraphElementsListener listener )
    {
		throw new RuntimeException( "not implemented !" );
    }

	public Iterable<GraphAttributesListener> getGraphAttributesListeners()
    {
		throw new RuntimeException( "not implemented !" );
    }

	public Iterable<GraphElementsListener> getGraphElementsListeners()
    {
		throw new RuntimeException( "not implemented !" );
    }

	public void removeGraphAttributesListener( GraphAttributesListener listener )
    {
    }

	public void removeGraphElementsListener( GraphElementsListener listener )
    {
    }

	public void clearListeners()
    {
		throw new RuntimeException( "not implemented !" );
    }
	
	public Edge addEdge( String id, String from, String to ) throws SingletonException,
            NotFoundException
    {
		return addEdge( id, from, to, false, null );
    }

	public Edge addEdge( String id, String from, String to, boolean directed )
            throws SingletonException, NotFoundException
    {
		return addEdge( id, from, to, directed, null );
    }

	public Node addNode( String id ) throws SingletonException
    {
		return addNode( id, 0, 0, 0, null );
    }

	public org.miv.graphstream.ui.GraphViewerRemote display()
    {
		throw new RuntimeException( "not implemented !" );
    }

	public org.miv.graphstream.ui.GraphViewerRemote display( boolean autoLayout )
    {
		throw new RuntimeException( "not implemented !" );
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

	public Iterable<? extends Edge> getEdgeSet()
    {
		return styleGroups.edges();
    }

	public List<GraphListener> getGraphListeners()
    {
	    return null;
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
	
	public Iterable<? extends Node> getNodeSet()
    {
		return styleGroups.nodes();
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

	public void removeGraphListener( GraphListener listener )
    {
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

	public void read( String filename ) throws IOException, GraphParseException, NotFoundException
    {
		throw new RuntimeException( "not implemented !" );
    }

	public void read( FileInput input, String filename ) throws IOException, GraphParseException
    {
		throw new RuntimeException( "not implemented !" );
    }

	public void write( FileOutput output, String filename ) throws IOException
    {
		throw new RuntimeException( "not implemented !" );
    }

	public void write( String filename ) throws IOException
    {
		throw new RuntimeException( "not implemented !" );
    }

// Output interface
	
	public void edgeAttributeAdded( String graphId, String edgeId, String attribute, Object value )
    {
    }

	public void edgeAttributeChanged( String graphId, String edgeId, String attribute,
            Object oldValue, Object newValue )
    {
    }

	public void edgeAttributeRemoved( String graphId, String edgeId, String attribute )
    {
    }

	public void graphAttributeAdded( String graphId, String attribute, Object value )
    {
    }

	public void graphAttributeChanged( String graphId, String attribute, Object oldValue,
            Object newValue )
    {
    }

	public void graphAttributeRemoved( String graphId, String attribute )
    {
    }

	public void nodeAttributeAdded( String graphId, String nodeId, String attribute, Object value )
    {
    }

	public void nodeAttributeChanged( String graphId, String nodeId, String attribute,
            Object oldValue, Object newValue )
    {
    }

	public void nodeAttributeRemoved( String graphId, String nodeId, String attribute )
    {
    }

	public void edgeAdded( String graphId, String edgeId, String fromNodeId, String toNodeId,
            boolean directed )
    {
    }

	public void edgeRemoved( String graphId, String edgeId )
    {
    }

	public void graphCleared( String graphId )
    {
    }

	public void nodeAdded( String graphId, String nodeId )
    {
    }

	public void nodeRemoved( String graphId, String nodeId )
    {
    }

	public void stepBegins( String graphId, double time )
    {
		step = time;
    }

	public void stepBegins( double time )
    {
		step = time;
    }
}