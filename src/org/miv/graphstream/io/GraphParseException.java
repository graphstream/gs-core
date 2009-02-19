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
 */
package org.miv.graphstream.io;

/**
 * Raised when a parse error occured while reading a graph file.
 *
 * @author Yoann Pigné
 * @author Antoine Dutot
 * @since 19 Sept. 2004
 */
public class GraphParseException
	extends Exception
{
	private static final long serialVersionUID = 8469350631709220693L;

	public
	GraphParseException()
	{
		super( "graph parse error" );
	}

	public
	GraphParseException( String message )
	{
		super( message );
	}
}
