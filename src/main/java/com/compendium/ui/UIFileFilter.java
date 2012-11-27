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

import java.io.File;
import java.util.Hashtable;
import java.util.Enumeration;
import javax.swing.*;
import javax.swing.filechooser.*;

/**
 * This class extends FileFilter to implement filtering for JFileDialogs throughout the system.
 *
 * @author	Michelle Bachler
 */
public class UIFileFilter extends FileFilter {

	/** The list of file extensions allowed.*/
	private Hashtable filters = null;

	/** The description for this file filter.*/
	private String description = null;

	/** The full description for this file filter.*/
	private String fullDescription = null;

	/** Whether to use extension in the description.*/
	private boolean useExtensionsInDescription = true;

	/**
	 * Create a new instance of UIFileFilter.
	 */
	public UIFileFilter() {
	   	this.filters = new Hashtable();
	}

	/**
	 * Create a new instance of UIFileFilter with the given extension to filter on.
	 * @param extension, the extension to filter on.
	 */
	public UIFileFilter(String extension) {
		this(extension,null);
	}

	/**
	 * Create a new instance of UIFileFilter with the given extension to filter on.
	 * @param extension, the extension to fileter on.
	 * @param extension, the description for this filter.
	 */
	public UIFileFilter(String extension, String description) {
		this();
		if (extension!=null)
			addExtension(extension);
		if (description != null)
			setDescription(description);
	}

	/**
	 * Create a new instance of UIFileFilter with the given extension list to filter on.
	 * @param filters, the extension list to filter on.
	 */
	public UIFileFilter(String[] filters) {
		this(filters, null);
	}

	/**
	 * Create a new instance of UIFileFilter with the given extension list to filter on.
	 * @param filters, the extension list to filter on.
	 * @param extension, the description for this filter.
	 */
	public UIFileFilter(String[] filters, String description) {
		this();
		for (int i = 0; i < filters.length; i++) {
			addExtension(filters[i]);
		}

		if (description != null)
			setDescription(description);
	}

    /**
     * Whether the given file is accepted by this filter.
	 * @param f, the file to check.
	 * @return boolean, true if the given file is accepted by this filter, else false.
     */
	public boolean accept(File f) {
		if (f != null){
			if (f.isDirectory()) {
				return true;
			}
			String extension = getExtension(f);
			if (extension != null && filters.get(getExtension(f)) != null) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Return the extension for the give file.
	 * @param f, the file to return the extension for.
	 * @return String, the extension for the given file.
 	 */
	public String getExtension(File f) {
		if (f != null) {
			String filename = f.getName();
			int i = filename.lastIndexOf('.');
			if (i>0 && i<filename.length()-1) {
				return filename.substring(i+1).toLowerCase();
			}
		}
		return null;
	}

	/**
	 * Add the given extension to the list of extensions to filter on.
	 * @param extension, the new extension to add to the list.
	 */
	public void addExtension(String extension) {
		if (filters == null) {
			filters = new Hashtable(5);
		}
		filters.put(extension.toLowerCase(), this);
		fullDescription = null;
	}

    /**
     * The description of this filter. For example: "JPG and GIF Images"
     * @see FileView#getName
     */
	public String getDescription() {
		if (fullDescription == null) {
			if (description == null || isExtensionListInDescription()) {
				fullDescription = description==null ? "(" : description + " ("; //$NON-NLS-1$ //$NON-NLS-2$
				Enumeration extensions = filters.keys();
				if (extensions != null) {
					fullDescription += "." + (String) extensions.nextElement(); //$NON-NLS-1$
					while (extensions.hasMoreElements()) {
						fullDescription += ", " + (String) extensions.nextElement(); //$NON-NLS-1$
					}
				}
				fullDescription += ")"; //$NON-NLS-1$
			}
			else {
				fullDescription = description;
			}
		}
		return fullDescription;
	}

	/**
	 * Set the description for this file filter.
	 * @param description, the description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
		fullDescription = null;
	}

	/**
	 * Set whether to use extension in the description.
	 * @param b, true extension should be used in the description, else false.
	 */
	public void setExtensionListInDescription(boolean b) {
		useExtensionsInDescription = b;
		fullDescription = null;
	}

	/**
	 * Return whether to use extension in the description.
	 * @return boolean, true ifextension should be used in the description, else false.
	 */
	public boolean isExtensionListInDescription() {
		return useExtensionsInDescription;
	}
}
