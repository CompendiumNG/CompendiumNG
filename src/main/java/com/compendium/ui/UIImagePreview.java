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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;

import com.compendium.ui.UIImages;

import java.io.File;

/**
 * Show a preview of the select image, if the current file is an image.
 * @author Michelle Bachler
 *
 */
public class UIImagePreview extends JComponent
                          implements PropertyChangeListener {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	ImageIcon thumbnail = null;
    File file = null;

    public UIImagePreview(JFileChooser fc) {
        setPreferredSize(new Dimension(100, 50));
        fc.addPropertyChangeListener(this);
    }

    public void loadImage() {
		thumbnail = null;

		if (file == null)
            return;

 		if (!UIImages.isImage(file.getName())) {
			return;
		}

        ImageIcon tmpIcon = new ImageIcon(file.getPath());
        if (tmpIcon.getIconWidth() > 90) {
            thumbnail = new ImageIcon(tmpIcon.getImage().
                                      getScaledInstance(90, -1,
                                                        Image.SCALE_DEFAULT));
        } else {
            thumbnail = tmpIcon;
        }
    }

    public void propertyChange(PropertyChangeEvent e) {
        String prop = e.getPropertyName();
        if (prop.equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)) {
            file = (File) e.getNewValue();
            if (isShowing()) {
                loadImage();
                repaint();
            }
        }
    }

    public void paintComponent(Graphics g) {
    	super.paintComponent(g);  
        
    	if (thumbnail == null) {
            loadImage();
        }
        if (thumbnail != null) {
            int x = getWidth()/2 - thumbnail.getIconWidth()/2;
            int y = getHeight()/2 - thumbnail.getIconHeight()/2;

            if (y < 0) {
                y = 0;
            }

            if (x < 5) {
                x = 5;
            }
            thumbnail.paintIcon(this, g, x, y);
        } 
    }
}
