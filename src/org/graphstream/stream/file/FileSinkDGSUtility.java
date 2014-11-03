package org.graphstream.stream.file;

import java.awt.Color;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Locale;

import org.graphstream.graph.CompoundAttribute;

public class FileSinkDGSUtility {
	protected static String formatStringForQuoting(String str) {
		return str.replaceAll("(^|[^\\\\])\"", "$1\\\\\"");
	}

	protected static String attributeString(String key, Object value, boolean remove) {
		if (key == null || key.length() == 0)
			return null;

		if (remove) {
			return String.format(" -\"%s\"", key);
		} else {
			if (value != null && value.getClass().isArray())
				return String.format(" \"%s\":%s", key, arrayString(value));
			else
				return String.format(" \"%s\":%s", key, valueString(value));
		}
	}

	protected static String arrayString(Object value) {
		if (value != null && value.getClass().isArray()) {
			StringBuilder sb = new StringBuilder();
			sb.append("{");

			if (Array.getLength(value) == 0)
				sb.append("\"\"");
			else
				sb.append(arrayString(Array.get(value, 0)));

			for (int i = 1; i < Array.getLength(value); ++i)
				sb.append(String
						.format(",%s", arrayString(Array.get(value, i))));

			sb.append("}");
			return sb.toString();
		} else {
			return valueString(value);
		}
	}

	protected static String valueString(Object value) {
		if (value == null)
			return "\"\"";

		if (value instanceof CharSequence) {
			if (value instanceof String)
				return String.format("\"%s\"",
						formatStringForQuoting((String) value));
			else
				return String.format("\"%s\"", (CharSequence) value);
		} else if (value instanceof Number) {
			Number nval = (Number) value;

			if (value instanceof Integer || value instanceof Short
					|| value instanceof Byte || value instanceof Long)
				return String.format(Locale.US, "%d", nval.longValue());
			else
				return String.format(Locale.US, "%f", nval.doubleValue());
		} else if (value instanceof Boolean) {
			return String.format(Locale.US, "%b", ((Boolean) value));
		} else if (value instanceof Character) {
			return String.format("\"%c\"", ((Character) value).charValue());
		} else if (value instanceof Object[]) {
			Object array[] = (Object[]) value;
			int n = array.length;
			StringBuffer sb = new StringBuffer();

			if (array.length > 0)
				sb.append(valueString(array[0]));

			for (int i = 1; i < n; i++) {
				sb.append(",");
				sb.append(valueString(array[i]));
			}

			return sb.toString();
		} else if (value instanceof HashMap<?, ?>
				|| value instanceof CompoundAttribute) {
			HashMap<?, ?> hash;

			if (value instanceof CompoundAttribute)
				hash = ((CompoundAttribute) value).toHashMap();
			else
				hash = (HashMap<?, ?>) value;

			return hashToString(hash);
		} else if (value instanceof Color) {
			Color c = (Color) value;
			return String.format("#%02X%02X%02X%02X", c.getRed(), c.getGreen(),
					c.getBlue(), c.getAlpha());
		} else {
			return String.format("\"%s\"", value.toString());
		}
	}

	protected static String hashToString(HashMap<?, ?> hash) {
		StringBuilder sb = new StringBuilder();

		sb.append("[ ");

		for (Object key : hash.keySet()) {
			sb.append(attributeString(key.toString(), hash.get(key), false));
			sb.append(",");
		}

		sb.append(']');

		return sb.toString();
	}
}
