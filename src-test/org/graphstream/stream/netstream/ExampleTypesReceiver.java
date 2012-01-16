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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Arrays;

import org.graphstream.stream.SinkAdapter;
import org.graphstream.stream.thread.ThreadProxyPipe;

/**
 * 
 * ExampleTypesReceiver.java
 * @since Aug 21, 2011
 *
 * @author Yoann Pigné
 */
public class ExampleTypesReceiver {

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

		pipe.addSink(new SinkAdapter() {
			public void graphAttributeAdded(String sourceId, long timeId,
					String attribute, Object value) {
				validate(attribute, value);
			}
			public void graphAttributeChanged(String sourceId, long timeId,
					String attribute, Object oldValue, Object newValue) {
				validate(attribute, newValue);
			}
			private void validate(String attribute, Object value) {
				String valueType = null;
				Class<?> valueClass = value.getClass();
				boolean isArray = valueClass.isArray();
				if (isArray) {
					valueClass = ((Object[]) value)[0].getClass();
				}
				if (valueClass.equals(Boolean.class)) {
					if (isArray) {
						valueType = "booleanArray";
						System.out.printf("found a %s for attribute %s=%s%n",
								valueType, attribute,
								Arrays.toString((Boolean[]) value));

					} else {
						valueType = "boolean";
						System.out.printf("found a %s for attribute %s=%s%n",
								valueType, attribute, value.toString());
					}
				} else if (valueClass.equals(Byte.class)) {
					if (isArray) {
						valueType = "byteArray";
						System.out.printf("found a %s for attribute %s=%s%n",
								valueType, attribute,
								Arrays.toString((Byte[]) value));
					} else {
						valueType = "byte";
						System.out.printf("found a %s for attribute %s=%s%n",
								valueType, attribute, value.toString());
					}
				} else if (valueClass.equals(Short.class)) {
					if (isArray) {
						valueType = "shortArray";
						System.out.printf("found a %s for attribute %s=%s%n",
								valueType, attribute,
								Arrays.toString((Short[]) value));
					} else {
						valueType = "short";
						System.out.printf("found a %s for attribute %s=%s%n",
								valueType, attribute, value.toString());
					}
				} else if (valueClass.equals(Integer.class)) {
					if (isArray) {
						valueType = "intArray";
						System.out.printf("found a %s for attribute %s=%s%n",
								valueType, attribute,
								Arrays.toString((Integer[]) value));
					} else {
						valueType = "int";
						System.out.printf("found a %s for attribute %s=%s%n",
								valueType, attribute, value.toString());
					}
				} else if (valueClass.equals(Long.class)) {
					if (isArray) {
						valueType = "longArray";
						System.out.printf("found a %s for attribute %s=%s%n",
								valueType, attribute,
								Arrays.toString((Long[]) value));
					} else {
						valueType = "long";
						System.out.printf("found a %s for attribute %s=%s%n",
								valueType, attribute, value.toString());
					}
				} else if (valueClass.equals(Float.class)) {
					if (isArray) {
						valueType = "floatArray";
						System.out.printf("found a %s for attribute %s=%s%n",
								valueType, attribute,
								Arrays.toString((Float[]) value));
					} else {
						valueType = "float";
						System.out.printf("found a %s for attribute %s=%s%n",
								valueType, attribute, value.toString());
					}
				} else if (valueClass.equals(Double.class)) {
					if (isArray) {
						valueType = "doubleArray";
						System.out.printf("found a %s for attribute %s=%s%n",
								valueType, attribute,
								Arrays.toString((Double[]) value));
					} else {
						valueType = "double";
						System.out.printf("found a %s for attribute %s=%s%n",
								valueType, attribute, value.toString());
					}
				} else if (valueClass.equals(String.class)) {
					if (isArray) {
						valueType = "typeArray";
						System.out.printf("found a %s for attribute %s=%s%n",
								valueType, attribute,
								Arrays.toString((Boolean[]) value));
					} else {
						valueType = "string";
						System.out.printf("found a %s for attribute %s=%s%n",
								valueType, attribute, value.toString());
					}
				}

				assertTrue(valueType.equals(attribute));

			}

		});
		
		
		
		while(true){
			pipe.pump();
			Thread.sleep(100);			
		}
	}

}
