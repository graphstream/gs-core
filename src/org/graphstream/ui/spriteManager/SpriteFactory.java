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
 * 
 * Copyright 2006 - 2009
 * 	Julien Baudry
 * 	Antoine Dutot
 * 	Yoann Pigné
 * 	Guilhelm Savin
 */

package org.graphstream.ui.spriteManager;

import org.graphstream.ui.graphicGraph.stylesheet.Values;

/**
 * Factory for sprites.
 * 
 * <p>Use the sprite factory in the sprite manager so that the manager produce instance of a chosen
 * subclass of {@link Sprite}. This is useful if you intend to have each sprite pertain to the same
 * subclass. If you intend to have different kinds of sprites at the same time in the same manager,
 * use {@link SpriteManager#addSprite(String, Class)} instead.</p>
 */
public class SpriteFactory
{
	/**
	 * Create a new sprite for the given manager with the given identifier.
	 * @param identifier Identifier of the newly created sprite.
	 * @param manager The sprite manager this sprite will pertain to.
	 * @param position The sprite initial position or null for (0,0,0,GU).
	 * @return A new sprite.
	 */
	public Sprite newSprite( String identifier, SpriteManager manager, Values position )
	{
		if( position != null )
			return new Sprite( identifier, manager, position );
		
		return new Sprite( identifier, manager );
	}
}