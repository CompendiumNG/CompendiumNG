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

import java.awt.event.KeyEvent;
import java.util.Enumeration;

import javax.help.CSH;
import javax.swing.ImageIcon;

import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.NodePosition;
import com.compendium.core.datamodel.NodeSummary;
import com.compendium.core.datamodel.View;

public class UINodeTypeManager {

	public static final String		QUESTION_STRING = "Question"; //$NON-NLS-1$
	public static final String		ANSWER_STRING = "Answer"; //$NON-NLS-1$
	public static final String		MAP_STRING = "Map"; //$NON-NLS-1$
	public static final String		MOVIEMAP_STRING = "Movie Map"; //$NON-NLS-1$
	public static final String		LIST_STRING = "List"; //$NON-NLS-1$
	public static final String		PRO_STRING = "Pro"; //$NON-NLS-1$
	public static final String		CON_STRING = "Con"; //$NON-NLS-1$
	public static final String		REFERENCE_STRING = "Reference"; //$NON-NLS-1$
	public static final String		NOTE_STRING = "Note"; //$NON-NLS-1$
	public static final String		DECISION_STRING = "Decision"; //$NON-NLS-1$
	public static final String		ARGUMENT_STRING = "Argument"; //$NON-NLS-1$
		
	public static String[] nodeTypeStrings = {
		QUESTION_STRING,
		ANSWER_STRING,
		MAP_STRING,
		MOVIEMAP_STRING,
		LIST_STRING,
		PRO_STRING,
		CON_STRING,
		REFERENCE_STRING,
		NOTE_STRING,
		DECISION_STRING,
		ARGUMENT_STRING};

	public static int[]	imgIndex = {
			IUIConstants.ISSUE_SM_ICON,
			IUIConstants.POSITION_SM_ICON,
			IUIConstants.MAP_SM_ICON,
			IUIConstants.MOVIEMAP_SM_ICON,
			IUIConstants.LIST_SM_ICON,
			IUIConstants.PRO_SM_ICON,
			IUIConstants.CON_SM_ICON,
			IUIConstants.REFERENCE_SM_ICON,
			IUIConstants.NOTE_SM_ICON,
			IUIConstants.DECISION_SM_ICON,
			IUIConstants.ARGUMENT_SM_ICON};
	
	/**
	 * Convert the given node type string (see nodeTypeStrings) into the node type int identifier.
	 * @param nodeString, the node type String to convert.
	 */
	public static int convertStringToNodeType (String nodeString){
		int iNodeType=0;

		if (nodeString.equals(QUESTION_STRING))
			iNodeType = ICoreConstants.ISSUE;
		else if (nodeString.equals(ANSWER_STRING))
			iNodeType = ICoreConstants.POSITION;
		else if (nodeString.equals(MAP_STRING))
			iNodeType = ICoreConstants.MAPVIEW;
		else if (nodeString.equals(MOVIEMAP_STRING))
			iNodeType = ICoreConstants.MOVIEMAPVIEW;
		else if (nodeString.equals(LIST_STRING))
			iNodeType = ICoreConstants.LISTVIEW;
		else if (nodeString.equals(PRO_STRING))
			iNodeType = ICoreConstants.PRO;
		else if (nodeString.equals(CON_STRING))
			iNodeType = ICoreConstants.CON;
		else if (nodeString.equals(REFERENCE_STRING))
			iNodeType = ICoreConstants.REFERENCE;
		else if (nodeString.equals(NOTE_STRING))
			iNodeType = ICoreConstants.NOTE;
		else if (nodeString.equals(DECISION_STRING))
			iNodeType = ICoreConstants.DECISION;
		else if (nodeString.equals(ARGUMENT_STRING))
			iNodeType = ICoreConstants.ARGUMENT;

		return (iNodeType);
	}	
		
	/**
	 * Convert the given node type string (see nodeTypeStrings) into the node type int identifier.
	 * @param nodeString, the node type String to convert.
	 */
	public static String convertNoteTypeToString(int type){
		String sString="Unknown"; //$NON-NLS-1$

	    switch (type) {
		case ICoreConstants.ISSUE:
			sString = QUESTION_STRING;
		    break;
		case ICoreConstants.POSITION:
			sString = ANSWER_STRING;
		    break;
		case ICoreConstants.ARGUMENT:
			sString = ARGUMENT_STRING;
			break;
		case ICoreConstants.REFERENCE:
			sString = REFERENCE_STRING;
		    break;
		case ICoreConstants.DECISION:
			sString = DECISION_STRING;
		    break;
		case ICoreConstants.NOTE:
			sString = NOTE_STRING;
		    break;
		case ICoreConstants.MAPVIEW:
			sString = MAP_STRING;
		    break;
		case ICoreConstants.MOVIEMAPVIEW:
			sString = MOVIEMAP_STRING;
		    break;
		case ICoreConstants.LISTVIEW:
			sString = LIST_STRING;
		    break;
		case ICoreConstants.PRO:
			sString = PRO_STRING;
		    break;
		case ICoreConstants.CON:
			sString = CON_STRING;
		    break;
	    }
	    
		return sString;
	}	
	
