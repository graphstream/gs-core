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

package org.graphstream.ui.old.swing.settingsPane;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import org.graphstream.ui.old.swing.SwingGraphViewer;

/**
 * Window containing various settings and actions for the graph panel.
 *
 * @since 2007
 */
public class SettingsWindow extends JFrame
{
// Attributes
	
	private static final long serialVersionUID = 3291555160601072956L;
	
	protected ViewSettingsPane viewerSettings;
	
	protected LayoutSettingsPane layoutSettings;
	
	protected ScreenShotPane screenShotSettings;
	
	protected SwingGraphViewer graphViewer;

	protected JTabbedPane tabs;
	
// Constructors
	
	public SettingsWindow( SwingGraphViewer graphViewer )
	{
		this.graphViewer = graphViewer;
		
		setTitle( "GraphStream Viewer: settings" );
		setPreferredSize( new Dimension( 400, 300 ) );
		setDefaultCloseOperation( JFrame.HIDE_ON_CLOSE );

		tabs = new JTabbedPane();
		
		viewerSettings     = new ViewSettingsPane( graphViewer );
		screenShotSettings = new ScreenShotPane( graphViewer );
		
		tabs.addTab( "View", createImageIcon( "SettingsIcon32.png", "" ), viewerSettings );
		tabs.addTab( "Screen shot", createImageIcon( "ScreenShotIcon32.png", "" ), screenShotSettings );
		
		if( graphViewer.getLayoutRemote() != null )
		{
			layoutSettings = new LayoutSettingsPane( graphViewer );
			tabs.addTab( "Layout", createImageIcon( "LayoutIcon32.png", "" ), layoutSettings );
		}
		
		ImageIcon icon = createImageIcon( "GraphStreamSmallLogo24.png", "" );
		
		setIconImage( icon.getImage() );
		add( tabs, BorderLayout.CENTER );
		pack();
	}

	protected ImageIcon createImageIcon(String path, String description)
	{
	    java.net.URL imgURL = getClass().getResource( path );
	    
	    if (imgURL != null)
	    {
	        return new ImageIcon( imgURL, description );
	    }
	    else
	    {
	    	return new ImageIcon( path, description );
	    }
	}
}