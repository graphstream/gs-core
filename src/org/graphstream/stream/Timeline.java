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
 * @since 2012-05-21
 * 
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.stream;

import java.util.Iterator;
import java.util.LinkedList;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.AdjacencyListGraph;
import org.graphstream.graph.implementations.Graphs;
import org.graphstream.util.GraphDiff;
import org.graphstream.util.VerboseSink;

public class Timeline implements Source, Replayable, Iterable<Graph> {

	public static final String TIME_PREFIX = "time";

	private class StepDiff {
		double step;
		GraphDiff diff;

		StepDiff(double step, GraphDiff diff) {
			this.step = step;
			this.diff = diff;
		}
	}

	LinkedList<StepDiff> diffs;

	protected boolean changed;
	protected Graph initialGraph, currentGraph;
	protected GraphDiff currentDiff;
	protected Connector connector;
	protected PipeBase pipe;
	protected int seeker;

	public Timeline() {
		this.diffs = new LinkedList<StepDiff>();
		this.changed = false;
		this.connector = new Connector();
		this.currentDiff = null;
		this.pipe = new PipeBase();
	}

	public void reset() {

	}

	public void play(double from, double to) {
		play(from, to, pipe);
	}

	public void play(double from, double to, Sink sink) {
		if (diffs.size() == 0)
			return;

		if (from > to) {
			int i = diffs.size() - 1, j;

			while (i > 0 && diffs.get(i).step > from)
				i--;

			j = i;

			while (j > 0 && diffs.get(j).step >= to)
				j--;

			for (int k = i; k >= j; k--)
				diffs.get(k).diff.reverse(sink);
		} else {
			int i = 0, j;

			while (i < diffs.size() - 1 && diffs.get(i).step < from)
				i++;

			j = i;

			while (j < diffs.size() - 1 && diffs.get(j).step <= to)
				j++;

			for (int k = i; k <= j; k++)
				diffs.get(k).diff.apply(sink);
		}
	}

	public void play() {
		play(initialGraph.getStep(), currentGraph.getStep());
	}

	public void play(Sink sink) {
		play(initialGraph.getStep(), currentGraph.getStep(), sink);
	}

	public void playback() {
		play(currentGraph.getStep(), initialGraph.getStep());
	}

	public void playback(Sink sink) {
		play(currentGraph.getStep(), initialGraph.getStep(), sink);
	}

	public void seek(int i) {
		seeker = i;
	}

	public void seekStart() {
		seeker = 0;
	}

	public void seekEnd() {
		seeker = diffs.size();
	}

	public boolean hasNext() {
		return seeker < diffs.size();
	}

	public void next() {
		if (seeker >= diffs.size())
			return;

		diffs.get(seeker++).diff.apply(pipe);
	}

	public boolean hasPrevious() {
		return seeker > 0;
	}

	public void previous() {
		if (seeker <= 0)
			return;

		diffs.get(--seeker).diff.reverse(pipe);
	}

	/**
	 * @param source
	 */
	public void begin(Source source) {
		initialGraph = new AdjacencyListGraph("initial");
		currentGraph = new AdjacencyListGraph("initial");
		begin();
	}

	/**
	 * @param source
	 */
	public void begin(Graph source) {
		initialGraph = Graphs.clone(source);
		currentGraph = source;
		begin();
	}

	protected void begin() {
		currentGraph.addSink(connector);
		pushDiff();
	}

	/**
	 *
	 */
	public void end() {
		if (currentDiff != null) {
			currentDiff.end();
			diffs.add(new StepDiff(currentGraph.getStep(), currentDiff));
		}

		currentGraph.removeSink(connector);
		currentGraph = Graphs.clone(currentGraph);
	}

