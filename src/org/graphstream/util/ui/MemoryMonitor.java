/*
 * Copyright 2006 - 2011 
 *     Stefan Balev 	<stefan.balev@graphstream-project.org>
 *     Julien Baudry	<julien.baudry@graphstream-project.org>
 *     Antoine Dutot	<antoine.dutot@graphstream-project.org>
 *     Yoann Pigné		<yoann.pigne@graphstream-project.org>
 *     Guilhelm Savin	<guilhelm.savin@graphstream-project.org>
 * 
 * This file is part of GraphStream <http://graphstream-project.org>.
 * 
 * GraphStream is a library whose purpose is to handle static or dynamic
 * graph, create them from scratch, file or any source and display them.
 * 
 * This program is free software distributed under the terms of two licenses, the
 * CeCILL-C license that fits European law, and the GNU Lesser General Public
 * License. You can  use, modify and/ or redistribute the software under the terms
 * of the CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
 * URL <http://www.cecill.info> or under the terms of the GNU LGPL as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C and LGPL licenses and that you accept their terms.
 */
	package org.graphstream.util.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public class MemoryMonitor extends Thread implements ActionListener
{

	private boolean die = false;
//	private JLabel memoire;
	private JButton b;
	private UIMemoryPanel xp;

	public MemoryMonitor()
	{

		// window
		JFrame window = new JFrame("Memory Monitor");
		window.setSize(150, 250);

		window.setFont(new Font("Vernada", Font.PLAIN, 9));

		// Panel m�moire
		JPanel panelMemoire = new JPanel();
		panelMemoire.setFont(new Font("Vernada", Font.PLAIN, 9));

		panelMemoire.setBorder(new CompoundBorder(new TitledBorder(null,
				"Memory Monitor", TitledBorder.LEFT, TitledBorder.TOP),
				new EmptyBorder(4, 4, 4, 4)));
		JPanel pm = new JPanel(new GridLayout(2, 1));
//		memoire = new JLabel();
//		memoire.setFont(new Font("Vernada", Font.PLAIN, 9));
//
//		pm.add(memoire);
		// BOUTON
		b = new JButton("Garbage");
		b.setFont(new Font("Vernada", Font.PLAIN, 9));

		b.addActionListener(this);
		pm.add(b);
		xp = new UIMemoryPanel();
		panelMemoire.add(pm);
		panelMemoire.add(xp);

		window.getContentPane().add(panelMemoire);
		window.setVisible(true);
		window.getContentPane().setVisible(true);
		// window.show();

		// Pour ferner l'application avec la petite croix.
		window.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e)
			{
				die = true;
				System.exit(0);
			}
		});

	}

	public void actionPerformed(ActionEvent ae)
	{
		if(ae.getSource() == b){
			// System.out.println("Appel au !GC! ");
			Runtime.getRuntime().gc();
		}
	}

	@Override
	public void run()
	{
		java.lang.Runtime r = Runtime.getRuntime();
		int mem;
		while(!die){
			try{
				
//				for(int j = 1; j < xp.tailleData; j++){
//					xp.data[j - 1] = xp.data[j];
//				}
//				xp.data[xp.tailleData - 1] = (int)((r.totalMemory() - r
//						.freeMemory()) / 1024);
				mem= (int)((r.totalMemory() - r
						.freeMemory()) / 1024);
				//memoire.setText(	mem+ " kB");
				xp.data[xp.dataPointer]=mem;
				xp.dataPointer = (xp.dataPointer +1)%xp.tailleData;
				xp.repaint();
				
				sleep(300);
			}catch (InterruptedException e){
			}
		}
		System.exit(0);

	}

	public void die()
	{
		die = true;
		// xp.die();
	}

	public static void main(String[] args)
	{

		MemoryMonitor frame = new MemoryMonitor();
		frame.start();
	}

	public static void show()
	{
		MemoryMonitor frame = new MemoryMonitor();
		frame.start();
	}

}


class UIMemoryPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5918194415533545847L;
	public int[] data = {};
	public int dataPointer = 0;
	public int tailleData = 100;
	public int dimensionX = 120;
	public int dimensionY = 70;

	public int maxMemory;
	// public JLabel lmax;
	// public JLabel lmin;

	private int initialMinMemory;
	private int initialMaxMemory;

	private int tailleRect;
	private int nbRect;

	public UIMemoryPanel() {
		super();
		setOpaque(true);
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createLineBorder(Color.black));
		setBackground(Color.white);
		setForeground(Color.blue);
		setPreferredSize(new Dimension(dimensionX, dimensionY + 65));
		setDoubleBuffered(true);
		setFont(new Font("Vernada", Font.PLAIN, 9));

		// lmax= new JLabel("");
		// lmax.setFont(
		data = new int[tailleData];
		for (int i = 0; i < tailleData; i++) {
			data[i] = 0;
		}
		initialMinMemory = Integer.MAX_VALUE;
		initialMaxMemory = 0;

		maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		// System.out.println("maxMemory=" + maxMemory);

		tailleRect = 8;
		nbRect = (int) ((dimensionX - 4) / tailleRect);
		tailleRect = (int) Math.round((dimensionX - 4) / (double) nbRect);
		// System.out.println("nbRect = "+nbRect+" tailleRect = "+tailleRect);
	}

	@Override
	public void paintComponent(Graphics g) {
		// Graphics mg = getGraphics();
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		super.paintComponent(g);
		int minMem = initialMinMemory;
		int maxMem = initialMaxMemory;
		// recherhce des bornes hautes et basses du graphe.
		for (int i = 0; i < tailleData; i++) {
			// System.out.print(data[i]+",");
			if (minMem > data[i]) {
				minMem = data[i];
			}
			if (maxMem < data[i]) {
				maxMem = data[i];
			}
		}

		g2.setColor(new Color(150, 70, 90));
		// System.out.println(" ->Min:"+minMem+" Max: "+maxMem);
		int i;
		for (int k = 0; k < tailleData - 1; k++) {
			i = (dataPointer + k) % tailleData;
			int x1 = (int) (dimensionX * (k) / tailleData);
			int y1 = dimensionY
					+ 10
					- (int) ((double) ((double) dimensionY / (double) (maxMem - minMem)) * (double) (data[i] - minMem));
			int x2 = (int) (dimensionX * (k + 1) / tailleData);
			// System.out.println("x2="+x2);
			int y2 = dimensionY
					+ 10
					- (int) ((double) ((double) dimensionY / (double) (maxMem - minMem)) * (double) (data[(i + 1)
							% tailleData] - minMem));
			// System.out.print("("+x1+" "+y1+" "+x2+" "+y2+") ");
			g2.drawLine(x1, y1, x2, y2);
		}
		g2.setColor(Color.black);
		g2.drawString(String.format(new Locale("US"), "%d", maxMem), 5, 10);
		g2.drawString(Integer.toString(minMem), 5, dimensionY + 20);

		// une ligne horizontale
		g2.drawLine(0, dimensionY + 25, dimensionX, dimensionY + 25);

		// 0 Mo --- 63.34 Mo
		g2.drawString("0 Mo", 5, dimensionY + 40);
		String mo64 = String.format(getLocale(), "%d Mo", (maxMemory / 1024));
		g2.drawString(mo64, dimensionX - 30, dimensionY + 40);

		// rectangles
		// System.out.println("nbRect="+nbRect);
		// System.out.println("truc="+data[(dataPointer-1+tailleData)%tailleData]
		// /(double)maxMemory);
		int nbRectToDraw = (int) Math
				.ceil(nbRect
						* ((double) (data[(dataPointer - 1 + tailleData)
								% tailleData] / (double) maxMemory)));
		// System.out.println("nbRectToDraw="+nbRectToDraw);
		g2.setColor(new Color(150, 170, 190));
		for (i = 0; i < nbRectToDraw; i++) {
			g2.fillRoundRect(3 + (i * tailleRect), dimensionY + 46,
					tailleRect - 3, tailleRect + 2, 4, 4);
			g2.setColor(new Color(50, 70, 90));
			g2.drawRoundRect(3 + (i * tailleRect), dimensionY + 46,
					tailleRect - 3, tailleRect + 2, 4, 4);
			g2.setColor(new Color(150, 170, 190));
			
		}
		g2.setColor(new Color(50, 70, 90));
		for (; i < nbRect; i++) {
			g2.drawRoundRect(3 + (i * tailleRect), dimensionY + 46,
					tailleRect - 3, tailleRect + 2, 4, 4);
		}
		// System.out.println();
	}
}

