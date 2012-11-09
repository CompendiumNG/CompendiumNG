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

import java.util.*;

import com.compendium.core.datamodel.*;


/**
 * UIArrange declares methods to arrange a map of nodes tidily
 *
 * @author	 Lakshmi Prabhakaran
 */
public interface IUIArrange extends IUIConstants {

	/** The value for the option Left to Right arrange.*/
	public static final String LEFTRIGHT	= "Left-Right"; //$NON-NLS-1$

	/** The value for the option Top to Down arrange.*/
	public static final String TOPDOWN		= "Top-Down"; //$NON-NLS-1$

	/**
	 * Process the given view for arranging.
	 * @param view com.compendium.core.datamodel.View, the view to arrange.
	 */
	public boolean processView(View view) ;

	/**
	 * This will arrange the nodes in the given view.
	 * @param view com.compendium.core.datamodel.View, the view to arrange.
	 * @param viewFrame com.compenduim.ui.UIViewFrame, the frame of the map view to arrange.
 	 */
	public void arrangeView(View view, UIViewFrame viewFrame) ;

//******** UNDO / REDO **************//

	/**
	 * Undo the last arrange for the given frame
	 * @param viewFrame com.compendium.ui.UIViewFrame, the frame to undo the arrange for.
	 */
	public void undoArrange(UIViewFrame viewFrame);

	/**
	 * Redo the last arrange for the given frame
	 * @param viewFrame com.compendium.ui.UIViewFrame, the frame to undo the arrange for.
	 */
	public void redoArrange(UIViewFrame viewFrame);

	/**
	 * Return the list of node levels.
	 * @return Vector, the node level list.
	 */
	public Vector getNodeLevelList() ;

	/**
	 * Return the list of nodes below.
	 * @return Hashtable, the nodes below list.
	 */
	public Hashtable getNodesBelow() ;

	/**
	 * Return the list of nodes.
	 * @return Hashtable, the nodes.
	 */
	public Hashtable getNodes();

	/**
	 * Return the list of nodes mapped to levels.
	 * @return Hashtable, the list of nodes mapped to levels.
	 */
	public Hashtable getNodesLevel() ;
}


