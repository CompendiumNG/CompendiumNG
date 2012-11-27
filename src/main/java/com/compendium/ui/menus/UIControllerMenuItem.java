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

package com.compendium.ui.menus;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.MouseInputListener;
import javax.swing.UIManager;

import com.compendium.ProjectCompendium;
import com.compendium.ui.IUIConstants;
import com.compendium.ui.UIImageButton;
import com.compendium.ui.UIImages;

public class UIControllerMenuItem extends JPanel implements MenuElement{
    
	private Color savedBackground = null;

	private static MenuElement NO_SUB_ELEMENTS[] = new MenuElement[0];
	
	/** The string that identifies if the menu is extended.*/
	public final static String	EXTEND_MENU				= "editextend";

	/** The string that identifies if the menu is contracted.*/
	public final static String	COLLAPSE_MENU			= "editcollapse";

	/** The item with the extender arrow.*/
	private UIImageButton			button					= null;
		
	public UIControllerMenuItem() {	
		JMenuItem test = new JMenuItem();
		setLayout(new BorderLayout());		
		setBorder(new EmptyBorder(4,4,4,4));		
		
		button = new UIImageButton(UIImages.get(IUIConstants.DOWN_ARROW_ICON));
		button.setName(EXTEND_MENU);		
		button.setHorizontalAlignment(SwingConstants.CENTER);	
		add(button,BorderLayout.CENTER);

    	button.setBackground((Color)UIManager.get("MenuItem.background"));
		this.setBackground((Color)UIManager.get("MenuItem.background"));
		
		init();
	}

	private void init() {
      setRequestFocusEnabled(true);
      
      // Borrows heavily from BasicMenuUI
      MouseInputListener mouseInputListener = new MouseInputListener() {
    	  // If mouse released over this menu item, activate it
    	  public void mouseReleased(MouseEvent mouseEvent) {
    		  MenuSelectionManager menuSelectionManager = MenuSelectionManager.defaultManager();
    		  Point point = mouseEvent.getPoint();
    		  if ((point.x >= 0) && (point.x < getWidth()) && (point.y >= 0)
    				  && (point.y < getHeight())) {
    			  //menuSelectionManager.clearSelectedPath();
    			  // component automatically handles "selection" at this point
    			  // doClick(0); // not necessary
    		  } else {
    			  menuSelectionManager.processMouseEvent(mouseEvent);
    		  }
    	  }

    	  // If mouse moves over menu item, add to selection path, so it
    	  // becomes armed
    	  public void mouseEntered(MouseEvent mouseEvent) {
    		  MenuSelectionManager menuSelectionManager = MenuSelectionManager.defaultManager();
    		  menuSelectionManager.setSelectedPath(getPath());
    	  }

    	  // When mouse moves away from menu item, dissarm it and select
    	  // something else
    	  public void mouseExited(MouseEvent mouseEvent) {
    		  MenuSelectionManager menuSelectionManager = MenuSelectionManager.defaultManager();
    		  MenuElement path[] = menuSelectionManager.getSelectedPath();
    		  if (path.length > 1) {
    			  MenuElement newPath[] = new MenuElement[path.length - 1];
    			  for (int i = 0, c = path.length - 1; i < c; i++) {
    				  newPath[i] = path[i];
    			  }
    			  menuSelectionManager.setSelectedPath(newPath);
    		  }
    	  }

    	  // Pass along drag events
    	  public void mouseDragged(MouseEvent mouseEvent) {
    		  MenuSelectionManager.defaultManager().processMouseEvent(mouseEvent);
    	  }

    	  public void mouseClicked(MouseEvent mouseEvent) {}

    	  public void mousePressed(MouseEvent mouseEvent) {}

    	  public void mouseMoved(MouseEvent mouseEvent) {}
      	};
      	button.addMouseListener(mouseInputListener);
      	button.addMouseMotionListener(mouseInputListener);
	}
	
	
	public void pointUp() {
		button.setIcon(UIImages.get(IUIConstants.UP_ARROW_ICON));
		button.setName(COLLAPSE_MENU);		
	}
	
	public void pointDown() {
		button.setIcon(UIImages.get(IUIConstants.DOWN_ARROW_ICON));
		button.setName(EXTEND_MENU);		
	}
	
