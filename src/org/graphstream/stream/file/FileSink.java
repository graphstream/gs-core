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
 * @author Richard O. Legendi <richard.legendi@gmail.com>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.stream.file;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import org.graphstream.graph.Graph;
import org.graphstream.stream.Sink;

/**
 * Output a graph or graph events to a file.
 * 
 * <p>
 * File outputs can work in two modes:
 * <ul>
 * <li>In the "writeAll()" mode, the file output is done "all at once" writing a
 * "snapshot" of the graph at this particular instant in time. This mode cannot
 * convey the dynamics of the graph.</li>
 * <li>In "begin()/end()" mode, the output is listener of an input (a graph or
 * any other sort of graph events producer) and it write events as they come,
 * conveying the dynamics of the graph correctly.</li>
 * </ul>
 * </p>
 */
public interface FileSink extends Sink {
	/**
	 * Write the current graph state in one big non-interruptible operation. This
	 * operation is a "snapshot" of the graph, it will never convey the dynamics of
	 * the graph. To ensure you store the graph "as it evolves in time" you must use
	 * the {@link #begin(OutputStream)} or {@link #begin(String)} as soon as the
	 * graph appears (or any source of graph event, any descendant of {@link Source}
	 * will do).
	 * 
	 * @param graph
	 *            The graph to send as events to the file.
	 * @param fileName
	 *            Name of the file to write.
	 * @throws IOException
	 *             if an I/O error occurs while writing.
	 */
	void writeAll(Graph graph, String fileName) throws IOException;

	/**
	 * Write the current graph state in one big non-interruptible operation. This
	 * operation is a "snapshot" of the graph, it will never convey the dynamics of
	 * the graph. To ensure you store the graph "as it evolves in time" you must use
	 * the {@link #begin(Writer)} or {@link #begin(OutputStream)} or
	 * {@link #begin(String)} as soon as the graph appears (or any source of graph
	 * event, any descendant of {@link Source} will do).
	 * 
	 * @param graph
	 *            The graph to send as events to the file.
	 * @param stream
	 *            The stream where the graph is sent.
	 * @throws IOException
	 *             if an I/O error occurs while writing.
	 */
	void writeAll(Graph graph, OutputStream stream) throws IOException;

	/**
	 * Write the current graph state in one big non-interruptible operation. This
	 * operation is a "snapshot" of the graph, it will never convey the dynamics of
	 * the graph. To ensure you store the graph "as it evolves in time" you must use
	 * the {@link #begin(Writer)} or {@link #begin(OutputStream)} or
	 * {@link #begin(String)} as soon as the graph appears (or any source of graph
	 * event, any descendant of {@link Source} will do).
	 * 
	 * @param graph
	 *            The graph to send as events to the file.
	 * @param writer
	 *            The writer where the graph is sent.
	 * @throws IOException
	 *             if an I/O error occurs while writing.
	 */
	void writeAll(Graph graph, Writer writer) throws IOException;

	/**
	 * Begin the output of the given stream of graph events. The graph events can
	 * come from any input (implementation of {@link Source} or you can directly use
	 * the methods inherited from {@link Sink}. Once the writing is started using
	 * begin(), you must close it using {@link #end()} when done to ensure data is
	 * correctly stored in the file.
	 * 
	 * @param fileName
	 *            The name of the file where to output the graph events.
	 * @throws IOException
	 *             If an I/O error occurs while writing.
	 */
	void begin(String fileName) throws IOException;

	/**
	 * Begin the output of the given stream of graph events. The graph events can
	 * come from any input (implementation of {@link Source} or you can directly use
	 * the methods inherited from {@link Sink}. Once the writing is started using
	 * begin(), you must close it using {@link #end()} when done to ensure data is
	 * correctly stored in the file.
	 * 
	 * @param stream
	 *            The file stream where to output the graph events.
	 * @throws IOException
	 *             If an I/O error occurs while writing.
	 */
	void begin(OutputStream stream) throws IOException;

	/**
	 * Begin the output of the given stream of graph events. The graph events can
	 * come from any input (implementation of {@link Source} or you can directly use
	 * the methods inherited from {@link Sink}. Once the writing is started using
	 * begin(), you must close it using {@link #end()} when done to ensure data is
	 * correctly stored in the file.
	 * 
	 * @param writer
	 *            The writer where to output the graph events.
	 * @throws IOException
	 *             If an I/O error occurs while writing.
	 */
	void begin(Writer writer) throws IOException;

	/**
	 * Ensure all data sent to the output are correctly written.
	 * 
	 * @throws IOException
	 *             If an I/O error occurs during write.
	 */
	void flush() throws IOException;

	/**
	 * End the writing process started with {@link #begin(OutputStream)} or
	 * {@link #begin(String)}.
	 * 
	 * @throws IOException
	 */
	void end() throws IOException;
}