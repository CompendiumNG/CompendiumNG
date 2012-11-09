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

package com.compendium.core.datamodel;

import java.util.*;
import java.util.Hashtable;

/**
 * This object store all the code and code groups information.
 *
 * @author	Michelle Bachler
 */
public class CodeCache {

	/** Holds a list of codes */
	private Hashtable		htCode 				= null;

	/**
	 * Holds a list of hashtables with the keys being the CodeGroupIDs.
	 * Currently the hastable for an individual code group contains two element keys:
	 * 'children' is mapped to a Vector of all Code object in the code group.
	 * 'group' is mapped to a Vector of code group information: 0=CodeGroupID, 1=Name.
	 */
	private Hashtable		htCodeGroup 		= null;

	/** Holds a list of all code not assigned to any group */
	private Hashtable		htUngroupedCodes 	= null;

	/**
	 * The constructor. Creates the hashtables used for caching the codes and code groups.
	 */
	public CodeCache() {

		htCode = new Hashtable(51);
		htCodeGroup = new Hashtable(51);
		htUngroupedCodes = new Hashtable(51);
	}


	/**
	 *  Clear the cache and initialize it with all the Codes in the current project.
	 */
	public void initializeCodeCache(Vector vtCodes) {
		htCode.clear();
		htCodeGroup.clear();
		htUngroupedCodes.clear();

		for(Enumeration e = vtCodes.elements();e.hasMoreElements();) {
			addCode( (Code)e.nextElement() );
		}
	}

	/**
	 * Returns a Code object with the given input Code ID
	 */
	public Code getCode(String sCodeID) {

		return (Code)htCode.get(sCodeID) ;
	}

	/**
	 * Return all the codes for this database
	 *
	 * @return Hashtable, of all the codes and thier names currently available.
	 */
	public Hashtable getCodesCheck() {

		return htCode;
	}

	/**
	 * Return all the codes in the cache.
	 *
	 * @return Enumeration, of all the code in the cache.
	 */
	public Enumeration getCodes() {

		return htCode.elements();
	}

	/**
	 * This method adds a code to the cache.
	 *
	 * @param Code code, the code to add to the cache
	 */
	public boolean addCode(Code code) {

		String sCodeID = code.getId();
		if (!htCode.containsKey(sCodeID)) {
			htCode.put(sCodeID, code) ;

			// UPDATE UNGROUPED CODES
			if (!htUngroupedCodes.containsKey(code.getId())) {
				htUngroupedCodes.put(sCodeID, code);
			}

			return true;
		}

		return false;
	}

	/**
	 * This method removes a code with the given Id from the cache.
	 *
 	 * @param Code code, the code to remove from the cache.
	 */
	public void removeCode(Code code) {

		String sCodeID = code.getId();

		// UPDATE CODES
		htCode.remove(sCodeID);

		// UPDATE UNGROUPED CODES
		htUngroupedCodes.remove(sCodeID);

		// UPDATE CODE GROUP CODES
		removeCodeGroupCodes(sCodeID);
	}

	/**
	 * This method replaces a code with the given Id in the cache.
	 *
	 * @param Code code, the code to replace in the cache.
	 */
	public void replaceCode(Code code) {

		String sCodeID = code.getId();

		// UPDATE CODES
		if (htCode.containsKey(sCodeID)) {
			htCode.put(sCodeID, code);
		}
		else {
			System.out.println("Code could not be replaced, not found id = "+sCodeID);
		}

		// UPDATE UNGROUPED CODES
		if (htUngroupedCodes.containsKey(sCodeID)) {
			htUngroupedCodes.put(sCodeID, code);
	}

		// UPDATE ALL CODE GROUPS CODES
		replaceCodeGroupCodes(sCodeID, code);
	}

	/**
	 * This method adds a Vector of ungrouped codes to the cache.
	 * Ungrouped codes are all those codes that have not been assigned to any code group.
	 *
	 * @param Vector vtUngroupedCodes, the Vector of ungrouped codes to add to the cache.
	 */
	public void addUngroupedCodes(Vector vtUngroupedCodes) {

		int nCount = vtUngroupedCodes.size();

		for (int i=0; i<nCount; i++) {
			Code code = (Code)vtUngroupedCodes.elementAt(i);
			htUngroupedCodes.put(code.getId(), code);
		}
	}

