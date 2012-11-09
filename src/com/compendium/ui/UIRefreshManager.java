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

import java.util.Timer;
import java.util.TimerTask;
import java.util.Date;
import com.compendium.*;

/**
 * Controls the timer for refreshing the cached data when connected to a remote shared database server.
 *
 * @author Michelle Bachler
 */
public class UIRefreshManager {


	/** The number of seconds to run the timed refresh at.*/
	private int 	nRefreshTime 	= 10;

	/** Has the user just jumped to a position in the history list?*/
	private Timer 	oTimer			= null;

	/** The task to perform using the timer.*/
	private RefreshCache oTask		= null;

	/** is the timer running?.*/
	private boolean bTimerRunning	= false;

	/**
	 * Constructor.
	 */
	public UIRefreshManager() {}

	/**
	 * Set the number of seconds for the timer refresh interval.
	 * @param nSeconds the number of seconds to set as the refresh timer interval.
	 */
	public void setRefreshTime(int nSeconds) {
		nRefreshTime = nSeconds;
		if (bTimerRunning) {
			stopTimer();
			startTimer();
		}
	}

	/**
	 * Start the cache refresh timer.
	 */
	public boolean startTimer() {
		try {
			int nMillis = nRefreshTime*1000;
			oTimer = new Timer();
			oTask = new RefreshCache();
			oTimer.schedule(oTask, new Date(), nMillis);
			bTimerRunning = true;

			FormatProperties.refreshTimerRunning = true;
			FormatProperties.setFormatProp("timerRunning", "true"); //$NON-NLS-1$ //$NON-NLS-2$
			FormatProperties.saveFormatProps();

			return true;
		} catch (IllegalArgumentException iae) {
			iae.printStackTrace();
			stopTimer();
		} catch (IllegalStateException ise) {
			ise.printStackTrace();
			stopTimer();
		}
		return false;
	}

	/**
	 * Stop the cache refresh timer.
	 */
	public void stopTimer() {

		bTimerRunning = false;
		FormatProperties.refreshTimerRunning = false;
		FormatProperties.setFormatProp("timerRunning", "false"); //$NON-NLS-1$ //$NON-NLS-2$
		FormatProperties.saveFormatProps();

		if (oTimer != null) {
			oTimer.cancel();
			oTimer = null;
		}
		if (oTask != null) {
			oTask = null;
		}
	}

	/**
	 * Return if this timer is currently running..
	 */
	public boolean isTimerRunning() {
		return bTimerRunning;
	}

	/**
	 * This inner class extends TimerTask to perform the cache refresh.
	 */
	private class RefreshCache extends TimerTask {

		public RefreshCache() {}

		public void run() {
			ProjectCompendium.APP.checkProjectDirty();
		}
	}
}
