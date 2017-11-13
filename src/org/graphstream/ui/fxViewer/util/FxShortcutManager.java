package org.graphstream.ui.fxViewer.util;

import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.view.Camera;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.util.ShortcutManager;

import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class FxShortcutManager implements ShortcutManager {
	// Attributes

	/**
	 * The viewer to control.
	 */
	protected View view;
	
	protected double viewPercent = 1;
	
	protected Point3 viewPos = new Point3();
	
	protected double rotation = 0;
	
	// Construction
	
	public void init(GraphicGraph graph, View view) {
		this.view = view;
		view.addEventFilter(KeyEvent.KEY_PRESSED, keyPressed);
	}
	
	public void release() {

	}
	
	// Events
	
	/**
	 * A key has been pressed.
	 * 
	 * @param event  The event that generated the key.
	 */
	EventHandler<KeyEvent> keyPressed = new EventHandler<KeyEvent>() {
	    @Override
	    public void handle(KeyEvent event) {
			Camera camera = view.getCamera();
		
			if (event.getCode() == KeyCode.PAGE_UP) {
				camera.setViewPercent(Math.max(0.0001f,
						camera.getViewPercent() * 0.9f));
			} else if (event.getCode() == KeyCode.PAGE_DOWN) {
				camera.setViewPercent(camera.getViewPercent() * 1.1f);
			} else if (event.getCode() == KeyCode.LEFT) {
				if (event.isAltDown()) {
					double r = camera.getViewRotation();
					camera.setViewRotation(r - 5);
				} else {
					double delta = 0;
		
					if (event.isShiftDown())
						delta = camera.getGraphDimension() * 0.1f;
					else
						delta = camera.getGraphDimension() * 0.01f;
		
					delta *= camera.getViewPercent();
		
					Point3 p = camera.getViewCenter();
					camera.setViewCenter(p.x - delta, p.y, 0);
				}
			} else if (event.getCode() == KeyCode.RIGHT) {
				if (event.isAltDown()) {
					double r = camera.getViewRotation();
					camera.setViewRotation(r + 5);
				} else {
					double delta = 0;
		
					if (event.isShiftDown())
						delta = camera.getGraphDimension() * 0.1f;
					else
						delta = camera.getGraphDimension() * 0.01f;
		
					delta *= camera.getViewPercent();
		
					Point3 p = camera.getViewCenter();
					camera.setViewCenter(p.x + delta, p.y, 0);
				}
			} else if (event.getCode() == KeyCode.UP) {
				double delta = 0;
		
				if (event.isShiftDown())
					delta = camera.getGraphDimension() * 0.1f;
				else
					delta = camera.getGraphDimension() * 0.01f;
		
				delta *= camera.getViewPercent();
		
				Point3 p = camera.getViewCenter();
				camera.setViewCenter(p.x, p.y + delta, 0);
			} else if (event.getCode() == KeyCode.DOWN) {
				double delta = 0;
		
				if (event.isShiftDown())
					delta = camera.getGraphDimension() * 0.1f;
				else
					delta = camera.getGraphDimension() * 0.01f;
		
				delta *= camera.getViewPercent();
		
				Point3 p = camera.getViewCenter();
				camera.setViewCenter(p.x, p.y - delta, 0);
			}
	    }
	};
}
