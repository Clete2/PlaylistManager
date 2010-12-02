// Credit for this file.
// http://vafer.org/blog/20071112204524
// This file has been modified from the original code above.

package playlistmanager;

import java.io.File;
import java.io.IOException;

public class FileTraversal {
	public final void traverse( final File f ) throws IOException {
		if (f.isDirectory()) {
			onDirectory(f);
			final File[] childs = f.listFiles();
			if(childs != null) {
				for( File child : childs ) {
					// Exclude hidden directories
					if(!child.getCanonicalFile().getName().substring(0,1)
							.equals(".")) {
						traverse(child);
					}
				}
				return;
			}
		}
		onFile(f);
	}

	public void onDirectory( final File d ) {
	}

	public void onFile( final File f ) {
	}
}