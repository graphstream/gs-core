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
 * @author Yoann Pigné <yoann.pigne@graphstream-project.org>
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Alex Bowen <bowen.a@gmail.com>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.ui.graphicGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.graphicGraph.stylesheet.Rule;
import org.graphstream.ui.graphicGraph.stylesheet.Selector;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.ShadowMode;
import org.graphstream.ui.graphicGraph.stylesheet.StyleSheet;
import org.graphstream.ui.graphicGraph.stylesheet.StyleSheetListener;

/**
 * A set of style groups.
 * 
 * <p>
 * This class is in charge or storing all the style groups and to update them.
 * Each time an element is added or removed the groups are updated. Each time
 * the style sheet changes the groups are updated.
 * </p>
 * 
 * @author Antoine Dutot
 */
public class StyleGroupSet implements StyleSheetListener {
	// Attribute

	/**
	 * The style sheet.
	 */
	protected StyleSheet stylesheet;

	/**
	 * All the groups indexed by their unique identifier.
	 */
	protected final Map<String, StyleGroup> groups = new TreeMap<String, StyleGroup>();

	/**
	 * Allows to retrieve the group containing a node knowing the node id.
	 */
	protected final Map<String, String> byNodeIdGroups = new TreeMap<String, String>();

	/**
	 * Allows to retrieve the group containing an edge knowing the node id.
	 */
	protected final Map<String, String> byEdgeIdGroups = new TreeMap<String, String>();

	/**
	 * Allows to retrieve the group containing a sprite knowing the node id.
	 */
	protected final Map<String, String> bySpriteIdGroups = new TreeMap<String, String>();

	/**
	 * Allows to retrieve the group containing a graph knowing the node id.
	 */
	protected final Map<String, String> byGraphIdGroups = new TreeMap<String, String>();

	/**
	 * Virtual set of nodes. This set provides fake methods to make it appear as a
	 * set of nodes whereas it only maps on the node style groups.
	 */
	protected NodeSet nodeSet = new NodeSet();

	/**
	 * Virtual set of edges. This set provides fake methods to make it appear as a
	 * set of edges whereas it only maps on the edge style groups.
	 */
	protected EdgeSet edgeSet = new EdgeSet();

	/**
	 * Virtual set of sprites. This set provides fake methods to make it appear as a
	 * set of sprites whereas it only maps on the sprite style groups.
	 */
	protected SpriteSet spriteSet = new SpriteSet();

	/**
	 * Virtual set of graphs. This set provides fake methods to make it appear as a
	 * set of graphs whereas it only maps on the graph style groups.
	 */
	protected GraphSet graphSet = new GraphSet();

	/**
	 * The set of events actually occurring.
	 */
	protected EventSet eventSet = new EventSet();

	/**
	 * The groups sorted by their Z index.
	 */
	protected ZIndex zIndex = new ZIndex();

	/**
	 * Set of groups that cast shadow.
	 */
	protected ShadowSet shadow = new ShadowSet();

	/**
	 * Remove groups if they become empty?.
	 */
	protected boolean removeEmptyGroups = true;

	/**
	 * Set of listeners.
	 */
	protected ArrayList<StyleGroupListener> listeners = new ArrayList<>();

	// Construction

	/**
	 * New empty style group set, using the given style sheet to create style
	 * groups. The group set installs itself as a listener of the style sheet. So in
	 * order to completely stop using such a group, you must call
	 * {@link #release()}.
	 * 
	 * @param stylesheet
	 *            The style sheet to use to create groups.
	 */
	public StyleGroupSet(StyleSheet stylesheet) {
		this.stylesheet = stylesheet;

		stylesheet.addListener(this);
	}

	// Access

	/**
	 * Number of groups.
	 * 
	 * @return The number of groups.
	 */
	public int getGroupCount() {
		return groups.size();
	}

	/**
	 * Return a group by its unique identifier. The way group identifier are
	 * constructed reflects their contents.
	 * 
	 * @param groupId
	 *            The group identifier.
	 * @return The corresponding group or null if not found.
	 */
	public StyleGroup getGroup(String groupId) {
		return groups.get(groupId);
	}

	/**
	 * Iterator on the set of groups in no particular order.
	 * 
	 * @return An iterator on the group set.
	 */
	public Iterator<? extends StyleGroup> getGroupIterator() {
		return groups.values().iterator();
	}

	/**
	 * Iterable set of groups elements, in no particular order.
	 * 
	 * @return An iterable on the set of groups.
	 */
	public Iterable<? extends StyleGroup> groups() {
		return groups.values();
	}

