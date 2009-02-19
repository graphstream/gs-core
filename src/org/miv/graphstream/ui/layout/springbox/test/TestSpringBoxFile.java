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

import java.awt.Color;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.swing.JFileChooser;

import org.miv.graphstream.algorithm.layout2.Layout;
import org.miv.graphstream.algorithm.layout2.LayoutListener;
import org.miv.graphstream.algorithm.layout2.springbox.*;
import org.miv.graphstream.graph.Graph;
import org.miv.graphstream.graph.Node;
import org.miv.graphstream.graph.implementations.DefaultGraph;
import org.miv.graphstream.io.GraphReader;
import org.miv.graphstream.io.GraphReaderFactory;
import org.miv.graphstream.io.GraphReaderListenerHelper;
import org.miv.graphstream.ui.GraphViewerListener;
import org.miv.graphstream.ui.GraphViewerRemote;
import org.miv.graphstream.ui.RemoteSprite;
import org.miv.util.Environment;

/**
 * Test the elastic box with various graphs and graph generators.
 * 
 * @author Antoine Dutot
 */
public class TestSpringBoxFile implements LayoutListener, GraphViewerListener
{
	protected Graph graph;
	protected Random random;
	protected Layout layout;
	protected GraphViewerRemote viewer;
	protected boolean loop;
	protected HashMap<String,MySprite> sprites;
	protected float maxForce = 0;
	
	public static void main( String args[] )
	{
		new TestSpringBoxFile( args );
	}
	
	public TestSpringBoxFile()
	{
		
	}
	
	public TestSpringBoxFile( String args[] )
	{
		init( args, 1 );
		testReader( getFileToRead( args ) );
	}
	
	protected void init( String args[], int randomSeed )
	{
		Environment.getGlobalEnvironment().setParameter( "SwingGraphRenderer.interpolateBounds", "1" );
		
		random  = new Random( randomSeed );
		graph   = new DefaultGraph( "Springs...", false, true );
		layout  = new SpringBox();// false, 40, 8, false, 1 );
		viewer  = graph.display( false );
		sprites = new HashMap<String,MySprite>();

		viewer.addViewerListener( this );
		layout.setSendNodeInfos( true );
		//layout.setQuality( 1 );
		layout.addListener( this );
		graph.addGraphListener( layout );
		viewer.setQuality( 0 );
		graph.setAttribute( "stylesheet", styleSheet );
	}
		
	protected void testReader( String fileName )
	{
		try
		{
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
				}
				
				maxForce = 0;
				viewer.pumpEvents();
				layout.compute();
				colorSprites();
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
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
	
	protected void sleep( long ms )
	{
		try { Thread.sleep( ms ); } catch( InterruptedException e ) {}
	}

// Layout listener
	
	public void edgeChanged( String id, float[] points )
    {
    }

	public void edgesChanged( Map<String,float[]> edges )
    {
    }

	public void nodeInfos( String id, float dx, float dy, float dz )
    {
		Node node = graph.getNode( id );
		
		if( node != null )
		{
			MySprite sprite = sprites.get( id );
			
			if( sprite == null )
			{
				sprite = new MySprite( id, viewer );
				sprite.attachToNode( id );
				sprites.put( id, sprite );
			}
			
			float len = (float)Math.sqrt(dx*dx+dy*dy);
			
			if( len > 0.0000001f )
			{
				float rot = angleFromCartesian( dx, dy );
			
				sprite.position( len, 0, rot );
			
				node.setAttribute( "color", Color.BLACK );
			}
			else
			{
				viewer.removeSprite( sprite.getId() );
				sprites.remove( id );
				node.setAttribute( "color", Color.GRAY );
			}

			if( len > maxForce )
				maxForce = len;
		}
    }

	public void nodeMoved( String id, float x, float y, float z )
    {
		Node node = graph.getNode( id );
		
		if( node != null )
			node.setAttribute( "xy", x, y );
    }

	public void nodesMoved( Map<String,float[]> nodes )
    {
    }

	public void stepCompletion( float percent )
    {
    }
	
	protected float angleFromCartesian( float dx, float dy )
	{
		if( dx > 0 && dy >= 0 )
		{
			return (float) Math.atan( dy/dx );
		}
		else if( dx > 0 && dy < 0 )
		{
			return (float) ( Math.atan( dy/dx ) + Math.PI + Math.PI );
		}
		else if( dx < 0 )
		{
			return (float) ( Math.atan( dy/dx ) + Math.PI );
		}
		else if( dx == 0 && dy > 0 )
		{
			return (float)( Math.PI / 2f );
		}
		else
		{
			return (float) ((3*Math.PI)/2);
		}
	}
	
	protected void colorSprites()
	{
		for( MySprite sprite: sprites.values() )
		{
			float color = 0;
			
			if( maxForce > 0 )
				color = sprite.len / maxForce;
		
			sprite.setAttribute( "color", new Color( 1, 0.8f-color*0.8f, 0.8f-color*0.8f ) );
		}
	}
	
	protected static final String styleSheet =
		"node { width:4; }" +
		"edge { color:#A0A0A0; }" +
		"sprite { width:2; sprite-shape:arrow; z-index:-1; arrow-width:3px; arrow-length:20px; }";
	
	public static class MySprite extends RemoteSprite
	{
		protected float len;
		
		public MySprite( String id, GraphViewerRemote remote )
		{
			super( id, remote );
		}
		
		@Override
		public void position( float x, float y, float z )
		{
			len = x;
			super.position( x, y, z );
		}
	}

	public void backgroundClicked( float x, float y, int button, boolean clicked )
    {
		System.err.printf( "(%f,%f)%n", x, y );
    }

	public void nodeSelected( String id, boolean selected )
    {
		if( selected )
			System.err.printf( "NODE %s selected%n", id );
    }

	public void spriteMoved( String id, float x, float y, float z )
    {
    }

	public void spriteSelected( String id, boolean selected )
    {
    }
}
