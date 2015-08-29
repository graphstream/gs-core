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

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.EnumMap;
import java.util.HashMap;
import org.graphstream.graph.Element.ElementType;

/**
 * A sink easily allowing a bind between attribute modifications and method
 * calls.
 *
 * <pre>
 * public class MyObject extends AnnotatedSink {
 * 	String a1;
 * 	double a2;
 *
 * 	&#064;Bind(&quot;myobject.set.a1&quot;)
 * 	public void setA1(String eventId, Object value) {
 * 		a1 = (String) value;
 * 	}
 *
 * 	&#064;Bind(&quot;myobject.set.a2&quot;)
 * 	public void setA2(String eventId, Object value) {
 * 		a2 = (Double) value;
 * 	}
 *
 * 	public static void main(String ... args) {
 * 			Graph g = ...;
 * 			MyObject obj = new MyObject();
 *
 * 			g.addSink(obj);
 *
 * 			g.addAttribute("myobject.set.a1", "MyObject A1");
 * 			g.addAttribute("myobject.set.a2", 100.0);
 * 		}
 * }
 * </pre>
 */
public abstract class AnnotatedSink implements Sink {
	/**
	 * Annotation used to bind an event to a method. This bind is composed
	 * of a name (the attribute key) and an element type. For example, the
	 * annotation
	 *
	 * <pre>
	 * @Bind(value = &quot;test&quot;, type = ElementType.NODE)
	 * </pre>
	 *
	 * will be triggered the annotated method when receiving
	 * 'nodeAttributeXXX()' methods.
	 */
	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target(java.lang.annotation.ElementType.METHOD)
	public static @interface Bind {
		/**
		 * Name of the attribute key that triggered the annotated
		 * method.
		 *
		 * @return an attribute key
		 */
		String value();

		/**
		 * Type of element that triggered the annotated method. Default
		 * is GRAPH.
		 *
		 * @return type of element in GRAPH, NODE or EDGE
		 */
		ElementType type() default ElementType.GRAPH;
	}

	private final EnumMap<ElementType, MethodMap> methods;

	protected AnnotatedSink() {
		methods = new EnumMap<>(ElementType.class);
		methods.put(ElementType.GRAPH, new MethodMap());
		methods.put(ElementType.EDGE, new MethodMap());
		methods.put(ElementType.NODE, new MethodMap());

		Method[] ms = getClass().getMethods();

		for (int i = 0; i < ms.length; i++) {
			Method m = ms[i];
			Bind b = m.getAnnotation(Bind.class);

			if (b != null) {
				methods.get(b.type()).put(b.value(), m);
			}
		}
	}

	private void invoke(Method m, Object... args) {
		if (m == null) {
			return;
		}
		try {
			m.invoke(this, args);
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void edgeAttributeAdded(String sourceId, long timeId, String edgeId,
		String attribute, Object value) {
		Method m = methods.get(ElementType.EDGE).get(attribute);

		invoke(m, edgeId, attribute, value);
	}

	@Override
	public void edgeAttributeChanged(String sourceId, long timeId,
		String edgeId, String attribute, Object oldValue, Object newValue) {
		Method m = methods.get(ElementType.EDGE).get(attribute);

		invoke(m, edgeId, attribute, newValue);
	}

	@Override
	public void edgeAttributeRemoved(String sourceId, long timeId,
		String edgeId, String attribute) {
		Method m = methods.get(ElementType.EDGE).get(attribute);

		invoke(m, edgeId, attribute, null);
	}

	@Override
	public void graphAttributeAdded(String sourceId, long timeId,
		String attribute, Object value) {
		Method m = methods.get(ElementType.GRAPH).get(attribute);

		invoke(m, attribute, value);
	}

	@Override
	public void graphAttributeChanged(String sourceId, long timeId,
		String attribute, Object oldValue, Object newValue) {
		Method m = methods.get(ElementType.GRAPH).get(attribute);

		invoke(m, attribute, newValue);
	}

	@Override
	public void graphAttributeRemoved(String sourceId, long timeId,
		String attribute) {
		Method m = methods.get(ElementType.GRAPH).get(attribute);

		invoke(m, attribute, null);
	}

	@Override
	public void nodeAttributeAdded(String sourceId, long timeId, String nodeId,
		String attribute, Object value) {
		Method m = methods.get(ElementType.NODE).get(attribute);

		invoke(m, nodeId, attribute, value);
	}

	@Override
	public void nodeAttributeChanged(String sourceId, long timeId,
		String nodeId, String attribute, Object oldValue, Object newValue) {
		Method m = methods.get(ElementType.NODE).get(attribute);

		invoke(m, nodeId, attribute, newValue);
	}

	@Override
	public void nodeAttributeRemoved(String sourceId, long timeId,
		String nodeId, String attribute) {
		Method m = methods.get(ElementType.NODE).get(attribute);

		invoke(m, nodeId, attribute, null);
	}

	private static class MethodMap extends HashMap<String, Method> {
		private static final long serialVersionUID = 1664854698109523697L;
	}
}