	/**
	 * Iterator on the Z index.
	 * 
	 * @return The z index iterator.
	 */
	public Iterator<HashSet<StyleGroup>> getZIterator() {
		return zIndex.getIterator();
	}

	/**
	 * Iterable set of "subsets of groups" sorted by Z level. Each subset of groups
	 * is at the same Z level.
	 * 
	 * @return The z levels.
	 */
	public Iterable<HashSet<StyleGroup>> zIndex() {
		return zIndex;
	}

	/**
	 * Iterator on the style groups that cast a shadow.
	 * 
	 * @return The shadow groups iterator.
	 */
	public Iterator<StyleGroup> getShadowIterator() {
		return shadow.getIterator();
	}

	/**
	 * Iterable set of groups that cast shadow.
	 * 
	 * @return All the groups that cast a shadow.
	 */
	public Iterable<StyleGroup> shadows() {
		return shadow;
	}

	/**
	 * True if the set contains and styles the node whose identifier is given.
	 * 
	 * @param id
	 *            The node identifier.
	 * @return True if the node is in this set.
	 */
	public boolean containsNode(String id) {
		return byNodeIdGroups.containsKey(id);
	}

	/**
	 * True if the set contains and styles the edge whose identifier is given.
	 * 
	 * @param id
	 *            The edge identifier.
	 * @return True if the edge is in this set.
	 */
	public boolean containsEdge(String id) {
		return byEdgeIdGroups.containsKey(id);
	}

	/**
	 * True if the set contains and styles the sprite whose identifier is given.
	 * 
	 * @param id
	 *            The sprite identifier.
	 * @return True if the sprite is in this set.
	 */
	public boolean containsSprite(String id) {
		return bySpriteIdGroups.containsKey(id);
	}

	/**
	 * True if the set contains and styles the graph whose identifier is given.
	 * 
	 * @param id
	 *            The graph identifier.
	 * @return True if the graph is in this set.
	 */
	public boolean containsGraph(String id) {
		return byGraphIdGroups.containsKey(id);
	}

	/**
	 * Get an element.
	 * 
	 * @param id
	 *            The element id.
	 * @param elt2grp
	 *            The kind of element.
	 * @return The element or null if not found.
	 */
	protected Element getElement(String id, Map<String, String> elt2grp) {
		String gid = elt2grp.get(id);

		if (gid != null) {
			StyleGroup group = groups.get(gid);
			return group.getElement(id);
		}

		return null;
	}

	/**
	 * Get a node element knowing its identifier.
	 * 
	 * @param id
	 *            The node identifier.
	 * @return The node if it is in this set, else null.
	 */
	public Node getNode(String id) {
		return (Node) getElement(id, byNodeIdGroups);
	}

	/**
	 * Get an edge element knowing its identifier.
	 * 
	 * @param id
	 *            The edge identifier.
	 * @return The edge if it is in this set, else null.
	 */
	public Edge getEdge(String id) {
		return (Edge) getElement(id, byEdgeIdGroups);
	}

	/**
	 * Get a sprite element knowing its identifier.
	 * 
	 * @param id
	 *            The sprite identifier.
	 * @return The sprite if it is in this set, else null.
	 */
	public GraphicSprite getSprite(String id) {
		return (GraphicSprite) getElement(id, bySpriteIdGroups);
	}

	/**
	 * Get a graph element knowing its identifier.
	 * 
	 * @param id
	 *            The graph identifier.
	 * @return The graph if it is in this set, else null.
	 */
	public Graph getGraph(String id) {
		return (Graph) getElement(id, byGraphIdGroups);
	}

	/**
	 * The number of nodes referenced.
	 * 
	 * @return The node count.
	 */
	public int getNodeCount() {
		return byNodeIdGroups.size();
	}

	/**
	 * The number of edges referenced.
	 * 
	 * @return The edge count.
	 */
	public int getEdgeCount() {
		return byEdgeIdGroups.size();
	}

	/**
	 * The number of sprites referenced.
	 * 
	 * @return The sprite count.
	 */
	public int getSpriteCount() {
		return bySpriteIdGroups.size();
	}

	/**
	 * Iterator on the set of nodes.
	 * 
	 * @return An iterator on all node elements contained in style groups.
	 */
	public Iterator<? extends Node> getNodeIterator() {
		return new ElementIterator<Node>(byNodeIdGroups);
	}

	/**
	 * Iterator on the set of graphs.
	 * 
	 * @return An iterator on all graph elements contained in style groups.
	 */
	public Iterator<? extends Graph> getGraphIterator() {
		return new ElementIterator<Graph>(byGraphIdGroups);
	}

