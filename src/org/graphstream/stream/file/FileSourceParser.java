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
 * @since 2011-07-22
 * 
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.stream.file;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import org.graphstream.stream.SourceBase;
import org.graphstream.util.parser.ParseException;
import org.graphstream.util.parser.Parser;
import org.graphstream.util.parser.ParserFactory;

/**
 * This defines source using a {@link org.graphstream.util.parser.Parser} object
 * to parse a stream and generate graph events.
 * 
 */
public abstract class FileSourceParser extends SourceBase implements FileSource {
	/**
	 * Factory used to create parser.
	 */
	protected ParserFactory factory;

	/**
	 * Parser opened by a call to {@link #begin(Reader)}.
	 */
	protected Parser parser;

	/**
	 * Get a new parser factory.
	 * 
	 * @return a parser factory
	 */
	public abstract ParserFactory getNewParserFactory();

	protected FileSourceParser() {
		factory = getNewParserFactory();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.file.FileSource#readAll(java.lang.String)
	 */
	public void readAll(String fileName) throws IOException {
		Parser parser = factory.newParser(createReaderForFile(fileName));

		try {
			parser.all();
			parser.close();
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
		Parser parser = factory.newParser(new InputStreamReader(url.openStream()));

		try {
			parser.all();
			parser.close();
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
		Parser parser = factory.newParser(new InputStreamReader(stream));

		try {
			parser.all();
			parser.close();
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
		Parser parser = factory.newParser(reader);

		try {
			parser.all();
			parser.close();
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
		if (parser != null)
			end();

		parser = factory.newParser(createReaderForFile(fileName));

		try {
			parser.open();
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
		parser = factory.newParser(new InputStreamReader(url.openStream()));

		try {
			parser.open();
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
		parser = factory.newParser(new InputStreamReader(stream));

		try {
			parser.open();
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
		parser = factory.newParser(reader);

		try {
			parser.open();
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
		parser.close();
		parser = null;
	}

	protected Reader createReaderForFile(String filename) throws IOException {
		return new FileReader(filename);
	}
}
