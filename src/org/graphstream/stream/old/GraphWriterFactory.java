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

import org.graphstream.graph.NotFoundException;

/**
 * Try to instantiate the correct writer given a graph filename.
 * 
 * <p>
 * This class tries to instantiate a writer given a filename.
 * Actually it purely tries to analyse the extension and propsose
 * the writer according to this extension.
 * </p>
 *
 * @since 2007
 */
public class GraphWriterFactory
{
	public static GraphWriter writerFor( String filename )
		throws NotFoundException
	{
		String fc = new String( filename );
		filename = filename.toLowerCase();
		
		if( filename.endsWith( ".dgs" ) )
			return new GraphWriterDGS();
		
		if( filename.endsWith( ".dot" ) )
			return new GraphWriterDOT();
		
		if( filename.endsWith( ".svg" ) )
			return new GraphWriterSVG();
		
		throw new NotFoundException( "cannot find a suitable writer format for the given filemane "+fc );
	}
}