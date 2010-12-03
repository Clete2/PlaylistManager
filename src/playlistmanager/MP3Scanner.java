package playlistmanager;

import java.io.File;
import java.io.IOException;

public class MP3Scanner {
	/**
	 * Scans the path given recursively and adds mp3s found to the database.
	 */
	public static void scanPath(String path) {
		try {
			new FileTraversal() {
				public void onFile( final File f ) {
					try {
						if(f.getCanonicalFile().getName().contains(".mp3")) {
							SongHandler sh = new SongHandler();
							sh.addSong(f);
						}
					} catch (IOException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}.traverse(new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
