/**
 * 
 * Copyright (c) 2013 University of Le Havre
 * 
 * @file NetStreamDecoder.java
 * @date May 31, 2013
 * 
 * @author Yoann Pigné
 * 
 */
package org.graphstream.stream.netstream;

import java.io.IOException;
import java.io.InputStream;

import org.graphstream.stream.thread.ThreadProxyPipe;

/**
 * 
 */
public interface NetStreamDecoder {

	/**
	 * Gives the stream (a ThreadProxyPipe) identified with this name. If no
	 * pipe exists under this name, a new one is created and returned
	 * 
	 * @param name
	 *            Identifier of the stream.
	 * @return the identified pipe
	 */
	public abstract ThreadProxyPipe getStream(String name);

	/**
	 * Gives the default stream (a ThreadProxyPipe) identified with the name
	 * "default". It is created if it does not exist.
	 * 
	 * @return the default pipe
	 */

	public abstract ThreadProxyPipe getDefaultStream();

	/**
	 * Register a stream. All events with the given stream name will be directed
	 * to it. The user has to ensure the ThreadProxyPipe can be safely written
	 * to by the Receiver's thread.
	 * 
	 * @param name
	 *            Filter only message with this name to the given message box.
	 * @param stream
	 *            The ThreadProxyPipe to push the events to.
	 * @throws Exception
	 *             If another Pipe is already registered at the given name.
	 */
	public abstract void register(String name, ThreadProxyPipe stream)
			throws Exception;

	/**
	 * Decode one message.
	 */
	public abstract void decodeMessage(InputStream in) throws IOException;

	/**
	 * Enable or disable debugging.
	 */
	public void setDebugOn(boolean on);
}