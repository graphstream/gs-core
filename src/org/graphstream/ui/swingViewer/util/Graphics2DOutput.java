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
package org.graphstream.ui.swingViewer.util;

import java.awt.Graphics2D;
import java.io.IOException;

/**
 * A special interface for renderers that allows to replace the Graphics2D.
 *
 * <p>
 * Several external libraries use to replace the {@link Graphics2D} of AWT in
 * order to produce a file or on a printer in a given format. However it is not possible to
 * link such libraries in the gs-core module of GraphStream. To avoid this
 * problem, this interface defines a plug-in that must implement be able to
 * yield a {@link Graphics2D} usable instead of the default one. 
 * </p>
 */
public interface Graphics2DOutput {
	/**
	 * The graphics to use instead of the default {@link Graphics2D} of AWT.
	 */
	Graphics2D getGraphics();

	/**
	 * Output (if needed) the results of the last painting done with the {@link Graphics2D}.
	 * @param outputName The name of the output to use, for some renderers it is a file,
	 * for others it is an URL, a string description of the output, etc. 
	 */
	void outputTo(String outputName) throws IOException;
}
