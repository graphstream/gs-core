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
 * @since 2012-01-14
 * 
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.stream.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.LinkedList;
import java.util.Random;

import org.graphstream.stream.Sink;
import org.graphstream.stream.SourceBase;
import org.junit.Test;

public class TestSourceBase {

	protected static String getRandomString(int size) {
		final String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_-%=+/:";
		StringBuilder sb = new StringBuilder();
		Random r = new Random();

		while (size-- > 0)
			sb.append(chars.charAt(r.nextInt(chars.length())));

		return sb.toString();
	}

	protected LinkedList<Event> generateEventList(int size) {
		Random r = new Random();
		LinkedList<Event> events = new LinkedList<Event>();
		EventType[] types = EventType.values();

		while (size-- > 0) {
			String sourceId = getRandomString(10);
			long timeId = r.nextLong();
			EventType type = types[r.nextInt(types.length)];
			Object[] args = null;

			switch (type) {
			case AN:
				args = new Object[] { getRandomString(10) };
				break;
			case DN:
				args = new Object[] { getRandomString(10) };
				break;
			case CNA:
				args = new Object[] { getRandomString(10), getRandomString(10), getRandomString(10) };
				break;
			case CNC:
				args = new Object[] { getRandomString(10), getRandomString(10), getRandomString(10),
						getRandomString(10) };
				break;
			case CNR:
				args = new Object[] { getRandomString(10), getRandomString(10) };
				break;
			case AE:
				args = new Object[] { getRandomString(10), getRandomString(10), getRandomString(10), r.nextBoolean() };
				break;
			case DE:
				args = new Object[] { getRandomString(10) };
				break;
			case CEA:
				args = new Object[] { getRandomString(10), getRandomString(10), getRandomString(10) };
				break;
			case CEC:
				args = new Object[] { getRandomString(10), getRandomString(10), getRandomString(10),
						getRandomString(10) };
				break;
			case CER:
				args = new Object[] { getRandomString(10), getRandomString(10) };
				break;
			case CGA:
				args = new Object[] { getRandomString(10), getRandomString(10) };
				break;
			case CGC:
				args = new Object[] { getRandomString(10), getRandomString(10), getRandomString(10) };
				break;
			case CGR:
				args = new Object[] { getRandomString(10) };
				break;
			case ST:
				args = new Object[] { r.nextDouble() };
				break;
			default:
				break;
			}

			if (args == null)
				events.add(new Event(type, sourceId, timeId));
			else
				events.add(new Event(type, sourceId, timeId, args));
		}

		return events;
	}

	@Test
	public void testSentEvents() {
		LinkedList<Event> events = generateEventList(10000);
		SourceBase source = new TestSource();
		EventStack stack = new EventStack();

		source.addSink(stack);

		for (Event e : events)
			send(source, e);

		assertEquals(events.size(), stack.size());

		for (int i = 0; i < events.size(); i++)
			events.get(i).assertEventEquals(stack.get(i));
	}

	protected void send(SourceBase base, Event e) {
		switch (e.type) {
		case AN:
			base.sendNodeAdded(e.sourceId, e.timeId, (String) e.args[0]);
			break;
		case DN:
			base.sendNodeRemoved(e.sourceId, e.timeId, (String) e.args[0]);
			break;
		case CNA:
			base.sendNodeAttributeAdded(e.sourceId, e.timeId, (String) e.args[0], (String) e.args[1], e.args[2]);
			break;
		case CNC:
			base.sendNodeAttributeChanged(e.sourceId, e.timeId, (String) e.args[0], (String) e.args[1], e.args[2],
					e.args[3]);
			break;
		case CNR:
			base.sendNodeAttributeRemoved(e.sourceId, e.timeId, (String) e.args[0], (String) e.args[1]);
			break;
		case AE:
			base.sendEdgeAdded(e.sourceId, e.timeId, (String) e.args[0], (String) e.args[1], (String) e.args[2],
					(Boolean) e.args[3]);
			break;
		case DE:
			base.sendEdgeRemoved(e.sourceId, e.timeId, (String) e.args[0]);
			break;
		case CEA:
			base.sendEdgeAttributeAdded(e.sourceId, e.timeId, (String) e.args[0], (String) e.args[1], e.args[2]);
			break;
		case CEC:
			base.sendEdgeAttributeChanged(e.sourceId, e.timeId, (String) e.args[0], (String) e.args[1], e.args[2],
					e.args[3]);
			break;
		case CER:
			base.sendEdgeAttributeRemoved(e.sourceId, e.timeId, (String) e.args[0], (String) e.args[1]);
			break;
		case CGA:
			base.sendGraphAttributeAdded(e.sourceId, e.timeId, (String) e.args[0], e.args[1]);
			break;
		case CGC:
			base.sendGraphAttributeChanged(e.sourceId, e.timeId, (String) e.args[0], e.args[1], e.args[2]);
			break;
		case CGR:
			base.sendGraphAttributeRemoved(e.sourceId, e.timeId, (String) e.args[0]);
			break;
		case ST:
			base.sendStepBegins(e.sourceId, e.timeId, (Double) e.args[0]);
			break;
		case CL:
			base.sendGraphCleared(e.sourceId, e.timeId);
			break;
		}
	}

