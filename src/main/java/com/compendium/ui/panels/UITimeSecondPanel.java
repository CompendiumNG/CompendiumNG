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
import java.sql.Time;
import javax.swing.*;

import com.compendium.*;

/**
 * This class draws a panel containing choiceboxes to edit/diplay a time including seconds.
 * It sublcasses UITimePanel.
 *
 * @author Michelle Bachler
 */
public class UITimeSecondPanel extends UITimePanel implements ActionListener {

	/** Item listener for when checkbox state changed.*/
	protected Vector<ItemListener> vtListeners = new Vector<ItemListener>();

	/** Action listener for when checkbox state changed.*/
	protected Vector<ActionListener> vtActionListeners = new Vector<ActionListener>();

	/** The choice box for the second information.*/
	protected JComboBox secondBox = null;

	/** The original date information.*/
	protected int originalSecond = 0;

	/** The current second information.*/
	protected int second = 0;
	
	/** This indiocates if the date should be drawn as well as the time.
	 * True for date and time, false for only time
	 */
	protected boolean bShowDate = true;
	
	/**
	 * Whether to draw short labels or long.
	 */
	protected boolean bShortLabels = false;

	/**
	 * Do nothing. 
	 */
	public UITimeSecondPanel() {}

	/**
	 * Constructor, takes a string which is the label to associate with this panel.
	 * @param labelText the label to associate with this panel.
	 * @param showDate indicates whether to display the date or just the time.
	 * True for date and time, false for just time
	 */
	public UITimeSecondPanel(String labelText, boolean showDate, boolean shortLabels) {
		this.bShowDate = showDate;
		this.bShortLabels = shortLabels;
		drawPanel(labelText);
		setDate((new Date()).getTime());
	}

