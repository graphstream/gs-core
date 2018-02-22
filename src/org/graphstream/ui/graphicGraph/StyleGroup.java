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
 * @author Antoine Dutot <antoine.dutot@graphstream-project.org>
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.ui.graphicGraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.graphstream.graph.Element;
import org.graphstream.ui.graphicGraph.GraphicElement.SwingElementRenderer;
import org.graphstream.ui.graphicGraph.stylesheet.Rule;
import org.graphstream.ui.graphicGraph.stylesheet.Selector;
import org.graphstream.ui.graphicGraph.stylesheet.Style;

/**
 * A group of graph elements that share the same style.
 * 
 * <p>
 * The purpose of a style group is to allow retrieving all elements with the
 * same style easily. Most of the time, with graphic engines, pushing the
 * graphic state (the style, colors, line width, textures, gradients) is a
 * costly operation. Doing it once for several elements can speed up things a
 * lot. This is the purpose of the style group.
 * </p>
 * 
 * <p>
 * The action of drawing elements in group (first push style, then draw all
 * elements) are called bulk drawing. All elements that can be drawn at once
 * this way are called bulk elements.
 * </p>
 * 
 * <p>
 * In a style group it is not always possible do draw elements in a such a
 * "bulk" operation. If the style contains "dynamic values" for example, that is
 * value that depend on the value of an attribute stored on the element, or if
 * the element is modified by an event (clicked, selected), the element will not
 * be drawn the same as others.
 * </p>
 * 
 * <p>
 * The style group provides iterators on each of these categories of elements :
 * <ul>
 * <li>{@link #elements()} allows to browse all elements contained in the group
 * without exception.</li>
 * <li>{@link #dynamicElements()} allows to browse the subset of elements having
 * a attribute that modify their style.</li>
 * <li>{@link #elementsEvents()} allows to browse the subset of elements
 * modified by an event.</li>
 * <li>{@link #bulkElements()} allows to browse all remaining elements that have
 * no dynamic attribute or event.</li>
 * </ul>
 * The calling the three last iterators would yield the same elements as calling
 * the first one. When drawing you can optimise the drawing by first pushing the
 * graphic state and then drawing at once all bulk elements. If the dynamic and
 * event subsets are not empty you then must draw such elements modifying the
 * graphic state for each one.
 * </p>
 */
public class StyleGroup extends Style implements Iterable<Element> {
	// Attribute

	/**
	 * The group unique identifier.
	 */
	protected String id;

	/**
	 * The set of style rules.
	 */
	protected ArrayList<Rule> rules = new ArrayList<Rule>();

	/**
	 * Graph elements of this group.
	 */
	protected HashMap<String, Element> elements = new HashMap<String, Element>();

	/**
	 * The global events actually occurring.
	 */
	protected StyleGroupSet.EventSet eventSet;

	/**
	 * Set of elements whose style is actually modified individually by an event.
	 * Such elements must be rendered one by one, not in groups like others.
	 */
	protected HashMap<Element, ElementEvents> eventsFor;

	/**
	 * Set of elements that have some dynamic style values. Such elements must be
	 * rendered one by one, not in groups, like others.
	 */
	protected HashSet<Element> dynamicOnes;

	/**
	 * A set of events actually pushed only for this group.
	 */
	protected String[] curEvents;

	/**
	 * The set of bulk elements.
	 */
	protected BulkElements bulkElements = new BulkElements();

	/**
	 * Associated renderers.
	 */
	public HashMap<String, SwingElementRenderer> renderers;

	// Construction

