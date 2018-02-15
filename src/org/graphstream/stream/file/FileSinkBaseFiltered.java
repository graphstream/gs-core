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
 * @since 2014-11-03
 * 
 * @author Thibaut Démare <fdhp_76@hotmail.com>
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.stream.file;

import java.util.ArrayList;

import org.graphstream.stream.file.FileSinkBase;

/**
 * Base implementation for filtered graph output to files.
 * 
 * <p>
 * This class provides the list of possible filters which could be used by the
 * final user to write graphs into files using a specific file format. Thus, it
 * allows to create an output stream where the dynamic events of
 * addition/deletion/modification can be filtered.
 * </p>
 * 
 * <p>
 * Since it extends FileSinkBase, you have to override the same methods in order
 * to implement an output.
 * </p>
 */
public abstract class FileSinkBaseFiltered extends FileSinkBase {
	/*
	 * List of possible filters
	 */
	protected boolean noFilterGraphAttributeAdded;
	protected boolean noFilterGraphAttributeChanged;
	protected boolean noFilterGraphAttributeRemoved;
	protected boolean noFilterNodeAttributeAdded;
	protected boolean noFilterNodeAttributeChanged;
	protected boolean noFilterNodeAttributeRemoved;
	protected boolean noFilterNodeAdded;
	protected boolean noFilterNodeRemoved;
	protected boolean noFilterEdgeAttributeAdded;
	protected boolean noFilterEdgeAttributeChanged;
	protected boolean noFilterEdgeAttributeRemoved;
	protected boolean noFilterEdgeAdded;
	protected boolean noFilterEdgeRemoved;
	protected boolean noFilterGraphCleared;
	protected boolean noFilterStepBegins;
	protected ArrayList<String> graphAttributesFiltered;
	protected ArrayList<String> nodeAttributesFiltered;
	protected ArrayList<String> edgeAttributesFiltered;

	/**
	 * Initialize with no filter
	 */
	public FileSinkBaseFiltered() {
		noFilterGraphAttributeAdded = true;
		noFilterGraphAttributeChanged = true;
		noFilterGraphAttributeRemoved = true;
		noFilterNodeAttributeAdded = true;
		noFilterNodeAttributeChanged = true;
		noFilterNodeAttributeRemoved = true;
		noFilterNodeAdded = true;
		noFilterNodeRemoved = true;
		noFilterEdgeAttributeAdded = true;
		noFilterEdgeAttributeChanged = true;
		noFilterEdgeAttributeRemoved = true;
		noFilterEdgeAdded = true;
		noFilterEdgeRemoved = true;
		noFilterGraphCleared = true;
		noFilterStepBegins = true;
		graphAttributesFiltered = new ArrayList<String>();
		nodeAttributesFiltered = new ArrayList<String>();
		edgeAttributesFiltered = new ArrayList<String>();
	}

	/**
	 * @return the list of every node attributes filtered
	 */
	public ArrayList<String> getGraphAttributesFiltered() {
		return graphAttributesFiltered;
	}

	/**
	 * Set the whole list of graph attributes filtered
	 * 
	 * @param graphAttributesFiltered
	 *            the new list
	 */
	public void setGraphAttributesFiltered(ArrayList<String> graphAttributesFiltered) {
		this.graphAttributesFiltered = graphAttributesFiltered;
	}

	/**
	 * Add a new attribute to filter
	 * 
	 * @param attr
	 *            the filtered attribute
	 * @return true if the attribute has been added, false otherwise
	 */
	public boolean addGraphAttributeFiltered(String attr) {
		return graphAttributesFiltered.add(attr);
	}

	/**
	 * Remove an attribute to filter
	 * 
	 * @param attr
	 *            the no more filtered attribute
	 * @return true if the attribute has been removed, false otherwise
	 */
	public boolean removeGraphAttributeFilter(String attr) {
		return graphAttributesFiltered.remove(attr);
	}

	/**
	 * @return the list of every node attributes filtered
	 */
	public ArrayList<String> getNodeAttributesFiltered() {
		return graphAttributesFiltered;
	}

	/**
	 * Set the whole list of node attributes filtered
	 * 
	 * @param nodeAttributesFiltered
	 *            the new list
	 */
	public void setNodeAttributesFiltered(ArrayList<String> nodeAttributesFiltered) {
		this.nodeAttributesFiltered = nodeAttributesFiltered;
	}