	static enum EventType {
		AN, DN, CNA, CNC, CNR, AE, DE, CEA, CEC, CER, CGA, CGC, CGR, ST, CL
	}

	static class Event {
		EventType type;
		String sourceId;
		long timeId;
		Object[] args;

		Event(EventType type, String sourceId, long timeId, Object... args) {
			this.type = type;
			this.sourceId = sourceId;
			this.timeId = timeId;
			this.args = args;
		}

		public void assertEventEquals(Event e) {
			assertEquals(e.type, type);
			assertEquals(sourceId, e.sourceId);
			assertEquals(timeId, e.timeId);

			assertFalse((args == null) ^ (e.args == null));

			if (args != null)
				assertArrayEquals(args, e.args);
		}
	}

	static class EventStack extends LinkedList<Event> implements Sink {
		private static final long serialVersionUID = -4953996922753724259L;

		public void edgeAttributeAdded(String sourceId, long timeId, String edgeId, String attribute, Object value) {
			Event e = new Event(EventType.CEA, sourceId, timeId, edgeId, attribute, value);
			addLast(e);
		}

		public void edgeAttributeChanged(String sourceId, long timeId, String edgeId, String attribute, Object oldValue,
				Object newValue) {
			Event e = new Event(EventType.CEC, sourceId, timeId, edgeId, attribute, oldValue, newValue);
			addLast(e);
		}

		public void edgeAttributeRemoved(String sourceId, long timeId, String edgeId, String attribute) {
			Event e = new Event(EventType.CER, sourceId, timeId, edgeId, attribute);
			addLast(e);
		}

		public void graphAttributeAdded(String sourceId, long timeId, String attribute, Object value) {
			Event e = new Event(EventType.CGA, sourceId, timeId, attribute, value);
			addLast(e);
		}

		public void graphAttributeChanged(String sourceId, long timeId, String attribute, Object oldValue,
				Object newValue) {
			Event e = new Event(EventType.CGC, sourceId, timeId, attribute, oldValue, newValue);
			addLast(e);
		}

		public void graphAttributeRemoved(String sourceId, long timeId, String attribute) {
			Event e = new Event(EventType.CGR, sourceId, timeId, attribute);
			addLast(e);
		}

		public void nodeAttributeAdded(String sourceId, long timeId, String nodeId, String attribute, Object value) {
			Event e = new Event(EventType.CNA, sourceId, timeId, nodeId, attribute, value);
			addLast(e);
		}

		public void nodeAttributeChanged(String sourceId, long timeId, String nodeId, String attribute, Object oldValue,
				Object newValue) {
			Event e = new Event(EventType.CNC, sourceId, timeId, nodeId, attribute, oldValue, newValue);
			addLast(e);
		}

		public void nodeAttributeRemoved(String sourceId, long timeId, String nodeId, String attribute) {
			Event e = new Event(EventType.CNR, sourceId, timeId, nodeId, attribute);
			addLast(e);
		}

		public void edgeAdded(String sourceId, long timeId, String edgeId, String fromNodeId, String toNodeId,
				boolean directed) {
			Event e = new Event(EventType.AE, sourceId, timeId, edgeId, fromNodeId, toNodeId, directed);
			addLast(e);
		}

		public void edgeRemoved(String sourceId, long timeId, String edgeId) {
			Event e = new Event(EventType.DE, sourceId, timeId, edgeId);
			addLast(e);
		}

		public void graphCleared(String sourceId, long timeId) {
			Event e = new Event(EventType.CL, sourceId, timeId);
			addLast(e);
		}

		public void nodeAdded(String sourceId, long timeId, String nodeId) {
			Event e = new Event(EventType.AN, sourceId, timeId, nodeId);
			addLast(e);
		}

		public void nodeRemoved(String sourceId, long timeId, String nodeId) {
			Event e = new Event(EventType.DN, sourceId, timeId, nodeId);
			addLast(e);
		}

		public void stepBegins(String sourceId, long timeId, double step) {
			Event e = new Event(EventType.ST, sourceId, timeId, step);
			addLast(e);
		}
	}

	static class TestSource extends SourceBase {

	}
}
