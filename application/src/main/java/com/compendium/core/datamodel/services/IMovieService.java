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

package com.compendium.core.datamodel.services;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Vector;

import com.compendium.core.datamodel.Movie;
import com.compendium.core.datamodel.MovieProperties;
import com.compendium.core.datamodel.PCSession;


/**
 * The interface for the MovieService class
 * The MovieService class provides services to use the Movie table in the database.
 *
 * @author Michelle Bachler
 */
public interface IMovieService extends IService {
	
	/**
	 *  Add a new movie record in the database and returns a Movie object representing the record.
	 *
	 *  @param session the current session
	 *	@param sMovieID the id for this new movie record.
	 *	@param sViewID the id of the view to insert the movie record for.
	 *	@param sLink the path or url for the movie.
	 *  @param sName the user-friendly name for this movie.
	 *  @param starttime the time at which to start this movie.
	 *  @param props<MovieProperties> a list of the associated movie properties for this movie.
	 *	@return updated Movie object.
	 *	@throws java.sql.SQLException
	 */
	public Movie addMovie(PCSession session, String sMovieID, String sViewID, String sLink, 
			String sName, long startTime, Vector<MovieProperties> props) throws SQLException;

	/**
	 *  Add a new movie record in the database and returns a Movie object representing the record.
	 *
	 *  @param session the current session
	 *	@param sMoviePropertiesID the id for this new movie record.
	 *	@param sMovieID the id of the movie these are properties for.
	 *	@param x the x position of the movie in the view.
	 *  @param y the y position of the movie in the view.
	 *	@param width the width of the movie in the view.
	 *  @param height the height of the movie in the view.
	 *  @param transparency the transparency to display the movie at.
	 *  @param time the time at which these properties apply.
	 *	@return updated MovieProperties object.
	 *	@throws java.sql.SQLException
	 */
	public MovieProperties addMovieProperties(PCSession session, String sMoviePropertiesIS, String sMovieID,  
			int x, int y, int width, int height, float transparency, long time) throws SQLException;
	
	/**
	 *  Add a new movie record in the database and returns a Movie object representing the record.
	 *
	 *  @param session the current session
	 *	@param passedMovie the movie object who contents too add as a new movie record.
	 *	@return updated Movie object.
	 *	@throws java.sql.SQLException
	 */
	public Movie addMovie(PCSession session, Movie movie) throws SQLException;

	/**
	 *  Add a new movie properties record in the database and returns a MovieProperties object representing the record.
	 *
	 *  @param session the current session
	 *	@param passedMovie the movie properties object who contents too add as a new movie properties record.
	 *	@return updated MovieProperties object.
	 *	@throws java.sql.SQLException
	 */
	public MovieProperties addMovieProperties(PCSession session, MovieProperties movie) throws SQLException;

	/**
	 *  Update a movie record in the database and returns a Movie object representing the record.
	 *  Also update the movie properties.
	 *
	 *  @param session the current session
	 *	@param sMovieID the id for this new movie record.
	 *	@param sViewID the id of the view to insert the movie record for.
	 *	@param sLink the path or url for the movie.
	 *  @param sName the user-friendly name for this movie.
	 *  @param time the time to start the movie at
	 *	@return updated Movie object.
	 *	@throws java.sql.SQLException
	 */
	public Movie updateMovie(PCSession session, String sMovieID, String sViewID, String sLink, 
			String sName, long time) throws SQLException;

	/**
	 *  Update just a movie record in the database and returns a Movie object representing the record.
	 *
	 *  @param session the current session
	 *	@param sMovieID the id for this new movie record.
	 *	@param sViewID the id of the view to insert the movie record for.
	 *	@param sLink the path or url for the movie.
	 *  @param sName the user-friendly name for this movie.
	 *  @param time the time to start the movie at
	 *  @param props<MovieProperties> a list of the associated movie properties for this movie.
	 *	@return updated Movie object.
	 *	@throws java.sql.SQLException
	 */
	public Movie updateMovie(PCSession session, String sMovieID, String sViewID, String sLink, 
			String sName, long time, Vector<MovieProperties> props) throws SQLException;

	/**
	 *  Update a movie record in the database and returns a Movie object representing the record.
	 *
	 *  @param session the current session
	 *	@param sMoviePropertiesID the id for this new movie properties record.
	 *	@param sMovieID the id of the movie these are properties for
	 *	@param x the x position of the movie in the view.
	 *  @param y the y position of the movie in the view.
	 *	@param width the width of the movie in the view.
	 *  @param height the height of the movie in the view.
	 *  @param transparency the transparency to display the movie at.
	 *  @param time the time from which these properties apply.
	 *	@return updated Movie object.
	 *	@throws java.sql.SQLException
	 */
	public MovieProperties updateMovieProperties(PCSession session, String sMoviePropertiesID, String sMovieID, 
			int x, int y, int width, int height, float transparency, long time) throws SQLException;

	/**
	 * Delete the movie record with the given id.
	 * @param session the current session
	 * @param sMovieID the id of the movie record to delete.
	 */
	public void deleteMovie(PCSession session, String sMovieID) throws SQLException;

	/**
	 * Delete the movie properties record with the given id.
	 * @param session the current session
	 * @param sMoviePropertiesID the id of the movie properties record to delete.
	 */
	public void deleteMovieProperties(PCSession session, String sMoviePropertiesID) throws SQLException;

	/**
	 * Get the movie record with the given movie id.
	 * @param session the current session
	 * @param sMovieID the id of the movie record to get.
	 * @return Movie object representing the movie for the given id
	 * @throws IOException Occurs if there is an error writing the file.
	 */
	public Movie getMovie(PCSession session, String sMovieID) throws SQLException;

	/**
	 * Get the movie properties record with the given movie properties id.
	 * @param session the current session
	 * @param sMoviePropertiesID the id of the movie properties record to get.
	 * @return MovieProperties object representing the movie properties for the given id
	 * @throws IOException Occurs if there is an error writing the file.
	 */
	public MovieProperties getMovieProperties(PCSession session, String sMoviePropertiesID) throws SQLException;

	/**
	 * Get all the movies for the given view.
	 * @param session session the current session
	 * @return Vector with all Movies for the given view
	 */
	public Vector<Movie> getMovies(PCSession session, String sViewID) throws SQLException;	
	
	/**
	 * Get all the movie properties for the given movie id.
	 * @param session session the current session
	 * @return Vector with all MoviesProperties for the given movie
	 */
	public Vector<Movie> getAllMovieProperties(PCSession session, String sMovieID) throws SQLException;	
	
}
