package malictus.tagcorral.lib.file;

import java.io.*;

/**
 * All TCFile types must implement these methods
 */
public interface TCFileInterface {
	
	/**
	 * Should be called by all file types to parse the file and pull out the metadata chunks. File validity should usually be determined at this stage as well.
	 * @throws IOException if the file can't be parsed at all (invalid file)
	 */
	public void parseMetadataChunks() throws IOException;
	
}
