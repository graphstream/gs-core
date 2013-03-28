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
public abstract class NetStreamUnpacker {
	
	/**
	 * An unpacker has to be abble to indicated what is the size of an int after being packed.
	 * @return
	 */
	public abstract int sizeOfInt();
	
	/**
	 * Unpack the given ByteBuffer from startIndex to endIdex 
	 * @param buffer The buffer to unpack/decode
	 * @param startIndex the index at which the decoding starts in the buffer
	 * @param endIndex the index at which the decoding stops
	 * @return a ByteBuffer that is the unpacked version of the input one. It may not have the same size.
	 */
	public abstract ByteBuffer unpackMessage(ByteBuffer buffer, int startIndex, int endIndex);

	/**
	 * Unpack the given ByteBuffer 
	 * @param buffer The buffer to unpack/decode
	 * @return a ByteBuffer that is the unpacked version of the input one. It may not have the same size.
	 */
	public ByteBuffer unpackMessage(ByteBuffer buffer){
		return this.unpackMessage(buffer, 0, buffer.capacity());
	}

	/**
	 * Unpacks the data necessary to decode a 4 bytes integer that indicates the size of the following message. 
	 * 
	 * The given buffer's position may be important for the unpacker to work. This method may also change the given bytebuffer's position attribute. 
	 * 
	 * @param buffer The byteBuffer who's content has the encoded value of the needed  size integer.
	 * @return
	 */
	public abstract int unpackMessageSize(ByteBuffer buffer);
	
	
	
}
