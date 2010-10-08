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
 * Copyright 2006 - 2010
 * 	Julien Baudry
 * 	Antoine Dutot
 * 	Yoann Pigné
 * 	Guilhelm Savin
 */

package org.graphstream.graph;

/**
 * Singleton exception.
 * 
 * <p>
 * This exception can be raised when a class instance should be unique in one
 * context, but was instantiated several times. This can be used in a
 * "singleton" pattern, but also in set classes (containers) when the same
 * element has been inserted multiple times but should not.
 * </p>
 * 
 * @since 19990811
 */
public class IdAlreadyInUseException extends RuntimeException {
	private static final long serialVersionUID = -3000770118436738366L;

	/**
	 * Throws the message "singleton exception".
	 */
	public IdAlreadyInUseException() {
		super("singleton exception");
	}

	/**
	 * Throws a given message.
	 * 
	 * @param message
	 *            The message to throw.
	 */
	public IdAlreadyInUseException(String message) {
		super("singleton exception: " + message);
	}
}