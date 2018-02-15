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
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.stream.file;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.zip.GZIPInputStream;

import org.graphstream.stream.file.dgs.DGSParser;
import org.graphstream.util.parser.ParseException;
import org.graphstream.util.parser.Parser;
import org.graphstream.util.parser.ParserFactory;

/**
 * Class responsible for parsing files in the DGS format.
 * 
 * <p>
 * The DGS file format is especially designed for storing dynamic graph
 * definitions into a file. More information about the DGS file format will be
 * found on the GraphStream web site:
 * <a href="http://graphstream-project.org/">http://graphstream-project.org/</a>
 * </p>
 * 
 * The usual file name extension used for this format is ".dgs".
 * 
 * @see FileSource
 */
public class FileSourceDGS extends FileSourceParser {
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.file.FileSourceParser#getNewParserFactory()
	 */
	public ParserFactory getNewParserFactory() {
		return new ParserFactory() {
			public Parser newParser(Reader reader) {
				return new DGSParser(FileSourceDGS.this, reader);
			}
		};
	}

	@Override
	public boolean nextStep() throws IOException {
		try {
			return ((DGSParser) parser).nextStep();
		} catch (ParseException e) {
			throw new IOException(e);
		}
	}

	@Override
	protected Reader createReaderForFile(String filename) throws IOException {
		InputStream is = null;

		is = new FileInputStream(filename);

		if (is.markSupported())
			is.mark(128);

		try {
			is = new GZIPInputStream(is);
		} catch (IOException e1) {
			//
			// This is not a gzip input.
			// But gzip has eat some bytes so we reset the stream
			// or close and open it again.
			//
			if (is.markSupported()) {
				try {
					is.reset();
				} catch (IOException e2) {
					//
					// Dirty but we hope do not get there
					//
					e2.printStackTrace();
				}
			} else {
				try {
					is.close();
				} catch (IOException e2) {
					//
					// Dirty but we hope do not get there
					//
					e2.printStackTrace();
				}

				is = new FileInputStream(filename);
			}
		}

		return new BufferedReader(new InputStreamReader(is));
	}
}
