package org.miv.graphstream.ui2.graphicGraph.stylesheet;

/**
 * Allows to specify the dimension of the graph in graph units as well as the dimension of
 * the canvas onto witch it is rendered in pixels (or rendering units when this is not pixels).
 */
public interface GraphMetrics
{
	float getGraphWidth();
	
	float getGraphHeight();
	
	float getGraphDepth();
	
	float getCanvasWidth();
	
	float getCanvasHeight();
}