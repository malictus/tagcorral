package malictus.tagcorral.lib.png;

import malictus.tagcorral.lib.file.*;
import java.io.*;

/**
 * PNGFile is an object that represents a PNG file.
 */
public class PNGFile extends TCFile {
	
	private PNGFileChunk pngChunk;

	public PNGFile(String pathname) throws IOException {
		super(pathname);
	}
	
	/**
	 * Retrieval method for the PNGFileChunk.
	 * 
	 * @return the PNGFileChunk object.
	 */
	public PNGFileChunk getPNGFileChunk() {
		return pngChunk;
	}
	
	/**
	 * Read and parse the PNG file for usable metadata.
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
			pngChunk = new PNGFileChunk(this, 0, this.length(), raf);
			metaChunks = pngChunk.getMetaChunks();
			raf.close();
		} catch (IOException err) {
			raf.close();
			throw err;
		}
	}

}
