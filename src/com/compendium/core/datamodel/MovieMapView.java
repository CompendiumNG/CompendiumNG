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

package com.compendium.core.datamodel;

import java.awt.Point;
import java.util.*;
import java.sql.SQLException;

import com.compendium.core.datamodel.services.*;
import com.compendium.core.db.*;
import com.compendium.core.CoreUtilities;
import com.compendium.core.ICoreConstants;

/**
 * The View object is a node that represents a collection of nodes and links.
 * The visual representation of the nodes and links depends on the type of the view.
 *
 * @author	Michelle Bachler 
 */
public class MovieMapView extends TimeMapView implements java.io.Serializable {

	/** time added property name for use with property change events */
	public final static String MOVIE_ADDED_PROPERTY = "movieadded";

	/** time removed property name for use with property change events */
	public final static String MOVIE_REMOVED_PROPERTY = "movieremoved";

	/** time changed property name for use with property change events */
	public final static String MOVIE_CHANGED_PROPERTY = "moviechanged";

	/** time added property name for use with property change events */
	public final static String MOVIEPROPERTIES_ADDED_PROPERTY = "moviepropertiesadded";

	/** time changed property name for use with property change events */
	public final static String MOVIEPROPERTIES_CHANGED_PROPERTY = "moviepropertieschanged";

	/** time removed property name for use with property change events */
	public final static String MOVIEPROPERTIES_REMOVED_PROPERTY = "moviepropertiesremoved";

	/** A List of all the time spans for this node. */
	protected Hashtable<String, Movie> htMemberMovies = new Hashtable<String,Movie>(51);

	/**
	 *	Constructor, takes in only the id value.
	 *
	 *	@param sNodeID String, the id of the view object.
	 */
	public MovieMapView(String sNodeID) {
		super(sNodeID);
	}

	/**
	 *	Constructor, creates a MovieMapView object.
	 *
	 *	@param String sViewID, the id of the view node.
	 *	@param nType int, the type of this node.
	 *	@param sXNodeType String, the extended node type id of the node - NOT CURRENTLY USED.
	 *	@param sOriginalID String, the original id of the node if it was imported.
	 *	@param sAuthor String, the author of this node.
	 *	@param dCreationDate Date, the creation date of this node.
	 *	@param dModificationDate Date, the date the node was last modified.
	 */
	protected MovieMapView(String sViewID, int nType, String sXNodeType, String sOriginalID,
					int nState, String sAuthor, Date dCreationDate, Date dModificationDate, 
					String sLabel, String sDetail)
	{
		super( sViewID,  nType,  sXNodeType,  sOriginalID, nState, sAuthor,  dCreationDate,  dModificationDate,  sLabel, sDetail);
	}

	/**
	 *	Constructor, creates a MovieMapView object.
	 *
	 *	@param String sViewID, the id of the view node.
	 *	@param int nType, the type of this node.
	 *	@param String sXNodeType, the extended node type id of the node - NOT CURRENTLY USED.
	 *	@param String sOriginalID, the original id of the node if it was imported.
	 *	@param int nPermission, the permissions in this node - NOT CURRENTLY USED.
	 *	@param int nState, the state of this node: not read (0) read (1), modified since last read (2).
	 *	@param String sAuthor, the author of the node.
	 *	@param Date dCreationDate, the creation date of this node.
	 *	@param Date dModificationDate, the date the node was last modified.
	 *	@param String sLabel, the label of this node.
	 *	@param String sDetail, the first page of detail for this node.
	 */
	protected MovieMapView(String sViewID, int nType, String sXNodeType, String sOriginalID, int nPermission,
							int nState, String sAuthor, Date dCreationDate, Date dModificationDate,
							String sLabel, String sDetail) {

		super(sViewID,  nType,  sXNodeType,  sOriginalID, nPermission, nState, sAuthor,  
				dCreationDate,  dModificationDate,  sLabel, sDetail);
	}
	
