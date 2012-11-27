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

import com.compendium.core.CoreUtilities;
import com.compendium.ui.*;
import com.compendium.ProjectCompendium;
import com.compendium.core.datamodel.Code;
import com.compendium.core.datamodel.NodeSummary;


/**
 * Displays a nodes codes in a rollover panel.
 *
 * @author	Michelle Bachler
 */
public class UIHintNodeCodePanel extends JPanel {

	/**
	 * Constructor. Loads the given nodes codes, and paints them in this panel.
	 *
	 * @param NodeSummary node, the node whose codes to put in this panel in a JTextArea.
	 * @param int xPos, the x position to draw this panel.
	 * @param int yPos, the y position to draw this panel.
	 */
	public UIHintNodeCodePanel(NodeSummary node, int xPos, int yPos) {

		setBorder(new LineBorder(Color.gray, 1));
		setLocation(xPos, yPos);

		JTextArea area = new JTextArea();

		//WANT THE TOOLTIP FONT AND BACKGROUND
		JToolTip tool = new JToolTip();

		setBackground(tool.getBackground());
		area.setBackground(getBackground());
		
		Font font = tool.getFont();
		int scale = ProjectCompendium.APP.getToolBarManager().getTextZoom();
		Font newFont = new Font(font.getName(), font.getStyle(), font.getSize()+ scale);
		area.setFont(newFont);
		
		area.setEditable(false);
		area.setMargin(new Insets(0,0,0,0));

		int i=0;
		try {
			Vector tags = new Vector();
			Code tmpCode = null;
			int originalCount = 0;
			for(Enumeration e = node.getCodes();e.hasMoreElements();) {
				tmpCode = (Code)e.nextElement();
				tags.addElement(tmpCode);
				originalCount++;
			}
			
			tags = CoreUtilities.sortList(tags);
			
			int count = tags.size();			
			Code code = null;
			for(i=0; i<count; i++) {
				code = (Code)tags.elementAt(i);
				if (i > 0) {
					area.append("\n"); //$NON-NLS-1$
				}
				area.append(code.getName());
			}			
		}
		catch(Exception ex) {
			System.out.println("Error: (UIHintCodePanel) \n\n"+ex.getMessage()); //$NON-NLS-1$
		}

		add(area);
		setSize(getPreferredSize());
        validate();
	}
}