/********************************************************************************
 *                                                                              *
 *  (c) Copyright 2009 Verizon Communications USA and The Open University UK    *
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


package com.compendium.ui.plaf;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.datatransfer.*;
import java.util.*;
import java.beans.*;

import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import javax.swing.*;

import com.compendium.ui.plaf.*;
import com.compendium.ui.*;
import com.compendium.*;
import com.compendium.core.datamodel.*;
import com.compendium.core.*;

/**
 * The UI class for the UILink Component
 *
 * @author	Mohammed Sajid Ali / Michelle Bachler
 */
public	class LinkUI extends LineUI implements PropertyChangeListener{


	/** The UILink associated with this LinkUI instance.*/
	protected UILink								oLink;

	/** The UIViewPane of the link associated with this LinkUI instance.*/
	protected UIViewPane							oViewPane;


    private Point actualFromPoint = new Point(0,0);
    private Point actualToPoint = new Point(0,0);
	private Rectangle linkTypeRec = new Rectangle();

  	/** The PropertyChangeListener registered for this list.*/
	private		PropertyChangeListener				oPropertyChangeListener;

	/** the colour to use for a selected node.*/
	private static final Color 	SELECTED_COLOR 		= Color.yellow;

	/** The colour to use for selected text in the node label.*/
	private static final Color 	SELECTED_TEXT_COLOR = Color.black;

	/** The colour to use for the label background when editing a node label.*/
	private static final Color 	EDITING_COLOR 		= Color.white;

	/** The colour to use for the label background when text is selected.*/
	private static final Color 	TSELECTED_COLOR 	 = Color.blue;

	/** the colour to use for the text when text is selected.*/
	private static final Color 	TSELECTED_TEXT_COLOR = Color.white;

	/** The colour to use for the label border when the link label is rolled over with the mouse.*/
	private static final Color 	ROLLOVER_COLOR		= Color.cyan;

	/**
	 * Holds the list of the <code>TextRowElement</code> objects
	 * with information on each row of text in the link label.
	 */
	private 	Vector								textRowElements = null;

	/** Used to hold the calculated maximum width required to paint this link.*/
	private 	int 								maxTextWidth 		= -1;

	/** Used to hold the calculated maximum height required to paint this link's text area.*/
	private 	int 								textHeight 		= -1;

	/** Used to hold the calculated maximum height required to paint this link.*/
	private 	Dimension							linkDimension 	= null;

	/** Defines whether dragging is started with right mouse button*/
	private		boolean								bDragging 		= false;

	/** The clipboard instance used when performing cut/copy/paste actions in the link label.*/
	private 	Clipboard 							clipboard 		= null;

	/** The are for the caret in the link label.*/
	private 	Rectangle							caretRectangle 	= null;

	/** The area for the node label.*/
	private		Rectangle							labelRectangle 	= null;

	/** The current position of the caret in the link label.*/
	private 	int									currentCaretPosition = 0;

	/** The previous contents of the label for undoind a cut/copy/paste to one level.*/
	private		String								previousString = "";

	/** Indocates if the node label is currently being edited - Is the link in edit mode?*/
	private 	boolean								editing = false;

	/** Indicates if a double-click was performed in the link label (for selecting a word).*/
	private 	boolean								doubleClicked = false;

	/** The x position the mouse was clicked to start label editing or move row.*/
	private		int									editX = 0;

	/** The y position the mouse was clicked to start label editing or move row.*/
	private 	int									editY = 0;

	/** The starting position for text selection in the link label.*/
	private		int									startSelection = -1;

	/** The stopping position for text selection in the link label.*/
	private 	int									stopSelection = -1;

	/** The current row the cursor is in, in the link label.*/
	private		int									currentRow = 0;

	/** Was the up key pressed to move the cursor up a row in the link label?*/
	private 	boolean								caretUp = false;

	/** Was the down key pressed to move the cursor down a row in the link label?*/
	private 	boolean								caretDown = false;

	/** Holds the last key pressed on this node.*/
	private String sKeyPressed = "";

	/** The shortcut key for the current platfrom.*/
	private int shortcutKey;


	/**
	 * Create a new LinkUI instance.
	 * @param c, the component this is the ui to install for.
	 */
  	public static ComponentUI createUI(JComponent c) {
		return new LinkUI();
  	}

	/**
	 * Run any install instructions for installing this UI.
	 * @param c, the component this is the ui for.
	 */
	public void installUI(JComponent c) {
		super.installUI(c);
	    oLink = (UILink)c;
		oViewPane = oLink.getViewPane();

		previousString = oLink.getText();
		if (previousString != null)
			currentCaretPosition = previousString.length();

		clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		shortcutKey = ProjectCompendium.APP.shortcutKey;

		oPropertyChangeListener = this;
		c.addPropertyChangeListener( oPropertyChangeListener );
	}

	/**
	 * Run any uninstall instructions for uninstalling this UI.
	 * @param c, the component this is the ui to uninstall for.
	 */
	public void uninstallUI(JComponent c) {
		oLink = null;
		super.uninstallUI(c);

		if ( oPropertyChangeListener != null ) {
	    	c.removePropertyChangeListener( oPropertyChangeListener );
		}
		oPropertyChangeListener = null;
	}

	/**
	 * Handles a property chagne to the UILink.
	 * @param evt, the associated PropertyChagenEvent object.
	 */
	public void propertyChange(PropertyChangeEvent evt) {

 		String prop = evt.getPropertyName();

		if (prop.equals(UILink.LABEL_PROPERTY)) {
			refreshBounds();
		}
		else if (prop.equals(UILink.TYPE_PROPERTY)) {
			refreshBounds();
		}
		else if (prop.equals(UILine.ARROW_PROPERTY)) {
			refreshBounds();
		}
	}

	/**
	 * Delete the given UILink from the associated map.
	 * @param uilink com.compendium.ui.UILink, the link to purge.
	 */
  	public void purgeLink(UILink uilink) {

		oViewPane = uilink.getViewPane();
		if (oViewPane != null) {

			UINode fromNode = uilink.getFromNode();
			UINode toNode = uilink.getToNode();

			try {
				//purge link from the database
				oViewPane.getView().purgeMemberLink(uilink.getLink());

				// REMOVE LINK FROM DATA STRUCUTURE
				fromNode.removeLink(uilink);
				toNode.removeLink(uilink);
				oViewPane.remove(uilink);
			}
			catch(Exception ex) {
				System.out.println("Error: (LinkUI.purgeLink) Unable to purge link\n\n"+ex.getMessage());
			}
		}
  	}

	/**
	 * Mark the given UILink as deleted from the associated map.
	 * @param uilink com.compendium.ui.UILink, the link to delete.
	 */
  	public void deleteLink(UILink uilink) {

		oViewPane = oLink.getViewPane();

		UINode fromNode = uilink.getFromNode();
		UINode toNode = uilink.getToNode();

		try {
			//delete link in the datamodel layer
			oViewPane.getView().removeMemberLink(uilink.getLink());

			fromNode.removeLink(uilink);
			toNode.removeLink(uilink);
			oViewPane.remove(uilink);
		}
		catch(Exception ex) {
			ProjectCompendium.APP.displayError("Error: (LinkUI.deleteLink) Unable to delete link\n\n"+ex.getMessage());
		}
 	}

	/**
	 * Open the right-click popup menu associated with this LinkUI instance at the given coordinates.
	 * @param x, the x position for the popup menu associated with this LinkUI instance.
	 * @param y, the y position for the popup menu associated with this LinkUI instance.
	 */
  	public void showPopupMenu(int x, int y) {
		oLink.showPopupMenu(this,x,y);
  	}

	/**
	 * Is this link currently being edited?
	 * @return boolean, true if it is, else false.
	 */
	public boolean isEditing() {
		return editing;
	}