	/**
	 *	Constructor, creates a MovieMapView object.
	 *
	 *	@param String sViewID, the id of the view node.
	 *	@param nType int, the type of this node.
	 *	@param sXNodeType String, the extended node type id of the node - NOT CURRENTLY USED.
	 *	@param sOriginalID String, the original id of the node if it was imported.
	 *	@param sAuthor String, the author of this node.
	 *	@param dCreationDate Date, the creation date of this node.
	 *	@param dModificationDate Date, the date the node was last modified.
	 *	@param sLastModAuthor the author who last modified this object.*
	 */
	protected MovieMapView(String sViewID, int nType, String sXNodeType, String sOriginalID,
					int nState, String sAuthor, Date dCreationDate, Date dModificationDate, 
					String sLabel, String sDetail, String sLastModAuthor)
	{
		super( sViewID,  nType,  sXNodeType,  sOriginalID, nState, sAuthor,  dCreationDate,  
				dModificationDate,  sLabel, sDetail, sLastModAuthor);
	}
	
	/**
	 *	Constructor, creates a MovieMapView object.
	 *
	 *	@param sViewID the id of the view node.
	 *	@param nType the type of this node.
	 *	@param sXNodeType the extended node type id of the node - NOT CURRENTLY USED.
	 *	@param sOriginalID the original id of the node if it was imported.
	 *	@param nPermission the permissions in this node - NOT CURRENTLY USED.
	 *	@param nState the state of this node: not read (0) read (1), modified since last read (2).
	 *	@param sAuthor the author of the node.
	 *	@param dCreationDate the creation date of this node.
	 *	@param dModificationDate the date the node was last modified.
	 *	@param sLabel the label of this node.
	 *	@param sDetail the first page of detail for this node.
	 *	@param sLastModAuthor the author who last modified this object.
	 */
	protected MovieMapView(String sViewID, int nType, String sXNodeType, String sOriginalID, int nPermission,
							int nState, String sAuthor, Date dCreationDate, Date dModificationDate,
							String sLabel, String sDetail, String sLastModAuthor) {

		super(sViewID,  nType,  sXNodeType,  sOriginalID, nPermission, nState, sAuthor,  dCreationDate,  
				dModificationDate, sLabel, sDetail, sLastModAuthor);
	}	

	/**
	 * Return a MovieMapView object with the given id.
	 * If a view node with the given id has already been created in this session, return that,
	 * else create a new one, and add it to the list.
	 *
	 * @param String id, the id of the node to return/create.
	 * @return View, a view node object with the given id.
	 */
	public static MovieMapView getView(String id) {

		int i = 0;
		Vector nodeSummaryList = NodeSummary.getNodeSummaryList();
		for (i = 0; i < nodeSummaryList.size(); i++) {
			if (id.equals(((NodeSummary)nodeSummaryList.elementAt(i)).getId())) {
				break;
			}
		}

		MovieMapView ns = null;
		if (i == nodeSummaryList.size()) {
			ns = new MovieMapView(id);
			nodeSummaryList.addElement(ns);
		}
		else {
			Object obj = nodeSummaryList.elementAt(i);
			if (obj instanceof MovieMapView) {
				ns = (MovieMapView)obj;
			}
			else {
				nodeSummaryList.removeElement(obj);
				ns = new MovieMapView(id);
				nodeSummaryList.addElement(ns);
			}
		}
		return ns;
	}

