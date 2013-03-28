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

/**
*
* Copyright (c) 2010 University of Luxembourg
*
* @file Base64Unpacker.java
* @date Dec 20, 2011
*
* @author Yoann Pigné
*
*/

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 
 */
public class Base64Unpacker extends NetStreamUnpacker {

	/* (non-Javadoc)
	 * @see org.graphstream.stream.netstream.packing.NetStreamUnpacker#unpackMessage(java.nio.ByteBuffer, int, int)
	 */
	@Override
	public ByteBuffer unpackMessage(ByteBuffer buffer, int startIndex,
			int endIndex) {
		try {
			byte[] raw = Base64.decode(buffer.array(), startIndex, (endIndex-startIndex), Base64.NO_OPTIONS);
			return ByteBuffer.wrap(raw);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.graphstream.stream.netstream.packing.NetStreamUnpacker#packMessageSize(java.nio.ByteBuffer)
	 */
	@Override
	public int unpackMessageSize(ByteBuffer buffer) {
		try {
			byte[] raw = Base64.decode(buffer.array(),buffer.position(), 8,Base64.NO_OPTIONS);
			return ByteBuffer.wrap(raw).getInt();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}

	public static void main(String[] args) {
		Base64Packer packer = new Base64Packer();
		Base64Unpacker b = new Base64Unpacker();

		ByteBuffer source = ByteBuffer.allocate(9);
		
		source.putInt(-1).putFloat(0.1f);
		source.put((byte)'e');
		
		source.rewind();
		
		ByteBuffer bb = b.unpackMessage(packer.packMessage( source ) );
		
		bb.rewind();
		
		System.out.println(bb.getInt());
		System.out.println(bb.getFloat());
		System.out.println((char)bb.get());
	}

	/* (non-Javadoc)
	 * @see org.graphstream.stream.netstream.packing.NetStreamUnpacker#sizeOfInt()
	 */
	@Override
	public int sizeOfInt() {
		return 8;
	}
}
