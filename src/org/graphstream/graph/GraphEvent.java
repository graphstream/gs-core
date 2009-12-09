/*
 * This file is part of GraphStream.
 * 
 * GraphStream is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * GraphStream is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with GraphStream.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Copyright 2006 - 2009
 * 	Julien Baudry
 * 	Antoine Dutot
 * 	Yoann Pigné
 * 	Guilhelm Savin
 */
package org.graphstream.graph;

import java.io.Serializable;

public class GraphEvent
	implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1797718669238595871L;
	
	public static enum GraphAction
	{
		elementAdded,
		elementRemoved,
		attributeAdded,
		attributeChanged,
		attributeRemoved,
		graphCleared,
		stepBegins,
		none
	}
	
	public static enum ElementType
	{
		node,
		edge,
		graph
	}

	public static GraphEvent createGraphClearedEvent(String sourceId,
			long eventId)
	{
		return new GraphEvent(sourceId, eventId, GraphAction.graphCleared);
	}
	
	public static GraphStepEvent createGraphStepEvent(String sourceId,
			long eventId, double time)
	{
		return new GraphStepEvent(sourceId,eventId,time);
	}

	public static NodeEvent createNodeAddedEvent(String sourceId,
			long eventId, String nodeId)
	{
		return new NodeEvent(sourceId, eventId, GraphAction.elementAdded,
				nodeId);
	}

	public static NodeEvent createNodeRemovedEvent(String sourceId,
			long eventId, String nodeId)
	{
		return new NodeEvent(sourceId, eventId, GraphAction.elementRemoved,
				nodeId);
	}

	public static EdgeEvent createEdgeAddedEvent(String sourceId,
			long eventId, String edgeId, String from, String to,
			boolean directed)
	{
		return new EdgeEvent(sourceId, eventId, GraphAction.elementAdded,
				edgeId, from, to, directed);
	}

	public static EdgeEvent createEdgeRemovedEvent(String sourceId,
			long eventId, String edgeId)
	{
		return new EdgeEvent(sourceId, eventId, GraphAction.elementRemoved,
				edgeId);
	}
	
	public static AttributeEvent createGraphAttributeAddedEvent(String sourceId,
			long eventId, String attributeKey, Object newValue )
	{
		return createAttributeAddedEvent(sourceId, eventId, ElementType.graph,
				sourceId, attributeKey, newValue);
	}
	
	public static AttributeEvent createNodeAttributeAddedEvent(String sourceId,
			long eventId, String nodeId, String attributeKey, Object newValue )
	{
		return createAttributeAddedEvent(sourceId, eventId, ElementType.node,
				nodeId, attributeKey, newValue);
	}
	
	public static AttributeEvent createEdgeAttributeAddedEvent(String sourceId,
			long eventId, String edgeId, String attributeKey, Object newValue )
	{
		return createAttributeAddedEvent(sourceId, eventId, ElementType.edge,
				edgeId, attributeKey, newValue);
	}
	
	public static AttributeEvent createAttributeAddedEvent(String sourceId,
			long eventId, ElementType elementType, String elementId, String attributeKey,
			Object newValue )
	{
		return new AttributeEvent(sourceId, eventId,
				GraphAction.attributeAdded, elementType, elementId,
				attributeKey, null, newValue);
	}
	
	public static AttributeEvent createGraphAttributeChangedEvent(String sourceId,
			long eventId, String attributeKey, Object oldValue, Object newValue )
	{
		return createAttributeChangedEvent(sourceId, eventId, ElementType.graph,
				sourceId, attributeKey, oldValue, newValue);
	}
	
	public static AttributeEvent createNodeAttributeChangedEvent(String sourceId,
			long eventId, String nodeId, String attributeKey, Object oldValue,
			Object newValue )
	{
		return createAttributeChangedEvent(sourceId, eventId, ElementType.node,
				nodeId, attributeKey, oldValue, newValue);
	}
	
	public static AttributeEvent createEdgeAttributeChangedEvent(String sourceId,
			long eventId, String edgeId, String attributeKey, Object oldValue,
			Object newValue )
	{
		return createAttributeChangedEvent(sourceId, eventId, ElementType.edge,
				edgeId, attributeKey, oldValue, newValue);
	}
	
	public static AttributeEvent createAttributeChangedEvent(String sourceId,
			long eventId, ElementType elementType, String elementId, String attributeKey,
			Object oldValue, Object newValue )
	{
		return new AttributeEvent(sourceId, eventId,
				GraphAction.attributeChanged, elementType, elementId,
				attributeKey, oldValue, newValue);
	}
	
	public static AttributeEvent createGraphAttributeRemovedEvent(String sourceId,
			long eventId, String attributeKey )
	{
		return createAttributeRemovedEvent(sourceId, eventId, ElementType.graph,
				sourceId, attributeKey );
	}
	
	public static AttributeEvent createNodeAttributeRemovedEvent(String sourceId,
			long eventId, String nodeId, String attributeKey )
	{
		return createAttributeRemovedEvent(sourceId, eventId, ElementType.node,
				nodeId, attributeKey );
	}
	
	public static AttributeEvent createEdgeAttributeRemovedEvent(String sourceId,
			long eventId, String edgeId, String attributeKey )
	{
		return createAttributeRemovedEvent(sourceId, eventId, ElementType.edge,
				edgeId, attributeKey);
	}
	
	public static AttributeEvent createAttributeRemovedEvent(String sourceId,
			long eventId, ElementType elementType, String elementId, String attributeKey )
	{
		return new AttributeEvent(sourceId, eventId,
				GraphAction.attributeRemoved, elementType, elementId,
				attributeKey, null, null);
	}
	
	/**
	 * The source which produces this event.
	 */
	protected String sourceId;
	/**
	 * ID of this event, according to the source.
	 * This is used in synchronization.
	 */
	protected long eventId;
	/**
	 * Defines the content of this event.
	 */
	protected GraphAction graphAction;
	
	/*
	 * Forbidden constructor.
	 */
	private GraphEvent()
	{
		
	}
	/**
	 * Constructs a new graph event.
	 * Action is set to none.
	 * 
	 * @param sourceId source of the event
	 */
	public GraphEvent( String sourceId )
	{
		this(sourceId,Long.MIN_VALUE,GraphAction.none);
	}
	/**
	 * Constructs a new graph event.
	 * Action is set to none.
	 * 
	 * @param sourceId source of this event
	 * @param eventId id of this event
	 */
	public GraphEvent( String sourceId, long eventId )
	{
		this(sourceId,eventId,GraphAction.none);
	}
	/**
	 * Constructs a new graph event.
	 * 
	 * @param sourceId source of this event
	 * @param eventId id of this event
	 * @param graphAction action of this event
	 */
	public GraphEvent( String sourceId, long eventId, GraphAction graphAction )
	{
		this();
		
		this.sourceId 		= sourceId;
		this.eventId		= eventId;
		this.graphAction	= graphAction;
	}

	/**
	 * Get the source which produces this event.
	 * 
	 * @return the source id
	 */
	public String getSourceId()
	{
		return sourceId;
	}
	/**
	 * Get the id of this event.
	 * 
	 * @return id of event
	 */
	public long getEventId()
	{
		return eventId;
	}
	/**
	 * Get the action of this event.
	 * 
	 * @return action of event
	 * @see org.graphstream.graph.GraphEvent.GraphAction
	 */
	public GraphAction getGraphAction()
	{
		return graphAction;
	}
	
	public static class GraphStepEvent
		extends GraphEvent
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -2851377166881311293L;
		
		protected double time;
		
		public GraphStepEvent( String sourceId, long eventId, double time )
		{
			super(sourceId,eventId,GraphAction.stepBegins);
			
			this.time = time;
		}
		
		public double getTime()
		{
			return time;
		}
	}
	
	/**
	 * Defines an event relative to an element of the graph.
	 */
	public static class ElementEvent
		extends GraphEvent
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -3503582275713042132L;

		/**
		 * Id of the element.
		 */
		protected String 		elementId;
		/**
		 * Type of the element.
		 */
		protected ElementType	elementType;
		
		/**
		 * Constructs a new element event.
		 * 
		 * @param sourceId source of the event
		 * @param eventId id of the event
		 * @param graphAction action of the event
		 * @param elementType type of the element
		 * @param elementId id of the element
		 */
		public ElementEvent(String sourceId, long eventId,
				GraphAction graphAction, ElementType elementType,
				String elementId)
		{
			super(sourceId,eventId,graphAction);
			
			this.elementId 		= elementId;
			this.elementType	= elementType;
		}
		
		/**
		 * Get the id of the element.
		 * 
		 * @return id of the element
		 */
		public String getElementId()
		{
			return elementId;
		}
		/**
		 * Get the type of the element.
		 * 
		 * @return type of element
		 */
		public ElementType getElementType()
		{
			return elementType;
		}
	}
	
	/**
	 * Defines an event relative to a node.
	 */
	public static class NodeEvent
		extends ElementEvent
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 3576404464736018332L;

		/**
		 * Constructs a new node event.
		 * 
		 * @param sourceId source of the event
		 * @param eventId id of the event
		 * @param graphAction action of the event
		 * @param nodeId id of the node
		 */
		public NodeEvent(String sourceId, long eventId,
				GraphAction graphAction, String nodeId)
		{
			super(sourceId,eventId,graphAction,ElementType.node,nodeId);
		}
		
		/**
		 * Get the node id.
		 * 
		 * @return id of the node
		 */
		public String getNodeId()
		{
			return elementId;
		}
	}
	
	/**
	 * Defines an event relative to an edge.
	 */
	public static class EdgeEvent
		extends ElementEvent
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 3576404464736018332L;

		/**
		 * Source node of the edge.
		 */
		protected String sourceNode;
		/**
		 * Target node of the edge.
		 */
		protected String targetNode;
		/**
		 * Defines is the edge is directed or not.
		 */
		protected boolean directed;
		
		/**
		 * Constructs a new edge event.
		 * 
		 * @param sourceId source of the event
		 * @param eventId id of the event
		 * @param graphAction action of the event
		 * @param edgeId id of the edge
		 */
		public EdgeEvent(String sourceId, long eventId,
				GraphAction graphAction, String edgeId)
		{
			this(sourceId,eventId,graphAction,edgeId,null,null,false);
		}
		/**
		 * Constructs a new edge event.
		 * 
		 * @param sourceId source of the event
		 * @param eventId id of the event
		 * @param graphAction action of the event
		 * @param edgeId id of the edge
		 * @param sourceNode source node of the edge
		 * @param targetNode target node of the edge
		 * @param directed edge is directed ?
		 */
		public EdgeEvent(String sourceId, long eventId,
				GraphAction graphAction, String edgeId, String sourceNode, String targetNode,
				boolean directed)
		{
			super(sourceId,eventId,graphAction,ElementType.edge,edgeId);
			
			this.sourceNode = sourceNode;
			this.targetNode = targetNode;
			this.directed	= directed;
		}
		
		/**
		 * Get the edge id.
		 * 
		 * @return id of the edge
		 */
		public String getEdgeId()
		{
			return elementId;
		}
		/**
		 * Get source node of the edge.
		 * 
		 * @return source node
		 */
		public String getSourceNode()
		{
			return sourceNode;
		}
		/**
		 * Get target node of the edge.
		 * 
		 * @return target node
		 */
		public String getTargetNode()
		{
			return targetNode;
		}
		/**
		 * Get extremity 0 of the edge.
		 * Same as source node.
		 * 
		 * @return source node
		 */
		public String getNode0()
		{
			return sourceNode;
		}
		/**
		 * Get extremity 1 of the edge.
		 * Same as target node.
		 * 
		 * @return target node
		 */
		public String getNode1()
		{
			return targetNode;
		}
		/**
		 * Is edge directed ?
		 * 
		 * @return true if edge is directed
		 */
		public boolean isDirected()
		{
			return directed;
		}
	}
	
	/**
	 * Defines an event relative to an attribute.
	 */
	public static class AttributeEvent
		extends ElementEvent
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -7714852440275130731L;
		
		/**
		 * Attribute key.
		 */
		protected String attribute;
		/**
		 * Old value of the attribute.
		 * Or null if attribute is added.
		 */
		protected Object oldValue;
		/**
		 * New value of the attribute.
		 * Or null if attribute is removed.
		 */
		protected Object newValue;

		/**
		 * Constructs a new attribute event.
		 * 
		 * @param sourceId source of the event
		 * @param eventId id of the event
		 * @param graphAction action of the event
		 * @param elementType type of the element owning the attribute
		 * @param elementId id of the element owning the attribute
		 * @param attribute key of the attribute
		 * @param oldValue old value of the attribute
		 * @param newValue new value of the attribute
		 */
		public AttributeEvent(String sourceId, long eventId,
				GraphAction graphAction, ElementType elementType,
				String elementId, String attribute, Object oldValue, Object newValue )
		{
			super(sourceId, eventId, graphAction, elementType, elementId);
			
			this.attribute 	= attribute;
			this.oldValue	= oldValue;
			this.newValue	= newValue;
		}
		
		/**
		 * Get the key of the attribute.
		 * 
		 * @return key of the attribute
		 */
		public String getAttributeKey()
		{
			return attribute;
		}
		/**
		 * Get the old value of the attribute.
		 * 
		 * @return old value
		 */
		public Object getOldValue()
		{
			return oldValue;
		}
		/**
		 * Get the new value of the attribute.
		 * 
		 * @return new value
		 */
		public Object getNewValue()
		{
			return newValue;
		}
	}
}