	/**
	 * Return the menu Mnemonic for the given node type.
	 * @param type the node type to return the Mnemonic for.
	 * @return char the Mnemonic for the given type or 0 if not found.
	 */
	public static char getMnemonicForNodeType(int type){
		char mnemonic='0';

	    switch (type) {
		case ICoreConstants.ISSUE:
			mnemonic = KeyEvent.VK_Q;
		    break;
		case ICoreConstants.POSITION:
			mnemonic = KeyEvent.VK_A;
		    break;
		case ICoreConstants.ARGUMENT:
			mnemonic = KeyEvent.VK_U;
			break;
		case ICoreConstants.REFERENCE:
			mnemonic = KeyEvent.VK_R;
		    break;
		case ICoreConstants.DECISION:
			mnemonic = KeyEvent.VK_D;
		    break;
		case ICoreConstants.NOTE:
			mnemonic = KeyEvent.VK_N;
		    break;
		case ICoreConstants.MAPVIEW:
			mnemonic = KeyEvent.VK_M;
		    break;
		case ICoreConstants.MOVIEMAPVIEW:
			mnemonic = KeyEvent.VK_O;
		    break;
		case ICoreConstants.LISTVIEW:
			mnemonic = KeyEvent.VK_L;
		    break;
		case ICoreConstants.PRO:
			mnemonic = KeyEvent.VK_P;
		    break;
		case ICoreConstants.CON:
			mnemonic = KeyEvent.VK_C;
		    break;
	    }
	    
		return mnemonic;
	}	
	
	/**
	 * Set the help context for the passed node depending on node type.
	 * @param type, the node type to set the help string for.
	 */
	public static void setHelp(UINode node, int type) {

	    switch (type) {
		case ICoreConstants.ISSUE:
		    CSH.setHelpIDString(node,"node.node_types"); //$NON-NLS-1$
		    break;
		case ICoreConstants.POSITION:
		    CSH.setHelpIDString(node,"node.node_types"); //$NON-NLS-1$
		    break;
		case ICoreConstants.ARGUMENT:
		    CSH.setHelpIDString(node,"node.node_types"); //$NON-NLS-1$
		    break;
		case ICoreConstants.REFERENCE:
		    CSH.setHelpIDString(node,"node.refimage"); //$NON-NLS-1$
		    break;
		case ICoreConstants.DECISION:
		    CSH.setHelpIDString(node,"node.node_types"); //$NON-NLS-1$
		    break;
		case ICoreConstants.NOTE:
		    CSH.setHelpIDString(node,"node.node_types"); //$NON-NLS-1$
		    break;
		case ICoreConstants.MAPVIEW:
		    CSH.setHelpIDString(node,"node.views"); //$NON-NLS-1$
		    break;
		case ICoreConstants.MOVIEMAPVIEW:
		    CSH.setHelpIDString(node,"node.movies");//$NON-NLS-1$
		    break;
		case ICoreConstants.LISTVIEW:
		    CSH.setHelpIDString(node,"node.views"); //$NON-NLS-1$
		    break;
		case ICoreConstants.PRO:
		    CSH.setHelpIDString(node,"node.node_types"); //$NON-NLS-1$
		    break;
		case ICoreConstants.CON:
		    CSH.setHelpIDString(node,"node.node_types"); //$NON-NLS-1$
		    break;
		case ICoreConstants.ISSUE_SHORTCUT:
		    CSH.setHelpIDString(node,"node.node_types"); //$NON-NLS-1$
		    break;
		case ICoreConstants.POSITION_SHORTCUT:
		case ICoreConstants.ARGUMENT_SHORTCUT:
		case ICoreConstants.REFERENCE_SHORTCUT:
		case ICoreConstants.DECISION_SHORTCUT:
		case ICoreConstants.NOTE_SHORTCUT:
		case ICoreConstants.MAP_SHORTCUT:
		case ICoreConstants.LIST_SHORTCUT:
		case ICoreConstants.PRO_SHORTCUT:
		case ICoreConstants.CON_SHORTCUT:
		    CSH.setHelpIDString(node,"node.shortcuts"); //$NON-NLS-1$
		    break;
		case ICoreConstants.TRASHBIN:
		    CSH.setHelpIDString(node,"basics.trashbin"); //$NON-NLS-1$
		    break;
	    }
	}	
	
	/**
	 * Get the Alt property string to use for the node icon area based on the node type passed. 
	 * @param nType
	 * @return
	 */
	public static String getNodeTypeDescription(int nType) {
		String label = "Unknown Node Type Icon"; //$NON-NLS-1$
	    switch (nType) {
		case ICoreConstants.ISSUE:
			label="Question Node"; //$NON-NLS-1$
		    break;

		case ICoreConstants.POSITION:
			label="Answer Node"; //$NON-NLS-1$
		    break;

		case ICoreConstants.ARGUMENT:
			label="Argument Node"; //$NON-NLS-1$
		    break;

		case ICoreConstants.REFERENCE:
			label="Reference Node"; //$NON-NLS-1$
		    break;

		case ICoreConstants.DECISION:
			label="Decision Node"; //$NON-NLS-1$
		    break;

		case ICoreConstants.NOTE:
			label="Note Node"; //$NON-NLS-1$
		    break;

		case ICoreConstants.MAPVIEW:
			label="Map Node"; //$NON-NLS-1$
		    break;

		case ICoreConstants.MOVIEMAPVIEW:
			label="Movie Map Node";  //$NON-NLS-1$
		    break;

		case ICoreConstants.LISTVIEW:
			label="List Node"; //$NON-NLS-1$
		    break;

		case ICoreConstants.PRO:
			label="Pro Node"; //$NON-NLS-1$
		    break;

		case ICoreConstants.CON:
			label="Con Node"; //$NON-NLS-1$
		    break;

		case ICoreConstants.ISSUE_SHORTCUT:
			label="Question Shortcut Node"; //$NON-NLS-1$
		    break;

		case ICoreConstants.POSITION_SHORTCUT:
			label="Answer Shortcut Node"; //$NON-NLS-1$
		    break;

		case ICoreConstants.ARGUMENT_SHORTCUT:
			label="Argument Shortcut Node"; //$NON-NLS-1$
		    break;

		case ICoreConstants.REFERENCE_SHORTCUT:
			label="Reference Shortcut Node"; //$NON-NLS-1$
		    break;

		case ICoreConstants.DECISION_SHORTCUT:
			label="Decision Shortcut Node"; //$NON-NLS-1$
		    break;

		case ICoreConstants.NOTE_SHORTCUT:
			label="Note Shortcut Node"; //$NON-NLS-1$
		    break;

		case ICoreConstants.MAP_SHORTCUT:
			label="Map Shortcut Node"; //$NON-NLS-1$
		    break;
		    
		case ICoreConstants.LIST_SHORTCUT:
			label="List Shortcut Node"; //$NON-NLS-1$
		    break;

		case ICoreConstants.PRO_SHORTCUT:
			label="Pro Shortcut Node"; //$NON-NLS-1$
		    break;

		case ICoreConstants.CON_SHORTCUT:
			label="Con Shortcut Node"; //$NON-NLS-1$
		    break;

		case ICoreConstants.TRASHBIN:
			label="Trashbin Node"; //$NON-NLS-1$
		    break;
	    }		
		return label;
	}

