package malictus.tagcorral.lib.riff;

import java.io.*;
import malictus.tagcorral.lib.InvalidReasons;
import malictus.tagcorral.lib.chunk.*;
import malictus.tagcorral.lib.file.*;

/**
 * Represents an XMP chunk in a wav file.
 */
public class RIFFXMPChunk extends PlainTextChunk {
	
	private RIFFChunk parentChunk;
	
	/**
	 * RIFFXMPChunk creator method.
	 * 
	 * @param parentFile the parent file that this chunk is associated with
	 * @param startByte the start of the chunk, in bytes
	 * @param length the length of the chunk, in bytes.
	 * @param raf a pointer to the file; doesn't have to be in the correct file position
	 * @param parentChunk the parent RIFF chunk to this one
	 * @throws IOException if file is invalid, or start point or length are invalid
	 */
	public RIFFXMPChunk(TCFile parentFile, long startByte, long length, TCRaf raf, RIFFChunk parentChunk) throws IOException {
		super(parentFile, startByte, length, raf);
		this.parentChunk = parentChunk;
		//for this kind (only), should be UTF-8
		if (!this.getCharsetInfo().getCharset().name().equals("UTF-8")) {
			this.addInvalidReason(InvalidReasons.INVALID_CHUNK_XMP_NOT_UTF8);
		}
	}
	
	/**
	 * @return the parent to this chunk
	 */
	public RIFFChunk getParent() {
		return parentChunk;
	}
	
}
