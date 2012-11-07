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

package com.compendium.ui.movie;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import javax.media.Player;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;
import com.compendium.core.datamodel.NodePosition;
import com.compendium.core.datamodel.NodePositionTime;
import com.compendium.core.datamodel.NodeSummary;
import com.compendium.core.datamodel.TimeMapView;
import com.compendium.ui.UIButton;
import com.compendium.ui.UIButtonPanel;
import com.compendium.ui.UIImageButton;
import com.compendium.ui.UIImages;
import com.compendium.ui.UINode;
import com.compendium.ui.UIUtilities;
import com.compendium.ui.dialogs.UINodeContentDialog;
import com.compendium.ui.panels.UITimeMilliSecondPanel;

/**
 * This panel manages node time periods.
 *
 * @author	Michelle Bachler
 */
public class UINodeTimeViewPanel extends JPanel implements PropertyChangeListener {

	/** The background colour when the calling timespan is known**/
	private static final Color	SELECTED_BACKGROUND_COLOUR = new Color(255,255,128);	

	/** The background colour of a time span property set**/
	private static final Color	BACKGROUND_COLOUR = new Color(233, 234, 253);	

	/** The parent dialog that this panel is in.*/
	private UINodeContentDialog oParentDialog	= null;

	/** The current node this is the contents for - if in a map.*/
	private UINode			oUINode			= null;

	/** The current node data this is the contents for.*/
	private NodeSummary		oNode			= null;

	private TimeMapView 	oTimeMapView 	= null;
	
	private Vector			vtTimes 		= new Vector();
	
	/** The main panel for all the rows of times.*/
	private JPanel  		mainPanel		= null;
	
	private boolean			localChange		= true;
		
	private GridBagLayout			layout		= null;
	private GridBagConstraints		cons		= null;
	
	private NodePositionTime		oCallingSpan = null;
	
	/** The button to close the parent dialog.*/
	private UIButton		pbClose				= null;	
	
	/** The panel to scroll to if span known.*/
	private JPanel			oScrollToPanel = null;
	
	/**
	 * Constructor.
	 * @param parent the parent frame for the dialog this panel is in.
	 * @param uinode the current node this is the contents for - if in a map.
 	 * @param tabbedPane the parent dialog this panel is in.
 	 * @param span the originating span that called this popup (so can highlight it)
	 */
	public UINodeTimeViewPanel(JFrame parent, UINode uinode, TimeMapView oView, UINodeContentDialog tabbedPane, NodePositionTime span) {
		super();
		oParentDialog = tabbedPane;
		oUINode = uinode;
		NodePosition pos = uinode.getNodePosition();
		pos.addPropertyChangeListener(this);

		oNode = oUINode.getNode();
		oTimeMapView = oView;
		this.oTimeMapView.addPropertyChangeListener(this); 
		this.oCallingSpan = span;
		
		init();
	}
	
	/**
	 * Constructor.
	 * @param parent, the parent frame for the dialog this panel is in.
	 * @param uinode com.compendium.ui.UINode, the current node this is the contents for - if in a map.
 	 * @param tabbedPane, the parent dialog this panel is in.
	 */
	public UINodeTimeViewPanel(JFrame parent, UINode uinode, TimeMapView oView, UINodeContentDialog tabbedPane) {
		super();
		oParentDialog = tabbedPane;
		oUINode = uinode;
		NodePosition pos = uinode.getNodePosition();
		pos.addPropertyChangeListener(this);
		
		oNode = oUINode.getNode();
		oTimeMapView = oView;
		this.oTimeMapView.addPropertyChangeListener(this); 
		
		init();
	}
	
	private void init() {
		mainPanel = new JPanel();
		setLayout(new BorderLayout());		
		JScrollPane scroll = new JScrollPane(mainPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				 JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);		
		add(scroll, BorderLayout.CENTER);
		add(createButtonPanel(), BorderLayout.SOUTH);
		
		this.addComponentListener(new ComponentAdapter() {
			public void componentShown(ComponentEvent e) {
				if (oScrollToPanel != null) {
					oScrollToPanel.scrollRectToVisible(oScrollToPanel.getBounds());
				}
			}
		});
		
		refreshTimes();		
				
		setSize(mainPanel.getPreferredSize().width+30, oParentDialog.getPreferredSize().height);
		setPreferredSize(new Dimension(mainPanel.getPreferredSize().width+30, oParentDialog.getPreferredSize().height));		
	}
	
