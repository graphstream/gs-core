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

package org.graphstream.ui;

import org.graphstream.ui.graphicGraph.stylesheet.Style;

/**
 * Element that travels on the graph.
 * 
 * <p>
 * A sprite represents a data element that is displayed on the graph viewer.
 * </p>
 */
public interface Sprite
{
// Access
	
	/**
	 * The sprite unique identifier.
	 * @return A string.
	 */
	String getId();
	
	/**
	 * The graph viewer remote command.
	 * @return A viewer remote.
	 */
	GraphViewerRemote getViewerRemote();
	
// Commands

	/**
	 * Attach a sprite to a node, all positions will be expressed choosing the node centre as
	 * origin. Therefore the position of the sprite can be seen as an offset from the centre of the
	 * node.
	 * @param nodeId The node identifier.
	 */
	void attachToNode( String nodeId );

	/**
	 * Attach a sprite to an edge, all positions will be expressed "along the edge". The position of
	 * the sprite will be seen as an offset from the source node along the edge. The sprite will
	 * remain on the edge. If {@link #position(float)} is used the value is a number
	 * between 0 and 1 that allows to express an offset from the source node along the edge. 0 means
	 * on the source node, 1 on the target node and values in between a percent of the distance
	 * between the two nodes. If {@link #position(float, float, float)} is used, the
	 * first value is the same as for {@link #position(float)}, the two other values
	 * allows to offset the sprite on a plane whose normal vector is the edge.
	 * @param edgeId The edge id.
	 */
	void attachToEdge( String edgeId );

	/**
	 * Detach a sprite from the node or edge it was attached to.
	 */
	void detach();

	/**
	 * Position a sprite along an edge. This method is dedicated to the positioning of a sprite
	 * along an edge. The value is a percent of the distance between the source and target node. 0
	 * means the sprite is on the source node and 1 means the sprite is on the target node. In
	 * between values indicate a percent of the distance.
	 * @param percent The percent of the distance between the two nodes of the edge.
	 */
	void position( float percent );

	/**
	 * Position a sprite in the graph, node or edge space.
	 * 
	 * <p>
	 * The positionning of sprites depends on what it is attached. Two coordinate systems are used.
	 * In cartesian coordinate, the X axis goes positive left to right, the Y axis goes positive
	 * bottom to top and the Z axis goes positive back to front (right-handed). The units are in
	 * "graph space" or "graph units". This means that the measurement used maps the units you
	 * choose when you assign a position to nodes (you can however choose to use pixels or percents
	 * as in graph style sheets).
	 * </p>
	 * 
	 * <p>
	 * If the sprite is not attached the coordinates are in graph space using cartesian coordinates.
	 * In this case, the sprite is positioned somewhere in the space of the graph, like nodes and
	 * edges. This is useful to put an informative label or icon somewhere on the display.
	 * </p>
	 * 
	 * <p>
	 * If the sprite is attached to a node, the coordinates express and offset from the node centre
	 * using spherical coordinates. The x value is a radius from the node centre used as origin for
	 * the coordinate system. This radius is given in graph units by default. The y value is a theta
	 * angle that
	 * express a rotation in radians around the cartesian Y axis. The z value is a phi angle that
	 * express a rotation in radians around the cartesian Z axis. Therefore to make a sprite orbit
	 * around a node in two dimensions, you only need to change the x and z coordinates, y has no
	 * meaning.
	 * </p>
	 * 
	 * <p>
	 * If the sprite is attached to an edge, the first coordinate must be in [0..1] and indicates a
	 * percent of the distance between the source and target node of the edge. If the two other
	 * coordinates are zero, the sprite will remain along the edge. Else they express a position on
	 * a plane that is perpendicular to the edge using polar coordinates. That is the edge is the
	 * normal vector of this plane. The y is a radius from a point on the edge defined by the x
	 * coordinate and the z is a theta angle around the axis defined by the edge. In two dimensions
	 * only x and y have a meaning. In this case also, y represents a radius, and can be negative to
	 * put the sprite on a side or another of the edge.
	 * </p>
	 * 
	 * <p>
	 * The units parameter allows to specify how lengths are measured. This has no effect on
	 * x when the sprite is attached to an edge or on angles (y and z, angles are always in radians)
	 * when the sprite is attached to a node. For non-attached sprites and for radii, the units
	 * allows to specify how the length must be interpreted (in fixed pixels, in graph units or
	 * in percents of the view width).
	 * </p>
	 * 
	 * @param x First coordinate.
	 * @param y Second coordinate.
	 * @param z Third coordinate.
	 * @param units for measuring length.
	 */
	void position( float x, float y, float z, Style.Units units );

	/**
	 * Like {@link #position(float, float, float, org.graphstream.ui.graphicGraph.stylesheet.Style.Units)}
	 * but consider the units are unchanged. By default the units are graph units.
	 * @param x First coordinate.
	 * @param y Second coordinate.
	 * @param z Third coordinate.
	 */
	void position( float x, float y, float z );
	
	/**
	 * Add or change an attribute on a sprite. You can pass as many values as
	 * you want. If there is no value, the attribute will be a boolean with value "true".
	 * If there are several values, the attribute will be an array of these values. Else
	 * the attribute is the value (but this is not an array).
	 * @param attribute The attribute name.
	 * @param values The attribute value or sequence of values.
	 */
	void addAttribute( String attribute, Object ... values );
	
	/**
	 * Like {@link #addAttribute(String, Object...)}.
	 */
	void setAttribute( String attribute, Object ... values );
	
	/**
	 * Like {@link #addAttribute(String, Object...)}.
	 */
	void changeAttribute( String attribute, Object ... values );
	
	/**
	 * Remove an attribute from a sprite.
	 * @param attribute The attribute name.
	 */
	void removeAttribute( String attribute );
}