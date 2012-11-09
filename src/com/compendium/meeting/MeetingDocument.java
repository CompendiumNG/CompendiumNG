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

package com.compendium.meeting;


/**
 * MeetingDocument defines the data object for storing a document reference related to a meeting.
 *
 * @author	Michelle Bachler
 * @version	1.0
 */
public class MeetingDocument extends MeetingItem {

	/** The web address of the document. */
	protected String sURL			= ""; //$NON-NLS-1$


	/**
	 * Constructor for an attendee object.
	 *
	 * @param sMeetingID the id of the meeting this event belongs to.
	 * @param sName the name of the document.
	 */
	public MeetingDocument(String sMeetingID, String sName, String sURL) {
		super(sMeetingID, sName);
		this.sURL = sURL;
	}

	/**
	 * Set the web address of this document.
	 *
	 * @param sURL the web address of this document.
	 */
	public void setURL(String sURL) {
		this.sURL = sURL;
	}

	/**
	 * Return the web address of this document.
	 */
	public String getURL() {
		return this.sURL;
	}
}