	/**
	 * New style group for a first graph element and the set of style rules that
	 * matches it. More graph elements can be added later.
	 * 
	 * @param identifier
	 *            The unique group identifier (see
	 *            {@link org.graphstream.ui.graphicGraph.stylesheet.StyleSheet#getStyleGroupIdFor(Element, ArrayList)}
	 *            ).
	 * @param rules
	 *            The set of style rules for the style group (see
	 *            {@link org.graphstream.ui.graphicGraph.stylesheet.StyleSheet#getRulesFor(Element)}
	 *            ).
	 * @param firstElement
	 *            The first element to construct the group.
	 */
	public StyleGroup(String identifier, Collection<Rule> rules, Element firstElement,
			StyleGroupSet.EventSet eventSet) {
		this.id = identifier;
		this.rules.addAll(rules);
		this.elements.put(firstElement.getId(), firstElement);
		this.values = null; // To avoid consume memory since this style will not
							// store anything.
		this.eventSet = eventSet;

		for (Rule rule : rules)
			rule.addGroup(identifier);
	}

	// Access

	/**
	 * The group unique identifier.
	 * 
	 * @return A style group identifier.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Type of graph element concerned by this style (node, edge, sprite, graph).
	 * 
	 * @return The type of the style group elements.
	 */
	public Selector.Type getType() {
		return rules.get(0).selector.type;
	}

	/**
	 * True if at least one of the style properties is dynamic (set according to an
	 * attribute of the element to draw). Such elements cannot therefore be drawn in
	 * a group operation, but one by one.
	 * 
	 * @return True if one property is dynamic.
	 */
	public boolean hasDynamicElements() {
		return (dynamicOnes != null && dynamicOnes.size() > 0);
	}

	/**
	 * If true this group contains some elements that are actually changed by an
	 * event. Such elements cannot therefore be drawn in a group operation, but one
	 * by one.
	 * 
	 * @return True if the group contains some elements changed by an event.
	 */
	public boolean hasEventElements() {
		return (eventsFor != null && eventsFor.size() > 0);
	}

	/**
	 * True if the given element actually has active events.
	 * 
	 * @param element
	 *            The element to test.
	 * @return True if the element has actually active events.
	 */
	public boolean elementHasEvents(Element element) {
		return (eventsFor != null && eventsFor.containsKey(element));
	}

	/**
	 * True if the given element has dynamic style values provided by specific
	 * attributes.
	 * 
	 * @param element
	 *            The element to test.
	 * @return True if the element has actually specific style attributes.
	 */
	public boolean elementIsDynamic(Element element) {
		return (dynamicOnes != null && dynamicOnes.contains(element));
	}

	/**
	 * Get the value of a given property.
	 * 
	 * This is a redefinition of the method in {@link Style} to consider the fact a
	 * style group aggregates several style rules.
	 * 
	 * @param property
	 *            The style property the value is searched for.
	 */
	@Override
	public Object getValue(String property, String... events) {
		int n = rules.size();

		if (events == null || events.length == 0) {
			if (curEvents != null && curEvents.length > 0) {
				events = curEvents;
			} else if (eventSet.events != null && eventSet.events.length > 0) {
				events = eventSet.events;
			}
		}

		for (int i = 1; i < n; i++) {
			Style style = rules.get(i).getStyle();

			if (style.hasValue(property, events))
				return style.getValue(property, events);
		}

		return rules.get(0).getStyle().getValue(property, events);
	}

	/**
	 * True if there are no elements in the group.
	 * 
	 * @return True if the group is empty of elements.
	 */
	public boolean isEmpty() {
		return elements.isEmpty();
	}

	/**
	 * True if the group contains the element whose identifier is given.
	 * 
	 * @param elementId
	 *            The element to search.
	 * @return true if the element is in the group.
	 */
	public boolean contains(String elementId) {
		return elements.containsKey(elementId);
	}

	/**
	 * True if the group contains the element given.
	 * 
	 * @param element
	 *            The element to search.
	 * @return true if the element is in the group.
	 */
	public boolean contains(Element element) {
		return elements.containsKey(element.getId());
	}

	/**
	 * Return an element of the group, knowing its identifier.
	 * 
	 * @param id
	 *            The searched element identifier.
	 * @return The element corresponding to the identifier or null if not found.
	 */
	public Element getElement(String id) {
		return elements.get(id);
	}

