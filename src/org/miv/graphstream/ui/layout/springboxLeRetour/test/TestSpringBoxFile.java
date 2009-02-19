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

package org.miv.graphstream.algorithm.layout2.springboxLeRetour.test;

import java.awt.Color;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import javax.swing.JFileChooser;

import org.miv.graphstream.algorithm.layout2.Layout;
import org.miv.graphstream.algorithm.layout2.LayoutListener;
import org.miv.graphstream.algorithm.layout2.springboxLeRetour.*;
import org.miv.graphstream.graph.Edge;
import org.miv.graphstream.graph.Graph;
import org.miv.graphstream.graph.Node;
import org.miv.graphstream.graph.implementations.MultiGraph;
import org.miv.graphstream.io.GraphReader;
import org.miv.graphstream.io.GraphReaderFactory;
import org.miv.graphstream.io.GraphReaderListenerHelper;
import org.miv.graphstream.ui.GraphViewerListener;
import org.miv.graphstream.ui.GraphViewerRemote;
import org.miv.graphstream.ui.RemoteSprite;
import org.miv.pherd.gui.SwingParticleViewer;
import org.miv.pherd.ntree.BarycenterCellData;
import org.miv.pherd.ntree.Cell;
import org.miv.pherd.ntree.NTree;
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
	protected HashMap<String,Barycenter> barys;
	protected float maxForce = 0;
	protected boolean showForces = false;
	protected boolean showBarycenters = false;
	
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
		graph   = new MultiGraph( "Springs...", false, true );
		layout  = new SpringBox( false, random );
		viewer  = graph.display( false );
		sprites = new HashMap<String,MySprite>();
		barys   = new HashMap<String,Barycenter>();

//		new SwingParticleViewer( ((SpringBox)layout).getSpatialIndex() );
		
		layout.setForce( 0.8f );
		viewer.addViewerListener( this );
		layout.setSendNodeInfos( showForces );
		layout.setQuality( 1 );
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
			int step = 0;
			
			while( loop )
			{
				if( reading )
				{
					int i = 0;
					while( reading && i < 200 )
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
				showBaryCenters();
			//	computeAverageEdgeLength();
				
				step++;
				
				if( step % 10 == 0 )
				{
					System.err.printf( "Stable = %f%n", layout.getStabilization() );
				}
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	protected void showBaryCenters()
	{
		if( showBarycenters )
		{
			NTree ntree = ((SpringBox)layout).getSpatialIndex().getNTree();
			Cell  root  = ntree.getRootCell();
			
			for( Barycenter b : barys.values() )
				b.mark = false;
			
			showBarycenter( root );

			Iterator<Barycenter> i = barys.values().iterator();
			
			while( i.hasNext() )
			{
				Barycenter b = i.next();
				
				if( ! b.mark )
				{
					viewer.removeSprite( b.getId() );
					i.remove();
				}
			}

		}
	}
	
	protected void showBarycenter( Cell cell )
	{
		String id = cell.getId();
		
		Barycenter sprite = barys.get( id );
		BarycenterCellData data = (BarycenterCellData) cell.getData();
		
		if( sprite == null )
		{
			sprite = new Barycenter( id, viewer );
			sprite.setAttribute( "ui.class", String.format( "bary%d", cell.getDepth() ) );
			barys.put( id, sprite );
		}
		
		sprite.position( data.center.x, data.center.y, 0 );
		sprite.setAttribute( "label", String.format( "%d",(int) data.weight ) );
		sprite.mark = true;
		
		if( ! cell.isLeaf() && cell.getDepth() < 3 )
		{
			int div = cell.getSpace().getDivisions();
		
			for( int i=0; i<div; i++ )
				showBarycenter( cell.getSub( i ) );
		}
	}
	
	protected void computeAverageEdgeLength()
	{
		Iterator<? extends Edge> i = graph.getEdgeIterator();
		
		float length = 0;
		int   n      = 0;
		float max    = Float.MIN_VALUE;
		
		while( i.hasNext() )
		{
			Edge e = i.next();
			float len = graph.algorithm().getEdgeLength( e );
			length += len;
			n ++;
			
			if( len > max )
				max = len;
		}
		
		length /= n;
		System.err.printf( "Average edge length = %f   max =%f%n", length, max );
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
		if( showForces )
		{
			Node node = graph.getNode( id );
			
			if( node != null )
			{
				MySprite sprite = sprites.get( id );
				
				if( sprite == null )
				{
					sprite = new MySprite( id, viewer );
					sprite.attachToNode( id );
					sprite.setAttribute( "ui.class", "force" );
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
		if( showForces )
		{
			for( MySprite sprite: sprites.values() )
			{
				float color = 0;
				
				if( maxForce > 0 )
					color = sprite.len / maxForce;
			
				sprite.setAttribute( "color", new Color( 1, 0.8f-color*0.8f, 0.8f-color*0.8f ) );
			}
		}
	}
	
	protected static final String styleSheet =
		"node         { width:4; }" +
		"edge         { color:#A0A0A0; }" +
//		"sprite       { text-align: aside; }" +
		"sprite       { text-color: black; }" +
		"sprite.force { width:2; sprite-shape:arrow; z-index:-1; arrow-width:3px; arrow-length:20px; }" +
		"sprite.bary0 { width:16px; color: red;      z-index: 2; }" +
		"sprite.bary1 { width:12px; color: orange;   z-index: 3; }" +
		"sprite.bary2 { width: 8px; color: green;    z-index: 4; }" +
		"sprite.bary3 { width: 4px; color: blue;     z-index: 5; }";

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
	
	public static class Barycenter extends RemoteSprite
	{
		public boolean mark;
		
		public Barycenter( String id, GraphViewerRemote remote )
		{
			super( id, remote );
		}
	
		@Override
		public void position( float x, float y, float z )
		{
			super.position( x, y, z );
		}
	}
}
