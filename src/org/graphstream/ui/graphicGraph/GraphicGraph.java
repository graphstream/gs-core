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

package org.graphstream.ui.graphicGraph;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.graphstream.stream.GraphParseException;
import org.graphstream.stream.Pipe;
import org.graphstream.stream.SourceBase;
import org.graphstream.stream.file.FileSink;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.sync.SinkTime;
import org.graphstream.ui.GraphViewerRemote;
import org.graphstream.ui.graphicGraph.stylesheet.Rule;
import org.graphstream.ui.graphicGraph.stylesheet.Style;
import org.graphstream.ui.graphicGraph.stylesheet.StyleSheet;
import org.graphstream.ui.graphicGraph.stylesheet.StyleSheetListener;

/**
 * Graph representation used in display classes.
 * 
 * <p>
 * The purpose of the graphic graph is to represent a graph with some graphic attributes (like
 * position, label, etc.) stored as fields in the nodes and edges and most of the style in a style
 * sheet that try imitate the way CSS works. For example, the GraphicNode class defines a label,
 * a position (x,y,z) and a style that is taken from the style sheet.
 * </p>
 * 
 * <p>
 * The style sheet is updated on the graph using an attribute correspondingly named "stylesheet".
 * It can be a string that contains the whole style sheet, or an url of the form :
 * </p>
 * 
 * <pre>
 *  url(name)
 * </pre>
 * 
 * <p>
 * The graphic graph is a listener of the style sheet. Each time a style changes the node and
 * edges are updated accordingly (their checkStyle() method is called).
 * </p>
 * 
 * <p>
 * To render the elements in this graphic graph you can iterate on them using the {@link #getNodes()},
 * {@link #getEdges()} and {@link #getSpriteManager()} methods, but this is not the better
 * way. The style sheet defines a z-index property that tells which elements are under others
 * when drawn. Therefore you should follow this ordering to render elements. This can be done using
 * the {@link #getZIndex()}. This method returns an array where each cell maps to one of the 256
 * possible z-index values. Each cell is either empty (and therefore contains null) or contains
 * an instance of the Elements class. This class defines all the elements (nodes, edges, sprites)
 * that must be rendered at this z-index.
 * </p>
 * 
 * <p>
 * The z-index values range from -127 to +127, but arrays are index from 0. Therefore, in the
 * array returned by {@link #getZIndex()}, the elements at z-index=0 are in fact in cell 128. 
 * </p>
 * 
 * <p>
 * Although this class implements Graph, its purpose is to be as light as possible and therefore
 * some operations throw a RuntimeException telling the operation is not implemented. They are
 * documented. The purpose of the runtime exception is to debug the application.
 * </p>
 * 
 * <p>
 * This class handles correctly multi-graph (graphs that can have several edges between
 * two nodes) at the conditions of putting "multigraph"
 * attribute on the graph. The MultiGraph of GraphStream does this. This is a (dirty) hack,
 * but to let the GraphicGraph be a light data structure and to avoid much computation
 * this is required.
 * </p>
 * 
 * <p>
 * This class also provides some static utility methods that allow to convert attributes to
 * specific values. For example the {@link #convertColor(Object)} method will do the most
 * to convert the given value, whatever its type, to a color.
 * </p>
 */
public class GraphicGraph extends AbstractElement implements Graph, StyleSheetListener
{
// Attributes

	/**
	 * All the nodes.
	 */
	protected HashMap<String, GraphicNode> nodes;

	/**
	 * All the edges.
	 */
	protected HashMap<String, GraphicEdge> edges;

	/**
	 * The way nodes are connected one with another.
	 */
	protected HashMap<GraphicNode, ArrayList<GraphicEdge>> connectivity;

	/**
	 * The sprites. This is created as soon as used.
	 */
	protected SpriteManager spriteManager;
	
	/**
	 * The style.
	 */
	protected StyleSheet styleSheet;
	
	/**
	 * Allow to know if this graph is or not a multi-graph. It is possible do
	 * draw and handle faster non-multi-graphs. Multi-graphs declare themselves
	 * by putting a "multigraph" attribute on the graph. TODO: this is awful.
	 */
	protected boolean isMultigraph = false;
	
	/**
	 * The style.
	 */
	protected Rule style;
	
	/**
	 * Set to true each time the graph was modified.
	 */
	public boolean graphChanged;
	
	/**
	 * Elements ordered by their z-index. For rendering.
	 */
	public ArrayList<Elements> zIndex = new ArrayList<Elements>();

	/**
	 * The lowest index available in the z-index array.
	 */
	public int lowestZIndex = 256;
	
	/**
	 * The highest index available in the z-index array.
	 */
	public int highestZIndex = 0;
	
	/**
	 * All the elements that cast a shadow.
	 */
	public HashSet<GraphicElement> shadows;
	
	/**
	 * Memorise the step events.
	 */
	public double step = 0;
	
	protected GraphListeners listeners;
	
	/**
	 * Store the set of elements to render at one given z-index.
	 */
	public static class Elements
	{
		/**
		 * The z-index.
		 */
		int zIndex;
		
		/**
		 * Set of nodes at this index.
		 */
		public HashMap<String,GraphicNode> nodes;
		
		/**
		 * Set of edges at this index.
		 */
		public HashMap<String,GraphicEdge> edges;
		
