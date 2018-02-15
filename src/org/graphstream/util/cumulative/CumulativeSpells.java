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
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.util.cumulative;

import java.util.LinkedList;

public class CumulativeSpells {
	public static class Spell {
		private double start;
		private double end;

		private boolean startOpen;
		private boolean endOpen;

		private boolean closed;

		private Object data;

		public Spell(double start, boolean startOpen, double end, boolean endOpen) {
			this.start = start;
			this.startOpen = startOpen;
			this.end = end;
			this.endOpen = endOpen;

			this.closed = false;
		}

		public Spell(double start, double end) {
			this(start, false, end, false);
		}

		public Spell(double start) {
			this(start, false, start, false);
		}

		public double getStartDate() {
			return start;
		}

		public double getEndDate() {
			return end;
		}

		public boolean isStartOpen() {
			return startOpen;
		}

		public boolean isEndOpen() {
			return endOpen;
		}

		public boolean isStarted() {
			return !Double.isNaN(start);
		}

		public boolean isEnded() {
			return closed;
		}

		public void setStartOpen(boolean open) {
			startOpen = open;
		}

		public void setEndOpen(boolean open) {
			endOpen = open;
		}

		public Object getAttachedData() {
			return data;
		}

		public void setAttachedData(Object data) {
			this.data = data;
		}

		public String toString() {
			String str = "";

			if (isStarted()) {
				str += isStartOpen() ? "]" : "[";
				str += start + "; ";
			} else
				str += "[...; ";

			if (isEnded()) {
				str += end;
				str += isEndOpen() ? "[" : "]";
			} else
				str += "...]";

			return str;
		}
	}

	LinkedList<Spell> spells;
	double currentDate;

	public CumulativeSpells() {
		this.spells = new LinkedList<Spell>();
		currentDate = Double.NaN;
	}

	public Spell startSpell(double date) {
		Spell s = new Spell(date);
		spells.add(s);

		return s;
	}

	public void updateCurrentSpell(double date) {
		if (spells.size() > 0 && !Double.isNaN(currentDate)) {
			Spell s = spells.getLast();

			if (!s.closed)
				s.end = currentDate;
		}

		currentDate = date;
	}

	public Spell closeSpell() {
		if (spells.size() > 0) {
			Spell s = spells.getLast();

			if (!s.closed) {
				s.closed = true;
				return s;
			}
		}

		return null;
	}

	public Spell getCurrentSpell() {
		Spell s = spells.getLast();

		if (s == null)
			return null;

		return s.closed ? null : s;
	}

	public Spell getSpell(int i) {
		return spells.get(i);
	}

	public int getSpellCount() {
		return spells.size();
	}

	public Spell getOrCreateSpell(double date) {
		Spell s = getCurrentSpell();

		if (s == null)
			s = startSpell(date);

		return s;
	}

	public boolean isEternal() {
		return spells.size() == 1 && !spells.get(0).isStarted() && !spells.get(0).isEnded();
	}

	public String toString() {
		StringBuilder buffer = new StringBuilder();

		buffer.append("{");

		for (int i = 0; i < spells.size(); i++) {
			if (i > 0)
				buffer.append(", ");

			buffer.append(spells.get(i).toString());
		}

		buffer.append("}");

		return buffer.toString();
	}
}
