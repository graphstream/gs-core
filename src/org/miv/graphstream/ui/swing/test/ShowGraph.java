/*
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 */

package org.miv.graphstream.ui.swing.test;

import java.io.IOException;

import org.miv.graphstream.graph.Graph;
import org.miv.graphstream.graph.implementations.DefaultGraph;
import org.miv.graphstream.io.GraphParseException;
import org.miv.graphstream.ui.GraphViewer;
import org.miv.graphstream.ui.GraphViewerRemote;
import org.miv.graphstream.ui.swing.SwingGraphViewer;
import org.miv.util.NotFoundException;

/**
 * Show all graphs given on the command line.
 * 
 * @author Antoine Dutot
 */
public class ShowGraph
{
	public static void main( String args[] )
	{
		new ShowGraph( args );
	}
	
	public ShowGraph( String args[] )
	{
		if( args.length == 1 )
			showGraph( args[0], null );
		else if( args.length == 2 )
			showGraph( args[0], args[1] );
		else
		{
			System.err.printf( "Usage: graphFileName styleSheetFileName%n" );
		}
	}
	
	protected void showGraph( String fileName, String styleSheetFileName )
	{
		Graph graph = new DefaultGraph();
		
		GraphViewer viewer = new SwingGraphViewer( graph, false );
		GraphViewerRemote viewerRemote = viewer.newViewerRemote();

		viewer.getLayoutRemote().setQuality( 4 );
		viewerRemote.setQuality( 4 );
	
		if( styleSheetFileName != null )
			graph.addAttribute( "ui.stylesheet", "url("+styleSheetFileName+")" );

		try
        {
	        graph.read( fileName );
        }
        catch( NotFoundException e )
        {
	        e.printStackTrace();
        }
        catch( IOException e )
        {
	        e.printStackTrace();
        }
        catch( GraphParseException e )
        {
	        e.printStackTrace();
        }
	}
}