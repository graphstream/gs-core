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
package org.graphstream.stream.file;

import java.awt.Color;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Locale;

import org.graphstream.graph.CompoundAttribute;

/**
 * File output for the DGS (Dynamic Graph Stream) file format.
 */
public class FileSinkDGS extends FileSinkBase {
	// Attribute

	/**
	 * A shortcut to the output.
	 */
	protected PrintWriter out;

	protected String graphName = "";

	// Command

	@Override
	protected void outputHeader() throws IOException {
		out = (PrintWriter) output;

		out.printf("DGS004%n");

		if (graphName.length() <= 0)
			out.printf("null 0 0%n");
		else
			out.printf("\"%s\" 0 0%n", formatStringForQuoting(graphName));
	}

	@Override
	protected void outputEndOfFile() throws IOException {
		// NOP
	}

	public void edgeAttributeAdded(String graphId, long timeId, String edgeId,
			String attribute, Object value) {
		edgeAttributeChanged(graphId, timeId, edgeId, attribute, null, value);
	}

	public void edgeAttributeChanged(String graphId, long timeId,
			String edgeId, String attribute, Object oldValue, Object newValue) {
		out.printf("ce \"%s\" %s%n", formatStringForQuoting(edgeId),
				attributeString(attribute, newValue, false));
	}

	public void edgeAttributeRemoved(String graphId, long timeId,
			String edgeId, String attribute) {
		out.printf("ce \"%s\" %s%n", formatStringForQuoting(edgeId),
				attributeString(attribute, null, true));
	}

	public void graphAttributeAdded(String graphId, long timeId,
			String attribute, Object value) {
		graphAttributeChanged(graphId, timeId, attribute, null, value);
	}

	public void graphAttributeChanged(String graphId, long timeId,
			String attribute, Object oldValue, Object newValue) {
		out.printf("cg %s%n", attributeString(attribute, newValue, false));
	}

	public void graphAttributeRemoved(String graphId, long timeId,
			String attribute) {
		out.printf("cg %s%n", attributeString(attribute, null, true));
	}

	public void nodeAttributeAdded(String graphId, long timeId, String nodeId,
			String attribute, Object value) {
		nodeAttributeChanged(graphId, timeId, nodeId, attribute, null, value);
	}

	public void nodeAttributeChanged(String graphId, long timeId,
			String nodeId, String attribute, Object oldValue, Object newValue) {
		out.printf("cn \"%s\" %s%n", formatStringForQuoting(nodeId),
				attributeString(attribute, newValue, false));
	}

	public void nodeAttributeRemoved(String graphId, long timeId,
			String nodeId, String attribute) {
		out.printf("cn \"%s\" %s%n", formatStringForQuoting(nodeId),
				attributeString(attribute, null, true));
	}

	public void edgeAdded(String graphId, long timeId, String edgeId,
			String fromNodeId, String toNodeId, boolean directed) {
		edgeId = formatStringForQuoting(edgeId);
		fromNodeId = formatStringForQuoting(fromNodeId);
		toNodeId = formatStringForQuoting(toNodeId);

		out.printf("ae \"%s\" \"%s\" %s \"%s\"%n", edgeId, fromNodeId,
				directed ? ">" : "", toNodeId);
	}

	public void edgeRemoved(String graphId, long timeId, String edgeId) {
		out.printf("de \"%s\"%n", formatStringForQuoting(edgeId));
	}

	public void graphCleared(String graphId, long timeId) {
		out.printf("cl%n");
	}

	public void nodeAdded(String graphId, long timeId, String nodeId) {
		out.printf("an \"%s\"%n", formatStringForQuoting(nodeId));
	}

	public void nodeRemoved(String graphId, long timeId, String nodeId) {
		out.printf("dn \"%s\"%n", formatStringForQuoting(nodeId));
	}

	public void stepBegins(String graphId, long timeId, double step) {
		out.printf(Locale.US, "st %f%n", step);
	}

	// Utility

	protected String formatStringForQuoting(String str) {
		return str.replaceAll("(^|[^\\\\])\"", "$1\\\\\"");
	}

	protected String attributeString(String key, Object value, boolean remove) {
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

	protected String arrayString(Object value) {
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

	protected String valueString(Object value) {
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

	protected String hashToString(HashMap<?, ?> hash) {
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