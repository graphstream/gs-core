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
 * Implementation of the {@link org.miv.graphstream.ui.GraphViewer} using Swing and
 * the Java2D API.
 * 
 * <p>
 * This implementation is the reference implementation. It respects the full style sheet
 * specification and provides a sprite renderer.
 * </p>
 * 
 * TODO: this renderer is not really object-oriented. The rendering of edges and
 *  node should be in the edge and node classes. For edge arrow there should exist
 *  an arrow class, etc. Maybe for the next release...
 * 
 * @author Antoine Dutot
 * @author Yoann Pigné
 */
package org.miv.graphstream.ui.swing;