	/**
	 * Return a MovieMapView object with the given id and details.
	 * If a view node with the given id has already been created in this session, update its data and return that,
	 * else create a new one, and add it to the list.
	 *
	 *	@param String sViewID, the id of the view node.
	 *	@param int nType, the type of this node.
	 *	@param String sXNodeType, the extended node type id of the node - NOT CURRENTLY USED.
	 *	@param String sOriginalID, the original id of the node if it was imported.
	 *	@param String sAuthor, the author of the node.
	 *	@param Date dCreationDate, the creation date of this node.
	 *	@param Date dModificationDate, the date the node was last modified.
	 *	@param String sLabel, the label of this node.
	 *	@param String sDetail, the first page of detail for this node.
	 *  @return View, a view node object with the given id.
	 */
	public static MovieMapView getView(String sViewID, int nType, String sXNodeType, String sOriginalID,
				int nState, String sAuthor, Date dCreationDate, Date dModificationDate, 
				String sLabel, String sDetail)
	{
		int i = 0;
		Vector nodeSummaryList = NodeSummary.getNodeSummaryList();
		for (i = 0; i < nodeSummaryList.size(); i++) {
			if (sViewID.equals(((NodeSummary)nodeSummaryList.elementAt(i)).getId())) {
				break;
			}
		}

		MovieMapView ns = null;
		if (i == nodeSummaryList.size()) {
			ns = new MovieMapView(sViewID, nType, sXNodeType, sOriginalID,
								 nState,
								 sAuthor, dCreationDate,
								 dModificationDate, sLabel, sDetail);
			nodeSummaryList.addElement(ns);
		}
		else {
			Object obj = nodeSummaryList.elementAt(i);
			if (obj instanceof MovieMapView) {
				ns = (MovieMapView)obj;

				// UPDATE THE DETAILS
				if (!ns.bLabelDirty) {
					ns.setLabelLocal(sLabel);
				}
				ns.setDetailLocal(sDetail);
				ns.setTypeLocal(nType);
				ns.setStateLocal(nState);
				ns.setAuthorLocal(sAuthor);
				ns.setCreationDateLocal(dCreationDate);
				ns.setModificationDateLocal(dModificationDate);
				ns.setOriginalIdLocal(sOriginalID);
				ns.setExtendedNodeTypeLocal(sXNodeType);
			}
			else {
				nodeSummaryList.removeElement(obj);
				ns = new MovieMapView(sViewID, nType, sXNodeType, sOriginalID,
								 nState, sAuthor, dCreationDate,
								 dModificationDate, sLabel, sDetail);
				nodeSummaryList.addElement(ns);
			}
		}
		return ns;
	}

	/**
	 * Return a MovieMapView object with the given id and details.
	 * If a view node with the given id has already been created in this session, update its data and return that,
	 * else create a new one, and add it to the list.
	 *
	 *	@param sViewID the id of the view node.
	 *	@param nType the type of this node.
	 *	@param sXNodeType the extended node type id of the node - NOT CURRENTLY USED.
	 *	@param sOriginalID the original id of the node if it was imported.
	 *	@param sAuthor the author of the node.
	 *	@param dCreationDate the creation date of this node.
	 *	@param dModificationDate the date the node was last modified.
	 *	@param sLabel the label of this node.
	 *	@param sDetail the first page of detail for this node.
	 *	@param sLastModAuthor the author who last modified this object.
	 *  @return View, a view node object with the given id.
	 */
	public static MovieMapView getView(String sViewID, int nType, String sXNodeType, String sOriginalID,
				int nState, String sAuthor, Date dCreationDate, Date dModificationDate, 
				String sLabel, String sDetail, String sLastModAuthor)
	{
		int i = 0;
		Vector nodeSummaryList = NodeSummary.getNodeSummaryList();
		for (i = 0; i < nodeSummaryList.size(); i++) {
			if (sViewID.equals(((NodeSummary)nodeSummaryList.elementAt(i)).getId())) {
				break;
			}
		}

		MovieMapView ns = null;
		if (i == nodeSummaryList.size()) {
			ns = new MovieMapView(sViewID, nType, sXNodeType, sOriginalID,
								 nState,
								 sAuthor, dCreationDate,
								 dModificationDate, sLabel, sDetail, sLastModAuthor);
			nodeSummaryList.addElement(ns);
		}
		else {
			Object obj = nodeSummaryList.elementAt(i);
			if (obj instanceof MovieMapView) {
				ns = (MovieMapView)obj;

				// UPDATE THE DETAILS
				if (!ns.bLabelDirty) {
					ns.setLabelLocal(sLabel);
				}
				ns.setDetailLocal(sDetail);
				ns.setTypeLocal(nType);
				ns.setStateLocal(nState);
				ns.setAuthorLocal(sAuthor);
				ns.setCreationDateLocal(dCreationDate);
				ns.setModificationDateLocal(dModificationDate);
				ns.setOriginalIdLocal(sOriginalID);
				ns.setExtendedNodeTypeLocal(sXNodeType);
				ns.setLastModificationAuthorLocal(sLastModAuthor);				
			}
			else {
				nodeSummaryList.removeElement(obj);
				ns = new MovieMapView(sViewID, nType, sXNodeType, sOriginalID,
								 nState, sAuthor, dCreationDate,
								 dModificationDate, sLabel, sDetail);
				nodeSummaryList.addElement(ns);
			}
		}
		return ns;
	}
	
