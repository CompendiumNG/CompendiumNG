/********************************************************************************
 *                                                                              *
 *  (c) Copyright 2009 Verizon Communications USA and The Open University UK    *
 *                                                                              *
 *  This software is freely distributed in accordance with                      *
 *  the GNU Lesser General Public (LGPL) license, version 3 or later            *
 *  as published by the Free Software Foundation.                               *
 *  For details see LGPL: http://www.fsf.org/licensing/licenses/lgpl.html       *
 *               and GPL: http://www.fsf.org/licensing/licenses/gpl-3.0.html    *
 *                                                                              *
 *  This software is provided by the copyright holders and contributors "as is" *
 *  and any express or implied warranties, including, but not limited to, the   *
 *  implied warranties of merchantability and fitness for a particular purpose  *
 *  are disclaimed. In no event shall the copyright owner or contributors be    *
 *  liable for any direct, indirect, incidental, special, exemplary, or         *
 *  consequential damages (including, but not limited to, procurement of        *
 *  substitute goods or services; loss of use, data, or profits; or business    *
 *  interruption) however caused and on any theory of liability, whether in     *
 *  contract, strict liability, or tort (including negligence or otherwise)     *
 *  arising in any way out of the use of this software, even if advised of the  *
 *  possibility of such damage.                                                 *
 *                                                                              *
 ********************************************************************************/

package com.compendium.ui;

import java.awt.*;
import javax.swing.*;
import java.beans.PropertyVetoException;

/**
 * This subclass of DefaultDesktopManager is to fix a Mac bug with closing frames.
 *
 * @author Michelle Bachler
 */
public class UIDesktopManager extends DefaultDesktopManager {

    /**
     * Removes the frame, and, if necessary, the
     * <code>desktopIcon</code>, from its parent.
	 *
     * @param f the <code>JInternalFrame</code> to be removed.
     */
    public void closeFrame(JInternalFrame f) {

        boolean findNext = f.isSelected();
		Container c = f.getParent();

		JDesktopPane pane = getDesktopPane((JComponent)f);

		if (findNext)
		    try { f.setSelected(false); } catch (PropertyVetoException e2) { }

        if(c != null) {
            c.remove(f);
            c.repaint(f.getX(), f.getY(), f.getWidth(), f.getHeight());
        }

        removeIconFor(f);

        if(f.getNormalBounds() != null)
            f.setNormalBounds(null);

        if(wasIcon(f))
            setWasIcon(f, null);

		if (findNext)
		    myActivateNextFrame(c, pane);
    }

	/**
	 * When a frame is closed find the next internal frame in the correct order to bring to the front.
	 *
	 * @param c, the container to look in.
	 * @param pane, the associated desktop pane.
	 */
    private void myActivateNextFrame(Container c, JDesktopPane pane) {
		int i;

		JInternalFrame nextFrame = null;
		if (pane == null) {
		    if (c == null)
				return;

		    for (i = 0; i < c.getComponentCount(); i++) {
				if (c.getComponent(i) instanceof JInternalFrame) {
				    nextFrame = (JInternalFrame) c.getComponent(i);
				    break;
				}
			}
		}
		else {
		    JInternalFrame[] frames = pane.getAllFrames();
			if (frames.length > 0)
			    nextFrame = frames[0];
		}

		if (nextFrame != null) {
		    try { nextFrame.setSelected(true); }
		    catch (PropertyVetoException e2) { System.out.println("frame selection failing as ;"+e2.getMessage());}
		    nextFrame.moveToFront();
		}
    }

	/**
	 * Return the desktop pane associated with the given component.
	 *
	 * @param frame, the component to get the JDesktopPane for.
	 * @return JDesktopPane, if found, else null.
	 */
    JDesktopPane getDesktopPane( JComponent frame ) {
        JDesktopPane pane = null;
		Component c = frame.getParent();

        // Find the JDesktopPane
        while ( pane == null ) {
		    if ( c instanceof JDesktopPane ) {
		        pane = (JDesktopPane)c;
		    }
		    else if ( c == null ) {
		        break;
		    }
		    else {
		        c = c.getParent();
		    }
		}

		return pane;
    }
}

