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

import org.miv.graphstream.algorithm.Algorithms;
import org.miv.graphstream.graph.AbstractElement;
import org.miv.graphstream.graph.Edge;
import org.miv.graphstream.graph.EdgeFactory;
import org.miv.graphstream.graph.Graph;
import org.miv.graphstream.graph.GraphListener;
import org.miv.graphstream.graph.Node;
import org.miv.graphstream.graph.NodeFactory;
import org.miv.graphstream.io.GraphParseException;
import org.miv.graphstream.io.GraphReader;
import org.miv.graphstream.io.GraphWriter;
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
 * @author Yoann Pigné
 * @author Antoine Dutot
 */
public class GraphicGraph extends AbstractElement implements Graph
{
// Attribute

	/**
	 * The way nodes are connected one with another. The map is sorted by node. For each node an
	 * array of edges lists the connectivity.
	 */
	protected HashMap<GraphicNode, ArrayList<GraphicEdge>> connectivity;

	/**
	 * The style.
	 */
	protected StyleSheet styleSheet;

	/**
	 * The style groups (styles and groups of graph elements).
	 */
	protected StyleGroupSet styleGroups;
	
	/**
	 * Allow to know if this graph is or not a multigraph. It is possible do
	 * draw and handle faster non-multigraphs. Multigraphs declare themselves
	 * by putting a "multigraph" attribute on the graph. TODO: this is awful.
	 */
	protected boolean isMultigraph = false;
	
	/**
	 * Set to true each time the graph was modified.
	 */
	public boolean graphChanged;
	
	/**
	 * The style of this graph.
	 */
	public StyleGroup style;
	
	/**
	 * Memorise the step events.
	 */
	public double step = 0;
	
	/**
	 * Set of common algorithms.
	 */
	protected Algorithms algos;

// Construction

	/**
	 * New empty graphic graph.
	 */
	public GraphicGraph()
	{
		super( "GraphicGraph" );

		styleSheet   = new StyleSheet();
		styleGroups  = new StyleGroupSet( styleSheet );
		connectivity = new HashMap<GraphicNode, ArrayList<GraphicEdge>>();
		
		styleGroups.addElement( this );	// Add style to this graph.
		
		style = styleGroups.getStyleFor( this );
	}

// Access

	/**
	 * The links between nodes.
	 */
	public Collection<GraphicEdge> getConnectivity( GraphicNode node )
	{
		return connectivity.get( node );
	}

