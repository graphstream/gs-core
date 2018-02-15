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
 * @since 2011-10-04
 * 
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Antoine Dutot <antoine.dutot@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.util.time;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Defines components of {@link ISODateIO}.
 * 
 */
public abstract class ISODateComponent {

	/**
	 * Directives shortcut of the component. This property can not be changed.
	 */
	protected final String directive;
	/**
	 * Replacement of the directive. Could be a regular expression. The value catch
	 * will be sent to the component with <i>set(catched_value,Calendar)</i>. This
	 * property can not be changed.
	 */
	protected final String replace;

	/**
	 * Build a new component composed of a directive name ("%.") and a replacement
	 * value.
	 * 
	 * @param directive
	 *            directive name, should start with a leading '%'.
	 * @param replace
	 *            replace the directive with the value given here.
	 */
	public ISODateComponent(String directive, String replace) {
		this.directive = directive;
		this.replace = replace;
	}

	/**
	 * Access to the directive name of the component.
	 * 
	 * @return directive of the component.
	 */
	public String getDirective() {
		return directive;
	}

	/**
	 * Return true if this component is an alias. An alias can contain other
	 * directive name and its replacement should be parse again.
	 * 
	 * @return true if component is an alias.
	 */
	public boolean isAlias() {
		return false;
	}

	/**
	 * Get the replacement value of this component.
	 * 
	 * @return replacement value
	 */
	public String getReplacement() {
		return replace;
	}

	/**
	 * Handle the value catched with the replacement value.
	 * 
	 * @param value
	 *            value matching the replacement string
	 * @param calendar
	 *            calendar we are working on
	 */
	public abstract void set(String value, Calendar calendar);

	/**
	 * Get a string representation of this component for a given calendar.
	 * 
	 * @param calendar
	 *            the calendar
	 * @return string representation of this component.
	 */
	public abstract String get(Calendar calendar);

	/**
	 * Defines an alias component. Such component does nothing else that replace
	 * them directive by another string.
	 */
	public static class AliasComponent extends ISODateComponent {

		public AliasComponent(String shortcut, String replace) {
			super(shortcut, replace);
		}

		public boolean isAlias() {
			return true;
		}

		public void set(String value, Calendar calendar) {
			// Nothing to do
		}

		public String get(Calendar calendar) {
			return "";
		}
	}

	/**
	 * Defines a text component. Such component does nothing else that append text
	 * to the resulting regular expression.
	 */
	public static class TextComponent extends ISODateComponent {
		String unquoted;

		public TextComponent(String value) {
			super(null, Pattern.quote(value));
			unquoted = value;
		}

		public void set(String value, Calendar calendar) {
			// Nothing to do
		}

		public String get(Calendar calendar) {
			return unquoted;
		}
	}

	/**
	 * Defines a component associated with a field of a calendar. When a value is
	 * handled, component will try to set the associated field of the calendar.
	 */
	public static class FieldComponent extends ISODateComponent {
		protected final int field;
		protected final int offset;
		protected final String format;

		public FieldComponent(String shortcut, String replace, int field, String format) {
			this(shortcut, replace, field, 0, format);
		}

		public FieldComponent(String shortcut, String replace, int field, int offset, String format) {
			super(shortcut, replace);
			this.field = field;
			this.offset = offset;
			this.format = format;
		}

		public void set(String value, Calendar calendar) {
			while (value.charAt(0) == '0' && value.length() > 1)
				value = value.substring(1);
			int val = Integer.parseInt(value);
			calendar.set(field, val + offset);
		}

		public String get(Calendar calendar) {
			return String.format(format, calendar.get(field));
		}
	}

	/**
	 * Base for locale-dependent component.
	 */
	protected static abstract class LocaleDependentComponent extends ISODateComponent {
		protected Locale locale;
		protected DateFormatSymbols symbols;

		public LocaleDependentComponent(String shortcut, String replace) {
			this(shortcut, replace, Locale.getDefault());
		}

		public LocaleDependentComponent(String shortcut, String replace, Locale locale) {
			super(shortcut, replace);
			this.locale = locale;
			this.symbols = DateFormatSymbols.getInstance(locale);
		}
	}

