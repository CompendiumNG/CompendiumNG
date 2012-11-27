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
package com.compendium.core.db;

import java.util.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

import com.compendium.core.datamodel.*;
import com.compendium.core.db.management.*;

/**
 * The DBMovies class serves as the interface layer between the Movie objects
 * and the Movies table in the database.
 *
 * @author	Michelle Bachler
 */
public class DBMovies {

	public final static String INSERT_MOVIES_QUERY = DBConstants.INSERT_MOVIES_QUERY;

	public final static String INSERT_MOVIEPROPERTIES_QUERY = DBConstants.INSERT_MOVIEPROPERTIES_QUERY;

	public final static String UPDATE_MOVIE_QUERY =
		"UPDATE Movies "+
		"SET Link = ?, Name = ?, StartTime = ?, ModificationDate = ?"+
		" WHERE MovieID = ?";

	public final static String UPDATE_MOVIEPROPERTIES_QUERY =
		"UPDATE MovieProperties "+
		"SET MovieID = ?, XPos = ?, YPos = ?, Width = ?, Height = ?, Transparency = ?, Time = ?, ModificationDate = ?"+
		" WHERE MoviePropertyID = ?";

	public final static String DELETE_MOVIES_QUERY =
		"DELETE FROM Movies "+
		"WHERE MovieID = ?";

	public final static String DELETE_MOVIEPROPERTIES_QUERY =
		"DELETE FROM MovieProperties "+
		"WHERE MoviePropertyID = ?";

	public final static String GET_MOVIE_QUERY =
		"SELECT ViewID, Link, Name, StartTime, CreationDate, ModificationDate "+
		"FROM Movies "+
		"WHERE MovieID = ?";	

	public final static String GET_MOVIEPROPERTIES_QUERY =
		"SELECT MovieID, XPos, YPos, Width, Height, Transparency, Time, CreationDate, ModificationDate "+
		"FROM MovieProperties "+
		"WHERE MoviePropertyID = ?";	

	public final static String GET_MOVIES_QUERY =
		"SELECT MovieID, Link, Name, StartTime, CreationDate, ModificationDate "+
		"FROM Movies "+
		"WHERE ViewID = ?";	

	public final static String GET_ALLMOVIEPROPERTIES_QUERY =
		"SELECT MoviePropertyID, XPos, YPos, Width, Height, Transparency, Time, CreationDate, ModificationDate "+
		"FROM MovieProperties "+
		"WHERE MovieID = ?";	

	/**
	 *  Inserts a new movie record in the database and returns a Movie object representing this record.
	 *  NOTE: If the record already exists, it returns the existing record. 
	 *  If an import is being run and updating was set or if it is not an import, 
	 *  then the record is first updated with the passed data.
	 *  But it is not really recommended that this function is used for updating records as it would use unnecessary additional database calls.
	 *  Use 'update' directly.
	 *
	 *	@param dbcon the DBConnection object to access the database with.
	 *	@param sMovieID the id for this new movie record.
	 *	@param sViewID the id of the view to insert the movie record for.
	 *	@param sLink the path or url for the movie.
	 *  @param sName the user-friendly name for this movie.
	 *  @param startTime the time at which to start this movie.
	 *  @param props<MovieProperties> a list of the associated movie properties for this movie.
	 *	@return Movie object.
	 *	@throws java.sql.SQLException
	 */
	public static Movie insert(DBConnection dbcon, String sMovieID, String sViewID,  
				String sLink, String sName, long startTime, Vector<MovieProperties> props) throws SQLException  {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		// CHECK IF THIS RECORD ALREADY EXISTS FIRST
		Movie movie = getMovie(dbcon, sMovieID);
		if (movie != null) {
			if (!DBNode.getImporting() || (DBNode.getImporting() && DBNode.getUpdateTranscludedNodes()) ) {			
				movie = DBMovies.update(dbcon, sMovieID, sViewID, sLink, sName, startTime, props);
			} 
			return movie;
		}
			
		Date now = new Date();
		double time = now.getTime();
		
		PreparedStatement pstmt = con.prepareStatement(INSERT_MOVIES_QUERY);

		pstmt.setString(1, sMovieID);
		pstmt.setString(2, sViewID);
		pstmt.setString(3, sLink);
		pstmt.setDouble(4, time);
		pstmt.setDouble(5, time);
		pstmt.setString(6, sName);
		pstmt.setFloat(7, startTime);

		int nRowCount = pstmt.executeUpdate();

		pstmt.close();

		if (nRowCount > 0) {					
			int count = props.size();
			MovieProperties next = null;
			for (int i=0; i<count; i++) {
				next = props.elementAt(i);
				DBMovies.insertProperties(dbcon, next);
			}
			
			movie = new Movie(sMovieID, sViewID, sLink, sName, startTime, now, now, props);
			if (DBAudit.getAuditOn()) {
				DBAudit.auditMovie(dbcon, DBAudit.ACTION_ADD, movie);
			}
		}

		return movie;
	}

