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
 * @author Mathieu Post <mathieupost@gmail.com>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 * @author Yoann Pigné <yoann.pigne@graphstream-project.org>
 */
package org.graphstream.ui.graphicGraph.stylesheet;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.graphicGraph.GraphicSprite;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.StrokeMode;
import org.graphstream.ui.graphicGraph.stylesheet.parser.StyleSheetParser;
import org.graphstream.util.parser.ParseException;

/**
 * Implementation of the style sheets that can be stored in the graphic graph.
 * 
 * @author Antoine Dutot
 */
public class StyleSheet {
	// Attributes

	/**
	 * The top-level default rule.
	 */
	public Rule defaultRule;

	/**
	 * The default, id and class rules for graphs.
	 */
	public NameSpace graphRules = new NameSpace(Selector.Type.GRAPH);

	/**
	 * The default, id and class rules for nodes.
	 */
	public NameSpace nodeRules = new NameSpace(Selector.Type.NODE);

	/**
	 * The default, id and class rules for edges.
	 */
	public NameSpace edgeRules = new NameSpace(Selector.Type.EDGE);

	/**
	 * The default, id and class rules for sprites.
	 */
	public NameSpace spriteRules = new NameSpace(Selector.Type.SPRITE);

	/**
	 * Set of listeners.
	 */
	public ArrayList<StyleSheetListener> listeners = new ArrayList<StyleSheetListener>();

	// Constructors

	/**
	 * New style sheet initialised to defaults.
	 */
	public StyleSheet() {
		initRules();
	}

	// Access

	/**
	 * The default rule for graphs.
	 * 
	 * @return A rule.
	 */
	public Rule getDefaultGraphRule() {
		return graphRules.defaultRule;
	}

	/**
	 * The default rule for nodes.
	 * 
	 * @return A rule.
	 */
	public Rule getDefaultNodeRule() {
		return nodeRules.defaultRule;
	}

	/**
	 * The default rule for edges.
	 * 
	 * @return A rule.
	 */
	public Rule getDefaultEdgeRule() {
		return edgeRules.defaultRule;
	}

	/**
	 * The default rule for sprites.
	 * 
	 * @return A rule.
	 */
	public Rule getDefaultSpriteRule() {
		return spriteRules.defaultRule;
	}

	/**
	 * The default style for graphs.
	 * 
	 * @return A style.
	 */
	public Style getDefaultGraphStyle() {
		return getDefaultGraphRule().getStyle();
	}

	/**
	 * The default style for nodes.
	 * 
	 * @return A style.
	 */
	public Style getDefaultNodeStyle() {
		return getDefaultNodeRule().getStyle();
	}

	/**
	 * The default style for edges.
	 * 
	 * @return A style.
	 */
	public Style getDefaultEdgeStyle() {
		return getDefaultEdgeRule().getStyle();
	}

	/**
	 * The default style for sprites.
	 * 
	 * @return A style.
	 */
	public Style getDefaultSpriteStyle() {
		return getDefaultSpriteRule().getStyle();
	}

	/**
	 * All the rules (default, specific and class) that apply to graphs.
	 * 
	 * @return The set of rules for graphs.
	 */
	public NameSpace getGraphStyleNameSpace() {
		return graphRules;
	}

	/**
	 * All the rules (default, specific and class) that apply to nodes.
	 * 
	 * @return The set of rules for nodes.
	 */
	public NameSpace getNodeStyleNameSpace() {
		return nodeRules;
	}

	/**
	 * All the rules (default, specific and class) that apply to edges.
	 * 
	 * @return The set of rules for edges.
	 */
	public NameSpace getEdgeStyleNameSpace() {
		return edgeRules;
	}

	/**
	 * All the rules (default, specific and class) that apply to sprites.
	 * 
	 * @return The set of rules for sprites.
	 */
	public NameSpace getSpriteStyleNameSpace() {
		return spriteRules;
	}

