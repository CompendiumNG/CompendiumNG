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

import java.io.*;
import java.awt.*;
import java.awt.image.*;
import java.net.*;

import javax.swing.*;

import com.compendium.core.ICoreConstants;
import com.compendium.*;

/**
 * This class has method for retrieving image files or references from various directories.
 *
 * @author	Mohammed Sajid Ali / Michelle Bachler
 */
public class UIImages implements IUIConstants {

	/** A reference to the system file path separator.*/
	private final static String	sFS						= System.getProperty("file.separator");

	/** A reference to the main image directory.*/
	private final static String	sPATH 					= "System"+sFS+"resources"+sFS+"Images"+sFS;

	/** A reference to the main image directory*/
	private final static String	sMACPATH 				= sPATH+"Mac"+sFS;

	/** A reference to the skins default directory.*/
	private final static String sDEFAULTNODEPATH		= "Skins"+sFS+"Default"+sFS;

	/** A reference to the main skins directory.*/
	private final static String	sNODEPATH 				= "Skins"+sFS;

	/** A reference to the reference node image directory.*/
	private final static String sREFERENCEPATH			= "System"+sFS+"resources"+sFS+"ReferenceNodeIcons"+sFS;

	/** A reference to the reference node image directory on the Mac.*/
	private final static String sMACREFERENCEPATH		= sREFERENCEPATH+"Mac"+sFS;

	/** Maximum dimension for a reference node image when a graphic is specified.*/
	public final static int MAX_DIM = 96;

	/** The array of images returned so far this session.*/
	private static ImageIcon img[] = new ImageIcon[IUIConstants.NUM_IMAGES];

	/** The array of mac specific images returned so far this session.*/
	private static ImageIcon macImg[] = new ImageIcon[IUIConstants.NUM_IMAGES];

	/**
	 * Return the appropriate ImageIcon, associated with the given identifier for this platform.
	 * @param int idx, an identifier for the image file required.
	 * @return the relevant ImageIcon
	 * @see IUIConstants
	 */
	public final static ImageIcon get(int idx) {
	    ImageIcon image = null;

	    if (ProjectCompendium.isMac && FormatProperties.currentLookAndFeel.equals("apple.laf.AquaLookAndFeel")) {
			image = macImg[idx];
			if (image == null) {
			    image = new ImageIcon(sMACPATH + IMG_NAMES[idx]);
			    macImg[idx] = image;
			}
		}
		else {
			image = img[idx];
			if (image == null) {
			    image = new ImageIcon(sPATH + IMG_NAMES[idx]);
			    img[idx] = image;
			}
		}

	    return image;
	}

	/**
	 * Return the path string for the given identifier for non-platofrm specific images.
	 * @param int idx, an identifier for the image file required.
	 * @return the String of the image path.
	 * @see IUIConstants
	 */
	public final static String getPathString(int idx) {

		return sPATH + IMG_NAMES[idx];
	}

	/**
	 * Return the path associated with the given reference type.
	 * @param int idx, an identifier for the reference type.
	 * @return a String representing relevant file path.
	 * @see IUIConstants
	 */
	public final static String getReferencePath(int idx) {

		String refPath = sREFERENCEPATH + IMG_NAMES[idx];

	    if (ProjectCompendium.isMac) {
			refPath = sMACREFERENCEPATH + IMG_NAMES[idx];
			File file = new File(refPath);
			if (!file.exists()) {
				refPath = sREFERENCEPATH + IMG_NAMES[idx];
			}
		}

		return refPath;
	}

	/**
	 * Return the path associated with the given reference file.
	 * @param refString the file name.
	 * @param sDefault the default file name.
	 * @param bSmallIcon true to return the small version of the node icon, false to return the standard size
	 * @return a String representing relevant file path.
	 * @see IUIConstants
	 */
	public final static String getReferencePath(String refString, String sDefault, boolean bSmallIcon) {				
		if (bSmallIcon) {
			return UIReferenceNodeManager.getSmallReferenceIconPath(refString, sDefault);
		}
		else {
			return UIReferenceNodeManager.getReferenceIconPath(refString, sDefault);
		}
	}

