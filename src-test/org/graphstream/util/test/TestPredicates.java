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
package org.graphstream.util.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.HashSet;

import org.graphstream.graph.Element;
import org.graphstream.graph.implementations.AbstractElement;
import org.graphstream.util.Predicates;

import org.junit.Test;

public class TestPredicates {
	@Test
	public void xorPredicate() {
		TogglePredicate f1, f2;
		Predicate<Element> xor;

		f1 = new TogglePredicate();
		f2 = new TogglePredicate();
		xor = Predicates.xor(f1, f2);

		f1.set(false);
		f2.set(false);

		assertFalse(xor.test(null));

		f1.set(true);
		f2.set(false);

		assertTrue(xor.test(null));

		f1.set(false);
		f2.set(true);

		assertTrue(xor.test(null));

		f1.set(true);
		f2.set(true);

		assertFalse(xor.test(null));
	}

	@Test
	public void falsePredicate() {
		Predicate<Element> f = Predicates.falsePredicate();
		assertFalse(f.test(null));
	}

	@Test
	public void truePredicate() {
		Predicate<Element> f = Predicates.truePredicate();
		assertTrue(f.test(null));
	}

	@Test
	public void byAttributePredicate() {
		Predicate<Element> f = Predicates.byAttributePredicate("keyTest", "ok");
		TestElement e = new TestElement("e");

		assertFalse(f.test(e));

		e.setAttribute("keyTest", "no");
		assertFalse(f.test(e));

		e.setAttribute("keyTest", "ok");
		assertTrue(f.test(e));
	}

	@Test
	public void byIdPredicate() {
		Predicate<Element> f = Predicates.byIdPredicate("A.*");
		Element a1, a2, b1, c2;

		a1 = new TestElement("A1");
		a2 = new TestElement("a2");
		b1 = new TestElement("B1");
		c2 = new TestElement("C2");

		assertTrue(f.test(a1));
		assertFalse(f.test(a2));
		assertFalse(f.test(b1));
		assertFalse(f.test(c2));
	}

	@Test
	public void isContainedPredicate() {
		Collection<Element> elements = new HashSet<>();
		Collection<String> elementsId = new HashSet<>();
		Predicate<Element> fObj = Predicates.isContained(elements);
		Predicate<Element> fId = Predicates.isIdContained(elementsId);
		Element a1, a2, a3;

		a1 = new TestElement("a1");
		a2 = new TestElement("a2");
		a3 = new TestElement("a3");

		elements.add(a1);
		elements.add(a2);
		elementsId.add("a3");

		assertTrue(fObj.test(a1));
		assertTrue(fObj.test(a2));
		assertFalse(fObj.test(a3));

		assertFalse(fId.test(a1));
		assertFalse(fId.test(a2));
		assertTrue(fId.test(a3));
	}

	static class TogglePredicate implements Predicate<Element> {
		boolean flag;

		public void set(boolean on) {
			flag = on;
		}

		@Override
		public boolean test(Element e) {
			return flag;
		}
	}

	static class TestElement extends AbstractElement {
		public TestElement(String id) {
			super(id);
		}

		@Override
		protected void attributeChanged(AttributeChangeEvent event,
				String attribute, Object oldValue, Object newValue) {
		}

		protected String myGraphId() {
			return "test";
		}

		protected long newEvent() {
			return 0;
		}

		@Override
		protected boolean nullAttributesAreErrors() {
			return false;
		}

	}
}
