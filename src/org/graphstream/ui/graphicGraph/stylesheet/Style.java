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
 * @since 2009-02-19
 * 
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Yoann Pigné <yoann.pigne@graphstream-project.org>
 * @author Antoine Dutot <antoine.dutot@graphstream-project.org>
 * @author Tim Wundke <gtwundke@gmail.com>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.ui.graphicGraph.stylesheet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * A style is a whole set of settings for a graphic element.
 * 
 * <p>
 * Styles inherit each others. By default a style is all set to invalid values
 * meaning "unset". This means that the value is to be taken from the parent.
 * The getters are able to resolve this process by themselves and therefore must
 * be used instead of a direct access to fields.
 * </p>
 */
public class Style extends StyleConstants {
	// Attributes

	/**
	 * The vertical part of the cascade.
	 */
	protected Rule parent = null;

	/**
	 * The values of each style property.
	 */
	protected HashMap<String, Object> values = null;

	/**
	 * The set of special styles that must override this style when some event
	 * occurs.
	 */
	protected HashMap<String, Rule> alternates = null;

	// Constructors

	/**
	 * New style with all settings to a special value meaning "unset". In this
	 * modeField, all the settings are inherited from the parent (when set).
	 */
	public Style() {
		this(null);
	}

	/**
	 * New style with all settings to a special value meaning "unset". In this
	 * modeField, all the settings are inherited from the parent.
	 * 
	 * @param parent
	 *            The parent style.
	 */
	public Style(Rule parent) {
		this.parent = parent;
		this.values = new HashMap<String, Object>();
	}

	// Access

	/**
	 * The parent style.
	 * 
	 * @return a style from which some settings are inherited.
	 */
	public Rule getParent() {
		return parent;
	}

	/**
	 * Get the value of a given property.
	 * 
	 * This code is the same for all "getX" methods so we explain it once here. This
	 * is the implementation of style inheritance.
	 * 
	 * First if some event is actually occurring, the alternative styles are
	 * searched first. If these events have unset values for the property, their
	 * parent are then searched.
	 * 
	 * If the value for the property is not found in the alternative styles,
	 * alternative styles parents, or if there is no event occurring actually, this
	 * style is checked.
	 * 
	 * If its value is unset, the parents of this style are checked.
	 * 
	 * Classes are not checked here, they are processed in the
	 * {@link org.graphstream.ui.graphicGraph.StyleGroup} class.
	 * 
	 * @param property
	 *            The style property the value is searched for.
	 */
	public Object getValue(String property, String... events) {
		if (events != null && events.length > 0)// && alternates != null )
		{
			Object o = null;
			int i = events.length - 1;

			do {
				o = getValueForEvent(property, events[i]);
				i--;
			} while (o == null && i >= 0);

			if (o != null)
				return o;
		}

		Object value = values.get(property);

		if (value == null) {
			if (parent != null)
				return parent.style.getValue(property, events);
		}

		return value;
	}

	protected Object getValueForEvent(String property, String event) {
		if (alternates != null) {
			Rule rule = alternates.get(event);

			if (rule != null) {
				Object o = rule.getStyle().values.get(property);

				if (o != null)
					return o;
			}
		} else if (parent != null) {
			return parent.style.getValueForEvent(property, event);
		}

		return null;
	}

	/**
	 * True if the given field exists in this style only (not the parents).
	 * 
	 * @param field
	 *            The field to test.
	 * @return True if this style has a value for the given field.
	 */
	public boolean hasValue(String field, String... events) {
		boolean hasValue = false;

		if (events != null && events.length > 0 && alternates != null) {
			for (String event : events) {
				Rule rule = alternates.get(event);

				if (rule != null) {
					if (rule.getStyle().hasValue(field)) {
						hasValue = true;
						break;
					}
				}
			}
		}

		if (!hasValue) {
			hasValue = (values.get(field) != null);
		}

		return hasValue;
	}

	// Individual style properties.

	/**
	 * How to fill the content of an element.
	 */
	public FillMode getFillMode() {
		return (FillMode) getValue("fill-mode");
	}

	/**
	 * Which color(s) to use for fill modes that use it.
	 */
	public Colors getFillColors() {
		return (Colors) getValue("fill-color");
	}

	public int getFillColorCount() {
		Colors colors = (Colors) getValue("fill-color");

		if (colors != null)
			return colors.size();

		return 0;
	}

	public Color getFillColor(int i) {
		Colors colors = (Colors) getValue("fill-color");

		if (colors != null)
			return colors.get(i);

		return null;
	}

	/**
	 * Which image to use when filling the element contents with it.
	 */
	public String getFillImage() {
		return (String) getValue("fill-image");
	}