	/**
	 * Component handling AM/PM.
	 */
	public static class AMPMComponent extends LocaleDependentComponent {
		public AMPMComponent() {
			super("%p", "AM|PM|am|pm");
		}

		public void set(String value, Calendar calendar) {
			if (value.equalsIgnoreCase(symbols.getAmPmStrings()[Calendar.AM]))
				calendar.set(Calendar.AM_PM, Calendar.AM);
			else if (value.equalsIgnoreCase(symbols.getAmPmStrings()[Calendar.PM]))
				calendar.set(Calendar.AM_PM, Calendar.PM);
		}

		public String get(Calendar calendar) {
			return symbols.getAmPmStrings()[calendar.get(Calendar.AM_PM)];
		}
	}

	/**
	 * Component handling utc offset (+/- 0000).
	 */
	public static class UTCOffsetComponent extends ISODateComponent {
		public UTCOffsetComponent() {
			super("%z", "(?:[-+]\\d{4}|Z)");
		}

		public void set(String value, Calendar calendar) {
			if (value.equals("Z")) {
				calendar.getTimeZone().setRawOffset(0);
			} else {
				String hs = value.substring(1, 3);
				String ms = value.substring(3, 5);
				if (hs.charAt(0) == '0')
					hs = hs.substring(1);
				if (ms.charAt(0) == '0')
					ms = ms.substring(1);

				int i = value.charAt(0) == '+' ? 1 : -1;
				int h = Integer.parseInt(hs);
				int m = Integer.parseInt(ms);

				calendar.getTimeZone().setRawOffset(i * (h * 60 + m) * 60000);
			}
		}

		public String get(Calendar calendar) {
			int offset = calendar.getTimeZone().getRawOffset();
			String sign = "+";

			if (offset < 0) {
				sign = "-";
				offset = -offset;
			}

			offset /= 60000;

			int h = offset / 60;
			int m = offset % 60;

			return String.format("%s%02d%02d", sign, h, m);
		}
	}

	/**
	 * Component handling a number of milliseconds since the epoch (january, 1st
	 * 1970).
	 */
	public static class EpochComponent extends ISODateComponent {
		public EpochComponent() {
			super("%K", "\\d+");
		}

		public void set(String value, Calendar calendar) {
			long e = Long.parseLong(value);
			calendar.setTimeInMillis(e);
		}

		public String get(Calendar calendar) {
			return String.format("%d", calendar.getTimeInMillis());
		}
	}

	/**
	 * Defines a not implemented component. Such components throw an Error if used.
	 */
	public static class NotImplementedComponent extends ISODateComponent {
		public NotImplementedComponent(String shortcut, String replace) {
			super(shortcut, replace);
		}

		public void set(String value, Calendar cal) {
			throw new Error("not implemented component");
		}

		public String get(Calendar calendar) {
			throw new Error("not implemented component");
		}
	}

