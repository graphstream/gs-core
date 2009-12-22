/*
 * This file is part of GraphStream.
 * 
 * GraphStream is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * GraphStream is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with GraphStream.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Copyright 2006 - 2009
 * 	Julien Baudry
 * 	Antoine Dutot
 * 	Yoann Pigné
 * 	Guilhelm Savin
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
