/*
 * This file is part of GraphStream <http://graphstream-project.org>.
 * 
 * GraphStream is a library whose purpose is to handle static or dynamic
 * graph, create them from scratch, file or any source and display them.
 * 
 * This program is free software distributed under the terms of two licenses, the
 * CeCILL-C license that fits European law, and the GNU Lesser General Public
 * License. You can  use, modify and/ or redistribute the software under the terms
 * of the CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
 * URL <http://www.cecill.info> or under the terms of the GNU LGPL as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C and LGPL licenses and that you accept their terms.
 */

/**
 * @since 2009-07-05
 * 
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Antoine Dutot <antoine.dutot@graphstream-project.org>
 */
package org.graphstream.ui.graphicGraph.stylesheet;

import java.util.HashSet;

/**
 * Style application rule.
 * 
 * <p>
 * A rule is made of a selector and values. The selector identifies the
 * element(s) this rule applies to, and the values are styles to apply to the
 * matched elements.
 * </p>
 */
public class Rule {
	// Attributes

	/**
	 * The match.
	 */
	public Selector selector;

	/**
	 * The style.
	 */
	public Style style;

	/**
	 * Optionally, the rule can store all the style groups it participates in.
	 */
	public HashSet<String> groups;

	// Constructors

	protected Rule() {
	}

	/**
	 * New rule with a matcher.
	 * 
	 * @param selector
	 *            The rule selector.
	 */
	public Rule(Selector selector) {
		this.selector = selector;
	}

	public Rule(Selector selector, Rule parent) {
		this.selector = selector;
		this.style = new Style(parent);
	}

	/**
	 * This rule style.
	 * 
	 * @return The rule style.
	 */
	public Style getStyle() {
		return style;
	}

	/**
	 * The group this rule participate in, maybe null if the rule does not
	 * participate in any group.
	 * 
	 * @return The group set or null.
	 */
	public HashSet<String> getGroups() {
		return groups;
	}

	/**
	 * True if this rule selector match the given identifier.
	 * 
	 * @param identifier
	 *            The identifier to test for the match.
	 * @return True if matching.
	 */
	public boolean matchId(String identifier) {
		String ident = selector.getId();

		if (ident != null)
			return ident.equals(identifier);

		return false;
	}

	/**
	 * Change the style.
	 * 
	 * @param style
	 *            A style specification.
	 */
	public void setStyle(Style style) {
		this.style = style;
	}

	/**
	 * Specify that this rule participates in the given style group.
	 * 
	 * @param groupId
	 *            The group unique identifier.
	 */
	public void addGroup(String groupId) {
		if (groups == null)
			groups = new HashSet<String>();
		groups.add(groupId);
	}

	/**
	 * Remove this rule from the style group.
	 * 
	 * @param groupId
	 *            The group unique identifier.
	 */
	public void removeGroup(String groupId) {
		if (groups != null)
			groups.remove(groupId);
	}

	@Override
	public String toString() {
		return toString(-1);
	}

	public String toString(int level) {
		StringBuilder builder = new StringBuilder();
		String prefix = "";

		if (level > 0) {
			for (int i = 0; i < level; i++)
				prefix += "    ";
		}

		builder.append(prefix);
		builder.append(selector.toString());
		builder.append(style.toString(level + 1));

		return builder.toString();
	}
}