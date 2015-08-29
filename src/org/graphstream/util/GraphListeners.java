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
package org.graphstream.util;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Element.ElementType;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.AbstractElement.AttributeChangeEvent;
import org.graphstream.stream.Pipe;
import org.graphstream.stream.SourceBase;
import org.graphstream.stream.sync.SinkTime;

/**
 * Helper object to handle events produced by a graph.
 *
 */
public class GraphListeners extends SourceBase implements Pipe {
	SinkTime sinkTime;
	boolean reentrant;
	Graph g;

	public GraphListeners(Graph g) {
		super(g.getId());

		this.sinkTime = new SinkTime();
		this.sourceTime.setSinkTime(sinkTime);
		this.g = g;
	}

	public long newEvent() {
		return sourceTime.newEvent();
	}

	public void sendAttributeChangedEvent(String eltId, ElementType eltType,
		String attribute, AttributeChangeEvent event, Object oldValue,
		Object newValue) {
		//
		// Attributes with name beginnig with a dot are hidden.
		//
		if (reentrant || attribute.charAt(0) == '.') {
			return;
		}

		sendAttributeChangedEvent(sourceId, newEvent(), eltId, eltType,
			attribute, event, oldValue, newValue);
	}

	public void sendNodeAdded(String nodeId) {
		if (reentrant) {
			return;
		}

		sendNodeAdded(sourceId, newEvent(), nodeId);
	}

	public void sendNodeRemoved(String nodeId) {
		if (reentrant) {
			return;
		}

		sendNodeRemoved(sourceId, newEvent(), nodeId);
	}

	public void sendEdgeAdded(String edgeId, String source, String target,
		boolean directed) {
		if (reentrant) {
			return;
		}

		sendEdgeAdded(sourceId, newEvent(), edgeId, source, target, directed);
	}

	public void sendEdgeRemoved(String edgeId) {
		if (reentrant) {
			return;
		}

		sendEdgeRemoved(sourceId, newEvent(), edgeId);
	}

	public void sendGraphCleared() {
		if (reentrant) {
			return;
		}

		sendGraphCleared(sourceId, newEvent());
	}

	public void sendStepBegins(double step) {
		if (reentrant) {
			return;
		}

		sendStepBegins(sourceId, newEvent(), step);
	}

	@Override
	public void edgeAttributeAdded(String sourceId, long timeId, String edgeId,
		String attribute, Object value) {
		if (sinkTime.isNewEvent(sourceId, timeId)) {
			Edge edge = g.getEdge(edgeId);
			if (edge != null) {
				reentrant = true;

				try {
					edge.setAttribute(attribute, value);
				} finally {
					reentrant = false;
				}

				sendEdgeAttributeAdded(sourceId, timeId, edgeId, attribute,
					value);
			}
		}
	}

	@Override
	public void edgeAttributeChanged(String sourceId, long timeId,
		String edgeId, String attribute, Object oldValue, Object newValue) {
		if (sinkTime.isNewEvent(sourceId, timeId)) {
			Edge edge = g.getEdge(edgeId);
			if (edge != null) {
				reentrant = true;

				if (oldValue == null) {
					oldValue = edge.getAttribute(attribute);
				}

				try {
					edge.setAttribute(attribute, newValue);
				} finally {
					reentrant = false;
				}

				sendEdgeAttributeChanged(sourceId, timeId, edgeId, attribute,
					oldValue, newValue);
			}
		}
	}

	@Override
	public void edgeAttributeRemoved(String sourceId, long timeId,
		String edgeId, String attribute) {
		if (sinkTime.isNewEvent(sourceId, timeId)) {
			Edge edge = g.getEdge(edgeId);
			if (edge != null) {
				sendEdgeAttributeRemoved(sourceId, timeId, edgeId, attribute);
				reentrant = true;

				try {
					edge.removeAttribute(attribute);
				} finally {
					reentrant = false;
				}

			}
		}
	}

	@Override
	public void graphAttributeAdded(String sourceId, long timeId,
		String attribute, Object value) {
		if (sinkTime.isNewEvent(sourceId, timeId)) {
			reentrant = true;

			try {
				g.setAttribute(attribute, value);
			} finally {
				reentrant = false;
			}

			sendGraphAttributeAdded(sourceId, timeId, attribute, value);
		}
	}

