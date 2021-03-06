package playlistmanager;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

/**
 * SongHandler does most of the database logic.
 * It includes adding songs, albums, and artists;
 * it also searches the database.
 */
public class SongHandler {
	private static final Exception AlbumNotInDatabaseException = null;
	private static final Exception ArtistNotInDatabaseException = null;
	private static final Exception InvalidRatingException = null;
	private static final Exception SongNotInDatabaseException = null;
	private Connection dbConnection;

	/**
	 * Initialize SQLite connection.
	 */
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

	/**
	 * Stop SQLite connection.
	 */
	private void closeConnection() {
		if(dbConnection != null) {
			try {
				dbConnection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Add a song to the database. Will not add if duplicate.
	 * @param songToAdd File to add to the database.
	 * @throws Exception 
	 */
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

		String query = "INSERT INTO songs" +
		" (song_title, album_id, song_rating, absolute_path, song_length)" +
		" VALUES (?, ?, null, ?, ?)";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setString(1, songTag.getFirst(FieldKey.TITLE));
		ps.setInt(2, this.getAlbumID(songToAdd));
		ps.setString(3, songToAdd.getAbsolutePath());
		ps.setInt(4, audioFile.getAudioHeader().getTrackLength());
		ps.executeUpdate();
		this.closeConnection();
	}

	/**
	 * Checks to see if a specified song exists in the database.
	 * @param songToAdd Song to check for existence. Checks by song path.
	 * @return True if the song exists; false otherwise.
	 */
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

	/**
	 * Checks to see if a specified song exists in the database.
	 * @param songID Song by songID.
	 * @return True if the song exists; false otherwise.
	 */
	private boolean doesSongExist(int songID) {
		boolean found = false;
		if(dbConnection == null) {
			this.prepareConnection();
		}

		try {
			String query = "SELECT * FROM songs WHERE " +
			"song_id = ?";
			PreparedStatement ps = dbConnection.prepareStatement(query);
			ps.setInt(1, songID);
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

	/**
	 * Checks to see if the specified album exists.
	 * @param song File containing the song with ID3
	 * tag containing the album information.
	 * @return True if the album exists, false otherwise.
	 * @throws Exception
	 */
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

	/**
	 * Checks to see if the specified artist exists.
	 * @param song File containing the song with ID3
	 * tag containing the artist information.
	 * @return True if the artist exists, false otherwise.
	 * @throws Exception
	 */
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

	/**
	 * Gets the album ID for the specified song.
	 * @param song
	 * @return 
	 * @throws Exception
	 */
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

	/**
	 * Gets the artist ID for the specified song.
	 * @param song
	 * @return
	 * @throws Exception
	 */
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

	/**
	 * Adds an album to the database.
	 * @param songToAdd Song containing ID3 tag with
	 * album information.
	 * @throws Exception
	 */
	private void addAlbum(File songToAdd) throws Exception {
		AudioFile audioFile = AudioFileIO.read(songToAdd);
		Tag songTag = audioFile.getTag();
		if(dbConnection == null) {
			this.prepareConnection();
		}

		if(!doesArtistExist(songToAdd)) {
			this.addArtist(songToAdd);
		}
		String query = "INSERT INTO album (album_name, album_year, " +
		"album_genre, artist_id) VALUES (?, ?, ?, ?)";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setString(1, songTag.getFirst(FieldKey.ALBUM));
		ps.setString(2, songTag.getFirst(FieldKey.YEAR));
		ps.setString(3, songTag.getFirst(FieldKey.GENRE));
		ps.setInt(4, this.getArtistID(songToAdd));		
		ps.executeUpdate();
	}

	/**
	 * Adds the specified artist to the database.
	 * @param songToAdd File with ID3 tag containing
	 * artist information.
	 * @throws Exception
	 */
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

	/**
	 * Sets the rating for the specified song, 1-5.
	 * @param rating
	 * @param songID
	 * @throws Exception For invalid rating (> 5 || < 1) or if the
	 * song is not in the database.
	 */
	public void setSongRating(int rating, int songID) throws Exception {
		if(dbConnection == null) {
			this.prepareConnection();
		}
		if(rating > 5 || rating < 1) {
			throw InvalidRatingException;
		}
		if(!doesSongExist(songID)) {
			throw SongNotInDatabaseException;
		}

		String query = "UPDATE songs SET song_rating = ? WHERE song_id = ?";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setInt(1, rating);
		ps.setInt(2, songID);
		ps.executeUpdate();
	}

	public ArrayList<Song> getSongsForArtist(String artistName) {
		ArrayList<Song> songArrayList = new ArrayList<Song>();
		if(dbConnection == null) {
			this.prepareConnection();
		}

		try {
			String query = "SELECT s.song_id, s.song_title, ar.artist_name, al.album_name," +
			" s.song_rating, al.album_genre, s.absolute_path, s.song_length" +
			" FROM songs s, artist ar, album al WHERE ar.artist_name LIKE ?" +
			" AND s.album_id = al.album_id AND ar.artist_id = al.artist_id" +
			" ORDER BY ar.artist_name ASC, al.album_name ASC, s.song_id ASC";
			PreparedStatement ps = dbConnection.prepareStatement(query);
			ps.setString(1, artistName);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				songArrayList.add(new Song(
						rs.getInt(1), rs.getString(2), rs.getString(3), 
						rs.getString(4), rs.getString(5), rs.getString(6),
						rs.getString(7), rs.getInt(8)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return songArrayList;
	}

	public ArrayList<Song> getSongsForAlbum(String albumName) {
		ArrayList<Song> songArrayList = new ArrayList<Song>();
		if(dbConnection == null) {
			this.prepareConnection();
		}

		try {
			String query = "SELECT s.song_id, s.song_title, ar.artist_name, al.album_name," +
			" s.song_rating, al.album_genre, s.absolute_path, s.song_length" +
			" FROM songs s, artist ar, album al WHERE al.album_name LIKE ?" +
			" AND s.album_id = al.album_id AND ar.artist_id = al.artist_id" +
			" ORDER BY ar.artist_name ASC, al.album_name ASC, s.song_id ASC";
			PreparedStatement ps = dbConnection.prepareStatement(query);
			ps.setString(1, albumName);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				songArrayList.add(new Song(
						rs.getInt(1), rs.getString(2), rs.getString(3), 
						rs.getString(4), rs.getString(5), rs.getString(6),
						rs.getString(7), rs.getInt(8)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return songArrayList;
	}

	public ArrayList<Song> getSongsForGenre(String genre) {
		ArrayList<Song> songArrayList = new ArrayList<Song>();
		if(dbConnection == null) {
			this.prepareConnection();
		}

		try {
			String query = "SELECT s.song_id, s.song_title, ar.artist_name, al.album_name," +
			" s.song_rating, al.album_genre, s.absolute_path, s.song_length" +
			" FROM songs s, artist ar, album al WHERE al.album_genre LIKE ?" +
			" AND s.album_id = al.album_id AND ar.artist_id = al.artist_id" +
			" ORDER BY ar.artist_name ASC, al.album_name ASC, s.song_id ASC";
			PreparedStatement ps = dbConnection.prepareStatement(query);
			ps.setString(1, genre);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				songArrayList.add(new Song(
						rs.getInt(1), rs.getString(2), rs.getString(3), 
						rs.getString(4), rs.getString(5), rs.getString(6),
						rs.getString(7), rs.getInt(8)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return songArrayList;
	}

	public ArrayList<Song> getSongsWithRatingAbove(int minRating) {
		ArrayList<Song> songArrayList = new ArrayList<Song>();
		if(dbConnection == null) {
			this.prepareConnection();
		}

		try {
			String query = "SELECT s.song_id, s.song_title, ar.artist_name, al.album_name," +
			" s.song_rating, al.album_genre, s.absolute_path, s.song_length" +
			" FROM songs s, artist ar, album al WHERE s.song_rating >= ?" +
			" AND s.album_id = al.album_id AND ar.artist_id = al.artist_id" +
			" ORDER BY ar.artist_name ASC, al.album_name ASC, s.song_id ASC";
			PreparedStatement ps = dbConnection.prepareStatement(query);
			ps.setInt(1, minRating);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				songArrayList.add(new Song(
						rs.getInt(1), rs.getString(2), rs.getString(3), 
						rs.getString(4), rs.getString(5), rs.getString(6),
						rs.getString(7), rs.getInt(8)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return songArrayList;
	}

	public ArrayList<Song> getSongsByTitle(String title) {
		ArrayList<Song> songArrayList = new ArrayList<Song>();
		if(dbConnection == null) {
			this.prepareConnection();
		}

		try {
			String query = "SELECT s.song_id, s.song_title, ar.artist_name, al.album_name," +
			" s.song_rating, al.album_genre, s.absolute_path, s.song_length" +
			" FROM songs s, artist ar, album al WHERE s.song_title LIKE ?" +
			" AND s.album_id = al.album_id AND ar.artist_id = al.artist_id" +
			" ORDER BY ar.artist_name ASC, al.album_name ASC, s.song_id ASC";
			PreparedStatement ps = dbConnection.prepareStatement(query);
			ps.setString(1, title);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				songArrayList.add(new Song(
						rs.getInt(1), rs.getString(2), rs.getString(3), 
						rs.getString(4), rs.getString(5), rs.getString(6),
						rs.getString(7), rs.getInt(8)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return songArrayList;
	}

	private boolean isAlbumEmpty(int albumID) throws SQLException {
		if(dbConnection == null) {
			this.prepareConnection();
		}

		String query = "SELECT * FROM songs s, album al WHERE " +
		"al.album_id = ? AND s.album_id = al.album_id";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setInt(1, albumID);
		if(ps.execute()) {
			return false;
		} else {
			return true;
		}
	}

	private boolean isArtistEmpty(int artistID) throws SQLException {
		if(dbConnection == null) {
			this.prepareConnection();
		}

		String query = "SELECT * FROM artist ar, album al WHERE " +
		"ar.artist_id = ? AND al.artist_id = ar.artist_id";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setInt(1, artistID);
		if(ps.execute()) {
			return false;
		} else {
			return true;
		}
	}
	
	public void deleteSong(Song songToDelete) throws Exception {
		this.deleteSong(new File(songToDelete.getAbsolutePath()));
	}

	public void deleteSong(File songToDelete) throws Exception {
		AudioFile audioFile = AudioFileIO.read(songToDelete);
		Tag songTag = audioFile.getTag();
		if(dbConnection == null) {
			this.prepareConnection();
		}

		String query = "DELETE FROM songs WHERE absolute_path = ? OR (" +
		"song_title = ? AND album_id = ?)";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setString(1, songToDelete.getAbsolutePath());
		ps.setString(2, songTag.getFirst(FieldKey.TITLE));
		ps.setInt(3, this.getAlbumID(songToDelete));
		ps.execute();

		if(this.isAlbumEmpty(this.getAlbumID(songToDelete))) {
			this.deleteAlbum(songToDelete);
		}
	}
	
	private void deleteAlbum(File songToDelete) throws Exception {
		if(dbConnection == null) {
			this.prepareConnection();
		}

		String query = "DELETE FROM album WHERE album_id = ?";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setInt(1, this.getAlbumID(songToDelete));
		ps.execute();

		if(this.isArtistEmpty(this.getArtistID(songToDelete))) {
			this.deleteArtist(songToDelete);
		}
	}
	
	private void deleteArtist(File songToDelete) throws Exception {
		if(dbConnection == null) {
			this.prepareConnection();
		}

		String query = "DELETE FROM artist WHERE artist_id = ?";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setInt(1, this.getArtistID(songToDelete));
		ps.execute();
	}
}
