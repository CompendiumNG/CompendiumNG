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

import java.awt.Cursor;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class UIImageButton extends JButton {

	public UIImageButton(String img) {
		this(new ImageIcon(img));
	}

  	public UIImageButton(ImageIcon icon) {
  		setHorizontalAlignment(JButton.CENTER);
  		setCursor(new Cursor(Cursor.HAND_CURSOR));
  		setIcon(icon);
  		setMargin(new Insets(0, 0, 0, 0));
  		setIconTextGap(0);
  		setBorderPainted(false);
  		setFocusPainted(false);
  		setContentAreaFilled(false);
  		setBorder(null);
  		setText(null);
  		setOpaque(false);
  		setSize(icon.getImage().getWidth(null), icon.getImage().getHeight(null));
  	}
}