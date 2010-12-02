package playlistmanager;

public final class PlaylistVariables {
	private final static String dbPath = "database/";
	private final static String dbFile = "playlist.db";
	
	public static String getDBPath() {
		return dbPath;
	}
	
	public static String getDBFile() {
		return dbFile;
	}
	
	public static String getFullDBName() {
		return dbPath + dbFile;
	}
}

