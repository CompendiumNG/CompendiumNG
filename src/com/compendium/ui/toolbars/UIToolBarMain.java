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

package com.compendium.ui.toolbars;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.help.*;
import javax.swing.*;
import javax.swing.undo.*;

import com.compendium.LanguageProperties;
import com.compendium.ui.*;
import com.compendium.ui.toolbars.system.*;
import com.compendium.core.datamodel.*;


/**
 * This class manages the main toolbar
 *
 * @author	Michelle Bachler
 * @version	1.0
 */
public class UIToolBarMain implements IUIToolBar, IUIConstants, ActionListener {

	/** Indicates whether the node format toolbar is switched on or not by default.*/
	private final static boolean DEFAULT_STATE			= true;
	
	/** Indicates the default orientation for this toolbars ui object.*/
	private final static int DEFAULT_ORIENTATION		= SwingConstants.HORIZONTAL;	
	
	/** This indicates the type of the toolbar.*/
	private	int 					nType			= -1;
	
	/** The parent frame for this class.*/
	private ProjectCompendiumFrame	oParent			= null;
	
	/** The overall toolbar manager.*/
	private IUIToolBarManager 		oManager		= null;

	/** The main toobar.*/
	private UIToolBar			tbrToolBar			= null;

	/** The open database toobar button.*/
	private JButton				pbOpen				= null;

	/** The cut toobar button.*/
	private JButton				pbCut				= null;

	/** The copy toobar button.*/
	private JButton				pbCopy				= null;

	/** The paste toobar button.*/
	private JButton				pbPaste				= null;

	/** The back history toobar button.*/
	private JButton				pbBack				= null;

	/** The close database toobar button.*/
	private JButton				pbClose				= null;

	/** The delete toobar button.*/
	private JButton				pbDelete			= null;

	/** The forward history toobar button.*/
	private JButton				pbForward			= null;

	/** The redo toobar button.*/
	private JButton				pbRedo				= null;

	/** The undo toobar button.*/
	private JButton				pbUndo				= null;

	/** The help toobar button.*/
	private JButton				pbHelp				= null;

	/** The search toobar button.*/
	private JButton				pbSearch			= null;

	/** The back history menu toobar button.*/
	private JButton				pbShowBackHistory	= null;

	/** The forward history menu toobar button.*/
	private JButton				pbShowForwardHistory= null;

	/** The image rollover toobar button.*/
	private JButton				pbImageRollover   	= null;
	
	/** Holds the history of views opened.*/
	private UIHistory history = new UIHistory();	
	
	/**Indicates whether this menu is draw as a Simple interface or a advance user inteerface.*/
	private boolean bSimpleInterface					= false;	
	
