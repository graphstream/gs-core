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
 * @since 2013-01-17
 * 
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.stream;

/**
 * Defines sources that can be replayed. This is usefull when you are connecting
 * a sink to a source but you need to get informations about the current state
 * of the dynamic graph.
 * <p>
 * 
 * <pre>
 * Replayable source = ... ;
 * Replayable.Controller replay = source.getReplayController();
 * ...
 * // source is building a graph
 * ...
 * Graph g = ... ;
 * //
 * // Replay the source to get the current state of the graph
 * //
 * replay.addSink(g);
 * replay.replay();
 * </pre>
 */
public interface Replayable {
	/**
	 * Get a controller to replay the graph.
	 *
	 * @return a new replay controller
	 */
	Controller getReplayController();

	/**
	 * A controller used to replay a source. Controller should be used as a source
	 * by adding sinks on it. When sinks are set, a call to {@link #replay()} send
	 * events describing the current state of the original source to sinks.
	 */
	public static interface Controller extends Source {
		/**
		 * Replay events describing the current state of the object being built by the
		 * source.
		 */
		void replay();

		/**
		 * Same as {@link #replay(Sink)} but you can set the id of the source sent in
		 * events.
		 *
		 * @param sourceId
		 *            id of the event source
		 */
		void replay(String sourceId);
	}

	/**
	 * Util method to replay a replayable source into a sink.
	 *
	 * @param source
	 *            a source implementing the Replayable interface
	 * @param sink
	 *            sink which will receive the events produced by the replay
	 */
	public static void replay(Replayable source, Sink sink) {
		Controller controller = source.getReplayController();

		controller.addSink(sink);
		controller.replay();
		controller.removeSink(sink);
	}

	/**
	 * Same as {@link #replay(Replayable, Sink)} but the first parameter is just a
	 * {@link org.graphstream.stream.Source} and it will be replayed only if the
	 * Replayable interface is implemented.
	 *
	 * @param source
	 *            a source
	 * @param sink
	 *            sink which will receive the events produced by the replay
	 */
	public static void tryReplay(Source source, Sink sink) {
		if (source instanceof Replayable)
			replay((Replayable) source, sink);
	}
}
