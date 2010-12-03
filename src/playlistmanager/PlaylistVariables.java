package playlistmanager;

public final class PlaylistVariables {
	private final static String dbPath = "database/";
	private final static String dbFile = "playlist.db";
	
	public final static String getDBPath() {
		return dbPath;
	}
	
	public final static String getDBFile() {
		return dbFile;
	}
	
	public final static String getFullDBName() {
		return dbPath + dbFile;
	}
}

