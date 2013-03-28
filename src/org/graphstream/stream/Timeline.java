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
package org.graphstream.stream;

import java.util.Iterator;
import java.util.TreeSet;

public class Timeline extends SourceBase implements Sink {

	public static final String TIME_PREFIX = "time";

	protected TreeSet<Event> events;
	protected boolean changed;
	protected long currentDate;
	protected int currentEvent;

	public Timeline() {
		this.events = new TreeSet<Event>();
		this.changed = false;
		this.currentDate = 0;
		this.currentEvent = 0;
	}

	private void insert(Event e) {
		events.add(e);
	}

	public void reset() {
		currentEvent = 0;
		events.clear();
	}

	public boolean next() {
		return false;
	}
	
	public void play(long dateFrom, long dateTo) {
		long timeId;
		Iterator<Event> it;
		Event e;

		if (dateFrom < dateTo)
			it = events.iterator();
		else {
			timeId = dateTo;
			dateTo = dateFrom;
			dateFrom = timeId;
			it = events.descendingIterator();
		}

		timeId = 0;

		while (it.hasNext()) {
			e = it.next();

			if (e.date >= dateFrom && e.date <= dateTo)
				e.doEvent(timeId++);
		}
	}

	public void playAll() {
		play(events.first().date, events.last().date);
	}

	public void addNodeAt(long date, String nodeId) {
		insert(new NodeAdded(date, nodeId));
	}

	public void removeNodeAt(long date, String nodeId) {
		insert(new NodeRemoved(date, nodeId));
	}

	public void addEdgeAt(long date, String edgeId, String source,
			String target, boolean directed) {
		insert(new EdgeAdded(date, edgeId, source, target, directed));
	}

	public void removeEdgeAt(long date, String edgeId) {
		insert(new EdgeRemoved(date, edgeId));
	}

	public void addNodeAttributeAt(long date, String nodeId,
			String attributeId, Object value) {
		insert(new AttributeAdded(date, ElementType.NODE, nodeId, attributeId,
				value));
	}

	public void addEdgeAttributeAt(long date, String edgeId,
			String attributeId, Object value) {
		insert(new AttributeAdded(date, ElementType.EDGE, edgeId, attributeId,
				value));
	}

	public void addGraphAttributeAt(long date, String attributeId, Object value) {
		insert(new AttributeAdded(date, ElementType.GRAPH, null, attributeId,
				value));
	}

	public void changeNodeAttributeAt(long date, String nodeId,
			String attributeId, Object oldValue, Object newValue) {
		insert(new AttributeChanged(date, ElementType.NODE, nodeId,
				attributeId, oldValue, newValue));
	}

	public void changeEdgeAttributeAt(long date, String edgeId,
			String attributeId, Object oldValue, Object newValue) {
		insert(new AttributeChanged(date, ElementType.EDGE, edgeId,
				attributeId, oldValue, newValue));
	}

	public void changeGraphAttributeAt(long date, String attributeId,
			Object oldValue, Object newValue) {
		insert(new AttributeChanged(date, ElementType.GRAPH, null, attributeId,
				oldValue, newValue));
	}

	public void removeNodeAttributeAt(long date, String nodeId,
			String attributeId) {
		insert(new AttributeRemoved(date, ElementType.NODE, nodeId, attributeId));
	}

	public void removeEdgeAttributeAt(long date, String edgeId,
			String attributeId) {
		insert(new AttributeRemoved(date, ElementType.EDGE, edgeId, attributeId));
	}

	public void removeGraphAttributeAt(long date, String attributeId) {
		insert(new AttributeRemoved(date, ElementType.GRAPH, null, attributeId));
	}

	public void stepBeginsAt(long date, double step) {
		insert(new StepBegins(date, step));
	}

	public void clearGraphAt(long date) {
		insert(new GraphCleared(date));
	}

