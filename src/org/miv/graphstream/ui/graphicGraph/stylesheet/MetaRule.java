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

import java.util.ArrayList;

/**
 * A meta rule is a rule that aggregates several rules (one ID rule and several class rules).
 * 
 * Most of the time all graphic elements have a meta rule instead of a rule.
 * 
 * @author Antoine Dutot
 */
public class MetaRule extends Rule
{
	/**
	 * Create a rule with a selector and style that are a composition of several rules and their
	 * styles.
	 * @param defaultRule The default rule (used only if idRule and classRules are null).
	 * @param idRule The rule for a given identifier.
	 * @param classRules The rules for several classes.
	 */
	public MetaRule( Rule defaultRule, Rule idRule, ArrayList<Rule> classRules )
	{
		assert( defaultRule != null );
		
		// The meta selector.
		
		if( idRule != null )
			selector = new Selector( defaultRule.selector );
		else if( classRules != null && classRules.size() > 0 )
			selector = new Selector( classRules.get(0).selector );
		else if( defaultRule != null )
			selector = new Selector( defaultRule.selector );
		
		if( selector == null )
			throw new RuntimeException( "Oups ! Meta rule agregates no rules ???" );
		
		// The meta style
		
		if( idRule != null )
		{
			style = new Style( idRule );
			
			if( classRules != null )
			{
				for( Rule r: classRules )
					style.addParentClass( r );
			}
		}
		else if( classRules != null )
		{
			style = new Style( defaultRule );
			
			for( Rule r: classRules )
				style.addParentClass( r );
/*
			style = new Style( classRules.get(0).style );
			
			int n = classRules.size();
			
			for( int i=1; i<n; ++i )
				style.addParentClass( classRules.get(i).style );
*/
		}
		else
		{
			style = new Style( defaultRule );
		}
	}
}