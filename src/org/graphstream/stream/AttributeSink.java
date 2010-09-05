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
 * Copyright 2006 - 2010
 * 	Julien Baudry
 * 	Antoine Dutot
 * 	Yoann Pigné
 * 	Guilhelm Savin
 */

package org.graphstream.stream;

/**
 * Interface to listen at changes on attributes of a graph.
 *
 * <p>The graph attributes listener is called each time an attribute is added,
 * or removed, and each time its value is changed.</p>
 */
public interface AttributeSink
{
	/**
	 * A graph attribute was added.
	 * @param sourceId Identifier of the graph where the attribute changed.
	 * @param attribute The attribute name.
	 * @param value The attribute new value.
	 */
	void graphAttributeAdded( String sourceId, long timeId, String attribute, Object value );
	
	/**
	 * A graph attribute was changed.
	 * @param sourceId Identifier of the graph where the attribute changed.
	 * @param attribute The attribute name.
	 * @param oldValue The attribute old value.
	 * @param newValue The attribute new value.
	 */
	void graphAttributeChanged( String sourceId, long timeId, String attribute, Object oldValue, Object newValue );
	
	/**
	 * A graph attribute was removed.
	 * @param sourceId Identifier of the graph where the attribute was removed.
	 * @param attribute The removed attribute name.
	 */
	void graphAttributeRemoved( String sourceId, long timeId, String attribute );
	
	/**
	 * A node attribute was added.
	 * @param sourceId Identifier of the graph where the change occurred.
	 * @param nodeId Identifier of the node whose attribute changed.
	 * @param attribute The attribute name.
	 * @param value The attribute new value.
	 */
	void nodeAttributeAdded( String sourceId, long timeId, String nodeId, String attribute, Object value );
	
	/**
	 * A node attribute was changed.
	 * @param sourceId Identifier of the graph where the change occurred.
	 * @param nodeId Identifier of the node whose attribute changed.
	 * @param attribute The attribute name.
	 * @param oldValue The attribute old value.
	 * @param newValue The attribute new value.
	 */
	void nodeAttributeChanged( String sourceId, long timeId, String nodeId, String attribute, Object oldValue, Object newValue );
	
	/**
	 * A node attribute was removed.
	 * @param sourceId Identifier of the graph where the attribute was removed.
	 * @param nodeId Identifier of the node whose attribute was removed.
	 * @param attribute The removed attribute name.
	 */
	void nodeAttributeRemoved( String sourceId, long timeId, String nodeId, String attribute );

	/**
	 * A edge attribute was added.
	 * @param sourceId Identifier of the graph where the change occurred.
	 * @param edgeId Identifier of the edge whose attribute changed.
	 * @param attribute The attribute name.
	 * @param value The attribute new value.
	 */
	void edgeAttributeAdded( String sourceId, long timeId, String edgeId, String attribute, Object value );

	/**
	 * A edge attribute was changed.
	 * @param sourceId Identifier of the graph where the change occurred.
	 * @param edgeId Identifier of the edge whose attribute changed.
	 * @param attribute The attribute name.
	 * @param oldValue The attribute old value.
	 * @param newValue The attribute new value.
	 */
	void edgeAttributeChanged( String sourceId, long timeId, String edgeId, String attribute, Object oldValue, Object newValue );
	
	/**
	 * A edge attribute was removed.
	 * @param sourceId Identifier of the graph where the attribute was removed.
	 * @param edgeId Identifier of the edge whose attribute was removed.
	 * @param attribute The removed attribute name.
	 */
	void edgeAttributeRemoved( String sourceId, long timeId, String edgeId, String attribute );
}