	public Stream<Node> nodes() {
		return byNodeIdGroups.entrySet().stream().map(entry -> {
			return (Node) groups.get(entry.getValue()).getElement(entry.getKey());
		});
	}

	public Stream<Edge> edges() {
		return byEdgeIdGroups.entrySet().stream().map(entry -> {
			return (Edge) groups.get(entry.getValue()).getElement(entry.getKey());
		});
	}

	public Stream<GraphicSprite> sprites() {
		return bySpriteIdGroups.entrySet().stream().map(entry -> {
			return (GraphicSprite) groups.get(entry.getValue()).getElement(entry.getKey());
		});
	}

	/**
	 * Iterable set of graphs.
	 * 
	 * @return The set of all graphs.
	 */
	public Iterable<? extends Graph> graphs() {
		return graphSet;
	}

	/**
	 * Iterator on the set of edges.
	 * 
	 * @return An iterator on all edge elements contained in style groups.
	 */
	public Iterator<? extends Edge> getEdgeIterator() {
		return new ElementIterator<Edge>(byEdgeIdGroups);
	}

	/**
	 * Iterator on the set of sprite.
	 * 
	 * @return An iterator on all sprite elements contained in style groups.
	 */
	public Iterator<? extends GraphicSprite> getSpriteIterator() {
		return new ElementIterator<GraphicSprite>(bySpriteIdGroups);
	}

	/**
	 * Retrieve the group identifier of an element knowing the element identifier.
	 * 
	 * @param element
	 *            The element to search for.
	 * @return Identifier of the group containing the element.
	 */
	public String getElementGroup(Element element) {
		if (element instanceof Node) {
			return byNodeIdGroups.get(element.getId());
		} else if (element instanceof Edge) {
			return byEdgeIdGroups.get(element.getId());
		} else if (element instanceof GraphicSprite) {
			return bySpriteIdGroups.get(element.getId());
		} else if (element instanceof Graph) {
			return byGraphIdGroups.get(element.getId());
		} else {
			throw new RuntimeException("What ?");
		}
	}

	/**
	 * Get the style of an element.
	 * 
	 * @param element
	 *            The element to search for.
	 * @return The style group of the element (which is also a style).
	 */
	public StyleGroup getStyleForElement(Element element) {
		String gid = getElementGroup(element);

		return groups.get(gid);
	}

	/**
	 * Get the style of a given node.
	 * 
	 * @param node
	 *            The node to search for.
	 * @return The node style.
	 */
	public StyleGroup getStyleFor(Node node) {
		String gid = byNodeIdGroups.get(node.getId());
		return groups.get(gid);
	}

	/**
	 * Get the style of a given edge.
	 * 
	 * @param edge
	 *            The edge to search for.
	 * @return The edge style.
	 */
	public StyleGroup getStyleFor(Edge edge) {
		String gid = byEdgeIdGroups.get(edge.getId());
		return groups.get(gid);
	}

	/**
	 * Get the style of a given sprite.
	 * 
	 * @param sprite
	 *            The node to search for.
	 * @return The sprite style.
	 */
	public StyleGroup getStyleFor(GraphicSprite sprite) {
		String gid = bySpriteIdGroups.get(sprite.getId());
		return groups.get(gid);
	}

	/**
	 * Get the style of a given graph.
	 * 
	 * @param graph
	 *            The node to search for.
	 * @return The graph style.
	 */
	public StyleGroup getStyleFor(Graph graph) {
		String gid = byGraphIdGroups.get(graph.getId());
		return groups.get(gid);
	}

	/**
	 * True if groups are removed when becoming empty. This setting allows to keep
	 * empty group when the set of elements is quite dynamic. This allows to avoid
	 * recreting groups when an element appears and disappears regularly.
	 * 
	 * @return True if the groups are removed when empty.
	 */
	public boolean areEmptyGroupRemoved() {
		return removeEmptyGroups;
	}

	/**
	 * The Z index object.
	 * 
	 * @return The Z index.
	 */
	public ZIndex getZIndex() {
		return zIndex;
	}

	/**
	 * The set of style groups that cast a shadow.
	 * 
	 * @return The set of shadowed style groups.
	 */
	public ShadowSet getShadowSet() {
		return shadow;
	}

	// Command

	/**
	 * Release any dependency to the style sheet.
	 */
	public void release() {
		stylesheet.removeListener(this);
	}

	/**
	 * Empties this style group set. The style sheet is listener is not removed, use
	 * {@link #release()} to do that.
	 */
	public void clear() {
		byEdgeIdGroups.clear();
		byNodeIdGroups.clear();
		bySpriteIdGroups.clear();
		byGraphIdGroups.clear();
		groups.clear();
		zIndex.clear();
		shadow.clear();
	}