		/**
		 * set of sprites at this index.
		 */
		public HashMap<String,GraphicSprite> sprites;
		
		/**
		 * New empty set of elements.
		 * @param index The z-index of the elements.
		 */
		public Elements( int index )
		{
			zIndex  = index;
			nodes   = new HashMap<String,GraphicNode>();
			edges   = new HashMap<String,GraphicEdge>();
			sprites = new HashMap<String,GraphicSprite>();
		}
		
		/**
		 * The edges at this z-index.
		 * @return The edge set at this index.
		 */
		public Map<String,GraphicEdge> getEdgeSet() { return edges; }

		/**
		 * The nodes at this z-index.
		 * @return The node set at this index.
		 */
		public Map<String,GraphicNode> getNodeSet() { return nodes; }

		/**
		 * The sprites at this z-index.
		 * @return The sprite set at this index.
		 */
		public Map<String,GraphicSprite> getSpriteSet() { return sprites; }
		
		/**
		 * True if all sets are empty (no node, edge or sprite).
		 * @return True if all sets are empty.
		 */
		public boolean isEmpty() { return( edges.isEmpty() && nodes.isEmpty() && sprites.isEmpty() ); }
	}
	
// Construction

	/**
	 * New empty graphic graph.
	 */
	public GraphicGraph()
	{
		super( "" );

		listeners	 = new GraphListeners();
		zIndex       = new ArrayList<Elements>( 256 );
		styleSheet   = new StyleSheet();
		nodes        = new HashMap<String, GraphicNode>();
		edges        = new HashMap<String, GraphicEdge>();
		connectivity = new HashMap<GraphicNode, ArrayList<GraphicEdge>>();
		
		for( int i=0; i<256; ++i )
			zIndex.add( null );
		
		styleSheet.addListener( this );
	}

// Access

	@Override
	protected String myGraphId()
	{
		return getId();
	}
	
	@Override
	protected long newEvent()
	{
		return listeners.newEvent();
	}
	
	/**
	 * The set of nodes. Use the z-index instead of iterating on the node set
	 * for rendering.
	 * @see #getZIndex()
	 */
	public Collection<GraphicNode> getNodes()
	{
		return nodes.values();
	}