	/**
	 *  Inserts a new movie property record in the database and returns a MovieProperty object representing this record.
	 *  NOTE: If the record already exists, it returns the existing record. 
	 *  If an import is being run and updating was set or if it is not an import, 
	 *  then the record is first updated with the passed data.
	 *  But it is not really recommended that this function is used for updating records as it would use unnecessary additional database calls.
	 *  Use 'updateProperties' directly.
	 *
	 *	@param dbcon the DBConnection object to access the database with.
	 *	@param sMoviePropertyID the id of the movie properties record.
	 *	@param sMovieID the id for the movie this is a property record for.
	 *	@param x the x position of the movie in the view.
	 *  @param y the y position of the movie in the view.
	 *	@param width the width of the movie in the view.
	 *  @param height the height of the movie in the view.
	 *  @param fTransparency the transparency level of the display of the movie.
	 *  @param startTime the time from which these properties apply.
	 *	@return MovieProperties object.
	 *	@throws java.sql.SQLException
	 */
	public static MovieProperties insertProperties(DBConnection dbcon, String sMoviePropertyID, String sMovieID,  
				int x, int y, int width, int height, float fTransparency, long startTime) throws SQLException  {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		// CHECK IF THIS RECORD ALREADY EXISTS FIRST
		MovieProperties movieprops = getMovieProperties(dbcon, sMoviePropertyID);
		if (movieprops != null) {
			if (!DBNode.getImporting() || (DBNode.getImporting() && DBNode.getUpdateTranscludedNodes()) ) {			
				movieprops = DBMovies.updateProperties(dbcon, sMoviePropertyID, sMovieID, x, y, width, height, fTransparency, startTime);
			} 
			return movieprops;
		}
			
		Date now = new Date();
		double time = now.getTime();
		
		PreparedStatement pstmt = con.prepareStatement(INSERT_MOVIEPROPERTIES_QUERY);

		pstmt.setString(1, sMoviePropertyID);
		pstmt.setString(2, sMovieID);
		pstmt.setInt(3, x);		
		pstmt.setInt(4, y);
		pstmt.setInt(5, width);		
		pstmt.setInt(6, height);
		pstmt.setFloat(7, fTransparency);
		pstmt.setDouble(8, startTime);
		pstmt.setDouble(9, time);
		pstmt.setDouble(10, time);

		int nRowCount = pstmt.executeUpdate();

		pstmt.close();

		if (nRowCount > 0) {
			movieprops = new MovieProperties(sMoviePropertyID, sMovieID, x, y, width, height, fTransparency, startTime, now, now);
			if (DBAudit.getAuditOn()) {
				DBAudit.auditMovieProperties(dbcon, DBAudit.ACTION_ADD, movieprops);
			}
		}

		return movieprops;
	}
	
