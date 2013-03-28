/*
 * Copyright 2006 - 2013
 *     Stefan Balev     <stefan.balev@graphstream-project.org>
 *     Julien Baudry    <julien.baudry@graphstream-project.org>
 *     Antoine Dutot    <antoine.dutot@graphstream-project.org>
 *     Yoann Pigné      <yoann.pigne@graphstream-project.org>
 *     Guilhelm Savin   <guilhelm.savin@graphstream-project.org>
 * 
 * This file is part of GraphStream <http://graphstream-project.org>.
 * 
 * GraphStream is a library whose purpose is to handle static or dynamic
 * graph, create them from scratch, file or any source and display them.
 * 
 * This program is free software distributed under the terms of two licenses, the
 * CeCILL-C license that fits European law, and the GNU Lesser General Public
 * License. You can  use, modify and/ or redistribute the software under the terms
 * of the CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
 * URL <http://www.cecill.info> or under the terms of the GNU LGPL as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C and LGPL licenses and that you accept their terms.
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

		if (filename.endsWith(".dgml"))
			return new FileSinkDynamicGML();
		
		if (filename.endsWith(".gml"))
			return new FileSinkGML();

		if (filename.endsWith(".dot"))
			return new FileSinkDOT();

		if (filename.endsWith(".svg"))
			return new FileSinkSVG();

		return null;
	}
}