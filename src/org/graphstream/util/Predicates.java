/*
 * Copyright 2006 - 2015
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
import java.util.function.Predicate;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;
import org.graphstream.graph.Node;

public class Predicates {
	public static <T> Predicate<T> falsePredicate() {
		return e -> false;
	}

	public static <T> Predicate<T> truePredicate() {
		return e -> true;
	}

	public static <T extends Element> Predicate<T> byAttributePredicate(String key,
		Object expectedValue) {
		return new ByAttributePredicate<>(key, expectedValue);
	}

	public static <T extends Element> Predicate<T> separateNodeAndEdgePredicate(
		Predicate<Node> nodePredicate, Predicate<Edge> edgePredicate) {
		return new SeparateNodeEdgePredicate<>(nodePredicate, edgePredicate);
	}

	public static Predicate<Edge> byExtremitiesPredicate(
		Predicate<Node> f) {
		return new ExtremitiesPredicate(f);
	}

	public static <T extends Element> Predicate<T> byIdPredicate(String idPattern) {
		return new ByIdPredicate<>(idPattern);
	}

	public static <T extends Element> Predicate<T> isContained(
		final Collection<? extends T> set) {
		return (e) -> set.contains(e);
	}

	public static <T extends Element> Predicate<T> isIdContained(
		final Collection<String> set) {
		return (e) -> set.contains(e.getId());
	}

	public static <T extends Element> Predicate<T> xor(Predicate<T> f1, Predicate<T> f2) {
		return new XorPredicate<>(f1, f2);
	}

	static class ByAttributePredicate<T extends Element> implements Predicate<T> {

		protected String key;
		protected Object value;

		ByAttributePredicate(String key, Object value) {
			this.key = key;
			this.value = value;
		}

		@Override
		public boolean test(T e) {
			return e.hasAttribute(key)
				&& (value == null ? e.getAttribute(key) == null : value
					.equals(e.getAttribute(key)));
		}
	}

	static class ByIdPredicate<T extends Element> implements Predicate<T> {
		String nodePattern;
		String edgePattern;

		ByIdPredicate(String pattern) {
			this(pattern, pattern);
		}

		ByIdPredicate(String nodePattern, String edgePattern) {
			this.nodePattern = nodePattern;
			this.edgePattern = edgePattern;
		}

		@Override
		public boolean test(Element e) {
			if (e instanceof Edge) {
				return e.getId().matches(edgePattern);
			}

			return e.getId().matches(nodePattern);
		}
	}

	static class SeparateNodeEdgePredicate<T extends Element> implements Predicate<T> {

		Predicate<? super Node> nodePredicate;
		Predicate<? super Edge> edgePredicate;

		SeparateNodeEdgePredicate(Predicate<Node> nodePredicate, Predicate<Edge> edgePredicate) {
			this.nodePredicate = nodePredicate;
			this.edgePredicate = edgePredicate;
		}

		@Override
		public boolean test(Element e) {
			if (e instanceof Edge) {
				return edgePredicate.test((Edge) e);
			} else if (e instanceof Node) {
				return nodePredicate.test((Node) e);
			}
			throw new UnsupportedOperationException();
		}
	}

	static class ExtremitiesPredicate implements Predicate<Edge> {
		Predicate<? super Node> f;

		ExtremitiesPredicate(Predicate<? super Node> f) {
			this.f = f;
		}

		@Override
		public boolean test(Edge e) {
			return f.test(e.getNode0()) && f.test(e.getNode1());
		}
	}

	static class XorPredicate<T extends Element> implements Predicate<T> {
		Predicate<T> f1, f2;

		XorPredicate(Predicate<T> f1, Predicate<T> f2) {
			this.f1 = f1;
			this.f2 = f2;
		}

		@Override
		public boolean test(T e) {
			return f1.test(e) ^ f2.test(e);
		}
	}
}
