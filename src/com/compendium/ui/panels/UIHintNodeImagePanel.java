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

package com.compendium.ui.panels;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.awt.image.*;

import javax.swing.*;
import javax.swing.border.*;

import com.compendium.ui.*;
import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;
import com.compendium.core.datamodel.Code;
import com.compendium.core.datamodel.NodeSummary;


/**
 * Displays a node image full size.
 *
 * @author	Michelle Bachler
 */
public class UIHintNodeImagePanel extends JPanel {

	/**
	 * Constructor. Loads the given node's image, and paints it in this panel.
	 *
	 * @param NodeSummary node, the node whose image to put in this panel.
	 * @param int xPos, the x position to draw this panel.
	 * @param int yPos, the y position to draw this panel.
	 * @param UIViewFrame frame, used to get the visible size if the current frame.
	 */
	public UIHintNodeImagePanel(NodeSummary node, int xPos, int yPos, UIViewFrame frame) {

		setLocation(xPos, yPos);
		setBorder(new LineBorder(Color.gray, 1));
		setBackground(Color.white);

		String ref = node.getImage();
		if (ref == null || ref.equals("")) { //$NON-NLS-1$
			ref = node.getSource();
		}

		Dimension initialdim = frame.getViewport().getExtentSize();

		// to allow for scrollbars and additions border on this panel
		Dimension dim = new Dimension(initialdim.width-22, initialdim.height-22);

		JLabel label = new JLabel();

		if (FormatProperties.scaleImageRollover) {

			if ( UIImages.isImage(ref) ) {

				ImageIcon icon = UIImages.createImageIcon(ref);
				if (icon.getImageLoadStatus() != MediaTracker.ERRORED) {
					Image inImage = icon.getImage();

					int imgWidth = inImage.getWidth(null) ;
					int imgHeight = inImage.getHeight(null) ;

					if (imgWidth > dim.width || imgHeight > dim.height ) {
						double scale = 0;
						double scaleWidth = scale = (double)dim.width/(double)imgWidth;
						double scaleHeight = scale = (double)dim.height/(double)imgHeight;

						if (scaleWidth < scaleHeight)
							scale = scaleWidth;
						else
							scale = scaleHeight;

						// Determine size of new image.
						//One of them should equal maxDim.
						int scaledW = (int)(scale*imgWidth);
						int scaledH = (int)(scale*imgHeight);

						// create thumbnail of image
						//Image thumbimage = Toolkit.getDefaultToolkit().getImage(ref);

						ImageFilter filter = new AreaAveragingScaleFilter(scaledW, scaledH);
						FilteredImageSource filteredSource = new FilteredImageSource((ImageProducer)inImage.getSource(), filter);
						JLabel comp = new JLabel();
						inImage = comp.createImage(filteredSource);

						// update icon here
						icon = new ImageIcon(inImage);
						label.setIcon(icon);
					}
					else {
						label.setIcon(icon);
					}
				} else {
					label.setText(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UIHintNodeImagePanel.unableToDisplay")); //$NON-NLS-1$
				}
			}
			else {
				label.setText(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UIHintNodeImagePanel.unsupportedFormat")); //$NON-NLS-1$
			}
		}
		else {
			ImageIcon icon = UIImages.createImageIcon(ref);
			if (icon.getImageLoadStatus() != MediaTracker.ERRORED) {
				label.setIcon(icon);
			} else {
				label.setText(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UIHintNodeImagePanel.unableToDisplay")); //$NON-NLS-1$
			}
		}

		add(label);
		setSize(getPreferredSize());
        validate();
	}
}
