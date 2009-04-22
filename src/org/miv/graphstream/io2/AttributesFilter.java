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
 * Allows to filter the attribute event stream.
 */
public interface AttributesFilter extends Filter
{
	/**
	 * Set an attribute filter for graph, node and edge attributes. If the filter is null,
	 * attributes will not be filtered globally.
	 * @param filter The filter to use, it can be null to disable global attribute filtering.
	 */
	void setGlobalAttributeFilter( AttributeFilter filter );
	
	/**
	 * Set an attribute filter for graph attributes only (node an edge attributes are not filtered
	 * by this filter). If the filter is null, graph attributes will not be filtered.
	 * @param filter The filter to use, it can be null to disable graph attribute filtering.
	 */
	void setGraphAttributeFilter( AttributeFilter filter );
	
	/**
	 * Set an attribute filter for node attributes only (graph an edge attributes are not filtered
	 * by this filter). If the filter is null, node attributes will not be filtered.
	 * @param filter The filter to use, it can be null to disable node attribute filtering.
	 */
	void setNodeAttributeFilter( AttributeFilter filter );
	
	/**
	 * Set an attribute filter for edge attributes only (graph an node attributes are not filtered
	 * by this filter). If the filter is null, edge attributes will not be filtered.
	 * @param filter The filter to use, it can be null to disable edge attribute filtering.
	 */
	void setEdgeAttributeFilter( AttributeFilter filter );

	/**
	 * The filter for all graph, node and edge attributes. This filter can be null.
	 * @return The global attribute filter or null if there is no global filter.
	 */
	AttributeFilter getGlobalAttributeFilter();
	
	/**
	 * The filter for all graph attributes. This filter can be null.
	 * @return The graph attribute filter or null if there is no graph filter.
	 */
	AttributeFilter getGraphAttributeFilter();
	
	/**
	 * The filter for all node attributes. This filter can be null.
	 * @return The node global attribute filter or null if there is no node filter.
	 */
	AttributeFilter getNodeAttributeFilter();
	
	/**
	 * The filter for all edge attributes. This filter can be null.
	 * @return The edge attribute filter or null of there is no edge filter.
	 */
	AttributeFilter getEdgeAttributeFilter();
}