	/**
	 * Get the rules that match a given element.
	 * 
	 * First a rule for the identifier of the element is looked for. It is looked
	 * for in its name space (nodes for Node element, etc.) If it is not found, the
	 * default rule for this kind of element is used. This rule is pushed at start
	 * of the returned array of rules.
	 * 
	 * After a rule for the element is found, then the various classes the element
	 * pertains to are looked at and each class rule found is added in order in the
	 * returned array.
	 * 
	 * @param element
	 *            The element a rules are searched for.
	 * @return A set of rules matching the element, with the main rule at index 0.
	 */
	public ArrayList<Rule> getRulesFor(Element element) {
		ArrayList<Rule> rules = null;

		if (element instanceof Graph) {
			rules = graphRules.getRulesFor(element);
		} else if (element instanceof Node) {
			rules = nodeRules.getRulesFor(element);
		} else if (element instanceof Edge) {
			rules = edgeRules.getRulesFor(element);
		} else if (element instanceof GraphicSprite) {
			rules = spriteRules.getRulesFor(element);
		} else {
			rules = new ArrayList<Rule>();
			rules.add(defaultRule);
		}

		return rules;
	}

	/**
	 * Compute the name of the style group and element will pertain to knowing its
	 * styling rules.
	 * 
	 * @param element
	 *            The element.
	 * @param rules
	 *            The styling rules.
	 * @return The unique identifier of the style group for the element.
	 * @see #getRulesFor(Element)
	 */
	public String getStyleGroupIdFor(Element element, ArrayList<Rule> rules) {
		StringBuilder builder = new StringBuilder();

		if (element instanceof Graph) {
			builder.append("g");
		} else if (element instanceof Node) {
			builder.append("n");
		} else if (element instanceof Edge) {
			builder.append("e");
		} else if (element instanceof GraphicSprite) {
			builder.append("s");
		} else {
			throw new RuntimeException("What ?");
		}

		if (rules.get(0).selector.getId() != null) {
			builder.append('_');
			builder.append(rules.get(0).selector.getId());
		}

		int n = rules.size();

		if (n > 1) {
			builder.append('(');
			builder.append(rules.get(1).selector.getClazz());
			for (int i = 2; i < n; i++) {
				builder.append(',');
				builder.append(rules.get(i).selector.getClazz());
			}
			builder.append(')');
		}

		return builder.toString();
	}

	// Commands

	/**
	 * Create the default rules. This method is the place to set defaults for
	 * specific element types. This is here that the edge width is reset to one,
	 * since the default width is larger. The default z index that is different for
	 * every class of element is also set here.
	 */
	protected void initRules() {
		defaultRule = new Rule(new Selector(Selector.Type.ANY), null);

		defaultRule.getStyle().setDefaults();

		graphRules.defaultRule = new Rule(new Selector(Selector.Type.GRAPH), defaultRule);
		nodeRules.defaultRule = new Rule(new Selector(Selector.Type.NODE), defaultRule);
		edgeRules.defaultRule = new Rule(new Selector(Selector.Type.EDGE), defaultRule);
		spriteRules.defaultRule = new Rule(new Selector(Selector.Type.SPRITE), defaultRule);

		graphRules.defaultRule.getStyle().setValue("padding", new Values(Style.Units.PX, 30));
		edgeRules.defaultRule.getStyle().setValue("shape", StyleConstants.Shape.LINE);
		edgeRules.defaultRule.getStyle().setValue("size", new Values(Style.Units.PX, 1));
		edgeRules.defaultRule.getStyle().setValue("z-index", new Integer(1));
		nodeRules.defaultRule.getStyle().setValue("z-index", new Integer(2));
		spriteRules.defaultRule.getStyle().setValue("z-index", new Integer(3));

		Colors colors = new Colors();
		colors.add(Color.WHITE);

		graphRules.defaultRule.getStyle().setValue("fill-color", colors);
		graphRules.defaultRule.getStyle().setValue("stroke-mode", StrokeMode.NONE);

		for (StyleSheetListener listener : listeners) {
			listener.styleAdded(defaultRule, defaultRule);
			listener.styleAdded(graphRules.defaultRule, graphRules.defaultRule);
			listener.styleAdded(nodeRules.defaultRule, nodeRules.defaultRule);
			listener.styleAdded(edgeRules.defaultRule, edgeRules.defaultRule);
			listener.styleAdded(spriteRules.defaultRule, spriteRules.defaultRule);
		}

		// for( StyleSheetListener listener: listeners )
		// listener.styleAdded( defaultRule, defaultRule );
		// for( StyleSheetListener listener: listeners )
		// listener.styleAdded( graphRules.defaultRule, graphRules.defaultRule
		// );
		// for( StyleSheetListener listener: listeners )
		// listener.styleAdded( nodeRules.defaultRule, nodeRules.defaultRule );
		// for( StyleSheetListener listener: listeners )
		// listener.styleAdded( edgeRules.defaultRule, edgeRules.defaultRule );
		// for( StyleSheetListener listener: listeners )
		// listener.styleAdded( spriteRules.defaultRule, spriteRules.defaultRule
		// );
	}

