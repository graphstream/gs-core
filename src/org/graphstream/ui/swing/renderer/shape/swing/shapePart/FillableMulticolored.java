package org.graphstream.ui.swing.renderer.shape.swing.shapePart;

import java.awt.Color;

import org.graphstream.ui.graphicGraph.stylesheet.Style;
import org.graphstream.ui.swing.SwingDefaultCamera;

public class FillableMulticolored {
	public Color[] fillColors = null ;
	
	public void configureFillableMultiColoredForGroup(Style style, SwingDefaultCamera camera) {
		int count = style.getFillColorCount();
		
		if(fillColors == null || fillColors.length != count) {
			fillColors = new Color[count];
		}
		
		for (int i = 0 ; i < count ; i++) {
			fillColors[i] = style.getFillColor(i);
		}
	}
}