	/**
	 * Return a MovieMapView object with the given id and details.
	 * If a view node with the given id has already been created in this session, update its data and return that,
	 * else create a new one, and add it to the list.
	 *
	 *	@param String sViewID, the id of the view node.
	 *	@param int nType, the type of this node.
	 *	@param String sXNodeType, the extended node type id of the node - NOT CURRENTLY USED.
	 *	@param String sOriginalID, the original id of the node if it was imported.
	 *	@param int nPermission, the permissions in this node - NOT CURRENTLY USED.
	 *	@param int nState, the state of this node: not read (0) read (1), modified since last read (/2).
	 *	@param String sAuthor, the author of the node.
	 *	@param Date dCreationDate, the creation date of this node.
	 *	@param Date dModificationDate, the date the node was last modified.
	 *	@param String sLabel, the label of this node.
	 *	@param String sDetail, the first page of detail for this node.
	 *  @return View, a view node object with the given id.
	 */
	public static MovieMapView getView(String sViewID, int nType, String sXNodeType, String sOriginalID, int nPermission,
							int nState, String sAuthor, Date dCreationDate, Date dModificationDate,
							String sLabel, String sDetail)
	{
		int i = 0;
		Vector nodeSummaryList = NodeSummary.getNodeSummaryList();
		for (i = 0; i < nodeSummaryList.size(); i++) {
			if (sViewID.equals(((NodeSummary)nodeSummaryList.elementAt(i)).getId())) {
				break;
			}
		}

		MovieMapView ns = null;
		if (i == nodeSummaryList.size()) {
			ns = new MovieMapView(sViewID, nType, sXNodeType, sOriginalID,
								 nPermission, nState, sAuthor, dCreationDate,
								 dModificationDate, sLabel, sDetail);
			nodeSummaryList.addElement(ns);
		}
		else {
			Object obj = nodeSummaryList.elementAt(i);
			if (obj instanceof MovieMapView) {
				ns = (MovieMapView)obj;

				// UPDATE THE DETAILS
				if (!ns.bLabelDirty) {				
					ns.setLabelLocal(sLabel);
				}
				ns.setDetailLocal(sDetail);
				ns.setTypeLocal(nType);
				ns.setAuthorLocal(sAuthor);
				ns.setCreationDateLocal(dCreationDate);
				ns.setModificationDateLocal(dModificationDate);
				ns.setOriginalIdLocal(sOriginalID);
				ns.setExtendedNodeTypeLocal(sXNodeType);
				ns.setPermissionLocal(nPermission);
				ns.setStateLocal(nState);
			}
			else {
				nodeSummaryList.removeElement(obj);
				ns = new MovieMapView(sViewID, nType, sXNodeType, sOriginalID,
								 nPermission, nState, sAuthor, dCreationDate,
								 dModificationDate, sLabel, sDetail);
				nodeSummaryList.addElement(ns);
			}
		}
		return ns;
	}