	protected void handleTimeAttribute(boolean delete, String key, Object value) {
		TimeAction action;

		action = TimeAction.valueOf(key.substring(TIME_PREFIX.length() + 1)
				.toUpperCase());

		switch (action) {
		case FORMAT:
		case SET:
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#edgeAttributeAdded(java.lang.String,
	 * long, java.lang.String, java.lang.String, java.lang.Object)
	 */
	public void edgeAttributeAdded(String sourceId, long timeId, String edgeId,
			String attribute, Object value) {
		addEdgeAttributeAt(currentDate, edgeId, attribute, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#edgeAttributeChanged(java.lang.String
	 * , long, java.lang.String, java.lang.String, java.lang.Object,
	 * java.lang.Object)
	 */
	public void edgeAttributeChanged(String sourceId, long timeId,
			String edgeId, String attribute, Object oldValue, Object newValue) {
		changeEdgeAttributeAt(currentDate, edgeId, attribute, oldValue,
				newValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#edgeAttributeRemoved(java.lang.String
	 * , long, java.lang.String, java.lang.String)
	 */
	public void edgeAttributeRemoved(String sourceId, long timeId,
			String edgeId, String attribute) {
		removeEdgeAttributeAt(currentDate, edgeId, attribute);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#graphAttributeAdded(java.lang.String
	 * , long, java.lang.String, java.lang.Object)
	 */
	public void graphAttributeAdded(String sourceId, long timeId,
			String attribute, Object value) {
		if (attribute.startsWith(TIME_PREFIX + "."))
			handleTimeAttribute(false, attribute, value);

		addGraphAttributeAt(currentDate, attribute, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#graphAttributeChanged(java.lang.
	 * String, long, java.lang.String, java.lang.Object, java.lang.Object)
	 */
	public void graphAttributeChanged(String sourceId, long timeId,
			String attribute, Object oldValue, Object newValue) {
		if (attribute.startsWith(TIME_PREFIX + "."))
			handleTimeAttribute(false, attribute, newValue);

		changeGraphAttributeAt(currentDate, attribute, oldValue, newValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#graphAttributeRemoved(java.lang.
	 * String, long, java.lang.String)
	 */
	public void graphAttributeRemoved(String sourceId, long timeId,
			String attribute) {
		if (attribute.startsWith(TIME_PREFIX + "."))
			handleTimeAttribute(true, attribute, null);

		removeGraphAttributeAt(currentDate, attribute);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#nodeAttributeAdded(java.lang.String,
	 * long, java.lang.String, java.lang.String, java.lang.Object)
	 */
	public void nodeAttributeAdded(String sourceId, long timeId, String nodeId,
			String attribute, Object value) {
		addNodeAttributeAt(currentDate, nodeId, attribute, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#nodeAttributeChanged(java.lang.String
	 * , long, java.lang.String, java.lang.String, java.lang.Object,
	 * java.lang.Object)
	 */
	public void nodeAttributeChanged(String sourceId, long timeId,
			String nodeId, String attribute, Object oldValue, Object newValue) {
		changeNodeAttributeAt(currentDate, nodeId, attribute, oldValue,
				newValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#nodeAttributeRemoved(java.lang.String
	 * , long, java.lang.String, java.lang.String)
	 */
	public void nodeAttributeRemoved(String sourceId, long timeId,
			String nodeId, String attribute) {
		removeNodeAttributeAt(currentDate, nodeId, attribute);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#edgeAdded(java.lang.String, long,
	 * java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
	public void edgeAdded(String sourceId, long timeId, String edgeId,
			String fromNodeId, String toNodeId, boolean directed) {
		addEdgeAt(currentDate, edgeId, fromNodeId, toNodeId, directed);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#edgeRemoved(java.lang.String,
	 * long, java.lang.String)
	 */
	public void edgeRemoved(String sourceId, long timeId, String edgeId) {
		removeEdgeAt(currentDate, edgeId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#graphCleared(java.lang.String,
	 * long)
	 */
	public void graphCleared(String sourceId, long timeId) {
		clearGraphAt(currentDate);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#nodeAdded(java.lang.String, long,
	 * java.lang.String)
	 */
	public void nodeAdded(String sourceId, long timeId, String nodeId) {
		addNodeAt(currentDate, nodeId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#nodeRemoved(java.lang.String,
	 * long, java.lang.String)
	 */
	public void nodeRemoved(String sourceId, long timeId, String nodeId) {
		removeNodeAt(currentDate, nodeId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#stepBegins(java.lang.String,
	 * long, double)
	 */
	public void stepBegins(String sourceId, long timeId, double step) {
		stepBeginsAt(currentDate, step);
	}

	protected static enum ElementType {
		NODE, EDGE, GRAPH
	}

	protected abstract class Event implements Comparable<Event> {
		long date;
		int priority;

		protected Event(long date, int priority) {
			this.date = date;
		}

		abstract void doEvent(long timeId);

		abstract void reverse(long timeId);

		public int compareTo(Event e) {
			if (date == e.date)
				return priority - e.priority;

			return (int) (date - e.date);
		}
	}

	protected class NodeAdded extends Event {
		String nodeId;

		public NodeAdded(long timeId, String nodeId) {
			super(timeId, 10);
			this.nodeId = nodeId;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.stream.Timeline.Event#doEvent(long)
		 */
		public void doEvent(long timeId) {
			sendNodeAdded(sourceId, timeId, nodeId);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.stream.Timeline.Event#reverse(long)
		 */
		public void reverse(long timeId) {
			sendNodeRemoved(sourceId, timeId, nodeId);
		}
	}

	protected class NodeRemoved extends Event {
		String nodeId;

		public NodeRemoved(long timeId, String nodeId) {
			super(timeId, 4);
			this.nodeId = nodeId;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.stream.Timeline.Event#doEvent(long)
		 */
		public void doEvent(long timeId) {
			sendNodeRemoved(sourceId, timeId, nodeId);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.stream.Timeline.Event#reverse(long)
		 */
		public void reverse(long timeId) {
			sendNodeAdded(sourceId, timeId, nodeId);
		}
	}

	protected class AttributeAdded extends Event {
		ElementType type;
		String elementId;
		String attrId;
		Object value;

		public AttributeAdded(long timeId, ElementType type, String elementId,
				String attrId, Object value) {
			super(timeId, 8);
			this.type = type;
			this.elementId = elementId;
			this.value = value;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.stream.Timeline.Event#doEvent(long)
		 */
		public void doEvent(long timeId) {
			switch (type) {
			case NODE:
				sendNodeAttributeAdded(sourceId, timeId, elementId, attrId,
						value);
				break;
			case EDGE:
				sendEdgeAttributeAdded(sourceId, timeId, elementId, attrId,
						value);
				break;
			case GRAPH:
				sendGraphAttributeAdded(sourceId, timeId, attrId, value);
				break;
			}
		}

		public void reverse(long timeId) {
			// TODO
		}
	}

	protected class AttributeChanged extends Event {
		ElementType type;
		String elementId;
		String attrId;
		Object newValue;
		Object oldValue;

		public AttributeChanged(long date, ElementType type, String elementId,
				String attrId, Object newValue, Object oldValue) {
			super(date, 7);

			this.type = type;
			this.elementId = elementId;
			this.attrId = attrId;
			this.newValue = newValue;
			this.oldValue = oldValue;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.stream.Timeline.Event#doEvent(long)
		 */
		public void doEvent(long timeId) {
			switch (type) {
			case NODE:
				sendNodeAttributeChanged(sourceId, timeId, elementId, attrId,
						oldValue, newValue);
			case EDGE:
				sendEdgeAttributeChanged(sourceId, timeId, elementId, attrId,
						oldValue, newValue);
				break;
			case GRAPH:
				sendGraphAttributeChanged(sourceId, timeId, attrId, oldValue,
						newValue);
				break;
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.stream.Timeline.Event#reverse(long)
		 */
		public void reverse(long timeId) {
			// TODO
		}
	}

	protected class AttributeRemoved extends Event {
		ElementType type;
		String elementId;
		String attrId;

		public AttributeRemoved(long date, ElementType type, String elementId,
				String attrId) {
			super(date, 6);

			this.type = type;
			this.elementId = elementId;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.stream.Timeline.Event#doEvent(long)
		 */
		public void doEvent(long timeId) {
			switch (type) {
			case NODE:
				sendNodeAttributeRemoved(sourceId, timeId, elementId, attrId);
				break;
			case EDGE:
				sendEdgeAttributeRemoved(sourceId, timeId, elementId, attrId);
				break;
			case GRAPH:
				sendGraphAttributeRemoved(sourceId, timeId, attrId);
				break;
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.stream.Timeline.Event#reverse(long)
		 */
		public void reverse(long timeId) {
			// TODO
		}
	}

	protected class EdgeAdded extends Event {
		String edgeId;
		String source, target;
		boolean directed;

		public EdgeAdded(long date, String edgeId, String source,
				String target, boolean directed) {
			super(date, 9);

			this.edgeId = edgeId;
			this.source = source;
			this.target = target;
			this.directed = directed;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.stream.Timeline.Event#doEvent(long)
		 */
		public void doEvent(long timeId) {
			sendEdgeAdded(sourceId, timeId, edgeId, source, target, directed);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.stream.Timeline.Event#reverse(long)
		 */
		public void reverse(long timeId) {
			sendEdgeRemoved(sourceId, timeId, edgeId);
		}
	}

	protected class EdgeRemoved extends Event {
		String edgeId;

		public EdgeRemoved(long date, String edgeId) {
			super(date, 5);
			this.edgeId = edgeId;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.stream.Timeline.Event#doEvent(long)
		 */
		public void doEvent(long timeId) {
			sendEdgeRemoved(sourceId, timeId, edgeId);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.stream.Timeline.Event#reverse(long)
		 */
		public void reverse(long timeId) {
			// TODO
		}
	}

	protected class StepBegins extends Event {
		double step;

		public StepBegins(long date, double step) {
			super(date, 0);
			this.step = step;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.stream.Timeline.Event#doEvent(long)
		 */
		public void doEvent(long timeId) {
			sendStepBegins(sourceId, timeId, step);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.stream.Timeline.Event#reverse(long)
		 */
		public void reverse(long timeId) {
			// TODO
		}
	}

	protected class GraphCleared extends Event {
		public GraphCleared(long date) {
			super(date, 0);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.stream.Timeline.Event#doEvent(long)
		 */
		public void doEvent(long timeId) {
			sendGraphCleared(sourceId, timeId);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.stream.Timeline.Event#reverse(long)
		 */
		public void reverse(long timeId) {
			// TODO
		}
	}

	protected static enum TimeAction {
		FORMAT, SET
	}
}
