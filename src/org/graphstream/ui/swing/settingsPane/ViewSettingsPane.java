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

package org.graphstream.ui.swing.settingsPane;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import org.graphstream.ui.swing.SwingGraphViewer;

/**
 * Window to set general options and call commands on the graphic output.
 */
class ViewSettingsPane extends JPanel implements ActionListener, ChangeListener, ItemListener
{
// Attributes
	
	private static final long serialVersionUID = 1L;

	protected SwingGraphViewer graphViewer;
	
// Constructors	

	public ViewSettingsPane( SwingGraphViewer graphViewer )
	{
		this.graphViewer = graphViewer;
	
		//setLayout( new GridLayout( 4, 1 ) );
		setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
		
		buildButtonsPanel();
		buildSlidersPanel();
		
		add( buttonsPanel );
		add( slidersPanel );
	}

	public void actionPerformed( ActionEvent arg0 )
	{
	}

	public void stateChanged( ChangeEvent ev )
	{
		if( ev.getSource() == quality )
		{
			int val = quality.getValue();
			
			graphViewer.getRenderer().setQuality( val );
		}
	}

	public void itemStateChanged( ItemEvent e )
	{
		if( e.getSource() == showFPS )
		{
			if( e.getStateChange() == ItemEvent.DESELECTED )
			     graphViewer.getRenderer().showFPS( false );
			else graphViewer.getRenderer().showFPS( true );
		}
		else if( e.getSource() == showNodes )
		{
			if( e.getStateChange() == ItemEvent.DESELECTED )
			     graphViewer.getRenderer().showNodes( false );
			else graphViewer.getRenderer().showNodes( true );
		}
		else if( e.getSource() == showEdges )
		{
			if( e.getStateChange() == ItemEvent.DESELECTED )
			     graphViewer.getRenderer().showEdges( false );
			else graphViewer.getRenderer().showEdges( true );			
		}
		else if( e.getSource() == showEdgeLabels )
		{
			if( e.getStateChange() == ItemEvent.DESELECTED )
			     graphViewer.getRenderer().showEdgeLabels( false );
			else graphViewer.getRenderer().showEdgeLabels( true );			
		}
		else if( e.getSource() == showNodeLabels )
		{
			if( e.getStateChange() == ItemEvent.DESELECTED )
			     graphViewer.getRenderer().showNodeLabels( false );
			else graphViewer.getRenderer().showNodeLabels( true );			
		}
		else if( e.getSource() == showEdgeArrows )
		{
			if( e.getStateChange() == ItemEvent.DESELECTED )
			     graphViewer.getRenderer().showEdgeDirection( false );
			else graphViewer.getRenderer().showEdgeDirection( true );
		}
	}

// Sliders
	
	/**
	 * Several sliders.
	 */
	protected JPanel slidersPanel;
	
	/**
	 * Set the quality on 5 values.
	 */
	protected JSlider quality;
	
	/**
	 * Build the sliders panel.
b	 */
	protected void buildSlidersPanel()
	{
		slidersPanel = new JPanel();
		
		slidersPanel.setLayout( new GridLayout( 1, 2 ) );
		slidersPanel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder( EtchedBorder.LOWERED ), "Quality", TitledBorder.CENTER, TitledBorder.CENTER ) );
		
		quality = new JSlider( 0, 4 );
		
		quality.setToolTipText( "Change the graph rendering quality from 0 to 4. 0 is lower quality but faster, 4 is higher quality but slower." );

		quality.addChangeListener( this );
		quality.setBorder( BorderFactory.createEmptyBorder( 4, 4, 4, 4 ) );
		slidersPanel.add( new JLabel( "Quality:" ) );
		slidersPanel.add( quality );
		quality.setPaintLabels( true );
		quality.setPaintTicks( true );
		quality.setMajorTickSpacing( 4 );
		quality.setMinorTickSpacing( 1 );
		quality.setSnapToTicks( true );
		quality.setValue( graphViewer.getRenderer().getQuality() );
	}
	
// Buttons
	
	/**
	 * Set of buttons.
	 */
	protected JPanel buttonsPanel;
	
	/**
	 * Allow to enable/disable the FPS monitor.
	 */
	protected JCheckBox showFPS;
	
	/**
	 * Allow to show or hide nodes.
	 */
	protected JCheckBox showNodes;
	
	/**
	 * Allow to show or hide edges.
	 */
	protected JCheckBox showEdges;
	
	/**
	 * Allow to show or hide the edge arrows.
	 */
	protected JCheckBox showEdgeArrows;

	private JCheckBox showNodeLabels;

	private JCheckBox showEdgeLabels;

	/**
	 * Builds the buttons panel.
	 */
	protected void buildButtonsPanel()
	{
		buttonsPanel = new JPanel();
		
		buttonsPanel.setLayout( new GridLayout( 2, 3 ) );
		buttonsPanel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder( EtchedBorder.LOWERED ), "Settings", TitledBorder.CENTER, TitledBorder.CENTER ) );
		
		showFPS   = new JCheckBox( "Show FPS" );
		showNodes = new JCheckBox( "Show nodes" );
		showEdges = new JCheckBox( "Show edges" );
		showNodeLabels = new JCheckBox( "Show node labels" );
		showEdgeLabels = new JCheckBox( "Show edge labels" );
		showEdgeArrows = new JCheckBox( "Show edge arrows" );
		
		showNodes.setSelected( true );
		showEdges.setSelected( true );
		
		showNodeLabels.setSelected(true );
		showEdgeLabels.setSelected( true );
		showEdgeArrows.setSelected( true );
		
		showFPS.setToolTipText( "Show or hide the frames-per-second monitor." );
		showNodes.setToolTipText( "Show or hide the nodes." );
		showEdges.setToolTipText( "Show or hide the edges." );
		showNodeLabels.setToolTipText( "Show or hide the node labels." );
		showEdgeLabels.setToolTipText( "Show or hide the edge labels." );
		showEdgeArrows.setToolTipText( "Show or hide the edge orientation arrows." );
		
		buttonsPanel.add( showFPS );
		buttonsPanel.add( showNodes );
		buttonsPanel.add( showEdges );
		buttonsPanel.add( showEdgeArrows );
		buttonsPanel.add( showNodeLabels );
		buttonsPanel.add( showEdgeLabels );
		
		showFPS.addItemListener( this );
		showNodes.addItemListener( this );
		showEdges.addItemListener( this );
		showNodeLabels.addItemListener( this );
		showEdgeLabels.addItemListener( this );
		showEdgeArrows.addItemListener( this );
	}
}