	/**
	 * Return the small ImageIcon associated with the given reference file.
	 * @param String refString, the file name.
	 * @param String sDefault, the default file name.
	 * @return a String representing relevant file path.
	 * @see IUIConstants
	 */
	public final static String getReferenceSmallPath(String refString, String sDefault) {
		return UIReferenceNodeManager.getSmallReferenceIconPath(refString, sDefault);
	}


	/**
	 * Used to get the reference icons for right-click menus.
	 * @param refString, the name of the image to return the icon for.
	 * @return ImageIcon, the icon for the given image exctension type.
	 */
	public static ImageIcon getSmallReferenceIcon(String refString) {
		return UIReferenceNodeManager.getSmallReferenceIcon(refString);
	}

	/**
	 * Return the ImageIcon associated with the given reference type.
	 * @param int idx, an identifier for the reference type.
	 * @return the relevant ImageIcon.
	 * @see IUIConstants
	 */
	public final static ImageIcon getReferenceIcon(int idx) {

		ImageIcon image = img[idx];
		if ( image == null ) {
			image = new ImageIcon( getReferencePath(idx) );
			img[idx] = image;
		}
		return image;
	}

	/**
	 * Return the standard size icon for the given node type.
	 * @param type, the node type to return the icon for.
	 * @return ImageIcon, the icon for the given node type.
	 */
	public static ImageIcon getNodeImage(int type) {

	    ImageIcon img = null;
	    switch (type) {
		case ICoreConstants.ISSUE:
		    img = getNodeIcon(IUIConstants.ISSUE_ICON);
		    break;

		case ICoreConstants.POSITION:
		    img = getNodeIcon(IUIConstants.POSITION_ICON);
		    break;

		case ICoreConstants.ARGUMENT:
		    img = getNodeIcon(IUIConstants.ARGUMENT_ICON);
		    break;

		case ICoreConstants.REFERENCE:
			img = getNodeIcon(IUIConstants.REFERENCE_ICON);
		    break;

		case ICoreConstants.DECISION:
		    img = getNodeIcon(IUIConstants.DECISION_ICON);
		    break;

		case ICoreConstants.NOTE:
		    img = getNodeIcon(IUIConstants.NOTE_ICON);
		    break;

		case ICoreConstants.MAPVIEW:
	    	img = getNodeIcon(IUIConstants.MAP_ICON);
		    break;

		case ICoreConstants.LISTVIEW:
		    img = getNodeIcon(IUIConstants.LIST_ICON);
		    break;

		case ICoreConstants.PRO:
		    img = getNodeIcon(IUIConstants.PRO_ICON);
		    break;

		case ICoreConstants.CON:
		    img = getNodeIcon(IUIConstants.CON_ICON);
		    break;

		case ICoreConstants.ISSUE_SHORTCUT:
		    img = getNodeIcon(IUIConstants.ISSUE_SHORTCUT_ICON);
		    break;

		case ICoreConstants.POSITION_SHORTCUT:
		    img = getNodeIcon(IUIConstants.POSITION_SHORTCUT_ICON);
		    break;

		case ICoreConstants.ARGUMENT_SHORTCUT:
		    img = getNodeIcon(IUIConstants.ARGUMENT_SHORTCUT_ICON);
		    break;

		case ICoreConstants.REFERENCE_SHORTCUT:
		    img = getNodeIcon(IUIConstants.REFERENCE_SHORTCUT_ICON);
		    break;

		case ICoreConstants.DECISION_SHORTCUT:
		    img = getNodeIcon(IUIConstants.DECISION_SHORTCUT_ICON);
		    break;

		case ICoreConstants.NOTE_SHORTCUT:
		    img = getNodeIcon(IUIConstants.NOTE_SHORTCUT_ICON);
		    break;

		case ICoreConstants.MAP_SHORTCUT:
		    img = getNodeIcon(IUIConstants.MAP_SHORTCUT_ICON);
		    break;

		case ICoreConstants.LIST_SHORTCUT:
		    img = getNodeIcon(IUIConstants.LIST_SHORTCUT_ICON);
		    break;

		case ICoreConstants.PRO_SHORTCUT:
		    img = getNodeIcon(IUIConstants.PRO_SHORTCUT_ICON);
		    break;

		case ICoreConstants.CON_SHORTCUT:
		    img = getNodeIcon(IUIConstants.CON_SHORTCUT_ICON);
		    break;

		case ICoreConstants.TRASHBIN:
		    img = getNodeIcon(IUIConstants.TRASHBIN_ICON);
		    break;
	    }
	    return img;
	}
	
