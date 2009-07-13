package org.miv.graphstream.ui2.graphicGraph.stylesheet;

/**
 * Allows to specify the dimension of the graph in graph units as well as the dimension of
 * the canvas onto witch it is rendered in pixels (or rendering units when this is not pixels).
 * 
 * <p>
 * The graph metrics allow to do conversions between units. The style sheet
 * provides three kinds of units :
 * <ul>
 * 		<li>PX: for pixels (or rendering units).</li>
 * 		<li>GU: for graph units or units used to specify graph elements positions and lengths.</li>
 * 		<li>percents: relative to the size of elements or of the whole graph.</li>
 * </ul>
 * </p>
 */
public interface GraphMetrics
{
	/**
	 * The width in GU of the whole graph.
	 */
	float getGraphWidth();
	
	/**
	 * The height in GU of the whole graph.
	 */
	float getGraphHeight();

	/**
	 * The depth in GU of the whole graph.
	 * @return
	 */
	float getGraphDepth();
	
	/**
	 * The width in pixels (or output units) of the rendering canvas.
	 */
	float getCanvasWidth();

	/**
	 * The height in pixels (or output units) of the rendering canvas.
	 */
	float getCanvasHeight();
}