	/**
	 * Override.
	 * Loads all the nodes and node times and links into this view from the DATABASE.
	 *
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 */
	public void initializeMembers() throws SQLException, ModelSessionException {

		if (!bMembersInitialized) {

			if (oModel == null)
				throw new ModelSessionException("Model is null in MovieMapView.initializeMembers");
			if (oSession == null) {
				oSession = oModel.getSession();
				if (oSession == null)
					throw new ModelSessionException("Session is null in MovieMapView.initializeMembers");
			}

			super.initializeMembers();
			bMembersInitialized = false;
			
			Vector<Movie> vtMovies = oModel.getMovieService().getMovies(oModel.getSession(), this.getId());

			for(Enumeration e = vtMovies.elements(); e.hasMoreElements();) {
				Movie movie = (Movie)e.nextElement();
				movie.initialize(oModel.getSession(), oModel);				
				htMemberMovies.put(movie.getId(), movie);
			}
		}
		bMembersInitialized = true;
	}
	
	/**
	 * Clear all data associated with this View and reloads it from scratch from the database.
	 * 
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException	 
	 * */
	public void reloadViewData() throws SQLException, ModelSessionException {
		this.htMemberMovies.clear();
		super.reloadViewData();
	}

	/**
	 * Get the Enumeration of <code>Movies</code>objects for this view.
	 * @return Enumeration of Movie objects for this view.
	 */
	public Enumeration getMovies() {
		return htMemberMovies.elements();
	}
	
	/**
	 * Adds a new movie to this view, both locally and in the DATABASE.
	 *
	 * @param sLink the path or url to the movie
	 * @param sName the user-friendly name for this movie.
	 * @param time the time to start the movie at.
	 * @param props<MovieProperties> a list of the associated movie properties for this movie.
	 * @return the Movie object if the movie was successfully added, otherwise null.
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 */
	public Movie addMovie(String sLink, String sName, long time, 
							Vector<MovieProperties> props) throws SQLException, ModelSessionException {

		
		if (oModel == null)
			throw new ModelSessionException("Model is null in MovieMap.addMovie");
		if (oSession == null) {
			oSession = oModel.getSession();
			if (oSession == null)
				throw new ModelSessionException("Session is null in MovieMap.addMovie");
		}

		//Create the Movie in the Movie table
  		IMovieService ms = getModel().getMovieService();
		String sID = oModel.getUniqueID();
		
 		Movie movie = ms.addMovie(oModel.getSession(), sID, this.getId(), sLink, sName, time, props);
 
		//Local Hashtable update
		if (htMemberMovies.containsKey(sID)){
			htMemberMovies.put(sID, movie);
		} else {
			htMemberMovies.put(sID, movie);
		}

 		//Add first properties set.
		MovieProperties prop = 	addMovieProperties(sID, 0, 0, 0, 0, 1.0f, 0);
		movie.setProperties(prop);
 		
		firePropertyChange(MOVIE_ADDED_PROPERTY, movie, movie);

		return movie;
	}

	/**
	 * Adds a new movie to this view, both locally and in the DATABASE.
	 *
	 * @param sMovieID the id of the movie these are properties for
	 * @param x the x location of the movie
	 * @param y the y location of the movie
	 * @param width the width of the movie
	 * @param height the height of the movie
	 * @param transparency the transparency of the movie
	 * @param time the time at which to apply these setting to the movie.
	 * @return the MovieProperties object if the movieproperties was successfully added, otherwise null.
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 */
	public MovieProperties addMovieProperties(String sMovieID, int x, int y, int width, int height, 
					float transparency, long time) throws SQLException, ModelSessionException {
		
		if (oModel == null)
			throw new ModelSessionException("Model is null in MovieMap.addMovieProperties");
		if (oSession == null) {
			oSession = oModel.getSession();
			if (oSession == null)
				throw new ModelSessionException("Session is null in MovieMap.addMovieProperties");
		}

		//Create the Movie in the Movie table
  		IMovieService ms = getModel().getMovieService();
		String sID = oModel.getUniqueID();
		
 		MovieProperties props = ms.addMovieProperties(oModel.getSession(), sID, sMovieID, x, y, width, height, transparency, time);

		//Local Hashtable update
 		if (htMemberMovies.containsKey(sMovieID)){
			Movie movie = htMemberMovies.get(sMovieID);
			movie.setProperties(props);
		} 

		firePropertyChange(MOVIEPROPERTIES_ADDED_PROPERTY, props, props);

		return props;
	}
	
