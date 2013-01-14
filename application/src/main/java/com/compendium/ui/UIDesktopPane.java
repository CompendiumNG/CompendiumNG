package com.compendium.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.beans.PropertyVetoException;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;


/**
 * This class handles setting scroll bars for when windows move too far to the left or
 * bottom, providing the DesktopPane is in a ScrollPane.
 * @author Gerald Nunn / Michelle Bachler
 */
//CURRENTLY NOT USED
//Was working with additional methods in UIDesktopPane (commented out) to try and implement a scrollable desktop.
//It was buggy and there was not time to fix, so it was removed for now.
//Will come back to it later.
public class UIDesktopPane extends JDesktopPane {
    private static int FRAME_OFFSET=20;
    private UIDesktopManager manager;

    public UIDesktopPane() {
        manager=new UIDesktopManager(this);
        setDesktopManager(manager);
        //setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
    }

    public void setBounds(int x, int y, int w, int h) {
        super.setBounds(x,y,w,h);
        checkDesktopSize();
    }

    public Component add(JInternalFrame frame) {
        JInternalFrame[] array = getAllFrames();
        Point p;
        int w;
        int h;

        Component retval=super.add(frame);
        checkDesktopSize();
        if (array.length > 0) {
            p = array[0].getLocation();
            p.x = p.x + FRAME_OFFSET;
            p.y = p.y + FRAME_OFFSET;
        }
        else {
            p = new Point(0, 0);
        }
        frame.setLocation(p.x, p.y);
        if (frame.isResizable()) {
            w = getWidth() - (getWidth()/3);
            h = getHeight() - (getHeight()/3);
            if (w < frame.getMinimumSize().getWidth()) { 
            	w = (int)frame.getMinimumSize().getWidth();
            }
            if (h < frame.getMinimumSize().getHeight()) {
            	h = (int)frame.getMinimumSize().getHeight();
            }
            frame.setSize(w, h);
        }
        moveToFront(frame);
        frame.setVisible(true);
        try {
            frame.setSelected(true);
        } catch (PropertyVetoException e) {
            frame.toBack();
        }
        return retval;
    }

    public void remove(Component c) {
        super.remove(c);
        checkDesktopSize();
    }

    /**
     * Sets all component size properties ( maximum, minimum, preferred)
     * to the given dimension.
     */
    public void setAllSize(Dimension d){
        setMinimumSize(d);
        setMaximumSize(d);
        setPreferredSize(d);
    }

    /**
     * Sets all component size properties ( maximum, minimum, preferred)
     * to the given width and height.
     */
    public void setAllSize(int width, int height){
        setAllSize(new Dimension(width,height));
    }

    private void checkDesktopSize() {
        //if (getParent()!=null&&isVisible()) manager.resizeDesktop();
    }
}
