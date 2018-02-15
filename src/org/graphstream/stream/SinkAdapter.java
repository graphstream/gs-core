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
 * @since 2009-04-22
 * 
 * @author Yoann Pigné <yoann.pigne@graphstream-project.org>
 * @author Antoine Dutot <antoine.dutot@graphstream-project.org>
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.stream;

/**
 * Adapter for the {@link Sink} interface.
 * 
 * <p>
 * All methods are empty.
 * </p>
 */
public class SinkAdapter implements Sink {
	public void edgeAttributeAdded(String sourceId, long timeId, String edgeId, String attribute, Object value) {
	}

	public void edgeAttributeChanged(String sourceId, long timeId, String edgeId, String attribute, Object oldValue,
			Object newValue) {
	}

	public void edgeAttributeRemoved(String sourceId, long timeId, String edgeId, String attribute) {
	}

	public void graphAttributeAdded(String sourceId, long timeId, String attribute, Object value) {
	}

	public void graphAttributeChanged(String sourceId, long timeId, String attribute, Object oldValue,
			Object newValue) {
	}

	public void graphAttributeRemoved(String sourceId, long timeId, String attribute) {
	}

	public void nodeAttributeAdded(String sourceId, long timeId, String nodeId, String attribute, Object value) {
	}

	public void nodeAttributeChanged(String sourceId, long timeId, String nodeId, String attribute, Object oldValue,
			Object newValue) {
	}

	public void nodeAttributeRemoved(String sourceId, long timeId, String nodeId, String attribute) {
	}

	public void edgeAdded(String sourceId, long timeId, String edgeId, String fromNodeId, String toNodeId,
			boolean directed) {
	}

	public void edgeRemoved(String sourceId, long timeId, String edgeId) {
	}

	public void graphCleared(String sourceId, long timeId) {
	}

	public void nodeAdded(String sourceId, long timeId, String nodeId) {
	}

	public void nodeRemoved(String sourceId, long timeId, String nodeId) {
	}

	public void stepBegins(String sourceId, long timeId, double step) {
	}
}