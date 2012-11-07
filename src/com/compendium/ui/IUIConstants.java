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

import com.compendium.LanguageProperties;

/**
 * This constants file holds all UI images and other UI constants.
 *
 * @author	Mohammed Sajid Ali / Michelle Bachler
 */
public interface IUIConstants  {

	/** Tool tip text to display if the projects data schema is up-to-date.*/
	public static final String PROJECT_SCHEMA_CORRECT				= LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "IUIConstants.schemaCorrect"); //$NON-NLS-1$

	/** Tool tip text to display if the projects data schema is out-of-date.*/
	public static final String PROJECT_SCHEMA_OLDER 				= LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "IUIConstants.schemaOlder"); //$NON-NLS-1$

	/** Tool tip text to display if the projects data schema is from a newer version of Compendium.*/
	public static final String PROJECT_SCHEMA_NEWER	 				= LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "IUIConstants.schemaNewer"); //$NON-NLS-1$

	/** Tool tip text to display if the projects data schema is unknown.*/
	public static final String PROJECT_SCHEMA_UNKNOWN 				= LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "IUIConstants.schemaUnknown"); //$NON-NLS-1$

	/** The character that are recognised when typing into a node label */
	//public static final StringBuffer KEYCHARS 					= new StringBuffer(" abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ‡¿·¡‰ƒÂ≈‚¬„√Ë»È…Í ÎÀÏÃÌÕÔœÓŒÚ“Û”ˆ÷Ù‘ı’˘Ÿ˙⁄¸‹˚€˝›ˇÁ«Ò—™∫0123456789<>.?:;,{[}]|=+~-_'\"#@£/!$%^&*()+\\°ø®∑`¥^¨\u00B4");

	/** Check characters for list navigation */
	public static final StringBuffer NAVKEYCHARS 					= new StringBuffer(" <>.?:;,{[}]|=+~-_'\"#@£/!$%^&*()+\\°ø®∑`¥^¨\u00B4\u00A7"); //$NON-NLS-1$


	/** Width of the project compendium application frame */
	public static final int			WIDTH							= 800;

	/** Height of the project compendium application frame */
	public static final int			HEIGHT							= 600;

	/** This is the colour to use for highlighting the default project and tag group in lists in the interface.*/
	public static final Color DEFAULT_COLOR 						= new Color(9, 192, 0);

	/** The value for display none option in outline view.*/
	public static final String DISPLAY_NONE 						= LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "IUIConstants.none"); //$NON-NLS-1$
	
	/** The value for display none option in outline view.*/
	public static final String DISPLAY_VIEWS_ONLY 					= LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "IUIConstants.viewsOnly"); //$NON-NLS-1$
	
	/** The value for display views and nodes option in outline view.*/
	public static final String DISPLAY_VIEWS_AND_NODES 				= LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "IUIConstants.viewsAndNodes"); //$NON-NLS-1$

	/** The outline view display text. */
	public final static String OUTLINE_VIEW							= LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "IUIConstants.outlineView"); //$NON-NLS-1$
	
	/** The unread view display text. */
	public final static String UNREAD_VIEW							= LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "IUIConstants.unreadView"); //$NON-NLS-1$

	/** The tags view display text. */
	public final static String TAGS_VIEW							= LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "IUIConstants.tagsView"); //$NON-NLS-1$
	
	/** The colour to use for node border for unread nodes.*/
	public static final Color 	UNREAD_BORDER_COLOR					= Color.red;

	/** The colour to use for node border for modified nodes.*/
	public static final Color 	MODIFIED_BORDER_COLOR				= Color.green;


	/**
	 * This array holds all the names of the images files used by the application for nodes, toolbar icons etc..
	 * but lists the node skin icons as png files.
	 * This was a quick fix for the new default skin needing to use png files to better draw the round images.
	 */
	public static final String DEFAULT_IMG_NAMES[] = {
		"new.png",			"open.png",		"save.png",		"list_sm.png", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		"map_sm.png",		"list.png",			"map.png",		"issue.png", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		"position.png",		"argument.png",		"plus.png",		"minus.png", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		"decision.png",		"reference.png",	"note.png",	"cut.png", "copy.png", "paste.png", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		"shortcut-to-list.png",		"shortcut-to-map.png",		"shortcut-to-issue.png", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		"shortcut-to-position.png",	"shortcut-to-argument.png",	"shortcut-to-plus.png", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		"shortcut-to-minus.png",	"shortcut-to-decision.png",	"shortcut-to-reference.png", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		"shortcut-to-note.png", "icon_16x16.jpg",  //$NON-NLS-1$ //$NON-NLS-2$
		"issue_sm.png", "position_sm.png", "argument_sm.png", "plus_sm.png", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		"minus_sm.png", "decision_sm.png", "reference_sm.png", "note_sm.png", "trashbin.png" , "trashbinfull.png", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		"close.png", "undo.png", "redo.png", "delete.png", "back.png", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		"forward.png", "codes.png", "ou-kmi_logo.gif", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		"word.gif", "excel.gif", "access.gif", "powerpoint.gif", "xml.gif", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		"text.gif", "acrobat.gif", "html.gif", "mediaplayer.gif", "java.gif", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		"trashbin_sm.png", "trashbinfull_sm.png", "help.png", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		"shortcut-to-list_sm.png",		"shortcut-to-map_sm.png",		"shortcut-to-issue_sm.png", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		"shortcut-to-position_sm.png",	"shortcut-to-argument_sm.png",	"shortcut-to-plus_sm.png", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		"shortcut-to-minus_sm.png",	"shortcut-to-decision_sm.png",	"shortcut-to-reference_sm.png", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		"shortcut-to-note_sm.png", //$NON-NLS-1$
		"word_sm.gif", "excel_sm.gif", "access_sm.gif", "powerpoint_sm.gif", "xml_sm.gif", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		"text_sm.gif", "acrobat_sm.gif", "html_sm.gif", "mediaplayer_sm.gif", "java_sm.gif", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		"search.png", "toNodes.png", "last.png", "first.png", "next.png", "previous.png", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		"email.gif", "email_sm.gif", "red_light.gif", "yellow_light.gif", "green_light.gif", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		"uparrow.gif", "downarrow.gif", "down.png", "magplus.png", "magminus.png", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		"zoomfull.png", "zoomfocus.png", "zoomfit.png", "imagerollover.png", "imagerolloveroff.png", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		"no_tool.png","circle_tool.png","line_tool.png","square_tool.png","pencil_tool.png","blank_toolbar_button.gif", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		"no_tool_select.png", "circle_tool_select.png", "line_tool_select.png", "square_tool_select.png", "pencil_tool_select.png", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		"scribble_on.png", "scribble_off.png", "erase_tool.png", "memetic_logo.jpg", "connect.png", "help2.gif", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		"play.png", "pause.png", "stop.png", "reset.gif", "upload.png", "resume.png", "record.png", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
		"meeting_map_big.gif", "meeting_map_small.gif", "movefront.png", "moveback.png", "refresh.png",  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		"udig16.gif", //$NON-NLS-1$
		"format_italic.png", "format_italic_selected.png", "format_bold.png", "format_bold_selected.png", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		"showtags.png", "showtags_selected.png", "showtext.png", "showtext_selected.png",  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		"showtrans.png", "showtrans_selected.png", "showweight.png", "showweight_selected.png", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		"smallicons.png",  "largeicons.png", "hideicons.png", "showicons.png", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		"wrapwidth.png", "inbox.png", "inbox_sm.png", "reference-internal.png", "reference-internal_sm.png", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		"textplus.png", "textminus.png", "textfull.png", "background-colour.png", "foreground-colour.png", "broken-image.png", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		"flashmeeting-logo.png", "inactive-user.png","leftarrow.gif","rightarrow.gif", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		"moviemap.png", "moviemap_sm.png", "shortcut-to-moviemap.png", "shortcut-to-moviemap_sm.png", "icon_32x32.jpg" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
	};
	

	/** This array holds all the names of the images files used by the application for nodes, toolbar icons etc..*/
	public static final String IMG_NAMES[] = {
		"new.png",			"open.png",		"save.png",		"list_sm.gif", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		"map_sm.gif",		"list.gif",			"map.gif",		"issue.gif", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		"position.gif",		"argument.gif",		"plus.gif",		"minus.gif", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		"decision.gif",		"reference.gif",	"note.gif",	"cut.png", "copy.png", "paste.png", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		"shortcut-to-list.gif",		"shortcut-to-map.gif",		"shortcut-to-issue.gif", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		"shortcut-to-position.gif",	"shortcut-to-argument.gif",	"shortcut-to-plus.gif", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		"shortcut-to-minus.gif",	"shortcut-to-decision.gif",	"shortcut-to-reference.gif", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		"shortcut-to-note.gif", //$NON-NLS-1$
		"icon_16x16.jpg", "issue_sm.gif", "position_sm.gif", "argument_sm.gif", "plus_sm.gif", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		"minus_sm.gif", "decision_sm.gif", "reference_sm.gif", "note_sm.gif", "trashbin.gif" , "trashbinfull.gif", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		"close.png", "undo.png", "redo.png", "delete.png", "back.png", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		"forward.png", "codes.png", "ou-kmi_logo.gif", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		"word.gif", "excel.gif", "access.gif", "powerpoint.gif", "xml.gif", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		"text.gif", "acrobat.gif", "html.gif", "mediaplayer.gif", "java.gif", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		"trashbin_sm.gif", "trashbinfull_sm.gif", "help.png", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		"shortcut-to-list_sm.gif",		"shortcut-to-map_sm.gif",		"shortcut-to-issue_sm.gif", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		"shortcut-to-position_sm.gif",	"shortcut-to-argument_sm.gif",	"shortcut-to-plus_sm.gif", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		"shortcut-to-minus_sm.gif",	"shortcut-to-decision_sm.gif",	"shortcut-to-reference_sm.gif", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		"shortcut-to-note_sm.gif", //$NON-NLS-1$
		"word_sm.gif", "excel_sm.gif", "access_sm.gif", "powerpoint_sm.gif", "xml_sm.gif", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		"text_sm.gif", "acrobat_sm.gif", "html_sm.gif", "mediaplayer_sm.gif", "java_sm.gif", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		"search.png", "toNodes.png", "last.png", "first.png", "next.png", "previous.png", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		"email.gif", "email_sm.gif", "red_light.gif", "yellow_light.gif", "green_light.gif", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		"uparrow.gif", "downarrow.gif", "down.png", "magplus.png", "magminus.png", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		"zoomfull.png", "zoomfocus.png", "zoomfit.png", "imagerollover.png", "imagerolloveroff.png", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		"no_tool.png","circle_tool.png","line_tool.png","square_tool.png","pencil_tool.png","blank_toolbar_button.gif", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		"no_tool_select.png", "circle_tool_select.png", "line_tool_select.png", "square_tool_select.png", "pencil_tool_select.png", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		"scribble_on.png", "scribble_off.png", "erase_tool.png", "memetic_logo.jpg", "connect.png", "help2.gif", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		"play.png", "pause.png", "stop.png", "reset.gif", "upload.png", "resume.png", "record.png", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
		"meeting_map_big.gif", "meeting_map_small.gif", "movefront.png", "moveback.png", "refresh.png", "udig16.gif", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		"format_italic.png", "format_italic_selected.png", "format_bold.png", "format_bold_selected.png", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		"showtags.png", "showtags_selected.png", "showtext.png", "showtext_selected.png",  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		"showtrans.png", "showtrans_selected.png", "showweight.png", "showweight_selected.png", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		"smallicons.png",  "largeicons.png", "hideicons.png", "showicons.png", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		"wrapwidth.png", "inbox.png", "inbox_sm.png", "reference-internal.png", "reference-internal_sm.png", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		"textplus.png", "textminus.png", "textfull.png", "background-colour.png", "foreground-colour.png", "broken-image.png", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		"flashmeeting-logo.png", "inactive-user.png","leftarrow.gif","rightarrow.gif", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		"moviemap.gif", "moviemap_sm.gif", "shortcut-to-moviemap.gif", "shortcut-to-moviemap_sm.gif", "icon_32x32.jpg" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
	};

	/** The total number of system images.*/
	public static final int NUM_IMAGES					= 167;

	/** Reference to the position in the array of the 'new' item icon image.*/
	public static final int NEW_ICON					= 0;

	/** Reference to the position in the array of the 'open' item icon image.*/
	public static final int OPEN_ICON					= 1;

	/** Reference to the position in the array of the 'save' icon image.*/
	public static final int SAVE_ICON					= 2;


	/** Reference to the position in the array of the small node list icon image.*/
	public static final int LIST_SM_ICON				= 3;

	/** Reference to the position in the array of the small node map icon image.*/
	public static final int MAP_SM_ICON					= 4;

	/** Reference to the position in the array of the node list icon image.*/
	public static final int LIST_ICON					= 5;

	/** Reference to the position in the array of the node map icon image.*/
	public static final int MAP_ICON					= 6;

	/** Reference to the position in the array of the node issue icon image.*/
	public static final int ISSUE_ICON					= 7;

	/** Reference to the position in the array of the node position icon image.*/
	public static final int POSITION_ICON				= 8;

	/** Reference to the position in the array of the node argument icon image.*/
	public static final int ARGUMENT_ICON				= 9;

	/** Reference to the position in the array of the node pro icon image.*/
	public static final int PRO_ICON					= 10;

	/** Reference to the position in the array of the node plus icon image.*/
	public static final int PLUS_ICON					= 10;

	/** Reference to the position in the array of the node con icon image.*/
	public static final int CON_ICON					= 11;

	/** Reference to the position in the array of the node minus icon image.*/
	public static final int MINUS_ICON					= 11;

	/** Reference to the position in the array of the node decision icon image.*/
	public static final int DECISION_ICON				= 12;

	/** Reference to the position in the array of the node reference icon image.*/
	public static final int REFERENCE_ICON				= 13;

	/** Reference to the position in the array of the node note icon image.*/
	public static final int NOTE_ICON					= 14;


	/** Reference to the position in the array of the 'cut' icon image.*/
	public static final int CUT_ICON					= 15;

	/** Reference to the position in the array of the 'copy' icon image.*/
	public static final int COPY_ICON					= 16;

	/** Reference to the position in the array of the 'paste' icon image.*/
	public static final int PASTE_ICON					= 17;


	// the shortcut icons
	/** Reference to the position in the array of the node shortcut list icon image.*/
	public static final int LIST_SHORTCUT_ICON			= 18;

	/** Reference to the position in the array of the node shoirtcut map icon image.*/
	public static final int MAP_SHORTCUT_ICON			= 19;

	/** Reference to the position in the array of the node shortcut issue icon image.*/
	public static final int ISSUE_SHORTCUT_ICON			= 20;

	/** Reference to the position in the array of the node shortcut position icon image.*/
	public static final int POSITION_SHORTCUT_ICON		= 21;

	/** Reference to the position in the array of the node shortcut argument icon image.*/
	public static final int ARGUMENT_SHORTCUT_ICON		= 22;

	/** Reference to the position in the array of the node shortcut pro icon image.*/
	public static final int PRO_SHORTCUT_ICON			= 23;

	/** Reference to the position in the array of the node shortcut con icon image.*/
	public static final int CON_SHORTCUT_ICON			= 24;

	/** Reference to the position in the array of the node shortcut decision icon image.*/
	public static final int DECISION_SHORTCUT_ICON		= 25;

	/** Reference to the position in the array of the node shortcut reference icon image.*/
	public static final int REFERENCE_SHORTCUT_ICON		= 26;

	/** Reference to the position in the array of the node shortcut note icon image.*/
	public static final int NOTE_SHORTCUT_ICON			= 27;

	/** Reference to the position in the array of the pc icon image.*/
	public static final int PC_ICON						= 28;


	// SMALL ICONS
	/** Reference to the position in the array of the small node issue icon image.*/
	public static final int ISSUE_SM_ICON				= 29;

	/** Reference to the position in the array of the small node position icon image.*/
	public static final int POSITION_SM_ICON			= 30;

	/** Reference to the position in the array of the small node argument icon image.*/
	public static final int ARGUMENT_SM_ICON			= 31;

	/** Reference to the position in the array of the small node pro icon image.*/
	public static final int PRO_SM_ICON					= 32;

	/** Reference to the position in the array of the small node con icon image.*/
	public static final int CON_SM_ICON					= 33;

	/** Reference to the position in the array of the small node decision icon image.*/
	public static final int DECISION_SM_ICON			= 34;

	/** Reference to the position in the array of the small node reference icon image.*/
	public static final int REFERENCE_SM_ICON			= 35;

	/** Reference to the position in the array of the small node note icon image.*/
	public static final int NOTE_SM_ICON				= 36;


	/** Reference to the position in the array of the trashbin empty icon image.*/
	public static final int TRASHBIN_ICON				= 37;

	/** Reference to the position in the array of the trashbin full icon image.*/
	public static final int TRASHBINFULL_ICON			= 38;


	/** Reference to the position in the array of the 'close' icon image.*/
	public static final int CLOSE_ICON					= 39;

	/** Reference to the position in the array of the 'undo' icon image.*/
	public static final int UNDO_ICON					= 40;

	/** Reference to the position in the array of the 'redo' icon image.*/
	public static final int REDO_ICON					= 41;

	/** Reference to the position in the array of the 'delete' icon image.*/
	public static final int DELETE_ICON					= 42;

	/** Reference to the position in the array of the 'back' arrow icon image.*/
	public static final int BACK_ICON					= 43;

	/** Reference to the position in the array of the 'forward' arrow icon image.*/
	public static final int FORWARD_ICON				= 44;

	/** Reference to the position in the array of the 'tags' icon image.*/
	public static final int CODES_ICON					= 45;

	/** Reference to the position in the array of the KMI logo image.*/
	public static final int OUKMILOGO					= 46;

	/** Reference to the position in the array of the Word file icon image.*/
	public static final int WORD_ICON					= 47;

	/** Reference to the position in the array of the Exel file icon image.*/
	public static final int EXCEL_ICON					= 48;

	/** Reference to the position in the array of the Access file icon image.*/
	public static final int ACCESS_ICON					= 49;

	/** Reference to the position in the array of the PowerPoint file icon image.*/
	public static final int POWERPOINT_ICON				= 50;

	/** Reference to the position in the array of the XML file icon image.*/
	public static final int XML_ICON					= 51;

	/** Reference to the position in the array of the Text file icon image.*/
	public static final int TEXT_ICON					= 52;

	/** Reference to the position in the array of the Acrobat file icon image.*/
	public static final int ACROBAT_ICON				= 53;

	/** Reference to the position in the array of the HTML file icon image.*/
	public static final int HTML_ICON					= 54;

	/** Reference to the position in the array of the Movie file icon image.*/
	public static final int MOVIEPLAYER_ICON			= 55;

	/** Reference to the position in the array of the Java file icon image.*/
	public static final int JAVA_ICON					= 56;

	// SMALL ICONS .. more
	/** Reference to the position in the array of the small trashbin empty icon image.*/
	public static final int TRASHBIN_SM_ICON			= 57;

	/** Reference to the position in the array of the small trashbin full icon image.*/
	public static final int TRASHBINFULL_SM_ICON		= 58;

	/** Reference to the position in the array of the 'help' icon imaeg.*/
	public static final int HELP_ICON					= 59;


	// THE SMALL SHORTCUT ICONS
	/** Reference to the position in the array of the small node shortcut list icon image.*/
	public static final int LIST_SHORTCUT_SM_ICON		= 60;

	/** Reference to the position in the array of the small node shortcut map icon image.*/
	public static final int MAP_SHORTCUT_SM_ICON		= 61;

	/** Reference to the position in the array of the small node shortcut issue icon image.*/
	public static final int ISSUE_SHORTCUT_SM_ICON		= 62;

	/** Reference to the position in the array of the small node shortcut position icon image.*/
	public static final int POSITION_SHORTCUT_SM_ICON	= 63;

	/** Reference to the position in the array of the small node shortcut argument icon image.*/
	public static final int ARGUMENT_SHORTCUT_SM_ICON	= 64;

	/** Reference to the position in the array of the small node shortcut pro icon image.*/
	public static final int PRO_SHORTCUT_SM_ICON		= 65;

	/** Reference to the position in the array of the small node shortcut con icon image.*/
	public static final int CON_SHORTCUT_SM_ICON		= 66;

	/** Reference to the position in the array of the small node shortcut decision icon image.*/
	public static final int DECISION_SHORTCUT_SM_ICON	= 67;

	/** Reference to the position in the array of the small node shortcut reference icon image.*/
	public static final int REFERENCE_SHORTCUT_SM_ICON	= 68;

	/** Reference to the position in the array of the small node shortcut note icon image.*/
	public static final int NOTE_SHORTCUT_SM_ICON		= 69;


	// THE SMALL REFERENCE TYPE ICONS
	/** Reference to the position in the array of the small Word file icon image.*/
	public static final int WORD_SM_ICON				= 70;

	/** Reference to the position in the array of the small Excel file icon image.*/
	public static final int EXCEL_SM_ICON				= 71;

	/** Reference to the position in the array of the small Access file icon image.*/
	public static final int ACCESS_SM_ICON				= 72;

	/** Reference to the position in the array of the small PowerPoint file icon image.*/
	public static final int POWERPOINT_SM_ICON			= 73;

	/** Reference to the position in the array of the small XML file icon image.*/
	public static final int XML_SM_ICON					= 74;

	/** Reference to the position in the array of the small Text file icon image.*/
	public static final int TEXT_SM_ICON				= 75;

	/** Reference to the position in the array of the small Acrobat file icon image.*/
	public static final int ACROBAT_SM_ICON				= 76;

	/** Reference to the position in the array of the small HTML file icon image.*/
	public static final int HTML_SM_ICON				= 77;

	/** Reference to the position in the array of the small Movie file icon image.*/
	public static final int MOVIEPLAYER_SM_ICON			= 78;

	/** Reference to the position in the array of the small Java file icon image.*/
	public static final int JAVA_SM_ICON				= 79;


	/** Reference to the position in the array of the 'search' icon image.*/
	public static final int SEARCH_ICON					= 80;

	/** Reference to the position in the array of the 'To Nodes' icon image.*/
	public static final int TONODES_ICON				= 81;

	/** Reference to the position in the array of the 'Last' item icon image.*/
	public static final int LAST_ICON					= 82;

	/** Reference to the position in the array of the 'First' item icon image.*/
	public static final int FIRST_ICON					= 83;

	/** Reference to the position in the array of the 'Next' item icon image.*/
	public static final int NEXT_ICON					= 84;

	/** Reference to the position in the array of the 'Previous' item icon image.*/
	public static final int PREVIOUS_ICON				= 85;

	/** Reference to the position in the array of the email icon image.*/
	public static final int MAIL_ICON					= 86;

	/** Reference to the position in the array of the small email icon image.*/
	public static final int MAIL_SM_ICON				= 87;

	/** Reference to the position in the array of the 'Red Light' icon image.*/
	public static final int RED_LIGHT_ICON				= 88;

	/** Reference to the position in the array of the 'Yellow Light' icon image.*/
	public static final int YELLOW_LIGHT_ICON			= 89;

	/** Reference to the position in the array of the 'Green Light' icon image.*/
	public static final int GREEN_LIGHT_ICON			= 90;

	/** Reference to the position in the array of the 'Up Arrow' icon image.*/
	public static final int UP_ARROW_ICON				= 91;

	/** Reference to the position in the array of the 'Down Arrow' icon image.*/
	public static final int DOWN_ARROW_ICON				= 92;

	/** Reference to the position in the array of the 'Down' icon image.*/
	public static final int DOWN_ICON					= 93;

	/** Reference to the position in the array of the 'Zoom Out' icon image.*/
	public static final int ZOOM_OUT_ICON				= 94;

	/** Reference to the position in the array of the 'Zoom In' icon image.*/
	public static final int ZOOM_IN_ICON				= 95;

	/** Reference to the position in the array of the 'Zoom To Full' icon image.*/
	public static final int ZOOM_FULL_ICON				= 96;

	/** Reference to the position in the array of the 'Zoom and Focus' icon image.*/
	public static final int ZOOM_FOCUS_ICON				= 97;

	/** Reference to the position in the array of the 'Zoom To Fit' icon image.*/
	public static final int ZOOM_FIT_ICON				= 98;

	/** Reference to the position in the array of the 'Image Rollover On' icon image.*/
	public static final int IMAGE_ROLLOVER_ICON			= 99;

	/** Reference to the position in the array of the 'Image Rollover Off' icon image.*/
	public static final int IMAGE_ROLLOVEROFF_ICON		= 100;


	/** Reference to the draw arrow for no selected tool.*/
	public static final int NO_TOOL_ICON				= 101;

	/** Reference to the drawing circle tool image.*/
	public static final int CIRCLE_TOOL_ICON			= 102;

	/** Reference to the drawing line tool image.*/
	public static final int LINE_TOOL_ICON				= 103;

	/** Reference to the drawing sqaure tool image.*/
	public static final int SQUARE_TOOL_ICON			= 104;

	/** Reference to the drawing pencil tool image.*/
	public static final int PENCIL_TOOL_ICON			= 105;

	/** Reference to the blank transparent toolbar button (for draw colour etc).*/
	public static final int BLANK_TOOLBAR_BUTTON		= 106;

	/** Reference to the draw arrow for no selected tool when selected.*/
	public static final int NO_TOOL_SELECT_ICON			= 107;

	/** Reference to the drawing circle tool image when selected.*/
	public static final int CIRCLE_TOOL_SELECT_ICON		= 108;

	/** Reference to the drawing line tool image when selected.*/
	public static final int LINE_TOOL_SELECT_ICON		= 109;

	/** Reference to the drawing sqaure tool image when selected.*/
	public static final int SQUARE_TOOL_SELECT_ICON		= 110;

	/** Reference to the drawing pencil tool image when selected.*/
	public static final int PENCIL_TOOL_SELECT_ICON		= 111;

	/** Reference to the image used for turing on the scribble pad.*/
	public static final int SCRIBBLE_ON_ICON			= 112;

	/** Reference to the image used for turing off the scribble pad.*/
	public static final int SCRIBBLE_OFF_ICON			= 113;

	/** Reference to the image used for clearing the scribble pad.*/
	public static final int SCRIBBLE_CLEAR_ICON			= 114;

	/** Reference to the image used for displaying the Memetic project logo.*/
	public static final int MEMETICLOGO					= 115;

	/** Reference to the image used for the database admin toolbar icon.*/
	public static final int CONNECT_ICON				= 116;

	/** Reference to the position in the array of the plain 'help' icon image.*/
	public static final int HELP_PLAIN_ICON				= 117;

	/** Reference to the play icon image.*/
	public static final int PLAY_ICON					= 118;

	/** Reference to the pause icon image.*/
	public static final int PAUSE_ICON					= 119;

	/** Reference to the stop icon image.*/
	public static final int	STOP_ICON					= 120;

	/** Reference to the reset icon image.*/
	public static final int RESET_ICON					= 121;

	/** Reference to the upload icon image.*/
	public static final int UPLOAD_ICON					= 122;

	/** Reference to the resume (after a pause) icon image.*/
	public static final int RESUME_ICON					= 123;

	/** Reference to the record icon image.*/
	public static final int RECORD_ICON					= 124;

	/** Reference to the icon to use for meeting maps.*/
	public static final int MEETING_BIG					= 125;

	/** Reference to the icon to use for meeting maps.*/
	public static final int MEETING_SMALL				= 126;

	/** Reference to the icon to move the scribble layer to the front.*/
	public static final int MOVE_FRONT_ICON				= 127;

	/** Reference tothe icon to move the scribble layer to the back.*/
	public static final int MOVE_BACK_ICON				= 128;

	/** Reference to the icon to refresh the cached data from the database.*/
	public static final int REFRESH_CACHE_ICON			= 129;

	/** Reference to the icon for opening a udig map.*/
	public static final int UDIG_ICON					= 130;
	

	/** Reference to the icon for formatting text bold.*/
	public static final int FORMAT_ITALIC				= 131;

	/** Reference to the icon for formatting text bold.*/
	public static final int FORMAT_ITALIC_SELECTED		= 132;

	/** Reference to the icon for formatting text italic.*/
	public static final int FORMAT_BOLD					= 133;

	/** Reference to the icon for formatting text italic.*/
	public static final int FORMAT_BOLD_SELECTED		= 134;

	/** Reference to the icon for showing tags indicator.*/
	public static final int SHOW_TAGS					= 135;

	/** Reference to the icon for showing tags indicator.*/
	public static final int SHOW_TAGS_SELECTED			= 136;

	/** Reference to the icon for showing text indicator.*/
	public static final int SHOW_TEXT					= 137;

	/** Reference to the icon for showing text indicator.*/
	public static final int SHOW_TEXT_SELECTED			= 138;

	/** Reference to the icon for showing transclusion indicator.*/
	public static final int SHOW_TRANS					= 139;

	/** Reference to the icon for showing transclusion indicator.*/
	public static final int SHOW_TRANS_SELECTED			= 140;

	/** Reference to the icon for showing weight indicator.*/
	public static final int SHOW_WEIGHT					= 141;

	/** Reference to the icon for showing weight indicator.*/
	public static final int SHOW_WEIGHT_SELECTED		= 142;

	/** Reference to the icon for showing small icons.*/
	public static final int SMALL_ICONS					= 143;

	/** Reference to the icon for showing small icons.*/
	public static final int SMALL_ICONS_SELECTED		= 144;
	
	/** Reference to the icon for hiding node icons.*/
	public static final int HIDE_ICONS					= 145;

	/** Reference to the icon for hiding node icons.*/
	public static final int HIDE_ICONS_SELECTED			= 146;

	/** Reference to the icon for indicating wrap width.*/
	public static final int WRAP_WIDTH					= 147;

	/** Reference to the icon for the inbox node.*/
	public static final int INBOX						= 148;

	/** Reference to the icon for the inbox node.*/
	public static final int INBOX_SM					= 149;

	/** Reference to the icon for internal reference node.*/
	public static final int REFERENCE_INTERNAL_ICON		= 150;

	/** Reference to the icon for internal reference node.*/
	public static final int REFERENCE_INTERNAL_SM_ICON	= 151;
	
	/** Reference to the position in the array of the Text Plus icon image.*/
	public static final int TEXT_PLUS_ICON				= 152;

	/** Reference to the position in the array of the Text Minus icon image.*/
	public static final int TEXT_MINUS_ICON				= 153;

	/** Reference to the position in the array of the Text Full icon image.*/
	public static final int TEXT_FULL_ICON				= 154;	
	
	/** Reference to the position in the array of the background colour icon image.*/
	public static final int BACKGROUND_COLOUR			= 155;
	
	/** Reference to the position in the array of the foreground colour icon image.*/
	public static final int FOREGROUND_COLOUR			= 156;	
	
	/** Reference to the image to draw when an external image ref is wrong and the image can't be loaded.*/
	public static final int BROKEN_IMAGE_ICON			= 157;
	
	/** Reference to the icon to use for meeting maps.*/
	public static final int FLASHMEETING_ICON			= 158;	
	
	/** Reference to the icon for inactive users */
	public static final int INACTIVE_USER_ICON			= 159;

	/** Reference to the left arrow - small black */
	public static final int LEFT_ARROW_ICON				= 160;

	/** Reference to the right arrow - small black */
	public static final int RIGHT_ARROW_ICON			= 161;
	
	/** Reference to the position in the array of the node movie map icon image.*/
	public static final int MOVIEMAP_ICON				= 162;

	/** Reference to the position in the array of the small node movie map icon image.*/
	public static final int MOVIEMAP_SM_ICON			= 163;

	/** Reference to the position in the array of the shortcut node movie map icon image.*/
	public static final int MOVIEMAP_SHORTCUT_ICON		= 164;

	/** Reference to the position in the array of the small shortcut node movie map icon image.*/
	public static final int MOVIEMAP_SHORTCUT_SM_ICON	= 165;

	/** Reference to the position in the array of the 32x32 Compendium icon image.*/
	public static final int COMPENDIUM_ICON_32			= 166;

}