	/**
	 * Return the ImageIcon associated with the given node type.
	 * @param int idx, tand identifier for the node type.
	 * @return the relevant ImageIcon.
	 * @see IUIConstants
	 */
	public final static ImageIcon getNodeIcon(int idx) {

		String sPath = "";
		String skin = FormatProperties.skin;

		if (skin.equals("Default") || skin.equals("Default_Mini")) {
			sPath = sNODEPATH+skin+sFS+DEFAULT_IMG_NAMES[idx];
		}
		else {
			sPath = sNODEPATH+skin+sFS+IMG_NAMES[idx];
		}

		File fileCheck = new File(sPath);
		if (!fileCheck.exists())
			sPath = sDEFAULTNODEPATH+sFS+DEFAULT_IMG_NAMES[idx];

		fileCheck = new File(sPath);
		if (!fileCheck.exists())
			sPath = sPATH+DEFAULT_IMG_NAMES[idx];

		ImageIcon image = new ImageIcon(sPath);

		return image;
	}

	/**
	 * Return the path of the given icon file.
	 * @param int idx, The node type.
	 * @param bSmallIcon true for returning the small version of the node icon, false for the standard size.
	 * @return a String representing the path to the given icon file.
	 * @see IUIConstants
	 */
	public final static String getPath(int idx, boolean bSmallIcon) {

		int type = 0;

		if (bSmallIcon) {
			return getSmallPath(idx);
		}
		else {
			switch (idx) {
				case ICoreConstants.ISSUE: {
					type = IUIConstants.ISSUE_ICON;
					break;
				}
				case ICoreConstants.POSITION: {
					type = IUIConstants.POSITION_ICON;
					break;
				}
				case ICoreConstants.ARGUMENT: {
					type = IUIConstants.ARGUMENT_ICON;
					break;
				}
				case ICoreConstants.REFERENCE: {
					type = IUIConstants.REFERENCE_ICON;
					break;
				}
				case ICoreConstants.DECISION: {
					type = IUIConstants.DECISION_ICON;
					break;
				}
				case ICoreConstants.NOTE: {
					type = IUIConstants.NOTE_ICON;
					break;
				}
				case ICoreConstants.MAPVIEW: {
					type = IUIConstants.MAP_ICON;
					break;
				}
				case ICoreConstants.LISTVIEW: {
					type = IUIConstants.LIST_ICON;
					break;
				}
				case ICoreConstants.PRO: {
					type = IUIConstants.PRO_ICON;
					break;
				}
				case ICoreConstants.CON: {
					type = IUIConstants.CON_ICON;
					break;
				}
				case ICoreConstants.ISSUE_SHORTCUT: {
					type = IUIConstants.ISSUE_SHORTCUT_ICON;
					break;
				}
				case ICoreConstants.POSITION_SHORTCUT: {
					type = IUIConstants.POSITION_SHORTCUT_ICON;
					break;
				}
				case ICoreConstants.ARGUMENT_SHORTCUT: {
					type = IUIConstants.ARGUMENT_SHORTCUT_ICON;
					break;
				}
				case ICoreConstants.REFERENCE_SHORTCUT: {
					type = IUIConstants.REFERENCE_SHORTCUT_ICON;
					break;
				}
				case ICoreConstants.DECISION_SHORTCUT: {
					type = IUIConstants.DECISION_SHORTCUT_ICON;
					break;
				}
				case ICoreConstants.NOTE_SHORTCUT: {
					type = IUIConstants.NOTE_SHORTCUT_ICON;
					break;
				}
				case ICoreConstants.MAP_SHORTCUT: {
					type = IUIConstants.MAP_SHORTCUT_ICON;
					break;
				}
				case ICoreConstants.LIST_SHORTCUT: {
					type = IUIConstants.LIST_SHORTCUT_ICON;
					break;
				}
				case ICoreConstants.PRO_SHORTCUT: {
					type = IUIConstants.PRO_SHORTCUT_ICON;
					break;
				}
				case ICoreConstants.CON_SHORTCUT: {
					type = IUIConstants.CON_SHORTCUT_ICON;
					break;
				}
				case ICoreConstants.TRASHBIN: {
					type = IUIConstants.TRASHBIN_ICON;
					break;
				}
			}
		}

		String sPath = "";
		String skin = FormatProperties.skin;
		if (skin.equals("Default") || skin.equals("Default_Mini")) {
			sPath = sNODEPATH+skin+sFS+DEFAULT_IMG_NAMES[type];
		}
		else {
			sPath = sNODEPATH+skin+sFS+IMG_NAMES[type];
		}

		File fileCheck = new File(sPath);
		if (!fileCheck.exists())
			sPath = sDEFAULTNODEPATH+sFS+DEFAULT_IMG_NAMES[type];

		fileCheck = new File(sPath);
		if (!fileCheck.exists())
			sPath = sPATH+DEFAULT_IMG_NAMES[type];

		return sPath;
	}

