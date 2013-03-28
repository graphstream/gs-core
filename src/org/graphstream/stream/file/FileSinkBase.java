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
package org.graphstream.stream.file;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

/**
 * Base implementation for graph output to files.
 * 
 * <p>
 * This class provides base services to write graphs into files using a specific
 * file format. It allows to create an output stream. By default a print stream
 * for easy text output, but binary files are possible.
 * </p>
 * 
 * <p>
 * It handles completely the {@link #writeAll(Graph, OutputStream)},
 * {@link #writeAll(Graph, String)}, {@link #begin(OutputStream)},
 * {@link #begin(String)}, {@link #flush()} and {@link #end()} methods. You
 * should not have to modify or override these.
 * </p>
 * 
 * <p>
 * In order to implement an output you have to:
 * <ul>
 * <li>Eventually override {@link #createWriter(OutputStream)} or
 * {@link #createWriter(String)} to replace the default instance of PrintStream
 * created for you.</li>
 * <li>Implement the {@link #outputHeader()} method. This method is called at
 * start, before any graph event is sent to output. Use it to output the header
 * of your file.</li>
 * <li>Implement the {@link #outputEndOfFile()} method. This method is called at
 * the end of the output, just before closing the output stream. Use it to
 * output any terminating syntax for the file format you implement.</li>
 * <li>Implement all the methods of {@link org.graphstream.stream.Sink}. All
 * these methods will be called for each graph event and must export these
 * events to the file you are writing. You should use the {@link #output} field
 * to write to the file. This field has type {@link java.io.OutputStream} but by
 * default is of type {@link java.io.PrintStream}, as most of the file format
 * will be textual.</li>
 * </ul>
 * </p>
 */
public abstract class FileSinkBase implements FileSink {
	// Attribute

	/**
	 * The output.
	 */
	protected Writer output;

	// Command

	public void writeAll(Graph graph, String fileName) throws IOException {
		begin(fileName);
		exportGraph(graph);
		end();
	}

	public void writeAll(Graph graph, OutputStream stream) throws IOException {
		begin(stream);
		exportGraph(graph);
		end();
	}

	public void writeAll(Graph graph, Writer writer) throws IOException {
		begin(writer);
		exportGraph(graph);
		end();
	}

	/**
	 * Echo each element and attribute of the graph to the actual output.
	 * 
	 * The elements are echoed as add events (add node, add edge, add
	 * attribute). This method guarantees there are no change or delete events.
	 * 
	 * @param graph
	 *            The graph to export.
	 */
	protected void exportGraph(Graph graph) {
		String graphId = graph.getId();
		long timeId = 0;

		for (String key : graph.getAttributeKeySet())
			graphAttributeAdded(graphId, timeId++, key, graph.getAttribute(key));

		for (Node node : graph) {
			String nodeId = node.getId();
			nodeAdded(graphId, timeId++, nodeId);

			if (node.getAttributeCount() > 0)
				for (String key : node.getAttributeKeySet())
					nodeAttributeAdded(graphId, timeId++, nodeId, key,
							node.getAttribute(key));
		}

		for (Edge edge : graph.getEachEdge()) {
			String edgeId = edge.getId();
			edgeAdded(graphId, timeId++, edgeId, edge.getNode0().getId(), edge
					.getNode1().getId(), edge.isDirected());

			if (edge.getAttributeCount() > 0)
				for (String key : edge.getAttributeKeySet())
					edgeAttributeAdded(graphId, timeId++, edgeId, key,
							edge.getAttribute(key));
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.graphstream.stream.file.FileSink#begin(java.lang.String)
	 */
	public void begin(String fileName) throws IOException {
		if (output != null)
			throw new IOException(
					"cannot call begin() twice without calling end() before.");

		output = createWriter(fileName);

		outputHeader();
	}

	/*
	 * (non-Javadoc)
	 * @see org.graphstream.stream.file.FileSink#begin(java.io.OutputStream)
	 */
	public void begin(OutputStream stream) throws IOException {
		if (output != null)
			throw new IOException(
					"cannot call begin() twice without calling end() before.");

		output = createWriter(stream);

		outputHeader();
	}

	/*
	 * (non-Javadoc)
	 * @see org.graphstream.stream.file.FileSink#begin(java.io.Writer)
	 */
	public void begin(Writer writer) throws IOException {
		if (output != null)
			throw new IOException(
					"cannot call begin() twice without calling end() before.");

		output = createWriter(writer);

		outputHeader();
	}

	/*
	 * (non-Javadoc)
	 * @see org.graphstream.stream.file.FileSink#flush()
	 */
	public void flush() throws IOException {
		if (output != null)
			output.flush();
	}

	/*
	 * (non-Javadoc)
	 * @see org.graphstream.stream.file.FileSink#end()
	 */
	public void end() throws IOException {
		outputEndOfFile();
		output.flush();
		output.close();
		output = null;
	}

	/**
	 * Method called at start just after the {@link #output} field is created.
	 * Use it to output the header of the file.
	 * 
	 * @throws IOException
	 *             If any I/O error occurs.
	 */
	protected abstract void outputHeader() throws IOException;

	/**
	 * Method called at the end just before the {@link #output} field is flushed
	 * and closed. Use it to output any information that closes the file.
	 * 
	 * @throws IOException
	 *             If any I/O error occurs.
	 */
	protected abstract void outputEndOfFile() throws IOException;

	/**
	 * Create a a writer from a file name. Override this method if the default
	 * PrintWriter does not suits your needs. This method is called by
	 * {@link #begin(String)} and {@link #writeAll(Graph, String)}.
	 * 
	 * @param fileName
	 *            Name of the file to output to.
	 * @return A new writer.
	 * @throws IOException
	 *             If any I/O error occurs.
	 */
	protected Writer createWriter(String fileName) throws IOException {
		return new PrintWriter(fileName);
	}

	/**
	 * Create a writer from an existing output stream. Override this method if
	 * the default PrintWriter does not suits your needs. This method is called
	 * by {@link #begin(OutputStream)} and
	 * {@link #writeAll(Graph, OutputStream)}. This method does not create an
	 * output stream if the given stream is already instance of PrintStream.
	 * 
	 * @param stream
	 *            An already existing output stream.
	 * @return A new writer.
	 * @throws IOException
	 *             If any I/O error occurs.
	 */
	protected Writer createWriter(OutputStream stream) throws IOException {
		return new PrintWriter(stream);
	}

	/**
	 * Create a writer from an existing writer. Override this method if the
	 * default PrintWriter does not suits your needs. This method is called by
	 * {@link #begin(Writer)} and {@link #writeAll(Graph, Writer)}. This method
	 * does not create a new writer if the given writer is already instance of
	 * PrintWriter.
	 * 
	 * @param writer
	 *            An already existing writer.
	 * @return A new writer.
	 * @throws IOException
	 *             If any I/O error occurs.
	 */
	protected Writer createWriter(Writer writer) throws IOException {
		if (writer instanceof PrintWriter)
			return writer;

		return new PrintWriter(writer);
	}
}