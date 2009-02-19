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

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

import org.miv.graphstream.algorithm.layout2.LayoutRunner.LayoutRemote;
import org.miv.graphstream.algorithm.layout2.springboxLeRetour.SpringBox;
import org.miv.graphstream.graph.Graph;
import org.miv.graphstream.graph.implementations.MultiGraph;
import org.miv.graphstream.io.GraphParseException;
import org.miv.graphstream.ui.GraphViewerRemote;
import org.miv.graphstream.ui.swing.SwingGraphViewer;
import org.miv.util.NotFoundException;

public class SimpleTest
{
	public static void main( String args[] ) {
		try
		{
			new SimpleTest( args );
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	public SimpleTest( String args[] ) throws NotFoundException, IOException, GraphParseException {
		String filename = getFileToRead( args );
		
		Graph graph = new MultiGraph( "Graph" );
		SwingGraphViewer viewer = new SwingGraphViewer( graph, new SpringBox( false ), false );
		
		GraphViewerRemote remote = viewer.newViewerRemote();
		LayoutRemote     layout = remote.getLayoutRemote();
		
		layout.setQuality( 1 );
		remote.setQuality( 0 );
		
		graph.read( filename );
		
		System.out.printf( "OK%n" );
		System.out.flush();
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
}
