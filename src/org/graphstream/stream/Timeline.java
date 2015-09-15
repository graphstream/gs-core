/*
 * Copyright 2006 - 2015
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
package org.graphstream.stream;

import java.util.Iterator;
import java.util.LinkedList;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.Graphs;
import org.graphstream.graph.implementations.SimpleAdjacencyListGraph;
import org.graphstream.util.GraphDiff;

public class Timeline implements Source, Replayable, Iterable<Graph<?,?>> {

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
	protected Graph<?,?>initialGraph, currentGraph;
	protected GraphDiff currentDiff;
	protected Connector connector;
	protected PipeBase pipe;
	protected int seeker;

	public Timeline() {
		this.diffs = new LinkedList<>();
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
		if (diffs.isEmpty()) {
			return;
		}

		if (from > to) {
			int i = diffs.size() - 1, j;

			while (i > 0 && diffs.get(i).step > from) {
				i--;
			}

			j = i;

			while (j > 0 && diffs.get(j).step >= to) {
				j--;
			}

			for (int k = i; k >= j; k--) {
				diffs.get(k).diff.reverse(sink);
			}
		} else {
			int i = 0, j;

			while (i < diffs.size() - 1 && diffs.get(i).step < from) {
				i++;
			}

			j = i;

			while (j < diffs.size() - 1 && diffs.get(j).step <= to) {
				j++;
			}

			for (int k = i; k <= j; k++) {
				diffs.get(k).diff.apply(sink);
			}
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
		if (seeker >= diffs.size()) {
			return;
		}

		diffs.get(seeker++).diff.apply(pipe);
	}

	public boolean hasPrevious() {
		return seeker > 0;
	}

	public void previous() {
		if (seeker <= 0) {
			return;
		}

		diffs.get(--seeker).diff.reverse(pipe);
	}

	public void begin(Source source) {
		initialGraph = new SimpleAdjacencyListGraph("initial");
		currentGraph = new SimpleAdjacencyListGraph("initial");
		begin();
	}

	public void begin(Graph<?,?>source) {
		initialGraph = Graphs.clone(source);
		currentGraph = source;
		begin();
	}

	protected void begin() {
		currentGraph.addSink(connector);
		pushDiff();
	}

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

	@Override
	public Iterator<Graph<?,?>> iterator() {
		return new TimelineIterator();
	}

	@Override
	public Controller getReplayController() {
		return new TimelineReplayController();
	}

	protected class Connector implements Sink {
		@Override
		public void stepBegins(String sourceId, long timeId, double step) {
			Timeline.this.pushDiff();
		}
	}

	protected class TimelineReplayController extends PipeBase implements
		Controller {
		@Override
		public void replay() {
			play(this);
		}

		@Override
		public void replay(String sourceId) {
			String tmp = this.sourceId;
			this.sourceId = sourceId;
			play(this);
			this.sourceId = tmp;
		}
	}

	protected class TimelineIterator implements Iterator<Graph<?,?>> {
		Graph<?,?> current;
		int idx;

		public TimelineIterator() {
			current = Graphs.clone(initialGraph);
			idx = 0;
		}

		@Override
		public boolean hasNext() {
			return idx < diffs.size();
		}

		@Override
		public Graph<?,?> next() {
			if (idx >= diffs.size()) {
				return null;
			}

			diffs.get(idx++).diff.apply(current);
			return Graphs.clone(current);
		}

		@Override
		public void remove() {
		}

	}

	@Override
	public void addSink(Sink sink) {
		pipe.addSink(sink);
	}

	@Override
	public void removeSink(Sink sink) {
		pipe.removeSink(sink);
	}

	@Override
	public void addAttributeSink(AttributeSink sink) {
		pipe.addAttributeSink(sink);
	}

	@Override
	public void removeAttributeSink(AttributeSink sink) {
		pipe.removeAttributeSink(sink);
	}

	@Override
	public void addElementSink(ElementSink sink) {
		pipe.addElementSink(sink);
	}

	@Override
	public void removeElementSink(ElementSink sink) {
		pipe.removeElementSink(sink);
	}

	@Override
	public void clearElementSinks() {
		pipe.clearElementSinks();
	}

	@Override
	public void clearAttributeSinks() {
		pipe.clearAttributeSinks();
	}

	@Override
	public void clearSinks() {
		pipe.clearSinks();
	}

	@Override
	public Iterable<AttributeSink> attributeSinks() {
		return pipe.attributeSinks();
	}

	@Override
	public Iterable<ElementSink> elementSinks() {
		return pipe.elementSinks();
	}
}