	/**
	 * Remove or keep groups that becomes empty, if true the groups are removed. If
	 * this setting was set to false, and is now true, the group set is purged of
	 * the empty groups.
	 * 
	 * @param on
	 *            If true the groups will be removed.
	 */
	public void setRemoveEmptyGroups(boolean on) {
		if (removeEmptyGroups == false && on == true) {
			Iterator<? extends StyleGroup> i = groups.values().iterator();

			while (i.hasNext()) {
				StyleGroup g = i.next();

				if (g.isEmpty())
					i.remove();
			}
		}

		removeEmptyGroups = on;
	}

	protected StyleGroup addGroup(String id, ArrayList<Rule> rules, Element firstElement) {
		StyleGroup group = new StyleGroup(id, rules, firstElement, eventSet);

		groups.put(id, group);
		zIndex.groupAdded(group);
		shadow.groupAdded(group);

		return group;
	}

	protected void removeGroup(StyleGroup group) {
		zIndex.groupRemoved(group);
		shadow.groupRemoved(group);
		groups.remove(group.getId());
		group.release();
	}

	/**
	 * Add an element and bind it to its style group. The group is created if
	 * needed.
	 * 
	 * @param element
	 *            The element to add.
	 * @return The style group where the element was added.
	 */
	public StyleGroup addElement(Element element) {
		StyleGroup group = addElement_(element);

		for (StyleGroupListener listener : listeners)
			listener.elementStyleChanged(element, null, group);

		return group;
	}

	protected StyleGroup addElement_(Element element) {
		ArrayList<Rule> rules = stylesheet.getRulesFor(element);
		String gid = stylesheet.getStyleGroupIdFor(element, rules);
		StyleGroup group = groups.get(gid);

		if (group == null)
			group = addGroup(gid, rules, element);
		else
			group.addElement(element);

		addElementToReverseSearch(element, gid);

		return group;
	}

	/**
	 * Remove an element from the group set. If the group becomes empty after the
	 * element removal, depending on the setting of {@link #areEmptyGroupRemoved()},
	 * the group is deleted or kept. Keeping groups allows to handle faster elements
	 * that constantly appear and disappear.
	 * 
	 * @param element
	 *            The element to remove.
	 */
	public void removeElement(Element element) {
		String gid = getElementGroup(element);
		if (null == gid) {
			return;
		}
		StyleGroup group = groups.get(gid);

		if (group != null) {
			group.removeElement(element);
			removeElementFromReverseSearch(element);

			if (removeEmptyGroups && group.isEmpty())
				removeGroup(group);
		}
	}

	/**
	 * Check if an element need to change from a style group to another.
	 * 
	 * <p>
	 * When an element can have potentially changed style due to some of its
	 * attributes (ui.class for example), instead of removing it then reading it,
	 * use this method to move the element from its current style group to a
	 * potentially different style group.
	 * </p>
	 * 
	 * <p>
	 * Explanation of this method : checking the style of an element may be done by
	 * removing it ({@link #removeElement(Element)}) and then re-adding it (
	 * {@link #addElement(Element)}). This must be done by the element since it
	 * knows when to check this. However you cannot only remove and add, since the
	 * style group inside which the element is can have events occurring on it, and
	 * these events must be passed from its old style to its new style. This method
	 * does all this information passing.
	 * </p>
	 * 
	 * @param element
	 *            The element to move.
	 */
	public void checkElementStyleGroup(Element element) {
		StyleGroup oldGroup = getGroup(getElementGroup(element));

		// Get the old element "dynamic" status.

		boolean isDyn = false;

		// Get the old event set for the given element.

		StyleGroup.ElementEvents events = null;

		if (oldGroup != null) {
			isDyn = oldGroup.isElementDynamic(element);
			events = oldGroup.getEventsFor(element);
		}

		// Remove the element from its old style and add it to insert it in the
		// correct style.

		removeElement(element);
		addElement_(element);

		// Eventually push the events on the new style group.

		StyleGroup newGroup = getGroup(getElementGroup(element));

		if (newGroup != null && events != null) {
			for (String event : events.events)
				pushEventFor(element, event);
		}

		for (StyleGroupListener listener : listeners)
			listener.elementStyleChanged(element, oldGroup, newGroup);

		// Eventually set the element as dynamic, if it was.

		if (newGroup != null && isDyn)
			newGroup.pushElementAsDynamic(element);
	}