	/**
	 * The number of elements of the group.
	 * 
	 * @return The element count.
	 */
	public int getElementCount() {
		return elements.size();
	}

	/**
	 * Iterator on the set of graph elements of this group.
	 * 
	 * @return The elements iterator.
	 */
	public Iterator<? extends Element> getElementIterator() {
		return elements.values().iterator();
	}

	/**
	 * Iterable set of elements. This the complete set of elements contained in this
	 * group without regard to the fact they are modified by an event or are
	 * dynamic. If you plan to respect events or dynamic elements, you must check
	 * the elements are not modified by events using
	 * {@link #elementHasEvents(Element)} and are not dynamic by using
	 * {@link #elementIsDynamic(Element)} and then draw modified elements using
	 * {@link #elementsEvents()} and {@link #dynamicElements()}. But the easiest way
	 * of drawing is to use first {@link #bulkElements()} for all non dynamic non
	 * event elements, then the {@link #dynamicElements()} and
	 * {@link #elementsEvents()} to draw all dynamic and event elements.
	 * 
	 * @return All the elements in no particular order.
	 */
	public Iterable<? extends Element> elements() {
		return elements.values();
	}

	/**
	 * Iterable set of elements that can be drawn in a bulk operation, that is the
	 * subset of all elements that are not dynamic or modified by an event.
	 * 
	 * @return The iterable set of bulk elements.
	 */
	public Iterable<? extends Element> bulkElements() {
		return bulkElements;
	}

	/**
	 * Subset of elements that are actually modified by one or more events. The
	 * {@link ElementEvents} class contains the element and an array of events that
	 * can be pushed on the style group set.
	 * 
	 * @return The subset of elements modified by one or more events.
	 */
	public Iterable<ElementEvents> elementsEvents() {
		return eventsFor.values();
	}

	/**
	 * Subset of elements that have dynamic style values and therefore must be
	 * rendered one by one, not in groups like others. Even though elements style
	 * can specify some dynamics, the elements must individually have attributes
	 * that specify the dynamic value. If the elements do not have these attributes
	 * they can be rendered in bulk operations.
	 * 
	 * @return The subset of dynamic elements of the group.
	 */
	public Iterable<Element> dynamicElements() {
		return dynamicOnes;
	}

	public Iterator<Element> iterator() {
		return elements.values().iterator();
	}

	/**
	 * The associated renderers.
	 * 
	 * @return A renderer or null if not found.
	 */
	public SwingElementRenderer getRenderer(String id) {
		if (renderers != null)
			return renderers.get(id);

		return null;
	}

	/**
	 * Set of events for a given element or null if the element has not currently
	 * occurring events.
	 * 
	 * @return A set of events or null if none occurring at that time.
	 */
	public ElementEvents getEventsFor(Element element) {
		if (eventsFor != null)
			return eventsFor.get(element);

		return null;
	}

	/**
	 * Test if an element is pushed as dynamic.
	 */
	public boolean isElementDynamic(Element element) {
		if (dynamicOnes != null)
			return dynamicOnes.contains(element);

		return false;
	}

	// Command

	/**
	 * Add a new graph element to the group.
	 * 
	 * @param element
	 *            The new graph element to add.
	 */
	public void addElement(Element element) {
		elements.put(element.getId(), element);
	}

	/**
	 * Remove a graph element from the group.
	 * 
	 * @param element
	 *            The element to remove.
	 * @return The removed element, or null if the element was not found.
	 */
	public Element removeElement(Element element) {
		if (eventsFor != null && eventsFor.containsKey(element))
			eventsFor.remove(element); // Remove an eventual remaining event.

		if (dynamicOnes != null && dynamicOnes.contains(element))
			dynamicOnes.remove(element); // Remove an eventual remaining dynamic
											// information.

		return elements.remove(element.getId());
	}

