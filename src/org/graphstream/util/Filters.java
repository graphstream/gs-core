/*
 * Copyright 2006 - 2013
 *     Stefan Balev     <stefan.balev@graphstream-project.org>
 *     Julien Baudry    <julien.baudry@graphstream-project.org>
 *     Antoine Dutot    <antoine.dutot@graphstream-project.org>
 *     Yoann Pigné      <yoann.pigne@graphstream-project.org>
 *     Guilhelm Savin   <guilhelm.savin@graphstream-project.org>
 * 
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
package org.graphstream.util;

import java.util.Collection;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;
import org.graphstream.graph.Node;

public class Filters {
	public static <T extends Element> Filter<T> falseFilter() {
		return new Filter<T>() {
			public boolean isAvailable(T e) {
				return false;
			}
		};
	}

	public static <T extends Element> Filter<T> trueFilter() {
		return new Filter<T>() {
			public boolean isAvailable(T e) {
				return true;
			}
		};
	}

	public static <T extends Element> Filter<T> byAttributeFilter(String key,
			Object expectedValue) {
		return new ByAttributeFilter<T>(key, expectedValue);
	}

	public static <T extends Element, U extends Element> Filter<Element> separateNodeAndEdgeFilter(
			Filter<T> nodeFilter, Filter<U> edgeFilter) {
		return new SeparateNodeEdgeFilter<T, U>(nodeFilter, edgeFilter);
	}

	public static <T extends Element, U extends Element> Filter<T> byExtremitiesFilter(
			Filter<U> f) {
		return new ExtremitiesFilter<T, U>(f);
	}

	public static <T extends Element> Filter<T> byIdFilter(String idPattern) {
		return new ByIdFilter<T>(idPattern);
	}

	public static <T extends Element> Filter<T> isContained(
			final Collection<? extends T> set) {
		return new Filter<T>() {
			public boolean isAvailable(T e) {
				return set.contains(e);
			}
		};
	}

	public static <T extends Element> Filter<T> isIdContained(
			final Collection<String> set) {
		return new Filter<T>() {
			public boolean isAvailable(T e) {
				return set.contains(e.getId());
			}
		};
	}

	public static <T extends Element> Filter<T> and(Filter<T> f1, Filter<T> f2) {
		return new AndFilter<T>(f1, f2);
	}

	public static <T extends Element> Filter<T> or(Filter<T> f1, Filter<T> f2) {
		return new OrFilter<T>(f1, f2);
	}

	public static <T extends Element> Filter<T> xor(Filter<T> f1, Filter<T> f2) {
		return new XorFilter<T>(f1, f2);
	}

	public static <T extends Element> Filter<T> not(Filter<T> f) {
		return new NotFilter<T>(f);
	}

	static class ByAttributeFilter<T extends Element> implements Filter<T> {

		protected String key;
		protected Object value;

		ByAttributeFilter(String key, Object value) {
			this.key = key;
			this.value = value;
		}

		public boolean isAvailable(T e) {
			return e.hasAttribute(key)
					&& (value == null ? e.getAttribute(key) == null : value
							.equals(e.getAttribute(key)));
		}
	}

	static class ByIdFilter<T extends Element> implements Filter<T> {
		String nodePattern;
		String edgePattern;

		ByIdFilter(String pattern) {
			this(pattern, pattern);
		}

		ByIdFilter(String nodePattern, String edgePattern) {
			this.nodePattern = nodePattern;
			this.edgePattern = edgePattern;
		}

		public boolean isAvailable(Element e) {
			if (e instanceof Edge)
				return e.getId().matches(edgePattern);

			return e.getId().matches(nodePattern);
		}
	}

	static class SeparateNodeEdgeFilter<T extends Element, U extends Element>
			implements Filter<Element> {

		Filter<T> nodeFilter;
		Filter<U> edgeFilter;

		SeparateNodeEdgeFilter(Filter<T> nodeFilter, Filter<U> edgeFilter) {
			this.nodeFilter = nodeFilter;
			this.edgeFilter = edgeFilter;
		}

		@SuppressWarnings("unchecked")
		public boolean isAvailable(Element e) {
			if (e instanceof Edge)
				return edgeFilter.isAvailable((U) e);

			return nodeFilter.isAvailable((T) e);
		}
	}

	static class ExtremitiesFilter<T extends Element, U extends Element>
			implements Filter<T> {
		Filter<U> f;

		ExtremitiesFilter(Filter<U> f) {
			this.f = f;
		}

		@SuppressWarnings("unchecked")
		public boolean isAvailable(T e) {
			if (e instanceof Node)
				return true;

			return f.isAvailable((U) ((Edge) e).getNode0())
					&& f.isAvailable((U) ((Edge) e).getNode1());
		}
	}

	static class AndFilter<T extends Element> implements Filter<T> {
		Filter<T> f1, f2;

		AndFilter(Filter<T> f1, Filter<T> f2) {
			this.f1 = f1;
			this.f2 = f2;
		}

		public boolean isAvailable(T e) {
			return f1.isAvailable(e) && f2.isAvailable(e);
		}
	}

	static class OrFilter<T extends Element> implements Filter<T> {
		Filter<T> f1, f2;

		OrFilter(Filter<T> f1, Filter<T> f2) {
			this.f1 = f1;
			this.f2 = f2;
		}

		public boolean isAvailable(T e) {
			return f1.isAvailable(e) || f2.isAvailable(e);
		}
	}

	static class XorFilter<T extends Element> implements Filter<T> {
		Filter<T> f1, f2;

		XorFilter(Filter<T> f1, Filter<T> f2) {
			this.f1 = f1;
			this.f2 = f2;
		}

		public boolean isAvailable(T e) {
			return f1.isAvailable(e) ^ f2.isAvailable(e);
		}
	}

	static class NotFilter<T extends Element> implements Filter<T> {
		Filter<T> f;

		NotFilter(Filter<T> f) {
			this.f = f;
		}

		public boolean isAvailable(T e) {
			return !f.isAvailable(e);
		}
	}
}