	/**
	 * Return the node type for the given key code. 
	 * @param nKeyCode the key code to process and return the node type for.
	 * @return the node type for the given key code.
	 */
	public static int getTypeForKeyCode(int nKeyCode) {
		int nType = -1;
		
		if(nKeyCode == KeyEvent.VK_P || nKeyCode == KeyEvent.VK_I 
				|| nKeyCode == KeyEvent.VK_A || nKeyCode == KeyEvent.VK_1 
				|| nKeyCode == KeyEvent.VK_EXCLAMATION_MARK) {
			nType = ICoreConstants.POSITION;
		}
		else if (nKeyCode == KeyEvent.VK_Q || nKeyCode == KeyEvent.VK_SLASH) { // There is no KeyCode for Question Mark
			nType = ICoreConstants.ISSUE;
		}
		else if(nKeyCode == KeyEvent.VK_U) {
			nType = ICoreConstants.ARGUMENT;
		}
		else if(nKeyCode == KeyEvent.VK_R ) {
			nType = ICoreConstants.REFERENCE;
		}
		else if(nKeyCode == KeyEvent.VK_D ) {
			nType = ICoreConstants.DECISION;
		}
		else if(nKeyCode == KeyEvent.VK_N) {
			nType = ICoreConstants.NOTE;
		}
		else if(nKeyCode == KeyEvent.VK_M) {
			nType = ICoreConstants.MAPVIEW;
		}
		else if(nKeyCode == KeyEvent.VK_O) {
			nType = ICoreConstants.MOVIEMAPVIEW;
		}
		else if(nKeyCode == KeyEvent.VK_L) {
			nType = ICoreConstants.LISTVIEW;
		}
		else if(nKeyCode == KeyEvent.VK_PLUS || nKeyCode == KeyEvent.VK_EQUALS) {
			nType = ICoreConstants.PRO;
		}
		else if(nKeyCode == KeyEvent.VK_MINUS) {
			nType = ICoreConstants.CON;
		}		

		return nType;
	}

	/**
	 * Return the node type for the given key pressed. 
	 * @param sKeyPressed the key pressed to process and return the node type for.
	 * @return the node type for the given key pressed.
	 */
	public static int getTypeForKeyPress(String sKeyPressed) {		
		int nType = -1;
		if(sKeyPressed.equalsIgnoreCase("p")) { //$NON-NLS-1$
			nType = ICoreConstants.POSITION;
		}
		else if (sKeyPressed.equalsIgnoreCase("q") || sKeyPressed.equals("?") || sKeyPressed.equals("/")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			nType = ICoreConstants.ISSUE;
		}
		else if (sKeyPressed.equalsIgnoreCase("i") || sKeyPressed.equalsIgnoreCase("a") || sKeyPressed.equals("!") || sKeyPressed.equals("1")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			nType = ICoreConstants.POSITION;
		}
		else if(sKeyPressed.equalsIgnoreCase("u")) { //$NON-NLS-1$
			nType = ICoreConstants.ARGUMENT;
		}
		else if(sKeyPressed.equalsIgnoreCase("r")) { //$NON-NLS-1$
			nType = ICoreConstants.REFERENCE;
		}
		else if(sKeyPressed.equalsIgnoreCase("d")) { //$NON-NLS-1$
			nType = ICoreConstants.DECISION;
		}
		else if(sKeyPressed.equalsIgnoreCase("n")) { //$NON-NLS-1$
			nType = ICoreConstants.NOTE;
		}
		else if(sKeyPressed.equalsIgnoreCase("m")) { //$NON-NLS-1$
			nType = ICoreConstants.MAPVIEW;
		}		
		else if(sKeyPressed.equalsIgnoreCase("o")) { //$NON-NLS-1$
			nType = ICoreConstants.MOVIEMAPVIEW;
		}
		else if(sKeyPressed.equalsIgnoreCase("l")) { //$NON-NLS-1$
			nType = ICoreConstants.LISTVIEW;
		}
		else if(sKeyPressed.equals("+") || sKeyPressed.equals("=")) { //$NON-NLS-1$ //$NON-NLS-2$
			nType = ICoreConstants.PRO;
		}
		else if(sKeyPressed.equals("-")) { //$NON-NLS-1$
			nType = ICoreConstants.CON;
		}

		return nType;
	}
	
