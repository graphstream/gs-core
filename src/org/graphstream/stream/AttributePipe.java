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
 * @since 2009-04-17
 * 
 * @author Yoann Pigné <yoann.pigne@graphstream-project.org>
 * @author Antoine Dutot <antoine.dutot@graphstream-project.org>
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.stream;

/**
 * Allows to filter the attribute event stream.
 * 
 * <p>
 * The filtering is based on attribute predicates. An attribute predicate is an
 * object that you provide and that only defines one method
 * {@link AttributePredicate#matches(String, Object)}. If the "matches()" method
 * return false, the attribute is discarded from the event stream, else it is
 * passed to the listeners of this filter.
 * </p>
 * 
 * <p>
 * You can setup a predicate from all attributes (graph, node and edge
 * attributes) and specific predicates for graph, node and edge attributes.
 * </p>
 */
public class AttributePipe extends PipeBase {
	protected AttributePredicate globalPredicate = new FalsePredicate();

	protected AttributePredicate graphPredicate = new FalsePredicate();

	protected AttributePredicate nodePredicate = new FalsePredicate();

	protected AttributePredicate edgePredicate = new FalsePredicate();

	/**
	 * Set an attribute filter for graph, node and edge attributes. If the filter is
	 * null, attributes will not be filtered globally.
	 * 
	 * @param filter
	 *            The filter to use, it can be null to disable global attribute
	 *            filtering.
	 */
	public void setGlobalAttributeFilter(AttributePredicate filter) {
		if (filter == null)
			globalPredicate = new FalsePredicate();
		else
			globalPredicate = filter;
	}

	/**
	 * Set an attribute filter for graph attributes only (node an edge attributes
	 * are not filtered by this filter). If the filter is null, graph attributes
	 * will not be filtered.
	 * 
	 * @param filter
	 *            The filter to use, it can be null to disable graph attribute
	 *            filtering.
	 */
	public void setGraphAttributeFilter(AttributePredicate filter) {
		if (filter == null)
			graphPredicate = new FalsePredicate();
		else
			graphPredicate = filter;
	}

	/**
	 * Set an attribute filter for node attributes only (graph an edge attributes
	 * are not filtered by this filter). If the filter is null, node attributes will
	 * not be filtered.
	 * 
	 * @param filter
	 *            The filter to use, it can be null to disable node attribute
	 *            filtering.
	 */
	public void setNodeAttributeFilter(AttributePredicate filter) {
		if (filter == null)
			nodePredicate = new FalsePredicate();
		else
			nodePredicate = filter;
	}

	/**
	 * Set an attribute filter for edge attributes only (graph an node attributes
	 * are not filtered by this filter). If the filter is null, edge attributes will
	 * not be filtered.
	 * 
	 * @param filter
	 *            The filter to use, it can be null to disable edge attribute
	 *            filtering.
	 */
	public void setEdgeAttributeFilter(AttributePredicate filter) {
		if (filter == null)
			edgePredicate = new FalsePredicate();
		else
			edgePredicate = filter;
	}

	/**
	 * The filter for all graph, node and edge attributes. This filter can be null.
	 * 
	 * @return The global attribute filter or null if there is no global filter.
	 */
	public AttributePredicate getGlobalAttributeFilter() {
		return globalPredicate;
	}

	/**
	 * The filter for all graph attributes. This filter can be null.
	 * 
	 * @return The graph attribute filter or null if there is no graph filter.
	 */
	public AttributePredicate getGraphAttributeFilter() {
		return graphPredicate;
	}

	/**
	 * The filter for all node attributes. This filter can be null.
	 * 
	 * @return The node global attribute filter or null if there is no node filter.
	 */
	public AttributePredicate getNodeAttributeFilter() {
		return nodePredicate;
	}

	/**
	 * The filter for all edge attributes. This filter can be null.
	 * 
	 * @return The edge attribute filter or null of there is no edge filter.
	 */
	public AttributePredicate getEdgeAttributeFilter() {
		return edgePredicate;
	}

	// GraphListener

	@Override
	public void edgeAttributeAdded(String graphId, long timeId, String edgeId, String attribute, Object value) {
		if (!edgePredicate.matches(attribute, value)) {
			if (!globalPredicate.matches(attribute, value)) {
				sendEdgeAttributeAdded(graphId, timeId, edgeId, attribute, value);
			}
		}
	}

	@Override
	public void edgeAttributeChanged(String graphId, long timeId, String edgeId, String attribute, Object oldValue,
			Object newValue) {
		if (!edgePredicate.matches(attribute, newValue)) {
			if (!globalPredicate.matches(attribute, newValue)) {
				sendEdgeAttributeChanged(graphId, timeId, edgeId, attribute, oldValue, newValue);
			}
		}
	}

	@Override
	public void edgeAttributeRemoved(String graphId, long timeId, String edgeId, String attribute) {
		if (!edgePredicate.matches(attribute, null)) {
			if (!globalPredicate.matches(attribute, null)) {
				sendEdgeAttributeRemoved(graphId, timeId, edgeId, attribute);
			}
		}
	}

	@Override
	public void graphAttributeAdded(String graphId, long timeId, String attribute, Object value) {
		if (!graphPredicate.matches(attribute, value)) {
			if (!globalPredicate.matches(attribute, value)) {
				sendGraphAttributeAdded(graphId, timeId, attribute, value);
			}
		}
	}

	@Override
	public void graphAttributeChanged(String graphId, long timeId, String attribute, Object oldValue, Object newValue) {
		if (!graphPredicate.matches(attribute, newValue)) {
			if (!globalPredicate.matches(attribute, newValue)) {
				sendGraphAttributeChanged(graphId, timeId, attribute, oldValue, newValue);
			}
		}
	}

	@Override
	public void graphAttributeRemoved(String graphId, long timeId, String attribute) {
		if (!graphPredicate.matches(attribute, null)) {
			if (!globalPredicate.matches(attribute, null)) {
				sendGraphAttributeRemoved(graphId, timeId, attribute);
			}
		}
	}

	@Override
	public void nodeAttributeAdded(String graphId, long timeId, String nodeId, String attribute, Object value) {
		if (!nodePredicate.matches(attribute, value)) {
			if (!globalPredicate.matches(attribute, value)) {
				sendNodeAttributeAdded(graphId, timeId, nodeId, attribute, value);
			}
		}
	}

	@Override
	public void nodeAttributeChanged(String graphId, long timeId, String nodeId, String attribute, Object oldValue,
			Object newValue) {
		if (!nodePredicate.matches(attribute, newValue)) {
			if (!globalPredicate.matches(attribute, newValue)) {
				sendNodeAttributeChanged(graphId, timeId, nodeId, attribute, oldValue, newValue);
			}
		}
	}

	@Override
	public void nodeAttributeRemoved(String graphId, long timeId, String nodeId, String attribute) {
		if (!nodePredicate.matches(attribute, null)) {
			if (!globalPredicate.matches(attribute, null)) {
				sendNodeAttributeRemoved(graphId, timeId, nodeId, attribute);
			}
		}
	}

	protected class FalsePredicate implements AttributePredicate {
		public boolean matches(String attributeName, Object attributeValue) {
			return false;
		}

	}
}