	/**
	 * Add a new attribute to filter
	 * 
	 * @param attr
	 *            the filtered attribute
	 * @return true if the attribute has been added, false otherwise
	 */
	public boolean addNodeAttributeFiltered(String attr) {
		return nodeAttributesFiltered.add(attr);
	}

	/**
	 * Remove an attribute to filter
	 * 
	 * @param attr
	 *            the no more filtered attribute
	 * @return true if the attribute has been removed, false otherwise
	 */
	public boolean removeNodeAttributeFilter(String attr) {
		return nodeAttributesFiltered.remove(attr);
	}

	/**
	 * @return the list of every edge attributes filtered
	 */
	public ArrayList<String> getEdgeAttributesFiltered() {
		return edgeAttributesFiltered;
	}

	/**
	 * Set the whole list of edge attributes filtered
	 * 
	 * @param edgeAttributesFiltered
	 *            the new list
	 */
	public void setEdgeAttributesFiltered(ArrayList<String> edgeAttributesFiltered) {
		this.edgeAttributesFiltered = edgeAttributesFiltered;
	}

	/**
	 * Add a new attribute to filter
	 * 
	 * @param attr
	 *            the filtered attribute
	 * @return true if the attribute has been added, false otherwise
	 */
	public boolean addEdgeAttributeFiltered(String attr) {
		return edgeAttributesFiltered.add(attr);
	}

	/**
	 * Remove an attribute to filter
	 * 
	 * @param attr
	 *            the filtered attribute
	 * @return true if the attribute has been removed, false otherwise
	 */
	public boolean removeEdgeAttributeFilter(String attr) {
		return edgeAttributesFiltered.remove(attr);
	}

	/**
	 * 
	 * @return true if this filter is disable, false otherwise
	 */
	public boolean isNoFilterGraphAttributeAdded() {
		return noFilterGraphAttributeAdded;
	}

	/**
	 * Disable or enable this filter
	 * 
	 * @param noFilterGraphAttributeAdded
	 */
	public void setNoFilterGraphAttributeAdded(boolean noFilterGraphAttributeAdded) {
		this.noFilterGraphAttributeAdded = noFilterGraphAttributeAdded;
	}

	/**
	 * 
	 * @return true if this filter is disable, false otherwise
	 */
	public boolean isNoFilterGraphAttributeChanged() {
		return noFilterGraphAttributeChanged;
	}

	/**
	 * Disable or enable this filter
	 * 
	 * @param noFilterGraphAttributeChanged
	 */
	public void setNoFilterGraphAttributeChanged(boolean noFilterGraphAttributeChanged) {
		this.noFilterGraphAttributeChanged = noFilterGraphAttributeChanged;
	}

	/**
	 * 
	 * @return true if this filter is disable, false otherwise
	 */
	public boolean isNoFilterGraphAttributeRemoved() {
		return noFilterGraphAttributeRemoved;
	}

	/**
	 * Disable or enable this filter
	 * 
	 * @param noFilterGraphAttributeRemoved
	 */
	public void setNoFilterGraphAttributeRemoved(boolean noFilterGraphAttributeRemoved) {
		this.noFilterGraphAttributeRemoved = noFilterGraphAttributeRemoved;
	}

	/**
	 * 
	 * @return true if this filter is disable, false otherwise
	 */
	public boolean isNoFilterNodeAttributeAdded() {
		return noFilterNodeAttributeAdded;
	}

	/**
	 * Disable or enable this filter
	 * 
	 * @param noFilterNodeAttributeAdded
	 */
	public void setNoFilterNodeAttributeAdded(boolean noFilterNodeAttributeAdded) {
		this.noFilterNodeAttributeAdded = noFilterNodeAttributeAdded;
	}

	/**
	 * 
	 * @return true if this filter is disable, false otherwise
	 */
	public boolean isNoFilterNodeAttributeChanged() {
		return noFilterNodeAttributeChanged;
	}

	/**
	 * Disable or enable this filter
	 * 
	 * @param noFilterNodeAttributeChanged
	 */
	public void setNoFilterNodeAttributeChanged(boolean noFilterNodeAttributeChanged) {
		this.noFilterNodeAttributeChanged = noFilterNodeAttributeChanged;
	}

	/**
	 * 
	 * @return true if this filter is disable, false otherwise
	 */
	public boolean isNoFilterNodeAttributeRemoved() {
		return noFilterNodeAttributeRemoved;
	}