	/**
	 * Get the Alt property string to use for the node icon area based on the node type passed. 
	 * @param nType
	 * @return
	 */
	public static String getShortcutKeyForType(int nType) {
		String shortcut = ""; //$NON-NLS-1$
	    switch (nType) {
		case ICoreConstants.ISSUE:
			shortcut="Q,?,/"; //$NON-NLS-1$
		    break;
		case ICoreConstants.POSITION:
			shortcut="I,A,P,!,1"; //$NON-NLS-1$
		    break;
		case ICoreConstants.ARGUMENT:
			shortcut="U"; //$NON-NLS-1$
		    break;
		case ICoreConstants.REFERENCE:
			shortcut="R"; //$NON-NLS-1$
		    break;
		case ICoreConstants.DECISION:
			shortcut="D"; //$NON-NLS-1$
		    break;
		case ICoreConstants.NOTE:
			shortcut="N"; //$NON-NLS-1$
		    break;
		case ICoreConstants.MAPVIEW:
			shortcut="M"; //$NON-NLS-1$
		    break;
		case ICoreConstants.MOVIEMAPVIEW:
			shortcut="O"; //$NON-NLS-1$
		    break;
		case ICoreConstants.LISTVIEW:
			shortcut="L"; //$NON-NLS-1$
		    break;
		case ICoreConstants.PRO:
			shortcut="+,="; //$NON-NLS-1$
		    break;
		case ICoreConstants.CON:
			shortcut="-"; //$NON-NLS-1$
		    break;
	    }
	    
		return shortcut;
	}

	/**
	 * User by the Shorthand parser to return the node type for the passed shorthand.
	 * @param charType the shorthand to process and return the type for.
	 */
	public static int getTypeForShortcutKey(String charType) {
		int nodeType = -1;
		
		if (charType.equals("+")) { //$NON-NLS-1$
			nodeType = ICoreConstants.PRO;
		} else if (charType.equals("-")) { //$NON-NLS-1$
			nodeType = ICoreConstants.CON;
		} else if (charType.equals("?")) { //$NON-NLS-1$
			nodeType = ICoreConstants.ISSUE;
		} else if (charType.equals("!")) { //$NON-NLS-1$
			nodeType = ICoreConstants.POSITION;
		} else if (charType.equals("[+]")) { //$NON-NLS-1$
			nodeType = ICoreConstants.PRO;
		} else if (charType.equals("[-]")) { //$NON-NLS-1$
			nodeType = ICoreConstants.CON;
		} else if (charType.equals("[?]") || charType.equalsIgnoreCase("[I]")  //$NON-NLS-1$ //$NON-NLS-2$
				|| charType.equalsIgnoreCase("[Q]")) { //$NON-NLS-1$
			nodeType = ICoreConstants.ISSUE;
		} else if (charType.equals("[!]") || charType.equalsIgnoreCase("[P]")  //$NON-NLS-1$ //$NON-NLS-2$
				|| charType.equalsIgnoreCase("[A]")) { //$NON-NLS-1$
			nodeType = ICoreConstants.POSITION;
		} else if (charType.equalsIgnoreCase("[D]")) { //$NON-NLS-1$
			nodeType = ICoreConstants.DECISION;
		} else if (charType.equalsIgnoreCase("[N]")) { //$NON-NLS-1$
			nodeType = ICoreConstants.NOTE;
		} else if (charType.equalsIgnoreCase("[R]")) { //$NON-NLS-1$
			nodeType = ICoreConstants.REFERENCE;
		} else if (charType.equalsIgnoreCase("[U]")) { //$NON-NLS-1$
			nodeType = ICoreConstants.ARGUMENT;
		} else if (charType.equalsIgnoreCase("[M]")) {  //$NON-NLS-1$
			nodeType = ICoreConstants.MAPVIEW;
		} else if (charType.equalsIgnoreCase("[L]")) { //$NON-NLS-1$
			nodeType = ICoreConstants.LISTVIEW;
		} else if (charType.equalsIgnoreCase("[L]")) { //$NON-NLS-1$
			nodeType = ICoreConstants.LISTVIEW;
		}

		
		return nodeType;
	}
	
	/**
	 * The triple store description type to return for the given node type
	 * Used by the Meeting package
	 * @param nNodeType the type to return the triplestore description for.
	 * @return the triple store description for the given node type.
	 */
	public static String getTripleStoreDescription(int nNodeType) {
		String type = ""; //$NON-NLS-1$
		switch(nNodeType) {
			case ICoreConstants.ISSUE:
			case ICoreConstants.ISSUE_SHORTCUT:
				type="Compendium-Question"; //$NON-NLS-1$
				break;
			case ICoreConstants.POSITION:
			case ICoreConstants.POSITION_SHORTCUT:
				type="Compendium-Answer"; //$NON-NLS-1$
				break;
			case ICoreConstants.ARGUMENT:
			case ICoreConstants.ARGUMENT_SHORTCUT:
				type="Compendium-Argument"; //$NON-NLS-1$
				break;
			case ICoreConstants.REFERENCE:
			case ICoreConstants.REFERENCE_SHORTCUT:
				type="Compendium-Reference"; //$NON-NLS-1$
				break;
			case ICoreConstants.DECISION:
			case ICoreConstants.DECISION_SHORTCUT:
				type="Compendium-Decision"; //$NON-NLS-1$
				break;
			case ICoreConstants.NOTE:
			case ICoreConstants.NOTE_SHORTCUT:
				type="Compendium-Note"; //$NON-NLS-1$
				break;
			case ICoreConstants.MAPVIEW:
			case ICoreConstants.MAP_SHORTCUT:
				type="Compendium-Map"; //$NON-NLS-1$
				break;
			case ICoreConstants.MOVIEMAPVIEW:
			case ICoreConstants.MOVIEMAP_SHORTCUT:
				type="Compendium-MovieMap"; //$NON-NLS-1$
				break;
			case ICoreConstants.LISTVIEW:
			case ICoreConstants.LIST_SHORTCUT:
				type="Compendium-List"; //$NON-NLS-1$
				break;
			case ICoreConstants.PRO:
			case ICoreConstants.PRO_SHORTCUT:
				type="Compendium-Pro"; //$NON-NLS-1$
				break;
			case ICoreConstants.CON:
			case ICoreConstants.CON_SHORTCUT:
				type="Compendium-Con"; //$NON-NLS-1$
				break;
			default :
				break;
		}
		
		return type;
	}
	
