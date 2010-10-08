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

/**
 * Try to instantiate the correct writer given a graph filename.
 * 
 * <p>
 * This class tries to instantiate a writer given a filename. Actually it purely
 * tries to analyze the extension and propose the writer according to this
 * extension.
 * </p>
 */
public class FileSinkFactory {
	/**
	 * Looks at the file name given and its extension and propose a file output
	 * for the format that match this extension.
	 * 
	 * @param filename
	 *            The file name where the graph will be written.
	 * @return A file sink or null.
	 */
	public static FileSink sinkFor(String filename) {
		// String fc = new String( filename );
		filename = filename.toLowerCase();

		if (filename.endsWith(".dgs"))
			return new FileSinkDGS();

		if (filename.endsWith(".gml"))
			return new FileSinkGML();

		if (filename.endsWith(".dot"))
			return new FileSinkDOT();

		if (filename.endsWith(".svg"))
			return new FileSinkSVG();

		return null;
	}
}