	/**
	 * Disable or enable this filter
	 * 
	 * @param noFilterNodeAttributeRemoved
	 */
	public void setNoFilterNodeAttributeRemoved(boolean noFilterNodeAttributeRemoved) {
		this.noFilterNodeAttributeRemoved = noFilterNodeAttributeRemoved;
	}

	/**
	 * 
	 * @return true if this filter is disable, false otherwise
	 */
	public boolean isNoFilterNodeAdded() {
		return noFilterNodeAdded;
	}

	/**
	 * Disable or enable this filter
	 * 
	 * @param noFilterNodeAdded
	 */
	public void setNoFilterNodeAdded(boolean noFilterNodeAdded) {
		this.noFilterNodeAdded = noFilterNodeAdded;
	}

	/**
	 * 
	 * @return true if this filter is disable, false otherwise
	 */
	public boolean isNoFilterNodeRemoved() {
		return noFilterNodeRemoved;
	}

	/**
	 * Disable or enable this filter
	 * 
	 * @param noFilterNodeRemoved
	 */
	public void setNoFilterNodeRemoved(boolean noFilterNodeRemoved) {
		this.noFilterNodeRemoved = noFilterNodeRemoved;
	}

	/**
	 * 
	 * @return true if this filter is disable, false otherwise
	 */
	public boolean isNoFilterEdgeAttributeAdded() {
		return noFilterEdgeAttributeAdded;
	}

	/**
	 * Disable or enable this filter
	 * 
	 * @param noFilterEdgeAttributeAdded
	 */
	public void setNoFilterEdgeAttributeAdded(boolean noFilterEdgeAttributeAdded) {
		this.noFilterEdgeAttributeAdded = noFilterEdgeAttributeAdded;
	}

	/**
	 * 
	 * @return true if this filter is disable, false otherwise
	 */
	public boolean isNoFilterEdgeAttributeChanged() {
		return noFilterEdgeAttributeChanged;
	}

	/**
	 * 
	 * @param noFilterEdgeAttributeChanged
	 */
	public void setNoFilterEdgeAttributeChanged(boolean noFilterEdgeAttributeChanged) {
		this.noFilterEdgeAttributeChanged = noFilterEdgeAttributeChanged;
	}

	/**
	 * 
	 * @return true if this filter is disable, false otherwise
	 */
	public boolean isNoFilterEdgeAttributeRemoved() {
		return noFilterEdgeAttributeRemoved;
	}

	/**
	 * 
	 * @param noFilterEdgeAttributeRemoved
	 */
	public void setNoFilterEdgeAttributeRemoved(boolean noFilterEdgeAttributeRemoved) {
		this.noFilterEdgeAttributeRemoved = noFilterEdgeAttributeRemoved;
	}

	/**
	 * 
	 * @return true if this filter is disable, false otherwise
	 */
	public boolean isNoFilterEdgeAdded() {
		return noFilterEdgeAdded;
	}

	/**
	 * 
	 * @param noFilterEdgeAdded
	 */
	public void setNoFilterEdgeAdded(boolean noFilterEdgeAdded) {
		this.noFilterEdgeAdded = noFilterEdgeAdded;
	}

	/**
	 * 
	 * @return true if this filter is disable, false otherwise
	 */
	public boolean isNoFilterEdgeRemoved() {
		return noFilterEdgeRemoved;
	}

	/**
	 * Disable or enable this filter
	 * 
	 * @param noFilterEdgeRemoved
	 */
	public void setNoFilterEdgeRemoved(boolean noFilterEdgeRemoved) {
		this.noFilterEdgeRemoved = noFilterEdgeRemoved;
	}

	/**
	 * 
	 * @return true if this filter is disable, false otherwise
	 */
	public boolean isNoFilterGraphCleared() {
		return noFilterGraphCleared;
	}

	/**
	 * Disable or enable this filter
	 * 
	 * @param noFilterGraphCleared
	 */
	public void setNoFilterGraphCleared(boolean noFilterGraphCleared) {
		this.noFilterGraphCleared = noFilterGraphCleared;
	}

	/**
	 * 
	 * @return true if this filter is disable, false otherwise
	 */
	public boolean isNoFilterStepBegins() {
		return noFilterStepBegins;
	}

	/**
	 * Disable or enable this filter
	 * 
	 * @param noFilterStepBegins
	 */
	public void setNoFilterStepBegins(boolean noFilterStepBegins) {
		this.noFilterStepBegins = noFilterStepBegins;
	}

}