	/**
	 * Return the path of the given icon file small image.
	 * @param int idx, The node type
	 * @return a String representing the path to the given icon file.
	 * @see IUIConstants
	 */
	public final static String getSmallPath(int idx) {

		int type = 0;

		switch (idx) {
			case ICoreConstants.ISSUE: {
				type = IUIConstants.ISSUE_SM_ICON;
				break;
			}
			case ICoreConstants.POSITION: {
				type = IUIConstants.POSITION_SM_ICON;
				break;
			}
			case ICoreConstants.ARGUMENT: {
				type = IUIConstants.ARGUMENT_SM_ICON;
				break;
			}
			case ICoreConstants.REFERENCE: {
				type = IUIConstants.REFERENCE_SM_ICON;
				break;
			}
			case ICoreConstants.DECISION: {
				type = IUIConstants.DECISION_SM_ICON;
				break;
			}
			case ICoreConstants.NOTE: {
				type = IUIConstants.NOTE_SM_ICON;
				break;
			}
			case ICoreConstants.MAPVIEW: {
				type = IUIConstants.MAP_SM_ICON;
				break;
			}
			case ICoreConstants.LISTVIEW: {
				type = IUIConstants.LIST_SM_ICON;
				break;
			}
			case ICoreConstants.PRO: {
				type = IUIConstants.PRO_SM_ICON;
				break;
			}
			case ICoreConstants.CON: {
				type = IUIConstants.CON_SM_ICON;
				break;
			}
			case ICoreConstants.ISSUE_SHORTCUT: {
				type = IUIConstants.ISSUE_SHORTCUT_SM_ICON;
				break;
			}
			case ICoreConstants.POSITION_SHORTCUT: {
				type = IUIConstants.POSITION_SHORTCUT_SM_ICON;
				break;
			}
			case ICoreConstants.ARGUMENT_SHORTCUT: {
				type = IUIConstants.ARGUMENT_SHORTCUT_SM_ICON;
				break;
			}
			case ICoreConstants.REFERENCE_SHORTCUT: {
				type = IUIConstants.REFERENCE_SHORTCUT_SM_ICON;
				break;
			}
			case ICoreConstants.DECISION_SHORTCUT: {
				type = IUIConstants.DECISION_SHORTCUT_SM_ICON;
				break;
			}
			case ICoreConstants.NOTE_SHORTCUT: {
				type = IUIConstants.NOTE_SHORTCUT_SM_ICON;
				break;
			}
			case ICoreConstants.MAP_SHORTCUT: {
				type = IUIConstants.MAP_SHORTCUT_SM_ICON;
				break;
			}
			case ICoreConstants.LIST_SHORTCUT: {
				type = IUIConstants.LIST_SHORTCUT_SM_ICON;
				break;
			}
			case ICoreConstants.PRO_SHORTCUT: {
				type = IUIConstants.PRO_SHORTCUT_SM_ICON;
				break;
			}
			case ICoreConstants.CON_SHORTCUT: {
				type = IUIConstants.CON_SHORTCUT_SM_ICON;
				break;
			}
			case ICoreConstants.TRASHBIN: {
				type = IUIConstants.TRASHBIN_SM_ICON;
				break;
			}
		}

		String sPath = "";
		String skin = FormatProperties.skin;
		if (skin.equals("Default") || skin.equals("Default_Mini")) {
			sPath = sNODEPATH+skin+sFS+DEFAULT_IMG_NAMES[type];
		}
		else {
			sPath = sNODEPATH+skin+sFS+IMG_NAMES[type];
		}

		File fileCheck = new File(sPath);
		if (!fileCheck.exists())
			sPath = sDEFAULTNODEPATH+sFS+DEFAULT_IMG_NAMES[type];

		fileCheck = new File(sPath);
		if (!fileCheck.exists())
			sPath = sPATH+DEFAULT_IMG_NAMES[type];

		return sPath;
	}


