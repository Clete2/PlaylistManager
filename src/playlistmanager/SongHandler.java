package playlistmanager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.farng.mp3.MP3File;
import org.farng.mp3.id3.ID3v1;

public class SongHandler {
	private static final Exception AlbumNotInDatabaseException = null;
	private static final Exception InsertFailedException = null;
	private static final Exception NotYetImplementedException = null;
	Connection dbConnection;
	Statement  dbStatement;

	private void prepareConnection() {
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

	private void closeConnection() {
		if(dbConnection != null) {
			try {
				dbConnection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void addSong(MP3File songToAdd) throws Exception {
		ID3v1 songTag = songToAdd.getID3v1Tag();
		if(dbConnection == null) {
			this.prepareConnection();
		}

		if(doesSongExistInDatabase(songToAdd)) {
			return;
		}
		if(!doesAlbumExistInDatabase(songToAdd)) {
			this.addAlbum(songToAdd);
		}

		String query = "INSERT INTO songs (song_title, song_artist," +
		" album_id, song_genre, song_rating, absolute_path)" +
		" VALUES ('" + this.getAlbumID(songToAdd) + "', '" +
		songTag.getGenre() + "', '', '" + 
		songToAdd.getMp3file().getAbsolutePath() + "')";

		if(!dbStatement.execute(query)) {
			throw InsertFailedException;
		}
		
		this.closeConnection();
	}

	private boolean doesSongExistInDatabase(MP3File songToAdd) {
		boolean found = false;
		if(dbConnection == null) {
			this.prepareConnection();
		}

		String query = "SELECT * FROM songs WHERE " +
		"absolute_path = '" + songToAdd.getMp3file()
		.getAbsolutePath() + "'";

		ResultSet rs;
		try {
			rs = dbStatement.executeQuery(query);
			if(rs.next()) {
				found = true;
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return found;
	}

	private boolean doesAlbumExistInDatabase(MP3File song) {
		boolean found = false;
		ID3v1 songTag = song.getID3v1Tag();
		if(dbConnection == null) {
			this.prepareConnection();
		}

		try {
			String query = "SELECT * FROM album WHERE " +
			"album_name = '" + songTag.getAlbumTitle() + "' AND " +
			"album_year = '" + songTag.getYearReleased() + "'";
			ResultSet rs = dbStatement.executeQuery(query);
			if(!rs.next()) {
				found = true;
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return found;
	}

	private int getAlbumID(MP3File song) throws Exception {
		ID3v1 songTag = song.getID3v1Tag();
		if(dbConnection == null) {
			this.prepareConnection();
		}

		String query = "SELECT album_id FROM album WHERE " +
		"album_name = '" + songTag.getAlbumTitle() + "' AND " +
		"album_yead = '" + songTag.getYearReleased() + "'";
		ResultSet rs;
		try {
			rs = dbStatement.executeQuery(query);
			if(!rs.next()) {
				throw AlbumNotInDatabaseException;
			} else {
				return Integer.parseInt(rs.getString("album_id"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	private void addAlbum(MP3File songToAdd) throws Exception {
		throw NotYetImplementedException;
	}
}