	/**
	 * The set of edges. Use the z-index instead of iterating on the node set
	 * for rendering.
	 * @see #getZIndex()
	 */
	public Collection<GraphicEdge> getEdges()
	{
		return edges.values();
	}

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
	 * The style associated with the graph.
	 * @return The graph style.
	 */
	public Style getStyle()
	{
		if( style != null )
			return style.getStyle();
		
		return null;
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
	 * The sprite manager. Use the z-index instead of iterating on the sprites of
	 * the sprite manager for rendering.
	 * @see #getZIndex()
	 * @return A sprite manager.
	 */
	public SpriteManager getSpriteManager()
	{
		if( spriteManager == null )
			spriteManager = new SpriteManager( this );
		
		return spriteManager;
	}
	
	/**
	 * The z-index is an array of 256 cells containing the nodes, edges and sprites
	 * to render in correct rendering order.
	 * 
	 * <p>
	 * The first cells are the firsts elements
	 * to render and the last cells the last to render. Each cell contains a set of
	 * elements or null if there is nothing at this index. A set of elements is
	 * represented by an instance of the Elements class that contains the z-index
	 * value and three map of elements, one for the nodes, one for the edges and
	 * one for the sprites.
	 * </p>
	 * 
	 * <p>
	 * The z-index should always be chosen over the direct iteration on node, edge
	 * and sprite sets for rendering.
	 * </p>
	 * 
	 * <p>
	 * As negative indices are accepted by style sheets, a z-index of 0 is stored
	 * in the array at index 128. This means that 127 maps the the -1 z-index. In
	 * this implementation, there are only 256 possible z-indices. 
	 * </p>
	 * 
	 * <p>
	 * You should also check the {@link #getShadowCasts()} method for a list of all
	 * elements that cast a shadow. These must be drawn before any other element.
	 * </p>
	 * 
	 * @return The z-index.
	 * @see #getShadowCasts()
	 */
	public ArrayList<Elements> getZIndex()
	{
		return zIndex;
	}
	
	/**
	 * All the elements that cast a shadow. As shadows must be drawn first, this list should be
	 * consulted before looking at the z-index. If there are not elements that cast shadows, null
	 * is returned.
	 * @return The set of graphic elements that cast a shadow, or null if there are no shadows.
	 * @see #getZIndex()
	 */
	public Set<GraphicElement> getShadowCasts()
	{
		return shadows;
	}
	
	/**
	 * The lowest value of z-index seen.
	 * @return An integer between 0 and 255.
	 */
	public int getLowestZIndex()
	{
		return lowestZIndex;
	}
	
	/**
	 * The highest value of z-index seen.
	 * @return An integer between 0 and 255.
	 */
	public int getHighestZIndex()
	{
		return highestZIndex;
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
		for( GraphicNode node: nodes.values() )
		{
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
		if( spriteManager != null )
			return spriteManager.findSprite( x, y );
		
		return null;
	}

	/**
	 * Find a node or sprite at or arround the given coordinates. If there is a node and a sprite,
	 * the node is prefered. Return null if nothing is found at (x,y). The coordinates are given in
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
	
	public double getStep()
	{
		return step;
	}
	
// Commands
	
	/**
	 * The position of z-index 0 in the zIndex array (z-indices can be negative).
	 */
	protected static int ZINDEX_OFFSET = 128;
	
	/**
	 * Register a node, edge, or sprite in the z-index array.
	 * @param i The index.
	 * @param element The element to insert at the given index.
	 */
	protected void registerAtIndex( int i, Element element )
	{
		i += ZINDEX_OFFSET;
		
		if( i < 0 ) i = 0;
		else if( i > 255 ) i = 255;
		
		Elements index = zIndex.get( i );
		
		if( index == null )
		{
			index = new Elements( i );
			zIndex.set( i, index );
		}
		
		Object o = null;
		
		if( element instanceof GraphicNode )
		{
			o = index.nodes.put( element.getId(), (GraphicNode)element );
		}
		else if( element instanceof GraphicEdge )
		{
			o = index.edges.put( element.getId(), (GraphicEdge)element );
		}
		else if( element instanceof GraphicSprite )
		{
			o = index.sprites.put( element.getId(), (GraphicSprite)element );			
		}
		else
		{
			throw new RuntimeException(
					"inconsistency in z-index : non-edge, non-node, non-sprite object inserted" );
		}
		
		if( o != null )
		{
			//throw new RuntimeException(
			System.err.printf(
					"inconsistency in z-index : double insertion of element %s at index %d (element=%s).%n", element.getId(), i, element.getClass().getName() );
		}

		if( i < lowestZIndex )
			lowestZIndex = i;
		if( i > highestZIndex )
			highestZIndex = i;
	}

	/**
	 * Remove a node, edge or sprite of the z-index array.
	 * @param i The index at which the element must be.
	 * @param element The element to remove.
	 */
	protected void unregisterAtIndex( int i, Element element )
	{
		i += ZINDEX_OFFSET;
		
		if( i < 0 || i > 255 )
			throw new RuntimeException( "inconsistency: z-index value of removed element "+element.getId()+" is invalid "+i );
		
		Elements index = zIndex.get( i );
		
		if( index != null )
		{
			Object o = null;
			
			if( element instanceof GraphicNode )
			{
				o = index.nodes.remove( element.getId() );
			}
			else if( element instanceof GraphicEdge )
			{
				o = index.edges.remove( element.getId() );
			}
			else if( element instanceof GraphicSprite )
			{
				o = index.sprites.remove( element.getId() );
			}
			
			if( o == null )
				throw new RuntimeException(
						"inconsistency in z-index : cannot unregister element "+element.getId()+" from z-index "+i );
			
			if( index.isEmpty() )
			{
				// Remove the index, and reset the lowest and highest indices.
				
				zIndex.set(  i, null );
			
				if( i == lowestZIndex )
				{
					int j = i;
					
					while( j < 256 && zIndex.get( j ) == null )
						lowestZIndex = j++;
				}
				else if( i == highestZIndex )
				{
					int j = i;
					
					while(  j >= 0 && zIndex.get( j ) == null )
						highestZIndex = j--;					
				}
			}
		}
		else
		{
			throw new RuntimeException(
				"inconsistency in z-index : cannot unregister element "+element.getId()+" from z-index "+i );
		}
	}
	
	/**
	 * Register an element as casting a shadow.
	 * @param element The element to add.
	 */
	public void registerShadow( GraphicElement element )
	{
		if( shadows == null )
			shadows = new HashSet<GraphicElement>();
		
		shadows.add( element );
	}
	
	/**
	 * Check that an element is not in the shadow casting list, else remove it.
	 * @param element The element to check.
	 */
	public void unregisterShadow( GraphicElement element )
	{
		if( shadows != null )
		{
			shadows.remove( element );
		
			if( shadows.size() <= 0 )
				shadows = null;
		}
	}

	// @SuppressWarnings( "unchecked" )
	public GraphicEdge addEdge( String id, String from, String to, boolean directed, HashMap<String, Object> attributes )
	{
		GraphicNode n1 = nodes.get( from );
		GraphicNode n2 = nodes.get( to );

		// TODO: A better test + raise of an exception

		if( n1 == null || n2 == null )
			throw new RuntimeException(
					"org.miv.graphstream.ui.bufferedData.BufferedGraph.addEdge() : ERROR : on of the nodes does not exist" );

		GraphicEdge edge = new GraphicEdge( id, n1, n2, directed, attributes );

		GraphicEdge old = edges.put( id, edge );
		
		if( old != null )
		{
			System.err.printf( "Double insertiong of edge %s (%s -- %s) in graphic graph (old %s -- %s)%n",
					id, from, to, old.from.getId(), old.to.getId() );
		}

		ArrayList<GraphicEdge> l1 = connectivity.get( n1 );

		if( l1 == null )
		{
			l1 = new ArrayList<GraphicEdge>();
			connectivity.put( n1, l1 );
		}

		l1.add( edge );
		
		ArrayList<GraphicEdge> l2 = connectivity.get( n2 );

		if( l2 == null )
		{
			l2 = new ArrayList<GraphicEdge>();
			connectivity.put( n2, l2 );
		}

		l2.add( edge );
		
		graphChanged = true;

		if( isMultigraph )
			edge.countSameEdges( l1 );
		
//printConnectivity();
		
		return edge;
	}

	// @SuppressWarnings( "unchecked" )
	public GraphicNode addNode( String id, float x, float y, float z, HashMap<String, Object> attributes )
	{
		GraphicNode n = new GraphicNode( this, id, x, y, z, attributes );

		nodes.put( id, n );
		
		graphChanged = true;
		
		return n;
	}

	public void changeEdge( String id, String attribute, Object value )
	{
		GraphicEdge edge = edges.get( id );
		
		if( edge != null )
		{
			edge.addAttribute( attribute, value );
		
			graphChanged = true;
		}
	}

	public void changeNode( String id, String attribute, Object value )
	{
		GraphicNode node = nodes.get( id );
		
		if( node != null )
		{
			node.addAttribute( attribute, value );

			graphChanged = true;
			System.err.printf( "ICI%n!!" );
		}
	}

	public void moveNode( String id, float x, float y, float z )
	{
		GraphicNode node = nodes.get( id );
		
		if( node != null )
		{
			node.x = x;
			node.y = y;
			node.z = z;
			node.addAttribute( "x", x );
			node.addAttribute( "y", y );
			node.addAttribute( "z", z );
			System.err.printf( "LA%n!!" );
			
			graphChanged = true;
		}
	}

	public Edge removeEdge( String id ) throws ElementNotFoundException
	{
		GraphicEdge edge = edges.get( id );
		
		if( edge != null )
		{
			if( connectivity.get( edge.from ) != null )
				connectivity.get( edge.from ).remove( edge );
			if( connectivity.get( edge.to ) != null )
				connectivity.get( edge.to ).remove( edge );
			edges.remove( id );
			edge.removed();
			
			graphChanged = true;
		}
		
//printConnectivity();
		
		return edge;
	}

	public Edge removeEdge( String from, String to ) throws ElementNotFoundException
	{
		GraphicNode node0 = nodes.get( from );
		GraphicNode node1 = nodes.get( to );
		
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
		GraphicNode node = nodes.get( id );
		
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
		    nodes.remove( id );
		    node.removed();
			
		    graphChanged = true;
		}
		
		return node;
	}

	/**
	 * Returns a {@link GraphicNode} who's {@code id} corresponds to the given
	 * one.
	 * 
	 * @param id The expected {@code id} of an existing BufferedNode.
	 * @return A {@link GraphicNode} who's {@code id} corresponds to the given
	 *         one.
	 */
	public GraphicNode getNode( String id )
	{
		return nodes.get( id );
	}

	public GraphicEdge getEdge( String id )
	{
		return edges.get( id );
	}

	@Override
	protected void attributeChanged( String sourceId, long timeId, String attribute, AttributeChangeEvent event, Object oldValue, Object newValue )
	{
//		System.err.printf( "attribute changed on graphic-graph : %s -> %s%n", attribute, newValue );
																																																		
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
					System.err.printf( "Error while parsing style sheet for graph '%s' :", getId() );
					if( ((String)newValue).startsWith( "url" ) )
						System.err.printf( "    %s%n", ((String)newValue) );
					System.err.printf( "    %s%n", e.getMessage() );
					// TODO handle this differently ?.
				}
			}
			else if( newValue == null )
			{
				// Remove the style.
				
				styleSheet.clear();
				checkStyles();
				graphChanged = true;
			}
			else
			{
				System.err.printf( "Stylesheet attribute not a string ? %s%n", newValue.getClass().getName() );
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
	 * @param styleSheet The style sheet name of content.
	 * @throws IOException If the loading or parsing of the style sheet failed.
	 */
	protected void loadStyleSheet( String styleSheet )
		throws IOException
	{
		if( styleSheet.startsWith( "url" ) )
		{
			int beg = styleSheet.indexOf( '(' );
			int end = styleSheet.lastIndexOf( ')' );
			
			if( beg >= 0 && end > beg )
				styleSheet = styleSheet.substring( beg+1, end );
			
			styleSheet = styleSheet.trim();
			
			if( styleSheet.startsWith( "'" ) )
			{
				beg = 0;
				end = styleSheet.lastIndexOf( '\'' );
				
				if( beg >= 0 && end > beg )
					styleSheet = styleSheet.substring( beg+1, end );
			}
			
			styleSheet = styleSheet.trim();
			
			if( styleSheet.startsWith( "\"" ) )
			{
				beg = 0;
				end = styleSheet.lastIndexOf( '"' );
				
				if( beg >= 0 && end > beg )
					styleSheet = styleSheet.substring( beg+1, end );			
			}
			/*
			if( ! styleSheet.startsWith( "http:" ) )
			{
				styleSheet = String.format( "file:%s", styleSheet );
			}
			*/
			this.styleSheet.parseFromURL( styleSheet );
			//this.styleSheet.parseFromFile( styleSheet );
		}
		else
		{
			this.styleSheet.parseFromString( styleSheet );
		}
		
		//System.err.printf( this.styleSheet.toString() );
	}

	public void styleChanged( Rule rule )
    {
		switch( rule.selector.type )
		{
			case ANY:
				checkStyles();
				break;
			case GRAPH:
				checkStyle();
				break;
			case NODE:
				checkNode( rule );
				break;
			case EDGE:
				checkEdge( rule );
				break;
			case SPRITE:
				if( spriteManager != null )
					spriteManager.checkStyles();
				break;
			default:
				throw new RuntimeException( "What's the fuck ?" );
		}
    }
	
	protected void checkStyles()
	{
		checkStyle();

		for( GraphicNode node: nodes.values() )
			node.checkStyle();
		
		for( GraphicEdge edge: edges.values() )
			edge.checkStyle();

		if( spriteManager != null )
			spriteManager.checkStyles();
	}
	
	protected void checkStyle()
	{
		style = styleSheet.getRuleFor( this );
	}
	
	protected void checkNode( Rule rule )
	{
		if( rule.selector.id != null )
		{
			GraphicNode node = getNode( rule.selector.id );
			
			if( node != null )
				node.checkStyle();
		}
		else if( rule.selector.clazz != null )
		{
			GraphicNode node = findNodeByClass( rule.selector.clazz );
			
			if( node != null )
				node.checkStyle();
		}
		else
		{
			for( GraphicNode node: nodes.values() )
				node.checkStyle();
		}
	}
	
	protected void checkEdge( Rule rule )
	{
		if( rule.selector.id != null )
		{
			GraphicEdge edge = getEdge( rule.selector.id );
			
			if( edge != null )
				edge.checkStyle();
		}
		else if( rule.selector.clazz != null )
		{
			GraphicEdge edge = findEdgeByClass( rule.selector.clazz );
			
			if( edge != null )
				edge.checkStyle();
		}		
		else
		{
			for( GraphicEdge edge: edges.values() )
				edge.checkStyle();
		}
	}
	
	protected GraphicNode findNodeByClass( String clazz )
	{
		for( GraphicNode node: nodes.values() )
		{
			String c = (String) node.getLabel( "class" );

			if( c != null && c.equals( clazz ) )
				return node;
		}
	
		return null;
	}
	
	protected GraphicEdge findEdgeByClass( String clazz )
	{
		for( GraphicEdge edge: edges.values() )
		{
			String c = (String) edge.getLabel( "class" );
			
			if( c != null && c.equals( clazz ) )
				return edge;
		}
	
		return null;
	}

	public void clear()
	{
		connectivity.clear();
		edges.clear();
		nodes.clear();
		
		graphChanged = true;
	}

// Static

	/**
	 * A set of color names mapped to real AWT Color objects.
	 */
	protected static HashMap<String, Color> colorMap;

	/**
	 * Pattern to ensure a "#FFFFFF" color is recognized.
	 */
	protected static Pattern sharpColor;

	/**
	 * Pattern to ensure a css style "rgb(1,2,3)" color is recognized.
	 */
	protected static Pattern cssColor;

	/**
	 * Pattern to ensuer a css style "rgba(1,2,3,4)" color is recognized.
	 */
	protected static Pattern cssColorA;

	/**
	 * Pattern to ensure that java.awt.Color.toString() strings are recognized as color.
	 */
	protected static Pattern awtColor;

	/**
	 * Pattern toensure an hexadecimal number is a recognized color.
	 */
	protected static Pattern hexaColor;

	static
	{
		// Prepare some pattern matchers.

		sharpColor = Pattern
				.compile( "#(\\p{XDigit}\\p{XDigit})(\\p{XDigit}\\p{XDigit})(\\p{XDigit}\\p{XDigit})((\\p{XDigit}\\p{XDigit})?)" );
		hexaColor = Pattern
				.compile( "0[xX](\\p{XDigit}\\p{XDigit})(\\p{XDigit}\\p{XDigit})(\\p{XDigit}\\p{XDigit})((\\p{XDigit}\\p{XDigit})?)" );
		cssColor = Pattern.compile( "rgb\\s*\\(\\s*([0-9]+)\\s*,\\s*([0-9]+)\\s*,\\s*([0-9]+)\\s*\\)" );
		cssColorA = Pattern.compile( "rgba\\s*\\(\\s*([0-9]+)\\s*,\\s*([0-9]+)\\s*,\\s*([0-9]+)\\s*,\\s*([0-9]+)\\s*\\)" );
		awtColor = Pattern.compile( "java.awt.Color\\[r=([0-9]+),g=([0-9]+),b=([0-9]+)\\]" );
		colorMap = new HashMap<String, Color>();

		// Load all the X11 predefined color names and their RGB definition
		// from a file stored in the graphstream.jar. This allows the DOT
		// import to correctly map color names to real Color AWT objects.
		// There are more than 800 such colors...

		URL url = GraphicGraph.class.getResource( "rgb.properties" );

		if( url == null )
			throw new RuntimeException( "corrupted graphstream.jar ? the org/miv/graphstream/ui/graphicGraph/rgb.properties file is not found" );
		
		Properties p = new Properties();

		try
		{
			p.load( url.openStream() );
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}

		for( Object o: p.keySet() )
		{
			String key = (String) o;
			String val = p.getProperty( key );
			Color  col = Color.decode( val );

			colorMap.put( key.toLowerCase(), col );
		}
	}

	/**
	 * Try to convert the given string value to a colour. It understands the 600
	 * colour names of the X11 RGB data base. It also understands colours given
	 * in the "#FFFFFF" format and the hexadecimal "0xFFFFFF" format. Finally,
	 * it understands colours given as a "rgb(1,10,100)", css-like format. If the
	 * input value is null, the result is null.
	 * @param anyValue The value to convert.
	 * @return the converted colour or null if the conversion failed.
	 */
	public static Color convertColor( Object anyValue )
	{
		if( anyValue == null )
			return null;

		if( anyValue instanceof Color )
			return (Color) anyValue;

		if( anyValue instanceof String )
		{
			Color c = null;
			String value = (String) anyValue;

			if( value.startsWith( "#" ) )
			{
				Matcher m = sharpColor.matcher( value );

				if( m.matches() )
				{
					if( value.length() == 7 )
					{
						try
						{
							c = Color.decode( value );

							return c;
						}
						catch( NumberFormatException e )
						{
							c = null;
						}
					}
					else if( value.length() == 9 )
					{
						String r = m.group( 1 );
						String g = m.group( 2 );
						String b = m.group( 3 );
						String a = m.group( 4 );

						c = new Color( Integer.parseInt( r, 16 ), Integer.parseInt( g, 16 ), Integer.parseInt( b, 16 ), Integer.parseInt(
								a, 16 ) );

						return c;
					}
				}
			}
			else if( value.startsWith( "rgb" ) )
			{
				Matcher m = cssColorA.matcher( value );

				if( m.matches() )
				{
					int r = Integer.parseInt( m.group( 1 ) );
					int g = Integer.parseInt( m.group( 2 ) );
					int b = Integer.parseInt( m.group( 3 ) );
					int a = Integer.parseInt( m.group( 4 ) );

					c = new Color( r, g, b, a );

					return c;
				}

				m = cssColor.matcher( value );

				if( m.matches() )
				{
					int r = Integer.parseInt( m.group( 1 ) );
					int g = Integer.parseInt( m.group( 2 ) );
					int b = Integer.parseInt( m.group( 3 ) );

					c = new Color( r, g, b );

					return c;
				}
			}
			else if( value.startsWith( "0x" ) || value.startsWith( "0X" ) )
			{
				Matcher m = hexaColor.matcher( value );

				if( m.matches() )
				{
					if( value.length() == 8 )
					{
						try
						{
							c = Color.decode( value );

							return c;
						}
						catch( NumberFormatException e )
						{
							c = null;
						}
					}
					else if( value.length() == 10 )
					{
						String r = m.group( 1 );
						String g = m.group( 2 );
						String b = m.group( 3 );
						String a = m.group( 4 );

						c = new Color( Integer.parseInt( r, 16 ), Integer.parseInt( g, 16 ), Integer.parseInt( b, 16 ), Integer.parseInt(
								a, 16 ) );

						return c;
					}
				}
			}
			else if( value.startsWith( "java.awt.Color[" ) )
			{
				Matcher m = awtColor.matcher( value );
				
				if( m.matches() )
				{
					int r = Integer.parseInt( m.group( 1 ) );
					int g = Integer.parseInt( m.group( 2 ) );
					int b = Integer.parseInt( m.group( 3 ) );

					c = new Color( r, g, b );

					return c;
				}
			}

			c = colorMap.get( value.toLowerCase() );

			return c;
		}

		return null;
	}

	/**
	 * Check if the given value is an instance of CharSequence (String is) and
	 * return it as a string. Else return null. If the input value is null, the
	 * return value is null. If the value returned is larger than 128 characters,
	 * this method cuts it to 128 characters.
	 * TODO: allow to set the max length of these strings.
	 * @param value The value to convert.
	 * @return The corresponding string, or null.
	 */
	public static String convertLabel( Object value )
	{
		String label = null;

		if( value != null )
		{
			if( value instanceof CharSequence )
				label = ((CharSequence)value).toString();
			else label = value.toString();

			if( label.length() > 128 )
				label = String.format( "%s...", label.substring( 0, 128 ) );
		}
		
		return label;
	}

	/**
	 * Try to convert an arbitrary value to a float. If it is a descendant of
	 * Number, the float value is returned. If it is a string, a conversion is
	 * tried to change it into a number and if successful, this number is
	 * returned as a float. Else, the -1 value is returned as no width can be
	 * negative to indicate the conversion failed. If the input is null, the
	 * return value is -1.
	 * @param value The input to convert.
	 * @return The value or -1 if the conversion failed.
	 */
	public static float convertWidth( Object value )
	{
		if( value instanceof CharSequence )
		{
			try
			{
				float val = Float.parseFloat( ( (CharSequence) value ).toString() );

				return val;
			}
			catch( NumberFormatException e )
			{
				return -1;
			}
		}
		else if( value instanceof Number )
		{
			return ( (Number) value ).floatValue();
		}

		return -1;
	}

	/**
	 * Try to convert an arbitrary value to a EdgeStyle. If the value is a
	 * descendant of CharSequence, it is used and parsed to see if it maps to
	 * one of the possible values.
	 * @param value The value to convert.
	 * @return The converted edge style or null if the value does not identifies
	 *         an edge style.
	 */
	public static Style.EdgeStyle convertEdgeStyle( Object value )
	{
		if( value instanceof CharSequence )
		{
			String s = ( (CharSequence) value ).toString().toLowerCase();

			if( s.equals( "dots" ) )
			{
				return Style.EdgeStyle.DOTS;
			}
			else if( s.equals( "dashes" ) )
			{
				return Style.EdgeStyle.DASHES;
			}
			else
			{
				return Style.EdgeStyle.PLAIN;
			}
		}

		return null;
	}
	
	protected void printConnectivity()
	{
		Iterator<GraphicNode> keys = connectivity.keySet().iterator();
		
		System.err.printf( "GG connectivity:%n" );
		
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
	
	public Edge addEdge( String id, String from, String to ) throws IdAlreadyInUseException,
            ElementNotFoundException
    {
		return addEdge( id, from, to, false, null );
    }

	public Edge addEdge( String id, String from, String to, boolean directed )
            throws IdAlreadyInUseException, ElementNotFoundException
    {
		return addEdge( id, from, to, directed, null );
    }

	public Node addNode( String id ) throws IdAlreadyInUseException
    {
		return addNode( id, 0, 0, 0, null );
    }

	public void addSink( Sink listener )
    {
		throw new RuntimeException( "not implemented !" );
    }
	
	public void addAttributeSink( AttributeSink listener )
	{
		throw new RuntimeException( "not implemented !" );		
	}
	
	public void addElementSink( ElementSink listener )
	{
		throw new RuntimeException( "not implemented !" );		
	}

	public void clearListeners()
    {
    }

	public GraphViewerRemote display()
    {
		throw new RuntimeException( "not implemented !" );
    }

	public GraphViewerRemote display( boolean autoLayout )
    {
		throw new RuntimeException( "not implemented !" );
    }
	
	public org.graphstream.ui2.swingViewer.Viewer display2() { throw new RuntimeException( "not implemented !" ); }
	public org.graphstream.ui2.swingViewer.Viewer display2( boolean autoLayout ) { throw new RuntimeException( "not implemented !" ); }

	public EdgeFactory edgeFactory()
    {
		return null;
    }
	
	public void setEdgeFactory( EdgeFactory ef )
	{
	}

	public int getEdgeCount()
    {
	    return edges.size();
    }

	public Iterator<? extends Edge> getEdgeIterator()
    {
	    return edges.values().iterator();
    }

	public Iterable<? extends Edge> edgeSet()
    {
	    return edges.values();
    }

	public Iterable<AttributeSink> attributeSinks()
    {
	    return null;
    }
	
	public Iterable<ElementSink> elementSinks()
    {
	    return null;
    }

	public int getNodeCount()
    {
	    return nodes.size();
    }

	public Iterator<? extends Node> getNodeIterator()
    {
	    return nodes.values().iterator();
    }
	
	public Iterator<Node> iterator()
	{
		return null;
	}
	
	public Iterable<? extends Node> nodeSet()
    {
		return nodes.values();
    }

	public boolean isAutoCreationEnabled()
    {
	    return false;
    }

	public boolean isStrict()
    {
	    return false;
    }

	public NodeFactory nodeFactory()
    {
	    return null;
    }
	
	public void setNodeFactory( NodeFactory nf )
	{
	}

	public void read( String filename ) throws IOException, GraphParseException, ElementNotFoundException
    {
		throw new RuntimeException( "not implemented !" );
    }

	public void read( FileSource input, String filename ) throws IOException, GraphParseException
    {
		throw new RuntimeException( "not implemented !" );
    }

	public int readPositionFile( String posFileName ) throws IOException
    {
		throw new RuntimeException( "not implemented !" );
    }

	public void removeSink( Sink listener )
    {
    }
	
	public void removeAttributeSink( AttributeSink listener )
	{
	}
	
	public void removeElementSink( ElementSink listener )
	{
		
	}

	public void setAutoCreate( boolean on )
    {
		throw new RuntimeException( "not implemented !" );
    }

	public void setStrict( boolean on )
    {
		throw new RuntimeException( "not implemented !" );
    }

	public void write( String filename ) throws IOException
    {
		throw new RuntimeException( "not implemented !" );
    }

	public void write( FileSink output, String filename ) throws IOException
    {
		throw new RuntimeException( "not implemented !" );
    }

	public void stepBegins( double time )
	{
		step = time;
	}

// Output

	public void edgeAdded( String graphId, long timeId, String edgeId, String fromNodeId, String toNodeId,
            boolean directed )
    {
		listeners.edgeAdded(graphId, timeId, edgeId, fromNodeId, toNodeId, directed);
    }

	public void edgeRemoved( String graphId, long timeId, String edgeId )
    {
		listeners.edgeRemoved(graphId, timeId, edgeId);
    }

	public void graphCleared()
    {
		clear();
    }

	public void nodeAdded( String graphId, long timeId, String nodeId )
    {
		listeners.nodeAdded(graphId, timeId, nodeId);
    }

	public void nodeRemoved( String graphId, long timeId, String nodeId )
    {
		listeners.nodeRemoved(graphId, timeId, nodeId);
    }

	public void stepBegins( String graphId, long timeId, double step )
    {
		listeners.stepBegins(graphId, timeId, step);
    }

	public void graphCleared( String graphId, long timeId )
    {
		listeners.graphCleared(graphId, timeId);
    }

	public void edgeAttributeAdded( String graphId, long timeId, String edgeId, String attribute, Object value )
    {
		listeners.edgeAttributeAdded(graphId, timeId, edgeId, attribute, value);
    }

	public void edgeAttributeChanged( String graphId, long timeId, String edgeId, String attribute,
            Object oldValue, Object newValue )
    {
		listeners.edgeAttributeChanged(graphId, timeId, edgeId, attribute, oldValue, newValue);
    }

	public void edgeAttributeRemoved( String graphId, long timeId, String edgeId, String attribute )
    {
		listeners.edgeAttributeRemoved(graphId, timeId, edgeId, attribute);
    }

	public void graphAttributeAdded( String graphId, long timeId, String attribute, Object value )
    {
		listeners.graphAttributeAdded(graphId, timeId, attribute, value);
    }

	public void graphAttributeChanged( String graphId, long timeId, String attribute, Object oldValue,
            Object newValue )
    {
		listeners.graphAttributeChanged(graphId, timeId, attribute, oldValue, newValue);
    }

	public void graphAttributeRemoved( String graphId, long timeId, String attribute )
    {
		listeners.graphAttributeRemoved(graphId, timeId, attribute);
    }

	public void nodeAttributeAdded( String graphId, long timeId, String nodeId, String attribute, Object value )
    {
		listeners.nodeAttributeAdded(graphId, timeId, nodeId, attribute, value);
    }

	public void nodeAttributeChanged( String graphId, long timeId, String nodeId, String attribute,
            Object oldValue, Object newValue )
    {
		listeners.nodeAttributeChanged(graphId, timeId, nodeId, attribute, oldValue, newValue);
    }

	public void nodeAttributeRemoved( String graphId, long timeId, String nodeId, String attribute )
    {
		listeners.nodeAttributeRemoved(graphId, timeId, nodeId, attribute);
    }
	
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
    				edge.addAttribute( attribute, value );
    		}
    	}

