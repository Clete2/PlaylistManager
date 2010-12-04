package playlistmanager;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class PlaylistCreator {
	public static void savePlaylistFile(ArrayList<Song> songArray, String fileToSavePath) {		
		StringBuffer playlistFileContents = new StringBuffer();
		int numberOfEntries = 0;
		playlistFileContents.append("[playlist]\n\n");

		for(Song mySong : songArray) {
			numberOfEntries++;
			playlistFileContents.append("File" + numberOfEntries + "=" +
					mySong.getAbsolutePath() + "\n");
			playlistFileContents.append("Title" + numberOfEntries + "=" +
					mySong.getTitle() + "\n");
			playlistFileContents.append("Length" + numberOfEntries + "=" +
					mySong.getLength() + "\n\n");
		}

		playlistFileContents.append("NumberOfEntries=" + numberOfEntries +
				"\n\n" + "Version=2");
		
		try {
			FileWriter myFile = new FileWriter(fileToSavePath);
			BufferedWriter myWriter = new BufferedWriter(myFile);
			myWriter.write(playlistFileContents.toString());
			myWriter.close();
			myFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
