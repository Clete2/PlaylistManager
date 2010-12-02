package playlistmanager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseCreator {
	private static Connection dbConnection;
	private static Statement  dbStatement;

	public static void createDatabase() {
		try {
			DatabaseCreator.prepareConnection();
			String query = "CREATE TABLE songs (song_id INTEGER PRIMARY KEY," +
			" song_title STRING, song_artist STRING, album_id INTEGER," +
			" song_genre STRING, song_rating INTEGER, absolute_path STRING)";
			dbStatement.executeUpdate(query);
			
			query = "CREATE TABLE album (album_id INTEGER PRIMARY KEY," +
			" album_name STRING, album_year INTEGER, artist_id INTEGER)";
			dbStatement.executeUpdate(query);
			
			query = "CREATE TABLE artist (artist_id INTEGER PRIMARY KEY," +
			" artist_first_name STRING, artist_last_name STRING)";
			dbStatement.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		DatabaseCreator.closeConnection();
	}

	private static void prepareConnection() {
		// Load SQLite JDBC driver.
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		try {
			dbConnection = DriverManager.getConnection("jdbc:sqlite:" +
					PlaylistVariables.getFullDBName());
			dbStatement = dbConnection.createStatement();
			dbStatement.setQueryTimeout(30); // 30 second timeout.
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void closeConnection() {
		if(dbConnection != null) {
			try {
				dbConnection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
