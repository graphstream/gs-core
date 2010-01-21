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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;

import org.graphstream.ui.old.GraphViewer;
import org.graphstream.ui.old.swing.SwingGraphViewer;

public class ScreenShotPane extends JPanel implements ActionListener
{
    private static final long serialVersionUID = 1L;
    
	protected SwingGraphViewer graphViewer;

    public ScreenShotPane( SwingGraphViewer graphViewer )
    {
		this.graphViewer = graphViewer;
		
		setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
		buildScreenShotPanel();
		buildAnimPanel();
		
		add( screenShotPanel );
		add( animPanel );
    }
	
 // Screenshots

	/**
	 * ScreenShot panel.
	 */
	protected JPanel screenShotPanel;
	
	protected JTextField screenShotTextField;

	protected String filename = "screenshot.svg";

	protected String fileFilter = "svg";

	protected JButton screenShot;

	protected JButton screenShotSelector;

	protected JRadioButton JRBOverwrite;

	protected JRadioButton JRBRename;

	protected ButtonGroup group;
	
	protected int count=0;

	/**
	 * The screen shot panel.
	 */
	protected void buildScreenShotPanel()
	{
		screenShotPanel = new JPanel();
		screenShotTextField = new JTextField(filename);
		screenShotSelector = new JButton( "Browse" );
		screenShot = new JButton( "ScreenShot" );

		screenShot.addActionListener( this );
		screenShotSelector.addActionListener( this );

		screenShotPanel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder( EtchedBorder.LOWERED ), "ScreenShot", TitledBorder.CENTER, TitledBorder.CENTER ) );
		
		screenShotTextField.setToolTipText( "The filename of the screenshot !" );
		screenShotSelector.setToolTipText( "Hit that button to define another filename for the screenshot." );
		screenShot.setToolTipText( "Hit that button to produce a screenshot." );
	
		JRBOverwrite =  new JRadioButton("Overwrite existing file");
		JRBOverwrite.setActionCommand("overwrite");
		JRBOverwrite.setSelected( true );
		JRBOverwrite.addActionListener( this );

		JRBRename = new JRadioButton( "Rename new file" );
		JRBRename.setActionCommand( "rename" );
		JRBRename.addActionListener( this );
	
		// Group the radio buttons.
		group = new ButtonGroup();
		group.add( JRBOverwrite );
		group.add( JRBRename );
		
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 1;
		gridBagConstraints2.gridy = 0;
		GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.gridx = 2;
		gridBagConstraints3.gridy = 0;
		GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
		gridBagConstraints4.gridx = 0;
		gridBagConstraints4.gridy = 1;
		GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
		gridBagConstraints5.gridx = 1;
		gridBagConstraints5.gridy = 2;
		gridBagConstraints5.anchor = GridBagConstraints.WEST;
		GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
		gridBagConstraints6.gridx = 1;
		gridBagConstraints6.gridy = 3;
		gridBagConstraints6.anchor = GridBagConstraints.WEST;
	
	
		screenShotPanel.setLayout( new GridBagLayout() );

		screenShotPanel.add( screenShotTextField , gridBagConstraints);
		screenShotPanel.add(screenShotSelector, gridBagConstraints2);
		screenShotPanel.add( screenShot , gridBagConstraints3);
		screenShotPanel.add( new JLabel("If the file exists : "), gridBagConstraints4 );
		screenShotPanel.add( JRBOverwrite, gridBagConstraints5);
		screenShotPanel.add( JRBRename, gridBagConstraints6);
	}

// Animation
	
	/**
	 * The animation panel.
	 */
	protected JPanel animPanel;
	
	/**
	 * The "output" animation button.
	 */
	protected JButton outputAnim;
	
	/**
	 * The "stop" animation button.
	 */
	protected JButton stopAnim;

	/**
	 * The animation base name.
	 */
	protected JTextField animName;
	
	/**
	 * Build the animation panel.
	 */
	protected void buildAnimPanel()
	{
		animPanel = new JPanel();

		animPanel.setLayout( new GridLayout( 2, 2 ) );
		animPanel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder( EtchedBorder.LOWERED ), "Animation", TitledBorder.CENTER, TitledBorder.CENTER ) );
		
		outputAnim = new JButton( "start" );
		stopAnim   = new JButton( "stop" );
		animName   = new JTextField( "anim" );
		
		animPanel.add( new JLabel( "Base name:" ) );
		animPanel.add( animName );
		animPanel.add( outputAnim );
		animPanel.add( stopAnim );
		outputAnim.addActionListener( this );
		stopAnim.addActionListener( this );
		
		animName.setToolTipText( "The base name of the animation, frames will start with this text, followed by six digits and the '.jpg' extension." );
		outputAnim.setToolTipText( "Start the output of image frames in order to build an animation." );
		stopAnim.setToolTipText( "Stop the output of image frames." );
	}
    
	public void actionPerformed( ActionEvent arg0 )
    {
		if( screenShot == arg0.getSource() )
		{
			filename = screenShotTextField.getText();
			
			if( group.getSelection().getActionCommand().equals("rename"))
			{
				File f = new File(filename);
				
				while(f.exists())
				{
					int point = filename.lastIndexOf( "." );
					f = new File(filename.substring( 0,point )+(count++)+filename.substring( point ));
					System.out.println(f.getAbsoluteFile());
				}
				
				graphViewer.getRenderer().screenshot( f.getAbsolutePath() );
			}
			else
			{
				graphViewer.getRenderer().screenshot( filename );
			}
		}

		else if( screenShotSelector == arg0.getSource() )
		{
			
			JFileChooser jfc = new JFileChooser( new File( "." ) );
			jfc.setDialogTitle( "Choose a filename for the screenshot..." );
			//jfc.setSelectedFile( new File( "screenshot.svg" ) );
			for( GraphViewer.ScreenshotType e: graphViewer.getScreenShotTypes() )
			{
				FileFilter mff = new MyFileFilter( e );
				jfc.addChoosableFileFilter( mff );
			}

			int returnVal = jfc.showDialog( null, "Ok" );
			if( returnVal == JFileChooser.APPROVE_OPTION )
			{
				File file = jfc.getSelectedFile();
				filename = file.getAbsolutePath();
				fileFilter = jfc.getFileFilter().toString();
				if(filename.lastIndexOf( "." ) < 0  || ! filename.endsWith( fileFilter ))
					filename = filename+"."+fileFilter;
				screenShotTextField.setText( filename );
			}

		}
		else if( outputAnim == arg0.getSource() )
		{
			graphViewer.getRenderer().outputFrames( animName.getText() );
		}
		else if( stopAnim == arg0.getSource() )
		{
			graphViewer.getRenderer().outputFrames( null );
		}
    }
	
// Nested classes

	public class MyFileFilter extends FileFilter
	{

		GraphViewer.ScreenshotType type;

		public MyFileFilter( GraphViewer.ScreenshotType e )
		{
			this.type = e;
		}

		@Override
		public boolean accept( File arg0 )
		{
			if( arg0.getPath().endsWith( type.toString() ) || arg0.isDirectory() )
				return true;
			return false;
		}

		@Override
		public String getDescription()
		{
			return type.getTag();
		}

		@Override
		public String toString()
		{
			return type.toString();
		}
	}
}