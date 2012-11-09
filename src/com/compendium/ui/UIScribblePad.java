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

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

import com.compendium.ui.*;
import com.compendium.ui.plaf.ScribblePadUI;

/**
 * THIS CLASS IS STILL UNDER DEVLEOPMENT.
 * <p>
 * Represents a scribble pad layer that can be placed over maps
 *
 * @author	Michelle Bachler
 */
public class UIScribblePad extends JComponent {

	/** Indicates that the current draw tool is none.*/
	public static int 		NO_TOOL 	= -1;

	/** Indicates the current draw tool is the pencil.*/
	public static int 		PENCIL 		= 0;

	/** Indicates the current draw tool is the pencil.*/
	public static int 		LINE 		= 1;

	/** Indicates that the current draw tool is the oval.*/
	public static int 		OVAL 		= 2;

	/** Indicates that the current draw tool is the rectangle.*/
	public static int 		RECTANGLE 	= 3;
	
	/** The UIViewPane object associated with this scribble pad */
	protected UIViewPane 	oViewPane		= null;


	/**
	 * Constructor.
	 * @param view com.compendium.ui.UIViewPane, the associated map view to scribble on.
	 */
	public UIScribblePad(UIViewPane view) {

		oViewPane = view;

		setOpaque(false);
		setBackground(null);
		
		setLocation(0,0);
        validate();
		updateUI();
	}
	
	/**
   	 * Returns the Look & Feel object that renders this component.
     *
     * @return ScribblePadUI, the Look & Feel object that renders this component.
     */
  	public ScribblePadUI getUI() {
		return (ScribblePadUI)ui;
  	}

	/**
   	 * Returns the UIViewPane object associated with this scribble pad.
     *
     * @return UIViewPane, the UIViewPane object associated with this scribble pad.
     */
	public UIViewPane getViewPane() {
		return oViewPane;
	}

 	/**
     * Sets the Look & Feel object that renders this component.
     *
     * @param ui com.compendium.ui.plaf.ScribblePadUI, the ScribblePadUI Lool & Feel object.
     */
   	public void setUI(ScribblePadUI ui) {
		super.setUI(ui);
   	}

	/**
     * Notification from the UIFactory that the Look & Feel has changed.
     *
     * @see JComponent#updateUI
     */
  	public void updateUI() {

	  	ScribblePadUI newScribblePadUI = (ScribblePadUI)ScribblePadUI.createUI(this);
	  	setUI(newScribblePadUI);
		invalidate();
  	}

  	/**
     * Returns a string that specifies the name of the look & feel class that renders this component.
     *
     * @return String "ScribblePadUI"
     *
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     */
  	public String getUIClassID() {
	  	return "ScribblePadUI"; //$NON-NLS-1$
  	}

	/**
	 * Process the string of pencil data from the database into a vector of UIShape objects,
	 * and add to ui.
	 * @param sData, the string of pencil data to process.
	 */
	public void processPencilData(String sData) {
		Vector vtPencilData = new Vector(51);

		StringTokenizer inner = null;
		StringTokenizer outer = null;
		StringTokenizer st = null;

		outer = new StringTokenizer(sData,";"); //$NON-NLS-1$
		while(outer.hasMoreTokens()) {
			String sOuter = outer.nextToken();

			Vector vtScribbleBlock = new Vector(51);

			st = new StringTokenizer(sOuter,":"); //$NON-NLS-1$
			while(st.hasMoreTokens()) {
				String element = st.nextToken();
				inner = new StringTokenizer(element,","); //$NON-NLS-1$
				int x = 0;
				int y = 0;
				int type = 0;
				int thickness = 1;
				Color colour = null;
				int i=0;
				while(inner.hasMoreTokens()) {
					String sNext = inner.nextToken();
					if (i == 0)
						x = new Integer(sNext).intValue();
					else if ( i == 1)
						y = new Integer(sNext).intValue();
					else if ( i == 2 )
						type = new Integer(sNext).intValue();
					else if ( i == 3 ) {
						colour = new Color(new Integer(sNext).intValue());
						if (colour == null)
							colour = Color.black;
					}
					else if ( i == 4 )
						thickness = new Integer(sNext).intValue();

					i++;
				}
				UIShape shape = new UIShape(x, y, 0, 0, type, colour, thickness);
				vtScribbleBlock.addElement(shape);
			}

			vtPencilData.addElement(vtScribbleBlock);
		}

		getUI().setPencilData(vtPencilData);
	}

