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
package org.graphstream.stream;

/**
 * A base pipe that merely let all events pass.
 *
 * <p>
 * This pipe does nothing and let all events pass. It can be used as a base to
 * implement more specific filters by refining some of its methods.
 * </p>
 *
 * <p>
 * Another use of this pipe is to duplicate a stream of events from one input
 * toward several outputs.
 * </p>
 */
public class PipeBase extends SourceBase implements Pipe {
	@Override
	public void edgeAttributeAdded(String graphId, long timeId, String edgeId,
		String attribute, Object value) {
		sendEdgeAttributeAdded(graphId, timeId, edgeId, attribute, value);
	}

	@Override
	public void edgeAttributeChanged(String graphId, long timeId,
		String edgeId, String attribute, Object oldValue, Object newValue) {
		sendEdgeAttributeChanged(graphId, timeId, edgeId, attribute, oldValue,
			newValue);
	}

	@Override
	public void edgeAttributeRemoved(String graphId, long timeId,
		String edgeId, String attribute) {
		sendEdgeAttributeRemoved(graphId, timeId, edgeId, attribute);
	}

	@Override
	public void graphAttributeAdded(String graphId, long timeId,
		String attribute, Object value) {
		sendGraphAttributeAdded(graphId, timeId, attribute, value);
	}

	@Override
	public void graphAttributeChanged(String graphId, long timeId,
		String attribute, Object oldValue, Object newValue) {
		sendGraphAttributeChanged(graphId, timeId, attribute, oldValue,
			newValue);
	}

	@Override
	public void graphAttributeRemoved(String graphId, long timeId,
		String attribute) {
		sendGraphAttributeRemoved(graphId, timeId, attribute);
	}

	@Override
	public void nodeAttributeAdded(String graphId, long timeId, String nodeId,
		String attribute, Object value) {
		sendNodeAttributeAdded(graphId, timeId, nodeId, attribute, value);
	}

	@Override
	public void nodeAttributeChanged(String graphId, long timeId,
		String nodeId, String attribute, Object oldValue, Object newValue) {
		sendNodeAttributeChanged(graphId, timeId, nodeId, attribute, oldValue,
			newValue);
	}

	@Override
	public void nodeAttributeRemoved(String graphId, long timeId,
		String nodeId, String attribute) {
		sendNodeAttributeRemoved(graphId, timeId, nodeId, attribute);
	}

	@Override
	public void edgeAdded(String graphId, long timeId, String edgeId,
		String fromNodeId, String toNodeId, boolean directed) {
		sendEdgeAdded(graphId, timeId, edgeId, fromNodeId, toNodeId, directed);
	}

	@Override
	public void edgeRemoved(String graphId, long timeId, String edgeId) {
		sendEdgeRemoved(graphId, timeId, edgeId);
	}

	@Override
	public void graphCleared(String graphId, long timeId) {
		sendGraphCleared(graphId, timeId);
	}

	@Override
	public void nodeAdded(String graphId, long timeId, String nodeId) {
		sendNodeAdded(graphId, timeId, nodeId);
	}

	@Override
	public void nodeRemoved(String graphId, long timeId, String nodeId) {
		sendNodeRemoved(graphId, timeId, nodeId);
	}

	@Override
	public void stepBegins(String graphId, long timeId, double step) {
		sendStepBegins(graphId, timeId, step);
	}
}