	/**
	 * Get a list of node contents information displayed for this node if it is a view (list or map).
	 * @param view the view node to update the contents information displayed for.
	 */
	public static String getTypesInformation(View view) {

		int general=0,listview=0,mapview=0,moviemapview=0;
		int issue=0,position=0,argument=0,pro=0,con=0,decision=0,reference=0,note=0;

		String sToDisplay = ""; //$NON-NLS-1$

		for(Enumeration e = view.getPositions();e.hasMoreElements();) {

			NodeSummary node = ((NodePosition)e.nextElement()).getNode();

			switch(node.getType()) {

				case(ICoreConstants.GENERAL):
					general++;
					break;
				case(ICoreConstants.LISTVIEW):
					listview++;
					break;
				case(ICoreConstants.MAPVIEW):
					mapview++;
					break;
				case(ICoreConstants.MOVIEMAPVIEW):
					moviemapview++;
					break;
				case(ICoreConstants.ISSUE):
					issue++;
					break;
				case(ICoreConstants.POSITION):
					position++;
					break;
				case(ICoreConstants.ARGUMENT):
					argument++;
					break;
				case(ICoreConstants.PRO):
					pro++;
					break;
				case(ICoreConstants.CON):
					con++;
					break;
				case(ICoreConstants.DECISION):
					decision++;
					break;
				case(ICoreConstants.REFERENCE):
					reference++;
					break;
				case(ICoreConstants.NOTE):
					note++;
					break;
			}
		}

		if (general > 0)
			sToDisplay += "general   = " + String.valueOf(general) + "\n"; //$NON-NLS-1$ //$NON-NLS-2$
		if (listview > 0)
			sToDisplay += "list      = " + String.valueOf(listview) + "\n"; //$NON-NLS-1$ //$NON-NLS-2$
		if (mapview > 0)
			sToDisplay += "map       = " + String.valueOf(mapview) + "\n"; //$NON-NLS-1$ //$NON-NLS-2$
		if (moviemapview > 0)
			sToDisplay += "movie map = " + String.valueOf(moviemapview) + "\n"; //$NON-NLS-1$ //$NON-NLS-2$
		if (issue > 0)
			sToDisplay += "question  = " + String.valueOf(issue) + "\n"; //$NON-NLS-1$ //$NON-NLS-2$
		if (position > 0)
			sToDisplay += "answer    = " + String.valueOf(position) + "\n"; //$NON-NLS-1$ //$NON-NLS-2$
		if (argument > 0)
			sToDisplay += "argument  = " + String.valueOf(argument) + "\n"; //$NON-NLS-1$ //$NON-NLS-2$
		if (pro > 0)
			sToDisplay += "pro       = " + String.valueOf(pro) + "\n"; //$NON-NLS-1$ //$NON-NLS-2$
		if (con > 0)
			sToDisplay += "con       = " + String.valueOf(con) + "\n"; //$NON-NLS-1$ //$NON-NLS-2$
		if (decision > 0)
			sToDisplay += "decision  = " + String.valueOf(decision) + "\n"; //$NON-NLS-1$ //$NON-NLS-2$
		if (reference > 0)
			sToDisplay += "reference = " + String.valueOf(reference) + "\n"; //$NON-NLS-1$ //$NON-NLS-2$
		if (note > 0)
			sToDisplay += "note      = " + String.valueOf(note); //$NON-NLS-1$

		return sToDisplay;
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
		    img = UIImages.getNodeIcon(IUIConstants.ISSUE_ICON);
		    break;

		case ICoreConstants.POSITION:
		    img = UIImages.getNodeIcon(IUIConstants.POSITION_ICON);
		    break;

		case ICoreConstants.ARGUMENT:
		    img = UIImages.getNodeIcon(IUIConstants.ARGUMENT_ICON);
		    break;

		case ICoreConstants.REFERENCE:
			img = UIImages.getNodeIcon(IUIConstants.REFERENCE_ICON);
		    break;

		case ICoreConstants.DECISION:
		    img = UIImages.getNodeIcon(IUIConstants.DECISION_ICON);
		    break;

		case ICoreConstants.NOTE:
		    img = UIImages.getNodeIcon(IUIConstants.NOTE_ICON);
		    break;
		    
		case ICoreConstants.MAPVIEW:
	    	img = UIImages.getNodeIcon(IUIConstants.MAP_ICON);
		    break;
		    
		case ICoreConstants.MOVIEMAPVIEW:	
			img = UIImages.getNodeIcon(IUIConstants.MOVIEMAP_ICON);
			break;
			
		case ICoreConstants.LISTVIEW:
		    img = UIImages.getNodeIcon(IUIConstants.LIST_ICON);
		    break;

		case ICoreConstants.PRO:
		    img = UIImages.getNodeIcon(IUIConstants.PRO_ICON);
		    break;

		case ICoreConstants.CON:
		    img = UIImages.getNodeIcon(IUIConstants.CON_ICON);
		    break;

		case ICoreConstants.ISSUE_SHORTCUT:
		    img = UIImages.getNodeIcon(IUIConstants.ISSUE_SHORTCUT_ICON);
		    break;

		case ICoreConstants.POSITION_SHORTCUT:
		    img = UIImages.getNodeIcon(IUIConstants.POSITION_SHORTCUT_ICON);
		    break;

		case ICoreConstants.ARGUMENT_SHORTCUT:
		    img = UIImages.getNodeIcon(IUIConstants.ARGUMENT_SHORTCUT_ICON);
		    break;

		case ICoreConstants.REFERENCE_SHORTCUT:
		    img = UIImages.getNodeIcon(IUIConstants.REFERENCE_SHORTCUT_ICON);
		    break;

		case ICoreConstants.DECISION_SHORTCUT:
		    img = UIImages.getNodeIcon(IUIConstants.DECISION_SHORTCUT_ICON);
		    break;

		case ICoreConstants.NOTE_SHORTCUT:
		    img = UIImages.getNodeIcon(IUIConstants.NOTE_SHORTCUT_ICON);
		    break;
		    
		case ICoreConstants.MAP_SHORTCUT:
		    img = UIImages.getNodeIcon(IUIConstants.MAP_SHORTCUT_ICON);
		    break;

		case ICoreConstants.MOVIEMAP_SHORTCUT:
		    img = UIImages.getNodeIcon(IUIConstants.MOVIEMAP_SHORTCUT_ICON);
		    break;
		    
		case ICoreConstants.LIST_SHORTCUT:
		    img = UIImages.getNodeIcon(IUIConstants.LIST_SHORTCUT_ICON);
		    break;

		case ICoreConstants.PRO_SHORTCUT:
		    img = UIImages.getNodeIcon(IUIConstants.PRO_SHORTCUT_ICON);
		    break;

		case ICoreConstants.CON_SHORTCUT:
		    img = UIImages.getNodeIcon(IUIConstants.CON_SHORTCUT_ICON);
		    break;

		case ICoreConstants.TRASHBIN:
		    img = UIImages.getNodeIcon(IUIConstants.TRASHBIN_ICON);
		    break;
	    }
	    return img;
	}	
	
	/**
	 * Return the standard size icon for the given node type.
	 * @param type, the node type to return the icon for.
	 * @return ImageIcon, the icon for the given node type.
	 */
	public static ImageIcon getNodeImage(int type, boolean isSmall) {

	    if (isSmall) {
	    	return getNodeImageSmall(type);
	    }

	    ImageIcon img = null;
	    switch (type) {
		case ICoreConstants.ISSUE:
		    img = UIImages.getNodeIcon(IUIConstants.ISSUE_ICON);
		    break;

		case ICoreConstants.POSITION:
		    img = UIImages.getNodeIcon(IUIConstants.POSITION_ICON);
		    break;

		case ICoreConstants.ARGUMENT:
		    img = UIImages.getNodeIcon(IUIConstants.ARGUMENT_ICON);
		    break;

		case ICoreConstants.REFERENCE:
			img = UIImages.getNodeIcon(IUIConstants.REFERENCE_ICON);
		    break;

		case ICoreConstants.DECISION:
		    img = UIImages.getNodeIcon(IUIConstants.DECISION_ICON);
		    break;

		case ICoreConstants.NOTE:
		    img = UIImages.getNodeIcon(IUIConstants.NOTE_ICON);
		    break;
		case ICoreConstants.MAPVIEW:
	    	img = UIImages.getNodeIcon(IUIConstants.MAP_ICON);
		    break;

		case ICoreConstants.MOVIEMAPVIEW:	
			img = UIImages.getNodeIcon(IUIConstants.MOVIEMAP_ICON);
			break;

		case ICoreConstants.LISTVIEW:
		    img = UIImages.getNodeIcon(IUIConstants.LIST_ICON);
		    break;

		case ICoreConstants.PRO:
		    img = UIImages.getNodeIcon(IUIConstants.PRO_ICON);
		    break;

		case ICoreConstants.CON:
		    img = UIImages.getNodeIcon(IUIConstants.CON_ICON);
		    break;

		case ICoreConstants.ISSUE_SHORTCUT:
		    img = UIImages.getNodeIcon(IUIConstants.ISSUE_SHORTCUT_ICON);
		    break;

		case ICoreConstants.POSITION_SHORTCUT:
		    img = UIImages.getNodeIcon(IUIConstants.POSITION_SHORTCUT_ICON);
		    break;

		case ICoreConstants.ARGUMENT_SHORTCUT:
		    img = UIImages.getNodeIcon(IUIConstants.ARGUMENT_SHORTCUT_ICON);
		    break;

		case ICoreConstants.REFERENCE_SHORTCUT:
		    img = UIImages.getNodeIcon(IUIConstants.REFERENCE_SHORTCUT_ICON);
		    break;

		case ICoreConstants.DECISION_SHORTCUT:
		    img = UIImages.getNodeIcon(IUIConstants.DECISION_SHORTCUT_ICON);
		    break;

		case ICoreConstants.NOTE_SHORTCUT:
		    img = UIImages.getNodeIcon(IUIConstants.NOTE_SHORTCUT_ICON);
		    break;

		case ICoreConstants.MAP_SHORTCUT:
		    img = UIImages.getNodeIcon(IUIConstants.MAP_SHORTCUT_ICON);
		    break;

		case ICoreConstants.MOVIEMAP_SHORTCUT:
		    img = UIImages.getNodeIcon(IUIConstants.MOVIEMAP_SHORTCUT_ICON);
		    break;
		    
		case ICoreConstants.LIST_SHORTCUT:
		    img = UIImages.getNodeIcon(IUIConstants.LIST_SHORTCUT_ICON);
		    break;

		case ICoreConstants.PRO_SHORTCUT:
		    img = UIImages.getNodeIcon(IUIConstants.PRO_SHORTCUT_ICON);
		    break;

		case ICoreConstants.CON_SHORTCUT:
		    img = UIImages.getNodeIcon(IUIConstants.CON_SHORTCUT_ICON);
		    break;

		case ICoreConstants.TRASHBIN:
		    img = UIImages.getNodeIcon(IUIConstants.TRASHBIN_ICON);
		    break;
	    }
	    return img;
	}	
	
	/**
	 * Return the small size icon for the given node type.
	 * @param type, the node type to return the icon for.
	 * @return ImageIcon, the icon for the given node type.
	 */
	public static ImageIcon getNodeImageSmall(int type) {
	    ImageIcon img = null;
	    
	    switch (type) {
		case ICoreConstants.ISSUE:
		    img = UIImages.getNodeIcon(IUIConstants.ISSUE_SM_ICON);
		    break;

		case ICoreConstants.POSITION:
		    img = UIImages.getNodeIcon(IUIConstants.POSITION_SM_ICON);
		    break;

		case ICoreConstants.ARGUMENT:
		    img = UIImages.getNodeIcon(IUIConstants.ARGUMENT_SM_ICON);
		    break;

		case ICoreConstants.REFERENCE:
		    img = UIImages.getNodeIcon(IUIConstants.REFERENCE_SM_ICON);
		    break;

		case ICoreConstants.DECISION:
		    img = UIImages.getNodeIcon(IUIConstants.DECISION_SM_ICON);
		    break;

		case ICoreConstants.NOTE:
		    img = UIImages.getNodeIcon(IUIConstants.NOTE_SM_ICON);
		    break;
		case ICoreConstants.MAPVIEW:
		    img = UIImages.getNodeIcon(IUIConstants.MAP_SM_ICON);
		    break;

		case ICoreConstants.MOVIEMAPVIEW:	
			img = UIImages.getNodeIcon(IUIConstants.MOVIEMAP_SM_ICON);
			break;

		case ICoreConstants.LISTVIEW:
		    img = UIImages.getNodeIcon(IUIConstants.LIST_SM_ICON);
		    break;

		case ICoreConstants.PRO:
		    img = UIImages.getNodeIcon(IUIConstants.PRO_SM_ICON);
		    break;

		case ICoreConstants.CON:
		    img = UIImages.getNodeIcon(IUIConstants.CON_SM_ICON);
		    break;

		case ICoreConstants.ISSUE_SHORTCUT:
		    img = UIImages.getNodeIcon(IUIConstants.ISSUE_SHORTCUT_SM_ICON);
		    break;

		case ICoreConstants.POSITION_SHORTCUT:
		    img = UIImages.getNodeIcon(IUIConstants.POSITION_SHORTCUT_SM_ICON);
		    break;

		case ICoreConstants.ARGUMENT_SHORTCUT:
		    img = UIImages.getNodeIcon(IUIConstants.ARGUMENT_SHORTCUT_SM_ICON);
		    break;

		case ICoreConstants.REFERENCE_SHORTCUT:
		    img = UIImages.getNodeIcon(IUIConstants.REFERENCE_SHORTCUT_SM_ICON);
		    break;

		case ICoreConstants.DECISION_SHORTCUT:
		    img = UIImages.getNodeIcon(IUIConstants.DECISION_SHORTCUT_SM_ICON);
		    break;

		case ICoreConstants.NOTE_SHORTCUT:
		    img = UIImages.getNodeIcon(IUIConstants.NOTE_SHORTCUT_SM_ICON);
		    break;

		case ICoreConstants.MAP_SHORTCUT:
		    img = UIImages.getNodeIcon(IUIConstants.MAP_SHORTCUT_SM_ICON);
		    break;
		    
		case ICoreConstants.MOVIEMAP_SHORTCUT:
		    img = UIImages.getNodeIcon(IUIConstants.MOVIEMAP_SHORTCUT_SM_ICON);
		    break;

		case ICoreConstants.LIST_SHORTCUT:
		    img = UIImages.getNodeIcon(IUIConstants.LIST_SHORTCUT_SM_ICON);
		    break;

		case ICoreConstants.PRO_SHORTCUT:
		    img = UIImages.getNodeIcon(IUIConstants.PRO_SHORTCUT_SM_ICON);
		    break;

		case ICoreConstants.CON_SHORTCUT:
		    img = UIImages.getNodeIcon(IUIConstants.CON_SHORTCUT_SM_ICON);
		    break;

		case ICoreConstants.TRASHBIN:
		    img = UIImages.getNodeIcon(IUIConstants.TRASHBIN_SM_ICON);
		    break;

		default:
		    img = UIImages.getNodeIcon(IUIConstants.REFERENCE_SM_ICON);
		    break;
	    }
	    return img;
	}
	
	
	/**
	 * Return the image index for the given node type.
	 * @param type the node type to return the image index for.
	 * @return the image index for the given node type
	 */
	public static int getImageIndexForType(int type) {

		int idx = 0;
		switch (type) {
			case ICoreConstants.ISSUE: {
				idx = IUIConstants.ISSUE_ICON;
				break;
			}
			case ICoreConstants.POSITION: {
				idx = IUIConstants.POSITION_ICON;
				break;
			}
			case ICoreConstants.ARGUMENT: {
				idx = IUIConstants.ARGUMENT_ICON;
				break;
			}
			case ICoreConstants.REFERENCE: {
				idx = IUIConstants.REFERENCE_ICON;
				break;
			}
			case ICoreConstants.DECISION: {
				idx = IUIConstants.DECISION_ICON;
				break;
			}
			case ICoreConstants.NOTE: {
				idx = IUIConstants.NOTE_ICON;
				break;
			}
			case ICoreConstants.MAPVIEW: {
				idx = IUIConstants.MAP_ICON;
				break;
			}
			case ICoreConstants.MOVIEMAPVIEW: {	
				idx = IUIConstants.MOVIEMAP_ICON;
				break;
			}
			case ICoreConstants.LISTVIEW: {
				idx = IUIConstants.LIST_ICON;
				break;
			}
			case ICoreConstants.PRO: {
				idx = IUIConstants.PRO_ICON;
				break;
			}
			case ICoreConstants.CON: {
				idx = IUIConstants.CON_ICON;
				break;
			}
			case ICoreConstants.ISSUE_SHORTCUT: {
				idx = IUIConstants.ISSUE_SHORTCUT_ICON;
				break;
			}
			case ICoreConstants.POSITION_SHORTCUT: {
				idx = IUIConstants.POSITION_SHORTCUT_ICON;
				break;
			}
			case ICoreConstants.ARGUMENT_SHORTCUT: {
				idx = IUIConstants.ARGUMENT_SHORTCUT_ICON;
				break;
			}
			case ICoreConstants.REFERENCE_SHORTCUT: {
				idx = IUIConstants.REFERENCE_SHORTCUT_ICON;
				break;
			}
			case ICoreConstants.DECISION_SHORTCUT: {
				idx = IUIConstants.DECISION_SHORTCUT_ICON;
				break;
			}
			case ICoreConstants.NOTE_SHORTCUT: {
				idx = IUIConstants.NOTE_SHORTCUT_ICON;
				break;
			}
			case ICoreConstants.MAP_SHORTCUT: {
				idx = IUIConstants.MAP_SHORTCUT_ICON;
				break;
			}
			case ICoreConstants.LIST_SHORTCUT: {
				idx = IUIConstants.LIST_SHORTCUT_ICON;
				break;
			}
			case ICoreConstants.PRO_SHORTCUT: {
				idx = IUIConstants.PRO_SHORTCUT_ICON;
				break;
			}
			case ICoreConstants.CON_SHORTCUT: {
				idx = IUIConstants.CON_SHORTCUT_ICON;
				break;
			}
			case ICoreConstants.TRASHBIN: {
				idx = IUIConstants.TRASHBIN_ICON;
				break;
			}
		}
		
		return idx;
	}
		
	/**
	 * Return the image index for the given node type.
	 * @param type
	 * @return the image index for the given node type
	 */
	public static int getSmallImageIndexForType(int type) {

		int idx = 0;
		switch (type) {
			case ICoreConstants.ISSUE: {
				idx = IUIConstants.ISSUE_SM_ICON;
				break;
			}
			case ICoreConstants.POSITION: {
				idx = IUIConstants.POSITION_SM_ICON;
				break;
			}
			case ICoreConstants.ARGUMENT: {
				idx = IUIConstants.ARGUMENT_SM_ICON;
				break;
			}
			case ICoreConstants.REFERENCE: {
				idx = IUIConstants.REFERENCE_SM_ICON;
				break;
			}
			case ICoreConstants.DECISION: {
				idx = IUIConstants.DECISION_SM_ICON;
				break;
			}
			case ICoreConstants.NOTE: {
				idx = IUIConstants.NOTE_SM_ICON;
				break;
			}
			case ICoreConstants.MAPVIEW: {
				idx = IUIConstants.MAP_SM_ICON;
				break;
			}
			case ICoreConstants.MOVIEMAPVIEW: {
				idx = IUIConstants.MOVIEMAP_SM_ICON;
				break;
			}
			case ICoreConstants.LISTVIEW: {
				idx = IUIConstants.LIST_SM_ICON;
				break;
			}
			case ICoreConstants.PRO: {
				idx = IUIConstants.PRO_SM_ICON;
				break;
			}
			case ICoreConstants.CON: {
				idx = IUIConstants.CON_SM_ICON;
				break;
			}
			case ICoreConstants.ISSUE_SHORTCUT: {
				idx = IUIConstants.ISSUE_SHORTCUT_SM_ICON;
				break;
			}
			case ICoreConstants.POSITION_SHORTCUT: {
				idx = IUIConstants.POSITION_SHORTCUT_SM_ICON;
				break;
			}
			case ICoreConstants.ARGUMENT_SHORTCUT: {
				idx = IUIConstants.ARGUMENT_SHORTCUT_SM_ICON;
				break;
			}
			case ICoreConstants.REFERENCE_SHORTCUT: {
				idx = IUIConstants.REFERENCE_SHORTCUT_SM_ICON;
				break;
			}
			case ICoreConstants.DECISION_SHORTCUT: {
				idx = IUIConstants.DECISION_SHORTCUT_SM_ICON;
				break;
			}
			case ICoreConstants.NOTE_SHORTCUT: {
				idx = IUIConstants.NOTE_SHORTCUT_SM_ICON;
				break;
			}
			case ICoreConstants.MAP_SHORTCUT: {
				idx = IUIConstants.MAP_SHORTCUT_SM_ICON;
				break;
			}
			case ICoreConstants.LIST_SHORTCUT: {
				idx = IUIConstants.LIST_SHORTCUT_SM_ICON;
				break;
			}
			case ICoreConstants.PRO_SHORTCUT: {
				idx = IUIConstants.PRO_SHORTCUT_SM_ICON;
				break;
			}
			case ICoreConstants.CON_SHORTCUT: {
				idx = IUIConstants.CON_SHORTCUT_SM_ICON;
				break;
			}
			case ICoreConstants.TRASHBIN: {
				idx = IUIConstants.TRASHBIN_SM_ICON;
				break;
			}
		}
		return idx;
	}
	
	public static boolean isShortcut(int type) {
		
		boolean isShort = false;
		switch (type) {
			case ICoreConstants.ISSUE_SHORTCUT: 
			case ICoreConstants.POSITION_SHORTCUT: 
			case ICoreConstants.ARGUMENT_SHORTCUT: 
			case ICoreConstants.REFERENCE_SHORTCUT: 
			case ICoreConstants.DECISION_SHORTCUT: 
			case ICoreConstants.NOTE_SHORTCUT: 
			case ICoreConstants.MAP_SHORTCUT: 
			case ICoreConstants.LIST_SHORTCUT: 
			case ICoreConstants.PRO_SHORTCUT: 
			case ICoreConstants.CON_SHORTCUT: 
			case ICoreConstants.MOVIEMAP_SHORTCUT: {
				isShort = true;
				break;
			}
		}
		return isShort;
	}
}
