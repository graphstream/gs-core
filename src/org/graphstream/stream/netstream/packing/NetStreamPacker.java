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
package org.graphstream.stream.netstream.packing;

import java.nio.ByteBuffer;

/**
 * 
 */
public abstract class NetStreamPacker {

	/**
	 * Pack the given ByteBuffer from startIndex to endIdex 
	 * @param buffer The buffer to pack/encode
	 * @param startIndex the index at which the encoding starts in the buffer
	 * @param endIndex the index at which the encoding stops
	 * @return a ByteBuffer that is the packed version of the input one. It may not have the same size.
	 */
	public abstract ByteBuffer packMessage(ByteBuffer buffer, int startIndex, int endIndex);

	/**
	 * Pack the given ByteBuffer form its position to its capacity.
	 * @param buffer The buffer to pack/encode
	 * @return a ByteBuffer that is the packed version of the input one. It may not have the same size.
	 */
	public ByteBuffer packMessage(ByteBuffer buffer){
		return this.packMessage(buffer, 0, buffer.capacity());
	}

	/**
	 * @param capacity
	 * @return
	 */
	public abstract ByteBuffer packMessageSize(int capacity) ;
	
}
