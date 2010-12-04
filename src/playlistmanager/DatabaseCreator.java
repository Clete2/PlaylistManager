package playlistmanager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseCreator {
	private static Connection dbConnection;

	public static void createDatabase() {
		try {
			DatabaseCreator.prepareConnection();
			String query = "CREATE TABLE songs (song_id INTEGER PRIMARY KEY," +
			" song_title STRING, album_id INTEGER, song_rating INTEGER," +
			" absolute_path STRING)";
			PreparedStatement ps = dbConnection.prepareStatement(query);
			ps.executeUpdate();

			query = "CREATE TABLE album (album_id INTEGER PRIMARY KEY," +
			" album_name STRING, album_genre STRING, album_year INTEGER," +
			" artist_id INTEGER)";
			ps = dbConnection.prepareStatement(query);
			ps.executeUpdate();

			query = "CREATE TABLE artist (artist_id INTEGER PRIMARY KEY," +
			" artist_name STRING)";
			ps = dbConnection.prepareStatement(query);
			ps.executeUpdate();
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
