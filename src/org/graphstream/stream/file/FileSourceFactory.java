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
 * Copyright 2006 - 2010
 * 	Julien Baudry
 * 	Antoine Dutot
 * 	Yoann Pigné
 * 	Guilhelm Savin
 */

package org.graphstream.stream.file;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * File source factory.
 *
 * <p>
 * A graph reader factory allow to create readers according to a given file. It
 * both tries to read the start of the file to infer its type (works well for
 * file formats with a magic cookie or header), and if it fails it tries to look
 * at the file name extension.
 * </p>
 */
public class FileSourceFactory
{
	/**
	 * Create a file input for the given file name.
	 *  
	 * <p>
	 * This method first tests if
	 * the file is a regular file and is readable. If so, it opens it and reads
	 * the magic cookie to test the known file formats that can be inferred from
	 * their header. If it works, it returns a file input for the format. Else it
	 * looks at the file name extension, and returns a file input for the extension.
	 * Finally if all fail, it throws a NotFoundException.
	 * </p>
	 * 
	 * <p>
	 * Notice that this method only creates the file input and does not connect it to
	 * a graph.
	 * </p>
	 *
	 * @param fileName Name of the graph file.
	 * @return A graph reader suitable for the fileName graph format.
	 * @throws IOException If the file is not readable or accessible.
	 */
	public static FileSource sourceFor( String fileName ) throws IOException
	{
		File file = new File( fileName );

		if( ! file.isFile() )
			throw new IOException( "not a regular file '" + fileName + "'" );

		if( ! file.canRead() )
			throw new IOException( "not a readable file '" + fileName + "'" );

		// Try to read the beginning of the file.
		
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
					return new FileSourceDGS1And2();
				}
				else if( b[5] == '3' || b[5] == '4' )
				{
					return new FileSourceDGS();
				}
			}
		}
		
		// Maybe match a GML file as most GML files begin by the line "graph [",
		// but not sure, you may create a GML file that starts by a comment, an
		// empty line, with any kind of spaces, etc.
		
		if( n >= 7 && b[0] == 'g' && b[1] == 'r' && b[2] == 'a' && b[3] == 'p' && b[4] == 'h' && b[5] == ' ' && b[6] == '[' )
		{
			return new FileSourceGML();
		}
		
		// The web reader.
	
		String flc = fileName.toLowerCase();

		if(  flc.endsWith( ".html" ) || flc.endsWith( ".htm" ) )
		{
			// TODO
		}
		
		// If we did not found anything, we try with the filename extension ...
		
		if( flc.endsWith( ".gml" ) )
		{
			return new FileSourceGML();
		}
		
		if( flc.endsWith( ".chaco" ) || flc.endsWith( ".graph" ) )
		{
			//return new GraphReaderChaco();
		}
		
		if( flc.endsWith( ".dot" ) ) 
		{
			return new FileSourceDOT();
		}
		
		if( flc.endsWith( ".edge" ) )
		{
			return new FileSourceEdge();
		}
		
		if( flc.endsWith( ".tlp" ) )
		{
//			return new FileInputTLP();
		}
		
		return null;
	}
}