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
package org.graphstream.ui.swingViewer;

import java.util.HashSet;

import org.graphstream.stream.ProxyPipe;
import org.graphstream.stream.SourceBase;

/**
 * Shell around a proxy pipe coming from the viewer allowing to put viewer
 * listeners on a viewer that runs in a distinct thread.
 * 
 * <p>
 * This pipe is a probe that you can place in the event loop between the viewer
 * and the graph. It will transmit all events coming from the viewer to the
 * graph (or any sink you connect to it). But in addition it will monitor
 * standard attribute changes to redistribute them to specify
 * "viewer listeners".
 * </p>
 * 
 * <p>
 * As any proxy pipe, a viewer pipe must be "pumped" to receive events coming
 * from other threads.
 * </p>
 */
public class ViewerPipe extends SourceBase implements ProxyPipe {
	// Attribute

	private String id;

	/**
	 * The incoming event stream.
	 */
	protected ProxyPipe pipeIn;

	/**
	 * Listeners on the viewer specific events.
	 */
	protected HashSet<ViewerListener> viewerListeners = new HashSet<ViewerListener>();

	// Construction

	/**
	 * A shell around a pipe coming from a viewer in another thread.
	 */
	protected ViewerPipe(String id, ProxyPipe pipeIn) {
		this.id = id;
		this.pipeIn = pipeIn;
		pipeIn.addSink(this);
	}

	// Access

	public String getId() {
		return id;
	}

	// Commands

	/**
	 * Pump events from the pipe.
	 */
	public void pump() {
		pipeIn.pump();
	}

	public void addViewerListener(ViewerListener listener) {
		viewerListeners.add(listener);
	}

	public void removeViewerListener(ViewerListener listener) {
		viewerListeners.remove(listener);
	}

	// Sink interface

	public void edgeAttributeAdded(String sourceId, long timeId, String edgeId,
			String attribute, Object value) {
		sendEdgeAttributeAdded(sourceId, timeId, edgeId, attribute, value);
	}

	public void edgeAttributeChanged(String sourceId, long timeId,
			String edgeId, String attribute, Object oldValue, Object newValue) {
		sendEdgeAttributeChanged(sourceId, timeId, edgeId, attribute, oldValue,
				newValue);
	}

	public void edgeAttributeRemoved(String sourceId, long timeId,
			String edgeId, String attribute) {
		sendEdgeAttributeRemoved(sourceId, timeId, edgeId, attribute);
	}

	public void graphAttributeAdded(String sourceId, long timeId,
			String attribute, Object value) {
		sendGraphAttributeAdded(sourceId, timeId, attribute, value);

		if (attribute.equals("ui.viewClosed") && value instanceof String) {
			for (ViewerListener listener : viewerListeners)
				listener.viewClosed((String) value);

			sendGraphAttributeRemoved(id, attribute);
		} else if (attribute.equals("ui.clicked") && value instanceof String) {
			for (ViewerListener listener : viewerListeners)
				listener.buttonPushed((String) value);

			sendGraphAttributeRemoved(id, attribute);
		}
	}

	public void graphAttributeChanged(String sourceId, long timeId,
			String attribute, Object oldValue, Object newValue) {
		sendGraphAttributeChanged(sourceId, timeId, attribute, oldValue,
				newValue);
	}

	public void graphAttributeRemoved(String sourceId, long timeId,
			String attribute) {
		sendGraphAttributeRemoved(sourceId, timeId, attribute);
	}

	public void nodeAttributeAdded(String sourceId, long timeId, String nodeId,
			String attribute, Object value) {
		sendNodeAttributeAdded(sourceId, timeId, nodeId, attribute, value);

		if(attribute.equals("ui.clicked")) {
			for(ViewerListener listener: viewerListeners)
				listener.buttonPushed(nodeId);
		}
	}

	public void nodeAttributeChanged(String sourceId, long timeId,
			String nodeId, String attribute, Object oldValue, Object newValue) {
		sendNodeAttributeChanged(sourceId, timeId, nodeId, attribute, oldValue,
			newValue);
	}

	public void nodeAttributeRemoved(String sourceId, long timeId,
			String nodeId, String attribute) {
		sendNodeAttributeRemoved(sourceId, timeId, nodeId, attribute);

		if(attribute.equals("ui.clicked")) {
			for(ViewerListener listener: viewerListeners)
				listener.buttonReleased(nodeId);
		}
	}

	public void edgeAdded(String sourceId, long timeId, String edgeId,
			String fromNodeId, String toNodeId, boolean directed) {
		sendEdgeAdded(sourceId, timeId, edgeId, fromNodeId, toNodeId, directed);
	}

	public void edgeRemoved(String sourceId, long timeId, String edgeId) {
		sendEdgeRemoved(sourceId, timeId, edgeId);
	}

	public void graphCleared(String sourceId, long timeId) {
		sendGraphCleared(sourceId, timeId);
	}

	public void nodeAdded(String sourceId, long timeId, String nodeId) {
		sendNodeAdded(sourceId, timeId, nodeId);
	}

	public void nodeRemoved(String sourceId, long timeId, String nodeId) {
		sendNodeRemoved(sourceId, timeId, nodeId);
	}

	public void stepBegins(String sourceId, long timeId, double step) {
		sendStepBegins(sourceId, timeId, step);
	}
}