package playlistmanager;

public final class PlaylistVariables {
	private final static String dbPath = "database/";
	private final static String dbFile = "playlist.db";
	private final static String scanPath = "/Users/Clete2/Music/";
	
	public final static String getDBPath() {
		return dbPath;
	}
	
	public final static String getDBFile() {
		return dbFile;
	}
	
	public final static String getFullDBName() {
		return dbPath + dbFile;
	}
	
	public final static String getScanPath() {
		return scanPath;
	}
}