	/**
	 * How to draw the element contour.
	 */
	public StrokeMode getStrokeMode() {
		return (StrokeMode) getValue("stroke-mode");
	}

	/**
	 * How to color the element contour.
	 */
	public Colors getStrokeColor() {
		return (Colors) getValue("stroke-color");
	}

	public int getStrokeColorCount() {
		Colors colors = (Colors) getValue("stroke-color");

		if (colors != null)
			return colors.size();

		return 0;
	}

	public Color getStrokeColor(int i) {
		Colors colors = (Colors) getValue("stroke-color");

		if (colors != null)
			return colors.get(i);

		return null;
	}

	/**
	 * Width of the element contour.
	 */
	public Value getStrokeWidth() {
		return (Value) getValue("stroke-width");
	}

	/**
	 * How to draw the shadow of the element.
	 */
	public ShadowMode getShadowMode() {
		return (ShadowMode) getValue("shadow-mode");
	}

	/**
	 * Color(s) of the element shadow.
	 */
	public Colors getShadowColors() {
		return (Colors) getValue("shadow-color");
	}

	public int getShadowColorCount() {
		Colors colors = (Colors) getValue("shadow-color");

		if (colors != null)
			return colors.size();

		return 0;
	}

	public Color getShadowColor(int i) {
		Colors colors = (Colors) getValue("shadow-color");

		if (colors != null)
			return colors.get(i);

		return null;
	}

	/**
	 * Width of the element shadow.
	 */
	public Value getShadowWidth() {
		return (Value) getValue("shadow-width");
	}

	/**
	 * Offset of the element shadow centre according to the element centre.
	 */
	public Values getShadowOffset() {
		return (Values) getValue("shadow-offset");
	}

	/**
	 * Additional space to add inside the element between its contour and its
	 * contents.
	 */
	public Values getPadding() {
		return (Values) getValue("padding");
	}

	/**
	 * How to draw the text of the element.
	 */
	public TextMode getTextMode() {
		return (TextMode) getValue("text-mode");
	}

	/**
	 * How and when to show the text of the element.
	 */
	public TextVisibilityMode getTextVisibilityMode() {
		return (TextVisibilityMode) getValue("text-visibility-mode");
	}

	/**
	 * Visibility values if the text visibility changes.
	 */
	public Values getTextVisibility() {
		return (Values) getValue("text-visibility");
	}

	/**
	 * The text color(s).
	 */
	public Colors getTextColor() {
		return (Colors) getValue("text-color");
	}

	public int getTextColorCount() {
		Colors colors = (Colors) getValue("text-color");

		if (colors != null)
			return colors.size();

		return 0;
	}

	public Color getTextColor(int i) {
		Colors colors = (Colors) getValue("text-color");

		if (colors != null)
			return colors.get(i);

		return null;
	}

	/**
	 * The text font style variation.
	 */
	public TextStyle getTextStyle() {
		return (TextStyle) getValue("text-style");
	}

	/**
	 * The text font.
	 */
	public String getTextFont() {
		return (String) getValue("text-font");
	}

	/**
	 * The text size in points.
	 */
	public Value getTextSize() {
		return (Value) getValue("text-size");
	}

	/**
	 * How to draw the icon around the text (or instead of the text).
	 */
	public IconMode getIconMode() {
		return (IconMode) getValue("icon-mode");
	}

	/**
	 * The icon image to use.
	 */
	public String getIcon() {
		return (String) getValue("icon");
	}

	/**
	 * How and when to show the element.
	 */
	public VisibilityMode getVisibilityMode() {
		return (VisibilityMode) getValue("visibility-mode");
	}

	/**
	 * The element visibility if it is variable.
	 */
	public Values getVisibility() {
		return (Values) getValue("visibility");
	}

	/**
	 * How to size the element.
	 */
	public SizeMode getSizeMode() {
		return (SizeMode) getValue("size-mode");
	}

	/**
	 * The element dimensions.
	 */
	public Values getSize() {
		return (Values) getValue("size");
	}

	/**
	 * The element polygonal shape.
	 */
	public Values getShapePoints() {
		return (Values) getValue("shape-points");
	}

	/**
	 * How to align the text according to the element centre.
	 */
	public TextAlignment getTextAlignment() {
		return (TextAlignment) getValue("text-alignment");
	}

	public TextBackgroundMode getTextBackgroundMode() {
		return (TextBackgroundMode) getValue("text-background-mode");
	}

	public Colors getTextBackgroundColor() {
		return (Colors) getValue("text-background-color");
	}

