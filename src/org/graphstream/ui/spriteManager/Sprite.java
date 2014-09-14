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

import org.graphstream.graph.Element;
import org.graphstream.ui.graphicGraph.stylesheet.Style;
import org.graphstream.ui.graphicGraph.stylesheet.Values;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * A gentle little sprite.
 * 
 * <p>
 * Sprite objects allow to add data representations in a graphic display of a
 * graph. A sprite is a graphical representation that can double anywhere in the
 * graph drawing surface, or be "attached" to nodes or edges. When attached to
 * an edge, a sprite can be positioned easily at any point along the edge, or
 * perpendicular to it with one or two coordinates. When attached to a node, a
 * sprite "orbits" around the node at any given radius and angle around it.
 * </p>
 * 
 * <p>
 * Sprites can have many shapes. Most of the CSS nodes shapes are available for
 * sprites, but more are possible. Some shapes follow the form of the element
 * (node or edge) they are attached to.
 * </p>
 * 
 * <p>
 * Sprites can be moved and animated easily along edges, around nodes, or
 * anywhere on the graph surface. Their shape can change. Some sprites allows to
 * draw pie charts or statistics, or images.
 * </p>
 * 
 * <p>
 * Sprites are not part of a graph so to speak. Furthermore they make sense only
 * when a graph is displayed with a viewer that supports sprites. Therefore they
 * are handled by a {@link SpriteManager} which is always associated to a graph
 * and is in charge of handling the whole set of sprites, creating them,
 * enumerating them, and destroying them.
 * </p>
 * 
 * <p>
 * Implementation note: sprites do not exist ! In fact the sprite class only
 * handles a set of attributes that are stored in the graph (the one associated
 * with the sprite manager that created the sprite). These special attributes
 * are handled for you by the sprite class. This technique allows to pass
 * sprites informations through the I/O system of GraphStream. Indeed sprites
 * appearing in a graph can therefore be stored in files and retrieved if the
 * graph file format supports attributes. If this is a dynamic graph format,
 * like DGS, the whole sprite history is remembered: when it moved, when it
 * changed, etc.
 * </p>
 * 
 * <p>
 * Second implementation node : often you will need to extend the sprite class.
 * This is easily possible, but you must remember that you cannot create sprites
 * yourself, you must use the {@link SpriteManager}. In order to create a sprite
 * of a special kind, you can either use a {@link SpriteFactory} with the
 * SpriteManager or the special {@link SpriteManager#addSprite(String, Class)}
 * method of the SpriteManager. In both cases, the
 * {@link #init(String, SpriteManager, Values)} method of the sprite will be
 * called. Override this method to initialise your sprite.
 * </p>
 * 
 * @see SpriteManager
 * @see SpriteFactory
 */
public class Sprite implements Element {
	// Attribute

	/**
	 * The sprite unique identifier.
	 */
	protected String id;

	/**
	 * The identifier prefixed by "ui.sprite.".
	 */
	protected String completeId;

	/**
	 * The boss.
	 */
	protected SpriteManager manager;

	/**
	 * Current sprite position.
	 */
	protected Values position;

	/**
	 * The element this sprite is attached to (or null).
	 */
	protected Element attachment;

	// Construction

	/** For the use with {@link #init(String, SpriteManager, Values)}. */
	protected Sprite() {
	}

	/**
	 * New sprite with a given identifier.
	 * 
	 * You cannot build sprites yourself, they are created by the sprite
	 * manager.
	 */
	protected Sprite(String id, SpriteManager manager) {
		this(id, manager, null);
	}

	/**
	 * New sprite with a given identifier.
	 * 
	 * You cannot build sprites yourself, they are created by the sprite
	 * manager.
	 */
	protected Sprite(String id, SpriteManager manager, Values position) {
		init(id, manager, position);
	}

	/**
	 * New sprite with a given identifier.
	 * 
	 * You cannot build sprites yourself, they are created by the sprite
	 * managern. This method is used by the manager when creating instances of
	 * sprites that inherit this class. If you derive the sprite class you can
	 * override this method to initialise your sprite. It is always called when
	 * creating the sprite.
	 */
	protected void init(String id, SpriteManager manager, Values position) {
		this.id = id;
		this.completeId = String.format("ui.sprite.%s", id);
		this.manager = manager;

		if (!manager.graph.hasAttribute(completeId)) {
			if (position != null) {
				manager.graph.addAttribute(completeId, position);
				this.position = position;
			} else {
				this.position = new Values(Style.Units.GU, 0f, 0f, 0f);
				manager.graph.addAttribute(completeId, new Values(this.position));
			}
		} else {
			if (position != null) {
				manager.graph.setAttribute(completeId, position);
				this.position = position;
			} else {
				this.position = SpriteManager.getPositionValue(manager.graph.getAttribute(completeId));
			}
		}
	}

	/**
	 * Called by the manager when the sprite is removed.
	 */
	protected void removed() {
		manager.graph.removeAttribute(completeId);

		String start = String.format("%s.", completeId);

		if (attached())
			detach();

		ArrayList<String> keys = new ArrayList<String>();

		for (String key : manager.graph.getAttributeKeySet()) {
			if (key.startsWith(start))
				keys.add(key);
		}

		for (String key : keys)
			manager.graph.removeAttribute(key);
	}

	// Access

	/**
	 * The element the sprite is attached to or null if the sprite is not
	 * attached.
	 * 
	 * @return An element the sprite is attached to or null.
	 */
	public Element getAttachment() {
		return attachment;
	}

	/**
	 * True if attached to an edge or node.
	 * 
	 * @return False if not attached.
	 */
	public boolean attached() {
		return (attachment != null);
	}

	/**
	 * X position.
	 * 
	 * @return The position in abscissa.
	 */
	public double getX() {
		if (position.values.size() > 0)
			return position.values.get(0);

		return 0;
	}

	/**
	 * Y position.
	 * 
	 * @return The position in ordinate.
	 */
	public double getY() {
		if (position.values.size() > 1)
			return position.values.get(1);

		return 0;
	}

	/**
	 * Z position.
	 * 
	 * @return The position in depth.
	 */
	public double getZ() {
		if (position.values.size() > 2)
			return position.values.get(2);

		return 0;
	}

	public Style.Units getUnits() {
		return position.units;
	}

	// Command

	/**
	 * Attach the sprite to a node with the given identifier. If needed the
	 * sprite is first detached. If the given node identifier does not exist,
	 * the sprite stays in detached state.
	 * 
	 * @param id
	 *            Identifier of the node to attach to.
	 */
	public void attachToNode(String id) {
		if (attachment != null)
			detach();

		attachment = manager.graph.getNode(id);

		if (attachment != null)
			attachment.addAttribute(completeId);
	}

	/**
	 * Attach the sprite to an edge with the given identifier. If needed the
	 * sprite is first detached. If the given edge identifier does not exist,
	 * the sprite stays in detached state.
	 * 
	 * @param id
	 *            Identifier of the edge to attach to.
	 */
	public void attachToEdge(String id) {
		if (attachment != null)
			detach();

		attachment = manager.graph.getEdge(id);

		if (attachment != null)
			attachment.addAttribute(completeId);
	}

	/**
	 * Detach the sprite from the element it is attached to (if any).
	 */
	public void detach() {
		if (attachment != null) {
			attachment.removeAttribute(completeId);
			attachment = null;
		}
	}

	public void setPosition(double percent) {
		setPosition(position.units, percent, 0, 0);
	}

	public void setPosition(double x, double y, double z) {
		setPosition(position.units, x, y, z);
	}

	public void setPosition(Style.Units units, double x, double y, double z) {
		boolean changed = false;

		if (position.get(0) != x) {
			changed = true;
			position.setValue(0, x);
		}
		if (position.get(1) != y) {
			changed = true;
			position.setValue(1, y);
		}
		if (position.get(2) != z) {
			changed = true;
			position.setValue(2, z);
		}
		if (position.units != units) {
			changed = true;
			position.setUnits(units);
		}

		if (changed)
			manager.graph.setAttribute(completeId, new Values(position));
	}

	protected void setPosition(Values values) {
		if (values != null) {
			int n = values.values.size();

			if (n > 2) {
				setPosition(values.units, values.get(0), values.get(1),
						values.get(2));
			} else if (n > 0) {
				setPosition(values.get(0));
			}
		}
	}

	// Access (Element)

    @Override
	public String getId() {
		return id;
	}

    @Override
	public String getLabel(String key) {
		return manager.graph.getLabel(String.format("%s.%s", completeId, key));
	}

    @Override
	public <T> T getAttribute(String key) {
		return manager.graph.getAttribute(String.format("%s.%s", completeId,
				key));
	}

    @Override
	public <T> T getAttribute(String key, Class<T> clazz) {
		return manager.graph.getAttribute(
				String.format("%s.%s", completeId, key), clazz);
	}

	/**
	 * Quite expensive operation !.
	 */
    @Override
	public int getAttributeCount() {
		String start = String.format("%s.", completeId);
		int count = 0;

		for (String key : manager.graph.getAttributeKeySet()) {
			if (key.startsWith(start))
				count++;
		}

		return count;
	}

    @Override
	public Iterator<String> getAttributeKeyIterator() {
		throw new RuntimeException("not implemented");
	}

    @Override
	public Iterable<String> getEachAttributeKey() {
		return getAttributeKeySet();
	}

    @Override
	public Collection<String> getAttributeKeySet() {
		throw new RuntimeException("not implemented");
	}

    @Override
	public <T> T getFirstAttributeOf(String... keys) {
		String completeKeys[] = new String[keys.length];
		int i = 0;

		for (String key : keys) {
			completeKeys[i] = String.format("%s.%s", completeId, key);
			i++;
		}

		return manager.graph.getFirstAttributeOf(completeKeys);
	}

    @Override
	public <T> T getFirstAttributeOf(Class<T> clazz, String... keys) {
		String completeKeys[] = new String[keys.length];
		int i = 0;

		for (String key : keys) {
			completeKeys[i] = String.format("%s.%s", completeId, key);
			i++;
		}

		return manager.graph.getFirstAttributeOf(clazz, completeKeys);
	}

    @Override
	public Object[] getArray(String key) {
		return manager.graph.getArray(String.format("%s.%s", completeId, key));
	}

    @Override
	public Map<?, ?> getHash(String key) {
		return manager.graph.getHash(String.format("%s.%s", completeId, key));
	}

    @Override
	public Number getNumber(String key) {
		return manager.graph.getNumber(String.format("%s.%s", completeId, key));
	}

    @Override
	public Collection<? extends Number> getVector(String key) {
		return manager.graph.getVector(String.format("%s.%s", completeId, key));
	}

    @Override
	public boolean hasAttribute(String key) {
		return manager.graph.hasAttribute(String.format("%s.%s", completeId,
				key));
	}

    @Override
	public boolean hasArray(String key) {
		return manager.graph.hasArray(String.format("%s.%s", completeId, key));
	}

    @Override
	public boolean hasAttribute(String key, Class<?> clazz) {
		return manager.graph.hasAttribute(
				String.format("%s.%s", completeId, key), clazz);
	}

    @Override
	public boolean hasHash(String key) {
		return manager.graph.hasHash(String.format("%s.%s", completeId, key));
	}

    @Override
	public boolean hasLabel(String key) {
		return manager.graph.hasLabel(String.format("%s.%s", completeId, key));
	}

    @Override
	public boolean hasNumber(String key) {
		return manager.graph.hasNumber(String.format("%s.%s", completeId, key));
	}

    @Override
	public boolean hasVector(String key) {
		return manager.graph.hasVector(String.format("%s.%s", completeId, key));
	}

	// Commands (Element)

    @Override
	public boolean addAttribute(String attribute, Object... values) {
		return manager.graph.addAttribute(String.format("%s.%s", completeId, attribute), values);
	}

    @Override
	public boolean addAttributes(Map<String, Object> attributes) {
        boolean modified = false;
		for (String key : attributes.keySet()) {
            modified |= manager.graph.addAttribute(String.format("%s.%s", completeId, key), attributes.get(key));
        }
        return modified;
	}

    @Override
	public boolean setAttribute(String attribute, Object... values) {
		return manager.graph.setAttribute(String.format("%s.%s", completeId, attribute), values);
	}

    @Override
	public boolean changeAttribute(String attribute, Object... values) {
		return manager.graph.changeAttribute(String.format("%s.%s", completeId, attribute), values);
	}

    @Override
	public void clearAttributes() {
		String start = String.format("%s.", completeId);
		ArrayList<String> keys = new ArrayList<String>();

		for (String key : manager.graph.getAttributeKeySet()) {
			if (key.startsWith(start))
				keys.add(key);
		}

		for (String key : keys)
			manager.graph.removeAttribute(key);
	}

    @Override
	public boolean removeAttribute(String attribute) {
		return manager.graph.removeAttribute(String.format("%s.%s", completeId, attribute));
	}
	
	// XXX -> UGLY FIX
	// Sprites do not have unique index but is this useful?
    @Override
	public int getIndex() {
		return 0;
	}

    @Override
    public double getDouble(String key) {
        final Number num = this.getNumber(key);
        if (null == num) {
            return Double.NaN;
        }
        return num.doubleValue();
    }

    @Override
    public float getFloat(String key) {
        final Number num = this.getNumber(key);
        if (null == num) {
            return Float.NaN;
        }
        return num.floatValue();
    }

    @Override
    public int getInteger(String key) {
        final Number num = this.getNumber(key);
        if (null == num) {
            return 0;
        }
        return num.intValue();
    }

    @Override
    public long getLong(String key) {
        final Number num = this.getNumber(key);
        if (null == num) {
            return 0L;
        }
        return num.longValue();
    }

    @Override
    public short getShort(String key) {
        final Number num = this.getNumber(key);
        if (null == num) {
            return 0;
        }
        return num.shortValue();
    }
}