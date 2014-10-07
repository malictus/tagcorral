package malictus.tagcorral.lib.riff;

import malictus.tagcorral.lib.InvalidReasons;
import malictus.tagcorral.lib.TCUtil;
import malictus.tagcorral.lib.file.*;

import java.io.*;

/**
 * RIFFFile is the class that represents any RIFF-formatted file, including both WAV and AVI.
 */
public class RIFFFile extends TCFile {
	
	private RIFFChunk mainChunk;

	public RIFFFile(String pathname) throws IOException {
		super(pathname);
	}
	
	/**
	 * Retrieval method for the top level RIFFChunk.
	 * 
	 * @return the RIFFChunk object.
	 */
	public RIFFChunk getRIFFChunk() {
		return mainChunk;
	}
	
	/**
	 * Convenience method to get the RIFF's file type (WAVE, AVI, etc.)
	 * @return the RIFF file's FourCC type
	 */
	public String getRIFFFileType() {
		return mainChunk.getSubType();
	}
	
	/**
	 * Read and parse the file for usable metadata. Along the way, file validity will be determined.
	 * @throws IOException if the file is completely invalid (can't be parsed at all)
	 */
	public void parseMetadataChunks() throws IOException {
		TCRaf raf;
		try {
			raf = new TCRaf(this, "r");
		} catch (Exception err) {
			throw new IOException("Error creating RAF object");
		}
		try {
			raf.seek(0);
			byte[] buf = new byte[4];
			raf.read(buf);
			String test = new String(buf);
			if (!test.equals("RIFF")) {
				throw new IOException("Outer chunk is not a RIFF chunk; incorrect FourCC");
			}
			//read to figure out size of outer chunk
			raf.seek(4);
			long size = raf.read4ByteInt(false, false);
		    long chunkSize = size + 8;
		    long realChunkSize = TCUtil.adjustForPadByte(chunkSize);
			if (this.length() > realChunkSize) {
				//extra data at end of file
				this.addInvalidReason(InvalidReasons.INVALID_FILE_UNEXPECTED_EXTRA_DATA);
		    }
			if (this.length() < chunkSize) {
				//file is truncated
				this.addInvalidReason(InvalidReasons.INVALID_FILE_UNEXPECTED_END);
				chunkSize = this.length();
			}
			mainChunk = new RIFFChunk(this, 0, chunkSize, raf, null);
			raf.close();
		} catch (IOException err) {
			raf.close();
			throw err;
		}
	}

}