    //*********************** PROPERTY CHANGE LISTENER *************************/

	/**
	 * Handles property change events for the TimeMapView else calls super.
	 * @param evt, the associated PropertyChangeEvent object.
	 */
	public void propertyChange(PropertyChangeEvent evt) {

		//System.out.println("property change="+evt.getPropertyName());
		
	    String prop = evt.getPropertyName();
		Object source = evt.getSource();
	    //Object oldvalue = evt.getOldValue();
	    Object newvalue = evt.getNewValue();
	    
	    if (source instanceof TimeMapView) {
	    	if (newvalue instanceof NodePositionTime) {
		    	NodePositionTime newtime = (NodePositionTime)newvalue;
		    	if (newtime.getNode().getId() == oNode.getId()) {	    	
			    	if (prop.equals(TimeMapView.TIME_ADDED_PROPERTY)) {
			    		vtTimes.add(newvalue);	
			    		refreshTimes();
			    	} else if (prop.equals(TimeMapView.TIME_CHANGED_PROPERTY)) {
			    		int count = vtTimes.size();
			    		for (int i=0; i< count; i++) {
				    		NodePositionTime nextTime = (NodePositionTime)vtTimes.elementAt(i);
				    		if (nextTime.getId().equals(newtime.getId())) {
				    			vtTimes.remove(nextTime);
				    			vtTimes.add(newvalue);
				    			break;
				    		}	    			
				    	} 
			    		if (!localChange) {
			    			refreshTimes();
			    		} else {
			    			localChange = false;
			    		}
			    	} 
		    	}
	    	}
	    
	    	if (prop.equals(TimeMapView.TIME_REMOVED_PROPERTY)) {
	    		String id = (String)newvalue;
	    		int count = vtTimes.size();
	    		for (int i=0; i< count; i++) {
	    			NodePositionTime nextTime = (NodePositionTime)vtTimes.elementAt(i);
	    			if (nextTime.getId().equals(id)) {
	    				vtTimes.remove(nextTime);
	    				break;
	    			}	    			
	    		}
	    		refreshTimes();
	    	}
	    		    	
	    } else if (source instanceof NodePosition) {	    
	    	// if the user moves the node then on mouse release, update the relevant time span
	    	if (prop.equals(NodePosition.POSITION_PROPERTY)) {
	    		refreshTimes();
	    	}
	    }
	}	
	
	public void focusNode() {
		Hashtable times = oTimeMapView.getTimesForNode(oNode.getId());
		vtTimes = new Vector(times.size());
		for (Enumeration e = times.elements(); e.hasMoreElements();) {
			NodePositionTime nextTime = (NodePositionTime)e.nextElement();
			vtTimes.add(nextTime);
		}
		
	}
	
	public synchronized void refreshTimes() {
		mainPanel.removeAll();
		layout = null;
		oScrollToPanel = null;

        layout = new GridBagLayout();
		mainPanel.setLayout(layout);
        cons = new GridBagConstraints();
        cons.fill = GridBagConstraints.HORIZONTAL;
        cons.anchor = GridBagConstraints.NORTHWEST;
        cons.gridwidth = GridBagConstraints.REMAINDER;
        cons.weightx = 2;
        cons.ipady = 4;
		
		Hashtable times = oTimeMapView.getTimesForNode(oNode.getId());
		vtTimes = new Vector(times.size());
		for (Enumeration e = times.elements(); e.hasMoreElements();) {
			NodePositionTime nextTime = (NodePositionTime)e.nextElement();
			vtTimes.add(nextTime);
		}
		
		Object[] sa = new Object[vtTimes.size()];
		vtTimes.copyInto(sa);
		List l = Arrays.asList(sa);
		
		Collections.sort(l, new Comparator() {
			public int compare(Object o1, Object o2) {

				NodePositionTime data1 = (NodePositionTime)o1;
				NodePositionTime data2 = (NodePositionTime)o2;
				
				long s1 = data1.getTimeToShow();
				long s2 = data2.getTimeToShow();

				return  (new Double(s1).compareTo(new Double(s2)));
			}
		});		
		
		vtTimes.removeAllElements();
		vtTimes.addAll(l);
						
		int count = vtTimes.size();
		for (int i=0; i<count; i++) {
			final NodePositionTime nextTime = (NodePositionTime)vtTimes.elementAt(i);			
	        JPanel next = createRow(nextTime);
	        layout.setConstraints(next, cons);		        			
			mainPanel.add(next);
		}

		JButton button = new JButton(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UINodeTimeViewPanel.newSpan"));	 //$NON-NLS-1$
		button.setToolTipText(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UINodeTimeViewPanel.newSpanTip")); //$NON-NLS-1$
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					int count = vtTimes.size();
					long biggestTime = 0;
					for (int i=0; i<count; i++) {
						final NodePositionTime nextTime = (NodePositionTime)vtTimes.elementAt(i);
						if (nextTime.getTimeToHide() > biggestTime) {
							biggestTime = nextTime.getTimeToHide();
						}
					}
					
					Point p = oUINode.getLocation();
					UITimeLinesController controller = ((UIMovieMapViewFrame)oUINode.getViewPane().getViewFrame()).getController();
			    	long milliDefaultSpanLength = controller.getDefaultNodeTimeSpanLength();
			    	oTimeMapView.addNodeTime(oNode.getId(), biggestTime+1000, biggestTime+milliDefaultSpanLength, p.x, p.y);					
				} catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		});		
		