	/**
	 * If required scale the given icon and return the scaled version.
	 * @param imageString, the image icon file name to create and scale the icon from.
	 * @return ImageIcon, the scaled image icon.
	 */
	public final static ImageIcon thumbnailIcon(String imageString) {

	    ImageIcon inImage = createImageIcon(imageString);
	    if (inImage == null) {
			return inImage;
		}

		return thumbnailIcon(inImage);
	}

	/**
	 * If required scale the given icon and return the scaled version.
	 * @param imageString, the image icon file name to create and scale the icon from.
	 * @return ImageIcon, the scaled image icon.
	 */
	public final static ImageIcon thumbnailIcon(ImageIcon inImage) {

	    if (inImage == null) {
			return inImage;
		}

		Image icon = inImage.getImage();

	    int imgWidth = icon.getWidth(null);
	    int imgHeight = icon.getHeight(null);

	    // DON'T SCALE IF IMAGE SMALLER THAN MAXDIM
	    if (imgWidth < MAX_DIM && imgHeight < MAX_DIM ) {
			return inImage;
	    }
	    else  {
			// Determine the scale.
			double scale = (double)MAX_DIM/(double)imgHeight;
			if (imgWidth > imgHeight) {
		    	scale = (double)MAX_DIM/(double)imgWidth;
			}

			// Determine size of new image.
	  		//One of them should equal MAX_DIM.
			int scaledW = (int)(scale*imgWidth);
			int scaledH = (int)(scale*imgHeight);

			// create scaled image
			//Image thumbimage = Toolkit.getDefaultToolkit().getImage(imageString);

			ImageFilter filter = new AreaAveragingScaleFilter(scaledW, scaledH);
			FilteredImageSource filteredSource = new FilteredImageSource((ImageProducer)icon.getSource(), filter);
			JLabel comp = new JLabel();
			icon = comp.createImage(filteredSource);

			return new ImageIcon(icon);
		}
	}
	
