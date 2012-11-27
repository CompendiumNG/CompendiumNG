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
import java.util.Date;

/**
 * The Movie object defines the movie attributes for a movie in a map
 *
 * @author	Michelle Bachler
 */
public class MovieProperties extends PCObject implements java.io.Serializable {

	/** Position property name for use with property change events */
	public final static String POSITION_PROPERTY = "movieposition";

	/** Dimension property name for use with property change events */
	public final static String DIMENSION_PROPERTY = "moviedimension";

	/** Dimension property name for use with property change events */
	public final static String TIME_PROPERTY = "movietime";

	/** Dimension property name for use with property change events */
	public final static String TRANSPARENCY_PROPERTY = "movietransparency";

	/** The unique id of this movie record.*/
	protected String 	sMoviePropertyID = "Unknown";

	/** The unique id of this movie record.*/
	protected String 	sMovieID = "";
	
	/** The x coordinates of the node associated with this object.*/
	protected int	 		nX				= -1;

	/** The y coordinates of the node associated with this object.*/
	protected int			nY				= -1;

	/** The width of the movie associated with this object.*/
	protected int	 		nWidth			= -1;

	/** The height of the movie associated with this object.*/
	protected int			nHeight			= -1;

	/** This determines how transparent to show the movie.*/
	protected float			fTransparency 	= 1.0f;
	
	/** The time at which these properties apply*/
	protected long			lTime			= 0;
	
	/** The date this object was created.*/
	protected Date			oCreationDate			= null;

	/** The date this object was last modified.*/
	protected Date			oModificationDate		= null;

	/**
	 * Constructor, creates a new Movie,
	 * defining the attributes of the given movie in the given view.
	 *
	 * @param sMoviePropertyID The unique id for this movie property record.
	 * @param sMovieID the id of the movie these are properties of.
	 * @param x The X coordinate of the movie's position in the view at the given time point.
	 * @param y The Y coordinate of the movie's position in the view.
	 * @param width The width of the movie.
	 * @param height The height of the movie.
	 * @param transparency the transparency level of the display of the movie.
	 * @param time the time at which these properties apply.
	 * @param dCreated the date this object was created.
	 * @param dModified the date this object was last modified.
	 
	 */
	public MovieProperties(String sMoviePropertyID, String sMovieID, int x, int y, 
						int width, int height, float transparency, long time, Date dCreated, Date dModified) {

		this.sMoviePropertyID = sMoviePropertyID;
		this.sMovieID = sMovieID;
		this.nX = x;
		this.nY = y;
		this.nWidth = width;
		this.nHeight = height;
		this.fTransparency = transparency;
		this.lTime = time;
		this.oCreationDate = dCreated;
		this.oModificationDate = dModified;
	}	

	/**
	 * Return a new Movie object with the properties of this movie.
	 */
	public MovieProperties getClone() {
		return new MovieProperties(sMoviePropertyID, sMovieID, nX, nY, nWidth, nHeight, fTransparency, lTime, oCreationDate, oModificationDate);
	}

	/**
	 * Return the unique id of this record.
	 * @return
	 */
	public String getId() {
		return sMoviePropertyID;
	}

	/**
	 * Return the movie id these are the properties for.
	 * @return
	 */
	public String getMovieID() {
		return sMovieID;
	}

	/**
	 * Set the id of the movie associated with this set of properties.
	 * @param sID
	 */
	public void setMovieID(String sID) {
		sMovieID = sID;
	}
	
	/**
	 * Sets The time at which these properties apply.
	 * and fires a PropertyChangeEvent.
	 *
	 * @param time the time at which these properties apply.
	 */
	public void setTime(long time) {
		long oldTime = lTime;
		this.lTime = time;
		firePropertyChange(TIME_PROPERTY, oldTime, time);
	}
	
	/**
	 * The time at which these properties apply.
	 * @return the time at which these properties apply.
	 */public long getTime() {
		return lTime;
	}
	
