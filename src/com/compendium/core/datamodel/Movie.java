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

import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.util.Date;
import java.util.Vector;

/**
 * The Movie object defines the movie attributes for a movie in a map
 *
 * @author	Michelle Bachler
 */
public class Movie extends PCObject implements java.io.Serializable {

	/** link property name for use with property change events */
	public final static String MOVIELINK_PROPERTY = "movielink";

	/** controller property name for use with property change events */
	public final static String MOVIENAME_PROPERTY = "moviename";

	/** controller property name for use with property change events */
	public final static String MOVIETIME_PROPERTY = "moviestarttime";

	/** Dimension property name for use with property change events */
	public final static String DEFAULTDIMENSION_PROPERTY = "moviedefaultdimension";

	/** The unique id of this movie record.*/
	protected String 	sMovieID = "Unknown";
	
	/** The unique id of the View this movie is in.*/
	protected String 		sViewID 		= "";

	/** The path or url to the movie data.*/
	protected String 		sLink 			= "";

	/** The time at which to start playing this movie*/
	protected long			lStartTime			= 0;
	
	/** The date this object was created.*/
	protected Date			oCreationDate			= null;

	/** The date this object was last modified.*/
	protected Date			oModificationDate		= null;
	
	/** The name displayed to identify this movie to the user.*/
	protected String		sName					= "";
	
	/** The default width of the movie associated with this object.*/
	protected int	 		nDefaultWidth			= -1;

	/** The default height of the movie associated with this object.*/
	protected int			nDefaultHeight			= -1;
	
	protected Vector<MovieProperties> vtProperties	= null;

	/**
	 * Constructor, creates a new Movie,
	 * defining the attributes of the given movie in the given view.
	 *
	 * @param sMovieID The unique id for this movie record.
	 * @param sViewID The view in which the node is placed.
	 * @param sLink The path or url to the movie.
	 * @param sName the name to identify this movie
	 * @param time the time at which to start this movie.
	 * @param dCreated the date this object was created.
	 * @param dModified the date this object was last modified.
	 * @param properties the movie properties for this movie	 
	 */
	public Movie(String sMovieID, String viewID, String link, 
						String name, long time, Date dCreated, Date dModified, 
						Vector<MovieProperties> properties) {

		this.sMovieID = sMovieID;
		this.sViewID = viewID;
		this.sLink = link;
		if (name == null || name.equals("")) {
			name = new File(sLink).getName();
		}
		this.sName = name;
		this.lStartTime = time;
		this.oCreationDate = dCreated;
		this.oModificationDate = dModified;
		this.vtProperties = properties;
	}	

	/**
	 * Return a new Movie object with the properties of this movie.
	 */
	public Movie getClone() {
		return new Movie(sMovieID, sViewID, sLink, sName, lStartTime, oCreationDate, oModificationDate, vtProperties);
	}

	/**
	 * Return the 0 time stamped set of properties.
	 * @return the 0 time stamped set of properties, or null if not found;
	 */
	public MovieProperties getStartingProperties() {
		MovieProperties props = null;
		int count = vtProperties.size();
		MovieProperties next = null;
		for (int i=0; i<count; i++) {
			next = (MovieProperties) vtProperties.elementAt(i);
			if (next.getTime() == 0) {
				props = next;
				break;
			}
		}
		return props;
	}
	
	/**
	 * Return the list of MovieProperties object associated with this movie.
	 * @return Vector the list of MovieProperties object associated with this movie.
	 */
	public Vector<MovieProperties> getProperties() {
		return vtProperties;
	}
	
	/**
	 * Return how many sets of Properties this movie has.
	 * @return how many sets of Properties this movie has.
	 */
	public int getPropertiesCount() {
		return vtProperties.size();
	}
	
	/**
	 * Return the MovieProperties object with the given id
	 * @param sMoviePropertyID the id to find the MovieProperties object for.
	 * @return the MovieProperties object for the given id else null;
	 */
	public MovieProperties getProperties(String sMoviePropertyID) {
		int count = vtProperties.size();
		MovieProperties reply = null;
		MovieProperties next = null;
		for (int i=0; i<count; i++) {
			next = (MovieProperties) vtProperties.elementAt(i);
			if (next.getId().equals(sMoviePropertyID)) {
				reply = next;
				break;
			}
		}
		return reply;
	}
	
	 /**
	  * Add the given MovieProperties object to the list.
	  * If a MovieProperties object with the same id already exists, replace it with this one.
	  * @param properties the properties to add/update
	  */
	public void setProperties(MovieProperties properties) {
		if (properties == null) {
			return;
		}
		
		String propID = properties.getId();
		int count = vtProperties.size();
		MovieProperties next = null;
		boolean bFound = false;
		for (int i=0; i<count; i++) {
			next = (MovieProperties) vtProperties.elementAt(i);
			if (next.getId().equals(propID)) {
				vtProperties.remove(next);
				vtProperties.add(properties);
				bFound = true;
				break;
			}
		}
		
		if (!bFound) {
			vtProperties.add(properties);
		}
	}
	