	/**
	 * Set the link in editing mode.
	 */
	/*public void setEditing() {

		editing = true;
		startSelection = -1;
		stopSelection = -1;

		currentCaretPosition = oLink.getText().length();
		doubleClicked = false;

		oLink.moveToFront();
		oLink.requestFocus();
	}*/

	/**
	 * Reset all the link label editing variables.
	 */
	public void resetEditing() {
		editing = false;
		currentCaretPosition = -1;
		editX=0;
		editY=0;
		bDragging = false;
		doubleClicked = false;
		startSelection = -1;
		stopSelection = -1;
	}

  	/**
   	 * Convenience method to get the UILink by the popupmenu and other gui operations.
	 * @return com.compendium.ui.UILink, the UILink associated with this LinkUI instance.
   	 */
 	public UILink getUILink() {
		return oLink;
  	}

	/**
	 * This class holds information about each row of the Link label.
	 */
	private class TextRowElement {

		String text = "";
		int startPos = 0;
		Rectangle textR = null;
		boolean isRowWithCaret;

		public TextRowElement(String text, int startPos, Rectangle textR, boolean isRowWithCaret) {
			this.text = text;
			this.startPos = startPos;
			this.textR = textR;
			this.isRowWithCaret = isRowWithCaret;
		}

		public String getText() {
			return text;
		}

		public int getStartPosition() {
			return startPos;
		}

		public Rectangle getTextRect() {
			return textR;
		}

		public boolean getIsRowWithCaret() {
			return isRowWithCaret;
		}
	}

