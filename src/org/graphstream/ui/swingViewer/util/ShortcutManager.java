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

package org.graphstream.ui.swingViewer.util;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.swingViewer.View;

/**
 * Utility to centralise the shortcuts and actions for all view instances.
 */
public class ShortcutManager implements KeyListener {
	// Attributes

	/**
	 * The viewer to control.
	 */
	protected View view;

	protected float viewPercent = 1;

	protected Point3 viewPos = new Point3();

	protected float rotation = 0;

	// Construction

	/**
	 * New manager operating on the given viewer.
	 * 
	 * @param view
	 *            The graph view to control.
	 */
	public ShortcutManager(View view) {
		this.view = view;
	}

	// Events

	/**
	 * A key has been pressed.
	 * 
	 * @param event
	 *            The event that generated the key.
	 */
	public void keyPressed(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.VK_PAGE_UP) {
			view.setViewPercent(view.getViewPercent() - 0.05f);
		} else if (event.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
			view.setViewPercent(view.getViewPercent() + 0.05f);
		} else if (event.getKeyCode() == KeyEvent.VK_LEFT) {
			if ((event.getModifiers() & KeyEvent.ALT_MASK) != 0) {
				float r = view.getViewRotation();
				view.setViewRotation(r - 5);
			} else {
				float delta = 0;

				if ((event.getModifiers() & KeyEvent.SHIFT_MASK) != 0)
					delta = view.getGraphDimension() * 0.01f;
				else
					delta = view.getGraphDimension() * 0.1f;

				Point3 p = view.getViewCenter();
				view.setViewCenter(p.x + delta, p.y, 0);
			}
		} else if (event.getKeyCode() == KeyEvent.VK_RIGHT) {
			if ((event.getModifiers() & KeyEvent.ALT_MASK) != 0) {
				float r = view.getViewRotation();
				view.setViewRotation(r + 5);
			} else {
				float delta = 0;

				if ((event.getModifiers() & KeyEvent.SHIFT_MASK) != 0)
					delta = view.getGraphDimension() * 0.01f;
				else
					delta = view.getGraphDimension() * 0.1f;

				Point3 p = view.getViewCenter();
				view.setViewCenter(p.x - delta, p.y, 0);
			}
		} else if (event.getKeyCode() == KeyEvent.VK_UP) {
			float delta = 0;

			if ((event.getModifiers() & KeyEvent.SHIFT_MASK) != 0)
				delta = view.getGraphDimension() * 0.01f;
			else
				delta = view.getGraphDimension() * 0.1f;

			Point3 p = view.getViewCenter();
			view.setViewCenter(p.x, p.y + delta, 0);
		} else if (event.getKeyCode() == KeyEvent.VK_DOWN) {
			float delta = 0;

			if ((event.getModifiers() & KeyEvent.SHIFT_MASK) != 0)
				delta = view.getGraphDimension() * 0.01f;
			else
				delta = view.getGraphDimension() * 0.1f;

			Point3 p = view.getViewCenter();
			view.setViewCenter(p.x, p.y - delta, 0);
		}
	}

	/**
	 * A key has been pressed.
	 * 
	 * @param event
	 *            The event that generated the key.
	 */
	public void keyReleased(KeyEvent event) {
	}

	/**
	 * A key has been typed.
	 * 
	 * @param event
	 *            The event that generated the key.
	 */
	public void keyTyped(KeyEvent event) {
		if (event.getKeyChar() == 'R') {
			view.resetView();
		}
		// else if( event.getKeyChar() == 'B' )
		// {
		// view.setModeFPS( ! view.getModeFPS() );
		// }
	}
}