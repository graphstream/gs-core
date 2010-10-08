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

package org.graphstream.ui.graphicGraph.stylesheet;

/**
 * Listener for style events.
 */
public interface StyleSheetListener {
	/**
	 * A style was changed or added. To differentiate the addition of a new
	 * style from a change (augmentation) of an existing style, two values are
	 * passed, the old style if augmented and the new style. The first is set to
	 * null if the style is added. The old style is set to a value if is was
	 * augmented.
	 * 
	 * @param oldRule
	 *            The style that changed.
	 * @param newRule
	 *            The style that was added to the style sheet.
	 */
	void styleAdded(Rule oldRule, Rule newRule);

	/**
	 * The complete style sheet was cleared.
	 */
	void styleSheetCleared();
}