	/**
	 * If required scale the given icon and return the scaled version.
	 * @param imageString, the image icon file name to create and scale the icon from.
	 * @return ImageIcon, the scaled image icon.
	 */
	public final static ImageIcon scaleIcon(ImageIcon inImage, Dimension newSize) {

	    if (inImage == null) {
			return inImage;
		}

		Image icon = inImage.getImage();
		ImageFilter filter = new AreaAveragingScaleFilter(newSize.width, newSize.height);
		FilteredImageSource filteredSource = new FilteredImageSource((ImageProducer)icon.getSource(), filter);
		JLabel comp = new JLabel();
		icon = comp.createImage(filteredSource);

		return new ImageIcon(icon);		
	}	

	/**
	 * If required scale the given image and return the scaled dimensions.
	 * @param refString the filename for the image to scale.
	 * @return Dimension the scaled size of the image with the given string.
	 */
	public final static Dimension thumbnailImage(String refString, int defaultWidth, int defaultHeight) {

		int width = defaultWidth;
		int height = defaultHeight;

		if (refString != null) {
			if ( isImage(refString)) {

				//determine scale of new image
				// Get the image from a file.

			    ImageIcon inImageIcon = createImageIcon(refString);
			    if (inImageIcon == null) {
					return new Dimension(width, height);
				}

				//if (!(new File(refString)).exists()) {
				//	return new Dimension(width, height);
				//}
				Image inImage = inImageIcon.getImage();

				int imgWidth = inImage.getWidth(null);
				int imgHeight = inImage.getHeight(null);

				// DON'T SCALE IF IMAGE SMALLER THAN MAX_DIM
				if (imgWidth < MAX_DIM && imgHeight < MAX_DIM )
					return new Dimension(imgWidth, imgHeight);

				// Determine the scale.
				double scale = (double)MAX_DIM/(double)imgHeight;
				if (imgWidth > imgHeight) {
					scale = (double)MAX_DIM/(double)imgWidth;
				}

				// Determine size of new image.
				//One of them should equal MAX_DIM.
				width = (int)(scale*imgWidth);
				height = (int)(scale*imgHeight);
			}
  		}

		return new Dimension(width, height);
	}

	/**
	 * Return the size the image produced from the given icon file name.
	 * @param refString, the name of the image to return the size for.
	 * @return Dimenaion, the size of the given image.
	 */
	public final static Dimension getImageSize(String refString) {

		int width = -1;
		int height = -1;

		// Get the image from a file.

	    ImageIcon inImageIcon = createImageIcon(refString);
	    if (inImageIcon == null) {
			return new Dimension(width, height);
		}

		//if (!(new File(refString)).exists()) {
		//	return new Dimension(width, height);
		//}

		Image inImage = inImageIcon.getImage();
		width = inImage.getWidth(null);
		height = inImage.getHeight(null);

		return new Dimension(width, height);
	}

	/**
	 * Check if the given string is the name of an image file (jpg, gif, png).
	 * @param refString, the name of the image to check.
	 * @return boolean, true if the file if an image else false.
	 */
	public static boolean isImage(String refString) {

		if (refString != null) {
			String ref = refString.toLowerCase();
			if ( ref.endsWith(".gif") || ref.endsWith(".jpg") || ref.endsWith(".jpeg") || ref.endsWith(".png")) {
				return true;
			}
		}
		return false;
  	}


