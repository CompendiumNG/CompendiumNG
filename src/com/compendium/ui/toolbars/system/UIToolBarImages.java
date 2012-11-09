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

package com.compendium.ui.toolbars.system;

import java.io.File;
import java.awt.*;

import javax.swing.*;

/**
 * This class has method for retrieving toolbar image files.
 *
 * @author	Michelle Bachler
 */
public class UIToolBarImages {

	/** An array of the image file names for the images used by the toolbar when painting.*/
	public static final String IMG_NAMES[] = {
		"toolbar-vertical.gif", "toolbar-horizontal.gif",
		"toolbarup-north.gif", "toolbardown-north.gif", "toolbarup-south.gif", "toolbardown-south.gif",
		"toolbarup-west.gif", "toolbardown-west.gif", "toolbarup-east.gif", "toolbardown-east.gif"
	};

	/** The number of images in the image array.*/
	public static final int NUM_IMAGES						= 10;

	/** The array position of the image file used to paint the vertical toolbar control button.*/
	public static final int TOOLBAR_VERTICAL_ICON			= 0;

	/** The array position of the image file used to paint the horizontal toolbar control button.*/
	public static final int TOOLBAR_HORIZONTAL_ICON			= 1;

	/** The array position of the image file used to paint the toolbar contraction button, when placed north.*/
	public static final int TOOLBAR_UP_NORTH_ICON			= 2;

	/** The array position of the image file used to paint the toolbar expansion button, when placed north.*/
	public static final int TOOLBAR_DOWN_NORTH_ICON			= 3;

	/** The array position of the image file used to paint the toolbar contraction button, when placed south.*/
	public static final int TOOLBAR_UP_SOUTH_ICON			= 4;

	/** The array position of the image file used to paint the toolbar expansion button, when placed south.*/
	public static final int TOOLBAR_DOWN_SOUTH_ICON			= 5;

	/** The array position of the image file used to paint the toolbar contraction button, when placed west.*/
	public static final int TOOLBAR_UP_WEST_ICON			= 6;

	/** The array position of the image file used to paint the toolbar expansion button, when placed west.*/
	public static final int TOOLBAR_DOWN_WEST_ICON			= 7;

	/** The array position of the image file used to paint the toolbar contraction button, when placed east.*/
	public static final int TOOLBAR_UP_EAST_ICON			= 8;

	/** The array position of the image file used to paint the toolbar expansion button, when placed east.*/
	public static final int TOOLBAR_DOWN_EAST_ICON			= 9;

	/**A reference to the system file path separator*/
	private final static String	sFS					= System.getProperty("file.separator");

	/**A reference to the main image directory*/
	private final static String	sPATH 				= "com/compendium/ui/toolbars/system/images/";

	/** An array of loaded images - saves loading an image more than once.*/
	private static ImageIcon img[] = new ImageIcon[NUM_IMAGES];

	/**
	 * Return the appropriate ImageIcon, associated with the given identifier.
	 *
	 * @param int idx, an identifier for the image file required.
	 * @return ImageIcon, the associated ImageIcon.
	 */
	public final static ImageIcon get(int idx) {

		ImageIcon image = img[idx];

		if ( image == null ) {
			//image = new ImageIcon(sPATH + IMG_NAMES[idx]);

			image = new ImageIcon( ClassLoader.getSystemResource(UIToolBarImages.sPATH+UIToolBarImages.IMG_NAMES[idx]) );

			img[idx] = image;
		}
		return image;
	}
}
