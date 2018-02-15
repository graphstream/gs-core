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
 * @since 2013-09-20
 * 
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.util;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.AbstractElement.AttributeChangeEvent;
import org.graphstream.stream.Pipe;
import org.graphstream.stream.SourceBase;
import org.graphstream.stream.sync.SinkTime;

/**
 * Helper object to handle events producted by a graph.
 * 
 */
public class GraphListeners extends SourceBase implements Pipe {

	SinkTime sinkTime;
	boolean passYourWay, passYourWayAE;
	String dnSourceId;
	long dnTimeId;

	Graph g;

	public GraphListeners(Graph g) {
		super(g.getId());

		this.sinkTime = new SinkTime();
		this.sourceTime.setSinkTime(sinkTime);
		this.passYourWay = false;
		this.passYourWayAE = false;
		this.dnSourceId = null;
		this.dnTimeId = Long.MIN_VALUE;
		this.g = g;
	}

	public long newEvent() {
		return sourceTime.newEvent();
	}

	public void sendAttributeChangedEvent(String eltId, ElementType eltType, String attribute,
			AttributeChangeEvent event, Object oldValue, Object newValue) {
		//
		// Attributes with name beginnig with a dot are hidden.
		//
		if (passYourWay || attribute.charAt(0) == '.')
			return;

		sendAttributeChangedEvent(sourceId, newEvent(), eltId, eltType, attribute, event, oldValue, newValue);
	}

	public void sendNodeAdded(String nodeId) {
		if (passYourWay)
			return;

		sendNodeAdded(sourceId, newEvent(), nodeId);
	}

	public void sendNodeRemoved(String nodeId) {
		if (dnSourceId != null) {
			sendNodeRemoved(dnSourceId, dnTimeId, nodeId);
		} else {
			sendNodeRemoved(sourceId, newEvent(), nodeId);
		}
	}

	public void sendEdgeAdded(String edgeId, String source, String target, boolean directed) {
		if (passYourWayAE)
			return;

		sendEdgeAdded(sourceId, newEvent(), edgeId, source, target, directed);
	}

	public void sendEdgeRemoved(String edgeId) {
		if (passYourWay)
			return;

		sendEdgeRemoved(sourceId, newEvent(), edgeId);
	}

	public void sendGraphCleared() {
		if (passYourWay)
			return;

		sendGraphCleared(sourceId, newEvent());
	}

