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

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Vector;

import com.compendium.core.datamodel.Movie;
import com.compendium.core.datamodel.MovieProperties;
import com.compendium.core.datamodel.PCSession;
import com.compendium.core.db.DBMovies;
import com.compendium.core.db.management.DBConnection;
import com.compendium.core.db.management.DBDatabaseManager;

/**
 * A service which provides access to the data from the linked files
 * table of the database
 * 
 * @author Michelle Bachler
 */
public class MovieService extends ClientService implements
		IMovieService, Serializable {

	/**
	 *	Constructor.
	 */
	public  MovieService() {
		super();
	}

	/**
	 * Constructor, set the name of the service.
	 *
	 * @param sName the name of this service.
	 */
	public MovieService(String sName) {
		super(sName);
	}

	/**
	 * Constructor.
	 *
	 * @param name the unique name of this service
 	 * @param sm the current ServiceManager
	 * @param dbMgr the current DBDatabaseManager
	 */
	public  MovieService(String name, ServiceManager sm, DBDatabaseManager dbMgr) {
		super(name, sm, dbMgr) ;
	}	
		
	/**
	 * {@inheritDoc}
	 */
	public Movie addMovie(PCSession session, String sMovieID, String sViewID, String sLink, 
			String sName, long startTime, Vector<MovieProperties> props) throws SQLException {
		
		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;
		Movie movie = DBMovies.insert(dbcon, sMovieID, sViewID, sLink, sName, startTime, props);
		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);
		return movie;
	}

	/**
	 * {@inheritDoc}
	 */
	public MovieProperties addMovieProperties(PCSession session, String sMoviePropertiesIS, String sMovieID, 			
			int x, int y, int width, int height, float transparency, long time) throws SQLException {
		
		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;
		MovieProperties movie = DBMovies.insertProperties(dbcon, sMoviePropertiesIS, sMovieID, x, y, width, height, transparency, time);
		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);
		return movie;

	}
	
	/**
	 * {@inheritDoc}
	 */
	public Movie addMovie(PCSession session, Movie passedMovie) throws SQLException {
		
		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;
		Movie movie = DBMovies.insert(dbcon, passedMovie);
		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);
		return movie;
	}

	/**
	 * {@inheritDoc}
	 */
	public MovieProperties addMovieProperties(PCSession session, MovieProperties movie) throws SQLException {
		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;
		MovieProperties movieprops = DBMovies.insertProperties(dbcon, movie);
		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);
		return movieprops;
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Movie updateMovie(PCSession session, String sMovieID, String sViewID, String sLink, 
			String sName, long startTime, Vector<MovieProperties> props) throws SQLException {
		
		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;
		Movie movie = DBMovies.update(dbcon, sMovieID, sViewID, sLink, sName, startTime, props);
		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);
		return movie;		
	}

	/**
	 * {@inheritDoc}
	 */
	public Movie updateMovie(PCSession session, String sMovieID, String sViewID, String sLink, 
			String sName, long startTime) throws SQLException {
		
		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;
		Movie movie = DBMovies.update(dbcon, sMovieID, sViewID, sLink, sName, startTime);
		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);
		return movie;		
	}

	/**
	 * {@inheritDoc}
	 */
	public MovieProperties updateMovieProperties(PCSession session, String sMoviePropertiesID, String sMovieID, 
			int x, int y, int width, int height, float transparency, long time) throws SQLException {
		
		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;
		MovieProperties movie = DBMovies.updateProperties(dbcon, sMoviePropertiesID, sMovieID, x, y, width, height, transparency, time);
		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);
		return movie;		
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void deleteMovie(PCSession session, String sMovieID) throws SQLException {
		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;
		DBMovies.delete(dbcon, sMovieID);
		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);
	}

	/**
	 * {@inheritDoc}
	 */
	public void deleteMovieProperties(PCSession session, String sMoviePropertiesID) throws SQLException {
		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;
		DBMovies.deleteProperties(dbcon, sMoviePropertiesID);
		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);
	}

	/**
	 * {@inheritDoc}
	 */
	public Movie getMovie(PCSession session, String sMovieID) throws SQLException {
		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;
		Movie movie = DBMovies.getMovie(dbcon, sMovieID);
		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);
		return movie;		
	}

	/**
	 * {@inheritDoc}
	 */
	public MovieProperties getMovieProperties(PCSession session, String sMoviePropertiesID) throws SQLException {
		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;
		MovieProperties movie = DBMovies.getMovieProperties(dbcon, sMoviePropertiesID);
		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);
		return movie;		
	}

	/**
	 * {@inheritDoc}
	 */
	public Vector<Movie> getMovies(PCSession session, String sViewID) throws SQLException {
		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;
		Vector movies = DBMovies.getMovies(dbcon, sViewID);
		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);
		return movies;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Vector<Movie> getAllMovieProperties(PCSession session, String sMovieID) throws SQLException {
		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;
		Vector movies = DBMovies.getAllMovieProperties(dbcon, sMovieID);
		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);
		return movies;
	}
	
}