	protected void addElementToReverseSearch(Element element, String groupId) {
		if (element instanceof Node) {
			byNodeIdGroups.put(element.getId(), groupId);
		} else if (element instanceof Edge) {
			byEdgeIdGroups.put(element.getId(), groupId);
		} else if (element instanceof GraphicSprite) {
			bySpriteIdGroups.put(element.getId(), groupId);
		} else if (element instanceof Graph) {
			byGraphIdGroups.put(element.getId(), groupId);
		} else {
			throw new RuntimeException("What ?");
		}
	}

	protected void removeElementFromReverseSearch(Element element) {
		if (element instanceof Node) {
			byNodeIdGroups.remove(element.getId());
		} else if (element instanceof Edge) {
			byEdgeIdGroups.remove(element.getId());
		} else if (element instanceof GraphicSprite) {
			bySpriteIdGroups.remove(element.getId());
		} else if (element instanceof Graph) {
			byGraphIdGroups.remove(element.getId());
		} else {
			throw new RuntimeException("What ?");
		}
	}

	/**
	 * Push a global event on the event stack. Events trigger the replacement of a
	 * style by an alternative style (or meta-class) when possible. If an event is
	 * on the event stack, each time a style has an alternative corresponding to the
	 * event, the alternative is used instead of the style.
	 * 
	 * @param event
	 *            The event to push.
	 */
	public void pushEvent(String event) {
		eventSet.pushEvent(event);
	}

	/**
	 * Push an event specifically for a given element. This is normally done
	 * automatically by the graphic element.
	 * 
	 * @param element
	 *            The element considered.
	 * @param event
	 *            The event to push.
	 */
	public void pushEventFor(Element element, String event) {
		StyleGroup group = getGroup(getElementGroup(element));

		if (group != null)
			group.pushEventFor(element, event);
	}

	/**
	 * Pop a global event from the event set.
	 * 
	 * @param event
	 *            The event to remove.
	 */
	public void popEvent(String event) {
		eventSet.popEvent(event);
	}

	/**
	 * Pop an event specifically for a given element. This is normally done
	 * automatically by the graphic element.
	 * 
	 * @param element
	 *            The element considered.
	 * @param event
	 *            The event to pop.
	 */
	public void popEventFor(Element element, String event) {
		StyleGroup group = getGroup(getElementGroup(element));

		if (group != null)
			group.popEventFor(element, event);
	}

	/**
	 * Specify the given element has dynamic style attribute values. This is
	 * normally done automatically by the graphic element.
	 * 
	 * @param element
	 *            The element to add to the dynamic subset.
	 */
	public void pushElementAsDynamic(Element element) {
		StyleGroup group = getGroup(getElementGroup(element));

		if (group != null)
			group.pushElementAsDynamic(element);
	}

	/**
	 * Remove the given element from the subset of elements having dynamic style
	 * attribute values. This is normally done automatically by the graphic element.
	 * 
	 * @param element
	 *            The element to remove from the dynamic subset.
	 */
	public void popElementAsDynamic(Element element) {
		StyleGroup group = getGroup(getElementGroup(element));

		if (group != null)
			group.popElementAsDynamic(element);
	}

	/**
	 * Add a listener for element style changes.
	 * 
	 * @param listener
	 *            The listener to add.
	 */
	public void addListener(StyleGroupListener listener) {
		listeners.add(listener);
	}

	/**
	 * Remove a style change listener.
	 * 
	 * @param listener
	 *            The listener to remove.
	 */
	public void removeListener(StyleGroupListener listener) {
		int index = listeners.lastIndexOf(listener);

		if (index >= 0) {
			listeners.remove(index);
		}
	}

	// Listener -- What to do when a change occurs in the style sheet.

	public void styleAdded(Rule oldRule, Rule newRule) {
		// When a style change, we need to update groups.
		// Several cases :
		// 1. The style already exists
		// * Nothing to do in fact. All the elements are still in place.
		// No style rule (selectors) changed, and therefore we do not have
		// to change the groups since they are built using the selectors.
		// 2. The style is new
		// * we need to check all the groups concerning this kind of element (we
		// can
		// restrict our search to these groups, since other will not be
		// impacted),
		// and check all elements of these groups.

		if (oldRule == null)
			checkForNewStyle(newRule); // no need to check Z and shadow, done
										// when adding/changing group.
		else
			checkZIndexAndShadow(oldRule, newRule);
	}

	public void styleSheetCleared() {
		ArrayList<Element> elements = new ArrayList<Element>();

		for (Element element : graphs())
			elements.add(element);

		nodes().forEach(elements::add);
		edges().forEach(elements::add);
		sprites().forEach(elements::add);

		clear();

		elements.forEach(this::removeElement);
		elements.forEach(this::addElement);
	}

