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
 * Source of graph events. All these methods are thread-safe.
 *
 * <p>
 * An source is something that produces graph events (attributes and elements),
 * but does not contain a graph instance.
 * </p>
 *
 * @see Sink
 * @see Pipe
 */
public interface Source {
	/**
	 * Add a sink for all graph events (attributes and graph elements)
	 * coming from this source. This is similar to registering a sink for
	 * attributes an another for elements.
	 *
	 * @param sink The sink to register.
	 */
	void addSink(Sink sink);

	/**
	 * Remove a sink.
	 *
	 * @param sink The sink to remove, if it does not exist, this is ignored
	 *             silently.
	 */
	void removeSink(Sink sink);

	/**
	 * Add a sink for attribute events only. Attribute events include
	 * attribute addition change and removal.
	 *
	 * @param sink The sink to register.
	 */
	void addAttributeSink(AttributeSink sink);

	/**
	 * Remove an attribute sink.
	 *
	 * @param sink The sink to remove, if it does not exist, this is ignored
	 *             silently.
	 */
	void removeAttributeSink(AttributeSink sink);

	/**
	 * Returns an "iterable" of {@link AttributeSink} objects registered to
	 * this graph.
	 *
	 * @return the set of {@link AttributeSink} under the form of an
	 *         iterable object.
	 */
	Iterable<AttributeSink> attributeSinks();

	/**
	 * Add a sink for elements events only. Elements events include,
	 * addition and removal of nodes and edges, as well as step events.
	 *
	 * @param sink The sink to register.
	 */
	void addElementSink(ElementSink sink);

	/**
	 * Remove an element sink.
	 *
	 * @param sink The sink to remove, if it does not exist, this is ignored
	 *             silently.
	 */
	void removeElementSink(ElementSink sink);

	/**
	 * Returns an "iterable" of {@link ElementSink} objects registered to
	 * this graph.
	 *
	 * @return the list of {@link ElementSink} under the form of an iterable
	 *         object.
	 */
	Iterable<ElementSink> elementSinks();

	/**
	 * Remove all listener element sinks.
	 */
	void clearElementSinks();

	/**
	 * Remove all listener attribute sinks.
	 */
	void clearAttributeSinks();

	/**
	 * Remove all listener sinks.
	 */
	void clearSinks();
}
