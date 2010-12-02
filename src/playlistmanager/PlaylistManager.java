package playlistmanager;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;

public class PlaylistManager
{
	public static void main(String[] args)
	{
		try {
			String text = "org.jaudiotagger.level = OFF";
			InputStream is = new ByteArrayInputStream(text.getBytes("UTF-8"));
			LogManager.getLogManager().readConfiguration(is);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(!(new File(PlaylistVariables.getFullDBName()).exists())) {
			// DB does not exist. Create DB.
			DatabaseCreator.createDatabase();
		}
		HardDrive.scanHardDrive();
	}
}