	 /**
	  * remove the MovieProperties object with the given id from the list.
	  * @param sMoviePropertiesID the id of the properties to remove
	  */
	public void removeProperties(String sMoviePropertiesID) {
		int count = vtProperties.size();
		MovieProperties next = null;
		for (int i=0; i<count; i++) {
			next = (MovieProperties) vtProperties.elementAt(i);
			if (next.getId().equals(sMoviePropertiesID)) {
				vtProperties.remove(next);
				break;
			}
		}
	}
	
	/**
	 * Return the unique id or this record.
	 * @return
	 */
	public String getId() {
		return sMovieID;
	}

	/**
	 * Return the unique view id this movie is in.
	 * @return
	 */
	public String getViewID() {
		return sViewID;
	}

	/**
	 * Returns the The path or url to the movie.
	 *
	 * @return The path or url to the movie.
	 */
	public String getLink() {
		 return sLink ;
	}

	/**
	 * Sets The path or url to the movie, in the local data ONLY.
	 * and fires a PropertyChangeEvent.
	 *
	 * @param link The path or url to the movie.
	 */
	public void setLink(String link) {
		String oldLink = sLink;
		this.sLink = link;
		firePropertyChange(MOVIELINK_PROPERTY, oldLink, sLink);
	}
	
	/**
	 * Returns the time to start this movie at.
	 *
	 * @return the time to start this movie at.
	 */
	public long getStartTime() {
		 return lStartTime ;
	}

	/**
	 * Sets the X coordinate of the movie's position in the defined view, in the local data ONLY.
	 * and fires a PropertyChangeEvent.
	 *
	 * @param int x, the X coordinate of the nodes position.
	 */
	public void setStartTime(long time) {
		long oldTime = lStartTime;
		lStartTime = time;
		firePropertyChange(MOVIETIME_PROPERTY, oldTime, lStartTime);
	}

	/**
	 * Returns the name that identifies this movie to the users.
	 *
	 * @return the name that identifies this movie to the users.
	 */
	public String getMovieName() {
		 return sName ;
	}

	/**
	 * Sets the name that identifies this movie to the users.
	 * and fires a PropertyChangeEvent.
	 *
	 * @param nmae the name that identifies this movie to the users
	 */
	public void setMovieName(String name) {
		String oldName = sName;
		this.sName = name;
		firePropertyChange(MOVIENAME_PROPERTY, oldName, sName);
	}	
	
	/**
	 *	Sets the date when this node was created, in the local data ONLY.
	 *
	 *	@param date the creation date of this object.
	 */
	public void setCreationDate(Date date) {
		oCreationDate = date ;
	}

	/**
	 *	Returns the creation date of this object.
	 *
	 *	@return the date when this object was created.
	 */
	public Date getCreationDate() {
		return oCreationDate;
	}

	/**
	 * Sets the ModificationDate date of this object, in the local data ONLY.
	 *
	 * @param date the date this object was last modified.
	 */
	public void setModificationDate(Date date) {
		oModificationDate = date;
	}

	/**
	 *	Returns the modification date of this object.
	 *
	 *	@return the date when this object was last modified.
	 */
	public Date getModificationDate() {
		return oModificationDate;
	}
	
	
	/**
	 * Returns the default width of the movie in the defined view.
	 *
	 * @return width the width of the movie.
	 */
	public int getDefaultWidth() {
		 return nDefaultWidth;
	}

	/**
	 * Sets the default width of the movie in the defined view, in the local data ONLY.
	 * and fires a PropertyChangeEvent.
	 *
	 * @param width the width of the movie.
	 */
	public void setDefaultWidth(int width) {
		Dimension oldDim = new Dimension(0, 0);
		nDefaultWidth = width;
		firePropertyChange(DEFAULTDIMENSION_PROPERTY, oldDim, new Dimension(nDefaultWidth, nDefaultHeight));
	}

	/**
	 * Returns the default height of the movie in the defined view.
	 *
	 * @return the height of the movie.
	 */
	public int getDefaultHeight() {
		return nDefaultHeight ;
	}

	/**
	 * Sets the default height of the movie in the defined view, in the local data ONLY.
	 * and fires a PropertyChangeEvent.
	 *
	 * @param height the height of the movie.
	 */
	public void setDefaultHeight(int height) {
		Dimension oldDim = new Dimension(0, 0);
		nDefaultHeight = height;
		firePropertyChange(DEFAULTDIMENSION_PROPERTY, oldDim, new Dimension(nDefaultWidth, nDefaultHeight));
	}	
		
}
