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

package com.compendium.ui.panels;

import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;

import com.compendium.*;

/**
 * This class draws a panel containing choiceboxes to edit/diplay a date and time.
 * It sublcasses UIDatePanel.
 *
 * @author Michelle Bachler
 */
public class UITimePanel extends UIDatePanel {

	/** The choice box for the hour information.*/
	protected JComboBox hourBox = null;

	/** The choice box for the minute information.*/
	protected JComboBox minuteBox = null;

	/** The original hour information.*/
	protected int originalHour = 0;

	/** The original date information.*/
	protected int originalMinute = 0;

	/** The current hour information.*/
	protected int hour = 0;

	/** The current minute information.*/
	protected int minute = 0;

	/**
	 * Constructor, does nothing.
	 */
	public UITimePanel() {}

	/**
	 * Constructor, takes a string which is the label to associate with this panel.
	 * @param String labelText, the label to associate with this panel.
	 */
	public UITimePanel(String labelText) {
		super();
		drawPanel(labelText);
		setDate((new Date()).getTime());
	}

	/**
	 * Draw this panel, with the given label.
	 * @param String labelText, the label to associate with this time panel.
	 */
	public void drawPanel(String labelText) {

		super.drawPanel(labelText);

		int i=0;

		JLabel label = new JLabel(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UITimePanel.at")+":"); //$NON-NLS-1$
		add(label);

		hourBox = new JComboBox();
		hourBox.addActionListener(this);
		for (i=1; i<25; i++) {
			hourBox.addItem(new Integer(i).toString());
		}
		add(hourBox);

		minuteBox = new JComboBox();
		minuteBox.addActionListener(this);
		for (i=0; i<60; i++) {
			if (i<10)
				minuteBox.addItem("0"+new Integer(i).toString()); //$NON-NLS-1$
			else
				minuteBox.addItem(new Integer(i).toString());
		}
		add(minuteBox);
	}

	// GETTERS AND SETTERS
	/**
	 * Set the current date and time displayed
	 * @param long dateMillis, date to set, in milliseconds
	 */
	public void setDate(long dateMillis) {

		if (dateMillis == 0)
			dateMillis = (new Date()).getTime();

		GregorianCalendar calendar = new GregorianCalendar();
		Date date = new Date();
		date.setTime(dateMillis);
		calendar.setTime(date);

		originalDay = calendar.get(Calendar.DATE);
		originalMonth = calendar.get(Calendar.MONTH);
		originalYear = calendar.get(Calendar.YEAR);
		originalHour = calendar.get(Calendar.HOUR_OF_DAY);
		originalMinute = calendar.get(Calendar.MINUTE);

		day = calendar.get(Calendar.DATE);
		month = calendar.get(Calendar.MONTH);
		year = calendar.get(Calendar.YEAR);
		hour = calendar.get(Calendar.HOUR_OF_DAY);
		minute = calendar.get(Calendar.MINUTE);

		dayBox.setSelectedIndex(day-1);
		monthBox.setSelectedIndex(month);
		yearBox.setSelectedItem(new Integer(year).toString());
		hourBox.setSelectedItem(new Integer(hour).toString());
		minuteBox.setSelectedItem(new Integer(minute).toString());
	}

	/**
	 * Return the date.
	 * @return GregorianCalendar, the current date entered
	 */
	public GregorianCalendar getDate() {

		GregorianCalendar calendar = null;
		if (day > 0 && month >= 0 && year > 0) {
			calendar = new GregorianCalendar(year, month, day, hour, minute);
		}

		return calendar;
	}


	// OTHER METHODS
	/**
	 * Check if the current date set is a valid date and return if true or false
	 * @return boolean, is the current date entered valid
	 */
	public boolean checkDate() {
		if (day > 0 && month >= 0 && year > 0 && hour > 0 && minute >= 0)
			return true;

		return false;
	}

	/**
	 * Enable/disable the date choide boxes.
	 * @param boolean state, true for enabled, false for disabled.
	 */
	public void setDateEnabled(boolean state) {
		dayBox.setEnabled(state);
		monthBox.setEnabled(state);
		yearBox.setEnabled(state);
		hourBox.setEnabled(state);
		minuteBox.setEnabled(state);
	}

	/**
	 * Return if the date set has been modified
	 * @return boolean, has the current date been modified
	 */
	public boolean dateChanged() {

		if (day != originalDay || month != originalMonth || year != originalYear
				|| hour != originalHour || minute != originalMinute) {
			return true;
		}

		return false;
	}

	/**
	 * Validate the date being entered each time a date element choice box is changed.
	 * @param e the ActionEvent object for this event.
	 */
	public void actionPerformed(ActionEvent e) {

		Object source = e.getSource();

		if (source.equals(dayBox) || source.equals(monthBox) || source.equals(yearBox)) {
			super.actionPerformed(e);
		}
		else if (source.equals(hourBox)) {
			hour = new Integer( (String)hourBox.getSelectedItem() ).intValue();
		}
		else if (source.equals(minuteBox)) {
			String sMinute = (String)minuteBox.getSelectedItem();
			if (sMinute.startsWith("0") && sMinute.length() == 2) //$NON-NLS-1$
				minute = new Integer( sMinute.substring(1) ).intValue();
			else
				minute = new Integer( sMinute ).intValue();
		}
	}
}
