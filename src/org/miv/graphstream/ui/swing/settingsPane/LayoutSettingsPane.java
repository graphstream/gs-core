/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */

package org.miv.graphstream.ui.swing.settingsPane;

import java.util.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

import org.miv.graphstream.ui.layout.LayoutRunner.LayoutRemote;
import org.miv.graphstream.ui.swing.SwingGraphViewer;

/**
 * Panel to configure a spring box runner, included in the SettingsWindow.
 * 
 * <p>
 * This pannel takes as argument a spring box runner message box and use it
 * to send commands and settings to it using a GUI.
 * </p>
 * 
 * @author Antoine Dutot
 * @author Yoann Pigné
 * @since 20070118
 */
class LayoutSettingsPane extends JPanel implements ActionListener, ChangeListener
{
	private static final long serialVersionUID = -7420536758576072743L;

	/**
	 * Set of forces values for the SpringBox algorithm. Use this array to get a non-linear progression.
	 */
	protected float forces[] = { 0.001f, 0.002f, 0.005f, 0.01f, 0.02f, 0.05f, 0.08f,  0.1f, 0.2f, 0.5f, 0.8f, 1f, 2f };
	
	/**
	 * Message box toward the spring layout runner.
	 */
	protected LayoutRemote layout;
	
	/**
	 * Button panel.
	 */
	protected JPanel buttons;
	
	/**
	 * Sliders panel.
	 */
	protected JPanel sliders;
	
	/**
	 * The activate/de-activate button.
	 */
	protected JButton pausePlay;
	
	/**
	 * Shake button.
	 */
	protected JButton shake;
	
	/**
	 * In which state the layout is?.
	 */
	protected boolean paused;
	
	/**
	 * Setup the layout force.
	 */
	protected JSlider force;
	
	/**
	 * Quality of the layout.
	 */
	protected JSlider quality;
	
	/**
	 * I/O panel.
	 */
	protected JPanel ioPanel;
	
	/**
	 * Filename of the output position filename. 
	 */
	protected JTextField outPosField;
	
	/**
	 * Do the output of the position file.
	 */
	protected JButton outPos;

	/**
	 * New settings panel that command the spring box runner given under
	 * the form of a message box where to send orders. 
	 * @param graphViewer The viewer.
	 */
	public LayoutSettingsPane( SwingGraphViewer graphViewer )
	{
		layout      = graphViewer.getLayoutRemote();
		buttons     = new JPanel();
		sliders     = new JPanel();
		pausePlay   = new JButton( "pause layout" );
		shake       = new JButton( "shake" );
		force       = new JSlider( 0, forces.length - 1 );
		quality     = new JSlider( 0, 4 );
		ioPanel     = new JPanel();
		outPosField = new JTextField( "positions.pos" );
		outPos      = new JButton( "Output Positions" );
		
		layout.pumpEvents();
		
		setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder( EtchedBorder.LOWERED ), "Dynamic layout settings", TitledBorder.CENTER, TitledBorder.CENTER ) );
		
		setLayout( new BorderLayout() );
		buttons.setLayout( new java.awt.GridLayout( 1, 2 ) );
		buttons.add( pausePlay );
		buttons.add( shake );
		ioPanel.setLayout( new GridLayout( 1, 2 ) );
		ioPanel.add( outPosField );
		ioPanel.add( outPos );
		sliders.setLayout( new GridLayout( 2, 2 ) );
		sliders.add( new JLabel( "Force :" ) );
		sliders.add( force );
		sliders.add( new JLabel( "Quality :" ) );
		sliders.add( quality );
		add( sliders, BorderLayout.CENTER );
		add( buttons, BorderLayout.SOUTH );
		add( ioPanel, BorderLayout.NORTH );
		
		pausePlay.addActionListener( this );
		shake.addActionListener( this );
		force.addChangeListener( this );
		quality.addChangeListener( this );
		outPos.addActionListener( this );
		
		shake.setToolTipText( "Move randomly nodes do exit a bad layout situation." );
		pausePlay.setToolTipText( "Stop the layout process to free some CPU cycles." );
		force.setToolTipText( "Change the attraction/repulsion forces between nodes." );
		quality.setToolTipText( "Change the quality of the layout, higher qualities are slower." );
		outPos.setToolTipText( "Output the positions/coordinates of each node to the file indicated aside." );
		outPosField.setToolTipText( "Indicate here the name of the file where node coordinates will be output." );
		
		force.setPaintLabels( true );
		force.setPaintTicks( true );
		force.setMajorTickSpacing( forces.length - 1 );
		force.setMinorTickSpacing( 1 );
		force.setSnapToTicks( true );
		force.setValue( closerForceValue( layout.getForce() ) );
		quality.setPaintLabels( true );
		quality.setPaintTicks( true );
		quality.setMajorTickSpacing( 4 );
		quality.setMinorTickSpacing( 1 );
		quality.setBorder( BorderFactory.createEmptyBorder( 4, 0, 0, 0 ) );
		quality.setSnapToTicks( true );
		quality.setValue( layout.getQuality() );
		
		Hashtable<Integer,JLabel> forceLabels = new Hashtable<Integer,JLabel>();
		
		forceLabels.put( new Integer( 0 ), new JLabel( "Epsilon" ) );
		forceLabels.put( new Integer( forces.length / 2 ), new JLabel( Float.toString( forces[forces.length/2] ) ) );
		forceLabels.put( new Integer( forces.length - 1 ), new JLabel( Float.toString( forces[forces.length-1] ) ) );
		force.setLabelTable( forceLabels );
		
		buttons.setBorder( BorderFactory.createEmptyBorder( 4, 4, 4, 4 ) );
		ioPanel.setBorder( BorderFactory.createEmptyBorder( 4, 4, 4, 4 ) );
		sliders.setBorder( BorderFactory.createEmptyBorder( 4, 4, 4, 4 ) );
	}
	
	public void actionPerformed( ActionEvent ev )
	{
		if( ev.getSource() == pausePlay )
		{
			if( paused )
			{
				layout.play();
				paused = false;
				pausePlay.setText( "pause layout" );
			}
			else
			{
				layout.pause();
				paused = true;
				pausePlay.setText( "play layout" );
			}
		}
		else if( ev.getSource() == shake )
		{
			layout.shake();
		}
		else if( ev.getSource() == outPos )
		{
			layout.savePositions( outPosField.getText() );
		}
	}

	public void stateChanged( ChangeEvent ev )
	{
		if( ev.getSource() == force )
		{
			int val = force.getValue();
			
			layout.setForce( forces[val] );
		}
		else if( ev.getSource() == quality )
		{
			int val = quality.getValue();
			
			layout.setQuality( val );
		}
	}
	
	public int closerForceValue( float value )
	{
		for( int i=0; i<forces.length; ++i )
		{
			if( forces[i] >= value )
				return i;
		}
		
		return( forces.length - 1 );
	}
}