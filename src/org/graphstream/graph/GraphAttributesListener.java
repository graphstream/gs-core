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
 * Copyright 2006 - 2009
 * 	Julien Baudry
 * 	Antoine Dutot
 * 	Yoann Pigné
 * 	Guilhelm Savin
 */
package org.graphstream.graph;

import org.graphstream.graph.GraphEvent.AttributeEvent;

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
	public void graphAttributeAdded( AttributeEvent e );
	
	/**
	 * A graph attribute was changed.
	 * @param sourceId Identifier of the graph where the attribute changed.
	 * @param attribute The attribute name.
	 * @param oldValue The attribute old value.
	 * @param newValue The attribute new value.
	 */
	public void graphAttributeChanged( AttributeEvent e );
	
	/**
	 * A graph attribute was removed.
	 * @param sourceId Identifier of the graph where the attribute was removed.
	 * @param attribute The removed attribute name.
	 */
	public void graphAttributeRemoved( AttributeEvent e );
	
	/**
	 * A node attribute was added.
	 * @param sourceId Identifier of the graph where the change occurred.
	 * @param nodeId Identifier of the node whose attribute changed.
	 * @param attribute The attribute name.
	 * @param value The attribute new value.
	 */
	public void nodeAttributeAdded( AttributeEvent e );
	
	/**
	 * A node attribute was changed.
	 * @param sourceId Identifier of the graph where the change occurred.
	 * @param nodeId Identifier of the node whose attribute changed.
	 * @param attribute The attribute name.
	 * @param oldValue The attribute old value.
	 * @param newValue The attribute new value.
	 */
	public void nodeAttributeChanged( AttributeEvent e );
	
	/**
	 * A node attribute was removed.
	 * @param sourceId Identifier of the graph where the attribute was removed.
	 * @param nodeId Identifier of the node whose attribute was removed.
	 * @param attribute The removed attribute name.
	 */
	public void nodeAttributeRemoved( AttributeEvent e );

	/**
	 * A edge attribute was added.
	 * @param sourceId Identifier of the graph where the change occurred.
	 * @param edgeId Identifier of the edge whose attribute changed.
	 * @param attribute The attribute name.
	 * @param value The attribute new value.
	 */
	public void edgeAttributeAdded( AttributeEvent e );

	/**
	 * A edge attribute was changed.
	 * @param sourceId Identifier of the graph where the change occurred.
	 * @param edgeId Identifier of the edge whose attribute changed.
	 * @param attribute The attribute name.
	 * @param oldValue The attribute old value.
	 * @param newValue The attribute new value.
	 */
	public void edgeAttributeChanged( AttributeEvent e );
	
	/**
	 * A edge attribute was removed.
	 * @param sourceId Identifier of the graph where the attribute was removed.
	 * @param edgeId Identifier of the edge whose attribute was removed.
	 * @param attribute The removed attribute name.
	 */
	public void edgeAttributeRemoved( AttributeEvent e );
}