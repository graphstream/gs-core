/*
 * Copyright 2006 - 2011 
 *     Stefan Balev     <stefan.balev@graphstream-project.org>
 *     Julien Baudry	<julien.baudry@graphstream-project.org>
 *     Antoine Dutot	<antoine.dutot@graphstream-project.org>
 *     Yoann Pigné		<yoann.pigne@graphstream-project.org>
 *     Guilhelm Savin	<guilhelm.savin@graphstream-project.org>
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
package org.graphstream.stream.file.dot;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import org.graphstream.stream.SourceBase;
import org.graphstream.stream.file.FileSource;

public class FileSourceDOT extends SourceBase implements FileSource {

	protected DOTParser parser;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.file.FileSource#readAll(java.lang.String)
	 */
	public void readAll(String fileName) throws IOException {
		DOTParser parser = new DOTParser(this, new FileReader(fileName));

		try {
			parser.all();
		} catch (ParseException e) {
			throw new IOException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.file.FileSource#readAll(java.net.URL)
	 */
	public void readAll(URL url) throws IOException {
		DOTParser parser = new DOTParser(this, new InputStreamReader(
				url.openStream()));

		try {
			parser.all();
		} catch (ParseException e) {
			throw new IOException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.file.FileSource#readAll(java.io.InputStream)
	 */
	public void readAll(InputStream stream) throws IOException {
		DOTParser parser = new DOTParser(this, stream);

		try {
			parser.all();
		} catch (ParseException e) {
			throw new IOException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.file.FileSource#readAll(java.io.Reader)
	 */
	public void readAll(Reader reader) throws IOException {
		DOTParser parser = new DOTParser(this, reader);

		try {
			parser.all();
		} catch (ParseException e) {
			throw new IOException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.file.FileSource#begin(java.lang.String)
	 */
	public void begin(String fileName) throws IOException {
		parser = new DOTParser(this, new FileReader(fileName));

		try {
			parser.begin();
		} catch (ParseException e) {
			throw new IOException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.file.FileSource#begin(java.net.URL)
	 */
	public void begin(URL url) throws IOException {
		parser = new DOTParser(this, new InputStreamReader(url.openStream()));

		try {
			parser.begin();
		} catch (ParseException e) {
			throw new IOException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.file.FileSource#begin(java.io.InputStream)
	 */
	public void begin(InputStream stream) throws IOException {
		parser = new DOTParser(this, new InputStreamReader(stream));

		try {
			parser.begin();
		} catch (ParseException e) {
			throw new IOException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.file.FileSource#begin(java.io.Reader)
	 */
	public void begin(Reader reader) throws IOException {
		parser = new DOTParser(this, reader);

		try {
			parser.begin();
		} catch (ParseException e) {
			throw new IOException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.file.FileSource#nextEvents()
	 */
	public boolean nextEvents() throws IOException {
		try {
			return parser.next();
		} catch (ParseException e) {
			throw new IOException(e);
		}
	}

	/**
	 * Since there is no step in DOT, this does the same action than
	 * {@link #nextEvents()}.
	 */
	public boolean nextStep() throws IOException {
		return nextEvents();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.file.FileSource#end()
	 */
	public void end() throws IOException {
		parser.end();
	}
}
