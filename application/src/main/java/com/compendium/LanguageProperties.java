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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * This class is used to load and store the language files.
 *
 * @author	Michelle Bachler
 */
public class LanguageProperties {
	
	private static final Logger log = LoggerFactory.getLogger(LanguageProperties.class);

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
	private static final int	MEETING_BUNDLE = 10;
	public static final int	IO_BUNDLE = 11;
	public static final int	MOVIE_BUNDLE = 12;
	

	/** The dialogs language files.*/
	private static ResourceBundle rsrc_bundle_ui = null;

	/** The menu language files.*/
	private static ResourceBundle rsrc_bundle_menus = null;

	/** The popup menu language files.*/
	private static ResourceBundle rsrc_bundle_popups = null;

	/** The toolbar language files.*/
	private static ResourceBundle rsrc_bundle_toolbars = null;

	/** The dialogs language files.*/
	private static ResourceBundle rsrc_bundle_dialogs = null;

	/** The panels language files.*/
	private static ResourceBundle rsrc_bundle_panels = null;

	/** The tags language files.*/
	private static ResourceBundle rsrc_bundle_tags = null;

	/** The stencils language files.*/
	private static ResourceBundle rsrc_bundle_stencils = null;
	
	/** The edit language files.*/
	private static ResourceBundle rsrc_bundle_edits = null;

	/** The linkgroup language files.*/
	private static ResourceBundle rsrc_bundle_linkgroups = null;

	/** The meeting language files.*/
	private static ResourceBundle rsrc_bundle_meeting = null;

	/** The io language files.*/
	private static ResourceBundle rsrc_bundle_io = null;

	/** The movie package language files.*/
	private static ResourceBundle rsrc_bundle_movie = null;

	/**
	 * Constructor. Does nothing.
	 */
    private LanguageProperties() {}

	/**
	 * Load the format properties into the class variables.
	 */
	public static void loadProperties() {
		Locale locale = null;
		String sLocaleDir = ProjectCompendium.Config.getString("base.ui.locale.dir", "");
		String sLocale = ProjectCompendium.Config.getString("base.ui.locale", "en_US");
	
			
		if (sLocale.isEmpty()) {
			locale = Locale.getDefault();
			log.info("Loading default UI locale (no other locale available)");
		} else {
			log.info(
					"Loading non-default locale ({}) specified in the configuration file",
					sLocale);
			locale = new Locale(sLocale);
		}
				
		try {
	        rsrc_bundle_ui = ResourceBundle.getBundle("general", locale, new LanguageClassLoader());
	        rsrc_bundle_menus = ResourceBundle.getBundle("menus", locale, new LanguageClassLoader());
	        rsrc_bundle_popups = ResourceBundle.getBundle("popups", locale, new LanguageClassLoader());
	        rsrc_bundle_toolbars = ResourceBundle.getBundle("toolbars", locale, new LanguageClassLoader());
	        rsrc_bundle_dialogs = ResourceBundle.getBundle("dialogs", locale, new LanguageClassLoader());
	        rsrc_bundle_panels = ResourceBundle.getBundle("panels", locale, new LanguageClassLoader());
	        rsrc_bundle_tags = ResourceBundle.getBundle("tags", locale, new LanguageClassLoader());
	        rsrc_bundle_stencils = ResourceBundle.getBundle("stencils", locale, new LanguageClassLoader());
	        rsrc_bundle_edits = ResourceBundle.getBundle("edits", locale, new LanguageClassLoader());
	        rsrc_bundle_linkgroups = ResourceBundle.getBundle("linkgroups", locale, new LanguageClassLoader());
	        rsrc_bundle_meeting = ResourceBundle.getBundle("meeting", locale, new LanguageClassLoader());
	        rsrc_bundle_io = ResourceBundle.getBundle("io", locale, new LanguageClassLoader());
	        rsrc_bundle_movie = ResourceBundle.getBundle("movie", locale, new LanguageClassLoader());
		} catch(Exception e) {
			log.error("Exception...", e);
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
		        value = rsrc_bundle_ui.getString(key);
		        break;
			case MENUS_BUNDLE:
		        value = rsrc_bundle_menus.getString(key);
		        break;
			case POPUPS_BUNDLE:
		        value = rsrc_bundle_popups.getString(key);
		        break;
			case TOOLBARS_BUNDLE:
		        value = rsrc_bundle_toolbars.getString(key);
		        break;
			case DIALOGS_BUNDLE:
		        value = rsrc_bundle_dialogs.getString(key);
		        break;
			case PANELS_BUNDLE:
		        value = rsrc_bundle_panels.getString(key);
		        break;
			case TAGS_BUNDLE:
		        value = rsrc_bundle_tags.getString(key);
		        break;
			case STENCILS_BUNDLE:
		        value = rsrc_bundle_stencils.getString(key);
		        break;
			case EDITS_BUNDLE:
		        value = rsrc_bundle_edits.getString(key);
		        break;
			case LINKGROUPS_BUNDLE:
		        value = rsrc_bundle_linkgroups.getString(key);
		        break;
			case MEETING_BUNDLE:
		        value = rsrc_bundle_meeting.getString(key);
		        break;
			case IO_BUNDLE:
		        value = rsrc_bundle_io.getString(key);
		        break;
			case MOVIE_BUNDLE:
		        value = rsrc_bundle_movie.getString(key);
		        break;
			}
		} catch(MissingResourceException mre) {
			log.error("Exception...", mre);
			value = "UNKNOWN STRING";
		}
		return value;
	}
}
