package org.graphstream.ui.scalaViewer.renderer.shape.swing.shapePart;

import java.awt.Color;

import org.graphstream.ui.graphicGraph.stylesheet.Style;
import org.graphstream.ui.scalaViewer.ScalaDefaultCamera;

public class FillableMulticolored {
	public Color[] fillColors = null ;
	
	public void configureFillableMultiColoredForGroup(Style style, ScalaDefaultCamera camera) {
		int count = style.getFillColorCount();
		
		if(fillColors == null || fillColors.length != count) {
			fillColors = new Color[count];
		}
		
		for (int i = 0 ; i < count ; i++) {
			fillColors[i] = style.getFillColor(i);
		}
	}
}