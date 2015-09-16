/*
 * Copyright 2006 - 2015
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

import java.io.IOException;
import java.util.HashMap;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

public class FileSinkGraphML extends FileSinkBase {

	protected void outputEndOfFile() throws IOException {
		print("</graphml>\n");
	}

	protected void outputHeader() throws IOException {
		print("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		print("<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\"\n");
		print("\t xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
		print("\t xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns\n");
		print("\t   http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd\">\n");
	}

	private void print(String format, Object... args) throws IOException {
		output.write(String.format(format, args));
	}

	@Override
	protected void exportGraph(Graph g) {
		try {
			int attribute = 0;
			HashMap<String, String> nodeAttributes = new HashMap<String, String>();
			HashMap<String, String> edgeAttributes = new HashMap<String, String>();

			for (Node n : g.getEachNode()) {
				for (String k : n.getAttributeKeySet()) {
					if (!nodeAttributes.containsKey(k)) {
						Object value = n.getAttribute(k);
						String type;

						if (value == null) {
							continue;
						}

						String id = String.format("attr%04X", attribute++);

						if (value instanceof Boolean) {
							type = "boolean";
						} else if (value instanceof Long) {
							type = "long";
						} else if (value instanceof Integer) {
							type = "int";
						} else if (value instanceof Double) {
							type = "double";
						} else if (value instanceof Float) {
							type = "float";
						} else {
							type = "string";
						}

						nodeAttributes.put(k, id);

						print(
							"\t<key id=\"%s\" for=\"node\" attr.name=\"%s\" attr.type=\"%s\"/>\n",
							id, k, type);
					}
				}
			}

			for (Edge n : g.getEachEdge()) {
				for (String k : n.getAttributeKeySet()) {
					if (!edgeAttributes.containsKey(k)) {
						Object value = n.getAttribute(k);
						String type;

						if (value == null) {
							continue;
						}

						String id = String.format("attr%04X", attribute++);

						if (value instanceof Boolean) {
							type = "boolean";
						} else if (value instanceof Long) {
							type = "long";
						} else if (value instanceof Integer) {
							type = "int";
						} else if (value instanceof Double) {
							type = "double";
						} else if (value instanceof Float) {
							type = "float";
						} else {
							type = "string";
						}

						edgeAttributes.put(k, id);
						print(
							"\t<key id=\"%s\" for=\"edge\" attr.name=\"%s\" attr.type=\"%s\"/>\n",
							id, k, type);
					}
				}
			}

			print("\t<graph id=\"%s\" edgedefault=\"undirected\">\n", g.getId());

			for (Node n : g.getEachNode()) {
				print("\t\t<node id=\"%s\">\n", n.getId());
				for (String k : n.getAttributeKeySet()) {
					print("\t\t\t<data key=\"%s\">%s</data>\n", nodeAttributes
						.get(k), n.getAttribute(k).toString());
				}
				print("\t\t</node>\n");
			}
			for (Edge e : g.getEachEdge()) {
				print(
					"\t\t<edge id=\"%s\" source=\"%s\" target=\"%s\" directed=\"%s\">\n",
					e.getId(), e.getSourceNode().getId(), e.getTargetNode()
					.getId(), e.isDirected());
				for (String k : e.getAttributeKeySet()) {
					print("\t\t\t<data key=\"%s\">%s</data>\n", edgeAttributes
						.get(k), e.getAttribute(k).toString());
				}
				print("\t\t</edge>\n");
			}
			print("\t</graph>\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void edgeAttributeAdded(String sourceId, long timeId, String edgeId,
		String attribute, Object value) {
		throw new UnsupportedOperationException();
	}

	public void edgeAttributeChanged(String sourceId, long timeId,
		String edgeId, String attribute, Object oldValue, Object newValue) {
		throw new UnsupportedOperationException();
	}

	public void edgeAttributeRemoved(String sourceId, long timeId,
		String edgeId, String attribute) {
		throw new UnsupportedOperationException();
	}

	public void graphAttributeAdded(String sourceId, long timeId,
		String attribute, Object value) {
		throw new UnsupportedOperationException();
	}

	public void graphAttributeChanged(String sourceId, long timeId,
		String attribute, Object oldValue, Object newValue) {
		throw new UnsupportedOperationException();
	}

	public void graphAttributeRemoved(String sourceId, long timeId,
		String attribute) {
		throw new UnsupportedOperationException();
	}

	public void nodeAttributeAdded(String sourceId, long timeId, String nodeId,
		String attribute, Object value) {
		throw new UnsupportedOperationException();
	}

	public void nodeAttributeChanged(String sourceId, long timeId,
		String nodeId, String attribute, Object oldValue, Object newValue) {
		throw new UnsupportedOperationException();
	}

	public void nodeAttributeRemoved(String sourceId, long timeId,
		String nodeId, String attribute) {
		throw new UnsupportedOperationException();
	}

	public void edgeAdded(String sourceId, long timeId, String edgeId,
		String fromNodeId, String toNodeId, boolean directed) {
		throw new UnsupportedOperationException();
	}

	public void edgeRemoved(String sourceId, long timeId, String edgeId) {
		throw new UnsupportedOperationException();
	}

	public void graphCleared(String sourceId, long timeId) {
		throw new UnsupportedOperationException();
	}

	public void nodeAdded(String sourceId, long timeId, String nodeId) {
		throw new UnsupportedOperationException();
	}

	public void nodeRemoved(String sourceId, long timeId, String nodeId) {
		throw new UnsupportedOperationException();
	}

	public void stepBegins(String sourceId, long timeId, double step) {
		throw new UnsupportedOperationException();
	}

}
