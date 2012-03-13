/*
 * Copyright 2006 - 2012
 *      Stefan Balev    <stefan.balev@graphstream-project.org>
 *      Julien Baudry	<julien.baudry@graphstream-project.org>
 *      Antoine Dutot	<antoine.dutot@graphstream-project.org>
 *      Yoann Pigné	    <yoann.pigne@graphstream-project.org>
 *      Guilhelm Savin	<guilhelm.savin@graphstream-project.org>
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
import java.util.HashSet;

import org.graphstream.graph.Element;
import org.graphstream.graph.implementations.AbstractElement;
import org.graphstream.util.Filter;
import org.graphstream.util.Filters;
import org.junit.Test;

public class TestFilters {
	@Test
	public void orFilter() {
		ToggleFilter<Element> f1, f2;
		Filter<Element> or;

		f1 = new ToggleFilter<Element>();
		f2 = new ToggleFilter<Element>();
		or = Filters.or(f1, f2);

		f1.set(false);
		f2.set(false);

		assertFalse(or.isAvailable(null));

		f1.set(true);
		f2.set(false);

		assertTrue(or.isAvailable(null));

		f1.set(false);
		f2.set(true);

		assertTrue(or.isAvailable(null));

		f1.set(true);
		f2.set(true);

		assertTrue(or.isAvailable(null));
	}

	@Test
	public void andFilter() {
		ToggleFilter<Element> f1, f2;
		Filter<Element> and;

		f1 = new ToggleFilter<Element>();
		f2 = new ToggleFilter<Element>();
		and = Filters.and(f1, f2);

		f1.set(false);
		f2.set(false);

		assertFalse(and.isAvailable(null));

		f1.set(true);
		f2.set(false);

		assertFalse(and.isAvailable(null));

		f1.set(false);
		f2.set(true);

		assertFalse(and.isAvailable(null));

		f1.set(true);
		f2.set(true);

		assertTrue(and.isAvailable(null));
	}

	@Test
	public void xorFilter() {
		ToggleFilter<Element> f1, f2;
		Filter<Element> xor;

		f1 = new ToggleFilter<Element>();
		f2 = new ToggleFilter<Element>();
		xor = Filters.xor(f1, f2);

		f1.set(false);
		f2.set(false);

		assertFalse(xor.isAvailable(null));

		f1.set(true);
		f2.set(false);

		assertTrue(xor.isAvailable(null));

		f1.set(false);
		f2.set(true);

		assertTrue(xor.isAvailable(null));

		f1.set(true);
		f2.set(true);

		assertFalse(xor.isAvailable(null));
	}

	@Test
	public void falseFilter() {
		Filter<Element> f = Filters.falseFilter();
		assertFalse(f.isAvailable(null));
	}

	@Test
	public void trueFilter() {
		Filter<Element> f = Filters.trueFilter();
		assertTrue(f.isAvailable(null));
	}

	@Test
	public void byAttributeFilter() {
		Filter<Element> f = Filters.byAttributeFilter("keyTest", "ok");
		TestElement e = new TestElement("e");
		
		assertFalse(f.isAvailable(e));
		
		e.setAttribute("keyTest", "no");
		assertFalse(f.isAvailable(e));
		
		e.setAttribute("keyTest", "ok");
		assertTrue(f.isAvailable(e));
	}
	
	@Test
	public void byIdFilter() {
		Filter<Element> f = Filters.byIdFilter("A.*");
		Element a1, a2, b1, c2;
		
		a1 = new TestElement("A1");
		a2 = new TestElement("a2");
		b1 = new TestElement("B1");
		c2 = new TestElement("C2");
		
		assertTrue(f.isAvailable(a1));
		assertFalse(f.isAvailable(a2));
		assertFalse(f.isAvailable(b1));
		assertFalse(f.isAvailable(c2));
	}
	
	@Test
	public void isContainedFilter() {
		Collection<Element> elements = new HashSet<Element>();
		Collection<String> elementsId = new HashSet<String>();
		Filter<Element> fObj = Filters.isContained(elements);
		Filter<Element> fId = Filters.isIdContained(elementsId);
		Element a1, a2, a3;
		
		a1 = new TestElement("a1");
		a2 = new TestElement("a2");
		a3 = new TestElement("a3");
		
		elements.add(a1);
		elements.add(a2);
		elementsId.add("a3");
		
		assertTrue(fObj.isAvailable(a1));
		assertTrue(fObj.isAvailable(a2));
		assertFalse(fObj.isAvailable(a3));
		
		assertFalse(fId.isAvailable(a1));
		assertFalse(fId.isAvailable(a2));
		assertTrue(fId.isAvailable(a3));
	}

	static class ToggleFilter<T extends Element> implements Filter<T> {
		boolean flag;

		public void set(boolean on) {
			flag = on;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.util.Filter#isAvailable(org.graphstream.graph.Element
		 * )
		 */
		public boolean isAvailable(T e) {
			return flag;
		}
	}

	static class TestElement extends AbstractElement {
		public TestElement(String id) {
			super(id);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.graph.implementations.AbstractElement#attributeChanged
		 * (java.lang.String, long, java.lang.String,
		 * org.graphstream.graph.implementations
		 * .AbstractElement.AttributeChangeEvent, java.lang.Object,
		 * java.lang.Object)
		 */
		protected void attributeChanged(String sourceId, long timeId,
				String attribute, AttributeChangeEvent event, Object oldValue,
				Object newValue) {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.graph.implementations.AbstractElement#myGraphId()
		 */
		protected String myGraphId() {
			return "test";
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.graph.implementations.AbstractElement#newEvent()
		 */
		protected long newEvent() {
			return 0;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @seeorg.graphstream.graph.implementations.AbstractElement#
		 * nullAttributesAreErrors()
		 */
		protected boolean nullAttributesAreErrors() {
			return false;
		}

	}
}