	/**
	 * Update the movie with the given id, both locally and in the DATABASE.
	 *
	 * @param sMovieID the id of the movie to update.
	 * @param sLink the path or url to the movie
	 * @param sName the user-friendly name for this movie.
	 * @param time the time to start the movie at.
	 * @param props<MovieProperties> a list of the associated movie properties for this movie.
	 * @return the Movie object if the movie was successfully updated, otherwise null.
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 */
	public Movie updateMovie(String sMovieID, String sLink, String sName, long time, 
					Vector<MovieProperties> props) throws SQLException, ModelSessionException {

		
		if (oModel == null)
			throw new ModelSessionException("Model is null in MovieMapView.updateMovie");
		if (oSession == null) {
			oSession = oModel.getSession();
			if (oSession == null)
				throw new ModelSessionException("Session is null in MovieMapView.updateMovie");
		}

		//Update the Movie in the Movie table
  		IMovieService ms = getModel().getMovieService();  		
 		Movie movie = ms.updateMovie(oModel.getSession(), sMovieID, this.getId(), sLink, sName, time, props);

		//Local Hashtable update
 		Movie oldMovie = null;
 		
		if (htMemberMovies.containsKey(sMovieID)){
			oldMovie = htMemberMovies.get(sMovieID);
			htMemberMovies.put(sMovieID, movie);
		} else {
			htMemberMovies.put(sMovieID, movie);
		}

		firePropertyChange(MOVIE_CHANGED_PROPERTY, oldMovie, movie);

		return movie;
	}
		
	/**
	 * Update the movie with the given id, both locally and in the DATABASE.
	 *
	 * @param sMovieID the id of the movie to update.
	 * @param sLink the path or url to the movie
	 * @param sName the user-friendly name for this movie.
	 * @param time the time to start the movie at.
	 * @return the Movie object if the movie was successfully updated, otherwise null.
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 */
	public Movie updateMovie(String sMovieID, String sLink, String sName, long time 
					) throws SQLException, ModelSessionException {

		
		if (oModel == null)
			throw new ModelSessionException("Model is null in MovieMapView.updateMovie");
		if (oSession == null) {
			oSession = oModel.getSession();
			if (oSession == null)
				throw new ModelSessionException("Session is null in MovieMapView.updateMovie");
		}

		//Update the Movie in the Movie table
  		IMovieService ms = getModel().getMovieService();  		
 		Movie movie = ms.updateMovie(oModel.getSession(), sMovieID, this.getId(), sLink, sName, time);

		//Local Hashtable update
 		Movie oldMovie = null;
 		
		if (htMemberMovies.containsKey(sMovieID)){
			oldMovie = htMemberMovies.get(sMovieID);
			htMemberMovies.put(sMovieID, movie);
		} else {
			htMemberMovies.put(sMovieID, movie);
		}

		firePropertyChange(MOVIE_CHANGED_PROPERTY, oldMovie, movie);

		return movie;
	}
	
