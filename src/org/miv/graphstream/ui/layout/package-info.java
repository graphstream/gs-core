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



/**
 * Various graph layout algorithms (visual organisation of a graph).
 * 
 * <p>
 * A graph layout algorithm takes as input a graph description and outputs
 * position in the 2D or 3D space for each node of the graph according to some
 * constraints. Most of the time the constraints are that the graph must be
 * easy to visualise, with the less possible edge crossing and node overlapping.
 * </p>
 * 
 * <p>
 * The <tt>Layout</tt> and <tt>LayoutListener</tt> interfaces are the main definition of a layout.
 * They are quite low level. The <tt>elasticbox</tt> is an implementation of these interfaces.
 * </p>
 * 
 * <p>
 * The <tt>LayoutAlgorithm</tt> is the class you need to use if you want to apply
 * a layout to a graph (store x,y,z coordinates in the graph) without opening
 * a graph viewer.
 * </p>
 * 
 * <p>
 * The <tt>LayoutRunner</tt>, <tt>LayoutListenerProxy</tt> are used by the graph viewer to
 * run the layout in a thread and communicate with this thread.
 * </p>
 * 
 * @author Antoine Dutot
 * @author Yoann Pigné
 */
package org.miv.graphstream.algorithm.layout2;