	/**
	 *  Inserts a new movie record in the database and returns a Movie object representing this record.
	 *  NOTE: If the record already exists, it returns the existing record. 
	 *  If an import is being run and updating was set or if it is not an import, 
	 *  then the record is first updated with the passed data.
	 *  But it is not really recommended that this function is used for updating records as it would use unnecessary additional database calls.
	 *  Use 'update' directly.
	 *
	 *	@param dbcon the DBConnection object to access the database with.
	 *	@param passedMovie the movie object to store.
	 *	@return Movie object.
	 *	@throws java.sql.SQLException
	 */
	public static Movie insert(DBConnection dbcon, Movie passedMovie) throws SQLException  {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		// CHECK IF THIS RECORD ALREADY EXISTS FIRST
		Movie movie = getMovie(dbcon, passedMovie.getId());
		if (movie != null) {
			if (!DBNode.getImporting() || (DBNode.getImporting() && DBNode.getUpdateTranscludedNodes()) ) {			
				movie = DBMovies.update(dbcon, passedMovie.getId(), passedMovie.getViewID(), passedMovie.getLink(), passedMovie.getMovieName(), passedMovie.getStartTime(), passedMovie.getProperties());
			} 
			return movie;
		}

		PreparedStatement pstmt = con.prepareStatement(INSERT_MOVIES_QUERY);

		pstmt.setString(1, passedMovie.getId());
		pstmt.setString(2, passedMovie.getViewID());
		pstmt.setString(3, passedMovie.getLink());
		pstmt.setDouble(4, passedMovie.getCreationDate().getTime());
		pstmt.setDouble(5, passedMovie.getModificationDate().getTime());
		pstmt.setString(6, passedMovie.getMovieName());
		pstmt.setDouble(7, passedMovie.getStartTime());

		int nRowCount = pstmt.executeUpdate();

		pstmt.close();

		if (nRowCount > 0) {
			Vector<MovieProperties> props = passedMovie.getProperties();
			int count = props.size();
			MovieProperties next = null;
			for (int i=0; i<count; i++) {
				next = props.elementAt(i);
				DBMovies.insertProperties(dbcon, next);
			}
			
			movie = passedMovie;
			if (DBAudit.getAuditOn()) {
				DBAudit.auditMovie(dbcon, DBAudit.ACTION_ADD, passedMovie);
			}
		}

		return movie;
	}
	
	/**
	 *  Inserts a new movie record in the database and returns a MovieProperties object representing this record.
	 *  NOTE: If the record already exists, it returns the existing record. 
	 *  If an import is being run and updating was set or if it is not an import, 
	 *  then the record is first updated with the passed data.
	 *  But it is not really recommended that this function is used for updating records as it would use unnecessary additional database calls.
	 *  Use 'updateProperties' directly.
	 *
	 *	@param dbcon the DBConnection object to access the database with.
	 *	@param passedMovie the movieproperties object to store.
	 *	@return MovieProperties object.
	 *	@throws java.sql.SQLException
	 */
	public static MovieProperties insertProperties(DBConnection dbcon, MovieProperties passedMovie) throws SQLException  {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		// CHECK IF THIS RECORD ALREADY EXISTS FIRST
		MovieProperties movieprops = getMovieProperties(dbcon, passedMovie.getId());
		if (movieprops != null) {
			if (!DBNode.getImporting() || (DBNode.getImporting() && DBNode.getUpdateTranscludedNodes()) ) {			
				movieprops = DBMovies.updateProperties(dbcon, passedMovie.getId(), passedMovie.getMovieID(), passedMovie.getXPos(), passedMovie.getYPos(), passedMovie.getWidth(), passedMovie.getHeight(), passedMovie.getTransparency(), passedMovie.getTime());
			} 
			return movieprops;
		}

		PreparedStatement pstmt = con.prepareStatement(INSERT_MOVIEPROPERTIES_QUERY);

		pstmt.setString(1, passedMovie.getId());
		pstmt.setString(2, passedMovie.getMovieID());
		pstmt.setInt(3, passedMovie.getXPos());		
		pstmt.setInt(4, passedMovie.getYPos());
		pstmt.setInt(5, passedMovie.getWidth());		
		pstmt.setInt(6, passedMovie.getHeight());
		pstmt.setFloat(7, passedMovie.getTransparency());
		pstmt.setDouble(8, passedMovie.getTime());
		pstmt.setDouble(9, passedMovie.getCreationDate().getTime());
		pstmt.setDouble(10, passedMovie.getModificationDate().getTime());

		int nRowCount = pstmt.executeUpdate();

		pstmt.close();

		if (nRowCount > 0) {
			if (DBAudit.getAuditOn()) {
				DBAudit.auditMovieProperties(dbcon, DBAudit.ACTION_ADD, movieprops);
			}
		}

		return movieprops;
	}
	
