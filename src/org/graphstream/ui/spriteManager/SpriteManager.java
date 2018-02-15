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
 * @since 2009-04-17
 * 
 * @author Antoine Dutot <antoine.dutot@graphstream-project.org>
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Richard O. Legendi <richard.legendi@gmail.com>
 * @author Alex Bowen <bowen.a@gmail.com>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.ui.spriteManager;

import org.graphstream.graph.Graph;
import org.graphstream.stream.AttributeSink;
import org.graphstream.ui.graphicGraph.stylesheet.Style;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.Units;
import org.graphstream.ui.graphicGraph.stylesheet.Value;
import org.graphstream.ui.graphicGraph.stylesheet.Values;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Set of sprites associated with a graph.
 * 
 * <p>
 * The sprite manager acts as a set of sprite elements that are associated with
 * a graph. There can be only one sprite manager per graph. The sprite manager
 * only role is to allow to create, destroy and enumerate sprites of a graph.
 * </p>
 * 
 * <p>
 * See the {@link Sprite} class for an explanation of what are sprites and how
 * to use them.
 * </p>
 * 
 * <p>
 * In case you need to refine the Sprite class, you can change the
 * {@link SpriteFactory} of this manager so that it creates specific instances
 * of sprites instead of the default ones. This is mostly useful when all
 * sprites will pertain to the same subclass. If you need to create several
 * sprites of distinct subclasses, you can use the
 * {@link #addSprite(String, Class)} and
 * {@link #addSprite(String, Class, Values)} methods.
 * </p>
 */
public class SpriteManager implements Iterable<Sprite>, AttributeSink {

	/**
	 * class level logger
	 */
	private static final Logger logger = Logger.getLogger(SpriteManager.class.getName());

	// Attribute

	/**
	 * The graph to add sprites to.
	 */
	protected Graph graph;

	/**
	 * The set of sprites.
	 */
	protected HashMap<String, Sprite> sprites = new HashMap<String, Sprite>();

	/**
	 * Factory to create new sprites.
	 */
	protected SpriteFactory factory = new SpriteFactory();

	// Attributes

	/**
	 * this acts as a lock when we are adding a sprite since we are also listener of
	 * the graph, and when we receive an "add" event, we automatically create a
	 * sprite. We can want to avoid listening at ourself.
	 */
	boolean attributeLock = false;

	// Construction

	/**
	 * Create a new manager for sprite and bind it to the given graph. If the graph
	 * already contains attributes describing sprites, the manager is automatically
	 * filled with the existing sprites.
	 * 
	 * @param graph
	 *            The graph to associate with this manager;
	 */
	public SpriteManager(Graph graph) throws InvalidSpriteIDException {
		this.graph = graph;

		lookForExistingSprites();
		graph.addAttributeSink(this);
	}

	protected void lookForExistingSprites() throws InvalidSpriteIDException {
		if (graph.getAttributeCount() > 0) {
			graph.attributeKeys().filter(key -> key.startsWith("ui.sprite.")).forEach(key -> {
				String id = key.substring(10);

				if (id.indexOf('.') < 0) {
					addSprite(id);
				} else {
					String sattr = id.substring(id.indexOf('.') + 1);
					id = id.substring(0, id.indexOf('.'));

					Sprite s = getSprite(id);

					if (s == null)
						s = addSprite(id);

					s.setAttribute(sattr, graph.getAttribute(key));
				}
			});
		}
	}

	// Access

	/**
	 * Number of sprites in the manager.
	 * 
	 * @return The sprite count.
	 */
	public int getSpriteCount() {
		return sprites.size();
	}

	/**
	 * True if the manager contains a sprite corresponding to the given identifier.
	 * 
	 * @param identifier
	 *            The sprite identifier to search for.
	 */
	public boolean hasSprite(String identifier) {
		return (sprites.get(identifier) != null);
	}

	/**
	 * Sprite corresponding to the given identifier or null if no sprite is
	 * associated with the given identifier.
	 * 
	 * @param identifier
	 *            The sprite identifier.
	 */
	public Sprite getSprite(String identifier) {
		return sprites.get(identifier);
	}

	/**
	 * Iterable set of sprites in no particular order.
	 * 
	 * @return The set of sprites.
	 */
	public Iterable<? extends Sprite> sprites() {
		return sprites.values();
	}

	/**
	 * Iterator on the set of sprites.
	 * 
	 * @return An iterator on sprites.
	 */
	public Iterator<? extends Sprite> spriteIterator() {
		return sprites.values().iterator();
	}

	/**
	 * Iterator on the set of sprites.
	 * 
	 * @return An iterator on sprites.
	 */
	public Iterator<Sprite> iterator() {
		return sprites.values().iterator();
	}

	/**
	 * The current sprite factory.
	 * 
	 * @return A Sprite factory.
	 */
	public SpriteFactory getSpriteFactory() {
		return factory;
	}

	// Command

	/**
	 * Detach this manager from its graph. This manager will no more be usable to
	 * create or remove sprites. However sprites not yet removed are still present
	 * as attributes in the graph and binding another sprite manager to this graph
	 * will retrieve all sprites.
	 */
	public void detach() {
		graph.removeAttributeSink(this);
		sprites.clear();

		graph = null;
	}

	/**
	 * Specify the sprite factory to use. This allows to use specific sprite classes
	 * (descendants of Sprite).
	 * 
	 * @param factory
	 *            The new factory to use.
	 */
	public void setSpriteFactory(SpriteFactory factory) {
		this.factory = factory;
	}

	/**
	 * Reset the sprite factory to defaults.
	 */
	public void resetSpriteFactory() {
		factory = new SpriteFactory();
	}

	/**
	 * Add a sprite with the given identifier. If the sprite already exists, nothing
	 * is done. The sprite identifier cannot actually contain dots. This character
	 * use is reserved by the sprite mechanism.
	 * 
	 * @param identifier
	 *            The identifier of the new sprite to add.
	 * @return The created sprite.
	 * @throws InvalidSpriteIDException
	 *             If the given identifier contains a dot.
	 */
	public Sprite addSprite(String identifier) throws InvalidSpriteIDException {
		return addSprite(identifier, (Values) null);
	}

	/**
	 * Add a sprite with the given identifier and position. If the sprite already
	 * exists, nothing is done, excepted if the position is not null in which case
	 * it is repositioned. If the sprite does not exists, it is added and if
	 * position is not null, it is used as the initial position of the sprite. The
	 * sprite identifier cannot actually contain dots. This character use is
	 * reserved by the sprite mechanism.
	 * 
	 * @param identifier
	 *            The sprite identifier.
	 * @param position
	 *            The sprite position (or null for (0,0,0)).
	 * @return The created sprite.
	 * @throws InvalidSpriteIDException
	 *             If the given identifier contains a dot.
	 */
	protected Sprite addSprite(String identifier, Values position) throws InvalidSpriteIDException {
		if (identifier.indexOf('.') >= 0)
			throw new InvalidSpriteIDException("Sprite identifiers cannot contain dots.");

		Sprite sprite = sprites.get(identifier);

		if (sprite == null) {
			attributeLock = true;
			sprite = factory.newSprite(identifier, this, position);
			sprites.put(identifier, sprite);
			attributeLock = false;
		} else {
			if (position != null)
				sprite.setPosition(position);
		}

		return sprite;
	}

	/**
	 * Add a sprite of a given subclass of Sprite with the given identifier. If the
	 * sprite already exists, nothing is done. This method allows to add a sprite of
	 * a chosen subclass of Sprite, without using a {@link SpriteFactory}. Most
	 * often you use a sprite factory when all sprites will pertain to the same
	 * subclass. If some sprites pertain to distinct subclasses, you can use this
	 * method.
	 * 
	 * @param identifier
	 *            The identifier of the new sprite to add.
	 * @param spriteClass
	 *            The class of the new sprite to add.
	 * @return The created sprite.
	 */
	public <T extends Sprite> T addSprite(String identifier, Class<T> spriteClass) {
		return addSprite(identifier, spriteClass, null);
	}

	/**
	 * Same as {@link #addSprite(String, Class)} but also allows to specify an
	 * initial position.
	 * 
	 * @param identifier
	 *            The identifier of the new sprite to add.
	 * @param spriteClass
	 *            The class of the new sprite to add.
	 * @param position
	 *            The sprite position, or null for position (0, 0, 0).
	 * @return The created sprite.
	 */
	public <T extends Sprite> T addSprite(String identifier, Class<T> spriteClass, Values position) {
		try {
			T sprite = spriteClass.newInstance();
			sprite.init(identifier, this, position);
			return sprite;
		} catch (Exception e) {
			logger.log(Level.WARNING,
					String.format("Error while trying to instantiate class %s.", spriteClass.getName()), e);
		}
		return null;
	}

	/**
	 * Remove a sprite knowing its identifier. If no such sprite exists, this fails
	 * silently.
	 * 
	 * @param identifier
	 *            The identifier of the sprite to remove.
	 */
	public void removeSprite(String identifier) {
		Sprite sprite = sprites.get(identifier);

		if (sprite != null) {
			attributeLock = true;
			sprites.remove(identifier);
			sprite.removed();
			attributeLock = false;
		}
	}

	// Utility

	protected static Values getPositionValue(Object value) {
		if (value instanceof Object[]) {
			Object[] values = (Object[]) value;

			if (values.length == 4) {
				if (values[0] instanceof Number && values[1] instanceof Number && values[2] instanceof Number
						&& values[3] instanceof Style.Units) {
					return new Values((Style.Units) values[3], ((Number) values[0]).floatValue(),
							((Number) values[1]).floatValue(), ((Number) values[2]).floatValue());
				} else {
					logger.warning("Cannot parse values[4] for sprite position.");
				}
			} else if (values.length == 3) {
				if (values[0] instanceof Number && values[1] instanceof Number && values[2] instanceof Number) {
					return new Values(Units.GU, ((Number) values[0]).floatValue(), ((Number) values[1]).floatValue(),
							((Number) values[2]).floatValue());
				} else {
					logger.warning("Cannot parse values[3] for sprite position.");
				}
			} else if (values.length == 1) {
				if (values[0] instanceof Number) {
					return new Values(Units.GU, ((Number) values[0]).floatValue());
				} else {
					logger.warning(String.format("Sprite position percent is not a number."));
				}
			} else {
				logger.warning(String.format("Cannot transform value '%s' (length=%d) into a position.",
						Arrays.toString(values), values.length));
			}
		} else if (value instanceof Number) {
			return new Values(Units.GU, ((Number) value).floatValue());
		} else if (value instanceof Value) {
			return new Values((Value) value);
		} else if (value instanceof Values) {
			return new Values((Values) value);
		} else {
			System.err.printf("GraphicGraph : cannot place sprite with posiiton '%s' (instance of %s)%n", value,
					value.getClass().getName());
		}

		return null;
	}

	// GraphAttributesListener

	public void graphAttributeAdded(String graphId, long time, String attribute, Object value) {
		if (attributeLock)
			return; // We want to avoid listening at ourselves.

		if (attribute.startsWith("ui.sprite.")) {
			String spriteId = attribute.substring(10);

			if (spriteId.indexOf('.') < 0) {
				if (getSprite(spriteId) == null) {
					// A sprite has been created by another entity.
					// Synchronise this manager.

					Values position = null;

					if (value != null)
						position = getPositionValue(value);

					try {
						addSprite(spriteId, position);
					} catch (InvalidSpriteIDException e) {
						e.printStackTrace();
						throw new RuntimeException(e);
						// Ho !! Dirty !!
					}
				}
			}
		}
	}

	public void graphAttributeChanged(String graphId, long time, String attribute, Object oldValue, Object newValue) {
		if (attributeLock)
			return; // We want to avoid listening at ourselves.

		if (attribute.startsWith("ui.sprite.")) {
			String spriteId = attribute.substring(10);

			if (spriteId.indexOf('.') < 0) {
				Sprite s = getSprite(spriteId);

				if (s != null) {
					// The sprite has been moved by another entity.
					// Update its position.

					if (newValue != null) {
						Values position = getPositionValue(newValue);
						s.setPosition(position);
					} else {
						logger.warning(
								String.format("%s changed but newValue == null ! (old=%s).", spriteId, oldValue));
					}
				} else {
					throw new IllegalStateException("Sprite changed, but not added.");
				}
			}
		}
	}

	public void graphAttributeRemoved(String graphId, long time, String attribute) {
		if (attributeLock)
			return; // We want to avoid listening at ourselves.

		if (attribute.startsWith("ui.sprite.")) {
			String spriteId = attribute.substring(10);

			if (spriteId.indexOf('.') < 0) {
				if (getSprite(spriteId) != null) {
					// A sprite has been removed by another entity.
					// Synchronise this manager.

					removeSprite(spriteId);
				}
			}
		}
	}

	// Unused.

	public void edgeAttributeAdded(String graphId, long time, String edgeId, String attribute, Object value) {
	}

	public void edgeAttributeChanged(String graphId, long time, String edgeId, String attribute, Object oldValue,
			Object newValue) {
	}

	public void edgeAttributeRemoved(String graphId, long time, String edgeId, String attribute) {
	}

	public void nodeAttributeAdded(String graphId, long time, String nodeId, String attribute, Object value) {
	}

	public void nodeAttributeChanged(String graphId, long time, String nodeId, String attribute, Object oldValue,
			Object newValue) {
	}

	public void nodeAttributeRemoved(String graphId, long time, String nodeId, String attribute) {
	}
}