	public boolean isDown() {
		return (button.getName().equals(EXTEND_MENU));
	}
		
	public void addActionListener(ActionListener a) {
		button.addActionListener(a);
	}
	
    /**
     *  Process a key event. 
     */
   public void processKeyEvent(KeyEvent keyEvent, MenuElement path[], MenuSelectionManager manager) {
	   // If user presses space while menu item armed, select it
	   if (button.getModel().isArmed()) {
		   int keyChar = keyEvent.getKeyChar();
		   if (keyChar == KeyEvent.VK_SPACE) {
			   //manager.clearSelectedPath();
			   button.doClick(0); // inherited from AbstractButton
           }
	   }
   }

   /**
     * Processes a mouse event. <code>event</code> is a <code>MouseEvent</code>
     * with source being the receiving element's component.
     * <code>path</code> is the path of the receiving element in the menu
     * hierarchy including the receiving element itself.
     * <code>manager</code> is the <code>MenuSelectionManager</code>
     * for the menu hierarchy.
     * This method should process the <code>MouseEvent</code> and change
     * the menu selection if necessary
     * by using <code>MenuSelectionManager</code>'s API
     * Note: you do not have to forward the event to sub-components.
     * This is done automatically by the <code>MenuSelectionManager</code>.
     */
   public void processMouseEvent(MouseEvent mouseEvent, MenuElement path[],MenuSelectionManager manager) {
	   // For when mouse dragged over menu and button released
       if (mouseEvent.getID() == MouseEvent.MOUSE_RELEASED) {
    	   //manager.clearSelectedPath();
           button.doClick(0); // inherited from AbstractButton
       }
   }

   public void setFocus() {
	   button.requestFocus();
   }
   
   /**
    * Call by the <code>MenuSelectionManager</code> when the
    * <code>MenuElement</code> is added or remove from 
    * the menu selection.
    */
   public void menuSelectionChanged(boolean isIncluded) {
        ButtonModel model = button.getModel();
        // only change armed state if different
        if (model.isArmed() != isIncluded) {
        	model.setArmed(isIncluded);
        }

        if (isIncluded) {
        	button.setSelected(true);
       		button.setBackground((Color)UIManager.get("MenuItem.selectionBackground"));
       		this.setBackground((Color)UIManager.get("MenuItem.selectionBackground"));
        } else {
        	button.setSelected(false);
        	button.setBackground((Color)UIManager.get("MenuItem.background"));
    		this.setBackground((Color)UIManager.get("MenuItem.background"));
        }
    }   
 
    /**
     * This method should return an array containing the sub-elements for the receiving menu element
     *
     * @return an array of MenuElements
     */
    public MenuElement[] getSubElements() {
        // no subelements
        return NO_SUB_ELEMENTS;
    }
    
    /**
     * This method should return the java.awt.Component used to paint the receiving element.
     * The returned component will be used to convert events and detect if an event is inside
     * a MenuElement's component.
     *
     * @return the Component value
     */
    public Component getComponent() {
    	return this;
    }
 
    // Borrows heavily from BasicMenuItemUI.getPath()
    private MenuElement[] getPath() {
    	MenuSelectionManager menuSelectionManager = MenuSelectionManager.defaultManager();
    	MenuElement oldPath[] = menuSelectionManager.getSelectedPath();
    	MenuElement newPath[];
    	int oldPathLength = oldPath.length;
    	if (oldPathLength == 0) {
    		return new MenuElement[0];
    	}
    	Component parent = getParent();
    	if (oldPath[oldPathLength - 1].getComponent() == parent) {
    		// Going deeper under the parent menu
    		newPath = new MenuElement[oldPathLength + 1];
    		System.arraycopy(oldPath, 0, newPath, 0, oldPathLength);
    		newPath[oldPathLength] = this;
    	} else {
    		// Sibling/child menu item currently selected
    		int newPathPosition;
    		for (newPathPosition = oldPath.length - 1; newPathPosition >= 0; newPathPosition--) {
    			if (oldPath[newPathPosition].getComponent() == parent) {
    				break;
    			}
    		}
    		newPath = new MenuElement[newPathPosition + 2];
    		System.arraycopy(oldPath, 0, newPath, 0, newPathPosition + 1);
    		newPath[newPathPosition + 1] = this;
    	}
    	return newPath;
    }    
}
