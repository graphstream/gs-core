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

package org.miv.graphstream.io2;

/**
 * Tell if an attribute must be filtered (removed from the stream of graph events).
 * 
 * <p>Attribute filters are used on objects that inherit the {@link Filter} or {@link Output}
 * interfaces. They allow to avoid some kinds of attributes to be passed in the stream of
 * events between various parts of the graph stream.</p>
 */
public interface AttributeFilter
{
	/**
	 * Tell if an attribute must be filtered, that is, removed from a stream of graph events. The
	 * filtering can be done on the name of the attribute, on its value or on both. Attribute
	 * filters are used on objects that inherit the {@link Filter} or {@link Output} interfaces.
	 * @param attributeName The name of the attribute.
	 * @param attributeValue The value of the attribute.
	 * @return True if the attribute must be removed from the stream of graph events.
	 */
	boolean isFiltered( String attributeName, Object attributeValue );
}