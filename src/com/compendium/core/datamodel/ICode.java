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

import java.sql.SQLException;


/**
 * The Code object represents a tag in Project Compendium.
 * Tags are additional information elements attached to a particular node.
 * They allow you to notionally group nodes with similar ideas or concepts.
 * You can then use these tags to search for all nodes with certain tags.
 * This is useful, for example, in investigating node and concept relationships between various views.
 *
 * @author	rema and sajid /  Michelle Bachler
 */
public interface ICode extends IIdObject {

	/**
	 * Returns the identifier (unique name) of the code
	 *
	 * @return the name of the code
	 */
	public String getName();

	/**
	 * Updates the unique name of the code both locally and in the database.
	 *
	 * @param name The name of the code
	 * @exception java.sql.SQLExcpetion
	 */
	public void setName(String name) throws SQLException, ModelSessionException;

	/**
	 * Returns a description about the meaning of the code
	 *
	 * @return The description about the meaning of the code
	 */
	public String getDescription();

	/**
	 * Updates the description defining the meaning of the code both locally and in the database
	 *
	 * @param description The description about the meaning of the code
	 * @exception java.sql.SQLExcpetion
	 */
	public void setDescription(String description) throws SQLException, ModelSessionException;

	/**
	 * Returns the behavior of the code.
	 * NOTE: The concept of code behavior has not been implemented yet.
	 *
	 * @return String, a string representing the behvior of the code.
	 */
	public String getBehavior();

	/**
	 * Updates the behavior of the code both locally and in the database.
	 * NOTE: The concept of code behavior has not been implemented yet.
	 *
	 * @param String behavior, the behavior to set.
	 * @exception java.sql.SQLExcpetion
	 * @exception ModelSessionException
	 */
	public void setBehavior(String behavior) throws SQLException, ModelSessionException;
}
