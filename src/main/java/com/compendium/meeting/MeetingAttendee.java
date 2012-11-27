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
 * MeetingAttendee defines the data object for storing a meeting attendee.
 *
 * @author	Michelle Bachler
 * @version	1.0
 */
public class MeetingAttendee extends MeetingItem {

	/** The email address of the attendee. */
	protected String sEmail			= ""; //$NON-NLS-1$


	/**
	 * Constructor for an attendee object without an email address
	 *
	 * @param sMeetingID, the id of the meeting this event belongs to.
	 * @param sName, the name of the attendee.
	 */
	public MeetingAttendee(String sMeetingID, String sName) {
		super(sMeetingID, sName);
	}

	/**
	 * Constructor for an attendee object with an email address.
	 *
	 * @param sMeetingID the id of the meeting this event belongs to.
	 * @param sName the name of the attendee.
	 * @param sEmail the email address for this attendee.
	 */
	public MeetingAttendee(String sMeetingID, String sName, String sEmail) {
		super(sMeetingID, sName);
		this.sEmail = sEmail;
	}

	/**
	 * Set the email address of this attendee.
	 *
	 * @param sEmail the email address of this attendee.
	 */
	public void setEmail(String sEmail) {
		this.sEmail = sEmail;
	}

	/**
	 * Return the email address of this attendee.
	 */
	public String getEmail() {
		return this.sEmail;
	}
}
