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

import java.awt.Component;
import javax.swing.*;
import javax.swing.border.*;

/**
 * Handles the rendering of lists of JLabels and Icons.
 *
 * @author	Mohammed Sajid Ali / Michelle Bachler
 */
public class LabelListCellRenderer extends JLabel implements ListCellRenderer {

  	protected static Border noFocusBorder;

	/**
	 * Constructor.
	 */
	public LabelListCellRenderer() {
		super();
		noFocusBorder = new EmptyBorder(1, 1, 1, 1);
		setOpaque(true);
		setBorder(noFocusBorder);
  	}

	/**
	 * Return the JComponent to use as a list element.
	 * @param list, the parent list.
	 * @param value, the value of an element in the list.
	 * @param index, the index of the current value.
	 * @param isSelected, is the current cell selected?
	 * @param cellHasFocus, do the list and the cell have the focus?
	 */
	public Component getListCellRendererComponent(JList list,
												Object value,
												int index,
												boolean isSelected,
												boolean cellHasFocus ) {
		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		}
		else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}

		if (value instanceof JLabel) {
			JLabel lbl = (JLabel)value;
			setText(lbl.getText());
			setIcon(lbl.getIcon());
		}
		else if (value instanceof Icon) {
	    	setIcon((Icon)value);
		}
		else {
			setText((value == null) ? "" : value.toString()); //$NON-NLS-1$
		}

		setFont(list.getFont());
		setBorder((cellHasFocus) ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder); //$NON-NLS-1$

		return this;
	}
}