	/**
	 * Draws the line on the given graphics context.
	 *
	 * @param g, the Graphics object for this pain method to use.
	 * @param c, the component to paint.
	 */
	public void paint(Graphics g, JComponent c) {

		UILink link = null;
		if (c instanceof UILink)
			link = (UILink)c;
		else
			return;

		// get from and to points
		Point from = link.getFrom();
		Point to   = link.getTo();

		// if one of the points is missing don't draw line
		if (from == null || to == null)
			return;

		// determine relative to and from points
		Point originalFrom = new Point();
		Point originalTo = new Point();
		Point actualFrom = new Point();
		Point actualTo = new Point();

		if (oLink.getCoordinateType() == UILine.RELATIVE) {
			// coordinates already relative to this components coordinate system
			originalFrom = from;
			originalTo = to;
			actualFrom = this.actualFromPoint;
			actualTo = this.actualToPoint;
		}
		else {
			// calculate the relative coordinates by converting the coordinates from
			// the parents coordinate system to this components coordinate system
			Container parent = link.getParent();
			if (parent != null) {
				originalFrom = SwingUtilities.convertPoint(parent, from, link);
				originalTo = SwingUtilities.convertPoint(parent, to, link);
				actualFrom = SwingUtilities.convertPoint(parent, this.actualFromPoint, link);
				actualTo = SwingUtilities.convertPoint(parent, this.actualToPoint, link);
			}
			else {
				return;
			}
		}

		// set color of line
		if (link.isSelected())
			g.setColor(link.getSelectedColor());
		else
			g.setColor(link.getForeground());

		g.drawLine(originalFrom.x, originalFrom.y, originalTo.x, originalTo.y);

        // DRAW ARROW
		switch (link.getArrow()) {
			case ICoreConstants.NO_ARROW: {
				break;
			}
			case ICoreConstants.ARROW_TO: {
 				drawArrow(g, originalFrom, originalTo, link.CURRENT_ARROW_WIDTH);
				break;
			}
			case ICoreConstants.ARROW_FROM: {
				drawArrow(g, originalTo, originalFrom, link.CURRENT_ARROW_WIDTH);
				break;
			}
			case ICoreConstants.ARROW_TO_AND_FROM: {
				drawArrow(g, originalFrom, originalTo, link.CURRENT_ARROW_WIDTH);
				drawArrow(g, originalTo, originalFrom, link.CURRENT_ARROW_WIDTH);
				break;
			}
		}

		drawTextArea(g, link);
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Draw the link label.
	 *
	 * @param g, the Graphics object for this pain method to use.
	 * @param c, the component to paint.
	 */
	private void drawTextArea(Graphics g, UILink link) {

		// IF THERE IS NO ROOM TO DRAW THE TEXT
		if (maxTextWidth <= 0 && textHeight <= 0)
			return;

        String text = link.getText();
        Font font = link.getFont();
 
        oViewPane = link.getViewPane();
		AffineTransform trans=new AffineTransform();
		trans.setToScale(oViewPane.getScale(), oViewPane.getScale());
		Point p1 = new Point(font.getSize(), font.getSize());
		try { p1 = (Point)trans.transform(p1, new Point(0, 0));}
		catch(Exception e) {System.out.println("can't convert font size (LinkUI.paint 1) \n\n"+e.getMessage()); }
		Font newFont = new Font(font.getFontName(), font.getStyle(), p1.x);                
        g.setFont(newFont);

		int maxWidth = maxTextWidth;

		FontMetrics fm = g.getFontMetrics();
		labelRectangle = null;
		textRowElements = new Vector();

		Rectangle viewR = new Rectangle(link.getSize());
		Insets viewInsets = link.getInsets();
		viewR.x = viewInsets.left;
		viewR.y = viewInsets.top;
		viewR.width -= (viewInsets.left + viewInsets.right);
		viewR.height -= (viewInsets.top + viewInsets.bottom);

		int textWidth = fm.stringWidth( text );
		Rectangle textR = new Rectangle();
        textR.width =  maxTextWidth;
        textR.height = viewR.height;

		if (textWidth == 0 && viewR.width >= 30)
			textWidth = 30;

	    if (textWidth < viewR.width && textWidth <= maxTextWidth)
			textR.width = textWidth;

        if ((fm.getAscent() + fm.getDescent()) < viewR.height)
            textR.height = fm.getAscent() + fm.getDescent();

		if (textHeight == -1)
			textHeight = viewR.height;
			
        if (textHeight < viewR.height) {
        	textR.y = viewR.y + ((viewR.height - textHeight) / 2) + fm.getAscent();
        } else {
			textR.y = viewR.y + fm.getAscent();
        }

        textR.x = viewR.x + ((viewR.width - textR.width) / 2);

		labelRectangle = new Rectangle(textR);
        labelRectangle.x = textR.x;
		labelRectangle.y = textR.y-fm.getAscent();
		labelRectangle.height = fm.getAscent();

		int startPos = 0;
		int stopPos = 0;

		// RE_SET THE FONT AND COLOR FOR TEXT
		Font nodeFont = g.getFont();

		g.setColor(Color.black);

		if (textWidth > maxWidth) {

			int row = -1;
			String textLeft = text;
			boolean isRowWithCaret = false;

			while ( textLeft.length() > 0 ) {
				row ++;
				isRowWithCaret = false;

				startPos = stopPos;

				textWidth = fm.stringWidth( textLeft );
				int textLen = textLeft.length();
				int curLen = 1;

				if (textWidth > maxWidth) {
					while(curLen <= textLen) {

						String next = textLeft.substring(0, curLen);
						if (fm.stringWidth(next) < maxWidth) {
							curLen++;
						}
						else if (fm.stringWidth(next) >= maxWidth) {
							curLen--;
							break;
						}
					}
				}
				else {
					curLen = textLen;
				}

				String nextText = textLeft.substring(0, curLen);
				if (curLen < textLen) {
					int lastSpace = nextText.lastIndexOf(" ");
					if (lastSpace != -1 && lastSpace != textLen) {
						curLen = lastSpace+1;
						nextText = textLeft.substring(0, curLen);
						textLeft = textLeft.substring(curLen);
					}
					else {
						nextText = textLeft.substring(0, curLen);
						textLeft = textLeft.substring(curLen);
					}
				}
				else {
					if (!textLeft.equals(""))
						nextText = textLeft;
					textLeft = "";
				}

				stopPos += nextText.length();

				int mousePosition = -1;

				// for dragging mouse to select
				if (link.hasFocus() && editing && (bDragging || doubleClicked)) {

					if (editY >= textR.y-fm.getAscent() && editY < (textR.y+fm.getDescent()) ) {

						int tX = textR.x;
						int tY = textR.y;

						int tWidth = fm.stringWidth( nextText );
						if (tWidth < maxWidth) {
							tX += (maxWidth-tWidth)/2;
						}

						int caretPos = 0;
						if (editX <= tX)
							caretPos = 0;
						else if (editX >= tX+tWidth && startPos+nextText.length() == text.length()) {
							caretPos = nextText.length();
						}
						else if (editX >= tX+tWidth) {
							caretPos = nextText.length()-1;
						}
						else {
							int ind = 1;
							int prev = 0;
							while(ind <= nextText.length()) {
								String n = nextText.substring(0, ind);
								int charX = fm.stringWidth(n);
								if (editX >= (tX+prev) && editX <= (tX+charX) ) {
									if ( (tX+prev) - editX < editX - (tX+charX))
										caretPos = ind-1;
									else
										caretPos = ind;
									break;
								}
								prev = charX;
								ind++;
							}
						}

						if (bDragging) {
							if (currentCaretPosition == -1) {
								currentCaretPosition = startPos+caretPos;
								startSelection = currentCaretPosition;
								stopSelection = currentCaretPosition;
							}
							else {
								// IF DRAGGING LEFT
								if (startPos+caretPos < currentCaretPosition) {

									if (stopSelection == -1)
										stopSelection = currentCaretPosition;

									currentCaretPosition = startPos+caretPos;

									if (startSelection == -1 || startSelection >= currentCaretPosition)
										startSelection = currentCaretPosition;
									else
										stopSelection = currentCaretPosition;
								}
								// IF DRAGGING RIGHT
								else {
									if (startSelection == -1)
										startSelection = currentCaretPosition;

									currentCaretPosition = startPos+caretPos;

									if (stopSelection == -1 || stopSelection < currentCaretPosition)
										stopSelection = currentCaretPosition;
								}
							}
						}

						// DOUBLE CLICK TO SELECT WORD
						if (doubleClicked) {
							int index = nextText.indexOf(" ", caretPos);
							if (index == -1)
								stopSelection = startPos + nextText.length();
							else {
								stopSelection = startPos + index;
							}

							currentCaretPosition = stopSelection;

							String bit = nextText.substring(0, caretPos);
							index = bit.lastIndexOf(" ");
							if (index == -1)
								startSelection = startPos;
							else {
								startSelection = startPos + index+1;
							}
							doubleClicked = false;
						}
					}
				}

				// If there is no more text break out or it cuases a never-ending loop.
				if (nextText.equals(""))
					break;

				drawText(g, fm, link, nextText, textR, maxWidth, startPos);

				// If mouse just clicked
				if (link.hasFocus() && currentCaretPosition == -1 && editing) {
					// IS THE CLICK IN THIS ROW
					if (editY >= textR.y-fm.getAscent() && editY < (textR.y+fm.getDescent()) ) {

						int tX = textR.x;
						int tY = textR.y;

						int tWidth = fm.stringWidth( nextText );
						if (tWidth < maxWidth) {
							tX += (maxWidth-tWidth)/2;
						}

						int caretPos = 0;
						if (editX <= tX)
							caretPos = 0;
						else if (editX >= tX+tWidth && startPos+nextText.length() == text.length()) {
							caretPos = nextText.length();
						}
						else if (editX >= tX+tWidth) {
							caretPos = nextText.length()-1;
						}
						else {
							int ind = 1;
							int prev = 0;
							while(ind <= nextText.length()) {

								String n = nextText.substring(0, ind);
								int charX = fm.stringWidth(n);
								if (editX >= (tX+prev) && editX <= (tX+charX) ) {
									if ( (tX+prev) - editX < editX - (tX+charX))
										caretPos = ind-1;
									else
										caretPos = ind;
									break;
								}
								prev = charX;
								ind++;
							}
						}

						currentCaretPosition = startPos+caretPos;
						currentRow = row;
						isRowWithCaret = true;
						setCaretRectangle(g, fm, textR, nextText, caretPos, maxWidth);
					}
				}
				else if (link.hasFocus() && editing) {
					if (caretUp) {
						// IF WE ARE ALREADY ON THE FIRST ROW
						if (currentRow == 0) {
							currentCaretPosition = 0;
							caretUp = false;
						}
					}

					if (caretDown) {
						// IF WE ARE ALREADY ON THE LAST ROW
						if (stopPos == text.length() && currentRow == row) {
							currentCaretPosition = text.length();
							caretDown = false;
						}
					}

					if (currentCaretPosition >= startPos &&
							(currentCaretPosition < stopPos || currentCaretPosition == stopPos && stopPos == text.length())) {
						int caretPos = currentCaretPosition - startPos;
						setCaretRectangle(g, fm, textR, nextText, caretPos, maxWidth);
						currentRow = row;
					}
					isRowWithCaret = true;
				}

				TextRowElement element = new TextRowElement(nextText, startPos, new Rectangle(textR.x, textR.y, textR.width, textR.height), isRowWithCaret);
				textRowElements.addElement(element);

				textR.y += fm.getAscent() + fm.getDescent();
				labelRectangle.height+=fm.getAscent();
			}

			if (caretUp) {
				if (currentRow > 0) {
					caretUp = false;
					recalculateCaretRectangle(g, fm, maxWidth, true);
				}
			}
			else if (caretDown) {
				if (currentRow < textRowElements.size()-1) {
					recalculateCaretRectangle(g, fm, maxWidth, false);
					caretDown = false;
				}
			}
		}
		else {
			// if dragging mouse to select text or double clicked to select word calculate selection

			if (link.hasFocus() && editing && (bDragging || doubleClicked)) {

				int tX = textR.x;
				int tY = textR.y;

				int tWidth = fm.stringWidth( text );
				if (tWidth < maxWidth) {
					tX += (maxWidth-tWidth)/2;
				}

				int caretPos = 0;
				if (editX <= tX)
					caretPos = 0;
				else if (editX >= tX+tWidth)
					caretPos = text.length();
				else {
					int ind = 1;
					int prev = 0;
					while(ind <= text.length()) {
						String n = text.substring(0, ind);
						int charX = fm.stringWidth(n);
						if (editX >= (tX+prev) && editX <= (tX+charX) ) {
							if ( (tX+prev) - editX < editX - (tX+charX))
								caretPos = ind-1;
							else
								caretPos = ind;
							break;
						}
						prev = charX;
						ind++;
					}
				}

				if (bDragging) {
					if (currentCaretPosition == -1) {
						currentCaretPosition = caretPos;
						startSelection = currentCaretPosition;
						stopSelection = currentCaretPosition;
					}
					else {
						// IF DRAGGING LEFT
						if (caretPos < currentCaretPosition) {

							if (stopSelection == -1)
								stopSelection = currentCaretPosition;

							currentCaretPosition = caretPos;

							if (startSelection == -1 || startSelection >= currentCaretPosition)
								startSelection = currentCaretPosition;
							else
								stopSelection = currentCaretPosition;
						}
						// IF DRAGGING RIGHT
						else {
							if (startSelection == -1)
								startSelection = currentCaretPosition;

							currentCaretPosition = caretPos;

							if (stopSelection == -1 || stopSelection < currentCaretPosition)
								stopSelection = currentCaretPosition;
						}
					}
				}

				// DOUBLE CLICK TO SELECT WORD
				if (doubleClicked) {
					int index = text.indexOf(" ", caretPos);
					if (index == -1)
						stopSelection = text.length();
					else {
						stopSelection = index;
					}

					currentCaretPosition = stopSelection;

					String bit = text.substring(0, caretPos);
					index = bit.lastIndexOf(" ");
					if (index == -1)
						startSelection = 0;
					else {
						startSelection = index+1;
					}
					doubleClicked = false;
				}
			}

			drawText(g, fm, link, text, textR, maxWidth, 0);

			// If mouse just clicked
			if (link.hasFocus() && editing && currentCaretPosition == -1) {

				int tX = textR.x;
				int tY = textR.y;

				int tWidth = fm.stringWidth( text );
				if (tWidth < maxWidth) {
					tX += (maxWidth-tWidth)/2;
				}

				int caretPos = 0;
				if (editX <= tX)
					caretPos = 0;
				else if (editX >= tX+tWidth)
					caretPos = text.length();
				else {
					int ind = 1;
					int prev = 0;
					while(ind <= text.length()) {
						String n = text.substring(0, ind);
						int charX = fm.stringWidth(n);
						if (editX >= (tX+prev) && editX <= (tX+charX) ) {
							if ( (tX+prev) - editX < editX - (tX+charX))
								caretPos = ind-1;
							else
								caretPos = ind;
							break;
						}
						prev = charX;
						ind++;
					}
				}

				currentCaretPosition = caretPos;
				currentRow = 0;
				setCaretRectangle(g, fm, textR, text, currentCaretPosition, maxWidth);
			}
			else if (link.hasFocus() && editing) {
				// IF UP/DOWN KEY PRESSED ON A SINGLE LINE LABEL GO HOME/END
				if (caretUp) {
					currentCaretPosition = 0;
					caretUp = false;
				}
				if (caretDown) {
					currentCaretPosition = text.length();
					caretDown = false;
				}
				currentRow = 0;
				setCaretRectangle(g, fm, textR, text, currentCaretPosition, maxWidth);
			}
		}

		if (editing) {
			// PAINT CARET
			if (caretRectangle != null) {
				Color oldCol = g.getColor();
				g.setColor(Color.red);
   		        g.fillRect(caretRectangle.x, caretRectangle.y, caretRectangle.width, caretRectangle.height);
				g.setColor(oldCol);
			}

			// PAINT SUROUNDING BOX
			g.setColor(Color.blue);
			g.drawRect(labelRectangle.x-1, labelRectangle.y-1, labelRectangle.width+1, labelRectangle.height+1);
		}
		else if (link.isRollover()) {
			g.setColor(ROLLOVER_COLOR);
			g.drawRect(labelRectangle.x-1, labelRectangle.y-1, labelRectangle.width+1, labelRectangle.height+1);
		}
  	}

	/**
	 * A helper method for the <code>paint</code> method.
	 * Calulate the caretRectangle position when it is moving up or down a row.
	 *
	 * @param g, the current Graphics context.
	 * @param fm, the font metrics to use.
	 * @param maxWidth, the maximum width for the node.
	 * @param isUp, is the caret moving up a row? True equals up a row, false equals down a row.
	 */
	private void recalculateCaretRectangle(Graphics g, FontMetrics fm, int maxWidth, boolean isUp) {

		if (isUp)
			currentRow--;
		else
			currentRow++;

		TextRowElement element = (TextRowElement)textRowElements.elementAt(currentRow);
		Rectangle currR = element.getTextRect();
		String currText = element.getText();
		int currStart = element.getStartPosition();

 		Rectangle oldCaretRectangle = new Rectangle(caretRectangle.x, caretRectangle.y, caretRectangle.width, caretRectangle.height);

		int textWidth = fm.stringWidth( currText );
		if (textWidth < maxWidth) {
			currR.x += (maxWidth-textWidth)/2;
		}

		if (oldCaretRectangle.x <= currR.x) {
			caretRectangle.x = currR.x;
			currentCaretPosition = currStart;
		}
		if (oldCaretRectangle.x >= currR.x+textWidth) {
			caretRectangle.x = currR.x+textWidth;
			currentCaretPosition = currStart+currText.length();
		}
		else {
			// FIND THE CLOSEST CHAR TO THE ONE ABOVE WHERE THE CARET WAS
			int caretPos = 0;
			int ind = 1;
			int prev = 0;
			int length = currText.length();
			while(ind <= length) {
				String n = currText.substring(0, ind);
				int charX = fm.stringWidth(n);
				if ( (currR.x+charX) > oldCaretRectangle.x ) {
					if ( oldCaretRectangle.x - (currR.x+prev) < (currR.x+charX)- oldCaretRectangle.x )
						caretPos = ind-1;
					else
						caretPos = ind;
					break;
				}
				prev = charX;
				ind++;
			}
			currentCaretPosition = currStart+caretPos;
			caretRectangle.x = currR.x+fm.stringWidth(currText.substring(0, caretPos)) - 1;
		}

		caretRectangle.y =  currR.y - fm.getAscent()+1;
        caretRectangle.width = oldCaretRectangle.width;
        caretRectangle.height = oldCaretRectangle.height;
	}

	/**
	 * A helper method for the <code>paint</code> method. Calulate and set the caretRectangle.
	 *
	 * @param g, the current Graphics context.
	 * @param fm, the font metrics to use.
	 * @param textR, the rectangle of the current text area of this label row.
	 * @param text, the row of text being addressed.
	 * @param caretIndex, the current position of the caret in this row.
	 * @param maxWidth, the maximum width for the node.
	 * @param isUp, is the caret moving up a row? True equals up a row, false equals down a row.
	 */
	private void setCaretRectangle(Graphics g, FontMetrics fm, Rectangle textR, String text, int caretIndex, int maxWidth) {

        if (caretIndex >= 0 && caretIndex <= text.length() ) {

			caretRectangle = new Rectangle();

			int textX = textR.x;
			int textY = textR.y;

			int textWidth = fm.stringWidth( text );

			int offset = 0;

			if (textWidth < maxWidth) {
				offset = (maxWidth-textWidth)/2;
				textX += offset;
			}

	        caretRectangle.x = textX + fm.stringWidth(text.substring(0, caretIndex));
            caretRectangle.y = textY - fm.getAscent()+1;
            caretRectangle.width = 1;
            caretRectangle.height = fm.getAscent()+1;
        }
	}

  	/**
   	 * Paint clippedText at textX, textY.
	 *
	 * @param g, the Graphics object to use to do the paint.
	 * @param s, the String of text to paint.
     * @param textX, the x position to start the paint.
	 * @param textY, the y position to start the paint
     * @see #paint
     */
  	protected void paintEnabledText(Graphics g, String s, int textX, int textY) {
		BasicGraphicsUtils.drawString(g, s, '\0', textX, textY);
  	}

	/**
	 * A helper method for the <code>paint</code> method. Paint a row of text.
	 *
	 * @param g, the current Graphics context.
	 * @param fm, the font metrics to use.
	 * @param link com.compendium.ui.UILink, the current link being painted.
	 * @param text, the row of text being painted.
	 * @param textR, the rectangle of the current text area of this label row.
	 * @param caretIndex, the current position of the caret in this row.
	 * @param maxWidth, the maximum width for the link.
	 * @param startPos, the starting position of this row in the context of the while label.
	 * @param isUp, is the caret moving up a row? True equals up a row, false equals down a row.
	 */
	private void drawText(Graphics g, FontMetrics fm, UILink link, String text, Rectangle textR,
									int maxWidth, int startPos) {

		Color oldColor = null;

		if (text != null) {

			int textX = textR.x;
			int textY = textR.y;

			int textWidth = fm.stringWidth( text );

			if (textWidth < maxWidth) {
				textR.width = textWidth;
				textX += (maxWidth-textWidth)/2;
			}

			// text background will always be opaque
			oldColor = g.getColor();

			int stopPos = startPos+text.length();
			if (link.isSelected()) {
				if (editing && (startSelection > -1 && stopSelection > -1 && stopSelection > startSelection)
						&& (startSelection < stopPos)
						&& (startSelection >= startPos || stopSelection > startPos) ) {

					// ONLY PAINT THE PART INSIDE SELECTION
					int begin = startSelection-startPos;
					int end = stopSelection-startPos;
					if (end > text.length())
						end = text.length();
					if (begin < 0)
						begin = 0;

					String beginText = text.substring(0, begin);
					String selText = text.substring(begin, end);
					String endText = text.substring(end);
					int beginWidth = fm.stringWidth(beginText);
					int selWidth = fm.stringWidth(selText);

					g.setColor(EDITING_COLOR);
					g.fillRect(textX, textY-fm.getAscent(), beginWidth, textR.height);
					g.setColor(TSELECTED_COLOR);
					g.fillRect(textX+beginWidth, textY-fm.getAscent(), selWidth, textR.height);
					g.setColor(EDITING_COLOR);
					g.fillRect(textX+beginWidth+selWidth, textY-fm.getAscent(), textR.width-selWidth, textR.height);

					g.setColor(SELECTED_TEXT_COLOR);
					paintEnabledText(g, beginText, textX, textY);
					g.setColor(TSELECTED_TEXT_COLOR);
					paintEnabledText(g, selText, textX+beginWidth, textY);
					g.setColor(SELECTED_TEXT_COLOR);
					paintEnabledText(g, endText, textX+beginWidth+selWidth, textY);
					g.setColor(oldColor);
				}
				else {
					if (editing)
						g.setColor(EDITING_COLOR);
					else
						g.setColor(SELECTED_COLOR);

					g.fillRect(textX, textY-fm.getAscent(), textR.width, textR.height);
					g.setColor(SELECTED_TEXT_COLOR);
					paintEnabledText(g, text, textX, textY);
					g.setColor(oldColor);
				}
			}
			else {
				g.setColor(Color.white);
				g.fillRect(textX, textY-fm.getAscent(), textR.width, textR.height);
				g.setColor(oldColor);
				g.setColor(link.getForeground());
				paintEnabledText(g, text, textX, textY);
				g.setColor(oldColor);
			}
		}
	}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////


	/**
	 * Add the passed string to the link label at the point of the current caret position.
	 *
	 * @param key, the string to add to the label.
	 */
	private void addCharToLabel(String key) {

		String oldText = oLink.getText();

		if (startSelection > -1 && stopSelection > -1 && stopSelection > startSelection) {
			String text = oldText.substring(0, startSelection) + oldText.substring(stopSelection);
			currentCaretPosition = startSelection;
			startSelection = -1;
			stopSelection = -1;
			oldText = text;
		}

		if(oldText.equals(ICoreConstants.NOLABEL_STRING)) {
			oldText = "";
			currentCaretPosition = 0;
		}

		String newText = "";
		if (currentCaretPosition < oldText.length())
			newText = oldText.substring(0, currentCaretPosition) + key + oldText.substring(currentCaretPosition);
		else
			newText = oldText + key;

		currentCaretPosition ++;
		previousString = oldText;
		oLink.setText(newText);
		ProjectCompendium.APP.setStatus(newText);
	}

	/**
	 * If editing, do a paste from the clipboard into the link label.
	 */
	public void paste() {
		if (editing) {
			String oldText = oLink.getText();
			previousString = oLink.getText();

			if (startSelection > -1 && stopSelection > -1 && stopSelection > startSelection) {
				String text = oldText.substring(0, startSelection) + oldText.substring(stopSelection);
				currentCaretPosition = startSelection;
				startSelection = -1;
				stopSelection = -1;
				oldText = text;
			}

			if (clipboard == null)
				clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

           	Transferable clipData = clipboard.getContents(this);

		    String s="";
    		try {
         		s = (String)(clipData.getTransferData(DataFlavor.stringFlavor));
        	}
            catch (Exception ufe) {}

			if (oldText.equals(ICoreConstants.NOLABEL_STRING)) {
				currentCaretPosition = 0;
				oldText = "";
			}

			String text = oldText.substring(0, currentCaretPosition) + s + oldText.substring(currentCaretPosition);
    		currentCaretPosition+=s.length();
			oLink.setText(text);
			ProjectCompendium.APP.setStatus(text);
		}
	}

	/**
	 * If editing, do a cut to the clipboard from the link label.
	 */
	public void cut() {
		if (editing) {
			String text = oLink.getText();

			if (startSelection < 0 || stopSelection < 0 || stopSelection > text.length())
				return;

			if (clipboard == null)
				clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

         	StringSelection data;
         	String selectedText = text.substring(startSelection, stopSelection);
         	data = new StringSelection(selectedText);
         	clipboard.setContents(data, data);

			previousString = text;
			text = text.substring(0, startSelection) + text.substring(stopSelection);
         	oLink.setText(text);

			currentCaretPosition = startSelection;
			startSelection = -1;
			stopSelection = -1;

			oLink.repaint();
		}
	}

	/**
	 * If editing, delete selected text from the link label.
	 */
	public void delete() {
		if (editing) {
			if (startSelection > -1 && stopSelection > -1 && stopSelection > startSelection) {
				String text = oLink.getText();
				previousString = text;

				text = text.substring(0, startSelection) + text.substring(stopSelection);
				currentCaretPosition = startSelection;
				startSelection = -1;
				stopSelection = -1;

				oLink.setText(text);
				oLink.repaint();
				ProjectCompendium.APP.setStatus(text);
			}
			else {
				String text = oLink.getText();
				previousString = text;

				if (text.equals(ICoreConstants.NOLABEL_STRING)) {
					text = " ";
					currentCaretPosition = 0;
				}
				else if (currentCaretPosition >= 0 &&  currentCaretPosition < text.length() && text != null && !text.equals("")) {
					text = text.substring(0, currentCaretPosition) + text.substring(currentCaretPosition+1);
				}

				oLink.setText(text);
				oLink.repaint();
				ProjectCompendium.APP.setStatus(text);
			}
		}
	}

	/**
	 * If editing, do a copy to the clipboard from the link label.
	 */
	public void copy() {
		if (editing) {
			String text = oLink.getText();

			if (startSelection < 0 || stopSelection < 0 || stopSelection > text.length())
				return;

			if (clipboard == null)
				clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

             	StringSelection data;
         	String selectedText = text.substring(startSelection, stopSelection);
         	data = new StringSelection(selectedText);
         	clipboard.setContents(data, data);
		}
	}

	/**
	 * If editing, select all the text in the link label.
	 */
	public void selectAll() {
		if (editing) {
			String temp = oLink.getText();
			startSelection = 0;
			stopSelection =temp.length();
			oLink.repaint();
		}
	}

	/**
	 * Invoked when a key is typed in a component.
	 * @param e, the associated KeyEvent.
	 */
	// NEED THIS HEAR TO PICK UP KEYCHARS FROM MULTIPLE KEYS LIKE ACCENTED LETTER ON FOREIGHN KEYBOARDS.
	public void keyTyped(KeyEvent evt) {

		if (oLink != null && !oLink.hasFocus()) {
			return;
		}

		// IF EDITING THE TEXT AREA
		if (editing) {
			char keyChar = evt.getKeyChar();
			char[] key = {keyChar};
			sKeyPressed = new String(key);
			int modifiers = evt.getModifiers();

			if (ProjectCompendium.isMac && modifiers == shortcutKey) {
				evt.consume();
			} else {
				if (( Character.isLetterOrDigit(keyChar) || sKeyPressed.equals(" ")  ||
							IUIConstants.NAVKEYCHARS.indexOf(sKeyPressed) != -1) ) {
					addCharToLabel(sKeyPressed);
					evt.consume();
				}
			}
		}
	}
	
	/**
	 * Invoked when a key is pressed in a component.
	 * @param evt, the associated KeyEvent.
	 */
	public void keyPressed(KeyEvent evt) {

		char keyChar = evt.getKeyChar();
		char[] key = {keyChar};
		sKeyPressed = new String(key);

		int keyCode = evt.getKeyCode();
		int modifiers = evt.getModifiers();

		// IF EDITING THE TEXT AREA
		if (editing) {
			if (modifiers == java.awt.Event.CTRL_MASK && keyCode == KeyEvent.VK_LEFT) {
				if (currentCaretPosition > 0) {
					String text = oLink.getText();
					String section = text.substring(0, currentCaretPosition);
					int index = section.lastIndexOf(" ");

					if (index == section.length()-1) {
						section = section.substring(0, section.length()-2);
						index = section.lastIndexOf(" ") - 1;
						if (index == -1) {
							currentCaretPosition = 0;
						}
						else {
							currentCaretPosition = currentCaretPosition - ( section.length() - index );
						}
					}
					else if (index == -1) {
						currentCaretPosition = 0;
					}
					else {
						currentCaretPosition = currentCaretPosition - ( section.length() - index - 1 );
					}
				}

				oLink.repaint();
				evt.consume();
			}
			else if (modifiers == java.awt.Event.CTRL_MASK && keyCode == KeyEvent.VK_RIGHT) {

				String text = oLink.getText();
				if (currentCaretPosition < text.length()) {

					String section = text.substring(currentCaretPosition);
					int index = section.indexOf(" ");

					if (index == currentCaretPosition+1) {
						section = section.substring(1);
						index = section.indexOf(" ")+1;
					}

					if (index == -1)
						currentCaretPosition = text.length();
					else
						currentCaretPosition += index+1;
				}

				oLink.repaint();
				evt.consume();
			}
			else if (modifiers == shortcutKey) {
				switch(keyCode) {
					case KeyEvent.VK_X: { // CUT
						cut();
						evt.consume();
						break;
					}
					case KeyEvent.VK_C: { // COPY
						copy();
						evt.consume();
						break;
					}
					case KeyEvent.VK_V: { // PASTE INTO LABEL
						paste();
						evt.consume();
						break;
					}
					case KeyEvent.VK_Z: { // UNDO LABEL PASTE
						String temp = oLink.getText();
						oLink.setText(previousString);
						currentCaretPosition = previousString.length();
						previousString = temp;
						break;
					}
					case KeyEvent.VK_A: { // SELECT ALL
						selectAll();
						evt.consume();
						break;
					}
				}
			}

			// FOLLOW SECTIONS FOR MIMICING TEXT FIELD BEHAVIOUR
			else if (keyCode == KeyEvent.VK_HOME && modifiers == 0) {
				currentCaretPosition=0;
				oLink.repaint();
				evt.consume();
			}
			else if (keyCode == KeyEvent.VK_END && modifiers == 0) {
				String text = oLink.getText();
				currentCaretPosition=text.length();
				oLink.repaint();
				evt.consume();
			}
			else if (keyCode == KeyEvent.VK_UP && modifiers == 0) {
				startSelection = -1;
				stopSelection = -1;
				caretUp = true;
				oLink.repaint();
				evt.consume();
			}
			else if (keyCode == KeyEvent.VK_DOWN && modifiers == 0) {
				startSelection = -1;
				stopSelection = -1;
				caretDown = true;
				oLink.repaint();
				evt.consume();
			}
			else if (keyCode == KeyEvent.VK_LEFT && modifiers == 0) {
				startSelection = -1;
				stopSelection = -1;

				if (currentCaretPosition > 0)
					currentCaretPosition--;
				oLink.repaint();
				evt.consume();
			}
			else if (keyCode == KeyEvent.VK_RIGHT && modifiers == 0) {
				startSelection = -1;
				stopSelection = -1;

				String text = oLink.getText();
				if (currentCaretPosition < text.length())
					currentCaretPosition++;
				oLink.repaint();
				evt.consume();
			}

			// SELECTION
			else if (keyCode == KeyEvent.VK_HOME && modifiers == Event.SHIFT_MASK) {
				if (startSelection == -1)
					startSelection = 0;

				if (stopSelection == -1)
					stopSelection = currentCaretPosition;

				currentCaretPosition=0;

				oLink.repaint();
				evt.consume();
			}
			else if (keyCode == KeyEvent.VK_END && modifiers == Event.SHIFT_MASK) {
				if (startSelection == -1)
					startSelection = currentCaretPosition;

				String text = oLink.getText();
				currentCaretPosition=text.length();
				stopSelection = currentCaretPosition;

				oLink.repaint();
				evt.consume();
			}
			else if (keyCode == KeyEvent.VK_LEFT && modifiers == Event.SHIFT_MASK) {

				if (stopSelection == -1)
					stopSelection = currentCaretPosition;

				if (currentCaretPosition > 0)
					currentCaretPosition--;

				if (startSelection == -1 || (startSelection < stopSelection && startSelection >= currentCaretPosition)) {
					startSelection = currentCaretPosition;
				}
				else
					stopSelection = currentCaretPosition;

				oLink.repaint();
				evt.consume();
			}
			else if (keyCode == KeyEvent.VK_RIGHT && modifiers == Event.SHIFT_MASK) {

				if (startSelection == -1)
					startSelection = currentCaretPosition;

				String text = oLink.getText();
				if (currentCaretPosition < text.length())
					currentCaretPosition++;

				if (stopSelection == -1 || stopSelection < currentCaretPosition)
					stopSelection = currentCaretPosition;

				oLink.repaint();
				evt.consume();
			}
			else if (keyCode == KeyEvent.VK_LEFT && modifiers == Event.SHIFT_MASK+Event.CTRL_MASK) {
				if (currentCaretPosition > 0) {
					if (stopSelection == -1)
						stopSelection = currentCaretPosition;

					String text = oLink.getText();
					String section = text.substring(0, currentCaretPosition);
					int index = section.lastIndexOf(" ");

					if (index == section.length()-1) {
						section = section.substring(0, section.length()-2);
						index = section.lastIndexOf(" ") - 1;
						if (index == -1)
							currentCaretPosition = 0;
						else
							currentCaretPosition = currentCaretPosition - ( section.length() - index );
					}
					else if (index == -1)
						currentCaretPosition = 0;
					else
						currentCaretPosition = currentCaretPosition - ( section.length() - index - 1 );

					if (startSelection == -1 || startSelection > currentCaretPosition)
						startSelection = currentCaretPosition;
				}

				oLink.repaint();
				evt.consume();
			}
			else if (keyCode == KeyEvent.VK_RIGHT && modifiers == Event.SHIFT_MASK+Event.CTRL_MASK) {
				String text = oLink.getText();
				if (currentCaretPosition < text.length()) {
					if (startSelection == -1)
						startSelection = currentCaretPosition;

					String section = text.substring(currentCaretPosition);
					int index = section.indexOf(" ");

					if (index == currentCaretPosition+1) {
						section = section.substring(1);
						index = section.indexOf(" ")+1;
					}

					if (index == -1)
						currentCaretPosition = text.length();
					else
						currentCaretPosition += index+1;

					stopSelection = currentCaretPosition;
				}

				oLink.repaint();
				evt.consume();
			}

			// DELETING CHARS
			else if (keyCode == KeyEvent.VK_BACK_SPACE && ( modifiers == 0 || modifiers == Event.SHIFT_MASK)) {

				if (startSelection > -1 && stopSelection > -1 && stopSelection > startSelection) {
					String text = oLink.getText();
					previousString = text;

					text = text.substring(0, startSelection) + text.substring(stopSelection);
					currentCaretPosition = startSelection;
					startSelection = -1;
					stopSelection = -1;

					oLink.setText(text);
					oLink.repaint();
					ProjectCompendium.APP.setStatus(text);
				}
				else {
					String text = oLink.getText();
					previousString = text;
					if (text.equals(ICoreConstants.NOLABEL_STRING)) {
						text = " ";
						currentCaretPosition = 0;
					}
					else if (currentCaretPosition > 0 && text != null && !text.equals("")) {
						text = text.substring(0, currentCaretPosition-1) + text.substring(currentCaretPosition);
							currentCaretPosition--;
					}

					if (stopSelection > -1)
						stopSelection = currentCaretPosition;

					oLink.setText(text);
					oLink.repaint();
					ProjectCompendium.APP.setStatus(text);
				}
				evt.consume();
			}
			else if (keyCode == KeyEvent.VK_DELETE && modifiers == 0) {
				delete();
				evt.consume();
			}
			
			// MOVED TO KEYTYPED TO PICK UP ACCENTED CHARACTER ETC WHICH ARE DOUBLE KEYSTOKES			
			/*else if ( Character.isLetterOrDigit(keyChar) || sKeyPressed.equals(" ")  ||
							IUIConstants.NAVKEYCHARS.indexOf(sKeyPressed) != -1) {
				addCharToLabel(sKeyPressed);
				oLink.repaint();
				evt.consume();
			}*/
		}
	}

	/**
	 * Handles the single and double click events.
	 * @param evt, the associated MouseEvent.
	 */
  	public void mouseClicked(MouseEvent evt) {

		int clickCount = evt.getClickCount();

	  	if (SwingUtilities.isLeftMouseButton(evt)) {
			int nX = evt.getX();
			int nY = evt.getY();

			// CHECK LABEL HAS BEEN CLICKED			
			if (labelRectangle != null && labelRectangle.contains(nX, nY)) {
				if (clickCount == 1) {
					editing = true;
					editX = nX;
					editY = nY;
					startSelection = -1;
					stopSelection = -1;

					currentCaretPosition = -1;
					doubleClicked = false;

					oLink.moveToFront();

					if (!oLink.isSelected()) {
						oLink.getViewPane().setSelectedLink(null, ICoreConstants.DESELECTALL);
						oLink.getViewPane().setSelectedLink(oLink, ICoreConstants.SINGLESELECT);
						oLink.setSelected(true);
					}
					
					oLink.requestFocus();
					evt.consume();
				}
				else if (clickCount == 2) {
					editing = true;
					editX = nX;
					editY = nY;
					startSelection = -1;
					stopSelection = -1;
					currentCaretPosition = -1;
					doubleClicked=true;
					oLink.moveToFront();
					if (!oLink.isSelected()) {
						oLink.getViewPane().setSelectedLink(null, ICoreConstants.DESELECTALL);
						oLink.getViewPane().setSelectedLink(oLink, ICoreConstants.SINGLESELECT);
						oLink.setSelected(true);
					}
					oLink.requestFocus();
					evt.consume();					
				}
			}
			else {
				editing = false;
				doubleClicked = false;
				oLine.getParent().dispatchEvent(evt);
			}
		}
		else {
			oLine.getParent().dispatchEvent(evt);
		}
	}

	/**
	 * Handles the initiation of drag and drop events.
	 * @param evt, the associated MouseEvent.
	 */
  	public void mousePressed(MouseEvent evt) {

		startSelection = -1;
		stopSelection = -1;
		doubleClicked = false;

		bDragging = true;
		if (editing && SwingUtilities.isLeftMouseButton(evt)) {
			if (labelRectangle != null && labelRectangle.contains(evt.getX(), evt.getY())) {
				editX = evt.getX();
				editY = evt.getY();
				currentCaretPosition = -1;
				oLink.repaint();
				return;
			}
			else {
				editing = false;
			}
		}
		else {
			oLine.getParent().dispatchEvent(evt);
		}
  	}

	/**
	 * Handles drag and drop finish operations.
	 * @param evt, the associated MouseEvent.
	 */
  	public void mouseReleased(MouseEvent evt) {

		Point p = SwingUtilities.convertPoint((Component)evt.getSource(), evt.getX(), evt.getY(), null);

		boolean isLeftMouse = SwingUtilities.isLeftMouseButton(evt);
		boolean isRightMouse = SwingUtilities.isRightMouseButton(evt);

		if (bDragging) {
			if (isLeftMouse && !evt.isAltDown() && editing) {
				if (labelRectangle != null && labelRectangle.contains(evt.getX(), evt.getY())) {
					editX = evt.getX();
					editY = evt.getY();
					oLink.repaint();
				}
			}
			else {
				oLine.getParent().dispatchEvent(evt);
			}
			bDragging = false;
		}
		else {
			oLine.getParent().dispatchEvent(evt);
		}
	}

	/**
	 * Invoked when a mouse is dragged (pressed and moved).
	 * @param evt, the associated MouseEvent.
	 */
	public void mouseDragged(MouseEvent evt) {

		if (bDragging) {
			if (SwingUtilities.isLeftMouseButton(evt) && !evt.isAltDown() && editing) {
				if (labelRectangle != null && labelRectangle.contains(evt.getX(), evt.getY())) {
					editX = evt.getX();
					editY = evt.getY();
					oLink.repaint();
					return;
				}
			}
			else {
				oLine.getParent().dispatchEvent(evt);
			}
		}
		else {
			oLine.getParent().dispatchEvent(evt);
		}
	}

	/**
	 * Calulate the size of the max with and height for the label text area.
	 * @param rFrom, the from node.
	 * @param rTo, the to node.
	 * @param viewR, the link palette size.
	 * @param fm, the fontmetrics to use.
	 */
	private Dimension calculateMaxSize(Rectangle rFrom, Rectangle rTo, FontMetrics fm) {

		int maxWidth = -1;
		int maxHeight = -1;
		int charWidth = fm.charWidth('W');
		int lineHeight = fm.getAscent() + fm.getDescent(); //(fm.getDescent()*2)

		// IF THE FROM NODE IS TOP
		if (rFrom.y < rTo.y) {
			// IF DRAWING HORIZONAL BOXES - THERE MUST BE ROOM FOR AT LEAST 1 LINE OF TEXT
			if (rFrom.y + rFrom.height+lineHeight < rTo.y) {
				maxHeight = rTo.y - (rFrom.y + rFrom.height);
				maxWidth = 0; // UNLIMITED
			}
			// IF VERTICAL VERTICAL BOXES - THERE MUST BE ROOM FOR AT LEAST 1 CHARACTER
			else if (rFrom.x + rFrom.width+charWidth < rTo.x) {
				maxWidth = rTo.x - (rFrom.x + rFrom.width);
				maxHeight = 0; //  UNLIMITED
			}
			else if (rTo.x + rTo.width+fm.charWidth('W') < rFrom.x) {
				maxWidth = rFrom.x - (rTo.x + rTo.width);
				maxHeight = 0; //  UNLIMITED
			}
			else {
				//no room for text.
			}
		}
		// IF THE TO NODE IS TOP
		else {
			// IF DRAWING HORIZONAL BOXES
			if (rTo.y + rTo.height + lineHeight < rFrom.y) {
				maxHeight = rFrom.y - (rTo.y + rTo.height);
				maxWidth = 0; // UNLIMITED
			}
			// IF VERTICAL VERTICAL BOXES - THERE MUST BE ROOM FOR AT LEAST 1 CHARACTER
			else if (rFrom.x + rFrom.width+charWidth < rTo.x) {
				maxWidth = rTo.x - (rFrom.x + rFrom.width);
				maxHeight = 0; //  UNLIMITED
			}
			else if (rTo.x + rTo.width+charWidth < rFrom.x) {
				maxWidth = rFrom.x - (rTo.x + rTo.width);
				maxHeight = 0; //  UNLIMITED
			}
			else {
				// no room for text
			}
		}

		return new Dimension(maxWidth, maxHeight);
	}

	/**
	 * Calculate the requred dimensions of the given node.
	 * Checks for icon and various node indicator extras when calculating.
	 * @param node com.compendium.ui.UILink, the link to calculate the dimensions for.
	 * @return Dimension, the dimension for the given link.
	 */
	private Dimension calculateDimension(UILink link) {

		String text = link.getText();

		Insets insets = link.getInsets();
		int dx = insets.left + insets.right;
		int dy = insets.top + insets.bottom;

		Point from = link.getFrom();
		Point to	 = link.getTo();

		if (from == null || to == null)
			return new Dimension(0,0);

		int width	= Math.abs(from.x - to.x);
		int height	= Math.abs(from.y - to.y);

		// make sure that the width and height are large enough
		// to fit a possible arrow, the line thickness and takes into account the minimum width
		int w = Math.max(link.getLineThickness(), link.getMinWidth());
		if ((link.getArrow() != ICoreConstants.NO_ARROW) && (link.CURRENT_ARROW_WIDTH > w))
			w = link.CURRENT_ARROW_WIDTH;

		width += w;
		height += w;

		linkDimension = new Dimension(width, height);

		Font font = link.getFont();
		FontMetrics fm = link.getFontMetrics(font);

		Rectangle textR = new Rectangle();
		Rectangle viewR = new Rectangle(link.getSize());

		viewR.x = insets.left;
		viewR.y = insets.top;
		viewR.width = width;
		viewR.height = height;

		int textWidth = fm.stringWidth( text );
		int widestLine = 0;

		UINode fromNode = link.getFromNode();
		UINode toNode = link.getToNode();
		Rectangle rFrom = fromNode.getBounds();
		Rectangle rTo = toNode.getBounds();

		Dimension maxSize = calculateMaxSize(rFrom, rTo, fm);
		int maxWidth = maxSize.width;
		int maxHeight = maxSize.height;

		int oldMaxWidth = maxWidth;
		
		Model oModel = (Model)ProjectCompendium.APP.getModel();		
		int wrapWidth = oModel.labelWrapWidth;
		int preferredWrapWidth = fm.charWidth('W') * wrapWidth;

		// NO ROOM FOR TEXT
		if (maxWidth == -1 && maxHeight == -1) {
			Dimension rv = new Dimension(width, height);
			rv.width += dx+1;
			rv.height += dy;
			textWidth = 0;
			textHeight = 0;
			return rv;
		}
		// UNLIMITED WIDTH AVAILABLE - MAKE THE MAXWIDTH THE WRAPWIDTH
		else if (maxWidth == 0) {
			maxWidth = preferredWrapWidth;
		}
		// UNLIMITED HEIGHT AVAILABLE
		else if (maxHeight == 0) {
			if (maxWidth > preferredWrapWidth)
				maxWidth = preferredWrapWidth;
		}

		textR.width = textWidth;
		if (textWidth == 0)
			textWidth = 30;
		textR.height = 0;
		textR.x = dx;

		if (textWidth > maxWidth) {
			int loop = -1;
			String textLeft = text;

			while ( textLeft.length() > 0 ) {
				loop ++;

				int thisTextWidth = fm.stringWidth( textLeft );
				int textLen = textLeft.length();
				int curLen = 1;

				if (thisTextWidth > maxWidth) {
					while(curLen <= textLen) {

						String next = textLeft.substring(0, curLen);
						if (fm.stringWidth(next) < maxWidth) {
							curLen++;
						}
						else if (fm.stringWidth(next) >= maxWidth) {
							curLen--;
							break;
						}
					}
				}
				else {
					curLen = textLen;
				}

				String nextText = textLeft.substring(0, curLen);
				if (curLen < textLen) {
					int lastSpace = nextText.lastIndexOf(" ");
					if (lastSpace != -1 && lastSpace != textLen) {
						curLen = lastSpace+1;
						nextText = textLeft.substring(0, curLen);
						textLeft = textLeft.substring(curLen);
					}
					else {
						nextText = textLeft.substring(0, curLen);
						textLeft = textLeft.substring(curLen);
					}

					textR.height += fm.getAscent()+fm.getDescent();
				}
				else {
					if (!textLeft.equals(""))
						textR.height += fm.getAscent() + fm.getDescent();
					nextText = textLeft;
					textLeft = "";
				}

				int thisWidth = fm.stringWidth( nextText );
				if ( thisWidth > widestLine) {
					widestLine = thisWidth;
				}
			}
		}
		else {
			if (maxWidth > 0) {
				widestLine = textWidth;
				textR.height += fm.getAscent() + fm.getDescent();
			}
			else {
				widestLine = 0;
				textR.height += fm.getAscent() + fm.getDescent();
			}
		}

		maxTextWidth = widestLine;
		if (maxTextWidth > width) {
			width = maxTextWidth;
		}

		if (textR.height > height) {
			height = textR.height;
			textHeight = height;
		}
		else {
			textHeight = textR.height;
		}

		Dimension rv = new Dimension(width, height);

		rv.width += dx+2;
		rv.height += dy+2;

		return rv;
	}

	/**
	 * Return the component preferred size.
	 * @param c, the component to return the preferred size for.
	 * @return Dimension, the preferred size for the given component.
	 */
 	public Dimension getPreferredSize(JComponent c) {
		return calculateDimension((UILink)c);
  	}

	/**
	 * @return getPreferredSize(c)
	 */
	public Dimension getMinimumSize(JComponent c) {
		return getPreferredSize(c);
	}

	/**
	 * @return getPreferredSize(c)
	 */
	public Dimension getMaximumSize(JComponent c) {
		return getPreferredSize(c);
	}

	/**
	 * Return the prefrerred bounds of this component.
	 * @param c, the component to return the preferred bounds for.
	 * @return Rectangle, the preferred bounds of this component.
	 */
	public Rectangle getPreferredBounds(JComponent c) {

		UILink link = (UILink)c;
		Point from = link.getFrom();
		Point to	 = link.getTo();

		if (from == null || to == null)
			return new Rectangle(0,0,0,0);

		Dimension size = getPreferredSize(c);

		// determine absolute to and from points in parent's coordinate system
		Point f = new Point(0,0);
		Point t = new Point(0,0);
		if (oLine.getCoordinateType() == UILine.ABSOLUTE) {
			// coordinates already relative to this components coordinate system
			f = from;
			t = to;
		}
		else {
			// calculate the absolute coordinates by converting the coordinates from
			// this components coordinate system to the parents coordinate system
			Container parent = link.getParent();
			if (parent != null) {
				f = SwingUtilities.convertPoint(link, from, parent);
				t = SwingUtilities.convertPoint(link, to, parent);
			}
		}

		int x = Math.min(f.x, t.x);
		int y = Math.min(f.y, t.y);

		// give room for possible arrow and/or line thickness
		int w = Math.max(link.getLineThickness(), link.getMinWidth());
		if ((link.getArrow() != ICoreConstants.NO_ARROW) && (link.CURRENT_ARROW_WIDTH > w))
			w = link.CURRENT_ARROW_WIDTH;

		x -= w/2;
		y -= w/2;

		// ADJUST THE X AND Y LOCATION TO CENTER POSSIBLE LARGER BOX
		// DO WE NEED TO MOVE THE X POS
		if (size.width > linkDimension.width) {
			x -= (size.width-linkDimension.width)/2;
		}

		// DO WE NEED TO MOVE THE Y POS
		if (size.height > linkDimension.height) {
			y -= (size.height-linkDimension.height)/2;
		}
		return new Rectangle(x, y, size.width, size.height);
	}

	/**
	 * Refresh the bounds of this object.
	 * Calls <code>getPreferredSize</code>,
	 * which is important for the paint method to work correctly later.#
	 * @see #getPreferredSize
	 */
	public void refreshBounds() {
		oLink.setBounds(getPreferredBounds(oLink));
	}

	/**
	 * Return the rectagnle for this link's label area.
	 * @return Rectangle, the rectagnle for this link's label area.
	 */
	public Rectangle getLabelRectangle() {
		return labelRectangle;
	}
}
