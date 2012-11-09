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

import java.util.Date;
import java.sql.SQLException;


/**
 * The MediaIndex object defines the media timestamp offset of a node in a view in a specific meeting.
 * This is used to tie a Compendium map into a video taken during a meeting.
 *
 * @author	Michelle Bachler
 */
public class MediaIndex extends PCObject implements java.io.Serializable {

	/** The View id the node associated with this object is in.*/
	protected String		sViewID 				= "";

	/** The node id associated with this object*/
	protected String		sNodeID					= "";

	/** The meeting id associated with this object*/
	protected String		sMeetingID				= "";

	/** The Date this object was created.*/
	protected Date			dMediaIndex				= null;

	/** The date this object was created.*/
	protected Date			dCreationDate			= null;

	/** The date this object was last modified.*/
	protected Date			dModificationDate		= null;

	/**
	 * Constructor, creates a new media index object,
	 * defining the media timstamp for the given node in the given view, and tyhe given meeting
	 *
	 * @param String sViewID, The view id of the view in which the node is placed.
	 * @param String sNodeID, The node id for which the media index is defined.
	 * @param String sMeetingID, The meeting id for which the media index is defined.
	 * @param Date dMediaIndex, the MediaIndex for this node in this view, in this meeting.
	 * @param Date dCreated, the date this object was created.
	 * @param Date dModified, the date this object was last modified.
	 */
	public MediaIndex(String sViewID, String sNodeID, String sMeetingID, Date dMediaIndex, Date dCreated, Date dModified) {
		this.sViewID = sViewID;
		this.sNodeID = sNodeID;
		this.sMeetingID = sMeetingID;
		this.dMediaIndex = dMediaIndex;
		this.dCreationDate = dCreated;
		this.dModificationDate = dModified;
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
	 * Return a new MedaiIdenx object with the properties of this object.
	 */
	public MediaIndex getClone() {
		return new MediaIndex(sViewID, sNodeID, sMeetingID, dMediaIndex, dCreationDate, dModificationDate);
	}

	/**
	 * Returns the node id for which this object defines its media index
	 *
	 * @return String, the node id for which the this media index is defined.
	 */
	public String getNodeID() {
		return sNodeID;
	}

	/**
	 * Returns the view id in which the node is placed.
	 *
	 * @return String, the view id in which the node is placed.
	 */
	public String getViewID() {
		return sViewID;
	}

	/**
	 * Returns the meeting associated with this media index.
	 *
	 * @return String, the meeting id associated with this media index.
	 */
	public String getMeetingID() {
		return sMeetingID;
	}


	/**
	 *	Returns the creation date of this object.
	 *
	 *	@return Date, the date when this object was created.
	 */
	public Date getCreationDate() {
		return dCreationDate;
	}

	/**
	 * Sets the ModificationDate date of this object, in the local data ONLY.
	 *
	 * @param Date date, the date this object was last modified.
	 */
	public void setModificationDate(Date date) {
		dModificationDate = date;
	}

	/**
	 *	Returns the modification date of this node.
	 *
	 *	@return Date, the date when this node was last modified.
	 */
	public Date getModificationDate() {
		return dModificationDate;
	}

	/**
	 *	Returns the media index offset of this node, in this view, in this meeting
	 *
	 *	@return Date, the media index offset for this node, in this view, in this meeting.
	 */
	public Date getMediaIndex() {
		return dMediaIndex;
	}

	/**
	 * Sets the Media index offset of this object, in the local data AND THE DATABASE.
	 *
	 * @param Date date, the medai index offset for this node, in this view, in this meeting.
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 */
	public void setMediaIndex(Date dIndex) throws SQLException, ModelSessionException {
		dMediaIndex = dIndex;
		dModificationDate = new Date();

		if (oModel == null)
			throw new ModelSessionException("Model is null in MediaIndex.setMediaIndex");
		if (oSession == null) {
			oSession = oModel.getSession();
			if (oSession == null)
				throw new ModelSessionException("Session is null in MediaIndex.setMediaIndex");
		}

		oModel.getMeetingService().setMediaIndex(oModel.getSession(), this);
	}
}
