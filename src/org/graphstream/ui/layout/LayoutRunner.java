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
 * 
 * Copyright 2006 - 2009
 * 	Julien Baudry
 * 	Antoine Dutot
 * 	Yoann Pigné
 * 	Guilhelm Savin
 */

package org.graphstream.ui.layout;

import org.graphstream.graph.Graph;
import org.graphstream.stream.ProxyPipe;
import org.graphstream.stream.Source;
import org.graphstream.stream.thread.ThreadProxyPipe;

/**
 * Allows to run a layout in a distinct thread. 
 */
public class LayoutRunner extends Thread
{
	/**
	 * The layout algorithm.
	 */
	protected Layout layout = null;
	
	/**
	 * The proxy on the source of graph events.
	 */
	protected ThreadProxyPipe pumpPipe = null;
	
	/**
	 * The meaning of life.
	 */
	protected boolean loop = true;

	/**
	 * New layout runner that listen at the given source and compute a layout on its graph structure
	 * in a distinct thread. 
	 * @param source The source of graph events.
	 * @param layout The layout algorithm to use.
	 */
	public LayoutRunner( Source source, Layout layout )
	{
		this( source, layout, true );
	}
	
	/**
	 * New layout runner that listen at the given source and compute a layout on its graph structure
	 * in a distinct thread. 
	 * @param source The source of graph events.
	 * @param layout The layout algorithm to use.
	 * @param start Start the layout thread immediately ? Else the start() method must be called later.
	 */
	public LayoutRunner( Source source, Layout layout, boolean start )
	{
		this.layout   = layout;
		this.pumpPipe = new ThreadProxyPipe( source );
		this.pumpPipe.addSink( layout );
		
		if( start )
			start();
	}
	
	public LayoutRunner( Graph graph, Layout layout, boolean start, boolean replay ) 
	{
		this.layout   = layout;
		this.pumpPipe = new ThreadProxyPipe( graph, true );
		this.pumpPipe.addSink( layout );
		
		if( start )
			start();
	}
	
	/**
	 * Pipe out whose input is connected to the layout algorithm. You can safely connect as a sink
	 * to it to receive events of the layout from a distinct thread. 
	 */
	public ProxyPipe newLayoutPipe()
	{
		return new ThreadProxyPipe( layout );
	}
	
	@Override
	public void run()
	{
		String layoutName = layout.getLayoutAlgorithmName();
		
		while( loop )
		{
			pumpPipe.pump();
			layout.compute();
			nap( 10 );
		}
		
		System.out.printf( "Layout '%s' process stopped.%n", layoutName );
		System.out.flush();
	}
	
	public void release()
	{
		pumpPipe.unregisterFromSource();
		pumpPipe.removeSink( layout );
		pumpPipe = null;
		layout = null;
		loop = false;
	}
	
	protected void nap( long ms )
	{
		try { Thread.sleep( ms ); } catch( Exception e ) {}
	}
}
