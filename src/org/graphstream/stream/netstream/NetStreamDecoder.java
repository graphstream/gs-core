/*
 * Copyright 2006 - 2015
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
package org.graphstream.stream.netstream;

import java.io.IOException;
import java.io.InputStream;

import org.graphstream.stream.thread.ThreadProxyPipe;

/**
 * 
 */
public interface NetStreamDecoder {

	/**
	 * Gives the stream (a ThreadProxyPipe) identified with this name. If no
	 * pipe exists under this name, a new one is created and returned
	 * 
	 * @param name
	 *            Identifier of the stream.
	 * @return the identified pipe
	 */
	public abstract ThreadProxyPipe getStream(String name);

	/**
	 * Gives the default stream (a ThreadProxyPipe) identified with the name
	 * "default". It is created if it does not exist.
	 * 
	 * @return the default pipe
	 */

	public abstract ThreadProxyPipe getDefaultStream();

	/**
	 * Register a stream. All events with the given stream name will be directed
	 * to it. The user has to ensure the ThreadProxyPipe can be safely written
	 * to by the Receiver's thread.
	 * 
	 * @param name
	 *            Filter only message with this name to the given message box.
	 * @param stream
	 *            The ThreadProxyPipe to push the events to.
	 * @throws Exception
	 *             If another Pipe is already registered at the given name.
	 */
	public abstract void register(String name, ThreadProxyPipe stream)
			throws Exception;

	/**
	 * Decode one message.
	 */
	public abstract void decodeMessage(InputStream in) throws IOException;

	/**
	 * Enable or disable debugging.
	 */
	public void setDebugOn(boolean on);
}