	/**
	 * Push an event specifically for the given element. Events are stacked in
	 * order. Called by the GraphicElement.
	 * 
	 * @param element
	 *            The element to modify with an event.
	 * @param event
	 *            The event to push.
	 */
	protected void pushEventFor(Element element, String event) {
		if (elements.containsKey(element.getId())) {
			if (eventsFor == null)
				eventsFor = new HashMap<Element, ElementEvents>();

			ElementEvents evs = eventsFor.get(element);

			if (evs == null) {
				evs = new ElementEvents(element, this, event);
				eventsFor.put(element, evs);
			} else {
				evs.pushEvent(event);
			}
		}
	}

	/**
	 * Pop an event for the given element. Called by the GraphicElement.
	 * 
	 * @param element
	 *            The element.
	 * @param event
	 *            The event.
	 */
	protected void popEventFor(Element element, String event) {
		if (elements.containsKey(element.getId())) {
			if ( eventsFor != null ) {
				ElementEvents evs = eventsFor.get(element);
				
				if (evs != null) {
					evs.popEvent(event);

					if (evs.eventCount() == 0)
						eventsFor.remove(element);
				}

				if (eventsFor.isEmpty())
					eventsFor = null;
	
			}
		}
	}

	/**
	 * Before drawing an element that has events, use this method to activate the
	 * events, the style values will be modified accordingly. Events for this
	 * element must have been registered via {@link #pushEventFor(Element, String)}.
	 * After rendering the {@link #deactivateEvents()} MUST be called.
	 * 
	 * @param element
	 *            The element to push events for.
	 */
	public void activateEventsFor(Element element) {
		ElementEvents evs = eventsFor.get(element);

		if (evs != null && curEvents == null)
			curEvents = evs.events();
	}

	/**
	 * De-activate any events activated for an element. This method MUST be called
	 * if {@link #activateEventsFor(Element)} has been called.
	 */
	public void deactivateEvents() {
		curEvents = null;
	}

	/**
	 * Indicate the element has dynamic values and thus cannot be drawn in bulk
	 * operations. Called by the GraphicElement.
	 * 
	 * @param element
	 *            The element.
	 */
	protected void pushElementAsDynamic(Element element) {
		if (dynamicOnes == null)
			dynamicOnes = new HashSet<Element>();

		dynamicOnes.add(element);
	}

	/**
	 * Indicate the element has no more dynamic values and can be drawn in bulk
	 * operations. Called by the GraphicElement.
	 * 
	 * @param element
	 *            The element.
	 */
	protected void popElementAsDynamic(Element element) {
		dynamicOnes.remove(element);

		if (dynamicOnes.isEmpty())
			dynamicOnes = null;
	}

	/**
	 * Remove all graph elements of this group, and remove this group from the group
	 * list of each style rule.
	 */
	public void release() {
		for (Rule rule : rules)
			rule.removeGroup(id);

		elements.clear();
	}

	/**
	 * Redefinition of the {@link Style} to forbid changing the values.
	 */
	@Override
	public void setValue(String property, Object value) {
		throw new RuntimeException("you cannot change the values of a style group.");
	}

	/**
	 * Add a renderer to this group.
	 * 
	 * @param id
	 *            The renderer identifier.
	 * @param renderer
	 *            The renderer.
	 */
	public void addRenderer(String id, SwingElementRenderer renderer) {
		if (renderers == null)
			renderers = new HashMap<String, SwingElementRenderer>();

		renderers.put(id, renderer);
	}

	/**
	 * Remove a renderer.
	 * 
	 * @param id
	 *            The renderer identifier.
	 * @return The removed renderer or null if not found.
	 */
	public SwingElementRenderer removeRenderer(String id) {
		return renderers.remove(id);
	}

	@Override
	public String toString() {
		return toString(-1);
	}

	@Override
	public String toString(int level) {
		StringBuilder builder = new StringBuilder();
		String prefix = "";
		String sprefix = "    ";

		for (int i = 0; i < level; i++)
			prefix += sprefix;

		builder.append(String.format("%s%s%n", prefix, id));
		builder.append(String.format("%s%sContains : ", prefix, sprefix));

		for (Element element : elements.values()) {
			builder.append(String.format("%s ", element.getId()));
		}

		builder.append(String.format("%n%s%sStyle : ", prefix, sprefix));

		for (Rule rule : rules) {
			builder.append(String.format("%s ", rule.selector.toString()));
		}

		builder.append(String.format("%n"));

		return builder.toString();
	}

