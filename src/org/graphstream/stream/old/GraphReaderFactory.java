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

package org.graphstream.io.old;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.graphstream.graph.Graph;
import org.graphstream.graph.NotFoundException;

/**
 * Graph reader factory.
 *
 * A graph reader factory allow to create readers according to a given file. It
 * both tries to read the start of the file to infer its type (works well for
 * file formats with a magic cookie or header), and if it fails it tries to look
 * at the file name extension.
 *
 * @see org.graphstream.io.old.GraphReader
 * @since 20050515
 */
public class GraphReaderFactory
{	
	/**
	 * Create a graph reader for the given file name.
	 *  
	 * <p>
	 * This method first tests if
	 * the file is a regular file and is readable. If so, it opens it and reads
	 * the magic cookie to test the known file formats that can be infered from
	 * their header. If it works, it returns a reader for the format. Else if
	 * looks at the file name extension, and returns a reader for the extension.
	 * Finally if all fail, it throws a NotFoundException.
	 * </p>
	 * 
	 * <p>
	 * Notice that this method only create the reader and does not connect it to
	 * a graph.
	 * </p>
	 *
	 * @see #readerFor(String, Graph)
	 * @param fileName Name of the graph file.
	 * @return A graph reader suitable for the fileName graph format.
	 * @throws IOException If the file is not readable or accessible.
	 * @throws NotFoundException If no reader can be found to read the given file.
	 */
	public static GraphReader readerFor( String fileName )
		throws IOException, NotFoundException
	{
		return readerFor( fileName, null );
	}
	
	/**
	 * Create a graph reader for the given file name and connect it to a graph
	 * that will be modified according to the data read.
	 * 
	 * <p>
	 * Works exactly like {@link #readerFor(String)} but additionnaly create a
	 * {@link org.graphstream.io.old.GraphReaderListenerHelper} that will listen
	 * at the created reader and modify the given graph accordingly.
	 * </p>
	 * 
	 * <p>
	 * This is an utility method.
	 * </p>
	 * 
	 * @see #readerFor(String)
	 * @param fileName Name of the graph file.
	 * @param graph The graph to modify accordingly.
	 * @return A graph reader suitable for the fileName graph format and
	 *   whose listener is connected to a
	 *   {@link org.graphstream.io.old.GraphReaderListenerHelper} that will
	 *   modify a graph according to what is read.
	 * @throws IOException If the file is not readable or accessible.
	 * @throws NotFoundException If no reader can be found to read the given file.
	 */
	public static GraphReader readerFor( String fileName, Graph graph )
		throws IOException, NotFoundException
	{
		File file = new File( fileName );

		if( ! file.isFile() )
			throw new IOException( "not a regular file '" + fileName + "'" );

		if( ! file.canRead() )
			throw new IOException( "not a readable file '" + fileName + "'" );

		
		// Try to read the begining of the file.
		
		RandomAccessFile in = new RandomAccessFile( fileName, "r" );
		
		byte b[] = new byte[10];
		int  n = in.read( b, 0, 10 );

//		System.err.printf( "[" );
//		for( int i=0; i<n; ++i )
//		{
//			System.err.printf( "%c", (char)b[i] );
//		}
//		System.err.printf( "]%n" );
		
		in.close();
		
		// Surely match a DGS file, as DGS files are well done and have a signature.
		
		if( n >= 3 && b[0] == 'D' && b[1] == 'G' && b[2] == 'S' )
		{
			if( n >= 6 && b[3] == '0' && b[4] == '0' )
			{
				if( b[5] == '1' || b[5] == '2' )
				{
					GraphReader gr = new GraphReaderDGS1And2();

					if( graph != null )
						gr.addGraphReaderListener( new GraphReaderListenerHelper( graph ) );
					
					return gr;
				}
				else if( b[5] == '3' || b[5] == '4' )
				{
					GraphReader gr = new GraphReaderDGS();
					
					if( graph != null )
						gr.addGraphReaderListener( new GraphReaderListenerHelper( graph ) );
					
					return gr;
				}
			}
		}
		
		// Maybe match a GML file as most GML files begin by the line "graph [",
		// but not sure, you may create a GML file that starts by a comment, an
		// empty line, with any kind of spaces, etc.
		
		if( n >= 7 && b[0] == 'g' && b[1] == 'r' && b[2] == 'a' && b[3] == 'p' && b[4] == 'h' && b[5] == ' ' && b[6] == '[' )
		{
			GraphReader gr = new GraphReaderGML();
			
			if( graph != null )
				gr.addGraphReaderListener( new GraphReaderListenerHelper( graph ) );
			
			return gr;
		}
		
		// The web reader.
		
		String flc = fileName.toLowerCase();
		
		if( flc.startsWith( "http://" ) || flc.startsWith( "https://" ) || flc.endsWith( ".html" ) || flc.endsWith( ".htm" ) )
		{
			GraphReader gr = new GraphReaderWeb();
			
			if( graph != null )
				gr.addGraphReaderListener( new GraphReaderListenerHelper( graph ) );
			
			return gr;
		}
		
		// If we did not found anything, we try with the filename extension ...
		
		if( flc.endsWith( ".gml" ) )
		{
			GraphReader gr = new GraphReaderGML();
			
			if( graph != null )
				gr.addGraphReaderListener( new GraphReaderListenerHelper( graph ) );
			
			return gr;
		}
		
		if( flc.endsWith( ".chaco" ) || flc.endsWith( ".graph" ) )
		{
			//return new GraphReaderChaco();
		}
		
		if( flc.endsWith( ".dot" ) ) 
		{
			GraphReader gr = new GraphReaderDOT();
			
			if( graph != null )
				gr.addGraphReaderListener( new GraphReaderListenerHelper( graph ) );
			
			return gr;
		}
		
		if( flc.endsWith( ".edge" ) )
		{
			GraphReader gr = new GraphReaderEdge();
			
			if( graph != null )
				gr.addGraphReaderListener( new GraphReaderListenerHelper( graph ) );
			
			return gr;
		}
		
		if( flc.endsWith( ".tlp" ) )
		{
			GraphReader gr = new GraphReaderTLP();
			
			if( graph != null )
				gr.addGraphReaderListener( new GraphReaderListenerHelper( graph ) );
			
			return gr;
		}
		
		throw new NotFoundException( "cannot find a suitable reader for file '" + fileName + "': unknown file content or extension" );
	}
}