	public void sendStepBegins(double step) {
		if (passYourWay)
			return;

		sendStepBegins(sourceId, newEvent(), step);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.AttributeSink#edgeAttributeAdded(java.lang
	 * .String, long, java.lang.String, java.lang.String, java.lang.Object)
	 */
	public void edgeAttributeAdded(String sourceId, long timeId, String edgeId, String attribute, Object value) {
		if (sinkTime.isNewEvent(sourceId, timeId)) {
			Edge edge = g.getEdge(edgeId);
			if (edge != null) {
				passYourWay = true;

				try {
					edge.setAttribute(attribute, value);
				} finally {
					passYourWay = false;
				}

				sendEdgeAttributeAdded(sourceId, timeId, edgeId, attribute, value);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.AttributeSink#edgeAttributeChanged(java.lang
	 * .String, long, java.lang.String, java.lang.String, java.lang.Object,
	 * java.lang.Object)
	 */
	public void edgeAttributeChanged(String sourceId, long timeId, String edgeId, String attribute, Object oldValue,
			Object newValue) {
		if (sinkTime.isNewEvent(sourceId, timeId)) {
			Edge edge = g.getEdge(edgeId);
			if (edge != null) {
				passYourWay = true;

				if (oldValue == null)
					oldValue = edge.getAttribute(attribute);

				try {
					edge.setAttribute(attribute, newValue);
				} finally {
					passYourWay = false;
				}

				sendEdgeAttributeChanged(sourceId, timeId, edgeId, attribute, oldValue, newValue);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.AttributeSink#edgeAttributeRemoved(java.lang
	 * .String, long, java.lang.String, java.lang.String)
	 */
	public void edgeAttributeRemoved(String sourceId, long timeId, String edgeId, String attribute) {
		if (sinkTime.isNewEvent(sourceId, timeId)) {
			Edge edge = g.getEdge(edgeId);
			if (edge != null) {
				sendEdgeAttributeRemoved(sourceId, timeId, edgeId, attribute);
				passYourWay = true;

				try {
					edge.removeAttribute(attribute);
				} finally {
					passYourWay = false;
				}

			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.AttributeSink#graphAttributeAdded(java.lang
	 * .String, long, java.lang.String, java.lang.Object)
	 */
	public void graphAttributeAdded(String sourceId, long timeId, String attribute, Object value) {
		if (sinkTime.isNewEvent(sourceId, timeId)) {
			passYourWay = true;

			try {
				g.setAttribute(attribute, value);
			} finally {
				passYourWay = false;
			}

			sendGraphAttributeAdded(sourceId, timeId, attribute, value);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.AttributeSink#graphAttributeChanged(java.lang
	 * .String, long, java.lang.String, java.lang.Object, java.lang.Object)
	 */
	public void graphAttributeChanged(String sourceId, long timeId, String attribute, Object oldValue,
			Object newValue) {
		if (sinkTime.isNewEvent(sourceId, timeId)) {
			passYourWay = true;

			if (oldValue == null)
				oldValue = g.getAttribute(attribute);

			try {
				g.setAttribute(attribute, newValue);
			} finally {
				passYourWay = false;
			}

			sendGraphAttributeChanged(sourceId, timeId, attribute, oldValue, newValue);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.AttributeSink#graphAttributeRemoved(java.lang
	 * .String, long, java.lang.String)
	 */
	public void graphAttributeRemoved(String sourceId, long timeId, String attribute) {
		if (sinkTime.isNewEvent(sourceId, timeId)) {
			sendGraphAttributeRemoved(sourceId, timeId, attribute);
			passYourWay = true;

			try {
				g.removeAttribute(attribute);
			} finally {
				passYourWay = false;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.AttributeSink#nodeAttributeAdded(java.lang
	 * .String, long, java.lang.String, java.lang.String, java.lang.Object)
	 */
	public void nodeAttributeAdded(String sourceId, long timeId, String nodeId, String attribute, Object value) {
		if (sinkTime.isNewEvent(sourceId, timeId)) {
			Node node = g.getNode(nodeId);
			if (node != null) {
				passYourWay = true;

				try {
					node.setAttribute(attribute, value);
				} finally {
					passYourWay = false;
				}

				sendNodeAttributeAdded(sourceId, timeId, nodeId, attribute, value);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.AttributeSink#nodeAttributeChanged(java.lang
	 * .String, long, java.lang.String, java.lang.String, java.lang.Object,
	 * java.lang.Object)
	 */
	public void nodeAttributeChanged(String sourceId, long timeId, String nodeId, String attribute, Object oldValue,
			Object newValue) {
		if (sinkTime.isNewEvent(sourceId, timeId)) {
			Node node = g.getNode(nodeId);
			if (node != null) {
				passYourWay = true;

				if (oldValue == null)
					oldValue = node.getAttribute(attribute);

				try {
					node.setAttribute(attribute, newValue);
				} finally {
					passYourWay = false;
				}

				sendNodeAttributeChanged(sourceId, timeId, nodeId, attribute, oldValue, newValue);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.AttributeSink#nodeAttributeRemoved(java.lang
	 * .String, long, java.lang.String, java.lang.String)
	 */
	public void nodeAttributeRemoved(String sourceId, long timeId, String nodeId, String attribute) {
		if (sinkTime.isNewEvent(sourceId, timeId)) {
			Node node = g.getNode(nodeId);
			if (node != null) {
				sendNodeAttributeRemoved(sourceId, timeId, nodeId, attribute);
				passYourWay = true;

				try {
					node.removeAttribute(attribute);
				} finally {
					passYourWay = false;
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#edgeAdded(java.lang.String, long,
	 * java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
	public void edgeAdded(String sourceId, long timeId, String edgeId, String fromNodeId, String toNodeId,
			boolean directed) {
		if (sinkTime.isNewEvent(sourceId, timeId)) {
			passYourWayAE = true;

			try {
				g.addEdge(edgeId, fromNodeId, toNodeId, directed);
			} finally {
				passYourWayAE = false;
			}

			sendEdgeAdded(sourceId, timeId, edgeId, fromNodeId, toNodeId, directed);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#edgeRemoved(java.lang.String, long,
	 * java.lang.String)
	 */
	public void edgeRemoved(String sourceId, long timeId, String edgeId) {
		if (sinkTime.isNewEvent(sourceId, timeId)) {
			sendEdgeRemoved(sourceId, timeId, edgeId);
			passYourWay = true;

			try {
				g.removeEdge(edgeId);
			} finally {
				passYourWay = false;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#graphCleared(java.lang.String, long)
	 */
	public void graphCleared(String sourceId, long timeId) {
		if (sinkTime.isNewEvent(sourceId, timeId)) {
			sendGraphCleared(sourceId, timeId);
			passYourWay = true;

			try {
				g.clear();
			} finally {
				passYourWay = false;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#nodeAdded(java.lang.String, long,
	 * java.lang.String)
	 */
	public void nodeAdded(String sourceId, long timeId, String nodeId) {
		if (sinkTime.isNewEvent(sourceId, timeId)) {
			passYourWay = true;

			try {
				g.addNode(nodeId);
			} finally {
				passYourWay = false;
			}

			sendNodeAdded(sourceId, timeId, nodeId);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#nodeRemoved(java.lang.String, long,
	 * java.lang.String)
	 */
	public void nodeRemoved(String sourceId, long timeId, String nodeId) {
		if (sinkTime.isNewEvent(sourceId, timeId)) {
			// sendNodeRemoved(sourceId, timeId, nodeId);
			dnSourceId = sourceId;
			dnTimeId = timeId;

			try {
				g.removeNode(nodeId);
			} finally {
				dnSourceId = null;
				dnTimeId = Long.MIN_VALUE;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#stepBegins(java.lang.String, long,
	 * double)
	 */
	public void stepBegins(String sourceId, long timeId, double step) {
		if (sinkTime.isNewEvent(sourceId, timeId)) {
			passYourWay = true;

			try {
				g.stepBegins(step);
			} finally {
				passYourWay = false;
			}

			sendStepBegins(sourceId, timeId, step);
		}
	}

	@Override
	public String toString() {
		return String.format("GraphListeners of %s.%s", g.getClass().getSimpleName(), g.getId());
	}
}