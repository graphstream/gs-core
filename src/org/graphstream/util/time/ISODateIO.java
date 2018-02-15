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
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.util.time;

import java.text.ParseException;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.graphstream.util.time.ISODateComponent.TextComponent;

/**
 * Scanner for date in ISO/IEC 9899:1999 format. The scanner takes a format and
 * then is able to parse timestamp in the given format.
 * 
 * The <i>parse()</i> return a {@link java.util.Calendar} for convenience.
 * 
 * Format of the scanner can be composed of %? directive which define components
 * of the time. These directives are listed below. For example, the format "%F
 * %T", which is equivalent to "%Y-%m-%d %H:%M:%S" can parse the following
 * timestamp: "2010-12-09 03:45:39";
 * 
 * <dl>
 * <dt>%a</dt>
 * <dd>locale's abbreviated weekday name</dd>
 * <dt>%A</dt>
 * <dd>locale's weekday name</dd>
 * <dt>%b</dt>
 * <dd>locale's abbreviated month name</dd>
 * <dt>%B</dt>
 * <dd>locale's month name</dd>
 * <dt>%c</dt>
 * <dd>locale's date and time representation</dd>
 * <dt>%C</dt>
 * <dd>two first digits of full year as an integer (00-99)</dd>
 * <dt>%d</dt>
 * <dd>day of the month (01-31)</dd>
 * <dt>%D</dt>
 * <dd>%m/%d/%y</dd>
 * <dt>%e</dt>
 * <dd>day of the month (1-31)</dd>
 * <dt>%F</dt>
 * <dd>%Y-%m-%d</dd>
 * <dt>%g</dt>
 * <dd>last 2 digits of the week-based year (00-99)</dd>
 * <dt>%G</dt>
 * <dd>"week-based year as a decimal number</dd>
 * <dt>%h</dt>
 * <dd>%b</dd>
 * <dt>%H</dt>
 * <dd>hour (24-hour clock) as a decimal number (00-23)</dd>
 * <dt>%I</dt>
 * <dd>hour (12-hour clock) as a decimal number (01-12)</dd>
 * <dt>%j</dt>
 * <dd>day of the year as a decimal number (001-366)</dd>
 * <dt>%k</dt>
 * <dd>milliseconds as a decimal number (001-999)</dd>
 * <dt>%K</dt>
 * <dd>milliseconds since the epoch</dd>
 * <dt>%m</dt>
 * <dd>month as a decimal number (01-12)</dd>
 * <dt>%M</dt>
 * <dd>minute as a decimal number (00-59)</dd>
 * <dt>%n</dt>
 * <dd>\n</dd>
 * <dt>%p</dt>
 * <dd>locale-s equivalent of the AM/PM</dd>
 * <dt>%r</dt>
 * <dd>locale's 12-hour clock time</dd>
 * <dt>%R</dt>
 * <dd>%H:%M</dd>
 * <dt>%S</dt>
 * <dd>second as a decimal number (00-60)</dd>
 * <dt>%t</dt>
 * <dd>\t</dd>
 * <dt>%T</dt>
 * <dd>%H:%M:%S</dd>
 * <dt>%u</dt>
 * <dd>ISO 8601 weekday as a decimal number (1-7)</dd>
 * <dt>%U</dt>
 * <dd>week number of the year as a decimal number (00-53)</dd>
 * <dt>%V</dt>
 * <dd>ISO 8601 week number as a decimal number (01-53)</dd>
 * <dt>%w</dt>
 * <dd>weekday as a decimal number (0-6)</dd>
 * <dt>%W</dt>
 * <dd>week number of the year as a decimal number (00-53)</dd>
 * <dt>%x</dt>
 * <dd>locale's date representation</dd>
 * <dt>%X</dt>
 * <dd>locale's time representation</dd>
 * <dt>%y</dt>
 * <dd>last 2 digits of the year as a decimal number (00-99)</dd>
 * <dt>%Y</dt>
 * <dd>year as a decimal number</dd>
 * <dt>%z</dt>
 * <dd>offset from UTC in the ISO 8601 format</dd>
 * <dt>%Z</dt>
 * <dd>locale's time zone name of abbreviation or empty</dd>
 * </dl>
 * 
 * @author Guilhelm Savin
 */
public class ISODateIO {