	/**
	 * Check each group that may have changed, for example to rebuild the Z index
	 * and the shadow set.
	 * 
	 * @param oldRule
	 *            The old rule that changed.
	 * @param newRule
	 *            The new rule that participated in the change.
	 */
	protected void checkZIndexAndShadow(Rule oldRule, Rule newRule) {
		if (oldRule != null) {
			if (oldRule.selector.getId() != null || oldRule.selector.getClazz() != null) {
				// We may accelerate things a bit when a class or id style is
				// modified,
				// since only the groups listed in the style are concerned (we
				// are at the
				// bottom of the inheritance tree).
				if (oldRule.getGroups() != null)
					for (String s : oldRule.getGroups()) {
						StyleGroup group = groups.get(s);
						if (group != null) {
							zIndex.groupChanged(group);
							shadow.groupChanged(group);
						}
					}
			} else {
				// For kind styles "NODE", "EDGE", "GRAPH", "SPRITE", we must
				// reset
				// the whole Z and shadows for the kind, since several styles
				// may
				// have changed.

				Selector.Type type = oldRule.selector.type;

				for (StyleGroup group : groups.values()) {
					if (group.getType() == type) {
						zIndex.groupChanged(group);
						shadow.groupChanged(group);
					}
				}
			}
		}
	}

	/**
	 * We try to avoid at most to affect anew styles to elements and to recreate
	 * groups, which is time consuming.
	 * 
	 * Two cases :
	 * <ol>
	 * <li>The style is an specific (id) style. In this case a new group may be
	 * added.
	 * <ul>
	 * <li>check an element matches the style and in this case create the group by
	 * adding the element.</li>
	 * <li>else do nothing.</li>
	 * </ul>
	 * </li>
	 * <li>The style is a kind or class style.
	 * <ul>
	 * <li>check all the groups in the kind of the style (graph, node, edge, sprite)
	 * and only in this kind (since other will never be affected).</li>
	 * <li>remove all groups of this kind.</li>
	 * <li>add all elements of this kind anew to recreate the group.</li>
	 * </ul>
	 * </li>
	 * </ol>
	 */
	protected void checkForNewStyle(Rule newRule) {
		switch (newRule.selector.type) {
		case GRAPH:
			if (newRule.selector.getId() != null)
				checkForNewIdStyle(newRule, byGraphIdGroups);
			else
				checkForNewStyle(newRule, byGraphIdGroups);
			break;
		case NODE:
			if (newRule.selector.getId() != null)
				checkForNewIdStyle(newRule, byNodeIdGroups);
			else
				checkForNewStyle(newRule, byNodeIdGroups);
			break;
		case EDGE:
			if (newRule.selector.getId() != null)
				checkForNewIdStyle(newRule, byEdgeIdGroups);
			else
				checkForNewStyle(newRule, byEdgeIdGroups);
			break;
		case SPRITE:
			if (newRule.selector.getId() != null)
				checkForNewIdStyle(newRule, bySpriteIdGroups);
			else
				checkForNewStyle(newRule, bySpriteIdGroups);
			break;
		case ANY:
		default:
			throw new RuntimeException("What ?");
		}
	}

	/**
	 * Check for a new specific style (applies only to one element).
	 * 
	 * @param newRule
	 *            The new style rule.
	 * @param elt2grp
	 *            The name space.
	 */
	protected void checkForNewIdStyle(Rule newRule, Map<String, String> elt2grp) {
		// There is only one element that matches the identifier.

		Element element = getElement(newRule.selector.getId(), elt2grp);

		if (element != null) {
			checkElementStyleGroup(element);
			// removeElement( element ); // Remove the element from its old
			// group. Potentially delete a group.
			// addElement( element ); // Add the element to its new own group
			// (since this is an ID style).
		}
	}

	/**
	 * Check for a new kind or class style in a given name space (node, edge,
	 * sprite, graph).
	 * 
	 * @param newRule
	 *            The new style rule.
	 * @param elt2grp
	 *            The name space.
	 */
	protected void checkForNewStyle(Rule newRule, Map<String, String> elt2grp) {
		elt2grp.keySet().stream().map(eltId -> getElement(eltId, elt2grp)).collect(Collectors.toList())
				.forEach(this::checkElementStyleGroup);
	}

	// Utility

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append(String.format("Style groups (%d) :%n", groups.size()));

		for (StyleGroup group : groups.values()) {
			builder.append(group.toString(1));
			builder.append(String.format("%n"));
		}