	/**
	 * Process the string of shapes data from the database into a vector of UIShape objects,
	 * and add to ui.
	 * @param sData, the string of shapes data to process.
	 */
	public void processShapesData(String sData) {

		Vector vtShapesData = new Vector(51);

		StringTokenizer inner = null;
		StringTokenizer st = new StringTokenizer(sData,":"); //$NON-NLS-1$
		while(st.hasMoreTokens()) {
			String element = st.nextToken();
			inner = new StringTokenizer(element,","); //$NON-NLS-1$
			int x = 0;
			int y = 0;
			int width = 0;
			int height = 0;
			int type = 0;
			int thickness = 1;
			Color colour = null;
			int i=0;
			while(inner.hasMoreTokens()) {
				String sNext = inner.nextToken();
				if (i == 0)
					x = new Integer(sNext).intValue();
				else if ( i == 1)
					y = new Integer(sNext).intValue();
				else if ( i == 2 )
					width = new Integer(sNext).intValue();
				else if ( i == 3 )
					height = new Integer(sNext).intValue();
				else if ( i == 4 )
					type = new Integer(sNext).intValue();
				else if ( i == 5 ) {
					colour = new Color(new Integer(sNext).intValue());
					if (colour == null)
						colour = Color.black;
				}
				else if ( i == 6 )
					thickness = new Integer(sNext).intValue();
				i++;
			}
			UIShape shape = new UIShape(x, y, width, height, type, colour, thickness);
			vtShapesData.addElement(shape);
		}

		getUI().setShapesData(vtShapesData);
	}

	/**
	 * Process the Vector of pencil data from Scribble pad ui and return the string to store in the database.
	 * @return String, the processed string to store in the database.
	 */
	public String reversePencilData() {

		Vector data = getUI().getPencilData();

		StringBuffer buffer = new StringBuffer(1000);
		Vector next = null;
		int count = data.size();

		for (int i=0; i<count;i++) {
			next = (Vector)data.elementAt(i);

			int jcount = next.size();
			for (int j=0; j<jcount; j++) {
				UIShape shape = (UIShape)next.elementAt(j);

				buffer.append(shape.getX());
				buffer.append(","); //$NON-NLS-1$
				buffer.append(shape.getY());
				buffer.append(","); //$NON-NLS-1$
				buffer.append(shape.getType());
				buffer.append(","); //$NON-NLS-1$

				Color color = Color.black;
				if (shape.getColour() != null)
					color = shape.getColour();
				buffer.append( color.getRGB() );
				buffer.append(","); //$NON-NLS-1$
				buffer.append(shape.getThickness());

				if (j < jcount-1)
					buffer.append(":"); //$NON-NLS-1$
			}

			if (i < count-1)
				buffer.append(";"); //$NON-NLS-1$
		}

		return buffer.toString();
	}

	/**
	 * Process the Vector of shapes data from Scribble pad ui and return the string to store in the database.
	 * @return String, the processed string to store in the database.
	 */
	public String reverseShapesData() {

		Vector shapes = getUI().getShapesData();
		StringBuffer buffer = new StringBuffer(1000);
		int count = shapes.size();

		for (int i=0; i<count; i++) {

			UIShape shape = (UIShape)shapes.elementAt(i);
			buffer.append(shape.getX());
			buffer.append(","); //$NON-NLS-1$
			buffer.append(shape.getY());
			buffer.append(","); //$NON-NLS-1$
			buffer.append(shape.getWidth());
			buffer.append(","); //$NON-NLS-1$
			buffer.append(shape.getHeight());
			buffer.append(","); //$NON-NLS-1$
			buffer.append(shape.getType());
			buffer.append(","); //$NON-NLS-1$

			Color color = Color.black;
			if (shape.getColour() != null)
				color = shape.getColour();
			buffer.append( color.getRGB() );
			buffer.append(","); //$NON-NLS-1$
			buffer.append(shape.getThickness());

			if (i < count-1)
				buffer.append(":"); //$NON-NLS-1$

		}

		return buffer.toString();
	}

	/**
	 * Undo the last paint operation - if a pencil scribble
	 */
	public void undo(Vector vtShapes) {
		getUI().undo(vtShapes);
		oViewPane.getViewFrame().refreshUndoRedo();
	}

	/**
	 * Redo the last undo - if a pencil scribble.
	 */
	public void redo(Vector vtShapes) {
		getUI().redo(vtShapes);
		oViewPane.getViewFrame().refreshUndoRedo();
	}

	/**
	 * Undo the last paint operation.
	 */
	public void undo(UIShape shape) {
		getUI().undo(shape);
		oViewPane.getViewFrame().refreshUndoRedo();
	}

	/**
	 * Redo the last undo.
	 */
	public void redo(UIShape shape) {
		getUI().redo(shape);
		oViewPane.getViewFrame().refreshUndoRedo();
	}

	/**
	 * Clear the scribble pad contents.
	 */
	public void clearPad() {
		ScribblePadUI scribble = getUI();
		scribble.clear();
		repaint();
	}
}
