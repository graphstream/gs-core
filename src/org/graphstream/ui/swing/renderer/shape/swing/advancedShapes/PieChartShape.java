package org.graphstream.ui.swing.renderer.shape.swing.advancedShapes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.util.logging.Logger;

import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.stylesheet.Style;
import org.graphstream.ui.swing.Backend;
import org.graphstream.ui.swing.SwingDefaultCamera;
import org.graphstream.ui.swing.renderer.AreaSkeleton;
import org.graphstream.ui.swing.renderer.Skeleton;
import org.graphstream.ui.swing.renderer.shape.Decorable;
import org.graphstream.ui.swing.renderer.shape.Shape;
import org.graphstream.ui.swing.renderer.shape.swing.Area;
import org.graphstream.ui.swing.renderer.shape.swing.shapePart.FillableMulticolored;
import org.graphstream.ui.swing.renderer.shape.swing.shapePart.Shadowable;
import org.graphstream.ui.swing.renderer.shape.swing.shapePart.Strokable;
import org.graphstream.ui.swing.util.AttributeUtils;

public class PieChartShape extends FillableMulticolored implements Shape, AttributeUtils {
	Color[] colors = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.MAGENTA,
	        Color.CYAN, Color.ORANGE, Color.PINK};
	
	Strokable strokabe ;
	Shadowable shadowable ;
	Decorable decorable ;
	Area area ;
	
	Ellipse2D.Double theShape = new Ellipse2D.Double();
	double[] theValues = null ;
	Object valuesRef = null ;
	
	public PieChartShape() {
		strokabe = new Strokable();
		shadowable = new Shadowable();
		decorable = new Decorable();
		area = new Area();
	}
	
	@Override
	public void configureForGroup(Backend backend, Style style, SwingDefaultCamera camera) {
		area.configureAreaForGroup(style, camera);
        configureFillableMultiColoredForGroup(style, camera);
        strokabe.configureStrokableForGroup(style, camera);
        shadowable.configureShadowableForGroup(style, camera);
        decorable.configureDecorableForGroup(style, camera);		
	}
	@Override
	public void configureForElement(Backend bck, GraphicElement element, Skeleton skel,
			SwingDefaultCamera camera) {
		Graphics2D g = bck.graphics2D();
		decorable.configureDecorableForElement(bck, camera, element, skel);
		area.configureAreaForElement(bck, camera, (AreaSkeleton)skel, element, decorable.theDecor);		
	}
	@Override
	public void make(Backend backend, SwingDefaultCamera camera) {
        theShape.setFrameFromCenter(area.theCenter.x, area.theCenter.y, area.theCenter.x + area.theSize.x / 2, area.theCenter.y + area.theSize.y / 2);		
	}
	
	@Override
	public void makeShadow(Backend backend, SwingDefaultCamera camera) {
		theShape.setFrameFromCenter(area.theCenter.x + shadowable.theShadowOff.x, area.theCenter.y + shadowable.theShadowOff.y,
				area.theCenter.x + (area.theSize.x + shadowable.theShadowWidth.x) / 2, area.theCenter.y + (area.theSize.y + shadowable.theShadowWidth.y) / 2);		
	}
	@Override
	public void render(Backend bck, SwingDefaultCamera camera, GraphicElement element, Skeleton skel) {
		Graphics2D g = bck.graphics2D();
		make(bck, camera);
		checkValues(element);
		fillPies(g, element);
		//fill(g, theSize, theShape)
		strokabe.stroke(g, theShape);
		decorable.decorArea(bck, camera, skel.iconAndText, element, theShape);
	}
	
	private void fillPies(Graphics2D g, GraphicElement element) {
		if (theValues != null) {
			// we assume the pies values sum up to one. And we wont check it, its a mater of speed ;-).
			Arc2D.Double arc = new Arc2D.Double();
            double beg = 0.0;
            double end = 0.0;
            double col = 0;
            double sum = 0.0;
            
            for( int i = 0 ; i < theValues.length ; i++ ) {
            	double value = theValues[i];
            	end = beg + value;
                arc.setArcByCenter(area.theCenter.x, area.theCenter.y, area.theSize.x / 2, beg * 360, value * 360, Arc2D.PIE);
                g.setColor(fillColors[(int) (col % fillColors.length)]);
                g.fill(arc);
                beg = end;
                sum += value;
                col += 1;
            }

            if (sum > 1.01f)
                Logger.getLogger(this.getClass().getSimpleName()).warning("[Sprite "+element.getId()+"] The sum of values for ui.pie-value should eval to 1 at max (actually "+sum+").");
        }
		else {
            // Draw a red empty circle to indicate "no value".
            g.setColor(Color.red);
            g.draw(theShape);
        }
	}

	private void checkValues(GraphicElement element) {
		Object pieValues = element.getAttribute("ui.pie-values");
	
		if (pieValues != null) {
			// Object oldRef = valuesRef;
			valuesRef = pieValues;
			// We use valueRef to avoid
			// recreating the values array for nothing.
			//if ((theValues == null) || (oldRef ne valuesRef)) {	// Cannot do this : the array reference can be the same and the values changed.
			theValues = getDoubles(valuesRef);
			//}
		}
	}

	@Override
	public void renderShadow(Backend bck, SwingDefaultCamera camera, GraphicElement element, Skeleton skeleton) {
		makeShadow(bck, camera);
		shadowable.cast(bck.graphics2D(), theShape);		
	}	
}