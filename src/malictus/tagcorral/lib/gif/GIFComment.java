package malictus.tagcorral.lib.gif;

import java.io.*;
import malictus.tagcorral.lib.InvalidReasons;
import malictus.tagcorral.lib.TCUtil;
import malictus.tagcorral.lib.file.*;
import malictus.tagcorral.lib.chunk.*;

/**
 * Chunk to represent a Comment Extension chunk.
 */
public class GIFComment extends MetaChunk {
	
	private GIFFileChunk parentChunk;
	private String theComment = "";
	
	/**
	 * GIFComment chunk creator method.
	 * 
	 * @param parentFile the parent file that this chunk is associated with
	 * @param startByte the start of the chunk, in bytes
	 * @param length the length of the chunk, in bytes.
	 * @param parentChunk the GIFFileChunk that this chunk is a part of
	 * @param raf a pointer to the file, can be in any byte position
	 * @throws IOException if file is invalid, or if this chunk can't be parsed at all (non-ASCII comments will not throw exception)
	 */
	public GIFComment(TCFile parentFile, long startByte, long length, GIFFileChunk parentChunk, TCRaf raf) throws IOException {
		super(parentFile, startByte, length);
		this.parentChunk = parentChunk;
		if (parentFile.length() < (startByte + length)) {
			throw new IOException("Chunk does not have enough bytes");
		}
		raf.seek(startByte);
		if (raf.read1ByteInt(false) != 0x21) {
			throw new IOException("Incorrect introducer");
		}
		if (raf.read1ByteInt(false) != 0xfe) {
			throw new IOException("Incorrect comment label");
		}
		int amt = 1;
		boolean commentIsValid = true;
		while ((amt != 0) && (raf.getFilePointer() < (this.getStartByte() + this.getLength()))) {
			amt = raf.read1ByteInt(false);
			if (amt != 0) {
				byte[] str = new byte[amt];
				raf.read(str);
				String test = new String(str, "US-ASCII");
				if (!TCUtil.encodingIsCorrect(str, "US-ASCII")) {
					commentIsValid = false;
				}
				theComment = theComment + test;
			}
		}
		if (!commentIsValid) {
			this.addInvalidReason(InvalidReasons.INVALID_CHUNK_NOT_ASCII);
		}
		if (parentChunk.is87a()) {
			this.addInvalidReason(InvalidReasons.INVALID_CHUNK_GIFCOMMENT_NOT_89A);
		}
	}
	
	/**
	 * Getter method for the comment itself
	 * 
	 * @return the comment string
	 */
	public String getComment() {
		return theComment;
	}
	
	/**
	 * Getter method for parent chunk.
	 * 
	 * @return the parent PNGChunk
	 */
	public GIFFileChunk getParentChunk() {
		return parentChunk;
	}

}
