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
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.table.TableCellRenderer;

/**
 * The class renderers table headers for tables using TableSorter.
 */
public class UITableHeaderRenderer extends JLabel implements TableCellRenderer {
 	
	private static final long serialVersionUID = 7005634601532410299L;

	public UITableHeaderRenderer() {
    	super();
        setBorder(new BevelBorder(BevelBorder.RAISED));
        setHorizontalAlignment(SwingConstants.CENTER);
	}

    public Component getTableCellRendererComponent(JTable table, Object value,
                  boolean isSelected, boolean hasFocus, int row, int column) {

    	Font font = getFont();
    	
		int selectedcolumn = ((TableSorter)table.getModel()).getSelectedColumn();		
		if (selectedcolumn == column) {
			setFont(new Font(font.getName(), Font.BOLD, font.getSize()));
			setForeground(IUIConstants.DEFAULT_COLOR);
			Object model = table.getModel();
			if (model instanceof TableSorter) {
				if (((TableSorter)model).getAscending()) {
					setIcon(UIImages.get(IUIConstants.UP_ARROW_ICON));
				} else {
					setIcon(UIImages.get(IUIConstants.DOWN_ARROW_ICON));
				}
			}
			
			this.setHorizontalTextPosition(SwingConstants.LEFT);							
		}
		else {
			setIcon(null);
			setFont(new Font(font.getName(), Font.PLAIN, font.getSize()));
			setBackground(this.getBackground());
			setForeground(table.getForeground());
		}

    	setValue(value);
        return this;
	}

    protected void setValue(Object value) {
    	setText((value == null) ? "" : value.toString()); //$NON-NLS-1$
    }
}
