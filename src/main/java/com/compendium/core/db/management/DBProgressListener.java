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

package com.compendium.core.db.management;

/**
 * <code>DBProgressListener</code> is an interface you can implement to get progress events.
 *
 * @author  Michelle Bachler
 */
public interface DBProgressListener {

	/**
	 * Set the amount of progress items being counted.
	 *
	 * @param int nCount, the amount of progress items being counted.
	 */
    public void progressCount(int nCount);

	/**
	 * Indicate that progress has been updated
	 *
	 * @param int nIncrement, the amount to increment to progress
	 * @param String sMessage, the message to display to the user
	 */
    public void progressUpdate(int nIncrement, String sMessage);

	/**
	 * Indicate that progress has complete
	 */
    public void progressComplete();

	/**
	 * Indicate that progress has had a problem
	 * @param String sMessage, the message to display to the user.
	 */
    public void progressAlert(String sMessage);
}
