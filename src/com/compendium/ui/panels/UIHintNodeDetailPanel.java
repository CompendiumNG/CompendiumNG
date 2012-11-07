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
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;

import com.compendium.ui.*;
import com.compendium.ProjectCompendium;
import com.compendium.core.datamodel.Code;
import com.compendium.core.datamodel.NodeSummary;


/**
 * Displays a node's detail in a rollover panel.
 *
 * @author	Michelle Bachler
 */
public class UIHintNodeDetailPanel extends JPanel {

	/** The length of detail text to display. Taken from FormatProperties.detailRolloverLength - Default if 250.*/
	private int len = 250;

	/**
	 * Constructor. Loads the given node's details, and paints them in this panel.
	 *
	 * @param NodeSummary node, the node whose deatils to put in this panel in a JTextArea.
	 * @param int xPos, the x position to draw this panel.
	 * @param int yPos, the y position to draw this panel.
	 */
	public UIHintNodeDetailPanel(NodeSummary node, int xPos, int yPos) {

		setLocation(xPos, yPos);
		setBorder(new LineBorder(Color.gray, 1));

		//WANT THE TOOLTIP FONT AND BACKGROUND
		JToolTip tool = new JToolTip();

		JTextArea area = new JTextArea();

		setBackground(tool.getBackground());
		area.setBackground(getBackground());
		
		Font font = tool.getFont();
		int scale = ProjectCompendium.APP.getToolBarManager().getTextZoom();
		Font newFont = new Font(font.getName(), font.getStyle(), font.getSize()+ scale);
		area.setFont(newFont);

		area.setEditable(false);

		area.setMargin(new Insets(1,1,1,1));
		area.setColumns(30);
		area.setLineWrap(true);
		area.setWrapStyleWord(true);

		String detail = node.getDetail();
		detail = detail.trim();

		if (FormatProperties.detailRolloverLength > 0)
			len = FormatProperties.detailRolloverLength;

		if (detail.length() > len)
			detail = detail.substring(0, len)+"..."; //$NON-NLS-1$

		area.append(detail);
		area.setSize(area.getPreferredScrollableViewportSize());

		add(area);
		setSize(getPreferredSize());
        validate();
	}
}