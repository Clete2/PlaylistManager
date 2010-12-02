package playlistmanager;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

public class SongHandler {
	private static final Exception AlbumNotInDatabaseException = null;
	private static final Exception ArtistNotInDatabaseException = null;
	private Connection dbConnection;

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

	public void addSong(File songToAdd) throws Exception {
		AudioFile audioFile = AudioFileIO.read(songToAdd);
		Tag songTag = audioFile.getTag();
		
		if(dbConnection == null) {
			this.prepareConnection();
		}

		if(doesSongExist(songToAdd)) {
			return;
		}
		if(!doesAlbumExist(songToAdd)) {
			this.addAlbum(songToAdd);
		}

		String query = "INSERT INTO songs (song_title," +
		" album_id, song_genre, song_rating, absolute_path)" +
		" VALUES (?, ?, ?, null, ?)";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setString(1, songTag.getFirst(FieldKey.TITLE));
		ps.setInt(2, this.getAlbumID(songToAdd));
		ps.setString(3, songTag.getFirst(FieldKey.GENRE));
		ps.setString(4, songToAdd.getAbsolutePath());
		ps.executeUpdate();
		this.closeConnection();
	}

	private boolean doesSongExist(File songToAdd) {
		boolean found = false;
		if(dbConnection == null) {
			this.prepareConnection();
		}

		try {
			String query = "SELECT * FROM songs WHERE " +
			"absolute_path = ?";
			PreparedStatement ps = dbConnection.prepareStatement(query);
			ps.setString(1, songToAdd.getAbsolutePath());
			ResultSet rs = ps.executeQuery();
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

	private boolean doesAlbumExist(File song) throws Exception {
		boolean found = false;
		AudioFile audioFile = AudioFileIO.read(song);
		Tag songTag = audioFile.getTag();
		if(dbConnection == null) {
			this.prepareConnection();
		}

		try {
			String query = "SELECT * FROM album WHERE album_name = ? AND" +
					" album_year = ?";
			PreparedStatement ps = dbConnection.prepareStatement(query);
			ps.setString(1, songTag.getFirst(FieldKey.ALBUM));
			ps.setString(2, songTag.getFirst(FieldKey.YEAR));
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				found = true;
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return found;
	}

	private boolean doesArtistExist(File song) throws Exception {
		boolean found = false;
		AudioFile audioFile = AudioFileIO.read(song);
		Tag songTag = audioFile.getTag();
		if(dbConnection == null) {
			this.prepareConnection();
		}

		try {
			String query = "SELECT * FROM artist WHERE " +
			"artist_name = ?";
			PreparedStatement ps = dbConnection.prepareStatement(query);
			ps.setString(1, songTag.getFirst(FieldKey.ARTIST));
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				found = true;
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return found;
	}

	private int getAlbumID(File song) throws Exception {
		AudioFile audioFile = AudioFileIO.read(song);
		Tag songTag = audioFile.getTag();
		if(dbConnection == null) {
			this.prepareConnection();
		}

		try {
			String query = "SELECT album_id FROM album WHERE " +
			"album_name = ? AND album_year = ?";
			PreparedStatement ps = dbConnection.prepareStatement(query);
			ps.setString(1, songTag.getFirst(FieldKey.ALBUM));
			ps.setString(2, songTag.getFirst(FieldKey.YEAR));
			ResultSet rs = ps.executeQuery();
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
	
	private int getArtistID(File song) throws Exception {
		AudioFile audioFile = AudioFileIO.read(song);
		Tag songTag = audioFile.getTag();
		if(dbConnection == null) {
			this.prepareConnection();
		}

		try {
			String query = "SELECT artist_id FROM artist WHERE " +
			"artist_name = ?";
			PreparedStatement ps = dbConnection.prepareStatement(query);
			ps.setString(1, songTag.getFirst(FieldKey.ARTIST));
			ResultSet rs = ps.executeQuery();
			if(!rs.next()) {
				throw ArtistNotInDatabaseException;
			} else {
				return Integer.parseInt(rs.getString("artist_id"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	private void addAlbum(File songToAdd) throws Exception {
		AudioFile audioFile = AudioFileIO.read(songToAdd);
		Tag songTag = audioFile.getTag();
		if(dbConnection == null) {
			this.prepareConnection();
		}
		
		if(!doesArtistExist(songToAdd)) {
			this.addArtist(songToAdd);
		}
		String query = "INSERT INTO album (album_name, album_year, artist_id)" +
		" VALUES (?, ?, ?)";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setString(1, songTag.getFirst(FieldKey.ALBUM));
		ps.setString(2, songTag.getFirst(FieldKey.YEAR));
		ps.setInt(3, this.getArtistID(songToAdd));		
		ps.executeUpdate();
	}

	private void addArtist(File songToAdd) throws Exception {
		AudioFile audioFile = AudioFileIO.read(songToAdd);
		Tag songTag = audioFile.getTag();
		if(dbConnection == null) {
			this.prepareConnection();
		}
		
		String query = "INSERT INTO artist (artist_name) VALUES (?)";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setString(1, songTag.getFirst(FieldKey.ARTIST));
		ps.executeUpdate();
	}
}
