/*
 * Copyright 2006 - 2011 
 *     Julien Baudry	<julien.baudry@graphstream-project.org>
 *     Antoine Dutot	<antoine.dutot@graphstream-project.org>
 *     Yoann Pigné		<yoann.pigne@graphstream-project.org>
 *     Guilhelm Savin	<guilhelm.savin@graphstream-project.org>
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
import java.util.Map;
import java.util.Random;

/**
 * This class intends to output a graph (a snapshot of it) into a <a
 * href="http://sourceforge.net/projects/pgf/">TikZ</a> drawing.
 * 
 * <p>
 * <b>This is a work in progress, it does not yet work properly. Do not try to
 * use it yet.</b>
 * </p>
 */
public class FileSinkTikz extends FileSinkBase {
	public enum NodeShape {
		triangle("triangle"), circle("circle"), rectangle("rectangle"), roundedRectangle(
				"rectangle,rounded corners");

		String code;

		private NodeShape(String code) {
			this.code = code;
		}

		public String getCode() {
			return code;
		}
	}

	class TikZColor {
		float alpha;
		float red;
		float green;
		float blue;
	}

	class NodeStyle {
		NodeShape shape;
		float width;
		String label;
		float opacity;

		TikZColor fillColor;
		TikZColor drawColor;
		TikZColor textColor;

		public NodeStyle() {

		}
	}

	class EdgeStyle {
		float width;
		TikZColor color;
		String src;
		String trg;

		public EdgeStyle(String src, String trg, boolean directed) {

		}
	}

	Map<String, NodeStyle> nodes;
	Map<String, EdgeStyle> edges;

	Random random;

	@Override
	protected void outputEndOfFile() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void outputHeader() throws IOException {
		random = new Random();

		nodes = new HashMap<String, NodeStyle>();
		edges = new HashMap<String, EdgeStyle>();
	}

	public void edgeAttributeAdded(String graphId, long timeId, String edgeId,
			String attribute, Object value) {

	}

	public void edgeAttributeChanged(String graphId, long timeId,
			String edgeId, String attribute, Object oldValue, Object newValue) {
		// TODO Auto-generated method stub

	}

	public void edgeAttributeRemoved(String graphId, long timeId,
			String edgeId, String attribute) {
		// TODO Auto-generated method stub

	}

	public void graphAttributeAdded(String graphId, long timeId,
			String attribute, Object value) {
		// TODO Auto-generated method stub

	}

	public void graphAttributeChanged(String graphId, long timeId,
			String attribute, Object oldValue, Object newValue) {
		// TODO Auto-generated method stub

	}

	public void graphAttributeRemoved(String graphId, long timeId,
			String attribute) {
		// TODO Auto-generated method stub

	}

	public void nodeAttributeAdded(String graphId, long timeId, String nodeId,
			String attribute, Object value) {
		// TODO Auto-generated method stub

	}

	public void nodeAttributeChanged(String graphId, long timeId,
			String nodeId, String attribute, Object oldValue, Object newValue) {
		// TODO Auto-generated method stub

	}

	public void nodeAttributeRemoved(String graphId, long timeId,
			String nodeId, String attribute) {
		// TODO Auto-generated method stub

	}

	public void edgeAdded(String graphId, long timeId, String edgeId,
			String fromNodeId, String toNodeId, boolean directed) {
		if (!edges.containsKey(edgeId))
			edges.put(edgeId, new EdgeStyle(fromNodeId, toNodeId, directed));
	}

	public void edgeRemoved(String graphId, long timeId, String edgeId) {
		if (edges.containsKey(edgeId))
			edges.remove(edgeId);
	}

	public void graphCleared(String graphId, long timeId) {
		// TODO Auto-generated method stub

	}

	public void nodeAdded(String graphId, long timeId, String nodeId) {
		if (!nodes.containsKey(nodeId))
			nodes.put(nodeId, new NodeStyle());
	}

	public void nodeRemoved(String graphId, long timeId, String nodeId) {
		if (nodes.containsKey(nodeId))
			nodes.remove(nodeId);
	}

	public void stepBegins(String graphId, long timeId, double time) {
		// TODO Auto-generated method stub

	}
}
