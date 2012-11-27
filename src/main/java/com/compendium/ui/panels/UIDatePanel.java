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
 * This class create a panel holding choiceboxes to edit/display a date.
 *
 * @author Michelle Bachler
 */
public class UIDatePanel extends JPanel implements ActionListener {

	/** The JComboBox which holds the day information.*/
	protected  JComboBox dayBox = null;

	/** The JComboBox which holds the month information.*/
	protected  JComboBox monthBox = null;

	/** The JComboBox which holds the year information.*/
	protected  JComboBox yearBox = null;

	/** Holds the original day setting.*/
	protected  int originalDay = 0;

	/** Holds the original month setting.*/
	protected  int originalMonth = 0;

	/** Holds the original year setting.*/
	protected  int originalYear = 0;

	/** Holds the current day setting.*/
	protected  int day = 0;

	/** Holds the current month setting.*/
	protected  int month = 0;

	/** Holds the current year setting.*/
	protected  int year = 0;


	/**
	 * Constructor, does nothing.
	 */
	public UIDatePanel() {}

	/**
	 * Constructor, set the label text for this panel, and then draws the panel.
	 * @param labelText, the label text for the label on this panel.
	 * @see #drawPanel
	 */
	public UIDatePanel(String labelText) {
		drawPanel(labelText);
		setDate((new Date()).getTime());
	}

	/**
	 * Draws the contents of this panel.
	 * @param labelText, the label text for the label on this panel.
	 */
	public void drawPanel(String labelText) {

		JLabel label = new JLabel(labelText);
		add(label);

		dayBox = new JComboBox();
		dayBox.addActionListener(this);
		int i=0;
		for (i=1; i<32; i++) {
			dayBox.addItem(new Integer(i).toString());
		}
		add(dayBox);

		monthBox = UIDatePanel.createMonthBox();
		monthBox.addActionListener(this);
		add(monthBox);

		yearBox = new JComboBox();
		yearBox.addActionListener(this);
		int year = 1985;
		for (i=0; i<50; i++) {
			yearBox.addItem(new Integer(year).toString());
			year++;
		}
		add(yearBox);
	}

