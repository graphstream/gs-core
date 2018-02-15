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
 * @since 2009-12-22
 * 
 * @author Yoann Pigné <yoann.pigne@graphstream-project.org>
 * @author Antoine Dutot <antoine.dutot@graphstream-project.org>
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
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