       	cons.weighty = 2;
        cons.fill = GridBagConstraints.NONE;
        cons.insets = new Insets(0,5,0,0);

        layout.setConstraints(button, cons);		        			
		mainPanel.add(button);		
		//mainPanel.add(Box.createRigidArea(new Dimension(20, 400)));		
		
		mainPanel.validate();
		repaint();
	}
	
	private JPanel createRow(NodePositionTime time) {
		final NodePositionTime nextTime = time;
		final NodeSummary oNode = nextTime.getNode();
		//final String fsViewTimeNodeID = nextTime.getId(); 
		final JLabel durationValue = new JLabel(""); //$NON-NLS-1$
		JPanel durationPanel = new JPanel();

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBorder(new EmptyBorder(5,5,5,5));

		JPanel timePanel = new JPanel();
		timePanel.setBorder(new LineBorder(Color.darkGray, 1));
		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gc = new GridBagConstraints();
		timePanel.setLayout(gb);
		gc.insets = new Insets(3,3,3,3);
		gc.anchor = GridBagConstraints.WEST;
		gc.weightx=1;
		gc.weighty=1;

		mainPanel.add(timePanel, BorderLayout.CENTER);
		
		final UITimeMilliSecondPanel timePanelIn = new UITimeMilliSecondPanel(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UINodeTimeViewPanel.ShowAt")+": ", false, true); //$NON-NLS-1$
		final UITimeMilliSecondPanel timePanelOut = new UITimeMilliSecondPanel(" "+LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UINodeTimeViewPanel.hideAt")+": ", false, true); //$NON-NLS-1$
		JPanel positionPanel = new JPanel();

		if (oCallingSpan != null 
				&& time.getId().equals(oCallingSpan.getId()) 
					&& vtTimes.size() > 1) {
			timePanel.setBackground(SELECTED_BACKGROUND_COLOUR);
			timePanelIn.setBackground(SELECTED_BACKGROUND_COLOUR);
			timePanelOut.setBackground(SELECTED_BACKGROUND_COLOUR);
			positionPanel.setBackground(SELECTED_BACKGROUND_COLOUR);
			durationPanel.setBackground(SELECTED_BACKGROUND_COLOUR);
			oScrollToPanel = mainPanel;
		} else {
			timePanel.setBackground(BACKGROUND_COLOUR);
			timePanelIn.setBackground(BACKGROUND_COLOUR);
			timePanelOut.setBackground(BACKGROUND_COLOUR);
			durationPanel.setBackground(BACKGROUND_COLOUR);
			positionPanel.setBackground(BACKGROUND_COLOUR);
		}
		
		//timePanelIn.setBorder(new LineBorder(Color.black, 1));
		timePanelIn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				long show = timePanelIn.getMilliSeconds();
				if (nextTime.getTimeToShow() != show) {
					boolean overlap = checkForOverlap(nextTime.getId(), show, nextTime.getTimeToHide());					
					if (overlap) {
						ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UINodeTimeViewPanel.errorOverlappingTime")); //$NON-NLS-1$
						timePanelIn.setSeconds(nextTime.getTimeToShow());
					} else if (show >= nextTime.getTimeToHide()) {
						ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UINodeTimeViewPanel.errorStartTime")); //$NON-NLS-1$
						timePanelIn.setSeconds(nextTime.getTimeToShow());
					} else {											
						nextTime.setTimeToShow(show);
						localChange = true;
						try {
							NodePositionTime pos = oTimeMapView.updateNodeTime(nextTime.getId(),
								oNode.getId(), 
								nextTime.getTimeToShow(),
								nextTime.getTimeToHide(),
								nextTime.getXPos(),
								nextTime.getYPos());
							
							durationValue.setText(formatDuration(nextTime.getTimeToHide()-nextTime.getTimeToShow()));
						} catch (Exception ex) {
							System.out.println("error:"+ex.getLocalizedMessage()); //$NON-NLS-1$
							ex.printStackTrace();
						}
					}
				}
			}
		});
		
		//timePanelOut.setBorder(new LineBorder(Color.black, 1));
		timePanelOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				long hide = timePanelOut.getMilliSeconds();
				if (nextTime.getTimeToHide() != hide) {
					boolean overlap = checkForOverlap(nextTime.getId(), nextTime.getTimeToShow(), hide);					
					if (overlap) {
						ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UINodeTimeViewPanel.errorOverLappingTime")); //$NON-NLS-1$
						timePanelOut.setSeconds(nextTime.getTimeToHide());
					} else if (hide <= nextTime.getTimeToShow()) {
						ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UINodeTimeViewPanel.errorStopTime")); //$NON-NLS-1$
						timePanelOut.setSeconds(nextTime.getTimeToHide());
					} else {
						nextTime.setTimeToHide(hide);				
						localChange = true;
						try {
							NodePositionTime pos = oTimeMapView.updateNodeTime(nextTime.getId(),
								oNode.getId(), 
								nextTime.getTimeToShow(),
								nextTime.getTimeToHide(),
								nextTime.getXPos(),
								nextTime.getYPos());

							durationValue.setText(formatDuration(nextTime.getTimeToHide()-nextTime.getTimeToShow()));
						} catch (Exception ex) {
							System.out.println(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UINodeTimeViewPanel.error")+ex.getLocalizedMessage()); //$NON-NLS-1$
							ex.printStackTrace();
						}
					}
				}
			}
		});
		
		timePanelIn.setMilliSeconds(nextTime.getTimeToShow());
		timePanelOut.setMilliSeconds(nextTime.getTimeToHide());
		
		//timePanel.add(timePanelIn);
		//timePanel.add(timePanelOut);
		
		int x = nextTime.getXPos();
		int y = nextTime.getYPos();
		final JLabel label = new JLabel("x:"+x+"  y:"+y); //$NON-NLS-1$ //$NON-NLS-2$

		JButton button  = new JButton(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UINodeTimeViewPanel.setButton")); //$NON-NLS-1$
		button.setToolTipText(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UINodeTimeViewPanel.setButtonTip")); //$NON-NLS-1$
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Point p = oUINode.getLocation();
				try {
					oTimeMapView.updateNodeTime(nextTime.getId(), nextTime.getNode().getId(), nextTime.getTimeToShow(), nextTime.getTimeToHide(), p.x, p.y);					
				} catch(Exception ex) {
					ex.printStackTrace();
				}
			}				
		});
		
		UIImageButton delete = new UIImageButton(UIImages.get(UIImages.DELETE_ICON));		
		delete.setToolTipText(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UINodeTimeViewPanel.deleteButtonTip")); //$NON-NLS-1$
		delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					oTimeMapView.deleteNodeTime(nextTime.getId(), oNode.getId());
				} catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		});			
		
		//positionPanel.setBorder(new LineBorder(Color.black, 1));
		positionPanel.add(label);
		positionPanel.add(button);
		positionPanel.setPreferredSize(new Dimension(positionPanel.getPreferredSize().width + 6, timePanelOut.getPreferredSize().height));

		JLabel durationLabel = new JLabel(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UINodeTimeViewPanel.visibleDuration")+": "); //$NON-NLS-1$
		durationValue.setText(formatDuration(nextTime.getTimeToHide()-nextTime.getTimeToShow()));

		durationPanel.add(durationLabel);
		durationPanel.add(durationValue);
		
		
		gc.gridy = 0;
		gc.gridx = 0;
		gc.gridwidth = 1;
		gb.setConstraints(timePanelIn, gc);
		timePanel.add(timePanelIn);

		gc.gridy = 0;
		gc.gridx = 1;
		gc.gridwidth = 1;
		gb.setConstraints(positionPanel, gc);
		timePanel.add(positionPanel);

		gc.gridy = 0;
		gc.gridx = 2;
		gc.gridwidth = GridBagConstraints.REMAINDER;
		gb.setConstraints(delete, gc);
		timePanel.add(delete);
		
		gc.gridy = 1;
		gc.gridx = 0;
		gc.gridwidth = 1;
		gb.setConstraints(timePanelOut, gc);
		timePanel.add(timePanelOut);		

		gc.gridy = 1;
		gc.gridx = 1;
		gc.gridwidth = 2;
		gb.setConstraints(durationPanel, gc);
		timePanel.add(durationPanel);

		//timePanel.add(positionPanel);	
		////timePanel.add(button);
		//timePanel.add(delete);
		return mainPanel;
	}
		
	/**
	 * Take the given time in milliseconds and return a string representing the time.
	 * @param duration
	 * @return
	 */
	private String formatDuration(long duration) {
		String format = ""; //$NON-NLS-1$
		
		long minutes = 0;
		long hours = 0;
    	long seconds = duration/1000;
    	long milliseconds = duration%1000;
    	
		if (seconds > 59) {
			minutes = seconds/60;
			seconds = seconds%60;
			if (minutes > 59) {
				hours = minutes/60;
				minutes = minutes%60;
			}
		}
		long hours10 = hours / 10;
		hours = hours % 10;
		long minutes10 = minutes / 10;
		minutes = minutes % 10;
		long seconds10 = seconds / 10;
		seconds = seconds % 10;

        String milli = String.valueOf(milliseconds);
        if (milli.length() == 3) {
        	milli = "0"+milli; //$NON-NLS-1$
        } else if (milli.length() == 2) {
        	milli = "00"+milli;        	 //$NON-NLS-1$
        } else if (milli.length() == 1) {
        	milli = "000"+milli; //$NON-NLS-1$
        }
		
		format = new String ( "" + hours10 + hours + ":" + minutes10 + minutes + ":" + seconds10 + seconds + "." + milli ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		
		return format;
	}
	
	/**
	 * Check to see if the passed time is inside another span or another span is inside it
	 * In which case there is a overlap.
	 * @param newTime the time to check
	 * @return true if there is an overlap with another time span, else false;
	 */
	private boolean checkForOverlap(String id, long start, long stop) {
		int count = vtTimes.size();
		boolean overlap = false;		
		for (int i=0; i<count; i++) {
			NodePositionTime time = (NodePositionTime)vtTimes.elementAt(i);
			if (!id.equals(time.getId())) {				
				overlap = time.checkForOverlap(start, stop);
				if (overlap) break;
			}
		}	
		
		return overlap;
	}
	
	/**
	 * Create and return the button panel.
	 */
	private UIButtonPanel createButtonPanel() {

		UIButtonPanel oButtonPanel = new UIButtonPanel();

		pbClose = new UIButton(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UINodeTimeViewPanel.closeButton")); //$NON-NLS-1$
		pbClose.setMnemonic(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UINodeTimeViewPanel.closeButtonMnemonic").charAt(0));
		pbClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				oParentDialog.onCancel();
			}
		});
		oButtonPanel.addButton(pbClose);

		UIButton pbHelp = new UIButton(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UINodeTimeViewPanel.helpButton")); //$NON-NLS-1$
		pbHelp.setMnemonic(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UINodeTimeViewPanel.helpButtonMnemonic").charAt(0));
		ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "node.times", ProjectCompendium.APP.mainHS); //$NON-NLS-1$
		oButtonPanel.addHelpButton(pbHelp);

		return oButtonPanel;
	}
	
	/**
	 * Set the default button for the parent dialog to be this panel's default button.
	 */
	public void setDefaultButton() {
		oParentDialog.getRootPane().setDefaultButton(pbClose);
	}	
	
	/**
	 * Save the time data.
	 */
	public void onUpdate() {
		int count = vtTimes.size();
		for (int i=0; i<count; i++) {
			NodePositionTime nextTime = (NodePositionTime)vtTimes.elementAt(i);
			try {
				NodePositionTime pos = oTimeMapView.updateNodeTime(nextTime.getId(),
					oNode.getId(), 
					nextTime.getTimeToShow(),
					nextTime.getTimeToHide(),
					nextTime.getXPos(),
					nextTime.getYPos());
			} catch (Exception ex) {
				System.out.println("error "+": "+ex.getLocalizedMessage()); //$NON-NLS-1$
				ex.printStackTrace();
			}						
		}
	}
}