	/**
	 * Add a listener for style events. You never receive events for default rules
	 * and styles. You receive events only for the rules and styles that are added
	 * after this listener is registered.
	 * 
	 * @param listener
	 *            The new listener.
	 */
	public void addListener(StyleSheetListener listener) {
		listeners.add(listener);
	}

	/**
	 * Remove a previously registered listener.
	 * 
	 * @param listener
	 *            The listener to remove.
	 */
	public void removeListener(StyleSheetListener listener) {
		int index = listeners.indexOf(listener);

		if (index >= 0)
			listeners.remove(index);
	}

	/**
	 * Clear all specific rules and initialise the default rules. The listeners are
	 * not changed.
	 */
	public void clear() {
		graphRules.clear();
		nodeRules.clear();
		edgeRules.clear();
		spriteRules.clear();
		initRules();

		for (StyleSheetListener listener : listeners)
			listener.styleSheetCleared();
	}

	/**
	 * Parse a style sheet from a file. The style sheet will complete the previously
	 * parsed style sheets.
	 * 
	 * @param fileName
	 *            Name of the file containing the style sheet.
	 * @throws IOException
	 *             For any kind of I/O error or parse error.
	 */
	public void parseFromFile(String fileName) throws IOException {
		parse(new InputStreamReader(new BufferedInputStream(new FileInputStream(fileName))));
	}

	/**
	 * Parse a style sheet from an URL. The style sheet will complete the previously
	 * parsed style sheets. First, this method will search the URL as
	 * SystemRessource, then as a file and if there is no match, just try to create
	 * an URL object giving the URL as constructor's parameter.
	 * 
	 * @param url
	 *            Name of the file containing the style sheet.
	 * @throws IOException
	 *             For any kind of I/O error or parse error.
	 */
	public void parseFromURL(String url) throws IOException {
		URL u = StyleSheet.class.getClassLoader().getResource(url);
		if (u == null) {
			String fileUrl = url.replace("file://", "");
			File f = new File(fileUrl);

			if (f.exists())
				u = f.toURI().toURL();
			else
				u = new URL(url);
		}

		parse(new InputStreamReader(u.openStream()));
	}

	/**
	 * Parse a style sheet from a string. The style sheet will complete the
	 * previously parsed style sheets.
	 * 
	 * @param styleSheet
	 *            The string containing the whole style sheet.
	 * @throws IOException
	 *             For any kind of I/O error or parse error.
	 */
	public void parseFromString(String styleSheet) throws IOException {
		parse(new StringReader(styleSheet));
	}