	/**
	 * Draw this panel, with the given label.
	 * @param String labelText, the label to associate with this time panel.
	 */
	public void drawPanel(String labelText) {

		int i=0;
		JLabel label = new JLabel(labelText);
		add(label);

		dayBox = new JComboBox();
		dayBox.setEnabled(false);
		for (i=1; i<32; i++) {
			dayBox.addItem(new Integer(i).toString());
		}

		monthBox = UIDatePanel.createMonthBox();
		monthBox.setEnabled(false);

		yearBox = new JComboBox();
		yearBox.setEnabled(false);
		int year = 1995;
		for (i=0; i<30; i++) {
			yearBox.addItem(new Integer(year).toString());
			year++;
		}
		
		if (bShowDate) {
			add(monthBox);
			add(yearBox);
			add(dayBox);
		}
		
		if (this.bShortLabels) {
			label = new JLabel(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UITimeSecondPanel.hourShort")); //$NON-NLS-1$
		} else {
			label = new JLabel(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UITimeSecondPanel.hour")); //$NON-NLS-1$
		}
		add(label);

		hourBox = new JComboBox();
		//hourBox.addItemListener(this);
		hourBox.addActionListener(this);
		for (i=0; i<25; i++) {
			hourBox.addItem(new Integer(i).toString());
		}
		add(hourBox);

		if (this.bShortLabels) {
			label = new JLabel(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UITimeSecondPanel.minuteShort")); //$NON-NLS-1$
		} else {
			label = new JLabel(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UITimeSecondPanel.minute")); //$NON-NLS-1$
		}
		add(label);

		minuteBox = new JComboBox();
		//minuteBox.addItemListener(this);
		minuteBox.addActionListener(this);
		for (i=0; i<60; i++) {
			minuteBox.addItem(new Integer(i).toString());
		}
		add(minuteBox);

		if (this.bShortLabels) {
			label = new JLabel(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UITimeSecondPanel.secondShort")); //$NON-NLS-1$
		} else {
			label = new JLabel(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UITimeSecondPanel.second")); //$NON-NLS-1$
		}
		add(label);

		secondBox = new JComboBox();
		//secondBox.addItemListener(this);
		secondBox.addActionListener(this);
		for (i=0; i<60; i++) {
			secondBox.addItem(new Integer(i).toString());
		}
		add(secondBox);
	}

	// GETTERS AND SETTERS
	/**
	 * Set the current time displayed
	 * @param long dateMillis, time to set, in milliseconds
	 */
	public void setDate(long dateMillis) {

		long originalDate = dateMillis;

		if (dateMillis == 0)
			dateMillis = (new Date()).getTime();

		GregorianCalendar calendar = new GregorianCalendar();
		Date date = new Date();
		date.setTime(dateMillis);
		calendar.setTime(date);

		// I only want the date part to be today, but the time offset to be zero.
		if (dateMillis == 0) {
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
		}

		originalDay = calendar.get(Calendar.DATE);
		originalMonth = calendar.get(Calendar.MONTH);
		originalYear = calendar.get(Calendar.YEAR);
		originalHour = calendar.get(Calendar.HOUR_OF_DAY);
		originalMinute = calendar.get(Calendar.MINUTE);
		originalSecond = calendar.get(Calendar.SECOND);

		day = calendar.get(Calendar.DATE);
		month = calendar.get(Calendar.MONTH);
		year = calendar.get(Calendar.YEAR);
		hour = calendar.get(Calendar.HOUR_OF_DAY);

		minute = calendar.get(Calendar.MINUTE);
		second = calendar.get(Calendar.SECOND);

		if (this.bShowDate) {
			dayBox.setSelectedIndex(day-1);
			monthBox.setSelectedIndex(month);
			yearBox.setSelectedItem(new Integer(year).toString());
		}
		hourBox.setSelectedItem(new Integer(hour).toString());
		minuteBox.setSelectedItem(new Integer(minute).toString());
		secondBox.setSelectedItem(new Integer(second).toString());
	}

	/**
	 * Over-ride to do nothing.
	 * Use getTime instead.
	 * @return null.
	 */
	public GregorianCalendar getDate() {
		GregorianCalendar calendar = null;
		if (day > 0 && month >= 0 && year > 0 && hour >= 0 && minute >= 0 && second >=0) {
			calendar = new GregorianCalendar(year, month, day, hour, minute, second);
		}

		return calendar;
	}

	/**
	 * Set the time from the passed seconds.
	 * day, month, year are all 0 and not used/set using this.
	 * If there are more than a days worth of seconds, call setDate.
	 * @return
	 */
	public void setSeconds(long time) {	
		if (time > (24*60*60)) {
			setDate(time);
			return;
		}
		
		int hours = 0;
		int minutes = 0;
		int seconds = 0;
	
		if (time < 60) {
			seconds = new Long(time).intValue();
		} else {
			minutes = (new Long(time).intValue())/60;
			seconds = (new Long(time).intValue())%60;
			if (minutes > 59) {
				hours = minutes/60;
				minutes = minutes%60;
			}
		} 
		
		hour = hours;
		minute = minutes;
		second = seconds;

		hourBox.setSelectedItem(new Integer(hour).toString());
		minuteBox.setSelectedItem(new Integer(minute).toString());
		secondBox.setSelectedItem(new Integer(second).toString());			
	}
	
	/**
	 * Return, in seconds, the current time for the day.
	 * i.e. return the hours, minutes, seconds as a seconds timestamp.
	 * @return
	 */
	public long getSeconds() {
		return second+(minute*60)+(hour*60*60);
	}
	
	// OTHER METHODS
	/**
	 * Check if the current time set is a valid time and return if true or false
	 * @return boolean, is the current date entered valid
	 */
	public boolean checkDate() {
		if (hour >= 0 || minute >= 0 || second >= 0)
			return true;

		return false;
	}

	/**
	 * Enable/disable the time choice boxes.
	 * @param boolean state, true for enabled, false for disabled.
	 */
	public void setDateEnabled(boolean state) {
		hourBox.setEnabled(state);
		minuteBox.setEnabled(state);
		secondBox.setEnabled(state);
	}

	/**
	 * Return if the time set has been modified
	 * @return boolean, has the current date been modified
	 */
	public boolean dateChanged() {

		if (hour != originalHour || minute != originalMinute || second != originalSecond) {
			return true;
		}

		return false;
	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		if (source.equals(hourBox)) {
			hour = new Integer( (String)hourBox.getSelectedItem() ).intValue();
		}
		else if (source.equals(minuteBox)) {
			String sMinute = (String)minuteBox.getSelectedItem();
			if (sMinute.startsWith("0") && sMinute.length() == 2) //$NON-NLS-1$
				minute = new Integer( sMinute.substring(1) ).intValue();
			else
				minute = new Integer( sMinute ).intValue();
		}
		else if (source.equals(secondBox)) {
			String sSecond = (String)secondBox.getSelectedItem();
			if (sSecond.startsWith("0") && sSecond.length() == 2) //$NON-NLS-1$
				second = new Integer( sSecond.substring(1) ).intValue();
			else
				second = new Integer( sSecond ).intValue();
		}
		
		fireActionPerformed(e);
	}
		
	public void addActionListener(ActionListener listener) {
		vtActionListeners.addElement(listener);
	}
	
	public void removeActionListener(ActionListener listener) {
		vtActionListeners.remove(listener);
	}
	
	public void fireActionPerformed(ActionEvent e) {
		int count = vtActionListeners.size();
		for (int i=0; i<count; i++) {
			ActionListener item = vtActionListeners.elementAt(i);
			item.actionPerformed(e);
		}
	}	
}