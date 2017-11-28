package org.graphstream.ui.viewerScala.test;

import org.graphstream.ui.spriteManager.Sprite;

public class DataSprite extends Sprite {
	public void setData( float[] values ) {
		float[] data = new float[values.length];
		
		for (int i = 0 ; i < values.length ; i++) {
			data[i] = values[i];
		}
		
		setAttribute( "ui.pie-values", data );
	}
}
