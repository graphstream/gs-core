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

package org.miv.graphstream.algorithm.layout2.springbox.test;

import java.io.File;

import javax.swing.JFileChooser;

import org.miv.graphstream.algorithm.layout2.LayoutRunner.LayoutRemote;
import org.miv.graphstream.graph.Edge;
import org.miv.graphstream.graph.Element;
import org.miv.graphstream.graph.Graph;
import org.miv.graphstream.graph.GraphListener;
import org.miv.graphstream.graph.Node;
import org.miv.graphstream.graph.implementations.DefaultGraph;
import org.miv.graphstream.io.GraphReader;
import org.miv.graphstream.io.GraphReaderFactory;
import org.miv.graphstream.io.GraphReaderListenerHelper;
import org.miv.graphstream.ui.GraphViewerRemote;

public class ReadLargeGraph implements GraphListener
{
	protected Graph graph;
	
	protected GraphViewerRemote viewer;
	
	protected LayoutRemote layout;
	
	protected boolean loop = true;
	
	protected int sleepTime = 100;
	
	public static void main( String args[] )
	{
		new ReadLargeGraph( args );
	}
	
	public ReadLargeGraph( String args[] )
	{
		if( args.length > 0 )
		{
			graph   = new DefaultGraph( "Springs...", false, true );
			viewer  = graph.display();
			layout  = viewer.getLayoutRemote();
		
			viewer.setQuality( 0 );
			layout.setNoSlowDown( true );
			layout.setPriority( 5 );
			graph.setAttribute( "stylesheet", styleSheet );
			
			steps( getFileToRead( args ) );
		}
	}
	
	protected void steps( String fileName )
	{
		try
		{
			graph.addGraphListener( this );
			
			GraphReader reader = GraphReaderFactory.readerFor( fileName );
			GraphReaderListenerHelper helper = new GraphReaderListenerHelper( graph );
			
			reader.addGraphReaderListener( helper );
			reader.begin( fileName );
			
			loop = true;
			boolean reading = true;
			
			while( loop )
			{
				if( reading )
				{
					int i = 0;
					while( reading && i < 3 )
					{
						reading = reader.nextEvents();
						i++;
						if( ! reading )
						{
							reader.end();
							System.err.printf( "Reading finished...%n" );
						}
					}
					
					sleep( sleepTime );
				}
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	protected void sleep( long ms )
	{
		try { Thread.sleep( ms ); } catch( InterruptedException e ) {}
	}
	
	protected String getFileToRead( String args[] )
	{
		String initialDir = null;
		
		if( args.length > 0 )
		{
			if( args[0].endsWith( "/" ) || args[0].endsWith( "\\" ) )
			{
				initialDir = args[0];
			}
			else 
			{
				return args[0];
			}
		}
		
		JFileChooser fileChooser = new JFileChooser();
			
		if( initialDir != null )
			fileChooser.setCurrentDirectory( new File( initialDir ) );
		
		if( fileChooser.showOpenDialog( null ) == JFileChooser.APPROVE_OPTION )
		{
			File file = fileChooser.getSelectedFile();
			return file.getAbsolutePath();
		}
		else
		{
			System.err.printf( "Please choose a file or pass it as argument." ) ;
			System.exit( 1 );
			return null;
		}
	}

	protected static final String styleSheet =
		"node { width:4; }" +
		"edge { color:#A0A0A0; }" +
		"sprite { width:2; sprite-shape:arrow; z-index:-1; arrow-width:3px; arrow-length:20px; }";

	public void afterEdgeAdd( Graph graph, Edge edge )
    {
		if( sleepTime < 300 )
			System.err.printf( "slowing down !!%n" );
		
		sleepTime = 300;
    }

	public void afterNodeAdd( Graph graph, Node node )
    {
    }

	public void attributeChanged( Element element, String attribute, Object oldValue,
            Object newValue )
    {
    }

	public void beforeEdgeRemove( Graph graph, Edge edge )
    {
    }

	public void beforeGraphClear( Graph graph )
    {
    }

	public void beforeNodeRemove( Graph graph, Node node )
    {
    }

	public void stepBegins( Graph graph, double time )
    {
    }
}