	@Override
	public void graphAttributeChanged(String sourceId, long timeId,
		String attribute, Object oldValue, Object newValue) {
		if (sinkTime.isNewEvent(sourceId, timeId)) {
			reentrant = true;

			if (oldValue == null) {
				oldValue = g.getAttribute(attribute);
			}

			try {
				g.setAttribute(attribute, newValue);
			} finally {
				reentrant = false;
			}

			sendGraphAttributeChanged(sourceId, timeId, attribute, oldValue,
				newValue);
		}
	}

	@Override
	public void graphAttributeRemoved(String sourceId, long timeId,
		String attribute) {
		if (sinkTime.isNewEvent(sourceId, timeId)) {
			sendGraphAttributeRemoved(sourceId, timeId, attribute);
			reentrant = true;

			try {
				g.removeAttribute(attribute);
			} finally {
				reentrant = false;
			}
		}
	}

	@Override
	public void nodeAttributeAdded(String sourceId, long timeId, String nodeId,
		String attribute, Object value) {
		if (sinkTime.isNewEvent(sourceId, timeId)) {
			Node node = g.getNode(nodeId);
			if (node != null) {
				reentrant = true;

				try {
					node.setAttribute(attribute, value);
				} finally {
					reentrant = false;
				}

				sendNodeAttributeAdded(sourceId, timeId, nodeId, attribute,
					value);
			}
		}
	}

	@Override
	public void nodeAttributeChanged(String sourceId, long timeId,
		String nodeId, String attribute, Object oldValue, Object newValue) {
		if (sinkTime.isNewEvent(sourceId, timeId)) {
			Node node = g.getNode(nodeId);
			if (node != null) {
				reentrant = true;

				if (oldValue == null) {
					oldValue = node.getAttribute(attribute);
				}

				try {
					node.setAttribute(attribute, newValue);
				} finally {
					reentrant = false;
				}

				sendNodeAttributeChanged(sourceId, timeId, nodeId, attribute,
					oldValue, newValue);
			}
		}
	}

	@Override
	public void nodeAttributeRemoved(String sourceId, long timeId,
		String nodeId, String attribute) {
		if (sinkTime.isNewEvent(sourceId, timeId)) {
			Node node = g.getNode(nodeId);
			if (node != null) {
				sendNodeAttributeRemoved(sourceId, timeId, nodeId, attribute);
				reentrant = true;

				try {
					node.removeAttribute(attribute);
				} finally {
					reentrant = false;
				}
			}
		}
	}

	@Override
	public void edgeAdded(String sourceId, long timeId, String edgeId,
		String fromNodeId, String toNodeId, boolean directed) {
		if (sinkTime.isNewEvent(sourceId, timeId)) {
			reentrant = true;

			try {
				g.addEdge(edgeId, fromNodeId, toNodeId, directed);
			} finally {
				reentrant = false;
			}

			sendEdgeAdded(sourceId, timeId, edgeId, fromNodeId, toNodeId,
				directed);
		}
	}

	@Override
	public void edgeRemoved(String sourceId, long timeId, String edgeId) {
		if (sinkTime.isNewEvent(sourceId, timeId)) {
			sendEdgeRemoved(sourceId, timeId, edgeId);
			reentrant = true;

			try {
				g.removeEdge(edgeId);
			} finally {
				reentrant = false;
			}
		}
	}

	@Override
	public void graphCleared(String sourceId, long timeId) {
		if (sinkTime.isNewEvent(sourceId, timeId)) {
			sendGraphCleared(sourceId, timeId);
			reentrant = true;

			try {
				g.clear();
			} finally {
				reentrant = false;
			}
		}
	}

	@Override
	public void nodeAdded(String sourceId, long timeId, String nodeId) {
		if (sinkTime.isNewEvent(sourceId, timeId)) {
			reentrant = true;

			try {
				g.addNode(nodeId);
			} finally {
				reentrant = false;
			}

			sendNodeAdded(sourceId, timeId, nodeId);
		}
	}

	@Override
	public void nodeRemoved(String sourceId, long timeId, String nodeId) {
		if (sinkTime.isNewEvent(sourceId, timeId)) {
			sendNodeRemoved(sourceId, timeId, nodeId);
			reentrant = true;

			try {
				g.removeNode(nodeId);
			} finally {
				reentrant = false;
			}
		}
	}

	@Override
	public void stepBegins(String sourceId, long timeId, double step) {
		if (sinkTime.isNewEvent(sourceId, timeId)) {
			reentrant = true;

			try {
				g.stepBegins(step);
			} finally {
				reentrant = false;
			}

			sendStepBegins(sourceId, timeId, step);
		}
	}

	@Override
	public String toString() {
		return String.format("GraphListeners of %s.%s", g.getClass()
			.getSimpleName(), g.getId());
	}
}