	/**
	 * Parse only one style, create a rule with the given selector, and add this
	 * rule.
	 * 
	 * @param select
	 *            The elements for which this style must apply.
	 * @param styleString
	 *            The style string to parse.
	 */
	public void parseStyleFromString(Selector select, String styleString) throws IOException {
		StyleSheetParser parser = new StyleSheetParser(this, new StringReader(styleString));

		Style style = new Style();

		try {
			parser.stylesStart(style);
		} catch (ParseException e) {
			throw new IOException(e.getMessage());
		}

		Rule rule = new Rule(select);

		rule.setStyle(style);
		addRule(rule);
	}

	/**
	 * Load a style sheet from an attribute value, the value can either be the
	 * contents of the whole style sheet, or begin by "url". If it starts with
	 * "url", it must then contain between parenthesis the string of the URL to
	 * load. For example:
	 * 
	 * <pre>
	 * 		url('file:///some/path/on/the/file/system')
	 * </pre>
	 * 
	 * Or
	 * 
	 * <pre>
	 * 		url('http://some/web/url')
	 * </pre>
	 * 
	 * The loaded style sheet will be merged with the styles already present in the
	 * style sheet.
	 * 
	 * @param styleSheetValue
	 *            The style sheet name of content.
	 * @throws IOException
	 *             If the loading or parsing of the style sheet failed.
	 */
	public void load(String styleSheetValue) throws IOException {
		if (styleSheetValue.startsWith("url")) {
			// Extract the part between '(' and ')'.

			int beg = styleSheetValue.indexOf('(');
			int end = styleSheetValue.lastIndexOf(')');

			if (beg >= 0 && end > beg)
				styleSheetValue = styleSheetValue.substring(beg + 1, end);

			styleSheetValue = styleSheetValue.trim();

			// Remove the quotes (') or (").

			if (styleSheetValue.startsWith("'")) {
				beg = 0;
				end = styleSheetValue.lastIndexOf('\'');

				if (beg >= 0 && end > beg)
					styleSheetValue = styleSheetValue.substring(beg + 1, end);
			}

			styleSheetValue = styleSheetValue.trim();

			if (styleSheetValue.startsWith("\"")) {
				beg = 0;
				end = styleSheetValue.lastIndexOf('"');

				if (beg >= 0 && end > beg)
					styleSheetValue = styleSheetValue.substring(beg + 1, end);
			}

			// That's it.

			parseFromURL(styleSheetValue);
		} else // Parse from string, the value is considered to be the style
				// sheet contents.
		{
			parseFromString(styleSheetValue);
		}
	}

	/**
	 * Parse the style sheet from the given reader.
	 * 
	 * @param reader
	 *            The reader pointing at the style sheet.
	 * @throws IOException
	 *             For any kind of I/O error or parse error.
	 */
	protected void parse(Reader reader) throws IOException {
		StyleSheetParser parser = new StyleSheetParser(this, reader);

		try {
			parser.start();
		} catch (ParseException e) {
			throw new IOException(e.getMessage());
		}
	}

	/**
	 * Add a new rule with its style. If the rule selector is just GRAPH, NODE, EDGE
	 * or SPRITE, the default corresponding rules make a copy (or augmentation) of
	 * its style. Else if an id or class is specified the rules are added (or
	 * changed/augmented if the id or class was already set) and their parent is set
	 * to the default graph, node, edge or sprite rules. If this is an event rule
	 * (or meta-class rule), its sibling rule (the same rule without the meta-class)
	 * is searched and created if not found and the event rule is added as an
	 * alternative to it.
	 * 
	 * @param newRule
	 *            The new rule.
	 */
	public void addRule(Rule newRule) {
		Rule oldRule = null;

		switch (newRule.selector.getType()) {
		case ANY:
			throw new RuntimeException("The ANY selector should never be used, it is created automatically.");
		case GRAPH:
			oldRule = graphRules.addRule(newRule);
			break;
		case NODE:
			oldRule = nodeRules.addRule(newRule);
			break;
		case EDGE:
			oldRule = edgeRules.addRule(newRule);
			break;
		case SPRITE:
			oldRule = spriteRules.addRule(newRule);
			break;
		default:
			throw new RuntimeException("Ho ho ho ?");
		}

		for (StyleSheetListener listener : listeners)
			listener.styleAdded(oldRule, newRule);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append("StyleSheet : {\n");
		builder.append("  default styles:\n");
		builder.append(defaultRule.toString(1));
		builder.append(graphRules.toString(1));
		builder.append(nodeRules.toString(1));
		builder.append(edgeRules.toString(1));
		builder.append(spriteRules.toString(1));

		return builder.toString();
	}

