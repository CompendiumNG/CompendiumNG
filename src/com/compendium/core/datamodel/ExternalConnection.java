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
 * The ExternalConnection object represents a connection to a MySQL database, or Jabber Server, or such.
 * This object must have its 'initialize' method called before it can read/write to the database.
 * Otherwise it is just a container class.
 *
 * @author Michelle Bachler
 */
public class ExternalConnection extends PCObject implements java.io.Serializable {

	/** String holding the conection profile name */
	protected String		sProfile			= "";

	/** String holding the UserID who set up this connection */
	protected String		sUserID				= "";

	/** int holding the connection type as listed in ICoreConstants, e.g. mysql, jabber etc.*/
	protected int			nType				= 0;

	/** Stirng holding the host name or ip address for the server.*/
	protected String		sServer				= "";

	/** String holding the server login name.*/
	protected String		sLogin				= "";

	/** String holding the server user password.*/
	protected String 		sPassword			= "";

	/**
	 * String holding various things depending on connection type.
	 * For MySQL, it holds the name of the default database.
	 * For Jabber it holds the user desired friendly name when seding messages.
	 */
	protected String		sName				= "";

	/** The port number to use.*/
	protected int			nPort				= 3306;

	/** The resource for a Jabber connection.*/
	protected String 		sResource			= "";

	/**
	 * Constructor, creates an empty new external conection object.
	 */
	public ExternalConnection() {}

	/**
	 * Constructor, creates a new external MySQL conection with the given details.
	 * Just used as a container class.
	 */
	public ExternalConnection(String sProfile, int nType, String sServer, String sLogin,
								String sPassword, String sName, int nPort) {

		this.sProfile = sProfile;
		this.nType = nType;
		this.sServer = sServer;
		this.sLogin = sLogin;
		this.sPassword = sPassword;
		this.sName = sName;
		this.nPort = nPort;
	}

	/**
	 * Constructor, creates a new external conection with the given details.
	 */
	public ExternalConnection(String sUserID, String sProfile, int nType, String sServer, String sLogin,
								String sPassword, String sName, int nPort, String sResource) {

		this.sUserID = sUserID;
		this.sProfile = sProfile;
		this.nType = nType;
		this.sServer = sServer;
		this.sLogin = sLogin;
		this.sPassword = sPassword;
		this.sName = sName;
		this.nPort = nPort;
		this.sResource = sResource;
	}

	/**
	 * The initialize method is called by the Model before adding the object to the cache.
	 *
	 * @param PCSession session, the session associated with this object.
	 * @param IMode model, the model this object belongs to.
	 */
	public void initialize(PCSession session, IModel model) {
		super.initialize(session, model);
	}

	/**
	 *	This method needs to be called on this object before the Model removes it from the cache.
	 */
	public void cleanUp() {
		super.cleanUp() ;
	}

	/**
	 * Returns the User id associated with this Connection
	 *
	 * @return the String of the User id associated with this Connection.
	 */
	public String getUserID() {
		return sUserID;
	}

	/**
	 * Sets the User id associated with this Connection
	 *
	 * @param String sUserID, the User id associated with this Connection
	 */
	public void setUserID(String sUserID) {
		this.sUserID = sUserID;
	}

	/**
	 * Returns the Profile name associated with this Connection
	 *
	 * @return the String of the profile name associated with this Connection.
	 */
	public String getProfile() {
		return sProfile;
	}

	/**
	 * Sets the Profile name associated with this Connection
	 *
	 * @param String sProfile, the profile name associated with this Connection
	 */
	public void setProfile(String sProfile) {
		this.sProfile = sProfile;
	}

	/**
	 * Returns the type of this Connection
	 *
	 * @return the int representing the type of this Connection.
	 */
	public int getType() {
		return nType;
	}

	/**
	 * Sets the type of this Connection
	 *
	 * @param int nType, the type of this Connection
	 */
	public void setType(int nType) {
		this.nType = nType;
	}

	/**
	 * Returns the Server name associated with this Connection
	 *
	 * @return the String of the Server name associated with this Connection.
	 */
	public String getServer() {
		return sServer;
	}

	/**
	 * Sets the Server name associated with this Connection
	 *
	 * @param String sServer, the Server name associated with this Connection
	 */
	public void setServer(String sServer) {
		this.sServer = sServer;
	}

	/**
	 * Returns the Login name associated with this Connection
	 *
	 * @return the String of the Login name associated with this Connection.
	 */
	public String getLogin() {
		return sLogin;
	}

	/**
	 * Sets the Login name associated with this Connection
	 *
	 * @param String sLogin, the Loginr name associated with this Connection
	 */
	public void setLogin(String sLogin) {
		this.sLogin = sLogin;
	}

	/**
	 * Returns the password associated with this Connection
	 *
	 * @return the String of the password associated with this Connection.
	 */
	public String getPassword() {
		return sPassword;
	}

	/**
	 * Sets the password associated with this Connection
	 *
	 * @param String sPassword, the password associated with this Connection
	 */
	public void setPassword(String sPassword) {
		this.sPassword = sPassword;
	}

	/**
	 * Returns the default database name if a MySQL connection, else various
	 *
	 * @return the name of the code
	 */
	public String getName() {
		return sName ;
	}

	/**
	 * The default database name if a MySQL connection, else various.
	 *
	 * @param sName
	 */
	public void setName(String sName) {
		this.sName = sName;
	}

	/**
	 * Returns the port associated with this Connection
	 *
	 * @return the int representing the port associated with this Connection.
	 */
	public int getPort() {
		return nPort;
	}

	/**
	 * Sets the port of this Connection
	 *
	 * @param int nPort, the port associated with this Connection
	 */
	public void setPort(int nPort) {
		this.nPort = nPort;
	}

	/**
	 * Returns the resource associated with this connection
	 *
	 * @return the name of the resource
	 */
	public String getResource() {
		return sResource;
	}

	/**
	 * The resource associated with this connection.
	 *
	 * @param sResource, the resource associated with this connection.
	 */
	public void setResource(String sResource) {
		this.sResource = sResource;
	}

	/**
	 * Create this record in the database.
	 *
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 */
	public void create() throws SQLException, ModelSessionException {

		if (oModel == null)
			throw new ModelSessionException("Model is null in View.initializeMembers");
		if (oSession == null) {
			oSession = oModel.getSession();
			if (oSession == null)
				throw new ModelSessionException("Session is null in View.initializeMembers");
		}

		oModel.getExternalConnectionService().createExternalConnection(oModel.getSession(), this);
	}

	/**
	 * Update this record in the database.
	 *
	 * @param String sProfile, the original profile name of the connection record to be updated.
	 * @param int nType, the original type of the connection record to be updated.
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 */
	public void update(String sProfile, int nType) throws SQLException, ModelSessionException {

		if (oModel == null)
			throw new ModelSessionException("Model is null in View.initializeMembers");
		if (oSession == null) {
			oSession = oModel.getSession();
			if (oSession == null)
				throw new ModelSessionException("Session is null in View.initializeMembers");
		}

		oModel.getExternalConnectionService().updateExternalConnection(oModel.getSession(), this, sProfile, nType);
	}

	/**
	 * Delete this record from the database.
	 *
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 */
	public void delete() throws SQLException, ModelSessionException {

		if (oModel == null)
			throw new ModelSessionException("Model is null in View.initializeMembers");
		if (oSession == null) {
			oSession = oModel.getSession();
			if (oSession == null)
				throw new ModelSessionException("Session is null in View.initializeMembers");
		}

		oModel.getExternalConnectionService().deleteExternalConnection(oModel.getSession(), this);
	}
}
