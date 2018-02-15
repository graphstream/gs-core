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
 * @since 2013-09-18
 * 
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.stream.file;

import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.xml.stream.XMLStreamException;

import org.graphstream.graph.Graph;
import org.graphstream.stream.GraphReplay;
import org.graphstream.stream.PipeBase;
import org.graphstream.stream.file.gexf.GEXF;
import org.graphstream.stream.file.gexf.SmartXMLWriter;

public class FileSinkGEXF2 extends PipeBase implements FileSink {
	class Context {
		GEXF gexf;
		Writer output;
		SmartXMLWriter stream;
		boolean closeStreamAtEnd;
	}

	Context currentContext;

	Context createContext(String fileName) throws IOException {
		FileWriter w = new FileWriter(fileName);
		Context ctx = createContext(w);
		ctx.closeStreamAtEnd = true;

		return ctx;
	}

	Context createContext(OutputStream output) throws IOException {
		OutputStreamWriter w = new OutputStreamWriter(output);
		return createContext(w);
	}

	Context createContext(Writer w) throws IOException {
		Context ctx = new Context();

		ctx.output = w;
		ctx.closeStreamAtEnd = false;
		ctx.gexf = new GEXF();

		try {
			ctx.stream = new SmartXMLWriter(w, true);
		} catch (Exception e) {
			throw new IOException(e);
		}

		return ctx;
	}

	protected void export(Context ctx, Graph g) throws IOException {
		ctx.gexf.disable(GEXF.Extension.DYNAMICS);

		GraphReplay replay = new GraphReplay("replay");
		replay.addSink(ctx.gexf);
		replay.replay(g);

		try {
			ctx.gexf.export(ctx.stream);
			ctx.stream.close();

			if (ctx.closeStreamAtEnd)
				ctx.output.close();
		} catch (XMLStreamException e) {
			throw new IOException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.file.FileSink#writeAll(org.graphstream.graph.Graph ,
	 * java.lang.String)
	 */
	public void writeAll(Graph graph, String fileName) throws IOException {
		Context ctx = createContext(fileName);
		export(ctx, graph);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.file.FileSink#writeAll(org.graphstream.graph.Graph ,
	 * java.io.OutputStream)
	 */
	public void writeAll(Graph graph, OutputStream stream) throws IOException {
		Context ctx = createContext(stream);
		export(ctx, graph);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.file.FileSink#writeAll(org.graphstream.graph.Graph ,
	 * java.io.Writer)
	 */
	public void writeAll(Graph graph, Writer writer) throws IOException {
		Context ctx = createContext(writer);
		export(ctx, graph);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.file.FileSink#begin(java.lang.String)
	 */
	public void begin(String fileName) throws IOException {
		if (currentContext != null)
			throw new IOException("cannot call begin() twice without calling end() before.");

		currentContext = createContext(fileName);
		addSink(currentContext.gexf);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.file.FileSink#begin(java.io.OutputStream)
	 */
	public void begin(OutputStream stream) throws IOException {
		if (currentContext != null)
			throw new IOException("cannot call begin() twice without calling end() before.");

		currentContext = createContext(stream);
		addSink(currentContext.gexf);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.file.FileSink#begin(java.io.Writer)
	 */
	public void begin(Writer writer) throws IOException {
		if (currentContext != null)
			throw new IOException("cannot call begin() twice without calling end() before.");

		currentContext = createContext(writer);
		addSink(currentContext.gexf);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.file.FileSink#flush()
	 */
	public void flush() throws IOException {
		if (currentContext != null)
			currentContext.stream.flush();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.file.FileSink#end()
	 */
	public void end() throws IOException {
		removeSink(currentContext.gexf);

		try {
			currentContext.gexf.export(currentContext.stream);
			currentContext.stream.close();

			if (currentContext.closeStreamAtEnd)
				currentContext.output.close();
		} catch (XMLStreamException e) {
			throw new IOException(e);
		}

		currentContext = null;
	}
}
