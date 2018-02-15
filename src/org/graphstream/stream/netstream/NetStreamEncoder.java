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
 * @since 2016-02-01
 * 
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.stream.netstream;

import org.graphstream.stream.binary.ByteEncoder;

import static org.graphstream.stream.netstream.NetStreamUtils.*;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @since 22/01/16.
 */
public class NetStreamEncoder implements ByteEncoder {
	private static final Logger LOGGER = Logger.getLogger(NetStreamEncoder.class.getName());

	protected final List<Transport> transportList;
	protected String sourceId;
	protected ByteBuffer sourceIdBuff;
	protected ByteBuffer streamBuffer;

	public NetStreamEncoder(Transport... transports) {
		this("default", transports);
	}

	public NetStreamEncoder(String stream, Transport... transports) {
		streamBuffer = encodeString(stream);
		transportList = new LinkedList<>();

		if (transports != null) {
			for (Transport transport : transports)
				transportList.add(transport);
		}
	}

	@Override
	public void addTransport(Transport transport) {
		transportList.add(transport);
	}

	@Override
	public void removeTransport(Transport transport) {
		transportList.remove(transport);
	}

	protected ByteBuffer getEncodedValue(Object in, int valueType) {
		ByteBuffer value = encodeValue(in, valueType);

		if (value == null) {
			LOGGER.warning(String.format("unknown value type %d\n", valueType));
		}

		return value;
	}

	protected void doSend(ByteBuffer event) {
		for (Transport transport : transportList) {
			event.rewind();
			transport.send(event);
		}
	}

