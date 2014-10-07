package malictus.tagcorral.lib.riff;

import java.io.*;

import malictus.tagcorral.lib.InvalidReasons;
import malictus.tagcorral.lib.chunk.*;
import malictus.tagcorral.lib.file.*;

/**
 * Represents a WAV file link chunk.
 */
public class LINKChunk extends PlainTextChunk {
	
	private RIFFChunk parentChunk;
	
	/**
	 * LINKChunk creator method.
	 * 
	 * @param parentFile the parent file that this chunk is associated with
	 * @param startByte the start of the chunk, in bytes
	 * @param length the length of the chunk, in bytes.
	 * @param raf a pointer to the file; doesn't have to be in the correct file position
	 * @param parentChunk the parent RIFF chunk to this one
	 * @throws IOException if file is invalid, or start point or length are invalid
	 */
	public LINKChunk(TCFile parentFile, long startByte, long length, TCRaf raf, RIFFChunk parentChunk) throws IOException {
		super(parentFile, startByte, length, raf);
		this.parentChunk = parentChunk;
		if (!parentChunk.getSubType().equals("WAVE")) {
			this.addInvalidReason(InvalidReasons.INVALID_CHUNK_NOT_WAVE);
		}
	}
	
	/**
	 * @return the parent to this chunk
	 */
	public RIFFChunk getParent() {
		return parentChunk;
	}
	
}