	// Nested classes

	/**
	 * Description of an element that is actually modified by one or more events
	 * occurring on it.
	 */
	public static class ElementEvents {
		// Attribute

		/**
		 * Set of events on the element.
		 */
		protected String events[];

		/**
		 * The element.
		 */
		protected Element element;

		/**
		 * The group the element pertains to.
		 */
		protected StyleGroup group;

		// Construction

		protected ElementEvents(Element element, StyleGroup group, String event) {
			this.element = element;
			this.group = group;
			this.events = new String[1];

			events[0] = event;
		}

		// Access

		/**
		 * The element on which the events are occurring.
		 * 
		 * @return an element.
		 */
		public Element getElement() {
			return element;
		}

		/**
		 * Number of events actually affecting the element.
		 * 
		 * @return The number of events affecting the element.
		 */
		public int eventCount() {
			if (events == null)
				return 0;

			return events.length;
		}

		/**
		 * The set of events actually occurring on the element.
		 * 
		 * @return A set of strings.
		 */
		public String[] events() {
			return events;
		}

		// Command

		public void activate() {
			group.activateEventsFor(element);
		}

		public void deactivate() {
			group.deactivateEvents();
		}

		protected void pushEvent(String event) {
			int n = events.length + 1;
			String e[] = new String[n];
			boolean found = false;

			for (int i = 0; i < events.length; i++) {
				if (!events[i].equals(event))
					e[i] = events[i];
				else
					found = true;
			}

			e[events.length] = event;

			if (!found)
				events = e;
		}

		protected void popEvent(String event) {
			if (events.length > 1) {
				String e[] = new String[events.length - 1];
				boolean found = false;

				for (int i = 0, j = 0; i < events.length; i++) {
					if (!events[i].equals(event)) {
						if (j < e.length) {
							e[j++] = events[i];
						}
					} else {
						found = true;
					}
				}

				if (found)
					events = e;
			} else {
				if (events[0].equals(event)) {
					events = null;
				}
			}
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();

			builder.append(String.format("%s events {", element.getId()));
			for (String event : events)
				builder.append(String.format(" %s", event));
			builder.append(" }");

			return builder.toString();
		}
	}

	/**
	 * Virtual set on the elements that have not dynamic style value or event.
	 */
	protected class BulkElements implements Iterable<Element> {
		public Iterator<Element> iterator() {
			return new BulkIterator(elements.values().iterator());
		}
	}

	/**
	 * Iterator on the set of elements that have no event or dynamic style values.
	 */
	protected class BulkIterator implements Iterator<Element> {
		/**
		 * Iterator on the set of all elements.
		 */
		protected Iterator<Element> iterator;

		/**
		 * The next element without event or dynamic style.value.
		 */
		Element next;

		/**
		 * New bulk iterator positioned on the first element with no event or dynamic
		 * style attribute.
		 * 
		 * @param iterator
		 *            Iterator on the set of all elements.
		 */
		public BulkIterator(Iterator<Element> iterator) {
			this.iterator = iterator;
			boolean loop = true;

			while (loop && iterator.hasNext()) {
				next = iterator.next();

				if (!elementHasEvents(next) && !elementIsDynamic(next))
					loop = false;
				else
					next = null;
			}
		}

		public boolean hasNext() {
			return (next != null);
		}

		public Element next() {
			Element e = next;
			boolean loop = true;

			next = null;

			while (loop && iterator.hasNext()) {
				next = iterator.next();

				if (!elementIsDynamic(next) && !elementHasEvents(next))
					loop = false;
				else
					next = null;
			}

			return e;
		}

		public void remove() {
			throw new UnsupportedOperationException("this iterator does not allows removing elements");
		}
	}
}