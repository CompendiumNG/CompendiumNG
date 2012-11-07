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
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;

import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;
import com.compendium.core.datamodel.View;

/**
 * The view history bar display a path for the views travel to get to the current view
 *
 * @author Michelle Bachler
 */
public class UIViewHistoryBar extends JPanel {

	/** Default top inset */
	public final static int TOP			= 3;

	/** Default left inset */
	public final static int LEFT		= 5;

	/** Default bottom inset */
	public final static int BOTTOM		= 3;

	/** Default right inset */
	public final static int RIGHT		= 5;

	/** The status label displaying the actual message */
	private	JLabel lblStatus = new JLabel();

	/** Insets for the status bar */
	private	Insets oInsets = new Insets(TOP, LEFT, BOTTOM, RIGHT);

	/** The list of labels currently displayed.*/
	private Vector vtLabels = null;

	/** The layout manager for the mainpanel.*/
	private GridBagLayout gb 	= null;

	/** The layout manager for this panel.*/
	private GridBagLayout maingb 	= null;

	/** Used for autoscrolling when the mouse is hovered over a button.*/
	private 	java.util.Timer 					timer = null;

	private JScrollPane oScrollPane	= null;
	private JPanel oLabelPanel = null;

	private JButton btLeftButton = null;
	private JButton btRightButton = null;

