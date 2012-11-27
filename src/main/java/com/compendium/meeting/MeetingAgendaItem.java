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
 * MeetingAgendaItem defines the data object for storing a meeting agenda item.
 *
 * @author	Michelle Bachler
 * @version	1.0
 */
public class MeetingAgendaItem extends MeetingItem {

	/** The sequence number of this item.*/
	protected float fNumber			= 0;

	/**
	 * Constructor for an agenda item object.
	 *
	 * @param sMeetingID the id of the meeting this event belongs to.
	 * @param sName the name/label for the agenda item.
	 * @param fNumber the sequence number for this agenda item.
	 */
	public MeetingAgendaItem(String sMeetingID, String sName, float fNumber) {
		super(sMeetingID, sName);
		this.fNumber = fNumber;
	}

	/**
	 * Return the label for this agenda item with the agenda item number prepended.
	 */
	public String getDisplayName() {
		return new String(fNumber+" "+sName); //$NON-NLS-1$
	}

	/**
	 * Set the sequence number for this agenda item.
	 *
	 * @param float fNumber the sequence number for this agenda item.
	 */
	public void setNumber(float fNumber) {
		this.fNumber = fNumber;
	}

	/**
	 * Return the sequence number for this agenda item.
	 */
	public float getNumber() {
		return this.fNumber;
	}
}