    	public void edgeAttributeChanged(String sourceId, long timeId,
    			String edgeId, String attribute, Object oldValue,
    			Object newValue) {
    		if( sinkTime.isNewEvent(sourceId, timeId) )
    		{
    			Edge edge = getEdge( edgeId );
    			
    			if( edge != null )
    				edge.changeAttribute( attribute, newValue );
    		}
    	}

    	public void edgeAttributeRemoved(String sourceId, long timeId,
    			String edgeId, String attribute) {
    		if( sinkTime.isNewEvent(sourceId, timeId) )
    		{
    			Edge edge = getEdge( edgeId );
    			
    			if( edge != null )
    				edge.removeAttribute( attribute );
    		}
    	}

    	public void graphAttributeAdded(String sourceId, long timeId,
    			String attribute, Object value) {
    		if( sinkTime.isNewEvent(sourceId, timeId) )
    		{
    			addAttribute( attribute, value );
    		}
    	}

    	public void graphAttributeChanged(String sourceId, long timeId,
    			String attribute, Object oldValue, Object newValue) {
    		if( sinkTime.isNewEvent(sourceId, timeId) )
    		{
    			changeAttribute( attribute, newValue );
    		}
    	}

    	public void graphAttributeRemoved(String sourceId, long timeId,
    			String attribute) {
    		if( sinkTime.isNewEvent(sourceId, timeId) )
    		{
    			removeAttribute( attribute );
    		}
    	}

