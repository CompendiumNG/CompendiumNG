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

package com.compendium.core;

import java.util.*;
import java.text.*;

/**
 * This class has some useful date/time methods
 *
 * @author Michelle Bachler
 */
public class CoreCalendar {

	/** This is the date format currently used for the log files. */
	private final static String FULLDATETIMEFILE = "yyyy-MMMM-dd-(HH.mm)";

	/** This is the date format currently used for the log files. */
	private final static String FULLDATETIME = "dd-MMMM-yyyy_HH-mm-ss";

	/**
	 * This method returns a string for the given date in the given format.
	 * It allows for Locale.UK hour changes.
	 *
	 * @param Date date, the date to get the String representation for.
	 * @param String format, the format to use for the String representation of the given date.
	 * @return String, a String representation of the given date in the given format.
	 */
	// NEED TO EXPAND IN FUTURE FOR OTHER TIMEZONES
	public static synchronized String getLocaleDateString(Date oDate, String sFormat) {

		Locale locale = Locale.getDefault();
		if (locale.equals(Locale.UK)) {
			SimpleTimeZone pdt = new SimpleTimeZone(0, "GMT");
			pdt.setStartRule(Calendar.APRIL, 1, Calendar.SUNDAY, 2*60*60*1000);
			pdt.setEndRule(Calendar.OCTOBER, -1, Calendar.SUNDAY, 2*60*60*1000);

			SimpleDateFormat formatter =
			   (SimpleDateFormat)DateFormat.getDateTimeInstance
			   (DateFormat.LONG, DateFormat.LONG, Locale.UK);

			formatter.setTimeZone(pdt);
			formatter.applyPattern(sFormat);
			return formatter.format(oDate);
		}
		else {
			SimpleDateFormat formatter = new SimpleDateFormat(sFormat);
			String dateString = formatter.format(oDate).toString();
			return dateString;
		}
	}

	/**
	 * This method returns a Date for the current date/time.
	 * It allows for Locale.UK hour changes.
	 *
	 * @return Date, a Date representing the current date/time.
	 */
	// NEED TO EXPAND IN FUTURE FOR OTHER TIMEZONES
	public static synchronized Date getCurrentLocaleDate() {

		Locale locale = Locale.getDefault();
		if (locale.equals(Locale.UK)) {
			SimpleTimeZone pdt = new SimpleTimeZone(0, "GMT");
			pdt.setStartRule(Calendar.APRIL, 1, Calendar.SUNDAY, 2*60*60*1000);
			pdt.setEndRule(Calendar.OCTOBER, -1, Calendar.SUNDAY, 2*60*60*1000);

			GregorianCalendar oCalendar = new GregorianCalendar(pdt);
			return oCalendar.getTime();
		}
		else {
			GregorianCalendar oCalendar = new GregorianCalendar();
			return oCalendar.getTime();
		}
	}

	/**
	 * This method returns a Date for the given number of millisecond.
	 * It allows for Locale.UK hour changes.
	 *
	 * @return Date, a Date representing the given number of milliseconds.
	 */
	// NEED TO EXPAND IN FUTURE FOR OTHER TIMEZONES
	public static synchronized Date getLocaleDate(long millis) {

		Locale locale = Locale.getDefault();
		if (locale.equals(Locale.UK)) {
			SimpleTimeZone pdt = new SimpleTimeZone(0, "GMT");
			pdt.setStartRule(Calendar.APRIL, 1, Calendar.SUNDAY, 2*60*60*1000);
			pdt.setEndRule(Calendar.OCTOBER, -1, Calendar.SUNDAY, 2*60*60*1000);

			GregorianCalendar oCalendar = new GregorianCalendar(pdt);
			oCalendar.setTime(new Date(millis));
			return oCalendar.getTime();
		}
		else {
			GregorianCalendar oCalendar = new GregorianCalendar();
			oCalendar.setTime(new Date(millis));
			return oCalendar.getTime();
		}
	}

	/**
	 * This method returns a String for the passed date in the given format.
	 *
	 * @param Date date the date to convert to the given format.
	 * @param String format, the format to use for the String representation of the current date.
	 * @return String a String representation of the current date in the given format.
     * @see #getCurrentLocaleDate
     * @see #getLocaleDateString
	 */
	public static synchronized String getDateString(Date oDate, String sFormat) {

		return getLocaleDateString(oDate, sFormat);
	}

	/**
	 * This method returns a String for the current date in the given format.
	 *
	 * @param String format, the format to use for the String representation of the current date.
	 * @return String a String representation of the current date in the given format.
     * @see #getCurrentLocaleDate
     * @see #getLocaleDateString
	 */
	public static synchronized String getCurrentLocaleDateString(String sFormat) {

		Date oDate = getCurrentLocaleDate();
		return getLocaleDateString(oDate, sFormat);
	}

	/**
	 * This method returns the current Date.
	 *
	 * @return Date the current Date.
     * @see #getCurrentLocaleDate
	 */
	public static synchronized Date getCurrentDate() {

		return getCurrentLocaleDate();
	}

	/**
	 * This method returns a String for the current date in the full date format.
	 * The full date format is: dd-MMMM-yyyy_hh-mm-ss
	 *
	 * @return String a String representation of the current date in the full date format.
     * @see #getCurrentLocaleDateString
	 */
	public static synchronized String getCurrentDateStringFull() {
		return getCurrentLocaleDateString(FULLDATETIME);
	}

	/**
	 * This method returns a String for the current date in the full date format.
	 * The full date format is: dd-MMMM-yyyy_hh-mm-ss
	 *
	 * @return String a String representation of the current date in the full date format.
     * @see #getCurrentLocaleDateString
	 */
	public static synchronized String getCurrentDateStringFullForFile() {
		return getCurrentLocaleDateString(FULLDATETIMEFILE);
	}

	/**
	 * This method returns a Date for the given number of milliseconds.
	 * It uses <code>getLocaleDate</code>.
	 *
	 * @param long time, a given number of milliseconds.
	 * @return Date a Date object for the given number of milliseconds.
     * @see #getLocaleDate
	 */
	public static synchronized Date getDateFromTime(long time) {

		return getLocaleDate(time);
	}
}