	/**
	 * Returns the Hashtable of information for the code group with the given id.
	 * Currently the hastable contains two element keys:
	 * 'children' is mapped to a Vector of all Code object in the code group.
	 * 'group' is mapped to a Vector of code group information: 0=CodeGroupID, 1=Name.
	 *
	 * @param String sCodeGroupID, the id of the code group to return.
	 * @return Hashtable, of all the codes for the given code group id.
	 */
	public Hashtable getCodeGroup(String sCodeGroupID) {
		Hashtable group = null;

		if (htCodeGroup.containsKey(sCodeGroupID))
			return (Hashtable)htCodeGroup.get(sCodeGroupID);

		return group;
	}

	/**
	 * Return all the ungrouped codes in the cache.
	 *
	 * @return Hashtable, of all codes that have not been assigned to any code group.
	 */
	public Hashtable getUngroupedCodes() {

		return htUngroupedCodes;
	}

	/**
	 * Return all the codes groups for this model
	 *
	 * @return Hastable, of all the code groups in the cache.
	 */
	public Hashtable getCodeGroups() {

		return htCodeGroup;
	}

	/**
	 * This method adds in a code group to the cache.
	 *
	 * @param String sCodeGroupID, the id of the code group to add to the cache.
	 * @param Vector vtGroup, the Vector of information about the code group.
	 * Currently the elements in the Vector are: 0=CodeGroupID, 1=Name
	 */
	public void addCodeGroup(String sCodeGroupID, Vector vtGroup) {

		// GREAT A HASHTABLE FOR THE GROUP VECTOR AND CHILDREN HASHTABLE
		if (!htCodeGroup.containsKey(sCodeGroupID)) {
			Hashtable table = new Hashtable(51);
			table.put("group", vtGroup);
			Hashtable children = new Hashtable(51);
			table.put("children", children);
			htCodeGroup.put(sCodeGroupID, table);
		}
	}

	/**
	 * This method replace a codegroup name for the given codegroup id in the cache.
	 *
	 * @param String sCodeGroupID, the id of the code group whose name to replace.
	 * @param String sName, the new name of the code group.
	 */
	public void replaceCodeGroupName(String sCodeGroupID, String sName) {

		if (htCodeGroup.containsKey(sCodeGroupID)) {
			Hashtable group = (Hashtable)htCodeGroup.get(sCodeGroupID);
			if (group.containsKey("group")) {
				Vector groupdata = (Vector)group.get("group");
				groupdata.setElementAt(sName, 1);
			}
		}
	}

	/**
	 * This method adds in a code into code group in the cache.
	 *
	 * @param String sCodeGroupID, the id of the code group to add the code to
	 * @param String sCodeID, the id of the code to add.
	 * @param Code code, the Code object to add to the code group with the given code group id.
	 */
	public void addCodeGroupCode(String sCodeGroupID, String sCodeID, Code code) {

		if (htCodeGroup.containsKey(sCodeGroupID)) {

			Hashtable group = (Hashtable)htCodeGroup.get(sCodeGroupID);
			if (group.containsKey("children")) {
				Hashtable children = (Hashtable)group.get("children");
				if (!children.containsKey(sCodeID))
					children.put(sCodeID, code);
			}
		}

		// UPDATE UNGROUPED CODES
		if (htUngroupedCodes.containsKey(sCodeID))
			htUngroupedCodes.remove(sCodeID);
	}

	/**
	 * This method removes a code group with the given id from the cache.
	 *
	 * @param String sCodeGroupID, the id of the cide group to remove from the cache.
	 */
	public void removeCodeGroup(String sCodeGroupID) {

		//move all codes to ungrouped codes
		if (htCodeGroup.containsKey(sCodeGroupID)) {
			Hashtable group = (Hashtable)htCodeGroup.get(sCodeGroupID);
			if (group.containsKey("children")) {
				Hashtable children = (Hashtable)group.get("children");
				for (Enumeration e = children.keys(); e.hasMoreElements();) {
					String sCodeID = (String)e.nextElement();
					removeCodeGroupCode(sCodeGroupID, sCodeID);
				}
			}
		}


		htCodeGroup.remove(sCodeGroupID);
	}