	public Color getTextBackgroundColor(int i) {
		Colors colors = (Colors) getValue("text-background-color");

		if (colors != null)
			return colors.get(i);

		return null;
	}

	/**
	 * Offset of the text from its computed position.
	 */
	public Values getTextOffset() {
		return (Values) getValue("text-offset");
	}

	/**
	 * Padding of the text inside its background, if any.
	 */
	public Values getTextPadding() {
		return (Values) getValue("text-padding");
	}

	/**
	 * The element shape.
	 */
	public Shape getShape() {
		return (Shape) getValue("shape");
	}

	/**
	 * The element JComponent type if available.
	 */
	public JComponents getJComponent() {
		return (JComponents) getValue("jcomponent");
	}

	/**
	 * How to orient a sprite according to its attachement.
	 */
	public SpriteOrientation getSpriteOrientation() {
		return (SpriteOrientation) getValue("sprite-orientation");
	}

	/**
	 * The shape of edges arrows.
	 */
	public ArrowShape getArrowShape() {
		return (ArrowShape) getValue("arrow-shape");
	}

	/**
	 * Image to use for the arrow.
	 */
	public String getArrowImage() {
		return (String) getValue("arrow-image");
	}

	/**
	 * Edge arrow dimensions.
	 */
	public Values getArrowSize() {
		return (Values) getValue("arrow-size");
	}

	/**
	 * Colour of all non-graph, non-edge, non-node, non-sprite things.
	 */
	public Colors getCanvasColor() {
		return (Colors) getValue("canvas-color");
	}

	public int getCanvasColorCount() {
		Colors colors = (Colors) getValue("canvas-color");

		if (colors != null)
			return colors.size();

		return 0;
	}

	public Color getCanvasColor(int i) {
		Colors colors = (Colors) getValue("canvas-color");

		if (colors != null)
			return colors.get(i);

		return null;
	}

	public Integer getZIndex() {
		return (Integer) getValue("z-index");
	}

	// Commands

	/**
	 * Set the default values for each setting.
	 */
	public void setDefaults() {
		Colors fillColor = new Colors();
		Colors strokeColor = new Colors();
		Colors shadowColor = new Colors();
		Colors textColor = new Colors();
		Colors canvasColor = new Colors();
		Colors textBgColor = new Colors();

		fillColor.add(Color.BLACK);
		strokeColor.add(Color.BLACK);
		shadowColor.add(Color.GRAY);
		textColor.add(Color.BLACK);
		canvasColor.add(Color.WHITE);
		textBgColor.add(Color.WHITE);

		values.put("z-index", new Integer(0));

		values.put("fill-mode", FillMode.PLAIN);
		values.put("fill-color", fillColor);
		values.put("fill-image", null);

		values.put("stroke-mode", StrokeMode.NONE);
		values.put("stroke-color", strokeColor);
		values.put("stroke-width", new Value(Units.PX, 1));

		values.put("shadow-mode", ShadowMode.NONE);
		values.put("shadow-color", shadowColor);
		values.put("shadow-width", new Value(Units.PX, 3));
		values.put("shadow-offset", new Values(Units.PX, 3, 3));

		values.put("padding", new Values(Units.PX, 0, 0, 0));

		values.put("text-mode", TextMode.NORMAL);
		values.put("text-visibility-mode", TextVisibilityMode.NORMAL);
		values.put("text-visibility", null);
		values.put("text-color", textColor);
		values.put("text-style", TextStyle.NORMAL);
		values.put("text-font", "default");
		values.put("text-size", new Value(Units.PX, 10));
		values.put("text-alignment", TextAlignment.CENTER);
		values.put("text-background-mode", TextBackgroundMode.NONE);
		values.put("text-background-color", textBgColor);
		values.put("text-offset", new Values(Units.PX, 0, 0));
		values.put("text-padding", new Values(Units.PX, 0, 0));

		values.put("icon-mode", IconMode.NONE);
		values.put("icon", null);

		values.put("visibility-mode", VisibilityMode.NORMAL);
		values.put("visibility", null);

		values.put("size-mode", SizeMode.NORMAL);
		values.put("size", new Values(Units.PX, 10, 10, 10));

		values.put("shape", Shape.CIRCLE);
		values.put("shape-points", null);
		values.put("jcomponent", null);

		values.put("sprite-orientation", SpriteOrientation.NONE);

		values.put("arrow-shape", ArrowShape.ARROW);
		values.put("arrow-size", new Values(Units.PX, 8, 4));
		values.put("arrow-image", null);

		values.put("canvas-color", canvasColor);

	}

