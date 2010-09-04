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
 * Copyright 2006 - 2010
 * 	Julien Baudry
 * 	Antoine Dutot
 * 	Yoann Pigné
 * 	Guilhelm Savin
 */

package org.graphstream.graph;

/**
 * Thrown when a searched object is not found.
 *
 * @since 20020615
 */
public class ElementNotFoundException
	extends RuntimeException
{
	private static final long serialVersionUID = 5089958436773409615L;

	/**
	 * Throws the message "not found".
	 */
	public
	ElementNotFoundException()
	{
		super( "not found" );
	}

	/**
	 * Throws <code>message</code>.
	 * @param message The message to throw.
	 */
	public
	ElementNotFoundException( String message )
	{
		super( "not found: " + message );
	}
}