	/**
	 *  Updates a movie record with the given id in the database and returns the updated Movie object representing this record.
	 *  Also update the MovieProperties in the database.
	 *
	 *	@param dbcon the DBConnection object to access the database with.
	 *	@param sMovieID the id for this new movie record.
	 *	@param sViewID the id of the view to insert the movie record for.
	 *	@param sLink the path or url for the movie.
	 *  @param sName the user-friendly name for this movie.
	 *  @param startTime the time to start this movie.
	 *  @param props<MovieProperties> a list of the associated movie properties for this movie.
	 *	@return updated Movie object.
	 *	@throws java.sql.SQLException
	 */
	public static Movie update(DBConnection dbcon, String sMovieID, String sViewID,  
			String sLink, String sName, long startTime, Vector<MovieProperties> props) throws SQLException  {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(UPDATE_MOVIE_QUERY);

		double time = new Date().getTime();
		
		pstmt.setString(1, sLink);
		pstmt.setString(2, sName);
		pstmt.setDouble(3, startTime);
		pstmt.setDouble(4, time);
		pstmt.setString(5, sMovieID);

		int nRowCount = pstmt.executeUpdate();

		pstmt.close();

		Movie movie = null;
		if (nRowCount > 0) {
			int count = props.size();
			MovieProperties next = null;
			for (int i=0; i<count; i++) {
				next = props.elementAt(i);
				DBMovies.updateProperties(dbcon, next.getId(), next.getMovieID(), next.getXPos(), next.getYPos(), next.getWidth(), next.getHeight(), next.getTransparency(), next.getTime());
			}
			
			movie = DBMovies.getMovie(dbcon, sMovieID);
			if (DBAudit.getAuditOn()) {
				DBAudit.auditMovie(dbcon, DBAudit.ACTION_EDIT, movie);
			}
		}

		return movie;
	}
	
	/**
	 *  Updates just a movie record with the given id in the database and returns the updated Movie object representing this record.
	 *
	 *	@param dbcon the DBConnection object to access the database with.
	 *	@param sMovieID the id for this new movie record.
	 *	@param sViewID the id of the view to insert the movie record for.
	 *	@param sLink the path or url for the movie.
	 *  @param sName the user-friendly name for this movie.
	 *  @param startTime the time to start this movie.
	 *	@return updated Movie object.
	 *	@throws java.sql.SQLException
	 */
	public static Movie update(DBConnection dbcon, String sMovieID, String sViewID,  
			String sLink, String sName, long startTime) throws SQLException  {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(UPDATE_MOVIE_QUERY);

		double time = new Date().getTime();
		
		pstmt.setString(1, sLink);
		pstmt.setString(2, sName);
		pstmt.setDouble(3, startTime);
		pstmt.setDouble(4, time);
		pstmt.setString(5, sMovieID);

		int nRowCount = pstmt.executeUpdate();

		pstmt.close();

		Movie movie = null;
		if (nRowCount > 0) {
			movie = DBMovies.getMovie(dbcon, sMovieID);
			if (DBAudit.getAuditOn()) {
				DBAudit.auditMovie(dbcon, DBAudit.ACTION_EDIT, movie);
			}
		}

		return movie;
	}
	
