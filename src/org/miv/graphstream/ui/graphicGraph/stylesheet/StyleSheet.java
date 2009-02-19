/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */

package org.miv.graphstream.ui.graphicGraph.stylesheet;

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

import org.miv.graphstream.graph.Edge;
import org.miv.graphstream.graph.Element;
import org.miv.graphstream.graph.Graph;
import org.miv.graphstream.graph.Node;
import org.miv.graphstream.ui.graphicGraph.GraphicSprite;
import org.miv.graphstream.ui.graphicGraph.stylesheet.parser.ParseException;
import org.miv.graphstream.ui.graphicGraph.stylesheet.parser.StyleSheetParser;

/**
 * Implementation of the style sheets that can be stored in the graphic graph.
 * 
 * @author Antoine Dutot
 * @author Yoann Pign
 */
public class StyleSheet
{
// Attributes

	/**
	 * The top-level default rule.
	 */
	public Rule defaultRule;
	
	/**
	 * The default rule for graphs.
	 */
	public Rule graphRule;
	
	/**
	 * The default rule for nodes.
	 */
	public Rule nodeRule;
	
	/**
	 * The default rule for edges.
	 */
	public Rule edgeRule;
	
	/**
	 * The default rule for sprites.
	 */
	public Rule spriteRule;
	
	/**
	 * The identifier name space of rules for graphs.
	 */
	public HashMap<String,Rule> byIdGraphRules = new HashMap<String,Rule>();
	
	/**
	 * The class name space of rules for graphs.
	 */
	public HashMap<String,Rule> byClassGraphRules = new HashMap<String,Rule>();
	
	/**
	 * The identifier name space of rules for nodes.
	 */
	public HashMap<String,Rule> byIdNodeRules = new HashMap<String,Rule>();
	
	/**
	 * The class name space of rules for nodes.
	 */
	public HashMap<String,Rule> byClassNodeRules = new HashMap<String,Rule>();
	
	/**
	 * The identifier name space of rules for edges.
	 */
	public HashMap<String,Rule> byIdEdgeRules = new HashMap<String,Rule>();
	
	/**
	 * The class name space of rules for edges.
	 */
	public HashMap<String,Rule> byClassEdgeRules = new HashMap<String,Rule>();
	
	/**
	 * The identifier name space of rules for sprites.
	 */
	public HashMap<String,Rule> byIdSpriteRules = new HashMap<String,Rule>();
	
	/**
	 * The class name space of rules for sprites.
	 */
	public HashMap<String,Rule> byClassSpriteRules = new HashMap<String,Rule>();
	
	/**
	 * Set of listeners.
	 */
	public ArrayList<StyleSheetListener> listeners = new ArrayList<StyleSheetListener>();
	
// Constructors
	
	/**
	 * New style sheet initialised to defaults.
	 */
	public StyleSheet()
	{
		initRules();
	}
	
// Access
	
	/**
	 * The default rule for graphs.
	 * @return A rule.
	 */
	public Rule getDefaultGraphRule()
	{
		return graphRule;
	}
	
	/**
	 * The default rule for nodes.
	 * @return A rule.
	 */
	public Rule getDefaultNodeRule()
	{
		return nodeRule;
	}
	
	/**
	 * The default rule for edges.
	 * @return A rule.
	 */
	public Rule getDefaultEdgeRule()
	{
		return edgeRule;
	}
	
	/**
	 * The default rule for sprites.
	 * @return A rule.
	 */
	public Rule getDefaultSpriteRule()
	{
		return spriteRule;
	}
	
	/**
	 * The default style for graphs.
	 * @return A style.
	 */
	public Style getDefaultGraphStyle()
	{
		return graphRule.getStyle();
	}
	
	/**
	 * The default style for nodes.
	 * @return A style.
	 */
	public Style getDefaultNodeStyle()
	{
		return nodeRule.getStyle();
	}
	
	/**
	 * The default style for edges.
	 * @return A style.
	 */
	public Style getDefaultEdgeStyle()
	{
		return edgeRule.getStyle();
	}
	
	/**
	 * The default style for sprites.
	 * @return A style.
	 */
	public Style getDefaultSpriteStyle()
	{
		return spriteRule.getStyle();
	}

