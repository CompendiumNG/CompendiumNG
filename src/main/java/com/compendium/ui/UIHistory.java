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

import java.util.Vector;

import com.compendium.*;
import com.compendium.core.datamodel.View;
import com.compendium.meeting.*;

/**
 * Holds a list of all views visited
 *
 * @author Michelle Bachler
 */
public class UIHistory {


	/** Holds a list of View instances of the views opened so far.*/
    private Vector 	vtHistory 		= null;

	/** The current index position within the history list.*/
	private int 	currentIndex 	= -1;

	/** The current limit on the number of Views held in the history list.*/
    private int 	limit 			= 100;

	/** Has the user just gone back in the history list?*/
	private boolean goneBack		= false;

	/** Has the user just gone forward in the history list?*/
	private boolean goneForward		= false;

	/** Has the user just jumped to a position in the history list?*/
	private boolean jumped			= false;


	/**
	 * Constructor.
	 */
	public UIHistory() {
		vtHistory = new Vector(51);
	}

	/**
	 * Removes all instances of the View from the history.
	 * @param view com.compendium.core.datamodel.View the view to remove from the history.
	 */
	public void remove(View view) {

		String sViewID = view.getId();
		int count = vtHistory.size();
		for (int i=0; i<count; i++) {
			View oView = (View)vtHistory.elementAt(i);
			if (oView.getId().equals(sViewID)) {
				vtHistory.removeElementAt(i);
				i--;
				count--;
			}
		}
		
		// should this adjust currentIndex?
	}


	/**
	 * Appends the new View to the history and makes this the current element.
	 * @param view com.compendium.core.datamodel.View the view to append to the history.
	 */
	public void add(View view) {

		if (currentIndex > -1) {
			View currentview  = (View)vtHistory.elementAt(currentIndex);

			boolean viewsMatch = false;
			if (view.getId().equals(currentview.getId()))
				viewsMatch = true;

			if ((goneBack || goneForward || jumped) && !viewsMatch) {
				return;
			}
			else if (goneBack && viewsMatch) {
				goneBack = false;
				return;
			}
			else if (goneForward && viewsMatch) {
				goneForward = false;
				return;
			}
			else if (jumped && viewsMatch) {
				jumped = false;
				return;
			}
			else if ( viewsMatch ) {
				return;
			}
		}

		if (ProjectCompendium.APP.oMeetingManager != null && ProjectCompendium.APP.oMeetingManager.captureEvents()
				&& (ProjectCompendium.APP.oMeetingManager.getMeetingType() == MeetingManager.RECORDING)) {
			ProjectCompendium.APP.oMeetingManager.addEvent(
					new MeetingEvent(ProjectCompendium.APP.oMeetingManager.getMeetingID(),
									 ProjectCompendium.APP.oMeetingManager.isReplay(),
									 MeetingEvent.VIEW_SELECTED_EVENT,
									 view,
									 view));
		}

		// IF WE ARE AT THE END OF THE HISTORY LIST, THEN JUST ADD TO HISTORY
		if (currentIndex == (vtHistory.size() - 1) ) {
			currentIndex++;
			vtHistory.addElement(view);
		}
		// IF WE ARE SOMEWHERE IN THE MIDDLE OF THE HISTORY LIST
		// REMOVE ALL FORWARD ITEMS
		else {
			currentIndex++;
			vtHistory.setElementAt(view, currentIndex);
			for (int i = currentIndex + 1; i < vtHistory.size();) {
				vtHistory.removeElementAt(i);
			}
		}
	}

	/**
	 * Clear the history and reset index.
	 */
	public void clear() {

		currentIndex =  -1;
		vtHistory.removeAllElements();
	}

	/**
	 * Return the forwards history.
	 * @return java.util.Vector, a list of the items forward of the current index position.
	 */
	public Vector getForwardHistory() {

		Vector forwardHistory = new Vector();
		int count = vtHistory.size();

		for (int i = currentIndex+1; i < count; i++) {
			forwardHistory.addElement(vtHistory.elementAt(i));
		}

		return forwardHistory;
	}

	/**
	 * Return the backwards history
	 * @return java.util.Vector, a list of the items backwards of the current index position.
	 */
	public Vector getBackHistory() {

		Vector backHistory = new Vector();

		for (int i = currentIndex-1; i >=0; i--) {
			backHistory.addElement(vtHistory.elementAt(i));
		}

		return backHistory;
	}

	/**
	 * This method returns whether it is possible to go forward
	 * @return boolean,  whether it is possible to go forward.
	 */
	public boolean canGoForward() {

		if (vtHistory.size() == 0)
			return false;

		//if last element in vtHistory
		if (currentIndex == (vtHistory.size() - 1) ) {
			return false;
		}

		return true;
	}

	/**
	 * This method returns whether it is possible to go back
	 * @return boolean,  whether it is possible to go back.
	 */
	public boolean canGoBack() {

		if (vtHistory.size() == 0)
			return false;

		//if last element in vtHistory
		if (currentIndex == 0) {
			return false;
		}

		return true;
	}

	/**
	 * Return the current index the history is on.
	 *
	 * @return int, the current index the history is on.
	 */
	public int getCurrentPosition() {

		return currentIndex;
	}

	/**
	 * Return whether it is possible to go the the given history index position
	 * @return index, the index position to test for.
	 * @return boolea, tue if it is possible to go the the given history index position, else false.
	 */
	public boolean goToHistoryItem(int index) {

		if (index == currentIndex)
			return false;

		if (index < 0 || index > vtHistory.size()-1)
			return false;

		jumped = true;
		currentIndex = index;
		return true;
	}

	/**
	 * This method returns the next object in the history.
	 * If the current object displayed is the last object in the history or
	 * if the history is empty then null is returned.
	 * @return com.compendium.core.datamodel.View, the next item in the history from the current position.
	 */
	public View goForward() {

		//if vtHistory is empty
		if (currentIndex == -1) {
			return null;
		}

		//if last element in vtHistory
		if (currentIndex == (vtHistory.size() - 1) ) {
			return null;
		}

		// if somehow the index gets in a muddle
		// go to the last item
		if (currentIndex > (vtHistory.size() - 1)) {
			currentIndex = (vtHistory.size() - 1);
		}

		currentIndex++;
		goneForward = true;
		
		return (View)vtHistory.elementAt(currentIndex);
	}

	/**
	 * This method returns the previous object in the History.
	 * If this is the first object in the History, then null is returned.
	 * @return com.compendium.core.datamodel.View, the previous item in the history to the current position.
	 */
	public View goBack() {

		//this is the first object. no previous object available
		if (currentIndex < 1) {
			return null;
		}

		if (currentIndex == 0) {
			return null;
		}

		currentIndex--;

		// if somehow the index gets in a muddle
		// go to the last item
		if (currentIndex > (vtHistory.size()-1)) {
			currentIndex = (vtHistory.size()-1);
		}
				
		goneBack = true;			
		return (View)vtHistory.elementAt(currentIndex);
	}
}
