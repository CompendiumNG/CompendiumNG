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
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.JOptionPane;

import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;
import com.compendium.core.CoreUtilities;
import com.compendium.core.datamodel.LinkedFileDatabase;
import com.compendium.core.datamodel.Movie;
import com.compendium.core.datamodel.MovieMapView;
import com.compendium.core.datamodel.MovieProperties;
import com.compendium.core.datamodel.NodeSummary;
import com.compendium.ui.IUIConstants;
import com.compendium.ui.UIButton;
import com.compendium.ui.UIButtonPanel;
import com.compendium.ui.UIImageButton;
import com.compendium.ui.UIImages;
import com.compendium.ui.UIUtilities;
import com.compendium.ui.dialogs.UINodeContentDialog;
import com.compendium.ui.panels.UITimeMilliSecondPanel;

/**
 * This panel manages movies for a movie map view.
 *
 * @author	Michelle Bachler
 */
public class UIMovieViewPanel extends JPanel implements PropertyChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** The background colour when the calling movie property is known**/
	private static final Color	SELECTED_BACKGROUND_COLOUR = new Color(255,255,128);	

	/** The background colour of a movie property set**/
	private static final Color	BACKGROUND_COLOUR = new Color(233, 234, 253);	

	/** Forms the first part of a new key for an item not saved to the database yet.*/
	private final static String		ID_STUB = "newentry"; //$NON-NLS-1$

	/** The last director browsed to when looking for movies.*/
	private static String lastFileDialogDir = ""; //$NON-NLS-1$
	
	/** The parent dialog that this panel is in.*/
	private UINodeContentDialog oParentDialog	= null;
	
	/** The current node data this is the contents for.*/
	private NodeSummary		oNode			= null;
	
	/** The movieMapView associated with this movie view panel.*/
	private MovieMapView 	oMovieMapView 	= null;
	
	/** Holds local copies of the Movie objects for this view*/
	private Vector<Movie>	vtLocalMovies 	= new Vector<Movie>();

	/** The main panel for all the rows of times.*/
	private JPanel  		mainPanel		= null;
	
	/** The button to copy the movie file to the database and back.*/
	private UIButton		pbDatabase			= null;

	/** The add Movie button, added here as used to scroll to **/
	private JButton 		oMovieButton	= null;
	
	/** the main scroll pane **/
	private JScrollPane 	scroll			= null;
	
	/** The layout manager used by this panel.*/
	private GridBagLayout			layout		= null;
	
	/**The layout constraints used by this panel.*/
	private GridBagConstraints		cons		= null;
	
	/** The button to close the parent dialog.*/
	private UIButton		pbCancel				= null;
		
	/** The MovieProperties that called this dialog - possibly null if not called from a properties element.*/
	private MovieProperties oProperties = null;

	/** The Movie that called this dialog - possibly null if not called from a movie element.*/
	private Movie oMovie = null;
	
	private String newlyAddedID = ""; //$NON-NLS-1$

	/**
	 * Constructor.
	 * @param parent the parent frame for the dialog this panel is in.
	 * @param pane the current movie map view pane for this node.
	 * @param node the current node to list the movies for.
 	 * @param tabbedPane the parent dialog this panel is in.
 	 * @param props the MovieProperties that called this dialog - so they can be highlighted.
	 */
	public UIMovieViewPanel(JFrame parent, NodeSummary node, UINodeContentDialog tabbedPane, MovieProperties props) {
		super();
		oParentDialog = tabbedPane;
		oNode = node;
		oProperties = props;
		oMovieMapView = (MovieMapView)oNode;
		this.oMovieMapView.addPropertyChangeListener(this); 
		
		init();
	}

	
	/**
	 * Constructor.
	 * @param parent the parent frame for the dialog this panel is in.
	 * @param pane the current movie map view pane for this node.
	 * @param node the current node to list the movies for.
 	 * @param tabbedPane the parent dialog this panel is in.
 	 * @param movie the Movie that called this dialog - so they can be highlighted.
	 */
	public UIMovieViewPanel(JFrame parent, NodeSummary node, UINodeContentDialog tabbedPane, Movie movie) {
		super();
		oParentDialog = tabbedPane;
		oNode = node;
		oMovie = movie;
		oMovieMapView = (MovieMapView)oNode;
		this.oMovieMapView.addPropertyChangeListener(this); 
		
		init();
	}

	/**
	 * Draws this panel and initialise variables.
	 */
	private void init() {
		mainPanel = new JPanel();
		
		setLayout(new BorderLayout());
		
		scroll = new JScrollPane(mainPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				 JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		this.addComponentListener(new ComponentAdapter() {
			public void componentShown(ComponentEvent e) {				
			    if (oMovieButton != null) {
					mainPanel.scrollRectToVisible(oMovieButton.getBounds());
					mainPanel.repaint();
			    }		
			}
		});
		
		Enumeration movies = oMovieMapView.getMovies();
		
        if (movies.hasMoreElements()) {
			for (Enumeration eMovies = movies; eMovies.hasMoreElements();) {
				Movie next = (Movie)eMovies.nextElement();			
				vtLocalMovies.addElement(next);
			}
        }
		
		Object[] sa = new Object[vtLocalMovies.size()];
		vtLocalMovies.copyInto(sa);
		List l = Arrays.asList(sa);
		
		Collections.sort(l, new Comparator() {
			public int compare(Object o1, Object o2) {

				Movie data1 = (Movie)o1;
				Movie data2 = (Movie)o2;
				
				Date s1 = data1.getCreationDate();
				Date s2 = data2.getCreationDate();

				return  (s1).compareTo(s2);
			}
		});		
		
		vtLocalMovies.removeAllElements();
		vtLocalMovies.addAll(l);

		refreshMovies();
		
		setSize(mainPanel.getPreferredSize().width+30, oParentDialog.getPreferredSize().height);
		setPreferredSize(new Dimension(mainPanel.getPreferredSize().width+30, oParentDialog.getPreferredSize().height));

		add(scroll, BorderLayout.CENTER);
		add(createButtonPanel(), BorderLayout.SOUTH);
	}
	
    //*********************** PROPERTY CHANGE LISTENER *************************/

	/**
	 * Handles property change events for the MovieMapView else calls super.
	 * @param evt, the associated PropertyChangeEvent object.
	 */
	public void propertyChange(PropertyChangeEvent evt) {

	    String prop = evt.getPropertyName();
		Object source = evt.getSource();
	    Object oldvalue = evt.getOldValue();
	    Object newvalue = evt.getNewValue();
	    
	    if (source instanceof MovieMapView) {
	    	if (newvalue instanceof Movie) {
	    		Movie newmovie = (Movie)newvalue;
	    		String sNewMovieID = newmovie.getId();
		    	if (newmovie.getViewID().equals(oMovieMapView.getId())) {	    	
			    	if (prop.equals(MovieMapView.MOVIE_ADDED_PROPERTY)) {
			    		newlyAddedID = newmovie.getId();
			    		vtLocalMovies.add((Movie)newmovie);			    		
			    		refreshMovies();
			    	} else if (prop.equals(MovieMapView.MOVIE_CHANGED_PROPERTY)) {
			    		int count = vtLocalMovies.size();
			    		Movie nextMovie = null;
			    		for (int i=0; i< count; i++) {
			    			nextMovie = (Movie)vtLocalMovies.elementAt(i);
				    		if (nextMovie.getId().equals(sNewMovieID)) {
				    			vtLocalMovies.removeElementAt(i);
				    			vtLocalMovies.insertElementAt(newmovie, i);					    			
				    			break;
				    		}	    			
				    	} 			    		
			    		refreshMovies();
			    		
		    			// make sure dialog adjust to be wide enough!
		    			if (nextMovie != null && nextMovie.getLink().equals("") && !newmovie.getLink().equals("") && count == 1) { //$NON-NLS-1$ //$NON-NLS-2$
		    				setSize(layout.preferredLayoutSize(mainPanel).width+30, oParentDialog.getPreferredSize().height);
		    				setPreferredSize(new Dimension(layout.preferredLayoutSize(mainPanel).width+30, oParentDialog.getPreferredSize().height));
		    			}	    		
			    	} 
		    	}
	    	}
	    
	    	if (prop.equals(MovieMapView.MOVIE_REMOVED_PROPERTY)) {
	    		String id = (String)newvalue;
	    		int count = vtLocalMovies.size();
	    		for (int i=0; i< count; i++) {
	    			Movie nextMovie = (Movie)vtLocalMovies.elementAt(i);
	    			if (nextMovie.getId().equals(id)) {
	    				vtLocalMovies.remove(nextMovie);
	    				break;
	    			}		    			
	    		}
	    		refreshMovies();	    		    	
		    } else if (prop.equals(MovieMapView.MOVIEPROPERTIES_ADDED_PROPERTY)) {	
	    		oProperties = (MovieProperties)newvalue;		    	
	    		refreshMovies();
		    } else if (prop.equals(MovieMapView.MOVIEPROPERTIES_CHANGED_PROPERTY)) {
	    		MovieProperties newprops = (MovieProperties)newvalue;
	    		String sMovieID = newprops.getMovieID();
	    		int count = vtLocalMovies.size();
	    		for (int i=0; i< count; i++) {
	    			Movie nextMovie = (Movie)vtLocalMovies.elementAt(i);
	    			if (nextMovie.getId().equals(sMovieID)) {
	    				nextMovie.setProperties(newprops);
	    				break;
	    			}		    			
	    		}
	    		refreshMovies();
		    } else if (prop.equals(MovieMapView.MOVIEPROPERTIES_REMOVED_PROPERTY)) {
	    		refreshMovies();
	    	}
	    }
	    if (oMovieButton != null) {
			mainPanel.scrollRectToVisible(oMovieButton.getBounds());
			mainPanel.repaint();
	    }
	}	
	
	/**
	 * Redraw the panel.
	 */
	private synchronized void refreshMovies() {
		
		mainPanel.removeAll();

        layout = new GridBagLayout();
		mainPanel.setLayout(layout);
        cons = new GridBagConstraints();
        cons.fill = GridBagConstraints.HORIZONTAL;
        cons.anchor = GridBagConstraints.NORTHWEST;
        cons.gridwidth = GridBagConstraints.REMAINDER;
        cons.weightx = 2;
        cons.ipady = 4;
        cons.insets = new Insets(3,3,3,3);
		
        int count = vtLocalMovies.size();
        for (int i=0; i<count; i++) {
			Movie next = (Movie)vtLocalMovies.elementAt(i);		
	        JPanel panel = createRow(next);
	        layout.setConstraints(panel, cons);		        			
			mainPanel.add(panel);        	
        }
        
		oMovieButton = new JButton(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMovieViewPanel.addMovieButton"));	 //$NON-NLS-1$
		oMovieButton.setToolTipText(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMovieViewPanel.addMovieButtonTip")); //$NON-NLS-1$
		oMovieButton.setMnemonic(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMovieViewPanel.addMovieButtonMnemonic").charAt(0)); //$NON-NLS-1$
		oMovieButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Date date  = new Date();
					try {
						String sMovieID = ID_STUB+date.getTime();
						Vector<MovieProperties> props = new Vector<MovieProperties>();
						
						UIMovieMapViewFrame frame = (UIMovieMapViewFrame)ProjectCompendium.APP.getInternalFrame(oMovieMapView);
						long time = 0;
						if (frame != null) {
							UITimeLinesController controller = frame.getController();
							time = controller.getCurrentTime();
						}						
						Movie movie = new Movie(sMovieID, oMovieMapView.getId(), "", "", time, date, date, props); //$NON-NLS-1$ //$NON-NLS-2$
						vtLocalMovies.addElement(movie);
					} catch (Exception ex) {
						System.out.println("error:"+ex.getLocalizedMessage()); //$NON-NLS-1$
						ex.printStackTrace();
					}			
					Thread thread = new Thread() {
						public void run() {
		    				refreshMovies();
		    			    if (oMovieButton != null) {
		    					mainPanel.scrollRectToVisible(oMovieButton.getBounds());
		    					mainPanel.repaint();
		    			    }
						}
					};
					thread.start();
				} catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		});		
		
       	cons.weighty = 2;
        cons.fill = GridBagConstraints.NONE;

        layout.setConstraints(oMovieButton, cons);		        			
		mainPanel.add(oMovieButton);		
				
		validate();
		repaint();		
	}
	
	/**
	 * Create a row of the panel representing the given movie data
	 * @param movie the movie this panel will display the details of.
	 * @return the JPanel representing the given movie data.
	 */
	private JPanel createRow(Movie movie) {		
		
		JPanel moviePanel = new JPanel();
		moviePanel.setBorder(new CompoundBorder(new LineBorder(Color.gray, 1), new EmptyBorder(6,6,6,6)));
		
		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gc = new GridBagConstraints();
		moviePanel.setLayout(gb);
		moviePanel.setName("MoviePanel"); //$NON-NLS-1$
		gc.insets = new Insets(3,3,3,3);
		gc.anchor = GridBagConstraints.WEST;
		gc.weightx=1;
		gc.weighty=1;

		int y=0;

		final Movie fMovie = movie;
		
		// MOVIE FIELD
		JLabel lbl = new JLabel(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMovieViewPanel.moviePathLabel")+":"); //$NON-NLS-1$
		gc.gridy = y;
		gc.gridx = 0;
		gc.gridwidth = 1;
		gb.setConstraints(lbl, gc);
		moviePanel.add(lbl);

		String sLink = fMovie.getLink();

		final JTextField txt = new JTextField(sLink);
		txt.setFont(new Font("Dialog", Font.PLAIN, 12)); //$NON-NLS-1$
		txt.setColumns(23);
		txt.setMargin(new Insets(2,2,2,2));
		txt.setEditable(false);
		txt.setSize(txt.getPreferredSize());		
		gc.gridy = y;
		gc.gridx = 1;
		gc.fill=GridBagConstraints.HORIZONTAL;
		gc.gridwidth = 8;
		gb.setConstraints(txt, gc);
		moviePanel.add(txt);

		txt.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				String sMovieID = fMovie.getId();
				if (sMovieID == null || sMovieID.equals("")  //$NON-NLS-1$
						|| sMovieID.equals("Unknown")  //$NON-NLS-1$
						|| sMovieID.startsWith(ID_STUB)) {
					try {
						String sPath = txt.getText();
						if (!txt.getText().equals("") && new File(sPath).exists()) { //$NON-NLS-1$
							vtLocalMovies.removeElement(fMovie);										
							oMovieMapView.addMovie(txt.getText(), fMovie.getMovieName(), fMovie.getStartTime(), fMovie.getProperties());
						}
					} catch (Exception ex) {
						System.out.println("error:"+ex.getLocalizedMessage()); //$NON-NLS-1$
						ex.printStackTrace();
					}										
				} else {
					try {
						oMovieMapView.updateMovie(fMovie.getId(), txt.getText(), fMovie.getMovieName(), fMovie.getStartTime());																						
					} catch (Exception ex) {
						System.out.println("error:"+ex.getLocalizedMessage()); //$NON-NLS-1$
						ex.printStackTrace();
					}					
				}
 			}
			public void changedUpdate(DocumentEvent e) {}
			public void removeUpdate(DocumentEvent e) {}
		});
		
		// Can't store movies in the database. 
		// They are too big and you get an out of Memory error.
		/*pbDatabase = new UIButton(UIImages.get(IUIConstants.BACK_ICON)); //$NON-NLS-1$
		pbDatabase.setFont(new Font("Dialog", Font.BOLD, 14)); //$NON-NLS-1$
		pbDatabase.setMargin(new Insets(0,0,0,0));
		pbDatabase.setToolTipText(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.intodatabase")); //$NON-NLS-1$
		pbDatabase.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ProjectCompendium.APP.setWaitCursor();
				String path = txt.getText();
				String sNewPath = path;
				sNewPath = UIUtilities.processLocation(path);
				if (!path.equals(sNewPath)) {
					try {
						oNode.setSource( sNewPath, oNode.getImage(), ProjectCompendium.APP.getModel().getUserProfile().getAuthor() );
						txt.setText(sNewPath);
					} catch(Exception ex) {
						System.out.println("Failed to store new changes: "+ex.getLocalizedMessage());
					}
				}								
				ProjectCompendium.APP.setDefaultCursor();
			}
		});
		processLinkChoices(sLink);

		gc.gridy = y;
		gc.gridx = 9;
		gc.fill=GridBagConstraints.NONE;
		gc.gridwidth = 1;
		gb.setConstraints(pbDatabase, gc);
		moviePanel.add(pbDatabase);*/

		JButton pbBrowse = new UIButton("./.");		 //$NON-NLS-1$
		pbBrowse.setFont(new Font("Dialog", Font.BOLD, 14)); //$NON-NLS-1$
		pbBrowse.setMargin(new Insets(0,0,0,0));
		pbBrowse.setToolTipText(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMovieViewPanel.browseButtonTip")); //$NON-NLS-1$
		pbBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fdgBrowse = new JFileChooser();
				fdgBrowse.setDialogTitle(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMovieViewPanel.chooseMovie"));
				UIUtilities.centerComponent(fdgBrowse, ProjectCompendium.APP);
				
				String path = txt.getText();				
				if (CoreUtilities.isFile(path)) {
					File file = new File(path);
					if (file.exists()) {
						fdgBrowse.setCurrentDirectory(new File(file.getParent()+ProjectCompendium.sFS));
						fdgBrowse.setSelectedFile(file);
					}			
				} else if (!lastFileDialogDir.equals("")) { //$NON-NLS-1$
					// FIX FOR MAC - NEEDS '/' ON END TO DENOTE A FOLDER
					File file = new File(lastFileDialogDir+ProjectCompendium.sFS);
					if (file.exists()) {
						fdgBrowse.setCurrentDirectory(file);
					}
				} else {
					// FIX FOR MAC - NEEDS '/' ON END TO DENOTE A FOLDER
					File file = new File("Movies"+ProjectCompendium.sFS); //$NON-NLS-1$
					if (file.exists()) {
						fdgBrowse.setCurrentDirectory(file); //$NON-NLS-1$
					}			
				}
				int retval = fdgBrowse.showOpenDialog(ProjectCompendium.APP);
				if (retval == JFileChooser.APPROVE_OPTION) {
					if (fdgBrowse.getSelectedFile() != null) {
						File fileDir = fdgBrowse.getCurrentDirectory();
						lastFileDialogDir = fileDir.getPath();
		            	String fileName = fdgBrowse.getSelectedFile().getAbsolutePath();
						txt.setText(fileName);
					}	
				}
			}
		});
		gc.gridy = y;
		gc.gridx = 9;
		gc.fill=GridBagConstraints.NONE;
		gc.gridwidth = 1;
		gb.setConstraints(pbBrowse, gc);
		moviePanel.add(pbBrowse);
		
		if ((oMovie != null && oMovie.getId().equals(movie.getId()) && newlyAddedID.equals("")) || //$NON-NLS-1$
				newlyAddedID.equals(movie.getId())) { 			
			txt.setBackground(SELECTED_BACKGROUND_COLOUR);
		}		

		if (!sLink.equals("")) { //$NON-NLS-1$						
			y++;
					
			lbl = new JLabel(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMovieViewPanel.name")+":"); //$NON-NLS-1$
			Font currentFont = lbl.getFont();
			lbl.setFont(new Font(currentFont.getFamily(), Font.PLAIN, currentFont.getSize()));
			gc.gridy = y;
			gc.gridx = 0;
			gc.gridwidth = 1;
			gb.setConstraints(lbl, gc);
			moviePanel.add(lbl);
			
			final JTextField txtName = new JTextField(String.valueOf(movie.getMovieName()));
			txtName.setFont(new Font("Dialog", Font.PLAIN, 12)); //$NON-NLS-1$
			txtName.setColumns(40);
			txtName.setMargin(new Insets(2,2,2,2));
			txtName.setSize(txtName.getPreferredSize());	
			txtName.addFocusListener(new FocusAdapter() {
				public void focusLost(FocusEvent e) {
					try {
						String name = txtName.getText();
						if (!name.equals(fMovie.getMovieName())) {
							try {
								String sMovieID = fMovie.getId();
								if (sMovieID != null && !sMovieID.equals("")  //$NON-NLS-1$
										&& !sMovieID.equals("Unknown") //$NON-NLS-1$
										&& !sMovieID.startsWith(ID_STUB)) {							
									oMovieMapView.updateMovie(fMovie.getId(), fMovie.getLink(), name, fMovie.getStartTime());																
								}
							} catch (Exception ex) {
								System.out.println("error:"+ex.getLocalizedMessage()); //$NON-NLS-1$
								ex.printStackTrace();
							}					
						}
					} catch(NumberFormatException ex) {
						ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMovieViewPanel.enterMessage")); //$NON-NLS-1$
					}
				}
			});
			gc.fill=GridBagConstraints.HORIZONTAL;
			gc.gridy = y;
			gc.gridx = 1;
			gc.gridwidth = 8;
			gb.setConstraints(txtName, gc);
			moviePanel.add(txtName);	
			
			UIImageButton delete = new UIImageButton(UIImages.get(UIImages.DELETE_ICON));		
			delete.setToolTipText(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMovieViewPanel.deleteButtonTip")); //$NON-NLS-1$
			delete.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int response = JOptionPane.showConfirmDialog(ProjectCompendium.APP, LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMovieViewPanel.deleteConfirmA")+":\n\n"+fMovie.getLink()+LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMovieViewPanel.deleteConfirmB")+"\n",LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMovieViewPanel.deleteConfirmTitle"), JOptionPane.YES_NO_OPTION); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					if (response == JOptionPane.NO_OPTION || response == JOptionPane.CLOSED_OPTION) {
						return;
					} else {								
					try {
						String sMovieID = fMovie.getId();
						if (sMovieID != null && !sMovieID.equals("")  //$NON-NLS-1$
								&& !sMovieID.equals("Unknown") //$NON-NLS-1$
								&& !sMovieID.startsWith(ID_STUB)) {
							oMovieMapView.deleteMovie(sMovieID);					
						} 
					} catch (Exception ex) {
						System.out.println("error:"+ex.getLocalizedMessage()); //$NON-NLS-1$
						ex.printStackTrace();
					}
					}
				}
			});	
			gc.fill=GridBagConstraints.NONE;			
			gc.gridy = y;
			gc.gridx = 9;
			gc.gridwidth = 1;
			gb.setConstraints(delete, gc);
			moviePanel.add(delete);		
			
			y++;
			
			// TIME AT WHICH TO START THE MOVIE PLAYING
			long time = fMovie.getStartTime();
			
			final UITimeMilliSecondPanel timePanel = new UITimeMilliSecondPanel(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMovieViewPanel.startPlaying")+": ", false, true); //$NON-NLS-1$
			timePanel.setMilliSeconds(time);
			timePanel.setToolTipText(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMovieViewPanel.startPlayingTip")); //$NON-NLS-1$
			timePanel.setBorder(null);
			//timePanel.setBorder(new LineBorder(Color.gray, 1));
			timePanel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					long apply = timePanel.getMilliSeconds();
					if (fMovie.getStartTime() != apply) {
						try {
							oMovieMapView.updateMovie(fMovie.getId(), fMovie.getLink(), fMovie.getMovieName(), apply);																
						} catch (Exception ex) {
							System.out.println("error:"+ex.getLocalizedMessage()); //$NON-NLS-1$
							ex.printStackTrace();
						}
					}
				}
			});
			
			gc.gridy = y;
			gc.gridx = 0;
			gc.gridwidth = 10;
			gb.setConstraints(timePanel, gc);
			moviePanel.add(timePanel);
						
			y++;
			
			// Properties
			Vector<MovieProperties> vtProps = fMovie.getProperties();
			Object[] sa = new Object[vtProps.size()];
			vtProps.copyInto(sa);
			List l = Arrays.asList(sa);
			
			Collections.sort(l, new Comparator() {
				public int compare(Object o1, Object o2) {
					MovieProperties data1 = (MovieProperties)o1;
					MovieProperties data2 = (MovieProperties)o2;
					Long s1 = new Long(data1.getTime());
					Long s2 = new Long(data2.getTime());
					return  (s1).compareTo(s2);
				}
			});		
			
			vtProps.removeAllElements();
			vtProps.addAll(l);
			
			int count = vtProps.size();
			MovieProperties next = null;
			for (int i=0; i<count;i++) {
				next = vtProps.elementAt(i);
				JPanel movieProperties = createPropertiesPanel(next, movie);
				
				gc.gridy = y;
				gc.gridx = 1;
				gc.gridwidth = GridBagConstraints.REMAINDER;
				gb.setConstraints(movieProperties, gc);
				moviePanel.add(movieProperties);		
				y++;
			}	

			JButton button = new JButton(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMovieViewPanel.newPropertiesButton"));	 //$NON-NLS-1$
			button.setToolTipText(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMovieViewPanel.newPropertiesButtonTip")); //$NON-NLS-1$
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						// if movie visible get current x,y
						UIMovieMapViewFrame frame = (UIMovieMapViewFrame)ProjectCompendium.APP.getInternalFrame(oMovieMapView);
						int x=0; int y=0;
						long time = 10;
						int width = fMovie.getDefaultWidth();
						int height = fMovie.getDefaultHeight();
						if (frame != null) {
							UITimeLinesController controller = frame.getController();
							if (controller.getCurrentTime() > 10) {
								time = controller.getCurrentTime();
							}							
							UIMovieMapViewPane pane = (UIMovieMapViewPane)frame.getViewPane();
							UIMoviePanel panel = pane.getMovie(fMovie.getId());
							x = panel.getX();
							y= panel.getY();
							width = panel.getWidth();
							height = panel.getHeight();
						}
						
						boolean matchFound = true;
						while(matchFound) {
							matchFound = checkForExisting(fMovie.getProperties(), "0", time); //$NON-NLS-1$
							if (matchFound) {
								time = time+1;
							}
						}
						
						oMovieMapView.addMovieProperties(fMovie.getId(), x, y, width, height, 1.0f, time);
					} catch(Exception ex) {
						ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMovieViewPanel.errorAddingNewProperties")+":\n\n"+ex.getLocalizedMessage()); //$NON-NLS-1$
					}
				}
			});		
			
			gc.gridy = y;
			gc.gridx = 1;
			gc.gridwidth = GridBagConstraints.REMAINDER;
			//gc.weighty = 2;
			gc.fill = GridBagConstraints.NONE;

	        gb.setConstraints(button, gc);		        			
	        moviePanel.add(button);						
	        
		}
		//Buggy on the Mac.
		/* else { 
			final JButton but = pbBrowse;
			Thread thread = new Thread("UIMovieViewPanel.createRow") { //$NON-NLS-1$
				public void run() {
					but.doClick();
				}
			};
			thread.start();
		}*/
		
		return moviePanel;
	}
			
	/**
	 * Determine if the reference in in database on a file and show appropriate button text etc.
	 */
	/*private void processLinkChoices(String sRef) {
		if (LinkedFileDatabase.isDatabaseURI(sRef)) {
			pbDatabase.setEnabled(true);
			pbDatabase.setIcon(UIImages.get(IUIConstants.FORWARD_ICON));
			pbDatabase.setToolTipText(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.outofdatabase")); //$NON-NLS-1$
		} else {
			if (CoreUtilities.isFile(sRef)) {
				pbDatabase.setEnabled(true);
				pbDatabase.setIcon(UIImages.get(IUIConstants.BACK_ICON));
				pbDatabase.setToolTipText(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.intodatabase")); //$NON-NLS-1$
			} else {
				pbDatabase.setEnabled(false);
			}
		}
	}*/
	
	/**
	 * Create a panel representing the details of the given MovieProperties.
	 * @param props the MovieProperties this panel will display the details for.
	 * @param movie the movie these are properties for.
	 * @return a JPanel showing the given MovieProperties.
	 */
	private JPanel createPropertiesPanel(MovieProperties props, Movie movie) {
		JPanel moviePanel = new JPanel();
		moviePanel.setBackground(BACKGROUND_COLOUR);
		moviePanel.setName("PropertiesPanel"); //$NON-NLS-1$
		moviePanel.setBorder(new CompoundBorder(new LineBorder(Color.gray, 1), new EmptyBorder(6,6,6,6)));
		final UITimeMilliSecondPanel timePanel = new UITimeMilliSecondPanel(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMovieViewPanel.applyAt")+": ", false, true); //$NON-NLS-1$
		timePanel.setBackground(BACKGROUND_COLOUR);
		
		if (oProperties != null 
				&& props.getId().equals(oProperties.getId())) {
			moviePanel.setBackground(SELECTED_BACKGROUND_COLOUR);
			timePanel.setBackground(SELECTED_BACKGROUND_COLOUR);
		} 
		
		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gc = new GridBagConstraints();
		moviePanel.setLayout(gb);
		gc.insets = new Insets(3,3,3,3);
		gc.anchor = GridBagConstraints.WEST;
		gc.weightx=1;
		gc.weighty=1;

		int y=0;

		final MovieProperties fMovieProperties = props;	
		final Movie fMovie = movie;
		
		gc.fill=GridBagConstraints.NONE;
		
		y++;		
		
		// TIME AT WHICH TO USE THESE SETTINGS
		long time = fMovieProperties.getTime();
		if (fMovie.getPropertiesCount() == 1) {
			time = 0;
		}
		
		timePanel.setMilliSeconds(time);
		timePanel.setToolTipText(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMovieViewPanel.applyAtTip")); //$NON-NLS-1$
		timePanel.setBorder(new LineBorder(Color.gray, 1));
		timePanel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				long apply = timePanel.getMilliSeconds();
				if (fMovieProperties.getTime() != apply) {
					boolean matchFound = checkForExisting(fMovie.getProperties(), fMovieProperties.getId(), apply);
					if (matchFound) {
						ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMovieViewPanel.duplicateProperties")); //$NON-NLS-1$
						timePanel.setSeconds(fMovieProperties.getTime());					
					} else {					
						try {
							oMovieMapView.updateMovieProperties(fMovieProperties.getId(), fMovieProperties.getMovieID(), fMovieProperties.getXPos(), fMovieProperties.getYPos(), fMovieProperties.getWidth(), fMovieProperties.getHeight(), fMovieProperties.getTransparency(), apply);															
						} catch (Exception ex) {
							System.out.println("error:"+ex.getLocalizedMessage()); //$NON-NLS-1$
							ex.printStackTrace();
						}
					}
				}
			}
		});
		
		if (time == 0) {
			timePanel.setDateEnabled(false);
		}
		
		gc.gridy = y;
		gc.gridx = 0;
		gc.gridwidth = 8;
		gb.setConstraints(timePanel, gc);
		moviePanel.add(timePanel);
		
		// TRANSPARENCY
		JLabel transLabel = new JLabel(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMovieViewPanel.transparency")+" %: "); //$NON-NLS-1$
		gc.gridy = y;
		gc.gridx = 8;
		gc.gridwidth = 1;
		gb.setConstraints(transLabel, gc);
		moviePanel.add(transLabel);
		
		// transparency choice box
		final JComboBox transparencyChoice = new JComboBox();
		transparencyChoice.addItem("0"); //$NON-NLS-1$
		transparencyChoice.addItem("5"); //$NON-NLS-1$
		transparencyChoice.addItem("10"); //$NON-NLS-1$
		transparencyChoice.addItem("15"); //$NON-NLS-1$
		transparencyChoice.addItem("20"); //$NON-NLS-1$
		transparencyChoice.addItem("25"); //$NON-NLS-1$
		transparencyChoice.addItem("30"); //$NON-NLS-1$
		transparencyChoice.addItem("35"); //$NON-NLS-1$
		transparencyChoice.addItem("40"); //$NON-NLS-1$
		transparencyChoice.addItem("45"); //$NON-NLS-1$
		transparencyChoice.addItem("50"); //$NON-NLS-1$
		transparencyChoice.addItem("55"); //$NON-NLS-1$
		transparencyChoice.addItem("60"); //$NON-NLS-1$
		transparencyChoice.addItem("65"); //$NON-NLS-1$
		transparencyChoice.addItem("70"); //$NON-NLS-1$
		transparencyChoice.addItem("75"); //$NON-NLS-1$
		transparencyChoice.addItem("80"); //$NON-NLS-1$
		transparencyChoice.addItem("85"); //$NON-NLS-1$
		transparencyChoice.addItem("90"); //$NON-NLS-1$
		transparencyChoice.addItem("95"); //$NON-NLS-1$
		
		float trans = fMovieProperties.getTransparency();
		float bit = new Float(1-trans).floatValue();		
		// need to round to correct for float math (as 'bit' can be say 0.01999999 not 0.02)
		int selection= new Double((Math.round(100.0D*bit))).intValue();
		transparencyChoice.setSelectedItem(String.valueOf(selection));
		
		transparencyChoice.addItemListener( new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				float trans = new Float( (String)transparencyChoice.getSelectedItem() ).floatValue();
				float ftrans = 1-(trans/100);
				try {
					oMovieMapView.updateMovieProperties(fMovieProperties.getId(), fMovieProperties.getMovieID(), fMovieProperties.getXPos(), fMovieProperties.getYPos(), fMovieProperties.getWidth(), fMovieProperties.getHeight(), ftrans, fMovieProperties.getTime());
				} catch (Exception ex) {
					System.out.println("error:"+ex.getLocalizedMessage()); //$NON-NLS-1$
					ex.printStackTrace();
				}								
			}
		});
		
		gc.gridy = y;
		gc.gridx = 9;
		gc.gridwidth = 1;
		
		gb.setConstraints(transparencyChoice, gc);
		moviePanel.add(transparencyChoice);		
		
		if (time != 0) {
			UIImageButton delete = new UIImageButton(UIImages.get(UIImages.DELETE_ICON));		
			delete.setToolTipText(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMovieViewPanel.deletePropertiesButton")); //$NON-NLS-1$
			delete.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
									 int response = JOptionPane.showConfirmDialog(ProjectCompendium.APP, LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMovieViewPanel.deletePropertiesButtonTip")+"\n",LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMovieViewPanel.deletePropertiesButtonTitle"), JOptionPane.YES_NO_OPTION); //$NON-NLS-1$ //$NON-NLS-2$
									 if (response == JOptionPane.NO_OPTION || response == JOptionPane.CLOSED_OPTION) {
										return;
									 } else {								
										try {
											oMovieMapView.deleteMovieProperties(fMovieProperties.getId(), fMovie.getId());
										} catch (Exception ex) {
											System.out.println("error:"+ex.getLocalizedMessage()); //$NON-NLS-1$
											ex.printStackTrace();
										}
									 }
							 }
			});	
			
			gc.fill=GridBagConstraints.NONE;			
			gc.gridy = y;
			gc.gridx = 10;
			gc.gridwidth = 1;
			gb.setConstraints(delete, gc);
			moviePanel.add(delete);				
		}		
		
		y++;
		
		// XPos
		JLabel lbl2 = new JLabel("x: "); //$NON-NLS-1$
		gc.gridy = y;
		gc.gridx = 0;
		gc.gridwidth = 1;
		gb.setConstraints(lbl2, gc);
		moviePanel.add(lbl2);

		int xPos = fMovieProperties.getXPos();
		final JTextField txt2 = new JTextField(String.valueOf(xPos));
		txt2.setFont(new Font("Dialog", Font.PLAIN, 12)); //$NON-NLS-1$
		txt2.setColumns(4);
		txt2.setMargin(new Insets(2,2,2,2));
		txt2.setSize(txt2.getPreferredSize());		
		gc.gridy = y;
		gc.gridx = 1;
		gb.setConstraints(txt2, gc);
		moviePanel.add(txt2);
		txt2.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				try {
					String pos = txt2.getText();
					if (!pos.equals("")) { //$NON-NLS-1$
						Integer xpos = new Integer(pos);
						try {
							oMovieMapView.updateMovieProperties(fMovieProperties.getId(), fMovieProperties.getMovieID(), xpos, fMovieProperties.getYPos(), fMovieProperties.getWidth(), fMovieProperties.getHeight(), fMovieProperties.getTransparency(), fMovieProperties.getTime());
						} catch (Exception ex) {
							System.out.println("error:"+ex.getLocalizedMessage()); //$NON-NLS-1$
							ex.printStackTrace();
						}					
					}
				} catch(NumberFormatException ex) {
					ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMovieViewPanel.errorXPos")); //$NON-NLS-1$
				}
			}
		});
		
		// YPos
		JLabel lbl3 = new JLabel("y: "); //$NON-NLS-1$
		gc.gridy = y;
		gc.gridx = 2;
		gc.gridwidth = 1;
		gb.setConstraints(lbl3, gc);
		moviePanel.add(lbl3);

		int yPos = fMovieProperties.getYPos();
		final JTextField txt3 = new JTextField(String.valueOf(yPos));
		txt3.setFont(new Font("Dialog", Font.PLAIN, 12)); //$NON-NLS-1$
		txt3.setColumns(4);
		txt3.setMargin(new Insets(2,2,2,2));
		txt3.setSize(txt3.getPreferredSize());		
		gc.gridy = y;
		gc.gridx = 3;
		gb.setConstraints(txt3, gc);
		moviePanel.add(txt3);
		txt3.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				try {
					String pos = txt3.getText();
					if (!pos.equals("")) { //$NON-NLS-1$
						Integer ypos = new Integer(pos);						
						try {
							oMovieMapView.updateMovieProperties(fMovieProperties.getId(), fMovieProperties.getMovieID(), fMovieProperties.getXPos(), ypos, fMovieProperties.getWidth(), fMovieProperties.getHeight(), fMovieProperties.getTransparency(), fMovieProperties.getTime());
						} catch (Exception ex) {
							System.out.println("error:"+ex.getLocalizedMessage()); //$NON-NLS-1$
							ex.printStackTrace();
						}					
					}
				} catch(NumberFormatException ex) {
					ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMovieViewPanel.errorYPos")); //$NON-NLS-1$
				}
			}
			public void changedUpdate(DocumentEvent e) {}
			public void removeUpdate(DocumentEvent e) {}
		});		
				
		JLabel width = new JLabel(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMovieViewPanel.width")+": "); //$NON-NLS-1$
		gc.gridy = y;
		gc.gridx = 4;
		gc.gridwidth = 1;
		gb.setConstraints(width, gc);
		moviePanel.add(width);

		final JTextField txtwidth = new JTextField(String.valueOf(fMovieProperties.getWidth()));
		txtwidth.setFont(new Font("Dialog", Font.PLAIN, 12)); //$NON-NLS-1$
		txtwidth.setColumns(4);
		txtwidth.setMargin(new Insets(2,2,2,2));
		txtwidth.setSize(txt2.getPreferredSize());	
		txtwidth.setEditable(false);
		gc.gridy = y;
		gc.gridx = 5;
		gb.setConstraints(txtwidth, gc);
		moviePanel.add(txtwidth);

		JLabel height = new JLabel(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMovieViewPanel.height")+": "); //$NON-NLS-1$
		gc.gridy = y;
		gc.gridx = 6;
		gc.gridwidth = 1;
		gb.setConstraints(height, gc);
		moviePanel.add(height);

		final JTextField txtheight = new JTextField(String.valueOf(fMovieProperties.getHeight()));
		txtheight.setFont(new Font("Dialog", Font.PLAIN, 12)); //$NON-NLS-1$
		txtheight.setColumns(4);
		txtheight.setMargin(new Insets(2,2,2,2));
		txtheight.setSize(txt2.getPreferredSize());	
		txtheight.setEditable(false);
		gc.gridy = y;
		gc.gridx = 7;
		gb.setConstraints(txtheight, gc);
		moviePanel.add(txtheight);
		
		JButton reset = new JButton("1:1"); //UIImages.get(UIImages.ZOOM_FULL_ICON));		 //$NON-NLS-1$
		reset.setFont(new Font("Dialog", Font.BOLD, 11)); //$NON-NLS-1$
		reset.setToolTipText(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMovieViewPanel.resetMovie")); //$NON-NLS-1$
		reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					oMovieMapView.updateMovieProperties(fMovieProperties.getId(), fMovieProperties.getMovieID(), fMovieProperties.getXPos(), fMovieProperties.getYPos(), fMovie.getDefaultWidth(), fMovie.getDefaultHeight(), fMovieProperties.getTransparency(), fMovieProperties.getTime());
				} catch (Exception ex) {
					System.out.println("error:"+ex.getLocalizedMessage()); //$NON-NLS-1$
					ex.printStackTrace();
				}					
			}
		});	
		gc.fill=GridBagConstraints.NONE;
		gc.gridy = y;
		gc.gridx = 8;
		gb.setConstraints(reset, gc);
		moviePanel.add(reset);

		JButton apply = new JButton(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMovieViewPanel.importButton")); 	 //$NON-NLS-1$
		apply.setFont(new Font("Dialog", Font.BOLD, 11)); //$NON-NLS-1$
		apply.setToolTipText(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMovieViewPanel.importButtonTip")); //$NON-NLS-1$
		apply.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							try {
								UIMovieMapViewFrame frame = (UIMovieMapViewFrame)ProjectCompendium.APP.getInternalFrame(oMovieMapView);
								UIMovieMapViewPane pane = (UIMovieMapViewPane)frame.getViewPane();
								UIMoviePanel panel = pane.getMovie(fMovie.getId());
								if (panel != null) {
									oMovieMapView.updateMovieProperties(fMovieProperties.getId(), fMovieProperties.getMovieID(), panel.getX(), panel.getY(), panel.getSize().width, panel.getSize().height, fMovieProperties.getTransparency(), fMovieProperties.getTime());
								}
							}catch (Exception ex) {
								System.out.println("error:"+ex.getLocalizedMessage()); //$NON-NLS-1$
								ex.printStackTrace();
								}					
								}
								});	
		gc.fill=GridBagConstraints.NONE;
		gc.gridy = y;
		gc.gridx = 9;
		gb.setConstraints(apply, gc);
		moviePanel.add(apply);

		JButton reset2 = new JButton(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMovieViewPanel.exportButton")); 	 //$NON-NLS-1$
		reset2.setFont(new Font("Dialog", Font.BOLD, 11)); //$NON-NLS-1$
		reset2.setToolTipText(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMovieViewPanel.exportButtonTip")); //$NON-NLS-1$
		reset2.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
								try {
								UIMovieMapViewFrame frame = (UIMovieMapViewFrame)ProjectCompendium.APP.getInternalFrame(oMovieMapView);
								UIMovieMapViewPane pane = (UIMovieMapViewPane)frame.getViewPane();
								UIMoviePanel panel = pane.getMovie(fMovie.getId());
								if (panel != null) {
									panel.setLocation(fMovieProperties.getXPos(), fMovieProperties.getYPos());
									panel.setSize(fMovieProperties.getWidth(), fMovieProperties.getHeight());
								}
								}catch (Exception ex) {
								System.out.println("error:"+ex.getLocalizedMessage()); //$NON-NLS-1$
								ex.printStackTrace();
								}					
								}
								});	
		gc.fill=GridBagConstraints.NONE;
		gc.gridy = y;
		gc.gridx = 10;
		gb.setConstraints(reset2, gc);
		moviePanel.add(reset2);
		
		return moviePanel;
	}
	
 	/**
 	 * Check to see if the passed time is the same as another time.
 	 * @param time the time to check
 	 * @return true if it is the same as an existing span, else false;
 	 */
 	private boolean checkForExisting(Vector vtProperties, String id, long time) {
 		int count = vtProperties.size();
 		boolean same = false;		
 		for (int i=0; i<count; i++) {
 			MovieProperties props = (MovieProperties)vtProperties.elementAt(i);
 			if (!id.equals(props.getId())) {				
 				if (props.getTime() == time) {
 					same = true;
 					break;
 				}
 			}
 		}	
 		
 		return same;
 	}
	
	/**
	 * Create and return the button panel.
	 */
	private UIButtonPanel createButtonPanel() {

		UIButtonPanel oButtonPanel = new UIButtonPanel();

		pbCancel = new UIButton(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMovieViewPanel.closeButton")); //$NON-NLS-1$
		pbCancel.setMnemonic(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMovieViewPanel.closeButtonMnemonic").charAt(0));
		pbCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				oParentDialog.onCancel();
			}
		});
		oButtonPanel.addButton(pbCancel);

		UIButton pbHelp = new UIButton(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMovieViewPanel.HelpButton")); //$NON-NLS-1$
		pbHelp.setMnemonic(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMovieViewPanel.HelpButtonMnemonic").charAt(0));
		ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "node.movies", ProjectCompendium.APP.mainHS); //$NON-NLS-1$
		oButtonPanel.addHelpButton(pbHelp);

		return oButtonPanel;
	}	
			
	/**
	 * Set the default button for the parent dialog to be this panel's default button.
	 */
	public void setDefaultButton() {
		oParentDialog.getRootPane().setDefaultButton(pbCancel);
	}
}