	/**
	 * Get the rule that most finely match the given element. First a rule for the identifier
	 * of the element is looked for. It is looked for in its name space (nodes for Node element,
	 * etc.) If it is not found, a rule for the class of the element is searched for. Finally
	 * if no rule if found, the default rule for this kind of element is returned.
	 * @param element The element a rule is searched for.
	 * @return A rule for the element.
	 */
	public Rule getRuleForOld( Element element )
	{
		if( element instanceof Node )
		{
			Rule rule = byIdNodeRules.get( element.getId() );
			
			if( rule == null )
				rule = byClassNodeRules.get( element.getLabel( "ui.class" ) );
			if( rule == null )
				rule = byClassNodeRules.get( element.getLabel( "class" ) );
			
			if( rule == null )
				rule = nodeRule;
			
			return rule;
		}
		else if( element instanceof Edge )
		{
			Rule rule = byIdEdgeRules.get( element.getId() );
			
			if( rule == null )
				rule = byClassEdgeRules.get( element.getLabel( "ui.class" ) );
			if( rule == null )
				rule = byClassEdgeRules.get( element.getLabel( "class" ) );
			
			if( rule == null )
				rule = edgeRule;
			
			return rule;			
		}
		else if( element instanceof Graph )
		{
			Rule rule = byIdGraphRules.get( element.getId() );
			
			if( rule == null )
				rule = byClassGraphRules.get( element.getLabel( "ui.class" ) );
			if( rule == null )
				rule = byClassGraphRules.get( element.getLabel( "class" ) );
			
			if( rule == null )
				rule = graphRule;
			
			return rule;			
		}
		else if( element instanceof GraphicSprite )
		{
			Rule rule = byIdSpriteRules.get( element.getId() );
			
			if( rule == null )
				rule = byClassSpriteRules.get( element.getLabel( "ui.class" ) );
			if( rule == null )
				rule = byClassSpriteRules.get( element.getLabel( "class" ) );
			
			if( rule == null )
				rule = spriteRule;
			
			return rule;
		}
		
		return defaultRule;
	}


	/**
	 * Get the rule that most finely match the given element.
	 * 
	 * First a rule for the identifier
	 * of the element is looked for. It is looked for in its name space (nodes for Node element,
	 * etc.) If it is not found, a rule for the class of the element is searched for. Finally
	 * if no rule if found, the default rule for this kind of element is returned.
	 * 
	 * When a rule for the element identifier is found, then the various classes the element
	 * pertains to are looked at. A meta rule is returned that aggregates the rule for the
	 * identifier and all the classes is returned.
	 * 
	 * When a rule for a class is found, if there are other classes, a meta rule is returned
	 * to aggregate all these classes.
	 * 
	 * Else, a meta rule is returned that "aggregates" only the default rule.
	 * 
	 * In any case, the rule returned is not the one stored in the style sheet but a so called
	 * "meta" rule that whose style can be changed freely. This allows the element to refine
	 * its style according to user actions. 
	 * 
	 * @param element The element a rule is searched for.
	 * @return A rule for the element.
	 */
	public Rule getRuleFor( Element element )
	{
		Rule            ruleId      = null;
		Rule            ruleDefault = null;
		ArrayList<Rule> ruleClass   = null;
		
		// Collect the rules.
		
		if( element instanceof Node )
		{
			ruleId      = byIdNodeRules.get( element.getId() );
			ruleClass   = getClassRules( byClassNodeRules, element );
			ruleDefault = nodeRule;
		}
		else if( element instanceof Edge )
		{
			ruleId      = byIdEdgeRules.get( element.getId() );
			ruleClass   = getClassRules( byClassEdgeRules, element );
			ruleDefault = edgeRule;
		}
		else if( element instanceof Graph )
		{
			ruleId      = byIdGraphRules.get( element.getId() );
			ruleClass   = getClassRules( byClassGraphRules, element );
			ruleDefault = graphRule;
		}
		else if( element instanceof GraphicSprite )
		{
			ruleId      = byIdSpriteRules.get( element.getId() );
			ruleClass   = getClassRules( byClassSpriteRules, element );
			ruleDefault = spriteRule;
		}
		else
		{
			ruleDefault = defaultRule;
		}
		
		// Create a "meta" rule.
		
		return new MetaRule( ruleDefault, ruleId, ruleClass );
	}
	
