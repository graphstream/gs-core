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
 * Copyright 2006 - 2009
 * 	Julien Baudry
 * 	Antoine Dutot
 * 	Yoann Pigné
 * 	Guilhelm Savin
 */

package org.miv.graphstream.io2.file;

import java.io.IOException;
import java.io.InputStream;

import org.miv.graphstream.io2.Input;

/**
 * Source of graph events coming from a file.
 */
public interface FileInput extends Input
{
	/**
	 * Read the whole file in one big non-interruptible operation.
	 * @param fileName Name of the file to read.
	 * @throws IOException If an I/O error occurs while reading.
	 */
	void readAll( String fileName ) throws IOException;
	
	/**
	 * Read the whole file in one big non-interruptible operation.
	 * @param stream The input stream to use for reading.
	 * @throws IOException If an I/O error occurs while reading.
	 */
	void readAll( InputStream stream ) throws IOException;
	
	/**
	 * Begin reading the file stopping as soon as possible. Next graph events stored in the file
	 * will be send by calling {@link #nextEvents()} or {@link #nextStep()}. Once begin()
	 * as been called, you must finish the reading process using {@link #end()}. You cannot
	 * call begin() twice without having called {@link #end()} in between.
	 * @param fileName Name of the file to read.
	 * @throws IOException If an I/O error occurs while reading.
	 */
	void begin( String fileName ) throws IOException;
	
	
	/**
	 * Begin reading the file stopping as soon as possible. Next graph events stored in the file
	 * will be send by calling {@link #nextEvents()} or {@link #nextStep()}. Once begin()
	 * as been called, you must finish the reading process using {@link #end()}. You cannot
	 * call begin() twice without having called {@link #end()} in between.
	 * @param stream The input stream to use for reading.
	 * @throws IOException If an I/O error occurs while reading.
	 */
	void begin( InputStream stream ) throws IOException;
	
	/**
	 * Try to process one graph event, or as few as possible, if more must be read at once.
	 * For this method to work, you must have called {@link #begin(InputStream)} or
	 * {@link #begin(String)} before. This method return true while there are still events to
	 * read.
	 * @returns true if there are still events to read, false as soon as the file is finished.
	 * @throws IOException If an I/O error occurs while reading.
	 */
	boolean nextEvents() throws IOException;
	
	/**
	 * Try to process all the events occurring during one time step. In GraphStream, a time step is
	 * a group of events that are considered occurring at the same time. Most file formats do not
	 * have this notion of step. The DGS format designed for GraphStream handles steps. This method
	 * return true while there are still events to read.
	 * @returns true if there are still events to read, false as soon as the file is finished.
	 * @throws IOException If an I/O error occurs while reading.
	 */
	boolean nextStep() throws IOException;
	
	/**
	 * Finish the reading process (even if {@link #nextEvents()} or {@link #nextStep()} did not
	 * returned false). You must call this method after reading.
	 * @throws IOException If an I/O error occurs while closing the file.
	 */
	void end() throws IOException;
}