	/**
	 * Returns the X coordinate of the movie's position in the defined view.
	 *
	 * @return int, the X coordinate of the nodes position.
	 */
	public int getXPos() {
		 return nX ;
	}

	/**
	 * Sets the X coordinate of the movie's position in the defined view, in the local data ONLY.
	 * and fires a PropertyChangeEvent.
	 *
	 * @param int x, the X coordinate of the nodes position.
	 */
	public void setXPos(int x) {
		Point oldPoint = new Point(nX, nY);
		nX = x;
		firePropertyChange(POSITION_PROPERTY, oldPoint, new Point(nX, nY));
	}

	/**
	 * Returns the Y coordinate of the movie's position in the defined view.
	 *
	 * @return int, the Y coordinate of the nodes position.
	 */
	public int getYPos() {
		return nY ;
	}

	/**
	 * Sets the Y coordinate of the movie's position in the defined view, in the local data ONLY.
	 * and fires a PropertyChangeEvent.
	 *
	 * @param int y, the Y coordinate of the nodes position.
	 */
	public void setYPos(int y) {
		Point oldPoint = new Point(nX, nY);
		nY = y;
		firePropertyChange(POSITION_PROPERTY, oldPoint, new Point(nX, nY));
	}
	
	/**
	 * Sets the movie's position in the defined view, in the local data ONLY.
	 * and fires a PropertyChangeEvent.
	 *
	 * @param int x, the X coordinate of the node's position.
	 * @param int y, the Y coordinate of the node's position.
	 */
	public void setPos(int x, int y) {

		Point oldPoint = new Point(nX, nY);
		nX = x;
		nY = y;
		firePropertyChange(POSITION_PROPERTY, oldPoint, new Point(x, y));
	}

	/**
	 * Sets the movie's position in the defined view, in the local data ONLY.
	 *
	 * @param Point oPoint, The node's position.
	 */
	public void setPos(Point oPoint) {
		Point oldPoint = new Point(nX, nY);
		nX = oPoint.x;
		nY = oPoint.y;
		firePropertyChange(POSITION_PROPERTY, oldPoint, oPoint);
	}
	
	/**
	 * Returns the width of the movie in the defined view.
	 *
	 * @return width the width of the movie.
	 */
	public int getWidth() {
		 return nWidth;
	}

	/**
	 * Sets the width of the movie in the defined view, in the local data ONLY.
	 * and fires a PropertyChangeEvent.
	 *
	 * @param width the width of the movie.
	 */
	public void setWidth(int width) {
		Dimension oldDim = new Dimension(nWidth, nHeight);
		nWidth = width;
		firePropertyChange(DIMENSION_PROPERTY, oldDim, new Dimension(nWidth, nHeight));
	}

	/**
	 * Returns the height of the movie in the defined view.
	 *
	 * @return the height of the movie.
	 */
	public int getHeight() {
		return nHeight ;
	}

	/**
	 * Sets the height of the movie in the defined view, in the local data ONLY.
	 * and fires a PropertyChangeEvent.
	 *
	 * @param height the height of the movie.
	 */
	public void setHeight(int height) {
		Dimension oldDim = new Dimension(nWidth, nHeight);
		nHeight = height;
		firePropertyChange(DIMENSION_PROPERTY, oldDim, new Dimension(nWidth, nHeight));
	}	
		
	/**
	 * Return the transparency of this movie window when displaying.
	 * @return the transparency of the movie window when displaying at the associated time point.
	 */
	public float getTransparency() {
		return fTransparency;
	}
	
	/**
	 * Set the transparency of the movie at this point and fire a PropertyChangeEvent.
	 * @param value
	 */
	public void setTransparency(float value) {
		float oldTrans = fTransparency;
		fTransparency = value;
		firePropertyChange(DIMENSION_PROPERTY, oldTrans, fTransparency);
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
}
