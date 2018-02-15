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
 * @since 2013-07-31
 * 
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 */
package org.graphstream.util.cumulative;

import java.util.Collections;
import java.util.HashMap;

import org.graphstream.util.cumulative.CumulativeSpells.Spell;

public class CumulativeAttributes {
	boolean nullAttributesAreErrors;
	HashMap<String, CumulativeSpells> data;
	double date;

	public CumulativeAttributes(double date) {
		data = new HashMap<String, CumulativeSpells>();
	}

	public Object get(String key) {
		CumulativeSpells o = data.get(key);

		if (o != null) {
			Spell s = o.getCurrentSpell();
			return s == null ? null : s.getAttachedData();
		}

		return null;
	}

	public Object getAny(String key) {
		CumulativeSpells o = data.get(key);

		if (o != null) {
			Spell s = o.getSpell(0);
			return s == null ? null : s.getAttachedData();
		}

		return null;
	}

	public Iterable<String> getAttributes() {
		return data.keySet();
	}

	@SuppressWarnings("unchecked")
	public Iterable<Spell> getAttributeSpells(String key) {
		CumulativeSpells o = data.get(key);

		if (o != null)
			return Collections.unmodifiableList(o.spells);

		return Collections.EMPTY_LIST;
	}

	public int getAttributesCount() {
		return data.size();
	}

	public void set(String key, Object value) {
		CumulativeSpells spells = data.get(key);

		if (spells == null) {
			spells = new CumulativeSpells();
			data.put(key, spells);
		}

		Spell s = spells.closeSpell();

		if (s != null)
			s.setEndOpen(true);

		s = spells.startSpell(date);
		s.setAttachedData(value);
	}

	public void remove(String key) {
		CumulativeSpells spells = data.get(key);

		if (spells == null)
			return;

		spells.closeSpell();
	}

	public void remove() {
		for (CumulativeSpells spells : data.values())
			spells.closeSpell();
	}

	public void updateDate(double date) {
		this.date = date;

		for (CumulativeSpells spells : data.values())
			spells.updateCurrentSpell(date);
	}

	public String toString() {
		StringBuilder buffer = new StringBuilder();

		buffer.append("(");

		for (String key : data.keySet()) {
			buffer.append(key).append(":").append(data.get(key));
		}

		buffer.append(")");

		return buffer.toString();
	}
}
