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

package org.graphstream.io.file;

import org.miv.util.NotFoundException;

/**
 * Try to instantiate the correct writer given a graph filename.
 * 
 * <p>
 * This class tries to instantiate a writer given a filename.
 * Actually it purely tries to analyse the extension and propose
 * the writer according to this extension.
 * </p>
 */
public class FileSinkFactory
{
	/**
	 * Look at the file name given and its extension and propose a file output for the format
	 * that match this extension.
	 * @param filename The file name where the graph will be written.
	 * @return A file output.
	 * @throws NotFoundException If no file output can be found for the given file name.
	 */
	public static FileSink outputFor( String filename ) throws NotFoundException
	{
		String fc = new String( filename );
		filename = filename.toLowerCase();
		
		if( filename.endsWith( ".dgs" ) )
			return new FileSinkDGS();
		
//		if( filename.endsWith( ".dot" ) )
//			return new FileOutputDOT();
		// TODO
		
//		if( filename.endsWith( ".svg" ) )
//			return new FileOutputSVG();
		// TODO
		
		throw new NotFoundException( "cannot find a suitable writer format for the given filemane "+fc );
	}
}