	/**
	 *  Updates a movieproperties record with the given id in the database and returns the updated MovieProperties object representing this record.
	 *
	 *	@param dbcon the DBConnection object to access the database with.
	 *	@param sMoviePropertyID the id for this new movieproperties record.
	 *	@param sMovieID the id of the movie these are properties for
	 *	@param x the x position of the movie in the view.
	 *  @param y the y position of the movie in the view.
	 *	@param width the width of the movie in the view.
	 *  @param height the height of the movie in the view.
	 *  @param fTransparency the transparency level of the display of the movie.
	 *  @param startTime the time from which these properties apply.
	 *	@return updated Movie object.
	 *	@throws java.sql.SQLException
	 */
	public static MovieProperties updateProperties(DBConnection dbcon, String sMoviePropertyID, String sMovieID,  
			int x, int y, int width, int height, float fTransparency, long startTime) throws SQLException  {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(UPDATE_MOVIEPROPERTIES_QUERY);

		double time = new Date().getTime();
		
		pstmt.setString(1, sMovieID);
		pstmt.setInt(2, x);
		pstmt.setInt(3, y);
		pstmt.setInt(4, width);
		pstmt.setInt(5, height);
		pstmt.setFloat(6, fTransparency);
		pstmt.setDouble(7, startTime);
		pstmt.setDouble(8, time);
		pstmt.setString(9, sMoviePropertyID);

		int nRowCount = pstmt.executeUpdate();

		pstmt.close();

		MovieProperties movie = null;
		if (nRowCount > 0) {
			movie = DBMovies.getMovieProperties(dbcon, sMoviePropertyID);
			if (DBAudit.getAuditOn()) {
				DBAudit.auditMovieProperties(dbcon, DBAudit.ACTION_EDIT, movie);
			}
		}

		return movie;
	}
	
	/**
	 *	Delete a Movies record (and its associated MovieProperty Records - CASCADE DELETE)
	 *
	 *	@param dbcon the DBConnection object to access the database with.
	 *	@param sMovieID the id of the record to delete.
	 *	@throws java.sql.SQLException
	 */
	public static void delete(DBConnection dbcon, String sMovieID) throws SQLException	{

		Connection con = dbcon.getConnection() ;
		if (con == null)
			throw new SQLException("Connection is null");

		PreparedStatement pstmt = con.prepareStatement(DELETE_MOVIES_QUERY) ;
		pstmt.setString(1, sMovieID);
		pstmt.executeUpdate();
		pstmt.close();
	}
	
	/**
	 *	Delete a MoviesProperties record.
	 *
	 *	@param dbcon the DBConnection object to access the database with.
	 *	@param sMoviePropertiesID the id of the record to delete.
	 *	@throws java.sql.SQLException
	 */
	public static void deleteProperties(DBConnection dbcon, String sMoviePropertyID) throws SQLException	{

		Connection con = dbcon.getConnection() ;
		if (con == null)
			throw new SQLException("Connection is null");

		PreparedStatement pstmt = con.prepareStatement(DELETE_MOVIEPROPERTIES_QUERY) ;
		pstmt.setString(1, sMoviePropertyID);
		pstmt.executeUpdate();
		pstmt.close();
	}

	/**
	 *	Returns the Movie for the given movieid.
	 *
	 *	@param dbcon the DBConnection object to access the database with.
	 *	@param sMovieID, the id of the mobie to return.
	 *	@return Movie instance or null
	 *	@throws java.sql.SQLException
	 */
	public static Movie getMovie(DBConnection dbcon, String sMovieID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_MOVIE_QUERY);

		pstmt.setString(1, sMovieID);

		ResultSet rs = null;
		
		try {
			rs = pstmt.executeQuery();
		} catch (Exception e){
			e.printStackTrace();
		}

		Movie movie = null;
		if (rs != null) {
			while (rs.next()) {
				String	sViewID		= rs.getString(1);
				String	sLink		= rs.getString(2);
				String	name 		= rs.getString(3);
				long	startTime	= rs.getLong(4);
				Date	created		= new Date(new Double(rs.getLong(5)).longValue());
				Date	modified	= new Date(new Double(rs.getLong(6)).longValue());
				
				Vector<MovieProperties> props = DBMovies.getAllMovieProperties(dbcon, sMovieID);
				movie = new Movie(sMovieID, sViewID, sLink, name, startTime, created, modified, props);
			}
			rs.close();
		}