	// Nested classes

	/**
	 * A name space is a tuple (default rule, id rule set, class rule set).
	 * 
	 * <p>
	 * The name space defines a default rule for a kind of elements, a set of rules
	 * for this kind of elements with a given identifier, and a set or rules for
	 * this kind of elements with a given class.
	 * </p>
	 */
	public class NameSpace {
		// Attribute

		/**
		 * The kind of elements in this name space.
		 */
		public Selector.Type type;

		/**
		 * The default rule for this kind of elements.
		 */
		public Rule defaultRule;

		/**
		 * The set of rules for this kind of elements with a given identifier.
		 */
		public HashMap<String, Rule> byId = new HashMap<String, Rule>();

		/**
		 * The set of rules for this kind of elements with a given class.
		 */
		public HashMap<String, Rule> byClass = new HashMap<String, Rule>();

		// Constructor

		public NameSpace(Selector.Type type) {
			this.type = type;
		}

		// Access

		/**
		 * The kind of elements this name space applies rules to.
		 * 
		 * @return A type of element (node, edge, sprite, graph).
		 */
		public Selector.Type getGraphElementType() {
			return type;
		}

		/**
		 * Number of specific (id) rules.
		 * 
		 * @return The number of rules that apply to elements by their identifiers.
		 */
		public int getIdRulesCount() {
			return byId.size();
		}

		/**
		 * Number of specific (class) rules.
		 * 
		 * @return The number of rules that apply to elements by their classes.
		 */
		public int getClassRulesCount() {
			return byClass.size();
		}

		/**
		 * Get the rules that match a given element. The rules are returned in a given
		 * order. The array always contain the "main" rule that matches the element.
		 * This rule is either a default rule for the kind of element given or the rule
		 * that matches its identifier if there is one. Then class rules the element has
		 * can be appended to this array in order.
		 * 
		 * @return an array of rules that match the element, with the main rule at index
		 *         0.
		 */
		protected ArrayList<Rule> getRulesFor(Element element) {
			Rule rule = byId.get(element.getId());
			ArrayList<Rule> rules = new ArrayList<Rule>();

			if (rule != null)
				rules.add(rule);
			else
				rules.add(defaultRule);

			getClassRules(element, rules);

			if (rules.isEmpty())
				rules.add(defaultRule);

			return rules;
		}

		/**
		 * Search if the given element has classes attributes and fill the given array
		 * with the set of rules that match these classes.
		 * 
		 * @param element
		 *            The element for which classes must be found.
		 * @param rules
		 *            The rule array to fill.
		 */
		protected void getClassRules(Element element, ArrayList<Rule> rules) {
			Object o = element.getAttribute("ui.class");

			if (o != null) {
				if (o instanceof Object[]) {
					for (Object s : (Object[]) o) {
						if (s instanceof CharSequence) {
							Rule rule = byClass.get((CharSequence) s);

							if (rule != null)
								rules.add(rule);
						}
					}
				} else if (o instanceof CharSequence) {
					String classList = ((CharSequence) o).toString().trim();
					String[] classes = classList.split("\\s*,\\s*");

					for (String c : classes) {
						Rule rule = byClass.get(c);

						if (rule != null)
							rules.add(rule);
					}
				} else {
					throw new RuntimeException("Oups ! class attribute is of type " + o.getClass().getName());
				}
			}
		}