	/**
	 * This method removes a code from a certain code group in the cache.
	 *
	 * @param String sCodeGroupID, the id of the code group to remove the code from.
	 * @param String sCodeID, the id of the code to remove from the code group.
	 */
	public void removeCodeGroupCode(String sCodeGroupID, String sCodeID) {

		Code removedCode = null;
		if (htCodeGroup.containsKey(sCodeGroupID)) {
			Hashtable group = (Hashtable)htCodeGroup.get(sCodeGroupID);
			if (group.containsKey("children")) {
				Hashtable children = (Hashtable)group.get("children");
				removedCode = (Code)children.remove(sCodeID);
			}
		}

		// UPDATE UNGROUPED CODES - IF NOT IN ANOTHER CODE GROUP
		if (removedCode != null) {
			boolean bFound1 = false;
			for (Enumeration e = htCodeGroup.elements(); e.hasMoreElements();) {
				Hashtable group = (Hashtable)e.nextElement();
				if (group.containsKey("children")) {
					Hashtable children = (Hashtable)group.get("children");
					if (children.containsKey(sCodeID)) {
						bFound1 = true;
						break;
					}
				}
			}
			
			if (!bFound1) {
				boolean found = false;
				for (Enumeration e = htUngroupedCodes.elements(); e.hasMoreElements();) {
					Code code = (Code)e.nextElement();
					if (code.getId().equals(removedCode.getId())) {
						found = true;
					}
				}
				if (!found) {
					htUngroupedCodes.put(sCodeID, removedCode);
				}
			}
		}
	}

	/**
	 * This method removes a code from all groups it may be in, in the cache.
	 *
	 * @param String sCodeID, the id of the code to remove from all code groups it may be in.
	 */
	private void removeCodeGroupCodes(String sCodeID) {

		for (Enumeration e = htCodeGroup.elements(); e.hasMoreElements();) {
			Hashtable group = (Hashtable)e.nextElement();
			if (group.containsKey("children")) {
				Hashtable children = (Hashtable)group.get("children");
				if (children.containsKey(sCodeID)) {
					children.remove(sCodeID);
				}
			}
		}
	}

	/**
	 * This method replaces codes with the given code id in all groups in the cache that it may be in.
	 *
	 * @param String sCodeID, the id of the code to replace in all the code groups.
	 * @param Code code, the Code object to replace in all the code groups.
	 */
	private void replaceCodeGroupCodes(String sCodeID, Code code) {

		for (Enumeration e = htCodeGroup.elements(); e.hasMoreElements();) {
			Hashtable group = (Hashtable)e.nextElement();
			if (group.containsKey("children")) {
				Hashtable children = (Hashtable)group.get("children");
				if (children.containsKey(sCodeID))
					children.put(sCodeID, code);
			}
		}
	}

	/**
	 * Checks if a code with the passed name already exists in the data 
	 * and does not have the the given code id.
	 * @param sCodeID the code id to ignore
	 * @param sName the code name to check.
	 * @return true if a duplicate named code exists, else false
	 */
	public boolean codeNameExists(String sCodeID, String sName) {

		Code code = null;
		String innertext = "";
		for(Enumeration e = htCode.elements(); e.hasMoreElements(); ) {
			code = (Code)e.nextElement();
			innertext = code.getName();
			if (innertext.equals(sName) && !code.getId().equals(sCodeID))
				return true;
		}

		return false;
	}		
	
	/**
	 *	Clear and null hashtables to help with garbarge collection.
	 */
	public void cleanUp() {

		htCode.clear();
		htCode = null;

		htCodeGroup.clear();
		htCodeGroup = null;

		htUngroupedCodes.clear();
		htUngroupedCodes = null;
	}
}
