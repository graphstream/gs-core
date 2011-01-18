/*
 * Copyright 2006 - 2011 
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
package org.graphstream.io.old;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.HashSet;

import org.graphstream.graph.NotFoundException;
import org.graphstream.io.GraphParseException;
import org.graphstream.io.tlp.GraphReaderTLPParser;

/**
 *
 */
public class GraphReaderTLP
	implements GraphReader
{
	final HashSet<GraphReaderListenerExtended> listeners = new HashSet<GraphReaderListenerExtended>();
	GraphReaderTLPParser parser;
	
	public GraphReaderTLP()
	{
		parser = new GraphReaderTLPParser(listeners);
	}
	
	/* (non-Javadoc)
	 * @see org.miv.graphstream.io.GraphReader#addGraphReaderListener(org.miv.graphstream.io.GraphReaderListener)
	 */
	public void addGraphReaderListener(GraphReaderListener listener) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.miv.graphstream.io.GraphReader#addGraphReaderListener(org.miv.graphstream.io.GraphReaderListenerExtended)
	 */
	public void addGraphReaderListener(GraphReaderListenerExtended listener) {
		listeners.add(listener);
	}

	/* (non-Javadoc)
	 * @see org.miv.graphstream.io.GraphReader#begin(java.lang.String)
	 */
	public void begin(String filename) throws NotFoundException,
			GraphParseException, IOException {
		try
		{
			FileReader reader = new FileReader(filename);
			
			parser.init(reader);
			parser.start();
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.miv.graphstream.io.GraphReader#begin(java.io.InputStream)
	 */
	public void begin(InputStream stream) throws GraphParseException,
			IOException {
		try
		{
			parser.init(stream);
			parser.start();
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.miv.graphstream.io.GraphReader#begin(java.io.Reader)
	 */
	public void begin(Reader reader) throws GraphParseException, IOException {
		try
		{
			parser.init(reader);
			parser.start();
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.miv.graphstream.io.GraphReader#end()
	 */
	public void end() throws GraphParseException, IOException {
	}

	/* (non-Javadoc)
	 * @see org.miv.graphstream.io.GraphReader#nextEvents()
	 */
	public boolean nextEvents() throws GraphParseException, IOException {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.miv.graphstream.io.GraphReader#nextStep()
	 */
	public boolean nextStep() throws GraphParseException, IOException {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.miv.graphstream.io.GraphReader#read(java.lang.String)
	 */
	public void read(String filename) throws NotFoundException,
			GraphParseException, IOException {
		try
		{
			FileReader reader = new FileReader(filename);
			
			parser.init(reader);
			parser.start();
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.miv.graphstream.io.GraphReader#read(java.io.InputStream)
	 */
	public void read(InputStream stream) throws GraphParseException,
			IOException {
		try
		{
			parser.init(stream);
			parser.start();
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.miv.graphstream.io.GraphReader#read(java.io.Reader)
	 */
	public void read(Reader reader) throws GraphParseException, IOException {
		try
		{
			parser.init(reader);
			parser.start();
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.miv.graphstream.io.GraphReader#removeGraphReaderListener(org.miv.graphstream.io.GraphReaderListener)
	 */
	public void removeGraphReaderListener(GraphReaderListener listener) {
	}

	/* (non-Javadoc)
	 * @see org.miv.graphstream.io.GraphReader#removeGraphReaderListener(org.miv.graphstream.io.GraphReaderListenerExtended)
	 */
	public void removeGraphReaderListener(GraphReaderListenerExtended listener) {
		listeners.remove(listener);
	}

	public static void main( String [] args )
	{
		GraphReaderTLP grtlp = new GraphReaderTLP();
		
		try
		{
			grtlp.read("/home/raziel/workspace/sample.tlp");
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
}
