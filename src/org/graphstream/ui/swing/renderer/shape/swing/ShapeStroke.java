package org.graphstream.ui.swing.renderer.shape.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

import org.graphstream.ui.graphicGraph.stylesheet.Style;

public abstract class ShapeStroke {
	public abstract Stroke stroke(double width) ;

	public static ShapeStroke strokeForArea(Style style) {
		switch (style.getStrokeMode()) {
			case PLAIN: return new PlainShapeStroke();
			case DOTS: return new DotsShapeStroke();
			case DASHES: return new DashesShapeStroke();
			case DOUBLE: return new DoubleShapeStroke();
			default: return null ;
		}
	}
	
	public static ShapeStroke strokeForConnectorFill(Style style) {
		switch (style.getFillMode()) {
			case PLAIN: return new PlainShapeStroke();
			case DYN_PLAIN: return new PlainShapeStroke();
			case NONE: return null	; // Gracefully handled by the drawing part.
			default: return new PlainShapeStroke() ;
		}
	}
	
	public ShapeStroke strokeForConnectorStroke(Style style) {
		return strokeForArea(style);
	}
	
	public static Color strokeColor(Style style) {
		if( style.getStrokeMode() != org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.StrokeMode.NONE ) {
			return style.getStrokeColor( 0 );
		} 
		else {
			return null;
		}
	}
	
}

class PlainShapeStroke extends ShapeStroke {
	private double oldWidth = 0.0 ;
	private Stroke oldStroke = null ;
	
	@Override
	public Stroke stroke(double width) {
		if( width == oldWidth ) {
			if( oldStroke == null ) 
				oldStroke = new BasicStroke( (float)width/*, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL*/ )	;// WTF ??
			return oldStroke;
		} 
		else {
			oldWidth  = width;
			oldStroke = new BasicStroke( (float)width/*, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL*/ );
			return oldStroke;
		}
	}
}

class DotsShapeStroke extends ShapeStroke {
	private double oldWidth = 0.0 ;
	private Stroke oldStroke = null ;
	
	@Override
	public Stroke stroke(double width) {
		if( width == oldWidth ) {
			if( oldStroke == null ) {
				float[] f = {(float)width, (float)width} ;
				oldStroke = new BasicStroke( (float)width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10, f, 0); // WTF ??
			}
			return oldStroke;
		} else {
			oldWidth = width;
			float[] f = {(float)width, (float)width} ;
			oldStroke = new BasicStroke( (float)width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10, f, 0);
			return oldStroke;
		}
	}
}

class DashesShapeStroke extends ShapeStroke {
	private double oldWidth = 0.0 ;
	private Stroke oldStroke = null ;
	
	@Override
	public Stroke stroke(double width) {
		if( width == oldWidth ) {
			if( oldStroke == null ){
				float[] f = {(float)(3*width), (float)(3*width)};
				oldStroke = new BasicStroke( (float)width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10, f, 0); // WTF ??
			}
			return oldStroke ;
		} else {
			oldWidth = width ;
			float[] f = {(float)(3*width), (float)(3*width)};
			oldStroke = new BasicStroke( (float)width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10, f, 0);
			return oldStroke ;
		}
	}	
}

class DoubleShapeStroke extends ShapeStroke {
	private double oldWidth = 0.0 ;
	private Stroke oldStroke = null ;
	
	@Override
	public Stroke stroke(double width) {
		if(width == oldWidth) {
            if(oldStroke == null) 
            	oldStroke = new CompositeStroke(new BasicStroke((float)width*3), new BasicStroke((float)width));
            return oldStroke;
        } else {
            oldWidth = width;
            oldStroke = new CompositeStroke(new BasicStroke((float)width*2), new BasicStroke((float)width));
            return oldStroke;
        }
	}
	
	class CompositeStroke implements Stroke {
		private Stroke stroke1 ;
		private Stroke stroke2 ;
		
		public CompositeStroke(Stroke stroke1, Stroke stroke2) {
			this.stroke1 = stroke1 ;
			this.stroke2 = stroke2 ;
		}
    	public java.awt.Shape createStrokedShape(java.awt.Shape shape) {
    		return stroke2.createStrokedShape(stroke1.createStrokedShape(shape));
    	}
    }
}