	public static final ISODateComponent ABBREVIATED_WEEKDAY_NAME = new NotImplementedComponent("%a", "\\w+[.]");
	public static final ISODateComponent FULL_WEEKDAY_NAME = new NotImplementedComponent("%A", "\\w+");
	public static final ISODateComponent ABBREVIATED_MONTH_NAME = new NotImplementedComponent("%b", "\\w+[.]");
	public static final ISODateComponent FULL_MONTH_NAME = new NotImplementedComponent("%B", "\\w+");
	public static final ISODateComponent LOCALE_DATE_AND_TIME = new NotImplementedComponent("%c", null);
	public static final ISODateComponent CENTURY = new NotImplementedComponent("%C", "\\d\\d");
	public static final ISODateComponent DAY_OF_MONTH_2_DIGITS = new FieldComponent("%d", "[012]\\d|3[01]",
			Calendar.DAY_OF_MONTH, "%02d");
	public static final ISODateComponent DATE = new AliasComponent("%D", "%m/%d/%y");
	public static final ISODateComponent DAY_OF_MONTH = new FieldComponent("%e", "\\d|[12]\\d|3[01]",
			Calendar.DAY_OF_MONTH, "%2d");
	public static final ISODateComponent DATE_ISO8601 = new AliasComponent("%F", "%Y-%m-%d");
	public static final ISODateComponent WEEK_BASED_YEAR_2_DIGITS = new FieldComponent("%g", "\\d\\d", Calendar.YEAR,
			"%02d");
	public static final ISODateComponent WEEK_BASED_YEAR_4_DIGITS = new FieldComponent("%G", "\\d{4}", Calendar.YEAR,
			"%04d");
	public static final ISODateComponent ABBREVIATED_MONTH_NAME_ALIAS = new AliasComponent("%h", "%b");
	public static final ISODateComponent HOUR_OF_DAY = new FieldComponent("%H", "[01]\\d|2[0123]", Calendar.HOUR_OF_DAY,
			"%02d");
	public static final ISODateComponent HOUR = new FieldComponent("%I", "0\\d|1[012]", Calendar.HOUR, "%02d");
	public static final ISODateComponent DAY_OF_YEAR = new FieldComponent("%j", "[012]\\d\\d|3[0-5]\\d|36[0-6]",
			Calendar.DAY_OF_YEAR, "%03d");
	public static final ISODateComponent MILLISECOND = new FieldComponent("%k", "\\d{3}", Calendar.MILLISECOND, "%03d");
	public static final ISODateComponent EPOCH = new EpochComponent();
	public static final ISODateComponent MONTH = new FieldComponent("%m", "0[1-9]|1[012]", Calendar.MONTH, -1, "%02d");
	public static final ISODateComponent MINUTE = new FieldComponent("%M", "[0-5]\\d", Calendar.MINUTE, "%02d");
	public static final ISODateComponent NEW_LINE = new AliasComponent("%n", "\n");
	public static final ISODateComponent AM_PM = new AMPMComponent();
	public static final ISODateComponent LOCALE_CLOCK_TIME_12_HOUR = new NotImplementedComponent("%r", "");
	public static final ISODateComponent HOUR_AND_MINUTE = new AliasComponent("%R", "%H:%M");
	public static final ISODateComponent SECOND = new FieldComponent("%S", "[0-5]\\d|60", Calendar.SECOND, "%02d");
	public static final ISODateComponent TABULATION = new AliasComponent("%t", "\t");
	public static final ISODateComponent TIME_ISO8601 = new AliasComponent("%T", "%H:%M:%S");
	public static final ISODateComponent DAY_OF_WEEK_1_7 = new FieldComponent("%u", "[1-7]", Calendar.DAY_OF_WEEK, -1,
			"%1d");
	public static final ISODateComponent WEEK_OF_YEAR_FROM_SUNDAY = new FieldComponent("%U", "[0-4]\\d|5[0123]",
			Calendar.WEEK_OF_YEAR, 1, "%2d");
	public static final ISODateComponent WEEK_NUMBER_ISO8601 = new NotImplementedComponent("%V",
			"0[1-9]|[2-4]\\d|5[0123]");
	public static final ISODateComponent DAY_OF_WEEK_0_6 = new FieldComponent("%w", "[0-6]", Calendar.DAY_OF_WEEK,
			"%01d");
	public static final ISODateComponent WEEK_OF_YEAR_FROM_MONDAY = new FieldComponent("%W", "[0-4]\\d|5[0123]",
			Calendar.WEEK_OF_YEAR, "%02d");
	public static final ISODateComponent LOCALE_DATE_REPRESENTATION = new NotImplementedComponent("%x", "");
	public static final ISODateComponent LOCALE_TIME_REPRESENTATION = new NotImplementedComponent("%X", "");
	public static final ISODateComponent YEAR_2_DIGITS = new FieldComponent("%y", "\\d\\d", Calendar.YEAR, "%02d");
	public static final ISODateComponent YEAR_4_DIGITS = new FieldComponent("%Y", "\\d{4}", Calendar.YEAR, "%04d");
	public static final ISODateComponent UTC_OFFSET = new UTCOffsetComponent();
	public static final ISODateComponent LOCALE_TIME_ZONE_NAME = new NotImplementedComponent("%Z", "\\w*");
	public static final ISODateComponent PERCENT = new AliasComponent("%%", "%");
}
