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
 * @since 2009-02-19
 * 
 * @author Yoann Pigné <yoann.pigne@graphstream-project.org>
 * @author Antoine Dutot <antoine.dutot@graphstream-project.org>
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 */
package org.graphstream.stream;

/**
 * Sink of graph events.
 * 
 * <p>
 * An output is something that can receive graph events. The output will send or
 * transform the graph events in another form: a file, a network stream, a
 * visualization, an algorithm, a metric, etc.
 * </p>
 * 
 * <p>
 * The output can filter the stream of attribute events using
 * {@link AttributePredicate}s.
 * </p>
 * 
 * 
 * <p>
 * This listener is in fact the grouping of two specialized listeners. The first
 * one listens only at structural changes in the graph (node and edge addition
 * and removal). It is the {@link org.graphstream.stream.ElementSink}. The
 * second one listens only at attributes values changes on elements of the graph
 * (attribute addition, removal and change of values). It is the
 * {@link org.graphstream.stream.AttributeSink}.
 * </p>
 * 
 * <p>
 * It is possible to listen only at elements or attributes changes with these
 * two interfaces. Registering a graph listener will allow to listen at the both
 * elements and attributes at the same time.
 * </p>
 * 
 * @see Source
 * @see Pipe
 * @see AttributeSink
 * @see ElementSink
 */
public interface Sink extends AttributeSink, ElementSink {
}