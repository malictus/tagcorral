package malictus.tagcorral.lib.gif;

import malictus.tagcorral.lib.file.*;
import java.io.*;

/**
 * GIFFile is an object that represents a GIF file. Both 87a and 89a file types are represented here.
 */
public class GIFFile extends TCFile {
	
	private GIFFileChunk gifChunk;

	public GIFFile(String pathname) throws IOException {
		super(pathname);
	}
	
	/**
	 * Retrieval method for the GIFFileChunk.
	 * 
	 * @return the GIFFileChunk object.
	 */
	public GIFFileChunk getGIFFileChunk() {
		return gifChunk;
	}
	
	/**
	 * Read and parse the GIF file for usable metadata.
	 * @throws IOException if the file is completely invalid
	 */
	public void parseMetadataChunks() throws IOException {
		TCRaf raf;
		try {
			raf = new TCRaf(this, "r");
		} catch (Exception err) {
			throw new IOException("Error creating RAF object");
		}
		try {
			gifChunk = new GIFFileChunk(this, 0, this.length(), raf);
			metaChunks = gifChunk.getMetaChunks();
			raf.close();
		} catch (IOException err) {
			raf.close();
			throw err;
		}
	}

}