	/**
	 * Search if the given element has classes attributes and return the set of rules that match
	 * these classes. Return null if the element has no class attribute.
	 * @param byClassRules The map of rules for the given class names.
	 * @param element The element for which classes must be found.
	 * @return The set of rules that match the element classes or null if the element has no class.
	 */
	protected ArrayList<Rule> getClassRules( HashMap<String,Rule> byClassRules, Element element )
	{
		ArrayList<Rule> rules = null;
		Object o;
		
		if( element.hasAttribute( "ui.class" ) )
		     o = element.getAttribute( "ui.class" );
		else o = element.getAttribute( "class" );
		
		if( o != null )
		{
			if( o instanceof Object[] )
			{
				for( Object s: (Object[])o )
				{
					if( s instanceof CharSequence )
					{
						Rule rule = byClassRules.get( (CharSequence)s );
						
						if( rule != null )
						{
							if( rules == null )
								rules = new ArrayList<Rule>();
							
							rules.add( rule );
						}
					}
				}
			}
			else if( o instanceof CharSequence )
			{
				String classList = ((CharSequence)o).toString().trim();
				String[] classes = classList.split( "\\s*,\\s*" );				
			
				for( String c: classes )
				{
					Rule rule = byClassRules.get( c );
					
					if( rule != null )
					{
						if( rules == null )
							rules = new ArrayList<Rule>();
						
						rules.add( rule );
					}
				}
			}
			else
			{
				throw new RuntimeException( "Oups ! class attribute is of type "+o.getClass().getName() );
			}
		}
		
		return rules;
	}
	
	/**
	 * Like the {@link #getRuleFor(Element)} method but return only the style part of the rule.
	 * @param element The element a rule is searched for.
	 * @return A style for the element.
	 */
	public Style getStyleFor( Element element )
	{
		Rule rule = getRuleFor( element );
		
		return rule.getStyle();
	}
	
// Commands

	/**
	 * Create the default rules.
	 * This method is the place to set defaults for specific element types. This is here that
	 * the edge width is reset to one, since the default width is larger. The default z index
	 * that is different for every class of element is also set here.
	 */
	protected void initRules()
	{
		defaultRule = new Rule( new Selector( Selector.Type.ANY ), null );
		
		defaultRule.getStyle().setDefaults();

		graphRule  = new Rule( new Selector( Selector.Type.GRAPH ),  defaultRule );
		nodeRule   = new Rule( new Selector( Selector.Type.NODE ),   graphRule );
		edgeRule   = new Rule( new Selector( Selector.Type.EDGE ),   graphRule );
		spriteRule = new Rule( new Selector( Selector.Type.SPRITE ), graphRule );
	
		edgeRule.getStyle().setWidth( 1, Style.Units.PX );
		edgeRule.getStyle().setZIndex( 1 );
		nodeRule.getStyle().setZIndex( 2 );
		spriteRule.getStyle().setZIndex( 3 );
	}
	
	/**
	 * Add a listener for style events. You never receive events for default rules and styles.
	 * You receive events only for the rules and styles that are added after this listener is
	 * registered.
	 * @param listener The new listener.
	 */
	public void addListener( StyleSheetListener listener )
	{
		listeners.add( listener );
	}
	
	/**
	 * Remove a previously registered listener.
	 * @param listener The listener to remove.
	 */
	public void removeListener( StyleSheetListener listener )
	{
		int index = listeners.indexOf( listener );
		
		if( index >= 0 )
			listeners.remove( index );
	}
	
	/**
	 * Clear all specific rules and initialise the default rules. 
	 */
	public void clear()
	{
		byIdGraphRules.clear();
		byClassGraphRules.clear();
		byIdNodeRules.clear();
		byClassNodeRules.clear();
		byIdEdgeRules.clear();
		byClassEdgeRules.clear();
		byIdSpriteRules.clear();
		byClassSpriteRules.clear();
		initRules();
	}
	
