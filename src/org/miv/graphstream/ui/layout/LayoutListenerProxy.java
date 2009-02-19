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

import org.miv.mbox.*;
import org.miv.graphstream.graph.*;
import java.util.*;

/**
 * Implementation of the layout listener that can be executed in
 * another thread than the layout one.
 * 
 * <p>
 * Use this class when the layout runs in its own thread and you need
 * to receive its events.
 * </p>
 * 
 * <p>
 * This class must be called regularly to update node move events. You do this
 * by calling the {@link #checkEvents()} method. This call will trigger the
 * consultation of all waiting events.
 * </p>
 * 
 * <p>
 * It can
 * either forward the listener events to another listener or directly
 * modify a graph given as argument. In this case, when a node move event
 * is generated the "x", "y", and "z" attributes of the node are changed
 * accordingly. When an edge changes, its "curve" attribute is changed.
 * </p>
 *
 * @author Antoine Dutot
 * @since 2007
 */
public class LayoutListenerProxy implements LayoutListener, MBoxListener
{
// Attributes
	
	/**
	 * The message box used to exchange messages between the two threads.
	 */
	protected MBoxStandalone events;
	
	/**
	 * The layout listeners in another thread than the layout one.
	 */
	protected ArrayList<LayoutListener> listeners = new ArrayList<LayoutListener>();
	
	/**
	 * The graph to modify.
	 */
	protected Graph graph;
	
	/**
	 * The current layout process completion. A value of one means "fully completed", a value of
	 * zero means "not started". 
	 */
	protected float completion;
	
// Constructors
	
	/**
	 * Forwards the events from the layout thread to a listener in another
	 * thread.
	 * @param listener The initial listener where events will be forwarded.
	 */
	public LayoutListenerProxy( LayoutListener listener )
	{
		this.events = new MBoxStandalone( this );

		listeners.add( listener );
	}
	
	/**
	 * Forwards the events directly in the given graph.
	 * @param graph The graph to modify according to the events.
	 */
	public LayoutListenerProxy( Graph graph )
	{
		this.events = new MBoxStandalone( this );
		this.graph  = graph;
	}
	
// Access
	
	/**
	 * The layout process completion. This is a value between 0 and 1, 1 meaning fully completed.
	 */
	public float getCompletion()
	{
		return completion;
	}
	
// Commands

	/**
	 * This method must be called regularly to check if the spring box sent
	 * events. If some event occurred, the listeners will be called (forward of
	 * the messages sent by the spring box) and the graph given as argument
	 * will be modified (if any).
	 */
	public void checkEvents()
	{
		events.processMessages();
	}
	
	public void nodeMoved( String id, float x, float y, float z )
	{
		events.post( "LLH", "NM", id, x, y, z );
	}

	public void nodeInfos( String id, float dx, float dy, float dz )
    {
		events.post( "LLH", "NI", id, dx, dy, dz );
    }

	public void nodesMoved( Map<String, float[]> nodes )
	{
		events.post( "LLH", "NMs", nodes );
	}

	public void edgeChanged( String id, float[] points )
	{
		events.post( "LLH", "EC", id, points );
	}

	public void edgesChanged( Map<String, float[]> edges )
	{
		events.post( "LLH", "ECs", edges );
	}

	public void stepCompletion( float percent )
	{
		events.post( "LLH", "SC", percent );
	}
	
	@SuppressWarnings("unchecked")
	public void processMessage( String from, Object[] data )
	{
		if( data[0].equals( "NM" ) )
		{
			String id = (String) data[1];
			float  x  = (Float)  data[2];
			float  y  = (Float)  data[3];
			float  z  = (Float)  data[4];
			
			for( LayoutListener listener: listeners )
				listener.nodeMoved( id, x, y, z );
		
			if( graph != null )
			{
				Node node = graph.getNode( id );
				
				if( node != null )
				{
					node.addAttribute( "x", x );
					node.addAttribute( "y", y );
					node.addAttribute( "z", z );
				}
			}
		}
		else if( data[0].equals( "NMs" ) )
		{
			Map<String,float[]> nodes = (Map<String,float[]>) data[1];
			
			for( LayoutListener listener: listeners )
				listener.nodesMoved( nodes );
			
			if( graph != null )
			{
				for( String key: nodes.keySet() )
				{
					Node node = graph.getNode( key );
					
					if( node != null )
					{
						float[] pos = nodes.get( key );
						
						node.addAttribute( "x", pos[0] );
						node.addAttribute( "y", pos[1] );
						
						if( pos.length > 2 )
							node.addAttribute( "z", pos[2] );
					}
				}
			}
		}
		else if( data[0].equals( "NI" ) )
		{
			String id = (String) data[1];
			float  dx = (Float)  data[2];
			float  dy = (Float)  data[3];
			float  dz = (Float)  data[4];
			
			for( LayoutListener listener: listeners )
				listener.nodeInfos( id, dx, dy, dz );
		}
		else if( data[0].equals( "EC" ) )
		{
			String  id  = (String)  data[1];
			float[] pts = (float[]) data[2];
			
			for( LayoutListener listener: listeners )
				listener.edgeChanged( id, pts );
			
			if( graph != null )
			{
				Edge edge = graph.getEdge( id );
				
				if( edge != null )
				{
					edge.addAttribute( "curve", pts );
				}
			}
		}
		else if( data[0].equals( "ECs" ) )
		{
			Map<String,float[]> edges = (Map<String,float[]>) data[1];
			
			for( LayoutListener listener: listeners )
				listener.edgesChanged( edges );
			
			if( graph != null )
			{
				for( String key: edges.keySet() )
				{
					Edge edge = graph.getEdge( key );
					
					if( edge != null )
					{
						float[] pts = edges.get( key );
						
						edge.addAttribute( "curve", pts );
					}
				}
			}
		}
		else if( data[0].equals( "SC" ) )
		{
			completion = (Float) data[1];
			
			for( LayoutListener listener: listeners )
				listener.stepCompletion( completion );			
		}
	}
}