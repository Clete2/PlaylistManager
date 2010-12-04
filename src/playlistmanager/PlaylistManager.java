package playlistmanager;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.LogManager;

/**
 * A sample command-line program. Edit the paths and queries below.
 */
public class PlaylistManager
{
	public static void main(String[] args)
	{
		try {
			// The jaudiotagger library is like that one guy you know
			// who never stops talking.
			String text = "org.jaudiotagger.level = OFF";
			InputStream is = new ByteArrayInputStream(text.getBytes("UTF-8"));
			LogManager.getLogManager().readConfiguration(is);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(!(new File(PlaylistVariables.getFullDBName()).exists())) {
			// DB does not exist. Create DB.
			DatabaseCreator.createDatabase();
			MP3Scanner.scanPath("/Users/Clete2/Music/");
		}
		
		SongHandler sh = new SongHandler();
		ArrayList<Song> songResult = sh.getSongsForArtist("Weird Al Yankovic");
		PlaylistCreator.savePlaylistFile(songResult, 
				"/Users/Clete2/Desktop/playlist.pls");
		Song.printSongHeader();
		for(Song mySong : songResult) {
			mySong.printSongInformation();
		}
	}
}