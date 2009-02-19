/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.builder.append( 
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */

package org.miv.graphstream.ui.graphicGraph.stylesheet.test;

import java.io.IOException;

import org.miv.graphstream.ui.graphicGraph.stylesheet.Selector;
import org.miv.graphstream.ui.graphicGraph.stylesheet.StyleSheet;

/**
 * Test the style sheet parsing.
 * @author Antoine Dutot
 */
public class TestStyleSheet
{
	public static String styleSheet1 =
		"graph { color:red; } " +
		"node { color:blue; }" +
		"edge { color:green; }" +
		"node.A { width:12; }";
	
	public static String styleSheet2 =
		"graph {\n"+
			"color:        rgba(255,0,0,128);\n"+
			"width:        5;\n"+
			"node-shape:   square;\n"+
			"edge-shape:   angle;\n"+
			"arrow-style:  large;\n"+
			"border-width: 1;\n"+
			"border-color: #00FF00;\n"+
			"text-color:   rgb(50,50,50);\n"+
			"text-font:    serif;\n"+
			"text-offset:  5 1;\n"+
			"text-style:   bold\n;\n"+
			"text-align:   left;\n"+
		"}\n"+
		"node {\n"+
			"color: rgb(128,128,128);\n"+
		"}\n"+
		"edge {\n"+
			"width: 1;\n"+
		"}\n"+
		"sprite {\n"+
			"node-image: url(\"truc.jpg\");\n"+
		"}\n";
	
	public static String styleSheet3 =
		"graph { color: red blue yellow; }" +
		"node { color: cyan magenta green; }" +
		"edge { color: #505050 #A0C0B0; }"; 
	
	public static String style1 = "border-width:2; border-color:cyan;";
		
	public static void main( String args[] )
	{
		new TestStyleSheet();
	}
	
	public TestStyleSheet()
	{
		//test( styleSheet1 );
		//test( styleSheet2 );
		//test2( styleSheet1, styleSheet2, style1 );
		test3( styleSheet3 );
	}
	
	/**
	 * Parse a sheet and print it.
	 * @param styleSheet The style sheet.
	 */
	protected void test( String styleSheet )
	{
		StyleSheet sheet = new StyleSheet();
		
		try
        {
	        sheet.parseFromString( styleSheet );
        }
        catch( IOException e )
        {
	        e.printStackTrace();
        }
        
        System.out.printf( "%s", sheet.toString() );
	}
	
	/**
	 * Accumulate several sheets (the cascade) and print the result.
	 * @param sheet1 The simplest sheet.
	 * @param sheet2 The more complex sheet.
	 */
	protected void test2( String sheet1, String sheet2, String style1 )
	{
		StyleSheet sheet = new StyleSheet();
		
		try
		{
			sheet.parseFromString( sheet2 );
			sheet.parseFromString( sheet1 );
			sheet.parseStyleFromString( new Selector( Selector.Type.EDGE ), style1 );
			sheet.parseStyleFromString( new Selector( Selector.Type.NODE, "A", null ), style1 );
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		
		System.out.printf( "%s", sheet.toString() );
	}
	
	/**
	 * Parsing test for various styling elements.
	 * @param sheet1 The style sheet to test.
	 */
	protected void test3( String sheet1 )
	{
		StyleSheet sheet = new StyleSheet();
		
		try
		{
			sheet.parseFromString( sheet1 );
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		
		System.out.printf( "%s" , sheet.toString() );
	}
}