	protected void pushDiff() {
		if (currentDiff != null) {
			currentDiff.end();
			diffs.add(new StepDiff(currentGraph.getStep(), currentDiff));
		}

		currentDiff = new GraphDiff();
		currentDiff.start(currentGraph);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<Graph> iterator() {
		return new TimelineIterator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.Replayable#getReplayController()
	 */
	public Controller getReplayController() {
		return new TimelineReplayController();
	}

	protected class Connector extends SinkAdapter {
		@Override
		public void stepBegins(String sourceId, long timeId, double step) {
			Timeline.this.pushDiff();
		}
	}

	protected class TimelineReplayController extends PipeBase implements Controller {
		public void replay() {
			play(this);
		}

		public void replay(String sourceId) {
			String tmp = this.sourceId;
			this.sourceId = sourceId;
			play(this);
			this.sourceId = tmp;
		}
	}

	protected class TimelineIterator implements Iterator<Graph> {
		Graph current;
		int idx;

		public TimelineIterator() {
			current = Graphs.clone(initialGraph);
			idx = 0;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext() {
			return idx < diffs.size();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Iterator#next()
		 */
		public Graph next() {
			if (idx >= diffs.size())
				return null;

			diffs.get(idx++).diff.apply(current);
			return Graphs.clone(current);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Iterator#remove()
		 */
		public void remove() {
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.Source#addSink(org.graphstream.stream.Sink)
	 */
	public void addSink(Sink sink) {
		pipe.addSink(sink);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.Source#removeSink(org.graphstream.stream.Sink)
	 */
	public void removeSink(Sink sink) {
		pipe.removeSink(sink);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.Source#addAttributeSink(org.graphstream.stream
	 * .AttributeSink)
	 */
	public void addAttributeSink(AttributeSink sink) {
		pipe.addAttributeSink(sink);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.Source#removeAttributeSink(org.graphstream.stream
	 * .AttributeSink)
	 */
	public void removeAttributeSink(AttributeSink sink) {
		pipe.removeAttributeSink(sink);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.Source#addElementSink(org.graphstream.stream.
	 * ElementSink)
	 */
	public void addElementSink(ElementSink sink) {
		pipe.addElementSink(sink);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.Source#removeElementSink(org.graphstream.stream
	 * .ElementSink)
	 */
	public void removeElementSink(ElementSink sink) {
		pipe.removeElementSink(sink);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.Source#clearElementSinks()
	 */
	public void clearElementSinks() {
		pipe.clearElementSinks();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.Source#clearAttributeSinks()
	 */
	public void clearAttributeSinks() {
		pipe.clearAttributeSinks();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.Source#clearSinks()
	 */
	public void clearSinks() {
		pipe.clearSinks();
	}

	public static void main(String... strings) throws Exception {
		Graph g = new AdjacencyListGraph("g");
		Timeline timeline = new Timeline();
		timeline.addSink(new VerboseSink());

		timeline.begin(g);

		g.stepBegins(0.0);
		g.addNode("A");
		g.addNode("B");
		g.stepBegins(1.0);
		g.addNode("C");

		timeline.end();

		System.out.printf("############\n");
		System.out.printf("# Play :\n");
		timeline.play();
		System.out.printf("############\n");
		System.out.printf("# Playback :\n");
		timeline.playback();
		System.out.printf("############\n");
		System.out.printf("# Sequence :\n");
		int i = 0;
		for (Graph it : timeline) {
			System.out.printf(" Graph#%d %s\n", i, toString(it));
		}
		System.out.printf("############\n");
	}

	private static String toString(Graph g) {
		StringBuilder buffer = new StringBuilder();
		buffer.append("id=\"").append(g.getId()).append("\" node={");

		g.nodes().forEach(n -> buffer.append("\"").append(n.getId()).append("\", "));

		buffer.append("}, edges={");

		g.edges().forEach(e -> {
			buffer.append("\"").append(e.getId()).append("\":\"").append(e.getSourceNode().getId()).append("\"--\"")
					.append(e.getTargetNode().getId()).append("\", ");
		});

		buffer.append("}");

		return buffer.toString();
	}
}
