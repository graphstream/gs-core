/*
 * Copyright 2006 - 2011 
 *     Stefan Balev 	<stefan.balev@graphstream-project.org>
 *     Julien Baudry	<julien.baudry@graphstream-project.org>
 *     Antoine Dutot	<antoine.dutot@graphstream-project.org>
 *     Yoann Pigné		<yoann.pigne@graphstream-project.org>
 *     Guilhelm Savin	<guilhelm.savin@graphstream-project.org>
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
package org.graphstream.util;

/**
 * Raised when an index was out of its range of possible values.
 *
 * @author Antoine Dutot
 * @author Yoann Pigné
 * @since 20000701
 * @version 0.1
 */
public class InvalidIndexException
	extends RuntimeException
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5843059698058058951L;
	/**
	 * The invalid index.
	 */
	public long index;

	public
	InvalidIndexException()
	{
		super( "invalid index" );
	}
	
	public
	InvalidIndexException( long index )
	{
		super( "invalid index " + index );
		this.index = index;
	}

	public
	InvalidIndexException( String message )
	{
		super( "invalid index: " + message );
	}

	public
	InvalidIndexException( String message, long index )
	{
		super( "invalid index " + index + ": " + message );
		this.index = index;
	}
}