		return builder.toString();
	}

	// Inner classes

	/**
	 * Set of events (meta-classes) actually active.
	 * 
	 * <p>
	 * The event set contains the set of events actually occurring. This is used to
	 * select alternate styles. The events actually occurring are in precedence
	 * order. The last one is the most important.
	 * </p>
	 * 
	 * @author Antoine Dutot
	 */
	public class EventSet {
		public ArrayList<String> eventSet = new ArrayList<String>();

		public String events[] = new String[0];

		/**
		 * Add an event to the set.
		 * 
		 * @param event
		 *            The event to add.
		 */
		public void pushEvent(String event) {
			eventSet.add(event);
			events = eventSet.toArray(events);
		}

		/**
		 * Remove an event from the set.
		 * 
		 * @param event
		 *            The event to remove.
		 */
		public void popEvent(String event) {
			int index = eventSet.lastIndexOf(event);

			if (index >= 0)
				eventSet.remove(index);

			events = eventSet.toArray(events);
		}

		/**
		 * The set of events in order, the most important at the end.
		 * 
		 * @return The event set.
		 */
		public String[] getEvents() {
			return events;
		}
	}

	/**
	 * All the style groups sorted by their Z index.
	 * 
	 * <p>
	 * This structure is maintained by each time a group is added or removed, or
	 * when the style of a group changed.
	 * </p>
	 * 
	 * @author Antoine Dutot
	 */
	public class ZIndex implements Iterable<HashSet<StyleGroup>> {
		/**
		 * Ordered set of groups.
		 */
		public ArrayList<HashSet<StyleGroup>> zIndex = new ArrayList<HashSet<StyleGroup>>();

		/**
		 * Knowing a group, tell if its Z index.
		 */
		public HashMap<String, Integer> reverseZIndex = new HashMap<String, Integer>();

		/**
		 * New empty Z index.
		 */
		public ZIndex() {
			initZIndex();
		}

		protected void initZIndex() {
			zIndex.ensureCapacity(256);

			for (int i = 0; i < 256; i++)
				zIndex.add(null);
		}

		/**
		 * Iterator on the set of Z index cells. Each item is a set of style groups that
		 * pertain to the same Z index.
		 * 
		 * @return Iterator on the Z index.
		 */
		protected Iterator<HashSet<StyleGroup>> getIterator() {
			return new ZIndexIterator();
		}

		public Iterator<HashSet<StyleGroup>> iterator() {
			return getIterator();
		}

		/**
		 * A new group appeared, put it in the z index.
		 * 
		 * @param group
		 *            The group to add.
		 */
		protected void groupAdded(StyleGroup group) {
			int z = convertZ(group.getZIndex());

			if (zIndex.get(z) == null)
				zIndex.set(z, new HashSet<StyleGroup>());

			zIndex.get(z).add(group);
			reverseZIndex.put(group.getId(), z);
		}

		/**
		 * A group eventually changed, check its location.
		 * 
		 * @param group
		 *            The group to check.
		 */
		protected void groupChanged(StyleGroup group) {
			int oldZ = reverseZIndex.get(group.getId());
			int newZ = convertZ(group.getZIndex());

			if (oldZ != newZ) {
				HashSet<StyleGroup> map = zIndex.get(oldZ);

				if (map != null) {
					map.remove(group);
					reverseZIndex.remove(group.getId());

					if (map.isEmpty())
						zIndex.set(oldZ, null);
				}

				groupAdded(group);
			}
		}

		/**
		 * A group was removed, remove it from the Z index.
		 * 
		 * @param group
		 *            The group to remove.
		 */
		protected void groupRemoved(StyleGroup group) {
			int z = convertZ(group.getZIndex());

			HashSet<StyleGroup> map = zIndex.get(z);

			if (map != null) {
				map.remove(group);
				reverseZIndex.remove(group.getId());

				if (map.isEmpty())
					zIndex.set(z, null);
			} else {
				throw new RuntimeException("Inconsistency in Z-index");
			}
		}

		public void clear() {
			zIndex.clear();
			reverseZIndex.clear();
			initZIndex();
		}

		/**
		 * Convert a [-127,127] value into a [0,255] value and check bounds.
		 * 
		 * @param z
		 *            The Z value to convert.
		 * @return The Z value converted and bounded to [0,255].
		 */
		protected int convertZ(int z) {
			z += 127;

			if (z < 0)
				z = 0;
			else if (z > 255)
				z = 255;

			return z;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();

			sb.append(String.format("Z index :%n"));

			for (int i = 0; i < 256; i++) {
				if (zIndex.get(i) != null) {
					sb.append(String.format("    * %d -> ", i - 127));

					HashSet<StyleGroup> map = zIndex.get(i);

					for (StyleGroup g : map)
						sb.append(String.format("%s ", g.getId()));

					sb.append(String.format("%n"));
				}
			}

			return sb.toString();
		}

		public class ZIndexIterator implements Iterator<HashSet<StyleGroup>> {
			public int index = 0;

			public ZIndexIterator() {
				zapUntilACell();
			}

			protected void zapUntilACell() {
				while (index < 256 && zIndex.get(index) == null)
					index++;
			}

			public boolean hasNext() {
				return (index < 256);
			}

			public HashSet<StyleGroup> next() {
				if (hasNext()) {
					HashSet<StyleGroup> cell = zIndex.get(index);
					index++;
					zapUntilACell();
					return cell;
				}

				return null;
			}

			public void remove() {
				throw new RuntimeException("This iterator does not support removal.");
			}
		}
	}

	/**
	 * Set of groups that cast a shadow.
	 * 
	 * @author Antoine Dutot
	 */
	public class ShadowSet implements Iterable<StyleGroup> {
		/**
		 * The set of groups casting shadow.
		 */
		protected HashSet<StyleGroup> shadowSet = new HashSet<StyleGroup>();

		/**
		 * Iterator on the set of groups that cast a shadow.
		 * 
		 * @return An iterator on the shadow style group set.
		 */
		protected Iterator<StyleGroup> getIterator() {
			return shadowSet.iterator();
		}

		public Iterator<StyleGroup> iterator() {
			return getIterator();
		}

		/**
		 * A group appeared, check its shadow status.
		 * 
		 * @param group
		 *            The group added.
		 */
		protected void groupAdded(StyleGroup group) {
			if (group.getShadowMode() != ShadowMode.NONE)
				shadowSet.add(group);
		}

		/**
		 * A group eventually changed, check its shadow status.
		 * 
		 * @param group
		 *            The group that changed.
		 */
		protected void groupChanged(StyleGroup group) {
			if (group.getShadowMode() == ShadowMode.NONE)
				shadowSet.remove(group);
			else
				shadowSet.add(group);
		}

		/**
		 * A group was removed, remove it from the shadow if needed.
		 * 
		 * @param group
		 *            The group removed.
		 */
		protected void groupRemoved(StyleGroup group) {
			// Faster than to first test its existence or shadow status :

			shadowSet.remove(group);
		}

		protected void clear() {
			shadowSet.clear();
		}
	}

	/**
	 * Iterator that allows to browse all graph elements of a given kind (nodes,
	 * edges, sprites, graphs) as if they where in a single set, whereas they are in
	 * style groups.
	 * 
	 * @author Antoine Dutot
	 * @param <E>
	 *            The kind of graph element.
	 */
	protected class ElementIterator<E extends Element> implements Iterator<E> {
		protected Map<String, String> elt2grp;

		protected Iterator<String> elts;

		public ElementIterator(final Map<String, String> elements2groups) {
			elt2grp = elements2groups;
			elts = elements2groups.keySet().iterator();
		}

		public boolean hasNext() {
			return elts.hasNext();
		}

		@SuppressWarnings("unchecked")
		public E next() {
			String eid = elts.next();
			String gid = elt2grp.get(eid);
			StyleGroup grp = groups.get(gid);

			return (E) grp.getElement(eid);
		}

		public void remove() {
			throw new RuntimeException("remove not implemented in this iterator");
		}
	}

	/**
	 * Dummy set of nodes.
	 */
	protected class NodeSet implements Iterable<Node> {
		@SuppressWarnings("unchecked")
		public Iterator<Node> iterator() {
			return (Iterator<Node>) getNodeIterator();
		}
	}

	/**
	 * Dummy set of edges.
	 */
	protected class EdgeSet implements Iterable<Edge> {
		@SuppressWarnings("unchecked")
		public Iterator<Edge> iterator() {
			return (Iterator<Edge>) getEdgeIterator();
		}
	}

	/**
	 * Dummy set of sprites.
	 */
	protected class SpriteSet implements Iterable<GraphicSprite> {
		@SuppressWarnings("unchecked")
		public Iterator<GraphicSprite> iterator() {
			return (Iterator<GraphicSprite>) getSpriteIterator();
		}
	}

	protected class GraphSet implements Iterable<GraphicGraph> {
		@SuppressWarnings("unchecked")
		public Iterator<GraphicGraph> iterator() {
			return (Iterator<GraphicGraph>) getGraphIterator();
		}
	}

}