	protected ByteBuffer getAndPrepareBuffer(String sourceId, long timeId, int eventType, int messageSize) {
		if (!sourceId.equals(this.sourceId)) {
			this.sourceId = sourceId;
			sourceIdBuff = encodeString(sourceId);
		}

		streamBuffer.rewind();
		sourceIdBuff.rewind();

		int size = 4 + +streamBuffer.capacity() // stream
				+ 1 // CMD
				+ sourceIdBuff.capacity() // source id
				+ getVarintSize(timeId) // timeId
				+ messageSize;

		ByteBuffer bb = ByteBuffer.allocate(size);
		bb.putInt(size).put(streamBuffer).put((byte) eventType).put(sourceIdBuff).put(encodeUnsignedVarint(timeId));

		return bb;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.graphstream.stream.AttributeSink#graphAttributeAdded(java.lang.String ,
	 * long, java.lang.String, java.lang.Object)
	 */
	public void graphAttributeAdded(String sourceId, long timeId, String attribute, Object value) {
		ByteBuffer attrBuff = encodeString(attribute);
		int valueType = getType(value);
		ByteBuffer valueBuff = getEncodedValue(value, valueType);

		int innerSize = attrBuff.capacity() // attribute id
				+ 1 // attr type
				+ valueBuff.capacity();

		ByteBuffer buff = getAndPrepareBuffer(sourceId, timeId, NetStreamConstants.EVENT_ADD_GRAPH_ATTR, innerSize);

		buff.put(attrBuff).put((byte) valueType).put(valueBuff);

		doSend(buff);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.stream.AttributeSink#graphAttributeChanged(java.lang.
	 * String, long, java.lang.String, java.lang.Object, java.lang.Object)
	 */
	public void graphAttributeChanged(String sourceId, long timeId, String attribute, Object oldValue,
			Object newValue) {
		ByteBuffer attrBuff = encodeString(attribute);
		int oldValueType = getType(oldValue);
		int newValueType = getType(newValue);

		ByteBuffer oldValueBuff = getEncodedValue(oldValue, oldValueType);
		ByteBuffer newValueBuff = getEncodedValue(newValue, newValueType);

		int innerSize = attrBuff.capacity() + // attribute id
				1 + // attr type
				oldValueBuff.capacity() + // attr value
				1 + // attr type
				newValueBuff.capacity(); // attr value

		ByteBuffer buff = getAndPrepareBuffer(sourceId, timeId, NetStreamConstants.EVENT_CHG_GRAPH_ATTR, innerSize);

		buff.put(attrBuff).put((byte) oldValueType).put(oldValueBuff).put((byte) newValueType).put(newValueBuff);

		doSend(buff);

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.stream.AttributeSink#graphAttributeRemoved(java.lang.
	 * String, long, java.lang.String)
	 */
	public void graphAttributeRemoved(String sourceId, long timeId, String attribute) {
		ByteBuffer attrBuff = encodeString(attribute);

		int innerSize = attrBuff.capacity();

		ByteBuffer buff = getAndPrepareBuffer(sourceId, timeId, NetStreamConstants.EVENT_DEL_GRAPH_ATTR, innerSize);
		buff.put(attrBuff);

		doSend(buff);

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.graphstream.stream.AttributeSink#nodeAttributeAdded(java.lang.String,
	 * long, java.lang.String, java.lang.String, java.lang.Object)
	 */
	public void nodeAttributeAdded(String sourceId, long timeId, String nodeId, String attribute, Object value) {
		ByteBuffer nodeBuff = encodeString(nodeId);
		ByteBuffer attrBuff = encodeString(attribute);
		int valueType = getType(value);
		ByteBuffer valueBuff = getEncodedValue(value, valueType);

		int innerSize = nodeBuff.capacity() + // nodeId
				attrBuff.capacity() + // attribute
				1 + // value type
				valueBuff.capacity();

		ByteBuffer buff = getAndPrepareBuffer(sourceId, timeId, NetStreamConstants.EVENT_ADD_NODE_ATTR, innerSize);

		buff.put(nodeBuff).put(attrBuff).put((byte) valueType).put(valueBuff);

		doSend(buff);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.graphstream.stream.AttributeSink#nodeAttributeChanged(java.lang.String ,
	 * long, java.lang.String, java.lang.String, java.lang.Object, java.lang.Object)
	 */
	public void nodeAttributeChanged(String sourceId, long timeId, String nodeId, String attribute, Object oldValue,
			Object newValue) {
		ByteBuffer nodeBuff = encodeString(nodeId);
		ByteBuffer attrBuff = encodeString(attribute);

		int oldValueType = getType(oldValue);
		int newValueType = getType(newValue);

		ByteBuffer oldValueBuff = getEncodedValue(oldValue, oldValueType);
		ByteBuffer newValueBuff = getEncodedValue(newValue, newValueType);

		int innerSize = nodeBuff.capacity() + // nodeId
				attrBuff.capacity() + // attribute
				1 + // value type
				oldValueBuff.capacity() + // value
				1 + // value type
				newValueBuff.capacity();

		ByteBuffer buff = getAndPrepareBuffer(sourceId, timeId, NetStreamConstants.EVENT_CHG_NODE_ATTR, innerSize);

		buff.put(nodeBuff).put(attrBuff).put((byte) oldValueType).put(oldValueBuff).put((byte) newValueType)
				.put(newValueBuff);

		doSend(buff);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.graphstream.stream.AttributeSink#nodeAttributeRemoved(java.lang.String ,
	 * long, java.lang.String, java.lang.String)
	 */
	public void nodeAttributeRemoved(String sourceId, long timeId, String nodeId, String attribute) {
		ByteBuffer nodeBuff = encodeString(nodeId);
		ByteBuffer attrBuff = encodeString(attribute);

		int innerSize = nodeBuff.capacity() + // nodeId
				attrBuff.capacity(); // attribute

		ByteBuffer buff = getAndPrepareBuffer(sourceId, timeId, NetStreamConstants.EVENT_DEL_NODE_ATTR, innerSize);

		buff.put(nodeBuff).put(attrBuff);

		doSend(buff);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.graphstream.stream.AttributeSink#edgeAttributeAdded(java.lang.String,
	 * long, java.lang.String, java.lang.String, java.lang.Object)
	 */
	public void edgeAttributeAdded(String sourceId, long timeId, String edgeId, String attribute, Object value) {
		ByteBuffer edgeBuff = encodeString(edgeId);
		ByteBuffer attrBuff = encodeString(attribute);

		int valueType = getType(value);

		ByteBuffer valueBuff = getEncodedValue(value, valueType);

		int innerSize = edgeBuff.capacity() + // nodeId
				attrBuff.capacity() + // attribute
				1 + // value type
				valueBuff.capacity(); // value

		ByteBuffer buff = getAndPrepareBuffer(sourceId, timeId, NetStreamConstants.EVENT_ADD_EDGE_ATTR, innerSize);

		buff.put(edgeBuff).put(attrBuff).put((byte) valueType) // value type
				.put(valueBuff);

		doSend(buff);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.graphstream.stream.AttributeSink#edgeAttributeChanged(java.lang.String ,
	 * long, java.lang.String, java.lang.String, java.lang.Object, java.lang.Object)
	 */
	public void edgeAttributeChanged(String sourceId, long timeId, String edgeId, String attribute, Object oldValue,
			Object newValue) {
		ByteBuffer edgeBuff = encodeString(edgeId);
		ByteBuffer attrBuff = encodeString(attribute);
		int oldValueType = getType(oldValue);
		int newValueType = getType(newValue);

		ByteBuffer oldValueBuff = getEncodedValue(oldValue, oldValueType);
		ByteBuffer newValueBuff = getEncodedValue(newValue, newValueType);

		int innerSize = edgeBuff.capacity() + // nodeId
				attrBuff.capacity() + // attribute
				1 + // value type
				oldValueBuff.capacity() + // value
				1 + // value type
				newValueBuff.capacity(); // value

		ByteBuffer buff = getAndPrepareBuffer(sourceId, timeId, NetStreamConstants.EVENT_CHG_EDGE_ATTR, innerSize);

		buff.put(edgeBuff).put(attrBuff).put((byte) oldValueType).put(oldValueBuff).put((byte) newValueType)
				.put(newValueBuff);

		doSend(buff);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.graphstream.stream.AttributeSink#edgeAttributeRemoved(java.lang.String ,
	 * long, java.lang.String, java.lang.String)
	 */
	public void edgeAttributeRemoved(String sourceId, long timeId, String edgeId, String attribute) {
		ByteBuffer edgeBuff = encodeString(edgeId);
		ByteBuffer attrBuff = encodeString(attribute);

		int innerSize = edgeBuff.capacity() + // nodeId
				attrBuff.capacity(); // attribute

		ByteBuffer buff = getAndPrepareBuffer(sourceId, timeId, NetStreamConstants.EVENT_DEL_EDGE_ATTR, innerSize);

		buff.put(edgeBuff).put(attrBuff);

		doSend(buff);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.stream.ElementSink#nodeAdded(java.lang.String, long,
	 * java.lang.String)
	 */
	public void nodeAdded(String sourceId, long timeId, String nodeId) {
		ByteBuffer nodeBuff = encodeString(nodeId);

		int innerSize = nodeBuff.capacity();

		ByteBuffer buff = getAndPrepareBuffer(sourceId, timeId, NetStreamConstants.EVENT_ADD_NODE, innerSize);
		buff.put(nodeBuff);

		doSend(buff);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.stream.ElementSink#nodeRemoved(java.lang.String, long,
	 * java.lang.String)
	 */
	public void nodeRemoved(String sourceId, long timeId, String nodeId) {
		ByteBuffer nodeBuff = encodeString(nodeId);

		int innerSize = nodeBuff.capacity();

		ByteBuffer buff = getAndPrepareBuffer(sourceId, timeId, NetStreamConstants.EVENT_DEL_NODE, innerSize);
		buff.put(nodeBuff);

		doSend(buff);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.stream.ElementSink#edgeAdded(java.lang.String, long,
	 * java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
	public void edgeAdded(String sourceId, long timeId, String edgeId, String fromNodeId, String toNodeId,
			boolean directed) {
		ByteBuffer edgeBuff = encodeString(edgeId);
		ByteBuffer fromNodeBuff = encodeString(fromNodeId);
		ByteBuffer toNodeBuff = encodeString(toNodeId);

		int innerSize = edgeBuff.capacity() + // edge
				fromNodeBuff.capacity() + // from nodeId
				toNodeBuff.capacity() + // to nodeId
				1; // direction

		ByteBuffer buff = getAndPrepareBuffer(sourceId, timeId, NetStreamConstants.EVENT_ADD_EDGE, innerSize);

		buff.put(edgeBuff).put(fromNodeBuff).put(toNodeBuff).put((byte) (!directed ? 0 : 1));

		doSend(buff);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.stream.ElementSink#edgeRemoved(java.lang.String, long,
	 * java.lang.String)
	 */
	public void edgeRemoved(String sourceId, long timeId, String edgeId) {
		ByteBuffer edgeBuff = encodeString(edgeId);

		int innerSize = edgeBuff.capacity();

		ByteBuffer buff = getAndPrepareBuffer(sourceId, timeId, NetStreamConstants.EVENT_DEL_EDGE, innerSize);
		buff.put(edgeBuff);

		doSend(buff);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.stream.ElementSink#graphCleared(java.lang.String, long)
	 */
	public void graphCleared(String sourceId, long timeId) {
		ByteBuffer buff = getAndPrepareBuffer(sourceId, timeId, NetStreamConstants.EVENT_CLEARED, 0);
		doSend(buff);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.stream.ElementSink#stepBegins(java.lang.String, long,
	 * double)
	 */
	public void stepBegins(String sourceId, long timeId, double step) {
		ByteBuffer buff = getAndPrepareBuffer(sourceId, timeId, NetStreamConstants.EVENT_STEP, 8);
		buff.putDouble(step);

		doSend(buff);
	}
}
