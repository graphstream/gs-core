/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 * 
 * Copyright 2006 - 2010
 * 	Julien Baudry
 * 	Antoine Dutot
 * 	Yoann Pigné
 * 	Guilhelm Savin
 */

package org.graphstream.stream.net;

import java.io.IOException;
import java.net.URL;

import org.graphstream.stream.Source;

/**
 * Graph event input source from an URL.
 */
public interface URLSource extends Source
{
	/**
	 * Read the whole URL in one big non-interruptible operation.
	 * @param url The URL to fetch.
	 * @throws IOException If an I/O error occurs while fetching the URL.
	 */
	void fetchAll( URL url ) throws IOException;
	
	/**
	 * Begin fetching the URL stopping as soon as possible. Next graph events from the URL
	 * will be send by calling {@link #nextEvents()}. Once begin()
	 * as been called, you must finish the reading process using {@link #end()}. You cannot
	 * call begin() twice without having called {@link #end()} in between.
	 * @param url The URL to fetch.
	 * @throws IOException If an I/O error occurs while reading.
	 */
	void begin( URL url ) throws IOException;
	
	/**
	 * Try to process one graph event, or as few as possible, if more must be read at once.
	 * For this method to work, you must have called {@link #begin(URL)}. 
	 * This method return true while there are still events to read.
	 * @return true if there are still events to read, false as soon as the file is finished.
	 * @throws IOException If an I/O error occurs while reading.
	 */
	boolean nextEvents() throws IOException;
	
	/**
	 * Finish the reading process (even if {@link #nextEvents()} did not
	 * returned false). You must call this method after reading.
	 * @throws IOException If an I/O error occurs while closing the file.
	 */
	void end() throws IOException;
}