	/**
	 * Take the given file path and create an ImageIcon file from it.
	 * If it is a image on the web, load appropriately.
	 * @param sImagePath the path of the image to load into an ImageIcon class.
	 * @return the Image icon, or null, if not successfully loaded.
	 */
	public final static ImageIcon createImageIcon(String sImagePath) {
		ImageIcon oIcon	= null;

		if (sImagePath.startsWith("http:") || sImagePath.startsWith("www.") || sImagePath.startsWith("https:")) {
			if (sImagePath.startsWith("www.")) {
				sImagePath = "http://"+sImagePath;
			}
			try {
				URL url = new URL(sImagePath);
				Image image = Toolkit.getDefaultToolkit().getImage(sImagePath);
				if (url != null && image != null) {
					oIcon = new ImageIcon(url);
					if (oIcon.getImageLoadStatus() == MediaTracker.ERRORED) {
						oIcon = null;
					}
				}
			}
			catch(Exception ex) {
				ex.printStackTrace();
				System.out.println("Exception URL trying to turn into image "+sImagePath+"\n\ndue to: "+ex.getMessage());
				System.out.flush();
			}
		} else {
			try {
				oIcon = new ImageIcon(sImagePath);
				if (oIcon.getImageLoadStatus() == MediaTracker.ERRORED) {
					oIcon = null;
				}				
			}
			catch(Exception ex) {
				ex.printStackTrace();
				System.out.println("Exception trying to turn into image "+sImagePath+"\n\ndue to: "+ex.getMessage());
			}
		}

		return oIcon;
	}

    /**
     * Get the contents of a URL and return it as an image.
	 * @return a String represting the path the file was actually saved to, or empty string if somthing failed.
     */
    public static String loadWebImageToLinkedFiles(String address, String sFileName, String sPath) throws Exception {

		ProjectCompendium.APP.setWaitCursor();

		File newFile = new File(sPath+sFileName);

		String imgAddress = address.toLowerCase();

		if ( (imgAddress.startsWith("www") || imgAddress.startsWith("http") || imgAddress.startsWith("https") )
			&& ( imgAddress.endsWith(".gif")
				|| imgAddress.endsWith(".jpg")
				|| imgAddress.endsWith(".jpeg")
				|| imgAddress.endsWith(".png")) ) {

			if (newFile.exists()) {
				int response = JOptionPane.showConfirmDialog(ProjectCompendium.APP, "A file with this name already exists in the 'Linked Files' directory.\nDo you wish to rename it before saving?\n\n(if you do not rename it, the existing file will be replaced)\n\n",
														"External Drag and Drop", JOptionPane.YES_NO_OPTION);

				if (response == JOptionPane.YES_OPTION) {

					UIFileChooser fileDialog = new UIFileChooser();
					fileDialog.setDialogType(JFileChooser.SAVE_DIALOG);
					fileDialog.setDialogTitle("Change the file name to...");
					fileDialog.setApproveButtonText("Save");

					fileDialog.setCurrentDirectory(newFile);
					fileDialog.setSelectedFile(newFile);
					UIUtilities.centerComponent(fileDialog, ProjectCompendium.APP);
					int retval = fileDialog.showSaveDialog(ProjectCompendium.APP);
					if (retval == JFileChooser.APPROVE_OPTION) {
						if ((fileDialog.getSelectedFile()) != null) {

							String fileName2 = fileDialog.getSelectedFile().getName();
							if (fileName2 != null) {
								sFileName = fileName2;
								File fileDir = fileDialog.getCurrentDirectory();

								if (ProjectCompendium.isMac)
									sPath = fileDir.getAbsolutePath()+ProjectCompendium.sFS;
								else
									sPath = fileDir.getPath();
							}
						}
					}
					else {
						return new String("");
					}
				}
			}

			URL url = new URL(address);
			URLConnection conn = url.openConnection();
			conn.connect();

			DataInputStream stream = new DataInputStream(new BufferedInputStream(conn.getInputStream()));
			FileOutputStream output = new FileOutputStream(sPath+sFileName);
			int count = conn.getContentLength();
			if (count > 0) {
				for (int i=0; i<count; i++) {
					output.write(stream.read());
				}
			}
			else {
				sFileName = "";
			}

			stream.close();
			output.flush();
			output.close();

			ProjectCompendium.APP.setDefaultCursor();
			return sPath+sFileName;

		}
		else {
			ProjectCompendium.APP.setDefaultCursor();
			return new String("");
		}
    }
}
