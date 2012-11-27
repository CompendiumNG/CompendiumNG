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

package com.compendium;

import java.util.*;

/**
 * This class is used to load and store the language files.
 *
 * @author	Michelle Bachler
 */
public class LanguageProperties {

	public static final int	UI_GENERAL_BUNDLE = 0;
	public static final int	MENUS_BUNDLE = 1;
	public static final int	POPUPS_BUNDLE = 2;
	public static final int	TOOLBARS_BUNDLE = 3;
	public static final int	DIALOGS_BUNDLE = 4;
	public static final int	PANELS_BUNDLE = 5;
	public static final int	TAGS_BUNDLE = 6;
	public static final int	STENCILS_BUNDLE = 7;
	public static final int	EDITS_BUNDLE = 8;
	public static final int	LINKGROUPS_BUNDLE = 9;
	public static final int	MEETING_BUNDLE = 10;
	public static final int	IO_BUNDLE = 11;
	public static final int	MOVIE_BUNDLE = 12;
	

	/** The dialogs language files.*/
	private static ResourceBundle uiBundle = null;

	/** The menu language files.*/
	private static ResourceBundle menusBundle = null;

	/** The popup menu language files.*/
	private static ResourceBundle popupsBundle = null;

	/** The toolbar language files.*/
	private static ResourceBundle toolbarsBundle = null;

	/** The dialogs language files.*/
	private static ResourceBundle dialogsBundle = null;

	/** The panels language files.*/
	private static ResourceBundle panelsBundle = null;

	/** The tags language files.*/
	private static ResourceBundle tagsBundle = null;

	/** The stencils language files.*/
	private static ResourceBundle stencilsBundle = null;
	
	/** The edit language files.*/
	private static ResourceBundle editsBundle = null;

	/** The linkgroup language files.*/
	private static ResourceBundle linkgroupsBundle = null;

	/** The meeting language files.*/
	private static ResourceBundle meetingBundle = null;

	/** The io language files.*/
	private static ResourceBundle ioBundle = null;

	/** The movie package language files.*/
	private static ResourceBundle movieBundle = null;

	/**
	 * Constructor. Does nothing.
	 */
	public LanguageProperties() {}

	/**
	 * Load the format properties into the class variables.
	 */
	public static void loadProperties() {
		Locale locale = Locale.getDefault();
				
		try {
	        uiBundle = ResourceBundle.getBundle("general", locale, new LanguageClassLoader());
	        menusBundle = ResourceBundle.getBundle("menus", locale, new LanguageClassLoader());
	        popupsBundle = ResourceBundle.getBundle("popups", locale, new LanguageClassLoader());
	        toolbarsBundle = ResourceBundle.getBundle("toolbars", locale, new LanguageClassLoader());
	        dialogsBundle = ResourceBundle.getBundle("dialogs", locale, new LanguageClassLoader());
	        panelsBundle = ResourceBundle.getBundle("panels", locale, new LanguageClassLoader());
	        tagsBundle = ResourceBundle.getBundle("tags", locale, new LanguageClassLoader());
	        stencilsBundle = ResourceBundle.getBundle("stencils", locale, new LanguageClassLoader());
	        editsBundle = ResourceBundle.getBundle("edits", locale, new LanguageClassLoader());
	        linkgroupsBundle = ResourceBundle.getBundle("linkgroups", locale, new LanguageClassLoader());
	        meetingBundle = ResourceBundle.getBundle("meeting", locale, new LanguageClassLoader());
	        ioBundle = ResourceBundle.getBundle("io", locale, new LanguageClassLoader());
	        movieBundle = ResourceBundle.getBundle("movie", locale, new LanguageClassLoader());
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Return the value against the given key if found, else an empty String.
	 * @param bundle the bundle that the string is in.
	 * @param key the key to get the value for.
	 * @return String, the associated value.
	 */
	public static String getString( int bundle, String key ) {
		String value = "";
		try {
			switch (bundle) {
			case UI_GENERAL_BUNDLE:
		        value = uiBundle.getString(key);
		        break;
			case MENUS_BUNDLE:
		        value = menusBundle.getString(key);
		        break;
			case POPUPS_BUNDLE:
		        value = popupsBundle.getString(key);
		        break;
			case TOOLBARS_BUNDLE:
		        value = toolbarsBundle.getString(key);
		        break;
			case DIALOGS_BUNDLE:
		        value = dialogsBundle.getString(key);
		        break;
			case PANELS_BUNDLE:
		        value = panelsBundle.getString(key);
		        break;
			case TAGS_BUNDLE:
		        value = tagsBundle.getString(key);
		        break;
			case STENCILS_BUNDLE:
		        value = stencilsBundle.getString(key);
		        break;
			case EDITS_BUNDLE:
		        value = editsBundle.getString(key);
		        break;
			case LINKGROUPS_BUNDLE:
		        value = linkgroupsBundle.getString(key);
		        break;
			case MEETING_BUNDLE:
		        value = meetingBundle.getString(key);
		        break;
			case IO_BUNDLE:
		        value = ioBundle.getString(key);
		        break;
			case MOVIE_BUNDLE:
		        value = movieBundle.getString(key);
		        break;
			}
		} catch(MissingResourceException mre) {
			System.out.println(mre.getMessage());
			value = "UNKNOWN STRING";
		}
		return value;
	}
}