	/**
	 * Update the movie with the given id, both locally and in the DATABASE.
	 *
	 * @param sMovieProertiesID the unique id of the properties to update
	 * @param sMovieID the id of the movie these are properties for
	 * @param x the x location of the movie
	 * @param y the y location of the movie
	 * @param width the width of the movie
	 * @param height the height of the movie
	 * @param transparency the transparency of the movie
	 * @param time the time at which to apply these setting to the movie.
	 * @return the MovieProperties object if the MovieProperties was successfully updated, otherwise null.
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 */
	public MovieProperties updateMovieProperties(String sMoviePropertiesID, String sMovieID, int xPos, int yPos, 
						int width, int height, float transparency, long time) throws SQLException, ModelSessionException {

		
		if (oModel == null)
			throw new ModelSessionException("Model is null in MovieMapView.updateMovieProperties");
		if (oSession == null) {
			oSession = oModel.getSession();
			if (oSession == null)
				throw new ModelSessionException("Session is null in MovieMapView.updateMovieProperties");
		}

		//Update the MovieProperties in the Movie table
  		IMovieService ms = getModel().getMovieService();  		
 		MovieProperties props = ms.updateMovieProperties(oModel.getSession(), sMoviePropertiesID, sMovieID, xPos, yPos, width, height, transparency, time);
 		
		//Local Hashtable update
 		if (props != null) {
	 		Movie movie = null;
	 		MovieProperties oldProps = null;
			if (htMemberMovies.containsKey(sMovieID)){
				movie = htMemberMovies.get(sMovieID);
				oldProps = movie.getProperties(sMoviePropertiesID);
				movie.setProperties(props);
			}

			firePropertyChange(MOVIEPROPERTIES_CHANGED_PROPERTY, oldProps, props);
 		}
		return props;
	}
	
	/**
	 * Delete a movie from this view, both locally and in the DATABASE.
	 *
	 * @param sMovieID the id of the movie to delete
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 */
	public void deleteMovie(String sMovieID) 
						throws SQLException, ModelSessionException { 
		
		if (oModel == null)
			throw new ModelSessionException("Model is null in MovieMapView.deleteMovie");
		if (oSession == null) {
			oSession = oModel.getSession();
			if (oSession == null)
				throw new ModelSessionException("Session is null in MovieMapView.deleteMovie");
		}

  		IMovieService ms = getModel().getMovieService();
 		ms.deleteMovie(oModel.getSession(), sMovieID);
		
 		//Local Hashtable update
		if (htMemberMovies.containsKey(sMovieID)){
			htMemberMovies.remove(sMovieID);
		} 

		firePropertyChange(MOVIE_REMOVED_PROPERTY, sMovieID, sMovieID);
	}
	
	/**
	 * Delete the MovieProperties with the given id from the Movie with the given id, both locally and in the DATABASE.
	 *
	 * @param sMovieID the id of the movie to delete
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 */
	public void deleteMovieProperties(String sMoviePropertiesID, String sMovieID) 
						throws SQLException, ModelSessionException { 
		
		if (oModel == null)
			throw new ModelSessionException("Model is null in MovieMapView.deleteMovieProperties");
		if (oSession == null) {
			oSession = oModel.getSession();
			if (oSession == null)
				throw new ModelSessionException("Session is null in MovieMapView.deleteMovieProperties");
		}

  		IMovieService ms = getModel().getMovieService();
 		ms.deleteMovieProperties(oModel.getSession(), sMoviePropertiesID);
		
 		Movie movie = null;
 		//Local Hashtable update
		if (htMemberMovies.containsKey(sMovieID)){
			movie = htMemberMovies.get(sMovieID);
			movie.removeProperties(sMoviePropertiesID);
		} 

		firePropertyChange(MOVIEPROPERTIES_REMOVED_PROPERTY, sMovieID, sMoviePropertiesID);
	}	
	
	/**
	 * Clear all the movie data as well as call super to clear times, nodes and links etc.
	 *
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 */
	public void clearViewForTypeChange() throws SQLException, ModelSessionException {
		super.clearViewForTypeChange();
		clearMovies();
	}
	
	/**
	 * Clear all the movie data (used for node type change).
	 *
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 */
	public void clearMovies() throws SQLException, ModelSessionException {
		// clear movie data
		for (Enumeration e = getMovies(); e.hasMoreElements();) {
			Movie movie = (Movie)e.nextElement();
			deleteMovie(movie.getId());
		}		
		// On cascade delete will take care of MovieProperties in the database when movies are deleted.
	}	
	
}
