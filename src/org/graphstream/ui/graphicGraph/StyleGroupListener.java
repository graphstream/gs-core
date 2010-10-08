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

package org.graphstream.ui.graphicGraph;

import org.graphstream.graph.Element;

/**
 * Listen at the changes in the style group set.
 * 
 * @author Antoine Dutot
 */
public interface StyleGroupListener {
	/**
	 * The style of the element changed.
	 * 
	 * @param element
	 *            The element.
	 * @param oldStyle
	 *            The old style.
	 * @param style
	 *            The changed style or the new style of the element.
	 */
	void elementStyleChanged(Element element, StyleGroup oldStyle,
			StyleGroup style);
}