	/**
	 * Create a choicebox with the months of the year as data (abbreviated to 3 characters)
	 * @return JComboBox, the choicebox with the months of the year as items.
	 */
	public static JComboBox createMonthBox() {
		JComboBox monthBox = new JComboBox();

		monthBox.addItem(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UIDatePanel.jan")); //$NON-NLS-1$
		monthBox.addItem(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UIDatePanel.feb")); //$NON-NLS-1$
		monthBox.addItem(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UIDatePanel.mar")); //$NON-NLS-1$
		monthBox.addItem(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UIDatePanel.apr")); //$NON-NLS-1$
		monthBox.addItem(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UIDatePanel.may")); //$NON-NLS-1$
		monthBox.addItem(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UIDatePanel.jun")); //$NON-NLS-1$
		monthBox.addItem(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UIDatePanel.jul")); //$NON-NLS-1$
		monthBox.addItem(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UIDatePanel.aug")); //$NON-NLS-1$
		monthBox.addItem(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UIDatePanel.sep")); //$NON-NLS-1$
		monthBox.addItem(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UIDatePanel.oct")); //$NON-NLS-1$
		monthBox.addItem(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UIDatePanel.nov")); //$NON-NLS-1$
		monthBox.addItem(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UIDatePanel.dec")); //$NON-NLS-1$

		return monthBox;
	}

	/**
	 * Set the current date displayed.
	 * @param long, date to set, in milliseconds
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

		day = calendar.get(Calendar.DATE);
		month = calendar.get(Calendar.MONTH);
		year = calendar.get(Calendar.YEAR);

		dayBox.setSelectedIndex(day-1);
		monthBox.setSelectedIndex(month);
		yearBox.setSelectedItem(new Integer(year).toString());
	}

	/**
	 * Return the date, with the time set to the start of the day
	 * @return GregorianCalendar, the current date entered.
	 */
	public GregorianCalendar getDate() {

		GregorianCalendar calendar = null;
		if (day > 0 && month >= 0 && year > 0) {
			calendar = new GregorianCalendar(year, month, day, 0, 0);
		}

		return calendar;
	}

	/**
	 * Return the date with the time set to the end of the day.
	 * @return GregorianCalendar, the current date entered.
	 */
	public GregorianCalendar getDateEnd() {

		GregorianCalendar calendar = null;
		if (day > 0 && month >= 0 && year > 0) {
			calendar = new GregorianCalendar(year, month, day, 23, 59);
		}

		return calendar;
	}

	/**
	 * Check if the current date set is a valid date and return if true or false.
	 * @return boolean, is the current date entered, valid.
	 */
	public boolean checkDate() {
		if (day > 0 && month >= 0 && year > 0)
			return true;

		return false;
	}

	/**
	 * Return if the date set has been modified.
	 * @return boolean, has the current date been modified.
	 */
	public boolean dateChanged() {
		if (day != originalDay || month != originalMonth || year != originalYear) {
			return true;
		}

		return false;
	}

	/**
	 * Set if the date JComboBox elements should be enabled or not.
	 * @param state, true if the date elements should be enabled, else false.
	 */
	public void setDateEnabled(boolean state) {
		dayBox.setEnabled(state);
		monthBox.setEnabled(state);
		yearBox.setEnabled(state);
	}

	/**
	 * Validate the date being entered.
	 * @param e the ActionEvent associated with this change event.
	 */
	public void actionPerformed(ActionEvent e) {

		Object source = e.getSource();

		if (source.equals(dayBox)) {
			day = new Integer( (String)dayBox.getSelectedItem() ).intValue();

			int realMonth = month+1;

			switch (realMonth) {
				case 2 :
					if (day > 29) {
						ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UIDatePanel.feb29")+"\n" +
								LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UIDatePanel.selectAgain")+"\n\n", 
								LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UIDatePanel.dateError")); //$NON-NLS-1$ //$NON-NLS-2$
						dayBox.setSelectedIndex(0);
						day = new Integer( (String)dayBox.getSelectedItem() ).intValue();
					}
					else if (day == 29) {
						GregorianCalendar cal = new GregorianCalendar();
						if (!cal.isLeapYear(year)) {
							ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UIDatePanel.feb28b")+"\n"+
									LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UIDatePanel.selectAgain")+"\n\n", 
									LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UIDatePanel.dateError")); //$NON-NLS-1$ //$NON-NLS-2$
							dayBox.setSelectedItem("28"); //$NON-NLS-1$
							day = 28;
						}
					}

				break;

				case 4 :
				case 6 :
				case 9 :
				case 11 :
					if (day == 31) {
						ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UIDatePanel.month31")+"\n"+ 
								LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UIDatePanel.selectAgain")+"\n\n", 								
								LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UIDatePanel.dateError")); //$NON-NLS-1$ //$NON-NLS-2$
						dayBox.setSelectedIndex(0);
						day = new Integer( (String)dayBox.getSelectedItem() ).intValue();
					}
				break;
			}
		}
		else if (source.equals(monthBox)) {
			month = monthBox.getSelectedIndex();

			int realMonth = month+1;
			switch (realMonth) {
				case 2 :
					if (day > 29) {
						ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UIDatePanel.feb29")+"\n" +
								LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UIDatePanel.selectAgain")+"\n\n", 
								LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UIDatePanel.dateError")); //$NON-NLS-1$ //$NON-NLS-2$
						monthBox.setSelectedIndex(0);
						month = 0;
					}
					else if (day == 29) {
						GregorianCalendar cal = new GregorianCalendar();
						if (!cal.isLeapYear(year)) {
							ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UIDatePanel.feb28b")+"\n"+
									LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UIDatePanel.selectAgain")+"\n\n", 
									LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UIDatePanel.dateError")); //$NON-NLS-1$ //$NON-NLS-2$
							dayBox.setSelectedItem("28"); //$NON-NLS-1$
							day = 28;
						}
					}
				break;

				case 4 :
				case 6 :
				case 9 :
				case 11 :
					if (day == 31) {
						ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UIDatePanel.month31")+"\n"+ 
								LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UIDatePanel.selectAgain")+"\n\n", 								
								LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UIDatePanel.dateError")); //$NON-NLS-1$ //$NON-NLS-2$
						monthBox.setSelectedIndex(0);
						month = 0;
					}
				break;
			}
		}
		else if (source.equals(yearBox)) {
			year = new Integer( (String)yearBox.getSelectedItem() ).intValue();

			int realMonth = month+1;
			if (realMonth == 2) {
				GregorianCalendar cal = new GregorianCalendar();
				if (!cal.isLeapYear(year) && day == 29) {
					ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UIDatePanel.feb28")+"\n"+ 
							LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UIDatePanel.selectAgain")+"\n\n", 								
							LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UIDatePanel.dateError")); //$NON-NLS-1$ //$NON-NLS-2$
					yearBox.setSelectedIndex(0);
					year = new Integer( (String)yearBox.getSelectedItem() ).intValue();
				}
			}
		}
	}
}
