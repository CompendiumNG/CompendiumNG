/********************************************************************************
 *                                                                              *
 *  (c) Copyright 2010 Verizon Communications USA and The Open University UK    *
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
 * Also it has code from Gerald Nunn to activate scrollbars when frame dragged off right/bottom
 *
 * @author Michelle Bachler /  Gerald Nunn
 */
public class UIDesktopManager extends DefaultDesktopManager {

	//private UIDesktopPane desktop;
	private JDesktopPane desktop;
	
	public UIDesktopManager(JDesktopPane desktop) {
        this.desktop = desktop;
    }

	// This is called anytime a frame is moved. This
	// implementation keeps the frame from leaving the desktop.
	public void dragFrame(JComponent f, int x, int y) {
		if (f instanceof JInternalFrame) { // Deal only w/internal frames
			JInternalFrame frame = (JInternalFrame) f;
			JDesktopPane desk = frame.getDesktopPane();
			Dimension d = desk.getSize();

			// Nothing all that fancy below, just figuring out how to adjust
			// to keep the frame on the desktop.
			if (x < 0) { // too far left?
				x = 0; // flush against the left side
			} else {
				if (x + frame.getWidth() > d.width) { // too far right?
					x = d.width - frame.getWidth(); // flush against right side
				}
			}
			if (y < 0) { // too high?
				y = 0; // flush against the top
			} else {
				if (y + frame.getHeight() > d.height) { // too low?
					y = d.height - frame.getHeight(); // flush against the
					// bottom
				}
			}
		}

		// Pass along the (possibly cropped) values to the normal drag handler.
		super.dragFrame(f, x, y);
	}
 
// CODE that worked alon with UIDesktopPane To try and implement and scrollable desktop
// Was buggy and so removed and dragFrame method added until more time to fix.
	
    /*public void endResizingFrame(JComponent f) {
        super.endResizingFrame(f);
        //resizeDesktop();
    }

    public void endDraggingFrame(JComponent f) {
        super.endDraggingFrame(f);
        //resizeDesktop();
    }

    public void setNormalSize() {
        JScrollPane scrollPane=getScrollPane();
        int x = 0;
        int y = 0;
        Insets scrollInsets = getScrollPaneInsets();

        if (scrollPane != null) {
            Dimension d = scrollPane.getVisibleRect().getSize();
            if (scrollPane.getBorder() != null) {
               d.setSize(d.getWidth() - scrollInsets.left - scrollInsets.right,
                         d.getHeight() - scrollInsets.top - scrollInsets.bottom);
            }

            d.setSize(d.getWidth() - 20, d.getHeight() - 20);
            desktop.setAllSize(x,y);
            scrollPane.invalidate();
            scrollPane.validate();
        }
    }

    private Insets getScrollPaneInsets() {
        JScrollPane scrollPane=getScrollPane();
        if (scrollPane==null) return new Insets(0,0,0,0);
        else return getScrollPane().getBorder().getBorderInsets(scrollPane);
    }

    private JScrollPane getScrollPane() {
        if (desktop.getParent() instanceof JViewport) {
            JViewport viewPort = (JViewport)desktop.getParent();
            if (viewPort.getParent() instanceof JScrollPane)
                return (JScrollPane)viewPort.getParent();
        }
        return null;
    }

    protected void resizeDesktop() {
    	int x = 0;
        int y = 0;
        JScrollPane scrollPane = getScrollPane();
        Insets scrollInsets = getScrollPaneInsets();

        if (scrollPane != null) {
            JInternalFrame allFrames[] = desktop.getAllFrames();
            for (int i = 0; i < allFrames.length; i++) {
                if (allFrames[i].getX()+allFrames[i].getWidth()> x) {
                    x = allFrames[i].getX() + allFrames[i].getWidth();
                }
                if (allFrames[i].getY()+allFrames[i].getHeight()>y) {
                    y = allFrames[i].getY() + allFrames[i].getHeight();
                }
            }
            Dimension d=scrollPane.getVisibleRect().getSize();
            if (scrollPane.getBorder() != null) {
               d.setSize(d.getWidth() - scrollInsets.left - scrollInsets.right,
                         d.getHeight() - scrollInsets.top - scrollInsets.bottom);
            }

            if (x <= d.getWidth()) {
            	x = ((int)d.getWidth()) - 20;
            }
            if (y <= d.getHeight()) { 
            	y = ((int)d.getHeight()) - 20;
            }
            desktop.setAllSize(x,y);
            scrollPane.invalidate();
            scrollPane.validate();
        }
    }*/
    
    
// ORIGINAL MANAGER CODE 
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
		    catch (PropertyVetoException e2) { System.out.println("frame selection failing as ;"+e2.getMessage());} //$NON-NLS-1$
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

