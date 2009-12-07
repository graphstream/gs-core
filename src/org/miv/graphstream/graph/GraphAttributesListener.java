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

package org.miv.graphstream.graph;

/**
 * Interface to listen at changes on attributes of a graph.
 *
 * <p>The graph attributes listener is called each time an attribute is added,
 * or removed, and each time its value is changed.</p>
 */
public interface GraphAttributesListener
{
	/**
	 * A graph attribute was added.
	 * @param sourceId Identifier of the graph where the attribute changed.
	 * @param attribute The attribute name.
	 * @param value The attribute new value.
	 */
	public void graphAttributeAdded( String sourceId, String attribute, Object value );
	
	/**
	 * A graph attribute was changed.
	 * @param sourceId Identifier of the graph where the attribute changed.
	 * @param attribute The attribute name.
	 * @param oldValue The attribute old value.
	 * @param newValue The attribute new value.
	 */
	public void graphAttributeChanged( String sourceId, String attribute, Object oldValue, Object newValue );
	
	/**
	 * A graph attribute was removed.
	 * @param sourceId Identifier of the graph where the attribute was removed.
	 * @param attribute The removed attribute name.
	 */
	public void graphAttributeRemoved( String sourceId, String attribute );
	
	/**
	 * A node attribute was added.
	 * @param sourceId Identifier of the graph where the change occurred.
	 * @param nodeId Identifier of the node whose attribute changed.
	 * @param attribute The attribute name.
	 * @param value The attribute new value.
	 */
	public void nodeAttributeAdded( String sourceId, String nodeId, String attribute, Object value );
	
	/**
	 * A node attribute was changed.
	 * @param sourceId Identifier of the graph where the change occurred.
	 * @param nodeId Identifier of the node whose attribute changed.
	 * @param attribute The attribute name.
	 * @param oldValue The attribute old value.
	 * @param newValue The attribute new value.
	 */
	public void nodeAttributeChanged( String sourceId, String nodeId, String attribute, Object oldValue, Object newValue );
	
	/**
	 * A node attribute was removed.
	 * @param sourceId Identifier of the graph where the attribute was removed.
	 * @param nodeId Identifier of the node whose attribute was removed.
	 * @param attribute The removed attribute name.
	 */
	public void nodeAttributeRemoved( String sourceId, String nodeId, String attribute );

	/**
	 * A edge attribute was added.
	 * @param sourceId Identifier of the graph where the change occurred.
	 * @param edgeId Identifier of the edge whose attribute changed.
	 * @param attribute The attribute name.
	 * @param value The attribute new value.
	 */
	public void edgeAttributeAdded( String sourceId, String edgeId, String attribute, Object value );

	/**
	 * A edge attribute was changed.
	 * @param sourceId Identifier of the graph where the change occurred.
	 * @param edgeId Identifier of the edge whose attribute changed.
	 * @param attribute The attribute name.
	 * @param oldValue The attribute old value.
	 * @param newValue The attribute new value.
	 */
	public void edgeAttributeChanged( String sourceId, String edgeId, String attribute, Object oldValue, Object newValue );
	
	/**
	 * A edge attribute was removed.
	 * @param sourceId Identifier of the graph where the attribute was removed.
	 * @param edgeId Identifier of the edge whose attribute was removed.
	 * @param attribute The removed attribute name.
	 */
	public void edgeAttributeRemoved( String sourceId, String edgeId, String attribute );
}