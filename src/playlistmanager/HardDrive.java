package playlistmanager;

import java.io.File;
import java.io.IOException;

public class HardDrive {
	/**
	 * Scans the hard drive and adds the songs to the database.
	 */
	public static void scanHardDrive() {
		try {
			new FileTraversal() {
				public void onFile( final File f ) {
					try {
						if(f.getCanonicalFile().getName().contains(".mp3")) {
							SongHandler sh = new SongHandler();
							sh.addSong(f);
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}.traverse(new File(PlaylistVariables.getScanPath()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