	/**
	 * The style sheet.
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
	 * Set set of style groups.
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
	 * @return The first node that match the coordinates, or null if no node match the coordinates.
	 */
	public GraphicNode findNode( float x, float y )
	{
		Iterator<? extends Node> nodes = styleGroups.getNodeIterator();
	
		while( nodes.hasNext() )
		{
			GraphicNode node = (GraphicNode) nodes.next();
			
			if( node.contains( x, y ) )
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
	public GraphicSprite findSprite( float x, float y )
	{
		Iterator<? extends GraphicSprite> sprites = styleGroups.getSpriteIterator();
		
		while( sprites.hasNext() )
		{
			GraphicSprite sprite = sprites.next();
			
			if( sprite.contains( x, y ) )
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
	 * @return The first node or sprite that match the coordinate, or null if no node or sprite
	 *    match these coordinates.
	 */
	public GraphicElement findNodeOrSprite( float x, float y )
	{
		GraphicElement e = findNode( x, y );
		
		if( e == null )
			e = findSprite( x, y );
		
		return e;
	}
	
// Command

	public GraphicEdge addEdge( String id, String from, String to, boolean directed, HashMap<String, Object> attributes )
	{
		GraphicNode n1 = (GraphicNode) styleGroups.getNode( from );
		GraphicNode n2 = (GraphicNode) styleGroups.getNode( to );

		// TODO: A better test + raise of an exception

		if( n1 == null || n2 == null )
			throw new RuntimeException(
					"org.miv.graphstream.ui.bufferedData.BufferedGraph.addEdge() : ERROR : on of the nodes does not exist" );

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

	public GraphicNode addNode( String id, float x, float y, float z, HashMap<String, Object> attributes )
	{
		GraphicNode n = new GraphicNode( this, id, x, y, z, attributes );

		styleGroups.addElement( n );
		
		graphChanged = true;
		
		return n;
	}

	public void changeEdge( String id, String attribute, Object value )
	{
		GraphicEdge edge = (GraphicEdge) styleGroups.getEdge( id );
		
		if( edge != null )
		{
			edge.addAttribute( attribute, value );
		
			graphChanged = true;
		}
	}

	public void changeNode( String id, String attribute, Object value )
	{
		GraphicNode node = (GraphicNode) styleGroups.getNode( id );
		
		if( node != null )
		{
			node.addAttribute( attribute, value );

			graphChanged = true;
		}
	}

	public void moveNode( String id, float x, float y, float z )
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
	
	public GraphicSprite addSprite( String id )
	{
		GraphicSprite s = new GraphicSprite( id, this );

		styleGroups.addElement( s );
		
		graphChanged = true;
		
		return s;
	}

	public GraphicSprite removeSprite( String id )
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

	public void attachSpriteToNode( String id, String nodeId )
	{
		GraphicSprite sprite = styleGroups.getSprite( id );
		GraphicNode   node   = (GraphicNode) styleGroups.getNode( nodeId );
		
		if( sprite != null && node != null )
			sprite.attachToNode( node );
	}

	public void attachSpriteToEdge( String id, String edgeId )
	{
		GraphicSprite sprite = styleGroups.getSprite( id );
		GraphicEdge   edge   = (GraphicEdge) styleGroups.getEdge( edgeId );
		
		if( sprite != null && edge != null )
			sprite.attachToEdge( edge );
	}

	public void detachSprite( String id )
	{
		GraphicSprite sprite = styleGroups.getSprite( id );
		
		sprite.detach();
	}

	public void positionSprite( String id, float percent )
	{
		GraphicSprite sprite = styleGroups.getSprite( id );
		
		sprite.setPosition( percent );
	}

	public void positionSprite( String id, float x, float y, float z, Style.Units units )
	{
		GraphicSprite sprite = styleGroups.getSprite( id );
		
		sprite.setPosition( x, y, z, units );
	}

	public void positionSprite( String id, float x, float y, float z )
	{
		GraphicSprite sprite = styleGroups.getSprite( id );
		
		sprite.setPosition( x, y, z, Units.GU );
	}
	
	public void addSpriteAttribute( String id, String attribute, Object value )
	{
		GraphicSprite sprite = styleGroups.getSprite( id );
		
		sprite.addAttribute( attribute, value );
	}

	public void removeSpriteAttribute( String id, String attribute )
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
					System.err.printf( "Error while parsing style sheet for graph '%s' : %n", id );
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

// Graph interface
	
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

	public void addGraphListener( GraphListener listener )
    {
		throw new RuntimeException( "not implemented !" );
    }

	public Algorithms algorithm()
    {
		if( algos == null )
			algos = new Algorithms( this );
		
		return algos;
    }

	public void clearListeners()
    {
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

	public Collection<? extends Edge> getEdgeSet()
    {
		throw new RuntimeException( "getEdgeSet() Not implemented in this " );
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
	
	public Collection<? extends Node> getNodeSet()
    {
		throw new RuntimeException( "getNodeSet() Not implemented in this " );
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

	public void read( String filename ) throws IOException, GraphParseException, NotFoundException
    {
		throw new RuntimeException( "not implemented !" );
    }

	public void read( GraphReader reader, String filename ) throws IOException, GraphParseException
    {
		throw new RuntimeException( "not implemented !" );
    }

	public int readPositionFile( String posFileName ) throws IOException
    {
		throw new RuntimeException( "not implemented !" );
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

	public void write( String filename ) throws IOException
    {
		throw new RuntimeException( "not implemented !" );
    }

	public void write( GraphWriter writer, String filename ) throws IOException
    {
		throw new RuntimeException( "not implemented !" );
    }

	public void stepBegins( double time )
	{
		step = time;
	}
}