	/**
	 * Parse a style sheet from a file. The style sheet will complete the previously parsed
	 * style sheets. 
	 * @param fileName Name of the file containing the style sheet.
	 * @throws IOException For any kind of I/O error or parse error.
	 */
	public void parseFromFile( String fileName )
		throws IOException
	{
		parse( new InputStreamReader( new BufferedInputStream( new FileInputStream( fileName ) ) ) );
	}
	
	/**
	 * Parse a style sheet from an url. The style sheet will complete the previously parsed
	 * style sheets. First, this method will search the URL as SystemRessource, then as a file
	 * and if there is no match, just try to create an URL object giving <i>url</i> as
	 * constructor's parameter.
	 * @param url Name of the file containing the style sheet.
	 * @throws IOException For any kind of I/O error or parse error.
	 */
	public void parseFromURL( String url )
		throws IOException
	{
		URL u = ClassLoader.getSystemResource( url );
		if( u == null )
		{
			File f = new File( url );
			
			if( f.exists() )
				u = f.toURI().toURL();
			else
				u = new URL( url );
		}
		
		parse( new InputStreamReader( u.openStream() ) );
	}
	
	/**
	 * Parse a style sheet from a string. The style sheet will complete the previously parsed
	 * style sheets.
	 * @param styleSheet The string containing the whole style sheet.
	 * @throws IOException For any kind of I/O error or parse error.
	 */
	public void parseFromString( String styleSheet )
		throws IOException
	{
		parse( new StringReader( styleSheet ) );
	}
	
	/**
	 * Parse only one style, create a rule with the given selector, and add this rule.
	 * @param select The elements for which this style must apply.
	 * @param styleString The style string to parse.
	 */
	public void parseStyleFromString( Selector select, String styleString )
		throws IOException
	{
		StyleSheetParser parser = new StyleSheetParser( this, new StringReader( styleString ) );
		
		Style style = new Style();
		
		try
        {
	        parser.styles( style );
        }
        catch( ParseException e )
        {
        	throw new IOException( e.getMessage() );
        }
        
        Rule rule = new Rule( select );

        rule.setStyle( style );
        addRule( rule );
	}
	
	/**
	 * Parse the style sheet from the given reader.
	 * @param reader The reader pointing at the style sheet.
	 * @throws IOException For any kind of I/O error or parse error.
	 */
	protected void parse( Reader reader ) throws IOException
	{
		StyleSheetParser parser = new StyleSheetParser( this, reader );
		
		try
        {
	        parser.start();
        }
        catch( ParseException e )
        {
        	throw new IOException( e.getMessage() );
        }
	}
	
	/**
	 * Add a new rule with its style. If the rule selector is just GRAPH, NODE, EDGE or SPRITE, the
	 * default corresponding rules make a copy (or augmentation) of its style. Else if and id or
	 * class is specified the rules are added (or changed/augmented if the id or class was already
	 * set) and their parent is set to the default graph, node, edge or sprite rules.
	 * @param rule The new rule.
	 */
	public void addRule( Rule rule )
	{
		switch( rule.selector.getType() )
		{
			case ANY:
				throw new RuntimeException( "The ANY selector should never be used, it is created automatically." );
			case GRAPH:
				addRule( rule, graphRule, byIdGraphRules, byClassGraphRules );
				break;
			case NODE:
				addRule( rule, nodeRule, byIdNodeRules, byClassNodeRules );
				break;
			case EDGE:
				addRule( rule, edgeRule, byIdEdgeRules, byClassEdgeRules );
				break;
			case SPRITE:
				addRule( rule, spriteRule, byIdSpriteRules, byClassSpriteRules );
				break;
			default:
				throw new RuntimeException( "Ho ho ho ?" );
		}
	}
	
