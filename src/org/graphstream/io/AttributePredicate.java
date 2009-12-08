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
 * Copyright 2006 - 2009
 * 	Julien Baudry
 * 	Antoine Dutot
 * 	Yoann Pigné
 * 	Guilhelm Savin
 */

package org.graphstream.io;

/**
 * Tell if an attribute is recognised.
 */
public interface AttributePredicate
{
	/**
	 * Tell if an attribute is recognised or not. The
	 * predicate can work on the name of the attribute, on its value or on both.
	 * @param attributeName The name of the attribute.
	 * @param attributeValue The value of the attribute.
	 * @return True if the attribute must be removed from the stream of graph events.
	 */
	boolean matches( String attributeName, Object attributeValue );
}