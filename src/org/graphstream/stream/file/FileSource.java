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
package org.graphstream.stream.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

import org.graphstream.stream.Source;

/**
 * Source of graph events coming from a file.
 * 
 * <p>
 * The file input interface is an input with specific methods that deals with
 * files. File inputs are designed to handle graphs stored under the form of a
 * textual or binary file either under the form of a file name or a Java input
 * stream. If the file comes from an URL, convert the URL to an input stream.
 * </p>
 * 
 * <p>
 * The file package is designed under the idea that it provides graph inputs
 * from files that store the graph under a given file format and encoding. The
 * package provides decoders for all these formats.
 * </p>
 * 
 * <p>
 * Do not confuse the file package with the net package that can also read from
 * URLs, but build graph not from encoded description of a graph, but from web
 * services like Flickr or Amazon, or simply networks of web pages tied by web
 * links. The graph construction task is entirely different.
 * </p>
 * 
 * <p>
 * Although not all graph format handle dynamic graphs, all file inputs must
 * provide both the readAll() and begin()/nextEvents()/end() methods. The later
 * must read one graph modification at a time.
 * </p>
 */
public interface FileSource extends Source {
	/**
	 * Read the whole file in one big non-interruptible operation.
	 * 
	 * @param fileName
	 *            Name of the file to read.
	 * @throws IOException
	 *             If an I/O error occurs while reading.
	 */
	void readAll(String fileName) throws IOException;

	/**
	 * Read the whole file in one big non-interruptible operation.
	 * 
	 * @param url
	 *            The URL of the file to read.
	 * @throws IOException
	 *             If an I/O error occurs while reading.
	 */
	void readAll(URL url) throws IOException;

	/**
	 * Read the whole file in one big non-interruptible operation.
	 * 
	 * @param stream
	 *            The input stream to use for reading.
	 * @throws IOException
	 *             If an I/O error occurs while reading.
	 */
	void readAll(InputStream stream) throws IOException;

	/**
	 * Read the whole file in one big non-interruptible operation.
	 * 
	 * @param reader
	 *            The reader to use.
	 * @throws IOException
	 *             If an I/O error occurs while reading.
	 */
	void readAll(Reader reader) throws IOException;

	/**
	 * Begin reading the file stopping as soon as possible. Next graph events stored
	 * in the file will be sent by calling {@link #nextEvents()} or
	 * {@link #nextStep()}. Once begin() has been called, you must finish the
	 * reading process using {@link #end()}. You cannot call begin() twice without
	 * having called {@link #end()} in between.
	 * 
	 * @param fileName
	 *            Name of the file to read.
	 * @throws IOException
	 *             If an I/O error occurs while reading.
	 */
	void begin(String fileName) throws IOException;

	/**
	 * Begin reading the file stopping as soon as possible. Next graph events stored
	 * in the file will be sent by calling {@link #nextEvents()} or
	 * {@link #nextStep()}. Once begin() has been called, you must finish the
	 * reading process using {@link #end()}. You cannot call begin() twice without
	 * having called {@link #end()} in between.
	 * 
	 * @param url
	 *            The URL of the file to read.
	 * @throws IOException
	 *             If an I/O error occurs while reading.
	 */
	void begin(URL url) throws IOException;

	/**
	 * Begin reading the file stopping as soon as possible. Next graph events stored
	 * in the file will be sent by calling {@link #nextEvents()} or
	 * {@link #nextStep()}. Once begin() has been called, you must finish the
	 * reading process using {@link #end()}. You cannot call begin() twice without
	 * having called {@link #end()} in between.
	 * 
	 * @param stream
	 *            The input stream to use for reading.
	 * @throws IOException
	 *             If an I/O error occurs while reading.
	 */
	void begin(InputStream stream) throws IOException;

	/**
	 * Begin reading the file stopping as soon as possible. Next graph events stored
	 * in the file will be sent by calling {@link #nextEvents()} or
	 * {@link #nextStep()}. Once begin() has been called, you must finish the
	 * reading process using {@link #end()}. You cannot call begin() twice without
	 * having called {@link #end()} in between.
	 * 
	 * @param reader
	 *            The file reader to use.
	 * @throws IOException
	 *             If an I/O error occurs while reading.
	 */
	void begin(Reader reader) throws IOException;

	/**
	 * Try to process one graph event, or as few as possible, if more must be read
	 * at once. For this method to work, you must have called
	 * {@link #begin(InputStream)} or {@link #begin(String)} before. This method
	 * return true while there are still events to read.
	 * 
	 * @return true if there are still events to read, false as soon as the file is
	 *         finished.
	 * @throws IOException
	 *             If an I/O error occurs while reading.
	 */
	boolean nextEvents() throws IOException;

	/**
	 * Try to process all the events occurring during one time step. In GraphStream,
	 * a time step is a group of events that are considered occurring at the same
	 * time. Most file formats do not have this notion of step. The DGS format
	 * designed for GraphStream handles steps. This method return true while there
	 * are still events to read.
	 * 
	 * @return true if there are still events to read, false as soon as the file is
	 *         finished.
	 * @throws IOException
	 *             If an I/O error occurs while reading.
	 */
	boolean nextStep() throws IOException;

	/**
	 * Finish the reading process (even if {@link #nextEvents()} or
	 * {@link #nextStep()} did not returned false). You must call this method after
	 * reading.
	 * 
	 * @throws IOException
	 *             If an I/O error occurs while closing the file.
	 */
	void end() throws IOException;
}