    	public void nodeAttributeAdded(String sourceId, long timeId,
    			String nodeId, String attribute, Object value) {
    		if( sinkTime.isNewEvent(sourceId, timeId) )
    		{
    			Node node = getNode( nodeId );
    			
    			if( node != null )
    				node.addAttribute( attribute, value );
    		}
    	}

    	public void nodeAttributeChanged(String sourceId, long timeId,
    			String nodeId, String attribute, Object oldValue,
    			Object newValue) {
    		if( sinkTime.isNewEvent(sourceId, timeId) )
    		{
    			Node node = getNode( nodeId );
    			
    			if( node != null )
    				node.changeAttribute( attribute, newValue );
    		}
    	}

    	public void nodeAttributeRemoved(String sourceId, long timeId,
    			String nodeId, String attribute) {
    		if( sinkTime.isNewEvent(sourceId, timeId) )
    		{
    			Node node = getNode( nodeId );
    			
    			if( node != null )
    				node.removeAttribute( attribute );
    		}
    	}

    	public void edgeAdded(String sourceId, long timeId, String edgeId,
    			String fromNodeId, String toNodeId, boolean directed) {
    		if( sinkTime.isNewEvent(sourceId, timeId) )
    		{
    			addEdge( edgeId, fromNodeId, toNodeId, directed );
    		}
    	}

    	public void edgeRemoved(String sourceId, long timeId, String edgeId) {
    		if( sinkTime.isNewEvent(sourceId, timeId) )
    		{
    			removeEdge( edgeId );
    		}
    	}

    	public void graphCleared(String sourceId, long timeId) {
    		if( sinkTime.isNewEvent(sourceId, timeId) )
    		{
    			clear();
    		}
    	}

    	public void nodeAdded(String sourceId, long timeId, String nodeId) {
    		if( sinkTime.isNewEvent(sourceId, timeId) )
    		{
    			System.out.printf( "graphicnode added\n");
    			addNode( nodeId );
    		}
    		else System.err.print("not synchro (graphic)\n");
    	}

    	public void nodeRemoved(String sourceId, long timeId, String nodeId) {
    		if( sinkTime.isNewEvent(sourceId, timeId) )
    		{
    			removeNode(nodeId);
    		}
    	}

    	public void stepBegins(String sourceId, long timeId, double step) {
    		if( sinkTime.isNewEvent(sourceId, timeId) )
    		{
    			GraphicGraph.this.stepBegins( step );
    		}
    	}
    }

	public void clearAttributeSinks()
	{
	}

	public void clearElementSinks()
	{		
	}

	public void clearSinks()
	{
	}
}