	private static final ISODateComponent[] KNOWN_COMPONENTS = { ISODateComponent.ABBREVIATED_WEEKDAY_NAME,
			ISODateComponent.FULL_WEEKDAY_NAME, ISODateComponent.ABBREVIATED_MONTH_NAME,
			ISODateComponent.FULL_MONTH_NAME, ISODateComponent.LOCALE_DATE_AND_TIME, ISODateComponent.CENTURY,
			ISODateComponent.DAY_OF_MONTH_2_DIGITS, ISODateComponent.DATE, ISODateComponent.DAY_OF_MONTH,
			ISODateComponent.DATE_ISO8601, ISODateComponent.WEEK_BASED_YEAR_2_DIGITS,
			ISODateComponent.WEEK_BASED_YEAR_4_DIGITS, ISODateComponent.ABBREVIATED_MONTH_NAME_ALIAS,
			ISODateComponent.HOUR_OF_DAY, ISODateComponent.HOUR, ISODateComponent.DAY_OF_YEAR,
			ISODateComponent.MILLISECOND, ISODateComponent.EPOCH, ISODateComponent.MONTH, ISODateComponent.MINUTE,
			ISODateComponent.NEW_LINE, ISODateComponent.AM_PM, ISODateComponent.LOCALE_CLOCK_TIME_12_HOUR,
			ISODateComponent.HOUR_AND_MINUTE, ISODateComponent.SECOND, ISODateComponent.TABULATION,
			ISODateComponent.TIME_ISO8601, ISODateComponent.DAY_OF_WEEK_1_7, ISODateComponent.WEEK_OF_YEAR_FROM_SUNDAY,
			ISODateComponent.WEEK_NUMBER_ISO8601, ISODateComponent.DAY_OF_WEEK_0_6,
			ISODateComponent.WEEK_OF_YEAR_FROM_MONDAY, ISODateComponent.LOCALE_DATE_REPRESENTATION,
			ISODateComponent.LOCALE_TIME_REPRESENTATION, ISODateComponent.YEAR_2_DIGITS, ISODateComponent.YEAR_4_DIGITS,
			ISODateComponent.UTC_OFFSET, ISODateComponent.LOCALE_TIME_ZONE_NAME, ISODateComponent.PERCENT };

	/**
	 * List of components, build from a string format. Some of these components can
	 * just be text.
	 */
	protected LinkedList<ISODateComponent> components;
	/**
	 * The regular expression builds from the components.
	 */
	protected Pattern pattern;

	/**
	 * Create a scanner with default format "%K".
	 * 
	 * @throws ParseException
	 */
	public ISODateIO() throws ParseException {
		this("%K");
	}

	/**
	 * Create a new scanner with a given format.
	 * 
	 * @param format
	 *            format of the scanner.
	 * @throws ParseException
	 *             if bad directives found
	 */
	public ISODateIO(String format) throws ParseException {
		setFormat(format);
	}

	/**
	 * Get the current pattern used to parse timestamp.
	 * 
	 * @return a regular expression as a string
	 */
	public Pattern getPattern() {
		return pattern;
	}

	/**
	 * Build a list of component from a string.
	 * 
	 * @param format
	 *            format of the scanner
	 * @return a list of components found in the string format
	 * @throws ParseException
	 *             if invalid component found
	 */
	protected LinkedList<ISODateComponent> findComponents(String format) throws ParseException {
		LinkedList<ISODateComponent> components = new LinkedList<ISODateComponent>();
		int offset = 0;

		while (offset < format.length()) {
			if (format.charAt(offset) == '%') {
				boolean found = false;
				for (int i = 0; !found && i < KNOWN_COMPONENTS.length; i++) {
					if (format.startsWith(KNOWN_COMPONENTS[i].getDirective(), offset)) {
						found = true;
						if (KNOWN_COMPONENTS[i].isAlias()) {
							LinkedList<ISODateComponent> sub = findComponents(KNOWN_COMPONENTS[i].getReplacement());
							components.addAll(sub);
						} else
							components.addLast(KNOWN_COMPONENTS[i]);

						offset += KNOWN_COMPONENTS[i].getDirective().length();
					}
				}
				if (!found)
					throw new ParseException("unknown identifier", offset);
			} else {
				int from = offset;
				while (offset < format.length() && format.charAt(offset) != '%')
					offset++;
				components.addLast(new TextComponent(format.substring(from, offset)));
			}
		}

		return components;
	}

	/**
	 * Build a regular expression from the components of the scanner.
	 */
	protected void buildRegularExpression() {
		String pattern = "";

		for (int i = 0; i < components.size(); i++) {
			Object c = components.get(i);
			String regexValue;
			if (c instanceof ISODateComponent)
				regexValue = ((ISODateComponent) c).getReplacement();
			else
				regexValue = c.toString();

			pattern += "(" + regexValue + ")";
		}

		this.pattern = Pattern.compile(pattern);
	}

	/**
	 * Set the format of this scanner.
	 * 
	 * @param format
	 *            new format of the scanner
	 * @throws ParseException
	 *             if an error is found in the new format
	 */
	public void setFormat(String format) throws ParseException {
		components = findComponents(format);
		buildRegularExpression();
	}

	/**
	 * Parse a string which should be in the scanner format. If not, null is
	 * returned.
	 * 
	 * @param time
	 *            timestamp in the scanner format
	 * @return a calendar modeling the time value or null if invalid format
	 */
	public Calendar parse(String time) {
		Calendar cal = Calendar.getInstance();
		Matcher match = pattern.matcher(time);

		if (match.matches()) {
			for (int i = 0; i < components.size(); i++)
				components.get(i).set(match.group(i + 1), cal);
		} else
			return null;

		return cal;
	}

	/**
	 * Convert a calendar into a string according to the format of this object.
	 * 
	 * @param calendar
	 *            the calendar to convert
	 * @return a string modeling the calendar.
	 */
	public String toString(Calendar calendar) {
		StringBuffer buffer = new StringBuffer();

		for (int i = 0; i < components.size(); i++)
			buffer.append(components.get(i).get(calendar));

		return buffer.toString();
	}
}
