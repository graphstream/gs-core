/*
 * Copyright 2006 - 2012
 *      Stefan Balev       <stefan.balev@graphstream-project.org>
 *      Julien Baudry	<julien.baudry@graphstream-project.org>
 *      Antoine Dutot	<antoine.dutot@graphstream-project.org>
 *      Yoann Pigné	<yoann.pigne@graphstream-project.org>
 *      Guilhelm Savin	<guilhelm.savin@graphstream-project.org>
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
package org.graphstream.stream.netstream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.UnknownHostException;

import org.graphstream.stream.Sink;
import org.graphstream.stream.thread.ThreadProxyPipe;

/**
 * 
 * 
 * @file ExampleEventsReceiver.java
 * @date Aug 21, 2011
 *
 * @author Yoann Pigné
 *
 */
public class ExampleEventsReceiver {

	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		NetStreamReceiver net = null;
		try {
			net = new NetStreamReceiver("localhost", 2001, true);
		} catch (UnknownHostException e1) {
			fail(e1.toString());
		} catch (IOException e1) {
			fail(e1.toString());
		}

		ThreadProxyPipe pipe = net.getDefaultStream();

		pipe.addSink(new Sink() {
			public void graphAttributeAdded(String sourceId, long timeId,
					String attribute, Object value) {
				assertEquals(0, value);
				assertEquals("graphAttribute", attribute);
			}
			
			public void graphAttributeChanged(String sourceId, long timeId,
					String attribute, Object oldValue, Object newValue) {
				assertTrue((Integer) newValue == 0 || (Integer) newValue == 1);
				assertEquals("graphAttribute", attribute);
			}
			
			public void graphAttributeRemoved(String sourceId, long timeId,
					String attribute) {
				assertEquals("graphAttribute", attribute);
			}
			
			public void nodeAttributeAdded(String sourceId, long timeId,
					String nodeId, String attribute, Object value) {
				assertEquals(0, value);
				assertEquals("nodeAttribute", attribute);
			}
			
			public void nodeAttributeChanged(String sourceId, long timeId,
					String nodeId, String attribute, Object oldValue,
					Object newValue) {
				assertTrue((Integer) newValue == 0 || (Integer) newValue == 1);
				assertEquals("nodeAttribute", attribute);
			}
			
			public void nodeAttributeRemoved(String sourceId, long timeId,
					String nodeId, String attribute) {
				assertEquals("nodeAttribute", attribute);
			}
			
			public void edgeAttributeAdded(String sourceId, long timeId,
					String edgeId, String attribute, Object value) {
				assertEquals(0, value);
				assertEquals("edgeAttribute", attribute);
			}
			
			public void edgeAttributeChanged(String sourceId, long timeId,
					String edgeId, String attribute, Object oldValue,
					Object newValue) {
				assertTrue((Integer) newValue == 0 || (Integer) newValue == 1);
				assertEquals("edgeAttribute", attribute);
			}
			
			public void edgeAttributeRemoved(String sourceId, long timeId,
					String edgeId, String attribute) {
				assertEquals("edgeAttribute", attribute);
			}
			
			public void nodeAdded(String sourceId, long timeId, String nodeId) {
				assertTrue("node0".equals(nodeId) || "node1".equals(nodeId));
			}
			
			public void nodeRemoved(String sourceId, long timeId, String nodeId) {
				assertTrue("node0".equals(nodeId) || "node1".equals(nodeId));
			}
			
			public void edgeAdded(String sourceId, long timeId, String edgeId,
					String fromNodeId, String toNodeId, boolean directed) {
				assertEquals("edge", edgeId);
				assertEquals("node0", fromNodeId);
				assertEquals("node1", toNodeId);
				assertEquals(true, directed);
			}
			
			public void edgeRemoved(String sourceId, long timeId, String edgeId) {
				assertEquals("edge", edgeId);
			}
			
			public void graphCleared(String sourceId, long timeId) {
				
			}
			
			public void stepBegins(String sourceId, long timeId, double step) {
				assertEquals(1.1, step);
			}
		});

		


		while(true){
			pipe.pump();
			Thread.sleep(100);			
		}

	}

}






