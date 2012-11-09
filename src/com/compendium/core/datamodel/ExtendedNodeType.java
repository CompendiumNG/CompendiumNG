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

/**
 * NOTE: THIS CLASS IS NOT BEING USED YET AND IS NOT COMPLETED
 *
 * @author Rema Natarajan
 */
public class ExtendedNodeType extends IdObject implements IExtendedNodeType {


	protected String	sName 			= "";
	protected String 	sDescription 	= "" ;
	protected int		nBaseType		= -1;
	protected String  	sIcon			= "";
	protected Hashtable	htCodes			= new Hashtable();

	public ExtendedNodeType(String id, String author, java.util.Date creationDate, java.util.Date modificationDate,
		String name, String description, int baseType, String icon) {

		super(id, author, creationDate, modificationDate) ;
		sName = name ;
		sDescription = description ;
		nBaseType = baseType ;
		sIcon = icon ;
	}


	/**
	 *	Returns the extended node type.
	 *
	 *	@return String, the extended node type
	 */
	public String getName() {
		return sName;
	}


	/**
	 *	Sets the extended node type to the new value, fires changes to local property change listeners
	 *	and updates the server.
	 *
	 *	@param extendedNodeType, the extended Node type
	 */
	public void setName(String name) {
		if (name.equals(sName))
			return ;

		setNameLocal(name) ;
		// TODO call server here
	}

	/**
	 *	Sets the extended node type to the new value, fires changes to local property change listeners
	 *
	 *	@param extendedNodeType, the extended Node type
	 *	@return String, the old value
	 */
	protected String setNameLocal(String name) {
		if (name.equals(sName))
			return sName ;
		String oldValue = sName ;
		sName = name ;
		firePropertyChange(IExtendedNodeType.NAME, oldValue, sName) ;
		return oldValue ;
	}


	/**
	 *	Returns the icon path
	 *
	 *	@return String, the icon path
	 */
	public String getIcon() {
		return sIcon;
	}

	/**
	 *	Sets the icon path
	 *	and fires changes to local listeners, and updates the server
	 *
	 *	@param iconPath, the icon path
	 */
	public void setIcon(String icon) {
		if (icon.equals(sIcon))
			return ;
		setIconLocal(icon) ;
		// call server here
	}

	/**
	 *	Sets the icon path
	 *	and fires changes to local listeners
	 *
	 *	@param iconPath, the icon path
	 *	@return String, the old value
	 */
	protected String setIconLocal(String icon) {
		if (icon.equals(sIcon))
			return sIcon ;
		String oldValue = sIcon ;
		sIcon = icon ;
		firePropertyChange(IExtendedNodeType.ICON, oldValue, sIcon) ;
		return sIcon ;
	}

	/**
	 *	Returns the base type
	 *
	 *	@return the base type
	 */
	public int getBaseType() {
		return nBaseType;
	}

	/**
	 *	Sets the base type, fires property change to local listeners, and updates the server
	 *
	 *	@param baseType, the integer base type
	 */
	public void setBaseType(int baseType) {
		if (baseType == nBaseType)
			return ;
		setBaseTypeLocal(baseType) ;
		// call server
	}

	/**
	 *	Sets the base type, fires property change to local listeners
	 *
	 *	@param baseType, the integer base type
	 *	@return the old value
	 */
	protected int setBaseTypeLocal(int baseType) {
		if (baseType == nBaseType)
			return nBaseType;
		int oldValue = nBaseType;
		nBaseType = baseType ;
		firePropertyChange(IExtendedNodeType.BASE_TYPE, oldValue, nBaseType) ;
		return oldValue ;
	}

	/**
	 *	Adds the given code to the list of references
	 *
	 *	@param code, the code to be added
	 *	@return the added code if successfully added
	 */
	public ICode addCode(ICode code) {
		return null;
	}

	/**
	 * Removes the reference to the given code.
	 *
	 * @param code The code to be removed.
	 * @return the removed code if it was successfully removed
	 * @exception java.util.NoSuchElementException
	 */
	public ICode removeCode(ICode code) throws NoSuchElementException {
		return null;
	}

	/**
	 * Removes the reference to the code with the given name.
	 *
	 * @param code The name of the code to be removed.
	 * @return the removed code if it was successfully removed
	 * @exception java.util.NoSuchElementException
	 */
	public ICode removeCode(String name) throws NoSuchElementException {
		return null;
	}

	/**
	 * Returns the referenced code with the given name
	 *
	 * @param name The name of the referenced code
	 * @return the referenced code if it was found
	 */
	public ICode getCode(String name) throws NoSuchElementException {
		return null;
	}

	/**
	 * Returns all the codes referenced by this node
	 *
	 * @return an array of all the codes referenced by this node
	 */
	public ICode[] getCodes() {
		return null;
	}
}
