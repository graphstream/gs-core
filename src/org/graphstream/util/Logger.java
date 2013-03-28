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
package org.graphstream.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Logger.
 *
 * @author Frédéric Guinand
 * @author Yoann Pigné
 * @author Antoine Dutot
 * @since  20061108
 */
public class Logger
{
// ---------- Constants --------------

	/**
	 * The different log levels.
	 * 
	 * It can be {@link LogLevel#DEBUG}, {@link LogLevel#INFO},
	 * {@link LogLevel#WARN} or {@link LogLevel#ERROR}. There is an order
	 * between the different log level: DEBUG < INFO < WARN < ERROR. So if you
	 * set the logLevel to DEBUG, then you will receive _all_ the messages, if
	 * you set it to INFO, then you will not receive the DEBUG messages. If you
	 * set it to WARN, you will not receive the INFO or DEBUG messages.
	 * And so on...
	 */
	public enum LogLevel
	{
		DEBUG(  "Debug",   0 ),
		INFO(   "Info",    1 ),
		WARN(   "Warning", 2 ),
		ERROR(  "Error",   3 ),
		RESULT( "Result",  4 );
		
		public String info;
		public int value;
		LogLevel( String info, int value ) { this.info = info; this.value = value; }
		public boolean ge( LogLevel other ) { return value >= other.value; }
	};
	
	public static Logger GLOBAL_LOGGER;
	
// ------- Attributes ------
	
	/**
	 * Name of the log file. Default value is "stderr". It means that output is
	 * written on the standard error stream.
	 */
	protected String logFileName = "stderr";

	/**
	 * Has the logging file been opened yet?.
	 */
	protected boolean logFileOpened = false;

	/**
	 * Output stream.
	 */
	protected static PrintWriter out;
	
	/**
	 * The current log level;
	 */
	protected LogLevel logLevel = LogLevel.DEBUG;

// ------- Methods -------

	/**
	 * The method that every class of the package should use to send exception
	 * messages to the user.
	 * @param level The log level of the message.
	 * @param ref The name of the class calling this method.
	 * @param e The exception to log.
	 */
	public void
	log( LogLevel level, String ref, Exception e )
	{
		if( level.ge( logLevel ) )
		{
			try
			{
				openLogFile();

				out.printf( "%-5s : %s : %s\n", logLevel.info, ref, e.toString() );
				out.printf( "The exception is in %s", java.lang.Thread.currentThread().toString() );
				e.printStackTrace( out );
				out.flush();
			}
			catch( IOException ioe )
			{
				System.err.printf( "%-5s : %s : %s\n", "ERROR", "Environment",
						ioe.toString() );
				ioe.printStackTrace();
				System.exit( 0 );
			}
		}
	}

	/**
	 * The method that every class of the package should use to send messages to
	 * the user.
	 * @param level The log level of the message.
	 * @param ref The name of the class calling this method.
	 * @param message The message to log (can be in printf format).
	 * @param params The parameter of the message if in printf format.
	 */
	public void
	log( LogLevel level, String ref, String message, Object ... params )
	{
		if( level.ge( logLevel ) )
		{
			try
			{
				openLogFile();

				out.printf( "%-5s : %s : ", level.info, ref );
				out.printf( message, params );
				out.printf( "%n" );
				out.flush();
			}
			catch( IOException ioe )
			{
				System.err.printf( "%-5s : %s : %s\n", "ERROR", "Environment",
						ioe.toString() );
				ioe.printStackTrace();
				System.exit( 0 );
			}
		}
	}
	
	/**
	 * Verifies that the output log file is open, and if not open it. 
	 * @throws IOException For any error while openning the file.
	 */
	protected void
	openLogFile()
		throws IOException
	{
		if( ! logFileOpened )
		{
			if( logFileName.equals( "stderr" ) )
			{
				out = new PrintWriter( System.err );
			}
			else
			{
				out = new PrintWriter( new BufferedWriter(
						new FileWriter( logFileName ) ) );
			}
			
			logFileOpened = true;
		}
	}
	
	/**
	 * Change the log level.
	 * @param level The new log level.
	 */
	public void
	setLogLevel( LogLevel level )
	{
		logLevel = level;
	}
	
	/**
	 * Return the shared global instance of the logger. This singleton instance
	 * is avaiable in the whole JVM.
	 * @return The singleton global instance of the logger.
	 */
	public static Logger
	getGlobalLogger()
	{
		if( GLOBAL_LOGGER == null )
			GLOBAL_LOGGER = new Logger();
		
		return GLOBAL_LOGGER;
	}
}