		// Command

		/**
		 * Remove all styles.
		 */
		protected void clear() {
			defaultRule = null;
			byId.clear();
			byClass.clear();
		}

		/**
		 * Add a new rule.
		 * 
		 * <p>
		 * Several cases can occur :
		 * </p>
		 * 
		 * <ul>
		 * <li>The rule to add has an ID or class and the rule does not yet exists and
		 * is not an event rule : add it directly.</li>
		 * <li>If the rule has an ID or class but the rule already exists, augment to
		 * already existing rule.</li>
		 * <li>If the rule has no ID or class and is not an event, augment the default
		 * style.</li>
		 * <li>If the rule is an event, the corresponding normal rule is searched, if it
		 * does not exists, it is created then or else, the event is added to the found
		 * rule.</li>
		 * </ul>
		 * 
		 * @param newRule
		 *            The rule to add or copy.
		 * @return It the rule added augments an existing rule, this existing rule is
		 *         returned, else null is returned.
		 */
		protected Rule addRule(Rule newRule) {
			Rule oldRule = null;

			if (newRule.selector.getPseudoClass() != null) {
				oldRule = addEventRule(newRule);
			} else if (newRule.selector.getId() != null) {
				oldRule = byId.get(newRule.selector.getId());

				if (oldRule != null) {
					oldRule.getStyle().augment(newRule.getStyle());
				} else {
					byId.put(newRule.selector.getId(), newRule);
					newRule.getStyle().reparent(defaultRule);
				}
			} else if (newRule.selector.getClazz() != null) {
				oldRule = byClass.get(newRule.selector.getClazz());

				if (oldRule != null) {
					oldRule.getStyle().augment(newRule.getStyle());
				} else {
					byClass.put(newRule.selector.getClazz(), newRule);
					newRule.getStyle().reparent(defaultRule);
				}
			} else {
				oldRule = defaultRule;
				defaultRule.getStyle().augment(newRule.getStyle());
				newRule = defaultRule;
			}

			// That's it.

			return oldRule;
		}

		protected Rule addEventRule(Rule newRule) {
			Rule parentRule = null;

			if (newRule.selector.getId() != null) {
				parentRule = byId.get(newRule.selector.getId());

				if (parentRule == null) {
					parentRule = addRule(new Rule(new Selector(newRule.selector.getType(), newRule.selector.getId(),
							newRule.selector.getClazz())));
				}
			} else if (newRule.selector.getClazz() != null) {
				parentRule = byClass.get(newRule.selector.getClazz());

				if (parentRule == null) {
					parentRule = addRule(new Rule(new Selector(newRule.selector.getType(), newRule.selector.getId(),
							newRule.selector.getClazz())));
				}
			} else {
				parentRule = defaultRule;
			}

			newRule.getStyle().reparent(parentRule);
			parentRule.getStyle().addAlternateStyle(newRule.selector.getPseudoClass(), newRule);

			return parentRule;
		}

		@Override
		public String toString() {
			return toString(-1);
		}

		public String toString(int level) {
			String prefix = "";

			if (level > 0) {
				for (int i = 0; i < level; i++)
					prefix += "    ";
			}

			StringBuilder builder = new StringBuilder();

			builder.append(String.format("%s%s default style :%n", prefix, type));
			builder.append(defaultRule.toString(level + 1));
			toStringRules(level, builder, byId, String.format("%s%s id styles", prefix, type));
			toStringRules(level, builder, byClass, String.format("%s%s class styles", prefix, type));

			return builder.toString();
		}

		protected void toStringRules(int level, StringBuilder builder, HashMap<String, Rule> rules, String title) {
			builder.append(title);
			builder.append(String.format(" :%n"));

			for (Rule rule : rules.values())
				builder.append(rule.toString(level + 1));
		}
	}
}