	/**
	 * Constuctor, creates a new view history bar.
	 */
	public UIViewHistoryBar() {
		super();

		setBorder(new SoftBevelBorder(BevelBorder.LOWERED));

		maingb = new GridBagLayout();
		setLayout(maingb);

		oLabelPanel = new JPanel();
		gb = new GridBagLayout();
		oLabelPanel.setLayout(gb);

		btLeftButton = new JButton(UIImages.get(IUIConstants.PREVIOUS_ICON));
		btLeftButton.setRequestFocusEnabled(false);
		btLeftButton.setToolTipText(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIViewHistoryBar.scrollLeft")); //$NON-NLS-1$
		btLeftButton.setMargin(new Insets(0,0,0,0));
		btLeftButton.setBorder(null);
		btLeftButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				JViewport port = oScrollPane.getViewport();
				Point pos = port.getViewPosition();
				if (pos.x > 100) {
					port.setViewPosition(new Point(pos.x-100, pos.y));
				}
				else {
					port.setViewPosition(new Point(0, pos.y));
				}
			}
		});

		/*btLeftButton.addMouseListener(new MouseAdapter() {
			public void mousePressed() {
				timer = new java.util.Timer();
				ScrollBarLeft task = new ScrollBarLeft();
				timer.schedule(task, new Date(), 10);
			}

			public void mouseReleased() {
				if (timer != null) {
					timer.cancel();
					timer = null;
				}
			}
		});*/

		GridBagConstraints gc = new GridBagConstraints();
		gc.fill = GridBagConstraints.NONE;
		gc.insets = new Insets(0,0,0,0);
		gc.gridx = 0;
		gc.weightx = 1;
		gc.gridwidth=1;
		gc.anchor = GridBagConstraints.WEST;
		maingb.setConstraints(btLeftButton, gc);
		add(btLeftButton);

		oScrollPane = new JScrollPane(oLabelPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		gc = new GridBagConstraints();
		gc.fill = GridBagConstraints.BOTH;
		gc.insets = new Insets(0,0,0,0);
		gc.gridx = 1;
		gc.weightx = 1000;
		gc.gridwidth=1;
		gc.anchor = GridBagConstraints.WEST;
		maingb.setConstraints(oScrollPane, gc);
		add(oScrollPane);

		btRightButton = new JButton(UIImages.get(IUIConstants.NEXT_ICON));
		btRightButton.setRequestFocusEnabled(false);
		btRightButton.setToolTipText(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIViewHistoryBar.scrollRight")); //$NON-NLS-1$
		btRightButton.setMargin(new Insets(0,0,0,0));
		btRightButton.setBorder(null);
		btRightButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				JViewport port = oScrollPane.getViewport();
				int x = oLabelPanel.getX();
				Point pos = port.getViewPosition();
				port.setViewPosition(new Point(pos.x+100, pos.y));

				oLabelPanel.validate();

				oLabelPanel.repaint();
				oScrollPane.validate();
				oScrollPane.repaint();
			}
		});
		/*btRightButton.addMouseListener(new MouseAdapter() {
			public void mousePressed() {
				timer = new java.util.Timer();
				ScrollBarRight task = new ScrollBarRight();
				timer.schedule(task, new Date(), 10);
			}

			public void mouseReleased() {
				if (timer != null) {
					timer.cancel();
					timer = null;
				}
			}
		});*/

		gc = new GridBagConstraints();
		gc.fill = GridBagConstraints.NONE;
		gc.insets = new Insets(0,0,0,0);
		gc.gridx = 2;
		gc.weightx = 1;
		gc.gridwidth=1;
		gc.anchor = GridBagConstraints.EAST;
		maingb.setConstraints(btRightButton, gc);
		add(btRightButton);

		JLabel label = new JLabel(" "); //$NON-NLS-1$
		vtLabels = new Vector(1);
		vtLabels.addElement(label);
		addLabel(label);
	}

	/**
	 * This inner class is used to perform the autoscrolling of the bar to the left.
	 */
	private class ScrollBarLeft extends TimerTask {

		public void run() {
			//JViewport oViewPort = oViewPane.getViewFrame().getViewport();
			//Point parentPos = SwingUtilities.convertPoint((Component)oViewPane, oStartingBounds.x, oStartingBounds.y, oViewPort);

			JViewport port = oScrollPane.getViewport();
			Point pos = port.getViewPosition();
			if (pos.x > 100) {
				port.setViewPosition(new Point(pos.x-100, pos.y));
			}
			else {
				port.setViewPosition(new Point(0, pos.y));
				if (timer != null) {					
					timer.cancel();
				}
			}
		}
	}

	/**
	 * This inner class is used to perform the autoscrolling of the bar to the right.
	 */
	private class ScrollBarRight extends TimerTask {

		public void run() {

			System.out.println(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIViewHistoryBar.aboutToMove")); //$NON-NLS-1$
			JViewport port = oScrollPane.getViewport();
			//int x = oLabelPanel.getX();
			Point pos = port.getViewPosition();
			port.setViewPosition(new Point(pos.x+100, pos.y));
			oScrollPane.validate();
			oScrollPane.repaint();
		}
	}

	/**
	 * Add the given label to this panel.
	 *
	 * @param label the labelto add.
	 */
	private void addLabel(JLabel label) {

		GridBagConstraints gc = new GridBagConstraints();
		gc.fill = GridBagConstraints.NONE;
		gc.insets = new Insets(0,2,0,2);
		gc.gridy = 0;
		gc.weightx = 1;
		gc.gridwidth=1;
		gc.anchor = GridBagConstraints.WEST;
		gb.setConstraints(label, gc);
		oLabelPanel.add(label);
		vtLabels.addElement(label);
	}

	/**
	 * Add the given label to this panel.
	 *
	 * @param label the labelto add.
	 */
	private void addPadding() {

		JLabel label = new JLabel(" "); //$NON-NLS-1$

		GridBagConstraints gc = new GridBagConstraints();
		gc.fill = GridBagConstraints.BOTH;
		gc.insets = new Insets(0,0,0,0);
		gc.weightx = 100;
		gc.gridy = 0;
		gc.gridwidth=GridBagConstraints.REMAINDER;
		gc.anchor = GridBagConstraints.WEST;
		gb.setConstraints(label, gc);
		oLabelPanel.add(label);
		vtLabels.addElement(label);

		//JViewport port = oScrollPane.getViewport();
		//Point pos = port.getViewPosition();
		//System.out.println("label.getBounds="+label.getBounds());

		//Point actual = SwingUtilities.convertPoint(oLabelPanel, label.getX(), label.getY(), port);

		//port.setViewPosition(new Point(actual.x, pos.y));
		//port.scrollRectToVisible(label.getBounds());
	}

	/**
	 * Sets the view history for the current view.
	 *
	 * @param vtHistory the list of views to build the bar from.
	 */
	public synchronized void setViewHistory(Vector svtHistory) {

		final Vector vtHistory = svtHistory;

		oLabelPanel.removeAll();

		int count = vtHistory.size();
		if (count == 0) {
			NavigationLabel label = new NavigationLabel(" "); //$NON-NLS-1$
			vtLabels = new Vector(1);
			vtLabels.addElement(label);
			addLabel(label);
		}
		else {
			vtLabels = new Vector( count*2 );

			String loopLabel = ""; //$NON-NLS-1$
			UIViewFrame loopFrame = null;
			View loopView = null;
			NavigationLabel label = null;
			NavigationLabel label2 = null;

			for (int i=0; i<count; i++) {

				Object obj = vtHistory.elementAt(i);
				if (obj instanceof UIViewFrame) {

					loopFrame = (UIViewFrame)obj;
					loopView  = loopFrame.getView();
					loopLabel = loopView.getLabel();
					if (loopLabel.length() > 40) {
						loopLabel = loopLabel.substring(0, 40)+"..."; //$NON-NLS-1$
					}
					loopLabel = loopLabel;

					label = new NavigationLabel(loopLabel);
					label.setForeground(Color.blue);
					final View fLoopView = loopView;
					label.addMouseListener(new MouseAdapter() {
						public void mouseClicked(MouseEvent e) {
							if (ProjectCompendium.APP != null) {
								ProjectCompendium.APP.addViewToDesktop(fLoopView, fLoopView.getLabel());
							}
						}
					});

					addLabel(label);

					label2 = new NavigationLabel("|"); //$NON-NLS-1$
					addLabel(label2);
				}
				else {
					label = new NavigationLabel((String)obj+"|"); //$NON-NLS-1$
					vtLabels.add(label);
					addLabel(label);
				}
			}
			addPadding();
		}

		validate();
		repaint();
	}

	/**
	 * Returns the insets of the status bar.
	 *
	 * @return Insets, the insets of the status bar.
	 */
	public Insets getInsets() {

		if (oInsets == null)
			oInsets = new Insets(TOP,LEFT,BOTTOM,RIGHT);
		return oInsets;
	}

	/**
	 * This class extends JLabel to produce a label with specific insets
	 *
	 * @return Insets, the insets of the label.
	 */
	private class NavigationLabel extends JLabel {

		public NavigationLabel() {
			super();
		}

		public NavigationLabel(String text) {
			super(text);
		}

		/**
		 * Returns the insets of the status bar.
		 *
		 * @return Insets, the insets of the label.
		 */
		public Insets getInsets() {
			return new Insets(0,2,0,2);
		}

	}
}