	/**
	 * Create a new instance of UIToolBarMain, with the given properties.
	 * @param oManager the IUIToolBarManager that is managing this toolbar.
	 * @param parent the parent frame for the application.
	 * @param nType the unique identifier for this toolbar.
	 * @param isSimple is the user in simple interface mode. True for yes, false for advanced mode.
	 */
	public UIToolBarMain(IUIToolBarManager oManager, ProjectCompendiumFrame parent, int nType, boolean isSimple) {

		this.oParent = parent;
		this.oManager = oManager;
		this.nType = nType;
		this.bSimpleInterface = isSimple;
		
		tbrToolBar = new UIToolBar(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarMain.mainToolbar")); //$NON-NLS-1$
		tbrToolBar.setOrientation(DEFAULT_ORIENTATION);
		
		createToolBarItems();		
	}
	
	/**
	 * Create a new instance of UIToolBarMain, with the given properties.
	 * @param oManager the IUIToolBarManager that is managing this toolbar.
	 * @param parent the parent frame for the application.
	 * @param nType the unique identifier for this toolbar.
	 * @param orientation the orientation of this toolbars ui object. 
	 * @param isSimple is the user in simple interface mode. True for yes, false for advanced mode.
	 */
	public UIToolBarMain(IUIToolBarManager oManager, ProjectCompendiumFrame parent, int nType, int orientation, boolean isSimple) {

		this.oParent = parent;
		this.oManager = oManager;
		this.nType = nType;
		this.bSimpleInterface = isSimple;
		
		tbrToolBar = new UIToolBar(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarMain.mainToolbar")); //$NON-NLS-1$
		tbrToolBar.setOrientation(orientation);
		
		createToolBarItems();		
	}

	/**
	 * If true, redraw the simple form of this menu, else redraw the complex form.
	 * @param isSimple true for the simple menu, false for the advanced.
	 */
	public void setIsSimple(boolean isSimple) {
		 bSimpleInterface = isSimple;
		 tbrToolBar.removeAll();
		 createToolBarItems();
	}
	
	/**
	 * Creates and return the main toolbar (for example, cut/copy/paste/open/close etc.).
	 * @return UIToolBar, the toolbar with all the main options.
	 */
	private UIToolBar createToolBarItems() {

		if (!bSimpleInterface) {
			pbOpen = tbrToolBar.createToolBarButton(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarMain.open"), UIImages.get(OPEN_ICON)); //$NON-NLS-1$
			pbOpen.addActionListener(this);
			pbOpen.setEnabled(false);
			tbrToolBar.add(pbOpen);
			CSH.setHelpIDString(pbOpen,"toolbars.main"); //$NON-NLS-1$
	
			pbClose = tbrToolBar.createToolBarButton(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarMain.close"), UIImages.get(CLOSE_ICON)); //$NON-NLS-1$
			pbClose.addActionListener(this);
			pbClose.setEnabled(true);
			tbrToolBar.add(pbClose);
			CSH.setHelpIDString(pbClose,"toolbars.main"); //$NON-NLS-1$

			tbrToolBar.addSeparator();
		}
		
		pbCut = tbrToolBar.createToolBarButton(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarMain.cut"), UIImages.get(CUT_ICON)); //$NON-NLS-1$
		pbCut.addActionListener(this);
		pbCut.setEnabled(false);
		tbrToolBar.add(pbCut);
		CSH.setHelpIDString(pbCut,"toolbars.main"); //$NON-NLS-1$

		pbCopy = tbrToolBar.createToolBarButton(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarMain.copy"), UIImages.get(COPY_ICON)); //$NON-NLS-1$
		pbCopy.addActionListener(this);
		pbCopy.setEnabled(false);
		tbrToolBar.add(pbCopy);
		CSH.setHelpIDString(pbCopy,"toolbars.main"); //$NON-NLS-1$

		pbPaste = tbrToolBar.createToolBarButton(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarMain.paste"), UIImages.get(PASTE_ICON)); //$NON-NLS-1$
		pbPaste.addActionListener(this);
		pbPaste.setEnabled(false);
		tbrToolBar.add(pbPaste);
		CSH.setHelpIDString(pbPaste,"toolbars.main"); //$NON-NLS-1$

		pbDelete = tbrToolBar.createToolBarButton(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarMain.delete"), UIImages.get(DELETE_ICON)); //$NON-NLS-1$
		pbDelete.addActionListener(this);
		pbDelete.setEnabled(false);
		tbrToolBar.add(pbDelete);
		CSH.setHelpIDString(pbDelete,"toolbars.main"); //$NON-NLS-1$

		tbrToolBar.addSeparator();

		pbUndo = tbrToolBar.createToolBarButton(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarMain.undo"), UIImages.get(UNDO_ICON)); //$NON-NLS-1$
		pbUndo.addActionListener(this);
		pbUndo.setEnabled(false);
		tbrToolBar.add(pbUndo);
		CSH.setHelpIDString(pbUndo,"toolbars.main"); //$NON-NLS-1$

		pbRedo = tbrToolBar.createToolBarButton(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarMain.redo"), UIImages.get(REDO_ICON)); //$NON-NLS-1$
		pbRedo.addActionListener(this);
		pbRedo.setEnabled(false);
		tbrToolBar.add(pbRedo);
		CSH.setHelpIDString(pbRedo,"toolbars.main"); //$NON-NLS-1$

		tbrToolBar.addSeparator();

		pbShowBackHistory = tbrToolBar.createToolBarButton(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarMain.backTo"), UIImages.get(PREVIOUS_ICON)); //$NON-NLS-1$
		pbShowBackHistory.addActionListener(this);
		pbShowBackHistory.setEnabled(false);
		tbrToolBar.add(pbShowBackHistory);
		CSH.setHelpIDString(pbShowBackHistory,"toolbars.main"); //$NON-NLS-1$

		pbBack = tbrToolBar.createToolBarButton(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarMain.back"), UIImages.get(BACK_ICON)); //$NON-NLS-1$
		pbBack.addActionListener(this);
		pbBack.setEnabled(false);
		tbrToolBar.add(pbBack);
		CSH.setHelpIDString(pbBack,"toolbars.main"); //$NON-NLS-1$

		pbForward = tbrToolBar.createToolBarButton(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarMain.forward"), UIImages.get(FORWARD_ICON)); //$NON-NLS-1$
		pbForward.addActionListener(this);
		pbForward.setEnabled(false);
		tbrToolBar.add(pbForward);
		CSH.setHelpIDString(pbForward,"toolbars.main"); //$NON-NLS-1$

		pbShowForwardHistory = tbrToolBar.createToolBarButton(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarMain.forwardTo"), UIImages.get(NEXT_ICON)); //$NON-NLS-1$
		pbShowForwardHistory.addActionListener(this);
		pbShowForwardHistory.setEnabled(false);
		tbrToolBar.add(pbShowForwardHistory);
		CSH.setHelpIDString(pbShowForwardHistory,"toolbars.main"); //$NON-NLS-1$

		tbrToolBar.addSeparator();

		pbImageRollover = tbrToolBar.createToolBarButton(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarMain.imageRollover"), UIImages.get(IMAGE_ROLLOVER_ICON)); //$NON-NLS-1$
		pbImageRollover.addActionListener(this);
		pbImageRollover.setEnabled(true);
		tbrToolBar.add(pbImageRollover);
		CSH.setHelpIDString(pbImageRollover, "toolbars.main"); //$NON-NLS-1$

		pbSearch = tbrToolBar.createToolBarButton(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarMain.search"), UIImages.get(SEARCH_ICON)); //$NON-NLS-1$
		pbSearch.addActionListener(this);
		pbSearch.setEnabled(true);
		tbrToolBar.add(pbSearch);
		CSH.setHelpIDString(pbSearch,"toolbars.main"); //$NON-NLS-1$

		pbHelp = tbrToolBar.createToolBarButton(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarMain.helpOnItem"), UIImages.get(HELP_ICON)); //$NON-NLS-1$
		if (((UIToolBarManager)oManager).getHelpBroker() != null) {
			pbHelp.addActionListener(new CSH.DisplayHelpAfterTracking(((UIToolBarManager)oManager).getHelpBroker()));
		}

		pbHelp.setEnabled(true);
		tbrToolBar.add(pbHelp);

		return tbrToolBar;
	}

	/**
	 * Update the look and feel of the toolbar.
	 */
	public void updateLAF() {

		if (pbOpen != null ) {
			pbOpen.setIcon(UIImages.get(OPEN_ICON));
		}
		if (pbClose != null) {
			pbClose.setIcon(UIImages.get(CLOSE_ICON));
		}
	    pbCut.setIcon(UIImages.get(CUT_ICON));
	    pbCopy.setIcon(UIImages.get(COPY_ICON));
	    pbPaste.setIcon(UIImages.get(PASTE_ICON));
	    pbDelete.setIcon(UIImages.get(DELETE_ICON));
	    pbUndo.setIcon(UIImages.get(UNDO_ICON));
	    pbRedo.setIcon(UIImages.get(REDO_ICON));
	    pbShowBackHistory.setIcon(UIImages.get(PREVIOUS_ICON));
	    pbBack.setIcon(UIImages.get(BACK_ICON));
	    pbForward.setIcon(UIImages.get(FORWARD_ICON));
	    pbShowForwardHistory.setIcon(UIImages.get(NEXT_ICON));
	    pbSearch.setIcon(UIImages.get(SEARCH_ICON));
	    pbHelp.setIcon(UIImages.get(HELP_ICON));

		if (FormatProperties.imageRollover) {
			pbImageRollover.setIcon(UIImages.get(IMAGE_ROLLOVER_ICON));
		}
		else {
			pbImageRollover.setIcon(UIImages.get(IMAGE_ROLLOVEROFF_ICON));
		}
		
		if (tbrToolBar != null)
			SwingUtilities.updateComponentTreeUI(tbrToolBar);
	}
	
	/**
	 * Handles toolbar action event for this toolbar.
	 *
	 * @param evt the genereated action event to be handled.
	 */
	public void actionPerformed(ActionEvent evt) {

		oParent.setWaitCursor();

		Object source = evt.getSource();

		if (source.equals(pbOpen)) {
			oParent.onFileOpen();
		}
		else if (source.equals(pbClose)) {
			oParent.onFileClose();
		}
		else if (source.equals(pbDelete)) {
			oParent.onEditDelete();
		}
		else if (source.equals(pbCut)) {
			oParent.onEditCut();
		}
		else if (source.equals(pbCopy)) {
			oParent.onEditCopy();
		}
		else if (source.equals(pbPaste)) {
			oParent.onEditPaste();
		}
		else if (source.equals(pbUndo)) {
			oParent.onEditUndo();
		}
		else if (source.equals(pbRedo)) {
			oParent.onEditRedo();
		}
		else if (source.equals(pbBack)) {
			onBack();
		}
		else if (source.equals(pbShowBackHistory)) {
			onShowBackHistory();
		}
		else if (source.equals(pbShowForwardHistory)) {
			onShowForwardHistory();
		}
		else if (source.equals(pbForward)) {
			onForward();
		}
		else if (source.equals(pbSearch)) {
			oParent.onSearch();
		}
		else if (source.equals(pbImageRollover)) {
			oParent.onImageRollover(!FormatProperties.imageRollover);
		}

		oParent.setDefaultCursor();
	}

	/**
	 * Move back one item in the window history, if possible and open the view.
	 */
	private void onBack() {

		View view = history.goBack();
		if (view != null)
			oParent.addViewToDesktop(view, view.getLabel());

		enableHistoryButtons();
	}

	/**
	 * Move forward one item in the window history, if possible, and open the view.
	 */
	private void onForward() {

		View view = history.goForward();
		if (view != null)
			oParent.addViewToDesktop(view, view.getLabel());

		enableHistoryButtons();
	}	
	
	/**
	 * Display the backwards window history in a menu.
	 */
	private void onShowBackHistory() {

		UIScrollableMenu hist = new UIScrollableMenu(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarMain.backwardHistory"), 0); //$NON-NLS-1$

		Vector views = history.getBackHistory();
		int count = views.size();
		if (count == 0)
			return;

		JMenuItem item = null;
		for (int i=0; i<count; i++) {
			View view = (View)views.elementAt(i);
			item = new JMenuItem(view.getLabel());

			final View fview = view;
			final int fi = (count-i)-1;
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					if (history.goToHistoryItem(fi))
					oParent.addViewToDesktop(fview, fview.getLabel());
				}
			});

			hist.add(item);
		}

		JPopupMenu pop = hist.getPopupMenu();
		pop.pack();

		Point loc = pbShowBackHistory.getLocation();
		Dimension size = pbShowBackHistory.getSize();
		Dimension popsize = hist.getPreferredSize();
		Point finalP = SwingUtilities.convertPoint(tbrToolBar.getParent(), loc, oParent.getDesktop());

		int x=0;
		int y=0;
		if (oManager.getLeftToolBarController().containsBar(tbrToolBar)) {
			x = finalP.x+size.width;
			y = finalP.y;
		}
		else if (oManager.getRightToolBarController().containsBar(tbrToolBar)) {
			x = finalP.x - popsize.width;
			y = finalP.y;
		}
		else if (oManager.getTopToolBarController().containsBar(tbrToolBar)) {
			x = finalP.x;
			y = finalP.y+size.width;
		}
		else if (oManager.getBottomToolBarController().containsBar(tbrToolBar)) {
			x = finalP.x;
			y = finalP.y - popsize.height;
		}

		hist.setPopupMenuVisible(true);
		pop.show(oParent.getDesktop(), x, y);
	}

	/**
	 * Display the forwards window history in a menu.
	 */
	private void onShowForwardHistory() {

		UIScrollableMenu hist = new UIScrollableMenu(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarMain.forwardHistory"), 0); //$NON-NLS-1$

		Vector views = history.getForwardHistory();
		int currentIndex = history.getCurrentPosition();

		int count = views.size();
		if (count == 0)
			return;

		JMenuItem item = null;
		for (int i=0; i<count; i++) {
			View view = (View)views.elementAt(i);
			item = new JMenuItem(view.getLabel());

			final View fview = view;
			final int fi = (currentIndex+1)+i;
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					if (history.goToHistoryItem(fi))
						oParent.addViewToDesktop(fview, fview.getLabel());
				}
			});

			hist.add(item);
		}

		JPopupMenu pop = hist.getPopupMenu();
		pop.pack();

		Point loc = pbShowForwardHistory.getLocation();
		Dimension size = pbShowForwardHistory.getSize();
		Dimension popsize = hist.getPreferredSize();
		Point finalP = SwingUtilities.convertPoint(tbrToolBar.getParent(), loc, oParent.getDesktop());

		int x=0;
		int y=0;
		if (oManager.getLeftToolBarController().containsBar(tbrToolBar)) {
			x = finalP.x+size.width;
			y = finalP.y;
		}
		else if (oManager.getRightToolBarController().containsBar(tbrToolBar)) {
			x = finalP.x - popsize.width;
			y = finalP.y;
		}
		else if (oManager.getTopToolBarController().containsBar(tbrToolBar)) {
			x = finalP.x;
			y = finalP.y+size.width;
		}
		else if (oManager.getBottomToolBarController().containsBar(tbrToolBar)) {
			x = finalP.x;
			y = finalP.y - popsize.height;
		}

		hist.setPopupMenuVisible(true);
		pop.show(oParent.getDesktop(), x, y);
	}
	
	/**
	 * Refreshes the undo/redo icons with the last action performed.
	 * @param oUndoManager, the manager to use to check for undo/redo possibilities.
	 */
	public void refreshUndoRedo(UndoManager oUndoManager) {

		//refresh undo
		pbUndo.setToolTipText(oUndoManager.getUndoPresentationName());
		pbUndo.setEnabled(oUndoManager.canUndo());

		// refresh redo
		pbRedo.setToolTipText(oUndoManager.getRedoPresentationName());
		pbRedo.setEnabled(oUndoManager.canRedo());
	}	

	/**
	 * Enalbe all the view history related toolbar icons.
	 */
	public void enableHistoryButtons() {
		pbBack.setEnabled(history.canGoBack());
		pbShowBackHistory.setEnabled(history.canGoBack());
		pbForward.setEnabled(history.canGoForward());
		pbShowForwardHistory.setEnabled(history.canGoForward());
		tbrToolBar.repaint();
	}

	/**
	 * Clear the view history.
	 */
	public void clearHistory() {
		history.clear();
	}

	/**
	 * Try to remove a View object from the history.
	 * @param view com.compendium.core.datamodel.View, the view to add to the history.
	 */
	public void removeFromHistory(View view) {
		history.remove(view);
	}

	/**
	 * Try to add a View object to the history.
	 * @param view com.compendium.core.datamodel.View, the view to add to the history.
	 */
	public void addToHistory(View view) {
		history.add(view);
	}
	
	/**
	 * Enable/disable paste button.
	 * @param enabled, true to enalbe, false to disable.
	 */
	public void setPasteEnabled(boolean enabled) {
		pbPaste.setEnabled(enabled);
	}

	/**
	 * Enable/disable file open button.
	 * @param enabled, true to enalbe, false to disable.
	 */
	public void setFileOpenEnablement(boolean enabled) {
		if (pbOpen != null) {
			pbOpen.setEnabled(enabled);
		}
	}

	/**
	 * Reset the image rollover icon as enabled/disabled.
	 * @param enabled, the status to draw the image rollover icon for.
	 */
	public void updateImageRollover(boolean enabled) {
		if (enabled) {
			pbImageRollover.setIcon(UIImages.get(IMAGE_ROLLOVER_ICON));
		}
		else {
			pbImageRollover.setIcon(UIImages.get(IMAGE_ROLLOVEROFF_ICON));
		}
	}	
	
	/**
	 * Updates the menu when a new database project is opened.
	 */
	public void onDatabaseOpen() {

		if (pbOpen != null)
			pbOpen.setEnabled(false);
		if (pbClose != null)
			pbClose.setEnabled(true);
		if (pbHelp != null)
			pbHelp.setEnabled(true);
		if (pbSearch != null)
			pbSearch.setEnabled(true);
		if (pbImageRollover != null)
			pbImageRollover.setEnabled(true);
	}

	/**
	 * Updates the menu when the current database project is closed.
	 */
	public void onDatabaseClose() {		
		if (pbUndo != null)
			pbUndo.setEnabled(false);
		if (pbRedo != null)
			pbRedo.setEnabled(false);
		if (pbOpen != null)
			pbOpen.setEnabled(true);
		if (pbClose != null)
			pbClose.setEnabled(false);
		if (pbHelp != null)
			pbHelp.setEnabled(false);
		if (pbSearch != null)
			pbSearch.setEnabled(false);
		if (pbImageRollover != null)
			pbImageRollover.setEnabled(false);
		
		history.clear();
	}

	/**
 	 * Enable of items if one or more nodes is selected.
 	 * @param selected, true to enable, false to disable.
	 */
	public void setNodeSelected(boolean selected) {}

	/**
 	 * Set the enabled status of cut cop and delete.
 	 * @param selected, true to enable, false to disable.
	 */
	public void setNodeOrLinkSelected(boolean selected) {

		if (pbCopy != null)
			pbCopy.setEnabled(selected);
		if (pbCut != null)
			pbCut.setEnabled(selected);
		if (pbDelete != null)
			pbDelete.setEnabled(selected);	
	}

	public UIToolBar getToolBar() {
		return tbrToolBar;
	}
	
	/**
	 * Enable/disable the toolbar.
	 * @param enabled true to enable, false to disable.
	 */
	public void setEnabled(boolean enabled) {
		tbrToolBar.setEnabled(enabled);
	}
	
	/**
	 * Return true if this toolbar is active by default, or false if it must be switched on by the user.
	 * @return true if the toolbar is active by default, else false.
	 */
	public boolean getDefaultActiveState() {
		return DEFAULT_STATE;
	}	
	
	/**
	 * Return a unique integer identifier for this toolbar.
	 * @return a unique integer identifier for this toolbar.
	 */
	public int getType() {
		return nType;
	}		
}