	/**
	 * Copy all the settings of the other style that are set, excepted the parent.
	 * Only the settings that have a value (different from "unset") are copied. The
	 * parent field is never copied.
	 * 
	 * @param other
	 *            Another style.
	 */
	public void augment(Style other) {
		if (other != this) {
			augmentField("z-index", other);
			augmentField("fill-mode", other);
			augmentField("fill-color", other);
			augmentField("fill-image", other);

			augmentField("stroke-mode", other);
			augmentField("stroke-color", other);
			augmentField("stroke-width", other);

			augmentField("shadow-mode", other);
			augmentField("shadow-color", other);
			augmentField("shadow-width", other);
			augmentField("shadow-offset", other);

			augmentField("padding", other);

			augmentField("text-mode", other);
			augmentField("text-visibility-mode", other);
			augmentField("text-visibility", other);
			augmentField("text-color", other);
			augmentField("text-style", other);
			augmentField("text-font", other);
			augmentField("text-size", other);
			augmentField("text-alignment", other);
			augmentField("text-background-mode", other);
			augmentField("text-background-color", other);
			augmentField("text-offset", other);
			augmentField("text-padding", other);

			augmentField("icon-mode", other);
			augmentField("icon", other);

			augmentField("visibility-mode", other);
			augmentField("visibility", other);

			augmentField("size-mode", other);
			augmentField("size", other);

			augmentField("shape", other);
			augmentField("shape-points", other);
			augmentField("jcomponent", other);

			augmentField("sprite-orientation", other);

			augmentField("arrow-shape", other);
			augmentField("arrow-size", other);
			augmentField("arrow-image", other);

			augmentField("canvas-color", other);
		}
	}

	protected void augmentField(String field, Style other) {
		Object value = other.values.get(field);

		if (value != null) {
			if (value instanceof Value)
				setValue(field, new Value((Value) value));
			else if (value instanceof Values)
				setValue(field, new Values((Values) value));
			else if (value instanceof Colors)
				setValue(field, new Colors((Colors) value));
			else
				setValue(field, value);
		}
	}

	/**
	 * Set or change the parent of the style.
	 * 
	 * @param parent
	 *            The new parent.
	 */
	public void reparent(Rule parent) {
		this.parent = parent;
	}

	/**
	 * Add an alternative style for specific events.
	 * 
	 * @param event
	 *            The event that triggers the alternate style.
	 * @param alternateStyle
	 *            The alternative style.
	 */
	public void addAlternateStyle(String event, Rule alternateStyle) {
		if (alternates == null)
			alternates = new HashMap<String, Rule>();

		alternates.put(event, alternateStyle);
	}

	// Commands -- Setters

	public void setValue(String field, Object value) {
		values.put(field, value);
	}

	// Utility

	@Override
	public String toString() {
		return toString(-1);
	}

	public String toString(int level) {
		StringBuilder builder = new StringBuilder();
		String prefix = "";
		String sprefix = "    ";

		if (level > 0) {
			for (int i = 0; i < level; i++)
				prefix += "    ";
		}

		// builder.append( String.format( "%s%s%n", prefix, super.toString() )
		// );

		if (parent != null) {
			Rule p = parent;

			while (!(p == null)) {
				builder.append(String.format(" -> %s", p.selector.toString()));
				p = p.getStyle().getParent();
			}

		}

		builder.append(String.format("%n"));

		Iterator<String> i = values.keySet().iterator();

		while (i.hasNext()) {
			String key = i.next();
			Object o = values.get(key);

			if (o instanceof ArrayList<?>) {
				ArrayList<?> array = (ArrayList<?>) o;

				if (array.size() > 0) {
					builder.append(String.format("%s%s%s%s: ", prefix, sprefix, sprefix, key));

					for (Object p : array)
						builder.append(String.format("%s ", p.toString()));

					builder.append(String.format("%n"));
				} else {
					builder.append(String.format("%s%s%s%s: <empty>%n", prefix, sprefix, sprefix, key));
				}
			} else {
				builder.append(String.format("%s%s%s%s: %s%n", prefix, sprefix, sprefix, key,
						o != null ? o.toString() : "<null>"));
			}
		}

		if (alternates != null && alternates.size() > 0) {
			for (Rule rule : alternates.values()) {
				// We use "level-1" to ensure that these styles line up with those above
				builder.append(rule.toString(level - 1));
			}
		}

		/*
		 * if( level >= 0 ) { if( parent != null ) { String rec = parent.style.toString(
		 * level + 1 );
		 * 
		 * builder.append( rec ); } }
		 */
		String res = builder.toString();

		if (res.length() == 0)
			return String.format("%s%s<empty>%n", prefix, prefix);

		return res;
	}
}