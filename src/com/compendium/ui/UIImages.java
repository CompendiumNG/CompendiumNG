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

import java.io.*;
import java.awt.*;
import java.awt.image.*;
import java.net.*;

import javax.swing.*;

import com.compendium.core.datamodel.LinkedFile;
import com.compendium.core.datamodel.LinkedFileDatabase;
import com.compendium.core.datamodel.Model;
import com.compendium.core.datamodel.PCSession;
import com.compendium.*;

/**
 * This class has method for retrieving image files or references from various directories.
 *
 * @author	Mohammed Sajid Ali / Michelle Bachler
 */
public class UIImages implements IUIConstants {

	/** The file filter to use when asking the user to select an image file */
	public final static UIFileFilter IMAGE_FILTER = new UIFileFilter(new String[] {"gif","jpg","jpeg","png"}, LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIImages.imageFiles")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
	
	/** Maximum dimension for a reference node image when a graphic is specified.*/
	public final static int MAX_DIM = 96;

	/** A reference to the system file path separator.*/
	protected final static String	sFS						= System.getProperty("file.separator"); //$NON-NLS-1$

	/** A reference to the main image directory.*/
	public final static String	sPATH 					= "System"+sFS+"resources"+sFS+"Images"+sFS; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	/** A reference to the main image directory*/
	public final static String	sMACPATH 				= sPATH+"Mac"+sFS; //$NON-NLS-1$

	/** A reference to the skins default directory.*/
	protected final static String sDEFAULTNODEPATH		= "Skins"+sFS+"Default"+sFS; //$NON-NLS-1$ //$NON-NLS-2$

	/** A reference to the main skins directory.*/
	protected final static String	sNODEPATH 				= "Skins"+sFS; //$NON-NLS-1$

	/** A reference to the reference node image directory.*/
	protected final static String sREFERENCEPATH			= "System"+sFS+"resources"+sFS+"ReferenceNodeIcons"+sFS; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	/** A reference to the reference node image directory on the Mac.*/
	protected final static String sMACREFERENCEPATH		= sREFERENCEPATH+"Mac"+sFS; //$NON-NLS-1$

	/** The array of images returned so far this session.*/
	protected static ImageIcon img[] = new ImageIcon[IUIConstants.NUM_IMAGES];

	/** The array of mac specific images returned so far this session.*/
	protected static ImageIcon macImg[] = new ImageIcon[IUIConstants.NUM_IMAGES];

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
				String sPath = sMACPATH + IMG_NAMES[idx];
				File file = new File(sPath);
				if (!file.exists()) {
					sPath = sPATH + IMG_NAMES[idx];
				}
		    	
			    image = new ImageIcon(sPath);
			    if (image != null) {
			    	macImg[idx] = image;
				}
			}
		} else {
			image = img[idx];
			if (image == null) {
			    image = new ImageIcon(sPATH + IMG_NAMES[idx]);
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

		return sPATH + IMG_NAMES[idx];
	}

	/**
	 * Return the path associated with the given reference type.
	 * @param int idx, an identifier for the reference type.
	 * @return a String representing relevant file path.
	 * @see IUIConstants
	 */
	public static String getReferencePath(int idx) {

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
		sPath = sNODEPATH+skin+sFS+DEFAULT_IMG_NAMES[idx];
		File fileCheck1 = new File(sPath);
		if (!fileCheck1.exists()) {
			//check if file is a gif
			sPath = sNODEPATH+skin+sFS+IMG_NAMES[idx];
			File fileCheck = new File(sPath);
			if (!fileCheck.exists()) {
				// if image not found, try getting the image from the default skin
				sPath = sDEFAULTNODEPATH+sFS+DEFAULT_IMG_NAMES[idx];
				fileCheck = new File(sPath);
				if (!fileCheck.exists()) {
					// If all else fails, get the backup images in the images folder
					sPath = sPATH+DEFAULT_IMG_NAMES[idx];
				}
			}
		}

		ImageIcon image = new ImageIcon(sPath);

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
					sPath = sDEFAULTNODEPATH+sFS+DEFAULT_IMG_NAMES[idx];
					fileCheck = new File(sPath);
					if (!fileCheck.exists()) {
						// If all else fails, get the backup images in the images folder
						sPath = sPATH+DEFAULT_IMG_NAMES[idx];
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
				sPath = sDEFAULTNODEPATH+sFS+DEFAULT_IMG_NAMES[idx];
				fileCheck = new File(sPath);
				if (!fileCheck.exists()) {
					// If all else fails, get the backup images in the images folder
					sPath = sPATH+DEFAULT_IMG_NAMES[idx];
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
	public static ImageIcon thumbnailIcon(String imageString) {

	    ImageIcon inImage = createImageIcon(imageString);
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
	 * Check if the given string is the name of a supported movie file (avi, swf, spl, mov).
	 * @param refString, the name of the movie to check.
	 * @return boolean, true if the file if a supported movie else false.
	 */
	/*public static boolean isMovie(String refString) {

		if (refString != null) {
			String ref = refString.toLowerCase();
			if ( ref.endsWith(".avi") || ref.endsWith(".swf")  //$NON-NLS-1$ //$NON-NLS-2$
					|| ref.endsWith(".spl") || ref.endsWith(".mov") //$NON-NLS-1$ //$NON-NLS-2$
					|| ref.endsWith(".mp4")) {
				return true;
			}
		}
		return false;
  	}*/


	/**
	 * Take the given file path and create an ImageIcon file from it.
	 * If it is a image on the web, load appropriately.
	 * @param sImagePath the path or URI of the image to load into an ImageIcon class.
	 * @return the Image icon, or null, if not successfully loaded.
	 */
	public final static ImageIcon createImageIcon(String sImagePath) {
		
		if (sImagePath.startsWith("www.")) { //$NON-NLS-1$
			sImagePath = "http://"+sImagePath; //$NON-NLS-1$
		}
		
		ImageIcon oIcon	= null;
		URI oImageUri = null;
		String scheme = null;
		String path = null;
		
		try {
			oImageUri = new URI(sImagePath);
			scheme = oImageUri.getScheme();
			path = oImageUri.getPath();
		}
		catch (URISyntaxException ex) {
			// ok, path is no URI
			oImageUri = null;
		}

		if (scheme != null) {
			if (scheme.equals("http") || scheme.equals("https")) { //$NON-NLS-1$ //$NON-NLS-2$
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
					System.out.println("Exception URL trying to turn into image "+sImagePath+"\n\ndue to: "+ex.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
					System.out.flush();
				}
			}
			else if (scheme.equals("file")) { //$NON-NLS-1$
				oIcon = createImageIcon(path);
			}
			else if (scheme.equals("linkedFile")) { //$NON-NLS-1$
				Model oModel = (Model)ProjectCompendium.APP.getModel();
				PCSession oSession = oModel.getSession();				
				LinkedFile linked = new LinkedFileDatabase(oImageUri);
				linked.initialize(oSession, oModel);
				try {
					oIcon = createImageIcon(linked.getFile(ProjectCompendium.temporaryDirectory).getPath());
				} catch(Exception e){
					e.printStackTrace();
					System.out.println("Exception trying to load image from database "+sImagePath+"\n\ndue to: "+e.getLocalizedMessage());									 //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
			else {
				System.out.println("createImageIcon: unknown URI scheme: "+ scheme); //$NON-NLS-1$
				System.out.flush();
				// Note mrudolf: this is more restrictive than before. As it was,
				// it would pass the URI path straight through to the non-Uri part below.
				// I can imagine that on Linux there might be URIs such as fish:// that
				// may have worked before...
				oIcon = null; 
			}
		}
		else {
			// non-URI 
			try {
				oIcon = new ImageIcon(sImagePath);
				if (oIcon.getImageLoadStatus() == MediaTracker.ERRORED) {
					oIcon = null;
				}				
			}
			catch(Exception ex) {
				ex.printStackTrace();
				System.out.println("Exception trying to turn into image "+sImagePath+"\n\ndue to: "+ex.getLocalizedMessage()); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}

		return oIcon;
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
    
    // Load an image from the image library.
    /*static synchronized public Image fetchImage(String name) {

       Image image=null;
       byte[] bits;

       bits = ImageLib.getImage(name);
       if (bits==null)
           return null;

       image = Toolkit.getDefaultToolkit().createImage(bits);

       try {  // wait for image
           MediaTracker imageTracker = new MediaTracker(panel);
           imageTracker.addImage(image, 0);
           imageTracker.waitForID(0);
       } catch (InterruptedException e) {
           System.err.println("ImageLoader: Interrupted at waitForID");
       }

		return image;
    }*/
    
 	/*
 	public static BufferedImage toCompatibleImage(BufferedImage image, GraphicsConfiguration gc) {
        if (gc == null)
            gc = UIUtilities.getDefaultConfiguration();
        int w = image.getWidth();
        int h = image.getHeight();
        int transparency = image.getColorModel().getTransparency();
        BufferedImage result = gc.createCompatibleImage(w, h, transparency);
        Graphics2D g2 = result.createGraphics();
        g2.drawRenderedImage(image, null);
        g2.dispose();
        return result;
    }
  	
    public static BufferedImage copy(BufferedImage source, BufferedImage target) {
        Graphics2D g2 = target.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        double scalex = (double) target.getWidth()/ source.getWidth();
        double scaley = (double) target.getHeight()/ source.getHeight();
        AffineTransform xform = AffineTransform.getScaleInstance(scalex, scaley);
        g2.drawRenderedImage(source, xform);
        g2.dispose();
        return target;
    }
  	
  	public static BufferedImage getScaledInstance(BufferedImage image, int width, int height, GraphicsConfiguration gc) {
        if (gc == null)
            gc = UIUtilities.getDefaultConfiguration();
        int transparency = image.getColorModel().getTransparency();
        return copy(image, gc.createCompatibleImage(width, height, transparency));
    }
  	
  	public static void loadImage(Image image, Component c) {
  	    try {
  	        if (image instanceof BufferedImage)
  	            return; //already buffered
  	        MediaTracker tracker = new MediaTracker(c);
  	        tracker.addImage(image, 0);
  	        tracker.waitForID(0);
  	        if (MediaTracker.COMPLETE != tracker.statusID(0, false))
  	            throw new IllegalStateException("image loading fails");
  	    } catch (InterruptedException e) {
  	        throw new RuntimeException("interrupted", e);
  	    }
  	}
  	 
  	public static ColorModel getColorModel(Image image) {
  	    try {
  	        PixelGrabber grabby = new PixelGrabber(image, 0, 0, 1, 1, false);
  	        if (!grabby.grabPixels())
  	            throw new RuntimeException("pixel grab fails");
  	        return grabby.getColorModel();
  	    } catch (InterruptedException e) {
  	        throw new RuntimeException("interrupted", e);
  	    }
  	}
  	 
  	public static BufferedImage toBufferedImage(Image image, GraphicsConfiguration gc) {
  	    if (image instanceof BufferedImage)
  	        return (BufferedImage) image;
  	    loadImage(image, new Label());
  	    int w = image.getWidth(null);
  	    int h = image.getHeight(null);
  	    int transparency = getColorModel(image).getTransparency();
  	    if (gc == null)
  	        gc = getDefaultConfiguration();
  	    BufferedImage result = gc.createCompatibleImage(w, h, transparency);
  	    Graphics2D g = result.createGraphics();
  	    g.drawImage(image, 0, 0, null);
  	    g.dispose();
  	    return result;
  	}
  	*/    
}
