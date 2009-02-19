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

package org.miv.graphstream.algorithm.layout2;

import java.util.Iterator;
import java.util.Map;

import org.miv.graphstream.graph.Edge;
import org.miv.graphstream.graph.Element;
import org.miv.graphstream.graph.Graph;
import org.miv.graphstream.graph.GraphListener;
import org.miv.graphstream.graph.Node;

/**
 * The layout algorithm let you run a layout algorithm of your choice on a graph that is modified
 * with "xyz" attributes (or an attribute of your choice).
 * 
 * This is a simple class that listens at a graph and redirects these information to a layout
 * implementation. It also listen at the layout and modifies the graph accordingly by adding
 * "xyz" attributes on each node moved by the layout.
 * 
 * By default the {@link org.miv.graphstream.algorithm.layout2.Layout} class is low-level and more
 * difficult to use because you must declare
 * nodes and edges using special methods. Also you must implement a layout listener to receive node
 * move events.
 * 
 * The LayoutAlgorithm class aims at simplifying this by providing an automatic way to listen at
 * a graph (the graph may already contain nodes and edges before the algorithm is used) and to
 * modify it according to a layout implementation.
 * 
 * To use it you must call the {@link #begin(Graph, Layout)} with a graph (that can already be
 * constructed) and a layout of your choice. Then each time you call the {@link #step()} method
 * the layout computes new positions for the nodes. As layouts are often iterative methods you
 * may have to call {@link #step()} many times until the layout stabilises. Alternatively, you can
 * use the {@link #stepUntilStabilized(int)} method that runs several steps until the layout is
 * stable (or a given maximum number of steps is reached). Finally, when finished with the layout,
 * you must call {@link #end()}. This operation is important so that the algorithm unregisters
 * the various listeners in the graph and layout implementation.  
 * 
 * @author Antoine Dutot
 */
public class LayoutAlgorithm implements GraphListener, LayoutListener
{
// Attributes
	
	/**
	 * The graph.
	 */
	protected Graph graph;
	
	/**
	 * The layout algorithm.
	 */
	protected Layout layout;
	
	/**
	 * Name of the attribute used to store the node positions.
	 */
	protected String positionAttribute = "xyz";
	
// Constructors
	
	/**
	 * New layout algorithm.
	 */
	public LayoutAlgorithm()
	{
	}
	
	/**
	 * New layout algorithm.
	 * @param positionAttributeName The name to use to store positions in the graph nodes. The 
	 * positions are arrays of three values (x,y,z).
	 */
	public LayoutAlgorithm( String positionAttributeName )
	{
		positionAttribute = positionAttributeName;
	}
	
// Access
	
	/**
	 * The current layout stabilisation.
	 */
	public double getLayoutStabilization()
	{
		if( layout != null )
			return layout.getStabilization();
		
		return 0;
	}
	
// Commands

	/**
	 * Change the name used to store the (x,y,z) positions of nodes.
	 * @param positionAttributeName The new name to use.
	 */
	public void setXYZAttributeName( String positionAttributeName )
	{
		this.positionAttribute = positionAttributeName;
	}
	
	/**
	 * Register listeners in the graph and layout and prepare to modify the graph with "xy"
	 * attributes.
	 */
	public void begin( Graph graph, Layout layout )
	{
		if( this.graph != null || this.layout != null )
			throw new RuntimeException( "cannot call begin() twice without calling end() first" );
		
		this.graph  = graph;
		this.layout = layout;
		
		graph.addGraphListener( this );
		layout.addListener( this );
		replayGraph();
	}
	
	/**
	 * Do one layout step. Many step may be needed until the layout is acceptable (most layouts
	 * are iterative algorithms).
	 * @see #stepUntilStabilized(int)
	 */
	public void step()
	{
		if( layout != null )
			layout.compute();
	}
	
	/**
	 * Run several layout steps until the layout is stabilised. As this may take times a step limit
	 * can be given.
	 * @param stepLimits The maximum number of steps to do, stop if this number is reached an
	 * the layout is still not stabilised. If the value is less than 0 there is no limit.
	 */
	public void stepUntilStabilized( int stepLimits )
	{
		if( layout == null )
			return;
		
		int steps = 0;
		
		if( stepLimits <= 0 )
			stepLimits = Integer.MAX_VALUE;
		
		while( steps < stepLimits && layout.getStabilization() != 1 )
		{
			step();
			steps++;
		}
	}
	
	/**
	 * Remove the listeners from the graph and layouts.
	 */
	public void end()
	{
		if( graph != null && layout != null )
		{
			graph.removeGraphListener( this );
			layout.removeListener( this );
		
			graph  = null;
			layout = null;
		}
	}
	
// Graph listener

	public void afterEdgeAdd( Graph graph, Edge edge )
    {
	    layout.afterEdgeAdd( graph, edge );
    }

	public void afterNodeAdd( Graph graph, Node node )
    {
		layout.afterNodeAdd( graph, node );
    }

	public void attributeChanged( Element element, String attribute, Object oldValue,
            Object newValue )
    {
		layout.attributeChanged( element, attribute, oldValue, newValue );
    }

	public void beforeEdgeRemove( Graph graph, Edge edge )
    {
		layout.beforeEdgeRemove( graph, edge );
    }

	public void beforeNodeRemove( Graph graph, Node node )
    {
	    layout.beforeNodeRemove( graph, node );	    
    }

	public void beforeGraphClear( Graph graph )
    {
		layout.clear();
    }

	public void stepBegins( Graph graph, double time )
    {
    }

// Layout listener
	
	public void nodeInfos( String id, float dx, float dy, float dz )
    {
    }

	public void nodeMoved( String id, float x, float y, float z )
    {
	    Node node = graph.getNode( id );
	    
	    node.setAttribute( positionAttribute, x, y, z );
    }

	public void nodesMoved( Map<String,float[]> nodes )
    {
    }

	public void stepCompletion( float percent )
    {
    }

	public void edgeChanged( String id, float[] points )
    {
    }

	public void edgesChanged( Map<String,float[]> edges )
    {
    }
	
// Utility
	
	protected void replayGraph()
	{
		replayAttributesOf( graph );
		
		Iterator<?extends Node> nodes = graph.getNodeIterator();
		
		while( nodes.hasNext() )
		{
			Node node = nodes.next();
			afterNodeAdd( graph, node );
			replayAttributesOf( node );
		}
		
		Iterator<?extends Edge> edges = graph.getEdgeIterator();
		
		while( edges.hasNext() )
		{
			Edge edge = edges.next();
			afterEdgeAdd( graph, edge );
			replayAttributesOf( edge );
		}
	}
	
	protected void replayAttributesOf( Element element )
	{
		Iterator<?extends String> keys = element.getAttributeKeyIterator();
		
		while( keys.hasNext() )
		{
			String key   = keys.next();
			Object value = element.getAttribute( key );
			
			attributeChanged( element, key, null, value );
		}
	}
}