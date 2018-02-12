/*
 * Copyright 2006 - 2016
 *     Stefan Balev     <stefan.balev@graphstream-project.org>
 *     Julien Baudry    <julien.baudry@graphstream-project.org>
 *     Antoine Dutot    <antoine.dutot@graphstream-project.org>
 *     Yoann Pigné      <yoann.pigne@graphstream-project.org>
 *     Guilhelm Savin   <guilhelm.savin@graphstream-project.org>
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
package org.graphstream.ui.view.util;

import org.graphstream.ui.graphicGraph.GraphicElement;

import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;

public class MouseOverMouseManager extends DefaultMouseManager {

    private GraphicElement hoveredElement;

    private long hoveredElementLastChanged;

    private ReentrantLock hoverLock = new ReentrantLock();

    private Timer hoverTimer = new Timer(true);

    private HoverTimerTask latestHoverTimerTask;

    private final long delay;

    /**
     * @param delay The mouse needs to stay on an element for at least this amount of milliseconds, until the element
     *              gets the attribute "ui.mouseOver" assigned. A value smaller or equal to zero indicates, that
     *              the attribute is assigned without delay.
     */
    public MouseOverMouseManager(final long delay) {
        super();
        this.delay = delay;
    }

    public MouseOverMouseManager() {
        this(1000);
    }

    protected void mouseOverElement(GraphicElement element) {
        element.setAttribute("ui.mouseOver");
    }

    protected void mouseLeftElement(GraphicElement element) {
    	this.hoveredElement = null;
        element.removeAttribute("ui.mouseOver");
    }

    public void mouseMoved(MouseEvent event) {
        try {
            hoverLock.lockInterruptibly();
            boolean stayedOnElement = false;
            GraphicElement currentElement = view.findGraphicElementAt(getManagedTypes(),event.getX(), event.getY());
            if (hoveredElement != null) {
                stayedOnElement = currentElement == null ? false : currentElement.equals(hoveredElement);
                if (!stayedOnElement && hoveredElement.hasAttribute("ui.mouseOver")) {
                    mouseLeftElement(hoveredElement);
                }
            }
            if (!stayedOnElement && currentElement != null) {
                if (delay <= 0) {
                    mouseOverElement(currentElement);
                } else {
                    hoveredElement = currentElement;
                    hoveredElementLastChanged = event.getWhen();
                    if (latestHoverTimerTask != null) {
                        latestHoverTimerTask.cancel();
                    }
                    latestHoverTimerTask = new HoverTimerTask(hoveredElementLastChanged, hoveredElement);
                    hoverTimer.schedule(latestHoverTimerTask, delay);
                }
            }

        } catch(InterruptedException iex) {
            // NOP
        } finally {
            hoverLock.unlock();
        }

    }

    private final class HoverTimerTask extends TimerTask {

        private final long lastChanged;

        private final GraphicElement element;

        public HoverTimerTask(long lastChanged, GraphicElement element) {
            this.lastChanged = lastChanged;
            this.element = element;
        }

        @Override
        public void run() {
            try {
                hoverLock.lock();
                if (hoveredElementLastChanged == lastChanged) {
                    mouseOverElement(element);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                hoverLock.unlock();
            }
        }
    }
}