		pstmt.close();
		return movie;
	}
	

	/**
	 *	Returns the MovieProperties for the given moviepropertyid.
	 *
	 *	@param dbcon the DBConnection object to access the database with.
	 *	@param sMoviePropertiesID the id of the movieproperties record to return.
	 *	@return MovieProperties instance or null
	 *	@throws java.sql.SQLException
	 */
	public static MovieProperties getMovieProperties(DBConnection dbcon, String sMoviePropertiesID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_MOVIEPROPERTIES_QUERY);

		pstmt.setString(1, sMoviePropertiesID);

		ResultSet rs = null;
		
		try {
			rs = pstmt.executeQuery();
		} catch (Exception e){
			e.printStackTrace();
		}

		MovieProperties movie = null;
		if (rs != null) {
			while (rs.next()) {
				String	sMovieID	= rs.getString(1);
				int		nX			= rs.getInt(2);
				int		nY			= rs.getInt(3);
				int		nWidth		= rs.getInt(4);
				int		nHeight		= rs.getInt(5);				
				float	fTransparency = rs.getFloat(6);
				long	time		= rs.getLong(7);
				Date	created		= new Date(new Double(rs.getLong(8)).longValue());
				Date	modified	= new Date(new Double(rs.getLong(9)).longValue());
				
				movie = new MovieProperties(sMoviePropertiesID, sMovieID, nX, nY, nWidth, nHeight, fTransparency, time, created, modified);
			}
		}

		pstmt.close();
		return movie;
	}
	
	/**
	 *	Returns the array of Movie objects in the given view.
	 *
	 *	@param dbcon the DBConnection object to access the database with.
	 *	@param sViewID the id of the View to return the Movies for.
	 *	@return Vector a list of <code>Movie</code> objects, or an empty list.
	 *	@throws java.sql.SQLException
	 */
	public static Vector getMovies(DBConnection dbcon, String sViewID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			throw new SQLException("Connection null");

		PreparedStatement pstmt = con.prepareStatement(GET_MOVIES_QUERY);
		pstmt.setString(1, sViewID);

		ResultSet rs = null;
		
		try {
			rs = pstmt.executeQuery(); 
		} catch (Exception e){
			e.printStackTrace();
		}

		Vector vtMovies = new Vector(51);
		Movie movie = null;
		if (rs != null) {
			while (rs.next()) {
				String	sMovieID	= rs.getString(1);
				String	sLink		= rs.getString(2);
				String	name		= rs.getString(3);
				long	time		= rs.getLong(4);
				Date	created		= new Date(new Double(rs.getLong(5)).longValue());
				Date	modified	= new Date(new Double(rs.getLong(6)).longValue());
				
				Vector<MovieProperties> props = DBMovies.getAllMovieProperties(dbcon, sMovieID);
				movie = new Movie(sMovieID, sViewID, sLink, name, time, created, modified, props);

				vtMovies.addElement(movie);
			}
		}

		pstmt.close();
		return vtMovies;
	}
	
	/**
	 *	Returns the array of MovieProperties objects in the given movie id.
	 *
	 *	@param dbcon the DBConnection object to access the database with.
	 *	@param sMovieID the id of the Movie to return the MovieProperties for.
	 *	@return Vector a list of <code>MovieProperties</code> objects, or an empty list.
	 *	@throws java.sql.SQLException
	 */
	public static Vector<MovieProperties> getAllMovieProperties(DBConnection dbcon, String sMovieID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			throw new SQLException("Connection null");

		PreparedStatement pstmt = con.prepareStatement(GET_ALLMOVIEPROPERTIES_QUERY);
		pstmt.setString(1, sMovieID);

		ResultSet rs = null;
		
		try {
			rs = pstmt.executeQuery(); 
		} catch (Exception e){
			e.printStackTrace();
		}

		Vector<MovieProperties> vtMovies = new Vector<MovieProperties>(51);
		MovieProperties movie = null;
		if (rs != null) {
			while (rs.next()) {
				String	sMoviePropertiesID	= rs.getString(1);
				int		nX			= rs.getInt(2);
				int		nY			= rs.getInt(3);
				int		nWidth		= rs.getInt(4);
				int		nHeight		= rs.getInt(5);				
				float	fTransparency = rs.getFloat(6);
				long 	time		= rs.getLong(7);
				Date	created		= new Date(new Double(rs.getLong(8)).longValue());
				Date	modified	= new Date(new Double(rs.getLong(9)).longValue());
				
				movie = new MovieProperties(sMoviePropertiesID, sMovieID, nX, nY, nWidth, nHeight, fTransparency, time, created, modified);

				vtMovies.add(movie);
			}
		}

		pstmt.close();
		return vtMovies;
	}	
}
