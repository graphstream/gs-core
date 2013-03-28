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
package org.graphstream.ui.spriteManager;

import org.graphstream.ui.graphicGraph.stylesheet.Values;

/**
 * Factory for sprites.
 * 
 * <p>
 * Use the sprite factory in the sprite manager so that the manager produce
 * instance of a chosen subclass of {@link Sprite}. This is useful if you intend
 * to have each sprite pertain to the same subclass. If you intend to have
 * different kinds of sprites at the same time in the same manager, use
 * {@link SpriteManager#addSprite(String, Class)} instead.
 * </p>
 */
public class SpriteFactory {
	/**
	 * Create a new sprite for the given manager with the given identifier.
	 * 
	 * @param identifier
	 *            Identifier of the newly created sprite.
	 * @param manager
	 *            The sprite manager this sprite will pertain to.
	 * @param position
	 *            The sprite initial position or null for (0,0,0,GU).
	 * @return A new sprite.
	 */
	public Sprite newSprite(String identifier, SpriteManager manager,
			Values position) {
		if (position != null)
			return new Sprite(identifier, manager, position);

		return new Sprite(identifier, manager);
	}
}