	/**
	 * Add a new rule if it has a specific id or class, else copy the style to the default rule
	 * style.
	 *
	 * This method is able to "augment" a pre-existing rule with the same id or class (or simply
	 * the default rule) if it encounters a new declaration for the rule: this is the cascade. This
	 * means that when a rule is encountered and there is already a rule with the same identifier/class,
	 * the old rule properties are overwritten or extended.
	 *
	 * @param newRule The rule to add or copy.
	 * @param defaultRule The rule by default.
	 * @param byId The rules by identifier map.
	 * @param byClass The rules by class map.
	 */
	protected Rule addRule( Rule newRule, Rule defaultRule, HashMap<String,Rule> byId, HashMap<String,Rule> byClass )
	{
		// Special case of the pseudo-classes. We do not add the class in the class map but
		// in the style. Therefore we find the parent rule (the same as the meta-class without
		// the meta-class value) and add the new rule as an "event-rule" that is a rule that
		// is used in specific conditions.
		//
		// If the parent rule does not exists yet we create it so that if it is found later when
		// parsing the style sheet, the meta-class is re-parented gently.
		
		if( newRule.selector.getPseudoClass() != null )
		{
			Rule parentRule = null;
			
			if( newRule.selector.getId() != null )
			{
				parentRule = byId.get( newRule.selector.getId() );
				
				if( parentRule == null )
				{
					parentRule = addRule(
							new Rule( new Selector( newRule.selector.getType(), newRule.selector.getId(), newRule.selector.getClazz() ) ), 
							defaultRule, byId, byClass );
				}
			}
			else if( newRule.selector.getClazz() != null )
			{
				parentRule = byId.get( newRule.selector.getClazz() );
				
				if( parentRule == null )
				{
					parentRule = addRule(
							new Rule( new Selector( newRule.selector.getType(), newRule.selector.getId(), newRule.selector.getClazz() ) ),
							defaultRule, byId, byClass );
				}
			}
			else
			{
				parentRule = defaultRule;
			}
			
			newRule.getStyle().reparent( parentRule );
			parentRule.getStyle().addAlternateStyle( newRule.selector.getPseudoClass(), newRule );
		}
		
		// Else we insert the new rule in the correct byId or byClass map.
		
		else if( newRule.selector.getId() != null )
		{
			Rule oldRule = byId.get( newRule.selector.getId() );
			
			if( oldRule != null )
			{
				oldRule.getStyle().augment( newRule.getStyle() );
				newRule = oldRule;
			}
			else
			{
				byId.put( newRule.selector.getId(), newRule );
				newRule.getStyle().reparent( defaultRule );
			}
		}
		else if( newRule.selector.getClazz() != null )
		{
			Rule oldRule = byClass.get( newRule.selector.getClazz() );
			
			if( oldRule != null )
			{
				oldRule.getStyle().augment( newRule.getStyle() );
				newRule = oldRule;
			}
			else
			{
				byClass.put( newRule.selector.getClazz(), newRule );			
				newRule.getStyle().reparent( defaultRule );
			}
		}
		else
		{
			defaultRule.getStyle().augment( newRule.getStyle() );
			newRule = defaultRule;
		}
		
		// Call all listeners.
		
		for( StyleSheetListener listener: listeners )
			listener.styleChanged( newRule );
		
		// That's it.
		
		return newRule;
	}
	
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		
		builder.append( "StyleSheet : {\n" );
		builder.append( "  default styles:\n" );
		builder.append( defaultRule.toString() );
		builder.append( graphRule.toString() );
		builder.append( nodeRule.toString() );
		builder.append( edgeRule.toString() );
		builder.append( spriteRule.toString() );

		toStringRules( builder, byIdGraphRules,     "graph id styles" );
		toStringRules( builder, byClassGraphRules,  "graph class styles" );
		toStringRules( builder, byIdNodeRules,      "node id styles" );
		toStringRules( builder, byClassNodeRules,   "node class styles" );
		toStringRules( builder, byIdEdgeRules,      "edge id styles" );
		toStringRules( builder, byClassEdgeRules,   "edge class styles" );
		toStringRules( builder, byIdSpriteRules,    "sprite id styles" );
		toStringRules( builder, byClassSpriteRules, "sprite class styles" );
		
		return builder.toString();
	}
	
	protected void toStringRules( StringBuilder builder, HashMap<String,Rule> rules, String title )
	{
		builder.append( "  " );
		builder.append( title );
		builder.append( ":\n" );
		
		for( Rule rule: rules.values() )
			builder.append( rule.toString() );
	}
}