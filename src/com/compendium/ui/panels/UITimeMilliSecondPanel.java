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
public class UITimeMilliSecondPanel extends UITimeSecondPanel implements ActionListener {

	/** The choice box for the millisecond information.*/
	protected JComboBox millisecondBox = null;

	/** The original date information.*/
	protected int originalMilliSecond = 0;

	/** The current millisecond information.*/
	protected int millisecond = 0;
	
	/**
	 * Constructor, takes a string which is the label to associate with this panel.
	 * @param labelText the label to associate with this panel.
	 * @param showDate indicates whether to display the date or just the time.
	 * True for date and time, false for just time
	 */
	public UITimeMilliSecondPanel(String labelText, boolean showDate, boolean shortLabels) {
		this.bShowDate = showDate;
		this.bShortLabels = shortLabels;
		drawPanel(labelText);
		setDate((new Date()).getTime());
	}

	/**
	 * Add the millisecond menu.
	 */
	public void drawPanel(String labelText) {
		super.drawPanel(labelText);

		JLabel label = null;		
		if (this.bShortLabels) {
			label = new JLabel(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UITimeMilliSecondPanel.millisecondShort")); //$NON-NLS-1$
			label.setToolTipText(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UITimeMilliSecondPanel.millisecond")); //$NON-NLS-1$
		} else {
			label = new JLabel(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UITimeMilliSecondPanel.millisecond")); //$NON-NLS-1$
		}
		add(label);

		millisecondBox = new JComboBox();
		millisecondBox.setSize(60, millisecondBox.getPreferredSize().height);
		millisecondBox.setToolTipText(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UITimeMilliSecondPanel.message1")); //$NON-NLS-1$
		millisecondBox.setPreferredSize(new Dimension(60, millisecondBox.getPreferredSize().height));
		millisecondBox.setEditable(true);
		/*millisecondBox.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				updateMilliseconds();
			}
		});*/
		millisecondBox.addActionListener(this);
		for (int i=0; i<1000; i+=100) {
			millisecondBox.addItem(new Integer(i).toString());
		}
		add(millisecondBox);

	}

	// GETTERS AND SETTERS
	/**
	 * Set the current time displayed. If passed time is 0, get current Time.
	 * @param long dateMillis, time to set, in milliseconds
	 */
	public void setDate(long dateMillis) {
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
			calendar.set(Calendar.MILLISECOND, 0);
		}

		originalDay = calendar.get(Calendar.DATE);
		originalMonth = calendar.get(Calendar.MONTH);
		originalYear = calendar.get(Calendar.YEAR);
		originalHour = calendar.get(Calendar.HOUR_OF_DAY);
		originalMinute = calendar.get(Calendar.MINUTE);
		originalSecond = calendar.get(Calendar.SECOND);
		originalMilliSecond = calendar.get(Calendar.MILLISECOND);

		day = calendar.get(Calendar.DATE);
		month = calendar.get(Calendar.MONTH);
		year = calendar.get(Calendar.YEAR);
		hour = calendar.get(Calendar.HOUR_OF_DAY);
		minute = calendar.get(Calendar.MINUTE);
		second = calendar.get(Calendar.SECOND);
		millisecond = calendar.get(Calendar.MILLISECOND);

		if (this.bShowDate) {
			dayBox.setSelectedIndex(day-1);
			monthBox.setSelectedIndex(month);
			yearBox.setSelectedItem(new Integer(year).toString());
		}
		hourBox.setSelectedItem(new Integer(hour).toString());
		minuteBox.setSelectedItem(new Integer(minute).toString());
		secondBox.setSelectedItem(new Integer(second).toString());
		millisecondBox.setSelectedItem(new Integer(millisecond).toString());
	}

	/**
	 * Over-ride to do nothing.
	 * Use getTime instead.
	 * @return null.
	 */
	public GregorianCalendar getDate() {
		GregorianCalendar calendar = null;
		if (day > 0 && month >= 0 && year > 0 && hour >= 0 && minute >= 0 && second >=0 && millisecond >= 0) {
			calendar = new GregorianCalendar(year, month, day, hour, minute, second);
			calendar.setTimeInMillis(calendar.getTimeInMillis()+millisecond);
		}

		return calendar;
	}

	/**
	 * Set the time from the passed milliseconds.
	 * day, month, year are all 0 and not used/set using this.
	 * If there are more than a days worth of milliseconds, call setDate.
	 * @return
	 */
	public void setMilliSeconds(long time) {	
		if (time > (24*60*60*1000)) {
			setDate(time);
			return;
		}
		
		int hours = 0;
		int minutes = 0;
		int seconds = 0;
		int milliseconds = 0;
		
		if (time < 1000) {
			milliseconds = new Long(time).intValue();
		} else {
			seconds = new Long(time/1000).intValue();
			milliseconds = new Long(time%1000).intValue();
			if (seconds > 59) {
				minutes = seconds/60;
				seconds = seconds%60;
				if (minutes > 59) {
					hours = minutes/60;
					minutes = minutes%60;
				}
			}
		} 
		
		hour = hours;
		minute = minutes;
		second = seconds;
		millisecond = milliseconds;

		hourBox.setSelectedItem(new Integer(hour).toString());
		minuteBox.setSelectedItem(new Integer(minute).toString());
		secondBox.setSelectedItem(new Integer(second).toString());			
		millisecondBox.setSelectedItem(new Integer(millisecond).toString());			
	}
	
	/**
	 * Return, in milliseconds, the current time for the day.
	 * i.e. return the hours, minutes, seconds, milliseconds as a milliseconds timestamp.
	 * @return
	 */
	public long getMilliSeconds() {
		long date = ((second+(minute*60)+(hour*60*60))*1000)+millisecond;
		return date;
	}
	
	// OTHER METHODS
	/**
	 * Check if the current time set is a valid time and return if true or false
	 * @return boolean, is the current date entered valid
	 */
	public boolean checkDate() {
		if (hour >= 0 || minute >= 0 || second >= 0 || millisecond >= 0) 
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
		millisecondBox.setEnabled(state);
	}

	/**
	 * Return if the time set has been modified
	 * @return boolean, has the current date been modified
	 */
	public boolean dateChanged() {

		if (hour != originalHour || minute != originalMinute 
				|| second != originalSecond || millisecond != originalMilliSecond) {
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
		else if (source.equals(millisecondBox)) {
			updateMilliseconds();
		}
		
		fireActionPerformed(e);
	}

	/**
	 * Update the millisecond value
	 */
	private void updateMilliseconds() {
		String sMilliSecond = (String)millisecondBox.getSelectedItem();
		int value = new Integer( sMilliSecond ).intValue();
		
		if (value == millisecond) {
			return;
		}
		if (value > 999 || value < 0) {
			ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UITimeMilliSecondPanel.message2")); //$NON-NLS-1$
			millisecondBox.setSelectedItem(millisecond);			
			millisecondBox.requestFocus();
			return;
		}
		
		millisecond = value;
			
		/*if (sMilliSecond.startsWith("0") && sMilliSecond.length() == 3)
			millisecond = new Integer( sMilliSecond.substring(2) ).intValue();
		else
			millisecond = new Integer( sMilliSecond.substring() ).intValue();	*/
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
