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

import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;
import org.compendiumng.tools.Utilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.image.AreaAveragingScaleFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * This class has method for retrieving image files or references from various directories.
 *
 * @author	Mohammed Sajid Ali / Michelle Bachler
 */
public class UIImages implements IUIConstants {
	/**
	 * class's own logger
	 */
	private static final Logger log = LoggerFactory.getLogger(UIImages.class);
	/** The file filter to use when asking the user to select an image file */
	public final static UIFileFilter IMAGE_FILTER = new UIFileFilter(new String[] {"gif","jpg","jpeg","png"}, LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIImages.imageFiles")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
	
	/** Maximum dimension for a reference node image when a graphic is specified.*/
	public final static int MAX_DIM = 96;

	/** A reference to the system file path separator.*/
	private final static String	sFS						= System.getProperty("file.separator"); //$NON-NLS-1$

	/** A reference to the skins default directory.*/
	private final static String sDEFAULTNODEPATH		= "Skins"+sFS+"Default"+sFS; //$NON-NLS-1$ //$NON-NLS-2$

	/** A reference to the main skins directory.*/
	private final static String	sNODEPATH 				= "Skins"+sFS; //$NON-NLS-1$

	/** The array of images returned so far this session.*/
	private static ImageIcon[] img = new ImageIcon[IUIConstants.NUM_IMAGES];

	/** The array of mac specific images returned so far this session.*/
	private static ImageIcon[] macImg = new ImageIcon[IUIConstants.NUM_IMAGES];

	/**
	 * Return the appropriate ImageIcon, associated with the given identifier for this platform.
	 * @param int idx, an identifier for the image file required.
	 * @return the relevant ImageIcon
	 * @see IUIConstants
	 */
	public static ImageIcon get(int idx) {
	    ImageIcon image = null;

	    if (ProjectCompendium.isMac && FormatProperties.currentLookAndFeel.equals("apple.laf.AquaLookAndFeel")) { //$NON-NLS-1$
			image = macImg[idx];
			if (image == null) {
				String sPath = ProjectCompendium.DIR_IMAGES + IMG_NAMES[idx];
				File file = new File(sPath);
				if (!file.exists()) {
					sPath = ProjectCompendium.DIR_IMAGES + IMG_NAMES[idx];
				}
		    	
			    image = new ImageIcon(sPath);
			    if (image != null) {
			    	macImg[idx] = image;
				}
			}
		} else {
			image = img[idx];
			if (image == null) {
			    image = new ImageIcon(ProjectCompendium.DIR_IMAGES + IMG_NAMES[idx]);
			    if (image != null) {
			    	img[idx] = image;
			    }
			}
		}

	    return image;
	}

	/**
	 * Return the path string for the given identifier for non-platform specific images.
	 * @param int idx, an identifier for the image file required.
	 * @return the String of the image path.
	 * @see IUIConstants
	 */
	public static String getPathString(int idx) {

		return ProjectCompendium.DIR_IMAGES + IMG_NAMES[idx];
	}

	/**
	 * Return the path associated with the given reference type.
	 * @param int idx, an identifier for the reference type.
	 * @return a String representing relevant file path.
	 * @see IUIConstants
	 */
	private static String getReferencePath(int idx) {

		String refPath = ProjectCompendium.DIR_REFERENCE_NODE_ICONS + IMG_NAMES[idx];

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
	public static String getReferencePath(String refString, String sDefault, boolean bSmallIcon) {				
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
	public static String getReferenceSmallPath(String refString, String sDefault) {
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
	public static ImageIcon getReferenceIcon(int idx) {

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
		return UINodeTypeManager.getNodeImage(type);
	}
	
	/**
	 * Return the ImageIcon associated with the given node type.
	 * @param idx the identifier for the image location for the node type.
	 * @return the relevant ImageIcon.
	 * @see IUIConstants
	 */
	public static ImageIcon getNodeIcon(int idx) {
		
		String sPath = ""; //$NON-NLS-1$
		String skin = FormatProperties.skin;

		// Old skins sets have gif files, new ones have png - so need to check.
		// Also handle skin missing by getting image from images folder.
		
		//Check if the icon is a png file
		sPath = ProjectCompendium.DIR_SKINS+skin+sFS+DEFAULT_IMG_NAMES[idx];
		
		log.debug("loading icon id: {} path: {}", idx, sPath);
		
		File fileCheck1 = new File(sPath);
		if (!fileCheck1.exists()) {
			log.warn("icon id: {} path: {} is missing !", idx, sPath);
			//check if file is a gif
			sPath = ProjectCompendium.DIR_SKINS +skin+sFS+IMG_NAMES[idx];
			File fileCheck = new File(sPath);
			if (!fileCheck.exists()) {
				// if image not found, try getting the image from the default skin
				sPath = sDEFAULTNODEPATH+DEFAULT_IMG_NAMES[idx];
				fileCheck = new File(sPath);
				if (!fileCheck.exists()) {
					// If all else fails, get the backup images in the images folder
					sPath = ProjectCompendium.DIR_IMAGES+DEFAULT_IMG_NAMES[idx];
				}
			}
		}

		ImageIcon image = Utilities.GetImageIcon(sPath, null);

		return image;
	}

	/**
	 * Return the path of the given icon file.
	 * @param type The node type.
	 * @param bSmallIcon true for returning the small version of the node icon, false for the standard size.
	 * @return a String representing the path to the given icon file.
	 * @see IUIConstants
	 */
	public static String getPath(int type, boolean bSmallIcon) {

		if (bSmallIcon) {
			return getSmallPath(type);
		}
		else {
			int idx = UINodeTypeManager.getImageIndexForType(type);
			String sPath = ""; //$NON-NLS-1$
			String skin = FormatProperties.skin;
			
			// Old skins sets have gif files, new ones have png - so need to check.
			// Also handle skin missing by getting image from images folder.
			
			//Check if the icon is a png file
			sPath = sNODEPATH+skin+sFS+DEFAULT_IMG_NAMES[idx];
			File fileCheck1 = new File(sPath);
			if (!fileCheck1.exists()) {
				//check if file is a gif
				sPath = sNODEPATH+skin+sFS+IMG_NAMES[idx];
				File fileCheck = new File(sPath);
				if (!fileCheck.exists()) {
					// if image not found, try getting the image from the default skin
					sPath = sDEFAULTNODEPATH+DEFAULT_IMG_NAMES[idx];
					fileCheck = new File(sPath);
					if (!fileCheck.exists()) {
						// If all else fails, get the backup images in the images folder
						sPath = ProjectCompendium.DIR_IMAGES+DEFAULT_IMG_NAMES[idx];
					}
				}
			}
	
			return sPath;
		}
	}

	/**
	 * Return the path of the given icon file small image.
	 * @param type The node type
	 * @return a String representing the path to the given icon file.
	 * @see IUIConstants
	 */
	public static String getSmallPath(int type) {

		int idx = UINodeTypeManager.getImageIndexForType(type);
		
		String sPath = ""; //$NON-NLS-1$
		String skin = FormatProperties.skin;
		
		// Old skins sets have gif files, new ones have png - so need to check.
		// Also handle skin missing by getting image from images folder.
		
		//Check if the icon is a png file
		sPath = sNODEPATH+skin+sFS+DEFAULT_IMG_NAMES[idx];
		File fileCheck1 = new File(sPath);
		if (!fileCheck1.exists()) {
			//check if file is a gif
			sPath = sNODEPATH+skin+sFS+IMG_NAMES[idx];
			File fileCheck = new File(sPath);
			if (!fileCheck.exists()) {
				// if image not found, try getting the image from the default skin
				sPath = sDEFAULTNODEPATH+DEFAULT_IMG_NAMES[idx];
				fileCheck = new File(sPath);
				if (!fileCheck.exists()) {
					// If all else fails, get the backup images in the images folder
					sPath = ProjectCompendium.DIR_IMAGES+DEFAULT_IMG_NAMES[idx];
				}
			}
		}

		return sPath;
	}


	/**
	 * If required scale the given icon and return the scaled version.
	 * @param imageString, the image icon file name to create and scale the icon from.
	 * @return ImageIcon, the scaled image icon.
	 */
	public static ImageIcon thumbnailIcon(String imageString, String base_path) {

	    ImageIcon inImage = Utilities.GetImageIcon(imageString, base_path);
	    if (inImage == null) {
			return inImage;
		}

		return thumbnailIcon(inImage);
	}

	/**
	 * If required scale the given icon and return the scaled version (96x96 max).
	 * @param imageString, the image icon file name to create and scale the icon from.
	 * @return ImageIcon, the scaled image icon.
	 */
	public static ImageIcon thumbnailIcon(ImageIcon inImage) {

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

			return scaleIcon(inImage, new Dimension(scaledW, scaledH));
		}
	}
	
	/**
	 * If required scale the given icon and return the scaled version.
	 * @param imageString, the image icon file name to create and scale the icon from.
	 * @return ImageIcon, the scaled image icon.
	 */
	public static ImageIcon scaleIcon(ImageIcon inImage, Dimension newSize) {

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
	 * Get the new Dinmension for this image ig it was scaled.
	 * @param refString the filename for the image to get the new dimension for.
	 * @return Dimension the scaled size of the image with the given string.
	 */
	public static Dimension thumbnailImage(String refString, int defaultWidth, int defaultHeight) {

		int width = defaultWidth;
		int height = defaultHeight;

		if (refString != null) {
			if ( isImage(refString)) {

				//determine scale of new image
				// Get the image from a file.

			    ImageIcon inImageIcon = Utilities.GetImageIcon(refString, null);
			    if (inImageIcon == null) {
					return new Dimension(width, height);
				}

				Image inImage = inImageIcon.getImage();

				int imgWidth = inImage.getWidth(null);
				int imgHeight = inImage.getHeight(null);

				// DON'T DO IT IF IMAGE SMALLER THAN MAX_DIM
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
	public static Dimension getImageSize(String refString) {

		int width = -1;
		int height = -1;

		// Get the image from a file.

	    ImageIcon inImageIcon = Utilities.GetImageIcon(refString, null);
	    if (inImageIcon == null) {
			return new Dimension(width, height);
		}

		Image inImage = inImageIcon.getImage();
		width = inImage.getWidth(null);
		height = inImage.getHeight(null);

		return new Dimension(width, height);
	}

	/**
	 * Check if the given string is the name of a supported image file (jpg, jpeg, gif, png, tiff, tif).
	 * @param refString, the name of the image to check.
	 * @return boolean, true if the file if a supported image type else false.
	 */
	public static boolean isImage(String refString) {

		if (refString != null) {
			String ref = refString.toLowerCase();
			if ( ref.endsWith(".gif") || ref.endsWith(".jpg") 
					|| ref.endsWith(".jpeg") || ref.endsWith(".png")
					|| ref.endsWith(".tiff") || ref.endsWith(".tif")) { 
				return true;
			}
		}
		return false;
  	}


    /**
     * Get the contents of a URL and return it as an image.
	 * @return a String representing the path the file was actually saved to, or empty string if something failed.
     */
    public static String loadWebImageToLinkedFiles(String address, String sFileName, String sPath) throws Exception {

		ProjectCompendium.APP.setWaitCursor();

		File newFile = new File(sPath+sFileName);

		String imgAddress = address.toLowerCase();

		if ( (imgAddress.startsWith("www") || imgAddress.startsWith("http") || imgAddress.startsWith("https") ) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			&& isImage(imgAddress)) { //$NON-NLS-1$

			if (newFile.exists()) {
				int response = JOptionPane.showConfirmDialog(ProjectCompendium.APP, 
						LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIImages.nameExistsMessage1a")+"\n"+ //$NON-NLS-1$ //$NON-NLS-2$
						LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIImages.nameExistsMessage1b")+"\n\n"+//$NON-NLS-1$ //$NON-NLS-2$
						"("+LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIImages.nameExistsMessage1c")+")\n\n", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIImages.externalDragAndDrop"), JOptionPane.YES_NO_OPTION); //$NON-NLS-1$

				if (response == JOptionPane.YES_OPTION) {

					UIFileChooser fileDialog = new UIFileChooser();
					fileDialog.setDialogType(JFileChooser.SAVE_DIALOG);
					fileDialog.setDialogTitle(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIImages.changeFileName")); //$NON-NLS-1$
					fileDialog.setApproveButtonText(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIImages.saveButton")); //$NON-NLS-1$
					fileDialog.setCurrentDirectory(new File(newFile.getParent()+ProjectCompendium.sFS));
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
						return new String(""); //$NON-NLS-1$
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
				sFileName = ""; //$NON-NLS-1$
			}

			stream.close();
			output.flush();
			output.close();

			ProjectCompendium.APP.setDefaultCursor();
			return sPath+sFileName;

		}
		else {
			ProjectCompendium.APP.setDefaultCursor();
			return new String(""); //$NON-NLS-1$
		}
    }
}
