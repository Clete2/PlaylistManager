package playlistmanager;

public final class Song {
	private final int songID;
	private final String title;
	private final String artist;
	private final String album;
	private final String rating;
	private final String genre;
	private final String absolutePath;
	
	public Song(int songID, String title, String artist, String album, 
			String rating, String genre, String absolutePath) {
		this.songID = songID;
		this.title = title;
		this.artist = artist;
		this.album = album;
		this.rating = rating;
		this.genre = genre;
		this.absolutePath = absolutePath;
	}
	
	public int getSongID() {
		return this.songID;
	}
	
	public String getTitle() {
		return this.title;
	}

	public String getArtist() {
		return this.artist;
	}

	public String getAlbum() {
		return this.album;
	}
	
	public String getRating() {
		return this.rating;
	}
	
	public String getGenre() {
		return this.genre;
	}
	
	public String getAbsolutePath() {
		return this.absolutePath;
	}
	
	public static void printSongHeader() {
		System.out.printf("%-50s%-30s%-30s%-15s%-10s%-15s\n", "Title", "Artist",
				"Album", "Rating", "Genre", "Path");
	}
	
	public void printSongInformation() {
		System.out.printf("%-50s%-30s%-30s%-15s%-15s\n", this.title, 
				this.artist, this.album, this.rating, this.genre, 
				this.absolutePath);
	}
}
